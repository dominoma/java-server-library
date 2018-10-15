package com.tennisrockt.jsl.exceptions;

import org.json.JSONObject;

import express.utils.Status;

public class CriticalServerException extends ServerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2670269853862298697L;
	
	private final Exception exception;

	public CriticalServerException(Exception e) {
		super(Status._500, e.getMessage());
		exception = e;
	}

	@Override
	public JSONObject getJSON() {
		return ServerException.getExceptionJSON(getStatus(), exception);
	}
	
	public Exception getException() {
		return exception;
	}
	
	

}
