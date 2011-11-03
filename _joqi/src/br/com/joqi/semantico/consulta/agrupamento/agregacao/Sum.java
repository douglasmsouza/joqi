package br.com.joqi.semantico.consulta.agrupamento.agregacao;

public class Sum extends FuncaoAgregacao {

	public Sum() {
		super(0);
	}

	@Override
	public void atualizaResultado(Object valor) {
		if (valor instanceof Number) {
			super.setResultado(getResultado().intValue() + ((Number) valor).intValue());
		}
	}

	@Override
	public String toString() {
		return "sum(" + getCampo() + ")";
	}

	@Override
	public FuncaoAgregacao copia() {
		Sum funcao = new Sum();
		funcao.setCampo(getCampo());
		return funcao;
	}

}
