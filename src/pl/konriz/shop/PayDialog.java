package pl.konriz.shop;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * shows when customer wants to pay; can pay with money and card; closes current order and opens new one
 * @author konriz
 *
 */
public class PayDialog extends JDialog
{
	private Shop shop;
	private SumLabel sumLabel;
	private JTable table;
	
	public PayDialog(ShopFrame owner)
	{			
		super(owner, "Zapłata", true);
		shop = owner.getShop();
		sumLabel = owner.getSumLabel();
		table = owner.getTable();
		JPanel sumPanel = new JPanel();
		sumPanel.add(new JLabel(sumLabel.getText()));
		JButton cardPay = new JButton ("Karta");
		cardPay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				shop.sellOrder(true);			
				JOptionPane.showMessageDialog(PayDialog.this, new JLabel("Zapłacono kartą"), "Reszta", JOptionPane.INFORMATION_MESSAGE);					
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.setRowCount(0);
				sumLabel.setSumText(0.0);						
				shop.makeOrder();
				setVisible(false);
			}				
		});
		
		JPanel payPanel = new JPanel();
		TxtField paidField = new TxtField("Klient zapłacił:");
		payPanel.add(paidField);
		payPanel.add(cardPay);
		
		add(sumPanel, BorderLayout.NORTH);
		add(payPanel, BorderLayout.CENTER);
		
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				
				try
				{
					double paid =(double) Double.parseDouble(paidField.getText().trim());					
					double rest = shop.getCurrentOrder().getRest(paid);
					if (rest > 0)
					{
						shop.sellOrder(false);			
						JLabel messageLabel = new JLabel("<html><h3>Reszta: </h3><br><h1>" + rest + "</h1></html>");
						JOptionPane.showMessageDialog(PayDialog.this, messageLabel, "Reszta", JOptionPane.INFORMATION_MESSAGE);					
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						model.setRowCount(0);
						sumLabel.setSumText(0.0);						
						shop.makeOrder();
						setVisible(false);
					}
					else
					{
						JOptionPane.showMessageDialog(PayDialog.this, "Za mało zapłacone!", "Uwaga!", JOptionPane.WARNING_MESSAGE);	
					}
				}
				catch (NumberFormatException e)
				{
					JOptionPane.showMessageDialog(PayDialog.this, "Wpisz liczbę!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		JButton cancel = new JButton("Anuluj");
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				setVisible(false);
			}
			
		});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		add(buttonPanel, BorderLayout.SOUTH);
		paidField.selectAll();
		
		pack();
		setSize(300, 200);
		setLocationRelativeTo(owner);
	}
	
	
}