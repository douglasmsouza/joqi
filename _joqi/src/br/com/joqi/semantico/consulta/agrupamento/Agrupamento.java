package br.com.joqi.semantico.consulta.agrupamento;

import java.util.LinkedHashSet;
import java.util.Set;

import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;

public class Agrupamento {

	private Set<ProjecaoCampo> campos;

	public Agrupamento() {
		this.campos = new LinkedHashSet<ProjecaoCampo>();
	}

	public Set<ProjecaoCampo> getCampos() {
		return campos;
	}

	public void addCampo(ProjecaoCampo campo) {
		campos.add(campo);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GROUP BY ");
		for (ProjecaoCampo campo : campos) {
			sb.append(campo.getProjecaoStr()).append(", ");
		}
		return sb.delete(sb.length() - 2, sb.length()).toString();
	}

}
