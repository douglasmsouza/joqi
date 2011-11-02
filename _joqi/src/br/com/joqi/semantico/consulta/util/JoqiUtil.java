package br.com.joqi.semantico.consulta.util;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import br.com.joqi.semantico.consulta.resultado.ResultObject;

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

	public static void imprimeResultado(int tamanhoColuna, double tempo, String[] headers, Collection<ResultObject> resultSet) {
		for (String x : headers) {
			for (int i = 0; i < tamanhoColuna; i++)
				System.out.print("-");
		}
		System.out.println();

		for (String h : headers) {
			char[] header = new char[tamanhoColuna];
			Arrays.fill(header, ' ');
			int i = 0;
			while (i < tamanhoColuna && i < h.length()) {
				header[i] = h.charAt(i);
				i++;
			}
			System.out.print(header);
		}
		//
		System.out.println();
		for (String x : headers) {
			for (int i = 0; i < tamanhoColuna; i++)
				System.out.print("-");
		}
		System.out.println();
		//
		for (ResultObject objeto : resultSet) {
			for (String c : headers) {
				Object o = objeto.get(c);
				char[] campo = new char[tamanhoColuna];
				Arrays.fill(campo, ' ');
				if (o == null) {
					campo[0] = 'N';
					campo[1] = 'U';
					campo[2] = 'L';
					campo[3] = 'L';
					for (int i = 4; i < tamanhoColuna; i++) {
						campo[i] = ' ';
					}
				} else {
					String valor = o.toString();
					if (valor == null) {
						campo[0] = 'N';
						campo[1] = 'U';
						campo[2] = 'L';
						campo[3] = 'L';
						for (int i = 4; i < tamanhoColuna; i++) {
							campo[i] = ' ';
						}
					} else {
						int i = 0;
						while (i < tamanhoColuna && i < valor.length()) {
							campo[i] = valor.charAt(i);
							i++;
						}
					}
				}
				System.out.print(campo);
			}
			System.out.println();
		}

		for (String x : headers) {
			for (int i = 0; i < tamanhoColuna; i++)
				System.out.print("-");
		}
		System.out.println();

		System.out.println("Registros...: " + resultSet.size());
		System.out.println("Tempo total : " + tempo + " ms");
		
		for (String x : headers) {
			for (int i = 0; i < tamanhoColuna; i++)
				System.out.print("-");
		}
	}
}
