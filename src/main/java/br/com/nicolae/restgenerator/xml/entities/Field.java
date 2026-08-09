package br.com.nicolae.restgenerator.xml.entities;

import br.com.nicolae.restgenerator.xml.enuns.DataType;

public class Field {

	public Field(String field, DataType dataType) {
		super();
		this.field = field;
		this.dataType = dataType;
	}

	public Field() {
	}

	private String field;

	private DataType dataType;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

}
