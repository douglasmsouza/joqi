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

	/*public NoArvore busca(NoArvore partindoDe, Object operacao) {
		NoArvore no = partindoDe.getFilho();
		while (no != null) {
			NoArvore irmao = no.getIrmao();
			while (irmao != null) {
				if (irmao.getOperacao().equals(operacao)) {
					return irmao;
				}
				NoArvore retorno = busca(irmao, operacao);
				if (retorno != null) {
					return retorno;
				}
				irmao = irmao.getIrmao();
			}
			//
			if (no.getOperacao().equals(operacao)) {
				return no;
			}
			no = no.getFilho();
		}
		//
		return null;
	}*/

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
			//
			if (raiz.getOperacao().getClass() == ArvoreConsulta.class) {
				arvoreInternasAux.add((ArvoreConsulta) raiz.getOperacao());
			}
		}
	}

}
