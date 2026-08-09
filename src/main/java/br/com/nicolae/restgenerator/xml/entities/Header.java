package br.com.nicolae.restgenerator.xml.entities;

public class Header {

	public Header() {
		super();
	}

	public Header(String name, String value) {
		super();
		this.setKey(name);
		this.value = value;
	}

	private String key;

	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
