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

	/**
	 * Insere as restricoes da consulta na arvore
	 * 
	 * @param no
	 * @param restricoes
	 * @throws RelacaoInexistenteException
	 * @author Douglas Matheus de Souza em
	 *         13/10/2011
	 */
	private void inserirRestricoes(NoArvore no, List<Restricao> restricoes)
			throws RelacaoInexistenteException {
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
				noRestricao = arvore.insere(noInserir, r);
				descerRestricoes(noRestricao);
			} else {
				/*Caso seja um conjunto de restricoes entre parenteses, eh necessario criar uma subarvore*/
				ArvoreConsulta subArvore = montarArvore(new ArvoreConsulta(), ((RestricaoConjunto) r).getRestricoes());
				noRestricao = arvore.insere(noInserir, subArvore);
			}
		}

		/*Insere as relacoes no fim de arvore*/
		inserirRelacoes(noRestricao);
	}

	/**
	 * Metodo que "desce" as restricoes, colocando
	 * as que nao fazem produto
	 * cartesiano na parte mais baixa da arvore.
	 * Este metodo tem o funcionamente
	 * semelhante ao de um comparator.
	 * 
	 * @param no
	 * @author Douglas Matheus de Souza em
	 *         13/10/2011
	 */
	private void descerRestricoes(NoArvore no) {
		NoArvore pai = no.getPai();
		while (pai != null && pai.getOperacao().getClass() == RestricaoSimples.class) {
			/*Para cada duas restricoes proximas, verifica qual deve ficar mais abaixo*/
			RestricaoSimples r1 = (RestricaoSimples) pai.getOperacao();
			RestricaoSimples r2 = (RestricaoSimples) no.getOperacao();
			if (r1.getTipoBusca() == TipoBusca.LINEAR) {
				if (r2.getTipoBusca() != TipoBusca.LINEAR) {
					/*Restricoes que fazem busca linear devem ficar mais abaixo*/
					pai.setOperacao(r2);
					no.setOperacao(r1);
				}
			} else if (r1.getTipoBusca() == TipoBusca.JUNCAO_HASH) {
				if (r2.getTipoBusca() == TipoBusca.LOOP_ANINHADO) {
					/*Restricoes que fazem juncao baseada em hash devem ficar 
					 * mais abaixo das que utilizam loop aninhado*/
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

	/*Lista auxiliar que guarda quais restricoes ja foram ordenadas*/
	private Set<RestricaoSimples> restricoesJaOrdenadas;

	/**
	 * Ordena as restricoes que utilizam o
	 * algoritmo de busca linear para que
	 * elas fiquem posicionadas na mesma subarvore
	 * da relacao sobre qual ira
	 * efetuar a busca.
	 * 
	 * @param raiz
	 * @author Douglas Matheus de Souza em
	 *         13/10/2011
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
				//
				ordenarRestricoesLineares(raiz.getFilho());
				ordenarRestricoesLineares(raiz.getIrmao());
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

	private void ordenarRestricoesJuncoes(NoArvore raiz) {
		if (raiz != null) {
			/*Procura na arvore a ultima restricao que faz juncao*/
			while (raiz.getOperacao().getClass() == RestricaoSimples.class
					&& ((RestricaoSimples) raiz.getOperacao()).getTipoBusca() != TipoBusca.LINEAR) {
				raiz = raiz.getFilho();
			}

			raiz = raiz.getPai();
			while (raiz.getOperacao().getClass() != ProdutoCartesiano.class) {
				RestricaoSimples restricaoJuncao = (RestricaoSimples) raiz.getOperacao();
				String relacao1 = restricaoJuncao.getOperando1().getRelacao();
				String relacao2 = restricaoJuncao.getOperando2().getRelacao();
				/*Uma vez encontrada a restricao, percorre os filhos e 
				 * verifica qual nao tem relacao alguma com a mesma*/
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
						} else {
							String relacao1Filho = restricaoFilho.getOperando1().getRelacao();
							String relacao2Filho = restricaoFilho.getOperando2().getRelacao();
							if (!relacao1Filho.equals(relacao1) && !relacao2Filho.equals(relacao2) &&
									!relacao2Filho.equals(relacao1) && !relacao1Filho.equals(relacao2)) {
								trocarPosicao = true;
							}
						}
					} else if (filho.getOperacao().getClass() == Relacao.class) {
						String relacaoFilho = ((Relacao) filho.getOperacao()).getNomeNaConsulta();
						if (!relacaoFilho.equals(relacao1) && !relacaoFilho.equals(relacao2)) {
							trocarPosicao = true;
						}
					}
					//
					if (trocarPosicao) {
						raiz.removeFilho(filho);
						filho.setIrmao(null);
						raiz.setIrmao(filho);
					}
					//
					filho = filho.getIrmao();
				}
				//
				raiz = raiz.getPai();
			}
		}
	}

	/**
	 * Insere as relacoes em um no
	 * 
	 * @param no
	 * @throws RelacaoInexistenteException
	 * @author Douglas Matheus de Souza em
	 *         13/10/2011
	 */
	private void inserirRelacoes(NoArvore no) throws RelacaoInexistenteException {
		for (Relacao relacao : relacoes) {
			relacao.setColecao(QueryUtils.getColecao(objetoConsulta, relacao.getNome()));
			arvore.insere(no, relacao);
		}
	}

	/**
	 * Insere na arvore a lista restricoes
	 * 
	 * @param arvore
	 * @param restricoes
	 * @author Douglas Matheus de Souza em
	 *         13/10/2011
	 */
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

	/**
	 * Monta a arvore de consulta principal
	 * 
	 * @param objetoConsulta
	 * @param restricoes
	 * @param relacoes
	 * @return
	 * @throws RelacaoInexistenteException
	 * @author Douglas Matheus de Souza em
	 *         13/10/2011
	 */
	public ArvoreConsulta montarArvore(Object objetoConsulta, List<Restricao> restricoes, List<Relacao> relacoes) throws RelacaoInexistenteException {
		this.objetoConsulta = objetoConsulta;
		this.relacoes = relacoes;
		//
		restricoesJaOrdenadas = new HashSet<RestricaoSimples>();
		//
		montarArvore(arvore, restricoes);
		ordenarRestricoesLineares(arvore.getRaizRestricoes().getFilho());
		ordenarRestricoesJuncoes(arvore.getRaizRestricoes().getFilho().getFilho());
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
