package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

import javax.inject._
import javax.enterprise.context._

import services._

import play.api.Play.current
import play.api.libs._
import play.api.libs.iteratee._
import play.api.libs.concurrent._

class StreamController extends Controller {

  @Inject var sseService: SseService = _

  def stream() = Action { implicit request => 
  	Ok.feed( sseService.hub.getPatchCord().through( EventSource( ) ) ).as( "text/event-stream" )
  }
}