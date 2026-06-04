-- ================================================
-- SỬA GIÁ GÓI HÀO HOA VÀ CHÍ TÔN TRONG BẢNG WELFARE
-- Chạy file này trong MySQL (qua phpMyAdmin hoặc dòng lệnh)
-- ================================================

-- Xem dữ liệu hiện tại trước khi sửa
SELECT * FROM welfare WHERE welfare_id IN (13, 14);

-- Sửa welfare_id = 13: Gói hào hoa = 100 vàng
UPDATE welfare
SET description = 'Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa@ +Mở khóa quà đầu tư'
WHERE welfare_id = 13;

-- Sửa welfare_id = 14: Gói chí tôn = 300 vàng
UPDATE welfare
SET description = 'Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa@ +Mở khóa quà đầu tư cao cấp'
WHERE welfare_id = 14;

-- Sửa welfare_id = 15: Thẻ tháng = 100 vàng
UPDATE welfare
SET description = 'Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa@ +Mỗi ngày nhận thêm 30 vàng khóa@ (nhận liên tục trong 30 ngày)'
WHERE welfare_id = 15;

-- Sửa welfare_id = 16: Thẻ vĩnh viễn = 300 vàng
UPDATE welfare
SET description = 'Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa@ +Mỗi ngày nhận thêm 20 vàng khóa@ (nhận vĩnh viễn)'
WHERE welfare_id = 16;

-- Xem lại sau khi sửa
SELECT * FROM welfare WHERE welfare_id IN (13, 14, 15, 16);
