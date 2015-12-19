package com.myMoneyTracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.myMoneyTracker.controller.model.Greeting;

@RestController
public class GreetingController {
	
	@RequestMapping("/greeting")
	public @ResponseBody Greeting sayHello() {
		Greeting greeting = new Greeting(1, "Hello, mon!");
		System.out.println("><><> @RequestMapping(/greeting) called !");
		return greeting;
	}

}
