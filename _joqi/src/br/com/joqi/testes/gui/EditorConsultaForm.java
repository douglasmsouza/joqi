package br.com.joqi.testes.gui;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
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

	public EditorConsultaForm() {
		setSize(new Dimension(800, 600));
		getContentPane().setLayout(null);

		lbStatus = new JLabel("");
		lbStatus.setBounds(10, 541, 772, 14);
		getContentPane().add(lbStatus);

		btnLimpar = new JButton("Limpar");
		btnLimpar.setMnemonic(KeyEvent.VK_0);
		btnLimpar.setBounds(10, 11, 89, 23);
		getContentPane().add(btnLimpar);

		btnExecutar = new JButton("Executar");
		btnExecutar.setMnemonic(KeyEvent.VK_F9);
		btnExecutar.setBounds(109, 11, 89, 23);
		getContentPane().add(btnExecutar);

		splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setBounds(10, 45, 772, 485);
		getContentPane().add(splitPane);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(23, 150));
		splitPane.setLeftComponent(scrollPane);

		textAreaQuery = new JTextArea();
		scrollPane.setViewportView(textAreaQuery);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);

		tableResultado = new JTable();
		scrollPane_1.setViewportView(tableResultado);

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
}
