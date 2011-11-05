package br.com.joqi.testes.modelo;

public class Album {

	private int cdArtista;
	private int nrAlbum;
	private String nmDisco;

	public Album(int cdArtista, int nrDisco, String nrAlbum) {
		super();
		this.cdArtista = cdArtista;
		this.nrAlbum = nrDisco;
		this.nmDisco = nrAlbum;
	}

	public int getNrAlbum() {
		return nrAlbum;
	}

	public void setNrAlbum(int nrAlbum) {
		this.nrAlbum = nrAlbum;
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

}
