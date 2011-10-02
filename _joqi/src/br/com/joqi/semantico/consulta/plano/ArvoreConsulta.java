package br.com.joqi.semantico.consulta.plano;


public class ArvoreConsulta {

	private NoArvore raiz;
	private NoArvore ultimoInserido;

	public NoArvore insere(NoArvore pai, Object valor) {
		if (pai == null) {
			raiz = new NoArvore(valor);
			return raiz;
		} else {
			NoArvore filho = new NoArvore(valor);
			filho.setIrmao(pai.getFilho());
			pai.setFilho(filho);
			return filho;
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
	
	public void imprime(){
		imprime(raiz, 0);
	}
	
	private void imprime(NoArvore raiz, int profundidade){
		for(int i = 0; i < profundidade-1; i++){
			System.out.print("\t");
		}
		if(profundidade > 0)
			System.out.print("|-----> ");
		System.out.println(raiz);
		NoArvore no = raiz.getFilho();
		while (no != null) {
			imprime(no, profundidade+1);
			no = no.getIrmao();
		}
	}

}
