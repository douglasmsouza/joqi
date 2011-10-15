package br.com.joqi.semantico.consulta.projecao.aritmetica;

public class Soma extends ExpressaoAritmetica {

	public Soma() {
		super();
	}

	public Soma(Object valor1, Object valor2) {
		super(valor1, valor2);
	}

	public Soma(Object valor1) {
		super(valor1);
	}

	@Override
	public String toString() {
		return super.toString("+");
	}

}
