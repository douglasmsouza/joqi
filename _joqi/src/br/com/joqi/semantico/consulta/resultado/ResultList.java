package br.com.joqi.semantico.consulta.resultado;

import java.util.Arrays;
import java.util.HashSet;

public class ResultList extends HashSet<ResultObject> {

	public ResultList(ResultObject[] array) {
		super(Arrays.asList(array));
	}

	public ResultList(ResultSet resultSet) {
		super(resultSet);
	}

	public ResultList() {

	}
}
