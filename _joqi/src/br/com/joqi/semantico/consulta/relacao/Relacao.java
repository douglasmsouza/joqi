package br.com.joqi.semantico.consulta.relacao;

/**
 * Guarda as informacoes de um objeto que sera consultado
 * 
 * @author Douglas Matheus de Souza em 20/07/2011
 */
public class Relacao {

	private String apelido;
	private String nome;

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
		StringBuilder s = new StringBuilder();
		s.append(nome);
		if (apelido != null)
			s.append(" as ").append(apelido);
		return s.toString();
	}

}
