package models

import play.api.libs.iteratee.{Enumerator, Iteratee, Concurrent}
import play.libs.Akka
import twitter4j.Status
import com.beachape.twitterFilterStream.{FilterStreamActor, TwitterConfig}
import akka.actor.PoisonPill
import play.api.libs.json.JsValue
import scala.concurrent.ExecutionContext
import play.api.Play

object FilterStreamSubscription {

  val twitterConfig = TwitterConfig(
    consumerKey = Play.current.configuration.getString("twitter.consumerKey").getOrElse(""),
    consumerSecret = Play.current.configuration.getString("twitter.consumerSecret").getOrElse(""),
    accessToken = Play.current.configuration.getString("twitter.accessToken").getOrElse(""),
    accessTokenSecret = Play.current.configuration.getString("twitter.accessTokenSecret").getOrElse(""))

  def apply(terms: List[String]): FilterStreamSubscription = new FilterStreamSubscription(terms)
}

class FilterStreamSubscription(val terms: List[String]) {

  import ExecutionContext.Implicits.global

  val actor = Akka.system.actorOf(FilterStreamActor(terms, FilterStreamSubscription.twitterConfig))

  val enumerator = Concurrent.unicast[Status](
    onStart = { channel => actor ! { status: Status => channel.push(status) } }, //register on start
    onComplete = actor ! PoisonPill,
    onError = { (_, _) => actor ! PoisonPill}
  )

  val iteratee = Iteratee.foreach[JsValue]{ i =>
    play.Logger.debug(s"Received from client: ${i.toString}")}.map { _ =>
    play.Logger.debug("Disconnected")
  }

  def webSocketPair: (Iteratee[JsValue, Unit], Enumerator[Status]) = (iteratee, enumerator)

}
