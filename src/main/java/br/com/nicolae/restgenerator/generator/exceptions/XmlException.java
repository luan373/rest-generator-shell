package br.com.nicolae.restgenerator.generator.exceptions;

import br.com.nicolae.restgenerator.generator.exceptions.enums.XMLErrorCode;

public class XmlException extends Exception {

	private final XMLErrorCode xmlErrorCode;
	
	public XmlException(String message, XMLErrorCode xmlErrorCode) {
		super(message);
		this.xmlErrorCode = xmlErrorCode;
	}
	
	public XmlException(String message, Throwable cause, XMLErrorCode xmlErrorCode) {
		super(message, cause);
		this.xmlErrorCode = xmlErrorCode;
	}

	
	public XMLErrorCode getCode() {
		return this.xmlErrorCode;
	}

}
