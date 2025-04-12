package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * UI component for instructors to review a students contributions and make a decision in regards to their review access
 */

public class ContributionSummary {
	private final DatabaseHelper databaseHelper;
	
	public ContributionSummary(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
	}
	
	/**
	 * method takes a Stage, student user, and current user and displays all the questions and answers from the student
	 * @param primaryStage
	 * @param student
	 * @param user
	 */
	
	public void show(Stage primaryStage, User student, User user){
		VBox root = new VBox(10);
    	root.setPadding(new Insets(15));
    	
    	List<question> studentQuestions;
    	List<answer> studentAnswers;
    	
    	
    	
    	Label questionHeader = new Label("Questions: ");
    	questionHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	root.getChildren().add(questionHeader);
    	
    	try {
    		
    		studentQuestions = databaseHelper.showQuesOnUser(student.getUserName());
    		
	    	for(question question : studentQuestions) {
	    		HBox questionRow = new HBox(10);
	    		
	    		Label questionLabel = new Label ("Question: "+ question.getText());
	    		
	    		questionRow.getChildren().add(questionLabel);
	    		
	    		root.getChildren().add(questionRow);
	    	}
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    	
    	Label answerHeader = new Label("Answer: ");
    	answerHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    	root.getChildren().add(answerHeader);
    	
    	try {
    		
    		studentAnswers = databaseHelper.getStudentAnswers(student);
    		
	    	for(answer answer : studentAnswers) {
	    		HBox answerRow = new HBox(10);
	    		
	    		Label answerLabel = new Label ("Answer: "+ answer.getText());
	    		
	    		answerRow.getChildren().add(answerLabel);
	    		
	    		root.getChildren().add(answerRow);
	    	}
	    	
    	} catch(SQLException e) {
    		e.printStackTrace();
    	}
    	
    	HBox userRow = new HBox(10);
    	Button denyRequest = new Button("Reject");
    	denyRequest.setOnAction(a -> {
    		
    		try {
    			
    			databaseHelper.rejectReviewerRequest(student);
    			List<User> students = databaseHelper.getPotentialReviewers();
    			
    			new ReviewerRequests(databaseHelper).show(primaryStage, students, user);
    			
    		} catch(SQLException e) {
    			e.printStackTrace();
    		}
    		
    	});
    	
    	Button acceptRequest = new Button("Accept");
    	acceptRequest.setOnAction(a -> {
    		try {
    			
    			databaseHelper.acceptReviewerRequest(student);
    			
    			System.out.println(student.getRole());
    			
    		} catch(SQLException e) {
    			e.printStackTrace();
    		}
    	});
    	
    	
    	userRow.getChildren().addAll(denyRequest, acceptRequest);
    	root.getChildren().add(userRow);
    	
    	Button back = new Button("Back");
    	back.setOnAction(a -> {
    		
    		try {
    			
    			List<User> students = databaseHelper.getPotentialReviewers();
    			
    			new ReviewerRequests(databaseHelper).show(primaryStage, students, user);
    		} catch(SQLException e) {
    			e.printStackTrace();
    		}
    	});
    	
    	root.getChildren().add(back);
    	
    	Scene reviewerScene = new Scene(root, 800, 400);
    	
    	primaryStage.setScene(reviewerScene);
	    primaryStage.setTitle("Student Contributions Page");
	}
}