package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

import javax.inject._
import javax.enterprise.context._
import javax.enterprise.event._

import services._

class ScalaController extends Controller {

  val form = Form( "message" -> text )

  @Inject var helloService: HelloService = _

  @Inject var evt: Event[String] = _

  def index() = Action { implicit request =>
    Ok(	views.html.index( helloService.hello() ) )	
  }

  def sendMessage() = Action { implicit request =>
  	form.bindFromRequest.fold (
	  formWithErrors => BadRequest( "You need to pass a 'message' value!" ),
	  { maybeValue =>
	    evt.fire( "{\"message\" : \"" + maybeValue + "\"}" );
	    Ok
	  }
  	)			
  }
}