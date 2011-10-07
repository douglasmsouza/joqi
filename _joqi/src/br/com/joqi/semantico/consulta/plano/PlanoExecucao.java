package br.com.joqi.semantico.consulta.plano;

import java.util.List;

import br.com.joqi.semantico.consulta.QueryUtils;
import br.com.joqi.semantico.consulta.busca.tipo.TipoBusca;
import br.com.joqi.semantico.consulta.disjuncao.UniaoRestricoes;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.restricao.RestricaoConjunto;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoOr;
import br.com.joqi.semantico.exception.RelacaoInexistenteException;

public class PlanoExecucao {

	private ArvoreConsulta arvore;

	public PlanoExecucao() {
		arvore = new ArvoreConsulta();
	}

	private void inserirRestricoes(NoArvore no, Object objetoConsulta, List<Restricao> restricoes, List<Relacao> relacoes)
			throws RelacaoInexistenteException {
		NoArvore noRestricao = null;
		//
		for (Restricao r : restricoes) {
			NoArvore noInserir = noRestricao;
			if (r.getOperadorLogico() == null || r.getOperadorLogico().getClass() == OperadorLogicoOr.class) {
				noInserir = no;
				//
				if (noRestricao != null) {
					inserirRelacoes(noRestricao, objetoConsulta, relacoes);
				}
			}
			//
			if (r.getClass() == RestricaoSimples.class) {
				noRestricao = arvore.insere(noInserir, r);
				//
				ordenarRestricoes(noRestricao);
			} else {
				ArvoreConsulta subArvore = montarArvore(new ArvoreConsulta(), objetoConsulta, ((RestricaoConjunto) r).getRestricoes(), relacoes);
				noRestricao = arvore.insere(noInserir, subArvore);
			}
		}
		//
		inserirRelacoes(noRestricao, objetoConsulta, relacoes);
	}

	private void inserirRelacoes(NoArvore no, Object objetoConsulta, List<Relacao> relacoes) throws RelacaoInexistenteException {
		for (Relacao relacao : relacoes) {
			relacao.setColecao(QueryUtils.getColecao(objetoConsulta, relacao.getNome()));
			arvore.insere(no, relacao);
		}
	}

	private void ordenarRestricoes(NoArvore no) {
		NoArvore pai = no.getPai();
		while (pai != null && pai.getOperacao().getClass() == RestricaoSimples.class) {
			RestricaoSimples r1 = (RestricaoSimples) pai.getOperacao();
			RestricaoSimples r2 = (RestricaoSimples) no.getOperacao();
			//
			if (r1.getTipoBusca() == TipoBusca.LINEAR) {
				if (r2.getTipoBusca() != TipoBusca.LINEAR) {
					pai.setOperacao(r2);
					no.setOperacao(r1);
				}
			} else if (r1.getTipoBusca() == TipoBusca.JUNCAO_HASH) {
				if (r2.getTipoBusca() == TipoBusca.LOOP_ANINHADO) {
					pai.setOperacao(r2);
					no.setOperacao(r1);
				}
			} else {
				break;
			}
			//
			no = no.getPai();
			pai = no.getPai();
		}
	}

	private ArvoreConsulta montarArvore(ArvoreConsulta arvore, Object objetoConsulta, List<Restricao> restricoes, List<Relacao> relacoes)
			throws RelacaoInexistenteException {
		if (restricoes.size() > 0) {
			NoArvore raizRestricoes = arvore.insere(new UniaoRestricoes());
			inserirRestricoes(raizRestricoes, objetoConsulta, restricoes, relacoes);
			arvore.setRaizRestricoes(raizRestricoes);
		}
		//
		return arvore;
	}

	public ArvoreConsulta montarArvore(Object objetoConsulta, List<Restricao> restricoes, List<Relacao> relacoes) throws RelacaoInexistenteException {
		return montarArvore(arvore, objetoConsulta, restricoes, relacoes);
	}

	public void insereOperacao(Object operacao) {
		arvore.insere(operacao);
	}

}
