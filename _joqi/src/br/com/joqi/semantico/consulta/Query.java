package br.com.joqi.semantico.consulta;

import java.util.ArrayList;
import java.util.List;

import br.com.joqi.semantico.consulta.plano.PlanoExecucao;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.IPossuiRestricoes;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.testes.BancoConsulta;

public class Query implements IPossuiRestricoes {

	private PlanoExecucao planoExecucao;
	//
	private BancoConsulta bancoConsulta;
	//
	private List<Projecao<?>> projecoes;
	private List<Relacao> relacoes;
	private List<Restricao> restricoes;

	public Query() {
		planoExecucao = new PlanoExecucao();
		//
		bancoConsulta = new BancoConsulta();
		//
		projecoes = new ArrayList<Projecao<?>>();
		relacoes = new ArrayList<Relacao>();
		restricoes = new ArrayList<Restricao>();
	}

	public void addProjecao(Projecao<?> projecao) {
		projecoes.add(projecao);
		planoExecucao.insereOperacao(projecao);
	}

	public void addRelacao(Relacao f) {
		relacoes.add(f);
	}

	@Override
	public void addRestricao(Restricao restricao) {
		restricoes.add(restricao);
	}

	public List<Projecao<?>> getProjecoes() {
		return projecoes;
	}

	public List<Relacao> getRelacoes() {
		return relacoes;
	}

	@Override
	public List<Restricao> getRestricoes() {
		return restricoes;
	}

	@Override
	public Restricao getUltima() {
		try {
			return restricoes.get(restricoes.size() - 1);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
	public void remove(Restricao restricao) {
		restricoes.remove(restricao);		
	}

	public void getResultado() {
		try {
			/*QueryImpl queryImpl = new QueryImpl(this, new BancoConsulta());*/
			/*QueryImplOtimizada queryImpl = new QueryImplOtimizada(this, new BancoConsulta());*/
			/*QueryImplOtimizada2 queryImpl = new QueryImplOtimizada2(this, new BancoConsulta());*/
			/*QueryImplOtimizada3 queryImpl = new QueryImplOtimizada3(this, new BancoConsulta());			
			queryImpl.getResultSet();*/
			//
			//
			planoExecucao.montarArvore(bancoConsulta, restricoes, relacoes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
