package br.com.joqi.semantico.consulta.projecao.aritmetica;

public class Divisao extends ExpressaoAritmetica {

	public Divisao() {
		super();
	}

	public Divisao(Object valor1, Object valor2) {
		super(valor1, valor2);
	}

	public Divisao(Object valor1) {
		super(valor1);
	}

	@Override
	public String toString() {
		return super.toString("/");
	}
}
