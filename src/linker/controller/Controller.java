package linker.controller;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.net.URI;

import javax.swing.JTextArea;
import javax.swing.JTextField;

import linker.model.Linker;
import linker.view.Frame;
import linker.view.HelpFrame;

public class Controller
{
	private Linker linker;
	private Frame frame;
	
	public Controller()
	{
		this.linker = new Linker();
		this.frame = new Frame(this);
	}
	
	public void sendScripture(String scripture, JTextField linkField)
	{
		String link = "";
		
		if(scripture.toLowerCase().contains("help"))
		{
			makeHelp();
			return;
		}
		
		linker.setScripture(scripture);
		link = linker.proscessScripture();
		
		linkField.setText(link);
	}
	
	public void copyLink(String link)
	{
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(link), null);
	}
	
	public void openLink(String link)
	{
		try
		{
			Desktop.getDesktop().browse(new URI("https://" + link));
		}
		catch(Exception URIException)
		{
			
		}
	}
	
	public void help(String text, JTextArea textArea)
	{
		String abbreviations = "";
		
		for(String abbreviation : linker.getHelp(text))
		{
			abbreviations += abbreviation + "\n";
		}
		
		textArea.setText(abbreviations);
	}
	
	public void makeHelp()
	{
		frame.setVisible(false);
		new HelpFrame(this, frame);
		frame.setVisible(true);
	}
}
