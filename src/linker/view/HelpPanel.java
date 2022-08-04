package linker.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import linker.controller.Controller;

public class HelpPanel extends JPanel
{
	private Controller app;
	private SpringLayout layout;
	private JLabel prompt;
	private JTextField helpField;
	private JTextArea listArea;
	private JScrollPane listHolder;
	private JButton submitButton;
	private JPanel panel;
	private JDialog parent;
	
	public HelpPanel(Controller app, JDialog parent)
	{
		super();
		this.app = app;
		this.layout = new SpringLayout();
		this.prompt = new JLabel("Type the name of the scripture here.");
		this.helpField = new JTextField();
		this.listArea = new JTextArea("Abbreviations will appear here.");
		this.listHolder = new JScrollPane();
		this.submitButton = new JButton("Submit");
		this.panel = new JPanel();
		this.parent = parent;
		
		setupPanel();
		setupLayout();
		setupListeners();
	}
	
	private void setupPanel()
	{
		this.add(panel);
		
		panel.add(prompt);
		panel.add(helpField);
		panel.add(listHolder);
		panel.add(submitButton);
		
		listHolder.setViewportView(listArea);
		listHolder.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listHolder.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		listArea.setEditable(false);
	}
	
	private void setupLayout()
	{
		SpringLayout panelLayout = new SpringLayout();
		
		this.setLayout(layout);
		panel.setLayout(panelLayout);
		
		panelLayout.putConstraint(SpringLayout.WEST, submitButton, 0, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, submitButton, -10, SpringLayout.SOUTH, panel);
		panelLayout.putConstraint(SpringLayout.EAST, submitButton, 0, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.NORTH, listHolder, 10, SpringLayout.SOUTH, helpField);
		panelLayout.putConstraint(SpringLayout.WEST, listHolder, 0, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.SOUTH, listHolder, -10, SpringLayout.NORTH, submitButton);
		panelLayout.putConstraint(SpringLayout.EAST, listHolder, 0, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.NORTH, helpField, 10, SpringLayout.SOUTH, prompt);
		panelLayout.putConstraint(SpringLayout.WEST, helpField, 0, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, helpField, 0, SpringLayout.EAST, panel);
		panelLayout.putConstraint(SpringLayout.NORTH, prompt, 0, SpringLayout.NORTH, panel);
		panelLayout.putConstraint(SpringLayout.WEST, prompt, 0, SpringLayout.WEST, panel);
		panelLayout.putConstraint(SpringLayout.EAST, prompt, 0, SpringLayout.EAST, panel);
		
		layout.putConstraint(SpringLayout.NORTH, panel, 10, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, panel, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.SOUTH, panel, -10, SpringLayout.SOUTH, this);
		layout.putConstraint(SpringLayout.EAST, panel, -10, SpringLayout.EAST, this);
	}
	
	private void setupListeners()
	{
		submitButton.addActionListener(click -> app.help(helpField.getText(), listArea));
		
		helpField.addKeyListener(new KeyListener()
		{

			@Override
			public void keyTyped(KeyEvent e)
			{
			}

			@Override
			public void keyPressed(KeyEvent key)
			{
				if(key.getKeyCode() == KeyEvent.VK_ENTER)
				{
					submitButton.doClick();
					helpField.requestFocus();
				}
				if(key.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					parent.dispose();
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
			}
			
		});
	}
}
