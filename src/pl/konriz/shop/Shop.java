package pl.konriz.shop;
import java.util.ArrayList;

/**
 * app's backend. gathers all informaction about stock, account, orders;
 * @author konriz
 *
 */
class Shop 
{
	
	private String name;
	private double account;
	private double money;
	private double card;
	private Shelf stock;
	private ArrayList<Invoice> invoiceList;
	private Order currentOrder;
	private OrderList currentList;
	private ArrayList<OrderList> allOrders;
	private int counter;
	private boolean open;
	private DBClass dataBase;
	private String login;
	private DBClass onlineBase;
		
	
	public Shop()
	{		
		dataBase = new DBClass();
		login = "Sklep1";
		Object[] state = dataBase.getShopState(login);
		onlineBase = null;		
		name = (String) state[0];
		account = (double) state[1];
		money = (double) state[2];
		card = (double) state[3];
		stock = dataBase.getShelf();
		counter = (int) state[4];
		invoiceList = new ArrayList<Invoice>();
		allOrders = new ArrayList<OrderList>();
		open = false;
		
		
	}
		
	// get/set methods
	public String getName()
	{
		return name;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public void setOnlineBase(DBClass onlineDB)
	{
		onlineBase = onlineDB;
	}
	
	public DBClass getOnlineBase(DBClass onlineDB)
	{
		return onlineBase;
	}
	
	public void setOnlineState(String log)
	{
		if (!dataBase.isUpdated())
		{
			onlineBase.syncDB(dataBase, log);
			dataBase.setUpdated(true);
		}
	}
	
	public double getAccount()
	{
		return account;
	}
	
	public void setAccount(double acc)
	{
		account = acc;
	}
	
	public double getMoney()
	{
		return money;
	}
	
	public double getCard()
	{
		return card;
	}
	
	public void setLogin(String log)
	{
		login = log;
	}
	
	public String getLogin()
	{
		return login;
	}
	
	public int getCounter()
	{
		return counter;
	}
	
	public Shelf getStock()
	{
		return stock;
	}
	
	public void setStock(Shelf st)
	{
		stock = st;
	}
	
	public ArrayList<Invoice> getInvoiceList()
	{
		return invoiceList;
	}
	
	public Invoice getInvoice(String serial)
	{
		Invoice inv = null;
		for (Invoice e : invoiceList)
		{
			if (e.getSerial().equals(serial))
			{
				inv = e;
			}
		}
		return inv;
	}
	
	public boolean isOpen()
	{
		return open;
	}
	
	public void setOpen(boolean state)
	{
		open = state;
	}
	//end of get/set methods
	

	
	//start daily order list
	public void openShop()
	{
		setOpen(true);
		currentList = new OrderList();
		makeOrder();
		dataBase.setUpdated(false);
	}
	

	
	//start selling to customer
	public void makeOrder()
	{
		currentOrder = new Order(counter);
		counter ++;
	}
	
	public ArrayList<OrderList> getAllOrders()
	{
		return allOrders;
	}
	
	public int getAllOrdersAmount()
	{
		return this.getAllOrders().size();
	}
	
	public double getAllOrdersValue()
	{
		ArrayList<OrderList> allOrders = this.getAllOrders();
		double value = 0.0;
		for (OrderList e : allOrders)
		{
			value += e.getOrdersValue();
		}
		return value;
	}
	
	public double getAllOrdersValueB()
	{
		ArrayList<OrderList> allOrders = this.getAllOrders();
		double valueB = 0.0;
		for (OrderList e : allOrders)
		{
			valueB += e.getOrdersValueB();
		}
		return valueB;
	}
		
	public OrderList getCurrentList()
	{
		return currentList;
	}
	
	public Order getCurrentOrder()
	{
		return currentOrder;
	}
	
	//add items to current order
	
	public boolean gatherItem(long ean, int am)
	{
		int ind = this.getStock().checkEAN(ean);
		if (ind > -1 && am > 0)
		{
			ItemPack stockItem= this.getStock().getInventory(ind);
			this.addToOrder(new ItemPack(stockItem.getItem(), am));
			stockItem.addAmount(-am);
			return true;
		}
		else return false;
	}
	
	public void addToOrder(ItemPack itemPack)
	{
		currentOrder.addPosition(itemPack);
	}
	
	//finish selling to customer
	public void sellOrder(boolean cardPayment)
	{
		double toPay = Math.round(currentOrder.getValueB()*100.0)/100.0;
		
		if (cardPayment == true)
		{
			currentOrder.pay("c");
			card +=  toPay;
		}
		else
		{
			currentOrder.pay("m");
			money += toPay;
		}
		
		account += toPay;
		dataBase.updateDB(this.getStock().getInventory());
		currentList.add(currentOrder);
		
	}
	
	
	
	//finish daily order list
	public void closeShop()
	{
		if (!currentList.isClosed() && currentList.getOrdersAmount() != 0)
		{
			currentList.closeOrderList();
			allOrders.add(currentList);			
		}		
		setOpen(false);
		dataBase.updateShopState(this);
		if (onlineBase != null)
		{
			onlineBase.syncDB(dataBase, login);
			dataBase.setUpdated(true);
		}
	}
	

	
	/**
	 *  Adds ItemPack to dataBase, checks if input EAN exists and increments or adds duplicates
	 * @param item item pack of stock items
	 * @param duplicate "true" - adds new product when duplicated EAN input; "false" - adds amount to existing matching EAN product 
	 * @param invoice - invoice which adds item to stock
	 * @return -1 on input error, 0 on increment duplicated EAN, 1 on add new EAN, 2 on duplicate EAN;
	 */
	
	public void updateStock(ItemPack item)
	{
		int ind = stock.checkEAN(item.getItem().getEan());
		int amount = item.getAmount();
		stock.getInventory(ind).addAmount(amount);
		dataBase.updateDB(stock.getInventory(ind));
	}
	
	public void addStock(ItemPack item)
	{
		stock.addItem(item);
		dataBase.addToDB(item);
	}
	
	public void getInvoices()
	{
		dataBase.getInvoices(this);
	}
	
	public void addInvoice(Invoice inv)
	{
		invoiceList.add(inv);
	}
	
	public void applyInvoice(Invoice inv, boolean duplicate)
	{
		for (ItemPack item : inv.getStock().getInventory())
		{
			Item it = item.getItem();
			long ean = it.getEan();
			//check if EAN is in dataBase
			int ind = stock.checkEAN(ean);
			if (ind == -1)
			{
				addStock(item);
			}	
			else
			{
				if (duplicate == false)
				{
					// zwiększ ilość produktu o danym ean
					updateStock(item);
				}
				else 
				{
					addStock(item);
				}			
			}
		}
		inv.close();
		dataBase.addInvoice(inv);
	}
}
