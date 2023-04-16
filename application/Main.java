package application;

public class Main
{
	public static void main(String[] args) throws DuplicateProductNameException
	{
		String[] names = {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
		Inventory<Item> inventory = new Inventory<>();
		for (int i=1; i <= 10; i++)
		{
			inventory.add(new Item(names[i-1], (double)i, (double)i, i));
		}
		
		for (Item i : inventory)
		{
			System.out.println(i.getName());
		}
		
		System.out.println();
		
		for (Item i : inventory.getSortedProductsByName())
		{
			System.out.println(i.getName());
		}
		
	}
	
	private static void report(Inventory i)
	{
		System.out.println("Total products: " + i.getTotalProductsInStock());
		System.out.println("Total items: " + i.getTotalItemsInStock());
		System.out.printf("Total wholesale price: %.2f\n",i.getTotalWholesalePrice());
		System.out.printf("Total retail price: %.2f\n" ,i.getTotalRetailPrice());
		System.out.println();
	}
}