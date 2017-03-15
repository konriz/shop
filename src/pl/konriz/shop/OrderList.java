package pl.konriz.shop;

import java.util.Date;
import java.util.ArrayList;

public class OrderList 
{
	private Date dateOpen;
	private Date dateClose;
	private boolean closed;
	private ArrayList<Order> orders;
	private int ordersAmount;
	private double ordersValue;
	
	public OrderList()
	{
		dateOpen = new Date();
		orders = new ArrayList<Order>();
		closed = false;
	}
	
	public Date getDateOpen()
	{
		return dateOpen;
	}
	
	public Date getDateClose()
	{
		return dateClose;
	}
	
	public boolean isClosed()
	{
		return closed;
	}
	
	public void add(Order e)
	{
		orders.add(e);
	}
	
	public int getOrdersAmount()
	{
		ordersAmount = orders.size();
		return ordersAmount;
	}
	
	public double getOrdersValue()
	{
		ordersValue = 0.0;
		for (Order o : orders)
		{
			ordersValue += o.getValue();
		}
		return ordersValue;
	}
	
	public double getOrdersValueB()
	{
		ordersValue = 0.0;
		for (Order o : orders)
		{
			ordersValue += o.getValueB();
		}
		return ordersValue;
	}
	
	public void closeOrderList()
	{
		dateClose = new Date();
		orders.trimToSize();
		closed = true;
	}
}
