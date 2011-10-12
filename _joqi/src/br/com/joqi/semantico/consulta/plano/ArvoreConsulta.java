package br.com.joqi.semantico.consulta.plano;

import java.util.ArrayList;
import java.util.List;

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

	public NoArvore insere(NoArvore pai, Object operacao) {
		if (pai == null) {
			raiz = new NoArvore(operacao);
			return raiz;
		} else {
			return pai.addFilho(operacao);
		}
	}

	public NoArvore insere(Object operacao) {
		ultimoInserido = insere(ultimoInserido, operacao);
		return ultimoInserido;
	}

	public NoArvore getUltimoInserido() {
		return ultimoInserido;
	}

	public NoArvore getRaiz() {
		return raiz;
	}

	private List<ArvoreConsulta> arvoreInternasAux = new ArrayList<ArvoreConsulta>();

	public void imprime() {
		imprime(raiz, 0);
		//
		for (ArvoreConsulta arvoreConsulta : arvoreInternasAux) {
			System.out.println();
			System.out.println("-------------------------------------------------------------------------------------------");
			System.out.println(arvoreConsulta);
			System.out.println("-------------------------------------------------------------------------------------------");
			arvoreConsulta.imprime();
		}
	}

	public void imprime(NoArvore raiz, int profundidade) {
		if (raiz != null) {
			for (int i = 0; i < profundidade - 1; i++) {
				System.out.print("\t");
			}
			if (profundidade > 0)
				System.out.print("|-----> ");
			System.out.println(raiz);
			imprime(raiz.getFilho(), profundidade + 1);
			imprime(raiz.getIrmao(), profundidade);
			//
			if (raiz.getOperacao().getClass() == ArvoreConsulta.class) {
				arvoreInternasAux.add((ArvoreConsulta) raiz.getOperacao());
			}
		}
	}

}
