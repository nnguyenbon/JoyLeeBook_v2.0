
USE [master]
GO
-- ============================================
-- Create Database: JoyLeeBook_v2 (TRIGGERS VERSION)
-- ============================================
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
-- Xóa dữ liệu
EXEC sp_MSforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL';
EXEC sp_MSforeachtable 'DELETE FROM ?';
EXEC sp_MSforeachtable 'ALTER TABLE ? CHECK CONSTRAINT ALL';
EXEC sp_MSforeachtable 'DBCC CHECKIDENT (''?'', RESEED, 0)';
ALTER DATABASE SCOPED CONFIGURATION SET IDENTITY_CACHE = OFF;

PRINT ' Database reset successfully!';
-- ============================================
-- 1. USERS TABLE
-- ============================================
CREATE TABLE users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    full_name NVARCHAR(255),
    bio NVARCHAR(300),
    email NVARCHAR(255) NOT NULL,
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
	CONSTRAINT FK_chapters_users FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
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
    type NVARCHAR(20) CHECK (type IN ('system', 'submission_status', 'moderation',  'coauthor_invitation')) NOT NULL,
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
('reader', N'John Doe', N'Book lover', 'reader@example.com', '$2a$10$39UOPPwOgXRF/Iaeo6CGg.bFQ8lVGx6ixef39nKp27Yax7tTe.fwS', NULL, 'reader', 1, 0, 'active', 100, '2025-10-25', '2025-10-25'),
('jane', N'Jane Smith', N'Fantasy writer', 'jane@example.com', '$2a$10$39UOPPwOgXRF/Iaeo6CGg.bFQ8lVGx6ixef39nKp27Yax7tTe.fwS', NULL, 'author', 1, 0, 'active', 500, '2025-10-26', '2025-10-26'),
('mike', N'Mike Johnson', N'Casual reader', 'mike@example.com', '$2a$10$39UOPPwOgXRF/Iaeo6CGg.bFQ8lVGx6ixef39nKp27Yax7tTe.fwS', NULL, 'reader', 0, 0, 'active', 50, '2025-10-27', '2025-10-27'),
('emily', N'Emily Davis', N'Mystery author', 'emily@example.com', '$2a$10$39UOPPwOgXRF/Iaeo6CGg.bFQ8lVGx6ixef39nKp27Yax7tTe.fwS', NULL, 'author', 1, 0, 'active', 300, '2025-10-28', '2025-10-28'),
('sarah', N'Sarah Wilson', N'Avid reader', 'sarah@example.com', 'hash5', NULL, 'reader', 1, 0, 'active', 200, '2025-10-29', '2025-10-29'),
('david', N'David Brown', N'Sci-fi enthusiast', 'david@example.com', 'hash6', 'google123', 'author', 0, 0, 'active', 400, '2025-10-30', '2025-10-30'),
('lisa', N'Lisa Garcia', N'Romance fan', 'lisa@example.com', 'hash7', NULL, 'reader', 1, 0, 'active', 75, '2025-10-31', '2025-10-31'),
('robert', N'Robert Martinez', N'Horror writer', 'robert@example.com', 'hash8', NULL, 'author', 1, 0, 'inactive', 250, '2025-10-24', '2025-10-24'),
('amanda', N'Amanda Rodriguez', N'New reader', 'amanda@example.com', 'hash9', NULL, 'reader', 0, 0, 'active', 10, '2025-10-25', '2025-10-25'),
('james', N'James Lee', N'Adventure author', 'james@example.com', 'hash10', NULL, 'author', 1, 0, 'active', 600, '2025-10-26', '2025-10-26'),
('chris', N'Chris Evans', N'Mystery fan', 'chris@example.com', 'hash11', NULL, 'reader', 1, 0, 'active', 150, '2025-10-27', '2025-10-27'),
('anna', N'Anna Taylor', N'Romance novelist', 'anna@example.com', 'hash12', NULL, 'author', 0, 0, 'active', 450, '2025-10-28', '2025-10-28'),
('kevin', N'Kevin Patel', N'Sci-fi reader', 'kevin@example.com', 'hash13', 'google456', 'reader', 1, 0, 'inactive', 80, '2025-10-29', '2025-10-29'),
('sophia', N'Sophia Kim', N'Thriller writer', 'sophia@example.com', 'hash14', NULL, 'author', 1, 0, 'active', 350, '2025-10-30', '2025-10-30'),
('olivia', N'Olivia Chen', N'Adventure lover', 'olivia@example.com', 'hash15', NULL, 'reader', 0, 0, 'active', 120, '2025-10-31', '2025-10-31'),
('michael', N'Michael Wong', N'Historical author', 'michael@example.com', 'hash16', NULL, 'author', 1, 0, 'banned', 200, '2025-10-24', '2025-10-24'),
('emma', N'Emma Lopez', N'Comedy enthusiast', 'emma@example.com', 'hash17', NULL, 'reader', 1, 0, 'active', 90, '2025-10-25', '2025-10-25'),
('noah', N'Noah Singh', N'Drama specialist', 'noah@example.com', 'hash18', NULL, 'author', 0, 0, 'active', 550, '2025-10-26', '2025-10-26'),
('sophie', N'Sophie Nguyen', N'Horror reader', 'sophie@example.com', 'hash19', NULL, 'reader', 1, 0, 'active', 60, '2025-10-27', '2025-10-27'),
('lucas', N'Lucas Garcia', N'Fantasy creator', 'lucas@example.com', 'hash20', 'google789', 'author', 1, 0, 'active', 700, '2025-10-28', '2025-10-28');
GO

-- XÓA TẤT CẢ DỮ LIỆU CŨ TRƯỚC KHI INSERT
DELETE FROM staffs;
GO

INSERT INTO staffs (username, password_hash, full_name, role, is_deleted, created_at, updated_at) VALUES
('admin', '$2a$10$39UOPPwOgXRF/Iaeo6CGg.bFQ8lVGx6ixef39nKp27Yax7tTe.fwS', N'Bob Admin', 'admin', 0, '2025-10-26', '2025-10-26'),
('staff1', '$2a$10$39UOPPwOgXRF/Iaeo6CGg.bFQ8lVGx6ixef39nKp27Yax7tTe.fwS', N'Alice Moderator', 'staff', 0, '2025-10-25', '2025-10-25'),
('staff2', 'staffhash2', N'Carol Reviewer', 'staff', 0, '2025-10-27', '2025-10-27'),
('staff3', 'staffhash3', N'Eve Checker', 'staff', 0, '2025-10-29', '2025-10-29'),
('staff4', 'staffhash4', N'Grace Editor', 'staff', 0, '2025-10-31', '2025-10-31'),
('staff5', 'staffhash5', N'Ivy Approver', 'staff', 0, '2025-10-25', '2025-10-25');
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

-- CLEAN SAMPLE SERIES (20 total)
INSERT INTO series (title, description, cover_image_url, status, approval_status, is_deleted, rating_points, created_at, updated_at) VALUES
-- Approved
(N'The New Kid in School', N'A story about Kaito starting a new life in a coastal town.', 'thenewkidinschool.png', 'ongoing', 'approved', 0, 45, '2025-10-25', '2025-10-25'),
(N'Cậu Bé Thông Minh (The Clever Boy)', N'During the Hùng Kings’ era, an official failed to find a wise person until he met a clever young boy in a village.
The king tested the boy by demanding three male buffalo produce nine calves, which the boy cleverly disproved.
He then solved another challenge by suggesting a needle be forged into a tiny knife to cook a sparrow.
When a neighboring country posed a riddle about threading a seashell, he solved it using an ant tied to a thread.
Impressed, the king named him trạng nguyên, honoring intelligence over status.', 'cau_be_thong_minh.avif', 'completed', 'approved', 0, 48, '2025-10-26', '2025-10-26'),
(N'Embers Ad Infinitum', N' In this latest work by Lord of the Mysteries author, Cuttlefish That Loves Diving,
be prepared for a well-thought out and detailed apocalyptic, cyberpunk world with a setting superseding Lord of the Mysteries!
Our protagonist, Shang Jianyao, is crazy—literally crazy, at least that’s what the doctors said. 
Living in a huge, underground building of Pangu Biology, one of the few remaining factions in this apocalyptic wasteland known as the Ashlands, 
he acts in unfathomable ways that’s head-scratching, comical, and shrewd. So is he really crazy? Probably.
', 'Embers_Ad_Infinitum.avif', 'ongoing', 'approved', 0, 42, '2025-10-28', '2025-10-28'),
(N'Goddess Medical Doctor', N'She was dressed in white, with a mysterious orchid imprint on her forehead, her skin like jade is always covered with a veil. Since childhood, she had left her parents due to her special status and lived her life with her master in the clouds. She was not yet ten when her medicinal skills were renowned in the world. In order to save the people, she descended the mountains, however, she encountered a storm on the way to the epidemic area. The man who saved her would tease her for enjoyment, little did they know…

', 'goddess_medical_doctor.avif', 'ongoing', 'approved', 0, 46, '2025-10-30', '2025-10-30'),
(N'Infinite Mana in The Apocalypse', N'Blessed with unlimited mana, Noah travels the worlds and sees rampant corruption and injustice.

Have you seen countless icebergs fall asunder?

Have you watched a dragon despair?

Follow one man as he overturns the order of the worlds...

---

The concepts appearing in this story are those of pure fantasy and fiction, they are not reflective of the real world. Everything is based on pure imagination.
', 'Infinite_Mana_In_The_Apocalypse.avif', 'ongoing', 'approved', 0, 47, '2025-10-31', '2025-10-31'),
(N'Shadow Slave', N'Chains, darkness, and survival in a cruel world.', 'shadow_slave.avif', 'ongoing', 'approved', 0, 41, '2025-10-25', '2025-10-25'),
(N'Sơn Tinh Thủy Tinh', N'Truyền thuyết Việt Nam về cuộc chiến giữa núi và nước.', 'Son_Tinh_Thuy_Tinh.avif', 'ongoing', 'approved', 0, 44, '2025-10-26', '2025-10-26'),
(N'Tensei Shitara Slime Datta Ken', N'A man reincarnates as a slime with great powers.', 'tensei_shitara_slime_datta_ken.avif', 'completed', 'approved', 0, 43, '2025-10-28', '2025-10-28'),
(N'The Legendary Mechanic', N'Sci-fi action story about technology and power.', 'the-legendary-mechanic-novel.avif', 'completed', 'approved', 0, 49, '2025-10-30', '2025-10-30'),
(N'The Legend of Mai An Tiêm', N'Truyền thuyết Việt Nam về quả dưa hấu và lòng trung hiếu.', 'The_Legend_of_Mai_An_Tiem.avif', 'completed', 'approved', 0, 48, '2025-10-25', '2025-10-25'),
(N'Sorce Stone', N'A mystical journey through ancient lands.', 'sorce_stone.avif', 'completed', 'approved', 0, 42, '2025-10-27', '2025-10-27'),
(N'So Dua', N'Truyện dân gian Việt Nam về người nông dân thông minh.', 'so_dua.avif', 'ongoing', 'approved', 0, 41, '2025-10-28', '2025-10-28'),

-- Pending / Rejected (no chapters, just for UI testing)
(N'Dungeon Diver: Stealing a Monster', N'Adventurer who steals rare monsters.', 'dungeon_diver_stealing_a_monster.avif', 'ongoing', 'pending', 0, 50, '2025-10-27', '2025-10-27'),
(N'Evolving Infinitely from Ground Zero', N'A man starts anew after the end of the world.', 'Evolving_infinitely_from_ground_zero.avif', 'completed', 'rejected', 0, 39, '2025-10-29', '2025-10-29'),
(N'Monarch of Time', N'A man manipulates time to change destiny.', 'monarch_of_time.avif', 'ongoing', 'pending', 0, 49, '2025-10-24', '2025-10-24'),
(N'Supreme Magus', N'A scholar strives to master the arcane arts.', 'supreme-magus-webnovel.avif', 'ongoing', 'rejected', 0, 46, '2025-10-27', '2025-10-27'),
(N'The Epi of Leviathan', N'A fantasy about gods and mortals clashing.', 'the_epi_of_leviathan.avif', 'ongoing', 'rejected', 0, 45, '2025-10-24', '2025-10-24'),
(N'The Mech Touch', N'Mecha engineering, war, and ambition.', 'The_Mech_Touch.avif', 'ongoing', 'pending', 0, 44, '2025-10-26', '2025-10-26'),
(N'The World of Otome Games is Tough', N'A man reincarnates in a dating sim world.', 'the-world-of-otome-games-is-toug.avif', 'ongoing', 'pending', 0, 40, '2025-10-31', '2025-10-31'),
(N'Thanh Giong', N'Truyền thuyết về vị anh hùng làng Gióng cứu nước.', 'thanh_giong.avif', 'ongoing', 'pending', 0, 47, '2025-10-29', '2025-10-29');
GO


-- Insert 20 series_categories (unchanged, but ensure category_id <=30)
INSERT INTO series_categories (series_id, category_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 11), (12, 12), (13, 13), (14, 14), (15, 15), (16, 16), (17, 17), (18, 18), (19, 19), (20, 20);
GO

DELETE FROM series_author;
GO

INSERT INTO series_author (series_id, user_id, is_owner) VALUES
(1, 2, 1),   -- author1
(2, 4, 1),   -- author2
(3, 6, 1),   -- author3
(4, 8, 1),   -- author4
(5, 10, 1),  -- author5
(6, 12, 1),  -- author6
(7, 14, 1),  -- author7
(8, 16, 1),  -- author8
(9, 18, 1),  -- author9
(10, 20, 1), -- author10
(11, 2, 0),  -- author1 góp thêm
(12, 4, 0),
(13, 6, 0),
(14, 8, 0),
(15, 10, 0),
(16, 12, 0),
(17, 14, 0),
(18, 16, 0),
(19, 18, 0),
(20, 20, 0),
(2, 6, 0);
GO


-- CLEAN SAMPLE CHAPTERS (100 total for approved series only)
INSERT INTO chapters (series_id, user_id, chapter_number, title, content, status, approval_status, is_deleted, created_at, updated_at)
VALUES
-- Series 1: The New Kid in School
(1, 2, 1, N'The Night Before the First Day', N'The night sky stretched out like a velvet blanket, dotted with fluffy clouds and shimmering with the soft glow of the moon. Below, rooftops huddled close together, the lights from their small windows twinkling like stars on the ground, stretching down the slope towards the deep blue sea. The night sea was calm, with only the distant whisper of waves like a lullaby.
On a small balcony of one of those houses, Kaito sat quietly, his gaze lost on the horizon. He was a "hopeless romantic," just like the words on the book cover he often read. He loved to watch the scenery, to lose himself in distant thoughts—about beautiful things, about love, and about new beginnings.
Tomorrow was Kaito''s first day at a new school. His family had just moved to this small coastal town, where everything was unfamiliar and full of promise. Kaito was the new student, the "new kid in school," and a mix of anxiety and excitement crept into his heart. He wondered what kind of friends he would meet here. Would there be someone who could understand his romantic soul?
He sighed softly, his chin resting on his hand, his fluffy brown hair stirring in the cool sea breeze. Kaito looked down at the lit streets, imagining the stories that would unfold. He knew that, despite the initial awkwardness, this place would become an important part of his life. And perhaps, he would find what his romantic heart had always been searching for.
His eyes landed on a lit house in the distance, where a faint silhouette moved past the window. Kaito smiled, a smile full of hope. "Welcome to the new chapter, Kaito," he told himself.', 'published', 'approved', 0, '2025-10-25', '2025-10-25'),
(1,2,2,N'New Friends',N'The morning sun spilled through the kitchen window, painting warm golden stripes across the floor. Kaito slipped on his shoes, heart pounding lightly as he prepared to walk to his new school. His mother straightened his collar, offering him a reassuring smile, while his father gave him a playful pat on the back.
The walk was short, but every step felt heavy with anticipation. The school gates stood tall, buzzing with laughter, chatter, and the rush of students hurrying to class. Kaito took a deep breath and entered.
Inside the classroom, the homeroom teacher smiled warmly and introduced him. Dozens of eyes turned toward him, curious but not unkind. Kaito felt a lump in his throat until a cheerful voice called from the second row.
“Hey! You can sit here!”
A girl with bright eyes and neatly tied hair waved at him—Hana. Beside her sat a boy sketching in his notebook—Riku. Kaito hesitated only a moment before taking the seat.
Hana immediately leaned in. “You’re new here, right? Don’t worry, we’ll help you out!”
Riku lifted his sketchbook slightly, showing a drawing of the beach waves. “You like the sea?”
Kaito nodded shyly.
And just like that, the conversation blossomed. They shared jokes about teachers, talked about secret snack stalls behind the pier, and even argued about which bakery had the best cream buns.
When lunch break arrived, Hana dragged Kaito to the courtyard, excitedly showing him the school’s famous cherry blossom tree that bloomed almost year-round.
By the time the final bell rang, the weight in Kaito’s chest had eased. The fear of loneliness that haunted him last night was now replaced by something warm—something like belonging.
Maybe… this school wouldn’t be so bad after all.', 'published','approved',0,'2025-10-26','2025-10-26'),
(1,2,3,N'Mystery Note',N'The next day began normally, filled with light chatter and morning classes. Kaito laughed more than he expected, even answering a few questions confidently. But everything changed during lunch break.
When he opened his locker, a white folded note fluttered to the ground. He picked it up curiously, feeling the unfamiliar texture of the paper between his fingers.
Inside, written in neat flowing handwriting, were the words:
“No one who comes to this town is here by accident.”
Kaito blinked. A strange chill ran down his spine.
Was it a prank? Some kind of welcome joke? Or something meant only for him?
Hana peeked over his shoulder. “A love letter already?” she teased.
But when she read the message, her smile faded slightly—too slightly for Kaito to ignore.
Riku, who came by at that moment, glanced at the note as well. He paused… just a little too long.
“Probably someone messing around,” he said, but his voice held a quiet stiffness.
Kaito tried to laugh it off, but a tiny knot of unease settled in his chest.
After school, Hana walked him partway home. She seemed unusually thoughtful, her eyes drifting toward the distant ocean that glimmered beneath the falling sun.
“Kaito… if you ever get another note like that, tell me, okay?” she murmured.
“Why? Does it mean something?”
Hana hesitated, then forced a cheerful smile. “Probably not! Don’t worry about it.”
But Kaito couldn’t shake the feeling that something in this town wasn’t as ordinary as it first seemed.
As he tucked the note into his pocket, the sea breeze picked up—almost as if whispering back to him.', 'published','approved',0,'2025-10-27','2025-10-27'),
(1,2,4,N'Seaside Secrets',N'Two days later, Hana and Riku invited Kaito to explore the cliffs after school. The path was narrow and rocky, but the view was breathtaking. The ocean spread endlessly before them, shimmering under the late-afternoon sun.
Riku pointed to a spot near the cliff’s edge. “Stay here until the sun sets,” he said quietly. “You’ll see something strange.”
Kaito sat beside them, the wind brushing his hair. The sky shifted from gold to orange, then to a deep purple twilight. Just as the last ray of sunlight dipped below the horizon—
Something glowed beneath the water.
Soft blue lights flickered below the surface, swirling like dancing spirits. They moved in patterns, forming shapes Kaito couldn’t understand—symbols? Messages?
“They appear every few weeks,” Hana whispered. “People in town say it’s just plankton, but…”
“But some people,” Riku added, “think it’s something older. Something alive.”
Kaito watched in awe as the lights pulsed like a heartbeat. He thought of the mysterious note. Suddenly, its message didn’t feel like a prank anymore.
As the lights dimmed, Riku’s expression turned solemn.
“Kaito,” he said, “if the sea ever feels like it’s calling you… don’t follow it alone.”
The wind howled gently, but the warning felt real—too real.
Kaito swallowed hard.
Something was happening in this town.
And he was now part of it.', 'published','approved',0,'2025-10-28','2025-10-28'),
(1,2,5,N'Storm Night',N'The storm came without warning. Rain hammered against rooftops, thunder cracked angrily across the sky, and the sea roared like a wild creature awakened.
Kaito lay in bed, unable to sleep. Something tugged at him—an unsettling instinct that he couldn’t explain.
A flash of lightning illuminated the room.
He suddenly knew:
Riku.
Without thinking, he grabbed his jacket and rushed outside. The wind shoved at him violently, but he pushed forward, following the familiar path to the cliffs.
When he reached the top, he froze.
Riku stood at the edge, drenched, trembling, staring at the raging ocean as if hypnotized.
“Riku!” Kaito shouted over the storm.
Riku turned slowly. His eyes were distant, unfocused, shimmering faintly with reflected blue light.
“I heard them,” Riku whispered. “The ones beneath the waves. They were calling me again.”
A massive wave crashed below, and for a moment, the water glowed—bright, unnatural, alive.
Lightning flashed, revealing a colossal shadow beneath the churning sea.
Kaito grabbed Riku’s arm. “Let’s go! Please!”
For a few terrifying seconds, Riku didn’t move. Then, as another thunderclap shook the earth, he finally snapped back to reality.
Together, they stumbled down the cliff path, soaked and shivering.
But Kaito couldn’t stop thinking about the shadow under the water.
Something old had awakened.
And it wasn’t done with them yet.', 'published','approved',0,'2025-10-29','2025-10-29'),
(1,2,6,N'The Farewell',N'The next morning, the town felt unnaturally quiet. The storm had passed, leaving behind the scent of wet earth and salt.
But something else was gone too.
Riku.
He didn’t show up at school, and when Hana asked the teacher, she simply replied with a forced smile, “Riku is visiting relatives.”
Hana clenched her fists. “That’s a lie,” she whispered.
Kaito knew she was right. The fear in Riku’s eyes last night… the whispering sea… it all added up to something terrible.
After classes, Kaito and Hana walked to the cliffs together. They found Riku’s sketchbook lying beneath a rock, pages soaked and torn.
Flipping through the ruined drawings, they found one page untouched—
A glowing figure beneath the waves.
A figure reaching out a hand.
Hana covered her mouth, tears forming. “He followed them…”
Kaito felt his chest tighten painfully. He picked up a small smooth stone from the ground, carved their initials on it, and placed it on the cliff edge.
“We’ll wait for you,” he whispered into the wind.
The sea answered only with a distant, haunting echo.', 'draft','approved',0,'2025-10-30','2025-10-30'),
(1,2,7,N'Hope Returns',N'Days slipped by. The town returned to normal, but nothing felt normal to Kaito or Hana. Every sunset reminded them of Riku’s absence.
One morning, while walking along the beach, Kaito noticed something glinting in the sand. Buried halfway beneath the tide was a small glass bottle.
Inside it was a soft blue glow—
And a rolled-up piece of paper.
Hands trembling, Kaito opened it.
The handwriting was unmistakable.
“I’m alright. The sea isn’t what we thought. I’ll return when I can.”
Hana gasped, tears immediately welling up. The message felt warm, hopeful, alive.
Attached to the note was a small glowing strand—like a feather made of light.
Kaito held it gently, feeling a soft hum vibrate through it.
“He’s alive,” Hana whispered.
Kaito nodded. “And he wants us to wait for him.”
The sea breeze wrapped around them, softer than before—almost like a gentle embrace.', 'published','pending',0,'2025-10-31','2025-10-31'),
(1,2,8,N'Promise',N'A few weeks later, the town’s rhythm settled again. Autumn winds brushed across the sea, carrying with them a faint chill and the promise of change.
One evening, Kaito and Hana stood on the cliff where everything began. The sun dipped low, casting orange light across the rippling water.
Kaito clutched the glowing strand from Riku’s message. It pulsed faintly, like a heartbeat.
“Do you think he’ll really come back?” Hana asked quietly.
Kaito looked out at the vast ocean. “He will.”
Hana smiled softly and nudged him. “Then we’ll be here when he does.”
The horizon shimmered. For a moment, Kaito thought he saw a shape—slender, glowing, swimming beneath the waves.
The sea lights flickered once… twice… then disappeared.
Kaito inhaled deeply. “This isn’t just the end of a chapter,” he murmured. “It’s the beginning of something else.”
Hana nodded. “A promise.”
The wind carried their voices outward, merging with the rhythmic hum of waves.
A promise made by two friends—
Waiting for the third to return.
And beneath the surface, the ocean quietly listened.', 'published','pending',0,'2025-11-15','2025-11-15'),

-- Series 2: Cậu Bé Thông Minh
(2,4,1,N'only 1 chapter',N'Once upon a time, when our country was in need of talented individuals to help govern, the court officials were aging and lacked vigor. The king sent an official to travel across the land to seek out a wise and capable person to assist in state affairs. The official journeyed far and wide, wearing out his horse, but found no one satisfactory. Wherever he went, he posed challenging riddles to identify talent, yet no one could solve them. One day, passing through a village field, exhausted and resting by the roadside while his horse grazed, he saw a father and son working the land. The father, frail and thin, was guiding a buffalo to plow, while his son, about seven or eight years old, was breaking up clumps of soil. The official asked, “Hey, old man! How many furrows can your buffalo plow in a day?” The father, puzzled and unsure how to respond to the official, hesitated. But the boy quickly answered, “Sir, let me ask you first: if you can tell me how many steps your horse takes in a day, I’ll tell you how many furrows our buffalo plows.” The official was astonished by the boy’s clever response, momentarily at a loss for words, but inwardly delighted, thinking, “This boy will surely grow up to be a great talent. Why bother searching further?”
The official inquired about the father and son’s names and hometown, then galloped back to report to the king. Seeing the official return enthusiastically and claim he had found a talent, the king was pleased but wanted to test the boy’s intelligence. He sent messengers to the village with three bushels of sticky rice and three male buffalo, ordering the village to raise the buffalo to produce nine offspring by the next year or face punishment. The villagers, upon receiving the king’s decree, were both hopeful and anxious—hopeful because the king’s attention might bring future support, but worried because male buffalo cannot bear offspring. Numerous village meetings yielded no solutions, and the decree was seen as an impending disaster. When the boy, son of the plowman, heard of this, he told his father, “Father, it’s rare to receive the king’s favor. Tell the village to slaughter two buffalo and cook two bushels of sticky rice for everyone to feast heartily. With the remaining buffalo and rice, we’ll ask the village to sell them to fund our journey to the capital.” The father, alarmed, replied, “Son, slaughtering the king’s buffalo? Not just a year from now, but tomorrow the whole village could be punished! Don’t be foolish.” The boy smiled confidently and said, “Trust me, Father. I know how to handle this, and it will all work out.”
The father, persuaded, went to the village elders to share the boy’s plan. Skeptical, the villagers demanded a written pledge from the father and son, agreeing to proceed only if they took responsibility. A few days later, the father and son packed and set off for the capital. At the royal palace, the boy told his father to wait outside for good news, then,趁着 guards were distracted, slipped into the courtyard and began crying loudly. Hearing a child’s commotion, the king sent soldiers to bring the boy before him and asked, “You there, why are you making such a racket in my courtyard?” The boy replied, “Your Majesty, my mother died early, and it’s just me and my father. But he won’t give me a sibling to play with, so I’m crying. Please, Your Majesty, order my father to give me a sibling!” The king and his courtiers burst into laughter. The king said, “If you want a sibling, your father must remarry. A man cannot give birth!” The boy, suddenly cheerful, responded, “Then why did Your Majesty order our village to make three male buffalo produce nine offspring? Male buffalo can’t give birth either.” The king laughed, saying, “That was just a test of your wit. Didn’t your village slaughter the buffalo and feast together?” The boy replied, “Your Majesty, upon receiving your buffalo and rice, our village, knowing it was your gift, held a grand feast.”
The king acknowledged that the official’s report was correct—this boy was remarkably clever. However, he wanted to test him further. The next day, while the father and son were eating at a public guesthouse, a royal messenger brought a sparrow and said, “The king orders you to prepare three dishes from this sparrow.” The quick-witted boy told his father to fetch a sewing needle and said to the messenger, “Take this needle to the king and ask him to have it forged into a knife so we can butcher the sparrow.” When the messenger reported this to the king, the king was thoroughly impressed by the boy’s ingenuity. He summoned the father and son to the palace and rewarded them generously.
At that time, a neighboring country, plotting to invade, sent an envoy with a tricky riddle to test for talent: “How can you thread a thin string through a hollow, spiral seashell with openings at both ends?” Upon hearing the envoy’s challenge, the king and his ministers exchanged worried glances. Failing to solve the riddle would signal weakness and a lack of talent, giving the neighboring country an advantage. The ministers tried various methods—sucking the thread through, coating it with wax to stiffen it—but all failed. With no solution, the king, to buy time, invited the envoy to rest at the guesthouse. A royal official was dispatched with the king’s decree to consult the clever boy. The boy, playing with friends behind his house, listened to the riddle and, instead of answering directly, sang a song: “Tang tính tang! Tính tình tang! Catch an ant, tie a thread around its waist. Cover one end with paper, smear grease on the other, the ant will gladly crawl through. Tang tình tang…” He added, “I don’t need to return to court. Follow my song, and the thread will pass through the shell!” The official joyfully returned and reported to the king. Sure enough, an ant threaded the string through the shell, astonishing the foreign envoy. From then on, the king appointed the boy as the top scholar (trạng nguyên) and had a residence built for him near the palace for easy consultation.
', 'published','approved',0,'2025-10-26','2025-10-26'),

-- Series 3: Embers Ad Infinitum
(3,6,1,N'Central Assignment',N'Translator:virtual groupEditor:virtual group
The outer walls of the 495th floor’s Zone C were grayish-green. Six or seven girls walked into the Rec Center that was covered with all kinds of graffiti. Their expressions were colored with excitement, anticipation, and nervousness.
Their clothes were simple, and their colors were nothing gaudy. They were mainly blue, black, white, and green in color, but that did not obscure their beautiful facial features. All of them were in the prime of their youth.
While looking at the only LCD screen on the entire floor, the girl right in front couldn’t help but whisper, “I wonder what kind of husband the company will assign me.”
Beside her, a girl in a green top and blue pants bit her lip and said, “The main question is what kind of person he is.”
Brought up as second-generation youths when genetic enhancement drugs had widespread use, they were not worried about their future husbands’ looks and height. They were definitely all above average.
The girl in the lead glanced at her companion. “Have you forgotten that besides those of our age, there are also those whose wives have already passed away? Some of them are already in their forties or fifties. They are highly defective due to the lack of genetic enhancement while they were embryos.”
In order to ensure that there were enough newborns, the company they belonged to had a rule: “Anyone at the age of 20 or university graduates, who hasn’t acquired a spouse by choice, will have a partner assigned to them by the company. Anyone in defiance of this regulation will be punished by the Order Supervisory Department. The first violation will have the said person’s energy allocations and contribution points reduced. A second violation will result in said person’s banishment from the company, and they will be left to fend for themselves in the Ashlands.”
Similarly, those who had lost their spouses and had yet to have children were forced to participate in a central assignment of spouses three years after their loss if they had yet to turn 60.
Another girl joined in the discussion, making a joke. “Besides, ignoring this batch of people, don’t you have any hopes that your future husband will be from an M-rank family?”
The company was divided into three classes: D-rank employees, from D1 to D9; M-rank management, from directors at M1 to the corresponding board members and chief scientists at M3; and finally, a rank without an alphabetical code name. It only had one title: ‘Big Boss.’ It was held by an unusually mysterious lady.
The girl in the green top and blue trousers curled her lips and said, “When have people from normal levels ever been assigned spouses from Floors 346, 347, 348, and 349?”
Floors 346–349 were where the M-rank managers lived. They were given a generous energy supply quota, and the per capita housing area was more than ten times the average employee’s residential floor. Moreover, these four stories had their own independent elevators, potable water, ventilation, drainage, and education system. Their children normally didn’t make contact with the average employees.
The only exception was at the stage of advanced education because there was only one university in the entire company, which was situated on the 350th floor.
Children of ordinary employees needed to take exams to determine if they needed to start working or enter university. In contrast, children from M-rank families were exempt from taking the exams.
Within the company, there was nobody who didn’t want to join management, nor was there anyone who didn’t want to be associated with management.
As for ‘Big Boss,’ the ordinary employees didn’t even know what she looked like, much less had a chance to interact with her. Only at the end of the year, the beginning of the year, or when there was a major event, could they hear her voice through the radio. Therefore, it was rare for people to fantasize about catching the Big Boss’s eye and immediately getting promoted to management.
Of course, rare didn’t mean no chance. There just weren’t many such occurrences.
The girl in the lead smiled and said, “That’s why I’ve been encouraging you gals to date freely while in school. Look at Chen Bei. Her husband was earmarked by the Supply Management Department the moment he graduated. He must have some background!”
“Meng Xia, you have the cheek to say that? Why didn’t you follow what you preach?” the other girls mocked.
“It’s not like you don’t know me. I’m all talk.” Meng Xia wasn’t ashamed at all to admit that she didn’t have the courage.
After laughing for a while, the girl in a green top and blue pants curiously asked, “Meng Xia, do you know what rank the family of Chen Bei’s husband is? You two have always been close.”
Meng Xia looked around and said in a suppressed voice, “Rumor has it that he’s the chief inspector of the Security Department’s Operations Group.”
“Wow…” While the girls were exclaiming, a group of young men walked into the Rec Center.
The two groups of people sized each other up for a few seconds before they looked away shyly. After all, no one knew if their future husband or wife was just across them.
A man—about 1.75 meters tall with a refreshing crew cut—glanced at the few old tables, the benches, and the high-back chairs around them in the Rec Center. He then nervously spoke to his companion beside him. “Shang Jianyao, what kind of wife do you think the company will assign me?”
His companion was about 1.85 meters tall, with straight eyebrows and bright brown eyes. His face was deeply contoured, and his black hair was slightly messy, covering half of his forehead.
The young man named Shang Jianyao turned his head to glance at his companion and said, “First, you’ll have to be assigned before we can discuss what she’ll be like.” He was wearing a dark-blue, two-piece suit. The muscles on his arms bulged, creasing the fabric ever so slightly. He looked masculine and strong.
“Ha, I won’t be that unlucky, will I? There are only two more men than women this time.” The 1.75-meter-tall man with average features laughed. His expression gradually turned solemn as he rambled. “Could it be that they won’t like me? I’m only 1.75 meters tall after genetic enhancement. I’m not handsome either. My grades are only average…”
Shang Jianyao said solemnly, “That’s not the point. The point is your feminine name.”
“Feminine name? What’s wrong with the name Long Yuehong? My father’s surname is Long, and my mother’s name has the word ‘Hong’ in it. How meaningful is that?” Long Yuehong muttered to himself in confusion. “That’s true. The company’s assignment doesn’t care about how tall or good-looking I am. It’s said that it’s all random after any blood-related possibilities are eliminated… Ah, will they mistake me as a woman because of my name and assign me to a husband? What should I do if that happens?”
Shang Jianyao sized up Long Yuehong and said, “Organ transplant, neural reconstruction, artificial uterus. A perfect solution.”
Long Yuehong laughed awkwardly. “Haha, how is that possible? I mean, how can they get it wrong? Every document about me states that I’m male! What a strange train of thought you have. Shouldn’t a normal person be thinking of making a complaint?”
Without waiting for Shang Jianyao’s response, he added, “So, why is my name the key?”
“A name represents a person’s destiny. A random assignment is all about one’s destiny,” Shang Jianyao replied seriously.
Long Yuehong’s expression froze for two seconds. “I knew you couldn’t give me any constructive ideas!” Just as he said that, he asked, “What kind of wife do you want?”
Shang Jianyao lifted his chin and said, “I don’t need one. The company has insufficient resources, and the humans above the Ashlands are in dire straits. The curse of famine, infection, mutation, and beastification still cloaks the entire world. How can I marry?”
“…” Long Yuehong laughed. “You’re getting better at joking.”
Shang Jianyao looked at him and said without a smile, “I’ve already applied to give up today’s central assignment.”
“Are you serious? That’s impossible. How could the company agree to your request!? Haha, I almost believed you!” Long Yuehong was shocked at first, but then he let out a sigh of relief.
As soon as he finished speaking, Chen Xianyu—the person-in-charge of the Rec Center on the 495th floor—left his seat. He walked to the LCD screen and began to adjust it.
This old man with white hair and staggering footsteps was once part of the Security Department. He was in charge of expedition members and had always been a D7 team leader. Later, he left the Security Department due to his age and was promoted to become a D8 manager, who was in charge of the Rec Center on this floor.
Shang Jianyao and Long Yuehong were very curious about the old man’s past. They had a penchant for coming to the Rec Center and asking all sorts of questions. However, Chen Xianyu strictly adhered to the confidentiality regulations and only selected things that everyone knew about for conversations. He was like an ordinary employee who was born, grew up, studied, worked, matured, and aged in the Inner Ecosystem. He had never left the underground building or seen the real sky.
“Alright, it’s about to begin.” Chen Xianyu held onto a remote control and pressed down heavily.
The display flickered a few times before emitting a faint light.
Long Yuehong, Meng Xia, and the others held their breaths as they waited for the assignment’s results to be announced.
They were not worried that the names would scroll down too quickly, preventing them from reading it clearly. This was because the Rec Centers on each floor only showed the results related to the residents on the respective floor.', 'published','approved',0,'2025-10-28','2025-10-28'),
(3,6,2,N'Follow-up Review',N'Translator:virtual groupEditor:virtual group
After the second hand on the Rec Center’s old, hanging clock creakingly spun three and a half rounds, lines of text finally appeared on the LCD screen.
Meng Xia and the others quickly searched for their names before they heaved a sigh of relief one after another.
Most of them weren’t necessarily satisfied or excited, but they weren’t unhappy either. To them, this was no different from participating in exams. It didn’t matter as long as the results weren’t too bad. After all, their parents and grandparents had been through the same.
The others were more confused because they didn’t know who their marriage partner was, what floor they came from, or which department their parents belonged to. Even though they had all entered university and received higher education, the people they knew were still limited to their classmates and neighbors on the same floor.
Long Yuehong looked up and down the list carefully and seriously. Finally, he could not help but mutter to himself, “Why isn’t my name on the list?”
“Because your name sucks.” Shang Jianyao’s expression didn’t change.
“…” Long Yuehong wanted to refute, but sadly, he found himself agreeing with Shang Jianyao’s conclusion.
Thousands of people had met the conditions and were forced to participate in the central assignment, but there were only two more men than women. If not for their ill fate, a sucky name, or bad luck, how could they have been one of the two unlucky fools?
Long Yuehong paused for a moment before indignantly saying, “Your name isn’t on the list either!” He did not notice Shang Jianyao receiving any successful assignment of a female.
Shang Jianyao raised his right eyebrow and said, “Didn’t I tell you? I’ve already applied to give up on this marriage assignment.”
“Seriously? Why would the company agree…” Long Yuehong was stunned and confused. He felt like his world had been turned upside down. He had lived for 21 years. In the past, he had indeed heard of people who fit the criteria but did not participate in the marriage assignment. However, they had sufficient reason for doing so. The other party was either bedridden and could die at any moment or had participated in the Security Department’s expeditions. It was even a question if they could return.
Nobody who was healthy and fit in the company dared to violate the rules if they met the criteria. This was one of the core duties of a company employee.
Long Yuehong’s sadness was washed away by this matter. He looked at Shang Jianyao and asked, “Are you prepared to accept a reduction in energy rationing? That’s not too bad. The scariest thing is to have contribution points deducted. You won’t even have enough to eat when that happens! People like us—who are only at D1—only receive 1,800 points every month, affording us meat just once a week. Having one-third deducted at once speaks volumes!”
“The company has agreed to my request. There won’t be any deductions.” Shang Jianyao smiled.
“No, impossible, impossible…” Long Yuehong muttered to himself as he suddenly thought of something.If Shang Jianyao really applied to give up on this marriage assignment, it means that there should only be one more male than female participants. Just one more person…
I-I am the only unlucky bastard…Long Yuehong’s mouth fell wide open, and a deep sense of sorrow rose from his heart.
At this moment, the display began to flip the pages. It briefly introduced the basic information of the people who had successfully matched up with this floor’s residents so that they could find each other and register their marriage at the Order Supervisory Department’s various branches.
“Meng Xia, your husband is an outsider!” The crowd stared at the screen for a while when an exclamation came from the women’s side.
Meng Xia’s expression was slightly solemn. Her eyes darted about slightly as she muttered, “Zhang Lei; Male; Born: Wilderness nomad; Age: 25; Recruited by the company three years ago; Performance has always been good; There are no latent problems with his body; Residence: 622nd floor, Zone A, Room 192; Employee Level: D4; Electronic Card Number: 04311029189…”
“There really are outsiders…” Long Yuehong was also attracted by this matter and started discussing it with his companions.
They all knew that the company would periodically take in nomads from the wilderness to supplement the population and perfect their genes. However, residents of this floor had never worked with outsiders before, and nobody had ever married them, so everyone treated this matter as an interesting piece of news.
“Meng Xia, it’s actually not that bad. Although he used to be a nomad in the wilderness, he’s now a D4 employee. He’s only 25 years old. That’s very impressive!” The girl in the green top and blue pants consoled her friend.
D4 meant that he had gone from an ordinary employee to a senior, high-ranking employee. He could be the deputy of a small research project, a factory production line’s supervisor, an assistant team leader of the Security Department, or the Order supervisor of a certain floor’s zone. His monthly compensation was at least 2,000 points higher than D1 employees.
A young man beside Long Yuehong muttered, “However, the effect of genetic enhancement after adulthood isn’t that good…” At this moment, he saw the information of his betrothed.
“Zhou Qi; Female; Born: Internal employee; Age: 30 years old; Had a former husband who died five years ago, currently raising a child; voluntary application to participate in this marriage assignment; There are no latent problems with her body; Residence: 569th floor, Zone B, Room 27; Employee level: D4; Electronic Card Number: 01609052558…”
“Yang Zhenyuan, your wife is ten years older than you…” Long Yuehong also saw the information.
Yang Zhenyuan was the same as most of his peers in the company. His face was fair and clean, and his body was muscular. He looked good, but he had slightly androgynous features and appeared more introverted.
Yang Zhenyuan’s face flushed red when he heard Long Yuehong’s words. He wanted to say something but couldn’t say a word.
After a while, everyone finally memorized their partner’s corresponding information. They then left the Rec Center one after another, preparing to find their betrothed or return home to wait for the other party to come looking for them.
At this moment, a voice suddenly sounded in the hall when only five or six people were left. “Who is Yang Zhenyuan?”
“Me, what is it?” Yang Zhenyuan—who was chatting with Long Yuehong and Shang Jianyao—subconsciously turned to look at the door.
A woman walked into the Rec Center. Her appearance was mature and charming. Although she wore simple and plain clothes, they could not hide her impressive figure. “I’m Zhou Qi.”
The woman glanced at Yang Zhenyuan and nodded in satisfaction. “Shall we go to your house for a chat?”
Yang Zhenyuan was shocked at first, but he quickly nodded. “Sure, sure.”
“Then let’s go now?” Zhou Qi smiled like a blooming flower.
“Sure, sure,” Yang Zhenyuan said as he walked over quickly.
Long Yuehong watched the couple leave the Rec Center and couldn’t help but sigh. “What should I do next?”
Shang Jianyao turned his head and looked at him in all seriousness. “A great cause awaits you.”
“…” Long Yuehong’s facial muscles twitched. “Speak human!”
Shang Jianyao smiled. “Wait for next year’s marriage assignment.”
“That’s true.” Long Yuehong sighed. “Sigh, forget it. I hope I can get assigned a good position in the company tomorrow. Also, I feel like you’re becoming more and more abnormal. I’m referring to your brain.” As he spoke, he pointed at his temple.
The most important thing for them next was to wait for the allocation of jobs. This would directly determine their future. Apart from those who had special talents in certain areas and were earmarked by corresponding departments, the rest of the graduates with higher education qualifications had to wait for the job allocation.
Before Shang Jianyao could reply, Long Yuehong saw Chen Xianyu, the PIC of the Rec Center, turn off the display. He held a cylindrical metal cup that had been excavated from the Old World’s ruins as he slowly walked over.
Long Yuehong asked nervously, “Grandpa Chen, what department do you think we will be assigned to?”
Chen Xianyu coughed. “As far as I know, those who have just gotten married and are about to have children will be assigned to relatively safe internal positions. Those who have not been assigned a partner or won’t have any need for children might get assigned to temporary positions that might be a little dangerous.”
Long Yuehong’s expression collapsed. “I-I have to go back and tell my father and mother regarding my marriage assignment’s results.” He didn’t wait for Shang Jiyao’s response. With a gloomy expression, he walked out of the Rec Center.
“Your dad and mom haven’t gotten off work yet…” Shang Jianyao muttered to himself before leaving and entering the corridor outside.
This was the underground building’s 495th floor. There was no sky, only a four-meter-high ceiling. Long light tubes were mounted on the ceiling, and relatively bright light shone down.
To the employees of the company, the switched-on lights represented day. When switched off, they represented night.
Shang Jianyao looked up at the street lamp in front of him before making a turn to enter another area in Zone C.
On both sides of the path, the rooms closely neighboured one another—about only two meters apart. They resembled beehives in textbooks that had been projected onto the same plane.
Compared to them, the Rec Center was as spacious as a square.
After walking down two ‘streets,’ a relatively open area appeared in front of Shang Jianyao, where 12 elevators were installed.
These were the elevators that led straight to the Research Zone.
In this underground building that originated from the Old World, the elevators that led to the Factory Zone, Research Zone, and the relatively small but special Indoor Ecosystem Zone from the Residential Zone were separated in order to prevent congestion and accidents. They were located in different zones of the building.
The Administrative Zone and Energy Zone were combined with the Research Zone. Authorized personnel could only reach them by swiping their electronic cards.
Shang Jianyao waited for a while before entering the elevator in the middle. He casually pressed the number ’21.’
As it was working hours, the elevator did not stop midway. It steadily descended all the way.
During this process, Shang Jianyao suddenly took out an electronic card and swiped at the corresponding area. He then pressed the metal button representing the third floor.
The elevator continued to descend, only stopping after a while.
Shang Jianyao exited the elevator and made a left turn. He saw a large metal door that was tightly shut. There were four armed security guards wearing bionic armor, which made them look like lizards.
Shang Jianyao did not attempt to approach the metal door. He walked along the aisle outside the door and went right.
Several rooms were lined up at the end of the corridor, but none of them had door signs.
Under the ceiling lamps’ illumination, Shang Jianyao knocked on the door in the corner.
“Please come in.” A gentle female voice was heard.
Shang Jianyao turned the doorknob and pushed open the door. He saw a lady in a white coat.
The lady sat behind a mahogany table. She looked to be in her thirties and wore a pair of gold-rimmed glasses. Her hair was neatly bundled up, with only a few stray strands hanging down.
“Ah, it’s you.” The lady glanced at Shang Jianyao and smiled as she pointed at the chair opposite the table. “Have a seat.”
Shang Jianyao sat down and smiled as if he was returning home. “Good afternoon, Dr. Lin.”
“Good afternoon, Jianyao.” Dr. Lin tucked her stray hair strands away and took a folder from the side before opening it. Then, she twirled the black fountain pen and casually asked, “How are you feeling lately?”
“My appetite has increased a little. My sleep has been normal, and I’ve been healthy.” Shang Jianyao spoke as he made a move to highlight his biceps.
Dr. Lin nodded. “I’ve already applied for you to give up the marriage assignment. I guess you know of the outcome?”
“Yes, thank you.” Shang Jianyao smiled and said, “Can I sing a song to thank you?”
“There’s no need.” Dr. Lin shook her head without hesitation. She then tapped her pen. “Actually, I’m curious. Why did you insist on giving up the central marriage assignment? Your condition isn’t really serious.”
Shang Jianyao’s expression turned serious, and he said in a deep voice, “To save all of humanity.”
“…” Dr. Lin picked up her pen and drew a circle on the document in front of her.
There was a line of words in the circle: “Moderate psychosis (suspected delusional disorder, awaiting observation).”', 'published','approved',0,'2025-10-29','2025-10-29'),
(3,3,3,N'New Dawn',N'Translator:virtual groupEditor:virtual group
After Dr. Lin finished circling, she raised her fountain pen and looked at Shang Jianyao. She chuckled and said, “This seems to be derived from the Salvation Army’s slogan?”
Shang Jianyao tersely agreed and said seriously, “Dr. Lin, I think you have some misunderstandings about my condition, treating normal things as evidence of an illness.”
Dr. Lin straightened her body, and a smile surfaced on her fair face. “What misunderstandings do you think exist?”
Shang Jianyao fell silent for two to three seconds as though he was organizing his words. “You can’t understand this kind of pure and noble sentiment or have any idea what it means to be a person who has broken away from vulgar interests.”
Dr. Lin pursed her lips tightly as if she was spending tons of effort to stop herself from laughing. She nudged her gold-rimmed glasses up the bridge of her nose, breathed in slightly, and slowly exhaled. “Indeed. In this era, there is no room for idealists. Even the Salvation Army has degenerated.”
The doctor paused and said, “I can try to understand you, but you have to tell me how you came up with such thoughts. What made you have such urges?”
“Nothing. This is what I believe.” Shang Jianyao sighed and smiled. “Dr. Lin, you’re the most gentle and elegant woman I’ve ever met. I have something to tell you.”
Dr. Lin’s eyebrows twitched slightly. “I have…”
Before she could finish her sentence, Shang Jianyao added, “I imagined that you could be my spiritual mother, but I’ve just realized that our ideas are in two completely different worlds. What a shame.”
Dr. Lin broke out into a fit of coughs as though she had choked on her saliva. She then picked up the porcelain cup beside her and drank two mouthfuls. She randomly brought up a topic and muttered to herself, “Sigh, I’ve finished all the allocated tea leaves this month.”
Without waiting for Shang Jianyao to speak, she lowered her voice and asked mysteriously, “Have you recently heard any voices that no one else can hear? Or seen anything that no one else can see?”
Shang Jianyao shook his head firmly. “No.”
Dr. Lin observed Shang Jianyao’s expression for a few seconds before asking about something else.
After more than ten minutes, a sweet female voice sounded on all of the underground building’s floor simultaneously. “Here is the announcement of the time. The time now is 6 p.m.”
“The time has been broadcast.” After the voice repeated the announcement three times and stopped, Dr. Lin rubbed her eyebrows and said, “Let’s call it a day.”
She thought for a moment and said, “Since there’s nothing wrong with your sleep, and you haven’t seen anything that others can’t see, I won’t prescribe any medicine. Come back for a follow-up next week.”
“Alright, Dr. Lin.” Shang Jianyao stood up and walked towards the door. After he opened the door, he suddenly turned around and said, “Thank you, Dr. Lin.”
Dr. Lin replied with a smile, “You’re welcome.”
After Shang Jianyao left and carefully closed the door, Dr. Lin sighed and spoke to herself with a smile. “How polite.”
As she sighed, she picked up the folder on the table and flipped through the records:
“Name: Shang Jianyao.
“Age: 21 years old.
“Birthdate: 8th September, Year 25 (New Calendar).
“Family situation: Father, Shang Shi’an, was a D7 level employee. He went missing with the entire ‘Old Task Force’ in Year 37 of the New Calendar. Mother, Zhang Ruxin, was a normal D3 employee—a primary school teacher. She passed away in October of Year 40 of the New Calendar. The cause of her illness is suspected to be excessive grief. From October 40 to September 43, Shang Jianyao grew up in an orphanage on the 495th floor before being admitted into the university’s electronics department.
“Situation description: In May 46, Shang Jianyao voluntarily applied to become a confidential experiment volunteer and participated in the C-14 project. His reason for doing this was his hope to obtain great strength and investigate the truth behind his father’s disappearance.
“Experimental result: Failure. He didn’t undergo any changes compared to the control group.
“Complications: Logic confusion that resulted in him jumping to conclusions to a certain extent. There are no other abnormalities.
“Additional matters: Genetic results are normal…
“Comprehensive judgment: Moderate psychosis (suspected delusional disorder, awaiting observation)…”
Dr. Lin read for a while before jotting down: “Outcome of review dated July 10, 46 (New Calendar): No symptomatic improvement, but no deterioration either. No violent tendencies or signs of aggression. He can be considered temporarily harmless.”
…
Six in the evening was the company’s designated knock-off time. Apart from the specific project teams that needed to work overtime and some jobs that had 24-hour shifts, all the employees would leave the Administrative Zone on the 5th floor, the Research Zone between the 6th to 45th floors, the Factory Zone (and Maintenance Zone) between the 46th to 145th floor, the Indoor Ecosystem Zone between the 146th to 345th floor, and return to the Residential Zone on the 300th floor.
Due to the limited energy quota and the fact that both husband and wife—as well as the elders at home—might be working, many employees chose to eat at the Supplies Allocation Market on each floor.
This place was divided into two areas. One area was supplied with sweet potatoes, potatoes, rice, flour, meat, vegetables, and fruits from the Indoor Ecosystem Zone, as well as cloth, sugar, salt, and other supplies from the Factory Zone. The other area provided all kinds of cooked food, which was affectionately known as the ‘staff cafeteria.’
The cost of dining in the cafeteria was higher than the cost of cooking at home, and it didn’t taste great. However, with the energy quota that everyone sorely lacked and the fatigue from a day’s work, it seemed to be a better choice.
This was also promoted by the company’s higher-ups—they hoped to reduce energy consumption by providing food in a centralized manner.
When Shang Jianyao returned to the 495th floor, there was still about 20 minutes before the cafeteria opened at 6:30 p.m. As certain jobs needed the employees to wash up, be sterilized, or undergo other necessary things after work, the board of directors stipulated that the cafeteria would open half an hour after work to ensure fairness.
For the employees who returned to their respective floors before 6:15, the Rec Center next to the Supplies Allocation Market was the best place to while the excess time away. People could gather together and chat about life, work, and other things under the lamps. This also gave them a clear sense of superiority when compared to the people struggling to survive outside.
Some of the employees also seized the time to sell things that they no longer needed at home in exchange for more contribution points. Therefore, small bazaars would appear in Rec Center’s hall every night from 6:00 to 6:30 and 7:00 to 8:30.
As soon as Shang Jianyao walked in, he saw the Rec Center’s PIC, Chen Xianyu, sitting on a small stool that creaked from time to time. In front of him was a pile of strange items.
“What is this?” Shang Jianyao squatted down and picked up a rectangular object with a metal shell and a black screen.
“Who knows? It’s quite sturdy and can be used to smash people or be used for bulletproofing.” Chen Xianyu poked his chest.
“Where did it come from?” Shang Jianyao asked while fiddling with it.
Chen Xianyu cleared his throat and said, “From my comrade’s youngest son. He’s currently in the Security Department. He just came back from an Old World city ruin. Sigh, time really flies. I was there when he was born and watched him grow up…”
Chen Xianyou smiled and added, “Anyway, it was screened. The company doesn’t need it, so he didn’t have to turn it in. Thus, he got me to sell it on his behalf. As you know, I don’t have to go to the cafeteria. Somebody will get me food.”
He had many employees under him.
Shang Jianyao looked at the spider web-like cracks on the black screen and said after some thought, “How much?”
“It’s not expensive at all, 500 points.” Chen Xianyu casually offered a price.
Shang Jianyao slowly put down the item and mumbled, “That’s ten pounds of meat.”
The moment ‘meat’ was mentioned, he and Chen Xianyu gulped at the same time.
Shang Jianyao swept his gaze and picked up another item. “Is this a watch?”
“Yes, it’s a watch. It has a complicated mechanical structure. It can still be used even now. You just need to adjust it a little.” Chen Xianyu’s eyes lit up. “What do you think of it? Do you want to consider buying it? Its needle and time markings will glow at night. You don’t need to turn on a flashlight to see it clearly. I’m telling you, there are no more than 100 people in the entire company who have a good watch. If you have it, you don’t need to rely on the radio anymore or come here to look at the clock to determine the time. You will become the object of envy of all the residents on your floor. There might be young ladies who will take the initiative to date you…”
The silver watch in Shang Jianyao’s hand had many cracks on it and was covered in rust. On the emerald-green watch plate, the second hand was ticking with glass shards everywhere.
“How much?” Shang Jianyao asked calmly.
Chen Xianyu paused for a moment before saying, “Sixty thousand.”
Shang Jianyao quickly put down the watch as if it scalded him.
With a monthly salary of 1,800 points for D1 employees, it would take almost three years of not eating or drinking for one to save up that amount.
Chen Xianyu didn’t expect Shang Jianyao to buy it. He was only joking with the young man. He then pointed at the pile of cylindrical metal containers in the middle and said, “Do you want to buy this? It’s good stuff—military canned food!”
Shang Jianyao picked up a can and saw that the plastic film outside was already tattered. The labels were extremely blurry, and only the words ‘Braised Beef’ and ‘500g’ could be vaguely seen.
“How about it? Doesn’t it feel heavy? This means that it is filled with good stuff!” said Chen Xianyu, his saliva splattering everywhere. “Hear me out. This military canned food is extremely delicious. It is a delicacy that I will never forget in my life. It is much better than the shrunken canned food from the Salvation Army!
“If it weren’t for the fact that my comrade’s child dug up an entire carton, you might never have a chance of eating it. As for the price, 60 contribution points each. Isn’t it very cheap? It will cost 50 points if you go to the Supplies Allocation Market to buy a pound of raw pork. Furthermore, it doesn’t contain any seasoning. Nobody will cook it for you, and you might not even be able to buy the meat! Also—ahem—after this can is finished, you can still give the metal shell to the Supply Management Department and get some contribution points in return. Isn’t it worth it?”
Shang Jianyao looked at the old man. When he finished speaking, he suddenly asked, “How long has it been since its expiry date?”
“Expiry date? How would I know? I don’t even know how to convert our years to the Old World’s years.” Old Chen’s eyes widened. “Anyway, the New Calendar is only at 46. It’s definitely edible.”
As he spoke, he revealed a reminiscing expression. “Back when I was in the Security Department, I went out on a mission and lost my supplies. I almost starved to death. Fortunately, I found a military warehouse and found canned food like these. Who knew how many years it had been since they expired. I still ate them, and they were fine. The taste was amazing.”', 'published','approved',0,'2025-10-30','2025-10-30'),
(3,6,4,N'Veins of Fire',N'A hero rises.', 'draft','approved',0,'2025-10-31','2025-10-31'),
(3,6,5,N'Eternal Flame',N'Hope endures.', 'published','approved',0,'2025-11-01','2025-11-01'),

-- Series 4: Goddess Medical Doctor
(4,8,1,N'Rebirth',N'The sky is bright, within the bamboo forest, there is a melodious song. Walking towards the end of the trail, a faint orchid scent seemed to permeate through the mist, one could vaguely see a figure of a young woman dressed in a snowy white dress, wearing a white belt embroidered with orchid tied around her waist playing the guzheng. Her long black hair flows smoothly down to the waist, shining faintly like satin. There are no ornaments in her hair, there was only a white silver headband fluttering in the wind. After the completion of one song, the sun has risen and the fog has gradually dispersed. The sun shines through the mist and onto the woman, her chest glows with a blue light.

She was scrutinizing what she was wearing on her chest, it was an orchid brooch, made of rare blue crystals. Looking closely, there was a faint orchid imprint on the young woman’s forehead, her phoenix eyes were narrow and deep, with a pale purple light. She had eyelashes that were long and thick, and a small upright nose that was accompanied by small cherry-like lips, her snow like cheeks showed a natural reddish appearance. Her exposed collar revealed a part of her jade-like neck, she had an expression of calmness. As beautiful as a goddess who could not be touched by a speck of dust.

Looking into the depths of the bamboo forest, she lightly said: “Master, was your disciple’s piece of 《Autumn Water》pleasing to the ears?”

《秋水》Qiūshuǐ: Autumn water, a song played on the guzheng, an instrument, it is a real song
After a burst of laughter, a middle-aged man with the aura of an immortal came out, “Not bad, Xin’er’s qin skills have gone a step further, even the music from the owner of the world’s number one qin, Huangfu Changtian, is not as good as you.”

Yue Xin’er’s lips slightly curled at the corners, there is an aura pressing down, causing people to be unable to open their eyes. “Master, you did not come to this bamboo forest to only listen to your disciple play the qin, right?”

Yun Zhong Zi sighed, “You little girl, you will soon be fifteen, during this time you should get ready to return to the palace to take a look. Your master really doesn’t understand you.”

She lowered her head, her eyes were red, the breeze blew bamboo leaves toward her body, making her look even more ethereal.

“Xin’er, master does not worry about your safety, you established the Crescent Moon Palace when you were ten years old, there are the seven gate keepers protecting you, even your master does not dare underestimate you. You also have medical skills, the title of ‘Goddess Medical Doctor’, you are full of talent. This time when you leave the mountains to travel the four seas, you must take care of yourself. Your master doesn’t have anything to gift to you other than this ‘mysterious book of heaven’, it is a gift from master to you. Remember ‘Do not be happy with objects, do not be sad.’ Then Yun Zhong Zi took out a yellow book from his sleeves and gave it to her.

新月宮 Xīn yuègōng: Crescent Moon Palace, or New Moon Palace; a sect of some sorts
玄妙天書 Mysterious/mystical book of heaven; most likely a martial arts manual
“Thank you, master.” Yue Xin’er showed her gratitude, she was curious about the contents of this mysterious book.

Yun Zhong Zi couldn’t help but laugh when he saw her whole heartily immersed in the mystical book of heaven. This little girl, she obviously still has a child’s nature, for the sake of the world, he made her the way she was, he did not know whether it was by luck or unfortunate.

Since thirteen years ago, when they had ascended the mountains Yun Zhong Zi had her go through self-cultivation. For a two-year-old doll-like girl, it is natural for her to be active. It was cruel for her to calm down and learn how to cultivate her body. He often saw the small figure of Yue Xin’er, she was a child, but stubbornly refused to bow to him. Sometimes he saw her longing eyes looking toward the path that led down the mountain, he secretly thought to himself that if she had begged him, perhaps he would take her down the mountains for the lantern festival. But she never asked for anything, only wishing to go pick herbs, she silently carried the medicine and went to the Qiqiu Valley, the place that makes the people of the jianghu eager to go. After returning, she had gained a little white tiger. When she was nine years old, she was very alluring, just one gaze of her’s could take away a man’s heart and mind. She was renowned with Zheng Sulan who was sixteen at the time they were known as “The duo of medicine gods”.

七絕穀 Qiqiu Valley: Very dangerous, but people still go there for a chance to gain power
江湖 Jiānghú: Rivers and lakes; the pugilistic world in the Wuxia/martial arts genre
鄭素蘭 Zheng Sulan: a name, Zheng being the surname, Su meaning plain, and Lan mean orchid
As he expected, her temper was cold as jade but not as cold as ice, she just treated everything with indifference. This is the realm he wanted her to be in —— the body as water, the heart as water. When she was ten, she descended the mountain with the seven boys and girls that she had carefully cultivated with and established the largest sect in the jianghu, the crescent moon palace. But no matter what, he never saw her with any expressions. Looking at those eyes that haven’t had any ripples in them for a long time other than due to the mysterious book of heaven, he did not know if he was in the right or in the wrong. This girl that only feels sadness and pity, he is afraid that everything will be out of control when she leaves the mountains!

If Yun Zhong Zi did not feel the fluctuations of the heart and sit quietly to look through the mysterious book of heaven. She didn’t know that her master’s heart was turning with such thoughts, she couldn’t blame him, as she didn’t have a choice, she also knew that master did it for her own good. She never asked him to take her down the mountains to play because she knew she had an extremely important identity, although she was a child, her mind had already surpassed mortal bounds. She knew that master had pity her and wanted to spoil her, but she could not let herself go and let herself relaxed, because she was responsible for the rise and fall of the country, and imperial father had already passed down her edict to seal her up as a goddess.

She is afraid that she will have to govern the country together with her imperial brother. But she does not wish to! If she was an ordinary woman, she does not have to bear so much responsibility! At the age of six, Master let her go to Qiqiu Valley, perhaps she was hesitant in her heart, after all, it was pernicious all year round, it was poisonous everywhere, even the birds did not dare to fly in. Many of the elixirs that people of jianghu dreamed of had all come out of this place. For centuries, the people who entered and were able to come out have been few in number. But she still agreed without wrinkling her eyebrows, at that time, she felt that master’s heart had been hit by a storm, he merely wanted her to beg for mercy, in his heart he already had a place for her.

After returning, she was followed by a white tiger that she saved in Qiqiu Valley, the little guy had strayed into the valley and gotten poisoned. Luckily, Yue Xin’er was there and he had survived. She also didn’t know why she was able to come back safely, maybe there was a mysterious force protecting her! Before she was ten years old, her master was always urging her to go down the mountains, to treat people’s illnesses. Due to her age, the people did not dare to let her diagnose them, but soon, when she cured the village chief’s persistent illness, one after another, the villagers began to seek medical advice from her.

After a small experiment with the knife, she did not want to cure a few minor illnesses, she then singled out incurable diseases and later learned to make poison and detoxification. During this time, she saved seven orphans that were older than her, healed them and took them to the mountains after curing their poisons. The seven people had their gratitude for her life-saving grace, and are determined to repay her. After hearing their sincerity she gave them a secret martial arts manual and also taught them different techniques when she was ten years old she let them leave the mountain and tasked them with establishing the Crescent Moon palace. The imperial palace was too far away, and she was often unable to handle its affairs, but the matters of jianghu, she allowed her older brothers to manage it.

Originally, she did not have much expectations for the seven people, who knows it was unintentional. Today’s Crescent moon palace is held in regard as the world’s number one sect for both the black and white sides of the jianghu, its forces have been extended to officials and businessmen, and casually rubbing a foot can cause the entire Jingyue Dynasty to tremble. She does not like politics but she was dragged into this circle because of her brother’s words, today more than half of the military power is in their hands, if she had the ambition to become the emperor, even her imperial elder brother will not wrinkle his brow, and give the throne to her! In addition, the founding emperor of the Jingyue Dynasty, Goddess of the Moon was a woman, there was no worry about women interfering with politics.

Black and white sides; Black being the dark/evil side of jianghu, and white being the light/good side of jianghu
靜月王朝 Jìng yuè wángcháo: Jingyue Imperial Dynasty; Jing means quiet/calm/tranquil, and Yue means moon
Some time ago, older sister Yue Ling left the palace, she left after taking care of the matter with brother-in-law in the cold northern dessert. I had already guessed that my older brother would not promise Yue Ling to him, so he left a letter to sister Yi, Zheng Sulan’s sister-in-law, threatening the Emperor that if he does not agree, he would hide the armory in the mountains. When the emperor heard, he dared not disregard his beautiful and intelligent younger sister who held a lot of power, he then arranged the marriage, and his young sister was relieved.

月翎 Yuè líng: Moonlight; Name of Yue Xin’er’s older sister
This month, Yue Xin’er is about to come of age, as the goddess of a country, her status is equal to that of an Emperor, how can it not be held in high regard? Therefore, when she returned to the palace, she was dragged for measuring her dress size, and she was asked what jewelry she preferred, when she shook her head and refused, she terrified a group of people, thinking that she was dissatisfied, they kneeled and begged for their lives.

When Zheng Sulan came, she saw that the Heart Moon Palace was in disarray, and the culprit is standing aside with a slight frown. Everyone saw the Empress and bowed, calling: “Greetings to the Empress, may your majesty live for thousands and thousands of years.” She waved her hand and allowed them to get up, but no one dared to stand up, this couldn’t help but make her feel weird. “Xin’er, what is going on?”

“I am also unsure, you should ask them!?”

When the Empress could not get a word from the girl’s mouth, she knew that this girl was never concerned about anything, so she turned around and asked those in the room.

Among them, there was a pretty maiden, whose status was not low replied: “Replying to Empress, these slaves were preparing for the princess’s grand ceremony, we presented a lot of jewelry, but the princess is not satisfied. This slave thought princess wanted to punish these slaves, we do not dare…”

One could see that Yue Xin’er’s eyebrows wrinkled even more, but there was no expression that could be seen beneath that veiled face. The Empress saw that Yue Xin’er refuse to speak, she had to explain, “You all misunderstood. The little princess did not want to punish you. She is dissatisfied because she never wears any jewelry besides ‘Fragrant Orchid of the Secluded Valley’ You guys think it over if that is the case?”

Those people listened, and they were bold enough to look at her. There was nothing other than the brochette worn on her chest, even her hair was just as soft as silver silk satin.

“Good, now that you have seen enough, get up! If this spreads, people will say that the Princess place blame on the servants.” Zheng Sulan pretended to be angry, and those people got up. Then said to Yue Xin’er: “Xin’er, the grand ceremony is different from other festivals, it only happens once in a lifetime, can you stand before the officials, or pick some jewelry that you like!”

Yue Xin’er thought for a moment, and nodded “When the time comes, I’ll make my own decisions, take these items away! At the time of the ceremony, the family’s dignity will not be lost.”

Seeing that she had promised, the Empress no longer forced her, knowing that she had the innate knowledge and that these common items could not enter her eyes.

When it comes to this righteous sister, she has never seen her true face, every time she sees her, she would either be wearing a veil or a human skin mask. The reason she can be with the Emperor is because of her, it can be said that she was their matchmaker. Because Yue Xin’er often cultivates in mount Jinyun, they rarely see each other, it was due to an accident that they became sisters, later, because of this relationship that she successfully climbed to this relation, the relationship between the two people became more intimate. She could never see through her, even her brother, sister, and master couldn’t understand what she was thinking. She could be considered fortunate, generally the children of the imperial are to be married, however, because of her status, no one even dares to mention engagements before her, could such a goddess be someone that a mortal would be able to match?

“Huang Sao, why don’t you return first, otherwise imperial brother will come here for you.”

皇嫂 Huáng sǎo: Huang means imperial, sao means elder brother’s wife; Imperial sister in law
She got an answer that was laced with blame, “You little girl, you still have the mind to ridicule me, next time I will bring Yu’er to see you, you should make preparations!”

煜 Yù: a name, it means brilliant/glorious
Yu’er? Yue Xin’er thought it had been a long time since she saw him, she really wants to see him sooner.

', 'published','approved',0,'2025-10-30','2025-10-30'),
(4,8,2,N'Grand Ceremony.',N'According to the traditions of the Jingyue Kingdom, a royal lady’s coming of age ceremony will be held at the Temple of Heaven. Because Yue Xin’er is a goddess, she must be at the altar. The altar can only be used by goddesses of the past, in addition to holding the ceremony outside, there would be stargazing, divination and worship of ancestors. This is a supreme honor for a woman, and also reflects the woman’s status in the Jingyue Kingdom, although it is not a female dominated society, both men and women are considered equal in terms of status.

To be honest, if her identity was not related to the fate of the world, she would not be willing to appear in front of the people, let alone on her fifteenth birthday. After completing the cumbersome ceremonial rituals, she stepped down from the altar, there was still a feeling of dazzling, which made her feel the heavy responsibility on her shoulders, she felt helpless.

The clothing on her body was extremely decorated and full of bright colors when she walked past there was a lingering fragrance left behind. Wealth is not my wish, but this feeling will be used as “A full man does not know the hungry man during a famine.” Still covered with a veil, which covered the appearance of inconvenience, but it could not cover her attitude as if she was not of the mundane world.

飽汗不知餓漢饑: A full man does not know the hungry man during a famine; when the other perspective is not worried about or cared for
She was about to go to Yuehui Palace when she was interrupted by a palace maid. The palace maid bowed, and said: “Princess, the Emperor wants you to go to Yuelan Palace right away.”

“Oh? What’s the matter?” Yue Xin’er raises her eyebrows, why did he need to use the two words ‘right away’? Could it be the sky is falling today?

Could it be the sky is falling today: means not usual, kind of similar to the sun rising from the west; it is an expression
“This slave does not know, Princess, please come with this slave to Yuelan Palace.” The palace maid never lifted her head from beginning to end, with a humble appearance.

She glanced at this palace maid, who she had never seen before, the corners of her mouth bent into a distinctive curve, “Then, you lead the way!”

The palace maid turns and leads the way in front, Yue Xin’er follows behind neither too slow nor too fast, she suddenly asks: “What time is it now?”

“Replying to Princess, it is Shen shi.”

申時 Shēn shí: 4 pm to 6 pm, an ancient Chinese measurement of time
“Oh, is it so?” She thought for a moment and stopped, “I don’t have time to play with you.”

The palace maid pauses, her body stiffens, she puts on an unsightly smile, “This slave does not understand Princess’ meaning.”

Yue Xin’er also gives a smile as her eyes brighten, “You really don’t understand? Brother-in-law’s junior sister, Wen Meixia, miss Wen.”

溫美霞 Wen Meixia: Her older sister’s husband’s junior sister
“How did you know I was Wen Meixia?” Since she has been exposed, there is no need to continue pretending.

“I need you to know, not only in medicine, my disguise skills are also number one in the world, up till now, no one has been able to recognize the people I have disguised. And you, I knew your identity the moment you approached me.”

“Haha, I was seen through by you! I originally thought to kill you to avenge my father, now…”

There was a smile on her face, and sarcasm in her eyes “You really think you can kill me? I originally promised your father if you did not seek revenge I would spare your life, and now you’ve come into the palace to assassinate me.” The silver bells from her bracelet on her wrist rang a few times, and in the blink of an eye, two people had appeared. “Zi Yuan, you take care of this. Qing Niao, follow me to Yuehui palace.” They immediately left, leaving Zi Yuan and Wen Meixia who were prepared for battle.

In the time of less than an incense stick, Zi Yuan returned to Yue Xin’er’s side. “Palace Lord, the matter has been taken care of.”

Palace Lord (宫主) as in the master of Crescent Moon palace, and not the imperial palace (公主), even though both are pronounced as Gong Zhu
“Bury her next to her father! It also counts as her filial piety.”

Filial piety: Respect for one’s parents and elders, derived from Confucian beliefs
“Yes, Palace Lord.” Zi Yuan was about to retreat, but was stopped by Yue Xin’er, “You stay here, let Qing Niao take care of this matter!”

Qing Niao left, leaving Zi Yuan and her alone, she did not stop but continued to walk to Yuehui Palace, because she had the servants retreat, she was not worried about causing the people to panic.

“Are you wondering why I didn’t let you go to take care of the aftermath?”

“This subordinate doesn’t dare.” Zi Yuan bows respectfully.

“No need to do this as such. I let you kill her because Qing Niao is a man, he is not suitable to kill a woman, this type of responsibility is something a man does not wish to take responsibility for.” Yue Xin’er explained expressionlessly, but it made Zi Yuan feel gratitude.

When they arrived at Yuehui Palace, the sky was already dark, but inside Yuehui Palace it was brightly lit, like a white pagoda. Once she entered, there was a pair of noble and elegant couples sitting in the grand hall, along with Yu Yuefeng and Yue Ling, a husband, and wife.

“Er’chen greets imperial father, mother.” Before she looked up, she had already been embraced by a middle-aged lady.

“Haotian, look, Xin’er has grown up. Look at this snow white skin. This charming figure is three points better than Xi Shi.”

Xi Shi, is one of the four beauties of ancient China, she lived during the spring and autumn period, caused the downfall of an empire (State of Wu), she is said to be so beautiful that fish would forget to swim
“Okay, Qing’er, you have yet to see Xin’er’s face, if outsiders heard that, they would laugh, saying ‘Auntie Wang sells melons, boasting what you’re selling’.” said the middle-aged man, who was Yue Xin’er’s father.

Auntie Wang sells melons, boasting what you’re selling: Means bragging; in this case, bragging about the beauty of one’s own daughter
Feng Yuqing did not agree, “What do you mean, I can be said to be a beauty ah, how much worse can my daughter be? What’s more, when Xin’er was little she was like a little doll, do you really think she would grow up to be ugly? Who would dare say my Xin’er is ugly, I will be anxious!” Finished speaking, she gave Yue Haotian an angry glare.

月昊天 Yuè hào tiān: Yue Xin’er’s father; Grand Emperor
鳳玉青 Fèng yùqīng: Yue Xin’er’s mother; Grand Empress
“Fine, consider it my wrong!” Yue Haotian said helplessly.

“Hump, you are the one who is wrong!” For Yue Haotian, this wife of his is very laughable at times, in his life it could be considered very joyful. Those from the Yue family line could be said to stay true to one person, in this lifetime he could have such a wife, that gave him a son, and two daughters, life as such, what can a husband ask for?

Yue Xin’er was standing with Yue Ling, chatting with each other, she was too lazy to deal with the bickering couple, playing tricks all day, and yet they do not get annoyed.

“My precious Xin’er, take down your veil and let imperial mother see!” Feng Yuqing’s voice caused Yue Xin’er and the people in the room to feel goosebumps creeping up all over their bodies.

Taking a few steps back for safety, she really does not know how imperial father endured her for so long. “Imperial mother, this…” Yue Xin’er said with some embarrassment.

Feng Yuqing looked at her with a puzzled look. “What is it?” Is it that she, as a mother cannot see her own daughter’s appearance?

Seeing Xin’er’s face showing some trouble, Yue Tianhao said to her: “Qing’er, have you forgotten? Before Xin’er can find her true love, she cannot let anyone other than Yun Zhongzi see her true appearance?”

It dawned on her that when Yun Zhongzi said that Xin’er was a goddess, she shouldered the rise and fall of the country, so she was brought to the mountains for education. It was also said that after she was ten years old, she would not be able to show her true face, unless she finds one who is destined to be with her, otherwise, there will be a big calamity. “Xin’er, you don’t have to worry, let’s go eat.”

For a time, the atmosphere of Yuehui palace was very good. Who said that the royal family was heartless?

It was almost Xu shi by the time she returned to Yuexin Palace, she was followed by a few servants who were holding gifts from her loved ones in their hands. There was a variety of gifts, including Nanhai Black Pearl, Donghai Red Coral, Tianshan snow lotus, and the Millennium He Shou Wu. The tables and cabinets in the palace are valuable, they were gifts from ministers and friends, as well as treasures sent from other countries, just seeing it made people dazzled.

Xu shi: time, 8pm-10pm [Nanhai: South Ocean/Sea], [Donghai: East Ocean/Sea], [Tianshan: Sky Mountain]
Suddenly, an item on the eastern footer attracted her attention. It was a box carved with Lan Tian jade, on the surface of the box was a pattern of a pair of dragons playing with beads, the box was covered, and a double heart lock hangs outside, carefully locking the box. Yue Xin’er smiled, such a refined box should have been sent by the Tianxiang Kingdom? “Whose gift is this from?”

A palace maid answered: “Replying to Princess, it was sent by the grand prince of Tianxiang Kingdom.”

“Oh.” As expected, the Tianxiang Kingdom and the Jingyue Kingdom are two neighboring countries and have maintained friendly relations with each other. The current Empress of Tianxiang Kingdom is her aunt, although this grand prince is not born from her aunt, according to relations he is her cousin! Opening the double heart clasp, there is a pair of hollow carved crystal balls, one large, about four centimeters in diameter, there is a pink pearl inside, there are many small hollow holes in the ball. Yue Xin’er’s eyes brightened, this is suitable for her own weapon, she took a white ribbon from her sleeve and pushed it with internal force, the ball gave off a sweet clink sound. Satisfied with a smile, she took the ribbon back into her sleeve, when it is empty she could put medicine into it, but until then it would be an incredible weapon. Looking at the ball again, the shape and structure of the ball is almost the same as that of the big ball, the diameter is about two centimeters, inside the ball it was purple, after careful observation, it was found that inside the sphere with a diameter of one centimeter, it contained a white orchid, there was a lingering fragrance of orchid surrounded by small spheres. The ball was strung on a silver chain, the chain did not seem to be made of silver, it was more like platinum.

“Shan’er, come and help me put it on.”

Shan’er next to her carefully took the sphere and put it on her neck, at that moment, her eyes were dazzled.

“Princess, you look so beautiful.” The people praised with sincerity, they had thought that the Empress and Princess Yue Ling were beautiful, although they had not seen the true face of the little Princess, her elegance and her charm than none could resist were deeply convincing.

Yue Xin’er’s face under the veil became slightly red from embarrassment: “It is late, you all retreat to rest first!”

“But Princess, you have not yet rested, how can this slave go first?” Shan’er said.

“No need, I have to stay late for a while.You go ahead, I want to be alone for a while.”

“Yes, Princess, this slave will retreat.” Shan’er took a group of people out and closed the door.

Seeing that everyone left, Yue Xin’er looked at other gifts, almost all of them were jewels, only the gifts from the seven main gates can enter her eyes.

Sitting on the couch, she touched the sphere around her neck and stared out the window. It was already late at night, the palace should have had many guards patrolling because she had always been a light sleeper, she worried she would be awakened by their footsteps, so she gave an order for them to keep their distance from her palace.

Strange, this grand prince has no connection to her, and is not really her cousin, why would he give her such a precious gift? These two balls are not just simple ornaments. It is said that they are two teardrops a heavenly fairy had shed for the world, they landed in Kunming Lake and were later acquired by a craftsman, who made them into what they are now. The world only knows that this object is priceless, but does not know of its use. The large one is Bing Xin pearl, the small one is called Lei Lan pearl, right now both items are in her hands.

冰心珠 Bing Xin Zhu: Bing means ice, Xin means heart
淚蘭珠 Lei Lan Zhu: Lei means tears, Lan means orchid
After thinking about it, she did not have a clue. He gave it to her as a present, she accepted, would he want it back if she does not respond?
', 'draft','approved',0,'2025-10-31','2025-10-31'),
(4,8,3,N'The Court Conspiracy',N'On this day, the sky was clear, the three children of the Yue family along with the Grand Emperor and Grand Empress went to the imperial garden, together. It was currently springtime, flowers were at the peak of bloom while the bees and butterflies are busy gathering nectar. Yue Xin’er took out a small porcelain bottle to gather the flower’s dew, while the others sat in the pavilion savoring Yue Xin’er’s original ‘Moon wind floss’. The dew was from the first day of spring, Yue Xin’er would use it for refining to make hundred flowers wine. The undisturbed appearance of the dust is a little thin in face of the sun, it feels like the sun’s rays and a gust of wind could easily blow it away. This was a pleasant morning, but such peace was broken due to the intrusion of a voice.

At a certain distance, a minister stood by a stone table, reporting a matter to Yue Feng. Yue Xin’er was able to see his frown. She handed the porcelain bottle to the maid next to her, allowing them to continue to talk among themselves, as she walked towards the stone table.

月楓 Yue Feng: Current Emperor, the son of the Grand Emperor who abdicated the throne, and is living a carefree life (He pushed everything onto his son)
“Imperial brother, what’s the matter?” Although Yue Xin’er did not like politics but seeing him so worried, she feared a major event such as a natural disaster has occurred!

“Xin’er, You came in time! The Luo River flooded the southern area and submerged it. It also caused a plague. In recent days, due to the attack of the Qing City, all the food and supplies were given to the army. Nowadays, the domestic food supply is insufficient. In addition, a number of doctors have been transferred to support the army. It is also lacking in medicine, with food shortages, and the lack of medical doctors ah! ”

As soon as she heard him, the beautiful lady frowns at him, what she then said was colder than ice, “This is great, you didn’t discuss with me and dared to send troops to Jing Lou Pass without authorization, as of now the state treasury is empty, where do you place the common people?”

Yue Feng wiped the cold sweat on his forehead, and gave a glance toward Yue Haotian as if begging for help while groaning in his heart, how could she still have more momentum than me? “That’s not it, you once said that you liked Qing City, I wanted to win it and give it to you as a birthday gift, who knew…”

“Is this an excuse you can use? Preposterous!” Yue Xin’er waved her sleeve, and the people around dare not speak. “Let it pass, I will deal with this matter, tomorrow I will go to Southern Wei, during this time you must deal with it properly, otherwise…”

Really did not have face, ah, this Emperor was really useless, when he heard her intimidation he dare not mess up. But this was also his willingness, after all, this little sister has been much better than Yue Ling, and with her help, these days have been much more comfortable.

After bidding farewell to her family, Yue Xin’er brought the four gatekeepers, Chi Wu, Cheng Feng, Zi Yuan, Qing Niao on the boat bound for Southern Wei. Only some herbs were on board, while the rest of the herb and food were taking the land route. She did not intend to bring too much, she prepared to use Crescent Moon Palace’s token to gather herbal supplies from the local medicinal shops after arriving at Southern Wei. Then let Chi Wu report to the godly doctor’s Gate, so that all the people under the Gate would rush to Southern Wei.

Godly doctor’s gate: also a sect, like Crescent moon palace, but filled with people who are adept in medicine
The sky was a bit gloomy and depressing, experienced sailors knew that a big storm was coming. Yue Xin’er stood on the bow of the boat, holding something in her hands while thinking, she gave orders to the four Gatekeepers. The sails have come down, and the seas are rough and swaying. A flash of lightning flashed over the horizon, and then only loud thunder was heard, heavy downpours began and the ship shook even more.

“Listen, if something happens, don’t panic. I will not die. You must go to Southern Wei according to your original plan. I will go to Ji Province to join you.” Yue Xin’er calmly stated that the storm did not worry her, she could not die so easily, her task has yet to be completed, the Heavens would not take her back so easily.

The four people looked at each other with a dignified face, they clasped their fists and said: “This subordinate obeys the orders, we will try our best to solve the difficulties of Southern Wei.”

As soon as their voice fell, a big wave hit, Yue Xin’er was hit by the wave and carried into the ocean, her figure soon disappeared, the remaining four finally managed to stabilize their bodies, and looked up to find that Yue Xin’er has disappeared. “Master, master…” At one time, the sea was full of shouts, accompanied by thunder and rain, the sounds eventually died down. Yue Xin’er was immersed in water for a long time, she began to swim hard, in the end, she got exhausted and fainted, drifting in the sea.

Chapter 3 Part 2

~

Originally she thought she had died, who knew that when she opened her eyes, she found that she was in a familiar cabin, similar to one she had lived before. Her entire body felt an unspeakable soreness, it seems this storm was really strong. Yue Xin’er was about to get up when the door suddenly opened, and a handsome young man came in with a bowl of medicine. Seeing that she was awake, the young man’s eyes curved like the bright moon, he quickly went to support her.

“Miss, you have awoken.” The man sat on the edge of the bed and smiled like a spring flower, as a child under the sun, it made a person’s heart warm up. His laugh felt like it had a hint of evil, with elegance.

“Who are you? DId you save me?” Yue Xin’er felt a bit strange, she felt that he was somehow familiar, but she could not think of where. Only his temperament, gave people a cynical feeling, but it is currently not showing.

The man said with a smile: “I am Mu Tianchen, I am the one who saved you” Then he glanced at her again, “But the clothes on your body were not changed by me.”

“You…” Yue Xin’er blushed, she pushed back the quilt to take a look, her clothes really have been changed, she then realized that the veil was no longer on her face. “No matter what, thank you, gongzi, for your life-saving grace.”

Mu Tianchen saw her blush, she was tender and pleasant, he stared at her for a while, it was a long time before he recovered, she was truly a stunning beauty! “I dare ask Miss’ name?”

Yue Xin’er looked at him with vigilance, although he was her savior, because of her special status, she would not allow herself to become a tool for others to threaten Jingyue Kingdom, therefore, she seldom told others her real name. After some consideration, she decided to tell him her real name, just that her surname is too special, she feared everyone would know her identity, so she only said her name was ‘Xin’er’. In her heart, she felt that Mu Tianchen would not hurt her, so she did not have her guard up around him.

The two talked for a while, Yue Xin’er knows that he is a merchant because he was on his way to the TianXiang Kingdom when he met with the storm, he saw her in the water and saved her, he also got someone to change her clothes. In fact, Yue Xin’er did not believe him, a merchant would not have the aura of a king, and his name reminds her of something she came across recently. However, since he refused to speak, then she did not ask, she believes that he has his own ideas! She did not tell him the truth as well. They could be considered equal. Only this appearance was actually seen by him, if something happened, she feared that he would not be able to place blame on her. So she asked him to find a veil to cover her face, but he rejected, and she had to tell her own difficulties to him. Even though he knew, he said: “It’s nonsense, you have been seen by me, and you want to cover your face, maybe it is better for me to be with you!”

After lying in bed for two days, her body finally recovered, she stretched and opened the door to walk out. It was noisy at the moment she opened the door. Everyone was shocked by her unique beauty, such god-like holiness, a white dress that looked like a fairy, a pair of crystal-like eyes, and an orchid imprint on her forehead, the whole person seems to be with a hint of charm with pure coldness. Her beauty could move a person’s heart and make a person feel as if they were in heaven, she seemed to have walked out of a painting.

Mu Tianchen felt regret, he should not have let her come out without wearing a veil, her shocking beauty would make everyone fall in love with her. At that moment, he felt that she would go with the wind and disappear from the world forever, so he hurriedly reached out and held her.

“Don’t go, Xin’er.” Mu Tianchen buried his head in her jade neck, as his lips whispered into her earlobe.

He never thought Yue Xin’er would push him on the board and take a few steps back to the side. Mu Tianchen did not expect Yue Xin’er to have such a reaction and was caught off guard. Looking at the beautiful woman again, her face was red, she looked at him with anger and annoyance. He couldn’t help but feel happy, this little girl really didn’t have a deep understanding of the world, seeing her shyness made one feel love and pity.

“Oh, Xin’er, I’ve fallen to death, are you trying to murder your husband?” Mu Tianchen turned his train of thought, he gave a look of being wronged to see how she would react, who knew she did not spare him a glance and walked back to her room. When Mu Tianchen saw her ignore him, he quickly got up, gave a wide smile and went in with her.

Yue Xin’er closed the door, leaving him outside, no matter how noisy he was she still refused to open the door, perhaps if he waited for a while, he would grow bored and leave. Thinking back, the scene of Mu Tianchen embracing her was on her mind, there was a strange feeling in her, it made her heart beat wildly and uncontrollably. Heavens, what happened to me? Did I my cultivation deviate to the demons? She closed her eyes, but the scene was now even clearer. She silently recited the《Jin Gang Sutra》, her mind becomes clearer, perhaps she should not get involved with him that much, after all, they got to know each other through a coincidence. Besides, it was imperative that they go to Southern We first, the people are awaiting salvation! She secretly made a decision, once they arrived in Southern Wei, she would part ways with him!

《Jin Gang Sutra》: Buddhist scripture used to calm the heart and mind when recited
Looking at Mu Tianchen, when he enters the room, he realizes that she has suddenly intruded on the beauty, and he is afraid she will be there to discipline him. When he thinks of the beauty’s faint scent, his heart couldn’t help but skip a beat. He was not the type of person to fall in love with beauty, it was just certain factors, and gestures that confused him, he truly hugged her involuntarily. It seems that her charm really cannot be underestimated, it is no wonder that people would feel respect towards her.

On the other side, because Yue Xin’er had gotten swept away by the wave, her followers were worried, they left a ship and continued to search for her on the sea, the other boats were taken towards Southern Wei, they also got Crescent Moon Palace to help search. Although they were worried about her safety when they thought about what she had said at the beginning, she had a love for the people of her Kingdom, she was ready to go to Southern Wei to help the victims of natural disaster.

', 'published','approved',0,'2025-11-01','2025-11-01'),
(4,8,4,N'Miracle Surgery',N'She saves a prince.', 'published','pending',0,'2025-11-02','2025-11-02'),
(4,8,5,N'The Cure',N'A cure for corruption.', 'published','pending',0,'2025-11-03','2025-11-03'),

-- Series 5: Infinite Mana in The Apocalypse
(5,10,1,N'The End Begins',N'As soon as I was back in my apartment, I took deep breaths and analyzed everything that had happened so far.

No numbers were showing for my [Focus], something I have never heard about before. Yet, I could still cast skills that require focus. I did not feel any exhaustion while casting F ranked skill over 500 times.

My goodness. Taking another deep breath, I continued thinking

The most important question now is, Do I just have an abnormal amount of Focus that surpasses most people, or something else? This was the biggest thing on my mind, as even the strongest B ranked Awakened individuals cannot cast skills that many times without exhausting themselves

Alright...with that in mind, I need to test just how many times I can continue to cast skills before I can’t anymore. Which also means I need to get my hands on higher-ranked skills that consume more energy

With that in mind, a smile bloomed on my face

Just the fact that I could cast an F ranked skill more than 500 times meant amazing things. As long as I am not braindead, I can easily clear an F and E ranked dungeons, even if I’m alone

D ranked dungeons might be a stretch, as the strength of monsters increase and I would need higher ranked skills besides fireball to take them down

But there’s no problem taking things slow. I pull out my phone and look at the remaining balance in my bank account

$5,867

Enough money for rent and food for a few months, and the remainder of my savings. I convinced myself to buy the F ranked skill book, [Fireball], for $20,000. After this, most of my inheritance money was now gone

A year ago, when the apocalypse began, my parents were not so lucky to survive. I was stuck in the bas.e.m.e.nt of my old house asleep and escaped the onslaught of monsters that were taken care of by the government and newly awakened people after a few days

As time passed, more information was released to the public. Monsters had appeared. Most of them congregated in Dungeons. If they were left alone, they would increase and something called dungeon break would occur. This caused monsters to leave the dungeon and indiscriminately attack everything on site

But never fear, Awakened people are here. Able to use powerful skills and great strength to protect everyone else...

And now, I was among those people too

The first order of business? Obtain an Awakened License that will allow me to enter dungeons. Without a license, you cannot bypass the security that allows the Awakened to clear dungeons

You can get a license fairly easily, as you just have to be examined and exhibit skills. I was itching to get started as soon as possible, so I grabbed my backpack and rushed downtown.

With the advent of monsters and heroes, many of the populations were condensed onto a few protected cities. Almost everything was close to each other, so the Awaken Center was not too far away

A few minutes and I was already there. On the way, I saw the usual people with sullen faces and occasional awakened individuals rushing about.

The Awakened Center was vibrating with life, as long lines of people visiting shops could be seen, with even more carefully selling monster [Cores] to the authorities

Many people chose to first consume the [Cores] and get stronger themselves, but many more also sold them to the government and other markets as they went for high prices

[Vitality Core]s and [Strength Core]s went for around a $1000, while [Focus] cores were almost double that price because of their demand

I moved my eyes to another section of the Center, which was dealing in the selling of Skill Books ranging from F to C. Obtaining a C ranked skill book was a dream for many people, as they were extremely rare and went for more than half a million dollars

Looking at the skill books that I greatly desired, I shook my head and moved towards the Registration Hall.

The receptionist was very welcoming, asking for my name, date of birth, address, and bank account information

As soon as she had everything down, I was told to head toward a certain door where my ability would be observed.

There were 3 people standing behind thick glass and an open area with testing dummies. After hearing the directions, I stood a few meters from the dummies and cast [Fireball] consecutively 8 times, making sure to look like I was struggling to get the last few out

"Wow.." (Judge 1)

"A peak F rank huh..." (Judge 2)

Drawing a few surprises from the observers behind the thick glass, everything was completed successfully and I received my license. It was a golden card similar to a credit card with my name, age, and rank. The surprise from the observers was most likely due to the fact that high ranked awakened are extremely rare, with there being only one B ranked awakened in this city that is known publicly

There a few tens of C ranked awakened, a few hundred D ranks, and thousands of E and F ranks. Most of the hunters were F ranked, so being at the peak of this rank at the starting point was a good thing by itself

One can slowly raise their rank by absorbing [Core]s, but unless you were in the army where they were provided for you by the government or in a rich household that could buy them in mass, you would rise very slowly

There were some slight changes to the panel on the corner of my eyes as I walked out of the Awakened Center and received my license.

[Noah] [Occupation: Hunter]

[Vitality: 10]

[Focus: - ]

[Strength: 10]

[Skill(s): (Fireball-8)]

A new occupation had appeared, marking me as an official awakened Hunter, and...Oh?

I was surprised because I didn’t see it before, but the proficiency of the [Fireball] skill had quickly risen to 8. The was an extremely fast process when it took people weeks to raise their proficiency with their skill

I guess it is understandable as I was able to cast the skill more than 500 times, and most people have to use their skills over a period of weeks to reach that number

My next course of action after getting my license was the nearest beginner dungeon in this city, [Ape’s Paradise]

Boasting 10 floors, each floor held 10-20 monsters, with the last floor housing a huge black Ape 3 meters in size. This dungeon was regularly cleared by parties of F and E ranked Hunters, who could take on the monsters and the boss without fear of casualties if everyone was doing their jobs

Parties consisting of 2 Knights who put their focus on their [Vitality], 2 berserkers with their focus on [Strength], and 2 mages with their focus on [Focus] were the most common for this dungeon

The Knights act as tanks to ward off coming monsters, Berserkers use their high strength to strike down and protect the mages that are the brunt of the firepower to kill everything on site

Equipment from dungeons was extremely rare, so most people had to make do with newly designed machetes or metal pipes and iron shields as their first equipment. Only the boss of the dungeon had a chance to drop [Skill Book]s and [Items].

Any item dropped from a dungeon was highly sought after, with their prices not being too different from the [Skill Book]s

My goal today was to enter [Ape’s Paradise]... and alone at that. My heart was pounding at this crazy idea. When the thought was forming in my head, I had to ask myself multiple times if I had gone insane

But it will not be that dangerous. Seeing how I can cast my Skills so many times without feeling any fatigue, the rank of my [Focus], even though I cant f.u.c.k.i.n.g see it for some reason, should be nearing B if not A

And any rank B hunter can easily clear Rank F and E dungeons, though they don’t care to, and put their focus on higher-ranked dungeons

Now, with me being the only F ranked Hunter entering and clearing this dungeon...My heart was pounding from the possible rewards

Usually, rewards are split between the many members of the Party, But I would keep all the [Core]s and possible [Skill Book]s and [Item]s that drop. Just the thought of it made my heart excited

I steeled my resolve and heading towards [Ape’s Paradise]
', 'published','approved',0,'2025-10-31','2025-10-31'),
(5,10,2,N'First Awakening',N'A huge monolith structure was surrounded by high walls. There was security at the entrance and heavily armed soldiers on the walls.

Even though this was only an F ranked dungeon, it made up for millions of dollars of revenue every day with a large number of people entering, clearing the monsters, and selling the resources they receive inside

This monolith had a blue color, indicating it was a normal dungeon with little danger of a dungeon break. If the color changed to orange and then to red, it meant a dungeon break was imminent

Regularly clearing the dungeon kept the color at blue, turning it into a treasure trove with capable people farming it

You always had casualties every now and then from Hunters that were either too full of themselves, bad teammates, or just plain unlucky

Now, I sincerely hope I don’t make myself on the category of being too full of myself

Gathering my wits, I show the guards at the metal entrance of the enclosure my hunter ID before heading in. Inside the walls, there were a few hunter teams getting their gear in order or just talking

A few people were standing around looking to either form parties or invite others to their party. At the center of the blue monolith, there was a square, door-like structure. This door like structure was how you entered the dungeon

Those in a group would have to touch the door with a few second differences, but under a minute, to be transferred to the first floor of the dungeon together

There was a party prepared to go into the dungeon in front of me already, so I reigned in my curiosity about the other hunters around me and stood a few feet away from the in-going party

It didn’t take long before a few hunters noticed a lone man waiting by himself

"Oy, is that kid thinking of going in alone?" Hunter A

"Bro, another one with a death wish" Hunter B

"Just ignore him, who knows, maybe he’s one of those prodigies that can solo an F rank dungeon? Haha" Hunter C

Ignoring the laughs around me, I wait patiently as the entire party in front of me disappeared in a flash, and waited the required minute before going forward and placing my hand on the square door

In an instant, I was transported into a large cavern, where I had just caught the attention of two 1 meter tall black apes

I brought myself to full attention and observed both the apes and my surrounding before I began my first dungeon hunt

[Fireball]

A single fireball, and then a second, and a third, and a forth quickly flew towards the two apes

When the ball of fire exploded on their faces, they were already incapacitated and letting out dying screeches. They would not survive after that, and a second fireball hit them at the same location, quickly finishing them off

I breathed heavily as I felt the adrenaline course through my body. 2 F rank monsters, easily taken care of by me. Haha, this was a feeling I could get used to!

Usually, F rank hunters would use their skills sparingly, making sure to cast them all either on the BOSS or on emergencies. I did not have such reservations

I went towards where the apes fell and grabbed a glistening [Strength Core] that dropped. It was small in size, smaller than a pigeon’s egg, and yet they went for a thousand dollars on the market

I placed the [Core] into my backpack and ventured farther in. I didn’t have to walk far, as 3 apes were quickly rushing towards me screeching after hearing the death screams of their brethren

With a thought, [Fireball] was cast one after another, each not missing its targets that were rushing forward.

Another [Strength Core] dropped from these three. [Core]s were not guaranteed to drop every time a monster died, and getting 2 from 5 monsters was already fairly average

Placing the newly acquired [Core]s in the bag, I rushed down the path of the Cavern, meeting another set of 5 apes that I quickly burned

I had to force myself to calm down as I so easily and quickly took down the monsters and collected [Core]s. I never imagined myself being able to do something like this and had to pinch myself a few times just to make sure it wasn’t a dream

Forcing myself to remain calmer, I cleared the first floor quickly and headed down the winding steps to the 2nd floor.

The apes came in batches of 3-5, and just ran towards me when I neared. I didn’t let them get any closer than 4 meters from me, hurling balls of fire towards them as soon as they were in my sight

And just like that, an incredulous sight occurred in the F ranked Dungeon [Ape’s Paradise]. Without a moment of rest, an F ranked hunter cleared the 2nd floor, and the 3rd...and the 5th...all the way to the end of the 9th floor with ease

"WAAH!"

I lost control of my emotions as I let out a triumphant shout before the boss room. I had never imagined something like this to be possible!

Without a single issue, I had effectively taken down groups of Apes, collected multiple [Core]s, and reached the BOSS room all by myself

I reeled my emotions in as I let out a confident smile...and went further down to the BOSS room

A huge ape awaits me in the circular cave that is the last room.

"ROAR!!!"

The cave lightly shakes from the scream. My smile does not leave my face as five fireballs fly towards the ape that is now charging towards me. The first hit its chest, stopping it on its tracks. The second and third smash its shoulders, knocking it backward. The fourth and fifth explode on either side of its head, effectively finishing it off.

As soon as the ape crashed down, a blinding light flashed in the last floor and a green crystal rose in the middle. Touching this crystal will allow you to leave the room and appear at a random location near the entrance of the dungeon

I slowly revel in my first ever completed dungeon hunt, a solo one at that.

I then go towards the now dead ape to see any possible drops. By its side, a few F rank [Core]s are present...along with a [Skill Book]

I grab the [Core]s and put them inside the bag, before reaching out for the skill book. [Minor Heal] was now in my hands, giving me quite a shock. It wasn’t the rarest of F rank skill books, but it was one of the more expensive ones as it easily took care of any cuts and minor injuries.

I also placed the skill inside the bag and proceeded to sit down and think while staring at the green crystal in front of me. The problem at hand is how I should handle myself from now on

An F rank newly awakened hunter clearing an F ranked Dungeon in under 30 minutes was unbelievable. It would have to be someone who was B ranked or above to be able to carry out the same thing

I made sure not to show too much of what I could do when I took the licensing exam because currently, all I have going for me is the fact that I seem to have a large pool of energy to cast skills. This does not mean I am powerful, nor do I hold the power of someone rank B or higher

Even a rank D or C could easily sneak behind me and take me out as long as they’re faster than me. So my plan was to stay low key until I could buy more skills, preferably higher ranking ones that would match my large energy reserves

Sigh, and that required money.

Another plan was forming in my head though. Looking over at the gains just from clearing [Ape’s Paradise] once, I had in total 1 F Rank [Skill Book] and 40+ [Core]s from the more than 160 apes I had killed from the 9 floors before

This alone made me bolder to make some accelerated plans...

Just from one Dungeon run alone, I can make more than $60,000...In under 30 minutes. This alone gave me a great shock.

Normal parties would have to split the loot, and it would take them more than 2 hours of intensely fighting monsters in close combat, which brought them exhaustion and made many parties recuperate for a few hours after each dungeon run.

Most parties would go into the dungeon once or twice a day. With each person making around $10,000, the life of a hunter can be considered very luxurious

So, assuming the BOSS drops either a skill book or possibly an item, I can make around 60 grand each run...this made many ideas dance around in my head wildly.

I’ve already spent many months observing hunters and researching every knowledge available to the public about them. I’ve researched even more into all the known [Skill Book]s and [Item]s that can be dropped from BOSS rooms

For someone with a large energy pool like me, I already had a few books in mind that would work very well. Of course, I also have to keep on testing just how much [Focus] I really have

A wild thought keeps appearing in my head that I do not want to entertain as it is too grand...the possibility that because my [Focus] appears blank, and I can repeatedly continue to cast skills with no feeling of exhaustion...could mean that I can simply keep on casting skills as much as I want...

BOOM

It felt like something had exploded in my head as this idea came and my mind entertained it more and more. My heart was also beating fast that this could even be a possibility

But...why not? From what I’ve seen so far, there is no value for my [Focus] attribute. Is it more logical to think that it is just a really high number that I somehow cannot see, or that because it is blank and I can still cast skills, I can simply continue to cast skills without any problem of depleting something?

I took a deep breath to calm my nerves before I came to terms with a new plan. It was somewhat risky, but it will be fast and reduce any possible future problems, whether it be from monsters or hunters.

The world after the apocalypse was vastly different, but still the same. Now even more filled with dangers and deaths. A hunter could easily be killed by monsters, robbed by other hunters, or killed by the government if they do not fall inline

To secure my safety while rising up, I will need a highly defensive and offensive skill, and well as a speed supporting skill. Something of C rank or above would be highly effective, and yet the cheapest C rank skills start off at $500,000

So...How do I go about this?


', 'published','approved',0,'2025-11-01','2025-11-01'),
(5,10,3,N'The Rift',N'A rift opens.', 'draft','approved',0,'2025-11-02','2025-11-02'),
(5,10,4,N'Ancient Power',N'Power grows.', 'published','pending',0,'2025-11-03','2025-11-03'),
(5,10,5,N'Infinite Mana',N'The legend begins.', 'published','pending',0,'2025-11-04','2025-11-04'),

-- Series 6: Shadow Slave
(6,12,1,N'Chains',N'The beginning of survival.', 'published','approved',0,'2025-10-25','2025-10-25'),
(6,12,2,N'Shadows',N'The darkness deepens.', 'draft','approved',0,'2025-10-26','2025-10-26'),
(6,12,3,N'The Test',N'Survival or death.', 'published','approved',0,'2025-10-27','2025-10-27'),
(6,12,4,N'Light in the Dark',N'A rare moment of hope.', 'published','approved',0,'2025-10-28','2025-10-28'),
(6,12,5,N'Slave No More',N'Freedom earned.', 'published','approved',0,'2025-10-29','2025-10-29'),

-- Series 7: Sơn Tinh Thủy Tinh
(7,14,1,N'Lời Cầu Hôn',N'Hai vị thần cầu hôn công chúa Mỵ Nương.', 'published','approved',0,'2025-10-26','2025-10-26'),
(7,14,2,N'Cuộc Thi',N'Cuộc thi giữa Sơn Tinh và Thủy Tinh.', 'published','approved',0,'2025-10-27','2025-10-27'),
(7,14,3,N'Trận Chiến',N'Nước dâng, núi cao.', 'published','approved',0,'2025-10-28','2025-10-28'),
(7,14,4,N'Hòa Bình',N'Thiên nhiên được cân bằng.', 'draft','approved',0,'2025-10-29','2025-10-29'),
(7,14,5,N'Huyền Thoại',N'Câu chuyện được lưu truyền.', 'published','approved',0,'2025-10-30','2025-10-30'),

-- Series 8: Tensei Shitara Slime Datta Ken
(8,16,1,N'Rebirth as a Slime',N'An ordinary man dies and awakens in a slime body.', 'published','approved',0,'2025-10-28','2025-10-28'),
(8,16,2,N'New Powers',N'Discovers unique absorption ability.', 'published','approved',0,'2025-10-29','2025-10-29'),
(8,16,3,N'Encounter with Veldora',N'Meets the Storm Dragon and forms a pact.', 'published','approved',0,'2025-10-30','2025-10-30'),
(8,16,4,N'Birth of Tempest',N'Builds a new nation of monsters.', 'published','approved',0,'2025-10-31','2025-10-31'),
(8,16,5,N'Delegation from Humans',N'First diplomatic contact with human kingdoms.', 'draft','approved',0,'2025-11-01','2025-11-01'),
(8,16,6,N'The Demon Lords',N'Introduction of powerful demon rulers.', 'published','approved',0,'2025-11-02','2025-11-02'),
(8,16,7,N'Evolution Begins',N'Power transcends limits.', 'published','approved',0,'2025-11-03','2025-11-03'),
(8,16,8,N'Final Duel',N'A fierce battle against ultimate evil.', 'published','approved',0,'2025-11-04','2025-11-04'),

-- Series 9: The Legendary Mechanic
(9,18,1,N'Mechanic Awakens',N'A human reincarnates into a sci-fi universe.', 'published','approved',0,'2025-10-30','2025-10-30'),
(9,18,2,N'Basic Engineering',N'Learns to craft basic machines.', 'published','approved',0,'2025-10-31','2025-10-31'),
(9,18,3,N'First Battle Mech',N'Assembles a custom combat unit.', 'published','approved',0,'2025-11-01','2025-11-01'),
(9,18,4,N'Power Overload',N'A failed experiment causes destruction.', 'draft','approved',0,'2025-11-02','2025-11-02'),
(9,18,5,N'Galactic Alliance',N'Forms alliances with other mechanics.', 'published','approved',0,'2025-11-03','2025-11-03'),
(9,18,6,N'The Betrayal',N'A trusted ally turns traitor.', 'published','approved',0,'2025-11-04','2025-11-04'),
(9,18,7,N'Iron Will',N'Rises stronger from failure.', 'draft','approved',0,'2025-11-05','2025-11-05'),
(9,18,8,N'Final Forge',N'Creates the strongest mecha ever built.', 'published','approved',0,'2025-11-06','2025-11-06'),

-- Series 10: The Legend of Mai An Tiêm
(10,20,1,N'Bị Đày Ra Đảo',N'Mai An Tiêm bị vua trừng phạt và đày ra hoang đảo.', 'published','approved',0,'2025-10-25','2025-10-25'),
(10,20,2,N'Hạt Giống Kỳ Lạ',N'Phát hiện hạt giống bí ẩn từ loài chim.', 'published','approved',0,'2025-10-26','2025-10-26'),
(10,20,3,N'Gieo Trồng Hy Vọng',N'Họ gieo hạt và chăm sóc mỗi ngày.', 'published','approved',0,'2025-10-27','2025-10-27'),
(10,20,4,N'Thu Hoạch Đầu Tiên',N'Những quả dưa đỏ mọng đầu tiên xuất hiện.', 'published','approved',0,'2025-10-28','2025-10-28'),
(10,20,5,N'Tin Vui Trở Lại',N'Vua nghe tin về dưa hấu và tha tội.', 'published','approved',0,'2025-10-29','2025-10-29'),
(10,20,6,N'Trở Về Vinh Quang',N'Mai An Tiêm được đón về kinh thành.', 'published','approved',0,'2025-10-30','2025-10-30'),
(10,20,7,N'Danh Tiếng Lưu Danh',N'Câu chuyện trở thành huyền thoại.', 'published','approved',0,'2025-10-31','2025-10-31'),
(10,20,8,N'Dưa Hấu Việt',N'Biểu tượng của lòng trung hiếu.', 'draft','approved',0,'2025-11-01','2025-11-01'),

-- Series 11: Sorce Stone
(11,2,1,N'The Discovery',N'A strange glowing stone is found in a cave.', 'published','approved',0,'2025-10-27','2025-10-27'),
(11,2,2,N'The Power Within',N'The stone grants vision of the past.', 'published','approved',0,'2025-10-28','2025-10-28'),
(11,2,3,N'Ancient Warnings',N'The artifact speaks through dreams.', 'draft','approved',0,'2025-10-29','2025-10-29'),
(11,2,4,N'Forgotten Civilization',N'An entire race connected to the stone.', 'published','approved',0,'2025-10-30','2025-10-30'),
(11,2,5,N'War for the Stone',N'Two nations fight for control.', 'published','approved',0,'2025-10-31','2025-10-31'),
(11,2,6,N'Sacrifice',N'The hero must destroy the stone to save the world.', 'published','approved',0,'2025-11-01','2025-11-01'),
(11,2,7,N'New Dawn',N'Peace returns to the world.', 'published','approved',0,'2025-11-02','2025-11-02'),
(11,2,8,N'Legacy',N'The memory of the stone lives on.', 'draft','approved',0,'2025-11-03','2025-11-03'),

-- Series 12: So Dua
(12,4,1,N'Anh Chàng Nông Dân',N'Một người nông dân nghèo nhưng thông minh.', 'published','approved',0,'2025-10-28','2025-10-28'),
(12,4,2,N'Nhà Vua Và Lũ Gian Thần',N'Vua bị kẻ xấu lừa dối.', 'published','approved',0,'2025-10-29','2025-10-29'),
(12,4,3,N'Mưu Kế Của So Dua',N'Anh tìm cách cứu vua khỏi cạm bẫy.', 'published','approved',0,'2025-10-30','2025-10-30'),
(12,4,4,N'Trò Đùa Thông Minh',N'Anh trêu đùa bọn gian thần khiến dân làng cười nghiêng ngả.', 'published','approved',0,'2025-10-31','2025-10-31'),
(12,4,5,N'Trở Thành Quân Sư',N'Nhà vua cảm phục và trọng dụng.', 'draft','approved',0,'2025-11-01','2025-11-01'),
(12,4,6,N'Trí Tuệ Dân Gian',N'Câu chuyện lan truyền khắp nơi.', 'published','approved',0,'2025-11-02','2025-11-02'),
(12,4,7,N'Hạnh Phúc Cuối Cùng',N'So Dừa sống bình an với gia đình.', 'published','approved',0,'2025-11-03','2025-11-03'),
(12,4,8,N'Hậu Duệ So Dua',N'Con cháu tiếp nối tinh thần thông minh.', 'published','approved',0,'2025-11-04','2025-11-04');
GO

-- RATINGS (approved series only)
INSERT INTO ratings (user_id, series_id, score, rated_at) VALUES
(1,1,5,'2025-11-01'), (2,2,4,'2025-11-01'), (3,3,5,'2025-11-01'),
(4,4,4,'2025-11-01'), (5,5,5,'2025-11-01'), (6,6,4,'2025-11-01'),
(7,7,5,'2025-11-01'), (8,8,5,'2025-11-01'), (9,9,5,'2025-11-01'),
(10,10,4,'2025-11-01'), (11,11,4,'2025-11-01'), (12,12,5,'2025-11-01');
GO

-- SAVED SERIES
INSERT INTO saved_series (user_id, series_id, saved_at) VALUES
(1,1,'2025-11-01'),(2,2,'2025-11-01'),(3,3,'2025-11-01'),
(4,4,'2025-11-01'),(5,5,'2025-11-01'),(6,6,'2025-11-01'),
(7,7,'2025-11-01'),(8,8,'2025-11-01'),(9,9,'2025-11-01'),
(10,10,'2025-11-01');
GO

-- READING HISTORY
INSERT INTO reading_history (user_id, chapter_id, last_read_at) VALUES
(1,1,'2025-11-02'),(2,2,'2025-11-02'),(3,3,'2025-11-02'),
(4,4,'2025-11-02'),(5,5,'2025-11-02'),(6,6,'2025-11-02'),
(7,7,'2025-11-02'),(8,8,'2025-11-02'),(9,9,'2025-11-02'),
(10,10,'2025-11-02');
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
(8, 1, 'chapter', NULL, 2, N'Poor quality', 'resolved', '2025-10-24', '2025-10-24'),
(9, 1, 'comment', 9, NULL, N'Off-topic', 'pending', '2025-10-25', '2025-10-25'),
(1, 1, 'comment', 5, NULL, N'Offensive language', 'pending', '2025-10-26', '2025-10-26'),
(2, 2, 'chapter', NULL, 5, N'Content violation', 'resolved', '2025-10-27', '2025-10-27'),
(3, NULL, 'comment', 3, NULL, N'Duplicate content', 'rejected', '2025-10-28', '2025-10-28'),
(4, 2, 'chapter', NULL, 1, N'Inappropriate theme', 'pending', '2025-10-29', '2025-10-29'),
(5, 2, 'comment', 10, NULL, N'Spoiler alert', 'resolved', '2025-10-30', '2025-10-30'),
(6, NULL, 'chapter', NULL, 11, N'Plagiarism concern', 'pending', '2025-10-31', '2025-10-31'),
(7, 3, 'comment', 7, NULL, N'Harsh criticism', 'rejected', '2025-10-24', '2025-10-24'),
(8, 4, 'chapter', NULL, 12, N'Quality issue', 'resolved', '2025-10-25', '2025-10-25'),
(9, 5, 'comment', 11, NULL, N'Off-topic post', 'pending', '2025-10-26', '2025-10-26'),
(10, 6, 'chapter', NULL, 13, N'Needs edit', 'rejected', '2025-10-27', '2025-10-27');
GO

-- Insert 15 notifications (added 5 more for coverage)
INSERT INTO notifications (user_id, type, title, message, is_read, url_redirect, created_at) VALUES
(2, 'submission_status', N'Chapter Approved', N'Your chapter "The Lost Kingdom" has been approved by the moderator.', 0, '/chapters/12', '2025-10-25'),
(2, 'submission_status', N'Chapter Approved', N'Your new series "Shadows of Dawn" has been approved and published.', 1, '/chapter/8', '2025-10-26'),
(4, 'moderation', N'Comment Approved', N'Your comment on "Ocean’s Heart" has passed moderation.', 0, '/comments/54', '2025-10-27'),
(4, 'submission_status', N'Chapter Rejected', N'Your chapter "Dark Forest" was rejected. Please review the feedback and resubmit.', 1, '/chapters/15', '2025-10-28'),
(6, 'submission_status', N'Chapter Rejected', N'Your series "Love in the Rain" did not meet our guidelines.', 0, '/chapter/11', '2025-10-29'),
(6, 'moderation', N'Content Reported', N'Your comment on "Hidden Truth" has been reported by another user.', 0, '/comments/77', '2025-10-30'),
(8, 'moderation', N'Chapter Reported', N'Your chapter "Fallen Angel" has been reported for review.', 0, '/chapters/22', '2025-10-31'),
(8, 'moderation', N'Report Resolved', N'Your report regarding "Shadow Blade" has been reviewed and resolved.', 0, '/chapters/45', '2025-10-24'),
(10, 'moderation', N'Report Dismissed', N'The report you submitted about "Dream Hunter" was dismissed after review.', 1, '/chapters/46', '2025-10-25'),
(10, 'system', N'New Badge Unlocked', N'Congratulations! You earned the "Post 100 comments" badge.', 0, '/badges', '2025-10-26'),
(12, 'submission_status', N'Series Approved', N'Your series submission is now live.', 1, '/series/20', '2025-10-27'),
(12, 'moderation', N'Comment Flagged', N'Your recent comment is under review.', 0, '/comments/100', '2025-10-28'),
(14, 'system', N'Points Update', N'You gained 50 points for rating a series.', 0, '/points', '2025-10-29'),
(16, 'submission_status', N'Chapter Pending', N'Your chapter is awaiting moderator approval.', 1, '/chapters/18', '2025-10-30');
GO

-- Insert 15 badges_users (added 7 more)
INSERT INTO badges_users (badge_id, user_id, awarded_at) VALUES
(1, 1, '2025-10-25'), (2, 2, '2025-10-26'), (3, 3, '2025-10-27'), (4, 4, '2025-10-28'),
(5, 5, '2025-10-29'), (6, 6, '2025-10-30'), (7, 7, '2025-10-31'), (8, 8, '2025-10-24'),
(1, 9, '2025-10-25'), (2, 10, '2025-10-26'), (3, 11, '2025-10-27'), (4, 12, '2025-10-28'),
(5, 13, '2025-10-29'), (6, 14, '2025-10-30'), (7, 15, '2025-10-31');
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