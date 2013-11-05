package controllers

import play.api._
import play.api.mvc._
import models.FilterStreamSubscription

import play.api.libs.iteratee.Enumeratee
import play.api.libs.json.{JsValue, Json}
import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._

object Application extends Controller {


  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  // Adapter from a stream of twitter4j Status to a stream of JsObject, to generate Json content.
  val asJson: Enumeratee[twitter4j.Status, JsValue] = Enumeratee.map[twitter4j.Status] {
    twitterStatus =>
      Json.obj(
        "event" -> "twitterStatus",
        "status" -> Json.obj(
          "username" -> twitterStatus.getUser.getScreenName,
          "user_image" -> twitterStatus.getUser.getProfileImageURLHttps,
          "created_at" -> twitterStatus.getCreatedAt,
          "text" -> twitterStatus.getText
        ))
  }

  def filterStream(terms: String) = Action {
    Ok.feed(
      FilterStreamSubscription(terms.split(",").map(_.trim).toList).enumerator &> asJson &> EventSource()).as("text/event-stream")
  }
}