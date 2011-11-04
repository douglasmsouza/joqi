package br.com.joqi.testes.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import br.com.joqi.semantico.consulta.Query;
import br.com.joqi.semantico.consulta.projecao.Projecao;
import br.com.joqi.semantico.consulta.relacao.Relacao;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.testes.BancoConsulta;

public class EditorConsulta extends EditorConsultaForm {

	private BancoConsulta bancoConsulta;

	public EditorConsulta() {
		this.bancoConsulta = new BancoConsulta();
		//
		getTableResultado().setModel(new EditorConsultaTableModel());
		getBtnExecutar().addActionListener(btnExecutarClick());
	}

	private ActionListener btnExecutarClick() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					String queryString = getTextAreaQuery().getText();
					if (queryString != null && !queryString.isEmpty()) {
						Query query = new Query(bancoConsulta);
						Collection<ResultObject> collection = query.getResultList(queryString);
						//
						getLbStatus().setText("Registros: " + collection.size() + "           Tempo: " + query.getTempoExecucao() + " ms");
						getTableResultado().setModel(new EditorConsultaTableModel(collection));
						if (query.getProjecoes().size() == 0) {
							int i = 0;
							for (Relacao r : query.getRelacoes()) {
								getTableResultado().getColumnModel().getColumn(i).setHeaderValue(r.getNomeNaConsulta());
								i++;
							}
						} else {
							int i = 0;
							for (Projecao<?> p : query.getProjecoes()) {
								getTableResultado().getColumnModel().getColumn(i).setHeaderValue(p.getNomeNaConsulta());
								i++;
							}
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.toString());
				}
			}
		};
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new EditorConsulta().setVisible(true);
	}
}
