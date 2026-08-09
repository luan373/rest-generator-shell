package br.com.nicolae.restgenerator.csv;

import java.util.List;

public record CSV(List<String> headers, List<Columns> columns) {
}
