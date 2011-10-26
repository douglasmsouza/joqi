package br.com.joqi.semantico.consulta.resultado;

import java.util.ArrayList;
import java.util.Arrays;

public class ResultSet extends ArrayList<ResultObject> {

	public ResultSet(ResultObject[] array) {
		super(Arrays.asList(array));
	}

	public ResultSet() {
	}

}
