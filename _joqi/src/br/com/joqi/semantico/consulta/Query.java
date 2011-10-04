package br.com.joqi.semantico.consulta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.plano.NoArvore;
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

	public void addProjecao(Projecao<?> p) {
		projecoes.add(p);
		planoExecucao.getArvore().insere(p);
	}

	public void addRelacao(Relacao f) {
		relacoes.add(f);
	}

	public void addRestricao(Restricao r) {
		restricoes.add(r);
		planoExecucao.getArvore().insere(r);
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
			planoExecucao.inserirRelacoes(relacoes);
			planoExecucao.organizarRestricoes();
			planoExecucao.getArvore().imprime();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
