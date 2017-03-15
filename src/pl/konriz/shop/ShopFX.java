package pl.konriz.shop;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;



public class ShopFX extends Stage{
	
	private Shop shop;
	
	private Label sumLbl;
	private double sum;
	
	
	private TextField eanFld;
	private TextField amountFld;
	
	public ShopFX(Shop sh){
		
		shop = sh;
		setTitle(shop.getName());
		
		// header - sum to pay and pay button
		sumLbl = new Label ("0.00");
		Button payBtn = new Button ("Zapłać");
		payBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				if (shop.getCurrentOrder().getValue() == 0)
				{
					Alert alert = new Alert(AlertType.ERROR, "Zerowa wartość zamówienia!", ButtonType.OK);
					alert.showAndWait();
				}
				else
				{
					// Dialog - choose payment way
				}
			}
		});
		
		HBox topPanel = new HBox();
		topPanel.setAlignment(Pos.BASELINE_CENTER);
		topPanel.setPadding(new Insets(10));
		topPanel.getChildren().addAll(sumLbl, payBtn);
		
		//Table with scroll option
		
		
		
		//Bottom - add item module - choose ean, amount and add button
		
		Label eanLbl = new Label("Kod EAN:");
		eanFld = new TextField();
		
		Label amountLbl = new Label("Ilość:");
		amountFld = new TextField();
		
		Button addBtn = new Button("Dodaj");
		addBtn.setOnAction(new AddItemAction());
		
		
		Button exitBtn = new Button("Exit");
		exitBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				Platform.exit();
			}
		});
		
		HBox bottomPanel = new HBox();
		bottomPanel.setAlignment(Pos.BASELINE_CENTER);
		bottomPanel.setPadding(new Insets(10));
		bottomPanel.getChildren().addAll(eanLbl, eanFld, amountLbl, amountFld, addBtn);		
		
		BorderPane root = new BorderPane();
		root.setTop(topPanel);
		root.setBottom(bottomPanel);
				
		Scene scene = new Scene(root, 900,650);
		setScene(scene);

	}
	
	
	class AddItemAction implements EventHandler<ActionEvent>
	{
		private Item itemToAdd;
		private int amountToAdd;
		
		public AddItemAction()
		{
			
		}
		
		public void handle(ActionEvent e){
			
			try
			{
			
				long ean = Long.parseLong(eanFld.getText().trim());
				amountToAdd = Integer.parseInt(amountFld.getText().trim());
	
				Shop currentShop = shop;
				Shelf currentStock = currentShop.getStock();
				Order currentOrder = currentShop.getCurrentOrder();
				// TODO table!
				// DefaultTableModel model = (DefaultTableModel) table.getModel();
				if (currentShop.gatherItem(ean, amountToAdd))
				{
					int position = currentOrder.getItemCount();
					Integer lp = (Integer) position;
					itemToAdd = currentStock.getInventory(ean).getItem();
					ItemPack currentItemPack = currentOrder.getOrder(position-1);
					Double itemValue = (Double) currentItemPack.getValue();
					Double itemVAT = (Double) currentItemPack.getVAT();
					Double itemValueB = (Double) currentItemPack.getValueB();
					Object[] rowToAdd = new Object[]{lp, ean, itemToAdd.getName(), (Double) itemToAdd.getPrice(), amountToAdd,
										itemValue, itemVAT, itemValueB};
					
					// model.addRow(rowToAdd);
					sum = currentOrder.getValueB();
					String sumText = "" + sum;
					sumLbl.setText(sumText);
					amountFld.setText("1");
					eanFld.requestFocus();
					eanFld.selectAll();
				}
			}
			catch (NumberFormatException ex)
			{
				//Dialog gdy ean lub amount to nie liczba
			}
			
		}
	}
}
