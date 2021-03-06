package br.com.joqi.semantico.consulta._old;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.joqi.semantico.consulta.Query;
import br.com.joqi.semantico.consulta.QueryUtils;
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
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.consulta.resultado.ResultSet;
import br.com.joqi.semantico.consulta.util.JoqiUtil;
import br.com.joqi.semantico.exception.ClausulaWhereException;
import br.com.joqi.semantico.exception.TiposIncompativeisException;

/**
 * Implementacao das clausulas da query
 * 
 * @author Douglas Matheus de Souza em 26/07/2011
 */
public class QueryImplOld4 {

	private class Tupla extends ResultObject {
		public Tupla() {
		}

		public Tupla(String relacao, Object objeto) {
			put(relacao, objeto);
		}
	}

	private class ResultList extends ArrayList<Tupla> {
	}

	private Query query;
	private Object objetoConsulta;
	//
	private Map<String, Collection<Object>> relacoes;
	private Map<String, Collection<Tupla>> relacoesResultantes;

	public QueryImplOld4(Query query, Object objetoConsulta) {
		this.query = query;
		this.objetoConsulta = objetoConsulta;
	}

	public void getResultSet() throws Exception {
		double tempoTotal = System.currentTimeMillis();

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

		relacoesResultantes = new HashMap<String, Collection<Tupla>>();

		/*Faz as restricoes*/
		ResultList resultadoTemp = where(query);
		//
		ResultSet resultSet = new ResultSet();
		resultSet.addAll(resultadoTemp);
		//
		tempoTotal = System.currentTimeMillis() - tempoTotal;

		JoqiUtil.imprimeResultado(15, tempoTotal, new String[] { "pai", "filho1", "filho2" }, resultSet);
	}

	private ResultList where(IPossuiRestricoes possuiRestricoes) throws Exception {
		ResultList resultadoFinal = null;
		ResultList resultadoTemp = null;
		//
		Collections.sort(possuiRestricoes.getRestricoes(), new Comparator<Restricao>() {
			@Override
			public int compare(Restricao o1, Restricao o2) {
				if (o1.getOperadorLogico() == null || o1.getOperadorLogico().getClass() == o2.getOperadorLogico().getClass()) {
					if (o1.getClass() == RestricaoSimples.class && o2.getClass() == RestricaoSimples.class) {
						RestricaoSimples r1 = (RestricaoSimples) o1;
						RestricaoSimples r2 = (RestricaoSimples) o2;
						//
						if (!r2.isJuncao() && r1.isJuncao()) {
							r1.setOperadorLogico(r2.getOperadorLogico());
							return 1; // Joga r1 para baixo
						}
					}
				}

				return 0;
			}
		});
		//
		Iterator<Restricao> iterator = possuiRestricoes.getRestricoes().iterator();
		Restricao r = iterator.next();
		while (r != null) {
			Restricao proxima = null;
			if (iterator.hasNext())
				proxima = iterator.next();
			//
			double tempoExecucaoRestricao = System.currentTimeMillis();
			//
			if (r.getClass() == RestricaoSimples.class) {
				RestricaoSimples restricao = (RestricaoSimples) r;
				//
				verificaRestricao(restricao);
				//
				if (restricao.isJuncao()) {
					resultadoTemp = juncao(restricao, proxima);
				} else if (restricao.isProdutoCartesiano()) {

				} else {
					resultadoTemp = where(restricao, proxima);
				}
			}/* else {
			 RestricaoConjunto restricao = (RestricaoConjunto) r;
			 if (restricao.isNegacao())
			 	restricao.negarRestricoes();
			 //
			 resultadoTemp = where(restricao);
			 }*/
			//
			if (resultadoFinal == null || r.getOperadorLogico().getClass() == OperadorLogicoAnd.class)
				resultadoFinal = resultadoTemp;
			else {
				ResultList resultadoFinalTemp = new ResultList();
				for (Tupla objeto1 : resultadoTemp) {
					for (Tupla objeto2 : resultadoFinal) {
						Tupla tupla = new Tupla();
						tupla.putAll((Tupla) objeto1);
						tupla.putAll((Tupla) objeto2);
						resultadoFinalTemp.add(tupla);
					}
				}
				resultadoFinal = resultadoFinalTemp;
			}

			//
			System.out.println(r + " = " + (System.currentTimeMillis() - tempoExecucaoRestricao) + " ms");
			//
			r = proxima;
		}
		//
		return resultadoFinal;
	}

	private ResultList juncao(RestricaoSimples restricao, Restricao proxima) throws Exception {
		ResultList resultList = new ResultList();
		//
		String nomeRelacao1 = restricao.getOperando1().getRelacao();
		String nomeRelacao2 = restricao.getOperando2().getRelacao();
		//
		Collection<?> relacao1 = relacoesResultantes.get(nomeRelacao1);
		if (relacao1 == null)
			relacao1 = relacoes.get(nomeRelacao1);
		Collection<?> relacao2 = relacoesResultantes.get(nomeRelacao2);
		if (relacao2 == null)
			relacao2 = relacoes.get(nomeRelacao2);
		//
		Map<Object, List<Object>> hashTable = new HashMap<Object, List<Object>>();
		/*Insere as tupla da relacao1 em uma tabela hash (representada por um HashMap)*/
		for (Object objeto1 : relacao1) {
			Object campo = restricao.getOperando1().getValor();
			if (objeto1.getClass() == Tupla.class) {
				objeto1 = ((Tupla) objeto1).get(nomeRelacao1);
			}
			Object valor = QueryUtils.getValorDoCampo(objeto1, campo.toString());
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
			if (objeto2.getClass() == Tupla.class) {
				objeto2 = ((Tupla) objeto2).get(nomeRelacao2);
			}
			Object valor = QueryUtils.getValorDoCampo(objeto2, campo.toString());
			List<Object> objetos1 = hashTable.get(valor);
			//
			if (verificaCondicao(objetos1 != null, restricao)) {
				for (Object objeto1 : objetos1) {
					Tupla tupla = new Tupla();
					tupla.put(nomeRelacao1, objeto1);
					tupla.put(nomeRelacao2, objeto2);
					resultList.add(tupla);
				}
			}
		}
		//
		relacoesResultantes.remove(nomeRelacao1);
		relacoesResultantes.remove(nomeRelacao2);
		if (proxima != null && proxima.getOperadorLogico().getClass() == OperadorLogicoAnd.class) {
			relacoesResultantes.put(nomeRelacao1, resultList);
			relacoesResultantes.put(nomeRelacao2, resultList);
		} else {
			ResultList resultListTemp = new ResultList();
			for (Entry<String, Collection<Tupla>> relacao : relacoesResultantes.entrySet()) {
				if (!relacao.getKey().equals(nomeRelacao1)) {
					if (!relacao.getKey().equals(nomeRelacao2)) {
						for (Object objeto1 : resultList) {
							for (Object objeto2 : relacao.getValue()) {
								Tupla tupla = new Tupla();
								tupla.putAll((Tupla) objeto1);
								tupla.putAll((Tupla) objeto2);
								resultListTemp.add(tupla);
							}
						}
						//
						resultList = resultListTemp;
					}
				}
			}
		}
		//
		return resultList;
	}

	private ResultList where(RestricaoSimples restricao, Restricao proxima) throws Exception {
		/*Relacao que sera pesquisada*/
		Collection<Object> relacao = getRelacaoCollection(restricao);

		String nomeRelacao = getRelacaoString(restricao);

		/*Tuplas resultantes desta restricao*/
		ResultList resultList = new ResultList();
		/*Guarda os operandos e o operador da restricao*/
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();

		/*Percorre a relacao, eliminando os registros que nao satisfazem a condicao*/
		for (Object objeto : relacao) {
			Object valorOperando1 = getValorOperando(operando1, objeto);

			/*Se eh uma instrucao IS TRUE ou IS FALSE, compara logo de cara, uma vez que nao*/
			/*existem outros operandos na restricao*/
			if (operadorRelacional.getClass() == IgualBooleano.class) {
				if (verificaCondicao(valorOperando1.equals(operando2.getValor()), restricao)) {
					resultList.add(new Tupla(nomeRelacao, objeto));
				}
				continue;
			}

			/*Se eh uma instrucao IS NULL, segue o mesmo caminho das instrucoes IS TRUE e IS FALSE*/
			if (operadorRelacional.getClass() == Nulo.class) {
				if (verificaCondicao(valorOperando1 == null, restricao)) {
					resultList.add(new Tupla(nomeRelacao, objeto));
				}
				continue;
			}

			Object valorOperando2 = getValorOperando(operando2, objeto);
			Object valorOperandoAux = null;

			/*Caso o valor do campo na tupla seja NULL, eh um caso "especial" */
			if (valorOperando1 == null || valorOperando2 == null) {
				if (operadorRelacional.getClass() == Igual.class) {
					if (restricao.isNegacao()) {
						resultList.add(new Tupla(nomeRelacao, objeto));
					}
				} else if (operadorRelacional.getClass() == Diferente.class) {
					if (!restricao.isNegacao()) {
						resultList.add(new Tupla(nomeRelacao, objeto));
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
				valorOperandoAux = getValorOperando(operandoAux, objeto);
				//
				/*Dada a expressao "NOT z between x and y": 
				 *Caso "z" seja NULO, a expressao eh valida por possuir o NOT na frente*/
				if (valorOperandoAux == null) {
					if (restricao.isNegacao()) {
						resultList.add(new Tupla(nomeRelacao, objeto));
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
			} catch (TiposIncompativeisException e) {
			}

			/*Converte os valores dos operando para Comparable para que seja possivel*/
			/*efetuar a comparacao*/
			Comparable<Object> valor1Comp = (Comparable<Object>) valorOperando1;
			Comparable<Object> valor2Comp = (Comparable<Object>) valorOperando2;
			Comparable<Object> valorAuxComp = (Comparable<Object>) valorOperandoAux;

			if (verificaCondicao(operadorRelacional.compara(valor1Comp, valor2Comp, valorAuxComp), restricao)) {
				resultList.add(new Tupla(nomeRelacao, objeto));
				continue;
			}
		}
		//
		relacoesResultantes.remove(nomeRelacao);
		if (proxima != null && proxima.getOperadorLogico().getClass() == OperadorLogicoAnd.class) {
			relacoesResultantes.put(nomeRelacao, resultList);
		}
		//
		return resultList;
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
		if (restricao.getOperadorRelacional().getClass() != Entre.class) {
			if (operando1.getRelacao() == null && operando2.getRelacao() == null) {
				return relacoes.keySet().iterator().next();
			} else if (operando1.getRelacao() != null) {
				return operando1.getRelacao();
			} else {
				return operando2.getRelacao();
			}
		} else {
			Entre entre = (Entre) restricao.getOperadorRelacional();
			if (entre.getOperandoEntre().getRelacao() != null)
				return entre.getOperandoEntre().getRelacao();
			else
				return relacoes.keySet().iterator().next();
		}
	}

	private Collection<Object> getRelacaoCollection(RestricaoSimples restricao) {
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		//
		if (restricao.getOperadorRelacional().getClass() != Entre.class) {
			if (operando1.getRelacao() == null && operando2.getRelacao() == null) {
				return relacoes.values().iterator().next();
			} else if (operando1.getRelacao() != null) {
				return relacoes.get(operando1.getRelacao());
			} else {
				return relacoes.get(operando2.getRelacao());
			}
		} else {
			Entre entre = (Entre) restricao.getOperadorRelacional();
			if (entre.getOperandoEntre().getRelacao() != null)
				return relacoes.get(entre.getOperandoEntre().getRelacao());
			else
				return relacoes.values().iterator().next();
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
				throw new TiposIncompativeisException();
			}
			if (valorOperandoOutro2 != null) {
				if (!(valorOperandoOutro2 instanceof Number))
					throw new TiposIncompativeisException();
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
}
