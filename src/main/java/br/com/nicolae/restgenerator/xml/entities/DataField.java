package br.com.nicolae.restgenerator.xml.entities;

import java.util.Map;

public class DataField extends Field {

	Map<String, Object> dataValue;

	public Map<String, Object> getDataValue() {
		return dataValue;
	}

	public void setDataValue(Map<String, Object> dataValue) {
		this.dataValue = dataValue;
	}

}
