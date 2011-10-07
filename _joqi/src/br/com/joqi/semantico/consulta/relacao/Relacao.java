package br.com.joqi.semantico.consulta.relacao;

import java.util.Collection;

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
		/*StringBuilder s = new StringBuilder();
		s.append(nome);
		if (apelido != null)
			s.append(" as ").append(apelido);
		return s.toString();*/
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

	public Collection<Object> getColecao() {
		return colecao;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Relacao))
			return false;
		return ((Relacao) obj).getNomeNaConsulta().equals(this.getNomeNaConsulta());
	}

}
