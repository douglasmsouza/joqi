package br.com.joqi.testes.modelo;

import java.util.Date;

public class Pessoa {

	private int cdPessoa;
	private String nmPessoa;
	private int vlIdade;
	private int cdPai;
	private int cdMae;
	private boolean apareceNaConsulta;
	private Date dtNascimento;
	private Pessoa irmao;
	
	public Pessoa() {
	}
	
	public Pessoa(int cdPessoa, String nmPessoa, int vlIdade, int cdPai, int cdMae, boolean apareceNaConsulta) {
		this(cdPessoa, nmPessoa, vlIdade, cdPai, cdMae, apareceNaConsulta, null);
	}

	public Pessoa(int cdPessoa, String nmPessoa, int vlIdade, int cdPai, int cdMae, boolean apareceNaConsulta, Date dtNascimento) {
		this.cdPessoa = cdPessoa;
		this.nmPessoa = nmPessoa;
		this.vlIdade = vlIdade;
		this.cdPai = cdPai;
		this.cdMae = cdMae;
		this.apareceNaConsulta = apareceNaConsulta;
		this.dtNascimento = dtNascimento; 
	}

	public int getCdPessoa() {
		return cdPessoa;
	}

	public void setCdPessoa(int cdPessoa) {
		this.cdPessoa = cdPessoa;
	}

	public String getNmPessoa() {
		return nmPessoa;
	}

	public void setNmPessoa(String nmPessoa) {
		this.nmPessoa = nmPessoa;
	}

	public int getVlIdade() {
		return vlIdade;
	}

	public void setVlIdade(int vlIdade) {
		this.vlIdade = vlIdade;
	}

	public int getCdPai() {
		return cdPai;
	}

	public void setCdPai(int cdPai) {
		this.cdPai = cdPai;
	}

	public int getCdMae() {
		return cdMae;
	}

	public void setCdMae(int cdMae) {
		this.cdMae = cdMae;
	}

	public boolean isApareceNaConsulta() {
		return apareceNaConsulta;
	}

	public void setApareceNaConsulta(boolean apareceNaConsulta) {
		this.apareceNaConsulta = apareceNaConsulta;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Pessoa getIrmao() {
		return irmao;
	}

	public void setIrmao(Pessoa irmao) {
		this.irmao = irmao;
	}

	@Override
	public String toString() {
		return nmPessoa;
	}

}
