package br.com.joqi.semantico.consulta.plano;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.joqi.semantico.consulta.agrupamento.Agrupamento;
import br.com.joqi.semantico.consulta.agrupamento.agregacao.FuncaoAgregacao;
import br.com.joqi.semantico.consulta.disjuncao.UniaoRestricoes;
import br.com.joqi.semantico.consulta.ordenacao.ItemOrdenacao;
import br.com.joqi.semantico.consulta.ordenacao.Ordenacao;
import br.com.joqi.semantico.consulta.produtocartesiano.ProdutoCartesiano;
import br.com.joqi.semantico.consulta.projecao.ListaProjecoes;
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
import br.com.joqi.semantico.exception.ClausulaGroupByException;
import br.com.joqi.semantico.exception.ClausulaOrderByException;
import br.com.joqi.semantico.exception.ClausulaSelectException;
import br.com.joqi.semantico.exception.ClausulaWhereException;
import br.com.joqi.semantico.exception.mensagem.MensagemErro;

public class PlanoExecucao {

	/**
	 * Classe auxiliar que eh utilizada na organizacao das juncoes
	 * 
	 * @author Douglas Matheus de Souza em 01/11/2011
	 */
	private class RelacionamentoFilhoJuncao {
		boolean relacao1;
		boolean relacao2;
		boolean ladoEsquerdoJuncao;
	}

	/*Lista auxiliar que guarda quais restricoes ja foram organizadas*/
	private Set<RestricaoSimples> restricoesJaOrdenadas;
	/*Arvore de consulta que sera montada*/
	private ArvoreConsulta arvore;
	/*Relacoes da clausula FROM*/
	private Set<Relacao> relacoes;
	/*Mapeamento dos apelidos das projecoes da clausula SELECT*/
	private Map<String, Projecao<?>> apelidosProjecoes;

	public PlanoExecucao() {
		arvore = new ArvoreConsulta();
	}

	/**
	 * Monta a arvore de consulta principal
	 * 
	 * @param projecoes
	 * @param restricoes
	 * @param relacoes
	 * @param agrupamentos
	 * @param ordenacao
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 30/10/2011
	 */
	public ArvoreConsulta montaArvore(ListaProjecoes projecoes, List<Restricao> restricoes, Set<Relacao> relacoes, Agrupamento agrupamento,
			Ordenacao ordenacao) throws Exception {
		this.arvore = new ArvoreConsulta();
		//
		this.relacoes = relacoes;
		this.apelidosProjecoes = new HashMap<String, Projecao<?>>();
		//
		/*Insere as projecoes na arvore*/
		insereProjecoes(arvore, projecoes);
		/*Insere a ordenacao na arvore*/
		insereOrdenacao(arvore, ordenacao);
		/*Insere o agrupamento na arvore*/
		insereAgrupamento(arvore, agrupamento);
		/*Monta a parte das restricoes*/
		if (restricoes.size() > 0) {
			restricoesJaOrdenadas = new HashSet<RestricaoSimples>();
			/*Insere as restricoes*/
			insereRestricoes(arvore, restricoes);
			/*Orgazina as restricoes simples*/
			ordenaRestricoesSimples(arvore.getRaizRestricoes().getFilho());
			/*Orgazina juncoes*/
			ordenaRestricoesJuncoes(arvore.getRaizRestricoes().getFilho());
		} else {
			arvore.setRaizRestricoes(arvore.insere(new UniaoRestricoes()));
			insereRelacoes(arvore.insere(new ProdutoCartesiano()));
		}
		//
		return this.arvore;
	}

	/**
	 * Insere as projecoes na arvore
	 * 
	 * @param arvore
	 * @param projecoes
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 28/10/2011
	 */
	private void insereProjecoes(ArvoreConsulta arvore, ListaProjecoes projecoes) throws Exception {
		if (projecoes.size() > 0) {
			for (Projecao<?> projecao : projecoes) {
				/*Caso a projecao tenha um apelido, faz o mapeamento desta projecao com o apelido*/
				if (projecao.getApelido() != null) {
					apelidosProjecoes.put(projecao.getApelido(), projecao);
				}
				//
				verificaSemanticaOperando(projecao, projecao, "select", ClausulaSelectException.class);
			}
			arvore.insere(projecoes);
		}
	}

	/**
	 * Insere a ordenacao na arvore
	 * 
	 * @param arvore
	 * @param ordenacao
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 30/10/2011
	 */
	private void insereOrdenacao(ArvoreConsulta arvore, Ordenacao ordenacao) throws Exception {
		if (ordenacao.getItens().size() > 0) {
			for (ItemOrdenacao item : ordenacao.getItens()) {
				verificaSemanticaOperando(item.getCampo(), null, "order by", ClausulaOrderByException.class);
			}
			arvore.insere(ordenacao);
		}
	}

	/**
	 * Insere o agrupamento na arvore
	 * 
	 * @param arvore
	 * @param agrupamento
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 30/10/2011
	 */
	private void insereAgrupamento(ArvoreConsulta arvore, Agrupamento agrupamento) throws Exception {
		if (agrupamento.getCampos().size() > 0) {
			for (ProjecaoCampo campo : agrupamento.getCampos()) {
				verificaSemanticaOperando(campo, null, "group by", ClausulaGroupByException.class);
			}
			for (FuncaoAgregacao funcao : agrupamento.getFuncoesAgregacao()) {
				verificaSemanticaOperando(funcao.getCampo(), null, "select", ClausulaGroupByException.class);
			}
			arvore.insere(agrupamento);
		} else if (agrupamento.getFuncoesAgregacao().size() > 0) {
			throw new ClausulaSelectException("Para utilizar funções de agregação (COUNT(), SUM(), MIN()...) a cláusula GROUP BY deve ser declarada");
		}
	}

	/**
	 * Insere na arvore a lista restricoes
	 * 
	 * @param arvore
	 * @param restricoes
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private ArvoreConsulta insereRestricoes(ArvoreConsulta arvore, List<Restricao> restricoes) throws Exception {
		NoArvore raizRestricoes = arvore.insere(new UniaoRestricoes());
		insereRestricoes(raizRestricoes, restricoes);
		arvore.setRaizRestricoes(raizRestricoes);
		return arvore;
	}

	/**
	 * Insere as restricoes da consulta na arvore
	 * 
	 * @param no
	 * @param restricoes
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private void insereRestricoes(NoArvore no, List<Restricao> restricoes) throws Exception {
		NoArvore noRestricao = null;
		//
		for (Restricao r : restricoes) {
			NoArvore noInserir = noRestricao;

			/*Caso seja a primeira restricao, ou seja uma restricao com operador OR, cria uma subarvore*/
			if (r.getOperadorLogico() == null || r.getOperadorLogico().getClass() == OperadorLogicoOr.class) {
				noInserir = arvore.insere(no, new ProdutoCartesiano());
				/*Insere as relacoes no fim da subarvore*/
				if (noRestricao != null) {
					insereRelacoes(noRestricao);
				}
			}

			/*Caso seja apenas uma restricao simples, vai inserindo os nos filhos*/
			if (r.getClass() == RestricaoSimples.class) {
				RestricaoSimples rSimples = (RestricaoSimples) r;
				verificaSemanticaRestricao(rSimples);
				setTipoBusca(rSimples);
				//
				noRestricao = arvore.insere(noInserir, r);
				desceRestricoes(noRestricao);
			} else {
				RestricaoConjunto rConjunto = (RestricaoConjunto) r;
				if (rConjunto.isNegacao())
					rConjunto.negarRestricoes();

				/*Caso seja um conjunto de restricoes entre parenteses, eh necessario criar uma subarvore*/
				ArvoreConsulta subarvoreRestricoes = insereRestricoes(new ArvoreConsulta(), rConjunto.getRestricoes());
				noRestricao = arvore.insere(noInserir, subarvoreRestricoes);
				sobeSubarvore(noRestricao);
			}
		}

		/*Insere as relacoes no fim de arvore*/
		insereRelacoes(noRestricao);
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
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 18/10/2011
	 */
	private void verificaSemanticaRestricao(RestricaoSimples restricao) throws Exception {
		verificaSemanticaOperando(restricao.getOperando1(), restricao, "where", ClausulaWhereException.class);
		verificaSemanticaOperando(restricao.getOperando2(), restricao, "where", ClausulaWhereException.class);
	}

	/**
	 * Verifica se a relacao informada junto a um operando existe. Caso exista
	 * mais de uma relacao na clausula FROM, verifica tambem se o operando
	 * que faz referencia ao nome de um atributo possue o nome da relacao.
	 * 
	 * @param operando
	 * @param operacao
	 * @param clausula
	 * @param classeExcecao
	 * @author Douglas Matheus de Souza em 30/10/2011
	 */
	private void verificaSemanticaOperando(Projecao<?> operando, Object operacao, String clausula,
			Class<? extends Exception> classeExcecao) throws Exception {
		if (operando != null) {
			//
			verificaSeEhApelido(operando);
			//
			/*Se eh o operando eh referencia a atributo, verifica se existe o nome da relacao*/
			if (operando.getClass() == ProjecaoCampo.class) {
				if (operando.getRelacao() == null) {
					/*Caso nao tenha sido declarado o nome da relacao e exista mais de uma 
					 * relacao declarada em FROM, lanca uma excecao*/
					if (relacoes.size() > 1) {
						lancarExcecao(MensagemErro.getNomeRelacaoObrigatorio(operando, operacao, clausula), classeExcecao);
					} else {
						/*Caso contrario, associa a unica relacao de FROM a este operando*/
						operando.setRelacao(getNomeRelacaoUnica());
					}
				} else {
					/*Se a relacao foi informado junto ao operando, verifica se a mesma existe na clausula FROM*/
					if (!relacoes.contains(new Relacao(operando.getRelacao()))) {
						lancarExcecao(MensagemErro.getRelacaoNaoDeclarada(operando, operacao), classeExcecao);
					}
				}
			}
		}
	}

	private void verificaSeEhApelido(Projecao operando) {
		Projecao<?> projecaoMapeada = apelidosProjecoes.get(operando.getValor());
		if (projecaoMapeada != null) {
			operando.setValor(projecaoMapeada.getValor());
			operando.setRelacao(projecaoMapeada.getRelacao());
		}
	}

	private void lancarExcecao(String mensagem, Class<? extends Exception> classeExcecao) throws Exception {
		Constructor<? extends Exception> constructor = classeExcecao.getConstructor(new Class<?>[] { String.class });
		throw constructor.newInstance(mensagem);
	}

	private void sobeSubarvore(NoArvore no) {
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
	 * cartesiano na parte mais baixa da arvore.
	 * 
	 * @param no
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private void desceRestricoes(NoArvore no) {
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

	/**
	 * Ordena as restricoes que utilizam o algoritmo de busca linear para que
	 * elas fiquem posicionadas na mesma subarvore da relacao sobre qual ira
	 * efetuar a busca.
	 * 
	 * @param raiz
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private void ordenaRestricoesSimples(NoArvore raiz) {
		if (raiz != null) {
			Object operacao = raiz.getOperacao();
			if (operacao.getClass() == ArvoreConsulta.class) {
				ordenaRestricoesSimples(((ArvoreConsulta) operacao).getRaiz().getFilho());
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
			ordenaRestricoesSimples(raiz.getFilho());
			ordenaRestricoesSimples(raiz.getIrmao());
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
	 * entrada. O algoritmo parte da raiz das restricoes e desce na arvore ate
	 * encontrar a ultima juncao. Ao encontrar esta ultima juncao, vai
	 * transformando em seus irmaos todos os filhos cuja subarvore nao possuem
	 * nenhum relacionamento com a propria juncao. O processo vai subindo na
	 * arvore ate que a ultima juncao seja visitada.
	 * 
	 * @author Douglas Matheus de Souza em 19/10/2011
	 */
	private void ordenaRestricoesJuncoes(NoArvore raiz) {
		while (raiz != null) {
			raiz = raiz.getFilho();

			/*Caso encontra uma subarvore (restricao entre parenteses), esta subarvore eh resolvida*/
			while (raiz != null && raiz.getOperacao().getClass() == ArvoreConsulta.class) {
				ordenaRestricoesJuncoes(((ArvoreConsulta) raiz.getOperacao()).getRaizRestricoes().getFilho());
				raiz = raiz.getFilho();
			}

			if (raiz == null)
				break;

			/*Vai descendo na arvore arvore ateh acha a ultima juncao*/
			while (raiz.getOperacao().getClass() == RestricaoSimples.class
					&& ((RestricaoSimples) raiz.getOperacao()).getTipoBusca() != TipoBusca.LINEAR) {
				raiz = raiz.getFilho();
			}

			/*Vai subindo na arvore e montando as juncoes*/
			raiz = raiz.getPai();
			while (raiz.getOperacao().getClass() == RestricaoSimples.class &&
					((RestricaoSimples) raiz.getOperacao()).getTipoBusca() != TipoBusca.LINEAR) {
				RestricaoSimples restricaoJuncao = (RestricaoSimples) raiz.getOperacao();
				String relacaoJuncao1 = restricaoJuncao.getOperando1().getRelacao();
				String relacaoJuncao2 = restricaoJuncao.getOperando2().getRelacao();
				/*Uma vez encontrada a restricao, percorre os filhos e verifica 
				  qual nao tem relacao alguma com a mesma. Os filhos que nao 
				  tem relacao alguma com a juncao irao virar seus irmaos. 
				  Para ter alguma relacao com a juncao, a subarvore do filho deve, em
				  algum momento fazer referencia a alguma das duas relacoes de juncao.*/
				NoArvore filho = raiz.getFilho();
				while (filho != null) {
					RelacionamentoFilhoJuncao relacionamentoFilhoJuncao = getRelacionamentoFilhoJuncao(filho, relacaoJuncao1, relacaoJuncao2);
					/*Caso o filho tenha relacionamento com a primeira relacao de juncao, deve
					 virar o primeiro filho da juncao. Os filhos devem seguir a mesma ordem das
					 relacoes que irao fazer a juncao*/
					if (relacionamentoFilhoJuncao.ladoEsquerdoJuncao) {
						raiz.removeFilho(filho);
						raiz.addFilho(filho);
					}
					/*Caso o filho nao tenha nenhuma relacao com a juncao, deve virar um de seus irmaos*/
					if (!relacionamentoFilhoJuncao.relacao1 && !relacionamentoFilhoJuncao.relacao2) {
						raiz.removeFilho(filho);
						raiz.addIrmao(filho);
					}
					//
					filho = filho.getIrmao();
				}
				/*Se no fim do processamento da juncao ela possuir somente um filho,significa que ela
				  consegue efetuar a juncao com um unica relacao de entrada. Neste caso, deve-se utilizar
				  o algoritmo de busca linear para resolver a restricao*/
				if (raiz.getFilho().getIrmao() == null) {
					((RestricaoSimples) raiz.getOperacao()).setTipoBusca(TipoBusca.LINEAR);
				}
				/*Sobe uma restricao na arvore*/
				raiz = raiz.getPai();
			}
			//
			raiz = raiz.getIrmao();
		}
	}

	/**
	 * Verifica qual o relacionamento de um no com uma juncao. Este metodo serve
	 * para verificar se uma subarvore que esta ligada a uma juncao realmente
	 * serve de entrada para esta juncao, ou seja, se nesta subarvore uma ou
	 * ambas as relacoes da juncao sao calculadas em algum momento.
	 * 
	 * @param no
	 * @param relacaoJuncao1
	 * @param relacaoJuncao2
	 * @author Douglas Matheus de Souza em 01/11/2011
	 */
	private RelacionamentoFilhoJuncao getRelacionamentoFilhoJuncao(NoArvore no, String relacaoJuncao1, String relacaoJuncao2) {
		RelacionamentoFilhoJuncao retorno = new RelacionamentoFilhoJuncao();
		//
		if (no != null) {
			String relacaoFilho1 = null;
			String relacaoFilho2 = null;
			/*Busca quais relacoes sao usadas nos filhos, dependendo do tipo de operacao*/
			if (no.getOperacao().getClass() == RestricaoSimples.class) {
				RestricaoSimples restricaoFilho = (RestricaoSimples) no.getOperacao();
				if (restricaoFilho.getTipoBusca() == TipoBusca.LINEAR) {
					relacaoFilho1 = getRelacaoString((RestricaoSimples) no.getOperacao());
				} else {
					relacaoFilho1 = restricaoFilho.getOperando1().getRelacao();
					relacaoFilho2 = restricaoFilho.getOperando2().getRelacao();
				}
			} else {
				relacaoFilho1 = ((Relacao) no.getOperacao()).getNomeNaConsulta();
			}
			//
			retorno.relacao1 = relacaoFilho1.equals(relacaoJuncao1) || relacaoFilho1.equals(relacaoJuncao2);
			retorno.relacao2 = relacaoFilho2 != null && (relacaoFilho2.equals(relacaoJuncao1) || relacaoFilho2.equals(relacaoJuncao2));
			retorno.ladoEsquerdoJuncao = relacaoFilho1.equals(relacaoJuncao1) || (relacaoFilho2 != null && relacaoFilho2.equals(relacaoJuncao1));

			/*Percore a subarvore para verificar se existe algum relacionamento*/
			NoArvore filho = no.getFilho();
			if (filho != null) {
				RelacionamentoFilhoJuncao relacionamentoSubarvore = getRelacionamentoFilhoJuncao(filho, relacaoJuncao1, relacaoJuncao2);
				retorno.relacao1 = retorno.relacao1 || relacionamentoSubarvore.relacao1;
				retorno.relacao2 = retorno.relacao2 || relacionamentoSubarvore.relacao2;
				retorno.ladoEsquerdoJuncao = retorno.ladoEsquerdoJuncao || relacionamentoSubarvore.ladoEsquerdoJuncao;
				//
				relacionamentoSubarvore = getRelacionamentoFilhoJuncao(filho.getIrmao(), relacaoJuncao1, relacaoJuncao2);
				retorno.relacao1 = retorno.relacao1 || relacionamentoSubarvore.relacao1;
				retorno.relacao2 = retorno.relacao2 || relacionamentoSubarvore.relacao2;
				retorno.ladoEsquerdoJuncao = retorno.ladoEsquerdoJuncao || relacionamentoSubarvore.ladoEsquerdoJuncao;
			}
		}
		//
		return retorno;
	}

	/**
	 * Insere as relacoes em um no
	 * 
	 * @param no
	 * @author Douglas Matheus de Souza em 13/10/2011
	 */
	private void insereRelacoes(NoArvore no) {
		if (no.getOperacao().getClass() != ArvoreConsulta.class) {
			for (Relacao relacao : relacoes) {
				arvore.insere(no, relacao);
			}
		}
	}

	private String getRelacaoString(RestricaoSimples restricao) {
		if (restricao.getOperando1().getRelacao() != null)
			return restricao.getOperando1().getRelacao();
		return restricao.getOperando2().getRelacao();
	}

	private String getNomeRelacaoUnica() {
		return relacoes.iterator().next().getNomeNaConsulta();
	}

}
