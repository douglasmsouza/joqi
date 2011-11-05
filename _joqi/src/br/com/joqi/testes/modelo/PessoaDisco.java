package br.com.joqi.testes.modelo;

public class PessoaDisco {

	private int cdPessoa;
	private String cdArtista;
	private int nrDisco;

	public PessoaDisco(int cdPessoa, String cdArtista, int nrDisco) {
		super();
		this.cdPessoa = cdPessoa;
		this.cdArtista = cdArtista;
		this.nrDisco = nrDisco;
	}

	public String getCdArtista() {
		return cdArtista;
	}

	public void setCdArtista(String cdArtista) {
		this.cdArtista = cdArtista;
	}

	public int getCdPessoa() {
		return cdPessoa;
	}

	public void setCdPessoa(int cdPessoa) {
		this.cdPessoa = cdPessoa;
	}

	public int getNrDisco() {
		return nrDisco;
	}

	public void setNrDisco(int nrDisco) {
		this.nrDisco = nrDisco;
	}

}
