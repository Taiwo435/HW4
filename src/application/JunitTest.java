package application;

import databasePart1.DatabaseHelper;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests for the StaffTestable class to verify staff role permissions including:
 * - Deleting reviews by index
 * - Deleting questions and answers
 * - Sending private messages to instructors
 * - Viewing messages exchanged between students and reviewers
 *
 * Author: Muzzamil Jolaade
 * Version: 2025-04-11
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JunitTest {

    private static DatabaseHelper db;
    private static staffTestable staffLogic;
    private static final String student = "staffTestStudent";
    private static final String reviewer = "staffTestReviewer";
    private static final String instructor = "staffTestInstructor";
    private static int questionId;

    /**
     * Initializes database connection and creates necessary users before tests.
     */
    @BeforeAll
    public static void setup() throws SQLException {
        db = new DatabaseHelper();
        db.connectToDatabase();
        staffLogic = new staffTestable(db);

        if (!db.doesUserExist(student)) {
            db.register(new User(student, "123", "s@email.com", "00100"));
        }
        if (!db.doesUserExist(reviewer)) {
            db.register(new User(reviewer, "456", "r@email.com", "00010"));
        }
        if (!db.doesUserExist(instructor)) {
            db.register(new User(instructor, "789", "i@email.com", "01000"));
        }

        db.sendPrivate(student, reviewer, "Testing message to reviewer");
        db.sendPrivate(reviewer, student, "Reply from reviewer to student");
    }

    /**
     * Tests whether staff can send a private message to an instructor.
     */
    @Test
    @Order(1)
    public void testSendMessageToInstructor() throws SQLException {
        boolean sent = staffLogic.sendMessageToInstructor(student, instructor, "Hi Instructor!");
        assertTrue(sent);
    }

    /**
     * Tests if staff can fetch only messages between students and reviewers.
     */
    @Test
    @Order(2)
    public void testGetStudentReviewerMessagesOnly() throws SQLException {
        List<String> messages = staffLogic.getStudentReviewerMessagesOnly();
        assertFalse(messages.isEmpty());
        assertTrue(messages.stream().anyMatch(m -> m.contains("[Student]") && m.contains("[Reviewer]")));
    }

    /**
     * Tests whether staff can delete a review from a review list.
     */
    @Test
    @Order(3)
    public void testDeleteReviewByIndex() throws SQLException {
        db.addReview(1010, null, reviewer, "Temporary review for deletion");
        List<reviews> reviews = db.getReview(1010);
        boolean deleted = staffLogic.deleteReviewByIndex(reviews, 0);
        assertTrue(deleted);
    }

    /**
     * Tests whether staff can delete a dummy question.
     */
    @Test
    @Order(4)
    public void testDeleteQuestion() throws SQLException {
        questionId = insertDummyQuestion("Test Q", student, "general");
        question q = new question(questionId, "Test Q", student, "now", "general", false);
        assertTrue(staffLogic.deleteQuestionOrAnswer(q));
    }

    /**
     * Tests whether staff can delete a dummy answer.
     */
    @Test
    @Order(5)
    public void testDeleteAnswer() throws SQLException {
        int qId = insertDummyQuestion("Q with answer", reviewer, "logic");
        insertDummyAnswer(qId, "DeleteMe", reviewer);
        answer ans = new answer(qId, "DeleteMe", reviewer, "now", false);
        assertTrue(staffLogic.deleteQuestionOrAnswer(ans));
    }

    /**
     * Inserts a dummy question.
     */
    private static int insertDummyQuestion(String text, String author, String type) throws SQLException {
        db.addQuestion(text, author, type, "now");
        return db.getID(text);
    }

    /**
     * Inserts a dummy answer.
     */
    private static void insertDummyAnswer(int questionId, String text, String author) throws SQLException {
        db.setAnswer(new answer(questionId, text, author, "now", false));
    }

    /**
     * Closes database connection.
     */
    @AfterAll
    public static void tearDown() {
        db.closeConnection();
    }
}
