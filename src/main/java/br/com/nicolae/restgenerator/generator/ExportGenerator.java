package br.com.nicolae.restgenerator.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Element;

import com.opencsv.CSVWriter;

import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.generator.util.ExportStorageSingleton;
import br.com.nicolae.restgenerator.xml.entities.Export;

public class ExportGenerator {

	public static FileWriter outputfile = null;

	public static CSVWriter writer = null;

	public void readExport(Element restGeneratorElement) {
		if (ExportStorageSingleton.getInstance().getExportVariables() == null) {
			setExportAttributes(ExportStorageSingleton.getInstance(), restGeneratorElement.attributes());
		}
	}

	private static void setExportAttributes(Export export, List<Attribute> attributes) {
		for (Attribute attribute : attributes) {
            if (attribute.getName().equals("export-variables")) {
                export.setExportVariables(
                        new ArrayList<>(Arrays.asList(StringUtils.deleteWhitespace(attribute.getValue()).split(","))));
            } else {
                throw new IllegalArgumentException("Atributo não reconhecido:: " + attribute.getName() + ". O atributo aceito é: export-variables.");
            }
		}

    }

	public void writeCSV(File filePath) throws IOException {
		writerHeaders(filePath);

		outputfile = new FileWriter(filePath, true);
		writer = new CSVWriter(outputfile);

		List<String> savedNameList = new ArrayList<>();
		for (String variableToSave : ExportStorageSingleton.getInstance().getExportVariables()) {
			if (EnvVariableStorageSingleton.getInstance().get(variableToSave) != null) {
				savedNameList.add(EnvVariableStorageSingleton.getInstance().get(variableToSave).getField());
			}
		}

		writer.writeNext(savedNameList.toArray(String[]::new));

		writer.close();
	}

	private void writerHeaders(File filePath) throws IOException {
		if (outputfile == null) {
			outputfile = new FileWriter(filePath, true);
			writer = new CSVWriter(outputfile);

			writer.writeNext(ExportStorageSingleton.getInstance().getExportVariables().toArray(String[]::new));

			writer.close();
		}
	}

}
