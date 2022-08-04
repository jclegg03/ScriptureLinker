package linker.view;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import linker.controller.Controller;

public class ContentPanel extends JPanel
{
	private Controller app;
	private SpringLayout layout;
	private JLabel prompt;
	private JTextField userResponse;
	private JTextField linkField;
	private JPanel buttonPanel;
	private JButton submitButton;
	private JButton copyButton;
	private JButton openButton;
	private JButton helpButton;
	
	public ContentPanel(Controller app)
	{
		super();
		this.app = app;
		this.layout = new SpringLayout();
		this.prompt = new JLabel("What scripture would you like a link to?");
		this.userResponse = new JTextField();
		this.linkField = new JTextField("Link will appear here");
		this.buttonPanel = new JPanel();
		this.submitButton = new JButton("Submit");
		this.copyButton = new JButton("Copy Link");
		this.openButton = new JButton("Open Link");
		this.helpButton = new JButton("Help!");
		
		setupPanel();
		setupLayout();
		setupListeners();
	}
	
	private void setupPanel()
	{
		linkField.setEditable(false);
		
		this.add(prompt);
		this.add(userResponse);
		this.add(linkField);
		this.add(buttonPanel);
		
		buttonPanel.add(submitButton);
		buttonPanel.add(copyButton);
		buttonPanel.add(openButton);
		buttonPanel.add(helpButton);
	}
	
	private void setupLayout()
	{	
		this.setLayout(layout);
		buttonPanel.setLayout(new GridLayout(1, 0, 10, 0));
		
		layout.putConstraint(SpringLayout.NORTH, buttonPanel, 10, SpringLayout.SOUTH, linkField);
		layout.putConstraint(SpringLayout.WEST, buttonPanel, 0, SpringLayout.WEST, linkField);
		layout.putConstraint(SpringLayout.SOUTH, buttonPanel, -10, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.EAST, buttonPanel, 0, SpringLayout.EAST, linkField);

		layout.putConstraint(SpringLayout.NORTH, linkField, 10, SpringLayout.SOUTH, userResponse);
		layout.putConstraint(SpringLayout.WEST, linkField, 0, SpringLayout.WEST, userResponse);
		layout.putConstraint(SpringLayout.EAST, linkField, 0, SpringLayout.EAST, userResponse);

		layout.putConstraint(SpringLayout.NORTH, prompt, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, prompt, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.EAST, prompt, -10, SpringLayout.EAST, this);

		layout.putConstraint(SpringLayout.NORTH, userResponse, 10, SpringLayout.SOUTH, prompt);
		layout.putConstraint(SpringLayout.WEST, userResponse, 0, SpringLayout.WEST, prompt);
		layout.putConstraint(SpringLayout.EAST, userResponse, 0, SpringLayout.EAST, prompt);
	}
	
	private void setupListeners()
	{
		submitButton.addActionListener(click -> app.sendScripture(userResponse.getText(), linkField));
		copyButton.addActionListener(click -> app.copyLink(linkField.getText()));
		openButton.addActionListener(click -> app.openLink(linkField.getText()));
		helpButton.addActionListener(click -> app.makeHelp());
		
		userResponse.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent key)
			{
			}

			@Override
			public void keyPressed(KeyEvent key)
			{
				if(key.getKeyCode() == KeyEvent.VK_ENTER)
				{
					submitButton.doClick();
				}
			}

			@Override
			public void keyReleased(KeyEvent key)
			{
			}
			
		});
	}
}
