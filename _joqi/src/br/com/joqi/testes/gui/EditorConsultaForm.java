package br.com.joqi.testes.gui;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class EditorConsultaForm extends javax.swing.JFrame {

	private JLabel lbStatus;
	private JTable tableResultado;
	private JTextArea textAreaQuery;
	private JButton btnExecutar;
	private JButton btnLimpar;
	private JSplitPane splitPane;
	private JList listColecoes;
	private JScrollPane scrollPane_2;
	private JButton btnAbrirArquivo;

	public EditorConsultaForm() {
		setSize(new Dimension(800, 600));
		getContentPane().setLayout(null);

		lbStatus = new JLabel("");
		lbStatus.setBounds(10, 541, 772, 14);
		getContentPane().add(lbStatus);

		btnLimpar = new JButton("Limpar");
		btnLimpar.setMnemonic(KeyEvent.VK_0);
		btnLimpar.setBounds(127, 11, 89, 23);
		getContentPane().add(btnLimpar);

		btnExecutar = new JButton("Executar");
		btnExecutar.setMnemonic(KeyEvent.VK_F9);
		btnExecutar.setBounds(226, 11, 89, 23);
		getContentPane().add(btnExecutar);

		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setBounds(10, 45, 764, 485);
		getContentPane().add(splitPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);

		tableResultado = new JTable();
		tableResultado.setShowGrid(true);
		scrollPane_1.setViewportView(tableResultado);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane.setLeftComponent(splitPane_1);

		JScrollPane scrollPane = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane);

		textAreaQuery = new JTextArea();
		scrollPane.setViewportView(textAreaQuery);

		scrollPane_2 = new JScrollPane();
		scrollPane_2.setMinimumSize(new Dimension(100, 150));
		splitPane_1.setLeftComponent(scrollPane_2);

		listColecoes = new JList();
		scrollPane_2.setViewportView(listColecoes);

		btnAbrirArquivo = new JButton("Abrir arquivo...");
		btnAbrirArquivo.setBounds(10, 11, 107, 23);
		getContentPane().add(btnAbrirArquivo);

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("Editor de consultas");
		setLocationRelativeTo(null);
	}

	public JTable getTableResultado() {
		return tableResultado;
	}

	public JButton getBtnExecutar() {
		return btnExecutar;
	}

	public JButton getBtnLimpar() {
		return btnLimpar;
	}

	public JLabel getLbStatus() {
		return lbStatus;
	}

	public JTextArea getTextAreaQuery() {
		return textAreaQuery;
	}

	public JList getListColecoes() {
		return listColecoes;
	}

	public JButton getBtnAbrirArquivo() {
		return btnAbrirArquivo;
	}
}
