package br.com.joqi.semantico.consulta.ordenacao;

import java.util.List;

public class Ordenacao {

	private List<ItemOrdenacao> itens;

	public Ordenacao(List<ItemOrdenacao> itens) {
		this.itens = itens;
	}

	public ItemOrdenacao getItem(int indice) {
		try {
			return itens.get(indice);
		} catch (Exception e) {
			return null;
		}
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
