package br.com.joqi.semantico.consulta.plano;

public class ArvoreConsulta {

	private NoArvore raiz;
	private NoArvore ultimoInserido;
	private NoArvore raizRestricoes;

	public NoArvore getRaizRestricoes() {
		return raizRestricoes;
	}

	public void setRaizRestricoes(NoArvore raizRestricoes) {
		this.raizRestricoes = raizRestricoes;
	}

	public NoArvore insere(NoArvore pai, Object valor) {
		if (pai == null) {
			raiz = new NoArvore(valor);
			return raiz;
		} else {
			NoArvore novo = new NoArvore(valor);
			novo.setIrmao(pai.getFilho());
			pai.setFilho(novo);
			return novo;
		}
	}

	public NoArvore insere(Object valor) {
		ultimoInserido = insere(ultimoInserido, valor);
		return ultimoInserido;
	}

	public NoArvore getUltimoInserido() {
		return ultimoInserido;
	}

	public NoArvore getRaiz() {
		return raiz;
	}

	public void imprime() {
		imprime(raiz, 0);
	}

	private void imprime(NoArvore raiz, int profundidade) {
		if (raiz != null) {
			for (int i = 0; i < profundidade - 1; i++) {
				System.out.print("\t");
			}
			if (profundidade > 0)
				System.out.print("|-----> ");
			System.out.println(raiz);
			imprime(raiz.getFilho(), profundidade + 1);
			imprime(raiz.getIrmao(), profundidade);
		}
	}

}
