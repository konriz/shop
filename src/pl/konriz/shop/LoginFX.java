package pl.konriz.shop;

import javafx.application.Application;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LoginFX extends Application{
	
	public static void main(String[] args){
		Application.launch(args);
	}
	

	@Override
	public void start(Stage st){
		
		st.setTitle("Logowanie");
		Shop shop = new Shop();
		
		Label helloLbl = new Label("Witaj użytkowniku!");
		HBox topPane = new HBox();
		topPane.getChildren().add(helloLbl);
		topPane.setAlignment(Pos.BASELINE_CENTER);
		
		Label userLbl = new Label("Login");
		TextField userFld = new TextField();
		
		Label passLbl = new Label("Hasło");
		PasswordField passFld = new PasswordField();
		
		GridPane inputPane = new GridPane();
		inputPane.getChildren().addAll(userLbl, userFld, passLbl, passFld);
		GridPane.setConstraints(userLbl, 0, 0);
		GridPane.setConstraints(userFld, 1, 0);
		GridPane.setConstraints(passLbl, 0, 1);
		GridPane.setConstraints(passFld, 1, 1);
		inputPane.setAlignment(Pos.CENTER);
		inputPane.setHgap(10);
		inputPane.setVgap(5);
		
		
		Button okBtn = new Button("Ok");
		okBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				DBClass online = new DBClass("jdbc:sqlite:/home/konriz/DB.sql", "data");
				
				String login = userFld.getText();
				String password = passFld.getText();
				String adress = online.authorizeDB(login, password);
				
				if (!adress.equals("")){
					online = new DBClass("jdbc:sqlite:/home/konriz/DB.sql", "towar" + adress);				
					shop.setOnlineBase(online);
					shop.setOnlineState(login);
					ShopFX shopFX = new ShopFX(shop);
					st.hide();
					shopFX.show();
				} else {
					Alert alertLogin = new Alert(AlertType.WARNING, "Błędny login lub hasło!", ButtonType.OK);
					alertLogin.showAndWait();

				}
			}
		});
		
		Button offlineBtn = new Button("Offline");
		offlineBtn.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent e){
				
				ShopFX shopFX = new ShopFX(shop);
				st.hide();
				Alert alertOffline = new Alert(AlertType.WARNING, "Uruchomienie offline!", ButtonType.OK);
				alertOffline.showAndWait();
				shopFX.show();
			}
			
		});
		
		HBox buttonPane = new HBox(10);
		buttonPane.getChildren().addAll(okBtn, offlineBtn);
		buttonPane.setAlignment(Pos.BASELINE_CENTER);
		buttonPane.setPadding(new Insets(10));
		
		BorderPane root = new BorderPane();
		root.setTop(topPane);
		root.setCenter(inputPane);
		root.setBottom(buttonPane);
		
		Scene scene = new Scene(root, 300, 150);
		st.setScene(scene);
		st.show();
	}
	
}

