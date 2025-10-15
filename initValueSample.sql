-- ============================================
-- SCRIPT INSERT DỮ LIỆU MẪU CHO JoyLeeBook
-- ============================================
USE JoyLeeBook;
GO

-- Vô hiệu hóa kiểm tra khóa ngoại để chèn dữ liệu dễ dàng hơn
EXEC sp_MSforeachtable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL'
GO

-- ============================================
-- 1. INSERT DATA INTO users
-- ============================================
-- Xóa dữ liệu cũ để tránh trùng lặp nếu chạy lại script
DELETE FROM users;
DBCC CHECKIDENT ('users', RESEED, 0);
GO

INSERT INTO users (username, full_name, bio, email, password_hash, role, is_verified, status, points) VALUES
('author_jane', N'Jane Doe', N'Tác giả của những câu chuyện giả tưởng. Yêu thích xây dựng thế giới và các nhân vật phức tạp.', 'jane.doe@example.com', 'hashed_password_placeholder', 'author', 1, 'active', 150),
('author_alex', N'Alex Ray', N'Người kể chuyện đam mê khoa học viễn tưởng và cyberpunk.', 'alex.ray@example.com', 'hashed_password_placeholder', 'author', 1, 'active', 200),
('reader_bob', N'Bob Smith', N'Mọt sách chính hiệu. Thích đọc mọi thể loại.', 'bob.smith@example.com', 'hashed_password_placeholder', 'reader', 1, 'active', 50),
('reader_sara', N'Sara Connor', N'Fan cứng của thể loại lãng mạn và hành động.', 'sara.connor@example.com', 'hashed_password_placeholder', 'reader', 0, 'active', 25),
('banned_user', N'Banned User', N'Tài khoản này đã bị khóa.', 'banned@example.com', 'hashed_password_placeholder', 'reader', 1, 'banned', 0);
GO

-- ============================================
-- 2. INSERT DATA INTO staffs
-- ============================================
DELETE FROM staffs;
DBCC CHECKIDENT ('staffs', RESEED, 0);
GO

INSERT INTO staffs (username, password_hash, full_name, role) VALUES
('admin_joy', 'hashed_password_placeholder', N'Joy Lee', 'admin'),
('staff_mike', 'hashed_password_placeholder', N'Mike Ross', 'staff');
GO

-- ============================================
-- 3. INSERT DATA INTO categories
-- ============================================
DELETE FROM categories;
DBCC CHECKIDENT ('categories', RESEED, 0);
GO

INSERT INTO categories (name, description) VALUES
(N'Fantasy', N'Thế giới phép thuật, sinh vật huyền bí và những cuộc phiêu lưu kỳ ảo.'),
(N'Science Fiction', N'Công nghệ tương lai, du hành không gian và các khái niệm khoa học tiên tiến.'),
(N'Romance', N'Những câu chuyện tình yêu lãng mạn, đầy cảm xúc.'),
(N'Horror', N'Những câu chuyện kinh dị, rùng rợn và ám ảnh.'),
(N'Action', N'Những pha hành động kịch tính, gay cấn.');
GO

-- ============================================
-- 4. INSERT DATA INTO series
-- ============================================
DELETE FROM series;
DBCC CHECKIDENT ('series', RESEED, 0);
GO

INSERT INTO series (title, description, cover_image_url, status, rating_points) VALUES
(N'Vệ Binh Ngân Hà Xanh', N'Một hành trình xuyên không gian để bảo vệ dải ngân hà khỏi một thế lực cổ xưa. Với công nghệ tối tân và những đồng minh bất đắc dĩ, liệu họ có thành công?', 'https://picsum.photos/seed/galaxy/400/600', 'approved', 9), -- rating 5 + 4
(N'Lời Nguyền Của Dòng Sông Quên Lãng', N'Tại một vương quốc bị lãng quên, một lời nguyền cổ xưa trỗi dậy. Một nữ pháp sư trẻ phải tìm ra sự thật để cứu lấy quê hương mình.', 'https://picsum.photos/seed/river/400/600', 'approved', 4), -- rating 4
(N'Trái Tim Băng Giá', N'Câu chuyện tình yêu giữa một nữ bá tước lạnh lùng và một chàng nghệ sĩ nghèo. Liệu tình yêu có thể làm tan chảy trái tim băng giá của cô?', 'https://picsum.photos/seed/heart/400/600', 'pending', 0),
(N'Bóng Đêm Trỗi Dậy', N'Một dự án truyện kinh dị đang trong giai đoạn bản thảo.', 'https://picsum.photos/seed/darkness/400/600', 'draft', 0);
GO

-- ============================================
-- 5. INSERT DATA INTO chapters
-- ============================================
DELETE FROM chapters;
DBCC CHECKIDENT ('chapters', RESEED, 0);
GO

-- Chapters for Series 1: Vệ Binh Ngân Hà Xanh (ID: 1)
INSERT INTO chapters (series_id, chapter_number, title, content, status) VALUES
(1, 1, N'Khởi Đầu Của Cuộc Hành Trình', N'Nội dung chương 1...', 'approved'),
(1, 2, N'Hành Tinh Bão Tố', N'Nội dung chương 2...', 'approved'),
(1, 3, N'Đồng Minh Mới', N'Nội dung chương 3...', 'pending');

-- Chapters for Series 2: Lời Nguyền Của Dòng Sông Quên Lãng (ID: 2)
INSERT INTO chapters (series_id, chapter_number, title, content, status) VALUES
(2, 1, N'Bí Mật Dưới Lòng Sông', N'Nội dung chương 1...', 'approved'),
(2, 2, N'Ma Thuật Cổ Xưa', N'Nội dung chương 2...', 'rejected');
GO

-- ============================================
-- 6. INSERT DATA INTO series_categories (many-to-many)
-- ============================================
DELETE FROM series_categories;
GO

INSERT INTO series_categories (series_id, category_id) VALUES
(1, 2), -- Vệ Binh Ngân Hà Xanh -> Science Fiction
(1, 5), -- Vệ Binh Ngân Hà Xanh -> Action
(2, 1), -- Lời Nguyền Của Dòng Sông Quên Lãng -> Fantasy
(3, 3); -- Trái Tim Băng Giá -> Romance
GO

-- ============================================
-- 7. INSERT DATA INTO series_author (many-to-many)
-- ============================================
DELETE FROM series_author;
GO

INSERT INTO series_author (series_id, user_id) VALUES
(1, 2), -- Vệ Binh Ngân Hà Xanh by Alex Ray
(2, 1), -- Lời Nguyền Của Dòng Sông Quên Lãng by Jane Doe
(3, 1), -- Trái Tim Băng Giá by Jane Doe
(4, 2); -- Bóng Đêm Trỗi Dậy by Alex Ray
GO

-- ============================================
-- 8. INSERT DATA INTO reading_history
-- ============================================
DELETE FROM reading_history;
GO

INSERT INTO reading_history (user_id, chapter_id, last_read_at) VALUES
(3, 1, DATEADD(day, -2, GETDATE())), -- Bob đọc chapter 1 của series 1
(3, 2, DATEADD(day, -1, GETDATE())), -- Bob đọc chapter 2 của series 1
(4, 1, GETDATE()),                   -- Sara đọc chapter 1 của series 1
(4, 4, DATEADD(hour, -5, GETDATE())); -- Sara đọc chapter 1 của series 2
GO

-- ============================================
-- 9. INSERT DATA INTO saved_series
-- ============================================
DELETE FROM saved_series;
GO

INSERT INTO saved_series (user_id, series_id, saved_at) VALUES
(3, 1, DATEADD(week, -1, GETDATE())), -- Bob lưu series 1
(3, 2, DATEADD(day, -3, GETDATE())),  -- Bob lưu series 2
(4, 1, GETDATE());                    -- Sara lưu series 1
GO

-- ============================================
-- 10. INSERT DATA INTO ratings
-- ============================================
DELETE FROM ratings;
GO

INSERT INTO ratings (user_id, series_id, score) VALUES
(3, 1, 5), -- Bob đánh giá series 1 5 sao
(4, 1, 4), -- Sara đánh giá series 1 4 sao
(3, 2, 4); -- Bob đánh giá series 2 4 sao
GO


-- ============================================
-- 11. INSERT DATA INTO comments
-- ============================================
DELETE FROM comments;
DBCC CHECKIDENT ('comments', RESEED, 0);
GO

INSERT INTO comments (user_id, chapter_id, content) VALUES
(3, 1, N'Chương mở đầu hay quá! Hóng chương tiếp theo.'),
(4, 1, N'Tình tiết hấp dẫn, tác giả viết chắc tay thật.'),
(3, 4, N'Cốt truyện có vẻ thú vị, mong tác giả ra chương mới sớm.');
GO

-- ============================================
-- 12. INSERT DATA INTO likes
-- ============================================
DELETE FROM likes;
GO

INSERT INTO likes (user_id, chapter_id) VALUES
(3, 1),
(4, 1),
(3, 2),
(4, 4);
GO

-- ============================================
-- 13. INSERT DATA INTO reports
-- ============================================
DELETE FROM reports;
DBCC CHECKIDENT ('reports', RESEED, 0);
GO

INSERT INTO reports (reporter_id, staff_id, target_type, comment_id, reason, status) VALUES
(4, 2, 'comment', 1, N'Bình luận này có chứa spoiler nhưng không cảnh báo trước.', 'resolved');

INSERT INTO reports (reporter_id, target_type, chapter_id, reason, status) VALUES
(3, 'chapter', 5, N'Nội dung của chương này có vẻ không phù hợp.', 'pending');
GO

-- ============================================
-- 14. INSERT DATA INTO notifications
-- ============================================
DELETE FROM notifications;
DBCC CHECKIDENT ('notifications', RESEED, 0);
GO

INSERT INTO notifications (user_id, type, title, message, url_redirect) VALUES
(2, 'submission_status', N'Chương mới đã được duyệt!', N'Chúc mừng, chương "Khởi Đầu Của Cuộc Hành Trình" của bạn đã được duyệt thành công.', '/series/1/chapter/1'),
(1, 'submission_status', N'Chương của bạn đã bị từ chối', N'Rất tiếc, chương "Ma Thuật Cổ Xưa" đã bị từ chối. Vui lòng xem lại và chỉnh sửa.', '/author/dashboard/series/2'),
(3, 'system', N'Bảo trì hệ thống', N'Hệ thống sẽ được bảo trì vào lúc 2 giờ sáng ngày mai.', NULL),
(4, 'moderation', N'Báo cáo của bạn đã được xử lý', N'Cảm ơn bạn đã báo cáo. Chúng tôi đã xử lý bình luận bạn báo cáo.', '/series/1/chapter/1');
GO

-- ============================================
-- 15. INSERT DATA INTO badges & badges_users
-- ============================================
DELETE FROM badges;
DBCC CHECKIDENT ('badges', RESEED, 0);
GO

INSERT INTO badges (icon_url, name, description, requirement_type, requirement_value) VALUES
('icon_first_comment.png', N'Bình Luận Viên Tập Sự', N'Trao cho người dùng có bình luận đầu tiên.', 'first_comment', 1),
('icon_veteran_reader.png', N'Độc Giả Kỳ Cựu', N'Đọc hơn 100 chương.', 'chapters_read', 100),
('icon_first_series.png', N'Tác Giả Mới Nổi', N'Xuất bản bộ truyện đầu tiên.', 'series_published', 1);
GO

DELETE FROM badges_users;
GO

INSERT INTO badges_users (badge_id, user_id) VALUES
(1, 3), -- Bob nhận huy hiệu 'Bình Luận Viên Tập Sự'
(3, 1), -- Jane nhận huy hiệu 'Tác Giả Mới Nổi'
(3, 2); -- Alex nhận huy hiệu 'Tác Giả Mới Nổi'
GO

-- ============================================
-- 16. INSERT DATA INTO review_series
-- ============================================
DELETE FROM review_series;
GO

INSERT INTO review_series (chapter_id, staff_id, status, comment, updated_at) VALUES
(1, 2, 'approved', N'Nội dung tốt, không có vấn đề.', GETDATE()), -- Mike duyệt chapter 1
(5, 2, 'rejected', N'Nội dung chương có chứa các chi tiết không phù hợp với quy định của nền tảng. Vui lòng chỉnh sửa lại.', GETDATE()); -- Mike từ chối chapter 5
GO


-- ============================================
-- 17. INSERT DATA INTO point_history
-- ============================================
DELETE FROM point_history;
DBCC CHECKIDENT ('point_history', RESEED, 0);
GO

INSERT INTO point_history (user_id, points_change, reason, reference_type, reference_id) VALUES
(1, 50, N'Xuất bản chương mới được duyệt', 'chapter', 4), -- Jane được +50 điểm cho chapter 4
(2, 50, N'Xuất bản chương mới được duyệt', 'chapter', 1), -- Alex được +50 điểm cho chapter 1
(2, 50, N'Xuất bản chương mới được duyệt', 'chapter', 2), -- Alex được +50 điểm cho chapter 2
(3, 5, N'Đăng bình luận hữu ích', 'comment', 1), -- Bob được +5 điểm cho comment 1
(4, -10, N'Vi phạm quy tắc bình luận', 'report', 1); -- Sara bị -10 điểm do bị report
GO


-- Kích hoạt lại toàn bộ kiểm tra khóa ngoại
EXEC sp_MSforeachtable 'ALTER TABLE ? WITH CHECK CHECK CONSTRAINT ALL'
GO

PRINT 'Sample data inserted successfully!';
GO