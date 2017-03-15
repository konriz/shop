package pl.konriz.shop;

import javax.swing.JLabel;

/**
 * main menu order sum label TODO maybe move this to order ? 
 * @author konriz
 *
 */
public class SumLabel extends JLabel
{
	private double sum;				
	private String sumText;
	
	SumLabel(double s)
	{
		super();
		setSumText(s);
		setText(getSumText());
	}
			
	public String getSumText()
	{
		return sumText;
	}
	
	public void setSumText(double newSum)
	{
		sum = Math.round(newSum * 100.0)/100.0;
		sumText = "<html>Suma do zapłaty: <br><h1 align=\"center\">" + sum + "</h1></html>";
		setText(sumText);
	}		
}