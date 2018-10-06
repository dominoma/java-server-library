package com.tennisrockt.jsl.exceptions;

import express.utils.Status;

public class RequestException extends ServerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 710530346273065626L;

	public RequestException(String msg) {
		super(Status._400, msg);
		// TODO Auto-generated constructor stub
	}

}
