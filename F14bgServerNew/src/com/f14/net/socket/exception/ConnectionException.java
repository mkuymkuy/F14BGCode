package com.f14.net.socket.exception;

public class ConnectionException extends Exception {
	private static final long serialVersionUID = 827662582585267236L;

	public ConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectionException(String message) {
		super(message);
	}

	public ConnectionException(Throwable cause) {
		super(cause);
	}

}
