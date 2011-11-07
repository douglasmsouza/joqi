package br.com.joqi.semantico.consulta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.joqi.semantico.consulta.agrupamento.Agrupamento;
import br.com.joqi.semantico.consulta.agrupamento.agregacao.FuncaoAgregacao;
import br.com.joqi.semantico.consulta.disjuncao.UniaoRestricoes;
import br.com.joqi.semantico.consulta.ordenacao.Ordenacao;
import br.com.joqi.semantico.consulta.ordenacao.ResultListComparator;
import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.plano.NoArvore;
import br.com.joqi.semantico.consulta.projecao.ListaProjecoes;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.projecao.ProjecaoFuncaoAgregacao;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples.TipoBusca;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.IgualBooleano;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Nulo;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;
import br.com.joqi.semantico.consulta.resultado.ResultList;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.consulta.util.JoqiUtil;
import br.com.joqi.semantico.consulta.util.ValorNulo;
import br.com.joqi.semantico.exception.CampoInexistenteException;
import br.com.joqi.semantico.exception.TiposIncompativeisException;
import br.com.joqi.semantico.exception.ValorInvalidoException;

public class QueryImpl {

	private class ObjetoHash {
		Object objeto;
		ObjetoHash proximo;

		ObjetoHash(Object objeto) {
			this.objeto = objeto;
		}
	}

	private ArvoreConsulta arvoreConsulta;

	public QueryImpl(ArvoreConsulta arvoreConsulta) {
		this.arvoreConsulta = arvoreConsulta;
	}

	public Collection<ResultObject> getResultList() throws Exception {
		return executaOperacao(arvoreConsulta.getRaiz());
	}

	private Collection<ResultObject> executaOperacao(NoArvore no) throws Exception {
		Object operacao = no.getOperacao();
		//
		if (operacao.getClass() == ListaProjecoes.class) {
			return projecao(executaOperacao(no.getFilho()), (ListaProjecoes) operacao);
		} else if (operacao.getClass() == Agrupamento.class) {
			return agrupamento(executaOperacao(no.getFilho()), (Agrupamento) operacao);
		} else if (operacao.getClass() == Ordenacao.class) {
			return ordenacao(executaOperacao(no.getFilho()), (Ordenacao) operacao);
		} else if (operacao.getClass() == UniaoRestricoes.class) {
			return resolveRestricoes(no);
		}
		//
		return new ResultList();
	}

	private Collection<ResultObject> projecao(Collection<ResultObject> resultList, ListaProjecoes projecoes) throws CampoInexistenteException {
		if (projecoes.size() == 0) {
			return resultList;
		} else {
			ResultList resultado = new ResultList();
			//
			for (ResultObject objeto : resultList) {
				ResultObject objetoNovo = new ResultObject();
				for (Projecao<?> projecao : projecoes) {
					Object valorProjecao = null;
					//
					if (projecao.getClass() == ProjecaoFuncaoAgregacao.class) {
						FuncaoAgregacao funcao = ((ProjecaoFuncaoAgregacao) projecao).getValor();
						valorProjecao = ((FuncaoAgregacao) objeto.get(funcao.toString())).getResultado();
					} else {
						valorProjecao = getValorProjecao(projecao, objeto);
					}
					//
					objetoNovo.put(projecao.getNomeNaConsulta(), valorProjecao);
				}
				resultado.add(objetoNovo);
			}
			//
			return resultado;
		}
	}

	private Object getValorProjecao(Projecao<?> projecao, ResultObject objeto) throws CampoInexistenteException {
		if (projecao.getClass() == ProjecaoCampo.class) {
			return QueryUtils.getValorDoCampo(objeto, (ProjecaoCampo) projecao);
		}
		return projecao.getValor();
	}

	/**
	 * Realiza o agrupamento de uma relacao
	 * 
	 * @param relacaoEntrada
	 * @param agrupamento
	 * @throws CampoInexistenteException
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
	private Collection<ResultObject> agrupamento(Collection<ResultObject> relacaoEntrada, Agrupamento agrupamento) throws CampoInexistenteException {
		Map<String, ResultObject> hashes = new HashMap<String, ResultObject>();
		//
		for (ResultObject objeto : relacaoEntrada) {
			String hash = hashAgrupamento(objeto, agrupamento);

			/*Se hash nao existe na tabela, insere o objeto*/
			if (!hashes.containsKey(hash)) {
				for (FuncaoAgregacao funcao : agrupamento.getFuncoesAgregacao()) {
					objeto.put(funcao.toString(), funcao.copia());
				}
				//
				hashes.put(hash, objeto);
			}
			//
			for (FuncaoAgregacao funcao : agrupamento.getFuncoesAgregacao()) {
				Object valor = QueryUtils.getValorDoCampoNaoNulo(objeto, funcao.getCampo());
				//
				FuncaoAgregacao funcaoObjeto = (FuncaoAgregacao) hashes.get(hash).get(funcao.toString());
				funcaoObjeto.atualizaResultado(valor);
			}
		}
		//
		relacaoEntrada = null;
		//
		return hashes.values();
	}

	/**
	 * Metodo para gera o hash do agrupamento com base nos campos de agrupamento
	 * 
	 * @param objeto
	 * @param agrupamento
	 * @throws CampoInexistenteException
	 * @author Douglas Matheus de Souza em 30/10/2011
	 */
	private String hashAgrupamento(ResultObject objeto, Agrupamento agrupamento) throws CampoInexistenteException {
		String hash = "";
		for (Projecao<?> campo : agrupamento.getCampos()) {
			hash += QueryUtils.getValorOperando(objeto, campo);
		}
		return hash;
	}

	/**
	 * Ordena uma relacao usando um comparator
	 * 
	 * @param relacaoEntrada
	 * @param ordenacao
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
	private Collection<ResultObject> ordenacao(Collection<ResultObject> relacaoEntrada, Ordenacao ordenacao) {
		List<ResultObject> resultado = new ArrayList<ResultObject>(relacaoEntrada);
		//
		ResultListComparator comparator = new ResultListComparator(ordenacao);
		Collections.sort(resultado, comparator);
		//
		relacaoEntrada = null;
		//
		return resultado;
	}

	/**
	 * Resolve as restricoes que estao ligadas a um determinado no
	 * 
	 * @param raiz
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
	private ResultList resolveRestricoes(NoArvore raiz) throws Exception {
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

	/**
	 * Faz o produto cartesiano com os filhos de um no da arvore de consulta
	 * 
	 * @param no
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
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

	/**
	 * Resolve as restricoes recursivamente. A relacao resultante de cada
	 * restricao resolvida serve de entrada para a restricao anterior.
	 * 
	 * @param no
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
	private ResultList restricao(NoArvore no) throws Exception {
		Object operacao = no.getOperacao();
		//
		if (operacao.getClass() == RestricaoSimples.class) {
			RestricaoSimples restricao = (RestricaoSimples) operacao;
			//
			TipoBusca tipoBusca = restricao.getTipoBusca();
			NoArvore filho1 = no.getFilho();
			NoArvore filho2 = filho1.getIrmao();
			//
			if (tipoBusca == TipoBusca.LINEAR) {
				ResultList relacaoEntrada = restricao(filho1);
				//
				return buscaLinear(relacaoEntrada, restricao);
			} else {
				ResultList relacaoEntrada1 = restricao(filho1);
				ResultList relacaoEntrada2 = restricao(filho2);
				if (tipoBusca == TipoBusca.JUNCAO_HASH) {
					return juncaoHash(relacaoEntrada1, relacaoEntrada2, restricao);
				} else {
					return juncaoLoopAninhado(relacaoEntrada1, relacaoEntrada2, restricao);
				}
			}
		} else if (operacao.getClass() == ArvoreConsulta.class) {
			if (no.isFolha()) {
				return resolveRestricoes(((ArvoreConsulta) no.getOperacao()).getRaizRestricoes());
			} else {
				ResultList relacaoEntrada1 = produtoCartesiano(no);
				ResultList relacaoEntrada2 = resolveRestricoes(((ArvoreConsulta) no.getOperacao()).getRaizRestricoes());
				return interseccao(relacaoEntrada1, relacaoEntrada2);
			}
		} else if (no.isFolha()) {
			return ((Relacao) no.getOperacao()).getResultList();
		}
		//
		return null;
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
		Map<ResultObject, Boolean> hashes = new HashMap<ResultObject, Boolean>();
		//
		for (ResultObject objeto1 : relacaoEntrada1) {
			hashes.put(objeto1, true);
		}
		//
		for (ResultObject objeto2 : relacaoEntrada2) {
			if (hashes.get(objeto2) != null) {
				resultado.add(objeto2);
			}
		}
		//
		relacaoEntrada1 = null;
		relacaoEntrada2 = null;
		//
		return resultado;
	}

	/**
	 * Efetua a juncao entre duas relacoes utilizando loops aninhados.
	 * 
	 * @param relacaoEntrada1
	 * @param relacaoEntrada2
	 * @param restricao
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
	private ResultList juncaoLoopAninhado(ResultList relacaoEntrada1, ResultList relacaoEntrada2, RestricaoSimples restricao) throws Exception {
		ResultList resultado = new ResultList();
		//
		ProjecaoCampo operando1 = (ProjecaoCampo) restricao.getOperando1();
		ProjecaoCampo operando2 = (ProjecaoCampo) restricao.getOperando2();
		//
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();
		//
		for (ResultObject objeto1 : relacaoEntrada1) {
			/*Retorna o valor do primeiro operando*/
			Object valor1 = QueryUtils.getValorDoCampoNaoNulo(objeto1, operando1);
			if (!(valor1 instanceof Comparable))
				continue;

			for (ResultObject objeto2 : relacaoEntrada2) {
				/*Retorna o valor do segundo operando*/
				Object valor2 = QueryUtils.getValorDoCampoNaoNulo(objeto2, operando2);
				if (!(valor2 instanceof Comparable))
					continue;

				/*Neste ponto, verifica se os valores de juncao sao do mesmo tipo de dado. 
				Caso nao sejam, uma excecao eh lancada e o processo deve continuar na proxima tupla*/
				try {
					valor1 = verificaTiposOperandos(valor1, valor2);
					valor2 = verificaTiposOperandos(valor2, valor1);
				} catch (TiposIncompativeisException e) {
					continue;
				}

				/*Converte os valores dos operando para Comparable para que seja possivel
				efetuar a comparacao*/
				Comparable<Object> valor1Comp = (Comparable<Object>) valor1;
				Comparable<Object> valor2Comp = (Comparable<Object>) valor2;
				//
				if (verificaCondicao(operadorRelacional.compara(valor1Comp, valor2Comp, null), restricao)) {
					ResultObject resultObject = new ResultObject();
					resultObject.putAll(objeto1);
					resultObject.putAll(objeto2);
					resultado.add(resultObject);
				}
			}
		}
		//
		relacaoEntrada1 = null;
		relacaoEntrada2 = null;
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
		Map<Object, ObjetoHash> hashes = new HashMap<Object, ObjetoHash>();
		//
		for (ResultObject objeto1 : relacaoEntrada1) {
			Object chave = QueryUtils.getValorDoCampo(objeto1, operando1);
			//
			ObjetoHash objetoHash = hashes.get(chave);
			if (objetoHash != null) {
				ObjetoHash objetoHashNovo = new ObjetoHash(objeto1);
				objetoHashNovo.proximo = objetoHash;
				hashes.put(chave, objetoHashNovo);
			} else {
				objetoHash = new ObjetoHash(objeto1);
				hashes.put(chave, objetoHash);
			}
		}
		//
		for (ResultObject objeto2 : relacaoEntrada2) {
			Object chave = QueryUtils.getValorDoCampo(objeto2, operando2);
			//
			ObjetoHash objetoHash = hashes.get(chave);
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
		relacaoEntrada1 = null;
		relacaoEntrada2 = null;
		//
		return resultado;
	}

	/**
	 * Algoritmo de busca linear
	 * 
	 * @param relacaoEntrada
	 * @param restricao
	 * @throws ValorInvalidoException
	 * @throws CampoInexistenteException
	 * @author Douglas Matheus de Souza em 22/10/2011
	 */
	private ResultList buscaLinear(ResultList relacaoEntrada, RestricaoSimples restricao) throws CampoInexistenteException {
		ResultList resultado = new ResultList();

		/*Guarda os operandos e o operador da restricao*/
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();

		/*Percorre a relacao, eliminando os registros que nao satisfazem a condicao*/
		for (ResultObject objeto : relacaoEntrada) {
			Object valorOperando1 = QueryUtils.getValorOperando(objeto, operando1);
			if (!(valorOperando1 instanceof Comparable))
				continue;

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
				if (verificaCondicao(valorOperando1.getClass() == ValorNulo.class, restricao)) {
					resultado.add(objeto);
				}
				continue;
			}

			Object valorOperando2 = QueryUtils.getValorOperando(objeto, operando2);
			if (!(valorOperando2 instanceof Comparable))
				continue;

			try {
				valorOperando1 = verificaTiposOperandos(valorOperando1, valorOperando2);
				valorOperando2 = verificaTiposOperandos(valorOperando2, valorOperando1);
			} catch (TiposIncompativeisException e) {
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
		relacaoEntrada = null;
		//
		return resultado;
	}

	/**
	 * Verifica a condicao de uma restricao
	 * 
	 * @param comparacao
	 * @param restricao
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
	private boolean verificaCondicao(boolean comparacao, RestricaoSimples restricao) {
		return (comparacao && !restricao.isNegacao()) || (!comparacao && restricao.isNegacao());
	}

	/**
	 * Verifica se os tipos de dados de dois valores sao compativeis
	 * 
	 * @param valor1
	 * @param valor2
	 * @throws TiposIncompativeisException
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
	private Object verificaTiposOperandos(Object valor1, Object valor2) throws TiposIncompativeisException {
		if (valor1.getClass() != valor2.getClass()) {
			/*Valores numericos podem ser de classes diferentes (Double, Integer, Float...), 
			uma vez que serao comparados sempre como Double. Entao, caso somente um dos dois 
			seja numerico, lanca excecao*/
			if (valor1 instanceof Number ^ valor2 instanceof Number)
				throw new TiposIncompativeisException();

			if (valor1 instanceof ValorNulo ^ valor2 instanceof ValorNulo)
				throw new TiposIncompativeisException();
		}
		//
		if (valor1 instanceof String) {
			/*Para Strings, os valores sao convertidos para minusculo e sem acentuacao para que fiquem iguais*/
			return JoqiUtil.retiraAcentuacao(((String) valor1).toLowerCase());
		} else if (valor1 instanceof Number) {
			/*Para valores numericos a comparacao eh feita sempre em Double*/
			return ((Number) valor1).doubleValue();
		}
		//
		return valor1;
	}
}
