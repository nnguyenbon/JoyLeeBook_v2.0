-- ============================================
-- Create Database: JoyLeeBook (NO TRIGGERS VERSION)
-- ============================================
CREATE DATABASE JoyLeeBook2;
GO

USE JoyLeeBook2;
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
    chapter_number INT NOT NULL,
    title NVARCHAR(255) NOT NULL,
    content NVARCHAR(MAX),
    status NVARCHAR(20) CHECK (status IN ('pending', 'approved', 'rejected', 'draft', 'hide')) DEFAULT 'pending' NOT NULL,
    is_deleted BIT DEFAULT 0 NOT NULL,
    created_at DATETIME DEFAULT GETDATE() NOT NULL,
    updated_at DATETIME DEFAULT GETDATE() NOT NULL,
    CONSTRAINT FK_chapters_series FOREIGN KEY (series_id) REFERENCES series(series_id) ON DELETE CASCADE,
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
    CONSTRAINT FK_reading_history_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(chapter_id) ON DELETE CASCADE
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
    CONSTRAINT FK_likes_chapter FOREIGN KEY (chapter_id) REFERENCES chapters(chapter_id) ON DELETE CASCADE
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