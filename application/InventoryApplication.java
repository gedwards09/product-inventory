/**
 * Main driver of inventory application. Displays UI for adding and viewing items in inventory.
 * @author Greg Edwards
 * @version 1.0
 */

package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InventoryApplication extends Application
{
	/** Backing inventory for items */
	private final Inventory<Item> _inventory = new Inventory<>();
	/** True if displayed items are sorted by name, otherwise sort by order added */
	private boolean _sortItemsByName = false;
	/** Observable list of item names in inventory */
    private final ObservableList<String> _itemNames = FXCollections.observableArrayList();
	/** ListView for displaying items in UI */
	private final ListView _listView = new ListView();
	/** Container for the text fields for adding items */
	private final ItemControl _control = new ItemControl();
	/** Reference to home stage where most user actions occur*/
	private Stage _homeStage;
	/** List view width */
	private static final int s_listViewWidth = 200;
	/** List view height */
	private static final int s_listViewHeight = 250;
	/** Vertical gap spacing */
	private static final int s_vSpace = 10;
	/** Horizontal gap spacing */
	private static final int s_hSpace = 10;
	/** Vertical inset pad spacing */
	private static final int s_vPad = 15;
	/** Horizontal inset pad spacing */
	private static final int s_hPad = 12;
	/** Vertical inset spacing for grid */
	private static final int s_gridVPad = 25;
	/** Horizontal inset spacing for grid */
	private static final int s_gridHPad = 25;
	/** Width for the navigation panel buttons */
	private static final int s_navButtonWidth = 100;
	/** Height for the navigation panel buttons*/
	private static final int s_navButtonHeight = 20;
	/** Width for item change buttons */
	private static final int s_itemButtonWidth = 80;
	/** Height for item change buttons */
	private static final int s_itemButtonHeight = 20;
	/** Application name for display on main stage */
	private static final String s_appTitle = "Inventory Application";
	
	/** Main entry point for inventory application */
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage stage)
	{
		_homeStage = stage;
		Parent ui = createUI();
		_control.requestFocus();
		Scene scene = new Scene(ui);
		stage.setScene(scene);
		stage.setTitle(s_appTitle);
		stage.show();
	}
	
	/**
	 * Create layout for the initial UI scene on home stage
	 * @returns The initial UI layout
	 */
	private Parent createUI()
	{
		BorderPane border = new BorderPane();
		border.setTop(createHeader());
		border.setLeft(createControlToolbar());
		border.setCenter(makeItemControlPanel(_control));
		border.setRight(addInventoryListViewNode());
		
		return border;
	}
	
	/**
	 * create the header panel for home stage UI scene
	 * @returns The Node containing header element
	 */
	private Node createHeader()
	{
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(s_vPad, s_hPad, s_vPad, s_hPad));
		
		return hbox;
	}
	
	/**
	 * Create the left side control bar
	 * @returns The Node containing the left side control bar
	 */
	private Node createControlToolbar()
	{
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(s_vPad, s_hPad, s_vPad, s_hPad));
		vbox.setSpacing(s_vSpace);
		
		Button report = makeReportButton();
		Button import_ = makeImportButton();
		Button export = makeExportButton();
		
		Button close = makeButton("Close");
		close.setPrefSize(s_navButtonWidth, s_navButtonHeight);
		close.setOnAction(e -> _homeStage.close());
		
		
		VBox footer = new VBox();
		footer.setAlignment(Pos.BOTTOM_CENTER);
		VBox.setVgrow(footer, Priority.ALWAYS);
		footer.getChildren().add(close);
		
		vbox.getChildren().addAll(
			report,
			import_,
			export,
			footer
		);
		
		return vbox;
	}
	
	/**
	 * Create the report button to display inventory totals
	 * @returns The report button element
	 */
	private Button makeReportButton()
	{
		Button report = makeButton("Report");
		report.setPrefSize(s_navButtonWidth, s_navButtonHeight);
		report.setOnAction(
			new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent e)
				{
					final Stage dialog = new Stage();
					dialog.initModality(Modality.APPLICATION_MODAL);
					dialog.initOwner(_homeStage);
					VBox dialogVbox = new VBox(s_vSpace);
					dialogVbox.setPadding(new Insets(s_vPad, s_hPad, s_vPad, s_hPad));
					Text title = new Text("Inventory Report");
					title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
					dialogVbox.getChildren().addAll(
						title,
						new Text("Total Products: " + _inventory.getTotalProductsInStock()),
						new Text(String.format("Items in Stock: %,d",_inventory.getTotalItemsInStock())),
						new Text(String.format("Total Wholesale Price: $%,.2f", _inventory.getTotalWholesalePrice())),
						new Text(String.format("Total Retail Price: $%,.2f", _inventory.getTotalRetailPrice()))
					);
					Scene dialogScene = new Scene(dialogVbox, 300, 200);
					dialog.setScene(dialogScene);
					dialog.show();
				}
			}
		);
		
		return report;
	}
	
	/**
	 * Create buttons with uniform size
	 * @returns The button element
	 */
	private Button makeButton(String name)
	{
		Button button = new Button(name);
		return button;
	}
	
	/**
	 * Create the import button
	 * @returns The import button element
	 */
	private Button makeImportButton()
	{
		Button importButton = makeButton("Import");
		importButton.setPrefSize(s_navButtonWidth, s_navButtonHeight);
		importButton.setOnAction(e -> onClickImportButton(e));
		
		return importButton;
	}
	
	/**
	 * Event handler for the event triggered when import button clicked
	 * @param e The event that was triggered
	 */
	private void onClickImportButton(ActionEvent e)
	{
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		ExtensionFilter csvFilter = new ExtensionFilter("CSV files (*.csv)", "*.csv");
		fc.getExtensionFilters().add(csvFilter);
		List<File> fileList = fc.showOpenMultipleDialog(_homeStage);
		if (fileList != null)
		{
			for (File file : fileList)
			{
				importInventory(file);
			}
		}
	}
	
	/**
	 * Method to read a comma-separated text file, create the items specified,
	 * and add them to the inventory
	 * @param file The comma-separated file to read
	 */
	private void importInventory(File file)
	{
		try
		{
			String line;
			String[] args;
			String name;
			double weight;
			double price;
			int quantity;
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);
			while ((line = reader.readLine()) != null)
			{
				args = splitCommaDelimitedString(line);
				name = args[0];
				weight = Double.parseDouble(args[1]);
				price = Double.parseDouble(args[2]);
				quantity = Integer.parseInt(args[3]);
				_inventory.add(new Item(name, weight, price, quantity));
			}
		}
		catch (FileNotFoundException e)
		{
			showAlertPopup("File Import Failed", e.getMessage());
		}
		catch(IOException e)
		{
			showAlertPopup("File Import Failed", e.getMessage());
		}
		catch (NumberFormatException e)
		{
			showAlertPopup("Invalid Number Format", e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			showAlertPopup("Invalid Item Data", e.getMessage());
		}
		catch (DuplicateProductNameException e)
		{
			showAlertPopup("Duplicate Product Name", "Item \"" + e.getDuplicateName() + "\" already exists in inventory");
		}
		
		updateListViewContents();
	}
	
	/**
	 * Splits a comma-delimited string into the properties of the item 
	 * to deserialize from csv: name, weight, price, and quantity
	 * Name field is enclosed in double-quotes if the item name contains 
	 * commas or double-quotes which must be unescaped.
	 * Other properties don't contain commas or quotes and do not need to be unescaped.
	 * @param line The comma-delimited string to parse
	 * @returns String array containing name, weight, price, and quantity of the item being deserialized.
 	 */
	private String[] splitCommaDelimitedString(String line)
	{
		if (!line.contains("\""))
		{
			return line.split(",");
		}
		
		String[] arr = new String[4];
		int firstQuoteIndex = 0;
		int lastQuoteIndex = line.lastIndexOf("\"");
		int nextPieceStartIndex = lastQuoteIndex + 2;
		arr[0] = line.substring(firstQuoteIndex + 1, lastQuoteIndex).replace("\"\"", "\"");
		int index = 1;
		for (String piece : line.substring(nextPieceStartIndex).split(","))
		{
			arr[index] = piece;
			index++;
		}
		
		return arr;
	}
	
	/**
	 * Creates and formats a popup to display error message
	 * @param header The alert header to use
	 * @param message The error message to display
	 */
	private void showAlertPopup(String header, String message)
	{
		Alert a = new Alert(Alert.AlertType.ERROR);
		a.setTitle("Error");
		a.setHeaderText(header);
		a.setContentText(message);
		a.showAndWait();
	}
	
	/**
	 * Method to update values stored in the _itemNames observable list when inventory changes
	 */
	private void updateListViewContents()
	{
		int index = 0;
		_itemNames.clear();
		if (_sortItemsByName)
		{
			for (Item item : _inventory.getSortedProductsByName())
			{
				_itemNames.add(index, item.getName());
				index++;
			}
		}
		else
		{
			for (Item item : _inventory)
			{
				_itemNames.add(index, item.getName());
				index++;
			}
		}
	}
	
	/**
	 * Creates the export button
	 * @returns The button that was created
	 */
	private Button makeExportButton()
	{
		Button export = makeButton("Export");
		export.setPrefSize(s_navButtonWidth, s_navButtonHeight);
		export.setOnAction(e -> onClickExportButton(e));
		
		return export;
	}
	
	/**
	 * Event handler for export buttone click
	 * @param e The event that was triggered
	 */
	private void onClickExportButton(ActionEvent e)
	{
		FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.home")));
		ExtensionFilter csvFilter = new ExtensionFilter("CSV files (*.csv)", "*.csv");
		fc.getExtensionFilters().add(csvFilter);
		File file = fc.showSaveDialog(_homeStage);
		if (file != null)
		{
			exportInventory(file);
		}
	}
	
	/**
	 * Exports the inventory contents to comma-delimited file
	 * @param file The file to write the inventory contents
	 */
	private void exportInventory(File file)
	{
		try
		{
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			for (Item item : _inventory)
			{
				writer.write(item.toString() + "\n");
			}
			
			writer.flush();
		}
		catch (Exception e)
		{
			showAlertPopup("Inventory Export Failed", e.getMessage());
		}
	}
	
	/**
	 * Create the text input form for adding items to inventory
	 * @returns The Node containing the form
	 */
	private GridPane makeItemControlPanel(ItemControl control)
	{
		GridPane grid  = createGridPane();
		grid = addPanelTitle(grid, "Add Product to Inventory");
		grid = addItemControlLayout(grid, control);
		
		Button addItemButton = new Button("Add Item");
		addItemButton.setPrefSize(s_itemButtonWidth, s_itemButtonHeight);
		addItemButton.setOnAction(e -> onClickAddItemButton(control));
	
		HBox hbox = new HBox(s_hSpace);
		hbox.setAlignment(Pos.BOTTOM_RIGHT);
		hbox.getChildren().add(addItemButton);
		grid.add(hbox, 1, 6);
		
		return grid;
	}
	
	/**
	 * Helper method for creating new grid panes and setting properties
	 * @returns The created GridPane
	 */
	private GridPane createGridPane()
	{
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(s_hSpace);
		grid.setVgap(s_vSpace);
		grid.setPadding(new Insets(s_gridVPad, s_gridHPad, s_gridVPad, s_gridHPad));
		
		return grid;
	}
	
	/**
	 * Adds title text to a grid panel
	 * @param grid The GridPane to modify
	 * @param title The title text to add
	 * @returns The modified GridPane
	 */
	private GridPane addPanelTitle(GridPane grid, String title)
	{
		Text titleText = new Text(title);
		titleText.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(titleText, 0, 0, 2, 1);
		
		return grid;
	}
	
	/**
	 * Adds the item control text fields to a GridPane
	 * @param grid The GridPane to modify
	 * @param control The ctem control containing the text fields to add
	 * @returns The modified GridPane
	 */
	private GridPane addItemControlLayout(GridPane grid, ItemControl control)
	{
		grid.add(new Label("Name:"), 0, 1);
		grid.add(control.getNameField(), 1, 1);
		
		grid.add(new Label("Weight (lbs):"), 0, 2);
		grid.add(control.getWeightField(), 1, 2);
		
		grid.add(new Label("Wholesale Price ($):"), 0, 3);
		grid.add(control.getPriceField(), 1, 3);
		
		grid.add(new Label("Quantity:"), 0, 4);
		grid.add(control.getQuantityField(), 1, 4);
		
		return grid;
	}
	
	/**
	 * Wrapper function to respond to add item button click
	 * @param control The item control to use
	 */
	private void onClickAddItemButton(ItemControl control)
	{
		createAndAddItem(control);
	}
	
	/**
	 * Creates an item with properties specified by the item control text fields
	 * @param control The item control to use
	 */
	private void createAndAddItem(ItemControl control)
	{
		try
		{
			Item item = control.createItem();
			_inventory.add(item);
		}
		catch (NumberFormatException e)
		{
			showAlertPopup("Invalid Number Format", e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			showAlertPopup("Invalid Item Data", e.getMessage());
		}
		catch (DuplicateProductNameException e)
		{
			showAlertPopup("Duplicate Product Name", "Item \"" + e.getDuplicateName() + "\" already exists in inventory");
		}
		
		updateListViewContents();
		control.clearTextFields();
		control.requestFocus();
	}
	
	/**
	 * Method to create the right list view for displaying names of items in inventory
	 */
	private StackPane addInventoryListViewNode()
	{
		StackPane stackPane = new StackPane();
		stackPane.setPadding(new Insets(s_vPad, s_hPad, s_vPad, s_hPad));
		initListView();
		
		CheckBox sortCheckBox = new CheckBox("Sort items alphabetically");
		sortCheckBox.setAllowIndeterminate(false);
		sortCheckBox.setOnAction(e -> onToggleSortCheckBox(e));
		
		Button viewButton = makeButton("View");
		viewButton.setPrefSize(s_itemButtonWidth, s_itemButtonHeight);
		viewButton.setOnAction(e -> onClickViewButton(e));
		
		Button deleteButton = makeButton("Delete");
		deleteButton.setPrefSize(s_itemButtonWidth, s_itemButtonHeight);
		deleteButton.setOnAction(e -> onClickDeleteButton(e));
		
		HBox hbox = new HBox();
		hbox.setSpacing(s_hSpace);
		hbox.getChildren().addAll(viewButton, deleteButton);
		
		VBox vbox = new VBox();
		vbox.setSpacing(s_vSpace);
		vbox.getChildren().addAll(
			sortCheckBox,
			_listView, 
			hbox
		);
		
		stackPane.getChildren().add(vbox);
		
		return stackPane;
	}
	
	/**
	 * Method to set the intial properties for the inventory list view
	 */
	private void initListView()
	{
		_listView.setPrefSize(s_listViewWidth, s_listViewHeight);
        _listView.setEditable(true);
		_itemNames.setAll();
		_listView.setItems(_itemNames);
		
		_listView.setOnMouseClicked(e -> onMouseClickListViewItem(e));
	}
	
	/**
	 * Event handler when an item in list view is clicked
	 * Wrapper for the popup view window when double clicking list item
	 * @param e The mouse event that was triggered
	 */
	private void onMouseClickListViewItem(MouseEvent e)
	{
		if (e.getClickCount() == 2)
		{
			showViewPopup();
		}
	}
	
	/**
	 * Show item summary popup to view item details
	 */
	private void showViewPopup()
	{
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(_homeStage);
		String name = (String)_listView.getSelectionModel().getSelectedItem();
		Item item = (Item)_inventory.get(name);
		
		ItemControl control = new ItemControl();
		control.setText(item);
		control.setDisable(true);
		
		Button saveButton = makeButton("Save");
		saveButton.setPrefSize(s_itemButtonWidth, s_itemButtonHeight);
		saveButton.setDisable(true);
		saveButton.setOnAction(e ->
		{
			setItemProperties(control, item);
			dialog.close();
		});
		
		Button editButton = makeButton("Edit");
		editButton.setPrefSize(s_itemButtonWidth, s_itemButtonHeight);
		editButton.setOnAction(e -> 
		{
			control.setDisable(false);
			saveButton.setDisable(false);
			editButton.setDisable(true);
			control.requestFocus();
		});
		
		Button closeButton = makeButton("Close");
		closeButton.setPrefSize(s_itemButtonWidth, s_itemButtonHeight);
		closeButton.setOnAction(e -> dialog.close());
		
		GridPane grid = createGridPane();
		grid = addPanelTitle(grid, "Viewing Item: " + item.getName());
		grid = addItemControlLayout(grid, control);
		grid = addAndSetViewOnlyFields(grid, item);
		
		HBox hbox = new HBox();
		hbox.setSpacing(s_hSpace);
		hbox.setAlignment(Pos.BOTTOM_RIGHT);
		hbox.getChildren().addAll(editButton, saveButton, closeButton);
		grid.add(hbox, 0, 7, 2, 7);
		
		Scene dialogScene = new Scene(grid);
		dialog.setScene(dialogScene);
		dialog.show();
	}
	
	/**
	 * Method to set an item control's text fields to the properties of a chosen item
	 * @param control The item control whose text fields to set
	 * @param item The item whose properties to set to the text fields
	 */
	private void setItemProperties(ItemControl control, Item item)
	{
		try
		{
			item = control.setItemProperties(item);
		}
		catch (NumberFormatException e)
		{
			showAlertPopup("Invalid Number Format", e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			showAlertPopup("Invalid Item Data", e.getMessage());
		}
		catch (DuplicateProductNameException e)
		{
			showAlertPopup("Duplicate Product Name", "Item \"" + e.getDuplicateName() + "\" already exists in inventory");
		}
		
		updateListViewContents();
		control.clearTextFields();
	}
	
	private GridPane addAndSetViewOnlyFields(GridPane grid, Item item)
	{
		grid.add(new Label("Retail Price ($):"), 0, 5);
		TextField retailPriceField = new TextField();
		retailPriceField.setText(String.format("%.2f",item.getRetailPrice()));
		retailPriceField.setDisable(true);
		grid.add(retailPriceField, 1, 5);
		
		grid.add(new Label("Storage Costs ($):"), 0, 6);
		TextField storageCostField = new TextField();
		storageCostField.setText(String.format("%.2f",item.getStorageCost()));
		storageCostField.setDisable(true);
		grid.add(storageCostField, 1, 6);
		
		return grid;
	}
	
	/**
	 * Event handler for when toggle sort alphabetically box is checked
	 * @param e The event that was triggered
	 */
	private void onToggleSortCheckBox(ActionEvent e)
	{
		_sortItemsByName = !_sortItemsByName;
		updateListViewContents();
	}
	
	/**
	 * Event handler for when view item button clicked
	 * @param e The event that was triggered
	 */
	private void onClickViewButton(ActionEvent e)
	{
		showViewPopup();
	}
	
	/**
	 * Event handler for when delete item button clicked
	 * @param e The event that was triggered
	 */
	private void onClickDeleteButton(ActionEvent e)
	{
		SelectionModel selectionModel = _listView.getSelectionModel();
		int index = selectionModel.getSelectedIndex();
		String name = (String)selectionModel.getSelectedItem();
		if (name != null)
		{
			_inventory.remove(_inventory.get(name));
			updateListViewContents();
			 // set selection to next item in list, if out of bounds returns no selection
			selectionModel.select(index);
		}
	}
}