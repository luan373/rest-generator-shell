package br.com.nicolae.restgenerator.xml.entities;

import br.com.nicolae.restgenerator.generator.enuns.Random;

public class EnvVariable {

	private Field field;

	private String name;

	private Random randomName;

	private String randomMinimum;

	private String randomMaximum;
	
	private String readFile;

	public EnvVariable() {
		this.field = new Field();
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Random getRandomName() {
		return randomName;
	}

	public void setRandomName(Random randomName) {
		this.randomName = randomName;
	}

	public String getRandomMinimum() {
		return randomMinimum;
	}

	public void setRandomMinimum(String randomMinimum) {
		this.randomMinimum = randomMinimum;
	}

	public String getRandomMaximum() {
		return randomMaximum;
	}

	public void setRandomMaximum(String randomMaximum) {
		this.randomMaximum = randomMaximum;
	}

	public String getReadFile() {
		return readFile;
	}

	public void setReadFile(String readFile) {
		this.readFile = readFile;
	}

}
