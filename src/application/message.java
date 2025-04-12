package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * The message class represents a private message sent from one user to another.
 *
 * <p>Each message contains:
 * <ul>
 *     <li>sender — The username of the person sending the message</li>
 *     <li>reciver — The username of the person receiving the message</li>
 *     <li>message — The content of the message</li>
 * </ul>
 *
 * <p>This class is primarily used to model and transfer message data 
 * between the UI and the database using DatabaseHelper.
 *
 */
public class message {
	
	private String sender;
	private String reciver;
	private String message;

     /**
     * Constructs a new message object with the given sender, receiver, and message content.
     *
     * @param sender The username of the sender.
     * @param reciver The username of the receiver.
     * @param message The message content.
     */
	public message(String sender, String reciver, String  message) {
		this.sender = sender;
		this.reciver = reciver;
		this.message = message;
		
	}
	
     /**
     * Returns the sender of the message.
     *
     * @return The sender's username.
     */
	public String getSender() {
		return sender;
	}

     /**
     * Returns the receiver of the message.
     *
     * @return The receiver's username.
     */
	public String getReciver() {
		return reciver;
	}
	
     /**
     * Returns the content of the message.
     *
     * @return The message text.
     */
	public String getMessage() {
		return message;
	}
	
	
}