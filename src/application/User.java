package application;

import java.util.ArrayList;
import java.util.List;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
	
    private String userName;
    private String password;
    private String email;
    private String role;

    // Constructor to initialize a new User object with userName, password, and role.
    public User( String userName, String password,
    		String email, String role) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.role = role;
    }
    
	// Sets the role of the user.
    public void setRole(String role) {
    	this.role=role;
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() {return email;}

    public int roleCount(String roleCode) {//count if there is more than one role
    	int count =0;
    	
    	for(int i=0; i< roleCode.length(); i++) {
    		if(roleCode.charAt(i)=='1') {
    			count++;
    		}
    	}
    	return count;
    	
    }
    
}
