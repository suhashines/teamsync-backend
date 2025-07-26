DELETE FROM Reactions;

-- Reset the sequence to start from 1
ALTER SEQUENCE reactions_id_seq RESTART WITH 1;

INSERT INTO Reactions (user_id, reaction_type, created_at, post_id, comment_id, message_id) VALUES
    (1, 'like', '2025-05-01 09:15:00+06', 1, NULL, NULL),
    (2, 'love', '2025-05-01 10:15:00+06', 2, NULL, NULL),
    (3, 'haha', '2025-05-01 11:15:00+06', 3, NULL, NULL),
    (4, 'wow', '2025-05-01 12:15:00+06', 4, NULL, NULL),
    (5, 'celebrate', '2025-05-01 13:15:00+06', 5, NULL, NULL),
    (6, 'support', '2025-05-02 09:15:00+06', 6, NULL, NULL),
    (7, 'insightful', '2025-05-02 10:15:00+06', 7, NULL, NULL),
    (8, 'like', '2025-05-02 11:15:00+06', 8, NULL, NULL),
    (9, 'love', '2025-05-02 12:15:00+06', 9, NULL, NULL),
    (10, 'haha', '2025-05-02 13:15:00+06', 10, NULL, NULL),
    (11, 'wow', '2025-05-03 09:15:00+06', NULL, 1, NULL),
    (12, 'celebrate', '2025-05-03 10:15:00+06', NULL, 2, NULL),
    (13, 'support', '2025-05-03 11:15:00+06', NULL, 3, NULL),
    (14, 'insightful', '2025-05-03 12:15:00+06', NULL, 4, NULL),
    (15, 'like', '2025-05-03 13:15:00+06', NULL, 5, NULL),
    (16, 'love', '2025-05-04 09:15:00+06', NULL, 1, NULL), -- Changed message_id=1 to comment_id=1
    (17, 'haha', '2025-05-04 10:15:00+06', NULL, 2, NULL), -- Changed message_id=2 to comment_id=2
    (18, 'wow', '2025-05-04 11:15:00+06', NULL, 3, NULL), -- Changed message_id=3 to comment_id=3
    (19, 'celebrate', '2025-05-04 12:15:00+06', NULL, 4, NULL), -- Changed message_id=4 to comment_id=4
    (20, 'support', '2025-05-04 13:15:00+06', NULL, 5, NULL), -- Changed message_id=5 to comment_id=5
    (21, 'insightful', '2025-05-05 09:15:00+06', 11, NULL, NULL),
    (1, 'like', '2025-05-05 10:15:00+06', 12, NULL, NULL),
    (2, 'love', '2025-05-05 11:15:00+06', 13, NULL, NULL),
    (3, 'haha', '2025-05-05 12:15:00+06', 14, NULL, NULL),
    (4, 'wow', '2025-05-05 13:15:00+06', 15, NULL, NULL),
    (5, 'celebrate', '2025-05-06 09:15:00+06', NULL, 6, NULL),
    (6, 'support', '2025-05-06 10:15:00+06', NULL, 7, NULL),
    (7, 'insightful', '2025-05-06 11:15:00+06', NULL, 8, NULL),
    (8, 'like', '2025-05-06 12:15:00+06', NULL, 9, NULL),
    (9, 'love', '2025-05-06 13:15:00+06', NULL, 10, NULL),
    (10, 'haha', '2025-05-07 09:15:00+06', NULL, 6, NULL), -- Changed message_id=6 to comment_id=6
    (11, 'wow', '2025-05-07 10:15:00+06', NULL, 7, NULL), -- Changed message_id=7 to comment_id=7
    (12, 'celebrate', '2025-05-07 11:15:00+06', NULL, 8, NULL), -- Changed message_id=8 to comment_id=8
    (13, 'support', '2025-05-07 12:15:00+06', NULL, 9, NULL), -- Changed message_id=9 to comment_id=9
    (14, 'insightful', '2025-05-07 13:15:00+06', NULL, 10, NULL), -- Changed message_id=10 to comment_id=10
    (15, 'like', '2025-05-08 09:15:00+06', 16, NULL, NULL),
    (16, 'love', '2025-05-08 10:15:00+06', 17, NULL, NULL),
    (17, 'haha', '2025-05-08 11:15:00+06', 18, NULL, NULL),
    (18, 'wow', '2025-05-08 12:15:00+06', 19, NULL, NULL),
    (19, 'celebrate', '2025-05-08 13:15:00+06', 20, NULL, NULL),
    (20, 'support', '2025-05-09 09:15:00+06', NULL, 11, NULL),
    (21, 'insightful', '2025-05-09 10:15:00+06', NULL, 12, NULL),
    (1, 'like', '2025-05-09 11:15:00+06', NULL, 13, NULL),
    (2, 'love', '2025-05-09 12:15:00+06', NULL, 14, NULL),
    (3, 'haha', '2025-05-09 13:15:00+06', NULL, 15, NULL),
    (4, 'wow', '2025-05-10 09:15:00+06', NULL, 11, NULL), -- Changed message_id=11 to comment_id=11
    (5, 'celebrate', '2025-05-10 10:15:00+06', NULL, 12, NULL), -- Changed message_id=12 to comment_id=12
    (6, 'support', '2025-05-10 11:15:00+06', NULL, 13, NULL), -- Changed message_id=13 to comment_id=13
    (7, 'insightful', '2025-05-10 12:15:00+06', NULL, 14, NULL), -- Changed message_id=14 to comment_id=14
    (8, 'like', '2025-05-10 13:15:00+06', NULL, 15, NULL), -- Changed message_id=15 to comment_id=15
    (9, 'love', '2025-05-11 09:15:00+06', 21, NULL, NULL),
    (10, 'haha', '2025-05-11 10:15:00+06', 22, NULL, NULL),
    (11, 'wow', '2025-05-11 11:15:00+06', 23, NULL, NULL),
    (12, 'celebrate', '2025-05-11 12:15:00+06', 24, NULL, NULL),
    (13, 'support', '2025-05-11 13:15:00+06', 25, NULL, NULL),
    (14, 'insightful', '2025-05-12 09:15:00+06', NULL, 16, NULL),
    (15, 'like', '2025-05-12 10:15:00+06', NULL, 17, NULL),
    (16, 'love', '2025-05-12 11:15:00+06', NULL, 18, NULL),
    (17, 'haha', '2025-05-12 12:15:00+06', NULL, 19, NULL),
    (18, 'wow', '2025-05-12 13:15:00+06', NULL, 20, NULL),
    (19, 'celebrate', '2025-05-13 09:15:00+06', NULL, 16, NULL), -- Changed message_id=16 to comment_id=16
    (20, 'support', '2025-05-13 10:15:00+06', NULL, 17, NULL), -- Changed message_id=17 to comment_id=17
    (21, 'insightful', '2025-05-13 11:15:00+06', NULL, 18, NULL), -- Changed message_id=18 to comment_id=18
    (1, 'like', '2025-05-13 12:15:00+06', NULL, 19, NULL), -- Changed message_id=19 to comment_id=19
    (2, 'love', '2025-05-13 13:15:00+06', NULL, 20, NULL), -- Changed message_id=20 to comment_id=20
    (3, 'haha', '2025-05-14 09:15:00+06', 26, NULL, NULL),
    (4, 'wow', '2025-05-14 10:15:00+06', 27, NULL, NULL),
    (5, 'celebrate', '2025-05-14 11:15:00+06', 28, NULL, NULL),
    (6, 'support', '2025-05-14 12:15:00+06', 29, NULL, NULL),
    (7, 'insightful', '2025-05-14 13:15:00+06', 30, NULL, NULL),
    (8, 'like', '2025-05-15 09:15:00+06', NULL, 21, NULL),
    (9, 'love', '2025-05-15 10:15:00+06', NULL, 22, NULL),
    (10, 'haha', '2025-05-15 11:15:00+06', NULL, 23, NULL),
    (11, 'wow', '2025-05-15 12:15:00+06', NULL, 24, NULL),
    (12, 'celebrate', '2025-05-15 13:15:00+06', NULL, 25, NULL),
    (13, 'support', '2025-05-16 09:15:00+06', NULL, 21, NULL), -- Changed message_id=21 to comment_id=21
    (14, 'insightful', '2025-05-16 10:15:00+06', NULL, 22, NULL), -- Changed message_id=22 to comment_id=22
    (15, 'like', '2025-05-16 11:15:00+06', NULL, 23, NULL), -- Changed message_id=23 to comment_id=23
    (16, 'love', '2025-05-16 12:15:00+06', NULL, 24, NULL), -- Changed message_id=24 to comment_id=24
    (17, 'haha', '2025-05-16 13:15:00+06', NULL, 25, NULL), -- Changed message_id=25 to comment_id=25
    (18, 'wow', '2025-05-17 09:15:00+06', 31, NULL, NULL),
    (19, 'celebrate', '2025-05-17 10:15:00+06', 32, NULL, NULL),
    (20, 'support', '2025-05-17 11:15:00+06', 33, NULL, NULL),
    (21, 'insightful', '2025-05-17 12:15:00+06', 34, NULL, NULL),
    (1, 'like', '2025-05-17 13:15:00+06', 35, NULL, NULL),
    (2, 'love', '2025-05-18 09:15:00+06', NULL, 26, NULL),
    (3, 'haha', '2025-05-18 10:15:00+06', NULL, 27, NULL),
    (4, 'wow', '2025-05-18 11:15:00+06', NULL, 28, NULL),
    (5, 'celebrate', '2025-05-18 12:15:00+06', NULL, 29, NULL),
    (6, 'support', '2025-05-18 13:15:00+06', NULL, 30, NULL),
    (7, 'insightful', '2025-05-19 09:15:00+06', NULL, 26, NULL), -- Changed message_id=26 to comment_id=26
    (8, 'like', '2025-05-19 10:15:00+06', NULL, 27, NULL), -- Changed message_id=27 to comment_id=27
    (9, 'love', '2025-05-19 11:15:00+06', NULL, 28, NULL), -- Changed message_id=28 to comment_id=28
    (10, 'haha', '2025-05-19 12:15:00+06', NULL, 29, NULL), -- Changed message_id=29 to comment_id=29
    (11, 'wow', '2025-05-19 13:15:00+06', NULL, 30, NULL), -- Changed message_id=30 to comment_id=30
    (12, 'celebrate', '2025-05-20 09:15:00+06', 36, NULL, NULL),
    (13, 'support', '2025-05-20 10:15:00+06', 37, NULL, NULL),
    (14, 'insightful', '2025-05-20 11:15:00+06', 38, NULL, NULL),
    (15, 'like', '2025-05-20 12:15:00+06', 39, NULL, NULL),
    (16, 'love', '2025-05-20 13:15:00+06', 40, NULL, NULL);