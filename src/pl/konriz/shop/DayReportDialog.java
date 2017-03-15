package pl.konriz.shop;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * statistics about daily income
 * @author konriz
 *
 */
public class DayReportDialog extends JDialog
{
	private Shop shop;
	
	public DayReportDialog(ShopFrame owner)
	{
		super(owner, "Raport dzienny", true);
		shop = owner.getShop();
		OrderList current = shop.getCurrentList();
		JPanel reportPanel = new JPanel();
		Border etchedBorder = BorderFactory.createEtchedBorder();
		
		JPanel totalPanel = new JPanel();
		totalPanel.setLayout(new GridLayout(0,2));			
		totalPanel.add(new JLabel("Całkowity stan kasy: "));
		totalPanel.add(new JLabel(String.valueOf(shop.getAccount())));
		totalPanel.add(new JLabel("Całkowita gotówka: "));
		totalPanel.add(new JLabel(String.valueOf(shop.getMoney())));
		totalPanel.add(new JLabel("Całkowita płatność kartą: "));
		totalPanel.add(new JLabel(String.valueOf(shop.getCard())));	
		totalPanel.setBorder(etchedBorder);
		
		JPanel dayPanel = new JPanel();
		dayPanel.setLayout(new GridLayout(0,2));
		
		dayPanel.add(new JLabel("Liczba paragonów: "));
		dayPanel.add(new JLabel(String.valueOf(current.getOrdersAmount())));
		dayPanel.add(new JLabel("Utarg dzienny Brutto: "));
		dayPanel.add(new JLabel(String.valueOf(current.getOrdersValueB())));
		dayPanel.add(new JLabel("Utarg gotówka: "));
		dayPanel.add(new JLabel(String.valueOf(current.getOrdersMoney())));
		dayPanel.add(new JLabel("Utarg karta: "));
		dayPanel.add(new JLabel(String.valueOf(current.getOrdersCard())));
		dayPanel.setBorder(etchedBorder);
		
		reportPanel.add(totalPanel);
		reportPanel.add(dayPanel);
		add(reportPanel, BorderLayout.CENTER);
		
		pack();
		setSize(300,200);
		setLocationRelativeTo(owner);
	}
}