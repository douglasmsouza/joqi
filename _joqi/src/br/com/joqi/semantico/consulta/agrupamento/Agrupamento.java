package br.com.joqi.semantico.consulta.agrupamento;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import br.com.joqi.semantico.consulta.agrupamento.agregacao.FuncaoAgregacao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;

public class Agrupamento {

	private Set<ProjecaoCampo> campos;
	private Set<FuncaoAgregacao> funcoesAgregacao;

	public Agrupamento() {
		this.campos = new LinkedHashSet<ProjecaoCampo>();
		this.funcoesAgregacao = new HashSet<FuncaoAgregacao>();
	}

	public Set<ProjecaoCampo> getCampos() {
		return campos;
	}

	public void addCampo(ProjecaoCampo campo) {
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
		for (ProjecaoCampo campo : campos) {
			sb.append(campo).append(", ");
		}
		return sb.delete(sb.length() - 2, sb.length()).toString();
	}

}
