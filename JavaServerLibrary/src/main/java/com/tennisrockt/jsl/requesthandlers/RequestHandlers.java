package com.tennisrockt.jsl.requesthandlers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.reflections.Reflections;

import com.tennisrockt.jsl.exceptions.ServerException;

import express.Express;
import express.http.request.Request;
import express.http.response.Response;

public class RequestHandlers {
	
	private static Map<String, RequestHandler> handlers;
	
	private static void searchForRegistrations() {
		if(handlers == null) {
			handlers = new TreeMap<>();
			Reflections ref = new Reflections();
	        for (Class<?> cl : ref.getTypesAnnotatedWith(RegisterHandler.class)) {
	        	RegisterHandler registerHandler = cl.getAnnotation(RegisterHandler.class);
	        	try {
					RequestHandler handler = (RequestHandler) cl.getConstructor().newInstance();
					handlers.put(registerHandler.name(), handler);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new ServerException(e);
				}
	        }
		}
	}
	
	public static void redirect(String name, Request req, Response res) {
		searchForRegistrations();
		if(!handlers.containsKey(name)) {
			throw new IllegalArgumentException("Handler with name '"+name+"' isn't registred!");
		}
		handlers.get(name).handle(req, res);
	}
	
	public static void install(Express express) {
		searchForRegistrations();
		List<RequestHandler> handlerIcs = new ArrayList<>(handlers.values());
		Collections.sort(handlerIcs, (arg0, arg1) -> {
			return arg0.url().compareTo(arg1.url());
		});
		for(RequestHandler handler : handlerIcs) {
			String url = handler.url();
			if(!url.equals("")) {
				express.all(url, handler);
			}
		}
	}
	
}
