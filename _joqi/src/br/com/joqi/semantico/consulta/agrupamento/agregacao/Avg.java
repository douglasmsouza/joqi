package br.com.joqi.semantico.consulta.agrupamento.agregacao;

public class Avg extends FuncaoAgregacao {

	public Avg() {
		super(0);
	}

	@Override
	public String toString() {
		return "avg(" + getCampo() + ")";
	}

	@Override
	public FuncaoAgregacao copia() {
		Avg funcao = new Avg();
		funcao.setCampo(getCampo());
		return funcao;
	}

	@Override
	public void atualizaResultado(Object valor) {
		
	}

}
