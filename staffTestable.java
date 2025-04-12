package application;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * StaffTestable provides isolated, testable logic used by the Staff role without any JavaFX dependencies.
 * This class extracts core business logic from the Staff UI so it can be easily tested with JUnit.
 *
 * Author: Muzzamil Jolaade
 * Version: 2025-04-11
 */
public class staffTestable {

    private final DatabaseHelper databaseHelper;

    /**
     * Constructs a testable logic class for staff operations.
     * @param databaseHelper reference to the shared DatabaseHelper
     */
    public staffTestable(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Deletes a review from the given list by index.
     * @param reviews list of reviews
     * @param index index to delete
     * @return true if successfully deleted
     * @throws SQLException if database error occurs
     */
    public boolean deleteReviewByIndex(List<reviews> reviews, int index) throws SQLException {
        if (index >= 0 && index < reviews.size()) {
            databaseHelper.deleteReview(reviews.get(index));
            return true;
        }
        return false;
    }

    /**
     * Deletes a question or answer based on the selected object.
     * @param selectedItem object selected (question or answer)
     * @return true if deletion successful, false otherwise
     * @throws SQLException if database error occurs
     */
    public boolean deleteQuestionOrAnswer(Object selectedItem) throws SQLException {
        if (selectedItem instanceof question q) {
            databaseHelper.deleteQuestion(q.getId());
            return true;
        } else if (selectedItem instanceof answer a) {
            databaseHelper.deleteAnswer(a.getText());
            return true;
        }
        return false;
    }

    /**
     * Sends a private message from one user to another.
     * @param sender username of sender
     * @param receiver username of receiver (instructor)
     * @param messageBody content of the message
     * @return true if successfully sent
     * @throws SQLException if database error occurs
     */
    public boolean sendMessageToInstructor(String sender, String receiver, String messageBody) throws SQLException {
        return databaseHelper.sendPrivate(sender, receiver, messageBody);
    }

    /**
     * Returns all reviews from the database.
     * @return list of reviews
     * @throws SQLException if database error occurs
     */
    public List<reviews> getAllReviews() throws SQLException {
        return new ArrayList<>(databaseHelper.getAllReviews());
    }

    /**
     * Returns all questions and their answers.
     * @return list of questions with answers inside them (not flattened)
     * @throws SQLException if database error occurs
     */
    public List<question> getAllQuestionsWithAnswers() throws SQLException {
        return databaseHelper.getquestion();
    }

    /**
     * Returns a list of unique private messages exchanged between students and reviewers only.
     * @return list of formatted message strings
     * @throws SQLException if database or user role retrieval fails
     */
    public List<String> getStudentReviewerMessagesOnly() throws SQLException {
        List<String> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        List<User> users = databaseHelper.getAllUsers();

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

                    String line = m.getSender() + " " + senderLabel + " ➔ " + m.getReciver() + " " + receiverLabel + ": " + m.getMessage();
                    if (seen.add(line)) {
                        result.add(line);
                    }
                }
            }
        }

        return result;
    }
}

