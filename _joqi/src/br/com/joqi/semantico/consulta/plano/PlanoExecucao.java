package br.com.joqi.semantico.consulta.plano;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.joqi.semantico.consulta.disjuncao.UniaoRestricoes;
import br.com.joqi.semantico.consulta.produtocartesiano.ProdutoCartesiano;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.restricao.RestricaoConjunto;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples.TipoBusca;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoOr;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Diferente;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Igual;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;
import br.com.joqi.semantico.exception.ClausulaSelectException;
import br.com.joqi.semantico.exception.ClausulaWhereException;
import br.com.joqi.semantico.exception.RelacaoInexistenteException;

public class PlanoExecucao {

	private ArvoreConsulta arvore;
	//
	private Set<Relacao> relacoes;

	public PlanoExecucao() {
		arvore = new ArvoreConsulta();
	}

	/**
	 * Monta a arvore de consulta principal
	 * 
	 * @param objetoConsulta
	 * @param restricoes
	 * @param relacoes
	 * @throws ClausulaWhereException
	 * @throws RelacaoInexistenteException
	 * @author Douglas Matheus de Souza em 13/10/2011
	 * @param projecoes
	 * @throws ClausulaSelectException
	 */
	public ArvoreConsulta montarArvore(List<Projecao<?>> projecoes, List<Restricao> restricoes, Set<Relacao> relacoes)
			throws ClausulaSelectException, ClausulaWhereException {
		this.arvore = new ArvoreConsulta();
		//
		this.relacoes = relacoes;
		//
		inserirProjecoes(arvore, projecoes);
		//
		if (restricoes.size() > 0) {
			restricoesJaOrdenadas = new HashSet<RestricaoSimples>();
			//
			inserirRestricoes(arvore, restricoes);
			ordenarRestricoesLineares(arvore.getRaizRestricoes().getFilho());
			ordenarRestricoesJuncoes(arvore.getRaizRestricoes().getFilho());
		} else {
			arvore.setRaizRestricoes(arvore.insere(new UniaoRestricoes()));
			inserirRelacoes(arvore.insere(new ProdutoCartesiano()));
		}
		//
		return this.arvore;
	}

	private void inserirProjecoes(ArvoreConsulta arvore, List<Projecao<?>> projecoes) throws ClausulaSelectException {
		for (Projecao<?> projecao : projecoes) {
			verificaRelacaoOperandoProjecao(projecao);
			arvore.insere(projecao);
		}
	}

	private void verificaRelacaoOperandoProjecao(Projecao<?> projecao) throws ClausulaSelectException {
		String exception1 = "Nome da relação obrigatório na constante \"{0}\" na cláusula SELECT (" + projecao + ")";
		String exception2 = "Relação \"{0}\" não declarada na cláusula FROM (" + projecao + ")";
		//
		if (projecao.getClass() == ProjecaoCampo.class) {
			if (projecao.getRelacao() == null) {
				if (relacoes.size() > 1) {
					throw new ClausulaSelectException(exception1.replace("{0}", (String) projecao.getValor()));
				} else {
					projecao.setRelacao(getNomeRelacaoUnica());
				}
			} else {
				if (!relacoes.contains(new Relacao(projecao.getRelacao()))) {
					throw new ClausulaSelectException(exception2.replace("{0}", projecao.getRelacao()));
				}
			}
		}
	}

	/**
	 * Insere na arvore a lista restricoes
	 * 
	 * @param arvore
	 * @param restricoes
	 * @throws ClausulaWhereException
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private ArvoreConsulta inserirRestricoes(ArvoreConsulta arvore, List<Restricao> restricoes) throws ClausulaWhereException {
		NoArvore raizRestricoes = arvore.insere(new UniaoRestricoes());
		inserirRestricoes(raizRestricoes, restricoes);
		arvore.setRaizRestricoes(raizRestricoes);
		return arvore;
	}

	/**
	 * Insere as restricoes da consulta na arvore
	 * 
	 * @param no
	 * @param restricoes
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private void inserirRestricoes(NoArvore no, List<Restricao> restricoes) throws ClausulaWhereException {
		NoArvore noRestricao = null;
		//
		for (Restricao r : restricoes) {
			NoArvore noInserir = noRestricao;

			/*Caso seja a primeira restricao, ou seja uma restricao com operador OR, cria uma subarvore*/
			if (r.getOperadorLogico() == null || r.getOperadorLogico().getClass() == OperadorLogicoOr.class) {
				noInserir = arvore.insere(no, new ProdutoCartesiano());
				/*Insere as relacoes no fim da subarvore*/
				if (noRestricao != null) {
					inserirRelacoes(noRestricao);
				}
			}

			/*Caso seja apenas uma restricao simples, vai inserindo os nos filhos*/
			if (r.getClass() == RestricaoSimples.class) {
				verificaRestricao((RestricaoSimples) r);
				setTipoBusca((RestricaoSimples) r);
				//
				noRestricao = arvore.insere(noInserir, r);
				descerRestricoesSimples(noRestricao);
			} else {
				RestricaoConjunto restricaoConjunto = (RestricaoConjunto) r;
				if (restricaoConjunto.isNegacao())
					restricaoConjunto.negarRestricoes();
				/*Caso seja um conjunto de restricoes entre parenteses, eh necessario criar uma subarvore*/
				ArvoreConsulta subArvore = inserirRestricoes(new ArvoreConsulta(), restricaoConjunto.getRestricoes());
				noRestricao = arvore.insere(noInserir, subArvore);
				subirSubarvore(noRestricao);
			}
		}

		/*Insere as relacoes no fim de arvore*/
		inserirRelacoes(noRestricao);
	}

	/**
	 * Determina qual algoritmo sera utilizado para efetuar a busca nas relacoes
	 * de entrada da restricao.
	 * 
	 * @param restricao
	 * @author Douglas Matheus de Souza em 18/10/2011
	 * @throws ClausulaWhereException
	 */
	private void setTipoBusca(RestricaoSimples restricao) {
		if (relacoes.size() == 1) {
			restricao.setTipoBusca(TipoBusca.LINEAR);
		} else {
			//
			Projecao<?> operando1 = restricao.getOperando1();
			Projecao<?> operando2 = restricao.getOperando2();
			OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();
			//
			if (operando1.getClass() == ProjecaoCampo.class) {
				if (operando2 != null && operando2.getClass() == ProjecaoCampo.class) {
					if (!operando1.getRelacao().equals(operando2.getRelacao())) {
						if (operadorRelacional.getClass() == Igual.class) {
							if (restricao.isNegacao()) {
								restricao.setTipoBusca(TipoBusca.JUNCAO_LOOP_ANINHADO);
							} else {
								restricao.setTipoBusca(TipoBusca.JUNCAO_HASH);
							}
						} else if (operadorRelacional.getClass() == Diferente.class) {
							if (restricao.isNegacao()) {
								restricao.setTipoBusca(TipoBusca.JUNCAO_HASH);
							} else {
								restricao.setTipoBusca(TipoBusca.JUNCAO_LOOP_ANINHADO);
							}
						} else {
							restricao.setTipoBusca(TipoBusca.JUNCAO_LOOP_ANINHADO);
						}
					} else {
						restricao.setTipoBusca(TipoBusca.LINEAR);
					}
				} else {
					restricao.setTipoBusca(TipoBusca.LINEAR);
				}
			} else {
				restricao.setTipoBusca(TipoBusca.LINEAR);
			}
		}
	}

	/**
	 * Verifica se as restricoes possuem apelidos, caso exista mais de uma
	 * relacao na clausula FROM.
	 * 
	 * @param restricao
	 * @throws ClausulaWhereException
	 * @author Douglas Matheus de Souza em 18/10/2011
	 */
	private void verificaRestricao(RestricaoSimples restricao) throws ClausulaWhereException {
		verificaRelacaoOperandoRestricao(restricao, restricao.getOperando1());
		verificaRelacaoOperandoRestricao(restricao, restricao.getOperando2());
	}

	/**
	 * Verifica se a relacao informada junto a um operando existe. Caso exista
	 * mais de uma relacao na clausula FROM, verifica tambem se o operando
	 * que faz referencia ao nome de um atributo possue o nome da relacao.
	 * 
	 * @param restricao
	 * @param operando
	 * @throws ClausulaWhereException
	 * @author Douglas Matheus de Souza em 19/10/2011
	 */
	private void verificaRelacaoOperandoRestricao(RestricaoSimples restricao, Projecao<?> operando) throws ClausulaWhereException {
		String exception1 = "Nome da relação obrigatório na constante \"{0}\" na cláusula WHERE (" + restricao.getRestricaoString() + ")";
		String exception2 = "Relação \"{0}\" não declarada na cláusula FROM (" + restricao.getRestricaoString() + ")";
		//
		if (operando != null) {
			if (operando.getClass() == ProjecaoCampo.class) {
				if (operando.getRelacao() == null) {
					if (relacoes.size() > 1) {
						throw new ClausulaWhereException(exception1.replace("{0}", (String) operando.getValor()));
					} else {
						operando.setRelacao(getNomeRelacaoUnica());
					}
				} else {
					if (!relacoes.contains(new Relacao(operando.getRelacao()))) {
						throw new ClausulaWhereException(exception2.replace("{0}", operando.getRelacao()));
					}
				}
			}
		}
	}

	private void subirSubarvore(NoArvore no) {
		NoArvore pai = no.getPai();
		while (pai != null && pai.getOperacao().getClass() != ProdutoCartesiano.class) {
			Object o1 = pai.getOperacao();
			Object o2 = no.getOperacao();
			//
			pai.setOperacao(o2);
			no.setOperacao(o1);
			//
			no = no.getPai();
			pai = no.getPai();
		}
	}

	/**
	 * Metodo que "desce" as restricoes, colocando as que nao fazem produto
	 * cartesiano na parte mais baixa da arvore. Este metodo tem o funcionamente
	 * semelhante ao de um comparator, onde dois objetos sao comparados um a um.
	 * 
	 * @param no
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private void descerRestricoesSimples(NoArvore no) {
		NoArvore pai = no.getPai();
		Object operacaoPai = pai.getOperacao();
		while (pai != null && operacaoPai.getClass() == RestricaoSimples.class) {
			/*Compara duas restricoes proximas para ver qual fica mais abaixo*/
			RestricaoSimples r1 = (RestricaoSimples) operacaoPai;
			RestricaoSimples r2 = (RestricaoSimples) no.getOperacao();
			if (r1.getTipoBusca() == TipoBusca.LINEAR) {
				if (r2.getTipoBusca() != TipoBusca.LINEAR) {
					/*Restricoes que fazem busca linear devem ficar mais abaixo*/
					pai.setOperacao(r2);
					no.setOperacao(r1);
				}
			} else if (r1.getTipoBusca() == TipoBusca.JUNCAO_HASH) {
				if (r2.getTipoBusca() == TipoBusca.JUNCAO_LOOP_ANINHADO) {
					/*Restricoes que fazem juncao baseada em hash devem ficar		       
					 * mais abaixo das que utilizam loop aninhado*/
					pai.setOperacao(r2);
					no.setOperacao(r1);
				}
			}
			//
			no = no.getPai();
			pai = no.getPai();
			operacaoPai = pai.getOperacao();
		}
	}

	/*Lista auxiliar que guarda quais restricoes ja foram ordenadas*/
	private Set<RestricaoSimples> restricoesJaOrdenadas;

	/**
	 * Ordena as restricoes que utilizam o algoritmo de busca linear para que
	 * elas fiquem posicionadas na mesma subarvore da relacao sobre qual ira
	 * efetuar a busca.
	 * 
	 * @param raiz
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private void ordenarRestricoesLineares(NoArvore raiz) {
		if (raiz != null) {
			Object operacao = raiz.getOperacao();
			if (operacao.getClass() == ArvoreConsulta.class) {
				ordenarRestricoesLineares(((ArvoreConsulta) operacao).getRaiz().getFilho());
			} else {
				if (operacao.getClass() == RestricaoSimples.class) {
					RestricaoSimples restricao = (RestricaoSimples) operacao;
					if (!restricoesJaOrdenadas.contains(restricao)) {
						if (restricao.getTipoBusca() == TipoBusca.LINEAR) {
							restricoesJaOrdenadas.add(restricao);

							/*Encontra o no que possui a relacao referente a restricao*/
							NoArvore noRelacao = encontraRelacao(raiz, getRelacaoString(restricao));

							/*Troca a posicao da restricao na arvore, deixando-a acima da relacao*/
							Object relacao = noRelacao.getOperacao();
							noRelacao.setOperacao(operacao);
							noRelacao.setFilho(new NoArvore(relacao));
							raiz.getPai().setFilho(raiz.getFilho());
						}
					}
				}
			}
			//
			ordenarRestricoesLineares(raiz.getFilho());
			ordenarRestricoesLineares(raiz.getIrmao());
		}
	}

	/**
	 * Encontra dentro da arvore o no que contem a operacao de relacao.
	 * 
	 * @param raiz
	 * @param relacao
	 * @author Douglas Matheus de Souza em 19/10/2011
	 */
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

	/**
	 * Arranja as juncoes de maneira que fiquem ligadas as suas duas relacoes de
	 * entrada.
	 * 
	 * @author Douglas Matheus de Souza em 19/10/2011
	 */
	private void ordenarRestricoesJuncoes(NoArvore raiz) {
		while (raiz != null) {
			raiz = raiz.getFilho();
			//
			while (raiz != null && raiz.getOperacao().getClass() == ArvoreConsulta.class) {
				ordenarRestricoesJuncoes(((ArvoreConsulta) raiz.getOperacao()).getRaizRestricoes().getFilho());
				raiz = raiz.getFilho();
			}

			if (raiz == null)
				break;

			/*Vai descendo na arvore arvore ateh achar a ultima restricao que faz juncao*/
			while (raiz.getOperacao().getClass() == RestricaoSimples.class
					&& ((RestricaoSimples) raiz.getOperacao()).getTipoBusca() != TipoBusca.LINEAR) {
				raiz = raiz.getFilho();
			}

			/*Vai subindo na arvore e montando as juncoes*/
			raiz = raiz.getPai();
			while (raiz.getOperacao().getClass() == RestricaoSimples.class &&
					((RestricaoSimples) raiz.getOperacao()).getTipoBusca() != TipoBusca.LINEAR) {
				RestricaoSimples restricaoJuncao = (RestricaoSimples) raiz.getOperacao();
				String relacao1 = restricaoJuncao.getOperando1().getRelacao();
				String relacao2 = restricaoJuncao.getOperando2().getRelacao();
				/*Uma vez encontrada a restricao, percorre os filhos e verifica 
				 * qual nao tem relacao alguma com a mesma. Os filhos que nao 
				 * fazem parte da juncao irao virar seus irmais*/
				NoArvore filho = raiz.getFilho();
				while (filho != null) {
					boolean trocarPosicao = false;
					//
					if (filho.getOperacao().getClass() == RestricaoSimples.class) {
						RestricaoSimples restricaoFilho = (RestricaoSimples) filho.getOperacao();
						if (restricaoFilho.getTipoBusca() == TipoBusca.LINEAR) {
							String relacaoFilho = getRelacaoString((RestricaoSimples) filho.getOperacao());
							if (!relacaoFilho.equals(relacao1) && !relacaoFilho.equals(relacao2)) {
								trocarPosicao = true;
							}
							//
							setaPrimeiroFilho(raiz, filho, relacao1, relacaoFilho);
						} else {
							String relacao1Filho = restricaoFilho.getOperando1().getRelacao();
							String relacao2Filho = restricaoFilho.getOperando2().getRelacao();
							if (!relacao1Filho.equals(relacao1) && !relacao2Filho.equals(relacao2) &&
									!relacao2Filho.equals(relacao1) && !relacao1Filho.equals(relacao2)) {
								trocarPosicao = true;
							}
							//
							setaPrimeiroFilho(raiz, filho, relacao1, relacao1Filho);
						}
					} else if (filho.getOperacao().getClass() == Relacao.class) {
						String relacaoFilho = ((Relacao) filho.getOperacao()).getNomeNaConsulta();
						if (!relacaoFilho.equals(relacao1) && !relacaoFilho.equals(relacao2)) {
							trocarPosicao = true;
						}
						//
						setaPrimeiroFilho(raiz, filho, relacao1, relacaoFilho);
					}
					//
					if (trocarPosicao) {
						raiz.removeFilho(filho);
						raiz.addIrmao(filho);
					}
					//
					filho = filho.getIrmao();
				}
				//
				raiz = raiz.getPai();
			}
			//
			raiz = raiz.getIrmao();
		}
	}

	private void setaPrimeiroFilho(NoArvore raiz, NoArvore filho, String relacaoRaiz, String relacaoFilho) {
		if (relacaoFilho.equals(relacaoRaiz)) {
			raiz.removeFilho(filho);
			raiz.addFilho(filho);
		}
	}

	/**
	 * Insere as relacoes em um no
	 * 
	 * @param no
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private void inserirRelacoes(NoArvore no) {
		if (no.getOperacao().getClass() != ArvoreConsulta.class) {
			for (Relacao relacao : relacoes) {
				arvore.insere(no, relacao);
			}
		}
	}

	private String getRelacaoString(RestricaoSimples restricao) {
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		//
		if (operando1.getRelacao() == null && operando2.getRelacao() == null) {
			return getNomeRelacaoUnica();
		} else if (operando1.getRelacao() != null) {
			return operando1.getRelacao();
		} else {
			return operando2.getRelacao();
		}
	}

	private String getNomeRelacaoUnica() {
		return relacoes.iterator().next().getNomeNaConsulta();
	}

}
