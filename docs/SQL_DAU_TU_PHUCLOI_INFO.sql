-- ============================================================
-- SQL ĐẦU TƯ - SERVER HIỆN TẠI NHƯ SERVER MẪU
-- Ngày: 27/05/2026
-- Mục tiêu: Sử dụng phucloi_info để tracking TongDauTu
-- ============================================================

-- ============================================================
-- PHẦN 1: TẠO BẢNG phucloi_info
-- ============================================================

DROP TABLE IF EXISTS `phucloi_info`;

CREATE TABLE `phucloi_info` (
  `id` int(11) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `value` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dữ liệu mặc định
INSERT INTO `phucloi_info` (`id`, `name`, `value`) VALUES
(0, 'TongRank', 0),
(1, 'RankCaoNhat', 0),
(2, 'TongDauTu', 0),      -- Tổng lượt đầu tư (mua gói Hào Hoa hoặc Chí Tôn)
(3, 'TongSoLanMuaTheThang', 0),
(4, 'ThoiGianX2Online', 0);

-- ============================================================
-- PHẦN 2: CẬP NHẬT BẢNG welfare
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
-- PHẦN 3: CẬP NHẬT BẢNG phucloi (ITEM ĐẦU TƯ)
-- ============================================================

-- Xóa item cũ của Gói Hào Hoa và Gói Chí Tôn
DELETE FROM phucloi WHERE ID_PhucLoi IN (13, 14);

-- Thêm item cho Gói Hào Hoa (ID_PhucLoi = 13)
-- Item: 858 (theo server mẫu)
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(13, 858, 'Gói hào hoa', '', 1, 1, 0);

-- Thêm item cho Gói Chí Tôn (ID_PhucLoi = 14)
-- Item: 859 (theo server mẫu)
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(14, 859, 'Gói chí tôn', '', 1, 1, 0);

-- ============================================================
-- KẾT QUẢ
-- ============================================================
-- 
-- 1. Bảng phucloi_info:
-- +----+------------------------------+-------+
-- | id | name                         | value |
-- +----+------------------------------+-------+
-- | 0  | TongRank                     | 0     |
-- | 1  | RankCaoNhat                 | 0     |
-- | 2  | TongDauTu                    | 0     | <-- Tăng khi mua gói
-- | 3  | TongSoLanMuaTheThang        | 0     |
-- | 4  | ThoiGianX2Online            | 0     |
-- +----+------------------------------+-------+
--
-- 2. Bảng welfare:
-- +----+---------------+-------------+------------------+-------------+--------------------------------------------------------------------+
-- | id | welfare_type  | welfare_id  | welfare_name     | is_package  | description                                                         |
-- +----+---------------+-------------+------------------+-------------+--------------------------------------------------------------------+
-- | xx | Đầu tư       | 13          | Gói hào hoa     | 1           | Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa @... |
-- | xx | Đầu tư       | 14          | Gói chí tôn     | 1           | Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa @... |
-- +----+---------------+-------------+------------------+-------------+--------------------------------------------------------------------+
--
-- 3. Bảng phucloi:
-- +----+---------------+-----------+------------------+-------------+--------+--------+---------+
-- | id | ID_PhucLoi    | ID_Item  | Name             | strOption  | isLock | soluong| yeucau  |
-- +----+---------------+-----------+------------------+-------------+--------+--------+---------+
-- | xx | 13            | 858       | Gói hào hoa     |             | 1      | 1      | 0       |
-- | xx | 14            | 859       | Gói chí tôn     |             | 1      | 1      | 0       |
-- +----+---------------+-----------+------------------+-------------+--------+--------+---------+
