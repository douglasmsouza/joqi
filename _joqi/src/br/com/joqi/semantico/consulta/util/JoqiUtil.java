package br.com.joqi.semantico.consulta.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JoqiUtil {

	public static Double converteParaDouble(Object objeto) {
		try {
			return Double.valueOf(objeto.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static Date converteParaDate(Object objeto) {
		try {
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
			return sd.parse(objeto.toString());
		} catch (Exception e) {
			return null;
		}
	}

}
