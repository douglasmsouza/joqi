package br.com.joqi.testes;

import java.util.Date;

import br.com.joqi.semantico.consulta.util.JoqiUtil;

public class Pessoa {

	private int cdPessoa;
	private String nmPessoa;
	private int vlIdade;
	private int cdPai;
	private int cdMae;
	private boolean apareceNaConsulta;
	private Date dtNascimento;

	public Pessoa(int cdPessoa, String nmPessoa, int vlIdade, int cdPai, int cdMae, boolean apareceNaConsulta) {
		this.cdPessoa = cdPessoa;
		this.nmPessoa = nmPessoa;
		this.vlIdade = vlIdade;
		this.cdPai = cdPai;
		this.cdMae = cdMae;
		this.apareceNaConsulta = apareceNaConsulta;
		this.dtNascimento = JoqiUtil.converteParaDate("25/02/1989");
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

	@Override
	public String toString() {
		return nmPessoa;
	}

}
