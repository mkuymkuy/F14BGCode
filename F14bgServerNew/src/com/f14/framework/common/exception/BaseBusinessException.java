package com.f14.framework.common.exception;

/**
 *  需要处理的业务异常，在业务层抛出此错误不会导致事务回滚
 * 
 * @author evan
 *
 */
public class BaseBusinessException extends Exception {
	private static final long serialVersionUID = -5772133545430282802L;

	private String key;

	private String message;

	public String getKey() {
		return key;
	}

	public String getMessage() {
		return message;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public BaseBusinessException() {

	}

	public BaseBusinessException(String key, String message) {
		this.key = key;
		this.message = message;
	}
}
