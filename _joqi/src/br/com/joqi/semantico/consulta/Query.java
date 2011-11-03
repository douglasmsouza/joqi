package br.com.joqi.semantico.consulta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import br.com.joqi.semantico.consulta.agrupamento.Agrupamento;
import br.com.joqi.semantico.consulta.ordenacao.ItemOrdenacao.TipoOrdenacao;
import br.com.joqi.semantico.consulta.ordenacao.Ordenacao;
import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.plano.PlanoExecucao;
import br.com.joqi.semantico.consulta.projecao.ListaProjecoes;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoCampo;
import br.com.joqi.semantico.consulta.projecao.ProjecaoFuncaoAgregacao;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.IPossuiRestricoes;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.consulta.util.JoqiUtil;
import br.com.joqi.semantico.exception.ClausulaFromException;
import br.com.joqi.semantico.exception.RelacaoInexistenteException;
import br.com.joqi.testes.BancoConsulta;

public class Query implements IPossuiRestricoes {

	private BancoConsulta objetoConsulta;
	//
	private ListaProjecoes projecoes;
	private Set<Relacao> relacoes;
	private List<Restricao> restricoes;
	private Ordenacao ordenacao;
	private Agrupamento agrupamento;

	public Query() {
		objetoConsulta = new BancoConsulta();
		//
		projecoes = new ListaProjecoes();
		relacoes = new LinkedHashSet<Relacao>();
		restricoes = new ArrayList<Restricao>();
		ordenacao = new Ordenacao();
		agrupamento = new Agrupamento();
	}

	public void addProjecao(Projecao<?> projecao) {
		projecoes.add(projecao);
		//
		if (projecao instanceof ProjecaoFuncaoAgregacao) {
			agrupamento.addFuncaoAgregacao(((ProjecaoFuncaoAgregacao) projecao).getValor());
		}
	}

	public void addRelacao(Relacao relacao) throws RelacaoInexistenteException, ClausulaFromException {
		if (!relacoes.add(relacao)) {
			/*Apresenta erro caso a relacao ja tenho sido declarada na clausula FROM com o mesmo apelido*/
			throw new ClausulaFromException("A relação \"" + relacao.getNomeNaConsulta() + "\" foi declarada mais de uma vez ");
		} else {
			/*Associa a colecao a relacao*/
			relacao.setColecao(QueryUtils.getColecao(objetoConsulta, relacao.getNome()));
		}
	}

	@Override
	public void addRestricao(Restricao restricao) {
		restricoes.add(restricao);
	}

	public ListaProjecoes getProjecoes() {
		return projecoes;
	}

	public Set<Relacao> getRelacoes() {
		return relacoes;
	}

	@Override
	public List<Restricao> getRestricoes() {
		return restricoes;
	}

	public void addItemOrdenacao(ProjecaoCampo campo, TipoOrdenacao tipoOrdenacao) {
		ordenacao.addItem(campo, tipoOrdenacao);
	}

	public void addCampoAgrupamento(ProjecaoCampo campo) {
		agrupamento.addCampo(campo);
	}

	public void getResultado() {
		try {
			PlanoExecucao planoExecucao = new PlanoExecucao();
			ArvoreConsulta arvore = planoExecucao.montaArvore(projecoes, restricoes, relacoes, agrupamento, ordenacao);
			QueryImpl queryImplOtimizada4 = new QueryImpl(arvore);
			//
			arvore.imprime();
			double time = System.currentTimeMillis();
			Collection<ResultObject> resultado = queryImplOtimizada4.getResultList();
			String[] colunas;
			if (projecoes.size() == 0) {
				colunas = new String[relacoes.size()];
				int i = 0;
				for (Relacao r : relacoes) {
					colunas[i] = r.getNomeNaConsulta();
					i++;
				}
			} else {
				colunas = new String[projecoes.size()];
				int i = 0;
				for (Projecao<?> projecao : projecoes) {
					colunas[i] = projecao.getNomeNaConsulta();
					i++;
				}
			}
			time = System.currentTimeMillis() - time;
			JoqiUtil.imprimeResultado(15, time, colunas, resultado);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
