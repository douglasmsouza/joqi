package br.com.joqi.semantico.consulta.restricao;

import java.util.ArrayList;
import java.util.List;

import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogico;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoAnd;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoOr;

/**
 * Conjunto de restricoes. Esta classe agrupa uma lista de restricoes da
 * clausula WHERE que estao entre parenteses
 * 
 * @author Douglas Matheus de Souza em 20/07/2011
 */
public class RestricaoConjunto extends Restricao implements IPossuiRestricoes {

	private List<Restricao> restricoes;

	public RestricaoConjunto(boolean negacao, OperadorLogico operadorLogico) {
		super(negacao, operadorLogico);
		restricoes = new ArrayList<Restricao>();
	}

	public List<Restricao> getRestricoes() {
		return restricoes;
	}

	public void addRestricao(Restricao r) {
		restricoes.add(r);
	}

	public void negarRestricoes() {
		for (Restricao restricao : restricoes) {
			restricao.setNegacao(!restricao.isNegacao());
			if (restricao.getOperadorLogico() != null) {
				if (restricao.getOperadorLogico().getClass() == OperadorLogicoAnd.class) {
					restricao.setOperadorLogico(new OperadorLogicoOr());
				} else {
					restricao.setOperadorLogico(new OperadorLogicoAnd());
				}
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (getOperadorLogico() != null)
			sb.append(getOperadorLogico()).append(" ");
		if (isNegacao())
			sb.append("not ");
		sb.append("(");
		for (Restricao r : restricoes) {
			sb.append(r);
		}
		sb.append(")");
		return sb.toString();
	}

}
