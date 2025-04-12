package application;

import java.sql.SQLException;

import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

public class UpdateQuestion {
	private DatabaseHelper databaseHelper;
	
	public UpdateQuestion(DatabaseHelper newDatabaseHelper) {
		databaseHelper = newDatabaseHelper;
	}
	
	public void show(Stage primaryStage, question question, User user,  Scene before) throws SQLException {
		
    	VBox layout = new VBox();
    	layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
    	Label errorLabel = new Label();
    	errorLabel.setStyle("-fx-text-fill: #d9534f; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 5px;");

    	
        TextField questionTitleField = new TextField();
        questionTitleField.setText(question.getType());
        questionTitleField.setMaxWidth(250);
        questionTitleField.setStyle("-fx-padding: 10px; -fx-border-color: #aaa; -fx-border-radius: 5px; -fx-background-color: #fff;");
        

        
        TextArea questionTextField = new TextArea();
        questionTextField.setText(question.getText());
        questionTextField.setMaxWidth(400);
        questionTextField.setStyle("-fx-padding: 10px; -fx-border-color: #aaa; -fx-border-radius: 5px; -fx-background-color: #fff;");
        
        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 5px;");
        

        
        updateButton.setOnAction(a -> {
        	
        	String newType = questionTitleField.getText();
        	String newText = questionTextField.getText();
        	
        	if(newType == "" || newText == "") {
        		errorLabel.setText("Question type or text must not be empty!");
        	}
        	else {
        		
        		
        		try {
        			
        			databaseHelper.updateQuestion(question, newType, newText);
        			
					List<question> updatedList = databaseHelper.getquestion();
					
					new QuestionPage(databaseHelper).show(primaryStage, updatedList, user, before);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		
        	}
        	
        });
        
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 5px;");
        backButton.setOnAction(a -> {
        	try {
        		
        		new QuestionPage(databaseHelper).show(primaryStage, databaseHelper.getquestion(), user,before);
        		
        	} catch(SQLException e) {
        		e.printStackTrace();
        	}
        });
       
    
        HBox buttonBox = new HBox(15, updateButton, backButton); 
        buttonBox.setAlignment(Pos.CENTER); 

        layout.getChildren().addAll(questionTitleField, questionTextField, buttonBox, errorLabel);

        Scene updateQuestionScene = new Scene(layout, 600, 450);

		
	    primaryStage.setScene(updateQuestionScene);
   	    primaryStage.setTitle("Update Question Page");
	}
}