package linker.view;

import javax.swing.JDialog;
import javax.swing.JFrame;

import linker.controller.Controller;

public class HelpFrame extends JDialog
{
	public HelpFrame(Controller app, JFrame parent)
	{
		super(parent, "Help", true);
		this.setContentPane(new HelpPanel(app, this));
		setupFrame();
	}
	
	private void setupFrame()
	{
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
}
