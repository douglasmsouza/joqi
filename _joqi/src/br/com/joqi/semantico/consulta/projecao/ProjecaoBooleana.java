package br.com.joqi.semantico.consulta.projecao;

public class ProjecaoBooleana extends Projecao<Boolean> {

	public ProjecaoBooleana() {
		super();
	}

	public ProjecaoBooleana(Boolean valor) {
		super(valor);
	}

	public ProjecaoBooleana(String apelido, Boolean valor) {
		super(apelido, valor);
	}

}
