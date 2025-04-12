package application;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;

import databasePart1.DatabaseHelper;

public class Student implements Role {

    private final DatabaseHelper databaseHelper;
    private User currentUser;

    public Student(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void showPage(Stage primaryStage, User user) {
        currentUser = user;

        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label studentLabel = new Label("Hello, Student!");
        studentLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button logoutButton = new Button("Log out");
        logoutButton.setOnAction(a -> new UserLoginPage(databaseHelper).show(primaryStage));

        Button changeRoleButton = new Button("Change Role");
        changeRoleButton.setOnAction(a -> new WelcomeLoginPage(databaseHelper).show(primaryStage, user));

        Button requestReviewAccess = new Button("Request to become reviewer");
        requestReviewAccess.setOnAction(a -> {
            try {
                databaseHelper.requestReviewerAccess(user);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Button checkQuestion = new Button("Check questions");
        checkQuestion.setOnAction(a -> {
            List<question> que = new ArrayList<>();
            try {
                que = databaseHelper.getquestion();
                new QuestionPage(databaseHelper).show(primaryStage, que, user, primaryStage.getScene());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        Button reviewersBtn = new Button("Reviewers");
        reviewersBtn.setOnAction(e -> showReviewersPage(primaryStage, user.getUserName()));

        Button followingBtn = new Button("Following");
        followingBtn.setOnAction(e -> {
            System.out.println("Following button clicked!");
            showFollowingReviewersPage(primaryStage, user.getUserName());
        });

        layout.getChildren().addAll(
            studentLabel,
            logoutButton,
            changeRoleButton,
            checkQuestion,
            reviewersBtn,
            followingBtn,
            requestReviewAccess
        );

        Scene studentScene = new Scene(layout, 800, 400);
        primaryStage.setScene(studentScene);
        primaryStage.setTitle("Student Page");
    }

    public void showReviewerReviews(Stage primaryStage, String reviewer, User currentUser) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));

        Label title = new Label(reviewer + "'s Reviews");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<reviews> reviewTable = new TableView<>();

        TableColumn<reviews, String> targetCol = new TableColumn<>("Review Target");
        targetCol.setCellValueFactory(data -> {
            reviews r = data.getValue();
            String target = (r.getAnswer() == null || r.getAnswer().isEmpty())
                ? "Question ID: " + r.getId()
                : "Answer: \"" + r.getAnswer() + "\"";
            return new SimpleStringProperty(target);
        });

        TableColumn<reviews, String> reviewTextCol = new TableColumn<>("Review");
        reviewTextCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getReview()));
        reviewTextCol.setPrefWidth(400);

        reviewTable.getColumns().addAll(targetCol, reviewTextCol);

        try {
            List<reviews> myReviews = databaseHelper.getReviewByUser(reviewer);
            reviewTable.setItems(FXCollections.observableArrayList(myReviews));
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load your reviews.", ButtonType.OK);
            alert.showAndWait();
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showPage(primaryStage, currentUser));

        layout.getChildren().addAll(title, reviewTable, backButton);

        Scene scene = new Scene(layout, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My Reviews");
    }

    public void showReviewersPage(Stage primaryStage, String studentUsername) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Label title = new Label("Available Reviewers");
        TableView<User> table = new TableView<>();

        TableColumn<User, String> nameCol = new TableColumn<>("Reviewer");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserName()));

        TableColumn<User, Void> actionCol = new TableColumn<>("Action");

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button followButton = new Button("Follow");
            private final Button unfollowButton = new Button("Unfollow");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                User reviewerUser = getTableView().getItems().get(getIndex());
                String reviewer = reviewerUser.getUserName();
                HBox box = new HBox(5);

                try {
                    List<String> followed = databaseHelper.getFollowedReviewers(studentUsername);
                    if (followed.contains(reviewer)) {
                        box.getChildren().add(unfollowButton);
                    } else {
                        box.getChildren().add(followButton);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                followButton.setOnAction(e -> {
                    try {
                        if (databaseHelper.followReviewer(studentUsername, reviewer)) {
                            showReviewersPage(primaryStage, studentUsername);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                unfollowButton.setOnAction(e -> {
                    try {
                        if (databaseHelper.unfollowReviewer(studentUsername, reviewer)) {
                            showReviewersPage(primaryStage, studentUsername);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                setGraphic(box);
            }
        });

        table.getColumns().addAll(nameCol, actionCol);

        try {
            List<User> reviewers = databaseHelper.getAllReviewers();
            table.setItems(FXCollections.observableArrayList(reviewers));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showPage(primaryStage, currentUser));

        layout.getChildren().addAll(title, table, backButton);
        primaryStage.setScene(new Scene(layout, 400, 550));
    }

    public void showFollowingReviewersPage(Stage primaryStage, String studentUsername) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20;");

        Label title = new Label("Reviewers You Follow");
        TableView<String> table = new TableView<>();

        TableColumn<String, String> nameCol = new TableColumn<>("Reviewer");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));

        TableColumn<String, Void> actionCol = new TableColumn<>("Action");

        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button unfollowButton = new Button("Unfollow");
            private final Button viewReviews = new Button("ViewReviews");

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                String reviewer = getTableView().getItems().get(getIndex());
                HBox box = new HBox(5);

                unfollowButton.setOnAction(e -> {
                    try {
                        if (databaseHelper.unfollowReviewer(studentUsername, reviewer)) {
                            showFollowingReviewersPage(primaryStage, studentUsername);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                viewReviews.setOnAction(e -> showReviewerReviews(primaryStage, reviewer, currentUser));

                box.getChildren().addAll(unfollowButton, viewReviews);
                setGraphic(box);
            }
        });

        table.getColumns().addAll(nameCol, actionCol);

        try {
            List<String> followedReviewers = databaseHelper.getFollowedReviewers(studentUsername);
            System.out.println("Followed reviewers: " + followedReviewers);
            if (followedReviewers.isEmpty()) {
                table.setPlaceholder(new Label("You are not following any reviewers yet."));
            }
            table.setItems(FXCollections.observableArrayList(followedReviewers));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showPage(primaryStage, currentUser));

        layout.getChildren().addAll(title, table, backButton);
        primaryStage.setScene(new Scene(layout, 400, 500));
    }

    @Override
    public void showPage(Stage stage) {
        // Required by Role interface
    }
}