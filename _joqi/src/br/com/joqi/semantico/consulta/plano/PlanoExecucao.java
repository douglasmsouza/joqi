package br.com.joqi.semantico.consulta.plano;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	/*private void ordenarRestricoes(NoArvore raizRestricoes) {
		if (raizRestricoes != null) {
			NoArvore filho = raizRestricoes.getFilho();
			while (filho != null) {
				NoArvore noRestricao = filho;
				while (noRestricao.getFilho() != null && !noRestricao.getFilho().isFolha()) {
					noRestricao = noRestricao.getFilho();
				}
				while (true) {
					Object operacao = noRestricao.getOperacao();
					if (operacao.getClass() == ArvoreConsulta.class) {
						ordenarRestricoes(((ArvoreConsulta) operacao).getRaiz());
					} else {
						RestricaoSimples restricao = (RestricaoSimples) operacao;
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
								no = noRelacao.getIrmao();
							}
						} else {
							System.out.println("NÃO LINEAR: " + restricao);
						}
					}
					//
					noRestricao = noRestricao.getPai();
					if (noRestricao.getOperacao().getClass() != RestricaoSimples.class) {
						break;
					} else if (((RestricaoSimples) noRestricao.getOperacao()).getTipoBusca() != TipoBusca.LINEAR) {
						break;
					}
				}
				//
				filho = filho.getIrmao();
			}
		}
	}*/
	
	private Set<RestricaoSimples> restricoesOrdenadasAux;

	private void ordenarRestricoes(NoArvore raiz) {
		if (raiz != null) {
			Object operacao = raiz.getOperacao();
			if(operacao.getClass() == ArvoreConsulta.class){
				ordenarRestricoes(((ArvoreConsulta) operacao).getRaiz().getFilho());
			} else {
    			if (operacao.getClass() == RestricaoSimples.class) {
    				RestricaoSimples restricao = (RestricaoSimples) operacao;
    				if(!restricoesOrdenadasAux.contains(restricao)){
    					restricoesOrdenadasAux.add(restricao);
        				if (restricao.getTipoBusca() == TipoBusca.LINEAR) {
        					NoArvore noRelacao = encontraRelacao(raiz, getRelacaoString(restricao));
        					Object relacao = noRelacao.getOperacao();
        					noRelacao.setOperacao(operacao);
        					noRelacao.setFilho(new NoArvore(relacao));
        					raiz.getPai().setFilho(raiz.getFilho());
        				}
    				}
    			}
    			//
    			ordenarRestricoes(raiz.getFilho());
				ordenarRestricoes(raiz.getIrmao());
			}
		}
	}

	private NoArvore encontraRelacao(NoArvore raiz, String relacao) {
		NoArvore noRelacao = null;
		//
		if (raiz != null) {
			if (noEhRelacao(raiz, relacao)) {
				return raiz;
			}
			noRelacao = encontraRelacao(raiz.getFilho(), relacao);
			if (noRelacao == null) {
				noRelacao = encontraRelacao(raiz.getIrmao(), relacao);
			}
		}
		//
		return noRelacao;
	}

	private boolean noEhRelacao(NoArvore no, String relacao) {
		if (no.getOperacao().getClass() == Relacao.class) {
			if (((Relacao) no.getOperacao()).getNomeNaConsulta().equals(relacao)) {
				return true;
			}
		}
		return false;
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
		restricoesOrdenadasAux = new HashSet<RestricaoSimples>();
		//
		montarArvore(arvore, restricoes);
		ordenarRestricoes(arvore.getRaizRestricoes().getFilho());
		arvore.imprime();
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
