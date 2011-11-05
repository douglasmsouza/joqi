package br.com.joqi.testes.modelo;

public class Artista {

	private int cdArtista;
	private String dsArtista;
	private int cdGenero;

	public Artista(int cdArtista, String dsArtista, int cdGenero) {
		super();
		this.cdArtista = cdArtista;
		this.dsArtista = dsArtista;
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

	public String getDsArtista() {
		return dsArtista;
	}

	public void setDsArtista(String dsArtista) {
		this.dsArtista = dsArtista;
	}

}
