package pl.konriz.shop;

import java.awt.*;
import javax.swing.*;
/**
 * assigns local shop to ShopFrame. TODO is it needed ? 
 * @author konriz
 *
 */
public class GUI {
	
	private Shop shop;
	private ShopFrame frame;
	
	public GUI(Shop sh)
	{
		
		shop = sh;
		frame = new ShopFrame(shop);
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

