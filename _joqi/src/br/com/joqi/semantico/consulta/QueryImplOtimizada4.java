
package br.com.joqi.semantico.consulta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import br.com.joqi.semantico.consulta.disjuncao.UniaoRestricoes;
import br.com.joqi.semantico.consulta.ordenacao.Ordenacao;
import br.com.joqi.semantico.consulta.ordenacao.ResultSetComparator;
import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.plano.NoArvore;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples.TipoBusca;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Diferente;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Igual;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.IgualBooleano;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Nulo;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;
import br.com.joqi.semantico.consulta.resultado.ResultList;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.consulta.resultado.ResultSet;
import br.com.joqi.semantico.consulta.util.JoqiUtil;
import br.com.joqi.semantico.exception.CampoInexistenteException;
import br.com.joqi.semantico.exception.CampoNaoComparableException;
import br.com.joqi.semantico.exception.OperandosIncompativeisException;

public class QueryImplOtimizada4 {

	private class ObjetoHash {
		Object objeto;
		ObjetoHash proximo;

		ObjetoHash(Object objeto) {
			this.objeto = objeto;
		}
	}

	private ArvoreConsulta arvoreConsulta;

	public QueryImplOtimizada4(ArvoreConsulta arvoreConsulta) {
		this.arvoreConsulta = arvoreConsulta;
	}

	public ResultSet getResultSet() throws Exception {
		return executaOperacao(arvoreConsulta.getRaiz());
	}

	private ResultSet executaOperacao(NoArvore no) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		Object operacao = no.getOperacao();
		//
		if (operacao.getClass() == Ordenacao.class) {
			resultado = executaOperacao(no.getFilho());
			/*Ordena o resultado*/
			ResultSetComparator comparator = new ResultSetComparator((Ordenacao) operacao);
			ResultList resultList = new ResultList(resultado);
			Collections.sort(resultList, comparator);
			resultado = new ResultSet(resultList);
		} else if (operacao.getClass() == UniaoRestricoes.class) {
			resultado = executaRestricoes(no);
		}
		//
		return resultado;
	}

	private ResultSet executaRestricoes(NoArvore raiz) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		NoArvore filho = raiz.getFilho();
		while (filho != null) {
			resultado.addAll(produtoCartesiano(filho));
			filho = filho.getIrmao();
		}
		//
		return resultado;
	}

	private ResultSet produtoCartesiano(NoArvore no) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		NoArvore filho = no.getFilho();
		resultado = restricao(filho);
		//
		filho = filho.getIrmao();
		while (filho != null) {
			ResultSet resultadoNovo = restricao(filho);
			//
			ResultSet temp = new ResultSet();
			for (ResultObject r1 : resultado) {
				for (ResultObject r2 : resultadoNovo) {
					ResultObject resultObject = new ResultObject();
					resultObject.putAll(r1);
					resultObject.putAll(r2);
					temp.add(resultObject);
				}
			}
			resultado = temp;
			//
			filho = filho.getIrmao();
		}
		//
		return resultado;
	}

	private ResultSet restricao(NoArvore no) throws Exception {
		Object operacao = no.getOperacao();
		//
		if (operacao.getClass() == RestricaoSimples.class) {
			RestricaoSimples restricao = (RestricaoSimples) operacao;
			if (restricao.getTipoBusca() == TipoBusca.LINEAR) {
				ResultSet relacaoEntrada = restricao(no.getFilho());
				//
				return buscaLinear(relacaoEntrada, restricao);
			} else if (restricao.getTipoBusca() == TipoBusca.JUNCAO_HASH) {
				ResultSet relacaoEntrada1 = restricao(no.getFilho());
				ResultSet relacaoEntrada2;
				if (no.getFilho().getIrmao() != null)
					relacaoEntrada2 = restricao(no.getFilho().getIrmao());
				else
					relacaoEntrada2 = (ResultSet) relacaoEntrada1.clone();
				//
				return juncaoHash(relacaoEntrada1, relacaoEntrada2, restricao);
			} else {
				ResultSet relacaoEntrada1 = restricao(no.getFilho());
				ResultSet relacaoEntrada2 = restricao(no.getFilho().getIrmao());
				//
				return juncaoLoopAninhado(relacaoEntrada1, relacaoEntrada2, restricao);
			}
		} else if (operacao.getClass() == ArvoreConsulta.class) {
			if (no.isFolha()) {
				return executaRestricoes(((ArvoreConsulta) no.getOperacao()).getRaizRestricoes());
			} else {
				ResultSet relacaoEntrada1 = produtoCartesiano(no);
				ResultSet relacaoEntrada2 = executaRestricoes(((ArvoreConsulta) no.getOperacao()).getRaizRestricoes());
				return interseccao(relacaoEntrada1, relacaoEntrada2);
			}
		} else if (no.isFolha()) {
			return ((Relacao) no.getOperacao()).getResultSet();
		}
		//
		return null;
	}

	private ResultSet juncaoLoopAninhado(ResultSet relacaoEntrada1, ResultSet relacaoEntrada2, RestricaoSimples restricao) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		//
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();
		//
		for (ResultObject objeto1 : relacaoEntrada1) {
			Comparable<Object> valor1 = (Comparable<Object>) QueryUtils.getValorDoCampo(objeto1, (ProjecaoCampo) operando1);
			for (ResultObject objeto2 : relacaoEntrada2) {
				Comparable<Object> valor2 = (Comparable<Object>) QueryUtils.getValorDoCampo(objeto2, (ProjecaoCampo) operando2);
				//
				if (verificaCondicao(operadorRelacional.compara(valor1, valor2, null), restricao)) {
					ResultObject resultObject = new ResultObject();
					resultObject.putAll(objeto1);
					resultObject.putAll(objeto2);
					resultado.add(resultObject);
				}
			}
		}
		//
		return resultado;
	}

	/**
	 * Efetua a operacao de interseccao entre duas relacoes
	 * 
	 * @param relacaoEntrada1
	 * @param relacaoEntrada2
	 * @author Douglas Matheus de Souza em 26/10/2011
	 */
	private ResultSet interseccao(ResultSet relacaoEntrada1, ResultSet relacaoEntrada2) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		Map<ResultObject, Boolean> hashTable = new HashMap<ResultObject, Boolean>();
		//
		for (ResultObject objeto1 : relacaoEntrada1) {
			hashTable.put(objeto1, true);
		}
		//
		for (ResultObject objeto2 : relacaoEntrada2) {
			if (hashTable.get(objeto2) != null) {
				resultado.add(objeto2);
			}
		}
		//
		return resultado;
	}

	/**
	 * Algoritmo de juncao utilizando hash
	 * 
	 * @param relacaoEntrada1
	 * @param relacaoEntrada2
	 * @param restricao
	 * @author Douglas Matheus de Souza em 22/10/2011
	 */
	private ResultSet juncaoHash(ResultSet relacaoEntrada1, ResultSet relacaoEntrada2, RestricaoSimples restricao) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		Map<Object, ObjetoHash> hashTable = new HashMap<Object, ObjetoHash>();
		//
		for (ResultObject objeto1 : relacaoEntrada1) {
			Object chave = null;
			try {
				chave = QueryUtils.getValorDoCampo(objeto1, (ProjecaoCampo) restricao.getOperando1());
			} catch (CampoInexistenteException e) {
				continue;
			}
			//
			ObjetoHash objetoHash = hashTable.get(chave);
			if (objetoHash != null) {
				ObjetoHash objetoHashNovo = new ObjetoHash(objeto1);
				objetoHashNovo.proximo = objetoHash;
				hashTable.put(chave, objetoHashNovo);
			} else {
				objetoHash = new ObjetoHash(objeto1);
				hashTable.put(chave, objetoHash);
			}
		}
		//
		for (ResultObject objeto2 : relacaoEntrada2) {
			Object chave = null;
			try {
				chave = QueryUtils.getValorDoCampo(objeto2, (ProjecaoCampo) restricao.getOperando2());
			} catch (CampoInexistenteException e) {
				continue;
			}
			ObjetoHash objetoHash = hashTable.get(chave);
			//
			while (objetoHash != null) {
				Object objeto = objetoHash.objeto;
				//
				ResultObject tupla = new ResultObject();
				tupla.putAll((ResultObject) objeto);
				tupla.putAll((ResultObject) objeto2);
				resultado.add(tupla);
				//
				objetoHash = objetoHash.proximo;
			}
		}
		//
		return resultado;
	}

	/**
	 * Algoritmo de busca linear
	 * 
	 * @param relacao
	 * @param restricao
	 * @throws CampoNaoComparableException
	 * @throws CampoInexistenteException
	 * @author Douglas Matheus de Souza em 22/10/2011
	 */
	private ResultSet buscaLinear(ResultSet relacao, RestricaoSimples restricao) throws CampoNaoComparableException, CampoInexistenteException {
		ResultSet resultado = new ResultSet();

		/*Guarda os operandos e o operador da restricao*/
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();

		/*Percorre a relacao, eliminando os registros que nao satisfazem a condicao*/
		for (ResultObject objeto : relacao) {
			Object valorOperando1 = getValorOperandoBuscaLinear(operando1, objeto);

			/*Se eh uma instrucao IS TRUE ou IS FALSE, compara logo de cara, uma vez que nao*/
			/*existem outros operandos na restricao*/
			if (operadorRelacional.getClass() == IgualBooleano.class) {
				if (verificaCondicao(valorOperando1.equals(operando2.getValor()), restricao)) {
					resultado.add(objeto);
				}
				continue;
			}

			/*Se eh uma instrucao IS NULL, segue o mesmo caminho das instrucoes IS TRUE e IS FALSE*/
			if (operadorRelacional.getClass() == Nulo.class) {
				if (verificaCondicao(valorOperando1 == null, restricao)) {
					resultado.add(objeto);
				}
				continue;
			}

			Object valorOperando2 = getValorOperandoBuscaLinear(operando2, objeto);

			/*Caso o valor do campo na tupla seja NULL, eh um caso "especial" */
			if (valorOperando1 == null || valorOperando2 == null) {
				if (operadorRelacional.getClass() == Igual.class) {
					if (restricao.isNegacao()) {
						resultado.add(objeto);
					}
				} else if (operadorRelacional.getClass() == Diferente.class) {
					if (!restricao.isNegacao()) {
						resultado.add(objeto);
					}
				}
				continue;
			}

			try {
				valorOperando1 = getValorOperandoTiposCompativeis(valorOperando1, valorOperando2);
				valorOperando2 = getValorOperandoTiposCompativeis(valorOperando2, valorOperando1);
			} catch (OperandosIncompativeisException e) {
				continue;
			}

			/*Converte os valores dos operando para Comparable para que seja possivel
			efetuar a comparacao*/
			Comparable<Object> valor1Comp = (Comparable<Object>) valorOperando1;
			Comparable<Object> valor2Comp = (Comparable<Object>) valorOperando2;

			if (verificaCondicao(operadorRelacional.compara(valor1Comp, valor2Comp, null), restricao)) {
				resultado.add(objeto);
				continue;
			}
		}
		//
		return resultado;
	}

	private boolean verificaCondicao(boolean comparacao, RestricaoSimples restricao) {
		return (comparacao && !restricao.isNegacao()) || (!comparacao && restricao.isNegacao());
	}

	private Object getValorOperandoBuscaLinear(Projecao<?> operando, ResultObject resultObject) throws CampoInexistenteException,
			CampoNaoComparableException {
		Object valor = operando.getValor();
		if (operando.getClass() == ProjecaoCampo.class) {
			valor = QueryUtils.getValorDoCampo(resultObject, (ProjecaoCampo) operando);
			if (!(valor instanceof Comparable<?>))
				throw new CampoNaoComparableException("O valor \"" + operando.getValor() + "\" deve implementar a interface Comparable.");
		}
		return valor;
	}

	private Object getValorOperandoTiposCompativeis(Object valor1, Object valor2) throws OperandosIncompativeisException {
		if (valor1.getClass() != valor2.getClass()) {
			/*Valores numericos podem ser de classes diferentes, uma vez que serao comparados
			 * sempre como Double. Entao*/
			if (valor1 instanceof Number ^ valor2 instanceof Number)
				throw new OperandosIncompativeisException();
		}
		//
		if (valor1 instanceof String) {
			/*Para Strings, os valores sao convertidos para minusculo para que fiquem iguais*/
			return JoqiUtil.retiraAcentuacao(((String) valor1).toLowerCase());
		} else if (valor1 instanceof Number) {
			/*Se comparacao for entre um numero e um "nao numero", eh invalida*/
			if (!(valor2 instanceof Number)) {
				throw new OperandosIncompativeisException();
			}

			/*Para valores numericos a comparacao eh feita sempre em Double*/
			return ((Number) valor1).doubleValue();
		}
		//
		return valor1;
	}
}
