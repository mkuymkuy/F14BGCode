package com.f14.framework.exception;

/**
 * DAO Exception Create at 2005-3-28
 * <p>
 * 
 * @version 1.0
 * @author shitianyu_kf
 */
public class DAOException extends RuntimeException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3257284725541254961L;

	/**
	 * 异常发生时，正在访问的实体的名称
	 */
	public final String entity;

	/**
	 * 异常发生时，正在访问的实体id
	 */
	public final Integer entityid;

	/**
	 * construction
	 * 
	 * @param code
	 *            message
	 * @param cause
	 *            throwable
	 */
	public DAOException(String entity, Integer entityid, String message,
			Throwable cause) {
		super(message, cause);
		this.entity = entity;
		this.entityid = entityid;
	}
}
