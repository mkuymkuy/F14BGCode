package com.f14.framework.exception;

import java.io.Serializable;

public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	private String errorCode;

	private Serializable[] parms;

	private String msg;

	
	public BusinessException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public BusinessException(String code, String msg) {
		super(msg);
		this.errorCode = code;
		this.msg = msg;
	}

	public BusinessException(String code, String msg, Serializable[] parms) {
		super(msg);
		this.errorCode = code;
		this.parms = parms ;
		this.msg = msg;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public Serializable[] getParms() {
		return parms;
	}

	public void setParms(Serializable[] parms) {
		this.parms = parms;
	}

	public String toString(){
		return msg;
	}
}
