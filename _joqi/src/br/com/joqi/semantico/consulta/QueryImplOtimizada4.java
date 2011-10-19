package br.com.joqi.semantico.consulta;

import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.plano.NoArvore;
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
		lerArvoreBottomUp();
		//
		return resultSet;
	}

	private void lerArvoreBottomUp() {
		System.out.println("Nó inicial: " + getNoInicial(arvoreConsulta.getRaiz()));
		arvoreConsulta.imprime();
	}

	private NoArvore getNoInicial(NoArvore raiz) {
		if (raiz.getFilho() != null) {
			raiz = raiz.getFilho();
			while (raiz.getIrmao() != null) {
				raiz = raiz.getIrmao();
			}
			return getNoInicial(raiz);
		}
		return raiz;
	}
}
