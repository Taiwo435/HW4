package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * The Staff class defines the interface and core functionalities available to users with the "staff" role.
 *
 * Staff members have extended permissions that allow them to manage system content and user accounts.
 * These features include viewing student-reviewer messages, deleting content, changing roles,
 * and sending private messages to instructors.
 *
 * Core functionalities include:
 * - Viewing private messages between students and reviewers
 * - Deleting user-submitted reviews
 * - Viewing and deleting all questions and answers
 * - Sending private messages to instructors
 * - Managing user roles by updating role codes
 * - Navigating the main staff interface
 *
 * This class interacts with the database through the DatabaseHelper class.
 * Each UI component is built using JavaFX and displayed through the main application stage.
 *
 * Author: Muzzamil Jolaade
 * Version: 2025-04-11
 */

public class Staff implements Role {

    private final DatabaseHelper databaseHelper;
    private User currentUser;
    
    /**
     * Constructs a Staff interface with a reference to the database helper.
     * @param databaseHelper used to manage all backend database operations
     */
    public Staff(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays all private messages exchanged between students and reviewers.
     * Filters out other roles and duplicates, and labels users based on their role.
     * @param primaryStage the main application window
     */
    public void showAllMessages(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label title = new Label("All Private Messages");
        ListView<String> listView = new ListView<>();

        try {
            List<User> users = databaseHelper.getAllUsers();
            Set<String> seen = new HashSet<>();

            for (User user : users) {
                List<message> messages = databaseHelper.getAllMessagesForUser(user.getUserName());
                for (message m : messages) {
                    String senderRole = databaseHelper.getUserRole(m.getSender());
                    String receiverRole = databaseHelper.getUserRole(m.getReciver());

                    boolean senderIsStudent = senderRole != null && senderRole.charAt(2) == '1';
                    boolean senderIsReviewer = senderRole != null && senderRole.charAt(3) == '1';
                    boolean receiverIsStudent = receiverRole != null && receiverRole.charAt(2) == '1';
                    boolean receiverIsReviewer = receiverRole != null && receiverRole.charAt(3) == '1';

                    boolean isBetweenStudentAndReviewer = 
                        (senderIsStudent && receiverIsReviewer) || 
                        (senderIsReviewer && receiverIsStudent);

                    if (isBetweenStudentAndReviewer) {
                    	String senderLabel = senderIsStudent ? "[Student]" : senderIsReviewer ? "[Reviewer]" : "";
                    	String receiverLabel = receiverIsStudent ? "[Student]" : receiverIsReviewer ? "[Reviewer]" : "";

                    	String line = m.getSender() + " " + senderLabel + " ➜ " + m.getReciver() + " " + receiverLabel + ": " + m.getMessage();

                        if (seen.add(line)) {
                            listView.getItems().add(line);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            listView.getItems().add("Error loading messages.");
        }


        Button back = new Button("Back");
        back.setOnAction(e -> showPage(primaryStage, null));

        layout.getChildren().addAll(title, listView, back);
        primaryStage.setScene(new Scene(layout, 600, 400));
    }


    /**
     * Displays all user reviews and allows the staff member to delete selected reviews.
     * @param primaryStage the main application window
     */
    public void showDeleteReviews(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label title = new Label("Reviews");
        ListView<String> listView = new ListView<>();
        final List<reviews>[] reviewList = new List[]{ new ArrayList<>() };

        try {
            reviewList[0] = databaseHelper.getAllReviews();  // ✅ get all reviews
            for (reviews r : reviewList[0]) {
                listView.getItems().add("Review by " + r.getAuthor() + ": " + r.getReview());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listView.getItems().add("Error loading reviews.");
        }

        Button deleteButton = new Button("Delete Selected Review");
        deleteButton.setOnAction(e -> {
            int selected = listView.getSelectionModel().getSelectedIndex();
            if (selected >= 0) {
                try {
                    databaseHelper.deleteReview(reviewList[0].get(selected));
                    reviewList[0].remove(selected);
                    listView.getItems().remove(selected);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    listView.getItems().add("Error deleting review.");
                }
            }
        });


        // Back button
        Button back = new Button("Back");
        back.setOnAction(e -> showPage(primaryStage, null));

        layout.getChildren().addAll(title, listView, deleteButton, back);
        primaryStage.setScene(new Scene(layout, 600, 400));
    }


    /**
     * Displays all questions and their answers in the system.
     * Allows the staff to delete either questions or answers.
     * @param primaryStage the main application window
     */
    public void showAllContent(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Label title = new Label("All Questions and Answers");
        ListView<String> listView = new ListView<>();
        List<Object> itemMap = new ArrayList<>(); // Can hold either question or answer objects

        try {
            List<question> questions = databaseHelper.getquestion();
            for (question q : questions) {
                listView.getItems().add("Q: " + q.getText() + " (by " + q.getAuthor() + ")");
                itemMap.add(q); // Store the question
                List<answer> answers = databaseHelper.getAnswer(q.getId());
                for (answer a : answers) {
                    listView.getItems().add("     A: " + a.getText() + " (by " + a.getAuthor() + ")");
                    itemMap.add(a); // Store the answer
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            listView.getItems().add("Error loading content.");
        }

        Button deleteButton = new Button("Delete Selected Item");
        deleteButton.setOnAction(e -> {
            int selected = listView.getSelectionModel().getSelectedIndex();
            if (selected >= 0 && selected < itemMap.size()) {
                Object selectedItem = itemMap.get(selected);
                try {
                    if (selectedItem instanceof question q) {
                        databaseHelper.deleteQuestion(q.getId());
                    } else if (selectedItem instanceof answer a) {
                        databaseHelper.deleteAnswer(a.getText());
                    }

                    // Refresh the page after deletion
                    showAllContent(primaryStage);

                } catch (Exception ex) {  // changed from SQLException to Exception
                    ex.printStackTrace();
                    listView.getItems().add("Error deleting item.");
                }
            }
        });


        Button back = new Button("Back");
        back.setOnAction(e -> showPage(primaryStage, null));

        layout.getChildren().addAll(title, listView, deleteButton, back);
        primaryStage.setScene(new Scene(layout, 600, 500));
    }


    /**
     * Allows staff members to send a private message to an instructor.
     * @param primaryStage the main application window
     * @param sender the current user sending the message
     */
    public void sendMessageToInstructor(Stage primaryStage, User sender) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        TextField receiverField = new TextField();
        receiverField.setPromptText("Enter Instructor Username");

        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Type your message here...");

        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            try {
                databaseHelper.sendPrivate(sender.getUserName(), receiverField.getText(), messageArea.getText());
                messageArea.clear();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e -> showPage(primaryStage, sender));

        layout.getChildren().addAll(new Label("Send Private Message to Instructor"), receiverField, messageArea, sendButton, back);
        primaryStage.setScene(new Scene(layout, 500, 400));
    }

    /**
     * Allows the staff member to assign or update the role of any user.
     * The new role is selected via a dropdown and mapped to a role code.
     * @param primaryStage the main application window
     */
    public void manageUserRoles(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        ComboBox<String> userDropdown = new ComboBox<>();
        TextField roleField = new TextField();
        roleField.setPromptText("Enter new role (e.g. student, reviewer, instructor, staff, admin)");

       
        try {
            List<User> users = databaseHelper.getAllUsers();
            for (User u : users) {
                userDropdown.getItems().add(u.getUserName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button updateButton = new Button("Update Role");
        updateButton.setOnAction(e -> {
            String selectedUser = userDropdown.getValue();
            String roleInput = roleField.getText().trim().toLowerCase();

            String roleCode = switch (roleInput) {
                case "student" -> "00100";
                case "reviewer" -> "00010";
                case "instructor" -> "01000";
                case "staff" -> "00001";
                case "admin" -> "10000";
                default -> null;
            };

            if (roleCode != null && selectedUser != null) {
                try {
                    databaseHelper.setRole(selectedUser, roleCode);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Role updated successfully.");
                    alert.showAndWait();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Failed to update role.").show();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Invalid input or user not selected.").show();
            }
        });

        Button back = new Button("Back");
        back.setOnAction(e -> showPage(primaryStage, currentUser));

        layout.getChildren().addAll(
            new Label("Change User Roles (Staff Access)"),
            userDropdown,
            roleField,
            updateButton,
            back
        );

        primaryStage.setScene(new Scene(layout, 500, 400));
    }


    /**
     * Displays the main staff homepage with access to all role-based features.
     * @param primaryStage the main application window
     * @param user the currently logged-in user
     */
    public void showPage(Stage primaryStage, User user) {
    	this.currentUser = user;
    	
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-alignment: center;");

        Label staffLabel = new Label("Hello, Staff!");
        staffLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button viewMessagesButton = new Button("View Messages");
        viewMessagesButton.setOnAction(e -> showAllMessages(primaryStage));

        Button deleteReviewsButton = new Button("Reviews");
        deleteReviewsButton.setOnAction(e -> showDeleteReviews(primaryStage));

        Button viewContentButton = new Button("View All Content");
        viewContentButton.setOnAction(e -> showAllContent(primaryStage));

        Button messageInstructorButton = new Button("Message Instructor");
        messageInstructorButton.setOnAction(e -> sendMessageToInstructor(primaryStage, currentUser));

        Button manageRolesButton = new Button("Manage User Roles");
        manageRolesButton.setOnAction(e -> manageUserRoles(primaryStage));


        Button changeRoleButton = new Button("Change Role");
        changeRoleButton.setOnAction(e -> new WelcomeLoginPage(databaseHelper).show(primaryStage, user));

        Button logoutButton = new Button("Log Out");
        logoutButton.setOnAction(e -> new UserLoginPage(databaseHelper).show(primaryStage));


      


        layout.getChildren().addAll(
                staffLabel,
                viewMessagesButton,
                deleteReviewsButton,
                viewContentButton,
                messageInstructorButton,
                manageRolesButton,
                changeRoleButton,
                logoutButton
        );

        Scene staffScene = new Scene(layout, 800, 500);
        primaryStage.setScene(staffScene);
        primaryStage.setTitle("Staff Page");
    }

    /**
     * Default implementation for showPage from the Role interface. Not used in this context.
     * @param stage the primary stage
     */
    @Override
    public void showPage(Stage stage) {
        // Not used
    }
}
