package application;

import javafx.scene.control.TextArea;
import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * The QuestionPage class is responsible for displaying questions from the database,
 * allowing users to:
 * - Search for questions based on type.
 * - Submit new questions.
 * - Reply to existing questions.
 * - View answers related to each question.
 */
public class updateAnswer {
    private final DatabaseHelper databaseHelper;
    private VBox questionList; // Container for displaying questions
    private List<question> questions; // List of questions
    private User currentUser;
    private Stage stage;
    /**
     * Constructor for the QuestionPage.
     * @param databaseHelper The database helper instance to interact with the database.
     */
    public updateAnswer(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage, question q, User user, answer ans, VBox questionBox, Scene before) {
        Stage stage = new Stage();
        stage.setTitle("Update Answer");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        stage.setResizable(true);


        Label label = new Label("Updating Answer for: " + ans.getText());
        
        TextArea answerField = new TextArea();
        answerField.setPromptText("Enter new answer");
        answerField.setWrapText(true);  // Enable text wrapping
        answerField.setPrefWidth(300);  // Make it wider
        answerField.setPrefRowCount(3);
        
        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            String updatedAnswer = answerField.getText();
            if (!updatedAnswer.isEmpty()) {
                ans.setText(updatedAnswer);
                databaseHelper.updateAnswer(ans);

                // Get the QuestionPage instance (assuming you have access to it)
                try {
                	QuestionPage ques = new QuestionPage(databaseHelper);
					ques.show(primaryStage, databaseHelper.getquestion(), user, before);
				
					
					
				} catch (SQLException e1) {
			
					e1.printStackTrace();
				}  
                System.out.println("Answer updated to: " + updatedAnswer);
                stage.close();
            } else {
                System.out.println("Answer cannot be empty!");
            }
        });

        
        
              
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> stage.close());

        layout.getChildren().addAll(label, answerField, saveButton, closeButton);
        
        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        System.out.println("Update Answer window opened!");  
        stage.show();
    }

	
}