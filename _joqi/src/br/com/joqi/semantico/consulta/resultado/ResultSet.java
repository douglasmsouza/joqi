package br.com.joqi.semantico.consulta.resultado;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;

public class ResultSet extends LinkedHashSet<ResultObject> {

	public ResultSet(ResultObject[] array) {
		super(Arrays.asList(array));
	}

	public ResultSet(ResultList resultList) {
		super(resultList);
	}

	public ResultSet(Collection<ResultObject> collection) {
		super(collection);
	}

	public ResultSet() {

	}

}
