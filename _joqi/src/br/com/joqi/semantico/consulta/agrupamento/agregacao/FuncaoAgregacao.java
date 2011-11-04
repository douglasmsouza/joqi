package br.com.joqi.semantico.consulta.agrupamento.agregacao;

import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;

public abstract class FuncaoAgregacao {

	private ProjecaoCampo campo;
	private Number resultado;

	public FuncaoAgregacao(Number resultado) {
		this.resultado = resultado;
	}

	public ProjecaoCampo getCampo() {
		return campo;
	}

	public void setCampo(ProjecaoCampo campo) {
		this.campo = campo;
	}

	public Number getResultado() {
		return resultado;
	}

	public void setResultado(Number resultado) {
		this.resultado = resultado;
	}

	public abstract void atualizaResultado(Object valor);

	public abstract FuncaoAgregacao copia();

}
