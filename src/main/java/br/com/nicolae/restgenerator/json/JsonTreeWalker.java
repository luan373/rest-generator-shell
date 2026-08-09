package br.com.nicolae.restgenerator.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.xml.entities.Field;
import br.com.nicolae.restgenerator.xml.enuns.DataType;

public class JsonTreeWalker {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public JsonNode convertJSONToNode(String json) throws IOException {
		return MAPPER.readTree(json);
	}

	public void walkTreeSave(JsonNode root, List<String> jsonSaveList) {
		walker(root, jsonSaveList);
	}

	public void walkTreeCustomSave(String json, List<String> jsonSaveCustomList) throws IOException {
		JsonNode root = MAPPER.readTree(json);
		for (String jsonNavigator : jsonSaveCustomList) {
			String[] jsonArray = jsonNavigator.split("\\.");
			saveJsonToStorage(root, jsonArray);
		}
	}

    private void walker(JsonNode node, List<String> jsonSaveList) {
        if (node.isObject()) {
            node.forEachEntry((name, newNode) -> {
                saveJsonToStorage(name, newNode, jsonSaveList);
                walker(newNode, jsonSaveList);
            });
        } else if (node.isArray()) {
            node.valueStream().forEach(arrayNode -> walker(arrayNode, jsonSaveList));
        }
    }

	private void saveJsonToStorage(String name, JsonNode newNode, List<String> jsonNameList) {
		for (String jsonName : jsonNameList) {
			if (jsonName.equals(name)) {
				Field field = new Field();
				field.setField(newNode.asText());

				if (newNode.isNumber()) {
					field.setDataType(DataType.NUMBER);
				}

				if (newNode.isTextual()) {
					field.setDataType(DataType.STRING);
				}

				if (newNode.isBoolean()) {
					field.setDataType(DataType.BOOLEAN);
				}

				EnvVariableStorageSingleton.getInstance().put(name, field);
			}
		}
	}

	private void saveJsonToStorage(JsonNode jsonNode, String[] jsonArray) {
		for (String jsonName : jsonArray) {
			if (jsonName.contains("[")) {
				jsonNode = jsonNode.get(jsonName.substring(0, jsonName.indexOf("[")));

				int position = Integer.parseInt(jsonName.substring(jsonName.indexOf("[") + 1, jsonName.indexOf("]")));
				jsonNode = jsonNode.get(position);
			} else {
				jsonNode = jsonNode.get(jsonName);
			}
		}

		if (jsonNode != null && !jsonNode.isMissingNode() && jsonNode.isValueNode()) {
			String value = jsonNode.asText();
			if (!value.isBlank() || value.isEmpty()) {
				EnvVariableStorageSingleton.getInstance().put(jsonArray[jsonArray.length - 1],
						new Field(value, DataType.STRING));
			}
		}
	}

}
