package controllers

import play.api._
import play.api.mvc._
import models._

import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._
import scala.concurrent.Future

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Twitter Filter Room"))
  }

  def newTerms(terms: String) = Action {
    FilterStreamRoom.newTerms(terms.split(",").map(_.trim).toList)
    Ok
  }

  def connect = Action.async {
    FilterStreamRoom.connect.map {
      filterStreamEnumerator =>
        Ok.feed(filterStreamEnumerator &> EventSource()).as("text/event-stream")
    }
  }

  def demo(pathParam: String) = Action.async { implicit request =>
    Future.successful { Ok(views.html.demo_endpoint("Demo endpoint", pathParam)) }
  }
}