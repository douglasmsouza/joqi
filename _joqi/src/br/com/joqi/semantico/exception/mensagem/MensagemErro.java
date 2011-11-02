package br.com.joqi.semantico.exception.mensagem;

import br.com.joqi.semantico.consulta.projecao.Projecao;

public class MensagemErro {

	public static String getNomeRelacaoObrigatorio(Projecao<?> campo, String clausula) {
		return getNomeRelacaoObrigatorio(campo, null, clausula);
	}

	public static String getNomeRelacaoObrigatorio(Projecao<?> campo, Object operacao, String clausula) {
		String exception = "Nome da rela��o obrigat�rio em \"" + campo + "\" na cl�usula " + clausula.toUpperCase();
		if (operacao != null)
			exception += " (" + operacao + ")";
		return exception;
	}

	public static String getRelacaoNaoDeclarada(Projecao<?> campo) {
		return getRelacaoNaoDeclarada(campo, null);
	}

	public static String getRelacaoNaoDeclarada(Projecao<?> campo, Object operacao) {
		String exception = "Rela��o \"" + campo.getRelacao() + "\" n�o declarada";
		if (operacao != null)
			exception += " (" + operacao + ")";
		return exception;
	}

}
