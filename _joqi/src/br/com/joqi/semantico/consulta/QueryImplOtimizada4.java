package br.com.joqi.semantico.consulta;

import java.util.Collection;

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
import br.com.joqi.semantico.exception.ClausulaWhereException;
import br.com.joqi.semantico.exception.OperandosIncompativeisException;

public class QueryImplOtimizada4 {

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
		executarRestricoes();
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
		System.out.println(resultado);
		return resultado;
	}

	private ResultSet produtoCartesiano(NoArvore no) throws Exception {
		return restringe(no.getFilho());
	}

	private ResultSet restringe(NoArvore no) throws Exception {
		if (no.isFolha()) {
			return transformaEmResultSet((Relacao) no.getOperacao());
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

	private ResultSet juncaoLoopAninhado(ResultSet relacaoEntrada1, ResultSet relacaoEntrada2, RestricaoSimples restricao) {
		ResultSet resultado = new ResultSet();
		return resultado;
	}

	private ResultSet juncaoHash(ResultSet relacaoEntrada1, ResultSet relacaoEntrada2, RestricaoSimples restricao) {
		ResultSet resultado = new ResultSet();
		return resultado;
	}

	private ResultSet buscaLinear(ResultSet relacao, RestricaoSimples restricao) throws Exception {
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

	private Object getValorOperandoBuscaLinear(Projecao<?> operando, ResultObject resultObject) throws Exception {
		Object valor = operando.getValor();
		if (operando.getClass() == ProjecaoCampo.class) {
			Object objeto = resultObject.get(operando.getRelacao());
			valor = QueryUtils.getValorDoCampo(objeto, (String) operando.getValor());
			if (!(valor instanceof Comparable<?>))
				throw new ClausulaWhereException("O valor \"" + operando.getValor() + "\" deve implementar a interface Comparable.");
		}
		return valor;
	}

	private Object getValorOperandoTiposCompativeis(Object valor1, Object valor2) throws Exception {
		if (valor1.getClass() != valor2.getClass()) {
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

	private ResultSet transformaEmResultSet(Relacao relacao) {
		ResultSet resultSet = new ResultSet();
		//
		Collection<Object> colecao = relacao.getColecao();
		for (Object o : colecao) {
			ResultObject tupla = new ResultObject();
			tupla.put(relacao.getNomeNaConsulta(), o);
			resultSet.add(tupla);
		}
		//
		return resultSet;
	}

}
