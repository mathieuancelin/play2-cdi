package services

import play.api.Play.current
import play.api.libs._
import play.api.libs.iteratee._
import play.api.libs.concurrent._
import java.util.concurrent._
import scala.concurrent.stm._
import play.api.libs.json._
import javax.enterprise.context._
import javax.enterprise.event ._

@ApplicationScoped
class SseService {

	val noise: Enumerator[JsValue] = Enumerator.generateM[JsValue] {
	  Promise.timeout( Some( 
	  	JsObject( List( "message" -> JsString( "System message : " + System.currentTimeMillis() ) ) ) ), 
	    2000 )
	}

	val hubEnumerator = Enumerator.imperative[JsValue]()
  	val hub = Concurrent.hub[JsValue]( hubEnumerator >- noise )

	def listenJsEvent( @Observes evt: JsValue ) = {
		hubEnumerator.push( evt )
	}

	def listenStringEvent( @Observes evt: String ) = {
		hubEnumerator.push( Json.parse( evt ) )
	}
}