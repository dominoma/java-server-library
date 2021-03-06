package com.tennisrockt.jsl.exceptions;

import org.json.JSONObject;

import express.http.response.Response;
import express.utils.Status;

public class ServerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 894461166009426659L;
	
	
	public static JSONObject getExceptionJSON(Status status, Exception e) {
		JSONObject err = new JSONObject();
		err.put("status", status.getCode());
		err.put("exception", e.getClass().getSimpleName());
		err.put("description", e.getMessage());
		return err;
	}
	
	private final Status status;
	private final Exception exception;
	
	public ServerException(Status status, String msg) {
		super(msg);
		this.status = status;
		this.exception = null;
	}
	public ServerException(Exception e) {
		super(e.getMessage());
		this.status = Status._500;
		this.exception = e;
	}
	
	public final void sendError(Response res) {
		if(!res.isClosed()) {
			doSendError(res);
		}
		if(exception != null) {
			exception.printStackTrace();
		}
	}
	
	protected void doSendError(Response res) {
		res.setStatus(getStatus()).send(getJSON().toString());
	}
	
	public Status getStatus() {
		return status;
	}
	public JSONObject getJSON() {
		if(exception == null) {
			return getExceptionJSON(status, this);
		}
		else {
			return getExceptionJSON(status, exception);
		}
	}
	
}