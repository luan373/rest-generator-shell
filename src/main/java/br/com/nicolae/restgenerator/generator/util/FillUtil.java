package br.com.nicolae.restgenerator.generator.util;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import br.com.nicolae.restgenerator.generator.exceptions.JsonException;
import br.com.nicolae.restgenerator.generator.exceptions.XmlException;
import br.com.nicolae.restgenerator.generator.exceptions.enums.JSONErrorCode;
import br.com.nicolae.restgenerator.generator.exceptions.enums.XMLErrorCode;
import br.com.nicolae.restgenerator.json.JsonUtil;
import br.com.nicolae.restgenerator.xml.entities.Field;

public class FillUtil {

	public static String fillTextWithEnv(String text) throws XmlException {
		if (text.contains("{{")) {
			String[] values = StringUtils.substringsBetween(text, "{{", "}}");

			if (values != null) {
				for (String value : values) {
					Field field = EnvVariableStorageSingleton.getInstance().get(value);

					if (field != null) {
						text = text.replace("{{" + value + "}}", field.getField());
					} else {
						throw new XmlException("Não foi encontrada a variável de nome " + value,
								XMLErrorCode.NO_ENVIROMENT);
					}
				}
			}
		}

		return text;
	}

	public static String putEnvInJson(String json) throws JsonException {
		for (Entry<String, Field> entry : EnvVariableStorageSingleton.getInstance().entrySet()) {
			String keyName = "{{" + entry.getKey() + "}}";
			if (json.contains(keyName)) {
				json = json.replace(keyName, entry.getValue().getField());
			}
		}


        if(!new JsonUtil().isValidJSON(json)){
            throw new JsonException("O formato deste json está incorreto: " + json, JSONErrorCode.JSON_FORMAT);
        };

		return json;
	}

}
