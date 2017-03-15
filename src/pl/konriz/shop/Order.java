package pl.konriz.shop;
import java.util.ArrayList;
import java.util.Date;

/**
 * gathers items ordered during one sale; counts itself value;
 * @author konriz
 *
 */
public class Order {
	
	private int serial;
	private ArrayList<ItemPack> itemSoldList;
	private double value;
	private double valueB;
	private double sumPaid;
	private double rest;
	private int itemCount;
	private int paid;
	private Date trStart;
	
	/**
	 * 
	 * @param s - current counter number
	 */
	public Order(int s)
	{
		serial = s;
		itemSoldList = new ArrayList<ItemPack>(30);
		value = 0;
		valueB = 0;
		sumPaid = 0;
		rest = 0;
		itemCount = 0;
		paid = 0;
		trStart = new Date();
	}
	
	public void setSerial(int s)
	{
		serial = s;
	}
	
	public int getSerial()
	{
		return serial;
	}
	
	public void addPosition(ItemPack itemSold)
	{
		itemSoldList.add(itemSold);
		value += itemSold.getValue();
		valueB += itemSold.getValueB();
		itemCount = itemSoldList.size();
	}
	
	public void addPosition(Item itemS, int amountS)
	{
		addPosition(new ItemPack(itemS, amountS));
	}
	
	public void pay(String type)
	{
		//TODO paid with what? money or card?
		if (type.equals("m"))
		{
			paid = 1;
		}
		else
		{
			paid = 2;
		}
	}
	
	public int checkPaid()
	{
		return paid;
	}
	
	public double getRest(double sumP)
	{
		sumPaid = sumP;
		rest = sumPaid - valueB;
		return Math.round(rest * 100.0) / 100.0;
	}
	
	public Date getStart()
	{
		return trStart;
	}
	
	public double getValue()
	{
		return value;
	}
	
	public double getValueB()
	{
		return valueB;
	}
	
	public int getItemCount()
	{
		return itemCount;
	}
	
	public ArrayList<ItemPack> getOrder()
	{
		return itemSoldList;
	}

	public ItemPack getOrder(int ind)
	{
		return getOrder().get(ind);
	}
}
