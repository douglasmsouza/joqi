package br.com.joqi.testes.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
		getBtnLimpar().addActionListener(btnLimparClick());
		//
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				getTextAreaQuery().requestFocus();
			}
		});
	}

	private ActionListener btnLimparClick() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getTextAreaQuery().setText("");
				getTextAreaQuery().requestFocus();
			}
		};
	}

	private ActionListener btnExecutarClick() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					((EditorConsultaTableModel) getTableResultado().getModel()).setObjetos(null);

					String queryString = getTextAreaQuery().getText();

					if (queryString != null && !queryString.isEmpty()) {
						Query query = new Query(bancoConsulta);
						Collection<ResultObject> collection = query.getResultList(queryString);
						//
						getLbStatus().setText("Registros: " + collection.size() + "           Tempo: " + query.getTempoExecucao() + " ms");
						if (collection.size() > 0) {
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
						} else {
							JOptionPane.showMessageDialog(null, "Nenhum resultado encontrado");
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.toString());
				}
				//
				getTextAreaQuery().requestFocus();
			}
		};
	}

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new EditorConsulta().setVisible(true);
	}
}
