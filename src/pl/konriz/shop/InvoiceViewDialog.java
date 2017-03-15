package pl.konriz.shop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 * dialog for viewing invoices - dates and stocks; can edit if invoice is not applied
 * TODO edit applied invoice using master's key
 * @author konriz
 *
 */
public class InvoiceViewDialog extends JDialog
{
	private Shop shop;
	private JTable invoiceTable;
	private JLabel openLabel;
	private JLabel applyLabel;
	private JButton editButton;
	
	public InvoiceViewDialog(ShopFrame owner)
	{
		super(owner, "Magazyn sklepu", false);
		shop = owner.getShop();
		invoiceTable = createTable();
		JScrollPane scrollPane = new JScrollPane(invoiceTable);
		scrollPane.setPreferredSize(new Dimension(700,400));
		add(scrollPane);
		
		JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new GridLayout(2,1));
		searchPanel.add(new JLabel("Nr faktury:"));	
		JComboBox<String> invoiceBox = createBox();
		searchPanel.add(invoiceBox);
		
		JPanel datePanel = new JPanel();
		datePanel.setLayout(new GridLayout(2,2));
		datePanel.add(new JLabel("Data utworzenia:"));
		datePanel.add(new JLabel("Data odpisania:"));
		
		openLabel = new JLabel("Wybierz FV");
		datePanel.add(openLabel);
		
		applyLabel = new JLabel("Wybierz FV");
		datePanel.add(applyLabel);
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridLayout(1, 3));
		topPanel.add(searchPanel);
		JPanel buttonPanel = new JPanel();

		editButton = new JButton("Edytuj");
		editButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				String invSerial = (String) invoiceBox.getSelectedItem();
				Invoice invoice = shop.getInvoice(invSerial);
				StockAddDialog editDialog = new StockAddDialog(owner, invoice);
				editDialog.setVisible(true);
				editDialog.setAlwaysOnTop(true);
			}
		});
		
		
		buttonPanel.add(editButton);
		topPanel.add(buttonPanel);
		topPanel.add(datePanel);
		
		
		
		add(topPanel, BorderLayout.NORTH);
		
		pack();
		setSize(800,500);
		setLocationRelativeTo(owner);
	}
	
	public void setDateOpen(String dateString)
	{
		openLabel.setText(dateString);
	}
	
	public void setDateApply(String dateString)
	{
		applyLabel.setText(dateString);
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
		
		return table;
	}

	public void updateTable(String serial)
	{
		DefaultTableModel model = (DefaultTableModel) invoiceTable.getModel();
		model.setRowCount(0);
		if (serial != null)
		{
			Invoice currentInvoice = shop.getInvoice(serial);
			int lp = 1;
			for (ItemPack e : currentInvoice.getStock().getInventory())
			{
				Item item = e.getItem();
				int amount = e.getAmount();
				Object[] rowToAdd = new Object[]{lp, (Long) item.getEan(), item.getName(), (Double) item.getPrice(), 
						(Double) item.getVAT(), (Double) item.getPriceB(), amount};
				model.addRow(rowToAdd);
				lp ++;
			}
			setDateOpen(currentInvoice.getDateOpen());
			
			if (currentInvoice.isAdded())
			{
				setDateApply(currentInvoice.getDateApplied());
			}
			else
			{
				applyLabel.setText("Nie odpisana");
			}
		}	
		
	}
	
	public JComboBox<String> createBox()
	{
		ArrayList<Invoice> list = shop.getInvoiceList();
		int listLimit = list.size();
		String[] serialList = new String[listLimit];
		
		for (int i=0; i < listLimit; i++)
		{
			serialList[i] = list.get(i).getSerial();
		}
		
		JComboBox<String> comboBox = new JComboBox<String>(serialList);
		comboBox.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e)
			{
				String selected = (String)comboBox.getSelectedItem();
				updateTable(selected);
				if (shop.getInvoice(selected).isAdded())
				{
					editButton.setEnabled(false);
				}
				else
				{
					editButton.setEnabled(true);
				}
			}
		});
		
		return comboBox;
	}
}