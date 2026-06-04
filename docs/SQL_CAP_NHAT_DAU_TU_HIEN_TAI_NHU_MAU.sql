-- ============================================================
-- SQL CẬP NHẬT ĐẦU TƯ SERVER HIỆN TẠI = SERVER MẪU
-- Ngày: 27/05/2026
-- Mục tiêu: Làm đầu tư y hệt server mẫu (D:\langlasever1)
-- ============================================================

-- ============================================================
-- PHẦN 1: CẬP NHẬT BẢNG welfare
-- ============================================================

-- Xóa dữ liệu welfare cũ liên quan đến đầu tư
DELETE FROM welfare WHERE welfare_id IN (13, 14);

-- Cập nhật welfare cho Gói Hào Hoa (giống server mẫu: 100 vàng khóa)
INSERT INTO `welfare` (`id`, `welfare_type`, `welfare_id`, `welfare_name`, `is_package`, `description`) VALUES
(NULL, 'Đầu tư', 13, 'Gói hào hoa', 1, 'Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa@ +Mở khóa quà đầu tư');

-- Cập nhật welfare cho Gói Chí Tôn (giống server mẫu: 300 vàng khóa)
INSERT INTO `welfare` (`id`, `welfare_type`, `welfare_id`, `welfare_name`, `is_package`, `description`) VALUES
(NULL, 'Đầu tư', 14, 'Gói chí tôn', 1, 'Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa@ +Mở khóa quà đầu tư cao cấp');

-- ============================================================
-- PHẦN 2: CẬP NHẬT BẢNG phucloi
-- ============================================================

-- Xóa template cũ của Gói Hào Hoa và Gói Chí Tôn
DELETE FROM phucloi WHERE ID_PhucLoi IN (13, 14);

-- Thêm template quà cho Gói Hào Hoa (ID_PhucLoi = 13)
-- Server mẫu: Gói Hào Hoa không có quà riêng, chỉ là cờ để mở khóa
-- Tuy nhiên bạn có thể thêm item tùy ý
INSERT INTO `phucloi` (`id`, `ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(NULL, 13, 858, 'Gói hào hoa', '', 1, 1, 0);

-- Thêm template quà cho Gói Chí Tôn (ID_PhucLoi = 14)
-- Server mẫu: Gói Chí Tôn không có quà riêng, chỉ là cờ để mở khóa
INSERT INTO `phucloi` (`id`, `ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(NULL, 14, 859, 'Gói chí tôn', '', 1, 1, 0);

-- ============================================================
-- PHẦN 3: TẠO BẢNG phucloi_info (GLOBAL STATS)
-- Server mẫu có bảng này để đếm TongDauTu
-- ============================================================

-- Tạo bảng nếu chưa có
CREATE TABLE IF NOT EXISTS `phucloi_info` (
  `id` int(11) NOT NULL,
  `value` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dữ liệu mặc định
INSERT IGNORE INTO `phucloi_info` (`id`, `value`) VALUES
(0, 0),   -- TongRank: Tổng số rank
(1, 0),   -- RankCaoNhat: Rank cao nhất
(2, 0),   -- TongDauTu: Tổng lượt đầu tư (mua gói hào hoa hoặc chí tôn)
(3, 0),   -- TongSoLanMuaTheThang: Tổng lần mua thẻ
(4, 0);   -- ThoiGianX2Online: Thời gian x2 online

-- ============================================================
-- PHẦN 4: CẬP NHẬT MÔ TẢ (NẾU CẦN)
-- ============================================================

-- Bạn có thể chạy câu lệnh này để cập nhật lại mô tả welfare
-- UPDATE welfare SET description = 'Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa@ +Mở khóa quà đầu tư' WHERE welfare_id = 13;
-- UPDATE welfare SET description = 'Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa@ +Mở khóa quà đầu tư cao cấp' WHERE welfare_id = 14;

-- ============================================================
-- KẾT QUẢ SAU KHI CHẠY
-- ============================================================
-- 
-- Bảng welfare:
-- +----+---------------+-------------+------------------+-------------+------------------------------------------------------------------+
-- | id | welfare_type  | welfare_id  | welfare_name     | is_package  | description                                                       |
-- +----+---------------+-------------+------------------+-------------+------------------------------------------------------------------+
-- | xx | Đầu tư        | 13          | Gói hào hoa      | 1           | Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa@ ... |
-- | xx | Đầu tư        | 14          | Gói chí tôn      | 1           | Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa@ ... |
-- +----+---------------+-------------+------------------+-------------+------------------------------------------------------------------+
--
-- Bảng phucloi:
-- +----+---------------+-----------+------------------+-------------+--------+--------+---------+
-- | id | ID_PhucLoi    | ID_Item  | Name             | strOption   | isLock | soluong| yeucau  |
-- +----+---------------+-----------+------------------+-------------+--------+--------+---------+
-- | xx | 13            | 858       | Gói hào hoa      |             | 1      | 1      | 0       |
-- | xx | 14            | 859       | Gói chí tôn      |             | 1      | 1      | 0       |
-- +----+---------------+-----------+------------------+-------------+--------+--------+---------+
--
-- Bảng phucloi_info:
-- +----+-------+
-- | id | value |
-- +----+-------+
-- | 0  | 0     | (TongRank)
-- | 1  | 0     | (RankCaoNhat)
-- | 2  | 0     | (TongDauTu) <-- Cần cập nhật khi user mua gói
-- | 3  | 0     | (TongSoLanMuaTheThang)
-- | 4  | 0     | (ThoiGianX2Online)
-- +----+-------+
