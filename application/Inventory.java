/**
 * Inventory class for tracking items
 * @authors Greg Edwards
 * @version 1.0
 */
 
 package application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Inventory<T extends IProduct> implements IProductList<T>, Iterable<T>
{
	/** Map of items indexed by item ID in order added */
	private SortedMap<Integer, T> _itemsById = new TreeMap<>();
	/** Index of item ID by item */
	private Map<T, Integer> _itemIndex = new HashMap<>();
	/** Sorted map of items index by name */
	private SortedMap<String, T> _itemsByName = new TreeMap<>();
	/** Next available item ID */
	private int _nextId = 0;
	/** total number of products in inventory */
	private int _totalProducts = 0;
	/** total number of items in stock in inventory */
	private int _itemsInStock = 0;
	/** Total wholesale price of all items in inventory */
	private double _totalWholesalePrice = 0;
	/** total retail price of all items in inventory */
	private double _totalRetailPrice = 0;
	
	/** Constructor */
	public Inventory() { }
	
	@Override
	public int getTotalProductsInStock()
	{
		return _totalProducts;
	}
	
	@Override
	public int getTotalItemsInStock()
	{
		return _itemsInStock;
	}
	
	@Override
	public double getTotalWholesalePrice()
	{
		return _totalWholesalePrice;
	}
	
	@Override
	public double getTotalRetailPrice()
	{
		return _totalRetailPrice;
	}
	
	@Override
	public void add(T prod)
	{
		if (_itemsByName.containsKey(prod.getName()))
		{
			throw new DuplicateProductNameException(prod.getName());
		}
		
		_itemsById.put(_nextId, prod);
		_itemIndex.put(prod, _nextId);
		_itemsByName.put(prod.getName(), prod);
		_nextId++;
		
		int quantity = prod.getQuantityInStock();
		_totalProducts++;
		_itemsInStock += quantity;
		_totalWholesalePrice += quantity * prod.getWholesalePrice();
		_totalRetailPrice += quantity * prod.getRetailPrice();
		
		prod.addPropertyChangeListener((PropertyChangeListener)this);
	}
	
	@Override
	public void remove(T prod)
	{
		if (contains(prod))
		{
			_itemsById.remove(_itemIndex.get(prod));
			_itemIndex.remove(prod);
			_itemsByName.remove(prod.getName());
			
			int quantity = prod.getQuantityInStock();
			_totalProducts--;
			_itemsInStock -= quantity;
			_totalWholesalePrice -= quantity * prod.getWholesalePrice();
			_totalRetailPrice -= quantity * prod.getRetailPrice();
			
			prod.removePropertyChangeListener((PropertyChangeListener)this);
		}
	}
	
	@Override
	public boolean contains(T prod)
	{
		return _itemIndex.containsKey(prod);
	}
	
	@Override
	public T get(String name)
	{
		return _itemsByName.get(name);
	}
	
	@Override
	public Iterator<T> iterator()
	{
		return _itemsById.values().iterator();
	}
	
	@Override
	public void forEach(Consumer<? super T> action)
	{
		for (T item : this)
		{
			action.accept(item);
		}
	}
	
	@Override
	public Spliterator<T> spliterator()
	{
		return _itemsById.values().spliterator();
	}
	
	@Override
	public Iterable<T> getSortedProductsByName()
	{
		return (Iterable<T>)_itemsByName.values();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		T item = (T)e.getSource();
		String property = e.getPropertyName();
		if (e instanceof PropertyChangingEvent)
		{
			if (property == "name")
			{
				String newName = (String)e.getNewValue();
				if (_itemsByName.containsKey(newName))
				{
					throw new DuplicateProductNameException(newName);
				}
			}
		}
		else if (e instanceof PropertyChangedEvent)
		{
			if (property == "name")
			{
				String oldName = (String)e.getOldValue();
				_itemsByName.remove(oldName);
				_itemsByName.put(item.getName(), item);
			}
			else if (property == "quantity")
			{
				int oldQuantity = (int)e.getOldValue();
				int newQuantity = item.getQuantityInStock();
				double wholesalePrice = item.getWholesalePrice();
				double retailPrice = item.getRetailPrice();
				
				_itemsInStock -= oldQuantity;
				_totalWholesalePrice -= oldQuantity * wholesalePrice;
				_totalRetailPrice -= oldQuantity * retailPrice;
				
				_itemsInStock += newQuantity;
				_totalWholesalePrice += newQuantity * wholesalePrice;
				_totalRetailPrice += newQuantity * retailPrice;
			}
			else if (property == "wholesalePrice")
			{
				updateTotalWholesalePrice(item.getQuantityInStock(), (double)e.getOldValue(), item.getWholesalePrice());
			}
			else if (property == "retailPrice")
			{
				updateTotalRetailPrice(item.getQuantityInStock(), (double)e.getOldValue(), item.getRetailPrice());
			}
		}
	}
	
	/**
	 * Updates totalWholesalePrice based on quantity and change in item price
	 * @param quantity the quantity of the item
	 * @param oldPrice the previous price of the item
	 * @param newPrice the new price of the item
	 */
	private void updateTotalWholesalePrice(int quantity, double oldPrice, double newPrice)
	{
		_totalWholesalePrice -= quantity * oldPrice;
		_totalWholesalePrice += quantity * newPrice;
	}
	
	/**
	 * Updates totalRetailPrice based on quantity and change in item price
	 * @param quantity the quantity of the item
	 * @param oldPrice the previous price of the item
	 * @param newPrice the new price of the item
	 */
	private void updateTotalRetailPrice(int quantity, double oldPrice, double newPrice)
	{
		_totalRetailPrice -= quantity * oldPrice;
		_totalRetailPrice += quantity * newPrice;
	}
}