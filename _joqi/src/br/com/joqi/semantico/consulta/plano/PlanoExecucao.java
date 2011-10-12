package br.com.joqi.semantico.consulta.plano;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.joqi.semantico.consulta.QueryUtils;
import br.com.joqi.semantico.consulta.busca.tipo.TipoBusca;
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

	private ArvoreConsulta arvore;
	//
	private Object objetoConsulta;
	private List<Relacao> relacoes;

	public PlanoExecucao() {
		arvore = new ArvoreConsulta();
	}

	private void inserirRestricoes(NoArvore no, List<Restricao> restricoes)
			throws RelacaoInexistenteException {
		NoArvore noRestricao = null;
		//
		for (Restricao r : restricoes) {
			NoArvore noInserir = noRestricao;
			if (r.getOperadorLogico() == null || r.getOperadorLogico().getClass() == OperadorLogicoOr.class) {
				noInserir = arvore.insere(no,new ProdutoCartesiano());
				//
				if (noRestricao != null) {
					inserirRelacoes(noRestricao);
				}
			}
			//
			if (r.getClass() == RestricaoSimples.class) {
				noRestricao = arvore.insere(noInserir, r);
				//
				descerRestricoes(noRestricao);
			} else {
				ArvoreConsulta subArvore = montarArvore(new ArvoreConsulta(), ((RestricaoConjunto) r).getRestricoes());
				noRestricao = arvore.insere(noInserir, subArvore);
			}
		}
		//
		inserirRelacoes(noRestricao);
	}

	private void descerRestricoes(NoArvore no) {
		NoArvore pai = no.getPai();
		while (pai != null && pai.getOperacao().getClass() == RestricaoSimples.class) {
			RestricaoSimples r1 = (RestricaoSimples) pai.getOperacao();
			RestricaoSimples r2 = (RestricaoSimples) no.getOperacao();
			//
			if (r1.getTipoBusca() == TipoBusca.LINEAR) {
				if (r2.getTipoBusca() != TipoBusca.LINEAR) {
					pai.setOperacao(r2);
					no.setOperacao(r1);
				}
			} else if (r1.getTipoBusca() == TipoBusca.JUNCAO_HASH) {
				if (r2.getTipoBusca() == TipoBusca.LOOP_ANINHADO) {
					pai.setOperacao(r2);
					no.setOperacao(r1);
				}
			} else {
				break;
			}
			//
			no = no.getPai();
			pai = no.getPai();
		}
	}

	private Set<RestricaoSimples> restricoesJaOrdenadas;

	private void ordenarRestricoesLineares(NoArvore raiz) {
		if (raiz != null) {
			Object operacao = raiz.getOperacao();
			if (operacao.getClass() == ArvoreConsulta.class) {
				ordenarRestricoesLineares(((ArvoreConsulta) operacao).getRaiz().getFilho());
			} else {
				if (operacao.getClass() == RestricaoSimples.class) {
					RestricaoSimples restricao = (RestricaoSimples) operacao;
					if (!restricoesJaOrdenadas.contains(restricao)) {
						if (restricao.getTipoBusca() == TipoBusca.LINEAR) {
							restricoesJaOrdenadas.add(restricao);
							NoArvore noRelacao = encontraRelacao(raiz, getRelacaoString(restricao));
							Object relacao = noRelacao.getOperacao();
							noRelacao.setOperacao(operacao);
							noRelacao.setFilho(new NoArvore(relacao));
							raiz.getPai().setFilho(raiz.getFilho());
						}
					}
				}
				//
				ordenarRestricoesLineares(raiz.getFilho());
				ordenarRestricoesLineares(raiz.getIrmao());
			}
		}
	}

	private void ordenarRestricoesJuncoes(NoArvore raiz) {
		if (raiz != null) {
			RestricaoSimples restricaoJuncao = (RestricaoSimples) raiz.getOperacao();
			/*raiz.getFilho() retorna um ProdutoCartesiano*/
			NoArvore filho = raiz.getFilho();
			while (filho != null) {				
				String relacaoFilho = null;
				//
				Object operacao = filho.getOperacao();
				if (operacao.getClass() == RestricaoSimples.class) {
					relacaoFilho = getRelacaoString((RestricaoSimples) operacao);
				} else if (operacao.getClass() == Relacao.class) {
					relacaoFilho = ((Relacao) operacao).getNomeNaConsulta();
				}
				//
				if(relacaoFilho != null){
					if (!restricaoJuncao.getOperando1().getRelacao().equals(relacaoFilho)) {
						if (!restricaoJuncao.getOperando2().getRelacao().equals(relacaoFilho)) {
							raiz.removeFilho(filho);
							raiz.getPai().addFilho(filho);
						}
					}
				} 
				//
				filho = filho.getIrmao();
			}
		}
	}

	private NoArvore encontraRelacao(NoArvore raiz, String relacao) {
		NoArvore noRelacao = null;
		//
		if (raiz != null) {
			if (noEhRelacao(raiz, relacao)) {
				return raiz;
			}
			noRelacao = encontraRelacao(raiz.getFilho(), relacao);
			if (noRelacao == null) {
				noRelacao = encontraRelacao(raiz.getIrmao(), relacao);
			}
		}
		//
		return noRelacao;
	}

	private boolean noEhRelacao(NoArvore no, String relacao) {
		if (no.getOperacao().getClass() == Relacao.class) {
			if (((Relacao) no.getOperacao()).getNomeNaConsulta().equals(relacao)) {
				return true;
			}
		}
		return false;
	}

	private void inserirRelacoes(NoArvore no) throws RelacaoInexistenteException {
		for (Relacao relacao : relacoes) {
			relacao.setColecao(QueryUtils.getColecao(objetoConsulta, relacao.getNome()));
			arvore.insere(no, relacao);
		}
	}

	private ArvoreConsulta montarArvore(ArvoreConsulta arvore, List<Restricao> restricoes) throws RelacaoInexistenteException {
		if (restricoes.size() > 0) {
			NoArvore raizRestricoes = arvore.insere(new UniaoRestricoes());
			inserirRestricoes(raizRestricoes, restricoes);

			arvore.setRaizRestricoes(raizRestricoes);
		} else {
			NoArvore no = arvore.insere(new ProdutoCartesiano());
			inserirRelacoes(no);
		}
		//
		return arvore;
	}

	public ArvoreConsulta montarArvore(Object objetoConsulta, List<Restricao> restricoes, List<Relacao> relacoes) throws RelacaoInexistenteException {
		this.objetoConsulta = objetoConsulta;
		this.relacoes = relacoes;
		//
		restricoesJaOrdenadas = new HashSet<RestricaoSimples>();
		//
		montarArvore(arvore, restricoes);
		ordenarRestricoesLineares(arvore.getRaizRestricoes().getFilho());
		ordenarRestricoesJuncoes(arvore.getRaizRestricoes().getFilho().getFilho());
		arvore.imprime();
		//
		return null;
	}

	public void insereOperacao(Object operacao) {
		arvore.insere(operacao);
	}

	private String getRelacaoString(RestricaoSimples restricao) {
		Projecao<?> operando1 = restricao.getOperando1();
		Projecao<?> operando2 = restricao.getOperando2();
		//
		if (operando1.getRelacao() == null && operando2.getRelacao() == null) {
			return relacoes.get(0).getNomeNaConsulta();
		} else if (operando1.getRelacao() != null) {
			return operando1.getRelacao();
		} else {
			return operando2.getRelacao();
		}
	}

}
