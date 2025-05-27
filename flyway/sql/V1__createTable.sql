-- Create custom ENUM types for constrained fields
CREATE TYPE task_status AS ENUM ('backlog', 'todo', 'in_progress', 'in_review', 'blocked', 'completed');
CREATE TYPE task_priority AS ENUM ('low', 'medium', 'high', 'urgent');
CREATE TYPE channel_type AS ENUM ('direct', 'group');
CREATE TYPE feedpost_type AS ENUM ('text', 'photo', 'event', 'appreciation', 'poll', 'birthday', 'highlight');
CREATE TYPE event_type AS ENUM ('Birthday', 'Workiversary', 'Outing');
CREATE TYPE mood_trend AS ENUM ('upward', 'downward', 'stable');
CREATE TYPE reaction_type AS ENUM ('like', 'love', 'haha', 'wow', 'sad', 'angry', 'celebrate', 'support', 'insightful');
CREATE TYPE project_role AS ENUM ('owner', 'admin', 'member', 'guest', 'viewer');


--- suggestions : UUID ---
-- Users table
CREATE TABLE Users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    profile_picture VARCHAR(255),
    designation VARCHAR(255),
    birthdate DATE,
    join_date DATE,
    mood_score FLOAT,
    predicted_burnout_risk BOOLEAN
);

-- Projects table
CREATE TABLE Projects (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    created_by INTEGER NOT NULL REFERENCES Users(id) ON DELETE RESTRICT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ProjectMembers table (junction table for Users-Projects many-to-many)
CREATE TABLE ProjectMembers (
    project_id INTEGER NOT NULL REFERENCES Projects(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE CASCADE,
    role project_role NOT NULL,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (project_id, user_id)
);

-- Tasks table
CREATE TABLE Tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status task_status NOT NULL,
    deadline TIMESTAMP WITH TIME ZONE,
    priority task_priority,
    time_estimate VARCHAR(50),
    ai_time_estimate VARCHAR(50),
    ai_priority task_priority,
    smart_deadline TIMESTAMP WITH TIME ZONE,
    project_id INTEGER NOT NULL REFERENCES Projects(id) ON DELETE CASCADE,
    assigned_to INTEGER REFERENCES Users(id) ON DELETE SET NULL,
    assigned_by INTEGER REFERENCES Users(id) ON DELETE SET NULL,
    assigned_at TIMESTAMP WITH TIME ZONE,
    parent_task_id INTEGER REFERENCES Tasks(id) ON DELETE SET NULL,
    attachments TEXT[]
);

-- TaskStatusHistory table
CREATE TABLE TaskStatusHistory (
    id SERIAL PRIMARY KEY,
    task_id INTEGER NOT NULL REFERENCES Tasks(id) ON DELETE CASCADE,
    status task_status NOT NULL,
    changed_by INTEGER NOT NULL REFERENCES Users(id) ON DELETE RESTRICT,
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    comment TEXT
);

-- Channels table
CREATE TABLE Channels (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type channel_type NOT NULL,
    project_id INTEGER REFERENCES Projects(id) ON DELETE SET NULL,
    members INTEGER[] -- Could be normalized into a ChannelMembers junction table
);

-- Messages table
CREATE TABLE Messages (
    id SERIAL PRIMARY KEY,
    sender_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE RESTRICT,
    channel_id INTEGER REFERENCES Channels(id) ON DELETE SET NULL,
    recipient_id INTEGER REFERENCES Users(id) ON DELETE SET NULL,
    content TEXT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    thread_parent_id INTEGER REFERENCES Messages(id) ON DELETE SET NULL,
    CHECK (channel_id IS NOT NULL OR recipient_id IS NOT NULL) -- Ensure at least one is set
);

-- FeedPosts table
CREATE TABLE FeedPosts (
    id SERIAL PRIMARY KEY,
    type feedpost_type NOT NULL,
    author_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE RESTRICT,
    content TEXT NOT NULL,
    media_urls TEXT[],
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    event_date DATE,
    poll_options TEXT[],
    is_ai_generated BOOLEAN NOT NULL DEFAULT FALSE,
    ai_summary TEXT
);

-- Events table
CREATE TABLE Events (
    id SERIAL PRIMARY KEY,
    parent_post_id INTEGER REFERENCES FeedPosts(id) ON DELETE SET NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    type event_type NOT NULL,
    date DATE NOT NULL,
    participants INTEGER[] -- Could be normalized into an EventParticipants junction table
);

-- Appreciations table
CREATE TABLE Appreciations (
    id SERIAL PRIMARY KEY,
    parent_post_id INTEGER REFERENCES FeedPosts(id) ON DELETE SET NULL,
    from_user_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE RESTRICT,
    to_user_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE RESTRICT,
    message TEXT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Comments table
CREATE TABLE Comments (
    id SERIAL PRIMARY KEY,
    post_id INTEGER NOT NULL REFERENCES FeedPosts(id) ON DELETE CASCADE,
    author_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE RESTRICT,
    content TEXT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    parent_comment_id INTEGER REFERENCES Comments(id) ON DELETE SET NULL,
    reply_count INTEGER NOT NULL DEFAULT 0
);

-- Reactions table
CREATE TABLE Reactions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE RESTRICT,
    reaction_type reaction_type NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    post_id INTEGER REFERENCES FeedPosts(id) ON DELETE CASCADE,
    comment_id INTEGER REFERENCES Comments(id) ON DELETE CASCADE,
    message_id INTEGER REFERENCES Messages(id) ON DELETE CASCADE,
    CHECK ((post_id IS NOT NULL AND comment_id IS NULL) OR (post_id IS NULL AND comment_id IS NOT NULL)) -- Ensure exactly one is set
);

-- PollVotes table
CREATE TABLE PollVotes (
    id SERIAL PRIMARY KEY,
    poll_id INTEGER NOT NULL REFERENCES FeedPosts(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES Users(id) ON DELETE RESTRICT,
    selected_option TEXT NOT NULL
);

