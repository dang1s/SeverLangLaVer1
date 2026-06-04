-- ================================================
-- SQL XÓA RECORD TRÙNG LẶP - CHỈ GIỮ LẠI BẢN GỐC
-- ================================================

-- ================================================
-- XÓA RECORD TRÙNG TRONG BẢNG WELFARE
-- ================================================

-- Xóa welfare trùng lặp (chỉ giữ lại id nhỏ nhất)
DELETE FROM welfare 
WHERE welfare_id IN (13, 14) 
AND id NOT IN (
    SELECT MIN(id) FROM welfare WHERE welfare_id IN (13, 14) GROUP BY welfare_id
);

-- Hoặc cách khác - xóa tất cả rồi thêm lại 1 bản gốc:
-- DELETE FROM welfare WHERE welfare_id IN (13, 14);

-- Thêm lại bản gốc (chỉ 1 record mỗi welfare_id)
-- INSERT INTO `welfare` (`id`, `welfare_type`, `welfare_id`, `welfare_name`, `is_package`, `description`) VALUES
-- (13, 'Đầu tư', 13, 'Gói hào hoa', 1, 'Giá : 100 vàng @ Phần thưởng sau khi mua: @ + Nhận ngay 100 vàng khóa@ + Mỗi cấp nhận quà từ 10-65'),
-- (14, 'Đầu tư', 14, 'Gói chí tôn', 1, 'Giá : 300 vàng @ Phần thưởng sau khi mua: @ + Nhận ngay 300 vàng khóa@ + Mỗi cấp nhận quà từ 10-65');

-- ================================================
-- XÓA RECORD TRÙNG TRONG BẢNG PHUCLOI
-- ================================================

-- Xóa phucloi trùng lặp cho welfare_id 13 (chỉ giữ lại bản gốc đầu tiên)
DELETE FROM phucloi 
WHERE ID_PhucLoi = 13 
AND id > (
    SELECT MIN_ID FROM (
        SELECT MIN(id) as MIN_ID FROM phucloi WHERE ID_PhucLoi = 13
    ) AS tmp
);

-- Xóa phucloi trùng lặp cho welfare_id 14 (chỉ giữ lại bản gốc đầu tiên)
DELETE FROM phucloi 
WHERE ID_PhucLoi = 14 
AND id > (
    SELECT MIN_ID FROM (
        SELECT MIN(id) as MIN_ID FROM phucloi WHERE ID_PhucLoi = 14
    ) AS tmp
);

-- ================================================
-- KIỂM TRA KẾT QUẢ
-- ================================================
SELECT * FROM welfare WHERE welfare_id IN (13, 14);
SELECT * FROM phucloi WHERE ID_PhucLoi IN (13, 14);
