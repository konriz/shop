package pl.konriz.shop;
/**
 * Item with its EAN, name, price and VAT;
 * @author konriz
 * 
 */
class Item {
	
	private long ean;
	private String name;
	private double price;
	private double priceB;
	private double VAT;
	
	//full constructor: EAN, name, price;
	public Item(long e, String n, double p, double v)
	{
		ean = e;
		name = n;
		price = p;
		VAT = v;
		priceB = price + p * v;
	}
	
	public Item(long e, String n, double p)
	{
		this(e, n, p, 0.23);
	}


	public long getEan()
	{
		return ean;
	}
	
	public void setEan(long Ean)
	{
		ean = Ean;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String n)
	{
		name = n;
	}
	
	public double getPrice()
	{
		return price;
	}
	
	
	public void setPrice(double newPrice)
	{
		price = newPrice;
	}
	
	public double getVAT()
	{
		return VAT;
	}
	
	public void setVAT(double v)
	{
		VAT = v;
	}
	
	public double getPriceB()
	{
		return Math.round(priceB * 100.0)/100.0;
	}
}
