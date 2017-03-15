package pl.konriz.shop;

import java.util.ArrayList;
/**
 * container for managing stock - amount and type of items to sale
 * also used for importing/exporting itempacks between DBClasses
 * @author konriz
 *
 */
public class Shelf 
{
	
	private ArrayList<ItemPack> inventory;
	private int size;
	
	public Shelf()
	{
		inventory = new ArrayList<ItemPack>();
		size = inventory.size();
	}
	
	public Shelf(ArrayList<ItemPack> stock)
	{
		inventory = stock;
		size = inventory.size();
	}
	
	/**
	 * force add item
	 * @param item
	 */
	public void addItem(ItemPack item)
	{
		inventory.add(item);
	}
		
	public int getSize()
	{
		return this.size;
	}
	
	public ArrayList<ItemPack> getInventory()
	{
		return inventory;
	}
	
	public ItemPack getInventory(int index)
	{
		return inventory.get(index);
	}
	
	public ItemPack getInventory(long ean)
	{
		int ind = checkEAN(ean);
		
		if (ind > -1)
		{
			return inventory.get(ind);
		}
		else
		{
			return null;
		}
	}
	/**
	 * checks if EAN is duplicated in shelf;
	 * @param ean
	 * @return -1 if not duplicated, index of duplicate otherwise
	 */
	public int checkEAN(long ean)
	{
		int ind = -1;
		int i = 0;
		while (i < this.getSize())
		{	
			if (inventory.get(i).getItem().getEan() == ean)
				{
				ind = i;
				break;
				}
			i ++;
		}
		return ind;
	}
	
}
