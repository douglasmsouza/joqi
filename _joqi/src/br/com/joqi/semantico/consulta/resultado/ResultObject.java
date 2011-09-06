package br.com.joqi.semantico.consulta.resultado;

import java.util.HashMap;
import java.util.Map;

public class ResultObject extends HashMap<String, Object> {

	public ResultObject(Map<String, Object> m) {
		super(m);
	}

	public ResultObject() {
	}

	public String getString(String name) {
		try {
			return String.valueOf(get(name));
		} catch (Exception e) {
			return "";
		}
	}

	public int getInt(String name) {
		try {
			return Integer.valueOf(get(name).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public double getDouble(String name) {
		try {
			return Double.valueOf(get(name).toString());
		} catch (Exception e) {
			return 0;
		}
	}

	public float getFloat(String name) {
		try {
			return Float.valueOf(get(name).toString());
		} catch (Exception e) {
			return 0;
		}
	}

}
