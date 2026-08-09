package br.com.nicolae.restgenerator.generator.exceptions;

import br.com.nicolae.restgenerator.generator.exceptions.enums.JSONErrorCode;

public class JsonException extends Exception {

	private static final long serialVersionUID = 1L;
	
	private final JSONErrorCode jsonErrorCode;
	
	public JsonException(String message, JSONErrorCode jsonErrorCode) {
		super(message);
		this.jsonErrorCode = jsonErrorCode;
	}
	
	public JsonException(String message, Throwable cause, JSONErrorCode jsonErrorCode) {
		super(message, cause);
		this.jsonErrorCode = jsonErrorCode;
	}

	
	public JSONErrorCode getCode() {
		return this.jsonErrorCode;
	}

}
