package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;


public class setPassword implements Role {

	 private final DatabaseHelper databaseHelper;
	
	 public setPassword(DatabaseHelper databaseHelper) {
	        this.databaseHelper = databaseHelper;
	    }
	 
   public void showPage(Stage primaryStage) {
       
       TextField userNameField = new TextField();
       userNameField.setPromptText("Enter userName");
       userNameField.setMaxWidth(250);

       PasswordField passwordField = new PasswordField();
       passwordField.setPromptText("Enter Password");
       passwordField.setMaxWidth(250);
       
       PasswordField rePasswordField = new PasswordField();
       passwordField.setPromptText("Reenter Password");
       passwordField.setMaxWidth(250);
	   
     
	   
	   Button setUp = new Button("Set Up");
	   
	   setUp.setOnAction(a -> {
		   
		   String userName = userNameField.getText();
           String password = passwordField.getText();
           String rePassword = rePasswordField.getText();
           
           if(password.equals(rePassword)) {
        	   
        	   
        	    if (!databaseHelper.doesUserExist(userName)) {
        	        Alert alert = new Alert(Alert.AlertType.ERROR);
        	        alert.setTitle("Error");
        	        alert.setHeaderText("User Not Found");
        	        alert.setContentText("The username does not exist. Please check your username.");
        	        alert.showAndWait();
        	        return;
        	    }
        	    else {
        	   
        	  databaseHelper.setPassword(userName, rePassword);
        	  
        	  try {
				databaseHelper.getPassword(userName);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	  
        	  Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
              successAlert.setTitle("Success");
              successAlert.setHeaderText(null);
              successAlert.setContentText("Password reset succeeded!");
              successAlert.showAndWait();
              
              
        	    }
              
        	  new UserLoginPage(databaseHelper).show(primaryStage);
           }
           else {
        	   Alert alert = new Alert(Alert.AlertType.ERROR);
   	        alert.setTitle("Error");
   	        alert.setHeaderText("Does not match");
   	        alert.setContentText("The reenter password does not match.");
   	        alert.showAndWait();
   	        return;
        	   
           }
           
		   
	   });
	   
		 VBox otpLayout = new VBox(10);
	        otpLayout.setPadding(new Insets(20));
	        otpLayout.setStyle("-fx-alignment: center;");
	        otpLayout.getChildren().addAll(userNameField,passwordField,rePasswordField,setUp);
	        
	        Scene otpScene = new Scene(otpLayout, 300, 150);
	        primaryStage.setScene(otpScene);
	        primaryStage.setTitle("Password Reset");
	   
   }
   
   

   
   
   
}