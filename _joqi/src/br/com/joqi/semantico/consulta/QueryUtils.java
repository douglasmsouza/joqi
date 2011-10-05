package br.com.joqi.semantico.consulta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoAnd;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Entre;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.MaiorIgual;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.MenorIgual;
import br.com.joqi.semantico.exception.CampoInexistenteException;
import br.com.joqi.semantico.exception.RelacaoInexistenteException;

/**
 * Classe responsavel pelas operacoes que utilizam Reflection
 * 
 * @author Douglas Matheus de Souza em 15/07/2011
 */
public class QueryUtils {

	/**
	 * Retorna uma collection
	 * 
	 * @param objeto
	 * @param nome
	 * @return
	 * @throws
	 * @throws
	 * @throws RelacaoInexistenteException
	 * @throws Exception
	 */
	public static Collection<Object> getColecao(Object objeto, String nome) throws RelacaoInexistenteException {
		Class<?> clazz = objeto.getClass();
		try {
			Field atributo = clazz.getDeclaredField(nome);
			boolean ehPrivado = !atributo.isAccessible();

			if (ehPrivado)
				atributo.setAccessible(true);

			Object valor = atributo.get(objeto);

			if (ehPrivado)
				atributo.setAccessible(false);

			if (valor instanceof Collection) {
				return (Collection<Object>) valor;
			}

			throw new RelacaoInexistenteException("O objeto \"" + nome + "\" deve implementar a interface Collection");
		} catch (NoSuchFieldException e) {
			throw new RelacaoInexistenteException("A coleção \"" + nome + "\" não existe na classe \"" + clazz.getName() + "\"");
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {			
		}
		return null;
	}

	/**
	 * Obtem o valor de um atributo de um objeto
	 * 
	 * @param objeto
	 * @param nomeCampo
	 * @return
	 */
	public static Object getValorDoCampo(Object objeto, String nomeCampo) throws Exception {
		Class<?> classe = objeto.getClass();

		/*Se existe o metodo informado, retorna o valor*/
		Method metodo = retornaMetodo(classe, nomeCampo);
		if (metodo != null) {
			return metodo.invoke(objeto);
		}

		String nmCampoMetodoGet = String.valueOf(nomeCampo.toCharArray()[0]).toUpperCase() + nomeCampo.substring(1);

		/*Busca pelo metodo "get" */
		metodo = retornaMetodo(classe, "get" + nmCampoMetodoGet);
		if (metodo != null) {
			return metodo.invoke(objeto);
		}

		/*Se nao existir o metodo "get", busca pelo metodo "is" */
		metodo = retornaMetodo(classe, "is" + nmCampoMetodoGet);
		if (metodo != null) {
			return metodo.invoke(objeto);
		}

		/*Caso nao exista nenhum metodo, lanca excecao*/
		throw new CampoInexistenteException("O campo/método \"" + nomeCampo + "\" não existe na classe \"" + objeto.getClass().getName() + "\"");
	}

	private static Method retornaMetodo(Class<?> classe, String nomeCampo) {
		Method metodo = null;
		try {
			metodo = classe.getMethod(nomeCampo);
		} catch (NoSuchMethodException e) {
			return null;
		}
		return metodo;
	}
	
	public static RestricaoSimples[] divideRestricaoBetween(RestricaoSimples restricao){
		RestricaoSimples[] restricoes = new RestricaoSimples[2];
		//
		Entre operadorBetween = (Entre) restricao.getOperadorRelacional();
		restricoes[0] = new RestricaoSimples(restricao.isNegacao(), operadorBetween.getOperandoEntre(),
				restricao.getOperando1(), new MaiorIgual(), restricao.getOperadorLogico());
		restricoes[1] = new RestricaoSimples(restricao.isNegacao(), operadorBetween.getOperandoEntre(),
				restricao.getOperando2(), new MenorIgual(), new OperadorLogicoAnd());
		//
		return restricoes;
	}

}
