package br.com.joqi.semantico.consulta.restricao.operadorrelacional;

public abstract class OperadorRelacional {

	public abstract boolean compara(Comparable<Object> valor1, Comparable<Object> valor2, Comparable<Object> valorAux);
}
