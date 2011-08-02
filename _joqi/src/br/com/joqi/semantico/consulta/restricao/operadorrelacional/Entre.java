package br.com.joqi.semantico.consulta.restricao.operadorrelacional;

import br.com.joqi.semantico.consulta.projecao.Projecao;

/**
 * Operador "Between"
 * 
 * @author Douglas Matheus de Souza em 20/07/2011
 */
public class Entre extends OperadorRelacional {

	private Projecao<?> operandoEntre;

	public Entre(Projecao<?> operandoEntre) {
		super();
		this.operandoEntre = operandoEntre;
	}

	@Override
	public boolean compara(Comparable<Object> valor1, Comparable<Object> valor2, Comparable<Object> valorAux) {
		return new MenorIgual().compara(valor1, valorAux, null) &&
				new MaiorIgual().compara(valor2, valorAux, null);
	}

	public Projecao<?> getOperandoEntre() {
		return operandoEntre;
	}

	public void setOperandoEntre(Projecao<?> operandoEntre) {
		this.operandoEntre = operandoEntre;
	}

	@Override
	public String toString() {
		return "";
	}

}
