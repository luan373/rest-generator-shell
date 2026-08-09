package br.com.nicolae.restgenerator.generator.util;

import java.util.ArrayList;

import br.com.nicolae.restgenerator.csv.CSV;

public class CsvSingleton {

	private static CSV csv = new CSV(new ArrayList<>(), new ArrayList<>());

	private CsvSingleton() {
	}

	public static CSV getInstance() {
		return csv;
	}

	public static void setCsv(CSV csv) {
		CsvSingleton.csv = csv;
	}
}
