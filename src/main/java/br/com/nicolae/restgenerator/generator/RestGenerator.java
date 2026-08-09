package br.com.nicolae.restgenerator.generator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import br.com.nicolae.restgenerator.csv.CsvUtil;
import br.com.nicolae.restgenerator.generator.exceptions.CsvException;
import br.com.nicolae.restgenerator.generator.exceptions.JsonException;
import br.com.nicolae.restgenerator.generator.exceptions.ResponseException;
import br.com.nicolae.restgenerator.generator.exceptions.SqlException;
import br.com.nicolae.restgenerator.generator.exceptions.XmlException;
import br.com.nicolae.restgenerator.generator.exceptions.enums.XMLErrorCode;
import br.com.nicolae.restgenerator.generator.util.CsvSingleton;
import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.generator.util.ExportStorageSingleton;
import br.com.nicolae.restgenerator.generator.util.RequestStorageSingleton;
import br.com.nicolae.restgenerator.xml.XmlReader;

public class RestGenerator {

	private final RequestsGenerator requestsGenerator;

	private final EnvVariableGenerator envVariableGenerator;

	private final ExportGenerator exportGenerator;

	public RestGenerator() {
		this.requestsGenerator = new RequestsGenerator();
		this.envVariableGenerator = new EnvVariableGenerator();
		this.exportGenerator = new ExportGenerator();
	}

	public void readXmlWithCsv(String file, String csvFile, String csvDelimiter)
			throws XmlException, JsonException, ResponseException, CsvException, SqlException {
		CsvUtil.readCSV(csvFile);
		readXML(file, CsvSingleton.getInstance().columns().size());
	}

	public void readXML(String file, int repeat) throws XmlException, JsonException, ResponseException, SqlException {
		XmlReader xmlReader = new XmlReader();
		Document document;
		try {
			document = xmlReader.parse(new File(file));
			
			if (repeat <= 0) {
				System.out.println("A quantidade de repetições deve ser maior que zero.");
				return;
			}
			
			if (repeat > 100000) {
				System.out.println("A quantidade máxima de repetições é 100.000.");
				return;
			}
			
			for (int i = 1; i <= repeat; i++) {
				clearSingletons();
				CsvUtil.loadEnvCSVData(i);
				readRestGenerator(document.getRootElement());

				this.requestsGenerator.execute();
				export();
			}
		} catch (DocumentException e) {
			throw new XmlException(e.getMessage(), e, XMLErrorCode.XML_ERROR);
		}
	}

	private void clearSingletons() {
		EnvVariableStorageSingleton.getInstance().clear();
		RequestStorageSingleton.getInstance().clear();
	}

	private void export() {
		try {
			this.exportGenerator.writeCSV(new File(ExportStorageSingleton.getInstance().getFileName()));
			System.out.println(
					"Arquivo " + ExportStorageSingleton.getInstance().getFileName() + " exportado com sucesso.");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Não foi possível salvar o arquivo csv.");
		}
	}

	private void readRestGenerator(Element xmlElement) throws XmlException, JsonException, ResponseException {
		if ("restGenerator".equals(xmlElement.getName())) {
			for (Iterator<Element> it = xmlElement.elementIterator(); it.hasNext();) {
				Element restGeneratorElement = it.next();
				if ("enviroments".equals(restGeneratorElement.getName())) {
					this.envVariableGenerator.readEnvVariables(restGeneratorElement);
				} else if ("requests".equals(restGeneratorElement.getName())) {
					this.requestsGenerator.readRequests(restGeneratorElement);
				} else if ("export".equals(restGeneratorElement.getName())) {
					this.exportGenerator.readExport(restGeneratorElement);
				}
			}
		} else {
			throw new XmlException("Não há o elemento restGenerator para iniciar a leitura do XML",
					XMLErrorCode.NO_REST_GENERATOR);
		}
	}

}
