package com.newegg.redis.exceptions;

public class ParameterException extends RuntimeException{
	private static final long serialVersionUID = 410062193427205847L;

	public ParameterException(String msg) {
		super(msg);
	}
	
	public ParameterException(String msg, Throwable e) {
		super(msg, e);
	}
}
