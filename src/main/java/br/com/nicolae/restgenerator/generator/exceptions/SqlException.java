package br.com.nicolae.restgenerator.generator.exceptions;

import br.com.nicolae.restgenerator.generator.exceptions.enums.SqlErrorCode;

public class SqlException extends Exception {

	private static final long serialVersionUID = 5470968654578759495L;

	private final SqlErrorCode sqlErrorCode;

	public SqlException(String message, SqlErrorCode sqlErrorCode) {
		super(message);
		this.sqlErrorCode = sqlErrorCode;
	}

	public SqlException(String message, Throwable cause, SqlErrorCode sqlErrorCode) {
		super(message, cause);
		this.sqlErrorCode = sqlErrorCode;
	}

	public SqlErrorCode getCode() {
		return this.sqlErrorCode;
	}

}
