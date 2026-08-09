package br.com.nicolae.restgenerator.generator.util;

import java.util.ArrayList;
import java.util.List;

import br.com.nicolae.restgenerator.xml.entities.Requests;

public class RequestStorageSingleton {

	private static List<Requests> requestsList;

	private RequestStorageSingleton() {
	}

	public static List<Requests> getInstance() {
		if (requestsList == null) {
			requestsList = new ArrayList<>();
		}
		return requestsList;
	}

	public static void clear() {
		requestsList = null;
	}

}
