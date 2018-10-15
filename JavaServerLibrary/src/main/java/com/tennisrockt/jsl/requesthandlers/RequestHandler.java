package com.tennisrockt.jsl.requesthandlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tennisrockt.jsl.exceptions.CriticalServerException;
import com.tennisrockt.jsl.exceptions.ServerException;

import express.http.HttpRequestHandler;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.MediaType;
import express.utils.Status;

public abstract class RequestHandler implements HttpRequestHandler {
	
	private Request request = null;
	private Response response = null;
	
	private final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	
	@Override
	public final void handle(Request req, Response res) {
		request = req;
		response = res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,HEAD");
		response.setContentType(MediaType._json);
		try {
			preHandle();
			if(!response.isClosed()) {
				if(req.getMethod().equals("GET")) {
					get();
				}
				else if(req.getMethod().equals("POST")) {
					post();
				}
				else if(req.getMethod().equals("DELETE")) {
					delete();
				}
				else if(req.getMethod().equals("PUT")) {
					put();
				}
				else if(req.getMethod().equals("OPTIONS")) {
					options();
				}
				else if(req.getMethod().equals("HEAD")) {
					head();
				}
			}
			if(!response.isClosed()) {
				response.sendStatus(Status._200);
			}
		} catch(CriticalServerException e) {
			e.sendError(response);
			logger.error(name(), e.getException());
		} catch(ServerException e) {
			e.sendError(response);
		} catch(Exception e) {
			if(!response.isClosed()) {
				sendError(Status._500, ServerException.getExceptionJSON(Status._500, e).toString());
			}
			logger.error(name(), e);
		}
	}
	
	public void preHandle() {
		
	}
	
	public void get() {
		response.sendStatus(Status._405);
	}
	public void post() {
		response.sendStatus(Status._405);
	}
	public void delete() {
		response.sendStatus(Status._405);
	}
	public void put() {
		response.sendStatus(Status._405);
	}
	public void options() {
		//CORS Request returns Access headers (always allowed)
	}
	public void head() {
		//HEAD is always allowed
	}
	
	public void sendError(Status code, String msg) {
		getResponse().setStatus(code);
		getResponse().send(msg);
	}
	
	public Request getRequest() {
		return request;
	}
	public Response getResponse() {
		return response;
	}
	
	public String name() {
		return this.getClass().getAnnotation(RegisterHandler.class).name();
	}
	
	public String url() {
		return this.getClass().getAnnotation(RegisterHandler.class).url();
	}

}