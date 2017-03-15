package pl.konriz.shop;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * creates new empty invoice or selects existing && not applied invoice for editing
 * @author konriz
 *
 */
public class InvoiceAddDialog extends JDialog
{

	private TxtField invoiceSerial = new TxtField("Nr faktury");
	private Invoice invoice;
	private Shop shop;
	
	public String getInvoiceSerial()
	{
		return invoiceSerial.getText();
	}
	
	public Invoice getInvoice()
	{
		return invoice;
	}
	
	public InvoiceAddDialog(ShopFrame owner)
	{
		shop = owner.getShop();
		
		JPanel invoicePanel = new JPanel();
		
		JLabel invoiceLabel = new JLabel("Podaj numer faktury:");
		invoicePanel.add(invoiceLabel, BorderLayout.NORTH);
		invoicePanel.add(invoiceSerial, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				String serial = getInvoiceSerial();
				if (shop.getInvoice(serial) == null)
				{
					invoice = new Invoice(shop, serial);
					StockAddDialog stockAdd = new StockAddDialog(owner, invoice);
					stockAdd.setVisible(true);
					setVisible(false);
				}
				else
				{
					invoice = shop.getInvoice(serial);
					if (!invoice.isAdded())
					{
						StockAddDialog stockAdd = new StockAddDialog(owner, invoice);
						stockAdd.setVisible(true);
						setVisible(false);
					}
					else
					{
						JOptionPane.showMessageDialog(owner, "Faktura zamknięta!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
						setVisible(false);
					}
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
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		invoicePanel.add(buttonPanel, BorderLayout.SOUTH);
		add(invoicePanel);
		setSize(300, 300);
		pack();
		setLocationRelativeTo(owner);
		
	}
	
}
