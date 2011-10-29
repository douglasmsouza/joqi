package br.com.joqi.semantico.consulta.agrupamento;

import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;

public class Agrupamento {

	private ProjecaoCampo campo;

	public Agrupamento(ProjecaoCampo campo) {
		this.campo = campo;
	}

	public ProjecaoCampo getCampo() {
		return campo;
	}

	@Override
	public String toString() {
		return "GROUP BY " + campo.toString();
	}

}
