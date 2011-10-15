package br.com.joqi.semantico.consulta;

import java.util.ArrayList;
import java.util.List;

import br.com.joqi.semantico.consulta.busca.tipo.TipoBusca;
import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.plano.PlanoExecucao;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.IPossuiRestricoes;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoAnd;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Diferente;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Entre;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Igual;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;
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

	public void addRestricao(Restricao restricao) {
		restricoes.add(restricao);
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
