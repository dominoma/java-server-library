package com.tennisrockt.jsl.exceptions;

import express.utils.Status;

public class ForbiddenException extends ServerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -97532368279621944L;

	public ForbiddenException(String msg) {
		super(Status._403, msg);
	}

}
