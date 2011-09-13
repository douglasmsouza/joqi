package br.com.joqi.semantico.consulta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.IPossuiRestricoes;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoAnd;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Entre;
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
public class QueryImplOtimizada {

	private class Tupla extends ResultObject {
	}

	private class Juncao extends ArrayList<Tupla> {
	}

	private Query query;
	private Object objetoConsulta;
	//
	private Map<String, Collection<?>> relacoes;
	private Map<String, Collection<Object>> relacoesResultantes;
	private ResultList resultList;

	public QueryImplOtimizada(Query query, Object objetoConsulta) {
		this.query = query;
		this.objetoConsulta = objetoConsulta;
	}

	public void getResultado() throws Exception {
		double time = System.currentTimeMillis();
		//
		relacoes = new HashMap<String, Collection<?>>();
		relacoesResultantes = new HashMap<String, Collection<Object>>();
		/*Cria as referencias para as relacoes*/
		for (Relacao relacao : query.getRelacoes()) {
			Collection<?> collection = QueryUtils.getColecao(objetoConsulta, relacao.getNome());
			collection = new ArrayList<Object>(collection);
			if (relacao.getApelido() != null) {
				relacoes.put(relacao.getApelido(), collection);
			} else {
				relacoes.put(relacao.getNome(), collection);
			}
		}
		//
		resultList = new ResultList();
		//
		where(query);
		//
		time = System.currentTimeMillis() - time;
		//
		/*for (ResultObject objeto : resultList) {
			System.out.println(objeto);
		}*/
		System.out.println("-------------------------------");
		System.out.println("Tempo total : " + time + " ms");
		System.out.println("-------------------------------");
	}

	private ResultList where(IPossuiRestricoes possuiRestricoes) throws Exception {
		ResultList resultado = new ResultList();
		//
		for (Restricao r : possuiRestricoes.getRestricoes()) {
			if (r.getClass() == RestricaoSimples.class) {
				RestricaoSimples restricao = (RestricaoSimples) r;
				//
				verificaRestricao(restricao);
				//
				if (restricao.efetuaJuncao()) {
					System.out.println(juncao(restricao));
				} else {
					String relacao = getRelacaoString(restricao);
					Collection<Object> relacaoAnterior = relacoesResultantes.get(relacao);
					Collection<Object> relacaoResultante = where(restricao);
					System.out.println(restricao);
					System.out.println(relacaoAnterior);
					System.out.println(relacaoResultante);
					System.out.println("-------------------");
					//
					if (relacaoAnterior == null) {
						relacoesResultantes.put(relacao, relacaoResultante);
					} else {
						if (restricao.getOperadorLogico() != null) {
							if (restricao.getOperadorLogico().getClass() == OperadorLogicoAnd.class) {
								relacaoAnterior.retainAll(relacaoResultante);
							} else {
								relacaoAnterior.addAll(relacaoResultante);
							}
						} else {
							relacoesResultantes.put(relacao, relacaoResultante);
						}
					}
				}
			}
		}
		//
		System.out.println(relacoesResultantes);
		//
		return resultado;
	}

	private Map<String, Collection<Object>> juncao(RestricaoSimples restricao) throws Exception {
		Collection<Object> resultListTemp1 = new ArrayList<Object>();
		Collection<Object> resultListTemp2 = new ArrayList<Object>();
		//
		Collection<?> relacao1 = relacoes.get(restricao.getOperando1().getRelacao());
		Collection<?> relacao2 = relacoes.get(restricao.getOperando2().getRelacao());
		//
		Map<Object, Object> hashTable = new HashMap<Object, Object>();
		//
		for (Object tupla : relacao1) {
			Object campo = restricao.getOperando1().getValor();
			Object valor = QueryUtils.getValorDoCampo(tupla, campo.toString());
			hashTable.put(valor, tupla);
		}
		//
		for (Object tupla2 : relacao2) {
			Object campo = restricao.getOperando2().getValor();
			Object valor = QueryUtils.getValorDoCampo(tupla2, campo.toString());
			Object tupla1 = hashTable.get(valor);
			if (tupla1 != null) {
				resultListTemp1.add(tupla1);
				resultListTemp2.add(tupla2);
			}
		}
		//
		Map<String, Collection<Object>> relacoesResultantes = new HashMap<String, Collection<Object>>();
		relacoesResultantes.put(restricao.getOperando1().getRelacao(), resultListTemp1);
		relacoesResultantes.put(restricao.getOperando2().getRelacao(), resultListTemp2);
		return relacoesResultantes;
	}

	private Collection<Object> where(RestricaoSimples restricao) throws Exception {
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
			/*Tupla tupla = transformaEmTupla(objeto);*/
			//
			Object valorOperando1 = getValorOperando(operando1, tupla);

			/*Se eh uma instrucao IS TRUE ou IS FALSE, compara logo de cara, uma vez que nao*/
			/*existem outros operandos na restricao*/
			if (operadorRelacional.getClass() == IgualBooleano.class) {
				if (verificaCondicao(valorOperando1.equals(operando2.getValor()), restricao)) {
					resultListTemp.add(tupla);
				}
			}

			/*Se eh uma instrucao IS NULL, segue o mesmo caminho das instrucoes IS TRUE e IS FALSE*/
			if (operadorRelacional.getClass() == Nulo.class) {
				if (verificaCondicao(valorOperando1 == null, restricao)) {
					resultListTemp.add(tupla);
				}
			}

			Object valorOperando2 = getValorOperando(operando2, tupla);
			Object valorOperandoAux = null;

			/*Para efetuar a comparacao, eh necessario que os dois valores implementem Comparable*/
			if (!(valorOperando1 instanceof Comparable<?>))
				throw new ClausulaWhereException("O valor \"" + operando1.getValor() + "\" deve implementar a interface Comparable.");
			if (!(valorOperando2 instanceof Comparable<?>))
				throw new ClausulaWhereException("O valor \"" + operando2.getValor() + "\" deve implementar a interface Comparable.");

			/*Cria outro operando caso seja uma restricao BETWEEN*/
			if (operadorRelacional.getClass() == Entre.class) {
				Projecao<?> operandoAux = ((Entre) operadorRelacional).getOperandoEntre();
				valorOperandoAux = getValorOperando(operandoAux, tupla);
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
			}
		}
		//
		return resultListTemp;
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
		if (restricao.getOperando1().getRelacao() == null && restricao.getOperando2().getRelacao() == null) {
			return relacoes.keySet().iterator().next();
		} else if (restricao.getOperando1().getRelacao() != null) {
			return restricao.getOperando1().getRelacao();
		} else {
			return restricao.getOperando2().getRelacao();
		}
	}

	private Collection<?> getRelacaoCollection(RestricaoSimples restricao) {
		if (restricao.getOperando1().getRelacao() == null && restricao.getOperando2().getRelacao() == null) {
			return relacoes.values().iterator().next();
		} else if (restricao.getOperando1().getRelacao() != null) {
			return relacoes.get(restricao.getOperando1().getRelacao());
		} else {
			return relacoes.get(restricao.getOperando2().getRelacao());
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

	private Object getValorOperando(Projecao<?> operando, Tupla tupla) throws Exception {
		Object valor = operando.getValor();
		/*Se o operando fizer referencia a um campo, busca o valor deste campo na tupla*/
		if (operando.getClass() == ProjecaoCampo.class) {
			valor = tupla.get((String) valor);
		}
		return valor;
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

	public ResultList getResultList() {
		return resultList;
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
