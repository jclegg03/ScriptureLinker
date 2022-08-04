package linker.model;

import java.util.ArrayList;

public class Linker
{
	private String scripture;
	private Collection dAndC;
	private Collection pOfGP;
	private Collection bOfM;
	private Collection oldT;
	private Collection newT;
	private String link;
	
	public Linker(String scripture)
	{
		this.scripture = scripture;
		this.link = "";
		
		setupScriptures();
	}
	
	public Linker()
	{
		setupScriptures();
	}
	
	public void setScripture(String scripture)
	{
		this.scripture = scripture;
	}
	
	public String proscessScripture()
	{
		link = "Scripture not found. Exapmle: \"1 John 3:17-21\"";
		
		scripture = scripture.replaceAll(" ", "");
		
		dissectScripture();
		
		return link;
	}
	
	public ArrayList<String> getHelp(String scripture)
	{
		ArrayList<String> abbreviations = new ArrayList<String>();
		
		Collection collection;
		
		if(dAndC.getBook(scripture) != null)
		{
			collection = dAndC;
		}
		else if(pOfGP.getBook(scripture) != null)
		{
			collection = pOfGP;
		}
		else if(bOfM.getBook(scripture) != null)
		{
			collection = bOfM;
		}
		else if(oldT.getBook(scripture) != null)
		{
			collection = oldT;
		}
		else if(newT.getBook(scripture) != null)
		{
			collection = newT;
		}
		else
		{
			abbreviations.add("The book \"" + scripture + "\" was not found.");
			return abbreviations;
		}
		
		abbreviations = collection.getBook(scripture).getAbbreviations();
		
		return abbreviations;
	}
	
	private void dissectScripture()
	{
		char bookNum;
		if(scripture.length() > 0)
		{
			bookNum = scripture.charAt(0);
		}
		else
		{
			link = "No scripture provided.";
			return;
		}
		
		scripture = scripture.substring(1);
		String[] letters = scripture.split("");
		
		int index = 0;
		try
		{
			while(! Character.isDigit(letters[index].charAt(0)) && index < letters.length - 1)
			{
				index++;
			}
		}
		catch(StringIndexOutOfBoundsException oneLetterError)
		{
			return;
		}
		
		String name = bookNum + scripture.substring(0, index);
		
		int endIndex = scripture.indexOf(":");
		int chapter;
		if(endIndex != -1)
		{
			try
			{
				chapter = Integer.parseInt(scripture.substring(index, endIndex));
			}
			catch(NumberFormatException badChapter)
			{
				link = "Your chapter can't have a letter in it. Exapmle: \"D&C 2:3\"";
				return;
			}
			catch(StringIndexOutOfBoundsException badScripture)
			{
				link = "You don't have a chapter.";
				return;
			}
		}
		else
		{
			try
			{
				chapter = Integer.parseInt(scripture.substring(index));
			}
			catch(NumberFormatException badChapter)
			{
				link = "The scripture must end with a number. Exapmle: \"D&C 2\"";
				return;
			}
			buildLink(name, chapter, 0, 0);
			return;
		}
		
		int startVerse = 0;
		int endVerse = 0;
		
		if(scripture.contains(","))
		{
			buildComplexLink(name, chapter, scripture.substring(endIndex + 1));
			return;
		}
		else if(scripture.contains("-"))
		{
			int dashIndex = scripture.indexOf("-");
			
			try
			{
				startVerse = Integer.parseInt(scripture.substring(endIndex + 1, dashIndex));
			}
			catch(NumberFormatException badStartVerse)
			{
				link = "Your starting verse can't have a letter in it.";
				return;
			}
			
			try
			{
				endVerse = Integer.parseInt(scripture.substring(dashIndex + 1));
			}
			catch(NumberFormatException badEndVerse)
			{
				link = "Your ending verse can't have a letter in it.";
				return;
			}
		}
		else
		{
			try
			{
				startVerse = Integer.parseInt(scripture.substring(endIndex + 1));
			}
			catch(NumberFormatException badStartVerse)
			{
				link = "Your verse can't have a letter in it.";
				return;
			}
		}
		
		if(startVerse > endVerse && endVerse != 0)
		{
			int temp = startVerse;
			startVerse = endVerse;
			endVerse = temp;
		}
		else if(startVerse == endVerse) endVerse = 0;
		
		buildLink(name, chapter, startVerse, endVerse);
	}
	
	private void buildLink(String name, int chapter, int startVerse, int endVerse)
	{
		Collection collection;
		Book book;
		
		if(dAndC.getBook(name) != null)
		{
			collection = dAndC;
		}
		else if(pOfGP.getBook(name) != null)
		{
			collection = pOfGP;
		}
		else if(bOfM.getBook(name) != null)
		{
			collection = bOfM;
		}
		else if(oldT.getBook(name) != null)
		{
			collection = oldT;
		}
		else if(newT.getBook(name) != null)
		{
			collection = newT;
		}
		else
		{
			link = "The book \"" + name + "\" was not found.";
			return;
		}
		
		book = collection.getBook(name);
		
		if(book.getChapters() < chapter || chapter <= 0)
		{
			link = "The book \"" + name + "\" doesn't have a chapter " + chapter + ". It has " + book.getChapters() + " chapters.";
			return;
		}
		
		if(startVerse == 0)
		{
			link = "lds.org/scriptures" + collection.getLink() + book.getLink() + chapter;
		}
		else if(endVerse == 0)
		{
			if(startVerse == 1)
			{
				link = "lds.org/scriptures" + collection.getLink() + book.getLink() + chapter + "." + startVerse + "#" + startVerse;
				 return;
			}
			link = "lds.org/scriptures" + collection.getLink() + book.getLink() + chapter + "." + startVerse + "#" + (startVerse - 1);
		}
		else
		{
			if(startVerse == 1)
			{
				link = "lds.org/scriptures" + collection.getLink() + book.getLink() + chapter + "." + startVerse + "-" + endVerse + "#" + startVerse;
				 return;
			}
			link = "lds.org/scriptures" + collection.getLink() + book.getLink() + chapter + "." + startVerse + "-" + endVerse + "#" + (startVerse - 1);
		}
	}

	private void buildComplexLink(String name, int chapter, String remaining)
	{
		Collection collection;
		Book book;
		String baseLink = "lds.org/scriptures";
		
		if(dAndC.getBook(name) != null)
		{
			collection = dAndC;
		}
		else if(pOfGP.getBook(name) != null)
		{
			collection = pOfGP;
		}
		else if(bOfM.getBook(name) != null)
		{
			collection = bOfM;
		}
		else if(oldT.getBook(name) != null)
		{
			collection = oldT;
		}
		else if(newT.getBook(name) != null)
		{
			collection = newT;
		}
		else
		{
			link = "The book \"" + name + "\" was not found.";
			return;
		}
		
		book = collection.getBook(name);
		
		baseLink += collection.getLink() + book.getLink() + chapter + ".";
		
		String[] verses = remaining.split(",");
		for(String verse : verses)
		{
			int startVerse = 0;
			int endVerse = 0;
			verse.replaceAll(",", "");
			
			if(verse.contains("-"))
			{
				try
				{
					int index = verse.indexOf("-");
					startVerse = Integer.parseInt(verse.substring(0, index));
					endVerse = Integer.parseInt(verse.substring(index + 1));
				}
				catch(NumberFormatException badVerse)
				{
					link = "Your verse \"" + verse + "\" isn't formated correctly.";
				}
			}
			else
			{
				try
				{
					startVerse = Integer.parseInt(verse.substring(0, 1));
				}
				catch(NumberFormatException badVerse)
				{
					link = "Your verse \"" + verse + "\" isn't formated correctly.";
					return;
				}
			}
			
			if(startVerse > endVerse && endVerse != 0)
			{
				int temp = startVerse;
				startVerse = endVerse;
				endVerse = temp;
				
				baseLink += startVerse + "-" + endVerse;
			}
			else if(startVerse == endVerse && endVerse != 0)
			{
				baseLink += startVerse + "";
			}
			else if(endVerse == 0)
			{
				baseLink += startVerse + "";
			}
			else if(startVerse < endVerse)
			{
				baseLink += startVerse + "-" + endVerse;
			}
			else
			{
				
			}
			
			baseLink += ",";
		}
		
		baseLink = baseLink.substring(0, baseLink.length() - 1);
		
		link = baseLink;
	}
	
	private void setupScriptures()
	{
		dAndC = new Collection("Doctrine and Covenants", 2, "/dc-testament");
		pOfGP = new Collection("Pearl of Great Price", 6, "/pgp");
		bOfM = new Collection("Book of Mormon", 15, "/bofm");
		oldT = new Collection("Old Testament", 39, "/ot");
		newT = new Collection("New Testament", 27, "/nt");
		
		dAndC();
		pOfGP();
		bOfM();
		oldT();
		newT();
	}
	
	private void dAndC()
	{
		String[] abbreviations = {"D&C", "DC", "DandC"};
		Book doctrine = new Book("Doctrine and Covenants", 138, abbreviations, "/dc/");
		
		Book declarations = new Book("Official Declarations", 2, "/od/");
		declarations.addAbbreviation("OD");
		declarations.addAbbreviation("Official Declaration");
		
		dAndC.addBook(doctrine, 0);
		dAndC.addBook(declarations, 1);
	}
	
	private void pOfGP()
	{
		String[] abbreviations = {"abr", "abrhm", "abrham", "aberham"};
		Book abraham = new Book("Abraham", 5, abbreviations, "/abr/");
		
		Book moses = new Book("Moses", 8, "/moses/");
		moses.addAbbreviation("mos");
		
		Book facsimilies = new Book("facsimiles", 3, "/abr/fac-");
		facsimilies.addAbbreviation("fac");
		facsimilies.addAbbreviation("facsimile");
		
		Book matthew = new Book("Joseph Smith - Matthew", 1, "/js-m/");
		matthew.addAbbreviation("JSM");
		matthew.addAbbreviation("Joseph Smith Matthew");
		matthew.addAbbreviation("JS matthew");
		matthew.addAbbreviation("JS matt");
		matthew.addAbbreviation("JS mat");
		matthew.addAbbreviation("JS-m");
		
		Book history = new Book("Joseph Smith - History", 1, "/js-h/");
		history.addAbbreviation("JSH");
		history.addAbbreviation("JS-H");
		history.addAbbreviation("Joseph Smith History");
		history.addAbbreviation("JSHis");
		history.addAbbreviation("JSHist");
		
		Book faith = new Book("Articles of Faith", 1, "/a-of-f/");
		faith.addAbbreviation("aof");
		faith.addAbbreviation("aoff");
		faith.addAbbreviation("af");
		faith.addAbbreviation("art of faith");
		faith.addAbbreviation("article of faith");
		faith.addAbbreviation("faith");
		
		pOfGP.addBook(moses, 0);
		pOfGP.addBook(abraham, 1);
		pOfGP.addBook(facsimilies, 2);
		pOfGP.addBook(matthew, 3);
		pOfGP.addBook(history, 4);
		pOfGP.addBook(faith, 5);
	}
	
	private void bOfM()
	{
		String[] abbreviations = {"ne", "nph", "nphi", "neph", "nephi"};
		Book nephi1 = new Book("1nephi", 22, "/1-ne/");
		for(String abbreviation : abbreviations)
		{
			nephi1.addAbbreviation("1" + abbreviation);
			nephi1.addAbbreviation("1st" + abbreviation);
		}
		
		Book nephi2 = new Book("2nephi", 33, "/2-ne/");
		for(String abbreviation : abbreviations)
		{
			nephi2.addAbbreviation("2" + abbreviation);
			nephi2.addAbbreviation("2nd" + abbreviation);
		}
		
		Book jacob = new Book("Jacob", 7, "/jacob/");
		jacob.addAbbreviation("jac");
		jacob.addAbbreviation("jacb");
		jacob.addAbbreviation("jcb");
		
		Book enos = new Book("Enos", 1, "/enos/");
		enos.addAbbreviation("eno");
		enos.addAbbreviation("ens");
		
		Book jarom = new Book("Jarom", 1, "/jarom/");
		jarom.addAbbreviation("jar");
		jarom.addAbbreviation("jrm");
		
		Book omni = new Book("Omni", 1, "/omni/");
		omni.addAbbreviation("omn");
		
		Book words = new Book("Words of Mormon", 1, "/w-of-m/");
		words.addAbbreviation("WMN");
		words.addAbbreviation("wofm");
		words.addAbbreviation("wom");
		
		Book mosiah = new Book("Mosiah", 29, "/mosiah/");
		mosiah.addAbbreviation("msh");
		mosiah.addAbbreviation("mos");
		
		Book alma = new Book("Alma", 63, "/alma/");
		alma.addAbbreviation("alm");
		
		Book helaman = new Book("Helaman", 16, "/hel/");
		helaman.addAbbreviation("hel");
		helaman.addAbbreviation("hlm");
		helaman.addAbbreviation("hlmn");
		
		Book nephi3 = new Book("3nephi", 30, "/3-ne/");
		for(String abbreviation : abbreviations)
		{
			nephi3.addAbbreviation("3" + abbreviation);
			nephi3.addAbbreviation("3rd" + abbreviation);
		}
		
		Book nephi4 = new Book("4nephi", 1, "/4-ne/");
		for(String abbreviation : abbreviations)
		{
			nephi4.addAbbreviation("4" + abbreviation);
			nephi4.addAbbreviation("4th" + abbreviation);
		}
		
		Book mormon = new Book("Mormon", 9, "/morm/");
		mormon.addAbbreviation("mor");
		mormon.addAbbreviation("morm");
		mormon.addAbbreviation("mrm");
		mormon.addAbbreviation("mrmn");
		
		Book ether = new Book("Ether", 15, "/ether/");
		ether.addAbbreviation("eth");
		ether.addAbbreviation("ethr");
		
		Book moroni = new Book("Moroni", 10, "/moro/");
		moroni.addAbbreviation("mni");
		moroni.addAbbreviation("moro");
		moroni.addAbbreviation("mrn");
		moroni.addAbbreviation("mrni");		
		
		bOfM.addBook(nephi1, 0);
		bOfM.addBook(nephi2, 1);
		bOfM.addBook(jacob, 2);
		bOfM.addBook(enos, 3);
		bOfM.addBook(jarom, 4);
		bOfM.addBook(omni, 5);
		bOfM.addBook(words, 6);
		bOfM.addBook(mosiah, 7);
		bOfM.addBook(alma, 8);
		bOfM.addBook(helaman, 9);
		bOfM.addBook(nephi3, 10);
		bOfM.addBook(nephi4, 11);
		bOfM.addBook(mormon, 12);
		bOfM.addBook(ether, 13);
		bOfM.addBook(moroni, 14);
	}
	
	private void oldT()
	{
		Book genesis = new Book("Genesis", 50, "/gen/");
		genesis.addAbbreviation("gen");
		
		Book exodus = new Book("Exodus", 40, "/ex/");
		exodus.addAbbreviation("ex");
		exodus.addAbbreviation("exo");
		
		Book leviticus = new Book("Leviticus", 27, "/lev/");
		leviticus.addAbbreviation("lev");
		
		Book numbers = new Book("numbers", 36, "/num/");
		numbers.addAbbreviation("num");
		
		Book deuteronomy = new Book("Deuteronomy", 34, "/deut/");
		deuteronomy.addAbbreviation("deu");
		deuteronomy.addAbbreviation("deut");
		
		Book joshua = new Book("Joshua", 24, "/josh/");
		joshua.addAbbreviation("jos");
		joshua.addAbbreviation("josh");
		
		Book judges = new Book("Judges", 21, "/judg/");
		judges.addAbbreviation("judg");
		judges.addAbbreviation("jdg");
		
		Book ruth = new Book("Ruth", 4, "/ruth/");
		ruth.addAbbreviation("rth");
		
		Book samuel1 = new Book("1Samuel", 31, "/1-sam/");
		samuel1.addAbbreviation("1sam");
		samuel1.addAbbreviation("1-sam");
		
		Book samuel2 = new Book("2samuel", 24, "/2-sam/");
		samuel2.addAbbreviation("2sam");
		samuel2.addAbbreviation("2-sam");
		
		Book kings1 = new Book("1kings", 22, "/1-kgs/");
		kings1.addAbbreviation("1kgs");
		kings1.addAbbreviation("1-kgs");
		kings1.addAbbreviation("1kin");
		kings1.addAbbreviation("1-kin");
		
		Book kings2 = new Book("2kings", 25, "/2-kgs/");
		kings2.addAbbreviation("2kgs");
		kings2.addAbbreviation("2-kgs");
		kings2.addAbbreviation("2kin");
		kings2.addAbbreviation("2-kin");
		
		Book chr1 = new Book("1chronicles", 29, "/1-chr/");
		chr1.addAbbreviation("1-chr");
		chr1.addAbbreviation("1chr");
		
		Book chr2 = new Book("2chronicles", 36, "/2-chr/");
		chr2.addAbbreviation("2-chr");
		chr2.addAbbreviation("2chr");
		
		Book ezra = new Book("Ezra", 10, "/ezra/");
		ezra.addAbbreviation("ezr");
		ezra.addAbbreviation("eza");
		
		Book nehemiah = new Book("nehemiah", 13, "/neh/");
		nehemiah.addAbbreviation("neh");
		
		Book esther = new Book("esther", 10, "/esth/");
		esther.addAbbreviation("est");
		esther.addAbbreviation("esth");
		
		Book job = new Book("job", 42, "/job/");
		
		Book psalms = new Book("psalms", 150, "/ps/");
		psalms.addAbbreviation("ps");
		psalms.addAbbreviation("psalm");
		psalms.addAbbreviation("psm");
		
		Book proverbs = new Book("proverbs", 31, "/prov/");
		proverbs.addAbbreviation("proverb");
		proverbs.addAbbreviation("prov");
		proverbs.addAbbreviation("pro");
		
		Book ecclesiastes = new Book("Ecclesiastes", 12, "/eccl/");
		ecclesiastes.addAbbreviation("eccl");
		ecclesiastes.addAbbreviation("ecc");
		ecclesiastes.addAbbreviation("ecl");
		
		Book song = new Book("Song of solomon", 8, "/song/");
		song.addAbbreviation("song");
		song.addAbbreviation("ss");
		song.addAbbreviation("sos");
		song.addAbbreviation("sofs");
		
		Book isaiah = new Book("Isaiah", 66, "/isa/");
		isaiah.addAbbreviation("isa");
		
		Book jeremiah = new Book("Jeremiah", 52, "/jer/");
		jeremiah.addAbbreviation("jer");
		
		Book lamentations = new Book("Lamentations", 5, "/lam/");
		lamentations.addAbbreviation("lam");
		
		Book ezekiel = new Book("ezekiel", 48, "/ezek/");
		ezekiel.addAbbreviation("ezek");
		ezekiel.addAbbreviation("ezk");
		
		Book daniel = new Book("daniel", 12, "/dan/");
		daniel.addAbbreviation("dan");
		daniel.addAbbreviation("dnl");
		
		Book hosea = new Book("hosea", 14, "/hosea/");
		hosea.addAbbreviation("hos");
		
		Book joel = new Book("Joel", 3, "/joel/");
		joel.addAbbreviation("joe");
		joel.addAbbreviation("jol");
		
		Book amos = new Book("amos", 9, "/amos/");
		amos.addAbbreviation("amo");
		amos.addAbbreviation("ams");
		
		Book obadiah = new Book("Obadiah", 1, "/obad/");
		obadiah.addAbbreviation("obad");
		obadiah.addAbbreviation("oba");
		
		Book jonah = new Book("Jonah", 4, "/jonah/");
		jonah.addAbbreviation("jna");
		
		Book micah = new Book("micah", 7, "/micah/");
		micah.addAbbreviation("mic");
		micah.addAbbreviation("mca");
		
		Book nahum = new Book("nahum", 3, "/nahum/");
		nahum.addAbbreviation("nah");
		nahum.addAbbreviation("nhm");
		
		Book habakkuk = new Book("Habakkuk", 3, "/hab/");
		habakkuk.addAbbreviation("hab");
		
		Book zephaniah = new Book("Zephaniah", 3, "/zeph/");
		zephaniah.addAbbreviation("zeph");
		zephaniah.addAbbreviation("zph");
		
		Book haggai = new Book("haggai", 2, "/hag/");
		haggai.addAbbreviation("hag");
		
		Book zechariah = new Book("zechariah", 14, "/zech/");
		zechariah.addAbbreviation("zech");
		zechariah.addAbbreviation("zch");
		
		Book malachi = new Book("malachi", 4, "/mal/");
		malachi.addAbbreviation("mal");
		
		oldT.addBook(genesis, 0);
		oldT.addBook(exodus, 1);
		oldT.addBook(leviticus, 2);
		oldT.addBook(numbers, 3);
		oldT.addBook(deuteronomy, 4);
		oldT.addBook(joshua, 5);
		oldT.addBook(judges, 6);
		oldT.addBook(ruth, 7);
		oldT.addBook(samuel1, 8);
		oldT.addBook(samuel2, 9);
		oldT.addBook(kings1, 10);
		oldT.addBook(kings2, 11);
		oldT.addBook(chr1, 12);
		oldT.addBook(chr2, 13);
		oldT.addBook(ezra, 14);
		oldT.addBook(nehemiah, 15);
		oldT.addBook(esther, 16);
		oldT.addBook(job, 17);
		oldT.addBook(psalms, 18);
		oldT.addBook(proverbs, 19);
		oldT.addBook(ecclesiastes, 20);
		oldT.addBook(song, 21);
		oldT.addBook(isaiah, 22);
		oldT.addBook(jeremiah, 23);
		oldT.addBook(lamentations, 24);
		oldT.addBook(ezekiel, 25);
		oldT.addBook(daniel, 26);
		oldT.addBook(hosea, 27);
		oldT.addBook(joel, 28);
		oldT.addBook(amos, 29);
		oldT.addBook(obadiah, 30);
		oldT.addBook(jonah, 31);
		oldT.addBook(micah, 32);
		oldT.addBook(nahum, 33);
		oldT.addBook(habakkuk, 34);
		oldT.addBook(zephaniah, 35);
		oldT.addBook(haggai, 36);
		oldT.addBook(zechariah, 37);
		oldT.addBook(malachi, 38);
	}
	
	private void newT()
	{
		Book matthew = new Book("Matthew", 28, "/matt/");
		matthew.addAbbreviation("matt");
		matthew.addAbbreviation("mat");
		
		Book mark = new Book("mark", 16, "/mark/");
		mark.addAbbreviation("mrk");
		mark.addAbbreviation("mar");
		
		Book luke = new Book("luke", 24, "/luke/");
		luke.addAbbreviation("luk");
		
		Book john = new Book("John", 21, "/john/");
		john.addAbbreviation("jhn");
		john.addAbbreviation("joh");
		
		Book acts = new Book("acts", 28, "/acts/");
		acts.addAbbreviation("act");
		acts.addAbbreviation("ats");
		
		Book romans = new Book("romans", 16, "/rom/");
		romans.addAbbreviation("rom");
		romans.addAbbreviation("rmn");
		romans.addAbbreviation("rmns");
		romans.addAbbreviation("roman");
		
		Book corinthians1 = new Book("1 Corinthians", 16, "/1-cor/");
		corinthians1.addAbbreviation("1cor");
		corinthians1.addAbbreviation("1-cor");
		corinthians1.addAbbreviation("1crn");
		corinthians1.addAbbreviation("1-crn");
		corinthians1.addAbbreviation("1crnth");
		corinthians1.addAbbreviation("1crnths");
		corinthians1.addAbbreviation("1-crnth");
		corinthians1.addAbbreviation("1-crnths");
		
		Book corinthians2 = new Book("2 Corinthians", 16, "/2-cor/");
		corinthians2.addAbbreviation("2cor");
		corinthians2.addAbbreviation("2-cor");
		corinthians2.addAbbreviation("2crn");
		corinthians2.addAbbreviation("2-crn");
		corinthians2.addAbbreviation("2crnth");
		corinthians2.addAbbreviation("2crnths");
		corinthians2.addAbbreviation("2-crnth");
		corinthians2.addAbbreviation("2-crnths");
		
		Book galatians = new Book("galatians", 6, "/gal/");
		galatians.addAbbreviation("gal");
		galatians.addAbbreviation("gls");
		galatians.addAbbreviation("gals");
		
		Book ephesians = new Book("ephesians", 6, "/eph/");
		ephesians.addAbbreviation("eph");
		ephesians.addAbbreviation("ephs");
		
		Book philippians = new Book("Philippians", 4, "/philip/");
		philippians.addAbbreviation("phl");
		philippians.addAbbreviation("philip");
		philippians.addAbbreviation("philips");
		philippians.addAbbreviation("phil");
		
		Book colossians = new Book("Colossians", 4, "/col/");
		colossians.addAbbreviation("col");
		colossians.addAbbreviation("cls");
		colossians.addAbbreviation("clo");
		colossians.addAbbreviation("clos");
		
		Book thessalonians1 = new Book("1 Thessalonians", 5, "/1-thes/");
		thessalonians1.addAbbreviation("1ths");
		thessalonians1.addAbbreviation("1-ths");
		thessalonians1.addAbbreviation("1thes");
		thessalonians1.addAbbreviation("1-thes");
		thessalonians1.addAbbreviation("1-thsl");
		thessalonians1.addAbbreviation("1thsl");
		thessalonians1.addAbbreviation("1-thesa");
		thessalonians1.addAbbreviation("1thesa");
		thessalonians1.addAbbreviation("1-thess");
		thessalonians1.addAbbreviation("1thess");
		
		Book thessalonians2 = new Book("2 Thessalonians", 3, "/2-thes/");
		thessalonians2.addAbbreviation("2ths");
		thessalonians2.addAbbreviation("2-ths");
		thessalonians2.addAbbreviation("2thes");
		thessalonians2.addAbbreviation("2-thes");
		thessalonians2.addAbbreviation("2-thsl");
		thessalonians2.addAbbreviation("2thsl");
		thessalonians2.addAbbreviation("2-thesa");
		thessalonians2.addAbbreviation("2thesa");
		thessalonians2.addAbbreviation("2-thess");
		thessalonians2.addAbbreviation("2thess");
		
		Book timothy1 = new Book("1timothy", 6, "/1-tim/");
		timothy1.addAbbreviation("1tim");
		timothy1.addAbbreviation("1-tim");
		timothy1.addAbbreviation("1-tmth");
		timothy1.addAbbreviation("1tmth");
		timothy1.addAbbreviation("1tmthy");
		timothy1.addAbbreviation("1-tmthy");
		
		Book timothy2 = new Book("2timothy", 6, "/2-tim/");
		timothy2.addAbbreviation("2tim");
		timothy2.addAbbreviation("2-tim");
		timothy2.addAbbreviation("2-tmth");
		timothy2.addAbbreviation("2tmth");
		timothy2.addAbbreviation("2tmthy");
		timothy2.addAbbreviation("2-tmthy");
		
		Book titus = new Book("titus", 3, "/titus/");
		titus.addAbbreviation("tts");
		titus.addAbbreviation("tit");
		
		Book philemon = new Book("philmon", 1, "/philem/");
		philemon.addAbbreviation("phm");
		philemon.addAbbreviation("philem");
		philemon.addAbbreviation("phlm");
		philemon.addAbbreviation("philmn");
		
		Book hebrews = new Book("hebrews", 13, "/heb/");
		hebrews.addAbbreviation("heb");
		hebrews.addAbbreviation("hbr");
		hebrews.addAbbreviation("hbrs");
		
		Book james = new Book("James", 5, "/james/");
		james.addAbbreviation("jas");
		james.addAbbreviation("jms");
		james.addAbbreviation("jam");
		
		Book peter1 = new Book("peter", 5, "/1-pet/");
		peter1.addAbbreviation("1pet");
		peter1.addAbbreviation("1-pet");
		peter1.addAbbreviation("1-petr");
		peter1.addAbbreviation("1petr");
		peter1.addAbbreviation("1ptr");
		peter1.addAbbreviation("1-ptr");
		
		Book peter2 = new Book("peter", 3, "/2-pet/");
		peter2.addAbbreviation("2pet");
		peter2.addAbbreviation("2-pet");
		peter2.addAbbreviation("2-petr");
		peter2.addAbbreviation("2petr");
		peter2.addAbbreviation("2ptr");
		peter2.addAbbreviation("2-ptr");
		
		Book john1 = new Book("1John", 5, "/1-jn/");
		john1.addAbbreviation("1jhn");
		john1.addAbbreviation("1joh");
		john1.addAbbreviation("1jn");
		john1.addAbbreviation("1-jhn");
		john1.addAbbreviation("1-joh");
		john1.addAbbreviation("1-jn");
		
		Book john2 = new Book("2John", 1, "/2-jn/");
		john2.addAbbreviation("2jhn");
		john2.addAbbreviation("2joh");
		john2.addAbbreviation("2jn");
		john2.addAbbreviation("2-jhn");
		john2.addAbbreviation("2-joh");
		john2.addAbbreviation("2-jn");
		
		Book john3 = new Book("3John", 1, "/3-jn/");
		john2.addAbbreviation("3jhn");
		john2.addAbbreviation("3joh");
		john2.addAbbreviation("3jn");
		john2.addAbbreviation("3-jhn");
		john2.addAbbreviation("3-joh");
		john2.addAbbreviation("3-jn");
		
		Book jude = new Book("jude", 1, "/jude/");
		jude.addAbbreviation("jud");
		jude.addAbbreviation("jde");
		
		Book revelation = new Book("revelation", 22, "/rev/");
		revelation.addAbbreviation("rev");
		revelation.addAbbreviation("revelations");
		revelation.addAbbreviation("rvl");
		revelation.addAbbreviation("rvls");
		revelation.addAbbreviation("revs");
		
		newT.addBook(matthew, 0);
		newT.addBook(mark, 1);
		newT.addBook(luke, 2);
		newT.addBook(john, 3);
		newT.addBook(acts, 4);
		newT.addBook(romans, 5);
		newT.addBook(corinthians1, 6);
		newT.addBook(corinthians2, 7);
		newT.addBook(galatians, 8);
		newT.addBook(ephesians, 9);
		newT.addBook(philippians, 10);
		newT.addBook(colossians, 11);
		newT.addBook(thessalonians1, 12);
		newT.addBook(thessalonians2, 13);
		newT.addBook(timothy1, 14);
		newT.addBook(timothy2, 15);
		newT.addBook(titus, 16);
		newT.addBook(philemon, 17);
		newT.addBook(hebrews, 18);
		newT.addBook(james, 19);
		newT.addBook(peter1, 20);
		newT.addBook(peter2, 21);
		newT.addBook(john1, 22);
		newT.addBook(john2, 23);
		newT.addBook(john3, 24);
		newT.addBook(jude, 25);
		newT.addBook(revelation, 26);
	}
}
