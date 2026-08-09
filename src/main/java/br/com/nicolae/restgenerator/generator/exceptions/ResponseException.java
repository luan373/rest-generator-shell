package br.com.nicolae.restgenerator.generator.exceptions;

import br.com.nicolae.restgenerator.generator.exceptions.enums.ResponseErrorCode;

public class ResponseException extends Exception {

	private static final long serialVersionUID = 2825751497982357501L;

	private final ResponseErrorCode respondeErrorCode;

	public ResponseException(String message, ResponseErrorCode respondeErrorCode) {
		super(message);
		this.respondeErrorCode = respondeErrorCode;
	}

	public ResponseException(String message, Throwable cause, ResponseErrorCode respondeErrorCode) {
		super(message, cause);
		this.respondeErrorCode = respondeErrorCode;
	}

	public ResponseErrorCode getCode() {
		return this.respondeErrorCode;
	}

}
