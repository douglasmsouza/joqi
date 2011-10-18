package br.com.joqi.semantico.consulta.restricao;

import java.util.List;

public interface IPossuiRestricoes {

	public List<Restricao> getRestricoes();

	public void addRestricao(Restricao restricao);

	public Restricao getUltima();

	public void remove(Restricao restricao);

}
