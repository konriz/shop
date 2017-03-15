package pl.konriz.shop;
import java.sql.*;
import java.util.ArrayList;

/**
 * DataBase import class;
 * Creates shelf from SQLite DB
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
	
	public Object[] getShopState(String log)
	{
		String name = "Sklep";
		String login = log;
		Double account = new Double(0.0);
		Integer counter = new Integer(0);
		openDB();
		try
		{
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(5);		
			rs = statement.executeQuery("SELECT * FROM sklep WHERE Login ='" + login + "'");

			while(rs.next())
			{				
				name = rs.getString("Name");
				account = rs.getDouble("Account");
				counter = rs.getInt("Counter");
			}
		}
		catch(SQLException e)
		{
			System.err.println(e.getMessage());
		}
		closeDB();
		Object[] ret = new Object[] {name, account, counter};
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
			rs = statement.executeQuery("SELECT * FROM sklep WHERE Login ='" + login + "'");

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
			rs = statement.executeQuery("select * from " + table);
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
		
	//TODO implement into gui
	public void syncDB(DBClass syncWith)
	{
		try
		{
			
			Shelf toDownload = syncWith.getShelf();
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
			closeDB();
		}
		catch (SQLException ex)
		{
			System.out.println(ex.getMessage());
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
	
	
	
}
