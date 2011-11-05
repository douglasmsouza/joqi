package br.com.joqi.testes.modelo;

public class Disco {

	private int cdArtista;
	private int nrDisco;
	private String nmDisco;
	private int anoLancamento;

	public Disco(int cdArtista, int nrDisco, String nmDisco, int anoLancamento) {
		super();
		this.cdArtista = cdArtista;
		this.nrDisco = nrDisco;
		this.nmDisco = nmDisco;
		this.anoLancamento = anoLancamento;
	}

	public int getNrDisco() {
		return nrDisco;
	}

	public void setNrDisco(int nrDisco) {
		this.nrDisco = nrDisco;
	}

	public int getCdArtista() {
		return cdArtista;
	}

	public void setCdArtista(int cdArtista) {
		this.cdArtista = cdArtista;
	}

	public String getNmDisco() {
		return nmDisco;
	}

	public void setNmDisco(String nmDisco) {
		this.nmDisco = nmDisco;
	}

	public int getAnoLancamento() {
		return anoLancamento;
	}

	public void setAnoLancamento(int anoLancamento) {
		this.anoLancamento = anoLancamento;
	}

}
