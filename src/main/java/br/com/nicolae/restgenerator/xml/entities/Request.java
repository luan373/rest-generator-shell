package br.com.nicolae.restgenerator.xml.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;

import br.com.nicolae.restgenerator.xml.enuns.HttpMethod;

public class Request {

	public Request() {
	}

	private String name;

	private String url;

	private HttpMethod httpMethod;

	private String body;

	private int timeWait = 1;

	private List<String> jsonSaveList = new ArrayList<>();

	private List<String> jsonSaveCustomList = new ArrayList<>();

	private HttpHeaders headersList = new HttpHeaders();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getTimeWait() {
		return timeWait;
	}

	public void setTimeWait(int timeWait) {
		this.timeWait = timeWait;
	}

	public List<String> getJsonSaveList() {
		return jsonSaveList;
	}

	public void setJsonSaveList(List<String> jsonNameList) {
		this.jsonSaveList = jsonNameList;
	}

	public List<String> getJsonSaveCustomList() {
		return jsonSaveCustomList;
	}

	public void setJsonSaveCustomList(List<String> jsonSaveCustomList) {
		this.jsonSaveCustomList = jsonSaveCustomList;
	}

	public HttpHeaders getHeadersList() {
		return headersList;
	}

	public void setHeadersList(HttpHeaders headersList) {
		this.headersList = headersList;
	}
}
