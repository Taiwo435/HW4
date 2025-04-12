package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;

public class AutomationTest {

    private static final DatabaseHelper databaseHelper = new DatabaseHelper();
    private static int numPassed = 0;
    private static int numFailed = 0;

    public static void main(String[] args) {
        /************** Test cases semi-automation report header **************/
        System.out.println("______________________________________");
        System.out.println("\nTesting Automation");

        try {
            databaseHelper.connectToDatabase();

            /************** Start of the test cases **************/
            performTestCase(1, "User Registration - testUser1", testUserRegistration("testUser1", "password123", "test1@asu.edu", "student"));
            performTestCase(2, "User Login - testUser1", testUserLogin("testUser1", "password123", "student"));
            performTestCase(3, "Adding Question - What is Java?", testAddingQuestion("What is Java?", "testUser1", "programming"));
            performTestCase(4, "Adding Answer - Java Definition", testAddingAnswer("What is Java?", "Java is a programming language.", "testUser2"));
            performTestCase(5, "Marking Answer as Starred", testMarkingStarredAnswer("Java is a programming language.", "testUser1"));

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } finally {
            cleanUpTestData();
            databaseHelper.closeConnection();
            /************** Test cases semi-automation report footer **************/
            System.out.println("____________________________________________________________________________");
            System.out.println();
            System.out.println("Number of tests passed: " + numPassed);
            System.out.println("Number of tests failed: " + numFailed);
        }
    }
    
    
    //testing for registration
    private static boolean testUserRegistration(String userName, String password, String email, String role) throws SQLException {
        if (!databaseHelper.doesUserExist(userName)) {
            databaseHelper.register(new User(userName, password, email, role));
            return databaseHelper.doesUserExist(userName);
        }
        return false;
    }
    
    
    //testing login
    private static boolean testUserLogin(String userName, String password, String role) throws SQLException {
        return databaseHelper.login(new User(userName, password, userName + "@example.com", role));
    }


    
    //testing add question
    private static boolean testAddingQuestion(String questionText, String author, String type) throws SQLException {
        databaseHelper.addQuestion(questionText, author, type, "2025-02-26");
        return databaseHelper.getID(questionText) != -1;
    }
    
    //testing add answer to question
    private static boolean testAddingAnswer(String questionText, String answerText, String author) throws SQLException {
        int questionId = databaseHelper.getID(questionText);
        if (questionId != -1) {
            databaseHelper.setAnswer(new answer(questionId, answerText, author, "2025-02-26", false));
            return databaseHelper.getAnswer(questionId).stream().anyMatch(ans -> ans.getText().equals(answerText));
        }
        return false;
    }
    
    //testing for starring answer
    private static boolean testMarkingStarredAnswer(String answerText, String user) throws SQLException {
        return databaseHelper.setStar(answerText, user);
    }
    
    
    //make a separate function to display number of passed 
    private static void performTestCase(int testCase, String description, boolean testResult) {
        System.out.println("____________________________________________________________________________\n\nTest case: " + testCase);
        System.out.println("Description: " + description);
        System.out.println("______________");

        if (testResult) {
            System.out.println("***Success*** The test passed!\n");
            numPassed++;
        } else {
            System.out.println("***Failure*** The test failed!\n");
            numFailed++;
        }
    }
    
    
    //delete all the test data
    private static void cleanUpTestData() {
        System.out.println("\n[TEST] Cleaning Up Test Data");
        try {
            deleteUser("testUser1");
            deleteQuestion("What is Java?");
        } catch (SQLException e) {
            System.err.println("Error cleaning up test data: " + e.getMessage());
        }
    }

    private static void deleteUser(String userName) throws SQLException {
        System.out.println("Deleting user: " + userName);
        databaseHelper.deleteUser(userName);
    }
    
    
    
  //delete test
    private static void deleteQuestion(String questionText) throws SQLException {
        int questionId = databaseHelper.getID(questionText);
        if (questionId != -1) {
            System.out.println("Deleting question: " + questionText);
            //databaseHelper.deleteQuestion(questionId);
        }
    }
    

    
}
