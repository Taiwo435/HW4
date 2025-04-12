package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
	
	boolean validUserName = false;
    boolean validPassword = false;
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        userNameField.setOnKeyTyped(event -> {						
				
            String userName = userNameField.getText();;
            // Perform the username validation by calling the checkForValidUserName method
            String validationMessage = UserNameRecognizer.checkForValidUserName(userName);

            // Check if the validation message is non-empty (indicating an invalid username)
            if (!validationMessage.equals("")) {
            	
            // If username is invalid, display the validation message in the errorLabel
                errorLabel.setText(validationMessage);
            } else {
            	 // If username is valid, clear the error message from the errorLabel
                errorLabel.setText("");
                validUserName = true;
            } 
        	});
        	
        passwordField.setOnKeyTyped(event -> {
        	// Retrieve the text entered in the passwordField
        	
            String password = passwordField.getText();
        	// Perform the password validation by calling the evaluatePassword method
            String validationMessage = PasswordEvaluator.evaluatePassword(password);

            // Check if the validation message is non-empty (indicating an invalid password)
            if (!validationMessage.equals("")) {
            	
            	// If password is invalid, display the validation message in the errorLabel
                errorLabel.setText(validationMessage);
            } else {
            	// If password is valid, clear the error message from the errorLabel
                errorLabel.setText("");
             // Set validPassword flag to true to indicate the password is valid
                validPassword = true;
            }
        	});
        

        Button loginButton = new Button("Login");
        
        loginButton.setOnAction(a -> {
        	
        	if(validUserName == true && validPassword == true) {
        	// Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
         	// Event handler for validating the username when the user types
        	
           
            try {
            	//check if the password match the username.
             if(password.equals(databaseHelper.getPassword(userName))) {
            	User user=new User(userName, password,"","");
            	

            	WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
            	
            	// Retrieve the user's role from the database using userName
            	String role = databaseHelper.getUserRole(userName);
            	
            	if(role!=null) {
            		user.setRole(role);
            
                	//if they has one role, led them to the role page directly
            		if(role.equals("10000")) {
        	    		new AdminHomePage(databaseHelper).show(primaryStage,user);
        	    	}					
        	    	else if(role.equals("00000")) {//FOR users
        	    		new UserHomePage(databaseHelper).show(primaryStage,user);
        	    	}
        	    	else if (role.equals("01000")) {//for instructor
        	    	    new Instructor(databaseHelper).showPage(primaryStage,user);
        	    	}
        	    	else if (role.equals("00100")) {//for student
        	    	    new Student(databaseHelper).showPage(primaryStage,user);
        	    	}
        	    	else if (role.equals("00010")) {//for reviewer
        	    	    new Reviewer(databaseHelper).showPage(primaryStage,user);
        	    	}
        	    	else if (role.equals("00001")) {//for staff
        	    	    new Staff(databaseHelper).showPage(primaryStage,user);
        	    	}
                	
            		//If there is more than one role the user have, led them to the Welcome page
        	    	else if(user.roleCount(role)>1) {
            			welcomeLoginPage.show(primaryStage,user);
            		}
            		
            		else {
            			// Display an error if the login fails
                        errorLabel.setText("Error logging in");
            		}
            	}
            	else {
            		// Display an error if the account does not exist
                    errorLabel.setText("user account doesn't exists");
            	}
             }
             
             else {
            	 
           	  Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
              successAlert.setTitle("Error");
              successAlert.setHeaderText(null);
              successAlert.setContentText("Password or username Incorrect");
              successAlert.showAndWait();
            	 
             }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            	} 
        	}else {
        		errorLabel.setText("Double check your input");
        	}
        	
        });
        
        Button forgetPassword = new Button("Forget Password?");
        forgetPassword.setOnAction(a -> {
        	
        	showVerification (primaryStage);
        	
        });
        
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, errorLabel,forgetPassword);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
    
   
    
    private void showVerification (Stage stage) {
    	
    	Label optLabel = new Label("Enter your one-time password: ");
    	TextField otpField = new TextField();
    	otpField.setPromptText("One-Time Code");
    	Button submit = new Button("Submit");
    	Label otpMessage = new Label();
    	
      
    	
    	
    	submit.setOnAction(a -> {
    		String currentCode = otpField.getText();
    		
    		if(databaseHelper.validatePassword(currentCode)) {
    			
    			
    		new setPassword(databaseHelper).showPage(stage);
    			
    			
    		}
    		
    		else {
    			
    			Alert alert = new Alert(Alert.AlertType.ERROR);
    	        alert.setTitle("Error");
    	        alert.setHeaderText("One-time-password");
    	        alert.setContentText("The One-time-password does not exist. Please check your input.");
    	        alert.showAndWait();
    	        return;
    		}
    		
    	});
    	
		 VBox otpLayout = new VBox(10);
	        otpLayout.setPadding(new Insets(20));
	        otpLayout.setStyle("-fx-alignment: center;");
	        otpLayout.getChildren().addAll(optLabel, otpField, submit, otpMessage);
	        
	        Scene otpScene = new Scene(otpLayout, 300, 150);
	        stage.setScene(otpScene);
	        stage.setTitle("OTP Verification");
    	
    }
    
}
