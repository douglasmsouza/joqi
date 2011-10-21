package br.com.joqi.semantico.consulta;

import java.util.Collection;

import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.plano.NoArvore;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.consulta.resultado.ResultSet;

public class QueryImplOtimizada4 {

	private class Tupla extends ResultObject {
	}

	private ArvoreConsulta arvoreConsulta;

	public QueryImplOtimizada4(ArvoreConsulta arvoreConsulta) {
		this.arvoreConsulta = arvoreConsulta;
	}

	public ResultSet getResultSet() {
		ResultSet resultado = new ResultSet();
		//
		restringe(arvoreConsulta.getRaizRestricoes());
		arvoreConsulta.imprime();
		//
		return resultado;
	}

	public ResultSet efetuaUniaoRestricoes() {
		ResultSet resultado = new ResultSet();
		//
		NoArvore no = arvoreConsulta.getRaizRestricoes();
		if (no != null) {
			NoArvore filho = no.getFilho();
			while (filho != null) {
				resultado.addAll(produtoCartesiano(filho));
				filho = filho.getIrmao();
			}
		}
		//
		return resultado;
	}

	private ResultSet produtoCartesiano(NoArvore no) {
		ResultSet resultado = new ResultSet();
		//
		//
		return resultado;
	}

	private Collection<Object> restringe(NoArvore no) {
		if (no != null) {
			if (no.isFolha()) {
				return ((Relacao) no.getOperacao()).getColecao();
			}
			Object operacao = no.getOperacao();
			//
			if (operacao instanceof RestricaoSimples) {
				Collection<Object> relacaoEntrada = restringe(no.getFilho());
			}
		}
		//
		return null;
	}

	private ResultSet where(Collection<Object> relacao, RestricaoSimples restricao) {

	}

}
