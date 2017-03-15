package pl.konriz.shop;
import java.util.ArrayList;

class Shop 
{
	
	private String name;
	private double account;
	private double money;
	private double card;
	private Shelf stock;
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
		//TODO import z DB
		money = 0.0;
		card = 0.0;
		stock = dataBase.getShelf();
		counter = (int) state[2];
		
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
		Object[] state = onlineBase.getShopState(log);
		dataBase.syncDB(onlineBase);
		stock = dataBase.getShelf();
		name = (String) state[0];
		account = (double) state[1];
		counter = (int) state[2];
		//TODO dataBase.syncDB(onlineBase);
		
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
	
	public Shelf getStock()
	{
		return stock;
	}
	
	public void setStock(Shelf st)
	{
		stock = st;
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
	}
	
	//start selling to customer
	public void makeOrder()
	{
		currentOrder = new Order(counter);
		counter ++;
		// TODO change DB counter
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
		double toPay = Math.round(currentOrder.pay()*100.0)/100.0;
		
		if (cardPayment = true)
		{
			card +=  toPay;
		}
		else
		{
			money += toPay;
		}
		
		account += toPay;
		dataBase.updateDB(currentOrder.getOrder());
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
		if (onlineBase != null)
		{
			onlineBase.syncDB(dataBase);
		}
	}
	
	/**
	 *  Adds ItemPack to dataBase, checks if input EAN exists and increments or adds duplicates
	 * @param item item pack of stock items
	 * @param duplicate "true" - adds new product when duplicated EAN input; "false" - adds amount to existing matching EAN product 
	 * @return -1 on input error, 0 on increment duplicated EAN, 1 on add new EAN, 2 on duplicate EAN;
	 */
	
	public int addItem(ItemPack item, boolean duplicate)
	{
		Item it = item.getItem();
		long ean = it.getEan();
		double price = it.getPriceB();	
		boolean name = (it.getName()).equals("Nazwa");
		//check if EAN is in dataBase
		int ind = stock.checkEAN(ean);
		if (ind == -1)
		{
			//dodaj nowy ean i nowy produkt
			if (price == 0 || name)
			{
				return -1;
			}
			
			else
			{
				stock.addItem(item);
				dataBase.addToDB(item);
				return  1;
			}
		}	
		else
		{
			if (duplicate == false)
			{
				// zwiększ ilość produktu o danym ean
				int amount = item.getAmount();
				stock.getInventory(ind).addAmount(amount);
				dataBase.updateDB(item);
				return 0;
			}
			else 
			{
				//dodaj nowy produkt na dublowanym ean
				if (price == 0 || name)
				{
					return -1;
				}
				
				else 
				{
				stock.addItem(item);
				dataBase.addToDB(item);
				return 2;
				}
			}			
		}
	}
}
