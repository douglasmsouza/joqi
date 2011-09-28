package br.com.joqi.semantico.consulta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.IgualBooleano;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Nulo;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;
import br.com.joqi.semantico.consulta.resultado.ResultList;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.consulta.util.JoqiUtil;
import br.com.joqi.semantico.exception.ClausulaWhereException;
import br.com.joqi.semantico.exception.OperandosIncompativeisException;

/**
 * Implementacao das clausulas da query
 * 
 * @author Douglas Matheus de Souza em 26/07/2011
 */
public class QueryImplOtimizada3 {

	private class Tupla extends ResultObject {
	}

	private Query query;
	private Object objetoConsulta;
	//
	private Map<String, Collection<Object>> relacoes;
	private ResultList resultado;

	public QueryImplOtimizada3(Query query, Object objetoConsulta) {
		this.query = query;
		this.objetoConsulta = objetoConsulta;
	}

	public void getResultado() throws Exception {
		double time = System.currentTimeMillis();

		/*Cria as referencias para as relacoes*/
		relacoes = new HashMap<String, Collection<Object>>();
		for (Relacao relacao : query.getRelacoes()) {
			Collection<Object> collection = QueryUtils.getColecao(objetoConsulta, relacao.getNome());
			if (relacao.getApelido() != null) {
				relacoes.put(relacao.getApelido(), collection);
			} else {
				relacoes.put(relacao.getNome(), collection);
			}
		}

		/*Faz as restricoes*/
		Map<Restricao, Collection<Object>> relacoesResultantes = where(query);

		time = System.currentTimeMillis() - time;

		/*StringBuilder sb = new StringBuilder(relacoesResultantes.toString().replace("], ", "]\n"));
		System.out.println(sb.replace(0, 1, "").replace(sb.length() - 1, sb.length(), ""));*/

		System.out.println("-------------------------------");
		System.out.println("Tempo total : " + time + " ms");
		System.out.println("-------------------------------");
	}

	private Map<Restricao, Collection<Object>> where(IPossuiRestricoes possuiRestricoes) throws Exception {
		/*Cada restricao ira ter como resultado uma relacao, que sera armazenada neste Map*/
		Map<Restricao, Collection<Object>> relacoesResultantes = new LinkedHashMap<Restricao, Collection<Object>>();
		//
		/*Ordena as restricoes de forma que, as restricoes simples (campo = constante ou vice-versa)
		 * sejam executadas primeiro e as juncoes sejam executadas depois*/
		/*Collections.sort(possuiRestricoes.getRestricoes());*/
		RestricaoSimples restricaoAnterior = null;
		//
		for (int i = 0; i < possuiRestricoes.getRestricoes().size(); i++) {
			Restricao r = possuiRestricoes.getRestricoes().get(i);
			//
			if (r.getClass() == RestricaoSimples.class) {
				RestricaoSimples restricaoAtual = (RestricaoSimples) r;
				//
				verificaRestricao(restricaoAtual);
				//
				if (restricaoAtual.isJuncao()) {
					juncao(relacoesResultantes, restricaoAtual);
				} else if (restricaoAtual.isProdutoCartesiano()) {

				} else {
					where(relacoesResultantes, restricaoAtual);
				}
				//
				if (restricaoAnterior != null) {
					Collection<Object> colecaoAnterior = relacoesResultantes.get(restricaoAnterior);
					Collection<Object> colecaoAtual = relacoesResultantes.get(r);
					Collection<Object> colecaoResultante = new ArrayList<Object>(colecaoAnterior);
					//
					if (restricaoAtual.getOperadorLogico().getClass() == OperadorLogicoAnd.class) {
						colecaoAnterior.retainAll(colecaoAtual);
					} else {
						colecaoAnterior.addAll(colecaoAtual);
					}
					/*List<Projecao<?>> operandosAnt = new ArrayList<Projecao<?>>(Arrays.asList(
							restricaoAnterior.getOperando1(),
							restricaoAnterior.getOperando2()
							));
					List<Projecao<?>> operandosAtuais = new ArrayList<Projecao<?>>(Arrays.asList(
							restricaoAtual.getOperando1(),
							restricaoAtual.getOperando2()
							));
					//
					for (Object objeto1 : colecaoAnterior) {
						for (Object objeto2 : colecaoAtual) {
							if (restricaoAtual.getOperadorLogico().getClass() == OperadorLogicoAnd.class) {
								boolean continua = true;
								//
								for (Projecao<?> operandoAnt : operandosAnt) {
									for (Projecao<?> operandoAtual : operandosAtuais) {
										if (operandoAnt.equals(operandoAtual)) {
											Tupla objeto1Tupla = (Tupla) objeto1;
											Tupla objeto2Tupla = (Tupla) objeto2;
											//
											if (objeto1Tupla.get(operandoAnt.getRelacao()).equals(objeto2Tupla.get(operandoAtual.getRelacao()))) {
												Tupla tupla = new Tupla();
												tupla.putAll(objeto1Tupla);
												tupla.putAll(objeto2Tupla);
												colecaoResultante.add(tupla);
												//
												continua = false;
												break;
											}
										}
									}
									if (!continua) {
										break;
									}
								}
							} else {
								if (objeto1.getClass() == Tupla.class && objeto2.getClass() == Tupla.class) {
									Tupla tupla = new Tupla();
									tupla.putAll((Tupla) objeto1);
									tupla.putAll((Tupla) objeto2);
									colecaoResultante.add(tupla);
								}
							}
						}
					}*/
					//
					System.out.println(colecaoResultante.size());
					relacoesResultantes.put(restricaoAnterior, colecaoResultante);
					relacoesResultantes.put(r, colecaoResultante);
				}
				//
				restricaoAnterior = restricaoAtual;
			}
		}
		//
		return relacoesResultantes;
	}

	private void juncao(Map<Restricao, Collection<Object>> relacoesResultantes, RestricaoSimples restricao) throws Exception {
		Collection<Object> resultListTemp = new ArrayList<Object>();
		//
		String nomeRelacao1 = restricao.getOperando1().getRelacao();
		String nomeRelacao2 = restricao.getOperando2().getRelacao();
		//
		Collection<Object> relacao1 = relacoes.get(nomeRelacao1);
		Collection<Object> relacao2 = relacoes.get(nomeRelacao2);
		//
		Map<Object, List<Object>> hashTable = new HashMap<Object, List<Object>>();
		/*Insere as tupla da relacao1 em uma tabela hash (representada por um HashMap)*/
		for (Object objeto1 : relacao1) {
			Object campo = restricao.getOperando1().getValor();
			Object objeto1Temp = objeto1;
			if (objeto1Temp.getClass() == Tupla.class) {
				objeto1Temp = ((Tupla) objeto1).get(nomeRelacao1);
			}
			Object valor = QueryUtils.getValorDoCampo(objeto1Temp, campo.toString());
			List<Object> objetos = hashTable.get(valor);
			if (objetos == null) {
				objetos = new ArrayList<Object>();
			}
			objetos.add(objeto1);
			hashTable.put(valor, objetos);
		}
		//
		for (Object objeto2 : relacao2) {
			Object campo = restricao.getOperando2().getValor();
			Object objeto2Temp = objeto2;
			if (objeto2Temp.getClass() == Tupla.class) {
				objeto2Temp = ((Tupla) objeto2).get(nomeRelacao1);
			}
			Object valor = QueryUtils.getValorDoCampo(objeto2Temp, campo.toString());
			List<Object> objetos1 = hashTable.get(valor);
			//
			if (verificaCondicao(objetos1 != null, restricao)) {
				for (Object objeto1 : objetos1) {
					Tupla tupla = new Tupla();
					if (objeto1.getClass() == Tupla.class) {
						tupla.putAll((Tupla) objeto1);
					} else {
						tupla.put(nomeRelacao1, objeto1);
					}
					tupla.put(nomeRelacao2, objeto2);
					resultListTemp.add(tupla);
				}
			}
		}
		//
		Collection<Object> resultList = new ArrayList<Object>();
		//
		for (Entry<String, Collection<Object>> relacao : relacoes.entrySet()) {
			if (!relacao.getKey().equals(nomeRelacao1)) {
				if (!relacao.getKey().equals(nomeRelacao2)) {
					for (Object objeto1 : resultListTemp) {
						for (Object objeto2 : relacao.getValue()) {
							Tupla tupla = new Tupla();
							tupla.putAll((Tupla) objeto1);
							tupla.put(relacao.getKey(), objeto2);
							resultList.add(tupla);
						}
					}
				}
			}
		}
		//
		relacoesResultantes.put(restricao, resultList);
	}

	private void where(Map<Restricao, Collection<Object>> relacoesResultantes, RestricaoSimples restricao) throws Exception {
		/*Relacao que sera pesquisada*/
		Collection<?> relacao = getRelacaoCollection(restricao);

		/*Tuplas resultantes desta restricao*/
		Collection<Object> resultListTemp = new ArrayList<Object>();
		/*Guarda os operandos e o operador da restricao*/
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();

		/*Percorre a relacao, eliminando os registros que nao satisfazem a condicao*/
		for (Object tupla : relacao) {
			Object valorOperando1 = getValorOperando(operando1, tupla);

			/*Se eh uma instrucao IS TRUE ou IS FALSE, compara logo de cara, uma vez que nao*/
			/*existem outros operandos na restricao*/
			if (operadorRelacional.getClass() == IgualBooleano.class) {
				if (verificaCondicao(valorOperando1.equals(operando2.getValor()), restricao)) {
					resultListTemp.add(tupla);
				}
				continue;
			}

			/*Se eh uma instrucao IS NULL, segue o mesmo caminho das instrucoes IS TRUE e IS FALSE*/
			if (operadorRelacional.getClass() == Nulo.class) {
				if (verificaCondicao(valorOperando1 == null, restricao)) {
					resultListTemp.add(tupla);
				}
				continue;
			}

			Object valorOperando2 = getValorOperando(operando2, tupla);
			Object valorOperandoAux = null;

			/*Caso o valor do campo na tupla seja NULL, eh um caso "especial" */
			if (valorOperando1 == null || valorOperando2 == null) {
				if (operadorRelacional.getClass() == Igual.class) {
					if (restricao.isNegacao()) {
						resultListTemp.add(tupla);
					}
				} else if (operadorRelacional.getClass() == Diferente.class) {
					if (!restricao.isNegacao()) {
						resultListTemp.add(tupla);
					}
				}
				continue;
			}

			/*Para efetuar a comparacao, eh necessario que os dois valores implementem Comparable*/
			if (!(valorOperando1 instanceof Comparable<?>))
				throw new ClausulaWhereException("O valor \"" + operando1.getValor() + "\" deve implementar a interface Comparable.");
			if (!(valorOperando2 instanceof Comparable<?>))
				throw new ClausulaWhereException("O valor \"" + operando2.getValor() + "\" deve implementar a interface Comparable.");

			/*Cria outro operando caso seja uma restricao BETWEEN*/
			if (operadorRelacional.getClass() == Entre.class) {
				Projecao<?> operandoAux = ((Entre) operadorRelacional).getOperandoEntre();
				valorOperandoAux = getValorOperando(operandoAux, tupla);
				//
				/*Dada a expressao "NOT z between x and y": 
				 *Caso "z" seja NULO, a expressao eh valida por possuir o NOT na frente*/
				if (valorOperandoAux == null) {
					if (restricao.isNegacao()) {
						resultListTemp.add(tupla);
					}
					continue;
				}
				//
				if (!(valorOperandoAux instanceof Comparable<?>))
					throw new ClausulaWhereException("O valor " + operandoAux.getValor() + " deve implementar a interface Comparable.");
			}

			try {
				valorOperando1 = getValorOperando(valorOperando1, valorOperando2, valorOperandoAux);
				valorOperando2 = getValorOperando(valorOperando2, valorOperando1, valorOperandoAux);
				valorOperandoAux = getValorOperando(valorOperandoAux, valorOperando1, valorOperando2);
			} catch (OperandosIncompativeisException e) {
			}

			/*Converte os valores dos operando para Comparable para que seja possivel*/
			/*efetuar a comparacao*/
			Comparable<Object> valor1Comp = (Comparable<Object>) valorOperando1;
			Comparable<Object> valor2Comp = (Comparable<Object>) valorOperando2;
			Comparable<Object> valorAuxComp = (Comparable<Object>) valorOperandoAux;

			if (verificaCondicao(operadorRelacional.compara(valor1Comp, valor2Comp, valorAuxComp), restricao)) {
				resultListTemp.add(tupla);
				continue;
			}
		}
		relacoesResultantes.put(restricao, resultListTemp);
	}

	private void verificaRestricao(RestricaoSimples restricao) throws ClausulaWhereException {
		/*Quando existe mais de uma relacao na clausula WHERE, se o nome de um campo*/
		/*eh utilizado na restricao, deve-se informar junto o nome da relacao */
		/*da qual o campo pertence*/
		if (relacoes.size() > 1) {
			String exception = "Nome da rela��o obrigat�rio na cl�usula WHERE (" + restricao.toString().trim() + ")";
			if (restricao.getOperando1().getClass() == ProjecaoCampo.class) {
				if (restricao.getOperando1().getRelacao() == null) {
					throw new ClausulaWhereException(exception);
				}
			}
			//
			if (restricao.getOperando2() != null) {
				if (restricao.getOperando2().getClass() == ProjecaoCampo.class) {
					if (restricao.getOperando2().getRelacao() == null) {
						throw new ClausulaWhereException(exception);
					}
				}
			}
		}
	}

	private String getRelacaoString(RestricaoSimples restricao) {
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		//
		if (operando1.getRelacao() == null && operando2.getRelacao() == null) {
			return relacoes.keySet().iterator().next();
		} else if (operando1.getRelacao() != null) {
			return operando1.getRelacao();
		} else {
			return operando2.getRelacao();
		}
	}

	private Collection<?> getRelacaoCollection(RestricaoSimples restricao) {
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		//
		if (operando1.getRelacao() == null && operando2.getRelacao() == null) {
			return relacoes.values().iterator().next();
		} else if (operando1.getRelacao() != null) {
			return relacoes.get(operando1.getRelacao());
		} else {
			return relacoes.get(operando2.getRelacao());
		}
	}

	/**
	 * Retorna o valor de um operando de uma restricao verificando a
	 * compatibilidade de tipos com os outros operandos
	 * 
	 * @author Douglas Matheus de Souza em 27/07/2011
	 */
	private Object getValorOperando(Object valorOperando, Object valorOperandoOutro1, Object valorOperandoOutro2) throws Exception {
		if (valorOperando instanceof String) {
			/*Para Strings, os valores sao convertidos para minusculo para que fiquem iguais*/
			return JoqiUtil.retiraAcentuacao(((String) valorOperando).toLowerCase());
		} else if (valorOperando instanceof Number) {
			/*Se comparacao for entre um numero e um "nao numero", eh invalida*/
			if (!(valorOperandoOutro1 instanceof Number)) {
				throw new OperandosIncompativeisException();
			}
			if (valorOperandoOutro2 != null) {
				if (!(valorOperandoOutro2 instanceof Number))
					throw new OperandosIncompativeisException();
			}
			/*Para valores numericos a comparacao eh feita sempre em Double*/
			return ((Number) valorOperando).doubleValue();
		}
		//
		return valorOperando;
	}

	private Object getValorOperando(Projecao<?> operando, Object tupla) throws Exception {
		Object valor = operando.getValor();
		/*Se o operando fizer referencia a um campo, busca o valor deste campo na tupla*/
		if (operando.getClass() == ProjecaoCampo.class) {
			valor = QueryUtils.getValorDoCampo(tupla, (String) valor);
		}
		return valor;
	}

	private boolean verificaCondicao(boolean comparacao, RestricaoSimples restricao) {
		return (comparacao && !restricao.isNegacao()) || (!comparacao && restricao.isNegacao());
	}

	private Tupla transformaEmTupla(Object objeto) throws Exception {
		Tupla tupla = new Tupla();
		//
		for (Field atributo : objeto.getClass().getDeclaredFields()) {
			boolean ehPrivado = !atributo.isAccessible();

			if (ehPrivado)
				atributo.setAccessible(true);

			tupla.put(atributo.getName(), atributo.get(objeto));

			if (ehPrivado)
				atributo.setAccessible(false);
		}
		//
		return tupla;
	}
}
