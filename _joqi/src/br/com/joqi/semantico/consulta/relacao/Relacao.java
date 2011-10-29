package br.com.joqi.semantico.consulta.relacao;

import java.util.Collection;

import br.com.joqi.semantico.consulta.resultado.ResultList;
import br.com.joqi.semantico.consulta.resultado.ResultObject;

/**
 * Guarda as informacoes de um objeto que sera consultado
 * 
 * @author Douglas Matheus de Souza em 20/07/2011
 */
public class Relacao {

	private String apelido;
	private String nome;
	//
	private Collection<Object> colecao;

	public Relacao(String nome) {
		super();
		this.nome = nome;
	}

	public Relacao(String apelido, String nome) {
		super();
		this.apelido = apelido;
		this.nome = nome;
	}

	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		String toString = nome;
		if (apelido != null)
			toString = apelido;
		if (colecao != null)
			toString += " [" + colecao.size() + "]";
		return toString;
	}

	public String getNomeNaConsulta() {
		if (apelido != null)
			return apelido;
		return nome;
	}

	public void setColecao(Collection<Object> colecao) {
		this.colecao = colecao;
	}

	public ResultList getResultList() {
		ResultList resultList = new ResultList();
		for (Object o : colecao) {
			ResultObject tupla = new ResultObject();
			tupla.put(getNomeNaConsulta(), o);
			resultList.add(tupla);
		}
		return resultList;
	}

	@Override
	public boolean equals(Object obj) {
		return obj.hashCode() == this.hashCode();
	}

	@Override
	public int hashCode() {
		return this.getNomeNaConsulta().hashCode();
	}
}
