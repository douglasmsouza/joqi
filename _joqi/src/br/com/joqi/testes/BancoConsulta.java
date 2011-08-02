package br.com.joqi.testes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BancoConsulta {

	private List<Pessoa> pessoas;
	private List<Integer> inteiros;
	private List<String> strings;
	private List<Float> floats;
	private List<Object> todos;

	public BancoConsulta() {
		pessoas = criaListaPessoas();
		inteiros = criaListaInteiros();
		strings = criaListaStrings();
		floats = criaListaFloats();
		todos = criaListaTodos();
	}

	private List<Object> criaListaTodos() {
		List<Object> list = new ArrayList<Object>();
		list.addAll(criaListaFloats());
		list.addAll(criaListaInteiros());
		list.addAll(criaListaStrings());
		list.addAll(criaListaPessoas());
		return list;
	}

	private List<Float> criaListaFloats() {
		return new ArrayList<Float>(Arrays.asList(1.4291108f, 29.935354f, 94.613106f, 6.0628295f, 42.810368f, 24.656958f, 70.30145f, 16.618633f,
				16.544987f, 61.666973f, 21.010345f, 88.59421f, 6.853187f, 3.804469f, 1.348269f, 94.00414f, 78.97231f, 66.82582f, 83.576675f,
				56.05551f, 79.167915f, 11.363501f, 90.98175f, 23.928648f, 60.164463f, 5.1635323f, 83.64953f, 61.945107f, 45.799732f, 24.365997f,
				35.020733f, 60.313797f, 58.143764f, 7.373333f, 85.6522f, 14.278102f, 84.39436f, 94.222626f, 60.962875f, 43.30566f, 97.16539f,
				26.485134f, 90.14883f, 37.14376f, 74.48101f, 41.015823f, 0.7291734f, 84.09561f, 10.601288f, 29.13757f, 88.21279f, 4.556477f,
				8.017832f, 10.150682f, 63.09504f, 33.297222f, 88.41919f, 63.802956f, 61.808903f, 53.62247f, 26.545763f, 5.2461567f, 17.431969f,
				29.598595f, 67.60458f, 63.06038f, 27.018421f, 36.094944f, 34.831738f, 26.480871f, 21.57551f, 85.272835f, 18.970972f, 72.65681f,
				52.19031f, 60.06651f, 92.54235f, 24.565262f, 0.95137954f, 15.617472f, 37.02659f, 51.365738f, 21.480537f, 6.848174f, 65.27435f,
				27.093489f, 13.533127f, 60.507107f, 70.9133f, 52.547085f, 55.74544f, 82.01025f, 24.178337f, 6.235653f, 15.771127f, 77.981766f,
				43.906086f, 80.83203f, 47.384064f, 74.11108f));
	}

	private List<String> criaListaStrings() {
		return new ArrayList<String>(Arrays.asList(
				"a",
				"b",
				"c",
				"d",
				"e",
				"f",
				"g",
				"h",
				"i",
				"j",
				"k",
				"l",
				"m",
				"n",
				"o",
				"p",
				"q",
				"r",
				"s",
				"t",
				"u",
				"v",
				"w",
				"x",
				"y",
				"z"
				));
	}

	private List<Integer> criaListaInteiros() {
		return new ArrayList<Integer>(Arrays.asList(31, 13, 61, 31, 85, 30, 10, 38, 67, 37, 4, 82, 55, 61, 1, 1, 99, 98, 68, 19, 50, 71, 77, 70, 37,
				36, 65, 15, 81, 88, 97, 16, 9, 78, 34, 83, 41, 27, 27, 19, 77, 28, 46, 79, 9, 95, 58, 29, 97, 46, 86, 96, 50, 26, 53, 63, 28, 53, 39,
				57, 90, 15, 13, 94, 7, 73, 18, 28, 30, 6, 31, 73, 31, 46, 39, 21, 23, 21, 15, 52, 0, 48, 98, 8, 68, 38, 64, 27, 30, 39, 66, 0, 58,
				31, 57, 66, 4, 93, 69, 32));
	}

	private List<Pessoa> criaListaPessoas() {
		return new ArrayList<Pessoa>(Arrays.asList(
				new Pessoa(1, "Douglas", 22, 4, 3, true),
				new Pessoa(2, "Nathalia", 18, 4, 3, true),
				new Pessoa(3, "Renata", 49, 7, 8, true),
				new Pessoa(4, "Carlos", 50, 5, 6, true),
				new Pessoa(5, "Assis", 75, 0, 0, true),
				new Pessoa(6, "Juracy", 75, 0, 0, true),
				new Pessoa(7, "Luna", 75, 0, 0, true),
				new Pessoa(8, "Neide", 75, 0, 0, true),
				new Pessoa(9, "Ricardo", 40, 7, 8, true),
				new Pessoa(10, "Rosana", 45, 7, 8, true),
				new Pessoa(11, "Tania", 52, 5, 6, true),
				new Pessoa(12, "Luiz", 45, 5, 6, true),
				new Pessoa(13, "Luiz Jr.", 14, 12, 15, true),
				new Pessoa(14, "Carol", 20, 12, 15, true),
				new Pessoa(15, "Virginia", 57, 0, 0, true),
				new Pessoa(16, "Andressa", 21, 17, 10, true),
				new Pessoa(17, "Joao", 45, 0, 0, true),
				new Pessoa(18, "Nicole", 13, 17, 10, true),
				new Pessoa(19, "Felipe", 16, 9, 21, true),
				new Pessoa(20, "Vitor", 13, 9, 21, true),
				new Pessoa(21, "Eliane", 40, 0, 0, true),
				new Pessoa(22, "Vanessa", 30, 12, 15, true),
				new Pessoa(23, "Kaue", 11, 0, 22, true),
				new Pessoa(24, "Junior", 29, 12, 15, true),
				new Pessoa(25, "Elder", 22, 27, 28, true),
				new Pessoa(26, "Deniele", 24, 27, 28, true),
				new Pessoa(27, "Zica", 50, 0, 37, true),
				new Pessoa(28, "Denise", 45, 0, 36, true),
				new Pessoa(29, "Gabrielle", 21, 31, 32, true),
				new Pessoa(30, "Guilherme", 16, 31, 32, true),
				new Pessoa(31, "Cacaio", 48, 0, 35, true),
				new Pessoa(32, "Katia", 45, 34, 33, true),
				new Pessoa(33, "Marilene", 70, 0, 0, true),
				new Pessoa(34, "Paulo", 70, 0, 0, true),
				new Pessoa(35, "Dona Neide", 70, 0, 0, true),
				new Pessoa(36, "Almira", 70, 0, 0, true),
				new Pessoa(37, "Elza", 70, 0, 0, true),
				new Pessoa(38, "Diego", 22, 39, 40, true),
				new Pessoa(39, "Antonio", 70, 0, 0, true),
				new Pessoa(40, "Joelma", 45, 0, 0, true),
				new Pessoa(41, "Cruz", 45, 42, 43, true),
				new Pessoa(42, "Jorge", 45, 0, 0, true),
				new Pessoa(43, "Maria", 45, 0, 0, true),
				new Pessoa(44, "Jorginho", 45, 42, 43, true)
				));
	}
}
