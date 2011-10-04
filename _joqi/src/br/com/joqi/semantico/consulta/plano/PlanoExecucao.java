package br.com.joqi.semantico.consulta.plano;

import java.util.Iterator;
import java.util.List;

import br.com.joqi.semantico.consulta.produtocartesiano.ProdutoCartesiano;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;

public class PlanoExecucao {

	private ArvoreConsulta arvore;

	public PlanoExecucao() {
		arvore = new ArvoreConsulta();
	}

	public ArvoreConsulta getArvore() {
		return arvore;
	}

	private void organizarRestricoes(NoArvore no) {
		if(no != null){
			if(no.getOperacao().getClass() == RestricaoSimples.class){
				RestricaoSimples restricao = (RestricaoSimples) no.getOperacao();
				
			}
		}
	}
	
	public void organizarRestricoes() {
		organizarRestricoes(arvore.getRaiz());
	}

	public void inserirRelacoes(List<Relacao> relacoes) {
		if (relacoes.size() > 1) {
			arvore.insere(inserirRelacoes(null, relacoes.iterator()));
		} else {
			arvore.insere(relacoes.get(0));
		}
	}

	private NoArvore inserirRelacoes(NoArvore no, Iterator<Relacao> relacoes) {
		if (relacoes.hasNext()) {
			if (no == null) {
				no = new NoArvore(new ProdutoCartesiano());
			}
			//
			if (no.getDireita() == null) {
				no.setDireita(new NoArvore(relacoes.next()));
			} else if (no.getEsquerda() == null) {
				no.setEsquerda(new NoArvore(relacoes.next()));
			} else {
				NoArvore noAntigo = no;
				no = new NoArvore(new ProdutoCartesiano());
				no.setEsquerda(noAntigo);
				no.setDireita(new NoArvore(relacoes.next()));
			}
			//
			no = inserirRelacoes(no, relacoes);
		}
		//
		return no;
	}

}
