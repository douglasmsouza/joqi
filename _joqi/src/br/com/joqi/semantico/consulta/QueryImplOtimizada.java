package br.com.joqi.semantico.consulta;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.IPossuiRestricoes;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.restricao.RestricaoConjunto;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Entre;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.IgualBooleano;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Nulo;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;
import br.com.joqi.semantico.exception.ClausulaWhereException;
import br.com.joqi.semantico.exception.OperandosIncompativeisException;

public class QueryImplOtimizada {

	private Query query;
	private Object objetoConsulta;
	//
	private HashMap<String, Collection<?>> relacoes;
	private HashMap<String, Map<Object, Object>> hashTables;

	public QueryImplOtimizada(Query query, Object objetoConsulta) {
		this.query = query;
		this.objetoConsulta = objetoConsulta;
	}

	public void getResultado() throws Exception {
		double time = System.currentTimeMillis();
		//
		criaReferenciasRelacoes();
		where(query);
		//
		time = System.currentTimeMillis() - time;
		//
		System.out.println(relacoes);
		//
		System.out.println("-------------------------------");
		System.out.println("Tempo total : " + time + " ms");
		System.out.println("-------------------------------");
		//
	}

	private void criaReferenciasRelacoes() throws Exception {
		relacoes = new HashMap<String, Collection<?>>();
		hashTables = new HashMap<String, Map<Object, Object>>();
		//
		for (Relacao relacao : query.getRelacoes()) {
			Collection<?> collection = QueryUtils.getColecao(objetoConsulta, relacao.getNome());
			/*Insere a relacao em um HashMap*/
			if (relacao.getApelido() != null) {
				relacoes.put(relacao.getApelido(), collection);
			} else {
				relacoes.put(relacao.getNome(), collection);
			}
		}
	}

	private void where(IPossuiRestricoes conjuntoRestricoes) throws Exception {
		for (Restricao restricao : conjuntoRestricoes.getRestricoes()) {
			if (restricao.getClass() == RestricaoSimples.class) {
				where((RestricaoSimples) restricao);
			} else if (restricao.getClass() == RestricaoConjunto.class) {
				RestricaoConjunto conjunto = (RestricaoConjunto) restricao;
				if (conjunto.isNegacao()) {
					conjunto.negarRestricoes();
				}
				where(conjunto);
			}
		}
	}

	/**
	 * Dado um objeto do produto cartesiano, verifica se ele passa em
	 * determinada restricao
	 * 
	 * @author Douglas Matheus de Souza em 26/07/2011
	 */
	private boolean where(RestricaoSimples restricao) throws Exception {
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

		/*Caso seja uma restricao simples, ou seja, somente um dos operandos eh uma
		 * constante e o outro eh uma referencia a um atributo*/
		if (operando1 instanceof ProjecaoCampo ^ operando2 instanceof ProjecaoCampo) {
			Collection<?> collection = relacoes.get(operando1.getRelacao());
			//
			for (Object tupla : collection) {
				Object valorOperando1 = getValorOperando(operando1, tupla);
				/*Se eh uma instrucao IS NULL, segue o mesmo caminho das instrucoes IS TRUE e IS FALSE*/
				if (operadorRelacional.getClass() == Nulo.class) {
					return verificaCondicao(valorOperando1 == null, restricao);
				}
				/*Se eh uma instrucao IS TRUE ou IS FALSE, compara logo de cara, uma vez que nao*/
				/*existem outros operandos na restricao*/
				if (operadorRelacional.getClass() == IgualBooleano.class) {
					return verificaCondicao(valorOperando1.equals(operando2.getValor()), restricao);
				}
				//
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

				/*Converte os valores dos operando para Comparable para que seja possivel*/
				/*efetuar a comparacao*/
				Comparable<Object> valor1Comp = (Comparable<Object>) valorOperando1;
				Comparable<Object> valor2Comp = (Comparable<Object>) valorOperando2;
				Comparable<Object> valorAuxComp = (Comparable<Object>) valorOperandoAux;

				return verificaCondicao(operadorRelacional.compara(valor1Comp, valor2Comp, valorAuxComp), restricao);
			}
		} else{
			Collection relacao1 = relacoes.get(operando1.getRelacao());
			Collection relacao2 = relacoes.get(operando2.getRelacao());
			Map<Object,Object> hashTable = new HashMap<Object, Object>();
			//
			for(Object tupla : relacao1){
				Object valorOperando1 = getValorOperando(operando1, tupla);
				//
				hashTable.put(valorOperando1, tupla);
			}
			//
			for(Object tupla : relacao2){
				Object valorOperando2 = getValorOperando(operando1, tupla);
				//
				if(hashTable.get(valorOperando2) != null){
					
				}
			}
		}
		//
		return false;
	}

	private Object getValorOperando(Projecao<?> operando, Object objeto) throws Exception {
		Object valor = operando.getValor();
		//
		if (operando.getClass() == ProjecaoCampo.class) {
			valor = QueryUtils.getValorDoCampo(objeto, valor.toString());
		}
		//
		return valor;
	}

	private boolean verificaCondicao(boolean comparacao, RestricaoSimples restricao) {
		return (comparacao && !restricao.isNegacao()) || (!comparacao && restricao.isNegacao());
	}
}