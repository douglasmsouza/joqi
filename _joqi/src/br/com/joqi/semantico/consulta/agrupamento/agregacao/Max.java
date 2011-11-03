package br.com.joqi.semantico.consulta.agrupamento.agregacao;

public class Max extends FuncaoAgregacao {

	public Max() {
		super(Double.MIN_VALUE);
	}

	@Override
	public void atualizaResultado(Object valor) {
		if (valor instanceof Number) {
			Number valorNumber = (Number) valor;
			if (valorNumber.doubleValue() > getResultado().doubleValue()) {
				super.setResultado(valorNumber);
			}
		}
	}

	@Override
	public String toString() {
		return "max(" + getCampo() + ")";
	}

	@Override
	public FuncaoAgregacao copia() {
		Max funcao = new Max();
		funcao.setCampo(getCampo());
		return funcao;
	}

}
