
package databasePart1;
import java.sql.*;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

import application.User;
import application.answer;
import application.message;
import application.question;
import application.reviews;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	static private  Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");
			//String query  =  "SHOW COLUMNS FROM answer";
			//statement.execute(query);
		

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	/**
	 * 
	 * @throws SQLException
	 * added a roleRequest colm to send request.
	 */
	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "email VARCHAR(50), "
				+ "role VARCHAR(20),"
				+ "request INT DEFAULT 00000)";
		statement.execute(userTable);
		
		//check 
		
		
		
		
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);


	    //Create the one-time password invitation table
	    //sets unique key, # of characters, BOOLEAN to false.
	    //@JeremyGastelo
	    String passwordTable = "CREATE TABLE IF NOT EXISTS oneTimePasswords ("
	    		+ "id INT AUTO_INCREMENT PRIMARY KEY, "
	    		+ "pass VARCHAR(8),"
	    		+ "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(passwordTable);
	    
	    
	    String questionTable = "CREATE TABLE IF NOT EXISTS question("
	    		+"id INT AUTO_INCREMENT PRIMARY KEY,"
	    		+"question VARCHAR(255),"
	    		+"author VARCHAR(30),"
	    		+"type VARCHAR(30),"
	    		+"time VARCHAR(30),"
	    		+"solved_status BOOLEAN DEFAULT FALSE)";
	    statement.execute(questionTable);
	    
	    
	    String answerTable = "CREATE TABLE IF NOT EXISTS answer ("
	            + "id INT,"
	            + "answer VARCHAR(255),"
	            + "author VARCHAR(30),"
	            + "time VARCHAR(30),"
	            + "read_status BOOLEAN DEFAULT FALSE,"
	            + "star_status BOOLEAN DEFAULT FALSE)"; 

	    statement.execute(answerTable);

	    
	    
	   //jeremy: I changed this table to have a unique review_id
	    //as well as a thmbsUp integer to count them.
	    String review = "CREATE TABLE IF NOT EXISTS review ("
	    	    +"review_id INT AUTO_INCREMENT," // Unique key
	    		+"id INT,"
	    		+"answer VARCHAR(255),"
	    		+"AUTHOR VARCHAR(25),"
	    		+"review VARCHAR(255),"
	    		+ "thumbsUp INT DEFAULT 0)";
	    
	    statement.execute(review);
	    
	    
	    String message = "CREATE TABLE IF NOT EXISTS message ("
	    		+"sender VARCHAR(25),"
	    		+"reciver VARCHAR(25),"
	    		+"message VARCHAR(255))";
	    
	    statement.execute(message);

	// Taiwo's modifications
	String followTable = "CREATE TABLE IF NOT EXISTS follows ("
	            + "follower VARCHAR(255), " +     // student who follows
	            "followed VARCHAR(255), " +       // reviewer being followed
	            "PRIMARY KEY (follower, followed))"; // avoid duplicates
	    statement.execute(followTable);
	    

	 String reviewTable = "CREATE TABLE IF NOT EXISTS review ("
	            + "answer_id INT, "
	            + "reviewer VARCHAR(255), "
	            + "comment VARCHAR(255))";
	    statement.execute(reviewTable);	
	  
	    		
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, email, role) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getRole());
			pstmt.executeUpdate();
		}
	}



	//Generates a one-time password for password reset
	//using a UUID, removing the hyphens and spaces and getting a length up to 8 characters
	//@JeremyGastelo
	public String generatePassword() {
		String pass = UUID.randomUUID().toString().replace("-","").substring(0, 8); // Generate a random 8-character code
	    String query = "INSERT INTO oneTimePasswords (pass) VALUES (?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, pass);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return pass;	
	}


	//Validates the password
	//@JeremyGastelo
	public boolean validatePassword(String pass) {
   	 String query = "SELECT * FROM oneTimePasswords WHERE pass = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	       pstmt.setString(1, pass);
	        ResultSet rs = pstmt.executeQuery();
	       if (rs.next()) {
	            // Mark the code as used
	            markPasswordAsUsed(pass);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}


	//Marks the given one-time password as used.
	//@JeremyGastelo
		private void markPasswordAsUsed(String pass) {
		    String query = "UPDATE oneTimePasswords SET isUsed = TRUE WHERE pass = ?";
		    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, pass);
		        pstmt.executeUpdate();
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
		


	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRole());
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	   /**
     * Retrieves all registered users from the database.
     * @return a list of User objects
     * @throws SQLException if query fails
     */
	public List<User> getAllUsers() throws SQLException {
		
		List<User> users = new ArrayList<>();
		
		String query = "SELECT * FROM cse360users";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query);
				
				ResultSet rs = pstmt.executeQuery()){
			
			while (rs.next()) {
				User user = new User(rs.getString("userName"), rs.getString("password"),
						rs.getString("email"), rs.getString("role"));
				
				users.add(user);
			}
			
		}
				
				
	return users;	
	}

	//create deleteUser for delete account.
/*	public void deleteUser(String userName) throws SQLException{
		String query = "DELETE FROM cse360users WHERE userName = ?";
		
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, userName);
		        pstmt.executeUpdate();
		    }
		
	}
	*/
	 /**
     * Deletes a user if allowed (e.g., not an admin).
     * @param userName the username to delete
     * @return a status message indicating the result
     * @throws SQLException if query fails
     */
	public String deleteUser(String userName) throws SQLException {
	    String checkRoleQuery = "SELECT role FROM cse360users WHERE userName = ?";
	    String deleteUserQuery = "DELETE FROM cse360users WHERE userName = ?";
	    
	    try (PreparedStatement checkRoleStmt = connection.prepareStatement(checkRoleQuery);) {

	        checkRoleStmt.setString(1, userName);
	        ResultSet rs = checkRoleStmt.executeQuery();
	        
	        if (rs.next()) {
	            String role = rs.getString("role");
	            if (role.equals("10000")) {
	               return "Admins cannot delete other admins!";
	                
	            }
	        } else {
	           
	            return "User not found";
	        }
	        
	        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteUserQuery)){

	        deleteStmt.setString(1, userName);
	        int affectedRows = deleteStmt.executeUpdate();
	        if (affectedRows > 0) {
	            return "Deletion succeeded";
	        } else {
	            return "No user deleted";
	        }
	        
	    }	}
	}

	
	 /**
     * Updates the password for a specific user.
     * @param userName the user whose password to change
     * @param newPassword the new password
     */
	public void setPassword(String userName, String newPassword) {
		String query = "UPDATE cse360users SET password = ? WHERE userName = ?";
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, newPassword);
		        pstmt.setString(2, userName);
		        int rowsAffected = pstmt.executeUpdate();
		        if (rowsAffected > 0) {
		            System.out.println("Password updated successfully for user: " + userName);
		        } else {
		            System.out.println("No user found with userName: " + userName);
		        }
		        
		
		        
		        
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	
	
	
	}
	
	 /**
     * Retrieves the password for a given username (for internal verification).
     * @param userName the username to check
     * @return the password string, or null if not found
     * @throws SQLException if query fails
     */
	public String getPassword(String userName) throws SQLException {
		
		String Query = "SELECT password FROM cse360users WHERE userName = ?";
		 try (PreparedStatement pstmt = connection.prepareStatement(Query)) {
		        pstmt.setString(1, userName);
		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		                return rs.getString("password");
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return null; // return null if the user is not found or an error occurs
}
	
	
	 /**
     * Updates the role code for a given user.
     * @param userNameToChange the username to update
     * @param roleCode the new role code
     * @throws SQLException if update fails
     */
	public void setRole(String userNameToChange, String roleCode) throws SQLException {
	  

	    String query = "UPDATE cse360users SET role = ? WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        // Set the new role
	        pstmt.setString(1, roleCode);

	        // Set the username of the user whose role is being changed
	        pstmt.setString(2, userNameToChange);

	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("Role updated successfully for user: " + userNameToChange);
	        } else {
	            System.out.println("No user found with userName: " + userNameToChange);
	        }
	    } catch (SQLException e) {
	        System.err.println("Error updating role: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	
	 /**
     * Adds a new question to the database.
     * @param ques the question text
     * @param author the user who posted it
     * @param type the category/type of question
     * @param time the time of posting
     * @throws SQLException if insertion fails
     */
	public void addQuestion(String ques, String author, String type, String time) throws SQLException {
		
		String insertUser = "INSERT INTO question (question, author, type, time) VALUES (?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, ques);
			pstmt.setString(2, author);
			pstmt.setString(3, type);
			pstmt.setString(4, time);
			int affectedRows  = pstmt.executeUpdate();
			if(affectedRows==0) {
				throw new SQLException("Creating question failed, no rows affected.");
				
			}
			 
		        
		    }
		}
	

	/**
     * Submits a new answer for a question.
     * @param answer the answer object
     * @throws SQLException if insertion fails
     */
	public void setAnswer(answer answer) throws SQLException {


	    
	    String insertUser = "INSERT INTO answer (id, answer, author, time) VALUES (?, ?, ?, ?)";
	    try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
	        pstmt.setInt(1, answer.getId());
	        pstmt.setString(2, answer.getText());
	        pstmt.setString(3, answer.getAuthor());
	        pstmt.setString(4, answer.getTime());
	        pstmt.executeUpdate();
	        
	        System.out.println("bugcheck");
	    }
	    


	}

	
	 /**
     * Retrieves all questions from the database.
     * @return list of question objects
     * @throws SQLException if query fails
     */
	public List<question> getquestion() throws SQLException {
		String query = "SELECT * FROM question";
		List<question> ques = new ArrayList<>();
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
					 try (ResultSet rs = pstmt.executeQuery()) {
				            while (rs.next()) {
				                 question Q = new question(rs.getInt("id"), rs.getString("question"),
				                		rs.getString("author"),  rs.getString("time"), 
				                		rs.getString("type"),rs.getBoolean("solved_status"));
				                 ques.add(Q);
				            }
				        }
				    } catch (SQLException e) {
				        e.printStackTrace();
				    }
				    return ques; // return null if the user is not found or an error occurs
		}
	
	
	/**
     * Gets all answers for a specific question ID.
     * @param id the question ID
     * @return list of answers
     * @throws SQLException if query fails
     */
	public List<answer> getAnswer(int id) throws SQLException {
	    String query = "SELECT * FROM answer WHERE id = ?";
	    List<answer> answers = new ArrayList<>();

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, id);  
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                answer ans = new answer(
	                    id,  
	                    rs.getString("answer"),
	                    rs.getString("author"),
	                    rs.getString("time"),
	                    rs.getBoolean("star_status")
	                    
	                );
	                answers.add(ans);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return answers;
	}


	 /**
     * Gets the question ID by its text content.
     * @param question the text of the question
     * @return the ID or -1 if not found
     * @throws SQLException if query fails
     */	
	public int getID(String question) throws SQLException {
	    String query = "SELECT id FROM question WHERE question = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, question);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("id");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1; // Return -1 if the question is not found
	}
	
	 /**
     * Filters and retrieves questions by type.
     * @param type the question type
     * @return a list of matching questions
     */
	public List<question> showQuesOnType(String type){
		String query = "SELECT * FROM question WHERE type = ?";
		List <question> ques = new ArrayList<>();
	
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, type);
		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		              question current = new question(rs.getInt("id"), rs.getString("question")
		            		  ,rs.getString("time"), rs.getString("author"), 
		            		  type,rs.getBoolean("solved_status"));
		              ques.add(current);
		            }
		           
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		 return ques;
	}
	
	

	 /**
     * Returns all questions submitted by a specific user.
     * @param user the author's username
     * @return a list of their questions
     */
	public List<question> showQuesOnUser(String user) throws SQLException {

		String query = "SELECT * FROM question WHERE author = ?";
		List <question> ques = new ArrayList<>();
	
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, user);
		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		              question current = new question(rs.getInt("id"), 
		            		  rs.getString("question"),
		            		  rs.getString("author"),
		            		  rs.getString("time"), 
		            		  rs.getString("type"),
		            		  rs.getBoolean("solved_status"));
		              ques.add(current);
		            }
		           
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		 return ques;
		
		
	}
	
	 /**
     * Retrieves unread answers for questions posted by a specific user.
     * @param userName the author of the questions
     * @return a list of unread answers
     * @throws SQLException if query fails
     */
	public List<answer> getUnreadAnswers(String userName) throws SQLException {
	    String query = "SELECT * FROM answer WHERE id IN (SELECT id FROM question WHERE author = ?) AND read_status = FALSE";
	    List<answer> unreadAnswers = new ArrayList<>();
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                answer ans = new answer(rs.getInt("id"), 
	                		rs.getString("answer"), 
	                		rs.getString("author"), 
	                		rs.getString("time"),
	                		rs.getBoolean("star_status")
	                		);
	                unreadAnswers.add(ans);
	            }
	        }
	    }
	    return unreadAnswers;
	}
	
	 /**
     * Marks all answers for a given question as read.
     * @param questionId the ID of the question
     * @throws SQLException if update fails
     */
	public void markAnswersAsRead(int questionId) throws SQLException {
	    String updateQuery = "UPDATE answer SET read_status = TRUE WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
	        pstmt.setInt(1, questionId);
	        pstmt.executeUpdate();
	    }
	}

	
	 /**
     * Gets questions that have at least one unread reply.
     * @param userName the user who posted the questions
     * @return list of questions with unread answers
     * @throws SQLException if query fails
     */
	public List<question> getQuestionsWithUnreadReplies(String userName) throws SQLException {
	    String query = "SELECT DISTINCT q.* FROM question q " +
	                   "JOIN answer a ON q.id = a.id " +
	                   "WHERE q.author = ? AND a.read_status = FALSE";

	    List<question> unreadQuestions = new ArrayList<>();

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                question q = new question(
	                    rs.getInt("id"),
	                    rs.getString("question"),
	                    rs.getString("author"),
	                    rs.getString("time"),
	                    rs.getString("type"),
	                    rs.getBoolean("solved_status")
	                );
	                unreadQuestions.add(q);
	            }
	        }
	    }
	    return unreadQuestions;
	}
	
	/**
     * Gets questions based on whether they are marked solved or unsolved.
     * @param request "solved" or "unsolved"
     * @return matching questions
     * @throws SQLException if query fails
     */
	public List<question> getSolvedQues(String request) throws SQLException{
		
		String query = "SELECT * FROM question WHERE solved_status = ?";
		List<question> solved = new ArrayList<>();
		
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			 
			 if(request.equals("solved")) {
		        pstmt.setBoolean(1, true);
			 }
			 else {
				 pstmt.setBoolean(1, false);
			 }
		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		                question q = new question(
		                    rs.getInt("id"),
		                    rs.getString("question"),
		                    rs.getString("author"),
		                    rs.getString("time"),
		                    rs.getString("type"),
		                    rs.getBoolean("solved_status")
		                );
		                solved.add(q);
		            }
		        }
		    }
		
		
		return solved;
		
		
	}
	
	  /**
     * Updates the solved status of a question.
     * @param questionId the question ID
     * @param solvedStatus true for solved, false otherwise
     * @return true if update occurred
     * @throws SQLException if query fails
     */
	public boolean setSolved(int questionId, boolean solvedStatus) throws SQLException {
		
		String check = "SELECT solved_status FROM question WHERE id=?";
		
		try(PreparedStatement select = connection.prepareStatement(check)) {
			select.setInt(1, questionId);
		
			try(ResultSet rs = select.executeQuery()){
				if(rs.next()) {
				boolean currentStatus = rs.getBoolean("solved_status");
				
				if(currentStatus == solvedStatus) {
					return false;
				}
					
				}
			}
			
		}
		
		
		
	    String query = "UPDATE question SET solved_status = ? WHERE id = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setBoolean(1, solvedStatus);
	        pstmt.setInt(2, questionId);
	        int rowsAffected = pstmt.executeUpdate();
	        
	       
	        return rowsAffected > 0;
	    }
	}

	
	  /**
     * Sets or toggles the star on an answer by the question author.
     * @param answerText the text of the answer
     * @param user the username trying to star it
     * @return true if the star was changed
     * @throws SQLException if query fails
     */
	public static boolean setStar(String answerText, String user) throws SQLException {
		boolean flag = false;
		String getQuestionAuthorQuery = "SELECT q.author, a.star_status, a.answer " +
	                                    "FROM answer a " +
	                                    "JOIN question q ON a.id = q.id " +
	                                    "WHERE a.answer = ?";

	    String updateStarStatusQuery = "UPDATE answer SET star_status = ? WHERE answer = ?";

	    try (PreparedStatement getAuthorStmt = connection.prepareStatement(getQuestionAuthorQuery);
	         PreparedStatement updateStmt = connection.prepareStatement(updateStarStatusQuery)) {

	        // Check the author of the question related to the answer
	        getAuthorStmt.setString(1, answerText);
	        ResultSet rs = getAuthorStmt.executeQuery();

	        if (rs.next()) {
	            String questionAuthor = rs.getString("author");
	            boolean currentStarStatus = rs.getBoolean("star_status");

	            // Verify if the requesting user is the author of the question
	            if (!questionAuthor.equals(user)) {
	              
	                flag = false;
	                return flag;
	            }

	            // Toggle the star status
	            boolean newStarStatus = !currentStarStatus;
	            updateStmt.setBoolean(1, newStarStatus);
	            updateStmt.setString(2, answerText);
	            
	            int rowsAffected = updateStmt.executeUpdate();
	            if (rowsAffected > 0) {
	               
	                flag = true;
	            } else {
	                
	                flag = false;
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Error updating star status:");
	        e.printStackTrace();
	    }
	    
	    return flag;
	}

	
	
	/**
     * Updates an answer in the database.
     * @param answer the updated answer
     */
	public void updateAnswer(answer answer ) {
		
		String updateAnswer = "UPDATE answer SET answer = ? WHERE id = ? AND author = ? ";
		try (PreparedStatement pstmt = connection.prepareStatement(updateAnswer)) {
			pstmt.setString(1, answer.getText());
			pstmt.setInt(2, answer.getId());
			pstmt.setString(3, answer.getAuthor());
			
			
			int affectedRows  = pstmt.executeUpdate();
			
			if(affectedRows==0) {
				throw new SQLException("Creating question failed, no rows affected.");
			}
				
			System.out.println("Answer updated successfully!");
			
			
		}
	
		
		catch (SQLException e) {
	        System.err.println("Error updating answer: " + e.getMessage());
	    }

		
		
	}

	
	 /**
     * Updates a question's type and text.
     * @param question the existing question
     * @param newType the new type/category
     * @param newText the new question text
     * @throws SQLException if update fails
     */
	public void updateQuestion(question question, String newType, String newText) throws SQLException {
		// TODO Auto-generated method stub
		String query = "UPDATE question SET type = ?, question = ? WHERE question = ?";
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, newType);
		        pstmt.setString(2, newText);
		        pstmt.setString(3, question.getText());
		        int rowsAffected = pstmt.executeUpdate();
		        if (rowsAffected > 0) {
		            System.out.println("Question posed by :" + question.getAuthor() + " has been updated");
		        } else {
		            System.out.println("No such question found");
		        }
		           
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
	}

	
	/**
     * Deletes a question and its associated answers.
     * @param questionId the ID to delete
     */
	public void deleteQuestion(int questionId) {
	    String deleteAnswersQuery = "DELETE FROM answer WHERE id = ?";
	    String deleteQuestionQuery = "DELETE FROM question WHERE id = ?";
	    try (PreparedStatement pstmtAnswers = connection.prepareStatement(deleteAnswersQuery);
	         PreparedStatement pstmtQuestion = connection.prepareStatement(deleteQuestionQuery)) {
	        
	        pstmtAnswers.setInt(1, questionId);
	        pstmtAnswers.executeUpdate();
	        
	        pstmtQuestion.setInt(1, questionId);
	        pstmtQuestion.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	 /**
     * Deletes an answer by its text.
     * @param answer the answer text
     */
	public void deleteAnswer(String answer) {
	    String query = "DELETE FROM answer WHERE answer = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, answer);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	
	
	/**
     * Adds a review for a question or answer.
     * @param id the question ID
     * @param answer the answer (nullable if reviewing question)
     * @param author the reviewer
     * @param review the review text
     * @return true if inserted
     * @throws SQLException if insertion fails
     */
	public boolean addReview(int id, String answer, String author, String review) throws SQLException {
		
		String query = "INSERT INTO review (id, answer, author, review) VALUES (?, ?, ?, ?)";
		boolean flag=false;
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setInt(1, id);
			if (answer == null) {
			    pstmt.setNull(2, java.sql.Types.VARCHAR);
			} else {
			    pstmt.setString(2, answer);
			}
			pstmt.setString(3, author);
			pstmt.setString(4, review);
			int affectedRows  = pstmt.executeUpdate();
			if(affectedRows==0) {
				throw new SQLException("Creating question failed, no rows affected.");
			}
			
			else {
				flag = true;
			}
			
			return flag;
		}
			
		
	}
	

	
	 /**
     * Sends a private message from one user to another.
     * @param author sender
     * @param reciver recipient
     * @param message the message body
     * @return true if inserted
     * @throws SQLException if fails
     */
	public boolean sendPrivate(String author, String reciver, String message) throws SQLException{
		
		String query = "INSERT INTO message (sender, reciver, message) VALUES (?, ?, ?)";
		boolean flag=false;
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, author);
			pstmt.setString(2, reciver);
			pstmt.setString(3, message);
			
			int affectedRows  = pstmt.executeUpdate();
			if(affectedRows==0) {
				throw new SQLException("Creating question failed, no rows affected.");
			}
			
			else {
				flag = true;
			}
			
			return flag;
		}
		
		
		
	}
	
	/**
     * Retrieves private messages for a recipient.
     * @param reciver the username receiving messages
     * @return a list of messages
     * @throws SQLException if fails
     */
	public List<message> getPrivate(String reciver) throws SQLException{
		
		ArrayList<message> list = new ArrayList<>();
	
		String query = "SELECT * FROM message WHERE reciver = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, reciver);
			
			 try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		            	message mes = new message(
		            			rs.getString("sender"),
		            			rs.getString("reciver"),
		            			rs.getString("message"));
		            	list.add(mes);
		            }	
    
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		 
		 		return list;
		
		
	}
	
	 /**
     * Retrieves all messages sent by a specific user.
     * @param sender the sender's username
     * @return a list of sent messages
     * @throws SQLException if query fails
     */
	public List<message> getSentMessages(String sender) throws SQLException {
	    List<message> sentList = new ArrayList<>();

	    String query = "SELECT * FROM message WHERE sender = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, sender);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                message mes = new message(
	                    rs.getString("sender"),
	                    rs.getString("reciver"),
	                    rs.getString("message"));
	                sentList.add(mes);
	            }
	        }
	    }
	    return sentList;
	}

			
	
	
	 /**
     * Retrieves reviews based on question ID.
     * @param id the question ID
     * @return list of reviews
     * @throws SQLException if query fails
     */
	public List<reviews> getReview(int id) throws SQLException {
		
		String query = "SELECT * FROM review WHERE id = ?";
		List<reviews> reviews = new ArrayList<>();
		
	
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setInt(1, id);
		        
		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		            	reviews res = new reviews(
		            			rs.getInt("review_id"),
		            			rs.getInt("id"),
		            			null,
		            			rs.getString("author"),
		            			rs.getString("review"),
		            			rs.getInt("thumbsUp")
		            			);
		            	 		reviews.add(res);
		            }	
       
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		 
		 		return reviews;
		
		
	}



	/**
	 * overwrite function, return a review list based on the answer text
	 * @param answer
	 * @return
	 * @throws SQLException
	 */
	public List<reviews> getReview(String answer) throws SQLException {
		
		String query = "SELECT * FROM review WHERE answer = ?";
		List<reviews> reviews = new ArrayList<>();
		
	
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, answer);
		        
		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		            	reviews res = new reviews(
		            			rs.getInt("review_id"),
		            			-1,
		            			rs.getString("answer"),
		            			rs.getString("author"),
		            			rs.getString("review"),
		            			rs.getInt("thumbsUp")
		            			);
		            	 		reviews.add(res);
		            }	
       
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		 
		 		return reviews;
		
		
	}

	 /**
     * Retrieves all messages where the user is either sender or receiver.
     * @param username the user to search for
     * @return all relevant messages
     * @throws SQLException if query fails
     */
	public List<message> getAllMessagesForUser(String username) throws SQLException {
	    List<message> allMessages = new ArrayList<>();
	    String query = "SELECT * FROM message WHERE sender = ? OR reciver = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, username);
	        pstmt.setString(2, username);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                allMessages.add(new message(
	                    rs.getString("sender"),
	                    rs.getString("reciver"),
	                    rs.getString("message")
	                ));
	            }
	        }
	    }
	    return allMessages;
	}

	//Taiwo
		
	 /**
     * Adds a follow relationship from student to reviewer.
     * @param follower the student
     * @param reviewer the reviewer to follow
     * @return true if follow was successful
     * @throws SQLException if query fails
     */
		public boolean followReviewer(String follower, String reviewer) throws SQLException {
	    // Prevent self-follow
	    if (follower.equals(reviewer)) {
	        System.out.println("You can't follow yourself!");
	        return false;
	    }

	    // Check if already following
	    String checkQuery = "SELECT * FROM follows WHERE follower = ? AND followed = ?";
	    String insertQuery = "INSERT INTO follows (follower, followed) VALUES (?, ?)";

	    try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
	         PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {

	        checkStmt.setString(1, follower);
	        checkStmt.setString(2, reviewer);
	        ResultSet rs = checkStmt.executeQuery();

	        if (rs.next()) {
	            System.out.println("You already follow this reviewer.");
	            return false;
	        }

	       
	        insertStmt.setString(1, follower);
	        insertStmt.setString(2, reviewer);
	        int rows = insertStmt.executeUpdate();

	        if (rows > 0) {
	            System.out.println("You are now following: " + reviewer);
	            return true;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	

		
		/**
	     * Removes a follow relationship between student and reviewer.
	     * @param follower the student
	     * @param reviewer the reviewer
	     * @return true if unfollowed
	     * @throws SQLException if query fails
	     */
	public boolean unfollowReviewer(String follower, String reviewer) throws SQLException {
	    String deleteQuery = "DELETE FROM follows WHERE follower = ? AND followed = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(deleteQuery)) {
	        pstmt.setString(1, follower);
	        pstmt.setString(2, reviewer);
	        int rows = pstmt.executeUpdate();
	        return rows > 0;
	    }
	}

	
	/**
     * Gets a list of reviewers followed by a student.
     * @param follower the student's username
     * @return list of followed reviewer usernames
     * @throws SQLException if query fails
     */
	public List<String> getFollowedReviewers(String follower) throws SQLException {
	    List<String> followedList = new ArrayList<>();
	    String query = "SELECT followed FROM follows WHERE follower = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, follower);
	        ResultSet rs = pstmt.executeQuery();
	        while (rs.next()) {
	            followedList.add(rs.getString("followed"));
	        }
	    }
	    return followedList;
	}
	

	 /**
     * Retrieves all users with the reviewer role based on role bitmask.
     * @return list of reviewer users
     * @throws SQLException if query fails
     */
	public List<User> getAllReviewers() throws SQLException {
	    List<User> reviewers = new ArrayList<>();

	    String query = "SELECT * FROM cse360users";

	    try (PreparedStatement pstmt = connection.prepareStatement(query);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            User reviewer = new User(
	                rs.getString("userName"),
	                rs.getString("password"),
	                rs.getString("email"),
	                rs.getString("role")
	            );
	            
	            if(reviewer.getRole().charAt(3)=='1'){
	            reviewers.add(reviewer);
	            }
	        }
	    }

	    return reviewers;
	}


	/**
     * Updates a specific review comment.
     * @param review the review to update
     * @param newReview the new comment text
     * @throws SQLException if query fails
     */
	public void updateReview(reviews review, String newReview) throws SQLException {
		
		String query = "UPDATE review SET review = ? WHERE author  =  ? AND review = ?";
		
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			 	
			 	pstmt.setString(1, newReview);
		        pstmt.setString(2, review.getAuthor());
		        pstmt.setString(3, review.getReview());
		        int rowsAffected = pstmt.executeUpdate();
		        if (rowsAffected > 0) {
		            System.out.println("Review posed by :" +review.getAuthor()  + " has been updated");
		        } else {
		            System.out.println("No such Review found");
		        }
		           
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		
		
		
		
	}

	/**
	 * This function returns a list of all the students who have requested Reviewer privileges 
	 * @author Blair Brownlie-Edward
	 * @throws a SQL exception in the case something goes wrong
	 * @return List of users
	 */
	public List<User> getPotentialReviewers() throws SQLException {
		List<User> pendingReviewers = new ArrayList<>();
		String query = "SELECT * FROM cse360users WHERE request = 00010";
				
				try (PreparedStatement pstmt = connection.prepareStatement(query);
						
						ResultSet rs = pstmt.executeQuery()){
					
					while (rs.next()) {
						User user = new User(rs.getString("userName"), rs.getString("password"),
								rs.getString("email"), rs.getString("role"));
						
						pendingReviewers.add(user);
					}
					
				}
		return pendingReviewers;
	}
	
	/**
	 * function that rejects the students request to be a reviewer
	 * @author Blair Brownlie-Edward
	 * @param student
	 * @throws SQLException
	 */
	public void rejectReviewerRequest(User student) throws SQLException {
		// TODO Auto-generated method stub
		String query = "UPDATE cse360users SET request = 00000 WHERE userName = ?";
		
		
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, student.getUserName());
			
			int rowsAffected = pstmt.executeUpdate();
			
			if(rowsAffected > 0) {
				System.out.println(student.getUserName() + " has been denied reviewer access");
			}
			else {
				System.out.println("Make sure the user exists");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * method that accepts a students request to be a reviewer
	 * @author Blair Brownlie-Edward
	 * @param student
	 * @throws SQLException
	 */

	
	public void acceptReviewerRequest(User student) throws SQLException {
		// TODO Auto-generated method stub
		String query = "UPDATE cse360users SET request = 00000 , role = ? WHERE userName = ?";
		
		
		
		char[] role = student.getRole().toCharArray();
		
		role[3] = '1';
		
		String roleString = new String(role);
		
		System.out.println(roleString);
		
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, roleString);
			pstmt.setString(2, student.getUserName());
			
			int rowsAffected = pstmt.executeUpdate();
			if(rowsAffected > 0) {
				System.out.println(student.getUserName() + " was granted reviewer access");
			}
			else {
				System.out.println("Make sure this user exists");
			}
		}
	}
	
	/**
	 * method that allows students to request reviewer privileges
	 * @author Blair Brownlie-Edward
	 * @param user
	 * @throws SQLException
	 */
	
	public void requestReviewerAccess(User user) throws SQLException {
		// TODO Auto-generated method stub
		String query = "UPDATE cse360users SET request = 00010 WHERE userName = ?";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			
			int rowsAffected = pstmt.executeUpdate();
			if(rowsAffected > 0) {
				System.out.println(user.getUserName() + " has applied for reviewer access.");
			}
			else {
				System.out.println("No rows affect, does this user exist?");
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * delete function, deletes a review based on author and review text
	 * @param review
	 * @return
	 * @throws SQLException
	 */
	public void deleteReview(reviews review) throws SQLException {
	    String query = "DELETE FROM review WHERE author = ? AND review = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, review.getAuthor());
	        pstmt.setString(2, review.getReview());
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	/**
	 * 
	 * @param reviewId Passes in the unique integer identifier for a reviewed post. (used for an answer review or question review)
	 * @return True if successful
	 * @throws SQLException
	 */
	public boolean incrementThumbsUp(int reviewId) throws SQLException {
	    String query = "UPDATE review SET thumbsUp = thumbsUp + 1 WHERE review_id = ?";
	    
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setInt(1, reviewId);
	        int affectedRows = pstmt.executeUpdate();
	        return affectedRows > 0;
	    }
	}
	
	/**
	 * method that returns a list of answers based on a student
	 * @author Blair Brownlie-Edward
	 * @param student
	 * @return
	 * @throws SQLException
	 */

	public List<answer> getStudentAnswers(User student) throws SQLException {
		// TODO Auto-generated method stub
		List<answer> studentAnswers = new ArrayList<>();
		String query = "SELECT * FROM answer WHERE author = ?";
		
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, student.getUserName());
			
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()) {
				answer A = new answer(rs.getInt("id"), 
                		rs.getString("answer"), 
                		rs.getString("author"), 
                		rs.getString("time"),
                		rs.getBoolean("star_status")
                		);
                 studentAnswers.add(A);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return studentAnswers;
	}

	

	

 	public  List<reviews> getReviewByUser(String author) throws SQLException{
 		String query = "SELECT * From review WHERE author = ?";
 		
		List<reviews> reviews = new ArrayList<>();
		
	
		 try (PreparedStatement pstmt = connection.prepareStatement(query)) {
		        pstmt.setString(1, author);
		        
		        try (ResultSet rs = pstmt.executeQuery()) {
		            while (rs.next()) {
		            	reviews res = new reviews(
		            			rs.getInt("review_Id"),
		            			rs.getInt("id"),
		            			rs.getString("answer"),
		            			rs.getString("author"),
		            			rs.getString("review"),
		            			rs.getInt("thumbsUp")
		            			);
		            	 		reviews.add(res);
		            }	
       
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		 
		 		return reviews;
 		
 	}
 	
 	public List<reviews> getAllReviews() throws SQLException {
 	    List<reviews> reviews = new ArrayList<>();
 	    String query = "SELECT * FROM review";
 	    try (PreparedStatement pstmt = connection.prepareStatement(query);
 	         ResultSet rs = pstmt.executeQuery()) {
 	        while (rs.next()) {
 	            reviews.add(new reviews(
 	                rs.getInt("review_id"),
 	                rs.getInt("id"),
 	                rs.getString("answer"),
 	                rs.getString("author"),
 	                rs.getString("review"),
 	                rs.getInt("thumbsUp")
 	            ));
 	        }
 	    }
 	    return reviews;
 	}

 	
 	
 	public Connection getConnection() {
 	    return connection;
 	}

 	
 	
}
	
	
	
		 



	
