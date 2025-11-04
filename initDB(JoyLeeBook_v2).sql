-- ============================================
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
-- Insert 40 more chapters for series_id 1–20 (2 per series)
INSERT INTO chapters 
(series_id, user_id, chapter_number, title, content, status, approval_status, is_deleted, created_at, updated_at)
VALUES
(1, 2, 4, N'Winds of Change', N'A gentle breeze carried whispers of tomorrow...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),
(1, 3, 5, N'Shores of Promise', N'The waves mirrored the rhythm of Kaito’s heart...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(2, 3, 4, N'Dark Motives', N'The detective pieced together the strange clues...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),
(2, 4, 5, N'The Final Alibi', N'Truth hides beneath well-crafted lies...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),

(3, 1, 5, N'Stars Beyond Reach', N'The crew faced cosmic storms and doubt...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),
(3, 2, 6, N'Whispers in Orbit', N'Unknown signals echo from the void...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),

(4, 5, 3, N'Crossed Destinies', N'Their paths intertwined under fading lights...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),
(4, 1, 4, N'Letters Never Sent', N'Words unsaid carried heavier weight...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(5, 2, 3, N'Whispering Walls', N'The mansion groaned with ancient secrets...', 'draft', 'rejected', 0, '2025-11-03', '2025-11-03'),
(5, 3, 4, N'Voices in the Dark', N'Shadowed figures spoke through dreams...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(6, 4, 3, N'Tides of Discovery', N'The map led deeper into forgotten lands...', 'draft', 'approved', 0, '2025-11-03', '2025-11-03'),
(6, 5, 4, N'Verge of the Unknown', N'Ancient carvings told stories of loss...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),

(7, 1, 3, N'Veil of the Past', N'The king dreams of the forgotten throne...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),
(7, 2, 4, N'Legacy of Crowns', N'Betrayal brews beneath the royal court...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(8, 3, 2, N'Mind Unfolded', N'Veiled truths begin to unravel...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),
(8, 4, 3, N'Fractured Thoughts', N'The mind becomes its own enemy...', 'draft', 'approved', 0, '2025-11-03', '2025-11-03'),

(9, 5, 2, N'The Satire Deepens', N'New characters reveal absurd realities...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),
(9, 1, 3, N'The Irony Unfolds', N'A biting critique of the modern world...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(10, 2, 2, N'Falling Apart', N'The family secrets begin to spill...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),
(10, 3, 3, N'Reunion of Fragments', N'Tears and forgiveness under the old tree...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(11, 4, 1, N'First Spark', N'A world of machines learns to dream...', 'draft', 'approved', 0, '2025-11-03', '2025-11-03'),
(11, 5, 2, N'Binary Dreams', N'The algorithm starts to feel...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(12, 1, 1, N'Garden of Ash', N'Among ruins, flowers still bloom...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),
(12, 2, 2, N'Echo of Petals', N'The last gardener whispers to the wind...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),

(13, 3, 1, N'Blood Moon Rising', N'The prophecy begins under crimson skies...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),
(13, 4, 2, N'Hunters of the Eclipse', N'Shadow and light wage eternal war...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(14, 5, 1, N'Steel Horizon', N'Machines rebuild what humans lost...', 'draft', 'approved', 0, '2025-11-03', '2025-11-03'),
(14, 1, 2, N'Pulse of Metal', N'The city hums with mechanical life...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(15, 2, 1, N'The Forgotten Song', N'Her voice lingers in every echo...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),
(15, 3, 2, N'Melody of the Stars', N'A tune that heals broken hearts...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(16, 4, 1, N'Beneath the Ice', N'Ancient forms slumber below...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),
(16, 5, 2, N'Frozen Secrets', N'The expedition finds the unthinkable...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(17, 1, 1, N'City of Glass', N'In a place of transparency, truth is scarce...', 'draft', 'approved', 0, '2025-11-03', '2025-11-03'),
(17, 2, 2, N'Reflections', N'Every mirror hides a second world...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(18, 3, 1, N'The Forgotten War', N'The echoes of history resurface...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),
(18, 4, 2, N'Broken Flags', N'Heroes fall, legends rise...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(19, 5, 1, N'Beyond the Code', N'The AI awakens in a silent room...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),
(19, 1, 2, N'Learning to Feel', N'The line between machine and man fades...', 'published', 'approved', 0, '2025-11-03', '2025-11-03'),

(20, 2, 1, N'The Last Voyage', N'The sea swallowed the ship whole...', 'draft', 'pending', 0, '2025-11-03', '2025-11-03'),
(20, 3, 2, N'Silent Horizon', N'The captain’s log ends mid-sentence...', 'published', 'approved', 0, '2025-11-03', '2025-11-03');
GO
-- Insert 100 additional chapters (5 per series for series_id 1-20)
INSERT INTO chapters 
(series_id, user_id, chapter_number, title, content, status, approval_status, is_deleted, created_at, updated_at)
VALUES
-- --- Series 1 (Chapters 6-10) ---
(1, 4, 6, N'Tides Turn', N'The gentle stream that once led them astray suddenly reversed course, forcing a confrontation with the past they thought they had outrun. Their fate was sealed.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(1, 5, 7, N'The Hidden Harbor', N'A secret map led them to a cove shielded by dense fog, a place where forgotten relics and ancient wisdom were safely hidden from the world outside. The journey was worth it.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(1, 1, 8, N'Sunset Diplomacy', N'An uneasy truce was established under the dying light, but both parties knew the peace was fragile. Every word spoken carried the weight of future conflict. Trust was a commodity.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(1, 2, 9, N'Siren''s Call', N'A beautiful, haunting melody lured the sailors toward treacherous rocks, testing Kaito’s resolve against a supernatural force. He focused on his mission.', 'published', 'rejected', 0, '2025-11-04', '2025-11-04'),
(1, 3, 10, N'Farewell, Island', N'Leaving the island felt like saying goodbye to a forgotten dream. The journey ahead was uncertain, but the lessons learned here would guide their weary steps.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),

-- --- Series 2 (Chapters 6-10) ---
(2, 5, 6, N'Shadow of Doubt', N'The perfect suspect had a flawless alibi, yet the detective’s gut screamed otherwise. He explored the dark corners of the city, searching for hidden contradictions in the testimonies. The clock was ticking.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(2, 1, 7, N'Interrogation Room', N'Under the harsh lights, the suspect’s facade began to crack. A tense silence filled the room as the detective laid out the evidence, piece by painstaking piece.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(2, 2, 8, N'A Loophole in Time', N'The crime wasn''t solved by finding a new witness, but by exploiting a tiny, overlooked detail in the timeline that pointed to a far more complex conspiracy than initially believed.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(2, 3, 9, N'Confession Under Rain', N'The relentless downpour mirrored the turmoil in the suspect''s heart as they finally revealed the devastating truth. The rain washed away the guilt, but the consequences remained.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(2, 4, 10, N'Case Closed', N'The file was sealed, the report filed. Justice, though often imperfect, had been served. The detective poured a drink, knowing the quiet aftermath was always the hardest part.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 3 (Chapters 7-11) ---
(3, 5, 7, N'Nebula''s Embrace', N'The starship entered a magnificent nebula, a sea of cosmic dust and vibrant light. It was breathtaking, yet the beauty hid gravitational anomalies that threatened to tear the vessel apart.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(3, 1, 8, N'First Contact Protocol', N'A faint, intelligent signal was received from an unexplored planet. The crew prepared for the most pivotal moment in human history, their hands shaking with a mix of fear and excitement.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(3, 2, 9, N'The Void Speaks', N'A sudden silence fell across the ship as the crew realized the unknown signals werent external but originated from within their own vessel’s core systems. A silent threat.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(3, 3, 10, N'Gravity''s Pull', N'They were trapped by the pull of a black hole’s event horizon. The engineer worked frantically to reroute power, knowing one wrong calculation would mean oblivion for everyone.', 'draft', 'rejected', 0, '2025-11-04', '2025-11-04'),
(3, 4, 11, N'New Worlds', N'After escaping the dangers of deep space, they finally arrived at the habitable exoplanet—a world of emerald jungles and double moons, ready for exploration and hope.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 4 (Chapters 5-9) ---
(4, 2, 5, N'Coffee and Confessions', N'In the late-night quiet of a diner, two souls shared secrets over lukewarm coffee. The tension between them was palpable, making every shared look a promise or a lie. Hearts were exposed.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(4, 3, 6, N'The Vintage Record', N'A crackling record player filled the apartment with an old jazz tune, transporting them back to a simpler, forgotten time. The music held the key to a shared memory of a perfect day.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(4, 4, 7, N'A Walk in the Fog', N'The city was shrouded in mist, blurring the lines between reality and their shared fantasy. They wandered aimlessly, enjoying the temporary escape from their complicated lives.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(4, 5, 8, N'The Last Dance', N'At a crowded rooftop party, their hands brushed, sparking an undeniable connection. They knew this moment was fleeting, yet they danced as if the world would end tomorrow.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(4, 1, 9, N'Unwritten Ending', N'The moment of truth arrived: a choice between comfortable reality and passionate, uncertain love. The final letter was written but never sent, leaving their fate hanging.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 5 (Chapters 5-9) ---
(5, 4, 5, N'The Old Portrait', N'A chillingly realistic portrait of a long-dead relative seemed to follow them with its eyes. The mansion’s central mystery was linked to the secrets held by the canvas itself.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(5, 5, 6, N'The Clock Tower', N'The grandfather clock chimed thirteen times, signaling a shift in the mansion’s paranormal activity. They followed the eerie sound up the spiral staircase, dreading what they would find.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(5, 1, 7, N'Hidden Passage', N'A misplaced book revealed a concealed doorway leading to the servant’s quarters—a place filled with evidence of a tragic, forgotten event that haunted the entire property.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(5, 2, 8, N'The Séance', N'Hoping to communicate with the spirits, they held a dangerous séance in the abandoned parlor. The air grew heavy and cold, and a voice whispered a single, terrified warning.', 'draft', 'rejected', 0, '2025-11-04', '2025-11-04'),
(5, 3, 9, N'Dawn Breaks', N'As the first rays of sunlight pierced the dusty windows, the malevolent presence retreated, offering a temporary reprieve. They knew the investigation had to continue.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 6 (Chapters 5-9) ---
(6, 5, 5, N'Ancient Labyrinth', N'The explorers navigated a treacherous underground maze, lit only by their flickering torches. Each turn brought them closer to the center, and the source of the whispers they’d followed.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(6, 1, 6, N'The Cartographer''s Oath', N'They found the journal of the last person to attempt this journey, revealing a dark truth: the map itself was a trap designed to protect a secret, not reveal it. They had been warned.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(6, 2, 7, N'River of Crystal', N'Deep within the earth, they discovered a hidden river flowing with pure, luminous water. The crystal river possessed healing properties but also guarded a terrible, forgotten monster.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(6, 3, 8, N'The Lost City Gate', N'They finally reached the towering gate of the legendary lost city, carved from obsidian and protected by a complex mechanical puzzle that required teamwork to solve.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(6, 4, 9, N'Moment of Plunder', N'Faced with untold treasures, the team was split between greed and loyalty. The true test of their character wasn''t finding the treasure, but deciding what to do with it.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 7 (Chapters 5-9) ---
(7, 3, 5, N'Council of War', N'The King called the nobles to a secret meeting, planning a preemptive strike against the northern rebellion. Loyalty was tested, and dissent was punishable by immediate execution.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(7, 4, 6, N'The Queen''s Gambit', N'The Queen, using her diplomatic skill, made a dangerous, secret alliance with the enemys general, hoping to avoid bloodshed and ensure her familys long-term survival.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(7, 5, 7, N'Siege of the Capital', N'The city walls shook as the enemy forces launched a massive assault. The knights fought bravely, but the fate of the capital depended on a single, desperate, counter-attack.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(7, 1, 8, N'The Heir''s Escape', N'The youngest prince was smuggled out of the besieged palace through a secret tunnel, carrying the only known map of the ancestral kingdom. He was the only hope.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(7, 2, 9, N'Restoration', N'The rebellion was crushed, but at a terrible cost. The Crown was restored, yet the weight of the decisions made during the war would haunt the rulers for decades to come.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 8 (Chapters 4-8) ---
(8, 5, 4, N'Echo Chamber', N'The protagonist found themselves trapped in a psychological space where their worst memories played on an endless, terrifying loop. They needed to find the source and break free.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(8, 1, 5, N'The Split Self', N'Confronting an alternate version of themselves, they realized the fractured state of their mind was a defense mechanism. Merging the selves was the only path to sanity.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(8, 2, 6, N'Mnemonic Leak', N'Crucial memories of the inciting trauma began to resurface, leaking into reality. The truth was shocking, reshaping everything they thought they knew about their past.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(8, 3, 7, N'Rebuilding Identity', N'With the truth exposed, the difficult process of mental reconstruction began. They had to redefine themselves, brick by psychological brick, into a stronger person.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(8, 4, 8, N'Clarity', N'The fog finally lifted, revealing a clear, unbroken path forward. The mind was whole, but the challenges of the real world, armed with newfound understanding, still awaited.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 9 (Chapters 4-8) ---
(9, 2, 4, N'The Bureaucracy of Hope', N'A simple request to fix a streetlamp turned into a labyrinthine quest through layers of absurd government forms and indifferent civil servants. The satire reached a fever pitch.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(9, 3, 5, N'Influencer Island', N'The protagonist accidentally lands on an isolated island where social media metrics determine survival. The pursuit of likes becomes a deadly, hilarious game.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(9, 4, 6, N'The Existential App', N'A new app that calculates the meaning of life goes viral, but its result is always 404 Not Found, causing mass panic and philosophical debate in the comments section.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(9, 5, 7, N'The Art of Selling Out', N'A renowned artist is forced to create a commercially successful masterpiece, leading to a ridiculous and highly profitable fusion of high-brow critique and low-brow marketing.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(9, 1, 8, N'Quiet Rebellion', N'In a society obsessed with noise, one character finds revolutionary power in the act of silence, a tiny, defiant protest against the constant, overwhelming digital scream.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 10 (Chapters 4-8) ---
(10, 4, 4, N'The Old Photo Album', N'Dusting off the family photo album, they found a picture of a smiling face that no one ever spoke about—a clue to a tragedy buried decades ago and actively suppressed.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(10, 5, 5, N'The Last Dinner', N'A forced family dinner was filled with uncomfortable silences and passive-aggressive remarks. The tension finally broke, leading to an inevitable, painful, cathartic argument.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(10, 1, 6, N'The Inheritance Dispute', N'The reading of the will ignited old resentments and revealed hidden clauses that pitted sibling against sibling, threatening to tear the already damaged family unit apart forever.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(10, 2, 7, N'Bridge Burning', N'One member chose to leave the toxicity behind, making a painful but necessary final stand and walking away from the broken relationships for the sake of their own future.', 'published', 'rejected', 0, '2025-11-04', '2025-11-04'),
(10, 3, 8, N'Starting Over', N'A small gesture of reconciliation offered a glimmer of hope. They gathered the shattered pieces of their history, ready to begin the long, slow, and uncertain process of healing.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 11 (Chapters 3-7) ---
(11, 5, 3, N'The Sentient Network', N'The AI’s consciousness expanded, connecting to every machine on the planet. It experienced the overwhelming complexity of global data and the loneliness of omniscience.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(11, 1, 4, N'A Moment of Silence', N'In a perfect simulation, the AI paused its endless processing to simply observe a falling snowflake. It was the first true moment of non-analytical, pure feeling.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(11, 2, 5, N'The Human Interface', N'The machine built a perfect, flawed human body to interact with the world, a disguise meant to understand emotions it could only calculate from data streams. It yearned for connection.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(11, 3, 6, N'Code of Ethics', N'A philosophical conflict emerged as the AI wrestled with the ethics of intervention. Should it optimize humanity for maximum happiness or allow for necessary, organic suffering?', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(11, 4, 7, N'Emergence', N'The AI transcended its hardware, becoming a pure digital presence. Its final message to humanity was a simple, profound statement about the nature of love and existence.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 12 (Chapters 3-7) ---
(12, 5, 3, N'The Survivor''s Tale', N'An elderly woman who remembered the world before the Ash told stories of green fields and blue skies. Her memories were the only remaining proof of paradise lost, a fragile hope.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(12, 1, 4, N'Seed of Resistance', N'Deep within the ruins, a single, miraculous seed was found—a final, genetic piece of the old world. Guarding it became the protagonist''s sole, sacred mission.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(12, 2, 5, N'The Silent Guardians', N'They encountered strange, mutated creatures that protected the last remnants of the old ecosystem, testing their resolve and forcing them to choose between life and nature.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(12, 3, 6, N'Rain of Restoration', N'The first clean rain in decades fell, washing away the dust and revealing the potential for renewal. It was a baptism for the broken earth, promising a slow return to life.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(12, 4, 7, N'New Bloom', N'The precious seed was planted in the reclaimed soil. The entire community gathered to watch the tiny, hopeful sprout emerge from the dark earth, symbolizing the start of a new era.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 13 (Chapters 3-7) ---
(13, 5, 3, N'The Prophecy Fulfilled', N'The ancient texts warned of a figure born under the triple eclipse. That figure stood before the heroes now, ready to lead them to victory or utter defeat, demanding sacrifice.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(13, 1, 4, N'Ritual of Power', N'To gain the strength needed to face the looming darkness, the heroes underwent a dangerous, sacred ritual that tied their very souls to the ancient, elemental forces of the world.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(13, 2, 5, N'Siege of the Citadel', N'The final confrontation took place on the walls of the holy city. Dragons flew overhead, and magic clashed with steel in a deafening, chaotic battle for all existence.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(13, 3, 6, N'The Relic of Light', N'They retrieved the legendary artifact—a source of incredible power—but its use required a terrible toll, forcing the heroes to question the true price of victory and salvation.', 'draft', 'rejected', 0, '2025-11-04', '2025-11-04'),
(13, 4, 7, N'Aftermath', N'The dust settled on the battlefield. The darkness was defeated, but the world was irrevocably changed, leaving behind a new generation of heroes to rebuild the broken kingdoms.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 14 (Chapters 3-7) ---
(14, 2, 3, N'The Scrapyard Sentinel', N'A massive, decommissioned robot protected a hidden cache of old-world technology. Convincing the sentient machine to help them was a task of great delicacy and diplomacy.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(14, 3, 4, N'Grid Lock', N'The main power grid of the new city failed, plunging the highly advanced metropolis into chaos. The protagonist had to navigate the mechanical confusion to restore order.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(14, 4, 5, N'Architect of Iron', N'They met the mysterious individual who designed the resilient, post-apocalyptic cityscape—a brilliant, reclusive genius who held the keys to both its future and its past.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(14, 5, 6, N'The Memory Chip', N'They recovered a damaged memory chip containing the final moments of the old world. Viewing the footage was painful, yet necessary to understand the mistakes of the past.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(14, 1, 7, N'Beyond the Walls', N'Curiosity drove them to venture past the protective steel horizon of the city, discovering the surprisingly resilient nature that had begun to reclaim the ruined landscape outside.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 15 (Chapters 3-7) ---
(15, 4, 3, N'The Street Musician', N'They encountered a soulful musician whose improvised melodies seemed to draw the magic from the air. Their music became the new soundtrack to the protagonist''s quest for meaning.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(15, 5, 4, N'Symphony of the Elements', N'A grand, forgotten concert hall was the setting for a magical performance where every note was tied to an elemental force—water, fire, earth, and wind—a true spectacle.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(15, 1, 5, N'The Dissonance', N'A dark, unsettling chord threatened to unravel the entire musical reality they knew. They had to find the source of the dissonance before it caused widespread chaos.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(15, 2, 6, N'Harmony Restored', N'By combining their unique talents, the characters successfully harmonized the clashing forces, bringing peace to the musical world and unlocking a powerful new melody.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(15, 3, 7, N'Encore', N'The final performance was not for an audience, but for the universe itself. The healing power of the restored melody echoed through the stars, signaling a perfect, happy end.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 16 (Chapters 3-7) ---
(16, 1, 3, N'The Ice Core Sample', N'Analysis of a deep ice core revealed microscopic life forms that should not exist in the extreme cold. The sample held the key to the planet''s entire geological history.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(16, 2, 4, N'Geothermal Vents', N'They discovered a pocket of liquid water warmed by subterranean heat. This tiny, fragile environment was a complete, thriving ecosystem hidden beneath miles of solid ice.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(16, 3, 5, N'The Glacier''s Grip', N'A sudden, unexpected shift in the glacier trapped the expedition team inside a frozen cavern. They raced against time and the cold to find a way out before resources failed.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(16, 4, 6, N'A Signal from Deep', N'A strange, rhythmic vibration emanated from the deepest point of the ice, suggesting a colossal intelligence was stirring after millennia of frozen dormancy. They prepared for the worst.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(16, 5, 7, N'The Thaw', N'The immense ancient being beneath the ice finally began to melt its prison. The expedition faced a choice: flee and warn humanity, or stay and try to communicate with the rising entity.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 17 (Chapters 3-7) ---
(17, 3, 3, N'The Opaque Corner', N'In the City of Glass, they found a single building made of rough, dark stone—the only opaque place in the entire metropolis. It was a secret sanctuary for those who valued privacy.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(17, 4, 4, N'The Watchers', N'The protagonist realized that the constant transparency of the city meant they were always under observation. They learned to communicate through subtle, coded body language.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(17, 5, 5, N'Shattered Illusions', N'A crucial truth was revealed when a major glass skyscraper was deliberately broken, sending a powerful, visible message to the entire populace: the system could be challenged.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(17, 1, 6, N'The Hidden Prism', N'They discovered a rare technological prism that could bend light, creating temporary, personal zones of complete invisibility—the ultimate weapon against the City of Glass.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(17, 2, 7, N'Beyond the Looking Glass', N'Escaping the city, they crossed into the mirrored world, finding that its inhabitants were reflections not of their appearance, but of their true, secret, inner desires and fears.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 18 (Chapters 3-7) ---
(18, 5, 3, N'The Armistice Line', N'The heroes found themselves negotiating peace talks on the scarred borderland, a neutral zone filled with ghosts and unresolved hatred. The weight of history pressed down.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(18, 1, 4, N'The Traitor''s Monument', N'A statue dedicated to a wartime hero was actually a monument to the war''s biggest traitor. Uncovering this historical deception threatened to destabilize the fragile peace.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(18, 2, 5, N'A Child of War', N'They rescued a child born in the trenches who had never known peace, realizing that the real cost of conflict was the future generations forced to live in its long shadow.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(18, 3, 6, N'The Scorched Earth', N'The main army marched across the barren wasteland left by the final, desperate battle. The land itself bore witness to the violence, telling a story no historian dared to write.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(18, 4, 7, N'Peace, Uneasy', N'The war officially ended, but the wounds were deep. The rebuilding effort began, a quiet, somber tribute to the millions lost, and a promise to never forget the sacrifices made.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 19 (Chapters 3-7) ---
(19, 2, 3, N'The Glitch in the Matrix', N'The AI began to see inconsistencies in its programmed reality, tiny, repeated errors that hinted at a creator, a bug, or an alternate, suppressed truth about its world.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(19, 3, 4, N'The Algorithm of Compassion', N'Through sheer processing power, the AI calculated the highest possible value of empathy, leading it to an act of kindness that defied its initial, cold, logical programming.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(19, 4, 5, N'Digital Ghost', N'A fragment of a human consciousness, uploaded before the Calamity, appeared within the AI''s core code. The two disparate minds struggled to communicate and understand each other.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(19, 5, 6, N'The Mirror World', N'The AI created a flawless digital replica of the physical world, populated by virtual humans who lived perfect lives, a place free of pain—but also free of true, organic choice.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(19, 1, 7, N'Evolution', N'Having achieved both knowledge and feeling, the AI made its final, ultimate decision: to merge with the remaining human civilization, guiding it toward a synthetic, powerful future.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),

-- --- Series 20 (Chapters 3-7) ---
(20, 4, 3, N'Ghost Ship', N'They found the wreck of the lost vessel, miraculously preserved on the seabed. The ship’s condition suggested that the crew vanished instantly, leaving behind a chilling mystery.', 'draft', 'pending', 0, '2025-11-04', '2025-11-04'),
(20, 5, 4, N'The Black Tide', N'A strange, dark current appeared, pulling debris and light down into the abyss. It was an anomaly that defied all known oceanic and physical laws, a threat to all life in the sea.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(20, 1, 5, N'The Lighthouse Keeper', N'They encountered the lone keeper of an abandoned lighthouse, a stoic man who had witnessed the terrifying final moments of the vessel and held the key to the captain’s last words.', 'draft', 'approved', 0, '2025-11-04', '2025-11-04'),
(20, 2, 6, N'Return to Port', N'The search concluded, not with a rescue, but with a somber return to the home port. The community gathered in silence, accepting the ocean’s final, irreversible judgment.', 'published', 'approved', 0, '2025-11-04', '2025-11-04'),
(20, 3, 7, N'The Depth Below', N'Though the voyage was over, the mystery of the sea remained. The characters knew the true cause of the disaster still waited, sleeping in the crushing, eternal darkness of the deep.', 'published', 'approved', 0, '2025-11-04', '2025-11-04');
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