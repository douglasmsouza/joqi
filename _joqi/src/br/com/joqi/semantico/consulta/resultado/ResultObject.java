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
			return (String) get(name);
		} catch (Exception e) {
			return "";
		}
	}

	public int getInt(String name) {
		try {
			return (Integer) get(name);
		} catch (Exception e) {
			return 0;
		}
	}

	public double getDouble(String name) {
		try {
			return (Double) get(name);
		} catch (Exception e) {
			return 0;
		}
	}

	public float getFloat(String name) {
		try {
			return (Float) get(name);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public Object get(Object key) {
		Object value = super.get(key);
		//
		if (value == null) {
			String nomeCampo = String.valueOf(key.toString().toCharArray()[0]).toUpperCase() + key.toString().substring(1);
			value = super.get("get" + nomeCampo);
			if (value == null) {
				value = super.get("is" + nomeCampo);
			}
		}
		//
		return value;
	}
}
