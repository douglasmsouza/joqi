package br.com.joqi.semantico.consulta.projecao;

import br.com.joqi.semantico.consulta.projecao.aritmetica.ExpressaoAritmetica;

public class ProjecaoAritmetica extends Projecao<ExpressaoAritmetica> {

	public ProjecaoAritmetica() {
		super();
	}

	public ProjecaoAritmetica(ExpressaoAritmetica valor) {
		super(valor);
	}

	public ProjecaoAritmetica(String apelido, ExpressaoAritmetica valor) {
		super(apelido, valor);
	}

}
