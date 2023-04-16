/**
 * Interface for a product list containing IProducts.
 * @param <T> The class implementing IProduct that this list contains
 * @author Greg Edwards
 * @version 1.0
 */
 
package application;

import java.util.Collection;
import java.lang.Iterable;
import java.beans.PropertyChangeListener;

public interface IProductList<T extends IProduct> extends PropertyChangeListener
{
	/**
	 * Get method for the number of products in the list
	 * @returns Total number of products
	 */
	int getTotalProductsInStock();
	
	/**
	 * Get method for quantity of all products in list
	 * @returns Sum of the quantities of all products in list
	 */
	int getTotalItemsInStock();
	
	/**
	 * Get method for wholesale price of all products in list
	 * @returns Sum of the wholesalePrice * quantity of all products in list
	 */
	double getTotalWholesalePrice();
	
	/**
	 * Get method for total retail price of all products in list
	 * @returns Sum of the retailPrice * quantity of all products in list
	 */
	double getTotalRetailPrice();
	
	/**
	 * Add a new product to the list
	 * @param product The product to add
	 * @throws DuplicateProductNameException if the name of the product matches a product name already in the list
	 */
	void add(T prod) throws DuplicateProductNameException;
	
	/**
	 * Remove a product from list
	 * @param prod The product to remove
	 */
	void remove(T prod);
	
	/**
	 * Checks if the product is in list
	 * @param prod Product to check for in list
	 * @returns True if the product is in the list, false otherwise
	 */
	boolean contains(T prod);
	
	/**
	 * Get the product based on its name. Names must be unique within the list.
	 * @param name The name of the product to return
	 * @returns The product with a matching name or null if no such product exists in the list
	 */
	T get(String name);

	/**
	 * Returns products in list sorted order by name. Used for reporting.
	 * @returns Iterable collection of products in sorted order by name
	 */
	Iterable<T> getSortedProductsByName();
}