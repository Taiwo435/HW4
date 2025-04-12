package application;

import databasePart1.DatabaseHelper;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseHelper dbHelper = new DatabaseHelper();
            dbHelper.connectToDatabase(); // Step 1: Connect to DB

            // Step 2: Create two students and one reviewer
            User student1 = new User("Jolaade.T", "MyP@ss123", "student1@example.com", "00100");
            User student2 = new User("Musty1", "123Password!", "student2@example.com", "00100");
            User reviewer = new User("Kehinde2", "Mudd@56thir", "reviewer1@example.com", "00010");
            User instructor = new User("Naheem", "AminuPet8@", "instructor@example.com", "01000");
            // Step 3: Register them if not already present
            if (!dbHelper.doesUserExist(student1.getUserName())) {
                dbHelper.register(student1);
            }

            if (!dbHelper.doesUserExist(student2.getUserName())) {
                dbHelper.register(student2);
            }

            if (!dbHelper.doesUserExist(reviewer.getUserName())) {
                dbHelper.register(reviewer);
            }

            // Step 4: Add two questions from each student
            dbHelper.addQuestion("What is polymorphism?", student1.getUserName(), "CS", "2025-04-03 10:00 AM");
            dbHelper.addQuestion("How does garbage collection work in Java?", student2.getUserName(), "CS", "2025-04-03 10:05 AM");
            
          

            System.out.println("Users and questions added successfully!");

            dbHelper.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

