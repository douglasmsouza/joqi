package br.com.joqi.semantico.consulta.restricao;

import java.util.List;

public interface IPossuiRestricoes {

	public List<Restricao> getRestricoes();

	public void addRestricao(Restricao restricao);

}
