package br.com.joqi.semantico.consulta.restricao;

import br.com.joqi.semantico.consulta.busca.tipo.TipoBusca;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogico;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Diferente;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Entre;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.Igual;
import br.com.joqi.semantico.consulta.restricao.operadorrelacional.OperadorRelacional;

/**
 * Restricao para a clausula WHERE
 * Formada por dois operando e um operador
 * 
 * @author Douglas Matheus de Souza em 20/07/2011
 */
public class RestricaoSimples extends Restricao {

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
		//
		/*if (getOperadorLogico() != null)
			s.append(getOperadorLogico()).append(" ");*/
		if (isNegacao())
			s.append("not ");
		//
		s.append(operando1).append(" ").append(operadorRelacional).append(" ").append(operando2);
		//
		s.append(" [").append(tipoBusca).append("]");
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

	public boolean isJuncao() {
		/*return operando1.getClass() == ProjecaoCampo.class && operando2 != null &&
				operando2.getClass() == ProjecaoCampo.class &&
				!operando1.getRelacao().equals(operando2.getRelacao()) &&
				((operadorRelacional.getClass() == Igual.class && !isNegacao()) ||
						(operadorRelacional.getClass() == Diferente.class && isNegacao()));*/
		return operando1.getClass() == ProjecaoCampo.class && operando2 != null &&
				operando2.getClass() == ProjecaoCampo.class &&
				!operando1.getRelacao().equals(operando2.getRelacao());
	}

	public boolean isProdutoCartesiano() {
		return operando1.getClass() == ProjecaoCampo.class && operando2 != null &&
				operando2.getClass() == ProjecaoCampo.class &&
				((operadorRelacional.getClass() == Igual.class && isNegacao()) ||
						(operadorRelacional.getClass() == Diferente.class && !isNegacao()) ||
				 (operadorRelacional.getClass() != Igual.class && operadorRelacional.getClass() != Diferente.class));
	}

	public boolean isConstante() {
		return (operando1.getClass() != ProjecaoCampo.class || operando2.getClass() != ProjecaoCampo.class) ||
				(operando1.getClass() == ProjecaoCampo.class && operando2 != null &&
						operando2.getClass() == ProjecaoCampo.class &&
				operando1.getRelacao().equals(operando2.getRelacao()));
	}

	public TipoBusca getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(TipoBusca tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	@Override
	public int compareTo(Restricao o) {
		RestricaoSimples outra = (RestricaoSimples) o;
		//
		if (outra.isJuncao()) {
			if (isConstante()) {
				return -1;
			}
			if (isProdutoCartesiano()) {
				return 1;
			}
		} else if (outra.isConstante()) {
			if (!isConstante()) {
				return 1;
			}
		} else if (outra.isProdutoCartesiano()) {
			if (!isProdutoCartesiano()) {
				return -1;
			}
		}
		//
		return 0;
	}
}
