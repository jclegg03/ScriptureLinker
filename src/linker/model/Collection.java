package linker.model;

class Collection
{
	private Book[] books;
	private String name;
	private String link;
	
	public Collection(String name, int size, String link)
	{
		this.books = new Book[size];
		this.name = name.replaceAll(" ", "").toUpperCase();
		this.link = link;
	}
	
	public Book getBook(String name)
	{
		for(Book book : books)
		{
			if(book.getName().equalsIgnoreCase(name)) return book;
			if(book.hasAbbreviation(name)) return book;
		}
		
		return null;
	}
	
	public void addBook(Book book, int index)
	{
		if(books[index] == null) books[index] = book;
		else throw new NullPointerException();
	}
	
	public String getLink()
	{
		return link;
	}
	
	public String getName()
	{
		return name;
	}
}
