package br.com.nicolae.restgenerator.generator.util;

import br.com.nicolae.restgenerator.xml.entities.Export;

public class ExportStorageSingleton {

	private static Export export;

	private ExportStorageSingleton() {
	}

	public static Export getInstance() {
		if (export == null) {
			export = new Export();
		}
		return export;
	}
	
	public static void clearInstance() {
		export = new Export();
	}

}
