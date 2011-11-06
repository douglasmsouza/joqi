package br.com.joqi.testes.modelo;

public class Album {

	private int cdArtista;
	private int nrAlbum;
	private String nmAlbum;

	public Album(int cdArtista, int nrDisco, String nmAlbum) {
		super();
		this.cdArtista = cdArtista;
		this.nrAlbum = nrDisco;
		this.nmAlbum = nmAlbum;
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

	public String getNmAlbum() {
		return nmAlbum;
	}

	public void setNmAlbum(String nmAlbum) {
		this.nmAlbum = nmAlbum;
	}

}
