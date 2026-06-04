# HƯỚNG DẪN SỬA CODE ĐẦU TƯ - SERVER HIỆN TẠI

**Ngày**: 27/05/2026
**Mục tiêu**: Sửa đầu tư y hệt server mẫu

---

## TÓM TẮT

| Gói | Giá hiện tại | Giá đúng (server mẫu) | Cần sửa? |
|------|---------------|------------------------|-----------|
| **Gói Hào Hoa** | 100 vàng | 100 vàng | ✅ Không |
| **Gói Chí Tôn** | 100 vàng | **300 vàng** | ❌ **CÓ** |

---

## PHẦN 1: CHẠY SQL

### 1.1 Chạy file SQL đã tạo

**File**: `E:\SeverLangLaVer1\SeverLangLaVer1\docs\SQL_DAU_TU_CHI_TIET.sql`

Chạy trong phpMyAdmin hoặc MySQL.

### 1.2 SQL cần chạy (nội dung)

```sql
-- Cập nhật mô tả Gói Hào Hoa
UPDATE welfare SET 
    description = 'Giá : 100 vàng @ Phần thưởng sau khi mua: @ +100 vàng khóa @ Mở khóa quà đầu tư'
WHERE welfare_id = 13;

-- Cập nhật mô tả Gói Chí Tôn
UPDATE welfare SET 
    description = 'Giá : 300 vàng @ Phần thưởng sau khi mua: @ +300 vàng khóa @ Mở khóa quà đầu tư cao cấp'
WHERE welfare_id = 14;

-- Xóa item cũ
DELETE FROM phucloi WHERE ID_PhucLoi IN (13, 14);

-- Thêm item mới cho Gói Hào Hoa
INSERT INTO phucloi (ID_PhucLoi, ID_Item, Name, strOption, isLock, soluong, yeucau)
VALUES (13, 858, 'Gói hào hoa', '', 1, 1, 0);

-- Thêm item mới cho Gói Chí Tôn
INSERT INTO phucloi (ID_PhucLoi, ID_Item, Name, strOption, isLock, soluong, yeucau)
VALUES (14, 859, 'Gói chí tôn', '', 1, 1, 0);
```

---

## PHẦN 2: SỬA CODE

### 2.1 File cần sửa

```
E:\SeverLangLaVer1\SeverLangLaVer1\src\main\java\com\sg188\real\Char.java
```

### 2.2 Tìm đến method `subscribeBenefitPackage` (line ~14200)

### 2.3 Sửa case 14 - Gói Chí Tôn

**TRƯỚC (line 14222-14227)**:
```java
case 14:
    if (phucLoi.goiChiTon) {
        getService().warningMessage("Bạn đã mua gói chí tôn rồi");
        return;
    }
    if (Bag.vang < 100) {  // ❌ SAI
        getService().warningMessage("Bạn không đủ 100 vàng");
        return;
    }
    addVang(-100);         // ❌ SAI
    addVangKhoa(100);     // ❌ SAI
    phucLoi.goiChiTon = true;
    getService().serverMessage("Mua thành công gói chí tôn");
    ClickEvent.PhucLoi(this);
    break;
```

**SAU (đúng - y hệt server mẫu)**:
```java
case 14:
    if (phucLoi.goiChiTon) {
        getService().warningMessage("Bạn đã mua gói chí tôn rồi");
        return;
    }
    if (Bag.vang < 300) {  // ✅ ĐÚNG: 300 vàng
        getService().warningMessage("Bạn không đủ 300 vàng");
        return;
    }
    addVang(-300);         // ✅ ĐÚNG: Trừ 300 vàng
    addVangKhoa(300);     // ✅ ĐÚNG: Nhận 300 vàng khóa
    phucLoi.goiChiTon = true;
    getService().serverMessage("Mua thành công gói chí tôn");
    ClickEvent.PhucLoi(this);
    break;
```

---

## PHẦN 3: TÓM TẮT THAY ĐỔI

### File: `Char.java`

| Vị trí | Thay đổi | Từ | Thành |
|---------|-----------|-----|-------|
| Line 14222 | Kiểm tra vàng | `< 100` | `< 300` |
| Line 14223 | Thông báo | `"100 vàng"` | `"300 vàng"` |
| Line 14226 | Trừ vàng | `-100` | `-300` |
| Line 14227 | Nhận vàng khóa | `+100` | `+300` |

---

## PHẦN 4: KIỂM TRA

### Test case:

1. **Mua Gói Hào Hoa**:
   - User có đủ 100 vàng → ✅ Trừ 100 vàng → Nhận 100 vàng khóa → `goiHaoHoa = true`

2. **Mua Gói Chí Tôn**:
   - User có đủ 300 vàng → ✅ Trừ 300 vàng → Nhận 300 vàng khóa → `goiChiTon = true`
   - User có 100 vàng → ❌ Báo "Không đủ 300 vàng"

---

## FILE LIÊN QUAN

- [SQL Đầu Tư](./SQL_DAU_TU_CHI_TIET.sql)
- [Tài liệu Server Hiện Tại](./HE_THONG_DAU_TU_PHUC_LOI_SERVER_HIEN_TAI.md)
- [So sánh đầu tư](./SO_SANH_DAU_TU_GOI_HHOA_CHI_TON.md)
