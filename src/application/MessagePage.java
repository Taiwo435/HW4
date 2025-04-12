package application;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The MessagePage class handles the display and interaction
 * of the messaging feature for a logged-in user.
 *
 * <p>This UI allows users to:
 * <ul>
 *     <li>View all sent and received messages</li>
 *     <li>Compose and send a new private message</li>
 *     <li>Return to the student dashboard</li>
 * </ul>
 *
 * <p>Messages are visually grouped and styled based on whether they were sent or received.
 * Database operations are facilitated through the {@link DatabaseHelper}.
 *
 */
public class MessagePage {

    private final DatabaseHelper databaseHelper;
    private User currentUser;
    private Stage stage;

    /**
     * Constructs a new MessagePage with the given database helper.
     *
     * @param databaseHelper The DatabaseHelper used to retrieve and send messages.
     */
    public MessagePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the message UI for the current user.
     * It includes all messages (sent and received), a message composition field, and send functionality.
     *
     * @param primaryStage The JavaFX stage to display the UI.
     * @param messages A list of message objects to display.
     * @param user The currently logged-in User.
     * @throws SQLException If any database operation fails while sending or retrieving messages.
     */
    public void show(Stage primaryStage, List<message> messages, User user) throws SQLException {
        currentUser = user;
        stage = primaryStage;

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setStyle("-fx-background-color: #f4f4f4;");

        double WINDOW_WIDTH = 1000;
        double WINDOW_HEIGHT = 600;

        Label title = new Label("Messages");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox messageList = new VBox(10);
        messageList.setPadding(new Insets(10));
        messageList.setStyle("-fx-background-color: #ffffff; -fx-padding: 10px;");

        // Sort messages if timestamp is available
        // messages.sort(Comparator.comparing(message::getTime)); // Uncomment if `getTime()` exists

        // Display each message (sent/received mixed)
        for (message msg : messages) {
            boolean isSent = msg.getSender().equals(currentUser.getUserName());

            String direction = isSent ? "To: " + msg.getReciver() : "From: " + msg.getSender();
            String fullText = direction + "\n" + msg.getMessage();

            Label msgLabel = new Label(fullText);
            msgLabel.setStyle(
                "-fx-padding: 10px;" +
                "-fx-border-radius: 10px;" +
                "-fx-background-radius: 10px;" +
                "-fx-background-color: " + (isSent ? "#DCF8C6" : "#FFFFFF") + ";" +
                "-fx-border-color: #ccc;"
            );

            HBox wrapper = new HBox(msgLabel);
            wrapper.setPadding(new Insets(5));
            wrapper.setAlignment(isSent ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            messageList.getChildren().add(wrapper);
        }

        ScrollPane scrollPane = new ScrollPane(messageList);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 5px;");

        // Send message section
        TextField recipientField = new TextField();
        recipientField.setPromptText("Enter recipient username");
        recipientField.setPrefWidth(200);

        TextField messageField = new TextField();
        messageField.setPromptText("Enter your message here...");
        messageField.setPrefWidth(400);

        Button sendButton = new Button("Send");
        sendButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        sendButton.setOnAction(e -> {
            String recipient = recipientField.getText().trim();
            String messageText = messageField.getText().trim();
            if (!recipient.isEmpty() && !messageText.isEmpty()) {
                try {
                    databaseHelper.sendPrivate(currentUser.getUserName(), recipient, messageText);
                    recipientField.clear();
                    messageField.clear();
                    // Refresh message view after sending
                    List<message> updatedMessages = databaseHelper.getAllMessagesForUser(currentUser.getUserName());
                    new MessagePage(databaseHelper).show(primaryStage, updatedMessages, currentUser);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        HBox sendBox = new HBox(10, recipientField, messageField, sendButton);
        sendBox.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new Student(databaseHelper).showPage(primaryStage, user));

        layout.getChildren().addAll(title, scrollPane, sendBox, backButton);

        Scene scene = new Scene(layout, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Messages");
    }
}