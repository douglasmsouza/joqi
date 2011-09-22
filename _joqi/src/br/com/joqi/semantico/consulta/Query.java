package br.com.joqi.semantico.consulta;

import java.util.ArrayList;
import java.util.List;

import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.IPossuiRestricoes;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.testes.BancoConsulta;

public class Query implements IPossuiRestricoes {

	private List<Projecao<?>> projecoes;
	private List<Relacao> relacoes;
	private List<Restricao> restricoes;

	public Query() {
		projecoes = new ArrayList<Projecao<?>>();
		relacoes = new ArrayList<Relacao>();
		restricoes = new ArrayList<Restricao>();
	}

	public void addProjecao(Projecao<?> p) {
		projecoes.add(p);
	}

	public void addRelacao(Relacao f) {
		relacoes.add(f);
	}

	public void addRestricao(Restricao r) {
		restricoes.add(r);
	}

	public List<Projecao<?>> getProjecoes() {
		return projecoes;
	}

	public List<Relacao> getRelacoes() {
		return relacoes;
	}

	public List<Restricao> getRestricoes() {
		return restricoes;
	}

	public void getResultado() {
		try {
			/*QueryImplOtimizada queryImpl = new QueryImplOtimizada(this, new BancoConsulta());*/
			QueryImplOtimizadaNew queryImpl = new QueryImplOtimizadaNew(this, new BancoConsulta());
			/*QueryImpl queryImpl = new QueryImpl(this, new BancoConsulta());*/
			queryImpl.getResultado();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
