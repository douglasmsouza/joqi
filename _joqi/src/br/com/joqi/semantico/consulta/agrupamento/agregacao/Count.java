package br.com.joqi.semantico.consulta.agrupamento.agregacao;

public class Count extends FuncaoAgregacao {

	public Count() {
		super(0);
	}

	@Override
	public void atualizaResultado(Object valor) {
		if (valor != null)
			super.setResultado(getResultado().intValue() + 1);
	}

	@Override
	public String toString() {
		return "count(" + getCampo() + ")";
	}

	@Override
	public FuncaoAgregacao copia() {
		Count funcao = new Count();
		funcao.setCampo(getCampo());
		return funcao;
	}

}
