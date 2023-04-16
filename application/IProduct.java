/**
 * Interface for a product listed in a IProductList
 * @authors Greg Edwards
 * @version 1.0
 */
 
 package application;

import java.beans.PropertyChangeListener;

public interface IProduct extends IListenable
{
	/**
	 * Get method for product name
	 * @returns The name of the product
	 */
	String getName();
	
	/**
	 * Set method for product name
	 * @param name The name of the product
	 */
	void setName(String name);
	
	/**
	 * Get method for product weight in pounds
	 * @returns Product weight in pounds
	 */
	double getWeight();
	
	/**
	 * Set method for product weight in pounds
	 * @param weight Product weight in pounds
	 */
	void setWeight(double weight);
	
	/**
	 * Get method for price of the product prior to any markup
	 * @returns Dollar value of the product prior to any markup
	 */
	double getWholesalePrice();
	
	/**
	 * Set method for price of the product prior to any markup
	 * @param wholesalePrice Dollar value of the product prior to any markup
	 * @throws IllegalArgumentException if value is negative
	 */
	void setWholesalePrice(double price);
	 
	/**
	 * Get method for product quantity available
	 * @returns Quantity of the product available
	 * @throws IllegalArgumentException if value is negative
	 */
	int getQuantityInStock();
	
	/**
	 * Set method for product quantity available
	 * @param quantity Quantity of the product available
	 */
	void setQuantityInStock(int quantity);
	
	/**
	 * Get method for retail price in dollars based on the markup and storage costs.
	 * Retail price is determined by storage costs plus 85 percent markup of wholesale price.
	 * @returns Retail price of the product in dollars
	 */
	double getRetailPrice();
	
	/**
	 * Get method for cost of storing one product in based on storage rate of 4.00 dollars per pound
	 * @returns Cost to ship one unit of the product
	 */
	double getStorageCost();
}