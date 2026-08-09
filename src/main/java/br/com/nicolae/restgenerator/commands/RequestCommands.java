package br.com.nicolae.restgenerator.commands;

import org.dom4j.DocumentException;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.CommandGroup;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import br.com.nicolae.restgenerator.generator.ExportGenerator;
import br.com.nicolae.restgenerator.generator.RestGenerator;
import br.com.nicolae.restgenerator.generator.exceptions.CsvException;
import br.com.nicolae.restgenerator.generator.exceptions.JsonException;
import br.com.nicolae.restgenerator.generator.exceptions.ResponseException;
import br.com.nicolae.restgenerator.generator.exceptions.SqlException;
import br.com.nicolae.restgenerator.generator.exceptions.XmlException;
import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.generator.util.ExportStorageSingleton;
import br.com.nicolae.restgenerator.generator.util.RequestStorageSingleton;

@Component
@CommandGroup(name = "Request Commands", prefix = "request")
public class RequestCommands {

	@Command(name = "start", description = "Executa o arquivo XML mencionado.")
	public void start(
			@Option(shortName = 'f', longName = "file", description = "Arquivo XML a ser executado.", defaultValue = "requests.xml") String file,
			@Option(shortName = 'r', longName = "repeat", description = "Quantidade de repetições.", defaultValue = "1") int repeat)
			throws DocumentException {
		try {
			clearSingletons();
			new RestGenerator().readXML(file, repeat);
			System.out.println("Requisição finalizada.");
		} catch (XmlException | JsonException | ResponseException | SqlException e) {
			System.err.println(e.getMessage());
		}
	}

	@Command(name = "start csv", description = "Executa o arquivo XML com dados de um CSV.")
	public void startCsv(
			@Option(shortName = 'f', longName = "file", description = "Arquivo XML a ser executado.", defaultValue = "requests.xml") String file,
			@Option(longName = "csvFile", description = "Arquivo CSV com os dados.", defaultValue = "") String csvFile,
			@Option(shortName = 'd', longName = "delimiter", description = "Delimitador do CSV.", defaultValue = ",") String csvDelimiter) {

		try {
			clearSingletons();
			new RestGenerator().readXmlWithCsv(file, csvFile, csvDelimiter);
			System.out.println("Requisição finalizada.");
		} catch (XmlException | JsonException | ResponseException | CsvException | SqlException e) {
			System.err.println(e.getMessage());
		}
	}

	private void clearSingletons() {
		EnvVariableStorageSingleton.getInstance().clear();
		ExportStorageSingleton.clearInstance();
		RequestStorageSingleton.getInstance().clear();
		ExportGenerator.outputfile = null;
	}
}
