package br.com.joqi.semantico.consulta.agrupamento.agregacao;

import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;

public class FuncaoAgregacao {

	private ProjecaoCampo campo;

	public FuncaoAgregacao() {
	}
	
	public FuncaoAgregacao(ProjecaoCampo campo) {
		this.campo = campo;
	}

	public ProjecaoCampo getCampo() {
		return campo;
	}

	public void setCampo(ProjecaoCampo campo) {
		this.campo = campo;
	}

}
