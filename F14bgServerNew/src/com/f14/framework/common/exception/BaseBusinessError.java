package com.f14.framework.common.exception;

/**
 * 不需要处理的业务异常，在业务层抛出此错误会导致事务回滚
 * 
 * @author evan
 * 
 */
public class BaseBusinessError extends RuntimeException {
	private static final long serialVersionUID = 3863270329072664358L;

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

	public BaseBusinessError() {

	}

	public BaseBusinessError(String key, String message) {
		this.key = key;
		this.message = message;
	}
}
