package br.com.joqi.semantico.consulta.restricao.operadorrelacional;

public class Nulo extends OperadorRelacional {

	@Override
	public boolean compara(Comparable<Object> valor1, Comparable<Object> valor2, Comparable<Object> valorAux) {
		return false;
	}

	@Override
	public String toString() {
		return "is";
	}

}
