package pl.konriz.shop;
import java.sql.*;
import java.util.ArrayList;

import javax.annotation.processing.SupportedSourceVersion;

/**
 * DataBase import class;
 * Creates shelf from SQLite DB
 * Stores invoices
 * TODO stores reports
 * @author konriz
 *
 */

public class DBClass {

	private ResultSet rs = null;
	private Shelf shelf;
	private Connection connection;
	private String path;
	private String table;
	
	public DBClass()
	{
		this("jdbc:sqlite:DB.sql","towar");	
	}
	
	public DBClass(String p, String tab)
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
		}			
		catch(ClassNotFoundException e)
		{
			System.out.println(e.getMessage());
		}

		
		shelf = new Shelf();	
		connection = null;
		path = p;
		table = tab;
	}
	
	
/**
 * Populates shop fields depending on dataBase values; will read from online DB;
 * 
 * @param log - shop login
 * @return shops name, account, orders counter;
 */
	
	public void updateShopState(Shop sh)
	{
		Shop shop = sh;
		String login = shop.getLogin();
		Double account = shop.getAccount();
		Double card = shop.getCard();
		Double money = shop.getMoney();
		Integer counter = shop.getCounter();
		openDB();
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);
			String query = "UPDATE data SET Account = "+ account +", Card = "+ card +", Money = "+ money +", Counter = "+ counter +" WHERE Login = '"+ login +"'";
			statement.executeUpdate(query);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}		
		closeDB();
		
	}
	
	public Object[] getShopState(String log)
	{
		String name = "Sklep";
		String login = log;
		Double account = new Double(0.0);
		Double card = new Double(0.0);
		Double money = new Double(0.0);
		Integer counter = new Integer(0);
		openDB();
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);		
			rs = statement.executeQuery("SELECT * FROM data WHERE Login ='" + login + "'");

			while(rs.next())
			{				
				name = rs.getString("Name");
				account = rs.getDouble("Account");
				card = rs.getDouble("Card");
				money = rs.getDouble("Money");
				counter = rs.getInt("Counter");

			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		closeDB();
		Object[] ret = new Object[] {name, account, money, card, counter};
		return ret;
	}
	
	/**
	 * 
	 * @param l - login
	 * @param p - password
	 * @return null if wrong login or passwd; path to db if ok
	 */
	
	public String authorizeDB(String l, String p)
	{
		String login = l;
		String password = p;
		String adress = "";
		openDB();
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);		
			rs = statement.executeQuery("SELECT * FROM data WHERE Login ='" + login + "'");

			while(rs.next())
			{
				String pwd = rs.getString("Password");
				if (password.equals(pwd))
				{
					adress = rs.getString("Adress");
//					String name = rs.getString("Name");
//					double account = rs.getDouble("Account");
				}
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		closeDB();
		return adress;		
	}
		
	public void openDB()
	{
		try
		{
			connection = DriverManager.getConnection(path);
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	public void createShelf()
	{
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);		
			rs = statement.executeQuery("SELECT * FROM " + table);
			long ean;
			String name;
			double price;
			double VAT;
			Item item;
			int amount;
			ItemPack itemPack;
			ArrayList<ItemPack> tempSh = new ArrayList<ItemPack>();
			while (rs.next())
			{
				ean = rs.getLong("EAN");
				name = rs.getString("Name");
				price = rs.getDouble("Price");
				VAT = rs.getDouble("VAT");
				amount = rs.getInt("Amount");				
				item = new Item(ean, name, price, VAT);
				itemPack = new ItemPack(item, amount);
				tempSh.add(itemPack);
			}
			this.shelf = new Shelf(tempSh);
		}
		
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void closeDB()
	{
		try
		{
			if (connection != null) connection.close();
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	public void updateDB(ArrayList<ItemPack> st)
	{
		openDB();
		try
		{ 
			ArrayList<ItemPack> stock = st;
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);
			for (ItemPack e : stock)
			{
				int amount = e.getAmount();
				long ean = e.getItem().getEan();
				String upSta = "UPDATE " + table + " SET Amount = " + amount + " WHERE Amount !=" + amount + " AND EAN ==" + ean;				
				statement.executeUpdate(upSta);
			}
			
		}
		catch (SQLException e)
		{
			System.err.println(e.getMessage());
		}
		closeDB();		
	}
	
	// update przy dublowanym EAN!
	public void updateDB(ItemPack itemPack)
	{
		ArrayList<ItemPack> st = new ArrayList<ItemPack>();
		st.add(itemPack);
		updateDB(st);				
	}
	
	public void addToDB(ItemPack itemPack)
	{
		openDB();
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);
			Item item = itemPack.getItem();
			long ean = item.getEan();
			String name = item.getName();
			double price = item.getPrice();
			double VAT = item.getVAT();
			int amount = itemPack.getAmount();
			String query = "INSERT INTO " + table + " (EAN, Name, Price, VAT, Amount)"
					+ " VALUES ("+ ean + ",'" + name + "'," + price+ "," + VAT +"," + amount +")";
			statement.executeUpdate(query);
			
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		closeDB();
	}
		
	public void syncDB(DBClass syncWith, String login)
	{
		try
		{
			
			Shelf toDownload = syncWith.getShelf();
			Object[] data = syncWith.getShopState(login);
			openDB();
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);
			for (ItemPack e : toDownload.getInventory())
			{
				Item item = e.getItem();
				long ean = item.getEan();
				String name = item.getName();
				double price = item.getPrice();
				double VAT = item.getVAT();
				int amount = e.getAmount();
				String query = "INSERT OR REPLACE INTO " + table + " (EAN, Name, Price, VAT, Amount)"
						+ " VALUES ("+ ean + ",'" + name + "'," + price+ "," + VAT +"," + amount +")";
				statement.executeUpdate(query);
			}
						
			String query = "UPDATE data SET Account = "+ data[1] +", Card = "+ data[3] +", Money = "+ data[2] +", Counter = "+ data[4] +" WHERE Login = '" + login +"'";
			statement.executeUpdate(query);
			
			closeDB();
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		
		
	}
	
	public void setUpdated(boolean state)
	{
		int ind = 0;
		if (state == true)
		{
			ind = 1;
		}
		
		openDB();
		try
		{
			String update = "UPDATE data SET Updated = " + ind;
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);
			statement.executeUpdate(update);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		closeDB();
	}
	
	public boolean isUpdated()
	{
		openDB();
		int updated = -1;
		try
		{
			Statement statement = connection.createStatement();
			String query = "SELECT * FROM data";
			ResultSet rs = statement.executeQuery(query);
			while (rs.next())
			{
				updated = rs.getInt("Updated");
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
		closeDB();
		if (updated == 1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * creates shelf from database 
	 */
	public Shelf getShelf()
	{
		openDB();
		this.createShelf();
		closeDB();
		return this.shelf;
	}
	
	public void addInvoice(Invoice inv)
	{
		openDB();
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);
			String invoiceSerial = inv.getSerial();
			String openDate = inv.getDateOpen();
			String applyDate = inv.getDateApplied();
			ArrayList<ItemPack> invoiceStock = inv.getStock().getInventory();
			for(ItemPack e : invoiceStock)
			{
				Item item = e.getItem();
				long ean = item.getEan();
				String name = item.getName();
				double price = item.getPrice();
				double VAT = item.getVAT();
				int amount = e.getAmount();
				String query = "INSERT INTO faktura (Serial, EAN, Name, Price, VAT, Amount, DateOpen, DateApply)"
						+ " VALUES ('" + invoiceSerial +"',"+ ean + ",'" + name + "'," + price+ "," + VAT +"," + amount + ",'" + openDate + "','" + applyDate + "')";
				statement.executeUpdate(query);
			}
			closeDB();	
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void getInvoices(Shop shop)
	{
		openDB();
		ArrayList<String> serialList = new ArrayList<String>();
		ArrayList<Shelf> shelfList = new ArrayList<Shelf>();
		ArrayList<String> openList = new ArrayList<String>();
		ArrayList<String> applyList = new ArrayList<String>();
		Shelf currentShelf = null;
		String dateOpen;
		String dateApply;
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);
			rs = statement.executeQuery("SELECT * FROM faktura");
			
			while (rs.next())
			{
				String serial = rs.getString("Serial");
				long ean = rs.getLong("EAN");
				String name = rs.getString("Name");
				double price = rs.getDouble("Price");
				double VAT = rs.getDouble("VAT");
				int amount = rs.getInt("Amount");	
				dateOpen = rs.getString("DateOpen");
				dateApply = rs.getString("DateApply");
				Item item = new Item(ean, name, price, VAT);
				ItemPack itemPack = new ItemPack(item, amount);
				
				if (!serialList.contains(serial))
				{
					serialList.add(serial);
					currentShelf = new Shelf();
					shelfList.add(currentShelf);
					openList.add(dateOpen);
					applyList.add(dateApply);
				}
				currentShelf.addItem(itemPack);
				
			}
			closeDB();	
			
			for (int i = 0; i < serialList.size(); i++)
			{
				new Invoice(shop, serialList.get(i), shelfList.get(i), openList.get(i), applyList.get(i));
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	public void addReport()
	{
		
	}
}


