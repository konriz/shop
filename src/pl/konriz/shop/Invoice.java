package pl.konriz.shop;

/**
 * official way to add stock to shop
 */
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Invoice {

	private String serial;
	private boolean isAdded;
	private Shelf stock;
	private Shop shop;
	private String dateOpen;
	private String dateApplied;

	
	public Invoice(Shop owner, String sN, Shelf shelf,String dOpen, String dApplied)
	{
		shop = owner;
		serial = sN;
		stock = shelf;
		if (!dOpen.equals("Otwarta"))
		{
			dateOpen = dOpen;
			dateApplied = dApplied;
			if (!dApplied.equals("Otwarta"))
			{
				isAdded = true;
				shop.addInvoice(this);
			}
			else
			{
				isAdded = false;
				dateApplied = dApplied;
				shop.addInvoice(this);
			}
		}
	}
	
	public Invoice(Shop owner, String sN)
	{
		this(owner, sN, new Shelf(), "Otwarta", "Otwarta");
		dateOpen = formatDate(new GregorianCalendar());
		isAdded = false;
		shop.addInvoice(this);
	}

	public boolean isAdded()
	{
		return isAdded;
	}
	
	public Shop getShop()
	{
		return shop;
	}
	
	public String getSerial()
	{
		return serial;
	}
	
	public String getDateOpen()
	{
		return dateOpen;
	}

	public String getDateApplied()
	{
		return dateApplied;
	}
	
	public int addItem(ItemPack item, boolean duplicate)
	{

		Item it = item.getItem();
		long ean = it.getEan();
		double price = it.getPriceB();
		boolean name = (it.getName()).equals("Nazwa");
		Shelf baseStock = shop.getStock();
		//check if EAN is in dataBase
		int ind = baseStock.checkEAN(ean);
		if (ind == -1)
		{
			if (price == 0 || name)
			{
				return -1;
			}
			else
			{
				stock.addItem(item);
				return 0;
			}
		}	
		else
		{
			if (duplicate == false)
			{
				// zwiększ ilość produktu o danym ean
				item = new ItemPack(baseStock.getInventory(ean).getItem(), item.getAmount());								
				stock.addItem(item);
				return 1;
			}
			else 
			{
				if (price == 0 || name)
				{
					return -1;
				}
				else
				{
					stock.addItem(item);
					return 0;
				}
			}			
		}
	}

	
	public Shelf getStock()
	{
		return stock;
	}
	
	public void close()
	{
		isAdded = true;
		dateApplied = formatDate(new GregorianCalendar());
	}
	
	public String formatDate(GregorianCalendar date)
	{
		int day = date.get(Calendar.DAY_OF_MONTH);
		int month = date.get(Calendar.MONTH) + 1;
		int year = date.get(Calendar.YEAR);
		String dateString = day + "." + month +"."+year;
		return dateString;
	}
}
