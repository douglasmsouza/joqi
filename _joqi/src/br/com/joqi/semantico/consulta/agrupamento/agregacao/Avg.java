package br.com.joqi.semantico.consulta.agrupamento.agregacao;

public class Avg extends Sum {

	private int qtRegistros;

	public Avg() {
		super();
		this.qtRegistros = 0;
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
		super.atualizaResultado(valor);
		this.qtRegistros++;
	}

	@Override
	public Number getResultado() {
		return super.getResultado().doubleValue() / qtRegistros;
	}
}
