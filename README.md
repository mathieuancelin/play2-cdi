Using CDI within a Play 2.0 application
===============================================

This is a simple application demonstrating how to integrate a Play 2.0 application components with CDI (especially with  <a href="http://seamframework.org/Weld">JBoss Weld</a>).

> This project is based on Guillaume Bort's work on <a href="https://github.com/guillaumebort/play20-spring-demo">how to integrate Spring in a Play2 application</a>. Thanks for the tip Guillaume :-)
> This project use Play 2.1 wich includes dynamic controller dispatching

## How to make it work

First, you need to add the Weld SE dependency. Here we use Weld SE because it's really easy to bootstrap it programmatically. In the `project/Build.scala` file, add a dependency to `weld-se`:

```scala
import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "play2-cdi"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "org.jboss.weld.se" % "weld-se" % "1.1.9.Final"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
```

You also need to add an empty (or not) `beans.xml` file in your Play 2 app. so Weld will be able to boot. Just create a `META-INF` folder in the `conf` and create an empty `beans.xml` inside.
 
Then, you need to use dynamic controller dispatching in your application so Weld will be able to create the controller instances for you. To do that, just prefix your controller class name with the `@` symbol in the `routes` file :

```
GET    /       @controllers.Application.index()
```


When you do that, he controllers instances management will be delegated to the `Global` object of your application. Here is an implementation of the `Global` using Weld :

```java
import play.*;

import org.jboss.weld.environment.se.ShutdownManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class Global extends GlobalSettings {

	private WeldContainer weld;

	@Override
	public void onStart(Application app) {
		weld = new Weld().initialize();
	}

	@Override
	public void onStop(Application app) {
		shutdown(weld);
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) {
		return weld.instance().select(clazz).get();
	}

	private void shutdown(WeldContainer weld) {
        ShutdownManager shutdownManager = weld.instance().select(ShutdownManager.class).get();
        shutdownManager.shutdown();
    }
}
```

Now, every class in your application is considered as a bean. If you want to exclude some classes, you need to write a CDI extension to veto bean types or you can use libraries like Solder or DeltaSpike to do the job. So we can write a basic service like the following :

```java
package services;

import javax.enterprise.context.*;

@ApplicationScoped
public class HelloService {

	public String hello() {
		return "Hello world!";
	}
}
```

and use it in a Java controller :

```java
package controllers;

import play.*;
import play.mvc.*;
import views.html.*;

import services.*;

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
```

or even in a Scala controller :

```scala
package controllers

import play.api._
import play.api.mvc._

import javax.inject._
import javax.enterprise.context._

import services._

class ScalaController extends Controller {

  @Inject var helloService: HelloService = _

  def index() = Action { implicit request =>
    Ok(	views.html.index( helloService.hello( ) ) )	
  }

}
```

Note that the controller is a class and not an object. If your controller is an object you won't be able to write something like the following in yoiur `routes` file :

```
GET     /scala                      @controllers.ScalaController.index()
```
