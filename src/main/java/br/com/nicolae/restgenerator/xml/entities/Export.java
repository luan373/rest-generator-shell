package br.com.nicolae.restgenerator.xml.entities;

import java.time.LocalDateTime;
import java.util.List;

public class Export {

	public Export() {
		super();
	}

	public Export(List<String> exportVariables) {
		super();
		this.exportVariables = exportVariables;
	}

	private List<String> exportVariables;
	
	private String fileName = "rest-generator" + LocalDateTime.now().hashCode() + ".csv";

	public List<String> getExportVariables() {
		return exportVariables;
	}

	public void setExportVariables(List<String> exportVariables) {
		this.exportVariables = exportVariables;
	}

	public String getFileName() {
		return fileName;
	}

}
