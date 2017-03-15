package pl.konriz.shop;

import java.util.GregorianCalendar;
import java.util.ArrayList;
/**
 * collects orders from one shop session (ex. one day)
 * @author konriz
 *
 */
public class OrderList 
{
	private GregorianCalendar dateOpen;
	private GregorianCalendar dateClose;
	private boolean closed;
	private ArrayList<Order> orders;
	private int ordersAmount;
	private double ordersValue;
	private double ordersValueB;
	private double ordersMoney;
	private double ordersCard;
	
	public OrderList()
	{
		dateOpen = new GregorianCalendar();
		orders = new ArrayList<Order>();
		closed = false;
		ordersAmount = 0;
		ordersValue = 0.0;
		ordersValueB = 0.0;
		ordersMoney = 0.0;
		ordersCard = 0.0;
	}
	
	public GregorianCalendar getDateOpen()
	{
		return dateOpen;
	}
	
	public GregorianCalendar getDateClose()
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
		calculate();
	}
	
	public int getOrdersAmount()
	{
		return ordersAmount;
	}
	
	public double getOrdersValue()
	{
		return ordersValue;
	}
	
	public double getOrdersValueB()
	{
		return ordersValueB;
	}
	
	public double getOrdersCard()
	{	
		return ordersCard;
	}
	
	public double getOrdersMoney()
	{
		return ordersMoney;
	}
	
	public void calculate()
	{
		ordersAmount = 0;
		ordersValue = 0.0;
		ordersValueB = 0.0;
		ordersMoney = 0.0;
		ordersCard = 0.0;
		ordersAmount = orders.size();
		for (Order o : orders)
		{
			ordersValue += o.getValue();
			ordersValueB += o.getValueB();
			
			if (o.checkPaid() == 1)
			{
				ordersMoney += o.getValueB();
			}
			else if (o.checkPaid() == 2)
			{
				ordersCard += o.getValueB();
			}
		}
	}
	
	public void closeOrderList()
	{
		dateClose = new GregorianCalendar();
		orders.trimToSize();
		calculate();
		closed = true;
	}
}
