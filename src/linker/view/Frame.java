package linker.view;

import javax.swing.JFrame;

import linker.controller.Controller;

public class Frame extends JFrame
{
	private Controller app;
	
	public Frame(Controller app)
	{
		super("Scripture Link Generator");
		this.app = app;
		
		setupFrame();
	}
	
	private void setupFrame()
	{
		this.setSize(450, 170);
		this.setContentPane(new ContentPanel(app));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}
}
