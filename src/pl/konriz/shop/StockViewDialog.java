package pl.konriz.shop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * stock preview module; TODO create search machine
 * @author konriz
 *
 */
public class StockViewDialog extends JDialog
{
	private TxtField eanSField = new TxtField("EAN");
	private TxtField nameSField = new TxtField("Nazwa", 10);
	private Shop shop;
	
	public StockViewDialog(ShopFrame owner)
	{
		super(owner, "Magazyn sklepu", false);
		shop = owner.getShop();
		JTable stockTable = createTable();
		JScrollPane scrollPane = new JScrollPane(stockTable);
		scrollPane.setPreferredSize(new Dimension(700,400));
		add(scrollPane);
		
		JPanel searchPanel = new JPanel();
					
		searchPanel.setLayout(new GridLayout(2,2));
		searchPanel.add(new JLabel("EAN"));
		searchPanel.add(new JLabel("Nazwa"));		
		searchPanel.add(eanSField);
		searchPanel.add(nameSField);	
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(searchPanel);
		JButton searchButton = new JButton("Szukaj");
		//TODO create stock search action
//		searchButton.addActionListener(new StockSearchAction(stockTable, this));
		bottomPanel.add(searchButton);
		
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
		
		Shelf currentStock = shop.getStock();
		int lp = 1;
		for (ItemPack e : currentStock.getInventory())
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
}