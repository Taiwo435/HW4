package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;


/**
 * UI elements for the instructor and associated functionality
 * 
 */

public class Instructor implements Role {

	 private final DatabaseHelper databaseHelper;
	
	 public Instructor(DatabaseHelper databaseHelper) {
	        this.databaseHelper = databaseHelper;
	    }
	 
    public void showPage(Stage primaryStage,User user) {
        
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello instructor
	    Label instructorLabel = new Label("Hello, Instructor!");
	    instructorLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
	    Button logoutButton = new Button("Log out");
	    logoutButton.setOnAction(a -> {
	    	
	    	new UserLoginPage(databaseHelper).show(primaryStage);
	 	    	
	    });
	    
    	//add change role button
	    Button changeRoleButton = new Button("Change Role");
	    changeRoleButton.setOnAction(a -> {
	    	
	    	new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
	 	    	
	    });
	    
	    Button viewAllReviewerRequests = new Button("View Reviewer Requests");
	    viewAllReviewerRequests.setOnAction(a ->{
	    	
	    	try {
	    		
	    		List<User> students = databaseHelper.getPotentialReviewers();
	    		
	    		new ReviewerRequests(databaseHelper).show(primaryStage, students, user);
	    		
	    	} catch(SQLException e) {
	    		e.printStackTrace();
	    	}
	    });
	    
	    layout.getChildren().addAll(instructorLabel,logoutButton,changeRoleButton, viewAllReviewerRequests);
	    Scene instructorScene = new Scene(layout, 800, 400);
	    
	    
	    // Set the scene to primary stage
	    primaryStage.setScene(instructorScene);
	    primaryStage.setTitle("Instructor Page");
    }

	@Override
	public void showPage(Stage stage) {
		// TODO Auto-generated method stub
		
	}
}


