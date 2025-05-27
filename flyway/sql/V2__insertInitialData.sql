--- Insert dummy data into Users
INSERT INTO Users (name, email, password)
VALUES 
    ('sadat', 'sadat@gmail.com', '$2a$10$rXwg//1CPd12gxGPFpC/yu3QCwY44SWplkvhKUDYQFC6BSO8p0haK'),
    ('riyad', 'riyad@gmail.com', '$2a$10$Zsk/ghxxz142M5/ZuVgjget5.APB7tzNlZKKO.Lq3FmdWS9JooCL2'),
    ('hossain', 'hossain@gmail.com', '$2a$10$GVZLuymconMfPbameqH/kOJ6r69ISqqa8VoqsuoBQpRfKkknOy6lW');

INSERT INTO Projects (id, title, created_by, created_at) VALUES (1, 'Project Alpha', 1, '2025-05-26T17:24:00+06:00');
INSERT INTO ProjectMembers (project_id, user_id, role) VALUES (1, 1, 'owner');
INSERT INTO Tasks (id, title, status, project_id, assigned_to) VALUES (1, 'Design dashboard', 'in_progress', 1, 1);
INSERT INTO Tasks (id, title, status, project_id, assigned_to) VALUES (2, 'Design dashboard', 'in_progress', 1, 2);



