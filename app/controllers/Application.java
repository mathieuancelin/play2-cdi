package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

import services.*;

import javax.enterprise.inject.*;
import javax.enterprise.context.*;

import javax.inject.*;

@ApplicationScoped
public class Application extends Controller {

	@Inject
	private HelloService helloService;
  
  	public Result index() {
    	return ok(index.render(helloService.hello()));
  	}
}