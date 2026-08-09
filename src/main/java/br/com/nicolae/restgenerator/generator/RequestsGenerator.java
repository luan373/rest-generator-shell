package br.com.nicolae.restgenerator.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import br.com.nicolae.restgenerator.generator.exceptions.JsonException;
import br.com.nicolae.restgenerator.generator.exceptions.ResponseException;
import br.com.nicolae.restgenerator.generator.exceptions.SqlException;
import br.com.nicolae.restgenerator.generator.exceptions.XmlException;
import br.com.nicolae.restgenerator.generator.exceptions.enums.JSONErrorCode;
import br.com.nicolae.restgenerator.generator.exceptions.enums.ResponseErrorCode;
import br.com.nicolae.restgenerator.generator.exceptions.enums.XMLErrorCode;
import br.com.nicolae.restgenerator.generator.util.FillUtil;
import br.com.nicolae.restgenerator.generator.util.RequestStorageSingleton;
import br.com.nicolae.restgenerator.generator.util.Util;
import br.com.nicolae.restgenerator.json.JsonTreeWalker;
import br.com.nicolae.restgenerator.sql.SqlRunner;
import br.com.nicolae.restgenerator.xml.entities.Header;
import br.com.nicolae.restgenerator.xml.entities.Request;
import br.com.nicolae.restgenerator.xml.entities.Requests;
import br.com.nicolae.restgenerator.xml.entities.Sql;
import br.com.nicolae.restgenerator.xml.enuns.HttpMethod;
import br.com.nicolae.restgenerator.xml.enuns.SGBD;

public class RequestsGenerator {

	private final JsonTreeWalker jsonTreeWalker = new JsonTreeWalker();

	public void readRequests(Element restGeneratorElement) throws XmlException {
		for (Element requestsElement : restGeneratorElement.elements()) {
			if (requestsElement.getName().contains("request")) {
				readRequest(requestsElement);
			} else {
				readSql(requestsElement);
			}
		}
	}

	private void readSql(Element requestsElement) throws XmlException {
		Sql sql = new Sql();
        setSqlAttributes(sql, requestsElement.attributes());

        if (requestsElement.getText() == null || requestsElement.getText().isBlank()) {
			throw new XmlException("A query não pode estar em branco.", XMLErrorCode.NO_SQL_QUERY);
		} else {
			sql.setQuery(FillUtil.fillTextWithEnv(requestsElement.getTextTrim()));
		}

		RequestStorageSingleton.getInstance().add(new Requests(sql));
	}

	private void readRequest(Element requestsElement) throws XmlException {
		Request request = new Request();
        setRequestAttributes(request, requestsElement.attributes());

        for (Element requestElement : requestsElement.elements()) {
			if (requestElement.hasContent() && requestElement.getName().contains("body")) {
				request.setBody(requestElement.getTextTrim());
			} else if (requestElement.hasContent() && requestElement.getName().contains("headers")) {
				request.setHeadersList(setHeaders(request, requestElement));
			} else if (requestElement.hasContent()
					&& !(requestElement.getName().contains("body") || requestElement.getName().contains("headers"))) {
				throw new XmlException("O primeiro elemento precisa ser body ou headers.", XMLErrorCode.NO_BODY);
			}
		}

		RequestStorageSingleton.getInstance().add(new Requests(request));
	}

	private HttpHeaders setHeaders(Request request, Element requestElement) throws XmlException {
		HttpHeaders headersList = new HttpHeaders();

		for (Element headerElement : requestElement.elements()) {
			if (headerElement.hasContent() && headerElement.getName().contains("header")) {
				Header header = setHeadersAttributes(headerElement.attributes());
				header.setValue(FillUtil.fillTextWithEnv(headerElement.getTextTrim()));
				headersList.add(header.getKey(), header.getValue());
			} else if (requestElement.hasContent()) {
				throw new XmlException("Não foi encontrado o elemento header", XMLErrorCode.NO_HEADER);
			}
		}

		return headersList;
	}

	private Header setHeadersAttributes(List<Attribute> attributes) {
		Header header = new Header();
		for (Attribute attribute : attributes) {
            if (attribute.getName().equals("header-key")) {
                header.setKey(attribute.getValue());
            } else {
                throw new IllegalArgumentException(
                        "Atributo não reconhecido: " + attribute.getName() + ". O atributo aceito é: header-key");
            }
		}

		return header;
	}

	private void setRequestAttributes(Request request, List<Attribute> attributes) {
		for (Attribute attribute : attributes) {
			switch (attribute.getName()) {
			case "request-name":
				request.setName(attribute.getValue());
				break;
			case "request-url":
				request.setUrl(attribute.getValue());
				break;
			case "request-type":
				request.setHttpMethod(HttpMethod.valueOf(attribute.getValue()));
				break;
			case "response-save":
				request.setJsonSaveList(
						new ArrayList<>(Arrays.asList(StringUtils.deleteWhitespace(attribute.getValue()).split(","))));
				break;
			case "response-save-custom":
				request.setJsonSaveCustomList(
						new ArrayList<>(Arrays.asList(StringUtils.deleteWhitespace(attribute.getValue()).split(","))));
				break;
			case "request-wait":
				request.setTimeWait(Integer.parseInt(attribute.getValue()));
				break;
			default:
				throw new IllegalArgumentException("Atributo não reconhecido: " + attribute.getName()
						+ ". Os atributos aceitos são: request-name, request-url, request-type, response-save, response-save-custom e request-wait");
			}
		}

    }

	private void setSqlAttributes(Sql sql, List<Attribute> attributes) {
		for (Attribute attribute : attributes) {
			switch (attribute.getName()) {
			case "database":
				sql.setDatabase(attribute.getValue());
				break;
			case "host":
				sql.setHost(attribute.getValue());
				break;
			case "password":
				sql.setPassword(attribute.getValue());
				break;
			case "port":
				sql.setPort(Integer.parseInt(attribute.getValue()));
				break;
			case "sgbd":
				sql.setSgbd(SGBD.valueOf(attribute.getValue()));
				break;
			case "url":
				sql.setUrl(attribute.getValue());
				break;
			case "userName":
				sql.setUserName(attribute.getValue());
				break;
			default:
				throw new IllegalArgumentException("Atributo não reconhecido: " + attribute.getName()
						+ ". Os atributos aceitos são: database, host, password, port, sgbd, url e userName");
			}
		}

    }

	public void execute() throws JsonException, ResponseException, XmlException, SqlException {
		int count = 1;
		for (Requests requests : RequestStorageSingleton.getInstance()) {
			if (requests.getRequest() != null) {
				executeRequest(requests.getRequest());
			}

			if (requests.getSql() != null) {
				executeSql(requests.getSql());
			}
			
			System.out.println("Requisição " + (requests.getRequest() != null ? requests.getRequest().getName()
					: "SQL") + " N.º " + count++ + " de " + RequestStorageSingleton.getInstance().size()
						+ " efetuada com sucesso.");
		}
	}

	private void executeSql(Sql sql) throws SqlException {
		SqlRunner.executeSQL(sql);
	}

	private void executeRequest(Request request) throws XmlException, JsonException, ResponseException {
		request.setUrl(FillUtil.fillTextWithEnv(request.getUrl()));

		switch (request.getHttpMethod()) {
		case GET:
			executeGET(request, 1);
			break;
		case POST:
			executePOST(request, 1);
			break;
		case PUT:
			executePUT(request, 1);
			break;
		case DELETE:
			executeDELETE(request, 1);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + request.getHttpMethod());
		}
	}

	private void executeGET(Request request, int count) throws JsonException {
		RestClient client = RestClient.create(request.getUrl());
		String entity = client.get()
				.headers(headers -> headers.addAll(request.getHeadersList()))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (req, res) -> {
					throw new RestClientResponseException(res.getStatusCode().value(), request, res);
				})
				.body(String.class);

		System.out.println(200);
		processResponseEntity(request, entity);
	}

	private void executePOST(Request request, int count) throws JsonException, ResponseException {
		String json = FillUtil.putEnvInJson(request.getBody());
		RestClient client = RestClient.create(request.getUrl());
		String entity;
		try {
			entity = client.post()
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.headers(headers -> headers.addAll(request.getHeadersList()))
					.body(json)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (req, res) -> {
						throw new RestClientResponseException(res.getStatusCode().value(), request, res);
					})
					.body(String.class);
		} catch (RestClientResponseException e) {
			handleError(request, e.getStatusCode(), e.getResponseBody(), json, count, this::executePOST);
			return;
		}

		System.out.println(200);
		processResponseEntity(request, entity);
	}

	private void executePUT(Request request, int count) throws JsonException, ResponseException {
		String json = FillUtil.putEnvInJson(request.getBody());
		RestClient client = RestClient.create(request.getUrl());
		String entity;
		try {
			entity = client.put()
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.headers(headers -> headers.addAll(request.getHeadersList()))
					.body(json)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (req, res) -> {
						throw new RestClientResponseException(res.getStatusCode().value(), request, res);
					})
					.body(String.class);
		} catch (RestClientResponseException e) {
			handleError(request, e.getStatusCode(), e.getResponseBody(), json, count, this::executePUT);
			return;
		}

		System.out.println(200);
		processResponseEntity(request, entity);
	}

	private void executeDELETE(Request request, int count) throws ResponseException {
		RestClient client = RestClient.create(request.getUrl());
		try {
			client.delete()
					.headers(headers -> headers.addAll(request.getHeadersList()))
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.onStatus(HttpStatusCode::isError, (req, res) -> {
						throw new RestClientResponseException(res.getStatusCode().value(), request, res);
					})
					.toBodilessEntity();
		} catch (RestClientResponseException e) {
			handleError(request, e.getStatusCode(), e.getResponseBody(), null, count, this::executeDELETE);
			return;
		}

		System.out.println(200);
	}

	private void processResponseEntity(Request request, String entity) throws JsonException {
		if (entity != null && (!request.getJsonSaveList().isEmpty() || !request.getJsonSaveCustomList().isEmpty())) {
			try {
				jsonTreeWalker.walkTreeSave(jsonTreeWalker.convertJSONToNode(entity), request.getJsonSaveList());
				jsonTreeWalker.walkTreeCustomSave(entity, request.getJsonSaveCustomList());
			} catch (IOException e) {
				throw new JsonException("O formato do Json está incorreto, verifique e tente novamente", e,
						JSONErrorCode.JSON_FORMAT);
			}
		}
	}

	private void handleError(Request request, int statusCode, String responseBody, String requestBody, int count,
			RequestExecutor executor) throws ResponseException {
		if (count <= 3) {
			System.err.println("Erro na requisição " + request.getName() + ". Tentativa " + count + " de 3");
			Util.sleep(request.getTimeWait());
			try {
				executor.execute(request, count + 1);
			} catch (JsonException | XmlException e) {
				throw new ResponseException(e.getMessage(), ResponseErrorCode.STATUS_ERROR);
			}
		} else {
			System.out.println("URL: " + request.getUrl());
			if (requestBody != null) {
				System.out.println("REQUISIÇÃO: " + requestBody);
			}
			if (responseBody != null) {
				System.out.println("RESPOSTA: " + responseBody);
			}
			throw new ResponseException("Erro " + statusCode + " na requisição " + request.getName(),
					ResponseErrorCode.STATUS_ERROR);
		}
	}

	@FunctionalInterface
	private interface RequestExecutor {
		void execute(Request request, int count) throws JsonException, ResponseException, XmlException;
	}

	private static class RestClientResponseException extends RuntimeException {
		private final int statusCode;
		private final String responseBody;

		RestClientResponseException(int statusCode, Request request, org.springframework.http.client.ClientHttpResponse response) {
			super("Erro " + statusCode + " na requisição " + request.getName());
			this.statusCode = statusCode;
			String body = null;
			try {
				body = new String(response.getBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
			} catch (IOException _) {
            }
			this.responseBody = body;
		}

		int getStatusCode() {
			return statusCode;
		}

		String getResponseBody() {
			return responseBody;
		}
	}

}
