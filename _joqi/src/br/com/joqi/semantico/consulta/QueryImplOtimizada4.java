package br.com.joqi.semantico.consulta;

import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.consulta.resultado.ResultSet;

public class QueryImplOtimizada4 {

	class Tupla extends ResultObject {
	}

	private ArvoreConsulta arvoreConsulta;

	public QueryImplOtimizada4(ArvoreConsulta arvoreConsulta) {
		this.arvoreConsulta = arvoreConsulta;
	}

	public ResultSet getResultSet() {
		ResultSet resultSet = new ResultSet();
		//
		//
		return resultSet;
	}
}
