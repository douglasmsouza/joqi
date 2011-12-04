package br.com.joqi.semantico.consulta.restricao;

import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogico;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;

/**
 * Restricao para a clausula WHERE
 * Formada por dois operando e um operador
 * 
 * @author Douglas Matheus de Souza em 20/07/2011
 */
public class RestricaoSimples extends Restricao {

	public static enum TipoBusca {
		LINEAR, JUNCAO_LOOP_ANINHADO, JUNCAO_HASH
	}

	private TipoBusca tipoBusca;
	//
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
		if (isNegacao())
			s.append("not ");
		s.append(operando1).append(" ").append(operadorRelacional).append(" ").append(operando2);
		/*s.append("[").append(tipoBusca).append("]");*/
		return s.toString();
	}

	public TipoBusca getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(TipoBusca tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	@Deprecated
	public boolean isJuncao() {
		return false;
	}

	public boolean isConstante() {
		return operando1.getClass() != ProjecaoCampo.class && operando2 != null && operando2.getClass() != ProjecaoCampo.class;
	}

	@Deprecated
	public boolean isProdutoCartesiano() {
		return false;
	}
}
