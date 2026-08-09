package br.com.nicolae.restgenerator.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.xml.entities.Field;

import java.util.List;

class JsonTreeWalkerTest {

	private final JsonTreeWalker walker = new JsonTreeWalker();

	@BeforeEach
	@AfterEach
	void clearStorage() {
		EnvVariableStorageSingleton.clear();
	}

	@Test
	void shouldSavePrimitiveValuesFromJson() throws Exception {
		String json = """
				{
				  "id": 123,
				  "name": "John Doe",
				  "active": true
				}
				""";

		walker.walkTreeSave(walker.convertJSONToNode(json), List.of("id", "name", "active"));

		assertEquals("123", EnvVariableStorageSingleton.getInstance().get("id").getField());
		assertEquals("John Doe", EnvVariableStorageSingleton.getInstance().get("name").getField());
		assertEquals("true", EnvVariableStorageSingleton.getInstance().get("active").getField());
	}

	@Test
	void shouldSaveNestedValuesFromJson() throws Exception {
		String json = """
				{
				  "user": {
				    "email": "john@example.com"
				  }
				}
				""";

		walker.walkTreeSave(walker.convertJSONToNode(json), List.of("email"));

		assertEquals("john@example.com", EnvVariableStorageSingleton.getInstance().get("email").getField());
	}

	@Test
	void shouldSaveValuesFromArrayItems() throws Exception {
		String json = """
				{
				  "items": [
				    { "code": "A1" },
				    { "code": "A2" }
				  ]
				}
				""";

		walker.walkTreeSave(walker.convertJSONToNode(json), List.of("code"));

		assertEquals("A2", EnvVariableStorageSingleton.getInstance().get("code").getField());
	}

	@Test
	void shouldSaveCustomPathWithArrayIndex() {
		String json = """
				{
				  "users": [
				    { "profile": { "name": "Alice" } },
				    { "profile": { "name": "Bob" } }
				  ]
				}
				""";

		walker.walkTreeCustomSave(json, List.of("users[1].profile.name"));

		assertEquals("Bob", EnvVariableStorageSingleton.getInstance().get("name").getField());
	}
}
