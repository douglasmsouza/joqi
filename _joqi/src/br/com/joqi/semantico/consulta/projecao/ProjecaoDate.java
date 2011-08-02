package br.com.joqi.semantico.consulta.projecao;

import java.util.Date;

public class ProjecaoDate extends Projecao<Date> {

	public ProjecaoDate() {
		super();
	}

	public ProjecaoDate(Date valor) {
		super(valor);
	}

	public ProjecaoDate(String apelido, Date valor) {
		super(apelido, valor);
	}

}
