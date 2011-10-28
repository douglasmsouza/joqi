package br.com.joqi.semantico.consulta.ordenacao;

import java.util.Comparator;

import br.com.joqi.semantico.consulta.QueryUtils;
import br.com.joqi.semantico.consulta.ordenacao.ItemOrdenacao.TipoOrdenacao;
import br.com.joqi.semantico.consulta.resultado.ResultObject;

public class ResultSetComparator implements Comparator<ResultObject> {

	private Ordenacao ordenacao;

	public ResultSetComparator(Ordenacao ordenacao) {
		this.ordenacao = ordenacao;
	}

	public int compare(ResultObject o1, ResultObject o2) {
		return efetuaComparacao(o1, o2, 0);
	}

	private int efetuaComparacao(ResultObject o1, ResultObject o2, int indiceItemOrdenacao) {
		try {
			int retornoComparacao = 0;
			ItemOrdenacao item = ordenacao.getItem(indiceItemOrdenacao);
			if (item != null) {
				Comparable<Object> valor1;
				Comparable<Object> valor2;
				if (item.getTipoOrdenacao() == TipoOrdenacao.ASC) {
					valor1 = (Comparable<Object>) QueryUtils.getValorDoCampo(o1, item.getCampo());
					valor2 = (Comparable<Object>) QueryUtils.getValorDoCampo(o2, item.getCampo());
				} else {
					valor1 = (Comparable<Object>) QueryUtils.getValorDoCampo(o2, item.getCampo());
					valor2 = (Comparable<Object>) QueryUtils.getValorDoCampo(o1, item.getCampo());
				}
				retornoComparacao = valor1.compareTo(valor2);
				if (retornoComparacao == 0) {
					return efetuaComparacao(o1, o2, indiceItemOrdenacao + 1);
				}
			}
			return retornoComparacao;
		} catch (Exception e) {
		}
		return 0;
	}
}
