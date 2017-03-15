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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

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
		
		this.setBounds(DEFAULT_X , DEFAULT_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		setJMenuBar(createMenu());
		
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
		
		//input map
		InputMap imap = framePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		imap.put(KeyStroke.getKeyStroke("ctrl O"), "framePanel.open");
		imap.put(KeyStroke.getKeyStroke("ctrl Z"), "framePanel.close");
		imap.put(KeyStroke.getKeyStroke("ctrl M"), "framePanel.stock");
		
		ActionMap amap = framePanel.getActionMap();		
		amap.put("framePanel.open", new OpenShopAction());
		amap.put("framePanel.close", new CloseShopAction());
		amap.put("framePanel.stock", new StockViewAction());
		
	}
			
	class SumLabel extends JLabel
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
			sumText = "<html>Suma do zapłaty: <br><h1 align=\"center\">" + sum + "</h1></html";
			setText(sumText);
		}		
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
				DBClass online = new DBClass("jdbc:sqlite:/home/konriz/DB.sql", "sklep");
				
				String login = loginField.getText();
				String password = String.valueOf(passwordField.getPassword());
				String adress = online.authorizeDB(login, password);
				if (!adress.equals(""))
				{
					online = new DBClass("jdbc:sqlite:/home/konriz/DB.sql", adress);				
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
	
	class PayDialog extends JDialog
	{
		public PayDialog(JFrame owner)
		{			
			super(owner, "Zapłata", true);
			
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
		
	class DayReportDialog extends JDialog
	{
		public DayReportDialog(JFrame owner)
		{
			super(owner, "Raport dzienny", true);
			OrderList current = shop.getCurrentList();
			JPanel reportPanel = new JPanel();
			reportPanel.setLayout(new GridLayout(0,2));
			
			reportPanel.add(new JLabel("Całkowity stan kasy: "));
			reportPanel.add(new JLabel(String.valueOf(shop.getAccount())));
			reportPanel.add(new JLabel("Gotówka: "));
			reportPanel.add(new JLabel(String.valueOf(shop.getMoney())));
			reportPanel.add(new JLabel("Płatność kartą: "));
			reportPanel.add(new JLabel(String.valueOf(shop.getCard())));	
			reportPanel.add(new JLabel("Liczba paragonów: "));
			reportPanel.add(new JLabel(String.valueOf(current.getOrdersAmount())));
			reportPanel.add(new JLabel("Utarg dzienny Netto : "));
			reportPanel.add(new JLabel(String.valueOf(current.getOrdersValue())));
			reportPanel.add(new JLabel("Utarg dzienny Brutto: "));
			reportPanel.add(new JLabel(String.valueOf(current.getOrdersValueB())));
			add(reportPanel, BorderLayout.CENTER);
			
			pack();
			setSize(300,300);
			setLocationRelativeTo(owner);
		}
	}
	// TODO table with each day and its parameters
	class MonthReportDialog extends JDialog
	{
		public MonthReportDialog(JFrame owner)
		{
			super(owner, "Raport miesięczny", true);
			Shop current = shop;
			
			JPanel reportPanel = new JPanel();
			reportPanel.setLayout(new GridLayout(0,2));
			
			reportPanel.add(new JLabel("Całkowity stan kasy: "));
			reportPanel.add(new JLabel(String.valueOf(current.getAccount())));
			reportPanel.add(new JLabel("Gotówka: "));
			reportPanel.add(new JLabel(String.valueOf(shop.getMoney())));
			reportPanel.add(new JLabel("Płatność kartą: "));
			reportPanel.add(new JLabel(String.valueOf(shop.getCard())));
			reportPanel.add(new JLabel("Liczba paragonów: "));
			reportPanel.add(new JLabel(String.valueOf(current.getAllOrdersAmount())));
			reportPanel.add(new JLabel("Utarg miesięczny Netto : "));
			reportPanel.add(new JLabel(String.valueOf(current.getAllOrdersValue())));
			reportPanel.add(new JLabel("Utarg miesięczny Brutto: "));
			reportPanel.add(new JLabel(String.valueOf(current.getAllOrdersValueB())));
			add(reportPanel, BorderLayout.CENTER);
			
			pack();
			setSize(300,300);
			setLocationRelativeTo(owner);
			
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
		
	class StockDialog extends JDialog
	{
		private TxtField eanSField = new TxtField("EAN");
		private TxtField amountSField = new TxtField("0");
		private TxtField nameSField = new TxtField("Nazwa", 10);
		private TxtField priceSField = new TxtField("0.0");
		private TxtField VATSField = new TxtField("0");

		
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

		
		public StockDialog(JFrame owner)
		{
			super(owner, "Magazyn sklepu", true);
			JTable orderTable = createTable();
			JScrollPane scrollPane = new JScrollPane(orderTable);
			scrollPane.setPreferredSize(new Dimension(700,400));
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
			addButton.addActionListener(new StockAddAction(orderTable, this));
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
	
	
	//create menu
	public JMenuBar createMenu()
	{
		JMenuBar menuBar = new JMenuBar();
				
		JMenu menuShop = new JMenu("Sklep");
		JMenuItem openShop = new JMenuItem("Otwórz");
		openShop.addActionListener(new OpenShopAction());
		openShop.setEnabled(false);
		
		JMenuItem closeShop = new JMenuItem("Zamknij");

		closeShop.addActionListener(new CloseShopAction());
		
		menuShop.add(openShop);
		menuShop.add(closeShop);
		menuBar.add(menuShop);
		
		JMenu menuStock = new JMenu ("Towar");
		JMenuItem stockList = new JMenuItem("Magazyn");
		stockList.addActionListener(new StockViewAction());
		//TODO add position according to VAT invoice
//		JMenuItem stockAdd = new JMenuItem("Dodaj");
		menuStock.add(stockList);
//		menuStock.add(stockAdd);
		menuBar.add(menuStock);
		
		JMenu menuReport = new JMenu ("Raporty");
		JMenuItem reportDay = new JMenuItem("Dzisiaj");
		reportDay.addActionListener(new DayReportAction());
		JMenuItem reportMonth = new JMenuItem("Miesiąc");		
		reportMonth.addActionListener(new MonthReportAction());
		menuReport.add(reportDay);
		menuReport.add(reportMonth);
		menuBar.add(menuReport);
		return menuBar;
				
	}
	
	//textfields with focus prop
	
	class TxtField extends JTextField
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
	
	//add item to order and table
	class OpenShopAction extends AbstractAction
	{
		
		public OpenShopAction()
		{
			putValue(Action.SHORT_DESCRIPTION, "Otwórz sklep - zacznij sprzedawać.");
		}
		
		public void actionPerformed(ActionEvent event)
		{			
			if (!shop.isOpen())
			{
				getJMenuBar().getMenu(0).getItem(0).setEnabled(false);
				getJMenuBar().getMenu(0).getItem(1).setEnabled(true);
				getJMenuBar().getMenu(2).getItem(0).setEnabled(true);
				getJMenuBar().getMenu(2).getItem(1).setEnabled(true);
				addItemButton.setEnabled(true);
				payButton.setEnabled(true);
				shop.openShop();
				eanField.grabFocus();
//				eanField.selectAll();
			}			
		}
	}
	
	class CloseShopAction extends AbstractAction
	{
		public CloseShopAction()
		{
			putValue(Action.SHORT_DESCRIPTION, "Zamknij sklep - zakończ sprzedaż.");
		}
		
		public void actionPerformed(ActionEvent event)
		{
			if (shop.isOpen())
			{
				getJMenuBar().getMenu(0).getItem(0).setEnabled(true);
				getJMenuBar().getMenu(0).getItem(1).setEnabled(false);
				getJMenuBar().getMenu(2).getItem(0).setEnabled(true);
				addItemButton.setEnabled(false);
				payButton.setEnabled(false);
				shop.closeShop();
				
			}				
		}
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
	
	class StockViewAction extends AbstractAction
	{		
		public void actionPerformed(ActionEvent event)
		{			
			StockDialog stockDialog = new StockDialog(ShopFrame.this);
			stockDialog.setVisible(true);
		}
	}
	
	class StockAddAction extends AbstractAction
	{
		private JTable orderTable;
		private StockDialog container;
		private Shop currentShop;
		
		public StockAddAction(JTable table, StockDialog owner)
		{
			orderTable = table;
			container = owner;
			currentShop = shop;
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
				int result = currentShop.addItem(item, false);
				
				// if item is new in the stock, add row
				if (result > 0)
				{
					int lp = currentShop.getStock().getSize() + 1;
					Object[] rowToAdd = new Object[]{lp, EAN, name, price, VAT, priceB, amount};
					model.addRow(rowToAdd);
				}				
				//if item is already in stock, add to its amount
				else if (result == 0)
				{
					int i;
					int j = model.getRowCount();
					for (i = 0; i < j; i++)
					{
						Long tean = (Long) model.getValueAt(i, 1);
						if (EAN.compareTo(tean) == 0)
						{
							Integer cA = (Integer) model.getValueAt(i, 6);
							model.setValueAt(cA + amount, i, 6);
						}
					}
				}				
				//warning when no price or name given when it's needed
				else if (result == -1)
				{
					JOptionPane.showMessageDialog(ShopFrame.this, "Błędna nazwa, cena lub VAT!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
				}				
			}			
			//warning when bad input in TextFields
			catch (NumberFormatException e)
			{
				JOptionPane.showMessageDialog(ShopFrame.this, "Wpisz poprawną wartość!", "Uwaga!", JOptionPane.WARNING_MESSAGE);
			}
			
		}
	}
	
	class DayReportAction extends AbstractAction
	{
		
		public void actionPerformed(ActionEvent event)
		{
			DayReportDialog reportDialog = new DayReportDialog(ShopFrame.this);
			reportDialog.setVisible(true);
		}
	}

	class MonthReportAction extends AbstractAction
	{
		
		public void actionPerformed(ActionEvent event)
		{
			MonthReportDialog reportDialog = new MonthReportDialog(ShopFrame.this);
			reportDialog.setVisible(true);
		}
	}
	

}	
