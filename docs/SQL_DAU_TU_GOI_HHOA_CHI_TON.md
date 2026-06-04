# SQL CẤU HÌNH ĐẦU TƯ - GÓI HÀO HOA & CHÍ TÔN

**Ngày cập nhật**: 27/05/2026

---

## 1. SERVER MẪU (D:\langlasever1)

### 1.1 Cấu trúc Database

Server mẫu lưu phúc lợi trong bảng `character` dưới dạng JSON trong cột `phucLoi`.

### 1.2 Bảng phucloi_info (Global Stats)

```sql
-- Tạo bảng lưu thông tin global về phúc lợi
CREATE TABLE IF NOT EXISTS `phucloi_info` (
  `id` int(11) NOT NULL,
  `value` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dữ liệu mặc định
INSERT INTO `phucloi_info` (`id`, `value`) VALUES
(0, 0),   -- TongRank: Tổng số rank
(1, 0),   -- RankCaoNhat: Rank cao nhất
(2, 0),   -- TongDauTu: Tổng lượt đầu tư
(3, 0),   -- TongSoLanMuaTheThang: Tổng lần mua thẻ
(4, 0);   -- ThoiGianX2Online: Thời gian x2 online
```

### 1.3 Bảng phucloi (Templates)

```sql
-- Tạo bảng lưu template phúc lợi
CREATE TABLE IF NOT EXISTS `phucloi` (
  `id` int(11) NOT NULL,
  `str` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

### 1.4 Cấu trúc dữ liệu trong bảng character

```sql
-- Cột phucLoi trong bảng character lưu JSON có cấu trúc:
-- {
--   "isGoiHaoHoa": false,
--   "isGoiChiTon": false,
--   "timeTheThang": -1,
--   "timeTheVinhVinhVien": -1,
--   ...
-- }
```

### 1.5 Giá trị trong bảng phucloi_info

| id | name | Mô tả |
|----|------|-------|
| 0 | TongRank | Tổng số rank (tăng khi user lên rank) |
| 1 | RankCaoNhat | Rank cao nhất đạt được |
| **2** | **TongDauTu** | **Tổng lượt mua gói đầu tư** |
| **3** | **TongSoLanMuaTheThang** | **Tổng lần mua thẻ** |
| 4 | ThoiGianX2Online | Thời gian x2 exp online |

---

## 2. SERVER HIỆN TẠI (E:\SeverLangLaVer1)

### 2.1 Bảng welfare (Danh mục phúc lợi)

```sql
-- Tạo bảng lưu danh sách welfare (danh mục)
CREATE TABLE IF NOT EXISTS `welfare` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `welfare_id` INT NOT NULL COMMENT 'ID welfare (13,14,15,16...)',
  `welfare_type` VARCHAR(50) COMMENT 'Loại: Đầu tư, Thẻ tháng...',
  `welfare_name` VARCHAR(100) COMMENT 'Tên welfare',
  `is_package` BOOLEAN DEFAULT FALSE COMMENT 'Là gói đặc biệt',
  `description` TEXT COMMENT 'Mô tả',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

### 2.2 Bảng phucloi (Template phúc lợi)

```sql
-- Tạo bảng lưu template phúc lợi
CREATE TABLE IF NOT EXISTS `phucloi` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ID_Item` INT NOT NULL COMMENT 'ID item thưởng',
  `ID_PhucLoi` INT NOT NULL COMMENT 'ID welfare cha',
  `soluong` INT DEFAULT 1 COMMENT 'Số lượng',
  `isLock` BOOLEAN DEFAULT FALSE COMMENT 'Khóa hay không',
  `Name` VARCHAR(100) COMMENT 'Tên',
  `strOption` TEXT COMMENT 'Options string',
  `yeucau` INT DEFAULT 0 COMMENT 'Yêu cầu',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

### 2.3 Dữ liệu mẫu cho bảng welfare

```sql
-- Xóa dữ liệu cũ (nếu có)
DELETE FROM welfare;

-- Thêm dữ liệu welfare cho ĐẦU TƯ
INSERT INTO `welfare` (`welfare_id`, `welfare_type`, `welfare_name`, `is_package`, `description`) VALUES
(13, 'Đầu tư', 'Gói hào hoa', TRUE, 'Gói hào hoa - mở khóa quà đầu tư'),
(14, 'Đầu tư', 'Gói chí tôn', TRUE, 'Gói chí tôn - mở khóa quà đầu tư cao cấp');

-- Thêm dữ liệu welfare cho THẺ
INSERT INTO `welfare` (`welfare_id`, `welfare_type`, `welfare_name`, `is_package`, `description`) VALUES
(15, 'Thẻ tháng', 'Thẻ tháng', TRUE, 'Thẻ tháng - nhận quà hàng ngày trong 30 ngày'),
(16, 'Thẻ tháng', 'Thẻ vĩnh viễn', TRUE, 'Thẻ vĩnh viễn - nhận quà hàng ngày vĩnh viễn');

-- Các welfare types khác
INSERT INTO `welfare` (`welfare_id`, `welfare_type`, `welfare_name`, `is_package`, `description`) VALUES
(1, 'Phúc lợi', 'Online ngày', FALSE, 'Nhận quà theo thời gian online'),
(2, 'Phúc lợi', 'Online liên tục', FALSE, 'Nhận quà theo số ngày online liên tục'),
(3, 'Quà nạp', 'Nạp ngày', FALSE, 'Nhận quà theo vàng nạp trong ngày'),
(4, 'Quà nạp', 'Nạp tuần', FALSE, 'Nhận quà theo vàng nạp trong tuần'),
(5, 'Quà nạp', 'Nạp liên tục', FALSE, 'Nhận quà theo số ngày nạp liên tục'),
(6, 'Quà nạp', 'Nạp 3 mốc', FALSE, 'Nhận quà khi nạp đạt 3 mốc'),
(7, 'Quà nạp', 'Nạp đơn', FALSE, 'Nhận quà theo vàng nạp đơn'),
(8, 'Quà nạp', 'Nạp nhiều', FALSE, 'Nhận quà theo tổng vàng nạp'),
(9, 'Quà Rank', 'Rank chung', FALSE, 'Nhận quà theo rank'),
(10, 'Quà Rank', 'Đếm rank', FALSE, 'Nhận quà theo số lần đạt rank');
```

### 2.4 Dữ liệu mẫu cho bảng phucloi

```sql
-- Xóa dữ liệu cũ (nếu có)
DELETE FROM phucloi;

-- Thêm template quà cho Gói Hào Hoa (ID_PhucLoi = 13)
INSERT INTO `phucloi` (`ID_Item`, `ID_PhucLoi`, `soluong`, `isLock`, `Name`, `strOption`, `yeucau`) VALUES
(457, 13, 1, TRUE, 'Bảo Hộ Hào Hoa', '60,50', 0),      -- Item ID 457, +50% bảo hộ
(458, 13, 1, TRUE, 'Ngọc Bảo Hộ', '61,20', 0);         -- Item ID 458, +20% bảo hộ

-- Thêm template quà cho Gói Chí Tôn (ID_PhucLoi = 14)
INSERT INTO `phucloi` (`ID_Item`, `ID_PhucLoi`, `soluong`, `isLock`, `Name`, `strOption`, `yeucau`) VALUES
(459, 14, 1, TRUE, 'Bảo Hộ Chí Tôn', '60,100', 0),     -- Item ID 459, +100% bảo hộ
(460, 14, 1, TRUE, 'Ngọc Bảo Hộ Cao Cấp', '61,50', 0); -- Item ID 460, +50% bảo hộ

-- Thêm template quà cho Thẻ Tháng (ID_PhucLoi = 15)
INSERT INTO `phucloi` (`ID_Item`, `ID_PhucLoi`, `soluong`, `isLock`, `Name`, `strOption`, `yeucau`) VALUES
(192, 15, 30, TRUE, 'Vàng khóa', '', 0);  -- 30 vàng khóa mỗi ngày

-- Thêm template quà cho Thẻ Vĩnh Viễn (ID_PhucLoi = 16)
INSERT INTO `phucloi` (`ID_Item`, `ID_PhucLoi`, `soluong`, `isLock`, `Name`, `strOption`, `yeucau`) VALUES
(192, 16, 50, TRUE, 'Vàng khóa', '', 0),   -- 50 vàng khóa/ngày
(192, 16, 550, TRUE, 'Vàng', '', 0);        -- 550 vàng/ngày
```

---

## 3. SO SÁNH SQL GIỮA 2 SERVER

### 3.1 Bảng welfare

| Khía cạnh | Server Mẫu | Server Hiện Tại |
|-----------|------------|-----------------|
| Bảng welfare | ❌ Không có | ✅ Có |
| Phân loại welfare | ❌ Không có | ✅ Có (welfare_type) |
| Dữ liệu | Lưu trong code/config | Lưu trong database |

### 3.2 Bảng phucloi_info

| Khía cạnh | Server Mẫu | Server Hiện Tại |
|-----------|------------|-----------------|
| Bảng phucloi_info | ✅ Có | ❌ Không có |
| TongDauTu | ✅ Có | ❌ Không có |
| TongSoLanMuaTheThang | ✅ Có | ❌ Không có |

### 3.3 Cấu trúc phucLoi per player

| Khía cạnh | Server Mẫu | Server Hiện Tại |
|-----------|------------|-----------------|
| Lưu trữ | JSON trong character | JSON trong character |
| Tên biến | `isGoiHaoHoa` | `goiHaoHoa` |
| Tên biến | `isGoiChiTon` | `goiChiTon` |

---

## 4. SƠ ĐỒ QUAN HỆ

### Server Mẫu

```
┌─────────────────────────────────────────────────────────────┐
│                      character                              │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ phucLoi: JSON {                                    │  │
│  │   isGoiHaoHoa: boolean,                            │  │
│  │   isGoiChiTon: boolean,                            │  │
│  │   timeTheThang: long,                              │  │
│  │   timeTheVinhVien: long,                           │  │
│  │   vangNapTichLuy: int,                             │  │
│  │   ...                                               │  │
│  │ }                                                   │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                           │
                           │ phucloi_info
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      phucloi_info                          │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ id: 2 = TongDauTu (tổng lượt đầu tư)               │  │
│  │ id: 3 = TongSoLanMuaTheThang (tổng lần mua thẻ)   │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Server Hiện Tại

```
┌─────────────────────────────────────────────────────────────┐
│                      player (character)                     │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ phucLoi: JSON {                                    │  │
│  │   goiHaoHoa: boolean,                              │  │
│  │   goiChiTon: boolean,                             │  │
│  │   theThang: long,                                  │  │
│  │   theVinhVien: long,                               │  │
│  │   napNgay: int,                                    │  │
│  │   ...                                               │  │
│  │ }                                                   │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                           │
                           │ welfare_id FK
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                        welfare                              │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ welfare_id: 13 = Gói hào hoa (type: Đầu tư)       │  │
│  │ welfare_id: 14 = Gói chí tôn (type: Đầu tư)      │  │
│  │ welfare_id: 15 = Thẻ tháng (type: Thẻ tháng)     │  │
│  │ welfare_id: 16 = Thẻ vĩnh viễn (type: Thẻ tháng) │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                           │
                           │ ID_PhucLoi FK
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                        phucloi                              │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ Template quà cho từng welfare                       │  │
│  │ - ID_Item: item thưởng                             │  │
│  │ - soluong: số lượng                                │  │
│  │ - strOption: options                                │  │
│  └─────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. CÁC CÂU LỆNH QUAN TRỌNG

### 5.1 Kiểm tra tổng đầu tư (Server Mẫu)

```sql
-- Xem giá trị TongDauTu
SELECT * FROM phucloi_info WHERE id = 2;

-- Cập nhật TongDauTu (khi user mua gói)
UPDATE phucloi_info SET value = value + 1 WHERE id = 2;
```

### 5.2 Kiểm tra welfare (Server Hiện Tại)

```sql
-- Xem tất cả welfare
SELECT * FROM welfare ORDER BY welfare_type, welfare_id;

-- Xem welfare theo loại
SELECT * FROM welfare WHERE welfare_type = 'Đầu tư';

-- Xem template phúc lợi
SELECT * FROM phucloi;

-- Xem template theo welfare
SELECT * FROM phucloi WHERE ID_PhucLoi = 13;  -- Gói hào hoa
SELECT * FROM phucloi WHERE ID_PhucLoi = 14;  -- Gói chí tôn
```

### 5.3 Cập nhật welfare (Server Hiện Tại)

```sql
-- Thêm welfare mới
INSERT INTO `welfare` (`welfare_id`, `welfare_type`, `welfare_name`, `is_package`, `description`)
VALUES (21, 'Đầu tư', 'Gói VIP mới', TRUE, 'Mô tả gói VIP');

-- Thêm template cho welfare mới
INSERT INTO `phucloi` (`ID_Item`, `ID_PhucLoi`, `soluong`, `isLock`, `Name`, `strOption`, `yeucau`)
VALUES (500, 21, 1, TRUE, 'Item VIP', '60,200', 0);

-- Sửa welfare
UPDATE welfare SET welfare_name = 'Tên mới' WHERE welfare_id = 13;

-- Xóa welfare
DELETE FROM phucloi WHERE ID_PhucLoi = 13;
DELETE FROM welfare WHERE welfare_id = 13;
```

---

## 6. KẾT LUẬN

### Server hiện tại giữ 100 vàng cho Gói Chí Tôn được không?

**✅ ĐƯỢC** - Nếu bạn muốn giữ như vậy thì hoàn toàn ổn.

**Nhưng cần lưu ý:**
1. Khác với server mẫu (300 vàng)
2. User có thể mua cả 2 gói với giá 200 vàng (100 + 100)
3. Nếu muốn đồng bộ → cần sửa code thành 300 vàng

### Cần thêm gì vào Database?

| Thứ tự | Hành động | SQL |
|--------|-----------|-----|
| 1 | Tạo bảng welfare | ✅ Đã có |
| 2 | Tạo bảng phucloi | ✅ Đã có |
| 3 | Thêm dữ liệu welfare | ⚠️ Cần kiểm tra |
| 4 | Thêm template quà | ⚠️ Cần kiểm tra |

---

## 7. FILE LIÊN QUAN

- [So sánh đầu tư](./SO_SANH_DAU_TU_GOI_HHOA_CHI_TON.md)
- [Tài liệu Server Hiện Tại](./HE_THONG_DAU_TU_PHUC_LOI_SERVER_HIEN_TAI.md)
