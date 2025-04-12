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


public class question {

	private int id;
	private String text;
	private String author;
	private String time;
	private String type;
	private boolean solved;
	
	
	public question(int id, String text, String author, String time,String type, boolean solved) {
		this.id = id;
		this.text = text;
		this.author = author;
		this.time = time;
		this.type = type;
		this.solved = solved;
	}
	
	
	public int getId() {
		return id;
	}
	
	
	public String getText() {
		
		return text;
	
	}
	
	public void setText(String text) {
		this.text = text;
	}


	public String getAuthor() {
		return author;
	
	}

	
	public  String getType() {
		return type;
	}
	
	
	public String getTime() {
		
		return time;
		
	}
	
	
	public boolean getSolve() {
		return solved;
	}
	
	

	
	
}
