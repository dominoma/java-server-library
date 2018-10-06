package com.tennisrockt.jsl.exceptions;

import express.utils.Status;

public class JSONFormatException extends ServerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8586864083572933691L;

	public JSONFormatException(String msg) {
		super(Status._400, msg);
		// TODO Auto-generated constructor stub
	}

}
