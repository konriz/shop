package pl.konriz.shop;

import java.awt.*;
import javax.swing.*;

public class GUI {
	
	private Shop shop;
	private JFrame frame;
	
	public GUI(Shop sh)
	{
		
		shop = sh;
		frame = (JFrame) new ShopFrame(shop);
	}
	
	public void runGUI()
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
			frame.setVisible(true);
			
			}
		});
	}
	
	
}//GUI

