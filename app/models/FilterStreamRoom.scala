package models

import play.api.libs.iteratee.{Enumerator, Concurrent}
import play.libs.Akka
import twitter4j.Status
import com.beachape.twitterFilterStream.{FilterStreamActor, TwitterConfig}
import akka.actor.{Props, ActorRef, Actor}
import akka.pattern.{gracefulStop, ask}
import play.api.libs.json.{Json, JsValue}
import scala.concurrent.{ExecutionContext, Await, Future}
import play.api.Play
import scala.concurrent.duration._
import language.postfixOps
import akka.util.Timeout
import ExecutionContext.Implicits.global

/**
 * Convenient singleton class to encapsulate a "room" of users looking at
 * a single stream.
 */
object FilterStreamRoom {

  val twitterConfig = TwitterConfig(
    consumerKey = Play.current.configuration.getString("twitter.consumerKey").getOrElse(""),
    consumerSecret = Play.current.configuration.getString("twitter.consumerSecret").getOrElse(""),
    accessToken = Play.current.configuration.getString("twitter.accessToken").getOrElse(""),
    accessTokenSecret = Play.current.configuration.getString("twitter.accessTokenSecret").getOrElse(""))

  lazy private val default = Akka.system.actorOf(Props[FilterStreamRoom])

  implicit val askTimeout = Timeout(5 seconds)

  /**
   * Returns a future enumerator for pushing events
   * @return Future[Enumerator[JsValue]]
   */
  def connect: Future[Enumerator[JsValue]] = (default ? Connect).map {
    case Connected(enumerator) => enumerator
    case _ => throw new RuntimeException("Connect failed")
  }

  /**
   * Sends a new set of terms to be used for filtering
   * @param terms List[String]
   */
  def newTerms(terms: List[String]) {
    default ! NewTerms(terms)
  }

  def currentTerms: Future[List[String]] = (default ? GetCurrentTerms).mapTo[List[String]]

}

class FilterStreamRoom extends Actor {

  val (enumerator, channel) = Concurrent.broadcast[JsValue]

  /**
   * The default mode on construction
   * @return Receive PartialFunction
   */
  def receive: Receive = {
    case NewTerms(listOfTerms) => {
      context.become(filteringOn(listOfTerms, newFilterStreamActor(listOfTerms)))
    }
    case Connect => sender ! Connected(enumerator)
    case GetCurrentTerms => sender ! Nil
  }

  /**
   * Returns a new Receive for when a room is listening to a stream of events
   * @param currentTerms
   * @param filterStreamActor
   * @return
   */
  private def filteringOn(currentTerms: List[String], filterStreamActor: ActorRef): Receive = {
    case NewTerms(newListOfTerms) => {
      syncKill(filterStreamActor)
      channel.push(newTermsJson(newListOfTerms))
      context.become(filteringOn(newListOfTerms, newFilterStreamActor(newListOfTerms)))
    }
    case Connect => sender ! Connected(enumerator)
    case GetCurrentTerms => sender ! currentTerms
  }

  /**
   * Kills an actor synchronously
   * @param actorRef
   */
  private def syncKill(actorRef: ActorRef) {
    try {
      val stopped: Future[Boolean] = gracefulStop(actorRef, 5 seconds)
      Await.result(stopped, 6 seconds)
    } catch {
      case e: akka.pattern.AskTimeoutException ⇒
    }
  }

  /**
   * Returns a new FilterStreamActor actor reference
   *
   * Should only be used if no other FilterStreamActors exist because
   * there can only be one Twitter stream per set of credentials. See syncKill
   *
   * @param terms List[String]
   * @return ActorRef
   */
  private def newFilterStreamActor(terms: List[String]): ActorRef = {
    Akka.system.actorOf(
      FilterStreamActor(
      terms,
      FilterStreamRoom.twitterConfig, {
        status: Status =>
          channel.push(asJson(terms, status))
      }))
  }

  /**
   * Returns a JsValue for a Twitter4J Status
   * @param currentTerms List[String] Current terms
   * @param status Status Twitter4J status
   * @return JsValue
   */
  private def asJson(currentTerms: List[String], status: Status): JsValue = {
    Json.obj(
      "event" -> "message",
      "currentTerms" -> Json.arr(currentTerms),
      "status" -> Json.obj(
        "userName" -> status.getUser.getScreenName,
        "userImage" -> status.getUser.getProfileImageURLHttps,
        "createdAt" -> status.getCreatedAt,
        "text" -> status.getText
      ))
  }

  private def newTermsJson(terms: List[String]): JsValue = {
    Json.obj(
      "event" -> "newTerms",
      "terms" -> Json.arr(terms)
    )
  }

}