package br.com.joqi.semantico.consulta.projecao;

public class ProjecaoString extends Projecao<String> {

	public ProjecaoString() {
		super();
	}

	public ProjecaoString(String apelido, String valor) {
		super(apelido, valor);
	}

	public ProjecaoString(String valor) {
		super(valor);
	}

	@Override
	public void setValor(String valor) {
		if (valor != null) {
			/*Retira as aspas do inicio e do fim*/
			StringBuilder sb = new StringBuilder(valor);
			super.setValor(sb.delete(0, 1).delete(sb.length() - 1, sb.length()).toString());
		} else {
			super.setValor(null);
		}
	}

}
