package pl.konriz.shop;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class Report {

	private double account = 0.0;
	private double money = 0.0;
	private double card = 0.0;
	private int ordersCount = 0;
	private ArrayList<OrderList> dateOrders;
	private GregorianCalendar dateStart;
	private GregorianCalendar dateStop;
	
	/**
	 * creates report from whole existence of shop till now
	 * @param shop
	 */
	public Report(Shop shop)
	{
		account = shop.getAccount();
		money = shop.getMoney();
		card = shop.getCard();
		ordersCount = shop.getCounter();
		dateOrders = shop.getAllOrders();
		if (!shop.getAllOrders().isEmpty())
		{
		dateStart = shop.getAllOrders().get(0).getDateOpen();
		dateStop = shop.getAllOrders().get(shop.getAllOrdersAmount()-1).getDateOpen();
		}
	}
	/**
	 * creates report from chosen day till now
	 * @param shop
	 * @param open
	 */
	public Report(Shop shop, GregorianCalendar start)
	{
		this(shop, start, shop.getAllOrders().get(shop.getAllOrdersAmount()-1).getDateOpen());
	}
	/**
	 * creates report from chosen day to chosen day
	 * @param shop
	 * @param open
	 * @param close
	 */
	public Report(Shop shop, GregorianCalendar start, GregorianCalendar stop)
	{
		dateStart = start;
		dateStop = stop;
		ArrayList<OrderList> allOrders = shop.getAllOrders();
		if (!allOrders.isEmpty())
		{
			for (int i=0; i < allOrders.size(); i++)
			{
				OrderList currentOrder = allOrders.get(i);
				GregorianCalendar currentOrderDate = currentOrder.getDateOpen();
				if (start.compareTo(currentOrderDate) >= 0 && stop.compareTo(currentOrderDate) <= 0)
				{
					ordersCount += currentOrder.getOrdersAmount();
					account += currentOrder.getOrdersValueB();
					money += currentOrder.getOrdersMoney();
					card += currentOrder.getOrdersCard();
				}
			}
		}
	}
	public double getAccount() {
		return account;
	}
	public double getMoney() {
		return money;
	}
	public double getCard() {
		return card;
	}
	public int getOrdersCount() {
		return ordersCount;
	}
	public ArrayList<OrderList> getDateOrders()
	{
		return dateOrders;
	}
	
	public GregorianCalendar getDateOpen() {
		return dateStart;
	}
	public GregorianCalendar getDateClose() {
		return dateStop;
	}
	
	
}
