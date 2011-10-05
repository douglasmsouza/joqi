package br.com.joqi.semantico.consulta.plano;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.joqi.semantico.consulta.QueryUtils;
import br.com.joqi.semantico.consulta.disjuncao.UniaoRestricoes;
import br.com.joqi.semantico.consulta.produtocartesiano.ProdutoCartesiano;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.restricao.RestricaoConjunto;
import br.com.joqi.semantico.consulta.restricao.RestricaoSimples;
import br.com.joqi.semantico.consulta.restricao.operadorlogico.OperadorLogicoOr;
import br.com.joqi.semantico.exception.RelacaoInexistenteException;

public class PlanoExecucao {

	private Map<String, Integer> tamanhosColecoes;
	//
	private ArvoreConsulta arvore;

	public PlanoExecucao() {
		arvore = new ArvoreConsulta();
		tamanhosColecoes = new HashMap<String, Integer>();
	}

	private void inserirRelacoes(Object objetoConsulta, List<Relacao> relacoes) throws RelacaoInexistenteException {
		inserirRelacoes(objetoConsulta, arvore.getRaizRestricoes(), relacoes);
	}

	private void inserirRelacoes(Object objetoConsulta, NoArvore raizRestricoes, List<Relacao> relacoes) throws RelacaoInexistenteException {
		if (raizRestricoes != null) {
			NoArvore filho = raizRestricoes.getFilho();
			while (filho != null) {
				NoArvore filho1 = filho;
				while (filho1.getFilho() != null) {
					filho1 = filho1.getFilho();
				}
				//
				while(filho1 != null){ 
    				NoArvore ultimo = arvore.insere(filho1, new ProdutoCartesiano());
    				for (Relacao relacao : relacoes) {
    					relacao.setColecao(QueryUtils.getColecao(objetoConsulta, relacao.getNome()));
    					arvore.insere(ultimo, relacao);
    				}
    				//
    				filho1 = filho1.getIrmao();
				}
				//
				filho = filho.getIrmao();
			}
		} else {
			NoArvore ultimo = arvore.insere(new ProdutoCartesiano());
			for (Relacao relacao : relacoes) {
				relacao.setColecao(QueryUtils.getColecao(objetoConsulta, relacao.getNome()));
				arvore.insere(ultimo, relacao);
			}
		}
	}

	private String getNomeRelacao(RestricaoSimples restricao) {
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		//
		if (operando1.getRelacao() == null && operando2.getRelacao() == null) {
			return tamanhosColecoes.keySet().iterator().next();
		} else if (operando1.getRelacao() != null) {
			return operando1.getRelacao();
		} else {
			return operando2.getRelacao();
		}
	}

	private NoArvore inserirRestricoes(NoArvore no, List<Restricao> restricoes) {
		NoArvore noRestricao = null;
		//
		for (Restricao r : restricoes) {
			NoArvore noInserir = noRestricao;			
			if (r.getOperadorLogico() == null || r.getOperadorLogico().getClass() == OperadorLogicoOr.class) {
				noInserir = no;
			}
			//
			if(r.getClass() == RestricaoSimples.class){    			
    			noRestricao = arvore.insere(noInserir, r);
			} else {
				ArvoreConsulta subArvore = inserirRestricoes(new ArvoreConsulta(), ((RestricaoConjunto)r).getRestricoes());
				noRestricao = arvore.insere(noInserir, subArvore);	
				/*noRestricao = inserirRestricoes(noInserir, ((RestricaoConjunto)r).getRestricoes());*/
			}
		}
		//
		return noRestricao;
	}

	private ArvoreConsulta inserirRestricoes(ArvoreConsulta arvore, List<Restricao> restricoes) {
		if (restricoes.size() > 0) {
			NoArvore raizRestricoes = arvore.insere(new UniaoRestricoes());
			arvore.setRaizRestricoes(inserirRestricoes(raizRestricoes, restricoes));
		}
		//
		return arvore;
	}

	public void montarArvore(Object objetoConsulta, List<Restricao> restricoes, List<Relacao> relacoes) throws RelacaoInexistenteException {
		inserirRestricoes(arvore, restricoes);
		/*inserirRelacoes(objetoConsulta, relacoes);*/
	}

	public void imprimirArvore() {
		arvore.imprime();

	}

	public void insereOperacao(Object operacao) {
		arvore.insere(operacao);

	}

}
