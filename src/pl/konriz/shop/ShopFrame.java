package pl.konriz.shop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * main shop frame; module for selling items to customers;
 * has built in login dialog
 * @author konriz
 *
 */
public class ShopFrame extends JFrame
{
	private Shop shop;
	
	Toolkit kit = Toolkit.getDefaultToolkit();
	Dimension screenSize = kit.getScreenSize();
	private int screenX = screenSize.width;
	private int screenY = screenSize.height;
	private int DEFAULT_WIDTH = 900;
	private int DEFAULT_HEIGHT = 650;
	private int DEFAULT_X = (screenX / 2) - DEFAULT_WIDTH / 2;
	private int DEFAULT_Y = (screenY / 2) - DEFAULT_HEIGHT / 2;
		
	private TxtField eanField;
	private TxtField amField;
	private JButton addItemButton;
	
	private JTable table;
	
	private SumLabel sumLabel;
	private double sum;
	private JButton payButton;
	
	public Shop getShop()
	{
		return shop;
	}
	
	public JButton getAddItemButton()
	{
		return addItemButton;
	}
	
	public JButton getPayButton()
	{
		return payButton;
	}
	
	public TxtField getEanField()
	{
		return eanField;
	}
	
	public SumLabel getSumLabel()
	{
		return sumLabel;
	}
	
	public JTable getTable()
	{
		return table;
	}
	
	ShopFrame(Shop sh)
	{
		shop = sh;
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (shop.isOpen())
				{
					shop.closeShop();
				}				
				System.exit(0);
			}
		});
		
		LoginDialog loginD = new LoginDialog(this);
		loginD.setVisible(true);
		
		setTitle(shop.getName());
		shop.openShop();
		shop.getInvoices();
		
		this.setBounds(DEFAULT_X , DEFAULT_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		setJMenuBar(new ShopMenu(this));
		
		//top panel - new order
		JPanel topPanel = new JPanel();
		Border borderTop = BorderFactory.createEtchedBorder();

		sumLabel = new SumLabel(0.0);
		
		payButton = new JButton("Zapłać");
		payButton.addActionListener(new PayAction());
		
		topPanel.add(sumLabel);
		topPanel.add(payButton);
				
		topPanel.setBorder(borderTop);

		//middle panel - table
		JPanel middlePanel = new JPanel();

		table = createTable();
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(700,400));
		middlePanel.add(scrollPane);

		//bottom panel - add item
		JPanel bottomPanel = new JPanel();
		Border borderBottom = BorderFactory.createEtchedBorder();
		eanField = new TxtField("0", 10);
		amField = new TxtField("1", 5);
		addItemButton = new JButton("Dodaj");
		AddItemAction addItemAction = new AddItemAction();
		addItemButton.addActionListener(addItemAction);

		bottomPanel.add(new JLabel("Kod EAN:"));
		bottomPanel.add(eanField);
		bottomPanel.add(new JLabel("Ilość:"));
		bottomPanel.add(amField);
		bottomPanel.add(addItemButton);
		bottomPanel.setBorder(borderBottom);

		// add panels to frame
		
		JPanel framePanel = new JPanel();
		framePanel.add(topPanel, BorderLayout.NORTH);
		framePanel.add(middlePanel, BorderLayout.CENTER);
		framePanel.add(bottomPanel, BorderLayout.SOUTH);
		add(framePanel);		
		
	}
			
	
	
	//dialogi
	
	class LoginDialog extends JDialog
	{
		public LoginDialog(JFrame owner)
		{
			super(owner, "Logowanie", true);
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e)
				{
					offlineClose();
				}
			});

			
			JPanel textFieldPanel = new JPanel();
			textFieldPanel.setLayout(new GridLayout(2, 1));
			
			JTextField loginField = new JTextField("Login", 10);
			JPasswordField passwordField = new JPasswordField("", 10);
			JPanel loginPanel = new JPanel();
			loginPanel.add(new JLabel("LOGIN:"));
			loginPanel.add(loginField);
			JPanel passwordPanel = new JPanel();
			passwordPanel.add(new JLabel("HASŁO:"));
			passwordPanel.add(passwordField);			
			textFieldPanel.add(loginPanel);
			textFieldPanel.add(passwordPanel);
			add(textFieldPanel, BorderLayout.CENTER);
			
			JPanel buttonPanel = new JPanel();
			JButton okButton = new JButton("OK");
			okButton.addActionListener(new ActionListener(){
				
			public void actionPerformed(ActionEvent e)
			{
				DBClass online = new DBClass("jdbc:sqlite:/home/konriz/DB.sql", "data");
				
				String login = loginField.getText();
				String password = String.valueOf(passwordField.getPassword());
				String adress = online.authorizeDB(login, password);
				if (!adress.equals(""))
				{
					online = new DBClass("jdbc:sqlite:/home/konriz/DB.sql", "towar" + adress);				
					shop.setOnlineBase(online);
					shop.setOnlineState(login);
					setVisible(false);
					dispose();
				}
				else
				{
					JOptionPane.showMessageDialog(LoginDialog.this, "Błędny login lub hasło!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
				}
				
			}
			});
						
			JButton cancelButton = new JButton("OFFLINE");
			cancelButton.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e)
				{
					offlineClose();
				}
				
			});
						
			buttonPanel.add(okButton);			
			buttonPanel.add(cancelButton);			
			add(buttonPanel, BorderLayout.SOUTH);
			loginField.grabFocus();
			loginField.selectAll();
			pack();
			setSize(300,150);
			setLocationRelativeTo(owner);
		}
		
		public void offlineClose()
		{
			JOptionPane.showMessageDialog(LoginDialog.this, "Uruchomienie w trybie OFFLINE!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
			setVisible(false);
			dispose();
		}
		
	}
		
	//create table
	public JTable createTable()
	{
		JTable orderTable;
		
		DefaultTableModel tableModel = new DefaultTableModel()
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
	
		orderTable = new JTable(tableModel);	
		String[] columnN1 = {"L.P.", "Kod", "Produkt", "Cena j.", "Ilość", "Koszt N", "VAT%", "Koszt B"};	
		
		for (String e : columnN1)
		{
		tableModel.addColumn(e);
		}
		
		orderTable.getColumnModel().getColumn(0).setMinWidth(30);
		orderTable.getColumnModel().getColumn(0).setMaxWidth(30);
		
		orderTable.getColumnModel().getColumn(1).setMinWidth(110);
		orderTable.getColumnModel().getColumn(1).setMaxWidth(110);
		
		orderTable.getColumnModel().getColumn(2).setMinWidth(225);
		orderTable.getColumnModel().getColumn(2).setMaxWidth(275);
		
		return orderTable;
	}
	
	class AddItemAction extends AbstractAction
	{
		private Item itemToAdd;
		private int amountToAdd;
		
		public AddItemAction()
		{
			putValue(Action.SHORT_DESCRIPTION, "Dodaj produkt do paragonu.");
		}
		
		public void actionPerformed(ActionEvent event)
		{
			try
			{
			
				long eanTable = Long.parseLong(eanField.getText().trim());
				amountToAdd = Integer.parseInt(amField.getText().trim());
	
				Shop currentShop = shop;
				Shelf currentStock = currentShop.getStock();
				Order currentOrder = currentShop.getCurrentOrder();
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				if (currentShop.gatherItem(eanTable, amountToAdd))
				{
					int position = currentOrder.getItemCount();
					Integer lp = (Integer) position;
					itemToAdd = currentStock.getInventory(eanTable).getItem();
					ItemPack currentItemPack = currentOrder.getOrder(position-1);
					Double itemValue = (Double) currentItemPack.getValue();
					Double itemVAT = (Double) currentItemPack.getVAT();
					Double itemValueB = (Double) currentItemPack.getValueB();
					Object[] rowToAdd = new Object[]{lp, eanTable, itemToAdd.getName(), (Double) itemToAdd.getPrice(), amountToAdd,
										itemValue, itemVAT, itemValueB};
					model.addRow(rowToAdd);
					sum = currentOrder.getValueB();
					sumLabel.setSumText(sum);
					amField.setText("1");
					eanField.requestFocus();
					eanField.selectAll();
				}
			}
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(ShopFrame.this, "Wpisz liczbę!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
			}
		}	
	}
	
	class PayAction extends AbstractAction
	{
		public PayAction()
		{
			putValue(Action.SHORT_DESCRIPTION, "Zamknij paragon, przyjmij pieniądze, wydaj resztę.");
		}
		
		public void actionPerformed(ActionEvent event)
		{
			if (shop.getCurrentOrder().getValue() == 0)
			{
				JOptionPane.showMessageDialog(ShopFrame.this, "Zerowa wartość zamówienia!", "Uwaga!", JOptionPane.WARNING_MESSAGE);	
			}
			else
			{
				PayDialog payDialog = new PayDialog(ShopFrame.this);
				payDialog.setVisible(true);
			}
		}
	}	
	
}	
