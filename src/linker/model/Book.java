package linker.model;

import java.util.ArrayList;

class Book
{
	private ArrayList<String> abbreviations;
	private String name;
	private int chapters;
	private String link;
	private int[] verses;
	
	public Book(String name, int chapters, String link)
	{
		this.abbreviations = new ArrayList<String>();
		this.name = name.replaceAll(" ", "").toUpperCase();
		this.chapters = chapters;
		this.link = link;
		this.verses = new int[chapters];
	}
	
	public Book(String name, int chapters, String[] abbreviations, String link)
	{
		this.abbreviations = new ArrayList<String>();
		this.name = name;
		this.chapters = chapters;
		this.link = link;
		this.verses = new int[chapters];
		
		for(String abbreviation : abbreviations)
		{
			this.abbreviations.add(abbreviation.toUpperCase());
		}
	}
	
	public void addAbbreviation(String abbreviation)
	{
		abbreviations.add(abbreviation.replaceAll(" ", "").toUpperCase());
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getChapters()
	{
		return chapters;
	}
	
	public boolean hasAbbreviation(String abbreviation)
	{
		return abbreviations.contains(abbreviation.toUpperCase());
	}
	
	public String getLink()
	{
		return link;
	}
	
	public ArrayList<String> getAbbreviations()
	{
		return abbreviations;
	}
	
	public int getVerses(int chapter)
	{
		return verses[chapter - 1];
	}
	
	public void setVerses(int chapter, int verses)
	{
		this.verses[chapter - 1] = verses;
	}
}
