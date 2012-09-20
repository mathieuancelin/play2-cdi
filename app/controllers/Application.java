package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

import services.*;

import javax.enterprise.inject.*;
import javax.enterprise.context.*;

import javax.inject.*;
import javax.enterprise.event.*;
import play.data.*;

@ApplicationScoped
public class Application extends Controller {

	public static class MessageForm {
	    public String message;
	}	

	@Inject HelloService helloService;

	@Inject Event<String> messageEvent;
  
  	public Result index() {
    	return ok(index.render(helloService.hello()));
  	}

  	public Result sendMessage() {
  		Form<MessageForm> form = form(MessageForm.class);
  		Form<MessageForm> filledForm = form.bindFromRequest();
  		if(form.hasErrors()) {
  		    return badRequest("You need to pass a 'message' value!"); 
  		} else {
  			messageEvent.fire( "{\"message\" : \"" + filledForm.get().message + "\"}" );
		    return ok();
  		}		
  	}
}