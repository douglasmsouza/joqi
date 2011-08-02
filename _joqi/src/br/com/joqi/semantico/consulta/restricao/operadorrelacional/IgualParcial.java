package br.com.joqi.semantico.consulta.restricao.operadorrelacional;

/**
 * Operador "Like"
 * 
 * @author Douglas Matheus de Souza em 20/07/2011
 */
public class IgualParcial extends OperadorRelacional {

	@Override
	public boolean compara(Comparable<Object> valor1, Comparable<Object> valor2, Comparable<Object> valorAux) {
		String valor1Str = valor1.toString();
		String valor2Str = valor2.toString();
		//
		if (valor2Str.startsWith("%")) {
			if (valor2Str.endsWith("%")) {
				/*Se comeca e termina com "%", verifica se contem o valor*/
				valor2Str = valor2Str.substring(0, valor2Str.length() - 1);
				valor2Str = valor2Str.substring(1, valor2Str.length());
				return valor1Str.contains(valor2Str);
			} else {
				/*Se somente comeca com "%", verifica se o final contem o valor*/
				valor2Str = valor2Str.substring(1, valor2Str.length());
				return valor1Str.endsWith(valor2Str);
			}
		} else if (valor2Str.endsWith("%")) {
			/*Se somente termina com "%", verifica se o comeco contem o valor*/
			valor2Str = valor2Str.substring(0, valor2Str.length() - 1);
			return valor1Str.startsWith(valor2Str);
		}
		//
		return new Igual().compara(valor1, valor2, null);
	}

	@Override
	public String toString() {
		return "like";
	}

}
