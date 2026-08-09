package br.com.nicolae.restgenerator.xml.entities;

import br.com.nicolae.restgenerator.xml.enuns.SGBD;

public class Sql {

	private String query;

	private SGBD sgbd;

	private String host;

	private int port;

	private String database;

	private String userName;

	private String password;

	private String url;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public SGBD getSgbd() {
		return sgbd;
	}

	public void setSgbd(SGBD sgbd) {
		this.sgbd = sgbd;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
