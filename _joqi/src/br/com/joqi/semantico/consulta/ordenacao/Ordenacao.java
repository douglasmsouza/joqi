package br.com.joqi.semantico.consulta.ordenacao;

import java.util.ArrayList;
import java.util.List;

import br.com.joqi.semantico.consulta.ordenacao.ItemOrdenacao.TipoOrdenacao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;

public class Ordenacao {

	private List<ItemOrdenacao> itens;

	public Ordenacao() {
		this.itens = new ArrayList<ItemOrdenacao>();
	}

	public List<ItemOrdenacao> getItens() {
		return itens;
	}

	public void addItem(ProjecaoCampo campo, TipoOrdenacao tipoOrdenacao) {
		itens.add(new ItemOrdenacao(campo, tipoOrdenacao));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ORDER BY ");
		for (ItemOrdenacao item : itens) {
			sb.append(item).append(", ");
		}
		return sb.delete(sb.length() - 2, sb.length()).toString();
	}

}
