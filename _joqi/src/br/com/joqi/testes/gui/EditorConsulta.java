package br.com.joqi.testes.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import br.com.joqi.semantico.consulta.Query;
import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.testes.BancoConsulta;

public class EditorConsulta extends EditorConsultaForm {

	private BancoConsulta bancoConsulta;

	public EditorConsulta() {
		this.bancoConsulta = new BancoConsulta();
		//
		getTableResultado().setModel(new EditorConsultaTableModel());
		getBtnAbrirArquivo().addActionListener(btnAbrirArquivoClick());
		getBtnExecutar().addActionListener(btnExecutarClick());
		getBtnLimpar().addActionListener(btnLimparClick());
		getListColecoes().setListData(getNomesColecoes(bancoConsulta).toArray());
		//
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				getTextAreaQuery().requestFocus();
			}
		});
	}

	private ActionListener btnAbrirArquivoClick() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					JFileChooser fileChooser = new JFileChooser("testes/joqi");
					if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File file = fileChooser.getSelectedFile();
						FileReader fileReader = new FileReader(file);
						BufferedReader reader = new BufferedReader(fileReader);
						getTextAreaQuery().read(reader, null);
						getTextAreaQuery().requestFocus();
						reader.close();
						fileReader.close();
						file = null;
					}
				} catch (Exception e) {
				}
			}
		};
	}

	private List<String> getNomesColecoes(Object objeto) {
		List<String> colecoes = new ArrayList<String>();
		//
		Class<?> clazz = objeto.getClass();
		try {
			Field[] atributos = clazz.getDeclaredFields();
			for (Field atributo : atributos) {
				boolean ehPrivado = !atributo.isAccessible();

				if (ehPrivado)
					atributo.setAccessible(true);

				Object valor = atributo.get(objeto);

				if (valor instanceof Collection) {
					colecoes.add(atributo.getName() + " (" + ((Collection) valor).size() + ")");
				} else if (valor instanceof Object[]) {
					colecoes.add(atributo.getName() + " (" + ((Object[]) valor).length + ")");
				}

				if (ehPrivado)
					atributo.setAccessible(false);
			}
		} catch (Exception e) {
		}
		//
		return colecoes;
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
						Collection<ResultObject> collection = query.getResultCollection(queryString);
						//
						getLbStatus().setText("Registros: " + collection.size() + "           Tempo: " + query.getTempoExecucao() + " ms");
						if (collection.size() > 0) {
							getTableResultado().setModel(new EditorConsultaTableModel(collection));
							ResultObject objeto = collection.iterator().next();
							int i = 0;
							for (String s : objeto.keySet()) {
								getTableResultado().getColumnModel().getColumn(i).setHeaderValue(s);
								i++;
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
		/*UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());*/
		new EditorConsulta().setVisible(true);
	}
}
