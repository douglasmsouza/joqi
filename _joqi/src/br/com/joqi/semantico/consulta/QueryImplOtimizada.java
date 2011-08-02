package br.com.joqi.semantico.consulta;

import java.util.Collection;
import java.util.HashMap;

import br.com.joqi.semantico.consulta.relacao.Relacao;

public class QueryImplOtimizada {

	private Query query;
	private Object objetoConsulta;
	//
	private HashMap<String, Collection<?>> relacoes;

	public QueryImplOtimizada(Query query, Object objetoConsulta) {
		this.query = query;
		this.objetoConsulta = objetoConsulta;
	}

	public void getResultado() throws Exception {
		double time = System.currentTimeMillis();
		//
		criaReferenciasRelacoes();
		//
		time = System.currentTimeMillis() - time;
		//
		System.out.println(relacoes);
		//
		System.out.println("-------------------------------");
		System.out.println("Tempo total : " + time + " ms");
		System.out.println("-------------------------------");
		//
	}

	private void criaReferenciasRelacoes() throws Exception {
		relacoes = new HashMap<String, Collection<?>>();
		//
		for (Relacao relacao : query.getRelacoes()) {
			Collection<?> collection = QueryUtils.getColecao(objetoConsulta, relacao.getNome());
			/*Insere a relacao em um HashMap*/
			if (relacao.getApelido() != null) {
				relacoes.put(relacao.getApelido(), collection);
			} else {
				relacoes.put(relacao.getNome(), collection);
			}
		}
	}	
}
