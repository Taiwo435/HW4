package application;

import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReviewerRequests {
	private final DatabaseHelper databaseHelper;
	
	public ReviewerRequests(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	public void show(Stage primaryStage, List<User> students, User user) {
		
		
		
		VBox root = new VBox(10);
    	root.setPadding(new Insets(15));
    	
    	Label header = new Label("Potential Reviewers: ");
    	header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	root.getChildren().add(header);
    	
    	
    	for(User student : students) {
    		
    		HBox userRow = new HBox(10);
    		
    		Label userLabel = new Label ("Username: "+ student.getUserName() 
    		+"|Email: "+ student.getEmail());
    		
	    	Button userContributionSummary = new Button("Review student contributions");
	    	userContributionSummary.setOnAction(a -> {
	    		
	    		new ContributionSummary(databaseHelper).show(primaryStage, student, user);
	    		
	    	});
	    	
	    	userRow.getChildren().addAll(userLabel, userContributionSummary);
    		root.getChildren().add(userRow);
    	
    	}
    	
    	Button backButton = new Button("Back");
    	backButton.setOnAction(a -> {
    		
    		new Instructor(databaseHelper).showPage(primaryStage, user);
    		
    	});
    	
    	root.getChildren().addAll(backButton);
    	
    	Scene reviewerScene = new Scene(root, 800, 400);
    	
    	primaryStage.setScene(reviewerScene);
	    primaryStage.setTitle("Potential Reviewers Page");
	}
}
