package br.com.joqi.semantico.consulta.projecao;

import br.com.joqi.semantico.consulta.agrupamento.agregacao.FuncaoAgregacao;

public class ProjecaoFuncaoAgregacao extends Projecao<FuncaoAgregacao> {

	public ProjecaoFuncaoAgregacao() {
		super();
	}

	public ProjecaoFuncaoAgregacao(FuncaoAgregacao valor) {
		super(valor);
	}

	public ProjecaoFuncaoAgregacao(String apelido, FuncaoAgregacao valor) {
		super(apelido, valor);
	}

}
