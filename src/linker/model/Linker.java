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
		
		scripture = scripture.toUpperCase().replace(" ", "");
		
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
		Collection collection = collectionContaining(name);
		Book book;
		
		if(collection == null)
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
	
	private Collection collectionContaining(String name)
	{
		Collection collection = null;
		
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
		
		return collection;
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
		
		addVerses();
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
		
		Book philemon = new Book("philemon", 1, "/philem/");
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
		
		Book peter1 = new Book("1peter", 5, "/1-pet/");
		peter1.addAbbreviation("1pet");
		peter1.addAbbreviation("1-pet");
		peter1.addAbbreviation("1-petr");
		peter1.addAbbreviation("1petr");
		peter1.addAbbreviation("1ptr");
		peter1.addAbbreviation("1-ptr");
		
		Book peter2 = new Book("2peter", 3, "/2-pet/");
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
	
	private void addVerses()
	{
		dAndCVerses();
		pOfGPVerses();
		bOfMVerses();
		oldTVerses();
		newTVerses();
	}
	
	private void dAndCVerses()
	{
		dAndC.addVerses("D&C",1,39);
		dAndC.addVerses("D&C",2,3);
		dAndC.addVerses("D&C",3,20);
		dAndC.addVerses("D&C",4,7);
		dAndC.addVerses("D&C",5,35);
		dAndC.addVerses("D&C",6,37);
		dAndC.addVerses("D&C",7,8);
		dAndC.addVerses("D&C",8,12);
		dAndC.addVerses("D&C",9,14);
		dAndC.addVerses("D&C",10,70);
		dAndC.addVerses("D&C",11,30);
		dAndC.addVerses("D&C",12,9);
		dAndC.addVerses("D&C",13,1);
		dAndC.addVerses("D&C",14,11);
		dAndC.addVerses("D&C",15,6);
		dAndC.addVerses("D&C",16,6);
		dAndC.addVerses("D&C",17,9);
		dAndC.addVerses("D&C",18,47);
		dAndC.addVerses("D&C",19,41);
		dAndC.addVerses("D&C",20,84);
		dAndC.addVerses("D&C",21,12);
		dAndC.addVerses("D&C",22,4);
		dAndC.addVerses("D&C",23,7);
		dAndC.addVerses("D&C",24,19);
		dAndC.addVerses("D&C",25,16);
		dAndC.addVerses("D&C",26,2);
		dAndC.addVerses("D&C",27,18);
		dAndC.addVerses("D&C",28,16);
		dAndC.addVerses("D&C",29,50);
		dAndC.addVerses("D&C",30,11);
		dAndC.addVerses("D&C",31,13);
		dAndC.addVerses("D&C",32,5);
		dAndC.addVerses("D&C",33,18);
		dAndC.addVerses("D&C",34,12);
		dAndC.addVerses("D&C",35,27);
		dAndC.addVerses("D&C",36,8);
		dAndC.addVerses("D&C",37,4);
		dAndC.addVerses("D&C",38,42);
		dAndC.addVerses("D&C",39,24);
		dAndC.addVerses("D&C",40,3);
		dAndC.addVerses("D&C",41,12);
		dAndC.addVerses("D&C",42,93);
		dAndC.addVerses("D&C",43,35);
		dAndC.addVerses("D&C",44,6);
		dAndC.addVerses("D&C",45,75);
		dAndC.addVerses("D&C",46,33);
		dAndC.addVerses("D&C",47,4);
		dAndC.addVerses("D&C",48,6);
		dAndC.addVerses("D&C",49,28);
		dAndC.addVerses("D&C",50,46);
		dAndC.addVerses("D&C",51,20);
		dAndC.addVerses("D&C",52,44);
		dAndC.addVerses("D&C",53,7);
		dAndC.addVerses("D&C",54,10);
		dAndC.addVerses("D&C",55,6);
		dAndC.addVerses("D&C",56,20);
		dAndC.addVerses("D&C",57,16);
		dAndC.addVerses("D&C",58,65);
		dAndC.addVerses("D&C",59,24);
		dAndC.addVerses("D&C",60,17);
		dAndC.addVerses("D&C",61,39);
		dAndC.addVerses("D&C",62,9);
		dAndC.addVerses("D&C",63,66);
		dAndC.addVerses("D&C",64,43);
		dAndC.addVerses("D&C",65,6);
		dAndC.addVerses("D&C",66,13);
		dAndC.addVerses("D&C",67,14);
		dAndC.addVerses("D&C",68,35);
		dAndC.addVerses("D&C",69,8);
		dAndC.addVerses("D&C",70,18);
		dAndC.addVerses("D&C",71,11);
		dAndC.addVerses("D&C",72,26);
		dAndC.addVerses("D&C",73,6);
		dAndC.addVerses("D&C",74,7);
		dAndC.addVerses("D&C",75,36);
		dAndC.addVerses("D&C",76,119);
		dAndC.addVerses("D&C",77,15);
		dAndC.addVerses("D&C",78,22);
		dAndC.addVerses("D&C",79,4);
		dAndC.addVerses("D&C",80,5);
		dAndC.addVerses("D&C",81,7);
		dAndC.addVerses("D&C",82,24);
		dAndC.addVerses("D&C",83,6);
		dAndC.addVerses("D&C",84,120);
		dAndC.addVerses("D&C",85,12);
		dAndC.addVerses("D&C",86,11);
		dAndC.addVerses("D&C",87,8);
		dAndC.addVerses("D&C",88,141);
		dAndC.addVerses("D&C",89,21);
		dAndC.addVerses("D&C",90,37);
		dAndC.addVerses("D&C",91,6);
		dAndC.addVerses("D&C",92,2);
		dAndC.addVerses("D&C",93,53);
		dAndC.addVerses("D&C",94,17);
		dAndC.addVerses("D&C",95,17);
		dAndC.addVerses("D&C",96,9);
		dAndC.addVerses("D&C",97,28);
		dAndC.addVerses("D&C",98,48);
		dAndC.addVerses("D&C",99,8);
		dAndC.addVerses("D&C",100,17);
		dAndC.addVerses("D&C",101,101);
		dAndC.addVerses("D&C",102,34);
		dAndC.addVerses("D&C",103,40);
		dAndC.addVerses("D&C",104,86);
		dAndC.addVerses("D&C",105,41);
		dAndC.addVerses("D&C",106,8);
		dAndC.addVerses("D&C",107,100);
		dAndC.addVerses("D&C",108,8);
		dAndC.addVerses("D&C",109,80);
		dAndC.addVerses("D&C",110,16);
		dAndC.addVerses("D&C",111,11);
		dAndC.addVerses("D&C",112,34);
		dAndC.addVerses("D&C",113,10);
		dAndC.addVerses("D&C",114,2);
		dAndC.addVerses("D&C",115,19);
		dAndC.addVerses("D&C",116,1);
		dAndC.addVerses("D&C",117,16);
		dAndC.addVerses("D&C",118,6);
		dAndC.addVerses("D&C",119,7);
		dAndC.addVerses("D&C",120,1);
		dAndC.addVerses("D&C",121,46);
		dAndC.addVerses("D&C",122,9);
		dAndC.addVerses("D&C",123,17);
		dAndC.addVerses("D&C",124,145);
		dAndC.addVerses("D&C",125,4);
		dAndC.addVerses("D&C",126,3);
		dAndC.addVerses("D&C",127,12);
		dAndC.addVerses("D&C",128,25);
		dAndC.addVerses("D&C",129,9);
		dAndC.addVerses("D&C",130,23);
		dAndC.addVerses("D&C",131,8);
		dAndC.addVerses("D&C",132,66);
		dAndC.addVerses("D&C",133,74);
		dAndC.addVerses("D&C",134,12);
		dAndC.addVerses("D&C",135,7);
		dAndC.addVerses("D&C",136,42);
		dAndC.addVerses("D&C",137,10);
		dAndC.addVerses("D&C",138,60);

		dAndC.addVerses("OD",1,0);
		dAndC.addVerses("OD",2,0);
	}
	
	private void pOfGPVerses()
	{
		pOfGP.addVerses("Moses",1,42);
		pOfGP.addVerses("Moses",2,31);
		pOfGP.addVerses("Moses",3,25);
		pOfGP.addVerses("Moses",4,32);
		pOfGP.addVerses("Moses",5,59);
		pOfGP.addVerses("Moses",6,68);
		pOfGP.addVerses("Moses",7,69);
		pOfGP.addVerses("Moses",8,30);

		pOfGP.addVerses("Abraham",1,31);
		pOfGP.addVerses("Abraham",2,25);
		pOfGP.addVerses("Abraham",3,28);
		pOfGP.addVerses("Abraham",4,31);
		pOfGP.addVerses("Abraham",5,21);

		pOfGP.addVerses("Fac",1,0);
		pOfGP.addVerses("Fac",1,0);
		pOfGP.addVerses("Fac",1,0);

		pOfGP.addVerses("JS-M",1,55);

		pOfGP.addVerses("JS-H",1,75);
	}
	
	private void bOfMVerses()
	{
		bOfM.addVerses("1ne",1,20);
		bOfM.addVerses("1ne",2,24);
		bOfM.addVerses("1ne",3,31);
		bOfM.addVerses("1ne",4,38);
		bOfM.addVerses("1ne",5,22);
		bOfM.addVerses("1ne",6,6);
		bOfM.addVerses("1ne",7,22);
		bOfM.addVerses("1ne",8,38);
		bOfM.addVerses("1ne",9,6);
		bOfM.addVerses("1ne",10,22);
		bOfM.addVerses("1ne",11,36);
		bOfM.addVerses("1ne",12,23);
		bOfM.addVerses("1ne",13,42);
		bOfM.addVerses("1ne",14,30);
		bOfM.addVerses("1ne",15,36);
		bOfM.addVerses("1ne",16,39);
		bOfM.addVerses("1ne",17,55);
		bOfM.addVerses("1ne",18,25);
		bOfM.addVerses("1ne",19,24);
		bOfM.addVerses("1ne",20,22);
		bOfM.addVerses("1ne",21,26);
		bOfM.addVerses("1ne",22,31);
		
		bOfM.addVerses("2ne",1,32);
		bOfM.addVerses("2ne",2,30);
		bOfM.addVerses("2ne",3,25);
		bOfM.addVerses("2ne",4,35);
		bOfM.addVerses("2ne",5,34);
		bOfM.addVerses("2ne",6,18);
		bOfM.addVerses("2ne",7,11);
		bOfM.addVerses("2ne",8,25);
		bOfM.addVerses("2ne",9,54);
		bOfM.addVerses("2ne",10,25);
		bOfM.addVerses("2ne",11,8);
		bOfM.addVerses("2ne",12,22);
		bOfM.addVerses("2ne",13,26);
		bOfM.addVerses("2ne",14,6);
		bOfM.addVerses("2ne",15,30);
		bOfM.addVerses("2ne",16,13);
		bOfM.addVerses("2ne",17,25);
		bOfM.addVerses("2ne",18,22);
		bOfM.addVerses("2ne",19,21);
		bOfM.addVerses("2ne",20,34);
		bOfM.addVerses("2ne",21,16);
		bOfM.addVerses("2ne",22,6);
		bOfM.addVerses("2ne",23,22);
		bOfM.addVerses("2ne",24,32);
		bOfM.addVerses("2ne",25,30);
		bOfM.addVerses("2ne",26,33);
		bOfM.addVerses("2ne",27,35);
		bOfM.addVerses("2ne",28,32);
		bOfM.addVerses("2ne",29,14);
		bOfM.addVerses("2ne",30,18);
		bOfM.addVerses("2ne",31,21);
		bOfM.addVerses("2ne",32,9);
		bOfM.addVerses("2ne",33,15);
		
		bOfM.addVerses("jacob",1,19);
		bOfM.addVerses("jacob",2,35);
		bOfM.addVerses("jacob",3,14);
		bOfM.addVerses("jacob",4,18);
		bOfM.addVerses("jacob",5,77);
		bOfM.addVerses("jacob",6,13);
		bOfM.addVerses("jacob",7,27);
		
		bOfM.addVerses("enos",1,27);
		
		bOfM.addVerses("jarom",1,15);
		
		bOfM.addVerses("omni",1,30);
		
		bOfM.addVerses("WofM",1,18);
		
		bOfM.addVerses("mosiah",1,18);
		bOfM.addVerses("mosiah",2,41);
		bOfM.addVerses("mosiah",3,27);
		bOfM.addVerses("mosiah",4,30);
		bOfM.addVerses("mosiah",5,15);
		bOfM.addVerses("mosiah",6,7);
		bOfM.addVerses("mosiah",7,33);
		bOfM.addVerses("mosiah",8,21);
		bOfM.addVerses("mosiah",9,19);
		bOfM.addVerses("mosiah",10,22);
		bOfM.addVerses("mosiah",11,29);
		bOfM.addVerses("mosiah",12,37);
		bOfM.addVerses("mosiah",13,35);
		bOfM.addVerses("mosiah",14,12);
		bOfM.addVerses("mosiah",15,31);
		bOfM.addVerses("mosiah",16,15);
		bOfM.addVerses("mosiah",17,20);
		bOfM.addVerses("mosiah",18,35);
		bOfM.addVerses("mosiah",19,29);
		bOfM.addVerses("mosiah",20,26);
		bOfM.addVerses("mosiah",21,36);
		bOfM.addVerses("mosiah",22,16);
		bOfM.addVerses("mosiah",23,39);
		bOfM.addVerses("mosiah",24,25);
		bOfM.addVerses("mosiah",25,24);
		bOfM.addVerses("mosiah",26,39);
		bOfM.addVerses("mosiah",27,37);
		bOfM.addVerses("mosiah",28,20);
		bOfM.addVerses("mosiah",29,47);
		
		bOfM.addVerses("alma",1,33);
		bOfM.addVerses("alma",2,38);
		bOfM.addVerses("alma",3,27);
		bOfM.addVerses("alma",4,20);
		bOfM.addVerses("alma",5,62);
		bOfM.addVerses("alma",6,8);
		bOfM.addVerses("alma",7,27);
		bOfM.addVerses("alma",8,32);
		bOfM.addVerses("alma",9,34);
		bOfM.addVerses("alma",10,32);
		bOfM.addVerses("alma",11,46);
		bOfM.addVerses("alma",12,37);
		bOfM.addVerses("alma",13,31);
		bOfM.addVerses("alma",14,29);
		bOfM.addVerses("alma",15,19);
		bOfM.addVerses("alma",16,21);
		bOfM.addVerses("alma",17,39);
		bOfM.addVerses("alma",18,43);
		bOfM.addVerses("alma",19,36);
		bOfM.addVerses("alma",20,30);
		bOfM.addVerses("alma",21,23);
		bOfM.addVerses("alma",22,35);
		bOfM.addVerses("alma",23,18);
		bOfM.addVerses("alma",24,30);
		bOfM.addVerses("alma",25,17);
		bOfM.addVerses("alma",26,37);
		bOfM.addVerses("alma",27,30);
		bOfM.addVerses("alma",28,14);
		bOfM.addVerses("alma",29,17);
		bOfM.addVerses("alma",30,60);
		bOfM.addVerses("alma",31,38);
		bOfM.addVerses("alma",32,43);
		bOfM.addVerses("alma",33,23);
		bOfM.addVerses("alma",34,41);
		bOfM.addVerses("alma",35,16);
		bOfM.addVerses("alma",36,30);
		bOfM.addVerses("alma",37,47);
		bOfM.addVerses("alma",38,15);
		bOfM.addVerses("alma",39,19);
		bOfM.addVerses("alma",40,26);
		bOfM.addVerses("alma",41,15);
		bOfM.addVerses("alma",42,31);
		bOfM.addVerses("alma",43,54);
		bOfM.addVerses("alma",44,24);
		bOfM.addVerses("alma",45,24);
		bOfM.addVerses("alma",46,41);
		bOfM.addVerses("alma",47,36);
		bOfM.addVerses("alma",48,25);
		bOfM.addVerses("alma",49,30);
		bOfM.addVerses("alma",50,40);
		bOfM.addVerses("alma",51,37);
		bOfM.addVerses("alma",52,40);
		bOfM.addVerses("alma",53,23);
		bOfM.addVerses("alma",54,24);
		bOfM.addVerses("alma",55,35);
		bOfM.addVerses("alma",56,57);
		bOfM.addVerses("alma",57,36);
		bOfM.addVerses("alma",58,41);
		bOfM.addVerses("alma",59,13);
		bOfM.addVerses("alma",60,36);
		bOfM.addVerses("alma",61,21);
		bOfM.addVerses("alma",62,52);
		bOfM.addVerses("alma",63,17);
		
		bOfM.addVerses("hel",1,34);
		bOfM.addVerses("hel",2,14);
		bOfM.addVerses("hel",3,37);
		bOfM.addVerses("hel",4,26);
		bOfM.addVerses("hel",5,52);
		bOfM.addVerses("hel",6,41);
		bOfM.addVerses("hel",7,29);
		bOfM.addVerses("hel",8,28);
		bOfM.addVerses("hel",9,41);
		bOfM.addVerses("hel",10,19);
		bOfM.addVerses("hel",11,38);
		bOfM.addVerses("hel",12,26);
		bOfM.addVerses("hel",13,39);
		bOfM.addVerses("hel",14,31);
		bOfM.addVerses("hel",15,17);
		bOfM.addVerses("hel",16,25);
		
		bOfM.addVerses("3ne",1,30);
		bOfM.addVerses("3ne",2,19);
		bOfM.addVerses("3ne",3,26);
		bOfM.addVerses("3ne",4,33);
		bOfM.addVerses("3ne",5,26);
		bOfM.addVerses("3ne",6,30);
		bOfM.addVerses("3ne",7,26);
		bOfM.addVerses("3ne",8,25);
		bOfM.addVerses("3ne",9,22);
		bOfM.addVerses("3ne",10,19);
		bOfM.addVerses("3ne",11,41);
		bOfM.addVerses("3ne",12,48);
		bOfM.addVerses("3ne",13,34);
		bOfM.addVerses("3ne",14,27);
		bOfM.addVerses("3ne",15,24);
		bOfM.addVerses("3ne",16,20);
		bOfM.addVerses("3ne",17,25);
		bOfM.addVerses("3ne",18,39);
		bOfM.addVerses("3ne",19,36);
		bOfM.addVerses("3ne",20,46);
		bOfM.addVerses("3ne",21,29);
		bOfM.addVerses("3ne",22,17);
		bOfM.addVerses("3ne",23,14);
		bOfM.addVerses("3ne",24,18);
		bOfM.addVerses("3ne",25,6);
		bOfM.addVerses("3ne",26,21);
		bOfM.addVerses("3ne",27,33);
		bOfM.addVerses("3ne",28,40);
		bOfM.addVerses("3ne",29,9);
		bOfM.addVerses("3ne",30,2);
		
		bOfM.addVerses("4ne",1,49);
		
		bOfM.addVerses("mormon",1,19);
		bOfM.addVerses("mormon",2,29);
		bOfM.addVerses("mormon",3,22);
		bOfM.addVerses("mormon",4,23);
		bOfM.addVerses("mormon",5,24);
		bOfM.addVerses("mormon",6,22);
		bOfM.addVerses("mormon",7,10);
		bOfM.addVerses("mormon",8,41);
		bOfM.addVerses("mormon",9,37);
		
		bOfM.addVerses("ether",1,43);
		bOfM.addVerses("ether",2,25);
		bOfM.addVerses("ether",3,28);
		bOfM.addVerses("ether",4,19);
		bOfM.addVerses("ether",5,6);
		bOfM.addVerses("ether",6,30);
		bOfM.addVerses("ether",7,27);
		bOfM.addVerses("ether",8,26);
		bOfM.addVerses("ether",9,35);
		bOfM.addVerses("ether",10,34);
		bOfM.addVerses("ether",11,23);
		bOfM.addVerses("ether",12,41);
		bOfM.addVerses("ether",13,31);
		bOfM.addVerses("ether",14,31);
		bOfM.addVerses("ether",15,34);
		
		bOfM.addVerses("moroni",1,4);
		bOfM.addVerses("moroni",2,3);
		bOfM.addVerses("moroni",3,4);
		bOfM.addVerses("moroni",4,3);
		bOfM.addVerses("moroni",5,2);
		bOfM.addVerses("moroni",6,9);
		bOfM.addVerses("moroni",7,48);
		bOfM.addVerses("moroni",8,30);
		bOfM.addVerses("moroni",9,26);
		bOfM.addVerses("moroni",10,34);
	}
	
	private void oldTVerses()
	{
		oldT.addVerses("genesis",1,31);
		oldT.addVerses("genesis",2,25);
		oldT.addVerses("genesis",3,24);
		oldT.addVerses("genesis",4,26);
		oldT.addVerses("genesis",5,32);
		oldT.addVerses("genesis",6,22);
		oldT.addVerses("genesis",7,24);
		oldT.addVerses("genesis",8,22);
		oldT.addVerses("genesis",9,29);
		oldT.addVerses("genesis",10,32);
		oldT.addVerses("genesis",11,32);
		oldT.addVerses("genesis",12,20);
		oldT.addVerses("genesis",13,18);
		oldT.addVerses("genesis",14,24);
		oldT.addVerses("genesis",15,21);
		oldT.addVerses("genesis",16,16);
		oldT.addVerses("genesis",17,27);
		oldT.addVerses("genesis",18,33);
		oldT.addVerses("genesis",19,38);
		oldT.addVerses("genesis",20,18);
		oldT.addVerses("genesis",21,34);
		oldT.addVerses("genesis",22,24);
		oldT.addVerses("genesis",23,20);
		oldT.addVerses("genesis",24,67);
		oldT.addVerses("genesis",25,34);
		oldT.addVerses("genesis",26,35);
		oldT.addVerses("genesis",27,46);
		oldT.addVerses("genesis",28,22);
		oldT.addVerses("genesis",29,35);
		oldT.addVerses("genesis",30,43);
		oldT.addVerses("genesis",31,55);
		oldT.addVerses("genesis",32,32);
		oldT.addVerses("genesis",33,20);
		oldT.addVerses("genesis",34,31);
		oldT.addVerses("genesis",35,29);
		oldT.addVerses("genesis",36,43);
		oldT.addVerses("genesis",37,36);
		oldT.addVerses("genesis",38,30);
		oldT.addVerses("genesis",39,23);
		oldT.addVerses("genesis",40,23);
		oldT.addVerses("genesis",41,57);
		oldT.addVerses("genesis",42,38);
		oldT.addVerses("genesis",43,34);
		oldT.addVerses("genesis",44,34);
		oldT.addVerses("genesis",45,28);
		oldT.addVerses("genesis",46,34);
		oldT.addVerses("genesis",47,31);
		oldT.addVerses("genesis",48,22);
		oldT.addVerses("genesis",49,33);
		oldT.addVerses("genesis",50,26);
		
		oldT.addVerses("exodus",1,22);
		oldT.addVerses("exodus",2,25);
		oldT.addVerses("exodus",3,22);
		oldT.addVerses("exodus",4,31);
		oldT.addVerses("exodus",5,23);
		oldT.addVerses("exodus",6,30);
		oldT.addVerses("exodus",7,25);
		oldT.addVerses("exodus",8,32);
		oldT.addVerses("exodus",9,35);
		oldT.addVerses("exodus",10,29);
		oldT.addVerses("exodus",11,10);
		oldT.addVerses("exodus",12,51);
		oldT.addVerses("exodus",13,22);
		oldT.addVerses("exodus",14,31);
		oldT.addVerses("exodus",15,27);
		oldT.addVerses("exodus",16,36);
		oldT.addVerses("exodus",17,16);
		oldT.addVerses("exodus",18,27);
		oldT.addVerses("exodus",19,25);
		oldT.addVerses("exodus",20,26);
		oldT.addVerses("exodus",21,36);
		oldT.addVerses("exodus",22,31);
		oldT.addVerses("exodus",23,33);
		oldT.addVerses("exodus",24,18);
		oldT.addVerses("exodus",25,40);
		oldT.addVerses("exodus",26,37);
		oldT.addVerses("exodus",27,21);
		oldT.addVerses("exodus",28,43);
		oldT.addVerses("exodus",29,46);
		oldT.addVerses("exodus",30,38);
		oldT.addVerses("exodus",31,18);
		oldT.addVerses("exodus",32,35);
		oldT.addVerses("exodus",33,23);
		oldT.addVerses("exodus",34,35);
		oldT.addVerses("exodus",35,35);
		oldT.addVerses("exodus",36,38);
		oldT.addVerses("exodus",37,29);
		oldT.addVerses("exodus",38,31);
		oldT.addVerses("exodus",39,43);
		oldT.addVerses("exodus",40,38);
		
		oldT.addVerses("lev",1,17);
		oldT.addVerses("lev",2,16);
		oldT.addVerses("lev",3,17);
		oldT.addVerses("lev",4,35);
		oldT.addVerses("lev",5,19);
		oldT.addVerses("lev",6,30);
		oldT.addVerses("lev",7,38);
		oldT.addVerses("lev",8,36);
		oldT.addVerses("lev",9,24);
		oldT.addVerses("lev",10,20);
		oldT.addVerses("lev",11,47);
		oldT.addVerses("lev",12,8);
		oldT.addVerses("lev",13,59);
		oldT.addVerses("lev",14,57);
		oldT.addVerses("lev",15,33);
		oldT.addVerses("lev",16,34);
		oldT.addVerses("lev",17,16);
		oldT.addVerses("lev",18,30);
		oldT.addVerses("lev",19,37);
		oldT.addVerses("lev",20,27);
		oldT.addVerses("lev",21,24);
		oldT.addVerses("lev",22,33);
		oldT.addVerses("lev",23,44);
		oldT.addVerses("lev",24,23);
		oldT.addVerses("lev",25,55);
		oldT.addVerses("lev",26,46);
		oldT.addVerses("lev",27,34);
		
		oldT.addVerses("num",1,54);
		oldT.addVerses("num",2,34);
		oldT.addVerses("num",3,51);
		oldT.addVerses("num",4,49);
		oldT.addVerses("num",5,31);
		oldT.addVerses("num",6,27);
		oldT.addVerses("num",7,89);
		oldT.addVerses("num",8,26);
		oldT.addVerses("num",9,23);
		oldT.addVerses("num",10,36);
		oldT.addVerses("num",11,35);
		oldT.addVerses("num",12,16);
		oldT.addVerses("num",13,33);
		oldT.addVerses("num",14,45);
		oldT.addVerses("num",15,41);
		oldT.addVerses("num",16,50);
		oldT.addVerses("num",17,13);
		oldT.addVerses("num",18,32);
		oldT.addVerses("num",19,22);
		oldT.addVerses("num",20,29);
		oldT.addVerses("num",21,35);
		oldT.addVerses("num",22,41);
		oldT.addVerses("num",23,30);
		oldT.addVerses("num",24,25);
		oldT.addVerses("num",25,18);
		oldT.addVerses("num",26,65);
		oldT.addVerses("num",27,23);
		oldT.addVerses("num",28,31);
		oldT.addVerses("num",29,40);
		oldT.addVerses("num",30,16);
		oldT.addVerses("num",31,54);
		oldT.addVerses("num",32,42);
		oldT.addVerses("num",33,56);
		oldT.addVerses("num",34,29);
		oldT.addVerses("num",35,34);
		oldT.addVerses("num",36,13);
		
		oldT.addVerses("deu",1,46);
		oldT.addVerses("deu",2,37);
		oldT.addVerses("deu",3,29);
		oldT.addVerses("deu",4,49);
		oldT.addVerses("deu",5,33);
		oldT.addVerses("deu",6,25);
		oldT.addVerses("deu",7,26);
		oldT.addVerses("deu",8,20);
		oldT.addVerses("deu",9,29);
		oldT.addVerses("deu",10,22);
		oldT.addVerses("deu",11,32);
		oldT.addVerses("deu",12,32);
		oldT.addVerses("deu",13,18);
		oldT.addVerses("deu",14,29);
		oldT.addVerses("deu",15,23);
		oldT.addVerses("deu",16,22);
		oldT.addVerses("deu",17,20);
		oldT.addVerses("deu",18,22);
		oldT.addVerses("deu",19,21);
		oldT.addVerses("deu",20,20);
		oldT.addVerses("deu",21,23);
		oldT.addVerses("deu",22,30);
		oldT.addVerses("deu",23,25);
		oldT.addVerses("deu",24,22);
		oldT.addVerses("deu",25,19);
		oldT.addVerses("deu",26,19);
		oldT.addVerses("deu",27,26);
		oldT.addVerses("deu",28,68);
		oldT.addVerses("deu",29,29);
		oldT.addVerses("deu",30,20);
		oldT.addVerses("deu",31,30);
		oldT.addVerses("deu",32,52);
		oldT.addVerses("deu",33,29);
		oldT.addVerses("deu",34,12);
		
		oldT.addVerses("joshua",1,18);
		oldT.addVerses("joshua",2,24);
		oldT.addVerses("joshua",3,17);
		oldT.addVerses("joshua",4,24);
		oldT.addVerses("joshua",5,15);
		oldT.addVerses("joshua",6,27);
		oldT.addVerses("joshua",7,26);
		oldT.addVerses("joshua",8,35);
		oldT.addVerses("joshua",9,27);
		oldT.addVerses("joshua",10,43);
		oldT.addVerses("joshua",11,23);
		oldT.addVerses("joshua",12,24);
		oldT.addVerses("joshua",13,33);
		oldT.addVerses("joshua",14,15);
		oldT.addVerses("joshua",15,63);
		oldT.addVerses("joshua",16,10);
		oldT.addVerses("joshua",17,18);
		oldT.addVerses("joshua",18,28);
		oldT.addVerses("joshua",19,51);
		oldT.addVerses("joshua",20,9);
		oldT.addVerses("joshua",21,45);
		oldT.addVerses("joshua",22,34);
		oldT.addVerses("joshua",23,16);
		oldT.addVerses("joshua",24,33);
		
		oldT.addVerses("jdg",1,36);
		oldT.addVerses("jdg",2,23);
		oldT.addVerses("jdg",3,31);
		oldT.addVerses("jdg",4,24);
		oldT.addVerses("jdg",5,31);
		oldT.addVerses("jdg",6,40);
		oldT.addVerses("jdg",7,25);
		oldT.addVerses("jdg",8,35);
		oldT.addVerses("jdg",9,57);
		oldT.addVerses("jdg",10,18);
		oldT.addVerses("jdg",11,40);
		oldT.addVerses("jdg",12,15);
		oldT.addVerses("jdg",13,25);
		oldT.addVerses("jdg",14,20);
		oldT.addVerses("jdg",15,20);
		oldT.addVerses("jdg",16,31);
		oldT.addVerses("jdg",17,13);
		oldT.addVerses("jdg",18,31);
		oldT.addVerses("jdg",19,30);
		oldT.addVerses("jdg",20,48);
		oldT.addVerses("jdg",21,25);
		
		oldT.addVerses("ruth",1,22);
		oldT.addVerses("ruth",2,23);
		oldT.addVerses("ruth",3,18);
		oldT.addVerses("ruth",4,22);
		
		oldT.addVerses("1sam",1,28);
		oldT.addVerses("1sam",2,36);
		oldT.addVerses("1sam",3,21);
		oldT.addVerses("1sam",4,22);
		oldT.addVerses("1sam",5,12);
		oldT.addVerses("1sam",6,21);
		oldT.addVerses("1sam",7,17);
		oldT.addVerses("1sam",8,22);
		oldT.addVerses("1sam",9,27);
		oldT.addVerses("1sam",10,27);
		oldT.addVerses("1sam",11,15);
		oldT.addVerses("1sam",12,25);
		oldT.addVerses("1sam",13,23);
		oldT.addVerses("1sam",14,52);
		oldT.addVerses("1sam",15,35);
		oldT.addVerses("1sam",16,23);
		oldT.addVerses("1sam",17,58);
		oldT.addVerses("1sam",18,30);
		oldT.addVerses("1sam",19,24);
		oldT.addVerses("1sam",20,42);
		oldT.addVerses("1sam",21,15);
		oldT.addVerses("1sam",22,23);
		oldT.addVerses("1sam",23,29);
		oldT.addVerses("1sam",24,22);
		oldT.addVerses("1sam",25,44);
		oldT.addVerses("1sam",26,25);
		oldT.addVerses("1sam",27,12);
		oldT.addVerses("1sam",28,25);
		oldT.addVerses("1sam",29,11);
		oldT.addVerses("1sam",30,31);
		oldT.addVerses("1sam",31,13);
		
		oldT.addVerses("2sam",1,27);
		oldT.addVerses("2sam",2,32);
		oldT.addVerses("2sam",3,39);
		oldT.addVerses("2sam",4,12);
		oldT.addVerses("2sam",5,25);
		oldT.addVerses("2sam",6,23);
		oldT.addVerses("2sam",7,29);
		oldT.addVerses("2sam",8,18);
		oldT.addVerses("2sam",9,13);
		oldT.addVerses("2sam",10,19);
		oldT.addVerses("2sam",11,27);
		oldT.addVerses("2sam",12,31);
		oldT.addVerses("2sam",13,39);
		oldT.addVerses("2sam",14,33);
		oldT.addVerses("2sam",15,37);
		oldT.addVerses("2sam",16,23);
		oldT.addVerses("2sam",17,29);
		oldT.addVerses("2sam",18,33);
		oldT.addVerses("2sam",19,43);
		oldT.addVerses("2sam",20,26);
		oldT.addVerses("2sam",21,22);
		oldT.addVerses("2sam",22,51);
		oldT.addVerses("2sam",23,39);
		oldT.addVerses("2sam",24,25);
		
		oldT.addVerses("1kings",1,53);
		oldT.addVerses("1kings",2,46);
		oldT.addVerses("1kings",3,28);
		oldT.addVerses("1kings",4,34);
		oldT.addVerses("1kings",5,18);
		oldT.addVerses("1kings",6,38);
		oldT.addVerses("1kings",7,51);
		oldT.addVerses("1kings",8,66);
		oldT.addVerses("1kings",9,28);
		oldT.addVerses("1kings",10,29);
		oldT.addVerses("1kings",11,43);
		oldT.addVerses("1kings",12,33);
		oldT.addVerses("1kings",13,34);
		oldT.addVerses("1kings",14,31);
		oldT.addVerses("1kings",15,34);
		oldT.addVerses("1kings",16,34);
		oldT.addVerses("1kings",17,24);
		oldT.addVerses("1kings",18,46);
		oldT.addVerses("1kings",19,21);
		oldT.addVerses("1kings",20,43);
		oldT.addVerses("1kings",21,29);
		oldT.addVerses("1kings",22,53);
		
		oldT.addVerses("2kings",1,18);
		oldT.addVerses("2kings",2,25);
		oldT.addVerses("2kings",3,27);
		oldT.addVerses("2kings",4,44);
		oldT.addVerses("2kings",5,27);
		oldT.addVerses("2kings",6,33);
		oldT.addVerses("2kings",7,20);
		oldT.addVerses("2kings",8,29);
		oldT.addVerses("2kings",9,37);
		oldT.addVerses("2kings",10,36);
		oldT.addVerses("2kings",11,21);
		oldT.addVerses("2kings",12,21);
		oldT.addVerses("2kings",13,25);
		oldT.addVerses("2kings",14,29);
		oldT.addVerses("2kings",15,38);
		oldT.addVerses("2kings",16,20);
		oldT.addVerses("2kings",17,41);
		oldT.addVerses("2kings",18,37);
		oldT.addVerses("2kings",19,37);
		oldT.addVerses("2kings",20,21);
		oldT.addVerses("2kings",21,26);
		oldT.addVerses("2kings",22,20);
		oldT.addVerses("2kings",23,37);
		oldT.addVerses("2kings",24,20);
		oldT.addVerses("2kings",25,30);
		
		oldT.addVerses("1chr",1,54);
		oldT.addVerses("1chr",2,55);
		oldT.addVerses("1chr",3,24);
		oldT.addVerses("1chr",4,43);
		oldT.addVerses("1chr",5,26);
		oldT.addVerses("1chr",6,81);
		oldT.addVerses("1chr",7,40);
		oldT.addVerses("1chr",8,40);
		oldT.addVerses("1chr",9,44);
		oldT.addVerses("1chr",10,14);
		oldT.addVerses("1chr",11,47);
		oldT.addVerses("1chr",12,40);
		oldT.addVerses("1chr",13,14);
		oldT.addVerses("1chr",14,17);
		oldT.addVerses("1chr",15,29);
		oldT.addVerses("1chr",16,43);
		oldT.addVerses("1chr",17,27);
		oldT.addVerses("1chr",18,17);
		oldT.addVerses("1chr",19,19);
		oldT.addVerses("1chr",20,8);
		oldT.addVerses("1chr",21,30);
		oldT.addVerses("1chr",22,19);
		oldT.addVerses("1chr",23,32);
		oldT.addVerses("1chr",24,31);
		oldT.addVerses("1chr",25,31);
		oldT.addVerses("1chr",26,32);
		oldT.addVerses("1chr",27,34);
		oldT.addVerses("1chr",28,21);
		oldT.addVerses("1chr",29,30);
		
		oldT.addVerses("2chr",1,17);
		oldT.addVerses("2chr",2,18);
		oldT.addVerses("2chr",3,17);
		oldT.addVerses("2chr",4,22);
		oldT.addVerses("2chr",5,14);
		oldT.addVerses("2chr",6,42);
		oldT.addVerses("2chr",7,22);
		oldT.addVerses("2chr",8,18);
		oldT.addVerses("2chr",9,31);
		oldT.addVerses("2chr",10,19);
		oldT.addVerses("2chr",11,23);
		oldT.addVerses("2chr",12,16);
		oldT.addVerses("2chr",13,22);
		oldT.addVerses("2chr",14,15);
		oldT.addVerses("2chr",15,19);
		oldT.addVerses("2chr",16,14);
		oldT.addVerses("2chr",17,19);
		oldT.addVerses("2chr",18,34);
		oldT.addVerses("2chr",19,11);
		oldT.addVerses("2chr",20,37);
		oldT.addVerses("2chr",21,20);
		oldT.addVerses("2chr",22,12);
		oldT.addVerses("2chr",23,21);
		oldT.addVerses("2chr",24,27);
		oldT.addVerses("2chr",25,28);
		oldT.addVerses("2chr",26,23);
		oldT.addVerses("2chr",27,9);
		oldT.addVerses("2chr",28,27);
		oldT.addVerses("2chr",29,36);
		oldT.addVerses("2chr",30,27);
		oldT.addVerses("2chr",31,21);
		oldT.addVerses("2chr",32,33);
		oldT.addVerses("2chr",33,25);
		oldT.addVerses("2chr",34,33);
		oldT.addVerses("2chr",35,27);
		oldT.addVerses("2chr",36,23);
		
		oldT.addVerses("ezra",1,11);
		oldT.addVerses("ezra",2,70);
		oldT.addVerses("ezra",3,13);
		oldT.addVerses("ezra",4,24);
		oldT.addVerses("ezra",5,17);
		oldT.addVerses("ezra",6,22);
		oldT.addVerses("ezra",7,28);
		oldT.addVerses("ezra",8,36);
		oldT.addVerses("ezra",9,15);
		oldT.addVerses("ezra",10,44);
		
		oldT.addVerses("neh",1,11);
		oldT.addVerses("neh",2,20);
		oldT.addVerses("neh",3,32);
		oldT.addVerses("neh",4,23);
		oldT.addVerses("neh",5,19);
		oldT.addVerses("neh",6,19);
		oldT.addVerses("neh",7,73);
		oldT.addVerses("neh",8,18);
		oldT.addVerses("neh",9,38);
		oldT.addVerses("neh",10,39);
		oldT.addVerses("neh",11,36);
		oldT.addVerses("neh",12,47);
		oldT.addVerses("neh",13,31);
		
		oldT.addVerses("est",1,22);
		oldT.addVerses("est",2,23);
		oldT.addVerses("est",3,15);
		oldT.addVerses("est",4,17);
		oldT.addVerses("est",5,14);
		oldT.addVerses("est",6,14);
		oldT.addVerses("est",7,10);
		oldT.addVerses("est",8,17);
		oldT.addVerses("est",9,32);
		oldT.addVerses("est",10,3);
		
		oldT.addVerses("job",1,22);
		oldT.addVerses("job",2,13);
		oldT.addVerses("job",3,26);
		oldT.addVerses("job",4,21);
		oldT.addVerses("job",5,27);
		oldT.addVerses("job",6,30);
		oldT.addVerses("job",7,21);
		oldT.addVerses("job",8,22);
		oldT.addVerses("job",9,35);
		oldT.addVerses("job",10,22);
		oldT.addVerses("job",11,20);
		oldT.addVerses("job",12,25);
		oldT.addVerses("job",13,28);
		oldT.addVerses("job",14,22);
		oldT.addVerses("job",15,35);
		oldT.addVerses("job",16,22);
		oldT.addVerses("job",17,16);
		oldT.addVerses("job",18,21);
		oldT.addVerses("job",19,29);
		oldT.addVerses("job",20,29);
		oldT.addVerses("job",21,34);
		oldT.addVerses("job",22,30);
		oldT.addVerses("job",23,17);
		oldT.addVerses("job",24,25);
		oldT.addVerses("job",25,6);
		oldT.addVerses("job",26,14);
		oldT.addVerses("job",27,23);
		oldT.addVerses("job",28,28);
		oldT.addVerses("job",29,25);
		oldT.addVerses("job",30,31);
		oldT.addVerses("job",31,40);
		oldT.addVerses("job",32,22);
		oldT.addVerses("job",33,33);
		oldT.addVerses("job",34,37);
		oldT.addVerses("job",35,16);
		oldT.addVerses("job",36,33);
		oldT.addVerses("job",37,24);
		oldT.addVerses("job",38,41);
		oldT.addVerses("job",39,30);
		oldT.addVerses("job",40,24);
		oldT.addVerses("job",41,34);
		oldT.addVerses("job",42,17);
		
		oldT.addVerses("psm",1,6);
		oldT.addVerses("psm",2,12);
		oldT.addVerses("psm",3,8);
		oldT.addVerses("psm",4,8);
		oldT.addVerses("psm",5,12);
		oldT.addVerses("psm",6,10);
		oldT.addVerses("psm",7,17);
		oldT.addVerses("psm",8,9);
		oldT.addVerses("psm",9,20);
		oldT.addVerses("psm",10,18);
		oldT.addVerses("psm",11,7);
		oldT.addVerses("psm",12,8);
		oldT.addVerses("psm",13,6);
		oldT.addVerses("psm",14,7);
		oldT.addVerses("psm",15,5);
		oldT.addVerses("psm",16,11);
		oldT.addVerses("psm",17,15);
		oldT.addVerses("psm",18,50);
		oldT.addVerses("psm",19,14);
		oldT.addVerses("psm",20,9);
		oldT.addVerses("psm",21,13);
		oldT.addVerses("psm",22,31);
		oldT.addVerses("psm",23,6);
		oldT.addVerses("psm",24,10);
		oldT.addVerses("psm",25,22);
		oldT.addVerses("psm",26,12);
		oldT.addVerses("psm",27,14);
		oldT.addVerses("psm",28,9);
		oldT.addVerses("psm",29,11);
		oldT.addVerses("psm",30,12);
		oldT.addVerses("psm",31,24);
		oldT.addVerses("psm",32,11);
		oldT.addVerses("psm",33,22);
		oldT.addVerses("psm",34,22);
		oldT.addVerses("psm",35,28);
		oldT.addVerses("psm",36,12);
		oldT.addVerses("psm",37,40);
		oldT.addVerses("psm",38,22);
		oldT.addVerses("psm",39,13);
		oldT.addVerses("psm",40,17);
		oldT.addVerses("psm",41,13);
		oldT.addVerses("psm",42,11);
		oldT.addVerses("psm",43,5);
		oldT.addVerses("psm",44,26);
		oldT.addVerses("psm",45,17);
		oldT.addVerses("psm",46,11);
		oldT.addVerses("psm",47,9);
		oldT.addVerses("psm",48,14);
		oldT.addVerses("psm",49,20);
		oldT.addVerses("psm",50,23);
		oldT.addVerses("psm",51,19);
		oldT.addVerses("psm",52,9);
		oldT.addVerses("psm",53,6);
		oldT.addVerses("psm",54,7);
		oldT.addVerses("psm",55,23);
		oldT.addVerses("psm",56,13);
		oldT.addVerses("psm",57,11);
		oldT.addVerses("psm",58,11);
		oldT.addVerses("psm",59,17);
		oldT.addVerses("psm",60,12);
		oldT.addVerses("psm",61,8);
		oldT.addVerses("psm",62,12);
		oldT.addVerses("psm",63,11);
		oldT.addVerses("psm",64,10);
		oldT.addVerses("psm",65,13);
		oldT.addVerses("psm",66,20);
		oldT.addVerses("psm",67,7);
		oldT.addVerses("psm",68,35);
		oldT.addVerses("psm",69,36);
		oldT.addVerses("psm",70,5);
		oldT.addVerses("psm",71,24);
		oldT.addVerses("psm",72,20);
		oldT.addVerses("psm",73,28);
		oldT.addVerses("psm",74,23);
		oldT.addVerses("psm",75,10);
		oldT.addVerses("psm",76,12);
		oldT.addVerses("psm",77,20);
		oldT.addVerses("psm",78,72);
		oldT.addVerses("psm",79,13);
		oldT.addVerses("psm",80,19);
		oldT.addVerses("psm",81,16);
		oldT.addVerses("psm",82,8);
		oldT.addVerses("psm",83,18);
		oldT.addVerses("psm",84,12);
		oldT.addVerses("psm",85,13);
		oldT.addVerses("psm",86,17);
		oldT.addVerses("psm",87,7);
		oldT.addVerses("psm",88,18);
		oldT.addVerses("psm",89,52);
		oldT.addVerses("psm",90,17);
		oldT.addVerses("psm",91,16);
		oldT.addVerses("psm",92,15);
		oldT.addVerses("psm",93,5);
		oldT.addVerses("psm",94,23);
		oldT.addVerses("psm",95,11);
		oldT.addVerses("psm",96,13);
		oldT.addVerses("psm",97,12);
		oldT.addVerses("psm",98,9);
		oldT.addVerses("psm",99,9);
		oldT.addVerses("psm",100,5);
		oldT.addVerses("psm",101,8);
		oldT.addVerses("psm",102,28);
		oldT.addVerses("psm",103,22);
		oldT.addVerses("psm",104,35);
		oldT.addVerses("psm",105,45);
		oldT.addVerses("psm",106,48);
		oldT.addVerses("psm",107,43);
		oldT.addVerses("psm",108,13);
		oldT.addVerses("psm",109,31);
		oldT.addVerses("psm",110,7);
		oldT.addVerses("psm",111,10);
		oldT.addVerses("psm",112,10);
		oldT.addVerses("psm",113,9);
		oldT.addVerses("psm",114,8);
		oldT.addVerses("psm",115,18);
		oldT.addVerses("psm",116,19);
		oldT.addVerses("psm",117,2);
		oldT.addVerses("psm",118,29);
		oldT.addVerses("psm",119,176);
		oldT.addVerses("psm",120,7);
		oldT.addVerses("psm",121,8);
		oldT.addVerses("psm",122,9);
		oldT.addVerses("psm",123,4);
		oldT.addVerses("psm",124,8);
		oldT.addVerses("psm",125,5);
		oldT.addVerses("psm",126,6);
		oldT.addVerses("psm",127,5);
		oldT.addVerses("psm",128,6);
		oldT.addVerses("psm",129,8);
		oldT.addVerses("psm",130,8);
		oldT.addVerses("psm",131,3);
		oldT.addVerses("psm",132,18);
		oldT.addVerses("psm",133,3);
		oldT.addVerses("psm",134,3);
		oldT.addVerses("psm",135,21);
		oldT.addVerses("psm",136,26);
		oldT.addVerses("psm",137,9);
		oldT.addVerses("psm",138,8);
		oldT.addVerses("psm",139,24);
		oldT.addVerses("psm",140,13);
		oldT.addVerses("psm",141,10);
		oldT.addVerses("psm",142,7);
		oldT.addVerses("psm",143,12);
		oldT.addVerses("psm",144,15);
		oldT.addVerses("psm",145,21);
		oldT.addVerses("psm",146,10);
		oldT.addVerses("psm",147,20);
		oldT.addVerses("psm",148,14);
		oldT.addVerses("psm",149,9);
		oldT.addVerses("psm",150,6);
		
		oldT.addVerses("pro",1,33);
		oldT.addVerses("pro",2,22);
		oldT.addVerses("pro",3,35);
		oldT.addVerses("pro",4,27);
		oldT.addVerses("pro",5,23);
		oldT.addVerses("pro",6,35);
		oldT.addVerses("pro",7,27);
		oldT.addVerses("pro",8,36);
		oldT.addVerses("pro",9,18);
		oldT.addVerses("pro",10,32);
		oldT.addVerses("pro",11,31);
		oldT.addVerses("pro",12,28);
		oldT.addVerses("pro",13,25);
		oldT.addVerses("pro",14,35);
		oldT.addVerses("pro",15,33);
		oldT.addVerses("pro",16,33);
		oldT.addVerses("pro",17,28);
		oldT.addVerses("pro",18,24);
		oldT.addVerses("pro",19,29);
		oldT.addVerses("pro",20,30);
		oldT.addVerses("pro",21,31);
		oldT.addVerses("pro",22,29);
		oldT.addVerses("pro",23,35);
		oldT.addVerses("pro",24,34);
		oldT.addVerses("pro",25,28);
		oldT.addVerses("pro",26,28);
		oldT.addVerses("pro",27,27);
		oldT.addVerses("pro",28,28);
		oldT.addVerses("pro",29,27);
		oldT.addVerses("pro",30,33);
		oldT.addVerses("pro",31,31);
		
		oldT.addVerses("ecl",1,18);
		oldT.addVerses("ecl",2,26);
		oldT.addVerses("ecl",3,22);
		oldT.addVerses("ecl",4,16);
		oldT.addVerses("ecl",5,20);
		oldT.addVerses("ecl",6,12);
		oldT.addVerses("ecl",7,29);
		oldT.addVerses("ecl",8,17);
		oldT.addVerses("ecl",9,18);
		oldT.addVerses("ecl",10,20);
		oldT.addVerses("ecl",11,10);
		oldT.addVerses("ecl",12,14);
		
		oldT.addVerses("song",1,17);
		oldT.addVerses("song",2,17);
		oldT.addVerses("song",3,11);
		oldT.addVerses("song",4,16);
		oldT.addVerses("song",5,16);
		oldT.addVerses("song",6,13);
		oldT.addVerses("song",7,13);
		oldT.addVerses("song",8,14);
		
		oldT.addVerses("isa",1,31);
		oldT.addVerses("isa",2,22);
		oldT.addVerses("isa",3,26);
		oldT.addVerses("isa",4,6);
		oldT.addVerses("isa",5,30);
		oldT.addVerses("isa",6,13);
		oldT.addVerses("isa",7,25);
		oldT.addVerses("isa",8,22);
		oldT.addVerses("isa",9,21);
		oldT.addVerses("isa",10,34);
		oldT.addVerses("isa",11,16);
		oldT.addVerses("isa",12,6);
		oldT.addVerses("isa",13,22);
		oldT.addVerses("isa",14,32);
		oldT.addVerses("isa",15,9);
		oldT.addVerses("isa",16,14);
		oldT.addVerses("isa",17,14);
		oldT.addVerses("isa",18,7);
		oldT.addVerses("isa",19,25);
		oldT.addVerses("isa",20,6);
		oldT.addVerses("isa",21,17);
		oldT.addVerses("isa",22,25);
		oldT.addVerses("isa",23,18);
		oldT.addVerses("isa",24,23);
		oldT.addVerses("isa",25,12);
		oldT.addVerses("isa",26,21);
		oldT.addVerses("isa",27,13);
		oldT.addVerses("isa",28,29);
		oldT.addVerses("isa",29,24);
		oldT.addVerses("isa",30,33);
		oldT.addVerses("isa",31,9);
		oldT.addVerses("isa",32,20);
		oldT.addVerses("isa",33,24);
		oldT.addVerses("isa",34,17);
		oldT.addVerses("isa",35,10);
		oldT.addVerses("isa",36,22);
		oldT.addVerses("isa",37,38);
		oldT.addVerses("isa",38,22);
		oldT.addVerses("isa",39,8);
		oldT.addVerses("isa",40,31);
		oldT.addVerses("isa",41,29);
		oldT.addVerses("isa",42,25);
		oldT.addVerses("isa",43,28);
		oldT.addVerses("isa",44,28);
		oldT.addVerses("isa",45,25);
		oldT.addVerses("isa",46,13);
		oldT.addVerses("isa",47,15);
		oldT.addVerses("isa",48,22);
		oldT.addVerses("isa",49,26);
		oldT.addVerses("isa",50,11);
		oldT.addVerses("isa",51,23);
		oldT.addVerses("isa",52,15);
		oldT.addVerses("isa",53,12);
		oldT.addVerses("isa",54,17);
		oldT.addVerses("isa",55,13);
		oldT.addVerses("isa",56,12);
		oldT.addVerses("isa",57,21);
		oldT.addVerses("isa",58,14);
		oldT.addVerses("isa",59,21);
		oldT.addVerses("isa",60,22);
		oldT.addVerses("isa",61,11);
		oldT.addVerses("isa",62,12);
		oldT.addVerses("isa",63,19);
		oldT.addVerses("isa",64,12);
		oldT.addVerses("isa",65,25);
		oldT.addVerses("isa",66,24);
		
		oldT.addVerses("jer",1,19);
		oldT.addVerses("jer",2,37);
		oldT.addVerses("jer",3,25);
		oldT.addVerses("jer",4,31);
		oldT.addVerses("jer",5,31);
		oldT.addVerses("jer",6,30);
		oldT.addVerses("jer",7,34);
		oldT.addVerses("jer",8,22);
		oldT.addVerses("jer",9,26);
		oldT.addVerses("jer",10,25);
		oldT.addVerses("jer",11,23);
		oldT.addVerses("jer",12,17);
		oldT.addVerses("jer",13,27);
		oldT.addVerses("jer",14,22);
		oldT.addVerses("jer",15,21);
		oldT.addVerses("jer",16,21);
		oldT.addVerses("jer",17,27);
		oldT.addVerses("jer",18,23);
		oldT.addVerses("jer",19,15);
		oldT.addVerses("jer",20,18);
		oldT.addVerses("jer",21,14);
		oldT.addVerses("jer",22,30);
		oldT.addVerses("jer",23,40);
		oldT.addVerses("jer",24,10);
		oldT.addVerses("jer",25,38);
		oldT.addVerses("jer",26,24);
		oldT.addVerses("jer",27,22);
		oldT.addVerses("jer",28,17);
		oldT.addVerses("jer",29,32);
		oldT.addVerses("jer",30,24);
		oldT.addVerses("jer",31,40);
		oldT.addVerses("jer",32,44);
		oldT.addVerses("jer",33,26);
		oldT.addVerses("jer",34,22);
		oldT.addVerses("jer",35,19);
		oldT.addVerses("jer",36,32);
		oldT.addVerses("jer",37,21);
		oldT.addVerses("jer",38,28);
		oldT.addVerses("jer",39,18);
		oldT.addVerses("jer",40,16);
		oldT.addVerses("jer",41,18);
		oldT.addVerses("jer",42,22);
		oldT.addVerses("jer",43,13);
		oldT.addVerses("jer",44,30);
		oldT.addVerses("jer",45,5);
		oldT.addVerses("jer",46,28);
		oldT.addVerses("jer",47,7);
		oldT.addVerses("jer",48,47);
		oldT.addVerses("jer",49,39);
		oldT.addVerses("jer",50,46);
		oldT.addVerses("jer",51,64);
		oldT.addVerses("jer",52,34);
		
		oldT.addVerses("lam",1,22);
		oldT.addVerses("lam",2,22);
		oldT.addVerses("lam",3,66);
		oldT.addVerses("lam",4,22);
		oldT.addVerses("lam",5,22);
		
		oldT.addVerses("ezk",1,28);
		oldT.addVerses("ezk",2,10);
		oldT.addVerses("ezk",3,27);
		oldT.addVerses("ezk",4,17);
		oldT.addVerses("ezk",5,17);
		oldT.addVerses("ezk",6,14);
		oldT.addVerses("ezk",7,27);
		oldT.addVerses("ezk",8,18);
		oldT.addVerses("ezk",9,11);
		oldT.addVerses("ezk",10,22);
		oldT.addVerses("ezk",11,25);
		oldT.addVerses("ezk",12,28);
		oldT.addVerses("ezk",13,23);
		oldT.addVerses("ezk",14,23);
		oldT.addVerses("ezk",15,8);
		oldT.addVerses("ezk",16,63);
		oldT.addVerses("ezk",17,24);
		oldT.addVerses("ezk",18,32);
		oldT.addVerses("ezk",19,14);
		oldT.addVerses("ezk",20,49);
		oldT.addVerses("ezk",21,32);
		oldT.addVerses("ezk",22,31);
		oldT.addVerses("ezk",23,49);
		oldT.addVerses("ezk",24,27);
		oldT.addVerses("ezk",25,17);
		oldT.addVerses("ezk",26,21);
		oldT.addVerses("ezk",27,36);
		oldT.addVerses("ezk",28,26);
		oldT.addVerses("ezk",29,21);
		oldT.addVerses("ezk",30,26);
		oldT.addVerses("ezk",31,18);
		oldT.addVerses("ezk",32,32);
		oldT.addVerses("ezk",33,33);
		oldT.addVerses("ezk",34,31);
		oldT.addVerses("ezk",35,15);
		oldT.addVerses("ezk",36,38);
		oldT.addVerses("ezk",37,28);
		oldT.addVerses("ezk",38,23);
		oldT.addVerses("ezk",39,29);
		oldT.addVerses("ezk",40,49);
		oldT.addVerses("ezk",41,26);
		oldT.addVerses("ezk",42,20);
		oldT.addVerses("ezk",43,27);
		oldT.addVerses("ezk",44,31);
		oldT.addVerses("ezk",45,25);
		oldT.addVerses("ezk",46,24);
		oldT.addVerses("ezk",47,23);
		oldT.addVerses("ezk",48,35);
		
		oldT.addVerses("dan",1,21);
		oldT.addVerses("dan",2,49);
		oldT.addVerses("dan",3,30);
		oldT.addVerses("dan",4,37);
		oldT.addVerses("dan",5,31);
		oldT.addVerses("dan",6,28);
		oldT.addVerses("dan",7,28);
		oldT.addVerses("dan",8,27);
		oldT.addVerses("dan",9,27);
		oldT.addVerses("dan",10,21);
		oldT.addVerses("dan",11,45);
		oldT.addVerses("dan",12,13);
		
		oldT.addVerses("hos",1,11);
		oldT.addVerses("hos",2,23);
		oldT.addVerses("hos",3,5);
		oldT.addVerses("hos",4,19);
		oldT.addVerses("hos",5,15);
		oldT.addVerses("hos",6,11);
		oldT.addVerses("hos",7,16);
		oldT.addVerses("hos",8,14);
		oldT.addVerses("hos",9,17);
		oldT.addVerses("hos",10,15);
		oldT.addVerses("hos",11,12);
		oldT.addVerses("hos",12,14);
		oldT.addVerses("hos",13,16);
		oldT.addVerses("hos",14,9);
		
		oldT.addVerses("jol",1,20);
		oldT.addVerses("jol",2,32);
		oldT.addVerses("jol",3,21);
		
		oldT.addVerses("amo",1,15);
		oldT.addVerses("amo",2,16);
		oldT.addVerses("amo",3,15);
		oldT.addVerses("amo",4,13);
		oldT.addVerses("amo",5,27);
		oldT.addVerses("amo",6,14);
		oldT.addVerses("amo",7,17);
		oldT.addVerses("amo",8,14);
		oldT.addVerses("amo",9,15);
		
		oldT.addVerses("oba",1,21);
		
		oldT.addVerses("jonah",1,17);
		oldT.addVerses("jonah",2,10);
		oldT.addVerses("jonah",3,10);
		oldT.addVerses("jonah",4,11);
		
		oldT.addVerses("mic",1,16);
		oldT.addVerses("mic",2,13);
		oldT.addVerses("mic",3,12);
		oldT.addVerses("mic",4,13);
		oldT.addVerses("mic",5,15);
		oldT.addVerses("mic",6,16);
		oldT.addVerses("mic",7,20);
		
		oldT.addVerses("nah",1,15);
		oldT.addVerses("nah",2,13);
		oldT.addVerses("nah",3,19);
		
		oldT.addVerses("hab",1,17);
		oldT.addVerses("hab",2,20);
		oldT.addVerses("hab",3,19);
		
		oldT.addVerses("zph",1,18);
		oldT.addVerses("zph",2,15);
		oldT.addVerses("zph",3,20);
		
		oldT.addVerses("hag",1,15);
		oldT.addVerses("hag",2,23);
		
		oldT.addVerses("zch",1,21);
		oldT.addVerses("zch",2,13);
		oldT.addVerses("zch",3,10);
		oldT.addVerses("zch",4,14);
		oldT.addVerses("zch",5,11);
		oldT.addVerses("zch",6,15);
		oldT.addVerses("zch",7,14);
		oldT.addVerses("zch",8,23);
		oldT.addVerses("zch",9,17);
		oldT.addVerses("zch",10,12);
		oldT.addVerses("zch",11,17);
		oldT.addVerses("zch",12,14);
		oldT.addVerses("zch",13,9);
		oldT.addVerses("zch",14,21);
		
		oldT.addVerses("mal",1,14);
		oldT.addVerses("mal",2,17);
		oldT.addVerses("mal",3,18);
		oldT.addVerses("mal",4,6);
	}
	
	private void newTVerses()
	{
		newT.addVerses("Matthew",1,25);
		newT.addVerses("Matthew",2,23);
		newT.addVerses("Matthew",3,17);
		newT.addVerses("Matthew",4,25);
		newT.addVerses("Matthew",5,48);
		newT.addVerses("Matthew",6,34);
		newT.addVerses("Matthew",7,29);
		newT.addVerses("Matthew",8,34);
		newT.addVerses("Matthew",9,38);
		newT.addVerses("Matthew",10,42);
		newT.addVerses("Matthew",11,30);
		newT.addVerses("Matthew",12,50);
		newT.addVerses("Matthew",13,58);
		newT.addVerses("Matthew",14,36);
		newT.addVerses("Matthew",15,39);
		newT.addVerses("Matthew",16,28);
		newT.addVerses("Matthew",17,27);
		newT.addVerses("Matthew",18,35);
		newT.addVerses("Matthew",19,30);
		newT.addVerses("Matthew",20,34);
		newT.addVerses("Matthew",21,46);
		newT.addVerses("Matthew",22,46);
		newT.addVerses("Matthew",23,39);
		newT.addVerses("Matthew",24,51);
		newT.addVerses("Matthew",25,46);
		newT.addVerses("Matthew",26,75);
		newT.addVerses("Matthew",27,66);
		newT.addVerses("Matthew",28,20);
		
		newT.addVerses("mark",1,45);
		newT.addVerses("mark",2,28);
		newT.addVerses("mark",3,35);
		newT.addVerses("mark",4,41);
		newT.addVerses("mark",5,43);
		newT.addVerses("mark",6,56);
		newT.addVerses("mark",7,37);
		newT.addVerses("mark",8,38);
		newT.addVerses("mark",9,50);
		newT.addVerses("mark",10,52);
		newT.addVerses("mark",11,33);
		newT.addVerses("mark",12,44);
		newT.addVerses("mark",13,37);
		newT.addVerses("mark",14,72);
		newT.addVerses("mark",15,47);
		newT.addVerses("mark",16,20);
		
		newT.addVerses("luke",1,80);
		newT.addVerses("luke",2,52);
		newT.addVerses("luke",3,38);
		newT.addVerses("luke",4,44);
		newT.addVerses("luke",5,39);
		newT.addVerses("luke",6,49);
		newT.addVerses("luke",7,50);
		newT.addVerses("luke",8,56);
		newT.addVerses("luke",9,62);
		newT.addVerses("luke",10,42);
		newT.addVerses("luke",11,54);
		newT.addVerses("luke",12,59);
		newT.addVerses("luke",13,35);
		newT.addVerses("luke",14,35);
		newT.addVerses("luke",15,32);
		newT.addVerses("luke",16,31);
		newT.addVerses("luke",17,37);
		newT.addVerses("luke",18,43);
		newT.addVerses("luke",19,48);
		newT.addVerses("luke",20,47);
		newT.addVerses("luke",21,38);
		newT.addVerses("luke",22,71);
		newT.addVerses("luke",23,56);
		newT.addVerses("luke",24,53);
		
		newT.addVerses("john",1,51);
		newT.addVerses("john",2,25);
		newT.addVerses("john",3,36);
		newT.addVerses("john",4,54);
		newT.addVerses("john",5,47);
		newT.addVerses("john",6,71);
		newT.addVerses("john",7,53);
		newT.addVerses("john",8,59);
		newT.addVerses("john",9,41);
		newT.addVerses("john",10,42);
		newT.addVerses("john",11,57);
		newT.addVerses("john",12,50);
		newT.addVerses("john",13,38);
		newT.addVerses("john",14,31);
		newT.addVerses("john",15,27);
		newT.addVerses("john",16,33);
		newT.addVerses("john",17,26);
		newT.addVerses("john",18,40);
		newT.addVerses("john",19,42);
		newT.addVerses("john",20,31);
		newT.addVerses("john",21,25);
		
		newT.addVerses("acts",1,26);
		newT.addVerses("acts",2,47);
		newT.addVerses("acts",3,26);
		newT.addVerses("acts",4,37);
		newT.addVerses("acts",5,42);
		newT.addVerses("acts",6,15);
		newT.addVerses("acts",7,60);
		newT.addVerses("acts",8,40);
		newT.addVerses("acts",9,43);
		newT.addVerses("acts",10,48);
		newT.addVerses("acts",11,30);
		newT.addVerses("acts",12,25);
		newT.addVerses("acts",13,52);
		newT.addVerses("acts",14,28);
		newT.addVerses("acts",15,41);
		newT.addVerses("acts",16,40);
		newT.addVerses("acts",17,34);
		newT.addVerses("acts",18,28);
		newT.addVerses("acts",19,41);
		newT.addVerses("acts",20,38);
		newT.addVerses("acts",21,40);
		newT.addVerses("acts",22,30);
		newT.addVerses("acts",23,35);
		newT.addVerses("acts",24,27);
		newT.addVerses("acts",25,27);
		newT.addVerses("acts",26,32);
		newT.addVerses("acts",27,44);
		newT.addVerses("acts",28,31);
		
		newT.addVerses("romans",1,32);
		newT.addVerses("romans",2,29);
		newT.addVerses("romans",3,31);
		newT.addVerses("romans",4,25);
		newT.addVerses("romans",5,21);
		newT.addVerses("romans",6,23);
		newT.addVerses("romans",7,25);
		newT.addVerses("romans",8,39);
		newT.addVerses("romans",9,33);
		newT.addVerses("romans",10,21);
		newT.addVerses("romans",11,36);
		newT.addVerses("romans",12,21);
		newT.addVerses("romans",13,14);
		newT.addVerses("romans",14,23);
		newT.addVerses("romans",15,33);
		newT.addVerses("romans",16,27);
		
		newT.addVerses("1corinthians",1,31);
		newT.addVerses("1corinthians",2,16);
		newT.addVerses("1corinthians",3,23);
		newT.addVerses("1corinthians",4,21);
		newT.addVerses("1corinthians",5,13);
		newT.addVerses("1corinthians",6,20);
		newT.addVerses("1corinthians",7,40);
		newT.addVerses("1corinthians",8,13);
		newT.addVerses("1corinthians",9,27);
		newT.addVerses("1corinthians",10,33);
		newT.addVerses("1corinthians",11,34);
		newT.addVerses("1corinthians",12,31);
		newT.addVerses("1corinthians",13,13);
		newT.addVerses("1corinthians",14,40);
		newT.addVerses("1corinthians",15,58);
		newT.addVerses("1corinthians",16,24);
		
		newT.addVerses("2corinthians",1,24);
		newT.addVerses("2corinthians",2,17);
		newT.addVerses("2corinthians",3,18);
		newT.addVerses("2corinthians",4,18);
		newT.addVerses("2corinthians",5,21);
		newT.addVerses("2corinthians",6,18);
		newT.addVerses("2corinthians",7,16);
		newT.addVerses("2corinthians",8,24);
		newT.addVerses("2corinthians",9,15);
		newT.addVerses("2corinthians",10,18);
		newT.addVerses("2corinthians",11,33);
		newT.addVerses("2corinthians",12,21);
		newT.addVerses("2corinthians",13,14);
		
		newT.addVerses("galatians",1,24);
		newT.addVerses("galatians",2,21);
		newT.addVerses("galatians",3,29);
		newT.addVerses("galatians",4,31);
		newT.addVerses("galatians",5,26);
		newT.addVerses("galatians",6,18);
		
		newT.addVerses("ephesians",1,23);
		newT.addVerses("ephesians",2,22);
		newT.addVerses("ephesians",3,21);
		newT.addVerses("ephesians",4,32);
		newT.addVerses("ephesians",5,33);
		newT.addVerses("ephesians",6,24);
		
		newT.addVerses("philippians",1,30);
		newT.addVerses("philippians",2,30);
		newT.addVerses("philippians",3,21);
		newT.addVerses("philippians",4,23);
		
		newT.addVerses("colossians",1,29);
		newT.addVerses("colossians",2,23);
		newT.addVerses("colossians",3,25);
		newT.addVerses("colossians",4,18);
		
		newT.addVerses("1thes",1,10);
		newT.addVerses("1thes",2,20);
		newT.addVerses("1thes",3,13);
		newT.addVerses("1thes",4,18);
		newT.addVerses("1thes",5,28);
		
		newT.addVerses("2thes",1,12);
		newT.addVerses("2thes",2,17);
		newT.addVerses("2thes",3,18);
		
		newT.addVerses("1tim",1,20);
		newT.addVerses("1tim",2,15);
		newT.addVerses("1tim",3,16);
		newT.addVerses("1tim",4,16);
		newT.addVerses("1tim",5,25);
		newT.addVerses("1tim",6,21);
		
		newT.addVerses("2tim",1,18);
		newT.addVerses("2tim",2,26);
		newT.addVerses("2tim",3,17);
		newT.addVerses("2tim",4,22);
		
		newT.addVerses("titus",1,16);
		newT.addVerses("titus",2,15);
		newT.addVerses("titus",3,15);
		
		newT.addVerses("philemon",1,25);
		
		newT.addVerses("heb",1,14);
		newT.addVerses("heb",2,18);
		newT.addVerses("heb",3,19);
		newT.addVerses("heb",4,16);
		newT.addVerses("heb",5,14);
		newT.addVerses("heb",6,20);
		newT.addVerses("heb",7,28);
		newT.addVerses("heb",8,13);
		newT.addVerses("heb",9,28);
		newT.addVerses("heb",10,39);
		newT.addVerses("heb",11,40);
		newT.addVerses("heb",12,29);
		newT.addVerses("heb",13,25);
		
		newT.addVerses("james",1,27);
		newT.addVerses("james",2,26);
		newT.addVerses("james",3,18);
		newT.addVerses("james",4,17);
		newT.addVerses("james",5,20);
		
		newT.addVerses("1peter",1,25);
		newT.addVerses("1peter",2,25);
		newT.addVerses("1peter",3,22);
		newT.addVerses("1peter",4,19);
		newT.addVerses("1peter",5,14);
		
		newT.addVerses("2peter",1,21);
		newT.addVerses("2peter",2,22);
		newT.addVerses("2peter",3,18);
		
		newT.addVerses("1john",1,10);
		newT.addVerses("1john",2,29);
		newT.addVerses("1john",3,24);
		newT.addVerses("1john",4,21);
		newT.addVerses("1john",5,21);
		
		newT.addVerses("2john",1,13);
		
		newT.addVerses("3john",1,14);
		
		newT.addVerses("jude",1,25);
		
		newT.addVerses("rev",1,20);
		newT.addVerses("rev",2,29);
		newT.addVerses("rev",3,22);
		newT.addVerses("rev",4,11);
		newT.addVerses("rev",5,14);
		newT.addVerses("rev",6,17);
		newT.addVerses("rev",7,17);
		newT.addVerses("rev",8,13);
		newT.addVerses("rev",9,21);
		newT.addVerses("rev",10,11);
		newT.addVerses("rev",11,19);
		newT.addVerses("rev",12,17);
		newT.addVerses("rev",13,18);
		newT.addVerses("rev",14,20);
		newT.addVerses("rev",15,8);
		newT.addVerses("rev",16,21);
		newT.addVerses("rev",17,18);
		newT.addVerses("rev",18,24);
		newT.addVerses("rev",19,21);
		newT.addVerses("rev",20,15);
		newT.addVerses("rev",21,27);
		newT.addVerses("rev",22,21);
	}
}
