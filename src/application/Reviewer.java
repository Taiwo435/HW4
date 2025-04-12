package application;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import databasePart1.DatabaseHelper;


/**
 * The {@code Reviewer} class implements the {@code Role} interface and provides
 * the user interface and functionality specific to a Reviewer in the system.
 * 
 * Reviewers have access to:
 * <ul>
 *     <li>Check all submitted questions</li>
 *     <li>Switch to another role</li>
 *     <li>Log out</li>
 * </ul>
 */
public class Reviewer implements Role {
	
	  private final DatabaseHelper databaseHelper;
	
	  
	    /**
	     * Constructs a Reviewer role screen with access to the shared database helper.
	     *
	     * @param databaseHelper The helper used to perform DB operations.
	     */
	 public Reviewer(DatabaseHelper databaseHelper) {
	        this.databaseHelper = databaseHelper;
	    }

	 
	 
	    /**
	     * Displays the Reviewer home page.
	     *
	     * @param primaryStage The main stage of the application.
	     * @param user         The current reviewer user.
	     */
    public void showPage(Stage primaryStage, User user) {
    	
    	VBox layout = new VBox();
	    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
	    
	    // Label to display Hello reviewer
	    Label reviewerLabel = new Label("Hello, Reviewer!");
	    reviewerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

	    Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(a -> {
        	
            new UserLoginPage(databaseHelper).show(primaryStage);});
	    
    	//add change role button
	    Button changeRoleButton = new Button("Change Role");
	    changeRoleButton.setOnAction(a -> {
	    	
	    	new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
	 	    	
	    });
	    

	    
    	Button checkReview = new Button("Check My Reviews");
    	checkReview.setOnAction(e -> showMyReviews(primaryStage, user));

    	
    	
	    layout.getChildren().addAll(reviewerLabel,logoutButton,changeRoleButton,checkReview);
	    Scene reviewerScene = new Scene(layout, 800, 400);

	    
	    Button checkQuestion = new Button("Check questions");
    	checkQuestion.setOnAction(a -> {
    		
    		List<question> que = new ArrayList<>();
    		
    		try {
				que = databaseHelper.getquestion();
				
				new QuestionPage(databaseHelper).show(primaryStage, que, user, reviewerScene);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	});
    	
    	layout.getChildren().add(checkQuestion);
	    
	    // Set the scene to primary stage
	    primaryStage.setScene(reviewerScene);
	    primaryStage.setTitle("Reviewer Page");
    }


    /**
     * Displays a table of all reviews submitted by the currently logged-in user.
     * <p>
     * This method retrieves the user's reviews from the database using
     * {@code DatabaseHelper.getReviewByUser(String author)} and shows them in a
     * {@code TableView}, distinguishing whether the review is for a question or
     * an answer. It also includes a back button to return to the previous page.
     * </p>
     *
     * @param primaryStage The main stage of the application where the UI is rendered.
     * @param user         The currently logged-in user whose reviews will be shown.
     */
    public void showMyReviews(Stage primaryStage, User user) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label("My Reviews");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<reviews> reviewTable = new TableView<>();

        // Column: Target (question ID or answer text)
        TableColumn<reviews, String> targetCol = new TableColumn<>("Review Target");
        targetCol.setCellValueFactory(data -> {
            reviews r = data.getValue();
            String target = (r.getAnswer() == null || r.getAnswer().isEmpty())
                    ? "Question ID: " + r.getId()
                    : "Answer: \"" + r.getAnswer() + "\"";
            return new SimpleStringProperty(target);
        });

        // Column: Review Content
        TableColumn<reviews, String> reviewTextCol = new TableColumn<>("Review");
        reviewTextCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReview()));
        reviewTextCol.setPrefWidth(400);

        reviewTable.getColumns().addAll(targetCol, reviewTextCol);

        try {
            List<reviews> myReviews = databaseHelper.getReviewByUser(user.getUserName());
            reviewTable.setItems(FXCollections.observableArrayList(myReviews));
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load your reviews.", ButtonType.OK);
            alert.showAndWait();
        }

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showPage(primaryStage, user)); // Your existing back method

        layout.getChildren().addAll(title, reviewTable, backButton);

        Scene scene = new Scene(layout, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My Reviews");
    }

    
    
    
    /**
     * Fallback implementation of the Role interface's showPage method.
     * This version is unused and exists only to fulfill the interface contract.
     *
     * @param stage The JavaFX stage.
     */
    @Override    
	public void showPage(Stage stage) {
		// TODO Auto-generated method stub
		
	}
}
