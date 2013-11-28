package controllers

import play.api._
import play.api.mvc._
import models._

import play.api.libs.EventSource
import play.api.libs.concurrent.Execution.Implicits._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
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
}