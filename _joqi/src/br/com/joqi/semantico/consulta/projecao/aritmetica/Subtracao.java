package br.com.joqi.semantico.consulta.projecao.aritmetica;

public class Subtracao extends ExpressaoAritmetica {

	public Subtracao() {
		super();
	}

	public Subtracao(Object valor1, Object valor2) {
		super(valor1, valor2);
	}

	public Subtracao(Object valor1) {
		super(valor1);
	}
	
	@Override
	public String toString() {
		return super.toString("-");
	}

}
