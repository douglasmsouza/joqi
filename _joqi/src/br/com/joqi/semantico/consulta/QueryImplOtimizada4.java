package br.com.joqi.semantico.consulta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import br.com.joqi.semantico.consulta.busca.tipo.TipoBusca;
import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.plano.NoArvore;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Diferente;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Igual;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.IgualBooleano;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Nulo;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;
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
		ResultSet resultado = new ResultSet();
		//
		System.out.println("--------------------------------------------------------------------");
		arvoreConsulta.imprime();
		System.out.println("--------------------------------------------------------------------");
		//
		double time = System.currentTimeMillis();
		resultado = executarConsulta(arvoreConsulta.getRaizRestricoes());
		time = System.currentTimeMillis() - time;
		/*resultado = mergeSort(resultado, "pai", "nmPessoa");*/
		imprimeResultado(15, time, new String[] { "pai", "filho1", "filho2" }, resultado);
		//
		return resultado;
	}

	private ResultSet executarConsulta(NoArvore raiz) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		if (raiz != null) {
			NoArvore filho = raiz.getFilho();
			while (filho != null) {
				resultado.addAll(produtoCartesiano(filho));
				filho = filho.getIrmao();
			}
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
				ResultSet relacaoEntrada2 = restricao(no.getFilho().getIrmao());
				//
				return juncaoHash(relacaoEntrada1, relacaoEntrada2, restricao);
			} else {
				ResultSet relacaoEntrada1 = restricao(no.getFilho());
				ResultSet relacaoEntrada2 = restricao(no.getFilho().getIrmao());
				//
				return juncaoLoopAninhado(relacaoEntrada1, relacaoEntrada2, restricao);
			}
		} else if (operacao.getClass() == ArvoreConsulta.class) {
			return executarConsulta(((ArvoreConsulta) operacao).getRaizRestricoes());
		} else if (no.isFolha()) {
			return ((Relacao) no.getOperacao()).getResultSet();
		}
		//
		return null;
	}

	private ResultSet juncaoLoopAninhado(ResultSet relacaoEntrada1, ResultSet relacaoEntrada2, RestricaoSimples restricao) throws Exception {
		return juncaoMergeSort(relacaoEntrada1, relacaoEntrada2, restricao);
	}

	private ResultSet juncaoMergeSort(ResultSet relacaoEntrada1, ResultSet relacaoEntrada2, RestricaoSimples restricao) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		/*Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		//
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();
		//
		ResultObject[] relacao1 = ordenaMergeSort(relacaoEntrada1, operando1.getRelacao(), (String) operando1.getValor());
		ResultObject[] relacao2 = ordenaMergeSort(relacaoEntrada2, operando2.getRelacao(), (String) operando2.getValor());
		//
		for (int i = 0; i < relacao1.length; i++) {
			Comparable<Object> valor1 = getValorOperandoJuncaoComparable(operando1, relacao1[i]);
			int j = 0;
			for (j = 0; j < relacao2.length; j++) {
				Comparable<Object> valor2 = getValorOperandoJuncaoComparable(operando2, relacao2[j]);
				if (verificaCondicao(operadorRelacional.compara(valor1, valor2, null), restricao)) {
					break;
				}
			}
		}*/
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
				chave = getValorOperandoJuncao(restricao.getOperando1(), objeto1);
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
				chave = getValorOperandoJuncao(restricao.getOperando2(), objeto2);
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
	 * @author Douglas Matheus de Souza em 22/10/2011
	 */
	private ResultSet buscaLinear(ResultSet relacao, RestricaoSimples restricao) throws Exception {
		ResultSet resultado = new ResultSet();

		/*Guarda os operandos e o operador da restricao*/
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();

		/*Percorre a relacao, eliminando os registros que nao satisfazem a condicao*/
		for (ResultObject objeto : relacao) {
			Object valorOperando1 = null;
			try {
				valorOperando1 = getValorOperandoBuscaLinear(operando1, objeto);
			} catch (CampoInexistenteException e) {
				continue;
			} catch (CampoNaoComparableException e) {
				continue;
			}

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

			Object valorOperando2 = null;
			try {
				valorOperando2 = getValorOperandoBuscaLinear(operando2, objeto);
			} catch (CampoInexistenteException e) {
				continue;
			} catch (CampoNaoComparableException e) {
				continue;
			}

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
			Object objeto = resultObject.get(operando.getRelacao());
			valor = QueryUtils.getValorDoCampo(objeto, (String) operando.getValor());
			if (!(valor instanceof Comparable<?>))
				throw new CampoNaoComparableException("O valor \"" + operando.getValor() + "\" deve implementar a interface Comparable.");
		}
		return valor;
	}

	private Object getValorOperandoJuncao(Projecao<?> operando, ResultObject resultObject) throws CampoInexistenteException {
		Object objeto = resultObject.get(operando.getRelacao());
		Object valor = QueryUtils.getValorDoCampo(objeto, (String) operando.getValor());
		/*if (!(valor instanceof Comparable<?>))
			throw new CampoNaoComparableException("O valor \"" + operando.getValor() + "\" deve implementar a interface Comparable.");*/
		return valor;
	}

	private Comparable<Object> getValorOperandoJuncaoComparable(Projecao<?> operando, ResultObject resultObject) throws CampoInexistenteException {
		Object objeto = resultObject.get(operando.getRelacao());
		Object valor = QueryUtils.getValorDoCampo(objeto, (String) operando.getValor());
		/*if (!(valor instanceof Comparable<?>))
			throw new CampoNaoComparableException("O valor \"" + operando.getValor() + "\" deve implementar a interface Comparable.");*/
		return (Comparable<Object>) valor;
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

	private ResultObject[] ordenaMergeSort(ResultSet resultSet, String relacao, String campo) throws CampoInexistenteException {
		ResultObject[] resultObjects = resultSet.toArray(new ResultObject[0]);
		merge(resultObjects, relacao, campo);
		return resultObjects;
	}

	private void merge(ResultObject[] a, String relacao, String campo) throws CampoInexistenteException {
		ResultObject[] tmpArray = new ResultObject[a.length];
		merge(a, tmpArray, 0, a.length - 1, relacao, campo);
	}

	private void merge(ResultObject[] a, ResultObject[] tmpArray, int left, int right, String relacao, String campo) throws CampoInexistenteException {
		if (left < right) {
			int center = (left + right) / 2;
			merge(a, tmpArray, left, center, relacao, campo);
			merge(a, tmpArray, center + 1, right, relacao, campo);
			merge(a, tmpArray, left, center + 1, right, relacao, campo);
		}
	}

	private void merge(ResultObject[] a, ResultObject[] tmpArray, int leftPos, int rightPos, int rightEnd, String relacao, String campo)
			throws CampoInexistenteException {
		int leftEnd = rightPos - 1;
		int tmpPos = leftPos;
		int numElements = rightEnd - leftPos + 1;

		/*MergeSorteMain loop*/
		while (leftPos <= leftEnd && rightPos <= rightEnd) {
			Comparable<Object> rLeftPos = (Comparable<Object>) QueryUtils.getValorDoCampo(a[leftPos].get(relacao), campo);
			Comparable<Object> rRightPos = (Comparable<Object>) QueryUtils.getValorDoCampo(a[rightPos].get(relacao), campo);
			if (rLeftPos.compareTo(rRightPos) <= 0)
				tmpArray[tmpPos++] = a[leftPos++];
			else
				tmpArray[tmpPos++] = a[rightPos++];
		}

		while (leftPos <= leftEnd)
			/*Copy rest of first half*/
			tmpArray[tmpPos++] = a[leftPos++];

		while (rightPos <= rightEnd)
			/*Copy rest of right half*/
			tmpArray[tmpPos++] = a[rightPos++];

		/*Copy tmpArray back*/
		for (int i = 0; i < numElements; i++, rightEnd--)
			a[rightEnd] = tmpArray[rightEnd];
	}

	private void imprimeResultado(int tamanhoColuna, double tempo, String[] headers, ResultSet resultSet) {
		for (String h : headers) {
			char[] header = new char[tamanhoColuna];
			Arrays.fill(header, ' ');
			for (int i = 0; i < h.length(); i++) {
				header[i] = h.charAt(i);
			}
			System.out.print(header);
		}
		//
		System.out.println();
		System.out.println("------------------------------------");
		//
		for (ResultObject objeto : resultSet) {
			for (String c : headers) {
				char[] campo = new char[tamanhoColuna];
				Arrays.fill(campo, ' ');
				String valor = objeto.get(c).toString();
				for (int i = 0; i < valor.length(); i++) {
					campo[i] = valor.charAt(i);
				}
				System.out.print(campo);
			}
			System.out.println();
		}

		System.out.println("-------------------------------");
		System.out.println("Registros...: " + resultSet.size());
		System.out.println("Tempo total : " + tempo + " ms");
		System.out.println("-------------------------------");
	}
}
