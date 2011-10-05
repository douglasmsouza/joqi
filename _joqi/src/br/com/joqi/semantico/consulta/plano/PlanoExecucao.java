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

	private void inserirRelacoes(Object objetoConsulta, NoArvore no, List<Relacao> relacoes) throws RelacaoInexistenteException {
		if (no != null) {
			no = no.getFilho();
			if (no != null) {
				if (no.getOperacao().getClass() == ArvoreConsulta.class) {
					inserirRelacoes(objetoConsulta, ((ArvoreConsulta) no.getOperacao()).getRaizRestricoes(), relacoes);
				}
				/*Desce ate a ultima restricao*/
				while (no.getFilho() != null) {
					no = no.getFilho();
				}
				/*Insere as relacoes no proprio noh e nos irmaos*/
				while (no != null) {
					NoArvore ultimo = arvore.insere(no, new ProdutoCartesiano());
					for (Relacao relacao : relacoes) {
						relacao.setColecao(QueryUtils.getColecao(objetoConsulta, relacao.getNome()));
						arvore.insere(ultimo, relacao);
					}
					//
					no = no.getIrmao();
				}
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

	private void inserirRestricoes(NoArvore no, List<Restricao> restricoes) {
		NoArvore noRestricao = null;
		//
		for (Restricao r : restricoes) {
			NoArvore noInserir = noRestricao;
			if (r.getOperadorLogico() == null || r.getOperadorLogico().getClass() == OperadorLogicoOr.class) {
				noInserir = no;
			}
			//
			if (r.getClass() == RestricaoSimples.class) {
				noRestricao = arvore.insere(noInserir, r);
			} else {
				ArvoreConsulta subArvore = inserirRestricoes(new ArvoreConsulta(), ((RestricaoConjunto) r).getRestricoes());
				noRestricao = arvore.insere(noInserir, subArvore);
			}
		}
	}

	private ArvoreConsulta inserirRestricoes(ArvoreConsulta arvore, List<Restricao> restricoes) {
		if (restricoes.size() > 0) {
			NoArvore raizRestricoes = arvore.insere(new UniaoRestricoes());
			inserirRestricoes(raizRestricoes, restricoes);
			arvore.setRaizRestricoes(raizRestricoes);
		}
		//
		return arvore;
	}

	private void organizarRestricoes(NoArvore noRestricao) {
		if (noRestricao != null) {
			NoArvore anterior = noRestricao;
			noRestricao = noRestricao.getFilho();
			while (noRestricao != null) {
				Object operacao1 = noRestricao.getOperacao();
				if (operacao1.getClass() == ArvoreConsulta.class) {
					organizarRestricoes(((ArvoreConsulta) operacao1).getRaizRestricoes());
				} else if (noRestricao.getFilho() != null) {
					Object operacao2 = noRestricao.getFilho().getOperacao();
					if (operacao2.getClass() == ArvoreConsulta.class) {
						organizarRestricoes(((ArvoreConsulta) operacao2).getRaizRestricoes());
					} else {
						RestricaoSimples r1 = (RestricaoSimples) operacao1;
						RestricaoSimples r2 = (RestricaoSimples) operacao2;
						//
						if(r1.isConstante() && r2.isJuncao()){
							NoArvore temp = noRestricao;
							NoArvore tempFilho = temp.getFilho().getFilho();
							//
							noRestricao = noRestricao.getFilho();
							noRestricao.setFilho(temp);
							temp.setFilho(tempFilho);
							anterior.setFilho(noRestricao);
						}
					}
				}
				//
				noRestricao = noRestricao.getFilho();
			}
		}
	}

	public void montarArvore(Object objetoConsulta, List<Restricao> restricoes, List<Relacao> relacoes) throws RelacaoInexistenteException {
		inserirRestricoes(arvore, restricoes);
		organizarRestricoes(arvore.getRaizRestricoes());
		inserirRelacoes(objetoConsulta, arvore.getRaizRestricoes(), relacoes);
	}

	public void imprimirArvore() {
		arvore.imprime();

	}

	public void insereOperacao(Object operacao) {
		arvore.insere(operacao);
	}

}
