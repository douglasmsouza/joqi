package br.com.joqi.semantico.consulta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.joqi.semantico.consulta.agrupamento.Agrupamento;
import br.com.joqi.semantico.consulta.disjuncao.UniaoRestricoes;
import br.com.joqi.semantico.consulta.ordenacao.Ordenacao;
import br.com.joqi.semantico.consulta.ordenacao.ResultListComparator;
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

	public Collection<ResultObject> getResultList() throws Exception {
		return executaOperacao(arvoreConsulta.getRaiz());
	}

	private Collection<ResultObject> executaOperacao(NoArvore no) throws Exception {
		Object operacao = no.getOperacao();
		//
		if (operacao instanceof Projecao) {
			return projecao(executaOperacao(no.getFilho()), (Projecao<?>) operacao);
		} else if (operacao.getClass() == Agrupamento.class) {
			return agrupamento(executaOperacao(no.getFilho()), (Agrupamento) operacao);
		} else if (operacao.getClass() == Ordenacao.class) {
			return ordenacao(executaOperacao(no.getFilho()), (Ordenacao) operacao);
		} else if (operacao.getClass() == UniaoRestricoes.class) {
			return executaRestricoes(no);
		}
		//
		return new ResultList();
	}

	private Collection<ResultObject> projecao(Collection<ResultObject> resultList, Projecao<?> projecao) {
		return resultList;
	}

	private Collection<ResultObject> agrupamento(Collection<ResultObject> resultList, Agrupamento agrupamento) {
		Map<String, ResultObject> tabelaHash = new HashMap<String, ResultObject>();
		//
		for (ResultObject objeto : resultList) {
			String hash = "";
			for (ProjecaoCampo campo : agrupamento.getCampos()) {
				try {
					hash += String.valueOf(QueryUtils.getValorDoCampo(objeto, campo).hashCode());
				} catch (CampoInexistenteException e) {
					continue;
				}
			}
			//
			if (!tabelaHash.containsKey(hash))
				tabelaHash.put(hash, objeto);
		}
		//
		return tabelaHash.values();
	}

	private Collection<ResultObject> ordenacao(Collection<ResultObject> resultList, Ordenacao ordenacao) {
		ResultListComparator comparator = new ResultListComparator(ordenacao);
		List<ResultObject> resultado = new ArrayList<ResultObject>(resultList);
		Collections.sort(resultado, comparator);
		return resultado;
	}

	private ResultList executaRestricoes(NoArvore raiz) throws Exception {
		ResultList resultado = new ResultList();
		//
		NoArvore filho = raiz.getFilho();
		while (filho != null) {
			resultado.addAll(produtoCartesiano(filho));
			filho = filho.getIrmao();
		}
		//
		return resultado;
	}

	private ResultList produtoCartesiano(NoArvore no) throws Exception {
		ResultList resultado = new ResultList();
		//
		NoArvore filho = no.getFilho();
		resultado = restricao(filho);
		//
		filho = filho.getIrmao();
		while (filho != null) {
			ResultList resultadoNovo = restricao(filho);
			//
			ResultList temp = new ResultList();
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

	private ResultList restricao(NoArvore no) throws Exception {
		Object operacao = no.getOperacao();
		//
		if (operacao.getClass() == RestricaoSimples.class) {
			RestricaoSimples restricao = (RestricaoSimples) operacao;
			if (restricao.getTipoBusca() == TipoBusca.LINEAR) {
				ResultList relacaoEntrada = restricao(no.getFilho());
				//
				return buscaLinear(relacaoEntrada, restricao);
			} else if (restricao.getTipoBusca() == TipoBusca.JUNCAO_HASH) {
				ResultList relacaoEntrada1 = restricao(no.getFilho());
				ResultList relacaoEntrada2;
				if (no.getFilho().getIrmao() != null)
					relacaoEntrada2 = restricao(no.getFilho().getIrmao());
				else
					relacaoEntrada2 = (ResultList) relacaoEntrada1.clone();
				//
				return juncaoHash(relacaoEntrada1, relacaoEntrada2, restricao);
			} else {
				ResultList relacaoEntrada1 = restricao(no.getFilho());
				ResultList relacaoEntrada2 = restricao(no.getFilho().getIrmao());
				//
				return juncaoLoopAninhado(relacaoEntrada1, relacaoEntrada2, restricao);
			}
		} else if (operacao.getClass() == ArvoreConsulta.class) {
			if (no.isFolha()) {
				return executaRestricoes(((ArvoreConsulta) no.getOperacao()).getRaizRestricoes());
			} else {
				ResultList relacaoEntrada1 = produtoCartesiano(no);
				ResultList relacaoEntrada2 = executaRestricoes(((ArvoreConsulta) no.getOperacao()).getRaizRestricoes());
				return interseccao(relacaoEntrada1, relacaoEntrada2);
			}
		} else if (no.isFolha()) {
			return ((Relacao) no.getOperacao()).getResultList();
		}
		//
		return null;
	}

	private ResultList juncaoLoopAninhado(ResultList relacaoEntrada1, ResultList relacaoEntrada2, RestricaoSimples restricao) throws Exception {
		ResultList resultado = new ResultList();
		//
		ProjecaoCampo operando1 = (ProjecaoCampo) restricao.getOperando1();
		ProjecaoCampo operando2 = (ProjecaoCampo) restricao.getOperando2();
		//
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();
		//
		for (ResultObject objeto1 : relacaoEntrada1) {
			Comparable<Object> valor1 = (Comparable<Object>) QueryUtils.getValorDoCampo(objeto1, operando1);
			for (ResultObject objeto2 : relacaoEntrada2) {
				Comparable<Object> valor2 = (Comparable<Object>) QueryUtils.getValorDoCampo(objeto2, operando2);
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
	private ResultList interseccao(ResultList relacaoEntrada1, ResultList relacaoEntrada2) throws Exception {
		ResultList resultado = new ResultList();
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
	private ResultList juncaoHash(ResultList relacaoEntrada1, ResultList relacaoEntrada2, RestricaoSimples restricao) throws Exception {
		ResultList resultado = new ResultList();
		//
		ProjecaoCampo operando1 = (ProjecaoCampo) restricao.getOperando1();
		ProjecaoCampo operando2 = (ProjecaoCampo) restricao.getOperando2();
		//
		Map<Object, ObjetoHash> tabelaHash = new HashMap<Object, ObjetoHash>();
		//
		for (ResultObject objeto1 : relacaoEntrada1) {
			Object chave = null;
			try {
				chave = QueryUtils.getValorDoCampo(objeto1, operando1);
			} catch (CampoInexistenteException e) {
				continue;
			}
			//
			ObjetoHash objetoHash = tabelaHash.get(chave);
			if (objetoHash != null) {
				ObjetoHash objetoHashNovo = new ObjetoHash(objeto1);
				objetoHashNovo.proximo = objetoHash;
				tabelaHash.put(chave, objetoHashNovo);
			} else {
				objetoHash = new ObjetoHash(objeto1);
				tabelaHash.put(chave, objetoHash);
			}
		}
		//
		for (ResultObject objeto2 : relacaoEntrada2) {
			Object chave = null;
			try {
				chave = QueryUtils.getValorDoCampo(objeto2, operando2);
			} catch (CampoInexistenteException e) {
				continue;
			}
			ObjetoHash objetoHash = tabelaHash.get(chave);
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
	private ResultList buscaLinear(ResultList relacao, RestricaoSimples restricao) throws CampoNaoComparableException, CampoInexistenteException {
		ResultList resultado = new ResultList();

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
