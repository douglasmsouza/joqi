package br.com.joqi.semantico.consulta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.joqi.parser.Parser;
import br.com.joqi.semantico.consulta.agrupamento.Agrupamento;
import br.com.joqi.semantico.consulta.ordenacao.ItemOrdenacao.TipoOrdenacao;
import br.com.joqi.semantico.consulta.ordenacao.Ordenacao;
import br.com.joqi.semantico.consulta.plano.ArvoreConsulta;
import br.com.joqi.semantico.consulta.plano.PlanoExecucao;
import br.com.joqi.semantico.consulta.projecao.ListaProjecoes;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.projecao.ProjecaoFuncaoAgregacao;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.restricao.IPossuiRestricoes;
import br.com.joqi.semantico.consulta.restricao.Restricao;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.exception.ClausulaFromException;
import br.com.joqi.semantico.exception.RelacaoInexistenteException;

public class Query implements IPossuiRestricoes {

	private Object objetoConsulta;
	//
	private ListaProjecoes projecoes;
	private Set<Relacao> relacoes;
	private List<Restricao> restricoes;
	private Ordenacao ordenacao;
	private Agrupamento agrupamento;
	//
	private Map<String, Projecao<?>> apelidosProjecoes;
	//
	private double tempoExecucao;
	//
	private int min;
	private int max;

	public Query(Object objetoConsulta) {
		this.objetoConsulta = objetoConsulta;
		//
		projecoes = new ListaProjecoes();
		relacoes = new LinkedHashSet<Relacao>();
		restricoes = new ArrayList<Restricao>();
		ordenacao = new Ordenacao();
		agrupamento = new Agrupamento();
		apelidosProjecoes = new HashMap<String, Projecao<?>>();
		tempoExecucao = 0;
	}

	private Projecao<?> getProjecaoMapeada(Projecao projecao) {
		Projecao<?> projecaoMapeada = apelidosProjecoes.get(projecao.getValor());
		if (projecaoMapeada != null) {
			try {
				projecao = projecaoMapeada.getClass().newInstance();
				projecao.setValor(projecaoMapeada.getValor());
				projecao.setApelido(projecaoMapeada.getApelido());
				projecao.setRelacao(projecaoMapeada.getRelacao());
			} catch (Exception e) {
			}
		}
		return projecao;
	}

	public void addProjecao(Projecao<?> projecao) {
		if (projecao.getApelido() != null) {
			apelidosProjecoes.put(projecao.getApelido(), projecao);
		}
		//
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

	public void addItemOrdenacao(Projecao<?> campo, TipoOrdenacao tipoOrdenacao) {
		ordenacao.addItem(getProjecaoMapeada(campo), tipoOrdenacao);
	}

	public void addCampoAgrupamento(Projecao<?> campo) {
		agrupamento.addCampo(getProjecaoMapeada(campo));
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

	public double getTempoExecucao() {
		return tempoExecucao;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public Collection<ResultObject> getResultCollection(String query) throws Exception {
		tempoExecucao = System.currentTimeMillis();

		/*Executa os analisares lexico e sintatico*/
		new Parser(this, query).executa();

		/*Monta a arvore de consulta*/
		PlanoExecucao planoExecucao = new PlanoExecucao();
		ArvoreConsulta arvore = planoExecucao.montaArvore(projecoes, restricoes, relacoes, agrupamento, ordenacao);
		arvore.imprime();

		tempoExecucao = System.currentTimeMillis();

		/*Executa a consulta com base na arvore montada*/
		Collection<ResultObject> resultado = new QueryImpl(arvore).getResultCollection();

		tempoExecucao = System.currentTimeMillis() - tempoExecucao;

		if (min > 0 || max > 0)
			return subList(new ArrayList<ResultObject>(resultado), min, max);
		else
			return resultado;
	}

	private static <T> List<T> subList(List<T> lista, int inicio, int fim) {
		inicio = inicio - 1;
		//
		if (inicio >= 0 && inicio < lista.size()) {
			if (inicio < fim) {
				if (fim <= lista.size())
					lista = lista.subList(inicio, fim);
				else {
					lista = lista.subList(inicio, lista.size());
				}
			}
		}
		//
		return lista;
	}
}
