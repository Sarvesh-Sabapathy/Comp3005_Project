package org.example;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.*;
import java.time.format.*;

/**
 * This class provides methods to interact with the database for various functionalities.
 */
public class Functions {
    // Database connection details
    private static final String url = "jdbc:postgresql://localhost:5432/Project";
    private static final String user = "postgres";
    private static final String password = "Tarvizzle123$$$";

    // Establishing the database connection
    private Connection conn;

    /**
     * Constructor to establish connection to the database.
     * @throws SQLException If a database access error occurs.
     */
    public Functions() throws SQLException {
        conn = DriverManager.getConnection(url, user, password);
    }

    // Member Functions

    /**
     * Registers a new member.
     * @param username The username of the member.
     * @param password The password of the member.
     * @param email The email address of the member.
     * @param fullName The full name of the member.
     * @return A message indicating the registration status.
     * @throws SQLException If a database access error occurs.
     */
    public String registerMember(String username, String password, String email, String fullName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) AS count FROM Members WHERE username = ? OR email = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    try (PreparedStatement emailStmt = conn.prepareStatement(
                            "SELECT email FROM Members WHERE email = ?")) {
                        emailStmt.setString(1, email);
                        ResultSet emailRs = emailStmt.executeQuery();
                        if (emailRs.next()) {
                            System.out.println("Email is already in use");
                            return "Email is already in use";
                        }
                    }
                    System.out.println("Username is already taken");
                    return "Username is already taken";
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO Members (username, password, email, full_name) VALUES (?, ?, ?, ?)")) {
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, password);
                        insertStmt.setString(3, email);
                        insertStmt.setString(4, fullName);
                        insertStmt.executeUpdate();
                        System.out.println("User registered successfully");
                        return "User registered successfully";
                    }
                }
            }
        }
        return "Failed to register user";
    }

    /**
     * Removes a member from the database.
     * @param memberId The ID of the member to be removed.
     * @throws SQLException If a database access error occurs.
     */
    public void removeMember(int memberId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM Members WHERE member_id = ?")) {
            stmt.setInt(1, memberId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Member with ID " + memberId + " removed successfully.");
            } else {
                System.out.println("No member found with ID " + memberId + ".");
            }
        }
    }

    /**
     * Updates the profile of a member.
     * @param memberId The ID of the member whose profile needs to be updated.
     * @param height The height of the member.
     * @param weight The weight of the member.
     * @param goalWeight The goal weight of the member.
     * @throws SQLException If a database access error occurs.
     */
    public void updateMemberProfile(int memberId, Double height, Double weight, Double goalWeight) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Members SET height = ?, weight = ?, goal_weight = ? WHERE member_id = ?")) {
            stmt.setDouble(1, height);
            stmt.setDouble(2, weight);
            stmt.setDouble(3, goalWeight);
            stmt.setInt(4, memberId);
            stmt.executeUpdate();
            System.out.println("Member profile updated successfully.");
        }
    }

    /**
     * Displays the dashboard for a member.
     * @param memberId The ID of the member whose dashboard needs to be displayed.
     * @throws SQLException If a database access error occurs.
     */
    public void displayDashboard(int memberId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM Members WHERE member_id = ?")) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Member ID: " + rs.getInt("member_id"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Full Name: " + rs.getString("full_name"));
                System.out.println("Height: " + rs.getDouble("height") + " inches");
                System.out.println("Weight: " + rs.getDouble("weight") + " lbs");
                System.out.println("Goal Weight: " + rs.getDouble("goal_weight") + " lbs");
                System.out.println("Member profile displayed.");
            } else {
                System.out.println("Member not found.");
            }
        }
    }

    /**
     Schedule a session for a member with a trainer.
     @param memberId The ID of the member scheduling the session.
     @param trainerId The ID of the trainer assigned to the session.
     @param sessionType The type of session (e.g., cardio, strength training).
     @param sessionDateStr The date of the session in the format "yyyy-MM-dd".
     @param startTime The start time of the session.
     @param endTime The end time of the session.
     @throws SQLException If an SQL exception occurs.
     */
    public void scheduleSession(int memberId, int trainerId, String sessionType, String sessionDateStr, Time startTime, Time endTime) throws SQLException {
        try {
            // Convert the session date string to a LocalDate object
            LocalDate sessionDate = LocalDate.parse(sessionDateStr);

            // Get the day of the week as a string
            String sessionDayOfWeek = sessionDate.getDayOfWeek().toString();

            // Check if the trainer is available at the specified time and day
            if (!isTrainerAvailable(trainerId, sessionDayOfWeek, startTime, endTime)) {
                return;
            }

            // Prepare and execute the SQL statement to insert the session
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Sessions (member_id, trainer_id, session_type, session_date, session_time, end_time) VALUES (?, ?, ?, ?, ?, ?)")) {
                stmt.setInt(1, memberId);
                stmt.setInt(2, trainerId);
                stmt.setString(3, sessionType);
                stmt.setDate(4, Date.valueOf(sessionDate));
                stmt.setTime(5, startTime);
                stmt.setTime(6, endTime);
                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Session scheduled successfully from " + startTime + " to " + endTime + ".");
                } else {
                    System.out.println("Failed to schedule session.");
                }
            }
        } catch (DateTimeParseException e) {
            System.out.println("Invalid session date or time format.");
        }
    }

    /**
     Check if a trainer is available at the specified time and day for a session.
     @param trainerId The ID of the trainer.
     @param dayOfWeek The day of the week as a string (e.g., "MONDAY").
     @param startTime The start time of the session.
     @param endTime The end time of the session.
     @return True if the trainer is available, false otherwise.
     @throws SQLException If an SQL exception occurs.
     */
    public boolean isTrainerAvailable(int trainerId, String dayOfWeek, Time startTime, Time endTime) throws SQLException {
        try {
            // Parse the day of the week string to DayOfWeek enum
            DayOfWeek day = DayOfWeek.valueOf(dayOfWeek.toUpperCase()); // Ensure proper capitalization

            // Query the database to check the trainer's availability for the given day and time slot
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT start_time, end_time, days_of_week_available FROM Trainers WHERE trainer_id = ?")) {
                stmt.setInt(1, trainerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Time trainerStartTime = rs.getTime("start_time");
                    Time trainerEndTime = rs.getTime("end_time");

                    // Check if the trainer is available on the given day of the week
                    if (isTrainerAvailableOnDay(day, rs.getString("days_of_week_available"))) {
                        // Check if the session time falls within the trainer's available time slot
                        if (startTime.after(trainerStartTime) && endTime.before(trainerEndTime)) {
                            return true; // Trainer is available at the specified time
                        } else {
                            System.out.println("Trainer is not available during the specified time slot.");
                            return false;
                        }
                    } else {
                        System.out.println("Trainer is not available on the specified day.");
                        return false;
                    }
                } else {
                    System.out.println("Trainer not found.");
                    return false;
                }
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid day of the week string
            System.out.println("Invalid day of the week format.");
            return false;
        }
    }

    /**
     Check if a trainer is available on the specified day of the week.
     @param day The day of the week as a DayOfWeek enum.
     @param daysOfWeek The comma-separated list of available days for the trainer.
     @return True if the trainer is available on the specified day, false otherwise.
     */
    private boolean isTrainerAvailableOnDay(DayOfWeek day, String daysOfWeek) {
        // Parse the comma-separated list of available days and check if the given day is included
        String[] availableDays = daysOfWeek.split(", ");
        for (String availableDay : availableDays) {
            if (DayOfWeek.valueOf(availableDay.toUpperCase()) == day) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cancels a session.
     * @param sessionId The ID of the session to be cancelled.
     * @throws SQLException If a database access error occurs.
     */
    public void cancelSession(int sessionId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM Sessions WHERE session_id = ?")) {
            stmt.setInt(1, sessionId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Session cancelled successfully.");
            } else {
                System.out.println("No session found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Trainer Functions

    /**
     * Registers a new trainer.
     * @param username The username of the trainer.
     * @param password The password of the trainer.
     * @param email The email address of the trainer.
     * @param fullName The full name of the trainer.
     * @return A message indicating the registration status.
     * @throws SQLException If a database access error occurs.
     */
    public String registerTrainer(String username, String password, String email, String fullName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) AS count FROM Trainers WHERE username = ? OR email = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                if (count > 0) {
                    try (PreparedStatement emailStmt = conn.prepareStatement(
                            "SELECT email FROM Trainers WHERE email = ?")) {
                        emailStmt.setString(1, email);
                        ResultSet emailRs = emailStmt.executeQuery();
                        if (emailRs.next()) {
                            System.out.println("Trainer email is already in use");
                            return "Trainer email is already in use";
                        }
                    }
                    System.out.println("Trainer username is already taken");
                    return "Trainer username is already taken";
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO Trainers (username, password, email, full_name) VALUES (?, ?, ?, ?)")) {
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, password);
                        insertStmt.setString(3, email);
                        insertStmt.setString(4, fullName);
                        insertStmt.executeUpdate();
                        System.out.println("Trainer registered successfully");
                        return "Trainer registered successfully";
                    }
                }
            }
        }
        return "Failed to register trainer";
    }

    public String removeTrainer(int trainerId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) AS count FROM Trainers WHERE trainer_id = ?")) {
            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt("count") > 0) {
                try (PreparedStatement deleteStmt = conn.prepareStatement(
                        "DELETE FROM Trainers WHERE trainer_id = ?")) {
                    deleteStmt.setInt(1, trainerId);
                    int rowsDeleted = deleteStmt.executeUpdate();
                    if (rowsDeleted > 0) {
                        System.out.println("Trainer removed successfully");
                        return "Trainer removed successfully";
                    } else {
                        System.out.println("Failed to remove trainer");
                        return "Failed to remove trainer";
                    }
                }
            } else {
                System.out.println("Trainer ID not valid");
                return "Trainer ID not valid";
            }
        }
    }

    /**
     * Updates the profile of a trainer.
     * @param trainerId The ID of the trainer whose profile needs to be updated.
     * @param email The email address of the trainer.
     * @param password The password of the trainer.
     * @param fullName The full name of the trainer.
     * @throws SQLException If a database access error occurs.
     */
    public void updateTrainerProfile(int trainerId, String email, String password, String fullName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Trainers SET email = ?, password = ?, full_name = ? WHERE trainer_id = ?")) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            stmt.setString(3, fullName);
            stmt.setInt(4, trainerId);
            stmt.executeUpdate();
            System.out.println("Trainer profile updated successfully.");
        }
    }

    /**
     * Displays the profile of a trainer.
     * @param username The username of the trainer.
     * @throws SQLException If a database access error occurs.
     */
    public void viewTrainerProfile(String username) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM Trainers WHERE username = ?")) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Full Name: " + rs.getString("full_name"));
                System.out.println("Available Days: " + rs.getString("days_of_week_available"));
                System.out.println("Start Time: " + rs.getTime("start_time"));
                System.out.println("End Time: " + rs.getTime("end_time"));
                // Add more information as needed
                System.out.println("Trainer profile displayed.");
            } else {
                System.out.println("Trainer not found.");
            }
        }
    }

    /**
     Updates the availability schedule of a trainer.
     @param username The username of the trainer.
     @param startTimeStr The start time of the available period in "HH:MM:SS" format.
     @param endTimeStr The end time of the available period in "HH:MM:SS" format.
     @param daysOfWeek The days of the week when the trainer is available, separated by commas (e.g., "Monday, Tuesday, Wednesday").
     @throws SQLException If an SQL exception occurs.
     */
    public void setTrainerAvailability(String username, String startTimeStr, String endTimeStr, String daysOfWeek) throws SQLException {
        try {
            // Convert string times to Time objects
            Time startTime = Time.valueOf(startTimeStr);
            Time endTime = Time.valueOf(endTimeStr);

            try (PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE trainers SET start_time = ?, end_time = ?, days_of_week_available = ? WHERE username = ?")) {
                stmt.setTime(1, startTime);
                stmt.setTime(2, endTime);
                stmt.setString(3, daysOfWeek);
                stmt.setString(4, username);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Trainer availability updated successfully.");
                } else {
                    System.out.println("Trainer not found.");
                }
            }
        } catch (IllegalArgumentException e) {
            // Handle invalid time format
            System.out.println("Invalid time format. Please use the format HH:MM:SS.");
        }
    }

    /**
     * Views the profile of a member.
     * @param memberName The full name of the member.
     * @throws SQLException If a database access error occurs.
     */
    public void viewMemberProfile(String memberName) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT username, weight, height, goal_weight FROM Members WHERE full_name = ?")) {
            stmt.setString(1, memberName);
            System.out.println("Executing SQL query: " + stmt.toString()); // Debug statement to print SQL query
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Weight: " + rs.getString("weight") + " lbs");
                System.out.println("Height: " + rs.getString("height") + " inches");
                System.out.println("Goal Weight: " + rs.getString("goal_weight") + " lbs");
                System.out.println("Member profile displayed.");
            } else {
                System.out.println("Member not found.");
            }
        }
    }

    // Administrative Staff Functions

    /**
     * Books a room for a specific date and time for a specific purpose.
     * @param roomId The ID of the room to be booked.
     * @param bookingDateStr The date of the booking in "yyyy-MM-dd" format.
     * @param startTimeStr The start time of the booking in "HH:mm:ss" format.
     * @param endTimeStr The end time of the booking in "HH:mm:ss" format.
     * @param purpose The purpose of the booking.
     * @throws SQLException If a database access error occurs.
     */
    public void bookRoom(int roomId, String bookingDateStr, String startTimeStr, String endTimeStr, String purpose) throws SQLException {
        try {
            // Convert the string representations of date, start time, and end time to java.sql.Date and java.sql.Time objects
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(bookingDateStr);
            java.sql.Date bookingDate = new java.sql.Date(parsedDate.getTime());

            Time startTime = Time.valueOf(startTimeStr); // Already in correct format
            Time endTime = Time.valueOf(endTimeStr); // Already in correct format

            // Prepare and execute the SQL statement
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO RoomBookings (room_id, booking_date, booking_time, end_time, purpose) VALUES (?, ?, ?, ?, ?)")) {
                stmt.setInt(1, roomId);
                stmt.setDate(2, bookingDate);
                stmt.setTime(3, startTime);
                stmt.setTime(4, endTime);
                stmt.setString(5, purpose);
                stmt.executeUpdate();
                System.out.println("Room booked successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a booking for a room.
     * @param bookingId The ID of the booking to be removed.
     * @throws SQLException If a database access error occurs.
     */
    public void removeBooking(int bookingId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM RoomBookings WHERE booking_id = ?")) {
            stmt.setInt(1, bookingId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Booking removed successfully.");
            } else {
                System.out.println("No booking found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a new equipment to the database.
     * @param equipmentName The name of the equipment.
     * @param underMaintenance Whether the equipment is under maintenance (true) or not (false).
     * @throws SQLException If a database access error occurs.
     */
    public void addEquipment(String equipmentName, boolean underMaintenance) throws SQLException {
        try {
            // Prepare and execute the SQL statement
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO Equipment (equipment_name, maintenance_status) VALUES (?, ?)")) {
                stmt.setString(1, equipmentName);
                stmt.setBoolean(2, underMaintenance);
                stmt.executeUpdate();
                System.out.println("Equipment added successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a piece of equipment from the database.
     * @param equipmentId The ID of the equipment to be removed.
     * @throws SQLException If a database access error occurs.
     */
    public void removeEquipment(int equipmentId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM Equipment WHERE equipment_id = ?")) {
            stmt.setInt(1, equipmentId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Equipment removed successfully.");
            } else {
                System.out.println("No equipment found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a specific piece of equipment is under maintenance.
     * @param equipmentId The ID of the equipment.
     * @return True if the equipment is under maintenance, otherwise false.
     * @throws SQLException If a database access error occurs.
     */
    public boolean isEquipmentUnderMaintenance(int equipmentId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT maintenance_status FROM Equipment WHERE equipment_id = ?")) {
            stmt.setInt(1, equipmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                boolean maintenanceStatus = rs.getBoolean("maintenance_status");
                if (maintenanceStatus) {
                    System.out.println("Equipment with ID " + equipmentId + " is under maintenance.");
                } else {
                    System.out.println("Equipment with ID " + equipmentId + " is not under maintenance.");
                }
                return maintenanceStatus;
            } else {
                System.out.println("Equipment ID " + equipmentId + " does not exist.");
                return false;
            }
        }
    }

    /**
     * Adds a new class to the database.
     * @param className The name of the class.
     * @param classTime The time of the class.
     * @param classDuration The duration of the class.
     * @throws SQLException If a database access error occurs.
     */
    public void addClass(String className, Time classTime, String classDuration) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO Classes (class_name, class_time, class_duration) VALUES (?, ?, CAST(? AS INTERVAL))")) {
            stmt.setString(1, className);
            stmt.setTime(2, classTime);
            stmt.setString(3, classDuration);
            stmt.executeUpdate();
            System.out.println("Class added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a class from the database.
     * @param classId The ID of the class to be removed.
     * @throws SQLException If a database access error occurs.
     */
    public void removeClass(int classId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM Classes WHERE class_id = ?")) {
            stmt.setInt(1, classId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Class removed successfully.");
            } else {
                System.out.println("No class found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the schedule of a class.
     * @param classId The ID of the class to be updated.
     * @param classTime The new time of the class.
     * @param classDuration The new duration of the class.
     * @throws SQLException If a database access error occurs.
     */
    public void updateClassSchedule(int classId, Time classTime, Time classDuration) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE Classes SET class_time = ?, class_duration = ? WHERE class_id = ?")) {
            stmt.setTime(1, classTime);
            stmt.setTime(2, classDuration);
            stmt.setInt(3, classId);
            stmt.executeUpdate();
        }
    }

    /**
     * Creates a bill for a member's payment.
     * @param memberId The ID of the member.
     * @param amount The amount of the bill.
     * @param paymentDate The date of the payment.
     * @param paymentMethod The payment method used.
     * @throws SQLException If a database access error occurs.
     */
    public void createBill(int memberId, double amount, Date paymentDate, String paymentMethod) throws SQLException {
        // Check if memberId is valid
        if (!isValidMemberId(memberId)) {
            System.out.println("Invalid member ID. Bill creation failed.");
            return;
        }

        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO payments (member_id, amount, payment_date, payment_method) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, memberId);
            stmt.setDouble(2, amount);
            stmt.setDate(3, paymentDate);
            stmt.setString(4, paymentMethod);
            stmt.executeUpdate();
            System.out.println("Bill created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether a given member ID exists in the database.
     * @param memberId The ID of the member to be checked.
     * @return True if the member ID exists in the database, otherwise false.
     * @throws SQLException If a database access error occurs.
     */
    private boolean isValidMemberId(int memberId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) AS count FROM Members WHERE member_id = ?")) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt("count");
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Removes a bill from the database.
     * @param billId The ID of the bill to be removed.
     * @throws SQLException If a database access error occurs.
     */
    public void removeBill(int billId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM payments WHERE payment_id = ?")) {
            stmt.setInt(1, billId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Bill removed successfully.");
            } else {
                System.out.println("No bill found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the details of a bill.
     * @param billId The ID of the bill to be displayed.
     * @throws SQLException If a database access error occurs.
     */
    public void displayBill(int billId) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM payments WHERE payment_id = ?")) {
            stmt.setInt(1, billId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int memberId = rs.getInt("member_id");
                double amount = rs.getDouble("amount");
                Date paymentDate = rs.getDate("payment_date");
                String paymentMethod = rs.getString("payment_method");
                System.out.println("Bill ID: " + billId);
                System.out.println("Member ID: " + memberId);
                System.out.println("Amount: $" + amount);
                System.out.println("Payment Date: " + paymentDate);
                System.out.println("Payment Method: " + paymentMethod);
            } else {
                System.out.println("No bill found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Close the connection
    public void close() throws SQLException {
        conn.close();
    }
}
