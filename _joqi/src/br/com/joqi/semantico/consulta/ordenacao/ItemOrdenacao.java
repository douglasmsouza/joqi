package br.com.joqi.semantico.consulta.ordenacao;

import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;

public class ItemOrdenacao {

	public static enum TipoOrdenacao {
		ASC, DESC
	}

	private ProjecaoCampo campo;
	private TipoOrdenacao tipoOrdenacao;

	public ItemOrdenacao() {
	}

	public ItemOrdenacao(ProjecaoCampo campo, TipoOrdenacao tipoOrdenacao) {
		this.campo = campo;
		this.tipoOrdenacao = tipoOrdenacao;
	}

	public ProjecaoCampo getCampo() {
		return campo;
	}

	public void setCampo(ProjecaoCampo campo) {
		this.campo = campo;
	}

	public TipoOrdenacao getTipoOrdenacao() {
		return tipoOrdenacao;
	}

	public void setTipoOrdenacao(TipoOrdenacao tipoOrdenacao) {
		this.tipoOrdenacao = tipoOrdenacao;
	}

	@Override
	public String toString() {
		return campo + " " + tipoOrdenacao;
	}
}
