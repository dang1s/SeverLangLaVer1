-- Kiểm tra dữ liệu shop 105 trong bảng store_data
SELECT id, item_id, store, Bac, lucdao, VangKhoa
FROM langladata.store_data
WHERE store = 105;

-- Kiểm tra xem cột lucdao có dữ liệu không
SELECT COUNT(*) as total_items,
       SUM(CASE WHEN lucdao > 0 THEN 1 ELSE 0 END) as items_with_lucdao,
       SUM(CASE WHEN lucdao = 0 THEN 1 ELSE 0 END) as items_without_lucdao
FROM langladata.store_data
WHERE store = 105;

-- Nếu cần cập nhật lucdao từ VangKhoa cho shop 105
UPDATE langladata.store_data
SET lucdao = VangKhoa
WHERE store = 105 AND lucdao = 0;
