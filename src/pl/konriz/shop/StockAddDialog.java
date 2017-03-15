package pl.konriz.shop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * module for adding items to invoice and finally adding invoice to shop's stock
 * @author konriz
 *
 */
public class StockAddDialog extends JDialog
{
	private TxtField eanSField = new TxtField("EAN");
	private TxtField amountSField = new TxtField("0");
	private TxtField nameSField = new TxtField("Nazwa", 10);
	private TxtField priceSField = new TxtField("0.0");
	private TxtField VATSField = new TxtField("0");
	private Shop shop;
	private Invoice currentInvoice;
	
	public String getEanSField() {
		return eanSField.getText();
	}

	public String getAmountSField() {
		return amountSField.getText();
	}
	
	public String getNameSField() {
		return nameSField.getText();
	}

	public String getPriceSField() {
		return priceSField.getText();
	}

	public String getVATSField() {
		return VATSField.getText();
	}

	public StockAddDialog(ShopFrame owner, Invoice inv)
	{
		super(owner, "Magazyn sklepu", false);
		shop = owner.getShop();
		currentInvoice = inv;
		JTable orderTable = createTable();
		JScrollPane scrollPane = new JScrollPane(orderTable);
		scrollPane.setPreferredSize(new Dimension(700,400));
		JPanel topPanel = new JPanel();
		JLabel invoiceSerial = new JLabel("Faktura : " + currentInvoice.getSerial());
		JButton applyButton = new JButton("Odpisz");
		applyButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				if (currentInvoice.getStock().getInventory().size() != 0)
				{
					shop.applyInvoice(currentInvoice, false);
					JOptionPane.showMessageDialog(StockAddDialog.this, "Faktura odpisana!", "Uwaga!", JOptionPane.INFORMATION_MESSAGE);
					setVisible(false);
				}
				else
				{
					JOptionPane.showMessageDialog(StockAddDialog.this, "Faktura pusta!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
				}
			}
			
		});
		topPanel.add(invoiceSerial);
		topPanel.add(applyButton);			
		add(topPanel, BorderLayout.NORTH);
		add(scrollPane);
		
		JPanel addPanel = new JPanel();
					
		addPanel.setLayout(new GridLayout(2,5));
		addPanel.add(new JLabel("EAN"));
		addPanel.add(new JLabel("Ilość"));
		addPanel.add(new JLabel("Nazwa"));
		addPanel.add(new JLabel("Cena Netto"));
		addPanel.add(new JLabel("VAT %"));
		
		addPanel.add(eanSField);
		addPanel.add(amountSField);
		addPanel.add(nameSField);
		addPanel.add(priceSField);
		addPanel.add(VATSField);
		
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(addPanel);
		JButton addButton = new JButton("Dodaj");
		addButton.addActionListener(new StockAddAction(orderTable, this, currentInvoice));
		bottomPanel.add(addButton);
		
		add(bottomPanel, BorderLayout.SOUTH);
		
		pack();
		setSize(800,500);
		setLocationRelativeTo(owner);
	}
		
	
	public JTable createTable()
	{
		DefaultTableModel tableModel = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
	
		JTable table = new JTable(tableModel);
		
		String[] columnNames = {"L.P.", "Kod", "Produkt", "Cena j. N","VAT", "Cena j. B" ,"Ilość"};
		for (String e : columnNames)
		{
		tableModel.addColumn(e);
		}
		
		table.getColumnModel().getColumn(0).setMinWidth(30);
		table.getColumnModel().getColumn(0).setMaxWidth(30);
		
		table.getColumnModel().getColumn(1).setMinWidth(110);
		table.getColumnModel().getColumn(1).setMaxWidth(110);
		
		table.getColumnModel().getColumn(2).setMinWidth(225);
		table.getColumnModel().getColumn(2).setMaxWidth(275);
		

		int lp = 1;
		for (ItemPack e : currentInvoice.getStock().getInventory())
		{
			Item item = e.getItem();
			int amount = e.getAmount();
			Object[] rowToAdd = new Object[]{lp, (Long) item.getEan(), item.getName(), (Double) item.getPrice(), 
					(Double) item.getVAT(), (Double) item.getPriceB(), amount};
			tableModel.addRow(rowToAdd);
			lp ++;
		}
				
		return table;
	}
	
	class StockAddAction extends AbstractAction
	{
		private JTable orderTable;
		private StockAddDialog container;
		private Invoice currentInvoice;
		
		public StockAddAction(JTable table, StockAddDialog owner, Invoice inv)
		{
			orderTable = table;
			container = owner;
			currentInvoice = inv;
		}
		
		public void actionPerformed(ActionEvent event)
		{
			try
			{
				DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
				Long EAN = Long.parseLong(container.getEanSField().trim());
				String name = container.getNameSField().trim();
				Double price = Double.parseDouble(container.getPriceSField().trim());
				Double VAT = Double.parseDouble(container.getVATSField().trim()) / 100.0;
				Integer amount = Integer.parseInt(container.getAmountSField().trim());
				Item newItem = new Item(EAN, name, price, VAT);
				Double priceB = newItem.getPriceB();
				ItemPack item = new ItemPack(newItem, amount);
				int result = currentInvoice.addItem(item, false);
				// add new item to stock 
				if (result == 0)
				{
					int lp = currentInvoice.getStock().getSize() + 1;
					Object[] rowToAdd = new Object[]{lp, EAN, name, price, VAT, priceB, amount};
					model.addRow(rowToAdd);					
				}	
				
				// increase current item stock amount
				else if (result == 1)
				{
					Item addItem = shop.getStock().getInventory(EAN).getItem();
					int lp = currentInvoice.getStock().getSize() + 1;
					Object[] rowToAdd = new Object[]{lp, EAN, addItem.getName(), addItem.getPrice(), addItem.getVAT(), addItem.getPriceB(), amount};
					model.addRow(rowToAdd);
				}
		
				//warning when no price or name given when it's needed
				else if (result == -1)
				{
					JOptionPane.showMessageDialog(StockAddDialog.this, "Nowy produkt - wymagana nazwa, cena i VAT!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
				}				
			}			
			//warning when bad input in TextFields
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(StockAddDialog.this, "Wpisz poprawną wartość!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
			}
			
		}
	}
}