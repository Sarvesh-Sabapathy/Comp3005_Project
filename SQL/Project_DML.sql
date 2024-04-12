-- Insert data into Members table
INSERT INTO Members (username, password, email, full_name, height, weight, goal_weight) 
VALUES ('Billy', 'Billy123', 'billy@gmail.com', 'Billy Newton', 60, 150, 130),
       ('Jon', 'Jon123', 'jon@gmail.com', 'Jon Jones', 72, 190, 180),
       ('Will', 'Will123', 'will@gmail.com', 'Will Smith', 74, 160, 150);

-- Insert data into Trainers table
INSERT INTO Trainers (username, password, email, full_name, days_of_week_available, start_time, end_time) 
VALUES ('Trey', 'Trey123', 'trey@gmail.com', 'Trey Parker', 'Friday, Saturday', '12:00:00', '15:00:00'),
       ('Walter', 'Walter123', 'walter@gmail.com', 'Walter White', 'Monday, Tuesday, Wednesday, Thursday', '09:00:00', '17:00:00');

-- Insert data into Room Booking table
INSERT INTO roombookings (room_id, booking_date, booking_time, purpose, end_time) 
VALUES (1, '2024-04-20', '16:00:00', 'Zumba', '18:00:00'),
       (2, '2024-04-19', '18:00:00', 'Weightlifting', '20:00:00');

-- Insert data into Payment table
INSERT INTO payments (member_id, amount, payment_date, payment_method) 
VALUES (1, 50.00, '2024-04-20', 'Credit'),
       (2, 50.00, '2024-04-25', 'Debit'),
       (3, 80.00, '2024-04-25', 'Debit');

-- Insert data into Classes table
INSERT INTO classes (class_name, class_time, class_duration) 
VALUES ('Zumba', '16:00:00', '2 hours'),
       ('Weightlifting', '18:00:00', '2 hours');

-- Insert data into Equipment table
INSERT INTO equipment (equipment_name, maintenance_status) 
VALUES ('Cable Flys', true),
       ('Leg press', false);

-- Insert data into Sessions table
INSERT INTO sessions (member_id, trainer_id, session_type, session_date, session_time, end_time) 
VALUES (1, 1, '1 on 1 workout', '2024-04-12', '13:00:00', '14:00:00');