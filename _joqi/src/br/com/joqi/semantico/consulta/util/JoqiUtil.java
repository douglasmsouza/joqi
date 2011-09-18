package br.com.joqi.semantico.consulta.util;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JoqiUtil {

	public static Date asDate(Object objeto) {
		try {
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
			return sd.parse(objeto.toString());
		} catch (Exception e) {
			return null;
		}
	}

	public static String retiraAcentuacao(String s) {
		return retiraAcentuacao(s, true);
	}

	public static String retiraAcentuacao(String s, boolean tiraCaractereOrdinal) {
		StringBuilder expressaoRegular = new StringBuilder();
		//
		expressaoRegular.append("[^\\p{ASCII}");
		if (!tiraCaractereOrdinal)
			expressaoRegular.append("|\\º");
		expressaoRegular.append("]");
		//
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll(expressaoRegular.toString(), "");
		return s;
	}
}
