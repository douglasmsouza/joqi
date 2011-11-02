package br.com.joqi.semantico.consulta.projecao;

import java.util.ArrayList;
import java.util.Iterator;

public class ListaProjecoes extends ArrayList<Projecao<?>> {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		for (Iterator<Projecao<?>> iterator = iterator(); iterator.hasNext();) {
			sb.append(iterator.next()).append(", ");
		}
		return sb.delete(sb.length() - 2, sb.length()).toString();
	}

}
