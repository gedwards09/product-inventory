/**
 * Container to hold text fields used for creating and editing items 
 * which can be added to inventory
 * @author Greg Edwards
 * @version 1.0
 */

package application;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TextField;

public class ItemControl
{
	/** TextField for setting item name */
	private TextField _nameField = new TextField();
	/** Text field for setting item weight */
	private TextField _weightField = new TextField();
	/** Text field for setting item wholesale price */
	private TextField _priceField = new TextField();
	/** Text field for setting item quantity */
	private TextField _quantityField = new TextField();
	/** Container to hold the item texts fields */
	private List<TextField> _fields = new ArrayList<>();
	
	/** Constructor */
	public ItemControl() 
	{
		_fields.add(_nameField);
		_fields.add(_weightField);
		_fields.add(_priceField);
		_fields.add(_quantityField);
	}
	
	/** 
	 * Get method for name text field
	 * @returns The name text field
	 */
	public TextField getNameField()
	{
		return _nameField;
	}
	
	/** 
	 * Get method for weight text field
	 * @returns The weight text field
	 */
	public TextField getWeightField()
	{
		return _weightField;
	}
	
	/** 
	 * Get method for wholesale price text field
	 * @returns The wholesale price text field
	 */
	public TextField getPriceField()
	{
		return _priceField;
	}
	
	/** 
	 * Get method for quantity text field
	 * @returns The quantity text field
	 */
	public TextField getQuantityField()
	{
		return _quantityField;
	}
	
	/**
	 * Sets the texts field to the corresponding properties of an item
	 * @param The item whose values to set to the text fields
	 */
	public void setText(Item item)
	{
		_nameField.setText(item.getName());
		_weightField.setText(String.valueOf(item.getWeight()));
		_priceField.setText(String.valueOf(item.getWholesalePrice()));
		_quantityField.setText(String.valueOf(item.getQuantityInStock()));
	}
	
	/**
	 * Creates a new item using the current values in the text fields
	 * @returns The item created
	 * @throws NumberFormatException if a text from a numeric text field 
	 * cannot be cast to the corresponding type
	 * @throws IllegalArgumentException if a numeric value cannot be set
	 * as the property of an item
	 * @throws DuplicateProductNameException
	 */
	public Item createItem() throws DuplicateProductNameException
	{
		String name = _nameField.getText();
		double weight = Double.parseDouble(_weightField.getText());
		double price = Double.parseDouble(_priceField.getText());
		int quantity = Integer.parseInt(_quantityField.getText());
		
		return new Item(name, weight, price, quantity);
	}
	
	public Item setItemProperties(Item item) throws DuplicateProductNameException
	{
		String name = _nameField.getText();
		item.setName(name);
		double weight = Double.parseDouble(_weightField.getText());
		item.setWeight(weight);
		double price = Double.parseDouble(_priceField.getText());
		item.setWholesalePrice(price);
		int quantity = Integer.parseInt(_quantityField.getText());
		item.setQuantityInStock(quantity);
		
		return item;
	}
	
	
	public void setDisable(boolean bool)
	{
		for (TextField field : _fields)
		{
			field.setDisable(bool);
		}
	}
	
	public void clearTextFields()
	{
		_nameField.clear();
		_weightField.clear();
		_priceField.clear();
		_quantityField.clear();
	}
	
	public void requestFocus()
	{
		_nameField.requestFocus();
	}
}