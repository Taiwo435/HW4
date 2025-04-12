package application;

import java.sql.SQLException;
import java.util.List;
import databasePart1.DatabaseHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The QuestionPage class is responsible for displaying questions from the database,
 * allowing users to:
 * - Search for questions based on type
 * - Submit new questions
 * - Reply to questions
 * - Comment on questions and answers (if reviewer)
 * - View answers and reviews
 */
public class QuestionPage {
    private final DatabaseHelper databaseHelper;
    private VBox questionList; // Container for displaying questions
    private List<question> questions; 
    private List<reviews> reviews;
    private User currentUser;
    private Stage stage;
    /**
     * Constructor for the QuestionPage.
     * @param databaseHelper The database helper instance to interact with the database.
     */
    public QuestionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the main question page.
     * @param primaryStage The main application stage.
     * @param ques The list of questions to display.
     * @param user The currently logged-in user.
     * @throws SQLException If database interaction fails.
     */
    public void show(Stage primaryStage, List<question> ques, User user, Scene before) throws SQLException {
        currentUser = user;
        stage = primaryStage;
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_LEFT);
        layout.setStyle("-fx-background-color: #f4f4f4;");
        
        System.out.println(user.getRole());

        double WINDOW_WIDTH = 1000;  
        double WINDOW_HEIGHT = 600;

        //Title Label
        Label title = new Label("Questions");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333;");


        //Container for displaying questions
        questionList = new VBox(5);
        this.questions = ques; 
        updateQuestionList(user, before); 

        //Add ScrollPane to handle many questions/answers
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(questionList);
        scrollPane.setFitToWidth(true); // Ensures it expands width-wise
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS); // Always show vertical scrollbar
        scrollPane.setPrefHeight(400); // Adjust height as needed
        scrollPane.setStyle("-fx-background: #ffffff; -fx-border-color: #ccc; -fx-border-radius: 5px;");

       

        //Input Fields for adding a new question
        TextField quesType = new TextField();
        quesType.setStyle("-fx-padding: 10px; -fx-border-color: #aaa; -fx-border-radius: 5px;");
        quesType.setPromptText("Enter your question type");

        TextField newQuestion = new TextField();
        newQuestion.setStyle("-fx-padding: 10px; -fx-border-color: #aaa; -fx-border-radius: 5px;");
        newQuestion.setPromptText("Enter your question here...");
        newQuestion.setPrefWidth(400);  
        newQuestion.setPrefHeight(50);


        // Button: Create Question
        Button create = new Button("Create Question");
        
        create.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 10px;");
        

        create.setOnAction(a -> {
            String typeInput = quesType.getText();
            String input = newQuestion.getText();
            Time time = new Time();

            if (!input.isEmpty() && !typeInput.isEmpty()) {
                try {
                    //Store question in database
                    databaseHelper.addQuestion(input, user.getUserName(), typeInput, time.getTime());

                    //Create new Question object and add to list
                    question newQ = new question(databaseHelper.getID(input), input, user.getUserName(), 
                            time.getTime(), typeInput,false);
                    questions.add(newQ); 

                    //Refresh UI

                    updateQuestionList(user, before);

                    //Clear input fields
                    newQuestion.clear();
                    quesType.clear();
                    new QuestionPage(databaseHelper).show(primaryStage, databaseHelper.getquestion(), user, before);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        // Button: Back
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-padding: 10px;");
        backButton.setOnAction(e -> primaryStage.setScene(before));



        // Search Feature
        TextField searchField = new TextField();
        searchField.setPromptText("Search By type");
        Button search = new Button("Search");

        search.setOnAction(a -> {
            String input = searchField.getText();
            if (!input.isEmpty()) {
                questions = databaseHelper.showQuesOnType(input);
                updateQuestionList(user, before); 
            }
        });

        Button MyQues = new Button("My Questions");
        MyQues.setOnAction(a -> {
        	try {
            questions = databaseHelper.showQuesOnUser(user.getUserName());

            updateQuestionList(user, before);

            
        	} catch(SQLException e) {
        		e.printStackTrace();
        	}

        });

        Button unRead  = new Button("Unread");
        unRead.setOnAction(a -> {
            try {
                questions = databaseHelper.getQuestionsWithUnreadReplies(user.getUserName());
                updateQuestionList(user, before);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Button unSolved = new Button("Unsolved question");
        unSolved.setOnAction(a -> {
            try {
                questions = databaseHelper.getSolvedQues("unsolved");
                updateQuestionList(user, before);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });

        Button Solved = new Button("Solved question");
        Solved.setOnAction(a -> {
            try {
                questions = databaseHelper.getSolvedQues("solved");
                updateQuestionList(user, before);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        
        
        
        //Align search field to the top-right
        HBox searchBox = new HBox(10, searchField, search);
        searchBox.setAlignment(Pos.TOP_RIGHT);
        searchBox.setPadding(new Insets(0, 20, 0, 0));

        // Button Panel
        FlowPane buttonPane = new FlowPane(10, 10);
        buttonPane.setAlignment(Pos.CENTER_LEFT);
        buttonPane.getChildren().addAll(create, MyQues, unRead, unSolved, Solved, backButton);
        

        //Input Section
        HBox inputSection = new HBox(20, quesType, newQuestion, buttonPane);

        //Update layout to use scrollPane for question list
        layout.getChildren().addAll(title, searchBox, scrollPane, inputSection);

        //Create scene
        Scene questionScene = new Scene(layout, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(questionScene);
        primaryStage.setTitle("Question Page");
    }

    /**
     * Updates the list of questions shown on the page based on user interaction.
     * 
     * @param user The currently logged-in user.
     */
    private void updateQuestionList(User user, Scene before) {
    	questionList.setStyle("-fx-spacing: 10px;");

        questionList.getChildren().clear();
       

        for (question q : questions) {
            VBox questionBox = new VBox(5);
            questionBox.setStyle("-fx-background-color: #fff; -fx-border-color: #ccc; -fx-border-radius: 5px; -fx-padding: 10px;");
            questionBox.setPadding(new Insets(5, 0, 10, 0));

            // Only show unread messages for the author of the question
            
            int unreadCount = 0;
            boolean isAuthor = q.getAuthor().equals(user.getUserName());
            if (isAuthor) {
                try {
                    List<answer> unreadReplies = databaseHelper.getUnreadAnswers(user.getUserName());
                    for (answer ans : unreadReplies) {
                        if (ans.getId() == q.getId()) {
                            unreadCount++;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // Display Unread Messages Badge (Only for question author)
            String unreadText = (unreadCount > 0) ? " (" + unreadCount + " new replies)" : "";
            
            // Create the question label
            Label questionLabel = new Label("Question ID: " + q.getId() + unreadText +
                    " | Question: " + q.getText() +
                    " | Author: " + q.getAuthor() +
                    " | Type: " + q.getType() +
                    " | Time: " + q.getTime());
            
            questionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333;");

            // Button: Delete question only for the author
            Button deleteQuestionButton = new Button("Delete");
            deleteQuestionButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

            deleteQuestionButton.setOnAction(e -> {  
                try {
                	databaseHelper.deleteQuestion(q.getId());
					questions = databaseHelper.getquestion();
					updateQuestionList(user, before);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
                
                
            });
            
            deleteQuestionButton.setVisible(isAuthor);
            // Button: Mark as Solved/Unsolved (only for the question author)
            Button markSolvedButton = new Button(q.getSolve() ? "Solved" : "Unsolved");
            markSolvedButton.setOnAction(e -> {
                if (isAuthor) { // Ensure only the author can change it
                    try {
                        // Toggle solved status in the database
                        databaseHelper.setSolved(q.getId(), !q.getSolve());

                        // Refresh the question list after update
                        questions = databaseHelper.getquestion();
                        updateQuestionList(user,before);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            markSolvedButton.setDisable(!isAuthor); // Disable button if user is not the author
            
            // update button sends user to new page with the question passed in @Blair
            Button updateQuestionButton = new Button("Update");
            updateQuestionButton.setOnAction(e -> {
            	if(isAuthor) {
            		try {
            			
            			new UpdateQuestion(databaseHelper).show(stage, q, user, before);
            			
            		} catch(SQLException ex) {
            			ex.printStackTrace();
            		}
            	}
            });
            
            

            
            
            // Reply Box (Initially Hidden)
            HBox replyBox = new HBox(10);
            TextField replyField = new TextField();
            replyField.setPrefWidth(300);
            Button submitReplyButton = new Button("Submit");
            Button cancelReplyButton = new Button("Cancel");
            replyBox.getChildren().addAll(replyField, submitReplyButton, cancelReplyButton);
            replyBox.setVisible(false);

            // Button: Reply
            Button reply = new Button("Reply");
           
            reply.setOnAction(a -> {
            
            replyField.setPromptText("Enter your reply...");
            replyBox.setVisible(true);
            
            submitReplyButton.setOnAction(event -> {
                submitReply(q, replyField.getText(), replyBox, user);
                replyField.clear();
                replyBox.setVisible(false);
            });
            
            });
            
            




            // Button: Check Answers (Always available to everyone)
            Button answer = new Button("Check Answers");
            answer.setOnAction(a -> {
                checkAnswer(q, questionBox, user,before);
                if (isAuthor) { // Mark as read only if user is the author
                    try {
                        databaseHelper.markAnswersAsRead(q.getId());
                        // Refresh UI only for unread status
                        questionLabel.setText("Question ID: " + q.getId() +
                                " | Question: " + q.getText() +
                                " | Author: " + q.getAuthor() +
                                " | Type: " + q.getType() +
                                " | Time: " + q.getTime());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    

                    
                }
            });
            
            

            
            Button CheckReview = new Button("Check Reviews");
            CheckReview.setOnAction(a -> {
                checkReview(q, questionBox, user, CheckReview);
            });


            
            
            
         // Button: Close Answers (Removes all displayed answers)
            Button closeAnswers = new Button("Close Answers");
            closeAnswers.setOnAction(a -> {
                questionBox.getChildren().removeIf(node ->
                    node instanceof VBox && ((VBox) node).getChildren().stream().anyMatch(child ->
                        child instanceof Label && ((Label) child).getText().startsWith("Answer by"))
                );
            });

            
            
            HBox buttonContainer = new HBox(10, reply, answer, closeAnswers, CheckReview);


            boolean isReviewer = user.getRole().equals("reviewer");
            
            
            if (isReviewer) {
                Button comment = new Button("Add Comment");
                comment.setOnAction(a -> {    	
                    replyField.setPromptText("Enter your comment...");
                    replyBox.setVisible(true);

                    submitReplyButton.setOnAction(event -> {
                        try {
                            databaseHelper.addReview(q.getId(), null, user.getUserName(), replyField.getText());
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                        replyField.clear();
                        replyBox.setVisible(false);
                    });
                });

                cancelReplyButton.setOnAction(a -> {
                    replyField.clear();
                    replyBox.setVisible(false);
                    questionBox.requestFocus();
                });

                buttonContainer.getChildren().add(comment); 
            }

           
            


            // Place question label and solved button in the same row
            HBox questionRow = new HBox(10, questionLabel, markSolvedButton, updateQuestionButton, deleteQuestionButton);
            questionRow.setStyle("-fx-spacing: 10px;");

           

            // Add components to questionBox
            questionBox.getChildren().addAll(questionRow, buttonContainer, replyBox);

            // Add questionBox to main question list
            questionList.getChildren().add(questionBox);
            
        }
    }





    /**
     * Submits a reply to the database and updates the UI.
     * @param q The question being replied to.
     * @param replyText The reply content.
     * @param replyBox The HBox that contains the reply field.
     * @param user The user submitting the reply.
     */
    private void submitReply(question q, String replyText, HBox replyBox, User user) {
        if (!replyText.isEmpty()) {
            try {
                Time time = new Time();
                answer newAnswer = new answer(q.getId(), replyText, user.getUserName(), time.getTime(),false);
                databaseHelper.setAnswer(newAnswer);
                System.out.println("Reply submitted: " + replyText);
                questionList.getChildren().remove(replyBox);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    
    
    /**
     * Loads and displays all answers for a given question.
     * @param q The question whose answers to show.
     * @param questionBox The UI container to insert answers into.
     * @param user The currently logged-in user.
     */
    private void checkAnswer(question q, VBox questionBox, User user, Scene before) {
        try {
            // Remove old answer-related nodes (Answer by ..., buttons, review fields, etc.)
            questionBox.getChildren().removeIf(node ->
                (node instanceof VBox && ((VBox) node).getChildren().stream().anyMatch(child ->
                    child instanceof Label && ((Label) child).getText().startsWith("Answer by"))));

            // Fetch latest answers from the database
            List<answer> answers = databaseHelper.getAnswer(q.getId());

            if (answers.isEmpty()) {
                Label noAnswerLabel = new Label("No answers found for this question.");
                questionBox.getChildren().add(noAnswerLabel);
                return;
            }

            // Re-add the answers and Star/Unstar buttons
            for (answer ans : answers) {
                // Answer label (with star if applicable)
                Label answerLabel = new Label("Answer by " + ans.getAuthor()
                        + " at " + ans.getTime() + (ans.getStar() ? " ★" : "") + ":\n"
                        + ans.getText());

             
                
                
                // Star/Unstar button only visible by the question author.
                
                
                Button star = new Button(ans.getStar() ? "Unstar" : "Star");
                star.setOnAction(event -> {
                    try {
                        // Toggle star value in DB
                        databaseHelper.setStar(ans.getText(), currentUser.getUserName());
                        // Refresh answers
                        checkAnswer(q, questionBox, user,before);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
                
                star.setVisible(q.getAuthor().equals(user.getUserName()));
                
                
                

                // Delete button (only visible to the answer's author)
                boolean isAnswerAuthor = ans.getAuthor().equals(currentUser.getUserName());   
                Button deleteAnswerButton = new Button("Delete");
                deleteAnswerButton.setVisible(isAnswerAuthor);
                deleteAnswerButton.setOnAction(event -> {
                    databaseHelper.deleteAnswer(ans.getText());
					checkAnswer(q, questionBox, user,before);
                });
                
                
                
                

                // Update button (only for author)
                Button update = new Button("Update");
                update.setVisible(isAnswerAuthor);
                update.setOnAction(e -> {
                    new updateAnswer(databaseHelper).show(stage, q, user, ans, questionBox, before);
                });

                VBox answerContainer = new VBox(5);
                answerContainer.setPadding(new Insets(5));
                answerContainer.getChildren().addAll(answerLabel, star, update, deleteAnswerButton);

                // Review Button
                Button checkRe = new Button("Check Reviews");
                checkRe.setOnAction(event -> {
                    checkReviewForAnswer(ans, answerContainer, checkRe, user);
                });
                answerContainer.getChildren().add(checkRe);

                // Add review input
                if (user.getRole().equals("reviewer")) {
                    TextField inputReview = new TextField();
                    inputReview.setPromptText("Write a comment for this answer...");
                    inputReview.setPrefWidth(300);

                    Button submitReview = new Button("Submit");
                    submitReview.setOnAction(a -> {
                        String reviewText = inputReview.getText();
                        if (!reviewText.isEmpty()) {
                            try {
                                databaseHelper.addReview(-1, ans.getText(), user.getUserName(), reviewText);
                                inputReview.clear();
                                checkReviewForAnswer(ans, answerContainer, checkRe, user); // Optional: refresh after submit
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                    VBox reviewBox = new VBox(inputReview, submitReview);
                    reviewBox.setSpacing(5);

                    answerContainer.getChildren().add(reviewBox);
                }


                // Add the whole answer container
                questionBox.getChildren().add(answerContainer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper to call checkAnswer from external class (like updateAnswer).
     * @param q The question.
     * @param questionBox The UI container for that question.
     * @param user The current user.
     * @param answerButton The button that triggered the update.
     */
    	public void updateAnswer(question q, VBox questionBox,User user, Button answerButton, Scene before) {
    		
    		checkAnswer(q, questionBox,user, before);
    		
    	}
    	
    	
    	
    	
        /**
         * Loads and toggles the review section for a specific question.
         * 
         * @param q The question being reviewed.
         * @param questionBox The container to show or hide reviews.
         * @param user The currently logged-in user.
         * @param toggleButton The button clicked to toggle review visibility.
         */
    	private void checkReview(question q, VBox questionBox, User user, Button toggleButton) {
    	    // Check if reviews are already visible
    	    boolean reviewsVisible = questionBox.getChildren().stream().anyMatch(node ->
    	    node instanceof VBox && "reviewBox".equals(node.getUserData()));

    	    if (reviewsVisible) {
    	        // Hide reviews
    	        questionBox.getChildren().removeIf(node ->
    	        node instanceof VBox && "reviewBox".equals(node.getUserData())
    	        		);
    	        toggleButton.setText("Check Reviews");
    	        
    	        return;
    	    }

    	    try {
    	        // Fetch and display reviews
    	        List<reviews> reviewList = databaseHelper.getReview(q.getId());
    	        
    	        reviewList.sort((r1, r2) -> Integer.compare(r2.getThumbsUp(), r1.getThumbsUp()));


    	        if (reviewList.isEmpty()) {
    	            Label noReviewLabel = new Label("No reviews found for this question.");
    	            noReviewLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
    	            questionBox.getChildren().add(noReviewLabel);
    	        } else {
    	        	
    	        	

    	        	
    	        	
    	            for (reviews r : reviewList) {
    	            	
    	            	
    	                Label reviewLabel = new Label("Review by " + r.getAuthor() + ":\n" + r.getReview() 
    	                +"\n👍 Likes: "+ r.getThumbsUp());
    	                reviewLabel.setStyle("-fx-background-color: #e0f7fa; -fx-padding: 8px; -fx-border-color: #ccc; -fx-border-radius: 4px;");
    	               
    	                Button likeButton = new Button("👍");
    	                likeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
    	                likeButton.setOnAction(a->{;
    	                try {
							databaseHelper.incrementThumbsUp(r.getReviewId());
							r.setThumbsUp(r.getThumbsUp()+1);
							reviewLabel.setText("Review by " + r.getAuthor() + ":\n" + r.getReview() 
    	                +"\n👍 Likes: "+ r.getThumbsUp());
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    	                
    	                
    	                });
    	                
    	                VBox reviewBox = new VBox(5);
    	                reviewBox.setPadding(new Insets(5));
    	                reviewBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; "
    	                		+ "-fx-border-radius: 5px; -fx-padding: 10px;");
    	                reviewBox.getChildren().add(likeButton);
    	                reviewBox.setUserData("reviewBox");
    	                
    	                
                    
    	                
    	                boolean isAuthor = user.getUserName().equals(r.getAuthor());
    	                
    	                reviewBox.getChildren().add(reviewLabel);
    	                
    	                if(isAuthor) {
    	                	Button delete = new Button("Delete");
    	                	delete.setOnAction(a -> {
    	                	    try {
    	                	        databaseHelper.deleteReview(r);
    	                	        questionBox.getChildren().remove(reviewBox);
    	                	    } catch (SQLException ex) {
    	                	        ex.printStackTrace();
    	                	        Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete review.");
    	                	        alert.showAndWait();
    	                	    }
    	                	});

    	                	
    	                	 Button update = new Button("Update");
    	                    	update.setOnAction(a ->{
    	                    		
    	                    		openUpdateWindow(r);
    	                    		
    	                    	});
    	                
    	                    	reviewBox.getChildren().addAll(update,delete);
    	                }
    	                
    	               
    	                
    	             
    	               questionBox.getChildren().add(reviewBox);
    	                
    	            }
    	        }

    	        toggleButton.setText("Hide Reviews");

    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	        Label errorLabel = new Label("Failed to load reviews.");
    	        errorLabel.setStyle("-fx-text-fill: red;");
    	        questionBox.getChildren().add(errorLabel);
    	    }
    	}

    	
    	
        /**
         * Loads and toggles the review section for a specific answer.
         * @param ans The answer being reviewed.
         * @param answerContainer The VBox container for that answer.
         * @param toggleButton The button clicked to toggle review visibility.
         * @param user The currently logged-in user.
         */
    	private void checkReviewForAnswer(answer ans, VBox answerContainer, Button toggleButton, User user) {
    	    boolean reviewsVisible = answerContainer.getChildren().stream().anyMatch(node ->
    	        node instanceof VBox && "answerReviewBox".equals(node.getUserData()));

    	    VBox reviewBox = new VBox(5);
    	    
    	    
    	    if (reviewsVisible) {
    	        // Hide reviews for this answer only
    	        answerContainer.getChildren().removeIf(node ->
    	            node instanceof VBox && "answerReviewBox".equals(node.getUserData()));
    	        
    	        toggleButton.setText("Check Reviews");
    	        return;
    	    }

    	    try {
    	        List<reviews> reviewList = databaseHelper.getReview(ans.getText());
    	        reviewList.sort((r1, r2) -> Integer.compare(r2.getThumbsUp(), r1.getThumbsUp()));

    	        if (reviewList.isEmpty()) {
    	            Label noReviewLabel = new Label("No reviews found for this answer.");
    	            noReviewLabel.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
    	            answerContainer.getChildren().add(noReviewLabel);
    	        } else {
    	            for (reviews r : reviewList) {
       	                Label reviewLabel = new Label("Review by " + r.getAuthor() + ":\n" + r.getReview() 
    	                +"\n👍 Likes: "+ r.getThumbsUp());
    	                reviewLabel.setStyle("-fx-background-color: #e0f7fa; -fx-padding: 8px; -fx-border-color: #ccc; -fx-border-radius: 4px;");
    	               
    	                
 
    	                
    	                Button likeButton = new Button("👍");
    	                likeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 16px;");
    	                likeButton.setOnAction(a->{;
    	                try {
							databaseHelper.incrementThumbsUp(r.getReviewId());
							r.setThumbsUp(r.getThumbsUp()+1);
							reviewLabel.setText("Review by " + r.getAuthor() + ":\n" + r.getReview() 
    	                +"\n👍 Likes: "+ r.getThumbsUp());
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    	                
    	                
    	                });
    	                
    	                
    	                
    	                
       	              
    	                

    	                

    	                
    	              
    	                reviewBox.setPadding(new  Insets(5));
    	                reviewBox.setStyle("-fx-background-color: #fdf6e3; -fx-border-color: #ddd; -fx-border-radius: 5px;");
    	                reviewBox.setUserData("answerReviewBox");
    	                
    	                reviewBox.getChildren().addAll(likeButton,reviewLabel);
    	                
    	                if (user.getUserName().equals(r.getAuthor())) {
    	                    Button deleteReview = new Button("Delete");

    	                    deleteReview.setOnAction(a -> {
    	                        try {
    	                            databaseHelper.deleteReview(r);
    	                            answerContainer.getChildren().remove(reviewBox);  // ✅ use the correct VBox
    	                        } catch (SQLException ex) {
    	                            ex.printStackTrace();
    	                            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete review.");
    	                            alert.showAndWait();
    	                        }
    	                    });
  
    	                	   	                	
    	                	Button updateButton = new Button("Update");
    	                    updateButton.setOnAction(e -> openUpdateWindow(r, reviewLabel));
    	                    reviewBox.getChildren().addAll(updateButton,deleteReview);
    	                
    	                	
    	                }
    	                answerContainer.getChildren().add(reviewBox);
    	            }
    	        }

    	        toggleButton.setText("Hide Reviews");

    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	        Label errorLabel = new Label("Failed to load answer reviews.");
    	        errorLabel.setStyle("-fx-text-fill: red;");
    	        answerContainer.getChildren().add(errorLabel);
    	    }
    	}

    	
        /**
         * Opens a separate window for the user to update a review (question or answer).
         * @param r The review object being edited.
         * @param reviewLabelToUpdate Optional: the label in the main UI to update immediately.
         */
    	private void openUpdateWindow(reviews r, Label reviewLabelToUpdate) {
    	    Stage updateStage = new Stage();
    	    updateStage.setTitle("Update Your Review");

    	    VBox layout = new VBox(10);
    	    layout.setPadding(new Insets(15));

    	    Label currentLabel = new Label("Edit your review:");
    	    TextArea reviewEditor = new TextArea(r.getReview());
    	    reviewEditor.setWrapText(true);
    	    reviewEditor.setPrefHeight(100);

    	    Button saveButton = new Button("Save");
    	    saveButton.setOnAction(e -> {
    	        String newReview = reviewEditor.getText();
    	        try {
    	            databaseHelper.updateReview(r, newReview);

    	            // Live update if label provided
    	            if (reviewLabelToUpdate != null) {
    	                reviewLabelToUpdate.setText("Review by " + r.getAuthor() + ":\n" + newReview);
    	            }

    	            updateStage.close();
    	        } catch (SQLException ex) {
    	            ex.printStackTrace();
    	            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to update review.", ButtonType.OK);
    	            alert.showAndWait();
    	        }
    	    });

    	    layout.getChildren().addAll(currentLabel, reviewEditor, saveButton);

    	    Scene scene = new Scene(layout, 400, 200);
    	    updateStage.setScene(scene);
    	    updateStage.initModality(Modality.APPLICATION_MODAL);
    	    updateStage.show();
    	}
    	
        /**
         * Overloaded method to open the update window without updating a specific label.
         * @param r The review to update.
         */
    	private void openUpdateWindow(reviews r) {
    	    openUpdateWindow(r, null);
    	}



    
}  