package br.com.joqi.semantico.consulta.plano;

public class NoArvore {

	private Object operacao;
	private NoArvore filho;
	private NoArvore irmao;

	public NoArvore(Object operacao, NoArvore irmao) {
		this.operacao = operacao;
		this.irmao = irmao;
	}

	public NoArvore(Object operacao) {
		this.operacao = operacao;
	}

	public Object getOperacao() {
		return operacao;
	}

	public void setOperacao(Object operacao) {
		this.operacao = operacao;
	}

	public NoArvore getIrmao() {
		return irmao;
	}

	public void setIrmao(NoArvore irmao) {
		this.irmao = irmao;
	}

	public NoArvore getFilho() {
		return filho;
	}

	public void setFilho(NoArvore filho) {
		this.filho = filho;
	}

	@Override
	public String toString() {
		return operacao.toString();
	}

}
