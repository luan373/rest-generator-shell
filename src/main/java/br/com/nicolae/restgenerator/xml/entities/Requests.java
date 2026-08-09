package br.com.nicolae.restgenerator.xml.entities;

public class Requests {

	public Requests() {
		super();
	}

	public Requests(Object object) {
		if (object instanceof Request) {
			this.request = (Request) object;
		}
		if (object instanceof Sql) {
			this.sql = (Sql) object;
		}

	}

	public Requests(Request request, Sql sql) {
		super();
		this.request = request;
		this.sql = sql;
	}

	private Request request;

	private Sql sql;

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	public Sql getSql() {
		return sql;
	}

	public void setSql(Sql sql) {
		this.sql = sql;
	}

}
