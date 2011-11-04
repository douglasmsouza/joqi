package br.com.joqi.testes.gui;

import java.util.Collection;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

import br.com.joqi.semantico.consulta.resultado.ResultObject;

public class EditorConsultaTableModel extends AbstractTableModel {

	private ResultObject[] objetos;

	public ResultObject[] getObjetos() {
		return objetos;
	}

	public void setObjetos(ResultObject[] objetos) {
		this.objetos = objetos;
	}

	public EditorConsultaTableModel() {
	}

	public EditorConsultaTableModel(Collection<ResultObject> objetos) {
		this.objetos = objetos.toArray(new ResultObject[0]);
	}

	@Override
	public int getColumnCount() {
		if (objetos != null) {
			if (objetos.length > 0)
				return objetos[0].keySet().size();
		}
		return 0;
	}

	@Override
	public int getRowCount() {
		if (objetos != null) {
			return objetos.length;
		}
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (objetos != null) {
			Object[] entries = objetos[rowIndex].entrySet().toArray();
			return ((Entry<String, Object>) entries[columnIndex]).getValue();
		}
		return "";
	}

}
