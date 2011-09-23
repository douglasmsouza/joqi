package br.com.joqi.semantico.consulta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.IPossuiRestricoes;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.restricao.RestricaoConjunto;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogico;
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
public class QueryImplOtimizada {

	private class Tupla extends ResultObject {
	}

	private Query query;
	private Object objetoConsulta;
	//
	private Map<String, Collection<Object>> relacoes;

	public QueryImplOtimizada(Query query, Object objetoConsulta) {
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
		Map<String, Collection<Object>> relacoesResultantes = where(query);

		time = System.currentTimeMillis() - time;

		StringBuilder sb = new StringBuilder(relacoesResultantes.toString().replace("], ", "]\n"));
		System.out.println(sb.replace(0, 1, "").replace(sb.length() - 1, sb.length(), ""));

		System.out.println("-------------------------------");
		System.out.println("Tempo total : " + time + " ms");
		System.out.println("-------------------------------");
	}

	private Map<String, Collection<Object>> where(IPossuiRestricoes possuiRestricoes) throws Exception {
		/*Cada restricao ira ter como resultado uma relacao, que sera armazenada neste Map*/
		Map<String, Collection<Object>> relacoesResultantes = new HashMap<String, Collection<Object>>();
		//
		/*Ordena as restricoes de forma que, as restricoes simples (campo = constante ou vice-versa)
		 * sejam executadas primeiro e as juncoes sejam executadas depois*/
		Collections.sort(possuiRestricoes.getRestricoes());
		//
		OperadorLogico operadorLogico = null;
		//
		for (int i = 0; i < possuiRestricoes.getRestricoes().size(); i++) {
			Restricao r = possuiRestricoes.getRestricoes().get(i);
			if (r.getClass() == RestricaoSimples.class) {
				RestricaoSimples restricao = (RestricaoSimples) r;
				//
				verificaRestricao(restricao);
				//
				Map<String, Collection<Object>> where;
				if (restricao.isJuncao()) {
					where = juncao(relacoesResultantes, restricao);
				} else if (restricao.isConstante()) {
					where = where(relacoesResultantes, restricao);
				} else {
					where = where(relacoesResultantes, restricao);
				}
				//
				/*Restringe a Collection com base na colecao produzida pela restricao e na 
				 * colecao produzida pela restricao anterior*/
				Iterator<Entry<String, Collection<Object>>> iterator = where.entrySet().iterator();
				do {
					Entry<String, Collection<Object>> relacaoWhere = iterator.next();
					Entry<String, Collection<Object>> relacao = iterator.next();
					restringeCollection(relacoesResultantes, operadorLogico, relacaoWhere.getKey(), relacao.getValue(), relacaoWhere.getValue());
				} while (iterator.hasNext());
				//
				operadorLogico = restricao.getOperadorLogico();
				if (operadorLogico == null) {
					if (i < possuiRestricoes.getRestricoes().size() - 1) {
						operadorLogico = possuiRestricoes.getRestricoes().get(i + 1).getOperadorLogico();
					}
				}
			} else {
				RestricaoConjunto restricaoConjunto = (RestricaoConjunto) r;
				if (r.isNegacao())
					restricaoConjunto.negarRestricoes();
				relacoesResultantes.putAll(where(restricaoConjunto));
			}
		}
		//
		return relacoesResultantes;
	}

	private void restringeCollection(Map<String, Collection<Object>> relacoesResultantes, OperadorLogico operadorLogico, String nomeRelacao,
			Collection<Object> relacao, Collection<Object> where) {
		Collection<Object> colecao1 = new ArrayList<Object>(where);
		if (relacao == null) {
			relacoesResultantes.put(nomeRelacao, colecao1);
		} else {
			if (operadorLogico != null) {
				Collection<Object> colecao2 = new ArrayList<Object>(relacao);
				//
				if (operadorLogico.getClass() == OperadorLogicoAnd.class) {
					/*Se nao eh a primeira restricao (operador logico <> null) ou tem um 
					 * operador logico AND, entao usa o metodo retainAll para fazer a restricao*/
					colecao2.retainAll(colecao1);
				} else {
					/*Se tem um operador logico OU, usa o addAll*/
					colecao2.addAll(colecao1);
				}
				//
				relacoesResultantes.put(nomeRelacao, colecao2);
			} else {
				relacoesResultantes.put(nomeRelacao, colecao1);
			}
		}
	}

	private Map<String, Collection<Object>> juncao(Map<String, Collection<Object>> relacoesResultantes, RestricaoSimples restricao) throws Exception {
		Collection<Object> resultListTemp1 = new ArrayList<Object>();
		Collection<Object> resultListTemp2 = new ArrayList<Object>();
		//
		String nomeRelacao1 = restricao.getOperando1().getRelacao();
		String nomeRelacao2 = restricao.getOperando2().getRelacao();
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();
		//
		Collection<Object> relacao1 = relacoesResultantes.get(nomeRelacao1);
		if (relacao1 == null) {
			relacao1 = relacoes.get(nomeRelacao1);
		}
		Collection<Object> relacao2 = relacoesResultantes.get(nomeRelacao2);
		if (relacao2 == null) {
			relacao2 = relacoes.get(nomeRelacao2);
		}
		//
		Map<Object, List<Object>> hashTable = new HashMap<Object, List<Object>>();
		/*Insere as tupla da relacao1 em uma tabela hash (representada por um HashMap)*/
		for (Object tupla : relacao1) {
			Object campo = restricao.getOperando1().getValor();
			Object valor = QueryUtils.getValorDoCampo(tupla, campo.toString());
			List<Object> objetos = hashTable.get(valor);
			if (objetos == null) {
				objetos = new ArrayList<Object>();
			}
			objetos.add(tupla);
			hashTable.put(valor, objetos);
		}
		//
		for (Object tupla2 : relacao2) {
			Object campo = restricao.getOperando2().getValor();
			Object valor = QueryUtils.getValorDoCampo(tupla2, campo.toString());
			List<Object> tupla1 = hashTable.get(valor);
			//
			/*A condicao para que a restricao esteja OK eh que a tupla recuperada da hashTable exista*/
			boolean condicao = tupla1 != null;
			if (operadorRelacional.getClass() == Diferente.class) {
				/*Se for um operador relacional "DIFERENTE", a condicao passa a ser o contrario*/
				condicao = tupla1 == null;
			}
			//
			if (verificaCondicao(condicao, restricao)) {
				resultListTemp1.addAll(tupla1);
				resultListTemp2.add(tupla2);
			}
		}
		//
		Map<String, Collection<Object>> retorno = new LinkedHashMap<String, Collection<Object>>();
		retorno.put(nomeRelacao1, resultListTemp1);
		retorno.put(nomeRelacao1 + "Anterior", relacao1);
		retorno.put(nomeRelacao2, resultListTemp2);
		retorno.put(nomeRelacao2 + "Anterior", relacao2);
		return retorno;
	}

	private Map<String, Collection<Object>> where(Map<String, Collection<Object>> relacoesResultantes, RestricaoSimples restricao) throws Exception {
		/*Nome da relacao*/
		String nomeRelacao = getRelacaoString(restricao);
		/*Busca na lista de relacoes resultantes a que teve o nome descoberto anteriormente*/
		Collection<Object> relacaoAnterior = relacoesResultantes.get(nomeRelacao);
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
		/*A relacao resultante deste where + a ultima relacao resultante do ultimo where*/
		Map<String, Collection<Object>> retorno = new LinkedHashMap<String, Collection<Object>>();
		retorno.put(nomeRelacao, resultListTemp);
		retorno.put(nomeRelacao + "Anterior", relacaoAnterior);
		//
		return retorno;
	}

	private void verificaRestricao(RestricaoSimples restricao) throws ClausulaWhereException {
		/*Quando existe mais de uma relacao na clausula WHERE, se o nome de um campo*/
		/*eh utilizado na restricao, deve-se informar junto o nome da relacao */
		/*da qual o campo pertence*/
		if (relacoes.size() > 1) {
			String exception = "Nome da relação obrigatório na cláusula WHERE (" + restricao.toString().trim() + ")";
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
