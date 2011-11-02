package br.com.joqi.semantico.consulta.projecao;

/**
 * Classe que representa uma projecao <br>
 * Uma projecao pode ser um campo de uma relacao, uma expressao aritmetica, uma
 * expressao booleana ou uma string qualquer
 * 
 * @author Douglas Matheus de Souza em 13/07/2011
 * 
 * @param <T>
 */
public abstract class Projecao<T> {

	private String apelido;
	private T valor;
	private String relacao;

	public Projecao() {
	}

	public Projecao(T valor) {
		setValor(valor);
	}

	public Projecao(String apelido, T valor) {
		setApelido(apelido);
		setValor(valor);
	}

	public String getApelido() {
		return apelido;
	}

	public String getNomeNaConsulta() {
		if (apelido != null)
			return apelido;
		return valor.toString();
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public T getValor() {
		return valor;
	}

	public void setValor(T valor) {
		this.valor = valor;
	}

	public String getRelacao() {
		return relacao;
	}

	public void setRelacao(String relacao) {
		this.relacao = relacao;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		if (relacao != null)
			s.append(relacao).append(".");
		s.append(valor);
		if (apelido != null)
			s.append(" as ").append(apelido);
		return s.toString();
	}

	@Override
	public boolean equals(Object obj) {
		Projecao<?> outra = (Projecao<?>) obj;
		//
		return outra.relacao.equals(this.relacao) && outra.valor.equals(this.valor);
	}

	@Override
	public int hashCode() {
		int hashCode = super.hashCode();
		if (valor != null)
			hashCode += valor.hashCode();
		if (relacao != null)
			hashCode += relacao.hashCode();
		return hashCode;
	}
}
