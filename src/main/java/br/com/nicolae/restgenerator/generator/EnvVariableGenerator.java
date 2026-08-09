package br.com.nicolae.restgenerator.generator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;

import br.com.nicolae.restgenerator.generator.enuns.Random;
import br.com.nicolae.restgenerator.generator.exceptions.XmlException;
import br.com.nicolae.restgenerator.generator.exceptions.enums.XMLErrorCode;
import br.com.nicolae.restgenerator.generator.util.EnvUtil;
import br.com.nicolae.restgenerator.generator.util.EnvVariableStorageSingleton;
import br.com.nicolae.restgenerator.generator.util.RandomUtil;
import br.com.nicolae.restgenerator.xml.entities.EnvVariable;
import br.com.nicolae.restgenerator.xml.entities.Field;
import br.com.nicolae.restgenerator.xml.enuns.DataType;

public class EnvVariableGenerator {

	public void readEnvVariables(Element restGeneratorElement) throws XmlException {
		for (Element envElement : restGeneratorElement.elements()) {
			EnvVariable envVariable = new EnvVariable();
			envVariable = setAttributes(envVariable, envElement.attributes());
			if (envVariable.getReadFile() != null) {
				File file = new File(envVariable.getReadFile());
			try {
				envVariable.setField(new Field(Files.readString(file.toPath(), StandardCharsets.UTF_8), DataType.STRING));
			} catch (IOException e) {
					throw new XmlException("Não foi possível ler o arquivo " + envVariable.getReadFile()
							+ " da variável " + envVariable.getName(), XMLErrorCode.NO_ENVIROMENT);
				}
			} else {
				if (envVariable.getRandomName() != null) {
					envVariable.setField(fillRandomField(envVariable.getRandomName(), envVariable.getRandomMinimum(),
							envVariable.getRandomMaximum()));
				} else {
					if (!envElement.getText().isEmpty() || !envElement.getText().isBlank()) {
						envVariable.getField().setField(EnvUtil.getText(envElement.getText().trim()));
					} else {
						throw new XmlException("Não foi encontrado o valor da variável " + envVariable.getName(),
								XMLErrorCode.NO_ENVIROMENT);
					}
				}
			}

			EnvVariableStorageSingleton.getInstance().put(envVariable.getName(), envVariable.getField());
		}
	}

	private Field fillRandomField(Random random, String minimum, String maximum) {
		String field = null;
		switch (random) {
		case BIRTHDAY:
			field = RandomUtil.randomBirthday();
			break;
		case DATE:
			field = RandomUtil.randomDate();
			break;
		case DOUBLE:
			field = RandomUtil.randomDouble(Double.parseDouble(minimum), Double.parseDouble(maximum));
			break;
		case FIRST_NAME:
			field = RandomUtil.randomFirstName();
			break;
		case FULL_NAME:
			field = RandomUtil.randomFullName();
			break;
		case FUTURE_DATE:
			field = RandomUtil.randomFutureDate();
			break;
		case INT:
			field = RandomUtil.randomInt(Integer.parseInt(minimum), Integer.parseInt(maximum));
			break;
		case LAST_NAME:
			field = RandomUtil.randomLastName();
			break;
		case PAST_DATE:
			field = RandomUtil.randomPastDate();
			break;
		case TELEPHONE:
			field = RandomUtil.randomTelephone();
			break;
		case CELLPHONE:
			field = RandomUtil.randomCellPhone();
			break;
		case CPF:
			field = RandomUtil.randomCPF();
			break;
		case CNPJ:
			field = RandomUtil.randomCNPJ();
			break;
		default:
			break;
		}

		return new Field(field, null);
	}

	public static EnvVariable setAttributes(EnvVariable envVariable, List<Attribute> attributes) {
		for (Attribute attribute : attributes) {
			switch (attribute.getName()) {
			case "variable-name":
				envVariable.setName(attribute.getValue());
				break;
			case "random-name":
				envVariable.setRandomName(Random.valueOf(attribute.getValue()));
				break;
			case "random-min":
				envVariable.setRandomMinimum(attribute.getValue());
				break;
			case "random-max":
				envVariable.setRandomMaximum(attribute.getValue());
				break;
			case "read-file":
				envVariable.setReadFile(attribute.getValue());
				break;
			case "number":
				if (attribute.getValue().equals("true")) {
					envVariable.getField().setDataType(DataType.NUMBER);
				}
				break;
			default:
				throw new IllegalArgumentException("Atributo não reconhecido: " + attribute.getName()
						+ ". Os atributos aceitos são: variable-name, random-name, random-min, random-max e read-file.");
			}
		}

		return envVariable;

	}

}
