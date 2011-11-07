package br.com.joqi.semantico.consulta.util;

public class ObjetoHash {

	private Object objeto;
	private ObjetoHash proximo;

	public ObjetoHash(Object objeto) {
		this.objeto = objeto;
	}

	public Object getObjeto() {
		return objeto;
	}

	public void setObjeto(Object objeto) {
		this.objeto = objeto;
	}

	public ObjetoHash getProximo() {
		return proximo;
	}

	public void setProximo(ObjetoHash proximo) {
		this.proximo = proximo;
	}

}
