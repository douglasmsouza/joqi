package br.com.joqi.semantico.consulta.agrupamento.agregacao;

public class Min extends FuncaoAgregacao {

	public Min() {
		super(Double.MAX_VALUE);
	}

	@Override
	public void atualizaResultado(Object valor) {
		if (valor instanceof Number) {
			Number valorNumber = (Number) valor;
			if (valorNumber.doubleValue() < getResultado().doubleValue()) {
				super.setResultado(valorNumber);
			}
		}
	}

	@Override
	public FuncaoAgregacao copia() {
		Min funcao = new Min();
		funcao.setCampo(getCampo());
		return funcao;
	}

	@Override
	public String toString() {
		return "min(" + getCampo() + ")";
	}

}
