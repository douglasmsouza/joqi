package br.com.joqi.semantico.consulta.projecao.aritmetica;

public class ExpressaoAritmetica {

	private Object valor1;
	private Object valor2;

	public ExpressaoAritmetica() {
	}

	public ExpressaoAritmetica(Object valor1) {
		this.valor1 = valor1;
	}

	public ExpressaoAritmetica(Object valor1, Object valor2) {
		this.valor1 = valor1;
		this.valor2 = valor2;
	}

	public Object getValor1() {
		return valor1;
	}

	public void setValor1(Object valor1) {
		this.valor1 = valor1;
	}

	public Object getValor2() {
		return valor2;
	}

	public void setValor2(Object valor2) {
		this.valor2 = valor2;
	}

	public String toString(String sinal) {
		StringBuilder sb = new StringBuilder();
		//
		if (valor1 instanceof ExpressaoAritmetica) {
			sb.append("(").append(valor1).append(")");
		} else {
			sb.append(valor1);
		}
		sb.append(sinal);
		if (valor2 != null) {
			if (valor2 instanceof ExpressaoAritmetica) {
				sb.append("(").append(valor2).append(")");
			} else {
				sb.append(valor2);
			}
		}
		//
		return sb.toString();
	}

	@Override
	public String toString() {
		return valor1.toString();
	}

}
