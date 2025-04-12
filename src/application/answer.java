package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;




public class answer {

		private int questionId;
		private String text;
		private String author;
		private String time;
		private boolean star =false;
		
		public answer(int questionId,String text,String author,String time, boolean star) {
			this.questionId = questionId;
			this.text = text;
			this.author = author;
			this.time = time;
			this.star = star;
			
		}

		
		public int getId() {
			return questionId;
		}
		
		
		public String getText() {
			return text;
		}
		
		public String getAuthor() {
			return author;
		}
		
		public String getTime() {
			return time;
		}
		
		
		public void setText(String text) {
			this.text = text;
		}
		
		public void setAuthor(String author) {
			this.author = author;
		}
		
		public void setTime(String time) {
			this.time = time;
			
		}
		
		
		public boolean getStar() {
			return star;
		}
	    	
	    }
	

