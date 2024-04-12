package org.example;
import java.sql.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            Functions functions = new Functions();

            /** Member Functions */
            // Register a new member
            //functions.registerMember("Sarvesh", "S123$", "s@gmail.com", "Sarvesh Sabapathy");
            //functions.registerMember("Sarvesh", "J123", "s@gmail.com", "John Smith");

            //Remove a member from database
            //functions.removeMember(4);

            // Update the member profile
            //functions.updateMemberProfile(4, 73.0, 200.0, 180.0);

            //Display Dashboard for member profiles
            //functions.displayDashboard(5);

            /** Trainer Functions */
            // Add a trainer to the database
            //functions.registerTrainer("Trainer", "T123$", "T@gmail.com", "Trainer Name");
            //functions.registerTrainer("Trainer", "T123$", "T@gmail.com", "Tester Name");

            //Remove trainer from database
            //functions.removeTrainer(5);

            // Update the trainer profile information
            //functions.updateTrainerProfile(, "t@gmail.com", "t123$", "Trainer Name");

            // Set the availability of trainers
            //functions.setTrainerAvailability("Trainer", "13:00:00", "18:00:00", "Monday, Tuesday, Wednesday, Thursday, Friday");

            // View trainer profile information
            //functions.viewTrainerProfile("Trainer");

            // Allows trainers to view the member profiles
            //functions.viewMemberProfile("Sarvesh Sabapathy");


            /** Administrative Staff Functions */
            //Time startTime = Time.valueOf("12:00:00");
            //Time endTime = Time.valueOf("15:59:59");

            // Allows members to schedule sessions with trainers
            //functions.scheduleSession(1, 1, "cardio", "2024-05-18", startTime, endTime);
            //functions.scheduleSession(4, 1, "cardio", "2024-04-13", Time.valueOf("13:00:00"), Time.valueOf("14:00:00"));

            // Allows members to cancel sessions
            //functions.cancelSession(2);

            // Staff can book rooms to use and remove bookings
            //functions.bookRoom(1, "2022-05-15", "18:00:00","20:00:00", "Zumba");
            //functions.removeBooking(3);

            // Staff can add new equipment to database, check if equipment is under maintenance and remove equipment from database
            //functions.addEquipment("incline chest press",true);
            //functions.isEquipmentUnderMaintenance(1);
            //functions.removeEquipment(1);

            // Staff can add and remove classes from the database
            //functions.addClass("Yoga", Time.valueOf("10:00:00"), "1 hour");
            //functions.removeClass(2);

            //Staff can create bills for members, remove bills, and display the bill info
            //functions.createBill(1, 50.0, Date.valueOf("2024-04-10"), "Credit Card");
            //.removeBill(1);
            //functions.displayBill(4);

            // Close the connection
            functions.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}