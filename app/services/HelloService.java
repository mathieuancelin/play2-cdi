package services;

import javax.enterprise.inject.*;
import javax.enterprise.context.*;

@ApplicationScoped
public class HelloService {

	public String hello() {
		return "Hello world!";
	}

}