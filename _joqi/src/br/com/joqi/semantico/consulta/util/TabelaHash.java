package br.com.joqi.semantico.consulta.util;

public class TabelaHash {

	private ObjetoHash[] objetosHash;
	private int tamanho;

	public TabelaHash(int tamanho) {
		this.objetosHash = new ObjetoHash[tamanho];
		this.tamanho = tamanho;
	}

	private int hash(Object chave) {
		return (chave.hashCode() % tamanho);
	}

	public ObjetoHash get(Object chave) {
		int indice = hash(chave);
		if (indice < objetosHash.length)
			return objetosHash[hash(chave)];
		return null;
	}

	public void put(Object chave, Object objeto) {
		int indice = hash(chave);
		//
		ObjetoHash objetoHash = objetosHash[indice];
		if (objetoHash != null) {
			ObjetoHash objetoHashNovo = new ObjetoHash(objeto);
			objetoHashNovo.setProximo(objetoHash);
			objetosHash[indice] = objetoHashNovo;
		} else {
			objetoHash = new ObjetoHash(objeto);
			objetosHash[indice] = objetoHash;
		}
	}
}