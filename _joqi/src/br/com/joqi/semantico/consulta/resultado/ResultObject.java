package br.com.joqi.semantico.consulta.resultado;

import java.util.HashMap;
import java.util.Map;

public class ResultObject {

	private Map<String, Object> campos;

	public ResultObject(Map<String, Object> objetos) {
		this.campos = new HashMap<String, Object>(objetos);
	}

	public ResultObject() {
		this(new HashMap<String, Object>());
	}

	public void add(String campo, Object valor) {
		campos.put(campo, valor);
	}

	public Object get(String name) {
		return campos.get(name);
	}

	public String getString(String name) {
		try {
			return String.valueOf(campos.get(name));
		} catch (Exception e) {
			return "";
		}
	}

	public int getInt(String name) {
		try {
			return Integer.valueOf(campos.get(name).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public double getDouble(String name) {
		try {
			return Double.valueOf(campos.get(name).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public float getFloat(String name) {
		try {
			return Float.valueOf(campos.get(name).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public Map<String, Object> get() {
		return campos;
	}

	@Override
	public String toString() {
		return campos.toString();
	}

}
