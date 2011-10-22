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
		if (this.filho != null)
			this.filho.pai = this;
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

	public NoArvore addFilho(Object operacao) {
		NoArvore novoFilho;
		if (operacao.getClass() == NoArvore.class) {
			NoArvore operacaoNo = (NoArvore) operacao;
			novoFilho = new NoArvore(operacaoNo.getOperacao());
			novoFilho.setFilho(operacaoNo.getFilho());
		} else {
			novoFilho = new NoArvore(operacao);
		}
		novoFilho.setIrmao(this.filho);
		this.setFilho(novoFilho);
		return novoFilho;
	}

	public NoArvore addIrmao(Object operacao) {
		return this.pai.addFilho(operacao);
	}

	public void removeFilho(NoArvore noRemover) {
		NoArvore no = this.filho;
		NoArvore anterior = null;
		while (no != noRemover) {
			anterior = no;
			no = no.getIrmao();
		}
		//
		if (anterior != null) {
			anterior.setIrmao(no.getIrmao());
		} else {
			setFilho(no.getIrmao());
		}
	}

	@Override
	public String toString() {
		return operacao.toString();
	}

	public boolean isFolha() {
		return filho == null;
	}
}
