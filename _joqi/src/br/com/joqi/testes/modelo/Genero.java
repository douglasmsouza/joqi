package br.com.joqi.testes.modelo;

public class Genero {

	private int cdGenero;
	private String dsGenero;

	public Genero(int cdGenero, String dsGenero) {
		super();
		this.cdGenero = cdGenero;
		this.dsGenero = dsGenero;
	}

	public int getCdGenero() {
		return cdGenero;
	}

	public void setCdGenero(int cdGenero) {
		this.cdGenero = cdGenero;
	}

	public String getDsGenero() {
		return dsGenero;
	}

	public void setDsGenero(String dsGenero) {
		this.dsGenero = dsGenero;
	}

}
