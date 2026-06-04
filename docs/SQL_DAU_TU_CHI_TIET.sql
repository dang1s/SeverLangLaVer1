-- ============================================================
-- SQL ĐẦU TƯ - SỬA THEO SERVER MẪU
-- Ngày: 27/05/2026
-- Mục tiêu: Sửa phần đầu tư y hệt server mẫu
-- ============================================================

-- ============================================================
-- PHẦN 1: CẬP NHẬT BẢNG welfare
-- ============================================================

-- Cập nhật mô tả Gói Hào Hoa (welfare_id = 13)
UPDATE welfare SET 
    description = 'Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa @ Mở khóa quà đầu tư'
WHERE welfare_id = 13;

-- Cập nhật mô tả Gói Chí Tôn (welfare_id = 14)
UPDATE welfare SET 
    description = 'Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa @ Mở khóa quà đầu tư cao cấp'
WHERE welfare_id = 14;

-- ============================================================
-- PHẦN 2: CẬP NHẬT BẢNG phucloi (ITEM ĐẦU TƯ)
-- ============================================================

-- Xóa item cũ của Gói Hào Hoa và Gói Chí Tôn
DELETE FROM phucloi WHERE ID_PhucLoi IN (13, 14);

-- Thêm item mới cho Gói Hào Hoa (ID_PhucLoi = 13)
-- Theo server mẫu: idItem = 858, amount = 1
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(13, 858, 'Gói hào hoa', '', 1, 1, 0);

-- Thêm item mới cho Gói Chí Tôn (ID_PhucLoi = 14)
-- Theo server mẫu: idItem = 859, amount = 1
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(14, 859, 'Gói chí tôn', '', 1, 1, 0);

-- ============================================================
-- PHẦN 3: TẠO BẢNG phucloi_info (NẾU CHƯA CÓ)
-- ============================================================
CREATE TABLE IF NOT EXISTS `phucloi_info` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `value` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT IGNORE INTO `phucloi_info` (`id`, `name`, `value`) VALUES
(0, 'TongRank', 0),
(1, 'RankCaoNhat', 0),
(2, 'TongDauTu', 0),
(3, 'TongSoLanMuaTheThang', 0),
(4, 'ThoiGianX2Online', 0);

-- ============================================================
-- KẾT QUẢ SAU KHI CHẠY
-- ============================================================
-- 
-- Bảng welfare (welfare_id = 13, 14):
-- +----+---------------+-------------+------------------+-------------+------------------------------------------------------------------+
-- | id | welfare_type  | welfare_id  | welfare_name     | is_package  | description                                                       |
-- +----+---------------+-------------+------------------+-------------+------------------------------------------------------------------+
-- | xx | Đầu tư        | 13          | Gói hào hoa      | 1           | Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa @... |
-- | xx | Đầu tư        | 14          | Gói chí tôn      | 1           | Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa @... |
-- +----+---------------+-------------+------------------+-------------+------------------------------------------------------------------+
--
-- Bảng phucloi:
-- +----+---------------+-----------+------------------+-------------+--------+--------+---------+
-- | id | ID_PhucLoi    | ID_Item  | Name             | strOption   | isLock | soluong| yeucau  |
-- +----+---------------+-----------+------------------+-------------+--------+--------+---------+
-- | xx | 13            | 858       | Gói hào hoa      |             | 1      | 1      | 0       |
-- | xx | 14            | 859       | Gói chí tôn      |             | 1      | 1      | 0       |
-- +----+---------------+-----------+------------------+-------------+--------+--------+---------+
