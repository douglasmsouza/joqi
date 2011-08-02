package br.com.joqi.semantico.consulta.resultado;

import java.util.HashMap;
import java.util.Map;

public class ResultObject {

	private Map<String, Object> objetos;

	public ResultObject(Map<String, Object> objetos) {
		this.objetos = new HashMap<String, Object>(objetos);
	}

	public ResultObject() {
		this(new HashMap<String, Object>());
	}

	public void add(String key, Object objeto) {
		objetos.put(key, objeto);
	}

	public Object get(String key) {
		return objetos.get(key);
	}

	public String getString(String key) {
		try {
			return String.valueOf(objetos.get(key));
		} catch (Exception e) {
			return "";
		}
	}

	public int getInt(String key) {
		try {
			return Integer.valueOf(objetos.get(key).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public double getDouble(String key) {
		try {
			return Double.valueOf(objetos.get(key).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public float getFloat(String key) {
		try {
			return Float.valueOf(objetos.get(key).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public Map<String, Object> get() {
		return objetos;
	}

	@Override
	public String toString() {
		return objetos.toString();
	}

}
