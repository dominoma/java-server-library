package com.tennisrockt.jsl.exceptions;

import express.utils.Status;

public class NotFoundException extends ServerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7736509504624451761L;

	public NotFoundException(String msg) {
		super(Status._404, msg);
		// TODO Auto-generated constructor stub
	}

}
