/**
 * An item which can be added to inventory
 * @authors Greg Edwards
 * @version 1.0
 */
 
 package application;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.ObjectOutputStream;

public class Item implements IProduct
{
	/** Name of the item */
	private String _name;
	/** Weight of the item in pounds */
	private double _weight = 0;
	/** The price of the item in dollars before markup */
	private double _wholesalePrice = 0;
	/** Quantity of item available */
	private int _quantity = 0;
	/** Retail price of the item based on storage and markup */
	private double _retailPrice = 0;
	/** Storage cost of storing one unit of item */
	private double _storageCost = 0;
	/** Helper to bind property changes to inventory */
	private final PropertyChangeSupport _pcs = new PropertyChangeSupport(this);
	/** Markup factor for determining item price */
	private static final double s_markupFactor = 1.85;
	/** The per pound cost of storing an item */
	private static final double s_storageRate = 4.00;
	
	/** Constructor */
	public Item(String name, double weight, double price, int quantity)
	{
		_name = name;
		setWeight(weight);
		setWholesalePrice(price);
		setQuantityInStock(quantity);
	}
	
	@Override
	public String getName()
	{
		return _name;
	}
	
	@Override
	public void setName(String name)
	{
		if (_name != name)
		{
			_pcs.firePropertyChange(new PropertyChangingEvent(this, "name", _name, name));
			String oldName = _name;
			_name = name;
			_pcs.firePropertyChange(new PropertyChangedEvent(this, "name", oldName, _name));
		}
	}
	
	@Override
	public double getWeight()
	{
		return _weight;
	}
	
	@Override
	public void setWeight(double weight)
	{
		if (!canSetWeight(weight))
		{
			throw new IllegalArgumentException("Weight cannot be less than or equal to 0");
		}
		
		if (_weight != weight)
		{
			double oldWeight = _weight;
			_weight = weight;
			_pcs.firePropertyChange(new PropertyChangedEvent(this, "weight", oldWeight, _weight));
			updateStorageCost();
		}
	}
	
	/**
	 * Helper function to validate item weight
	 * @param weight The weight to validate
	 * @returns True if weight is valid
	 */
	private boolean canSetWeight(double weight)
	{
		return weight > 0;
	}
	
	/**
	 * Helper function to set storage cost based on weight and storage factor.
	 */
	private void updateStorageCost()
	{
		_storageCost = _weight * s_storageRate;
		updateRetailPrice();
	}
	
	/**
	 * Helper function to calculate retail price based on wholesale price, markup, and storage costs.
	 */
	private void updateRetailPrice()
	{
		double oldPrice = _retailPrice;
		_retailPrice = _wholesalePrice * s_markupFactor + _storageCost;
		_pcs.firePropertyChange(new PropertyChangedEvent(this, "retailPrice", oldPrice, _retailPrice));
	}
	
	@Override
	public double getWholesalePrice()
	{
		return _wholesalePrice;
	}
	
	@Override
	public void setWholesalePrice(double wholesalePrice)
	{
		if (!canSetWholesalePrice(wholesalePrice))
		{
			throw new IllegalArgumentException("Wholesale price cannot be negative");
		}
		
		if (_wholesalePrice != wholesalePrice)
		{
			double oldPrice = _wholesalePrice;
			_wholesalePrice = wholesalePrice;
			updateRetailPrice();
			_pcs.firePropertyChange(new PropertyChangedEvent(this, "wholesalePrice", oldPrice, _wholesalePrice));
		}
	}
	
	/**
	 * Helper function to validate item wholesale price.
	 * @param price the price to validate
	 * @returns True if price is valid
	 */
	private boolean canSetWholesalePrice(double price)
	{
		return price >= 0;
	}
	
	@Override
	public int getQuantityInStock()
	{
		return _quantity;
	}
	
	@Override
	public void setQuantityInStock(int QuantityInStock)
	{
		if (!canSetQuantityInStock(QuantityInStock))
		{
			throw new IllegalArgumentException("Quantity cannot be negative.");
		}
		
		int oldQuantity = _quantity;
		_quantity = QuantityInStock;
		_pcs.firePropertyChange(new PropertyChangedEvent(this, "quantity", oldQuantity, _quantity));
	}
	
	/**
	 * @param quantity The quantity to validate
	 * @returns True if quantity is valid
	 */
	private boolean canSetQuantityInStock(int quantity)
	{
		return quantity >= 0;
	}
	
	@Override
	public double getRetailPrice()
	{
		return _retailPrice;
	}
	
	@Override
	public double getStorageCost()
	{
		return _storageCost;
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		_pcs.addPropertyChangeListener(listener);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		_pcs.removePropertyChangeListener(listener);
	}
	
	/**
	 * Gets Item data as string for serialization to CSV.
	 * If name contains commas (,) or double-quotes ("), name is enclosed in double-quotes
	 * and any double-quote literals are replaced with pair of double quotes ("").
	 * @returns Comma separated string of item data
	 */
	public String toString()
	{
		return escapeCommasAndQuotes(_name) + "," 
			+ String.valueOf(_weight) + "," 
			+ String.valueOf(_wholesalePrice) + "," 
			+ String.valueOf(_quantity);
	}
	
	/**
	 * If input string contians commas or double-quotes, enclose input in double-quotes and 
	 * replace any double-quote literals with pair of double-quotes.
	 * @param str The string to process
	 * @returns The string with characters escaped if necessary
	 */
	private String escapeCommasAndQuotes(String str)
	{
		if (!str.contains(",") && !str.contains("\""))
		{
			return str;
		}
		
		return "\"" + str.replace("\"", "\"\"") + "\"";
	}
}