package br.com.joqi.semantico.consulta.restricao;

import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogico;

/**
 * Restricao para a clausula WHERE
 * 
 * @author Douglas Matheus de Souza em 20/07/2011
 */
public class Restricao {

	/*Flag que informa se tem o operador NOT na frente da restricao*/
	private boolean negacao = false;
	/*Operador logico AND ou OR*/
	private OperadorLogico operadorLogico;

	public Restricao(boolean negacao) {
		this.negacao = negacao;
	}

	public Restricao(OperadorLogico operadorLogico) {
		this.operadorLogico = operadorLogico;
	}

	public Restricao(boolean negacao, OperadorLogico operadorLogico) {
		this.negacao = negacao;
		this.operadorLogico = operadorLogico;
	}

	public Restricao() {
	}

	public OperadorLogico getOperadorLogico() {
		return operadorLogico;
	}

	public void setOperadorLogico(OperadorLogico operadorLogico) {
		this.operadorLogico = operadorLogico;
	}

	public boolean isNegacao() {
		return negacao;
	}

	public void setNegacao(boolean negacao) {
		this.negacao = negacao;
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

}
