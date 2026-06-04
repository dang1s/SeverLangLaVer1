-- ================================================
-- SQL BỔ SUNG QUÀ THEO CẤP CHO GÓI ĐẦU TƯ
-- Hào hoa (welfare_id=13) và Chí tôn (welfare_id=14)
-- Cấp nhận quà: 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65
-- ================================================

-- XÓA DỮ LIỆU CŨ (nếu muốn reset)
-- DELETE FROM phucloi WHERE ID_PhucLoi IN (13, 14);

-- ================================================
-- BẢNG PHUCLOI - QUÀ THEO CẤP CHO GÓI HÀO HOA (ID_PhucLoi = 13)
-- ================================================
INSERT INTO `phucloi` (`id`, `ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
-- Hào hoa cấp 10
(1001, 13, 277, 'Hào hoa Cấp 10', '209,50', 1, 1, 10),
-- Hào hoa cấp 15
(1002, 13, 277, 'Hào hoa Cấp 15', '209,60', 1, 1, 15),
-- Hào hoa cấp 20
(1003, 13, 277, 'Hào hoa Cấp 20', '209,70', 1, 1, 20),
-- Hào hoa cấp 25
(1004, 13, 277, 'Hào hoa Cấp 25', '209,80', 1, 1, 25),
-- Hào hoa cấp 30
(1005, 13, 277, 'Hào hoa Cấp 30', '209,90', 1, 1, 30),
-- Hào hoa cấp 35
(1006, 13, 277, 'Hào hoa Cấp 35', '209,100', 1, 1, 35),
-- Hào hoa cấp 40
(1007, 13, 277, 'Hào hoa Cấp 40', '209,110', 1, 1, 40),
-- Hào hoa cấp 45
(1008, 13, 277, 'Hào hoa Cấp 45', '209,120', 1, 1, 45),
-- Hào hoa cấp 50
(1009, 13, 277, 'Hào hoa Cấp 50', '209,130', 1, 1, 50),
-- Hào hoa cấp 55
(1010, 13, 277, 'Hào hoa Cấp 55', '209,140', 1, 1, 55),
-- Hào hoa cấp 60
(1011, 13, 277, 'Hào hoa Cấp 60', '209,150', 1, 1, 60),
-- Hào hoa cấp 65
(1012, 13, 277, 'Hào hoa Cấp 65', '209,200', 1, 1, 65);

-- ================================================
-- BẢNG PHUCLOI - QUÀ THEO CẤP CHO GÓI CHÍ TÔN (ID_PhucLoi = 14)
-- ================================================
INSERT INTO `phucloi` (`id`, `ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
-- Chí tôn cấp 10
(1013, 14, 277, 'Chí tôn Cấp 10', '209,100', 1, 1, 10),
-- Chí tôn cấp 15
(1014, 14, 277, 'Chí tôn Cấp 15', '209,120', 1, 1, 15),
-- Chí tôn cấp 20
(1015, 14, 277, 'Chí tôn Cấp 20', '209,140', 1, 1, 20),
-- Chí tôn cấp 25
(1016, 14, 277, 'Chí tôn Cấp 25', '209,160', 1, 1, 25),
-- Chí tôn cấp 30
(1017, 14, 277, 'Chí tôn Cấp 30', '209,180', 1, 1, 30),
-- Chí tôn cấp 35
(1018, 14, 277, 'Chí tôn Cấp 35', '209,200', 1, 1, 35),
-- Chí tôn cấp 40
(1019, 14, 277, 'Chí tôn Cấp 40', '209,220', 1, 1, 40),
-- Chí tôn cấp 45
(1020, 14, 277, 'Chí tôn Cấp 45', '209,240', 1, 1, 45),
-- Chí tôn cấp 50
(1021, 14, 277, 'Chí tôn Cấp 50', '209,260', 1, 1, 50),
-- Chí tôn cấp 55
(1022, 14, 277, 'Chí tôn Cấp 55', '209,280', 1, 1, 55),
-- Chí tôn cấp 60
(1023, 14, 277, 'Chí tôn Cấp 60', '209,300', 1, 1, 60),
-- Chí tôn cấp 65
(1024, 14, 277, 'Chí tôn Cấp 65', '209,400', 1, 1, 65);

-- ================================================
-- CẬP NHẬT BẢNG WELFARE - THÊM WELFARE_ID 13, 14
-- ================================================
-- Xóa nếu đã tồn tại
DELETE FROM welfare WHERE welfare_id IN (13, 14);

-- Thêm welfare cho gói hào hoa (13) và chí tôn (14)
INSERT INTO `welfare` (`id`, `welfare_type`, `welfare_id`, `welfare_name`, `is_package`, `description`) VALUES
(13, 'Đầu tư', 13, 'Gói hào hoa', 1, 'Giá : 100 vàng @ Phần thưởng sau khi mua: @ + Nhận ngay 100 vàng khóa@ + Mỗi cấp nhận quà từ 10-65'),
(14, 'Đầu tư', 14, 'Gói chí tôn', 1, 'Giá : 300 vàng @ Phần thưởng sau khi mua: @ + Nhận ngay 300 vàng khóa@ + Mỗi cấp nhận quà từ 10-65');

-- ================================================
-- CẬP NHẬT BẢNG PHUCLOI ĐỂ THÊM 2 CỘT TRACKING
-- ================================================
-- Kiểm tra và thêm cột nếu chưa có
-- ALTER TABLE phucloi ADD COLUMN capnhanhaohoa INT DEFAULT 0;
-- ALTER TABLE phucloi ADD COLUMN capnhanchiton INT DEFAULT 0;

-- ================================================
-- LƯU Ý QUAN TRỌNG:
-- ================================================
-- 1. ID_Item = 277 là ví dụ (thay đổi theo item bạn muốn)
-- 2. strOption '209,X' là ví dụ về option của item (thay đổi theo nhu cầu)
-- 3. Hãy điều chỉnh ID_Item và strOption phù hợp với server của bạn
-- 4. Chạy SQL này sau khi đã chạy SQL chính (langladata.sql)
