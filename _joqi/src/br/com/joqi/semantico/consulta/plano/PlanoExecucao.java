package br.com.joqi.semantico.consulta.plano;

public class PlanoExecucao {

	private ArvoreConsulta arvore;

	public PlanoExecucao() {
		arvore = new ArvoreConsulta();
	}
	
	public ArvoreConsulta getArvore() {
		return arvore;
	}
	
	private void descerRestricoes(NoArvore raiz){
		
	}

	public void executarOtimizacoes() {
		descerRestricoes(arvore.getRaiz());		
	}

}
