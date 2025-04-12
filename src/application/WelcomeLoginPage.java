package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.geometry.Pos;

import java.sql.SQLException;
import java.util.List;

import databasePart1.*;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show( Stage primaryStage, User user) {
    	
    	
    	 //this VBox CSS/Styling is for the buttons on the left underneath the header
    	 HBox subHeaderLayout = new HBox(5);
    	 subHeaderLayout.setStyle("-fx-padding: 20;");
    	 subHeaderLayout.setAlignment(Pos.CENTER);
    	 
    	 HBox contentLayout = new HBox(10);
    	 contentLayout.setAlignment(Pos.CENTER);
    	 
    	
    	
    	 //HeaderLabel that displays atop the welcome scene
    	 Label headerLabel = new Label("Welcome!");
         headerLabel.setFont(new Font("Arial", 20));
         headerLabel.setTextFill(Color.WHITE);
        
         //This HBox takes in the header and the "layout" VBox which
         //contains the rest of the buttons.
         HBox header = new HBox(headerLabel);
         header.setStyle("-fx-background-color: #9966CC; -fx-padding: 15px;");
         header.setPrefHeight(50);
         header.setAlignment(Pos.TOP_LEFT);
         
         //this root will take in (header, layout) at the end of this class
         VBox root = new VBox();
         
         
	    Label welcomeLabel = new Label("Please select your role and "
	    		+ "continue to your page");
	    welcomeLabel.setStyle(("-fx-font-size: 18px; " +  
                "-fx-font-weight: bold; " + 
                "-fx-font-family: 'Arial', sans-serif; " +
                "-fx-text-fill: #333333; " +  
                "-fx-padding: 10px; " + 
                "-fx-background-color: white; " + 
                "-fx-border-color: #cccccc; " + 
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " + 
                "-fx-background-radius: 5px;"));
	    
	    String buttonCSS = ("-fx-font-size: 14px; " +  
                "-fx-font-weight: bold; " + 
                "-fx-font-family: 'Arial', sans-serif; " +
                "-fx-background-color: #d6b4fc; " +  
                "-fx-text-fill: black; " +   
                "-fx-border-width: 2px; " +
                "-fx-border-radius: 5px; " + 
                "-fx-background-radius: 5px; " +
                "-fx-padding: 5px 10px;");
	    
	    
	    //add role selection box to chose if they have different role
	    ComboBox<String> roleComboBox = new ComboBox<>();
	    roleComboBox.setPromptText("Select your role here");
	    roleComboBox.setStyle(buttonCSS);
	    roleComboBox.setOnMouseEntered(e -> { 
        	roleComboBox.setStyle(buttonCSS + "-fx-background-color: #e3ccfd;" );
        });
	    roleComboBox.setOnMouseExited(e -> { 
	    	roleComboBox.setStyle(buttonCSS);
	    });
        	
	    //Replace strings with data return from user set. default is user.
	    
	    String roleCode = databaseHelper.getUserRole(user.getUserName());
	    String[] roleArray = new String[6];
	    roleArray[0] = "admin";
	    roleArray[1] = "instructor";
	    roleArray[2] = "student";
	    roleArray[3] = "reviewer";
	    roleArray[4] = "staff";
	    
	    
	    for(int i =0; i<roleCode.length(); i++) {
	    	if(roleCode.charAt(i)=='1') {
	    		 roleComboBox.getItems().add(roleArray[i]);
	    	}
	    }
	   
	  
	    // Set the default selection to the user's current role (if valid).
	    //10000 is the roleCode for admin, 00000 is the roleCode for users.
	    if (user != null && (user.getRole().equals("00000"))) {
	        
	        roleComboBox.setValue("user");  // Or default if user role is not recognized
	        user.setRole("user");
	    }
	    

	    
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to your Page");
	    
	  
	    
	    
	    
	    continueButton.setOnAction(a -> {
	    	String role =roleComboBox.getValue();
	    	System.out.println("Selected role: "+ role);
	    	
	    	//add those if else statement to drive the user to their role page
	    	if(role.equals("admin")) {
	    		user.setRole("admin");
	    		new AdminHomePage(databaseHelper).show(primaryStage,user);
	    	}
	    	else if(role.equals("user")) {
	    		user.setRole("user");
	    		new UserHomePage(databaseHelper).show(primaryStage,user);
	    	}
	    	if (role.equals("instructor")) {
	    		user.setRole("instructor");
	    	    new Instructor(databaseHelper).showPage(primaryStage,user);
	    	}
	    	else if (role.equals("student")) {
	    		user.setRole("student");
	    	    new Student(databaseHelper).showPage(primaryStage,user);
	    	}
	    	else if (role.equals("reviewer")) {
	    		user.setRole("reviewer");
	    	    new Reviewer(databaseHelper).showPage(primaryStage,user);
	    	}
	    	else if (role.equals("staff")) {
	    		user.setRole("staff");
	    	    new Staff(databaseHelper).showPage(primaryStage,user);
	    	}
	    	
	    });
	    
	    
	    Button messageButton = new Button("Messages");
	    messageButton.setStyle(buttonCSS);
	    messageButton.setOnMouseEntered(e -> messageButton.setStyle(buttonCSS + "-fx-background-color: #e3ccfd;" ));
	    messageButton.setOnMouseExited(e -> messageButton.setStyle(buttonCSS));

	    messageButton.setOnAction(e -> {
	        try {
	            List<message> messages = databaseHelper.getPrivate(user.getUserName());
	            new MessagePage(databaseHelper).show(primaryStage, messages, user);
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	    });

	    
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    //changed root to be in the welcomeScene instead of layout
	    contentLayout.getChildren().addAll(roleComboBox, continueButton, messageButton, quitButton);
	    subHeaderLayout.getChildren().addAll(welcomeLabel);
	    root.getChildren().addAll(header, subHeaderLayout, contentLayout);
	    Scene welcomeScene = new Scene(root, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}