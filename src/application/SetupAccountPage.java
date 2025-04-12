package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

		

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {

	//Create boolean variable to determine what's the error message shows at the end.
    boolean validUserName = false;
    boolean validPassword = false;
    boolean validEmail = false;
    private final DatabaseHelper databaseHelper;
    
    
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
        
        

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");
        emailField.setMaxWidth(250);
        
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        

       
        
        Button setupButton = new Button("Setup");
        
        	// Event handler for validating the username when the user types
        	userNameField.setOnKeyTyped(event -> {						
        	// Retrieve the text entered in the userNameField
            String userName = userNameField.getText();
            
            // Perform the username validation by calling the checkForValidUserName method
            String validationMessage = UserNameRecognizer.checkForValidUserName(userName);

            // Check if the validation message is non-empty (indicating an invalid username)
            if (!validationMessage.equals("")) {
            	
            // If username is invalid, display the validation message in the errorLabel
                errorLabel.setText(validationMessage);
            } else {
            	 // If username is valid, clear the error message from the errorLabel
                errorLabel.setText("");
                // Set validUserName flag to true to indicate the username is valid
                validUserName = true;
            }
        });
        	
        	// Event handler for validating the password when the user types
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
        	
        	//event handler for validating email
        	emailField.setOnKeyTyped(event -> {				//Joshua
        		//call the function to check validation for email
        		String email = emailField.getText();
        		
        		String vlidationMessage = EmailEvaluator.evaluateEmail(email);
        		
        		errorLabel.setText(vlidationMessage);
        		
        	});
        
        
        
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();
            String code = inviteCodeField.getText();
            
            
            
       	
            if(validUserName == true && validPassword == true) {
            try {
            	
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            		// Validate the invitation code
            		if(databaseHelper.validateInvitationCode(code) && validUserName && validPassword) {
            			
            			// Create a new user and register them in the database
		            	User user=new User(userName, password,email ,"00000");
		                databaseHelper.register(user);
		                
		             // Navigate to the Welcome Login Page
		                // CHANGES MADE HERE! Commented out redirection to home page and replaced with log in page
		                UserLoginPage userLoginPage = new UserLoginPage(databaseHelper);
						// Navigate to the Welcome Login Page
		                // new 
		                userLoginPage.show(primaryStage);
            		}
            		else {
            			if(!validUserName) {
            				errorLabel.setText(UserNameRecognizer.checkForValidUserName(userName));
            			}
            			else if(!validPassword) {
            				errorLabel.setText(PasswordEvaluator.evaluatePassword(password));
            			}
            			else {
            			errorLabel.setText("Please enter a valid invitation code or username, password.");
            			}
            		}
            	}
            	else {
            		errorLabel.setText("This useruserName is taken!!.. Please use another to setup an account");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            	}
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField,emailField,inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}