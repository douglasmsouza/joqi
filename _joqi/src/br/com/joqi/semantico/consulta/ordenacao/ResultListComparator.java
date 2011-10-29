package br.com.joqi.semantico.consulta.ordenacao;

import java.util.Comparator;
import java.util.Iterator;

import br.com.joqi.semantico.consulta.QueryUtils;
import br.com.joqi.semantico.consulta.ordenacao.ItemOrdenacao.TipoOrdenacao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.exception.CampoInexistenteException;

public class ResultListComparator implements Comparator<ResultObject> {

	private Ordenacao ordenacao;

	public ResultListComparator(Ordenacao ordenacao) {
		this.ordenacao = ordenacao;
	}

	public int compare(ResultObject o1, ResultObject o2) {
		try {
			Iterator<ItemOrdenacao> iterator = ordenacao.getItens().iterator();
			return efetuaComparacao(o1, o2, iterator);
		} catch (CampoInexistenteException e) {
			return 0;
		}
	}

	private int efetuaComparacao(ResultObject o1, ResultObject o2, Iterator<ItemOrdenacao> iterator) throws CampoInexistenteException {
		int retornoComparacao = 0;
		//
		if (iterator.hasNext()) {
			ItemOrdenacao item = iterator.next();
			ProjecaoCampo campo = item.getCampo();
			//
			Comparable<Object> valor1;
			Comparable<Object> valor2;
			if (item.getTipoOrdenacao() == TipoOrdenacao.ASC) {
				valor1 = (Comparable<Object>) QueryUtils.getValorDoCampo(o1, campo);
				valor2 = (Comparable<Object>) QueryUtils.getValorDoCampo(o2, campo);
			} else {
				valor1 = (Comparable<Object>) QueryUtils.getValorDoCampo(o2, campo);
				valor2 = (Comparable<Object>) QueryUtils.getValorDoCampo(o1, campo);
			}
			//
			if (valor1 == null && valor2 != null) {
				return -1;
			} else if (valor1 != null && valor2 == null) {
				return 1;
			} else if (valor1 == null && valor2 == null) {
				return 0;
			}
			//
			retornoComparacao = valor1.compareTo(valor2);
			if (retornoComparacao == 0) {
				return efetuaComparacao(o1, o2, iterator);
			}
		}
		return retornoComparacao;
	}
}