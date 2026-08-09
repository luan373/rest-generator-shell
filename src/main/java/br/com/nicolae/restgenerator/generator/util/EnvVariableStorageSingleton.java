package br.com.nicolae.restgenerator.generator.util;

import java.util.HashMap;
import java.util.Map;

import br.com.nicolae.restgenerator.xml.entities.Field;

public class EnvVariableStorageSingleton {

	private static Map<String, Field> mapEnvStorage;

	private EnvVariableStorageSingleton() {
	}

	public static Map<String, Field> getInstance() {
		if (mapEnvStorage == null) {
			mapEnvStorage = new HashMap<>();
		}
		return mapEnvStorage;
	}

	public static void clear() {
		mapEnvStorage = null;
	}
}
