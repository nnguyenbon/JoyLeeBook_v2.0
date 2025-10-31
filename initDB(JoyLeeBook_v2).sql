﻿-- ============================================
-- Create Database: JoyLeeBook_v2 (TRIGGERS VERSION)
-- ============================================
USE [master]
GO

IF EXISTS (SELECT name FROM master.dbo.sysdatabases WHERE name = 'JoyLeeBook_v2')
BEGIN
	ALTER DATABASE JoyLeeBook_v2 SET OFFLINE WITH ROLLBACK IMMEDIATE;
	ALTER DATABASE JoyLeeBook_v2 SET ONLINE;
	DROP DATABASE JoyLeeBook_v2;
END

GO

CREATE DATABASE JoyLeeBook_v2;
GO

USE JoyLeeBook_v2;
GO

-- ============================================
-- 1. USERS TABLE
-- ============================================
CREATE TABLE users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    full_name NVARCHAR(255),
    bio NVARCHAR(300),
    email NVARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    google_id NVARCHAR(255),
    role NVARCHAR(10) CHECK (role IN ('reader', 'author')) NOT NULL DEFAULT 'reader',
    is_verified BIT DEFAULT 0 NOT NULL,
    is_deleted BIT DEFAULT 0 NOT NULL,
    status NVARCHAR(20) CHECK (status IN ('active', 'inactive', 'banned')) DEFAULT 'active' NOT NULL,
    points INT DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NOT NULL
);
GO

CREATE INDEX IX_users_username ON users(username);
CREATE INDEX IX_users_email ON users(email);
CREATE INDEX IX_users_created_at ON users(created_at DESC);
CREATE INDEX IX_users_points ON users(points DESC);
GO

-- ============================================
-- 2. STAFFS TABLE
-- ============================================
CREATE TABLE staffs (
    staff_id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name NVARCHAR(100) NOT NULL,
    role VARCHAR(10) CHECK (role IN ('staff', 'admin')) NOT NULL,
    is_deleted BIT DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NOT NULL
);
GO

CREATE INDEX IX_staffs_username ON staffs(username);
GO

-- ============================================
-- 3. SERIES TABLE
-- ============================================
CREATE TABLE series (
    series_id INT IDENTITY(1,1) PRIMARY KEY,
    title NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    cover_image_url NVARCHAR(500),
    status NVARCHAR(20) CHECK (status IN ('completed', 'ongoing')) DEFAULT 'ongoing' NOT NULL,
	approval_status NVARCHAR(20) CHECK (approval_status IN ('pending', 'approved', 'rejected')) DEFAULT 'pending' NOT NULL,
    is_deleted BIT DEFAULT 0 NOT NULL,
    rating_points INT DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NOT NULL
);
GO

CREATE INDEX IX_series_title ON series(title);
CREATE INDEX IX_series_status ON series(status) WHERE is_deleted = 0;
CREATE INDEX IX_series_created_at ON series(created_at DESC);
CREATE INDEX IX_series_rating_points ON series(rating_points DESC);
GO

-- ============================================
-- 4. CHAPTERS TABLE
-- ============================================
CREATE TABLE chapters (
    chapter_id INT IDENTITY(1,1) PRIMARY KEY,
    series_id INT NOT NULL,
	user_id INT NOT NULL,
    chapter_number INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    content NVARCHAR(MAX),
    status NVARCHAR(20) CHECK (status IN ('draft', 'published')) DEFAULT 'draft' NOT NULL,
	approval_status NVARCHAR(20) CHECK (approval_status IN ('pending', 'approved', 'rejected')) DEFAULT 'pending',
    is_deleted BIT DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NOT NULL,
    CONSTRAINT FK_chapters_series FOREIGN KEY (series_id) REFERENCES series(series_id) ON DELETE CASCADE,
	CONSTRAINT FK_chapters_users FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT UQ_chapter_series_number UNIQUE (series_id, chapter_number)
);
GO

CREATE INDEX IX_chapters_series_id ON chapters(series_id);
CREATE INDEX IX_chapters_created_at ON chapters(created_at DESC);
CREATE INDEX IX_chapters_status ON chapters(status);
GO

-- ============================================
-- 5. CATEGORIES TABLE
-- ============================================
CREATE TABLE categories (
    category_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) UNIQUE NOT NULL,
    description NVARCHAR(255),
    is_deleted BIT DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL
);
GO

-- ============================================
-- 6. SERIES_CATEGORIES (many-to-many)
-- ============================================
CREATE TABLE series_categories (
    series_id INT NOT NULL,
    category_id INT NOT NULL,
    PRIMARY KEY (series_id, category_id),
    CONSTRAINT FK_series_categories_series FOREIGN KEY (series_id) REFERENCES series(series_id) ON DELETE CASCADE,
    CONSTRAINT FK_series_categories_category FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_series_categories_category ON series_categories(category_id);
GO

-- ============================================
-- 7. SERIES_AUTHOR (many-to-many)
-- ============================================
CREATE TABLE series_author (
    series_id INT NOT NULL,
    user_id INT NOT NULL,
	is_owner BIT DEFAULT 0 NOT NULL,
    PRIMARY KEY (series_id, user_id),
    CONSTRAINT FK_series_author_series FOREIGN KEY (series_id) REFERENCES series(series_id) ON DELETE CASCADE,
    CONSTRAINT FK_series_author_user FOREIGN KEY (user_id) REFERENCES users(user_id)
);
GO

CREATE INDEX IX_series_author_user ON series_author(user_id);
GO

-- ============================================
-- 8. READING_HISTORY
-- ============================================
CREATE TABLE reading_history (
    user_id INT NOT NULL,
    chapter_id INT NOT NULL,
    last_read_at DATETIME DEFAULT GETDATE() NOT NULL,
    PRIMARY KEY (user_id, chapter_id),
    CONSTRAINT FK_reading_history_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_reading_history_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(chapter_id) ON DELETE NO ACTION
);
GO

CREATE INDEX IX_reading_history_user_lastread ON reading_history(user_id, last_read_at DESC);
CREATE INDEX IX_reading_history_chapter ON reading_history(chapter_id);
GO

-- ============================================
-- 9. SAVED_SERIES
-- ============================================
CREATE TABLE saved_series (
    user_id INT NOT NULL,
    series_id INT NOT NULL,
    saved_at DATETIME DEFAULT GETDATE() NOT NULL,
    PRIMARY KEY (user_id, series_id),
    CONSTRAINT FK_saved_series_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_saved_series_series FOREIGN KEY (series_id) REFERENCES series(series_id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_saved_series_user_savedat ON saved_series(user_id, saved_at DESC);
GO

-- ============================================
-- 10. RATINGS
-- ============================================
CREATE TABLE ratings (
    user_id INT NOT NULL,
    series_id INT NOT NULL,
    score INT CHECK (score BETWEEN 1 AND 5) NOT NULL,
    rated_at DATETIME DEFAULT GETDATE() NOT NULL,
    PRIMARY KEY (user_id, series_id),
    CONSTRAINT FK_ratings_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_ratings_series FOREIGN KEY (series_id) REFERENCES series(series_id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_ratings_series ON ratings(series_id);
GO

-- ============================================
-- 11. COMMENTS
-- ============================================
CREATE TABLE comments (
    comment_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    chapter_id INT NOT NULL,
    content NVARCHAR(1000) NOT NULL,
    is_deleted BIT DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NOT NULL,
    CONSTRAINT FK_comments_user FOREIGN KEY (user_id) REFERENCES users(user_id), 
    CONSTRAINT FK_comments_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(chapter_id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_comments_chapter_created ON comments(chapter_id, created_at DESC) WHERE is_deleted = 0;
CREATE INDEX IX_comments_user ON comments(user_id);
GO

-- ============================================
-- 12. LIKES
-- ============================================
CREATE TABLE likes (
    user_id INT NOT NULL,
    chapter_id INT NOT NULL,
    liked_at DATETIME DEFAULT GETDATE() NOT NULL,
    PRIMARY KEY (user_id, chapter_id),
    CONSTRAINT FK_likes_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT FK_likes_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(chapter_id) ON DELETE NO ACTION
);
GO

CREATE INDEX IX_likes_chapter ON likes(chapter_id);
GO

-- ============================================
-- 13. REPORTS
-- ============================================
CREATE TABLE reports (
    report_id INT IDENTITY(1,1) PRIMARY KEY,
    reporter_id INT NOT NULL,
    staff_id INT NULL,

    target_type NVARCHAR(20) CHECK (target_type IN ('comment', 'chapter')) NOT NULL,
    comment_id INT NULL,
    chapter_id INT NULL,

    reason NVARCHAR(500) NOT NULL,
    status NVARCHAR(20) CHECK (status IN ('pending', 'resolved', 'rejected')) DEFAULT 'pending' NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NOT NULL,
    CONSTRAINT FK_reports_reporter FOREIGN KEY (reporter_id) REFERENCES users(user_id),
    CONSTRAINT FK_reports_staff FOREIGN KEY (staff_id) REFERENCES staffs(staff_id),   
    CONSTRAINT FK_reports_comment FOREIGN KEY (comment_id) REFERENCES comments(comment_id),
    CONSTRAINT FK_reports_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(chapter_id)

);
GO

-- Xóa các index cũ và tạo lại cho phù hợp với cấu trúc mới
CREATE INDEX IX_reports_status ON reports(status, created_at DESC);
CREATE INDEX IX_reports_staff ON reports(staff_id);
-- Index trên các cột target để tìm kiếm nhanh
CREATE INDEX IX_reports_comment_id ON reports(comment_id) WHERE comment_id IS NOT NULL;
CREATE INDEX IX_reports_chapter_id ON reports(chapter_id) WHERE chapter_id IS NOT NULL;
GO

-- ============================================
-- 14. NOTIFICATIONS
-- ============================================
CREATE TABLE notifications (
    notification_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    type NVARCHAR(20) CHECK (type IN ('system', 'submission_status', 'moderation')) NOT NULL,
    title NVARCHAR(255) NOT NULL,
    message NVARCHAR(500),
    is_read BIT DEFAULT 0 NOT NULL,
    url_redirect NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    CONSTRAINT FK_notifications_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_notifications_user_read ON notifications(user_id, is_read, created_at DESC);
GO

-- ============================================
-- 15. BADGES & BADGES_USERS
-- ============================================
CREATE TABLE badges (
    badge_id INT IDENTITY(1,1) PRIMARY KEY,
    icon_url NVARCHAR(500),
    name NVARCHAR(100) NOT NULL UNIQUE,
    description NVARCHAR(255),
    requirement_type NVARCHAR(50),
    requirement_value INT,
    created_at DATETIME DEFAULT GETDATE() NOT NULL
);
GO
-- ============================================
-- 16. BADGES_USERS
-- ============================================
CREATE TABLE badges_users (
    badge_id INT NOT NULL,
    user_id INT NOT NULL,
    awarded_at DATETIME DEFAULT GETDATE() NOT NULL,
    PRIMARY KEY (badge_id, user_id),
    CONSTRAINT FK_badges_users_badge FOREIGN KEY (badge_id) REFERENCES badges(badge_id),
    CONSTRAINT FK_badges_users_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_badges_users_user ON badges_users(user_id);
GO
-- ============================================
-- 17. REVIEW_SERIES
-- ============================================
CREATE TABLE review_series (
	series_id INT NOT NULL,
	staff_id INT NOT NULL,
	status VARCHAR(20) CHECK (status IN ('pending', 'approved', 'rejected')) NOT NULL,
	comment NVARCHAR(500),
	created_at DATETIME DEFAULT GETDATE() NOT NULL,
	PRIMARY KEY (series_id, staff_id),
	CONSTRAINT FK_review_series_series FOREIGN KEY (series_id) REFERENCES series(series_id) ON DELETE CASCADE,
	CONSTRAINT FK_review_series_staff FOREIGN KEY (staff_id) REFERENCES staffs(staff_id) ON DELETE CASCADE

)
GO
-- ============================================
-- 18. REVIEW_CHAPTER
-- ============================================
CREATE TABLE review_chapter (
    chapter_id INT NOT NULL,
    staff_id INT NOT NULL,
    status VARCHAR(20) CHECK (status IN ('pending', 'approved', 'rejected')) NOT NULL,
    comment NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    PRIMARY KEY (chapter_id, staff_id),
    CONSTRAINT FK_review_chapter_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(chapter_id) ON DELETE CASCADE,
    CONSTRAINT FK_review_chapter_staff FOREIGN KEY (staff_id) REFERENCES staffs(staff_id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_review_chapter_staff ON review_chapter(staff_id, created_at DESC);
CREATE INDEX IX_review_chapter_status ON review_chapter(status);
GO

-- ============================================
-- 19. POINT_HISTORY
-- ============================================
CREATE TABLE point_history (
    history_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    points_change INT NOT NULL,
    reason NVARCHAR(255) NOT NULL,
    reference_type NVARCHAR(50),
    reference_id INT,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    CONSTRAINT FK_point_history_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);
GO

CREATE INDEX IX_point_history_user_created ON point_history(user_id, created_at DESC);
GO


CREATE TRIGGER trg_update_series_approval_status
ON review_series
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE s
    SET s.approval_status = i.status,
        s.updated_at = GETDATE()
    FROM series s
    INNER JOIN inserted i ON s.series_id = i.series_id;
END;
GO


CREATE TRIGGER trg_update_chapter_approval_status
ON review_chapter
AFTER INSERT, UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE c
    SET c.approval_status = i.status,
        c.updated_at = GETDATE()
    FROM chapters c
    INNER JOIN inserted i ON c.chapter_id = i.chapter_id;
END;
GO




USE JoyLeeBook_v2;
GO
-- Insert 20 users (unchanged structure, timestamps to recent dates)
INSERT INTO users (username, full_name, bio, email, password_hash, google_id, role, is_verified, is_deleted, status, points, created_at, updated_at) VALUES
('reader1', N'John Doe', N'Book lover', 'john@example.com', 'hash1', NULL, 'reader', 1, 0, 'active', 100, '2025-10-25', '2025-10-25'),
('author1', N'Jane Smith', N'Fantasy writer', 'jane@example.com', 'hash2', NULL, 'author', 1, 0, 'active', 500, '2025-10-26', '2025-10-26'),
('reader2', N'Mike Johnson', N'Casual reader', 'mike@example.com', 'hash3', NULL, 'reader', 0, 0, 'active', 50, '2025-10-27', '2025-10-27'),
('author2', N'Emily Davis', N'Mystery author', 'emily@example.com', 'hash4', NULL, 'author', 1, 0, 'active', 300, '2025-10-28', '2025-10-28'),
('reader3', N'Sarah Wilson', N'Avid reader', 'sarah@example.com', 'hash5', NULL, 'reader', 1, 0, 'active', 200, '2025-10-29', '2025-10-29'),
('author3', N'David Brown', N'Sci-fi enthusiast', 'david@example.com', 'hash6', 'google123', 'author', 0, 0, 'active', 400, '2025-10-30', '2025-10-30'),
('reader4', N'Lisa Garcia', N'Romance fan', 'lisa@example.com', 'hash7', NULL, 'reader', 1, 0, 'active', 75, '2025-10-31', '2025-10-31'),
('author4', N'Robert Martinez', N'Horror writer', 'robert@example.com', 'hash8', NULL, 'author', 1, 0, 'inactive', 250, '2025-10-24', '2025-10-24'),
('reader5', N'Amanda Rodriguez', N'New reader', 'amanda@example.com', 'hash9', NULL, 'reader', 0, 0, 'active', 10, '2025-10-25', '2025-10-25'),
('author5', N'James Lee', N'Adventure author', 'james@example.com', 'hash10', NULL, 'author', 1, 0, 'active', 600, '2025-10-26', '2025-10-26'),
('reader6', N'Chris Evans', N'Mystery fan', 'chris@example.com', 'hash11', NULL, 'reader', 1, 0, 'active', 150, '2025-10-27', '2025-10-27'),
('author6', N'Anna Taylor', N'Romance novelist', 'anna@example.com', 'hash12', NULL, 'author', 0, 0, 'active', 450, '2025-10-28', '2025-10-28'),
('reader7', N'Kevin Patel', N'Sci-fi reader', 'kevin@example.com', 'hash13', 'google456', 'reader', 1, 0, 'inactive', 80, '2025-10-29', '2025-10-29'),
('author7', N'Sophia Kim', N'Thriller writer', 'sophia@example.com', 'hash14', NULL, 'author', 1, 0, 'active', 350, '2025-10-30', '2025-10-30'),
('reader8', N'Olivia Chen', N'Adventure lover', 'olivia@example.com', 'hash15', NULL, 'reader', 0, 0, 'active', 120, '2025-10-31', '2025-10-31'),
('author8', N'Michael Wong', N'Historical author', 'michael@example.com', 'hash16', NULL, 'author', 1, 0, 'banned', 200, '2025-10-24', '2025-10-24'),
('reader9', N'Emma Lopez', N'Comedy enthusiast', 'emma@example.com', 'hash17', NULL, 'reader', 1, 0, 'active', 90, '2025-10-25', '2025-10-25'),
('author9', N'Noah Singh', N'Drama specialist', 'noah@example.com', 'hash18', NULL, 'author', 0, 0, 'active', 550, '2025-10-26', '2025-10-26'),
('reader10', N'Sophie Nguyen', N'Horror reader', 'sophie@example.com', 'hash19', NULL, 'reader', 1, 0, 'active', 60, '2025-10-27', '2025-10-27'),
('author10', N'Lucas Garcia', N'Fantasy creator', 'lucas@example.com', 'hash20', 'google789', 'author', 1, 0, 'active', 700, '2025-10-28', '2025-10-28');
GO

-- Insert 10 staffs (timestamps updated)
INSERT INTO staffs (username, password_hash, full_name, role, is_deleted, created_at, updated_at) VALUES
('staff1', 'staffhash1', N'Alice Moderator', 'staff', 0, '2025-10-25', '2025-10-25'),
('admin1', 'adminhash1', N'Bob Admin', 'admin', 0, '2025-10-26', '2025-10-26'),
('staff2', 'staffhash2', N'Carol Reviewer', 'staff', 0, '2025-10-27', '2025-10-27'),
('admin2', 'adminhash2', N'Dave SuperAdmin', 'admin', 0, '2025-10-28', '2025-10-28'),
('staff3', 'staffhash3', N'Eve Checker', 'staff', 0, '2025-10-29', '2025-10-29'),
('admin3', 'adminhash3', N'Frank Manager', 'admin', 0, '2025-10-30', '2025-10-30'),
('staff4', 'staffhash4', N'Grace Editor', 'staff', 0, '2025-10-31', '2025-10-31'),
('admin4', 'adminhash4', N'Hank Supervisor', 'admin', 0, '2025-10-24', '2025-10-24'),
('staff5', 'staffhash5', N'Ivy Approver', 'staff', 0, '2025-10-25', '2025-10-25'),
('admin5', 'adminhash5', N'Jack Owner', 'admin', 0, '2025-10-26', '2025-10-26');
GO

-- Insert 30 categories (timestamps updated)
INSERT INTO categories (name, description, is_deleted, created_at) VALUES
(N'Fantasy', N'Epic tales and magic', 0, '2025-10-25'),
(N'Mystery', N'Puzzles and detectives', 0, '2025-10-26'),
(N'Sci-Fi', N'Future and space', 0, '2025-10-27'),
(N'Romance', N'Love stories', 0, '2025-10-28'),
(N'Horror', N'Scares and thrills', 0, '2025-10-29'),
(N'Adventure', N'Journeys and quests', 0, '2025-10-30'),
(N'Historical', N'Past events', 0, '2025-10-31'),
(N'Thriller', N'Suspense and action', 0, '2025-10-24'),
(N'Comedy', N'Humor and laughs', 0, '2025-10-25'),
(N'Drama', N'Emotions and conflicts', 0, '2025-10-26'),
(N'Urban Fantasy', N'Modern magic worlds', 0, '2025-10-27'),
(N'Cozy Mystery', N'Light-hearted puzzles', 0, '2025-10-28'),
(N'Cyberpunk', N'High-tech dystopia', 0, '2025-10-29'),
(N'Paranormal Romance', N'Supernatural love', 0, '2025-10-30'),
(N'Gothic Horror', N'Dark atmospheric tales', 0, '2025-10-31'),
(N'Survival Adventure', N'Against the odds', 0, '2025-10-24'),
(N'Biographical Historical', N'Real lives retold', 0, '2025-10-25'),
(N'Psychological Thriller', N'Mind games', 0, '2025-10-26'),
(N'Satirical Comedy', N'Social commentary', 0, '2025-10-27'),
(N'Tragedy Drama', N'Heartbreaking stories', 0, '2025-10-28'),
(N'Young Adult', N'Coming of age stories for teens', 0, '2025-10-29'),
(N'Children''s', N'Stories for young kids', 0, '2025-10-30'),
(N'Non-Fiction', N'Real world facts and information', 0, '2025-10-31'),
(N'Biography', N'Life stories of notable people', 0, '2025-10-24'),
(N'Self-Help', N'Guides for personal improvement', 0, '2025-10-25'),
(N'Poetry', N'Expressive verse and rhymes', 0, '2025-10-26'),
(N'Literary Fiction', N'Artistic and character-driven prose', 0, '2025-10-27'),
(N'Crime', N'Heists and criminal investigations', 0, '2025-10-28'),
(N'Western', N'Frontier and cowboy tales', 0, '2025-10-29'),
(N'Steampunk', N'Victorian-era science fiction', 0, '2025-10-30');
GO

-- Insert 8 badges (timestamps updated)
INSERT INTO badges (icon_url, name, description, requirement_type, requirement_value, created_at) VALUES
('post100Comments.png', N'Post 100 comments', N'Post 100 comments', 'comments', 100, '2025-10-25'),
('registerAsAuthor.png', N'Register As Author', N'Register As Author', 'Author', 1, '2025-10-26'),
('FinishfirstSeries.png', N'Finish first Series', N'Finish first Series', 'Series', 1, '2025-10-27'),
('Got100Likes.png', N'Got 100 Likes', N'Got 100 Likes', 'Like', 100, '2025-10-28'),
('Got1000points.png', N'Got 1000 points', N'Got 1000 points', 'Points', 1000, '2025-10-29'),
('Got1500Points.png', N'Got 1500 Points', N'Got 1500 Points', 'Points', 1500, '2025-10-30'),
('Got3000Points.png', N'Got 3000 Points', N'Got 3000 Points', 'Points', 3000, '2025-10-31'),
('Got9999Points.png', N'Got 9999 Points', N'Got 9999 Points', 'Points', 9999, '2025-10-24');
GO

-- Insert 20 series (timestamps updated, added approval_status variations)
INSERT INTO series (title, description, cover_image_url, status, approval_status, is_deleted, rating_points, created_at, updated_at) VALUES
(N'The New Kid in School', N'A story about a hopeless romantic, Kaito, as he starts a new life in a coastal town and faces the challenges of friendship and love.', 'thenewkidinschool.png', 'ongoing', 'approved', 0, 45, '2025-10-25', '2025-10-25'),
(N'Cậu Bé Thông Minh', N'Một câu chuyện dân gian Việt Nam về trí thông minh và lòng dũng cảm.', 'cau_be_thong_minh.avif', 'completed', 'approved', 0, 48, '2025-10-26', '2025-10-26'),
(N'Dungeon Diver: Stealing a Monster', N'An adventurer who dives into dungeons to steal rare monsters and treasures.', 'dungeon_diver_stealing_a_monster.avif', 'ongoing', 'pending', 0, 50, '2025-10-27', '2025-10-27'),
(N'Embers Ad Infinitum', N'In a post-apocalyptic world, humanity clings to the remnants of civilization.', 'Embers_Ad_Infinitum.avif', 'ongoing', 'approved', 0, 42, '2025-10-28', '2025-10-28'),
(N'Evolving Infinitely from Ground Zero', N'A man starts anew after the end of the world.', 'Evolving_infinitely_from_ground_zero.avif', 'completed', 'rejected', 0, 39, '2025-10-29', '2025-10-29'),
(N'Goddess Medical Doctor', N'A skilled doctor reincarnates in a fantasy world to save lives and fight corruption.', 'goddess_medical_doctor.avif', 'ongoing', 'approved', 0, 46, '2025-10-30', '2025-10-30'),
(N'Infinite Mana in The Apocalypse', N'Magic, evolution, and the end of the world collide.', 'Infinite_Mana_In_The_Apocalypse.avif', 'completed', 'approved', 0, 47, '2025-10-31', '2025-10-31'),
(N'Monarch of Time', N'A man manipulates time to change destiny.', 'monarch_of_time.avif', 'ongoing', 'pending', 0, 49, '2025-10-24', '2025-10-24'),
(N'Shadow Slave', N'Chains, darkness, and survival in a cruel world.', 'shadow_slave.avif', 'completed', 'approved', 0, 41, '2025-10-25', '2025-10-25'),
(N'Sơn Tinh Thủy Tinh', N'Truyền thuyết Việt Nam về cuộc chiến giữa núi và nước.', 'Son_Tinh_Thuy_Tinh.avif', 'ongoing', 'approved', 0, 44, '2025-10-26', '2025-10-26'),
(N'Supreme Magus', N'A scholar strives to master the arcane arts.', 'supreme-magus-webnovel.avif', 'ongoing', 'rejected', 0, 46, '2025-10-27', '2025-10-27'),
(N'Tensei Shitara Slime Datta Ken', N'An ordinary man reincarnates as a slime with incredible powers.', 'tensei_shitara_slime_datta_ken.avif', 'completed', 'approved', 0, 43, '2025-10-28', '2025-10-28'),
(N'Thanh Giong', N'Truyền thuyết về vị anh hùng làng Gióng cứu nước.', 'thanh_giong.avif', 'ongoing', 'pending', 0, 47, '2025-10-29', '2025-10-29'),
(N'The Legendary Mechanic', N'Sci-fi action story about technology and power.', 'the-legendary-mechanic-novel.avif', 'completed', 'approved', 0, 49, '2025-10-30', '2025-10-30'),
(N'The World of Otome Games is Tough', N'A man reincarnates in a dating sim game world.', 'the-world-of-otome-games-is-toug.avif', 'ongoing', 'approved', 0, 40, '2025-10-31', '2025-10-31'),
(N'The Epi of Leviathan', N'A fantasy about gods and mortals clashing.', 'the_epi_of_leviathan.avif', 'ongoing', 'rejected', 0, 45, '2025-10-24', '2025-10-24'),
(N'The Legend of Mai An Tiêm', N'Truyền thuyết Việt Nam về quả dưa hấu và lòng trung hiếu.', 'The_Legend_of_Mai_An_Tiem.avif', 'completed', 'approved', 0, 48, '2025-10-25', '2025-10-25'),
(N'The Mech Touch', N'Mecha engineering, war, and ambition.', 'The_Mech_Touch.avif', 'ongoing', 'pending', 0, 44, '2025-10-26', '2025-10-26'),
(N'Sorce Stone', N'A mystical journey through ancient lands.', 'sorce_stone.avif', 'completed', 'approved', 0, 42, '2025-10-27', '2025-10-27'),
(N'So Dua', N'Truyện dân gian Việt Nam về người nông dân thông minh.', 'so_dua.avif', 'ongoing', 'approved', 0, 41, '2025-10-28', '2025-10-28');
GO

-- Insert 20 series_categories (unchanged, but ensure category_id <=30)
INSERT INTO series_categories (series_id, category_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 11), (12, 12), (13, 13), (14, 14), (15, 15), (16, 16), (17, 17), (18, 18), (19, 19), (20, 20);
GO

-- Insert 20 series_author (fixed: no added_at, added is_owner variations, timestamps removed as no column)
INSERT INTO series_author (series_id, user_id, is_owner) VALUES
(1, 2, 1), (2, 4, 1), (3, 6, 0), (4, 8, 1), (5, 10, 0), (6, 1, 1), (7, 3, 0), (8, 5, 1), (9, 7, 0), (10, 9, 1),
(11, 2, 0), (12, 4, 1), (13, 6, 0), (14, 8, 1), (15, 10, 0), (16, 1, 1), (17, 3, 0), (18, 5, 1), (19, 7, 0), (20, 9, 1);
GO

-- Insert 20 chapters (timestamps updated, approval_status aligned, chapter_number unique per series)
INSERT INTO chapters (series_id, user_id, chapter_number, title, content, status, approval_status, is_deleted, created_at, updated_at) VALUES
(1, 1, 1, N'The Night Before the First Day', N'The night sky stretched out like a velvet blanket, dotted with fluffy clouds and shimmering with the soft glow of the moon. Below, rooftops huddled close together, the lights from their small windows twinkling like stars on the ground, stretching down the slope towards the deep blue sea. The night sea was calm, with only the distant whisper of waves like a lullaby.
On a small balcony of one of those houses, Kaito sat quietly, his gaze lost on the horizon. He was a "hopeless romantic," just like the words on the book cover he often read. He loved to watch the scenery, to lose himself in distant thoughts—about beautiful things, about love, and about new beginnings.
Tomorrow was Kaito''s first day at a new school. His family had just moved to this small coastal town, where everything was unfamiliar and full of promise. Kaito was the new student, the "new kid in school," and a mix of anxiety and excitement crept into his heart. He wondered what kind of friends he would meet here. Would there be someone who could understand his romantic soul?
He sighed softly, his chin resting on his hand, his fluffy brown hair stirring in the cool sea breeze. Kaito looked down at the lit streets, imagining the stories that would unfold. He knew that, despite the initial awkwardness, this place would become an important part of his life. And perhaps, he would find what his romantic heart had always been searching for.
His eyes landed on a lit house in the distance, where a faint silhouette moved past the window. Kaito smiled, a smile full of hope. "Welcome to the new chapter, Kaito," he told himself.', 'published', 'approved', 0, '2025-10-25', '2025-10-25'),
(1, 1, 2, N'The Awakening', N'The hero wakes up...', 'published', 'approved', 0, '2025-10-26', '2025-10-26'),
(2, 2, 1, N'The Crime Scene', N'A body is found...', 'published', 'approved', 0, '2025-10-27', '2025-10-27'),
(2, 2, 2, N'Suspects Emerge', N'Questioning begins...', 'draft', 'pending', 0, '2025-10-28', '2025-10-28'),
(3, 3, 1, N'Launch Day', N'Spaceship departs...', 'published', 'pending', 0, '2025-10-29', '2025-10-29'),
(3, 3, 2, N'Alien Encounter', N'First contact...', 'published', 'approved', 0, '2025-10-30', '2025-10-30'),
(4, 4, 1, N'Meeting Cute', N'Boy meets girl...', 'published', 'approved', 0, '2025-10-31', '2025-10-31'),
(5, 5, 1, N'The Haunting Begins', N'Strange noises...', 'draft', 'rejected', 0, '2025-10-24', '2025-10-24'),
(6, 1, 1, N'The Map', N'Old treasure map...', 'draft', 'approved', 0, '2025-10-25', '2025-10-25'),
(7, 2, 1, N'The Coronation', N'Royal ceremony...', 'published', 'approved', 0, '2025-10-26', '2025-10-26'),
(1, 3, 3, N'Shadow Awakening', N'In the city shadows...', 'published', 'approved', 0, '2025-10-27', '2025-10-27'),
(2, 4, 3, N'First Clue', N'A quiet village murder...', 'draft', 'pending', 0, '2025-10-28', '2025-10-28'),
(3, 5, 4, N'Neon Startup', N'In a futuristic city...', 'published', 'approved', 0, '2025-10-29', '2025-10-29'),
(4, 1, 2, N'Moonlit Meeting', N'Under the full moon...', 'published', 'approved', 0, '2025-10-30', '2025-10-30'),
(5, 2, 2, N'Veiled Secrets', N'An old mansion...', 'draft', 'rejected', 0, '2025-10-31', '2025-10-31'),
(6, 3, 2, N'Wild Call', N'In the jungle depths...', 'draft', 'pending', 0, '2025-10-24', '2025-10-24'),
(7, 4, 2, N'Echoes Begin', N'A historical figure awakens...', 'published', 'approved', 0, '2025-10-25', '2025-10-25'),
(8, 5, 1, N'Mind Games Start', N'Psychological descent...', 'draft', 'pending', 0, '2025-10-26', '2025-10-26'),
(9, 1, 1, N'Witty Introduction', N'A satirical society...', 'published', 'approved', 0, '2025-10-27', '2025-10-27'),
(10, 2, 1, N'Torn Beginnings', N'Family fractures...', 'published', 'approved', 0, '2025-10-28', '2025-10-28');
GO

-- Insert 25 reading_history (added 5 more for coverage)
INSERT INTO reading_history (user_id, chapter_id, last_read_at) VALUES
(1, 1, '2025-10-31'), (2, 2, '2025-10-31'), (3, 3, '2025-10-30'), (4, 4, '2025-10-30'), (5, 5, '2025-10-29'),
(6, 6, '2025-10-29'), (7, 7, '2025-10-28'), (8, 8, '2025-10-28'), (9, 9, '2025-10-27'), (10, 10, '2025-10-27'),
(1, 11, '2025-10-26'), (2, 12, '2025-10-26'), (3, 13, '2025-10-25'), (4, 14, '2025-10-25'), (5, 15, '2025-10-24'),
(6, 16, '2025-10-24'), (7, 17, '2025-10-31'), (8, 18, '2025-10-30'), (9, 19, '2025-10-29'), (10, 20, '2025-10-28'),
(11, 1, '2025-10-27'), (12, 2, '2025-10-26'), (13, 3, '2025-10-25'), (14, 4, '2025-10-24'), (15, 5, '2025-10-31');
GO

-- Insert 15 saved_series (added 5 more)
INSERT INTO saved_series (user_id, series_id, saved_at) VALUES
(1, 1, '2025-10-31'), (2, 2, '2025-10-31'), (3, 3, '2025-10-30'), (4, 4, '2025-10-30'), (5, 5, '2025-10-29'),
(6, 6, '2025-10-29'), (7, 7, '2025-10-28'), (8, 8, '2025-10-28'), (9, 9, '2025-10-27'), (10, 10, '2025-10-27'),
(11, 11, '2025-10-26'), (12, 12, '2025-10-26'), (13, 13, '2025-10-25'), (14, 14, '2025-10-25'), (15, 15, '2025-10-24');
GO

-- Insert 25 ratings (added 5 more, ensured unique per user-series, user_id 1-20)
INSERT INTO ratings (user_id, series_id, score, rated_at) VALUES
(1, 1, 5, '2025-10-31'), (2, 1, 4, '2025-10-31'), (3, 1, 5, '2025-10-30'), (4, 1, 5, '2025-10-30'),
(5, 2, 4, '2025-10-29'), (6, 2, 5, '2025-10-29'), (7, 2, 4, '2025-10-28'), (8, 3, 3, '2025-10-28'),
(9, 3, 4, '2025-10-27'), (10, 3, 3, '2025-10-27'), (11, 4, 5, '2025-10-26'), (12, 4, 5, '2025-10-26'),
(13, 4, 4, '2025-10-25'), (14, 5, 2, '2025-10-25'), (15, 5, 3, '2025-10-24'), (16, 6, 4, '2025-10-24'),
(17, 6, 4, '2025-10-31'), (18, 7, 5, '2025-10-31'), (19, 7, 5, '2025-10-30'), (20, 7, 5, '2025-10-30'),
(1, 8, 3, '2025-10-29'), (2, 8, 4, '2025-10-29'), (3, 9, 5, '2025-10-28'), (4, 9, 5, '2025-10-28'),
(5, 10, 4, '2025-10-27'), (6, 10, 3, '2025-10-27');
GO

-- Insert 25 comments (added 5 more)
INSERT INTO comments (user_id, chapter_id, content, is_deleted, created_at, updated_at) VALUES
(1, 1, N'Great start! Love the atmosphere!', 0, '2025-10-31', '2025-10-31'),
(2, 2, N'Exciting! Cozy vibe!', 0, '2025-10-31', '2025-10-31'),
(3, 3, N'Intriguing plot, futuristic thrill!', 0, '2025-10-30', '2025-10-30'),
(4, 4, N'Needs more suspense, but romantic spark is nice.', 0, '2025-10-30', '2025-10-30'),
(5, 5, N'Love the sci-fi elements. Chilling atmosphere!', 0, '2025-10-29', '2025-10-29'),
(6, 6, N'Awesome action and intense survival!', 0, '2025-10-29', '2025-10-29'),
(7, 7, N'Sweet romance and insightful history!', 0, '2025-10-28', '2025-10-28'),
(8, 8, N'Mind-bending chapter!', 0, '2025-10-28', '2025-10-28'),
(9, 9, N'Fun adventure and hilarious take!', 0, '2025-10-27', '2025-10-27'),
(10, 10, N'Deep historical insight with emotional depth!', 0, '2025-10-27', '2025-10-27'),
(1, 11, N'This chapter is even better!', 0, '2025-10-26', '2025-10-26'),
(2, 12, N'Love the character development!', 0, '2025-10-26', '2025-10-26'),
(3, 13, N'The world-building is fantastic.', 0, '2025-10-25', '2025-10-25'),
(4, 14, N'Unexpected turn of events!', 0, '2025-10-25', '2025-10-25'),
(5, 15, N'Thrilling moment!', 0, '2025-10-24', '2025-10-24'),
(6, 16, N'A beautiful scene.', 0, '2025-10-24', '2025-10-24'),
(7, 17, N'Creepy ending!', 0, '2025-10-31', '2025-10-31'),
(8, 18, N'This made me laugh!', 0, '2025-10-30', '2025-10-30'),
(9, 19, N'Very informative!', 0, '2025-10-29', '2025-10-29'),
(10, 20, N'Cant wait for the next part!', 0, '2025-10-28', '2025-10-28'),
(11, 1, N'Well-written opener!', 0, '2025-10-27', '2025-10-27'),
(12, 2, N'Keeps me hooked!', 0, '2025-10-26', '2025-10-26'),
(13, 3, N'Great sci-fi twist.', 0, '2025-10-25', '2025-10-25'),
(14, 4, N'Romantic and fun.', 0, '2025-10-24', '2025-10-24'),
(15, 5, N'Spooky good!', 0, '2025-10-31', '2025-10-31');
GO

-- Insert 15 likes (added 5 more)
INSERT INTO likes (user_id, chapter_id, liked_at) VALUES
(1, 1, '2025-10-31'), (2, 2, '2025-10-31'), (3, 3, '2025-10-30'), (4, 4, '2025-10-30'), (5, 5, '2025-10-29'),
(6, 6, '2025-10-29'), (7, 7, '2025-10-28'), (8, 8, '2025-10-28'), (9, 9, '2025-10-27'), (10, 10, '2025-10-27'),
(11, 11, '2025-10-26'), (12, 12, '2025-10-26'), (13, 13, '2025-10-25'), (14, 14, '2025-10-25'), (15, 15, '2025-10-24');
GO

-- Insert 20 reports (timestamps updated)
INSERT INTO reports (reporter_id, staff_id, target_type, comment_id, chapter_id, reason, status, created_at, updated_at) VALUES
(1, 1, 'comment', 8, NULL, N'Inappropriate content', 'pending', '2025-10-25', '2025-10-25'),
(2, 2, 'chapter', NULL, 8, N'Violates guidelines', 'resolved', '2025-10-26', '2025-10-26'),
(3, 3, 'comment', 1, NULL, N'Spam', 'rejected', '2025-10-27', '2025-10-27'),
(3, NULL, 'comment', 2, NULL, N'Spam', 'pending', '2025-10-27', '2025-10-27'),
(4, 4, 'chapter', NULL, 4, N'Offensive material', 'pending', '2025-10-28', '2025-10-28'),
(5, 5, 'comment', 4, NULL, N'Harassment', 'resolved', '2025-10-29', '2025-10-29'),
(6, NULL, 'chapter', NULL, 6, N'Copyright issue', 'pending', '2025-10-30', '2025-10-30'),
(7, 6, 'comment', 6, NULL, N'Inaccurate', 'rejected', '2025-10-31', '2025-10-31'),
(8, 7, 'chapter', NULL, 2, N'Poor quality', 'resolved', '2025-10-24', '2025-10-24'),
(9, 8, 'comment', 9, NULL, N'Off-topic', 'pending', '2025-10-25', '2025-10-25'),
(1, 9, 'comment', 5, NULL, N'Offensive language', 'pending', '2025-10-26', '2025-10-26'),
(2, 10, 'chapter', NULL, 5, N'Content violation', 'resolved', '2025-10-27', '2025-10-27'),
(3, NULL, 'comment', 3, NULL, N'Duplicate content', 'rejected', '2025-10-28', '2025-10-28'),
(4, 1, 'chapter', NULL, 1, N'Inappropriate theme', 'pending', '2025-10-29', '2025-10-29'),
(5, 2, 'comment', 10, NULL, N'Spoiler alert', 'resolved', '2025-10-30', '2025-10-30'),
(6, NULL, 'chapter', NULL, 11, N'Plagiarism concern', 'pending', '2025-10-31', '2025-10-31'),
(7, 3, 'comment', 7, NULL, N'Harsh criticism', 'rejected', '2025-10-24', '2025-10-24'),
(8, 4, 'chapter', NULL, 12, N'Quality issue', 'resolved', '2025-10-25', '2025-10-25'),
(9, 5, 'comment', 11, NULL, N'Off-topic post', 'pending', '2025-10-26', '2025-10-26'),
(10, 6, 'chapter', NULL, 13, N'Needs edit', 'rejected', '2025-10-27', '2025-10-27');
GO

-- Insert 15 notifications (added 5 more for coverage)
INSERT INTO notifications (user_id, type, title, message, is_read, url_redirect, created_at) VALUES
(1, 'submission_status', N'Chapter Approved', N'Your chapter "The Lost Kingdom" has been approved by the moderator.', 0, '/chapters/12', '2025-10-25'),
(2, 'submission_status', N'Chapter Approved', N'Your new series "Shadows of Dawn" has been approved and published.', 1, '/chapter/8', '2025-10-26'),
(3, 'moderation', N'Comment Approved', N'Your comment on "Ocean’s Heart" has passed moderation.', 0, '/comments/54', '2025-10-27'),
(4, 'submission_status', N'Chapter Rejected', N'Your chapter "Dark Forest" was rejected. Please review the feedback and resubmit.', 1, '/chapters/15', '2025-10-28'),
(5, 'submission_status', N'Chapter Rejected', N'Your series "Love in the Rain" did not meet our guidelines.', 0, '/chapter/11', '2025-10-29'),
(6, 'moderation', N'Content Reported', N'Your comment on "Hidden Truth" has been reported by another user.', 0, '/comments/77', '2025-10-30'),
(7, 'moderation', N'Chapter Reported', N'Your chapter "Fallen Angel" has been reported for review.', 0, '/chapters/22', '2025-10-31'),
(9, 'moderation', N'Report Resolved', N'Your report regarding "Shadow Blade" has been reviewed and resolved.', 0, '/chapters/45', '2025-10-24'),
(10, 'moderation', N'Report Dismissed', N'The report you submitted about "Dream Hunter" was dismissed after review.', 1, '/chapters/46', '2025-10-25'),
(11, 'system', N'New Badge Unlocked', N'Congratulations! You earned the "Post 100 comments" badge.', 0, '/badges', '2025-10-26'),
(12, 'submission_status', N'Series Approved', N'Your series submission is now live.', 1, '/series/20', '2025-10-27'),
(13, 'moderation', N'Comment Flagged', N'Your recent comment is under review.', 0, '/comments/100', '2025-10-28'),
(14, 'system', N'Points Update', N'You gained 50 points for rating a series.', 0, '/points', '2025-10-29'),
(15, 'submission_status', N'Chapter Pending', N'Your chapter is awaiting moderator approval.', 1, '/chapters/18', '2025-10-30');
GO

-- Insert 15 badges_users (added 7 more)
INSERT INTO badges_users (badge_id, user_id, awarded_at) VALUES
(1, 1, '2025-10-25'), (2, 2, '2025-10-26'), (3, 3, '2025-10-27'), (4, 4, '2025-10-28'),
(5, 5, '2025-10-29'), (6, 6, '2025-10-30'), (7, 7, '2025-10-31'), (8, 8, '2025-10-24'),
(1, 9, '2025-10-25'), (2, 10, '2025-10-26'), (3, 11, '2025-10-27'), (4, 12, '2025-10-28'),
(5, 13, '2025-10-29'), (6, 14, '2025-10-30'), (7, 15, '2025-10-31');
GO

-- Insert 10 review_series (new addition for coverage)
INSERT INTO review_series (series_id, staff_id, status, comment, created_at) VALUES
(1, 1, 'approved', N'Strong narrative and engaging plot.', '2025-10-25'),
(2, 2, 'approved', N'Well-researched folklore elements.', '2025-10-26'),
(3, 3, 'pending', N'Needs more world-building details.', '2025-10-27'),
(4, 4, 'approved', N'Post-apoc theme handled masterfully.', '2025-10-28'),
(5, 5, 'rejected', N'Lacks originality in evolution mechanics.', '2025-10-29'),
(6, 6, 'approved', N'Compelling medical fantasy blend.', '2025-10-30'),
(7, 7, 'approved', N'Apocalyptic mana system innovative.', '2025-10-31'),
(8, 8, 'pending', N'Time manipulation needs clarification.', '2025-10-24'),
(9, 9, 'approved', N'Dark fantasy survival gripping.', '2025-10-25'),
(10, 10, 'approved', N'Vietnamese legend retelling excellent.', '2025-10-26');
GO

-- Insert 20 review_chapter (timestamps updated)
INSERT INTO review_chapter (chapter_id, staff_id, status, comment, created_at) VALUES
(1, 1, 'approved', N'Good content, well structured.', '2025-10-25'),
(2, 2, 'approved', N'Well written and engaging.', '2025-10-26'),
(3, 3, 'pending', N'Awaiting secondary review.', '2025-10-27'),
(4, 4, 'rejected', N'Needs major revision for clarity.', '2025-10-28'),
(5, 5, 'approved', N'Excellent pacing and tone.', '2025-10-29'),
(6, 6, 'approved', N'Meets all publication criteria.', '2025-10-30'),
(7, 7, 'pending', N'Awaiting final feedback.', '2025-10-31'),
(8, 8, 'rejected', N'Poor narrative flow, needs rewrite.', '2025-10-24'),
(9, 9, 'rejected', N'Lacks coherence and structure.', '2025-10-25'),
(10, 10, 'approved', N'Clean and polished.', '2025-10-26'),
(11, 1, 'approved', N'Solid opening, sets tone well.', '2025-10-27'),
(12, 2, 'pending', N'Review in progress by senior editor.', '2025-10-28'),
(13, 3, 'approved', N'Engaging tech details, clear style.', '2025-10-29'),
(14, 4, 'rejected', N'Too clichéd, lacks originality.', '2025-10-30'),
(15, 5, 'approved', N'Atmospheric and emotionally strong.', '2025-10-31'),
(16, 6, 'pending', N'Needs grammar corrections before approval.', '2025-10-24'),
(17, 7, 'approved', N'Accurate historical references.', '2025-10-25'),
(18, 8, 'pending', N'Requires deeper thematic review.', '2025-10-26'),
(19, 9, 'approved', N'Funny, sharp, and well-paced.', '2025-10-27'),
(20, 10, 'rejected', N'Overly dramatic; lacks subtlety.', '2025-10-28');
GO

-- Insert 20 point_history (timestamps updated)
INSERT INTO point_history (user_id, points_change, reason, reference_type, reference_id, created_at) VALUES
(1, 10, N'Read chapter', 'chapter', 1, '2025-10-25'),
(2, 50, N'Published series', 'series', 1, '2025-10-26'),
(3, 5, N'Commented', 'comment', 1, '2025-10-27'),
(4, -10, N'Report rejected', 'report', 3, '2025-10-28'),
(5, 20, N'Rated series', 'rating', 1, '2025-10-29'),
(6, 100, N'Badge awarded', 'badge', 1, '2025-10-30'),
(7, 15, N'Saved series', 'series', 7, '2025-10-31'),
(8, 30, N'Chapter approved', 'chapter', 2, '2025-10-24'),
(9, 25, N'Liked chapter', 'like', 9, '2025-10-25'),
(10, 40, N'Report resolved', 'report', 2, '2025-10-26'),
(1, 15, N'Read chapter', 'chapter', 11, '2025-10-27'),
(2, 60, N'Published chapter', 'chapter', 12, '2025-10-28'),
(3, 8, N'Commented', 'comment', 13, '2025-10-29'),
(4, 25, N'Rated series', 'rating', 1, '2025-10-30'),
(5, -5, N'Comment moderated', 'comment', 14, '2025-10-31'),
(6, 35, N'Saved series', 'series', 6, '2025-10-24'),
(7, 70, N'Chapter approved', 'chapter', 15, '2025-10-25'),
(8, 12, N'Liked comment', 'like', 16, '2025-10-26'),
(9, 45, N'Report accepted', 'report', 17, '2025-10-27'),
(10, 55, N'New series created', 'series', 10, '2025-10-28');
GO