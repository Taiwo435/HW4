package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**if (affectedRows > 0) {
            return "Deletion succeeded";
        } else {
            return "No user deleted";
        }
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
	
private final DatabaseHelper databaseHelper;
	
	public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
    public void show(Stage primaryStage,User user) {
    	
    	
    //*****The CSS for the UI below is separated into two vboxes.
    //*****One is LeftVbox which contains admin functions the other is right Vbox
    	//Arrange the buttons and headers to the top left of the scene
    	VBox leftVBox = new VBox(5); 
        leftVBox.setAlignment(Pos.TOP_LEFT);
        leftVBox.setMaxHeight(350);
        
        //set a border around the box
        leftVBox.setStyle("-fx-background-color: #CECECD;" +
                "-fx-background-radius: 12px;" +
                "-fx-background-width: 7px; " + 
                "-fx-padding: 15px; ");
        
        //header CSS and initialization
        Label leftHeader = new Label();
        leftHeader.setMinWidth(200);
        leftHeader.setMaxWidth(400);
        leftHeader.setText("ADMIN FUNCTIONS");
        leftHeader.setStyle("-fx-font-size: 20px; " +
        	       "-fx-font-weight: bold;" +
                       "-fx-font-family: 'Segoe UI', sans-serif; " +
                       "-fx-background-color: #0077B6; " +
                       "-fx-text-fill: white; " +
                       "-fx-padding: 12px 25px; " +
                       "-fx-border-radius: 8px; " +
                       "-fx-border-color: #005B8C;" +
                       "-fx-background-radius: 8px; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0.5, 0, 2);");
        
        //This is a string that copies the CSS for all buttons to clean up the code
        String buttonCSS= ("-fx-font-size: 14px; " +
                "-fx-font-weight: normal; " +
                "-fx-font-family: 'Segoe UI', sans-serif; " +
                "-fx-background-color: #4C9DFF; " +  
                "-fx-text-fill: white; " +
                "-fx-padding: 10px 20px; " +
                "-fx-border-radius: 8px; " +
                "-fx-background-radius: 8px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.2), 4, 0.3, 0, 2);");
        
        
        
        // "Invite" button for admin to generate invitation codes
        //CSS underneath to highlight the button on hover and return to normal on exit
        Button inviteButton = new Button("Invite New Users");
        inviteButton.setStyle(buttonCSS);
        inviteButton.setOnMouseEntered(e -> { 
        	inviteButton.setStyle(buttonCSS +"-fx-background-color: #00B4D8; " );
        	
        });
        inviteButton.setOnMouseExited(e -> { 
        	inviteButton.setStyle(buttonCSS);
        	
        });
        inviteButton.setOnAction(a -> {
        	 new InvitationPage().show(databaseHelper, primaryStage, user);
        });
        
        
      //get all users information button:
	    Button getAllButton = new Button("Display All Users");
	    getAllButton.setStyle(buttonCSS);
	    getAllButton.setOnMouseEntered(e -> { 
        	getAllButton.setStyle(buttonCSS +"-fx-background-color: #00B4D8; " );
        	
        });
	    getAllButton.setOnMouseExited(e -> { 
	    	getAllButton.setStyle(buttonCSS);
        	
        });
	    //get all users when clicked
	    getAllButton.setOnAction(a -> {
	    	try {
	    	 // Retrieve the list of all users from the database
	    	List<User> users = databaseHelper.getAllUsers();
	    		
	    	// users: user list from the database, user: admin user
	    	new AllUsers(databaseHelper).show(primaryStage,users,user,"");
	    	
	    }catch(SQLException ex) {
	    	ex.printStackTrace();
	    }
	    
	    });
	    
	    
	    
	    
	    //new button that will generate the one-time password for password resets.
	    //Also includes the styling for when hovered and exited
	    Button newPassButton = new Button();
	    newPassButton.setText("Generate One Time Password");
	    newPassButton.setStyle(buttonCSS);
	    newPassButton.setOnMouseEntered(e -> { 
	    	newPassButton.setStyle(buttonCSS +"-fx-background-color: #00B4D8; " );
        	
        });
	    newPassButton.setOnMouseExited(e -> { 
	    	newPassButton.setStyle(buttonCSS);
        	
        });
	    
	    
	    
	    //This label is the text printed when newPassButton is clicked
	    Label newPassLabel = new Label(""); 
        newPassLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
	    newPassButton.setOnAction(a -> {
        	// Generate the oneTimePassword using the databaseHelper and set it to the label
            String oneTimePassword = databaseHelper.generatePassword();
            newPassLabel.setText(oneTimePassword);
        });
        
        
	    //the header, invite button, get all user button, and new password text is put into LEFT vBox
        leftVBox.getChildren().addAll(leftHeader,inviteButton,getAllButton,newPassButton,newPassLabel);
    	
    	// label to display the welcome message for the admin, invite button
	    Label adminLabel = new Label("Welcome, " + user.getUserName() + ".");
	    adminLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
	    
        
    
	    //right vbox to contain the other buttons
	    //TODO: Probably change this to be more of a header
	    //@jeremyGastelo
	    VBox rightVBox = new VBox(15);
        rightVBox.setAlignment(Pos.TOP_RIGHT);
        rightVBox.setPadding(new Insets(20));
        rightVBox.setStyle("-fx-alignment: top-right; -fx-padding: 10px;");
        
	         
        //add logout button    
            Button logoutButton = new Button("Log out");
    	    logoutButton.setOnAction(a -> {
    	    	
    	    	new UserLoginPage(databaseHelper).show(primaryStage);
    	 	    	
    	    });
    	    
    	    //add change role button
    	    Button changeRoleButton = new Button("Change Role");
    	    changeRoleButton.setOnAction(a -> {
    	    	
    	    	new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
    	 	    	 
    	    });

	    //rightVbox contains "Hello, <user>", log out, and change role
            rightVBox.getChildren().addAll(adminLabel,logoutButton,changeRoleButton);
            
            HBox mainLayout = new HBox(100); //This constructor adds some distance between left and right box
            mainLayout.setPadding(new Insets(20));
            mainLayout.getChildren().addAll(leftVBox, rightVBox);
            Scene adminScene = new Scene(mainLayout, 800, 400);
	    
	    // Set the scene to primary stage
	    primaryStage.setScene(adminScene);
	    primaryStage.setTitle("Admin");
    }
}