package pl.konriz.shop;

/**
 * connects Item with Amount; used in buying and stocking items
 * @author konriz
 * 
 */
public class ItemPack {

	private Item itemSold;
	private int itemAmount;
	
	public ItemPack(Item item, int amount)
	{
		itemSold = item;
		itemAmount = amount;
	}
	
	public Item getItem()
	{
		return itemSold;
	}
	
	public int getAmount()
	{
		return itemAmount;
	}
	
	public double getValue()
	{
		double value = this.getAmount() * this.getItem().getPrice();
		return value;
	}
	
	public double getVAT()
	{
		double v = getValue() * this.getItem().getVAT();
		double vat = Math.round(v * 100.0)/100.0;		 
		return vat;
	}
	
	public double getValueB()
	{
		double valueB = getValue() + getVAT();
		return Math.round(valueB * 100.0) / 100.0;		
	}
	
	public void setAmount(int amount)
	{
		itemAmount = amount;
	}
	
	public void addAmount(int amount)
	{
		itemAmount += amount;
	}
}
