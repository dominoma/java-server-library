package com.tennisrockt.jsl.requesthandlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tennisrockt.jsl.exceptions.CriticalServerException;

import express.Express;

public class RequestHandlers {
	
	private final Map<String, RequestHandler> handlers = new TreeMap<>();
	private final String handlerPackage;
	private final Express express = new Express();
	
	private final Logger logger = LoggerFactory.getLogger(RequestHandlers.class);
	
	
	public RequestHandlers(String handlerPackage) {
		this.handlerPackage = handlerPackage;
		searchForRegistrations();
		install();
	}
	
	private void install() {
		List<RequestHandler> handlerIcs = new ArrayList<>(handlers.values());
		Collections.sort(handlerIcs, (arg0, arg1) -> {
			return arg0.url().compareTo(arg1.url());
		});
		for(RequestHandler handler : handlerIcs) {
			String url = handler.url();
			if(!url.equals("")) {
				logger.info("Setting up '"+handler.name()+"' at '"+url+"'");
				express.all(url, handler);
			}
		}
	}

	private void searchForRegistrations() {
		Reflections ref = new Reflections(handlerPackage);
        for (Class<?> cl : ref.getTypesAnnotatedWith(RegisterHandler.class)) {
        	RegisterHandler registerHandler = cl.getAnnotation(RegisterHandler.class);
        	try {
				RequestHandler handler = (RequestHandler) cl.getConstructor().newInstance();
				handlers.put(registerHandler.name(), handler);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new CriticalServerException(e);
			}
        }
	}
	
	public void redirectTo(String name, RequestHandler origin) {
		if(!handlers.containsKey(name)) {
			throw new IllegalArgumentException("Handler with name '"+name+"' isn't registred!");
		}
		handlers.get(name).handle(origin.getRequest(), origin.getResponse());
	}
	
	public void listen() {
		express.listen();
	}
	
	public void stop() {
		express.stop();
	}
	
	
	
}
