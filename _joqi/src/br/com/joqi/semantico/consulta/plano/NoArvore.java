package br.com.joqi.semantico.consulta.plano;

public class NoArvore {

	private Object operacao;
	private NoArvore pai;
	private NoArvore filho;
	private NoArvore irmao;

	public NoArvore(Object operacao, NoArvore filho) {
		this.operacao = operacao;
		this.filho = filho;
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

	public NoArvore getFilho() {
		return filho;
	}

	public void setFilho(NoArvore filho) {
		this.filho = filho;
		this.filho.pai = this;
	}
	
	public void setPai(NoArvore pai) {
		this.pai = pai;
		this.pai.filho = this;
	}

	public NoArvore getIrmao() {
		return irmao;
	}

	public void setIrmao(NoArvore irmao) {
		this.irmao = irmao;
	}

	public NoArvore getPai() {
		return pai;
	}

	@Override
	public String toString() {
		return operacao.toString();
	}

	public boolean isFolha() {
		return filho == null;
	}
}
