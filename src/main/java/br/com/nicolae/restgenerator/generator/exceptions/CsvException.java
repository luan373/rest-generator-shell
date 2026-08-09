package br.com.nicolae.restgenerator.generator.exceptions;

import br.com.nicolae.restgenerator.generator.exceptions.enums.CsvErrorCode;

public class CsvException extends Exception {

	private static final long serialVersionUID = 5470968654578759495L;

	private final CsvErrorCode csvErrorCode;

	public CsvException(String message, CsvErrorCode csvErrorCode) {
		super(message);
		this.csvErrorCode = csvErrorCode;
	}

	public CsvException(String message, Throwable cause, CsvErrorCode csvErrorCode) {
		super(message, cause);
		this.csvErrorCode = csvErrorCode;
	}

	public CsvErrorCode getCode() {
		return this.csvErrorCode;
	}

}
