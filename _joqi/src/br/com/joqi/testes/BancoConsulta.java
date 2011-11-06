package br.com.joqi.testes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.joqi.testes.modelo.Album;
import br.com.joqi.testes.modelo.Artista;
import br.com.joqi.testes.modelo.Genero;

public class BancoConsulta {

	private List<Album> albuns;
	private List<Artista> artistas;
	private List<Genero> generos;

	public BancoConsulta() {
		albuns = criaListaAlbuns();
		artistas = criaListaArtistas();
		generos = criaListaGeneros();
	}

	public List<Album> getAlbuns() {
		return albuns;
	}

	public void setAlbuns(List<Album> albuns) {
		this.albuns = albuns;
	}

	public List<Artista> getArtistas() {
		return artistas;
	}

	public void setArtistas(List<Artista> artistas) {
		this.artistas = artistas;
	}

	public List<Genero> getGeneros() {
		return generos;
	}

	public void setGeneros(List<Genero> generos) {
		this.generos = generos;
	}

	private List<Genero> criaListaGeneros() {
		return new ArrayList<Genero>(Arrays.asList(
				new Genero(1, "Metal"),
				new Genero(2, "Reggae")
				));
	}

	private List<Artista> criaListaArtistas() {
		return new ArrayList<Artista>(Arrays.asList(
				new Artista(1, "Black Sabbath", 1),
				new Artista(2, "Iron Maiden", 1),
				new Artista(3, "Metallica", 1),
				new Artista(4, "Judas Priest", 1),
				new Artista(5, "Mot�rhead", 1),
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
				new Artista(25, "M�tley Cr�e", 1),
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
				new Artista(100, "Atheist", 1),
				new Artista(101, "Natiruts", 2),
				new Artista(102, "Natural Dread Killaz", 2),
				new Artista(103, "Nayah", 2),
				new Artista(104, "Negril", 2),
				new Artista(105, "Negroots", 2),
				new Artista(106, "Neto Trindade", 2),
				new Artista(107, "No��o Rasta", 2),
				new Artista(108, "Nomad", 2),
				new Artista(109, "Nuevas Raices", 2),
				new Artista(110, "Onda R", 2),
				new Artista(111, "Pato Banton", 2),
				new Artista(112, "Patrice", 2),
				new Artista(113, "Peter Tosh", 2),
				new Artista(114, "Planet Roots", 2),
				new Artista(115, "Planta e Raiz", 2),
				new Artista(116, "Ponto De Equilibrio", 2),
				new Artista(117, "Praiera", 2),
				new Artista(118, "Prato Feito", 2),
				new Artista(119, "Rabo jah raia", 2),
				new Artista(120, "Raiz do Porto", 2),
				new Artista(121, "Raizes Rasta", 2),
				new Artista(122, "Raiztafari", 2),
				new Artista(123, "Rastaclone", 2),
				new Artista(124, "Rastafeeling", 2),
				new Artista(125, "Rebel Lion", 2),
				new Artista(126, "Reggalize", 2),
				new Artista(127, "Richie Spice", 2),
				new Artista(128, "S.O.J.A", 2),
				new Artista(129, "Semente Reggada", 2),
				new Artista(130, "Sensimilla Dub", 2),
				new Artista(131, "Shamanes", 2),
				new Artista(132, "Sine Calmon", 2),
				new Artista(133, "Sins�milia", 2),
				new Artista(134, "Sizzla", 2),
				new Artista(135, "Solarise", 2),
				new Artista(136, "Soldiers of Jah Army", 2),
				new Artista(137, "Steel Pulse", 2),
				new Artista(138, "S�ditos do Reggae", 2),
				new Artista(139, "Tafari Roots", 2),
				new Artista(140, "Tanya Stephens", 2),
				new Artista(141, "Temptations", 2),
				new Artista(142, "The Gladiators", 2),
				new Artista(143, "The Skatalites", 2),
				new Artista(144, "To Fly", 2),
				new Artista(145, "Tommy Torres", 2),
				new Artista(146, "Toots And The Maytals", 2),
				new Artista(147, "Tribo de Jah", 2),
				new Artista(148, "Trilhas e Ra�zes", 2),
				new Artista(149, "Trio Carcar�", 2),
				new Artista(150, "Tryo", 2),
				new Artista(151, "Tupi e Nag�", 2),
				new Artista(152, "UB40", 2),
				new Artista(153, "Usina Reggae", 2),
				new Artista(154, "Vibra��o Nativa", 2),
				new Artista(155, "Vibra��es Rasta", 2),
				new Artista(156, "X Rabbits", 2),
				new Artista(157, "Yellowman", 2),
				new Artista(158, "YoManaHo", 2),
				new Artista(159, "Zach Ashton", 2),
				new Artista(160, "Zandaroots", 2),
				new Artista(161, "Ziggy Marley", 2),
				new Artista(162, "", 2),
				new Artista(163, "Edson Gomes", 2),
				new Artista(164, "Edu Ribeiro", 2),
				new Artista(165, "Ekolu", 2),
				new Artista(166, "Express�o Regueira", 2),
				new Artista(167, "Filhos Do Totem", 2),
				new Artista(168, "Filosofia Reggae", 2),
				new Artista(169, "For�as do Gueto", 2),
				new Artista(170, "Gabi e Os Manos de Jah", 2),
				new Artista(171, "Gentleman", 2),
				new Artista(172, "Gondwana", 2),
				new Artista(173, "Gregory Isaacs", 2),
				new Artista(174, "Groundation", 2),
				new Artista(175, "Horace Andy", 2),
				new Artista(176, "Iri�", 2),
				new Artista(177, "Israel Vibration", 2),
				new Artista(178, "Jaafa Reggae", 2),
				new Artista(179, "Jacob Miller", 2),
				new Artista(180, "Jah Cure", 2),
				new Artista(181, "Jah I Ras", 2),
				new Artista(182, "Jah Live", 2),
				new Artista(183, "Javaroots", 2),
				new Artista(184, "Jimmy Cliff", 2),
				new Artista(185, "Johnny Jack Mesclado", 2),
				new Artista(186, "Julian Marley", 2),
				new Artista(187, "Ka'au Crater Boys", 2),
				new Artista(188, "Kafu Banton", 2),
				new Artista(189, "Kana", 2),
				new Artista(190, "Kanabaus", 2),
				new Artista(191, "Kaymakan", 2),
				new Artista(192, "Khriz y Angel", 2),
				new Artista(193, "King Chango", 2),
				new Artista(194, "La Verdolaga", 2),
				new Artista(195, "Larry Marshall", 2),
				new Artista(196, "Le�o de Judah", 2),
				new Artista(197, "Lee Perry", 2),
				new Artista(198, "Le�es de Israel", 2),
				new Artista(199, "Libertus", 2),
				new Artista(200, "Lion Jump", 2),
				new Artista(201, "Los Baganas", 2),
				new Artista(202, "Los Cafres", 2),
				new Artista(203, "Los Pericos", 2),
				new Artista(204, "Luau�", 2),
				new Artista(205, "Luciano", 2),
				new Artista(206, "Macucos", 2),
				new Artista(207, "Mama Quilla", 2),
				new Artista(208, "Mano Bantu", 2),
				new Artista(209, "Marcinho do Surf", 2),
				new Artista(210, "Marlon Asher", 2),
				new Artista(211, "Maskavo", 2),
				new Artista(212, "Matisyahu", 2),
				new Artista(213, "Mato Seco", 2),
				new Artista(214, "Maverick", 2),
				new Artista(215, "Max Romeo", 2),
				new Artista(216, "Medida Salvadora", 2),
				new Artista(217, "Michael Franti & Spearhead", 2),
				new Artista(218, "Midnite", 2),
				new Artista(219, "Morgan Heritage", 2),
				new Artista(220, "Mosaico", 2),
				new Artista(221, "Mosiah Roots", 2),
				new Artista(222, "Mystical Roots", 2),
				new Artista(223, "Na��o Forreggae", 2),
				new Artista(224, "", 2),
				new Artista(225, "2face Idibia", 2),
				new Artista(226, "A Cor do Reggae", 2),
				new Artista(227, "Abyssinians", 2),
				new Artista(228, "Ac�stico Reggae", 2),
				new Artista(229, "Ad�o Negro", 2),
				new Artista(230, "Afrodizia", 2),
				new Artista(231, "Afroman", 2),
				new Artista(232, "Akundum", 2),
				new Artista(233, "Alma D'Jem", 2),
				new Artista(234, "Alpha Blondy", 2),
				new Artista(235, "Anthony B.", 2),
				new Artista(236, "Anti-Babylon", 2),
				new Artista(237, "Ant�nio Brother", 2),
				new Artista(238, "Armandinho", 2),
				new Artista(239, "Augustus Pablo", 2),
				new Artista(240, "Ayakay�", 2),
				new Artista(241, "Ayo", 2),
				new Artista(242, "Bahiano", 2),
				new Artista(243, "Bai�o de Quatro", 2),
				new Artista(244, "Banda Nayah", 2),
				new Artista(245, "Beca Arruda", 2),
				new Artista(246, "Ben Roots", 2),
				new Artista(247, "Bennie Man", 2),
				new Artista(248, "Beres Hammond", 2),
				new Artista(249, "Bernard Lavilliers", 2),
				new Artista(250, "Black Uhuru", 2),
				new Artista(251, "Bob Marley", 2),
				new Artista(252, "Bob Marley & Lauryn Hill", 2),
				new Artista(253, "Bob Marley & The Wailers", 2),
				new Artista(254, "Bob Sinclar", 2),
				new Artista(255, "Bounty Killer", 2),
				new Artista(256, "Brisa Verde", 2),
				new Artista(257, "Brownman Revival", 2),
				new Artista(258, "Buju Banton", 2),
				new Artista(259, "Burning Spear", 2),
				new Artista(260, "Bushman", 2),
				new Artista(261, "Butch Helemano", 2),
				new Artista(262, "Canamar�", 2),
				new Artista(263, "Capleton", 2),
				new Artista(264, "Casaca", 2),
				new Artista(265, "Chaka Demus & Pliers", 2),
				new Artista(266, "Chicken Power", 2),
				new Artista(267, "Chimarruts", 2),
				new Artista(268, "Christafari", 2),
				new Artista(269, "Cidade Negra", 2),
				new Artista(270, "Circulo Rasta", 2),
				new Artista(271, "Cultura Profetica", 2),
				new Artista(272, "Culture", 2),
				new Artista(273, "Da'ville", 2),
				new Artista(274, "Damian Marley", 2),
				new Artista(275, "Dazaranha", 2),
				new Artista(276, "Dezarie", 2),
				new Artista(277, "Diamba", 2),
				new Artista(278, "Digo Cardkamp", 2),
				new Artista(279, "Djamborii Roots", 2),
				new Artista(280, "Don Carlos", 2),
				new Artista(281, "Dona Leda", 2),
				new Artista(282, "Dread Lion", 2),
				new Artista(283, "Dread Roots", 2),
				new Artista(284, "Dreadlock", 2),
				new Artista(285, "Dred Nox", 2),
				new Artista(286, "Du'Casco", 2),
				new Artista(287, "Easy Star All Stars", 2)
				));
	}

	private List<Album> criaListaAlbuns() {
		return new ArrayList<Album>(Arrays.asList(
				new Album(1, 1, " Paranoid"),
				new Album(1, 2, " Master of Reality"),
				new Album(1, 3, " Black Sabbath, Vol. 4"),
				new Album(1, 4, " Sabbath Bloody Sabbath"),
				new Album(1, 5, " Sabotage"),
				new Album(1, 6, " Technical Ecstasy"),
				new Album(1, 7, " Never Say Die!"),
				new Album(1, 8, " Heaven and Hell"),
				new Album(1, 9, " Mob Rules"),
				new Album(1, 10, " Born Again"),
				new Album(1, 11, " Seventh Star"),
				new Album(1, 12, " The Eternal Idol"),
				new Album(1, 13, " Headless Cross"),
				new Album(1, 14, " Tyr"),
				new Album(1, 15, " Dehumanizer"),
				new Album(1, 16, " Cross Purposes"),
				new Album(1, 17, " Forbidden"),
				new Album(2, 1, "Iron Maiden"),
				new Album(2, 2, "Killers"),
				new Album(2, 3, "The Number of the Beast"),
				new Album(2, 4, "Piece of Mind"),
				new Album(2, 5, "Powerslave"),
				new Album(2, 6, "Somewhere in Time"),
				new Album(2, 7, "Seventh Son of a Seventh Son"),
				new Album(2, 8, "No Prayer for the Dying"),
				new Album(2, 9, "Fear of the Dark"),
				new Album(2, 10, "The X Factor"),
				new Album(2, 11, "Virtual XI"),
				new Album(2, 12, "Brave New World"),
				new Album(2, 13, "Dance of Death"),
				new Album(2, 14, "A Matter of Life and Death"),
				new Album(2, 15, "The Final Frontier"),
				new Album(3, 1, "Kill 'Em All"),
				new Album(3, 2, "Ride the Lightning"),
				new Album(3, 3, "Master of Puppets"),
				new Album(3, 4, "...And Justice for All"),
				new Album(3, 5, "Metallica"),
				new Album(3, 6, "Load"),
				new Album(3, 7, "ReLoad"),
				new Album(3, 8, "St. Anger"),
				new Album(3, 9, "Death Magnetic"),
				new Album(4, 1, "Rocka Rolla"),
				new Album(4, 2, "Sad Wings of Destiny"),
				new Album(4, 3, "Sin After Sin"),
				new Album(4, 4, "Stained Class"),
				new Album(4, 5, "Killing Machine"),
				new Album(4, 6, "British Steel"),
				new Album(4, 7, "Point of Entry"),
				new Album(4, 8, "Screaming for Vengeance"),
				new Album(4, 9, "Defenders of the Faith"),
				new Album(4, 10, "Turbo"),
				new Album(4, 11, "Ram It Down"),
				new Album(4, 12, "Painkiller"),
				new Album(4, 13, "Jugulator"),
				new Album(4, 14, "Demolition"),
				new Album(4, 15, "Angel of Retribution"),
				new Album(4, 16, "Nostradamus"),
				new Album(5, 1, "On Parole"),
				new Album(5, 2, "Mot�rhead"),
				new Album(5, 3, "Overkill"),
				new Album(5, 4, "Bomber"),
				new Album(5, 5, "Ace of Spades"),
				new Album(5, 6, "Iron Fist"),
				new Album(5, 7, "Another Perfect Day"),
				new Album(5, 8, "Orgasmatron"),
				new Album(5, 9, "Rock 'n' Roll"),
				new Album(5, 10, "1916"),
				new Album(5, 11, "March �r Die"),
				new Album(5, 12, "Bastards"),
				new Album(5, 13, "Sacrifice"),
				new Album(5, 14, "Overnight Sensation"),
				new Album(5, 15, "Snake Bite Love"),
				new Album(5, 16, "We Are Mot�rhead"),
				new Album(5, 17, "Hammered"),
				new Album(5, 18, "Inferno"),
				new Album(5, 19, "Kiss of Death"),
				new Album(5, 20, "Mot�rizer"),
				new Album(5, 21, "The W�rld Is Yours"),
				new Album(6, 1, "Show No Mercy (1983)"),
				new Album(6, 2, "Hell Awaits (1985)"),
				new Album(6, 3, "Reign in Blood (1986)"),
				new Album(6, 4, "South of Heaven (1988)"),
				new Album(6, 5, "Seasons in the Abyss (1990)"),
				new Album(6, 6, "Divine Intervention (1994)"),
				new Album(6, 7, "Undisputed Attitude (1996)"),
				new Album(6, 8, "Diabolus in Musica (1998)"),
				new Album(6, 9, "God Hates Us All (2001)"),
				new Album(6, 10, "Christ Illusion (2006)"),
				new Album(6, 11, "World Painted Blood (2009)"),
				new Album(7, 1, "Killing Is My Business... and Business Is Good!"),
				new Album(7, 2, "Peace Sells... but Who's Buying?"),
				new Album(7, 3, "So Far, So Good... So What!"),
				new Album(7, 4, "Rust in Peace"),
				new Album(7, 5, "Countdown to Extinction"),
				new Album(7, 6, "Youthanasia"),
				new Album(7, 7, "Cryptic Writings"),
				new Album(7, 8, "Risk"),
				new Album(7, 9, "The World Needs a Hero"),
				new Album(7, 10, "The System Has Failed"),
				new Album(7, 11, "United Abominations"),
				new Album(7, 12, "Endgame"),
				new Album(7, 13, "TH1RT3EN"),
				new Album(8, 1, "Welcome To Hell (1981)"),
				new Album(8, 2, "Black Metal (1982)"),
				new Album(8, 3, "At War With Satan (1984)"),
				new Album(8, 4, "Possessed (1985)"),
				new Album(8, 5, "Calm Before the Storm (1987)"),
				new Album(8, 6, "Prime Evil (1989)"),
				new Album(8, 7, "Temples of Ice (1991)"),
				new Album(8, 8, "The Waste Lands (1992)"),
				new Album(8, 9, "Cast in Stone (1997)"),
				new Album(8, 10, "Resurrection (2000)"),
				new Album(8, 11, "Metal Black (2006)"),
				new Album(8, 12, "Hell (2008)"),
				new Album(8, 13, "Fallen Angels (2011)"),
				new Album(9, 1, "Metal Magic"),
				new Album(9, 2, "Projects in the Jungle"),
				new Album(9, 3, "I Am the Night"),
				new Album(9, 4, "Power Metal"),
				new Album(9, 5, "Cowboys from Hell"),
				new Album(9, 6, "Vulgar Display of Power"),
				new Album(9, 7, "Far Beyond Driven"),
				new Album(9, 8, "The Great Southern Trendkill"),
				new Album(9, 9, "Official Live: 101 Proof"),
				new Album(9, 10, "Reinventing the Steel"),
				new Album(9, 11, "The Best of Pantera: Far Beyond the Great Southern Cowboys' Vulgar Hits!"),
				new Album(9, 12, "Reinventing Hell: The Best of Pantera"),
				new Album(9, 13, "1990-2000: A Decade Of Domination"),
				new Album(10, 1, "Scream Bloody Gore 	1987"),
				new Album(10, 2, "Leprosy 	1988"),
				new Album(10, 3, "Spiritual Healing 	1990"),
				new Album(10, 4, "Human 	1991"),
				new Album(10, 5, "Fate: The Best Of Death 	1992"),
				new Album(10, 6, "Individual Thought Patterns 	1993"),
				new Album(10, 7, "Symbolic 	1995"),
				new Album(10, 8, "The Sound Of Perseverance 	1998"),
				new Album(10, 9, "Live In LA (Death & Raw) 	2001"),
				new Album(10, 10, "Live in Eindhoven 	2001"),
				new Album(11, 1, "Blizzard of Ozz (1980)"),
				new Album(11, 2, "Diary of a Madman (1981)"),
				new Album(11, 3, "Bark at The Moon (1983)"),
				new Album(11, 4, "The Ultimate Sin (1986)"),
				new Album(11, 5, "No Rest for the Wicked (1988)"),
				new Album(11, 6, "No More tears (1991)"),
				new Album(11, 7, "Ozzmosis (1995)"),
				new Album(11, 8, "Down to Earth (2001)"),
				new Album(11, 9, "Under Cover (2005)"),
				new Album(11, 10, "Black Rain (2007)"),
				new Album(11, 11, "Scream (2010)"),
				new Album(251, 1, "The Wailing Wailers"),
				new Album(251, 2, "Soul Rebels"),
				new Album(251, 3, "Soul Revolution"),
				new Album(251, 4, "Soul Revolution Part II"),
				new Album(251, 5, "Best of The Wailers"),
				new Album(251, 6, "Catch a Fire"),
				new Album(251, 7, "Burnin'"),
				new Album(251, 8, "Natty Dread"),
				new Album(251, 9, "Rastaman Vibration"),
				new Album(251, 10, "Exodus"),
				new Album(251, 11, "Kaya"),
				new Album(251, 12, "Survival"),
				new Album(251, 13, "Uprising"),
				new Album(251, 14, "Confrontation"),
				new Album(157, 1, "Them A Mad Over Me (1981, Channel One Records)"),
				new Album(157, 2, "Mister Yellowman (1982)"),
				new Album(157, 3, "King Mellow Yellow Meets Yellowman (1982, Jam Rock Records)"),
				new Album(157, 4, "Duppy Or Gunman (1982, Volcano Records)"),
				new Album(157, 5, "Supermix (1982, Volcano Records)"),
				new Album(157, 6, "Bad Boy Skanking (1982, Greensleeves Records)"),
				new Album(157, 7, "The Yellow, The Purple & The Nancy (1982, Greensleeves Records)"),
				new Album(157, 8, "Live At Aces (1982, VP Records)"),
				new Album(157, 9, "Jack Sprat (1982, GG's Records)"),
				new Album(157, 10, "One Yellowman (1982, Hitbound Records)"),
				new Album(157, 11, "Divorced (For Your Eyes Only) (1982)"),
				new Album(157, 12, "Superstar Yellowman Has Arrived With Toyan (1982)"),
				new Album(157, 13, "Zungguzungguguzungguzeng (1983)"),
				new Album(157, 14, "Live At Kilamanjaro (1983, Hawkeye Records)"),
				new Album(157, 15, "King Yellowman (1983)"),
				new Album(157, 16, "Nobody Move, Nobody Get Hurt (1984)"),
				new Album(157, 17, "Operation Radication (1984, Top 1000 Records)"),
				new Album(157, 18, "Galong Galong Galong (1985)"),
				new Album(157, 19, "Blueberry Hill (1987)"),
				new Album(157, 20, "Yellow Like Cheese (1987)"),
				new Album(157, 21, "Yellowman Rides Again (1988)"),
				new Album(157, 22, "A Feast of Yellow Dub (1990)"),
				new Album(157, 23, "Party (1991)"),
				new Album(157, 24, "One In A Million (1991, Shanachie Records)"),
				new Album(157, 25, "Reggae on the Move (1992)"),
				new Album(157, 26, "Prayer (1994)"),
				new Album(157, 27, "Message to the World (1995)"),
				new Album(157, 28, "Freedom of Speech (1997)"),
				new Album(157, 29, "RAS Portraits � Yellowman (1997)"),
				new Album(157, 30, "A Very, Very, Yellow Christmas (1998)"),
				new Album(157, 31, "Yellow Fever (1999)"),
				new Album(157, 32, "New York (2003)"),
				new Album(157, 33, "Just Cool (2004)"),
				new Album(157, 34, "Round 1 (2005)"),
				new Album(113, 1, "Legalize It (1976)"),
				new Album(113, 2, "Equal Rights (1977)"),
				new Album(113, 3, "Bush Doctor (1978)"),
				new Album(113, 4, "Mystic Man (1979)"),
				new Album(113, 5, "Wanted Dread And Alive (1981)"),
				new Album(113, 6, "Mama Africa (1983)"),
				new Album(113, 7, "Captured Live (1984)"),
				new Album(113, 8, "No Nuclear War (1987)"),
				new Album(142, 1, "Trenchtown Mix Up 	1976"),
				new Album(142, 2, "Studio One Presenting the Gladiators 	1977"),
				new Album(142, 3, "Proverbial Reggae 	1978"),
				new Album(142, 4, "Naturality 	1979"),
				new Album(142, 5, "Sweet So Till 	1979"),
				new Album(142, 6, "Gladiators 	1980"),
				new Album(142, 7, "Babylon Street 	1982"),
				new Album(142, 8, "Back To Roots 	1982"),
				new Album(142, 9, "Symbol of Reality 	1982"),
				new Album(142, 10, "Reggae To Bone 	1982"),
				new Album(142, 11, "Serious Thing 	1984"),
				new Album(142, 12, "Show Down Vol. 3 w/ Don Carlos & Gold 	1984"),
				new Album(142, 13, "Country Living 	1985"),
				new Album(142, 14, "Dread Prophesy w/ The Ethiopians 	1986"),
				new Album(142, 15, "In Store For You 	1988"),
				new Album(142, 16, "On The Right Track 	1989"),
				new Album(142, 17, "Valley of Decision 	1991"),
				new Album(142, 18, "A True Rastaman 	1992"),
				new Album(142, 19, "The Storm 	1994"),
				new Album(142, 20, "Something a Gwaan 	2000"),
				new Album(142, 21, "Once Upon A Time In Jamaica 	2002"),
				new Album(142, 22, "Fathers and Sons 	2005"),
				new Album(142, 23, "Continuation 	2009"),
				new Album(234, 1, "Jah Glory"),
				new Album(234, 2, "Cocody Rock!!!"),
				new Album(234, 3, "Apartheid Is Nazism"),
				new Album(234, 4, "Jerusalem (featuring The Wailers)"),
				new Album(234, 5, "Revolution"),
				new Album(234, 6, "The Prophets"),
				new Album(234, 7, "Masada"),
				new Album(234, 8, "SOS Guerres Tribales"),
				new Album(234, 9, "Live Au Z�nith (Paris)"),
				new Album(234, 10, "Dieu"),
				new Album(234, 11, "Grand Bassam Zion Rock"),
				new Album(234, 12, "Best Of"),
				new Album(234, 13, "Yitzhak Rabin"),
				new Album(234, 14, "Elohim"),
				new Album(234, 15, "Blondy Paris Bercy"),
				new Album(234, 16, "Merci"),
				new Album(234, 17, "Akwaba"),
				new Album(234, 18, "Jah Victory"),
				new Album(234, 19, "I Wish You Were Here"),
				new Album(234, 20, "Vision"),
				new Album(163, 1, "Reggae Resist�ncia (1988)"),
				new Album(163, 2, "Rec�ncavo (1990)"),
				new Album(163, 3, "Campo de Batalha (1992)"),
				new Album(163, 4, "Resgate Fatal (1995)"),
				new Album(163, 5, "Meus Momentos (Colet�nea) (1997)"),
				new Album(163, 6, "Apocalipse (1999)"),
				new Album(163, 7, "S�rie Bis (Colet�nea) (2000)"),
				new Album(163, 8, "Acorde, Levante e Lute (2001)"),
				new Album(163, 9, "Ao Vivo em Salvador (2006, CD e DVD ao vivo)."),
				new Album(174, 1, "Young Tree"),
				new Album(174, 2, "Each One Teach One"),
				new Album(174, 3, "Hebron Gate"),
				new Album(174, 4, "We Free Again"),
				new Album(174, 5, "Dub Wars"),
				new Album(174, 6, "Upon the Bridge"),
				new Album(174, 7, "Rockamovya"),
				new Album(174, 8, "Here I Am"),
				new Album(174, 9, "Gathering of the Elders"),
				new Album(184, 1, "Hard Road to Travel (1968)"),
				new Album(184, 2, "Jimmy Cliff (1969)"),
				new Album(184, 3, "Wonderful World, Beautiful People (1970)"),
				new Album(184, 4, "Another Cycle (1971)"),
				new Album(184, 5, "The Harder They Come (1972)"),
				new Album(184, 6, "Unlimited (1973)"),
				new Album(184, 7, "Struggling Man (1974)"),
				new Album(184, 8, "House of Exile (1974)"),
				new Album(184, 9, "Brave Warrior (1975)"),
				new Album(184, 10, "Follow My Mind (1975)"),
				new Album(184, 11, "In Concert: The Best of Jimmy Cliff (1976)"),
				new Album(184, 12, "Give Thanx (1978)"),
				new Album(184, 13, "I Am The Living (1980)"),
				new Album(184, 14, "Give the People What They Want (1981)"),
				new Album(184, 15, "Special (1982)"),
				new Album(184, 16, "The Power and the Glory (1983)"),
				new Album(184, 17, "Cliff Hanger (1985)"),
				new Album(184, 18, "Club Paradise (1986)"),
				new Album(184, 19, "Hanging Fire (Mar�o 1988)"),
				new Album(184, 20, "Images (Outubro 1989)"),
				new Album(184, 21, "Save Our Planet Earth (1990)"),
				new Album(184, 22, "Higher and Higher (1998)"),
				new Album(184, 23, "Humanitarian (1999)"),
				new Album(184, 24, "Fantastic Plastic People (2002)"),
				new Album(184, 25, "Black Magic (2004)")
				));
	}
}
