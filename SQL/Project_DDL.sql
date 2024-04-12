-- Create Members table
CREATE TABLE members (
    member_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    height DOUBLE PRECISION,
    weight DOUBLE PRECISION,
    goal_weight DOUBLE PRECISION
);

-- Create Trainers table
CREATE TABLE trainers (
    trainer_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    days_of_week_available VARCHAR(50),
    start_time TIME,
    end_time TIME
);

-- Create Sessions table
CREATE TABLE sessions (
    session_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    trainer_id INT NOT NULL,
    session_type VARCHAR(100),
    session_date DATE,
    session_time TIME,
    end_time TIME,
    FOREIGN KEY (member_id) REFERENCES Members(member_id),
    FOREIGN KEY (trainer_id) REFERENCES Trainers(trainer_id)
);

-- Create Equipment table
CREATE TABLE equipment (
    equipment_id SERIAL PRIMARY KEY,
    equipment_name VARCHAR(100) UNIQUE NOT NULL,
    maintenance_status BOOLEAN NOT NULL
);


-- Create RoomBookings table
CREATE TABLE roombookings (
    booking_id SERIAL PRIMARY KEY,
    room_id INT NOT NULL,
    booking_date DATE,
    booking_time TIME,
    end_time TIME,
    purpose VARCHAR(200),
);

-- Create Classes table
CREATE TABLE classes (
    class_id SERIAL PRIMARY KEY,
    class_name VARCHAR(100) UNIQUE NOT NULL,
    class_time TIME,
    class_duration INTERVAL
);

-- Create Payments table
CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    member_id INT NOT NULL,
    amount NUMERIC(10, 2) NOT NULL,
    payment_date DATE,
    payment_method VARCHAR(100),
    FOREIGN KEY (member_id) REFERENCES Members(member_id)
);