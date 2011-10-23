package br.com.joqi.semantico.consulta;

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
		executarRestricoes();
		System.out.println("Tempo: " + (System.currentTimeMillis() - time) + " ms");
		//
		return resultado;
	}

	public ResultSet executarRestricoes() throws Exception {
		ResultSet resultado = new ResultSet();
		//
		NoArvore no = arvoreConsulta.getRaizRestricoes();
		if (no != null) {
			NoArvore filho = no.getFilho();
			while (filho != null) {
				resultado.addAll(produtoCartesiano(filho));
				filho = filho.getIrmao();
			}
		}
		//
		System.out.println(resultado.size());
		return resultado;
	}

	private ResultSet produtoCartesiano(NoArvore no) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		NoArvore filho = no.getFilho();
		resultado = restringe(filho);
		//
		filho = filho.getIrmao();
		while (filho != null) {
			ResultSet resultadoNovo = restringe(filho);
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

	private ResultSet restringe(NoArvore no) throws Exception {
		if (no.isFolha()) {
			return ((Relacao) no.getOperacao()).getResultSet();
		}
		//
		Object operacao = no.getOperacao();
		//
		if (operacao instanceof RestricaoSimples) {
			RestricaoSimples restricao = (RestricaoSimples) operacao;
			if (restricao.getTipoBusca() == TipoBusca.LINEAR) {
				ResultSet relacaoEntrada = restringe(no.getFilho());
				//
				return buscaLinear(relacaoEntrada, restricao);
			} else if (restricao.getTipoBusca() == TipoBusca.JUNCAO_HASH) {
				ResultSet relacaoEntrada1 = restringe(no.getFilho());
				ResultSet relacaoEntrada2 = restringe(no.getFilho().getIrmao());
				//
				return juncaoHash(relacaoEntrada1, relacaoEntrada2, restricao);
			} else {
				ResultSet relacaoEntrada1 = restringe(no.getFilho());
				ResultSet relacaoEntrada2 = restringe(no.getFilho().getIrmao());
				//
				return juncaoLoopAninhado(relacaoEntrada1, relacaoEntrada2, restricao);
			}
		}
		//
		return null;
	}

	private ResultSet juncaoLoopAninhado(ResultSet relacaoEntrada1, ResultSet relacaoEntrada2, RestricaoSimples restricao) throws Exception {
		ResultSet resultado = new ResultSet();
		// TODO: Testar a implementacao do Sort-Merge. O loop aninhado ficou
		// lento.
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
