package br.com.joqi.semantico.consulta.projecao.aritmetica;

public class Multiplicacao extends ExpressaoAritmetica {

	public Multiplicacao() {
		super();
	}

	public Multiplicacao(Object valor1, Object valor2) {
		super(valor1, valor2);
	}

	public Multiplicacao(Object valor1) {
		super(valor1);
	}

	@Override
	public String toString() {
		return super.toString("*");
	}
	
}
