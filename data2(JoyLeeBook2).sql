USE JoyLeeBook2;
GO
-- Insert next 10 users (1-10, mix of readers and authors)
INSERT INTO users (username, full_name, bio, email, password_hash, google_id, role, is_verified, is_deleted, status, points, created_at, updated_at) VALUES
('reader6', N'Chris Evans', N'Mystery fan', 'chris@example.com', 'hash11', NULL, 'reader', 1, 0, 'active', 150, '2025-10-14', '2025-10-14'),
('author6', N'Anna Taylor', N'Romance novelist', 'anna@example.com', 'hash12', NULL, 'author', 0, 0, 'active', 450, '2025-10-14', '2025-10-14'),
('reader7', N'Kevin Patel', N'Sci-fi reader', 'kevin@example.com', 'hash13', 'google456', 'reader', 1, 0, 'inactive', 80, '2025-10-14', '2025-10-14'),
('author7', N'Sophia Kim', N'Thriller writer', 'sophia@example.com', 'hash14', NULL, 'author', 1, 0, 'active', 350, '2025-10-14', '2025-10-14'),
('reader8', N'Olivia Chen', N'Adventure lover', 'olivia@example.com', 'hash15', NULL, 'reader', 0, 0, 'active', 120, '2025-10-14', '2025-10-14'),
('author8', N'Michael Wong', N'Historical author', 'michael@example.com', 'hash16', NULL, 'author', 1, 0, 'banned', 200, '2025-10-14', '2025-10-14'),
('reader9', N'Emma Lopez', N'Comedy enthusiast', 'emma@example.com', 'hash17', NULL, 'reader', 1, 0, 'active', 90, '2025-10-14', '2025-10-14'),
('author9', N'Noah Singh', N'Drama specialist', 'noah@example.com', 'hash18', NULL, 'author', 0, 0, 'active', 550, '2025-10-14', '2025-10-14'),
('reader10', N'Sophie Nguyen', N'Horror reader', 'sophie@example.com', 'hash19', NULL, 'reader', 1, 0, 'active', 60, '2025-10-14', '2025-10-14'),
('author10', N'Lucas Garcia', N'Fantasy creator', 'lucas@example.com', 'hash20', 'google789', 'author', 1, 0, 'active', 700, '2025-10-14', '2025-10-14');
GO
-- Insert next 10 staffs (1-10)
INSERT INTO staffs (username, password_hash, full_name, role, is_deleted, created_at, updated_at) VALUES
('staff6', 'staffhash6', N'Kate Validator', 'staff', 0, '2025-10-14', '2025-10-14'),
('admin6', 'adminhash6', N'Leo Director', 'admin', 0, '2025-10-14', '2025-10-14'),
('staff7', 'staffhash7', N'Mia Inspector', 'staff', 0, '2025-10-14', '2025-10-14'),
('admin7', 'adminhash7', N'Noah CEO', 'admin', 0, '2025-10-14', '2025-10-14'),
('staff8', 'staffhash8', N'Oscar Auditor', 'staff', 0, '2025-10-14', '2025-10-14'),
('admin8', 'adminhash8', N'Paula Lead', 'admin', 0, '2025-10-14', '2025-10-14'),
('staff9', 'staffhash9', N'Quinn Analyst', 'staff', 0, '2025-10-14', '2025-10-14'),
('admin9', 'adminhash9', N'Ryan Coordinator', 'admin', 0, '2025-10-14', '2025-10-14'),
('staff10', 'staffhash10', N'Sam Enforcer', 'staff', 0, '2025-10-14', '2025-10-14'),
('admin10', 'adminhash10', N'Tina Overseer', 'admin', 0, '2025-10-14', '2025-10-14');
GO
-- Insert next 10 categories (1-10)
INSERT INTO categories (name, description, is_deleted, created_at) VALUES
(N'Urban Fantasy', N'Modern magic worlds', 0, '2025-10-14'),
(N'Cozy Mystery', N'Light-hearted puzzles', 0, '2025-10-14'),
(N'Cyberpunk', N'High-tech dystopia', 0, '2025-10-14'),
(N'Paranormal Romance', N'Supernatural love', 0, '2025-10-14'),
(N'Gothic Horror', N'Dark atmospheric tales', 0, '2025-10-14'),
(N'Survival Adventure', N'Against the odds', 0, '2025-10-14'),
(N'Biographical Historical', N'Real lives retold', 0, '2025-10-14'),
(N'Psychological Thriller', N'Mind games', 0, '2025-10-14'),
(N'Satirical Comedy', N'Social commentary', 0, '2025-10-14'),
(N'Tragedy Drama', N'Heartbreaking stories', 0, '2025-10-14');
GO
-- Insert next 10 badges (1-10)
INSERT INTO badges (icon_url, name, description, requirement_type, requirement_value, created_at) VALUES
('cau-be-thong-minh.avif', N'Super Fan', N'Read 500 chapters', 'chapters_read', 500, '2025-10-14'),
('evolving-infinity-from-ground-zero.avif', N'Master Author', N'Published 50 series', 'series_published', 50, '2025-10-14'),
('goddess-mana-doctor-apocalypse.avif', N'Review Master', N'Rated 200 series', 'ratings_given', 200, '2025-10-14'),
('logo2.png', N'Debater', N'Posted 500 comments', 'comments_posted', 500, '2025-10-14'),
('monarch-of-time.avif', N'Archivist', N'Saved 200 series', 'series_saved', 200, '2025-10-14'),
('shadow-slave7.svg', N'Vigilante', N'Reported 50 issues', 'reports_submitted', 50, '2025-10-14'),
('so-dua.avif', N'Eternal Member', N'Active for 5 years', 'account_age', 1825, '2025-10-14'),
('son-thin-thy-thin.avif', N'Point Millionaire', N'10000 points', 'points_earned', 10000, '2025-10-14'),
('supreme-stone.avif', N'Critic Choice', N'Series rated 5.0 avg', 'average_rating', 50, '2025-10-14'),
('webnovel.avif', N'Admin Elite', N'Admin role', 'role', NULL, '2025-10-14');
GO
-- Insert next 10 series (1-10)
INSERT INTO series (title, description, cover_image_url, status, is_deleted, rating_points, created_at, updated_at) VALUES
(N'Shadow Realms', N'Urban fantasy intrigue', 'tensei-shitara-slime-datta-ken.avif', 'ongoing', 0, 46, '2025-10-14', '2025-10-14'),
(N'Cozy Killings', N'Gentle mystery series', 'thanh-gion.avif', 'completed', 0, 43, '2025-10-14', '2025-10-14'),
(N'Neon Nights', N'Cyberpunk adventures', 'the-epi-of-le-van-an-tiem.avif', 'ongoing', 0, 47, '2025-10-14', '2025-10-14'),
(N'Moonlit Hearts', N'Paranormal romance', 'the-mech-touch.avif', 'completed', 0, 49, '2025-10-14', '2025-10-14'),
(N'Veiled Terrors', N'Gothic horror', 'the-legendary-mechanic-novel.avif', 'ongoing', 0, 40, '2025-10-14', '2025-10-14'),
(N'Wild Survival', N'Extreme adventure', 'the-world-of-otome-games-is-tough-for-mobs.avif', 'ongoing', 0, 45, '2025-10-14', '2025-10-14'),
(N'Echoes of History', N'Biographical tales', 'cau-be-thong-minh.avif', 'completed', 0, 48, '2025-10-14', '2025-10-14'),
(N'Mind Traps', N'Psychological thriller', 'evolving-infinity-from-ground-zero.avif', 'ongoing', 0, 44, '2025-10-14', '2025-10-14'),
(N'Witty Satire', N'Humorous critiques', 'goddess-mana-doctor-apocalypse.avif', 'completed', 0, 42, '2025-10-14', '2025-10-14'),
(N'Torn Families', N'Tragedy drama', 'logo2.png', 'ongoing', 0, 41, '2025-10-14', '2025-10-14');
GO
-- Insert next 10 series_categories (assign to new series 1-10 with new categories 1-10)
INSERT INTO series_categories (series_id, category_id) VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5),
(6, 6), (7, 7), (8, 8), (9, 9), (10, 10);
GO
-- Insert next 10 series_author (assign new authors 1-10 to new series 1-10, some shared)
INSERT INTO series_author (series_id, user_id, added_at) VALUES
(1, 2, '2025-10-14'), (2, 4, '2025-10-14'), (3, 6, '2025-10-14'), (4, 2, '2025-10-14'), (5, 8, '2025-10-14'),
(6, 10, '2025-10-14'), (7, 4, '2025-10-14'), (8, 6, '2025-10-14'), (9, 2, '2025-10-14'), (10, 8, '2025-10-14');
GO
-- Insert next 10 chapters (for new series 1-10, chapter 1 each)
INSERT INTO chapters (series_id, chapter_number, title, content, status, is_deleted, created_at, updated_at) VALUES
(1, 1, N'Shadow Awakening', N'In the city shadows...', 'approved', 0, '2025-10-14', '2025-10-14'),
(2, 1, N'First Clue', N'A quiet village murder...', 'pending', 0, '2025-10-14', '2025-10-14'),
(3, 1, N'Neon Startup', N'In a futuristic city...', 'approved', 0, '2025-10-14', '2025-10-14'),
(4, 1, N'Moonlit Meeting', N'Under the full moon...', 'approved', 0, '2025-10-14', '2025-10-14'),
(5, 1, N'Veiled Secrets', N'An old mansion...', 'rejected', 0, '2025-10-14', '2025-10-14'),
(6, 1, N'Wild Call', N'In the jungle depths...', 'draft', 0, '2025-10-14', '2025-10-14'),
(7, 1, N'Echoes Begin', N'A historical figure awakens...', 'approved', 0, '2025-10-14', '2025-10-14'),
(8, 1, N'Mind Games Start', N'Psychological descent...', 'pending', 0, '2025-10-14', '2025-10-14'),
(9, 1, N'Witty Introduction', N'A satirical society...', 'approved', 0, '2025-10-14', '2025-10-14'),
(10, 1, N'Torn Beginnings', N'Family fractures...', 'approved', 0, '2025-10-14', '2025-10-14');
GO
-- Insert next 10 reading_history (users 1-10 reading chapters 1-10)
INSERT INTO reading_history (user_id, chapter_id, last_read_at) VALUES
(1, 1, '2025-10-14'), (2, 2, '2025-10-14'), (3, 3, '2025-10-14'), (4, 4, '2025-10-14'),
(5, 5, '2025-10-14'), (6, 6, '2025-10-14'), (7, 7, '2025-10-14'), (8, 8, '2025-10-14'),
(9, 9, '2025-10-14'), (10, 10, '2025-10-14');
GO
-- Insert next 10 saved_series (users 1-10 saving series 1-10)
INSERT INTO saved_series (user_id, series_id, saved_at) VALUES
(1, 1, '2025-10-14'), (2, 2, '2025-10-14'), (3, 3, '2025-10-14'), (4, 4, '2025-10-14'),
(5, 5, '2025-10-14'), (6, 6, '2025-10-14'), (7, 7, '2025-10-14'), (8, 8, '2025-10-14'),
(9, 9, '2025-10-14'), (10, 10, '2025-10-14');
GO
-- Insert next 10 ratings (users 1-10 rating series 1-10)
INSERT INTO ratings (user_id, series_id, score, rated_at) VALUES
(1, 1, 4, '2025-10-14'), (2, 2, 5, '2025-10-14'), (3, 3, 3, '2025-10-14'), (4, 4, 5, '2025-10-14'),
(5, 5, 1, '2025-10-14'), (6, 6, 4, '2025-10-14'), (7, 7, 5, '2025-10-14'), (8, 8, 2, '2025-10-14'),
(9, 9, 4, '2025-10-14'), (10, 10, 3, '2025-10-14');
GO
-- Insert next 10 comments (users 1-10 on chapters 1-10)
INSERT INTO comments (user_id, chapter_id, content, is_deleted, created_at, updated_at) VALUES
(1, 1, N'Magical start!', 0, '2025-10-14', '2025-10-14'),
(2, 2, N'Cozy vibe!', 0, '2025-10-14', '2025-10-14'),
(3, 3, N'Futuristic thrill!', 0, '2025-10-14', '2025-10-14'),
(4, 4, N'Romantic spark!', 0, '2025-10-14', '2025-10-14'),
(5, 5, N'Chilling atmosphere!', 1, '2025-10-14', '2025-10-14'),
(6, 6, N'Intense survival!', 0, '2025-10-14', '2025-10-14'),
(7, 7, N'Insightful history!', 0, '2025-10-14', '2025-10-14'),
(8, 8, N'Mind-bending!', 0, '2025-10-14', '2025-10-14'),
(9, 9, N'Hilarious take!', 0, '2025-10-14', '2025-10-14'),
(10, 10, N'Emotional depth!', 0, '2025-10-14', '2025-10-14');
GO
-- Insert next 10 likes (users 1-10 on chapters 1-10)
INSERT INTO likes (user_id, chapter_id, liked_at) VALUES
(1, 1, '2025-10-14'), (2, 2, '2025-10-14'), (3, 3, '2025-10-14'), (4, 4, '2025-10-14'),
(5, 5, '2025-10-14'), (6, 6, '2025-10-14'), (7, 7, '2025-10-14'), (8, 8, '2025-10-14'),
(9, 9, '2025-10-14'), (10, 10, '2025-10-14');
GO
-- Insert next 10 reports (mix of comment and chapter reports, valid targets)
INSERT INTO reports (reporter_id, staff_id, target_type, comment_id, chapter_id, reason, status, created_at, updated_at) VALUES
(1, 9, 'comment', 5, NULL, N'Offensive language', 'pending', '2025-10-14', '2025-10-14'),
(2, 10, 'chapter', NULL, 5, N'Content violation', 'resolved', '2025-10-14', '2025-10-14'),
(3, NULL, 'comment', 1, NULL, N'Duplicate content', 'rejected', '2025-10-14', '2025-10-14'),
(4, 1, 'chapter', NULL, 1, N'Inappropriate theme', 'pending', '2025-10-14', '2025-10-14'),
(5, 2, 'comment', 2, NULL, N'Spoiler alert', 'resolved', '2025-10-14', '2025-10-14'),
(6, NULL, 'chapter', NULL, 2, N'Plagiarism concern', 'pending', '2025-10-14', '2025-10-14'),
(7, 3, 'comment', 3, NULL, N'Harsh criticism', 'rejected', '2025-10-14', '2025-10-14'),
(8, 4, 'chapter', NULL, 3, N'Quality issue', 'resolved', '2025-10-14', '2025-10-14'),
(9, 5, 'comment', 4, NULL, N'Off-topic post', 'pending', '2025-10-14', '2025-10-14'),
(10, 6, 'chapter', NULL, 4, N'Needs edit', 'rejected', '2025-10-14', '2025-10-14');
GO
-- Insert next 10 notifications (for users 1-10)
INSERT INTO notifications (user_id, type, title, message, is_read, url_redirect, created_at) VALUES
(1, 'system', N'Account Verified', N'Your account is now verified', 0, '/profile', '2025-10-14'),
(2, 'submission_status', N'Series Published', N'Your new series is live', 1, '/series', '2025-10-14'),
(3, 'moderation', N'Comment Approved', N'Your comment passed review', 0, '/comments', '2025-10-14'),
(4, 'system', N'Points Bonus', N'Extra points for activity', 0, '/points', '2025-10-14'),
(5, 'submission_status', N'Chapter Pending', N'Your chapter is under review', 1, '/chapters', '2025-10-14'),
(6, 'moderation', N'Report Submitted', N'Thanks for reporting', 0, '/reports', '2025-10-14'),
(7, 'system', N'New Follower', N'Someone followed you', 0, '/followers', '2025-10-14'),
(8, 'submission_status', N'Badge Unlocked', N'New badge available', 1, '/badges', '2025-10-14'),
(9, 'moderation', N'Like Received', N'Someone liked your chapter', 0, '/likes', '2025-10-14'),
(10, 'system', N'Update Available', N'New features added', 0, '/updates', '2025-10-14');
GO
-- Insert next 10 badges_users (badges 1-10 to users 1-10)
INSERT INTO badges_users (badge_id, user_id, awarded_at) VALUES
(1, 1, '2025-10-14'), (2, 2, '2025-10-14'), (3, 3, '2025-10-14'), (4, 4, '2025-10-14'),
(5, 5, '2025-10-14'), (6, 6, '2025-10-14'), (7, 7, '2025-10-14'), (8, 8, '2025-10-14'),
(9, 9, '2025-10-14'), (10, 10, '2025-10-14');
GO
-- Insert next 10 review_chapter (staff 1-10 reviewing chapters 1-10)
INSERT INTO review_chapter (chapter_id, staff_id, status, comment, created_at, updated_at) VALUES
(1, 1, 'approved', N'Solid opening', '2025-10-14', '2025-10-14'),
(2, 2, 'pending', N'Review in progress', '2025-10-14', '2025-10-14'),
(3, 3, 'approved', N'Engaging tech', '2025-10-14', '2025-10-14'),
(4, 4, 'rejected', N'Too clichéd', '2025-10-14', '2025-10-14'),
(5, 5, 'approved', N'Atmospheric', '2025-10-14', '2025-10-14'),
(6, 6, 'draft', N'Save for later', '2025-10-14', '2025-10-14'),
(7, 7, 'approved', N'Accurate history', '2025-10-14', '2025-10-14'),
(8, 8, 'pending', N'Deep review needed', '2025-10-14', '2025-10-14'),
(9, 9, 'approved', N'Funny and sharp', '2025-10-14', '2025-10-14'),
(10, 10, 'rejected', N'Overly dramatic', '2025-10-14', '2025-10-14');
GO
-- Insert next 10 point_history (for users 1-10)
INSERT INTO point_history (user_id, points_change, reason, reference_type, reference_id, created_at) VALUES
(1, 15, N'Read chapter', 'chapter', 1, '2025-10-14'),
(2, 60, N'Published chapter', 'chapter', 2, '2025-10-14'),
(3, 8, N'Commented', 'comment', 1, '2025-10-14'),
(4, 25, N'Rated series', 'rating', 1, '2025-10-14'),
(5, -5, N'Comment moderated', 'comment', 5, '2025-10-14'),
(6, 35, N'Saved series', 'series', 6, '2025-10-14'),
(7, 70, N'Chapter approved', 'chapter', 7, '2025-10-14'),
(8, 12, N'Liked comment', 'like', 8, '2025-10-14'),
(9, 45, N'Report accepted', 'report', 8, '2025-10-14'),
(10, 55, N'New series created', 'series', 10, '2025-10-14');
GO