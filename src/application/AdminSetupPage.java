
package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
	boolean passwordValidation = false;
	boolean userNameValidation = false;
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input fields for userName and password
    	
    	Label errorLabel = new Label();
    	errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
    	
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email");
        emailField.setMaxWidth(250);
        

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
                userNameValidation = true;
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
                passwordValidation = true;
            }
        	});
        
        	//event handler for validating email
        	emailField.setOnKeyTyped(event -> {
    		//call the function to check validation for email
        		//call the function to check validation for email
        		String email = emailField.getText();
        		
        		String vlidationMessage = EmailEvaluator.evaluateEmail(email);
        		
        		errorLabel.setText(vlidationMessage);
    		
        	});
    
        
        
        Button setupButton = new Button("Setup");
   
        setupButton.setOnAction(a -> {
        	if(userNameValidation == true && passwordValidation == true) {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            try {
            	// Create a new User object with admin role and register in the database
            	User user=new User(userName, password, email, "10000");
                databaseHelper.register(user);
                System.out.println("Administrator setup completed.");
                
                // Navigate to the Welcome Login Page
                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            	} catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            			}
            
        	}else {
        		errorLabel.setText("Double check your input.");
        	}
        });

        VBox layout = new VBox(10, userNameField, passwordField,emailField,errorLabel, setupButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
}
