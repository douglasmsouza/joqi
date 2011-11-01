package br.com.joqi.semantico.consulta.ordenacao;

import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;

public class ItemOrdenacao {

	public static enum TipoOrdenacao {
		ASC, DESC
	}

	private ProjecaoCampo campo;
	private TipoOrdenacao tipoOrdenacao;

	public ItemOrdenacao(ProjecaoCampo campo, TipoOrdenacao tipoOrdenacao) {
		this.campo = campo;
		this.tipoOrdenacao = tipoOrdenacao;
	}

	public ProjecaoCampo getCampo() {
		return campo;
	}

	public TipoOrdenacao getTipoOrdenacao() {
		return tipoOrdenacao;
	}

	@Override
	public String toString() {
		return campo.getProjecaoStr() + " " + tipoOrdenacao;
	}
}
