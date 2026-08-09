package br.com.nicolae.restgenerator.generator;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.generator.util.RequestStorageSingleton;
import br.com.nicolae.restgenerator.xml.entities.Request;
import br.com.nicolae.restgenerator.xml.entities.Requests;
import br.com.nicolae.restgenerator.xml.enuns.HttpMethod;

class RequestsGeneratorTest {

	private WireMockServer wireMockServer;
	private RequestsGenerator requestsGenerator;

	@BeforeEach
	void setup() {
		wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
		wireMockServer.start();
		com.github.tomakehurst.wiremock.client.WireMock.configureFor("localhost", wireMockServer.port());
		requestsGenerator = new RequestsGenerator();
		EnvVariableStorageSingleton.clear();
		RequestStorageSingleton.clear();
	}

	@AfterEach
	void tearDown() {
		wireMockServer.stop();
		EnvVariableStorageSingleton.clear();
		RequestStorageSingleton.clear();
	}

	@Test
	void shouldExecuteGetRequestAndStoreResponseVariables() throws Exception {
		stubFor(get(urlEqualTo("/users/1"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody("""
								{"id": 1, "name": "Alice"}
								""")));

		Request request = new Request();
		request.setName("get-user");
		request.setUrl(baseUrl() + "/users/1");
		request.setHttpMethod(HttpMethod.GET);
		request.setJsonSaveList(java.util.List.of("id", "name"));

		RequestStorageSingleton.getInstance().add(new Requests(request));

		requestsGenerator.execute();

		assertEquals("1", EnvVariableStorageSingleton.getInstance().get("id").getField());
		assertEquals("Alice", EnvVariableStorageSingleton.getInstance().get("name").getField());
	}

	@Test
	void shouldExecutePostRequestAndStoreResponseVariables() throws Exception {
		stubFor(post(urlEqualTo("/users"))
				.withRequestBody(equalToJson("""
						{"name": "Bob"}
						"""))
				.willReturn(aResponse()
						.withStatus(201)
						.withHeader("Content-Type", "application/json")
						.withBody("""
								{"id": 42, "name": "Bob"}
								""")));

		Request request = new Request();
		request.setName("create-user");
		request.setUrl(baseUrl() + "/users");
		request.setHttpMethod(HttpMethod.POST);
		request.setBody("""
				{"name": "Bob"}
				""");
		request.setJsonSaveList(java.util.List.of("id", "name"));

		RequestStorageSingleton.getInstance().add(new Requests(request));

		requestsGenerator.execute();

		assertEquals("42", EnvVariableStorageSingleton.getInstance().get("id").getField());
		assertEquals("Bob", EnvVariableStorageSingleton.getInstance().get("name").getField());
	}

	private String baseUrl() {
		return "http://localhost:" + wireMockServer.port();
	}
}
