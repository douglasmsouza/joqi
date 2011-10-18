package br.com.joqi.semantico.consulta.restricao.operadorlogico;

public abstract class OperadorLogico {

	@Override
	public boolean equals(Object obj) {
		return obj.getClass() == this.getClass();
	}

}
