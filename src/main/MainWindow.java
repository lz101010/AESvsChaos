package main;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainWindow
{
	/**
	 * At some point in the future, this will give a GUI so you don't have to use Main.java
	 * "manually" anymore :-)
	 */
	private static void createWindow()
	{
		//-----------------------------------------------------------------------------------------
		// create the frame
		//-----------------------------------------------------------------------------------------
		JFrame frame = new JFrame("chaosDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//-----------------------------------------------------------------------------------------
		// create left-right pane, left for UI, right for display
		// create up-down pane within the left pane, up for input, down for results (time, size)
		//-----------------------------------------------------------------------------------------
		final JSplitPane splitPaneLR = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane splitPaneUD = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		//-----------------------------------------------------------------------------------------
		// create the panes for the up-down pane
		//-----------------------------------------------------------------------------------------
		JPanel panel1 = new JPanel();
		JPanel panel2 = new JPanel();
		
		//-----------------------------------------------------------------------------------------
		// create content for the upper left panel:
		//
		// - create the options AES and Chaos, and a "load image" button
		//   for this, the button needs an action listener that calls a file chooser
		// - before AES or Chaos is selected, the button is disabled
		//   for this, the radiobuttons need an action listener that activates the button
		//-----------------------------------------------------------------------------------------
		final JRadioButton aes   = new JRadioButton("AES");
		final JRadioButton chaos = new JRadioButton("Chaos");
		final JButton loadFile   = new JButton("load image...");
		loadFile.setEnabled(false);
		
		aes.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				chaos.setSelected(false);
				loadFile.setEnabled(true);			
			}
		});
		chaos.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				aes.setSelected(false);
				loadFile.setEnabled(true);			
			}
		});
		
		loadFile.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				JFileChooser input = new JFileChooser();
				if(aes.isSelected())
					input.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg"));
				else
					input.setFileFilter(new FileNameExtensionFilter("Image Files", "bmp"));
				
				input.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	            if (input.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
	            {
	                File file = input.getSelectedFile();
	                BufferedImage myPicture = null;
	                
					try { myPicture = ImageIO.read(file); } 
					catch (IOException e1) { e1.printStackTrace(); }
					
	                JLabel picLabel = new JLabel(new ImageIcon(myPicture));
	                JPanel help = new JPanel();
	                help.add(picLabel);
	                splitPaneLR.setRightComponent(help);
	                
//	                encrypt(aes.isSelected(), file);
	            }
			}
		});
		
		//-----------------------------------------------------------------------------------------
		// add the buttons to the upper left panel
		//-----------------------------------------------------------------------------------------
		panel1.add(aes);
		panel1.add(chaos);
		panel1.add(loadFile);
		
		
		//-----------------------------------------------------------------------------------------
		// create content for the lower left panel
		//-----------------------------------------------------------------------------------------
		JTextField text = new JTextField("blabla");
		
		//-----------------------------------------------------------------------------------------
		// add stuff to the lower left panel
		//-----------------------------------------------------------------------------------------
		panel2.add(text);
		
		//-----------------------------------------------------------------------------------------
		// pack all panes together and into the frame
		//-----------------------------------------------------------------------------------------
		splitPaneUD.setTopComponent(panel1);
		splitPaneUD.setBottomComponent(panel2);
		
		splitPaneLR.setLeftComponent(splitPaneUD);
		
		frame.getContentPane().add(splitPaneLR);		
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
	}



	public static void main(String[] args)
	{
		try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} 
		catch (Exception e) { e.printStackTrace(); }
		
		createWindow();
	}

}
