package br.com.joqi.testes.modelo;

public class Artista {

	private int cdArtista;
	private String nmArtista;
	private int cdGenero;

	public Artista(int cdArtista, String nmArtista, int cdGenero) {
		super();
		this.cdArtista = cdArtista;
		this.nmArtista = nmArtista;
		this.cdGenero = cdGenero;
	}

	public int getCdArtista() {
		return cdArtista;
	}

	public void setCdArtista(int cdArtista) {
		this.cdArtista = cdArtista;
	}

	public int getCdGenero() {
		return cdGenero;
	}

	public void setCdGenero(int cdGenero) {
		this.cdGenero = cdGenero;
	}

	public String getNmArtista() {
		return nmArtista;
	}

	public void setNmArtista(String nmArtista) {
		this.nmArtista = nmArtista;
	}

}
