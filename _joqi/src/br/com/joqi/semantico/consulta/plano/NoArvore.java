package br.com.joqi.semantico.consulta.plano;

import br.com.joqi.semantico.consulta.produtocartesiano.ProdutoCartesiano;

public class NoArvore {

	private Object operacao;
	private NoArvore esquerda;
	private NoArvore direita;

	public NoArvore(Object operacao, NoArvore direita) {
		this.operacao = operacao;
		this.direita = direita;
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

	public NoArvore getEsquerda() {
		return esquerda;
	}

	public void setEsquerda(NoArvore esquerda) {
		this.esquerda = esquerda;
	}

	public NoArvore getDireita() {
		return direita;
	}

	public void setDireita(NoArvore direita) {
		this.direita = direita;
	}

	@Override
	public String toString() {
		if (operacao.getClass() == ProdutoCartesiano.class) {
			return esquerda + " X " + direita;
		}
		return operacao.toString();
	}

	public boolean isFolha() {
		return esquerda == null && direita == null;
	}

}
