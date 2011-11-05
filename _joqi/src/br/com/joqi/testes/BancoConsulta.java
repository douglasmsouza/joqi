package br.com.joqi.testes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.joqi.testes.modelo.Artista;
import br.com.joqi.testes.modelo.Emprestimo;
import br.com.joqi.testes.modelo.Disco;
import br.com.joqi.testes.modelo.Genero;
import br.com.joqi.testes.modelo.PessoaDisco;
import br.com.joqi.testes.modelo.Pessoa;

public class BancoConsulta {

	private List<Pessoa> pessoas;
	private List<PessoaDisco> pessoasDiscos;
	private List<Disco> discos;
	private List<Artista> artistas;
	private List<Genero> generos;

	public BancoConsulta() {
		pessoas = criaListaPessoas();
		pessoasDiscos = criaListaPessoasDiscos();
		discos = criaListaDiscos();
		artistas = criaListaColecoes();
		generos = criaListaGeneros();
	}

	private List<Genero> criaListaGeneros() {
		return new ArrayList<Genero>(Arrays.asList(
				new Genero(1, "Metal"),
				new Genero(2, "Reggae"),
				new Genero(3, "Pop")
				));
	}

	private List<Artista> criaListaColecoes() {
		return new ArrayList<Artista>(Arrays.asList(
				new Artista(1, "Black Sabbath", 1),
				new Artista(2, "Iron Maiden", 1),
				new Artista(3, "Metallica", 1),
				new Artista(4, "Judas Priest", 1),
				new Artista(5, "Motörhead", 1),
				new Artista(6, "Slayer", 1),
				new Artista(7, "Megadeth", 1),
				new Artista(8, "Venom", 1),
				new Artista(9, "Pantera", 1),
				new Artista(10, "Death", 1),
				new Artista(11, "Ozzy Osbourne", 1),
				new Artista(12, "Queensrcyhe", 1),
				new Artista(13, "Dream Theater", 1),
				new Artista(14, "Celtic Frost", 1),
				new Artista(15, "Manowar", 1),
				new Artista(16, "Dio", 1),
				new Artista(17, "Mercyful Fate", 1),
				new Artista(18, "Helloween", 1),
				new Artista(19, "Anthrax", 1),
				new Artista(20, "Bathory", 1),
				new Artista(21, "Napalm Death", 1),
				new Artista(22, "Carcass", 1),
				new Artista(23, "Alice in Chains", 1),
				new Artista(24, "Sepultura", 1),
				new Artista(25, "Mötley Crüe", 1),
				new Artista(26, "Scorpions", 1),
				new Artista(27, "Morbid Angel", 1),
				new Artista(28, "Tool", 1),
				new Artista(29, "Mayhem", 1),
				new Artista(30, "Korn", 1),
				new Artista(31, "Opeth", 1),
				new Artista(32, "Emperor", 1),
				new Artista(33, "Rainbow", 1),
				new Artista(34, "Soundgarden", 1),
				new Artista(35, "Exodus", 1),
				new Artista(36, "Possessed", 1),
				new Artista(37, "Blind Guardian", 1),
				new Artista(38, "Cannibal Corpse", 1),
				new Artista(39, "Testament", 1),
				new Artista(40, "Faith No More", 1),
				new Artista(41, "King Diamond", 1),
				new Artista(42, "Suicidal Tendencies", 1),
				new Artista(43, "Accept", 1),
				new Artista(44, "Fates Warning", 1),
				new Artista(45, "Overkill", 1),
				new Artista(46, "Symphony X", 1),
				new Artista(47, "Yngwie Malmsteen", 1),
				new Artista(48, "Kreator", 1),
				new Artista(49, "Deicide", 1),
				new Artista(50, "Burzum", 1),
				new Artista(51, "At the Gates", 1),
				new Artista(52, "System of a Down", 1),
				new Artista(53, "Candlemass", 1),
				new Artista(54, "Saxon", 1),
				new Artista(55, "Slipknot", 1),
				new Artista(56, "In Flames", 1),
				new Artista(57, "Rage Against the Machine", 1),
				new Artista(58, "Children of Bodom", 1),
				new Artista(59, "Voivod", 1),
				new Artista(60, "Machine Head", 1),
				new Artista(61, "Dimmu Borgir", 1),
				new Artista(62, "Diamond Head", 1),
				new Artista(63, "Neurosis", 1),
				new Artista(64, "Metal Church", 1),
				new Artista(65, "Savatage", 1),
				new Artista(66, "Type O Negative", 1),
				new Artista(67, "Darkthrone", 1),
				new Artista(68, "DRI", 1),
				new Artista(69, "My Dying Bride", 1),
				new Artista(70, "Sodom", 1),
				new Artista(71, "W.A.S.P.", 1),
				new Artista(72, "Immortal", 1),
				new Artista(73, "Iced Earth", 1),
				new Artista(74, "Twisted Sister", 1),
				new Artista(75, "Cradle of Filth", 1),
				new Artista(76, "Ministry", 1),
				new Artista(77, "Terrorizer", 1),
				new Artista(78, "Annihilator", 1),
				new Artista(79, "Danzig", 1),
				new Artista(80, "Dokken", 1),
				new Artista(81, "White Zombie", 1),
				new Artista(82, "HammerFall", 1),
				new Artista(83, "Gamma Ray", 1),
				new Artista(84, "Edge of Sanity", 1),
				new Artista(85, "St,Vitus", 1),
				new Artista(86, "Nevermore", 1),
				new Artista(87, "Pentagram", 1),
				new Artista(88, "Budgie", 1),
				new Artista(89, "Fear Factory", 1),
				new Artista(90, "Mastodon", 1),
				new Artista(91, "Meshuggah", 1),
				new Artista(92, "Entombed", 1),
				new Artista(93, "Lamb of God", 1),
				new Artista(94, "Suffocation", 1),
				new Artista(95, "Skid Row", 1),
				new Artista(96, "Raven", 1),
				new Artista(97, "Therion", 1),
				new Artista(98, "Trouble", 1),
				new Artista(99, "Stratovarius", 1),
				new Artista(100, "Atheist", 1)
				));
	}

	private List<Disco> criaListaDiscos() {
		return null;
	}

	private List<PessoaDisco> criaListaPessoasDiscos() {
		return null;
	}

	private List<Pessoa> criaListaPessoas() {
		return null;
	}

}
