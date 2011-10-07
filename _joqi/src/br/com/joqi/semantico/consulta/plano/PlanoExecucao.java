package br.com.joqi.semantico.consulta.plano;

import java.util.List;

import br.com.joqi.semantico.consulta.QueryUtils;
import br.com.joqi.semantico.consulta.busca.tipo.TipoBusca;
import br.com.joqi.semantico.consulta.disjuncao.UniaoRestricoes;
import br.com.joqi.semantico.consulta.produtocartesiano.ProdutoCartesiano;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.restricao.RestricaoConjunto;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoOr;
import br.com.joqi.semantico.exception.RelacaoInexistenteException;

public class PlanoExecucao {

	private ArvoreConsulta arvore;
	//
	private Object objetoConsulta;
	private List<Relacao> relacoes;

	public PlanoExecucao() {
		arvore = new ArvoreConsulta();
	}

	private void inserirRestricoes(NoArvore no, List<Restricao> restricoes)
			throws RelacaoInexistenteException {
		NoArvore noRestricao = null;
		//
		for (Restricao r : restricoes) {
			NoArvore noInserir = noRestricao;
			if (r.getOperadorLogico() == null || r.getOperadorLogico().getClass() == OperadorLogicoOr.class) {
				noInserir = no;
				//
				if (noRestricao != null) {
					inserirRelacoes(noRestricao);
				}
			}
			//
			if (r.getClass() == RestricaoSimples.class) {
				noRestricao = arvore.insere(noInserir, r);
				//
				descerRestricoes(noRestricao);
			} else {
				ArvoreConsulta subArvore = montarArvore(new ArvoreConsulta(), ((RestricaoConjunto) r).getRestricoes());
				noRestricao = arvore.insere(noInserir, subArvore);
			}
		}
		//
		inserirRelacoes(noRestricao);
	}

	private void descerRestricoes(NoArvore no) {
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

	private void ordenarRestricoes(NoArvore raizRestricoes) {
		NoArvore filho = raizRestricoes.getFilho();
		while (filho != null) {
			NoArvore noRestricao = filho;
			while (noRestricao.getFilho() != null && !noRestricao.getFilho().isFolha()) {
				noRestricao = noRestricao.getFilho();
			}
			while (true) {				
				RestricaoSimples restricao = (RestricaoSimples) noRestricao.getOperacao();
				if (restricao.getTipoBusca() == TipoBusca.LINEAR) {
					NoArvore no = noRestricao.getFilho();
					while (no != null) {
						NoArvore noRelacao = no;
						while (noRelacao.getOperacao().getClass() != Relacao.class) {
							noRelacao = noRelacao.getFilho();
						}
						if (((Relacao) noRelacao.getOperacao()).getNomeNaConsulta().equals(getRelacaoString(restricao))) {
							Object temp = noRelacao.getOperacao();
							noRelacao.setOperacao(noRestricao.getOperacao());
							noRelacao.setFilho(new NoArvore(temp));

							NoArvore filhoTemp = noRestricao.getFilho();
							NoArvore paiTemp = noRestricao.getPai();
							if (paiTemp.getOperacao().getClass() != UniaoRestricoes.class) {
								paiTemp.setFilho(filhoTemp);
							} else {
								noRestricao.setOperacao(new ProdutoCartesiano());
							}
						}
						no = no.getIrmao();
					}
				}
				noRestricao = noRestricao.getPai();
				if(noRestricao.getOperacao().getClass() != RestricaoSimples.class){
					break;	
				} else if(((RestricaoSimples)noRestricao.getOperacao()).getTipoBusca() != TipoBusca.LINEAR){
					break;	
				}				
			}
			//
			filho = filho.getIrmao();
		}
	}

	private void inserirRelacoes(NoArvore no) throws RelacaoInexistenteException {
		for (Relacao relacao : relacoes) {
			relacao.setColecao(QueryUtils.getColecao(objetoConsulta, relacao.getNome()));
			arvore.insere(no, relacao);
		}
	}

	private ArvoreConsulta montarArvore(ArvoreConsulta arvore, List<Restricao> restricoes) throws RelacaoInexistenteException {
		if (restricoes.size() > 0) {
			NoArvore raizRestricoes = arvore.insere(new UniaoRestricoes());
			inserirRestricoes(raizRestricoes, restricoes);
			ordenarRestricoes(raizRestricoes);
			arvore.setRaizRestricoes(raizRestricoes);
		} else {
			NoArvore no = arvore.insere(new ProdutoCartesiano());
			inserirRelacoes(no);
		}
		//
		return arvore;
	}

	public ArvoreConsulta montarArvore(Object objetoConsulta, List<Restricao> restricoes, List<Relacao> relacoes) throws RelacaoInexistenteException {
		this.objetoConsulta = objetoConsulta;
		this.relacoes = relacoes;
		//
		montarArvore(arvore, restricoes).imprime();
		//
		return null;
	}

	public void insereOperacao(Object operacao) {
		arvore.insere(operacao);
	}

	private String getRelacaoString(RestricaoSimples restricao) {
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		//
		if (operando1.getRelacao() == null && operando2.getRelacao() == null) {
			return relacoes.get(0).getNomeNaConsulta();
		} else if (operando1.getRelacao() != null) {
			return operando1.getRelacao();
		} else {
			return operando2.getRelacao();
		}
	}

}
