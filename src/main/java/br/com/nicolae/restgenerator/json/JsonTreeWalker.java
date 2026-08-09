package br.com.nicolae.restgenerator.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.xml.entities.Field;
import br.com.nicolae.restgenerator.xml.enuns.DataType;

public class JsonTreeWalker {

	public JsonNode convertJSONToNode(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(json);

		return jsonNode;
	}

	public void walkTreeSave(JsonNode root, List<String> jsonSaveList) {
		walker(null, root, jsonSaveList);
	}

	public void walkTreeCustomSave(String json, List<String> jsonSaveCustomList) {
		for (String jsonNavigator : jsonSaveCustomList) {
			String jsonArray[] = jsonNavigator.split("\\.");
			saveJsonToStorage(JsonParser.parseString(json), jsonArray);
		}
		
	}

	private void walker(String nodename, JsonNode node, List<String> jsonSaveList) {
		// String nameToPrint = nodename != null ? nodename : "must_be_root";
		// System.out.println("walker - node name: " + nameToPrint);
		if (node.isObject()) {
			Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();

			ArrayList<Map.Entry<String, JsonNode>> nodesList = new ArrayList<>();
		iterator.forEachRemaining(nodesList::add);
			// System.out.println("Walk Tree - root:" + node + ", elements keys:" +
			// nodesList);
			for (Map.Entry<String, JsonNode> nodEntry : nodesList) {
				String name = nodEntry.getKey();
				JsonNode newNode = nodEntry.getValue();

				saveJsonToStorage(name, newNode, jsonSaveList);
				// System.out.println(" entry - key: " + name + ", value:" + node);
				walker(name, newNode, jsonSaveList);
			}
		} else if (node.isArray()) {
		Iterator<JsonNode> arrayItemsIterator = node.elements();
		ArrayList<JsonNode> arrayItemsList = new ArrayList<>();
		arrayItemsIterator.forEachRemaining(arrayItemsList::add);
		for (JsonNode arrayNode : arrayItemsList) {
				walker("array item", arrayNode, jsonSaveList);
			}
		} /**
			 * else { if (node.isValueNode()) { System.out.println(" valueNode: " +
			 * node.asText()); } else { System.out.println(" node some other type"); } }
			 */
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

	private void saveJsonToStorage(JsonElement jsonElement, String[] jsonArray) {
		for (String jsonName : jsonArray) {
			if (jsonName.contains("[")) {
				jsonElement = jsonElement.getAsJsonObject().get(jsonName.substring(0, jsonName.indexOf("[")));

				int position = Integer.parseInt(jsonName.substring(jsonName.indexOf("[") + 1, jsonName.indexOf("]")));
				jsonElement = jsonElement.getAsJsonArray().get(position);
			} else {
				jsonElement = jsonElement.getAsJsonObject().get(jsonName);
			}
		}
		
		if (jsonElement != null && (!jsonElement.getAsString().isBlank() || jsonElement.getAsString().isEmpty())) {
			EnvVariableStorageSingleton.getInstance().put(jsonArray[jsonArray.length -1],
					new Field(jsonElement.getAsString(), DataType.STRING));
		}
	}

}
