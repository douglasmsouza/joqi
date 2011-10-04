package br.com.joqi.semantico.consulta.plano;


public class ArvoreConsulta {

	private NoArvore raiz;
	private NoArvore ultimoInserido;

	public NoArvore insere(NoArvore pai, Object valor) {
		if (pai == null) {
			raiz = new NoArvore(valor);
			return raiz;
		} else {
			NoArvore esquerda = new NoArvore(valor);
			pai.setEsquerda(esquerda);
			return esquerda;
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
			imprime(raiz.getEsquerda(), profundidade + 1);
			imprime(raiz.getDireita(), profundidade + 1);
		}
	}

}
