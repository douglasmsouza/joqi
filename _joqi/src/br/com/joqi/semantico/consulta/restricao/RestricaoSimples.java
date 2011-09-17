package br.com.joqi.semantico.consulta.restricao;

import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogico;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Entre;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;

/**
 * Restricao para a clausula WHERE
 * Formada por dois operando e um operador
 * 
 * @author Douglas Matheus de Souza em 20/07/2011
 */
public class RestricaoSimples extends Restricao {

	private Projecao<?> operando1;
	private Projecao<?> operando2;
	private OperadorRelacional operadorRelacional;

	public RestricaoSimples(boolean negacao, Projecao<?> operando1, Projecao<?> operando2, OperadorRelacional operadorRelacional,
			OperadorLogico operadorLogico) {
		super(negacao, operadorLogico);
		this.operando1 = operando1;
		this.operando2 = operando2;
		this.operadorRelacional = operadorRelacional;
	}

	public Projecao<?> getOperando1() {
		return operando1;
	}

	public void setOperando1(Projecao<?> operando1) {
		this.operando1 = operando1;
	}

	public Projecao<?> getOperando2() {
		return operando2;
	}

	public void setOperando2(Projecao<?> operando2) {
		this.operando2 = operando2;
	}

	public OperadorRelacional getOperadorRelacional() {
		return operadorRelacional;
	}

	public void setOperadorRelacional(OperadorRelacional operadorRelacional) {
		this.operadorRelacional = operadorRelacional;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		//
		if (getOperadorLogico() != null)
			s.append(getOperadorLogico()).append(" ");
		if (isNegacao())
			s.append("not ");
		//
		if (operadorRelacional instanceof Entre) {
			Entre entre = (Entre) operadorRelacional;
			s.append(entre.getOperandoEntre()).append(" between ").append(operando1).append(" and ").append(operando2).append(" ");
		} else {
			s.append(operando1).append(" ").append(operadorRelacional).append(" ").append(operando2).append(" ");;
		}
		//
		return s.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass() == RestricaoSimples.class) {
			RestricaoSimples outra = ((RestricaoSimples) obj);
			//
			boolean operandosIguais = outra.operando1.equals(this.operando1) && outra.operando2.equals(this.operando2);
			boolean operadorRelacionalIgual = outra.operadorRelacional.getClass() == this.operadorRelacional.getClass();
			//
			return operandosIguais && operadorRelacionalIgual;
		}
		return false;
	}

	public boolean efetuaJuncao() {
		return operando1.getClass() == ProjecaoCampo.class && operando2.getClass() == ProjecaoCampo.class;
	}

	@Override
	public int compareTo(Restricao o) {
		if (efetuaJuncao()) {
			return 1;
		}
		/*Restricao que nao eh uma juncao deve ser executada primeiro*/
		return -1;
	}
}
