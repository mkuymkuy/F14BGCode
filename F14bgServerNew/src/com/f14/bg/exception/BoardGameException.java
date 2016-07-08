package com.f14.bg.exception;

/**
 * 桌游的异常信息
 * 
 * @author F14eagle
 *
 */
public class BoardGameException extends Exception {
	private static final long serialVersionUID = -7308251586513736435L;

	public BoardGameException(String msg){
		super(msg);
	}
}
