package br.com.joqi.semantico.consulta.restricao.operadorrelacional;

public class Diferente extends OperadorRelacional {

	@Override
	public boolean compara(Comparable<Object> valor1, Comparable<Object> valor2, Comparable<Object> valorAux) {
		return valor1.compareTo(valor2) != 0;
	}

	@Override
	public String toString() {
		return "<>";
	}

}
