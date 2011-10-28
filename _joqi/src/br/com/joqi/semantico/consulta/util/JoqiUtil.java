package br.com.joqi.semantico.consulta.util;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import br.com.joqi.semantico.consulta.resultado.ResultObject;
import br.com.joqi.semantico.consulta.resultado.ResultSet;

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
	
	public static void imprimeResultado(int tamanhoColuna, double tempo, String[] headers, ResultSet resultSet) {
		System.out.println("--------------------------------------------------------------------");
		for (String h : headers) {
			char[] header = new char[tamanhoColuna];
			Arrays.fill(header, ' ');
			for (int i = 0; i < h.length(); i++) {
				header[i] = h.charAt(i);
			}
			System.out.print(header);
		}
		//
		System.out.println();
		System.out.println("--------------------------------------------------------------------");
		//
		for (ResultObject objeto : resultSet) {
			for (String c : headers) {
				char[] campo = new char[tamanhoColuna];
				Arrays.fill(campo, ' ');
				String valor = objeto.get(c).toString();
				for (int i = 0; i < valor.length(); i++) {
					campo[i] = valor.charAt(i);
				}
				System.out.print(campo);
			}
			System.out.println();
		}

		System.out.println("--------------------------------------------------------------------");
		System.out.println("Registros...: " + resultSet.size());
		System.out.println("Tempo total : " + tempo + " ms");
		System.out.println("--------------------------------------------------------------------");
	}
}
