package br.com.joqi.semantico.consulta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoOr;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Entre;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.IgualBooleano;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Nulo;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;
import br.com.joqi.semantico.consulta.resultado.ResultSet;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.exception.ClausulaWhereException;
import br.com.joqi.semantico.exception.OperandosIncompativeisException;

/**
 * Implementacao das clausulas da query
 * 
 * @author Douglas Matheus de Souza em 26/07/2011
 */
public class QueryImpl {

	/**
	 * Classe auxiliar que representa um objeto do produto cartesiano
	 * 
	 * @author Douglas Matheus de Souza em 27/07/2011
	 */
	private class Tupla extends ResultObject {

		private boolean resultadoWhere;

		public Tupla(Map<String, Object> campos, boolean resultadoWhere) {
			super(campos);
			setResultadoWhere(resultadoWhere);
		}

		public Tupla(Map<String, Object> campos) {
			super(campos);
			setResultadoWhere(true);
		}

		public Tupla() {
			this(new HashMap<String, Object>());
		}

		public boolean isResultadoWhere() {
			return resultadoWhere;
		}

		public void setResultadoWhere(boolean resultadoWhere) {
			this.resultadoWhere = resultadoWhere;
		}

		public Tupla copiaCampos() {
			return new Tupla(this);
		}

		public Tupla copiaTudo() {
			return new Tupla(this, isResultadoWhere());
		}
	}

	private class Relacoes extends HashMap<String, Collection<?>> {
	}

	private class ProdutoCartesiano extends ArrayList<Tupla> {
	}

	private Query query;
	private Object objetoConsulta;

	/*Armazena o objeto Collection + o seu apelido na clausula FROM*/
	private Relacoes relacoes;
	/*Produto cartesiano entre as Collection da clausula FROM*/
	private ProdutoCartesiano produtoCartesiano;

	public QueryImpl(Query query, Object objetoConsulta) {
		this.query = query;
		this.objetoConsulta = objetoConsulta;
	}

	public void getResultado() throws Exception {
		double time = System.currentTimeMillis();
		//
		relacoes = getRelacoes();
		produtoCartesiano = getProdutoCartesiano();
		//
		ResultSet resultado = where(produtoCartesiano);
		//
		time = System.currentTimeMillis() - time;
		//
		for (ResultObject objeto : resultado) {
			System.out.println(objeto);
		}		
		System.out.println("-------------------------------");		
		System.out.println("Tempo total : " + time + " ms");
		System.out.println("Registro....: " + resultado.size());
		System.out.println("-------------------------------");
	}

	/**
	 * Retorna uma lista com as relacoes da clausula FROM
	 * 
	 * @throws Exception
	 * @author Douglas Matheus de Souza em 26/07/2011
	 */
	private Relacoes getRelacoes() throws Exception {
		Relacoes relacoes = new Relacoes();
		//
		for (Relacao relacao : query.getRelacoes()) {
			Collection<?> collection = QueryUtils.getColecao(objetoConsulta, relacao.getNome());
			collection = new ArrayList<Object>(collection);
			/*Insere a relacao em um HashMap*/
			if (relacao.getApelido() != null) {
				relacoes.put(relacao.getApelido(), collection);
			} else {
				relacoes.put(relacao.getNome(), collection);
			}
		}
		//
		return relacoes;
	}

	/**
	 * Cria um produto cartesiano entre todas as relacoes da clausula FROM
	 * 
	 * @author Douglas Matheus de Souza em 26/07/2011
	 */
	private ProdutoCartesiano getProdutoCartesiano() {
		ProdutoCartesiano resultado = new ProdutoCartesiano();
		//
		/*Insere no produto cartesiano todos os objetos da primeira relacao do FROM*/
		Iterator<Entry<String, Collection<?>>> iterator = relacoes.entrySet().iterator();
		Entry<String, Collection<?>> entry = iterator.next();
		for (Object o1 : entry.getValue()) {
			Tupla objCartesiano = new Tupla();
			objCartesiano.put(entry.getKey(), o1);
			resultado.add(objCartesiano);
		}
		/*Vai fazendo o produto cartesiano com as seguintes relacoes*/
		while (iterator.hasNext()) {
			entry = iterator.next();
			//
			Collection<Tupla> resultadoAux = new ArrayList<Tupla>();
			//
			for (Object o1 : entry.getValue()) {
				for (Tupla objCartesiano : resultado) {
					Tupla novoObjCartesiano = objCartesiano.copiaTudo();
					novoObjCartesiano.put(entry.getKey(), o1);
					resultadoAux.add(novoObjCartesiano);
				}
			}
			//
			resultado.clear();
			resultado.addAll(resultadoAux);
		}
		//
		return resultado;
	}

	/**
	 * Faz a projecao dos campos de um objeto
	 * 
	 * @author Douglas Matheus de Souza em 27/07/2011
	 */
	private ResultObject select(Tupla objetoCartesiano) throws Exception {
		if (query.getProjecoes().size() == 0) {
			return objetoCartesiano;
		} else {
			ResultObject joqiObject = new ResultObject();
			//
			for (Projecao<?> projecao : query.getProjecoes()) {
				Object valorProjecao = getValorOperando(projecao, objetoCartesiano);
				//
				if (projecao.getApelido() == null) {
					joqiObject.put(projecao.getValor().toString(), valorProjecao);
				} else {
					joqiObject.put(projecao.getApelido(), valorProjecao);
				}
			}
			//
			return joqiObject;
		}
	}

	/**
	 * Retorna o novo produto cartesiano, contendo somente os objetos que
	 * passaram em todas as restricoes
	 * 
	 * @author Douglas Matheus de Souza em 26/07/2011
	 */
	private ResultSet where(ProdutoCartesiano produtoCartesiano) throws Exception {
		ResultSet resultado = new ResultSet();
		//
		/*Passa por cada objeto do produto cartesiano, verificando se o objeto*/
		/*encaixa-se em todas as restricoes do WHERE*/
		for (Tupla objetoCartesiano : produtoCartesiano) {
			if (where(query, objetoCartesiano)) {
				resultado.add(select(objetoCartesiano));
			}
		}
		//
		return resultado;
	}

	/**
	 * Dado um objeto do produto cartesiano, verifica se ele passa em todas as
	 * restricoes
	 * 
	 * @author Douglas Matheus de Souza em 26/07/2011
	 */
	private boolean where(IPossuiRestricoes conjuntoRestricoes, Tupla objetoCartesiano) throws Exception {
		List<Restricao> restricoes = conjuntoRestricoes.getRestricoes();
		/*Itera em todas as restricoes da clausula WHERE*/
		for (int i = restricoes.size() - 1; i >= 0; i--) {
			Restricao restricao = restricoes.get(i);
			//
			boolean resultadoWhere = false;

			/*Verifica se o objeto se encaixa na restricao*/
			if (restricao.getClass() == RestricaoSimples.class) {
				resultadoWhere = where((RestricaoSimples) restricao, objetoCartesiano);
			} else if (restricao.getClass() == RestricaoConjunto.class) {
				RestricaoConjunto conjunto = (RestricaoConjunto) restricao;
				if (conjunto.isNegacao()) {
					conjunto.negarRestricoes();
				}
				resultadoWhere = where(conjunto, objetoCartesiano.copiaCampos());
			}

			/*Verifica se encaixa-se na restricao de acordo com o operador logico*/
			OperadorLogico operadorLogico = restricao.getOperadorLogico();
			if (operadorLogico == null) {
				objetoCartesiano.setResultadoWhere(resultadoWhere && objetoCartesiano.isResultadoWhere());
			} else if (operadorLogico.getClass() == OperadorLogicoOr.class) {
				objetoCartesiano.setResultadoWhere(resultadoWhere || objetoCartesiano.isResultadoWhere());
			} else if (operadorLogico.getClass() == OperadorLogicoAnd.class) {
				objetoCartesiano.setResultadoWhere(resultadoWhere && objetoCartesiano.isResultadoWhere());
			}

			/*Ignora as proximas restricoes caso seja certo que o resultado sera o mesmo*/
			if (!resultadoWhere) {
				while (i > 0 && restricao.getOperadorLogico().getClass() == OperadorLogicoAnd.class) {
					i--;
					restricao = restricoes.get(i);
				}
			} else {
				while (i > 0 && restricao.getOperadorLogico().getClass() == OperadorLogicoOr.class) {
					i--;
					restricao = restricoes.get(i);
				}
			}
		}
		//
		return objetoCartesiano.isResultadoWhere();
	}

	/**
	 * Dado um objeto do produto cartesiano, verifica se ele passa em
	 * determinada restricao
	 * 
	 * @author Douglas Matheus de Souza em 26/07/2011
	 */
	private boolean where(RestricaoSimples restricao, Tupla objetoCartesiano) throws Exception {
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
		//
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		OperadorRelacional operadorRelacional = restricao.getOperadorRelacional();
		//
		Object valorOperando1 = getValorOperando(operando1, objetoCartesiano);

		/*Se eh uma instrucao IS TRUE ou IS FALSE, compara logo de cara, uma vez que nao*/
		/*existem outros operandos na restricao*/
		if (operadorRelacional.getClass() == IgualBooleano.class) {
			return verificaCondicao(valorOperando1.equals(operando2.getValor()), restricao);
		}

		/*Se eh uma instrucao IS NULL, segue o mesmo caminho das instrucoes IS TRUE e IS FALSE*/
		if (operadorRelacional.getClass() == Nulo.class) {
			return verificaCondicao(valorOperando1 == null, restricao);
		}

		Object valorOperando2 = getValorOperando(operando2, objetoCartesiano);
		Object valorOperandoAux = null;

		/*Para efetuar a comparacao, eh necessario que os dois valores implementem Comparable*/
		if (!(valorOperando1 instanceof Comparable<?>))
			throw new ClausulaWhereException("O valor \"" + operando1.getValor() + "\" deve implementar a interface Comparable.");
		if (!(valorOperando2 instanceof Comparable<?>))
			throw new ClausulaWhereException("O valor \"" + operando2.getValor() + "\" deve implementar a interface Comparable.");

		/*Cria outro operando caso seja uma restricao BETWEEN*/
		if (operadorRelacional.getClass() == Entre.class) {
			Projecao<?> operandoAux = ((Entre) operadorRelacional).getOperandoEntre();
			valorOperandoAux = getValorOperando(operandoAux, objetoCartesiano);
			if (!(valorOperandoAux instanceof Comparable<?>))
				throw new ClausulaWhereException("O valor " + operandoAux.getValor() + " deve implementar a interface Comparable.");
		}

		try {
			valorOperando1 = getValorOperando(valorOperando1, valorOperando2, valorOperandoAux);
			valorOperando2 = getValorOperando(valorOperando2, valorOperando1, valorOperandoAux);
			valorOperandoAux = getValorOperando(valorOperandoAux, valorOperando1, valorOperando2);
		} catch (OperandosIncompativeisException e) {
			return false;
		}

		/*Converte os valores dos operando para Comparable para que seja possivel*/
		/*efetuar a comparacao*/
		Comparable<Object> valor1Comp = (Comparable<Object>) valorOperando1;
		Comparable<Object> valor2Comp = (Comparable<Object>) valorOperando2;
		Comparable<Object> valorAuxComp = (Comparable<Object>) valorOperandoAux;

		return verificaCondicao(operadorRelacional.compara(valor1Comp, valor2Comp, valorAuxComp), restricao);
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
			return valorOperando.toString().toLowerCase();
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

	/**
	 * Retorna o valor de um operando de uma restricao
	 * 
	 * @author Douglas Matheus de Souza em 27/07/2011
	 */
	private Object getValorOperando(Projecao<?> operando, Tupla objetoCartesiano) throws Exception {
		Object valor = operando.getValor();

		/*Busca o objeto referente ao operando*/
		Object objetoOperando;
		if (operando.getRelacao() != null)
			objetoOperando = objetoCartesiano.get(operando.getRelacao());
		else
			objetoOperando = objetoCartesiano.values().iterator().next();

		/*Se o operando fizer referencia a um campo, busca o valor deste campo no objeto*/
		if (operando.getClass() == ProjecaoCampo.class) {
			return QueryUtils.getValorDoCampo(objetoOperando, valor.toString());
		}

		return valor;
	}

	private boolean verificaCondicao(boolean comparacao, RestricaoSimples restricao) {
		return (comparacao && !restricao.isNegacao()) || (!comparacao && restricao.isNegacao());
	}
}
