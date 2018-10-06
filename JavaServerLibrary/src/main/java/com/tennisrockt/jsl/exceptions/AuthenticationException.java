package com.tennisrockt.jsl.exceptions;

import express.http.response.Response;
import express.utils.Status;

public class AuthenticationException extends ServerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4865175578802294180L;

	public AuthenticationException() {
		super(Status._401, "Valid authentication required");
	}

	@Override
	public void doSendError(Response res) {
		res.setHeader("WWW-Authenticate", "OAuth");
		super.doSendError(res);
	}
	
	

}
