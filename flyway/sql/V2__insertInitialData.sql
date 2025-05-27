--- Insert dummy data into Users
INSERT INTO Users (name, email, password, profile_picture, designation, birthdate, join_date, mood_score, predicted_burnout_risk) VALUES
('John Doe', 'john.doe@example.com', 'pass123', 'john.jpg', 'Developer', '1990-05-15', '2023-01-10', 0.75, false),
('Jane Smith', 'jane.smith@example.com', 'pass456', 'jane.jpg', 'Manager', '1988-08-22', '2022-06-15', 0.60, true),
('Alice Johnson', 'alice.j@example.com', 'pass789', 'alice.jpg', 'Designer', '1992-03-10', '2023-03-01', 0.85, false),
('Bob Brown', 'bob.brown@example.com', 'pass101', 'bob.jpg', 'Analyst', '1985-11-30', '2022-09-20', 0.40, true),
('Charlie Davis', 'charlie.d@example.com', 'pass202', 'charlie.jpg', 'Engineer', '1995-07-12', '2023-02-15', 0.90, false),
('Emma Wilson', 'emma.w@example.com', 'pass303', 'emma.jpg', 'HR', '1991-04-25', '2022-11-10', 0.65, false),
('Michael Lee', 'michael.l@example.com', 'pass404', 'michael.jpg', 'Tester', '1987-09-18', '2023-04-05', 0.55, true),
('Sarah Taylor', 'sarah.t@example.com', 'pass505', 'sarah.jpg', 'Marketing', '1993-12-03', '2022-08-12', 0.80, false),
('David Clark', 'david.c@example.com', 'pass606', 'david.jpg', 'Support', '1989-06-20', '2023-01-25', 0.70, false),
('Lisa Adams', 'lisa.a@example.com', 'pass707', 'lisa.jpg', 'Lead', '1994-10-14', '2022-07-30', 0.95, false);

-- Insert dummy data into projects
INSERT INTO Projects (title, description, created_by, created_at) VALUES
('Project Alpha', 'Develop a new feature set', 1, '2025-05-01 09:00:00+06'),
('Project Beta', 'Enhance user interface', 2, '2025-04-15 14:30:00+06'),
('Project Gamma', 'Database optimization', 3, '2025-05-10 11:15:00+06'),
('Project Delta', 'Security audit', 4, '2025-04-20 16:45:00+06'),
('Project Epsilon', 'Marketing campaign', 5, '2025-05-05 13:20:00+06');