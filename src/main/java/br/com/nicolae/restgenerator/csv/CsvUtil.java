package br.com.nicolae.restgenerator.csv;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import br.com.nicolae.restgenerator.generator.exceptions.CsvException;
import br.com.nicolae.restgenerator.generator.exceptions.enums.CsvErrorCode;
import br.com.nicolae.restgenerator.generator.util.CsvSingleton;
import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.xml.entities.Field;
import br.com.nicolae.restgenerator.xml.enuns.DataType;

public class CsvUtil {

	public static void loadEnvCSVData(int position) {
		CSV csv = CsvSingleton.getInstance();
		if (!csv.headers().isEmpty()) {
			Columns columns = csv.columns().get(position - 1);

			for (int i = 0; i < csv.headers().size(); i++) {
				EnvVariableStorageSingleton.getInstance().put(csv.headers().get(i),
						new Field(columns.columns().get(i), DataType.STRING));
			}
		}
	}

	public static void readCSV(String file) throws CsvException {

		try {
			FileReader filereader = new FileReader(file);
			try (CSVReader csvReader = new CSVReader(filereader)) {
				String[] nextRecord;

				int i = 0;

				List<String> headers = new ArrayList<>();
				List<Columns> columnsList = new ArrayList<>();
				while ((nextRecord = csvReader.readNext()) != null) {

					List<String> cells = new ArrayList<>();
					for (String cell : nextRecord) {
						if (i == 0) {
							headers.add(cell);
						} else {
							cells.add(cell);
						}
					}
					if (!cells.isEmpty()) {
						columnsList.add(new Columns(cells));
					}
					i++;
				}

				CsvSingleton.setCsv(new CSV(headers, columnsList));

				System.out.println(CsvSingleton.getInstance());

            }
		} catch (Exception e) {
			throw new CsvException("Não foi encontrado o arquivo " + file, CsvErrorCode.CSV_FILE_NOT_FOUND);
		}
	}

}
