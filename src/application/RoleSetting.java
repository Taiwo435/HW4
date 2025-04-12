package application;

import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RoleSetting {

	private final DatabaseHelper databaseHelper;
	
	public RoleSetting(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
	
	
	public void show(Stage primaryStage, List<User> users, User user, User current) {
		
		
    	VBox root = new VBox(10);
    	root.setPadding(new Insets(15));
    	
 	    String[] roleArray = new String[5];
 	    roleArray[0] = "admin";
 	    roleArray[1] = "instructor";
 	    roleArray[2] = "student";
 	    roleArray[3] = "reviewer";
 	    roleArray[4] = "staff";
    	
    	
    		
    		String roleCode = user.getRole();
    		String roles="";
    		
    		if(roleCode.equals("00000")) {
    			roles = "user";}
    		else {
    		for(int i =0; i<roleCode.length(); i++) {
    			if(roleCode.charAt(i)=='1') {
    			roles += roleArray[i]+", "; }
    		}
    		
    		}
    		HBox userRow = new HBox(10);
    		
    		Label userLabel = new Label ("Username: "+ user.getUserName() 
    		+"| Role: " + roles);
		
    		Button back = new Button("Back");
    		
    		back.setOnAction(a -> {
    			
    			try {
					new AllUsers(databaseHelper).show(primaryStage, users, current, "");
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
    			
    			
    		});
    		
    		
    		
			//chose which role that the admin want to change	
			ComboBox<String> roleComboBox = new ComboBox<>();
			
			for(int i=0; i< roleCode.length(); i++) {
				
				roleComboBox.getItems().add(roleArray[i]);
				
			}
			
			
    		
    		//create button for change role
    		Button changeRole = new Button("Change Role");
    		
    		
    		
    		changeRole.setOnAction(a -> {
    		    // Display the confirmation alert
    		    Alert alert = new Alert(AlertType.CONFIRMATION);
    		    alert.setTitle("Confirm Role Change");
    		    alert.setHeaderText("Are you sure?");
    		    alert.setContentText("Do you really want to change the role for user " + user.getUserName() + "?");

    		    ButtonType yesButton = new ButtonType("Yes", ButtonData.YES);
    		    ButtonType noButton = new ButtonType("No", ButtonData.NO);
    		    alert.getButtonTypes().setAll(yesButton, noButton);

    		    // Show the alert and capture the result
    		    Optional<ButtonType> result = alert.showAndWait();

    		    // Proceed only if the "Yes" button is clicked
    		    if (result.isPresent() && result.get() == yesButton) { 
    		        System.out.println(user.getUserName());

    		        // Check if role count is valid (roleCode logic is assumed to be correct)
    		        if (user.roleCount(roleCode) <= 1) {
    		            try {
    		                // Get the selected role from the ComboBox
    		                String roleValue = roleComboBox.getValue();
    		                	
    		                // Update the role based on the selected value
    		                switch (roleValue) {
    		                    case "admin":
    		                        databaseHelper.setRole(user.getUserName(), "10000");
    		                        break;
    		                    case "instructor":
    		                        databaseHelper.setRole(user.getUserName(), "01000");
    		                        break;
    		                    case "student":
    		                        databaseHelper.setRole(user.getUserName(), "00100");
    		                        break;
    		                    case "reviewer":
    		                        databaseHelper.setRole(user.getUserName(), "00010");
    		                        break;
    		                    default:
    		                        databaseHelper.setRole(user.getUserName(), "00001");
    		                        break;
    		                }

    		                System.out.println("Role updated successfully to: " + roleValue);

    		            } catch (SQLException e) {
    		                e.printStackTrace(); // Log the error if the database update fails
    		            }
    		        } else {
    		            System.out.println("Invalid role code or count.");
    		        }
    		    }
    		    
    		 
    		});

    		Button addRole = new  Button("Add role");
    		addRole.setOnAction(a -> {
    		    // Display the confirmation alert
    		    Alert alert = new Alert(AlertType.CONFIRMATION);
    		    alert.setTitle("Confirm Role Addition");
    		    alert.setHeaderText("Are you sure?");
    		    alert.setContentText("Do you really want to add role for user " + user.getUserName() + "?");

    		    ButtonType yesButton = new ButtonType("Yes", ButtonData.YES);
    		    ButtonType noButton = new ButtonType("No", ButtonData.NO);
    		    alert.getButtonTypes().setAll(yesButton, noButton);
    		    
    		    // Show the alert and capture the result
    		    Optional<ButtonType> result = alert.showAndWait();
    		    
    		    if (result.isPresent() && result.get() == yesButton) { 
    		        System.out.println(user.getUserName());

    		        // Get the current role code and role to add
    		        String currentRoleCode = user.getRole();  // Example: "10000" for admin
    		        String roleToAddCode = "";

    		        // Map the selected role to the corresponding binary code
    		        switch (roleComboBox.getValue()) {
    		            case "admin":
    		                roleToAddCode = "10000";
    		                break;
    		            case "instructor":
    		                roleToAddCode = "01000";
    		                break;
    		            case "student":
    		                roleToAddCode = "00100";
    		                break;
    		            case "reviewer":
    		                roleToAddCode = "00010";
    		                break;
    		            case "staff":
    		                roleToAddCode = "00001";
    		                break;
    		        }

    		        // Check if the role is already present before adding
    		        if (currentRoleCode.contains(roleToAddCode)) {
    		            System.out.println("Role already assigned.");
    		            return;  // No need to add the same role
    		        }

    		        // Convert the binary role codes to integers for bitwise operations
    		        int currentRole = Integer.parseInt(currentRoleCode, 2);  // Convert binary string to integer
    		        int roleToAdd = Integer.parseInt(roleToAddCode, 2);  // Convert binary string to integer

    		        // Use bitwise OR to combine the roles
    		        int updatedRole = currentRole | roleToAdd;

    		        // Convert the updated role back to a binary string (5 characters long)
    		        String updatedRoleCode = String.format("%5s", Integer.toBinaryString(updatedRole)).replace(' ', '0');

    		        // Update the role in the database
    		        try {
    		            databaseHelper.setRole(user.getUserName(), updatedRoleCode);
    		            System.out.println("Role updated successfully to: " + updatedRoleCode);
    		        } catch (SQLException e) {
    		            e.printStackTrace();  // Log the error if the database update fails
    		        }
    		    }
    		 
    		});
    		
    		Button deleteRole = new Button("Delete Role");
    		deleteRole.setOnAction(a -> {
    		    // Display the confirmation alert
    		    Alert alert = new Alert(AlertType.CONFIRMATION);
    		    alert.setTitle("Confirm Role Deletion");
    		    alert.setHeaderText("Are you sure?");
    		    alert.setContentText("Do you really want to delete the selected role for user " + user.getUserName() + "?");

    		    ButtonType yesButton = new ButtonType("Yes", ButtonData.YES);
    		    ButtonType noButton = new ButtonType("No", ButtonData.NO);
    		    alert.getButtonTypes().setAll(yesButton, noButton);
    		    
    		    // Show the alert and capture the result
    		    Optional<ButtonType> result = alert.showAndWait();
    		    
    		    if (result.isPresent() && result.get() == yesButton) { 
    		        System.out.println(user.getUserName());

    		        // Get the current role code
    		        String currentRoleCode = user.getRole();  // Example: "10000" for admin
    		        String roleToDeleteCode = "";

    		        // Map the selected role to the corresponding binary code to delete
    		        switch (roleComboBox.getValue()) {
    		            case "admin":
    		                roleToDeleteCode = "10000";
    		                break;
    		            case "instructor":
    		                roleToDeleteCode = "01000";
    		                break;
    		            case "student":
    		                roleToDeleteCode = "00100";
    		                break;
    		            case "reviewer":
    		                roleToDeleteCode = "00010";
    		                break;
    		            case "staff":
    		                roleToDeleteCode = "00001";
    		                break;
    		        }

    		        // Create a mask to clear the bit for the selected role
    		        int currentRole = Integer.parseInt(currentRoleCode, 2);  // Convert binary string to integer
    		        int roleToDelete = Integer.parseInt(roleToDeleteCode, 2);  // Convert binary string to integer

    		        // Use bitwise AND with the inverse of the role to delete (bitwise NOT)
    		        int updatedRole = currentRole & (~roleToDelete);  // Bitwise AND with inverted role code to clear the bit

    		        // Convert the result back to a binary string (5 bits, zero-padded)
    		        String updatedRoleCode = String.format("%5s", Integer.toBinaryString(updatedRole)).replace(' ', '0');

    		        // Update the role in the database
    		        try {
    		            databaseHelper.setRole(user.getUserName(), updatedRoleCode);
    		            System.out.println("Role deleted successfully. New Role: " + updatedRoleCode);
    		        } catch (SQLException e) {
    		            e.printStackTrace();  // Log the error if the database update fails
    		        }
    		    }
    		  
    		});

    	
    		
    	
    		

    		
    		root.getChildren().addAll(userRow, userLabel, roleComboBox, changeRole,addRole, deleteRole, back);
    		
    		 Scene Role = new Scene(root, 800, 400);
    		
       	    // Set the scene to primary stage
       	    primaryStage.setScene(Role);
       	    primaryStage.setTitle("Role Setting Page");
		
	}
	
	
	
	
	
	
	
}