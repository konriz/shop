package pl.konriz.shop;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;


/**
 * JTextField with auto selectAll mode
 * @author konriz
 *
 */
public class TxtField extends JTextField
{		
	public TxtField(String text)
	{
		this(text, 0);
	}
	
	public TxtField(String text, int length)
	{
		super(text, length);
		addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e)
			{
				selectAll();
			}
			
			public void focusLost(FocusEvent e)
			{}
		});
	}	
}