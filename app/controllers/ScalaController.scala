package controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._

import javax.inject._
import javax.enterprise.context._

import services._

class ScalaController extends Controller {

  @Inject var helloService: HelloService = _

  def index() = Action { implicit request =>
    Ok(	views.html.index( helloService.hello() ) )	
  }

}