package br.com.joqi.semantico.consulta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import br.com.joqi.semantico.consulta.agrupamento.agregacao.FuncaoAgregacao;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.projecao.ProjecaoFuncaoAgregacao;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoAnd;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Entre;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.MaiorIgual;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.MenorIgual;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.consulta.util.ValorNulo;
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
	 * @throws TipoGenericoException
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

			/*if (valor instanceof Collection) {
				Type tipo = atributo.getGenericType();
				if (tipo instanceof ParameterizedType) {
					ParameterizedType tipoGenerico = (ParameterizedType) tipo;
					Class<?> clazzGenerica = (Class<?>) tipoGenerico.getActualTypeArguments()[0];
					if (clazzGenerica != Object.class) {
						return (Collection<Object>) valor;
					}
					//
					throw new TipoGenericoException("Tipo genérico não pode ser Object na coleção \"" + nome + "\"");
				}
				//
				throw new TipoGenericoException("Tipo genérico não declarado na coleção \"" + nome + "\"");
			}*/

			if (valor instanceof Collection) {
				return (Collection<Object>) valor;
			}

			if (valor instanceof Object[]) {
				return Arrays.asList((Object[]) valor);
			}
			//
			throw new RelacaoInexistenteException("O objeto \"" + nome + "\" deve implementar a interface Collection");
		} catch (NoSuchFieldException e) {
			throw new RelacaoInexistenteException("A coleção \"" + nome + "\" não existe na classe \"" + clazz.getName() + "\"");
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		}
		return null;
	}

	public static Object getValorDoCampo(Object objeto, ProjecaoCampo campo) throws CampoInexistenteException {
		return getValorDoCampo(objeto, (String) campo.getValor());
	}

	public static Object getValorDoCampo(ResultObject objeto, ProjecaoCampo campo) throws CampoInexistenteException {
		return getValorDoCampo(objeto.get(campo.getRelacao()), (String) campo.getValor());
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
		throw new CampoInexistenteException("O campo/método \"" + nomeCampo + "\" não existe na classe \"" + objeto.getClass().getName() + "\"");
	}

	/**
	 * Retorna o valor de um operando
	 * 
	 * @param resultObject
	 * @param operando
	 * 
	 * @throws CampoInexistenteException
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
	public static Object getValorOperando(ResultObject resultObject, Projecao<?> operando) throws CampoInexistenteException {
		if (operando.getClass() == ProjecaoCampo.class) {
			return getValorDoCampoNaoNulo(resultObject, (ProjecaoCampo) operando);
		}
		if (operando.getClass() == ProjecaoFuncaoAgregacao.class) {
			FuncaoAgregacao funcao = ((ProjecaoFuncaoAgregacao) operando).getValor();
			FuncaoAgregacao funcaoObjeto = (FuncaoAgregacao) resultObject.get(funcao.toString()); 
			return funcaoObjeto.getResultado();
		}
		return operando.getValor();
	}

	/**
	 * Retorna o valor de um atributo de um objeto
	 * 
	 * @param resultObject
	 * @param operando
	 * 
	 * @throws CampoInexistenteException
	 * @author Douglas Matheus de Souza em 02/11/2011
	 */
	public static Object getValorDoCampoNaoNulo(ResultObject resultObject, ProjecaoCampo operando) throws CampoInexistenteException {
		Object valor = getValorDoCampo(resultObject, operando);
		if (valor == null) {
			return new ValorNulo();
		}
		return valor;
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
