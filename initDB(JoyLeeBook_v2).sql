-- ============================================
-- Create Database: JoyLeeBook_v2 (NO TRIGGERS VERSION)
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
    status NVARCHAR(20) CHECK (status IN ('pending', 'approved', 'rejected', 'draft', 'hide')) DEFAULT 'pending' NOT NULL,
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
    CONSTRAINT FK_series_categories_category FOREIGN KEY (category_id) REFERENCES categories(category_id)
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
    added_at DATETIME DEFAULT GETDATE() NOT NULL,
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
-- 16. REVIEW_CHAPTER
-- ============================================
CREATE TABLE review_chapter (
    chapter_id INT NOT NULL,
    staff_id INT NOT NULL,
    status VARCHAR(20) CHECK (status IN ('pending', 'approved', 'rejected', 'draft')) NOT NULL,
    comment NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NOT NULL,
    PRIMARY KEY (chapter_id, staff_id),
    CONSTRAINT FK_review_chapter_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(chapter_id) ON DELETE CASCADE,
    CONSTRAINT FK_review_chapter_staff FOREIGN KEY (staff_id) REFERENCES staffs(staff_id)
);
GO

CREATE INDEX IX_review_chapter_staff ON review_chapter(staff_id, updated_at DESC);
CREATE INDEX IX_review_chapter_status ON review_chapter(status);
GO

-- ============================================
-- 17. POINT_HISTORY
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


USE JoyLeeBook_v2;
GO



-- Insert 20 users (mix of readers and authors)
INSERT INTO users (username, full_name, bio, email, password_hash, google_id, role, is_verified, is_deleted, status, points, created_at, updated_at) VALUES
('reader1', N'John Doe', N'Book lover', 'john@example.com', 'hash1', NULL, 'reader', 1, 0, 'active', 100, '2025-10-09', '2025-10-09'),
('author1', N'Jane Smith', N'Fantasy writer', 'jane@example.com', 'hash2', NULL, 'author', 1, 0, 'active', 500, '2025-10-10', '2025-10-10'),
('reader2', N'Mike Johnson', N'Casual reader', 'mike@example.com', 'hash3', NULL, 'reader', 0, 0, 'active', 50, '2025-10-11', '2025-10-11'),
('author2', N'Emily Davis', N'Mystery author', 'emily@example.com', 'hash4', NULL, 'author', 1, 0, 'active', 300, '2025-10-12', '2025-10-12'),
('reader3', N'Sarah Wilson', N'Avid reader', 'sarah@example.com', 'hash5', NULL, 'reader', 1, 0, 'active', 200, '2025-10-13', '2025-10-13'),
('author3', N'David Brown', N'Sci-fi enthusiast', 'david@example.com', 'hash6', 'google123', 'author', 0, 0, 'active', 400, '2025-10-14', '2025-10-14'),
('reader4', N'Lisa Garcia', N'Romance fan', 'lisa@example.com', 'hash7', NULL, 'reader', 1, 0, 'active', 75, '2025-10-15', '2025-10-15'),
('author4', N'Robert Martinez', N'Horror writer', 'robert@example.com', 'hash8', NULL, 'author', 1, 0, 'inactive', 250, '2025-10-09', '2025-10-09'),
('reader5', N'Amanda Rodriguez', N'New reader', 'amanda@example.com', 'hash9', NULL, 'reader', 0, 0, 'active', 10, '2025-10-10', '2025-10-10'),
('author5', N'James Lee', N'Adventure author', 'james@example.com', 'hash10', NULL, 'author', 1, 0, 'active', 600, '2025-10-11', '2025-10-11'),
('reader6', N'Chris Evans', N'Mystery fan', 'chris@example.com', 'hash11', NULL, 'reader', 1, 0, 'active', 150, '2025-10-12', '2025-10-12'),
('author6', N'Anna Taylor', N'Romance novelist', 'anna@example.com', 'hash12', NULL, 'author', 0, 0, 'active', 450, '2025-10-13', '2025-10-13'),
('reader7', N'Kevin Patel', N'Sci-fi reader', 'kevin@example.com', 'hash13', 'google456', 'reader', 1, 0, 'inactive', 80, '2025-10-14', '2025-10-14'),
('author7', N'Sophia Kim', N'Thriller writer', 'sophia@example.com', 'hash14', NULL, 'author', 1, 0, 'active', 350, '2025-10-15', '2025-10-15'),
('reader8', N'Olivia Chen', N'Adventure lover', 'olivia@example.com', 'hash15', NULL, 'reader', 0, 0, 'active', 120, '2025-10-09', '2025-10-09'),
('author8', N'Michael Wong', N'Historical author', 'michael@example.com', 'hash16', NULL, 'author', 1, 0, 'banned', 200, '2025-10-10', '2025-10-10'),
('reader9', N'Emma Lopez', N'Comedy enthusiast', 'emma@example.com', 'hash17', NULL, 'reader', 1, 0, 'active', 90, '2025-10-11', '2025-10-11'),
('author9', N'Noah Singh', N'Drama specialist', 'noah@example.com', 'hash18', NULL, 'author', 0, 0, 'active', 550, '2025-10-12', '2025-10-12'),
('reader10', N'Sophie Nguyen', N'Horror reader', 'sophie@example.com', 'hash19', NULL, 'reader', 1, 0, 'active', 60, '2025-10-13', '2025-10-13'),
('author10', N'Lucas Garcia', N'Fantasy creator', 'lucas@example.com', 'hash20', 'google789', 'author', 1, 0, 'active', 700, '2025-10-14', '2025-10-14');
GO

-- Insert 10 staffs (mix of staff and admin)
INSERT INTO staffs (username, password_hash, full_name, role, is_deleted, created_at, updated_at) VALUES
('staff1', 'staffhash1', N'Alice Moderator', 'staff', 0, '2025-10-09', '2025-10-09'),
('admin1', 'adminhash1', N'Bob Admin', 'admin', 0, '2025-10-10', '2025-10-10'),
('staff2', 'staffhash2', N'Carol Reviewer', 'staff', 0, '2025-10-11', '2025-10-11'),
('admin2', 'adminhash2', N'Dave SuperAdmin', 'admin', 0, '2025-10-12', '2025-10-12'),
('staff3', 'staffhash3', N'Eve Checker', 'staff', 0, '2025-10-13', '2025-10-13'),
('admin3', 'adminhash3', N'Frank Manager', 'admin', 0, '2025-10-14', '2025-10-14'),
('staff4', 'staffhash4', N'Grace Editor', 'staff', 0, '2025-10-15', '2025-10-15'),
('admin4', 'adminhash4', N'Hank Supervisor', 'admin', 0, '2025-10-09', '2025-10-09'),
('staff5', 'staffhash5', N'Ivy Approver', 'staff', 0, '2025-10-10', '2025-10-10'),
('admin5', 'adminhash5', N'Jack Owner', 'admin', 0, '2025-10-11', '2025-10-11'),
('staff6', 'staffhash6', N'Kate Validator', 'staff', 0, '2025-10-12', '2025-10-12'),
('admin6', 'adminhash6', N'Leo Director', 'admin', 0, '2025-10-13', '2025-10-13'),
('staff7', 'staffhash7', N'Mia Inspector', 'staff', 0, '2025-10-14', '2025-10-14'),
('admin7', 'adminhash7', N'Noah CEO', 'admin', 0, '2025-10-15', '2025-10-15'),
('staff8', 'staffhash8', N'Oscar Auditor', 'staff', 0, '2025-10-09', '2025-10-09'),
('admin8', 'adminhash8', N'Paula Lead', 'admin', 0, '2025-10-10', '2025-10-10'),
('staff9', 'staffhash9', N'Quinn Analyst', 'staff', 0, '2025-10-11', '2025-10-11'),
('admin9', 'adminhash9', N'Ryan Coordinator', 'admin', 0, '2025-10-12', '2025-10-12'),
('staff10', 'staffhash10', N'Sam Enforcer', 'staff', 0, '2025-10-13', '2025-10-13'),
('admin10', 'adminhash10', N'Tina Overseer', 'admin', 0, '2025-10-14', '2025-10-14');
GO

-- Insert 30 categories
INSERT INTO categories (name, description, is_deleted, created_at) VALUES
(N'Fantasy', N'Epic tales and magic', 0, '2025-10-09'),
(N'Mystery', N'Puzzles and detectives', 0, '2025-10-10'),
(N'Sci-Fi', N'Future and space', 0, '2025-10-11'),
(N'Romance', N'Love stories', 0, '2025-10-12'),
(N'Horror', N'Scares and thrills', 0, '2025-10-13'),
(N'Adventure', N'Journeys and quests', 0, '2025-10-14'),
(N'Historical', N'Past events', 0, '2025-10-15'),
(N'Thriller', N'Suspense and action', 0, '2025-10-09'),
(N'Comedy', N'Humor and laughs', 0, '2025-10-10'),
(N'Drama', N'Emotions and conflicts', 0, '2025-10-11'),
(N'Urban Fantasy', N'Modern magic worlds', 0, '2025-10-12'),
(N'Cozy Mystery', N'Light-hearted puzzles', 0, '2025-10-13'),
(N'Cyberpunk', N'High-tech dystopia', 0, '2025-10-14'),
(N'Paranormal Romance', N'Supernatural love', 0, '2025-10-15'),
(N'Gothic Horror', N'Dark atmospheric tales', 0, '2025-10-09'),
(N'Survival Adventure', N'Against the odds', 0, '2025-10-10'),
(N'Biographical Historical', N'Real lives retold', 0, '2025-10-11'),
(N'Psychological Thriller', N'Mind games', 0, '2025-10-12'),
(N'Satirical Comedy', N'Social commentary', 0, '2025-10-13'),
(N'Tragedy Drama', N'Heartbreaking stories', 0, '2025-10-14'),
(N'Young Adult', N'Coming of age stories for teens', 0, '2025-10-15'),
(N'Children''s', N'Stories for young kids', 0, '2025-10-09'),
(N'Non-Fiction', N'Real world facts and information', 0, '2025-10-10'),
(N'Biography', N'Life stories of notable people', 0, '2025-10-11'),
(N'Self-Help', N'Guides for personal improvement', 0, '2025-10-12'),
(N'Poetry', N'Expressive verse and rhymes', 0, '2025-10-13'),
(N'Literary Fiction', N'Artistic and character-driven prose', 0, '2025-10-14'),
(N'Crime', N'Heists and criminal investigations', 0, '2025-10-15'),
(N'Western', N'Frontier and cowboy tales', 0, '2025-10-09'),
(N'Steampunk', N'Victorian-era science fiction', 0, '2025-10-10');
GO

-- Insert 10 badges
INSERT INTO badges (icon_url, name, description, requirement_type, requirement_value, created_at) VALUES
('post100Comments.png', N'Post 100 comments', N'Post 100 comments', 'comments', 100, '2025-01-01'),
('registerAsAuthor.png', N'Register As Author', N'Register As Author', 'Author', 1, '2025-01-02'),
('FinishfirstSeries.png', N'Finish first Series', N'Finish first Series', 'Series', 1, '2025-01-03'),
('Got100Likes.png', N'Got 100 Likes', N'Got 100 Likes', 'Like', 100, '2025-01-04'),
('Got1000points.png', N'Got 1000 points', N'Got 1000 points', 'Points', 1000, '2025-01-05'),
('Got1500Points.png', N'Got 1500 Points', N'Got 1500 Points', 'Points', 1500, '2025-01-06'),
('Got3000Points.png', N'Got 3000 Points', N'Got 3000 Points', 'Points', 3000, '2025-01-07'),
('Got9999Points.png', N'Got 9999 Points', N'Got 9999 Points', 'Points', 9999, '2025-01-08');
GO

-- Insert 20 series
INSERT INTO series (title, description, cover_image_url, status, is_deleted, rating_points, created_at, updated_at) VALUES
(N'The New Kid in School', N'A story about a hopeless romantic, Kaito, as he starts a new life in a coastal town and faces the challenges of friendship and love.', 'thenewkidinschool.png', 'ongoing', 0, 45, '2025-10-15', '2025-10-15'),
(N'Cậu Bé Thông Minh', N'Một câu chuyện dân gian Việt Nam về trí thông minh và lòng dũng cảm.', 'cau_be_thong_minh.avif', 'completed', 0, 48, '2025-10-15', '2025-10-15'),
(N'Dungeon Diver: Stealing a Monster', N'An adventurer who dives into dungeons to steal rare monsters and treasures.', 'dungeon_diver_stealing_a_monster.avif', 'ongoing', 0, 50, '2025-10-15', '2025-10-15'),
(N'Embers Ad Infinitum', N'In a post-apocalyptic world, humanity clings to the remnants of civilization.', 'Embers_Ad_Infinitum.avif', 'ongoing', 0, 42, '2025-10-15', '2025-10-15'),
(N'Evolving Infinitely from Ground Zero', N'A man starts anew after the end of the world.', 'Evolving_infinitely_from_ground_zero.avif', 'completed', 0, 39, '2025-10-15', '2025-10-15'),
(N'Goddess Medical Doctor', N'A skilled doctor reincarnates in a fantasy world to save lives and fight corruption.', 'goddess_medical_doctor.avif', 'ongoing', 0, 46, '2025-10-15', '2025-10-15'),
(N'Infinite Mana in The Apocalypse', N'Magic, evolution, and the end of the world collide.', 'Infinite_Mana_In_The_Apocalypse.avif', 'completed', 0, 47, '2025-10-15', '2025-10-15'),
(N'Monarch of Time', N'A man manipulates time to change destiny.', 'monarch_of_time.avif', 'ongoing', 0, 49, '2025-10-15', '2025-10-15'),
(N'Shadow Slave', N'Chains, darkness, and survival in a cruel world.', 'shadow_slave.avif', 'completed', 0, 41, '2025-10-15', '2025-10-15'),
(N'Sơn Tinh Thủy Tinh', N'Truyền thuyết Việt Nam về cuộc chiến giữa núi và nước.', 'Son_Tinh_Thuy_Tinh.avif', 'ongoing', 0, 44, '2025-10-15', '2025-10-15'),
(N'Supreme Magus', N'A scholar strives to master the arcane arts.', 'supreme-magus-webnovel.avif', 'ongoing', 0, 46, '2025-10-15', '2025-10-15'),
(N'Tensei Shitara Slime Datta Ken', N'An ordinary man reincarnates as a slime with incredible powers.', 'tensei_shitara_slime_datta_ken.avif', 'completed', 0, 43, '2025-10-15', '2025-10-15'),
(N'Thanh Giong', N'Truyền thuyết về vị anh hùng làng Gióng cứu nước.', 'thanh_giong.avif', 'ongoing', 0, 47, '2025-10-15', '2025-10-15'),
(N'The Legendary Mechanic', N'Sci-fi action story about technology and power.', 'the-legendary-mechanic-novel.avif', 'completed', 0, 49, '2025-10-15', '2025-10-15'),
(N'The World of Otome Games is Tough', N'A man reincarnates in a dating sim game world.', 'the-world-of-otome-games-is-toug.avif', 'ongoing', 0, 40, '2025-10-15', '2025-10-15'),
(N'The Epi of Leviathan', N'A fantasy about gods and mortals clashing.', 'the_epi_of_leviathan.avif', 'ongoing', 0, 45, '2025-10-15', '2025-10-15'),
(N'The Legend of Mai An Tiêm', N'Truyền thuyết Việt Nam về quả dưa hấu và lòng trung hiếu.', 'The_Legend_of_Mai_An_Tiem.avif', 'completed', 0, 48, '2025-10-15', '2025-10-15'),
(N'The Mech Touch', N'Mecha engineering, war, and ambition.', 'The_Mech_Touch.avif', 'ongoing', 0, 44, '2025-10-15', '2025-10-15'),
(N'Sorce Stone', N'A mystical journey through ancient lands.', 'sorce_stone.avif', 'completed', 0, 42, '2025-10-15', '2025-10-15'),
(N'So Dua', N'Truyện dân gian Việt Nam về người nông dân thông minh.', 'so_dua.avif', 'ongoing', 0, 41, '2025-10-15', '2025-10-15');
GO


-- Insert 10 unique series_categories (assign categories to series)
INSERT INTO series_categories (series_id, category_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5), (6, 6), (7, 7), (8, 8), (9, 9), (10, 10), 
(11, 1), (12,15), (13, 17), (14, 1), (15, 1), (16, 2), (17, 2), (18, 3), (19, 4), (20, 3);
GO

-- Insert 20 unique series_author records (assign different authors/dates to series)
INSERT INTO series_author (series_id, user_id, added_at) VALUES
-- Ngày 2025-10-15 (Hôm nay)
(1, 2, '2025-10-15'),
(2, 4, '2025-10-15'),
(3, 6, '2025-10-15'),
(4, 8, '2025-10-15'),
(5, 10, '2025-10-15'),
(6, 1, '2025-10-15'),
(7, 3, '2025-10-15'),
(8, 5, '2025-10-15'),
(9, 7, '2025-10-15'),
(10, 9, '2025-10-15'),
(11, 2, '2025-10-15'),
(12, 4, '2025-10-15'),
(13, 6, '2025-10-15'),
(14, 8, '2025-10-15'),
(15, 10, '2025-10-15'),
(16, 1, '2025-10-15'),
(17, 3, '2025-10-15'),
(18, 5, '2025-10-15'),
(19, 7, '2025-10-15'),
(20, 9, '2025-10-15'),

-- Ngày 2025-10-14 (Hôm qua)
(1, 3, '2025-10-14'), (2, 5, '2025-10-14'), (3, 7, '2025-10-14'), (4, 9, '2025-10-14'), (5, 1, '2025-10-14'),
(6, 2, '2025-10-13'), -- Ngày 2025-10-13
(7, 4, '2025-10-12'), -- Ngày 2025-10-12
(8, 6, '2025-10-11'), -- Ngày 2025-10-11
(9, 8, '2025-10-10'), -- Ngày 2025-10-10
(10, 10, '2025-10-09'); -- Ngày 2025-10-09
GO

-- Insert 10 chapters (distribute across series)
INSERT INTO chapters (series_id, user_id, chapter_number, title, content, status, is_deleted, created_at, updated_at) VALUES
(1, 1, 1, N'The Night Before the First Day', N'The night sky stretched out like a velvet blanket, dotted with fluffy clouds and shimmering with the soft glow of the moon. Below, rooftops huddled close together, the lights from their small windows twinkling like stars on the ground, stretching down the slope towards the deep blue sea. The night sea was calm, with only the distant whisper of waves like a lullaby.
On a small balcony of one of those houses, Kaito sat quietly, his gaze lost on the horizon. He was a "hopeless romantic," just like the words on the book cover he often read. He loved to watch the scenery, to lose himself in distant thoughts—about beautiful things, about love, and about new beginnings.
Tomorrow was Kaito''s first day at a new school. His family had just moved to this small coastal town, where everything was unfamiliar and full of promise. Kaito was the new student, the "new kid in school," and a mix of anxiety and excitement crept into his heart. He wondered what kind of friends he would meet here. Would there be someone who could understand his romantic soul?
He sighed softly, his chin resting on his hand, his fluffy brown hair stirring in the cool sea breeze. Kaito looked down at the lit streets, imagining the stories that would unfold. He knew that, despite the initial awkwardness, this place would become an important part of his life. And perhaps, he would find what his romantic heart had always been searching for.
His eyes landed on a lit house in the distance, where a faint silhouette moved past the window. Kaito smiled, a smile full of hope. "Welcome to the new chapter, Kaito," he told himself.', 'approved', 0, '2025-10-15', '2025-10-15'),
(1, 1, 2, N'The Awakening', N'The hero wakes up...', 'approved', 0, '2023-01-02', '2023-01-02'),
(2, 2, 1, N'The Crime Scene', N'A body is found...', 'approved', 0, '2023-01-03', '2023-01-03'),
(2, 2, 2, N'Suspects Emerge', N'Questioning begins...', 'pending', 0, '2023-01-04', '2023-01-04'),
(3, 3, 1, N'Launch Day', N'Spaceship departs...', 'approved', 0, '2023-01-05', '2023-01-05'),
(3, 3, 2, N'Alien Encounter', N'First contact...', 'approved', 0, '2023-01-06', '2023-01-06'),
(4, 4, 1, N'Meeting Cute', N'Boy meets girl...', 'approved', 0, '2023-01-07', '2023-01-07'),
(5, 5, 1, N'The Haunting Begins', N'Strange noises...', 'rejected', 0, '2023-01-08', '2023-01-08'),
(6, 1, 1, N'The Map', N'Old treasure map...', 'draft', 0, '2023-01-09', '2023-01-09'),
(7, 2, 1, N'The Coronation', N'Royal ceremony...', 'approved', 0, '2023-01-10', '2023-01-10'),
-- Dữ liệu mới hơn
(1, 3, 3, N'Shadow Awakening', N'In the city shadows...', 'approved', 0, '2025-10-14', '2025-10-14'), -- Lưu ý: chapter_number đã được thay đổi để tránh lỗi UNIQUE
(2, 4, 3, N'First Clue', N'A quiet village murder...', 'pending', 0, '2025-10-14', '2025-10-14'), -- Lưu ý: chapter_number đã được thay đổi
(3, 5, 3, N'Neon Startup', N'In a futuristic city...', 'approved', 0, '2025-10-14', '2025-10-14'), -- Lưu ý: chapter_number đã được thay đổi
(4, 1, 2, N'Moonlit Meeting', N'Under the full moon...', 'approved', 0, '2025-10-14', '2025-10-14'), -- Lưu ý: chapter_number đã được thay đổi
(5, 2, 2, N'Veiled Secrets', N'An old mansion...', 'rejected', 0, '2025-10-14', '2025-10-14'), -- Lưu ý: chapter_number đã được thay đổi
(6, 3, 2, N'Wild Call', N'In the jungle depths...', 'draft', 0, '2025-10-14', '2025-10-14'), -- Lưu ý: chapter_number đã được thay đổi
(7, 4, 2, N'Echoes Begin', N'A historical figure awakens...', 'approved', 0, '2025-10-14', '2025-10-14'), -- Lưu ý: chapter_number đã được thay đổi
(8, 5, 1, N'Mind Games Start', N'Psychological descent...', 'pending', 0, '2025-10-14', '2025-10-14'),
(9, 1, 1, N'Witty Introduction', N'A satirical society...', 'approved', 0, '2025-10-14', '2025-10-14'),
(10, 2, 1, N'Torn Beginnings', N'Family fractures...', 'approved', 0, '2025-10-14', '2025-10-14');
GO


-- Insert 20 unique reading_history records
INSERT INTO reading_history (user_id, chapter_id, last_read_at) VALUES
-- Lượt đọc 1: Ngày 2025-10-15
(1, 1, '2025-10-15'), -- User 1 đọc Chapter 1
(2, 2, '2025-10-15'),
(3, 3, '2025-10-15'),
(4, 4, '2025-10-15'),
(5, 5, '2025-10-15'),
(6, 6, '2025-10-14'), -- Ngày 2025-10-14
(7, 7, '2025-10-14'),
(8, 8, '2025-10-14'),
(9, 9, '2025-10-14'),
(10, 10, '2025-10-14'),

-- Lượt đọc 2: Người dùng đọc một chương khác trong 7 ngày qua
(1, 11, '2025-10-13'), -- User 1 đọc Chapter 11 (adjusted to avoid potential duplicates)
(2, 12, '2025-10-13'),
(3, 13, '2025-10-12'), -- Ngày 2025-10-12
(4, 14, '2025-10-12'),
(5, 15, '2025-10-11'), -- Ngày 2025-10-11
(6, 16, '2025-10-11'),
(7, 17, '2025-10-10'), -- Ngày 2025-10-10
(8, 18, '2025-10-10'),
(9, 19, '2025-10-09'), -- Ngày 2025-10-09
(10, 20, '2025-10-09'); -- User 10 đọc Chapter 20
GO

-- Insert 10 unique saved_series (users saving series)
INSERT INTO saved_series (user_id, series_id, saved_at) VALUES
(1, 1, '2025-10-15'), -- Cập nhật ngày gần nhất
(2, 2, '2025-10-15'),
(3, 3, '2025-10-14'), -- Ngày 2025-10-14
(4, 4, '2025-10-14'),
(5, 5, '2025-10-13'), -- Ngày 2025-10-13
(6, 6, '2025-10-12'), -- Ngày 2025-10-12
(7, 7, '2025-10-11'), -- Ngày 2025-10-11
(8, 8, '2025-10-10'), -- Ngày 2025-10-10
(9, 9, '2025-10-09'), -- Ngày 2025-10-09
(10, 10, '2025-10-09');
GO

INSERT INTO ratings (user_id, series_id, score, rated_at) VALUES
-- The New Kid in School (series_id = 1)
(1, 1, 5, '2025-10-15'),
(2, 1, 4, '2025-10-15'),
(3, 1, 5, '2025-10-14'),
(4, 1, 5, '2025-10-14'),

-- Cậu Bé Thông Minh (series_id = 2)
(5, 2, 4, '2025-10-15'),
(6, 2, 5, '2025-10-15'),
(7, 2, 4, '2025-10-14'),

-- Dungeon Diver (series_id = 3)
(8, 3, 3, '2025-10-14'),
(9, 3, 4, '2025-10-15'),
(10, 3, 3, '2025-10-15'),

-- Embers Ad Infinitum (series_id = 4)
(11, 4, 5, '2025-10-14'),
(12, 4, 5, '2025-10-15'),
(13, 4, 4, '2025-10-15'),

-- Evolving Infinitely (series_id = 5)
(14, 5, 2, '2025-10-15'),
(15, 5, 3, '2025-10-14'),

-- Goddess Medical Doctor (series_id = 6)
(16, 6, 4, '2025-10-15'),
(17, 6, 4, '2025-10-14'),

-- Infinite Mana in The Apocalypse (series_id = 7)
(18, 7, 5, '2025-10-15'),
(19, 7, 5, '2025-10-15'),
(20, 7, 5, '2025-10-15'),

-- Monarch of Time (series_id = 8)
(1, 8, 3, '2025-10-14'),
(2, 8, 4, '2025-10-15'),

-- Shadow Slave (series_id = 9)
(3, 9, 5, '2025-10-15'),
(4, 9, 5, '2025-10-15'),

-- Sơn Tinh Thủy Tinh (series_id = 10)
(5, 10, 4, '2025-10-15'),
(6, 10, 3, '2025-10-14');
GO

-- Insert 20 unique comments (on various chapters)
INSERT INTO comments (user_id, chapter_id, content, is_deleted, created_at, updated_at) VALUES
-- 10 Bình luận đầu tiên (ngày gần nhất)
(1, 1, N'Great start! Love the atmosphere!', 0, '2025-10-15', '2025-10-15'),
(2, 2, N'Exciting! Cozy vibe!', 0, '2025-10-15', '2025-10-15'),
(3, 3, N'Intriguing plot, futuristic thrill!', 0, '2025-10-14', '2025-10-14'),
(4, 4, N'Needs more suspense, but romantic spark is nice.', 0, '2025-10-14', '2025-10-14'),
(5, 5, N'Love the sci-fi elements. Chilling atmosphere!', 0, '2025-10-13', '2025-10-13'),
(6, 6, N'Awesome action and intense survival!', 0, '2025-10-12', '2025-10-12'),
(7, 7, N'Sweet romance and insightful history!', 0, '2025-10-11', '2025-10-11'),
(8, 8, N'Mind-bending chapter!', 0, '2025-10-10', '2025-10-10'),
(9, 9, N'Fun adventure and hilarious take!', 0, '2025-10-09', '2025-10-09'),
(10, 10, N'Deep historical insight with emotional depth!', 0, '2025-10-09', '2025-10-09'),

-- 10 Bình luận khác nhau (tạo cặp chapter_id mới)
(1, 11, N'This chapter is even better!', 0, '2025-10-15', '2025-10-15'), -- User 1 bình luận Chapter 11
(2, 12, N'Love the character development!', 0, '2025-10-14', '2025-10-14'), -- User 2 bình luận Chapter 12
(3, 13, N'The world-building is fantastic.', 0, '2025-10-13', '2025-10-13'),
(4, 14, N'Unexpected turn of events!', 0, '2025-10-12', '2025-10-12'),
(5, 15, N'Thrilling moment!', 0, '2025-10-11', '2025-10-11'),
(6, 16, N'A beautiful scene.', 0, '2025-10-10', '2025-10-10'),
(7, 17, N'Creepy ending!', 0, '2025-10-09', '2025-10-09'),
(8, 18, N'This made me laugh!', 0, '2025-10-09', '2025-10-09'),
(9, 19, N'Very informative!', 0, '2025-10-10', '2025-10-10'),
(10, 20, N'Cant wait for the next part!', 0, '2025-10-11', '2025-10-11'); -- User 10 bình luận Chapter 20
GO

-- Insert 10 unique likes (on chapters)
INSERT INTO likes (user_id, chapter_id, liked_at) VALUES
(1, 1, '2025-10-15'), -- Cập nhật ngày gần nhất
(2, 2, '2025-10-15'),
(3, 3, '2025-10-14'),
(4, 4, '2025-10-14'),
(5, 5, '2025-10-13'),
(6, 6, '2025-10-12'),
(7, 7, '2025-10-11'),
(8, 8, '2025-10-10'),
(9, 9, '2025-10-09'),
(10, 10, '2025-10-09');
GO

-- Insert 10 reports (mix of comment and chapter reports)
INSERT INTO reports (reporter_id, staff_id, target_type, comment_id, chapter_id, reason, status, created_at, updated_at) VALUES
(1, 1, 'comment', 8, NULL, N'Inappropriate content', 'pending', '2023-01-15', '2023-01-15'),
(2, 2, 'chapter', NULL, 8, N'Violates guidelines', 'resolved', '2023-01-16', '2023-01-16'),
(3, 3, 'comment', 1, NULL, N'Spam', 'rejected', '2023-01-17', '2023-01-17'),
(3, NULL, 'comment', 2, NULL, N'Spam', 'pending', '2023-01-17', '2023-01-17'),
(4, 4, 'chapter', NULL, 4, N'Offensive material', 'pending', '2023-01-18', '2023-01-18'),
(5, 5, 'comment', 4, NULL, N'Harassment', 'resolved', '2023-01-19', '2023-01-19'),
(6, NULL, 'chapter', NULL, 6, N'Copyright issue', 'pending', '2023-01-20', '2023-01-20'),
(7, 6, 'comment', 6, NULL, N'Inaccurate', 'rejected', '2023-01-21', '2023-01-21'),
(8, 7, 'chapter', NULL, 2, N'Poor quality', 'resolved', '2023-01-22', '2023-01-22'),
(9, 8, 'comment', 9, NULL, N'Off-topic', 'pending', '2023-01-23', '2023-01-23'),
(1, 9, 'comment', 5, NULL, N'Offensive language', 'pending', '2025-10-14', '2025-10-14'),
(2, 10, 'chapter', NULL, 5, N'Content violation', 'resolved', '2025-10-14', '2025-10-14'),
(3, NULL, 'comment', 3, NULL, N'Duplicate content', 'rejected', '2025-10-14', '2025-10-14'),
(4, 1, 'chapter', NULL, 1, N'Inappropriate theme', 'pending', '2025-10-14', '2025-10-14'),
(5, 2, 'comment', 10, NULL, N'Spoiler alert', 'resolved', '2025-10-14', '2025-10-14'),
(6, NULL, 'chapter', NULL, 11, N'Plagiarism concern', 'pending', '2025-10-14', '2025-10-14'),
(7, 3, 'comment', 7, NULL, N'Harsh criticism', 'rejected', '2025-10-14', '2025-10-14'),
(8, 4, 'chapter', NULL, 12, N'Quality issue', 'resolved', '2025-10-14', '2025-10-14'),
(9, 5, 'comment', 11, NULL, N'Off-topic post', 'pending', '2025-10-14', '2025-10-14'),
(10, 6, 'chapter', NULL, 13, N'Needs edit', 'rejected', '2025-10-14', '2025-10-14');
GO

-- Dữ liệu mẫu cho bảng notifications (chỉ các trường hợp approve/reject/reported)
INSERT INTO notifications (user_id, type, title, message, is_read, url_redirect, created_at) VALUES
-- Các thông báo APPROVED
(1, 'submission_status', N'Chapter Approved', N'Your chapter "The Lost Kingdom" has been approved by the moderator.', 0, '/chapters/12', '2025-10-10'),
(2, 'submission_status', N'Chapter Approved', N'Your new series "Shadows of Dawn" has been approved and published.', 1, '/chapter/8', '2025-10-11'),
(3, 'moderation', N'Comment Approved', N'Your comment on "Ocean’s Heart" has passed moderation.', 0, '/comments/54', '2025-10-12'),

-- Các thông báo REJECTED
(4, 'submission_status', N'Chapter Rejected', N'Your chapter "Dark Forest" was rejected. Please review the feedback and resubmit.', 1, '/chapters/15', '2025-10-13'),
(5, 'submission_status', N'Chapter Rejected', N'Your series "Love in the Rain" did not meet our guidelines.', 0, '/chapter/11', '2025-10-13'),

-- Các thông báo REPORTED
(6, 'moderation', N'Content Reported', N'Your comment on "Hidden Truth" has been reported by another user.', 0, '/comments/77', '2025-10-14'),
(7, 'moderation', N'Chapter Reported', N'Your chapter "Fallen Angel" has been reported for review.', 0, '/chapters/22', '2025-10-14'),
(9, 'moderation', N'Report Resolved', N'Your report regarding "Shadow Blade" has been reviewed and resolved.', 0, '/chapters/45', '2025-10-15'),
(10, 'moderation', N'Report Dismissed', N'The report you submitted about "Dream Hunter" was dismissed after review.', 1, '/chapters/46', '2025-10-15');

GO

-- Insert 10 badges_users (assign badges to users)
INSERT INTO badges_users (badge_id, user_id, awarded_at) VALUES
(1, 1, '2023-01-15'), (2, 2, '2023-01-16'), (3, 3, '2023-01-17'), (4, 4, '2023-01-18'),
(5, 5, '2023-01-19'), (6, 6, '2023-01-20'), (7, 7, '2023-01-21'), (8, 8, '2023-01-22');
GO

-- Insert 10 review_chapter (staff reviewing chapters)
INSERT INTO review_chapter (chapter_id, staff_id, status, comment, created_at, updated_at) VALUES
(1, 1, 'approved', N'Good content, well structured.', '2025-01-15', '2025-01-15'),
(2, 2, 'approved', N'Well written and engaging.', '2025-01-16', '2025-01-16'),
(3, 3, 'pending', N'Awaiting secondary review.', '2025-01-17', '2025-01-17'),
(4, 4, 'rejected', N'Needs major revision for clarity.', '2025-01-18', '2025-01-18'),
(5, 5, 'approved', N'Excellent pacing and tone.', '2025-01-19', '2025-01-19'),
(6, 6, 'approved', N'Meets all publication criteria.', '2025-01-20', '2025-01-20'),
(7, 7, 'pending', N'Awaiting final feedback.', '2025-01-21', '2025-01-21'),
(8, 8, 'rejected', N'Poor narrative flow, needs rewrite.', '2025-01-22', '2025-01-22'),
(9, 9, 'rejected', N'Lacks coherence and structure.', '2025-01-23', '2025-01-23'),
(10, 10, 'approved', N'Clean and polished.', '2025-01-24', '2025-01-24'),
(11, 1, 'approved', N'Solid opening, sets tone well.', '2025-10-14', '2025-10-14'),
(12, 2, 'pending', N'Review in progress by senior editor.', '2025-10-14', '2025-10-14'),
(13, 3, 'approved', N'Engaging tech details, clear style.', '2025-10-14', '2025-10-14'),
(14, 4, 'rejected', N'Too clichéd, lacks originality.', '2025-10-14', '2025-10-14'),
(15, 5, 'approved', N'Atmospheric and emotionally strong.', '2025-10-14', '2025-10-14'),
(16, 6, 'pending', N'Needs grammar corrections before approval.', '2025-10-14', '2025-10-14'),
(17, 7, 'approved', N'Accurate historical references.', '2025-10-14', '2025-10-14'),
(18, 8, 'pending', N'Requires deeper thematic review.', '2025-10-14', '2025-10-14'),
(19, 9, 'approved', N'Funny, sharp, and well-paced.', '2025-10-14', '2025-10-14'),
(20, 10, 'rejected', N'Overly dramatic; lacks subtlety.', '2025-10-14', '2025-10-14');
GO


-- Insert 10 point_history (points changes for users)
INSERT INTO point_history (user_id, points_change, reason, reference_type, reference_id, created_at) VALUES
(1, 10, N'Read chapter', 'chapter', 1, '2023-01-15'),
(2, 50, N'Published series', 'series', 1, '2023-01-16'),
(3, 5, N'Commented', 'comment', 1, '2023-01-17'),
(4, -10, N'Report rejected', 'report', 3, '2023-01-18'),
(5, 20, N'Rated series', 'rating', 1, '2023-01-19'),
(6, 100, N'Badge awarded', 'badge', 1, '2023-01-20'),
(7, 15, N'Saved series', 'series', 7, '2023-01-21'),
(8, 30, N'Chapter approved', 'chapter', 2, '2023-01-22'),
(9, 25, N'Liked chapter', 'like', 9, '2023-01-23'),
(10, 40, N'Report resolved', 'report', 2, '2023-01-24'),
(1, 15, N'Read chapter', 'chapter', 11, '2025-10-14'),
(2, 60, N'Published chapter', 'chapter', 12, '2025-10-14'),
(3, 8, N'Commented', 'comment', 13, '2025-10-14'),
(4, 25, N'Rated series', 'rating', 1, '2025-10-14'),
(5, -5, N'Comment moderated', 'comment', 14, '2025-10-14'),
(6, 35, N'Saved series', 'series', 6, '2025-10-14'),
(7, 70, N'Chapter approved', 'chapter', 15, '2025-10-14'),
(8, 12, N'Liked comment', 'like', 16, '2025-10-14'),
(9, 45, N'Report accepted', 'report', 17, '2025-10-14'),
(10, 55, N'New series created', 'series', 10, '2025-10-14');
GO