-- ============================================================
-- SQL CHUYỂN ĐỔI PHÚC LỢI TỪ SERVER MẪU SANG SERVER HIỆN TẠI
-- Ngày: 27/05/2026
-- Nguồn: D:\langlasever1\langla.sql (bảng phuc_loi)
-- Mục tiêu: Lấy template phúc lợi, điều kiện, phần thưởng y hệt
-- ============================================================

-- ============================================================
-- PHẦN 1: CẬP NHẬT BẢNG welfare (DANH MỤC)
-- ============================================================

-- Xóa dữ liệu cũ
DELETE FROM welfare;

-- Thêm tất cả danh mục welfare (giữ nguyên từ server mẫu)
INSERT INTO `welfare` (`welfare_type`, `welfare_id`, `welfare_name`, `is_package`, `description`) VALUES
-- idLoai = 0: Online ngày
('Phúc lợi', 0, 'Online ngày', 0, 'Thời gian Online hôm nay : %d phút'),
-- idLoai = 1: Online liên tục
('Phúc lợi', 1, 'Online 7 ngày', 0, 'Đã Online liên tục: %d ngày @ Nếu ngưng online số lần online sẽ trở về 1'),
-- idLoai = 2: Thăng cấp
('Phúc lợi', 2, 'Thăng cấp', 0, 'Cấp hiện tại : %d'),
-- idLoai = 3: Tiêu ngày
('Phúc lợi', 3, 'Tiêu ngày', 0, 'Hôm nay bạn đã tiêu xài : %d vàng'),
-- idLoai = 4: Tiêu tuần (Rank)
('Phúc lợi', 4, 'Tiêu tuần', 0, 'Rank hiện tại : %d'),
-- idLoai = 5: Nạp ngày
('Quà nạp', 5, 'Nạp ngày', 0, 'Hôm nay bạn đã nạp : %d vàng'),
-- idLoai = 6: Nạp tuần
('Quà nạp', 6, 'Nạp tuần', 0, 'Tuần này bạn đã nạp : %d vàng'),
-- idLoai = 7: Nạp liên tục
('Quà nạp', 7, 'Nạp liên tục', 0, 'Bạn đã nạp liên tục : %d ngày'),
-- idLoai = 8: Nạp 3 mốc
('Quà nạp', 8, 'Nạp 3 mốc', 0, '- Nạp 1 lần đúng theo các giá trị yêu cầu sẽ được nhận quà. @ - Các mốc chỉ nhận 1 lần trong 1 đợt sự kiện'),
-- idLoai = 9: Nạp đơn
('Quà nạp', 9, 'Nạp Đơn', 0, '- Nạp 1 lần đúng theo các giá trị yêu cầu hoặc lớn hơn sẽ được nhận quà. @ - Các mốc chỉ nhận 1 lần trong 1 đợt sự kiện'),
-- idLoai = 10: Nạp Rank
('Quà Rank', 10, 'Nạp Rank', 0, 'Đã tích lũy nạp : %d vàng'),
-- idLoai = 11: Rank chung
('Quà Rank', 11, 'Rank chung', 0, 'Rank hiện tại cao nhất trong máy chủ : Rank %d'),
-- idLoai = 12: Rank tất cả
('Quà Rank', 12, 'Rank tất cả', 0, 'Tổng số Rank trong máy chủ : %d @Khi đủ số lượng Rank tất cả nhân vật sẽ có quà'),
-- idLoai = 13: Gói Hào Hoa (giữ nguyên giá 100 vàng)
('Đầu tư', 13, 'Gói hào hoa', 1, 'Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa@ +Mở khóa quà đầu tư'),
-- idLoai = 14: Gói Chí Tôn (giữ nguyên giá 300 vàng)
('Đầu tư', 14, 'Gói chí tôn', 1, 'Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa@ +Mở khóa quà đầu tư cao cấp'),
-- idLoai = 15: Vòng quay nạp
('Quà Rank', 15, 'Vòng quay nạp', 0, 'Đã tích lũy nạp : %d vàng @ Số lần quay : %d @Sự kiện sẽ hiển thị khi cấu hình hợp lệ'),
-- idLoai = 16: Nạp nhiều
('Quà nạp', 16, 'Nạp nhiều', 0, 'Tổng nạp : %d vàng'),
-- idLoai = 17: Tổng đầu tư
('Đầu tư', 17, 'Tổng đầu tư', 0, 'Tổng số lần mua gói : %d'),
-- idLoai = 18: Mua thẻ
('Thẻ tháng', 18, 'Mua thẻ', 0, 'Số lần mua thẻ : %d'),
-- idLoai = 19: Nạp liên tục (reset)
('Quà nạp', 19, 'Nạp liên tục', 0, 'Bạn đã nạp liên tục : %d ngày @ Reset khi nhận'),
-- idLoai = 20: Rank cao nhất
('Quà Rank', 20, 'Rank cao nhất', 0, 'Rank cao nhất đạt được : %d');

-- ============================================================
-- PHẦN 2: CẬP NHẬT BẢNG phucloi (TEMPLATE)
-- ============================================================

-- Xóa dữ liệu cũ
DELETE FROM phucloi;

-- ============================================================
-- 2.1: ONLINE NGÀY (idLoai = 0)
-- Điều kiện: Thời gian online hàng ngày (phút)
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(0, 163, '15 phút', '', 1, 750000, 10),
(0, 192, '20 phút', '', 1, 250, 20),
(0, 163, '30 phút', '', 1, 1500000, 30),
(0, 192, '60 phút', '', 1, 500, 60),
(0, 163, '90 phút', '', 1, 2000000, 90),
(0, 192, '120 phút', '', 1, 750, 120),
(0, 428, '180 phút', '', 1, 20, 180),
(0, 163, '240 phút', '', 1, 2500000, 240);

-- ============================================================
-- 2.2: ONLINE LIÊN TỤC (idLoai = 1)
-- Điều kiện: Số ngày online liên tục
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(1, 163, 'Ngày 1', '', 1, 1500000, 1),
(1, 277, 'Ngày 2', '', 1, 30, 2),
(1, 428, 'Ngày 3', '', 1, 30, 3),
(1, 192, 'Ngày 4', '', 1, 1500, 4),
(1, 434, 'Ngày 5', '', 1, 100, 5),
(1, 687, 'Ngày 6', '', 1, 100, 6),
(1, 353, 'Ngày 7', '', 1, 40, 7);

-- ============================================================
-- 2.3: THĂNG CẤP (idLoai = 2)
-- Điều kiện: Level nhân vật
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(2, 163, 'Cấp 10', '', 1, 5000000, 10),
(2, 8, 'Cấp 20', '', 1, 1, 20),
(2, 592, 'Cấp 30', '', 1, 1, 30),
(2, 9, 'Cấp 35', '', 1, 1, 35),
(2, 294, 'Cấp 40', '', 1, 100, 40),
(2, 158, 'Cấp 45', '', 1, 1, 45),
(2, 151, 'Cấp 50', '', 1, 1, 50),
(2, 154, 'Cấp 55', '209,20', 1, 1, 55);

-- ============================================================
-- 2.4: TIÊU NGÀY (idLoai = 3)
-- Điều kiện: Vàng tiêu trong ngày
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(3, 417, '50 vàng', '', 1, 1, 50),
(3, 418, '300 vàng', '', 1, 1, 300),
(3, 419, '500 vàng', '', 1, 1, 500),
(3, 420, '1.000 vàng', '', 1, 1, 1000),
(3, 421, '5.000 vàng', '', 1, 1, 5000),
(3, 422, '20.000 vàng', '', 1, 1, 20000),
(3, 423, '100.000 vàng', '', 1, 1, 100000),
(3, 424, '200.000 vàng', '', 1, 1, 200000),
(3, 425, '500.000 vàng', '', 1, 1, 500000),
(3, 426, '1.000.000 vàng', '', 1, 1, 1000000);

-- ============================================================
-- 2.5: TIÊU TUẦN (idLoai = 4) - Rank
-- Điều kiện: Số Rank
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(4, 428, '50 Rank', '', 1, 20, 50),
(4, 428, '100 Rank', '', 1, 40, 100),
(4, 428, '200 Rank', '', 1, 50, 200),
(4, 428, '300 Rank', '', 1, 60, 300),
(4, 428, '400 Rank', '', 1, 80, 400),
(4, 428, '500 Rank', '', 1, 100, 500);

-- ============================================================
-- 2.6: NẠP NGÀY (idLoai = 5)
-- Điều kiện: Vàng nạp trong ngày
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(5, 163, '2.500 vàng', '', 1, 15000000, 2500),
(5, 763, '5.000 vàng', '', 1, 300, 5000),
(5, 687, '10.000 vàng', '', 1, 200, 10000),
(5, 277, '12.000 vàng', '', 1, 150, 12000),
(5, 564, '20.000 vàng', '', 1, 300, 20000),
(5, 354, '15.000 vàng', '', 1, 150, 15000),
(5, 819, '30.000 vàng', '', 1, 200, 30000),
(5, 819, '50.000 vàng', '', 1, 500, 50000);

-- ============================================================
-- 2.7: NẠP TUẦN (idLoai = 6)
-- Điều kiện: Vàng nạp trong tuần
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(6, 127, '5.000 vàng', '136,145,-1;137,145,-1;149,8,-1;174,10,-1;311,200,-1;346,300,-1;0,2500,-1;1,2500,-1;122,175,-1;119,120,-1;120,120,-1;152,500,-1;168,30,-1;169,30,-1;170,30,-1;171,30,-1;172,30,-1', 1, 1, 5000),
(6, 163, '20.000 vàng', '', 1, 20000000, 20000),
(6, 763, '25.000 vàng', '', 1, 500, 25000),
(6, 687, '35.000 vàng', '', 1, 600, 35000),
(6, 564, '40.000 vàng', '', 1, 650, 40000),
(6, 562, '45.000 vàng', '', 1, 650, 45000),
(6, 566, '50.000 vàng', '', 1, 650, 50000),
(6, 428, '100.000 vàng', '', 1, 300, 100000);

-- ============================================================
-- 2.8: NẠP LIÊN TỤC (idLoai = 7)
-- Điều kiện: Số ngày nạp liên tục
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(7, 163, '500 vàng', '', 1, 5000000, 500),
(7, 163, '2.000 vàng', '', 1, 10000000, 2000),
(7, 10, '5.000 vàng', '', 1, 1, 5000),
(7, 687, '10.000 vàng', '', 1, 100, 10000),
(7, 163, '15.000 vàng', '', 1, 25000000, 15000),
(7, 435, '30.000 vàng', '', 1, 1, 30000),
(7, 688, '50.000 vàng', '', 1, 1, 50000);

-- ============================================================
-- 2.9: NẠP 3 MỐC (idLoai = 8)
-- Điều kiện: Nạp đạt 3 mốc
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(8, 9, '5.000 vàng', '', 1, 1, 5000),
(8, 163, '12.000 vàng', '', 1, 10000000, 12000),
(8, 763, '16.000 vàng', '', 1, 200, 16000),
(8, 687, '20.000 vàng', '', 1, 200, 20000),
(8, 10, '25.000 vàng', '', 1, 1, 25000),
(8, 163, '35.000 vàng', '', 1, 35000000, 35000),
(8, 687, '150.000 vàng', '', 1, 1200, 150000),
(8, 672, '100.000 vàng', '209,105;2,200;3,300;122,8', 1, 1, 100000);

-- ============================================================
-- 2.10: NẠP ĐƠN (idLoai = 9)
-- Điều kiện: Nạp đơn lẻ lớn nhất
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(9, 163, '2.000 vàng', '', 1, 50000000, 2000),
(9, 435, '5.000 vàng', '', 1, 1, 5000),
(9, 163, '10.000 vàng', '', 1, 100000000, 10000);

-- ============================================================
-- 2.11: NẠP RANK (idLoai = 10)
-- Điều kiện: Tổng điểm nạp
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(10, 417, '0 Vàng', '', 1, 1, 0),
(10, 418, '0 Vàng', '', 1, 1, 0),
(10, 419, '0 Vàng', '', 1, 1, 0),
(10, 420, '0 Vàng', '', 1, 1, 0),
(10, 421, '0 Vàng', '', 1, 1, 0),
(10, 422, '2.000 Vàng', '', 1, 1, 2000);

-- ============================================================
-- 2.12: RANK CHUNG (idLoai = 11)
-- Điều kiện: Rank cao nhất đạt được
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(11, 192, 'Rank 5', '', 1, 5, 5),
(11, 192, 'Rank 6', '', 1, 10, 6),
(11, 192, 'Rank 7', '', 1, 15, 7),
(11, 192, 'Rank 8', '', 1, 20, 8),
(11, 192, 'Rank 9', '', 1, 25, 9),
(11, 192, 'Rank 10', '', 1, 30, 10);

-- ============================================================
-- 2.13: RANK TẤT CẢ (idLoai = 12)
-- Điều kiện: Tổng số Rank tất cả nhân vật
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(12, 428, '50 lần', '', 1, 20, 50),
(12, 428, '100 lần', '', 1, 40, 100),
(12, 428, '200 lần', '', 1, 50, 200),
(12, 428, '300 lần', '', 1, 60, 300),
(12, 428, '500 lần', '', 1, 80, 500);

-- ============================================================
-- 2.14: GÓI HÀO HOA (idLoai = 13) - y hệt server mẫu
-- Điều kiện: Đã mua goiHaoHoa = true
-- Giá: 100 vàng, Thưởng: 100 vàng khóa
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(13, 858, 'Gói hào hoa', '', 1, 1, 0);

-- ============================================================
-- 2.15: GÓI CHÍ TÔN (idLoai = 14) - y hệt server mẫu
-- Điều kiện: Đã mua goiChiTon = true
-- Giá: 300 vàng, Thưởng: 300 vàng khóa
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(14, 859, 'Gói chí tôn', '', 1, 1, 0);

-- ============================================================
-- 2.16: VÒNG QUAY NẠP (idLoai = 15)
-- Điều kiện: Điểm tích lũy vòng quay
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(15, 192, 'Vòng quay nạp', '', 1, 1, 0);

-- ============================================================
-- 2.17: NẠP NHIỀU (idLoai = 16)
-- Điều kiện: Tổng nạp
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(16, 192, 'Cấp 10', '', 1, 200, 10),
(16, 192, 'Cấp 15', '', 1, 400, 15),
(16, 192, 'Cấp 20', '', 1, 600, 20),
(16, 192, 'Cấp 25', '', 1, 800, 25),
(16, 192, 'Cấp 30', '', 1, 1000, 30),
(16, 192, 'Cấp 35', '', 1, 1200, 35),
(16, 192, 'Cấp 40', '', 1, 1400, 40),
(16, 192, 'Cấp 45', '', 1, 1600, 45),
(16, 192, 'Cấp 50', '', 1, 2000, 50),
(16, 192, 'Cấp 60', '', 1, 5000, 60);

-- ============================================================
-- 2.18: TỔNG ĐẦU TƯ (idLoai = 17)
-- Điều kiện: Tổng số lần mua gói (TongDauTu)
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(17, 163, '50 lần', '', 1, 2000000, 50),
(17, 163, '100 lần', '', 1, 5000000, 100),
(17, 163, '200 lần', '', 1, 10000000, 200),
(17, 163, '300 lần', '', 1, 20000000, 300),
(17, 163, '500 lần', '', 1, 30000000, 500);

-- ============================================================
-- 2.19: MUA THẺ (idLoai = 18)
-- Điều kiện: Tổng số lần mua thẻ
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(18, 192, '100 lần', '', 1, 500, 100),
(18, 192, '200 lần', '', 1, 700, 200),
(18, 192, '300 lần', '', 1, 1000, 300),
(18, 192, '500 lần', '', 1, 1500, 500),
(18, 192, '700 lần', '', 1, 2500, 700),
(18, 192, '1000 lần', '', 1, 5000, 1000);

-- ============================================================
-- 2.20: NẠP LIÊN TỤC RESET (idLoai = 19)
-- Điều kiện: Số ngày nạp liên tục (reset sau khi nhận)
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(19, 163, '1 ngày', '', 1, 15000000, 1),
(19, 277, '2 ngày', '', 1, 150, 2),
(19, 347, '3 ngày', '', 1, 30, 3),
(19, 163, '4 ngày', '', 1, 25000000, 4),
(19, 428, '5 ngày', '', 1, 30, 5),
(19, 11, '6 ngày', '', 1, 1, 6),
(19, 677, '7 ngày', '0,1000;209,88;167,80', 1, 1, 7);

-- ============================================================
-- 2.21: RANK CAO NHẤT (idLoai = 20)
-- Điều kiện: Rank cao nhất đạt được
-- ============================================================
INSERT INTO `phucloi` (`ID_PhucLoi`, `ID_Item`, `Name`, `strOption`, `isLock`, `soluong`, `yeucau`) VALUES
(20, 192, 'Rank 5', '', 1, 5, 5),
(20, 192, 'Rank 6', '', 1, 10, 6),
(20, 192, 'Rank 7', '', 1, 15, 7),
(20, 192, 'Rank 8', '', 1, 20, 8),
(20, 192, 'Rank 9', '', 1, 25, 9),
(20, 192, 'Rank 10', '', 1, 30, 10);

-- ============================================================
-- PHẦN 3: TẠO BẢNG phucloi_info (GLOBAL STATS)
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
-- KẾT QUẢ
-- ============================================================
-- Sau khi chạy SQL này:
-- 1. Bảng welfare sẽ có đầy đủ danh mục phúc lợi y hệt server mẫu
-- 2. Bảng phucloi sẽ có đầy đủ template phúc lợi với điều kiện và phần thưởng
-- 3. Bảng phucloi_info sẽ lưu stats global (TongDauTu, TongSoLanMuaTheThang...)
