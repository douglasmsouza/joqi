package br.com.joqi.semantico.consulta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoAnd;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Entre;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.MaiorIgual;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.MenorIgual;
import br.com.joqi.semantico.exception.CampoInexistenteException;
import br.com.joqi.semantico.exception.RelacaoInexistenteException;
import br.com.joqi.semantico.exception.TipoGenericoException;

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
	 * @throws TipoGenericoException
	 * @throws
	 * @throws
	 * @throws RelacaoInexistenteException
	 * @throws Exception
	 */
	public static Collection<Object> getColecao(Object objeto, String nome) throws TipoGenericoException, RelacaoInexistenteException {
		Class<?> clazz = objeto.getClass();
		try {
			Field atributo = clazz.getDeclaredField(nome);
			boolean ehPrivado = !atributo.isAccessible();

			if (ehPrivado)
				atributo.setAccessible(true);

			Object valor = atributo.get(objeto);

			if (ehPrivado)
				atributo.setAccessible(false);

			/*if (valor instanceof Collection) {
				Type tipo = atributo.getGenericType();
				if (tipo instanceof ParameterizedType) {
					ParameterizedType tipoGenerico = (ParameterizedType) tipo;
					Class<?> clazzGenerica = (Class<?>) tipoGenerico.getActualTypeArguments()[0];
					if (clazzGenerica != Object.class) {
						return (Collection<Object>) valor;
					}
					//
					throw new TipoGenericoException("Tipo gen�rico n�o pode ser Object na cole��o \"" + nome + "\"");
				}
				//
				throw new TipoGenericoException("Tipo gen�rico n�o declarado na cole��o \"" + nome + "\"");
			}*/
			if (valor instanceof Collection) {
				return (Collection<Object>) valor;
			}
			//
			throw new RelacaoInexistenteException("O objeto \"" + nome + "\" deve implementar a interface Collection");
		} catch (NoSuchFieldException e) {
			throw new RelacaoInexistenteException("A cole��o \"" + nome + "\" n�o existe na classe \"" + clazz.getName() + "\"");
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
	public static Object getValorDoCampo(Object objeto, String nomeCampo) throws CampoInexistenteException {
		Class<?> classe = objeto.getClass();

		try {
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
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		}

		/*Caso nao exista nenhum metodo, lanca excecao*/
		throw new CampoInexistenteException("O campo/m�todo \"" + nomeCampo + "\" n�o existe na classe \"" + objeto.getClass().getName() + "\"");
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

	public static RestricaoSimples[] divideRestricaoBetween(RestricaoSimples restricao) {
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
