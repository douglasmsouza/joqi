package br.com.joqi.semantico.consulta.resultado;

import java.util.ArrayList;
import java.util.Arrays;

public class ResultList extends ArrayList<ResultObject> {

	public ResultList(ResultObject[] array) {
		super(Arrays.asList(array));
	}

	public ResultList(ResultSet resultSet) {
		super(resultSet);
	}

	public ResultList() {

	}
}
