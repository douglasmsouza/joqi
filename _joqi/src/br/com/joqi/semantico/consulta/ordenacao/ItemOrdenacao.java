package br.com.joqi.semantico.consulta.ordenacao;

import br.com.joqi.semantico.consulta.projecao.Projecao;

public class ItemOrdenacao {

	public static enum TipoOrdenacao {
		ASC, DESC
	}

	private Projecao<?> campo;
	private TipoOrdenacao tipoOrdenacao;

	public ItemOrdenacao(Projecao<?> campo, TipoOrdenacao tipoOrdenacao) {
		this.campo = campo;
		this.tipoOrdenacao = tipoOrdenacao;
	}

	public Projecao<?> getCampo() {
		return campo;
	}

	public TipoOrdenacao getTipoOrdenacao() {
		return tipoOrdenacao;
	}

	@Override
	public String toString() {
		return campo + " " + tipoOrdenacao;
	}
}
