package br.com.joqi.semantico.consulta.agrupamento;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import br.com.joqi.semantico.consulta.agrupamento.agregacao.FuncaoAgregacao;
import br.com.joqi.semantico.consulta.projecao.Projecao;

public class Agrupamento {

	private Set<Projecao<?>> campos;
	private Set<FuncaoAgregacao> funcoesAgregacao;

	public Agrupamento() {
		this.campos = new LinkedHashSet<Projecao<?>>();
		this.funcoesAgregacao = new HashSet<FuncaoAgregacao>();
	}

	public Set<Projecao<?>> getCampos() {
		return campos;
	}

	public void addCampo(Projecao<?> campo) {
		campos.add(campo);
	}

	public void addFuncaoAgregacao(FuncaoAgregacao funcao) {
		funcoesAgregacao.add(funcao);
	}

	public Set<FuncaoAgregacao> getFuncoesAgregacao() {
		return funcoesAgregacao;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GROUP BY ");
		for (Projecao<?> campo : campos) {
			sb.append(campo).append(", ");
		}
		return sb.delete(sb.length() - 2, sb.length()).toString();
	}

}
