# HƯỚNG DẪN SỬA CODE ĐẦU TƯ - SỬ DỤNG PHUCLOI_INFO

**Ngày**: 27/05/2026
**Mục tiêu**: Sửa đầu tư như server mẫu, sử dụng phucloi_info

---

## TÓM TẮT THAY ĐỔI

### Database:
- Tạo bảng `phucloi_info` để tracking TongDauTu

### Java:
- Tạo class `PhucLoiInfo.java`
- Sửa `Char.java` để sử dụng PhucLoiInfo

---

## PHẦN 1: CHẠY SQL

**File**: `docs\SQL_DAU_TU_PHUCLOI_INFO.sql`

```sql
-- Chạy toàn bộ file SQL này trong phpMyAdmin
```

---

## PHẦN 2: TẠO FILE MỚI

### 2.1 Tạo file `PhucLoiInfo.java`

**Đường dẫn**: `src\main\java\InfoChar\PhucLoiInfo.java`

**Nội dung đã tạo sẵn** trong file này.

---

## PHẦN 3: SỬA CODE TRONG CHAR.JAVA

### 3.1 File cần sửa

```
src\main\java\com\sg188\real\Char.java
```

### 3.2 Thêm import (đầu file)

```java
import InfoChar.PhucLoiInfo;
```

### 3.3 Sửa method `subscribeBenefitPackage`

**Tìm đến case 13 (Gói Hào Hoa)** - Thêm tracking:

```java
case 13:
    if (phucLoi.goiHaoHoa) {
        getService().warningMessage("Bạn đã mua gói hào hoa rồi");
        return;
    }
    if (Bag.vang < 100) {
        getService().warningMessage("Bạn không đủ 100 vàng");
        return;
    }
    addVang(-100);
    addVangKhoa(100);
    phucLoi.goiHaoHoa = true;
    // ========== THÊM: Tracking TongDauTu ==========
    PhucLoiInfo.gI().tangTongDauTu();
    // ===============================================
    getService().serverMessage("Mua thành công gói hào hoa");
    ClickEvent.PhucLoi(this);
    break;
```

**Tìm đến case 14 (Gói Chí Tôn)** - Sửa giá + thêm tracking:

```java
case 14:
    if (phucLoi.goiChiTon) {
        getService().warningMessage("Bạn đã mua gói chí tôn rồi");
        return;
    }
    // ========== SỬA: Đổi 100 thành 300 ==========
    if (Bag.vang < 300) {
        getService().warningMessage("Bạn không đủ 300 vàng");
        return;
    }
    addVang(-300);          // Sửa từ -100 thành -300
    addVangKhoa(300);     // Sửa từ +100 thành +300
    // ===============================================
    phucLoi.goiChiTon = true;
    // ========== THÊM: Tracking TongDauTu ==========
    PhucLoiInfo.gI().tangTongDauTu();
    // ===============================================
    getService().serverMessage("Mua thành công gói chí tôn");
    ClickEvent.PhucLoi(this);
    break;
```

### 3.4 Sửa case 19 (Thẻ Tháng) - Thêm tracking

```java
case 19:
    if (phucLoi.theThang > System.currentTimeMillis()) {
        getService().warningMessage("Bạn đã mua thẻ tháng rồi");
        return;
    }
    if (Bag.vang < 100) {
        getService().warningMessage("Bạn không đủ 100 vàng");
        return;
    }
    addVang(-100);
    addVangKhoa(100);
    phucLoi.theThang = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30);
    // ========== THÊM: Tracking TongSoLanMuaTheThang ==========
    PhucLoiInfo.gI().tangTongSoLanMuaTheThang();
    // ===========================================================
    getService().serverMessage("Mua thành công thẻ tháng");
    ClickEvent.PhucLoi(this);
    break;
```

### 3.5 Sửa case 20 (Thẻ Vĩnh Viễn) - Thêm tracking

```java
case 20:
    if (phucLoi.theVinhVien > System.currentTimeMillis()) {
        getService().warningMessage("Bạn đã mua thẻ vĩnh viễn rồi");
        return;
    }
    if (Bag.vang < 300) {
        getService().warningMessage("Bạn không đủ 300 vàng");
        return;
    }
    addVang(-300);
    addVangKhoa(300);
    phucLoi.theVinhVien = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365);
    // ========== THÊM: Tracking TongSoLanMuaTheThang ==========
    PhucLoiInfo.gI().tangTongSoLanMuaTheThang();
    // ===========================================================
    getService().serverMessage("Mua thành công thẻ vĩnh viễn");
    ClickEvent.PhucLoi(this);
    break;
```

---

## PHẦN 4: KHỞI TẠO KHI SERVER START

### 4.1 Tìm file khởi tạo server

Thường là `Server.java` hoặc `Main.java` hoặc class quản lý server.

### 4.2 Thêm vào hàm khởi tạo

```java
// Load PhucLoiInfo khi server start
PhucLoiInfo.gI().load();
```

---

## PHẦN 5: TÓM TẮT THAY ĐỔI

### Files tạo mới:

| File | Đường dẫn |
|------|-----------|
| `PhucLoiInfo.java` | `src\main\java\InfoChar\PhucLoiInfo.java` |

### Files sửa:

| File | Thay đổi |
|------|----------|
| `Char.java` | Thêm import, sửa case 13, 14, 19, 20 |
| `Server.java` | Thêm `PhucLoiInfo.gI().load()` |

### Database:

| Bảng | Thay đổi |
|------|----------|
| `phucloi_info` | Tạo mới với TongDauTu |
| `welfare` | Cập nhật mô tả Gói Hào Hoa, Gói Chí Tôn |
| `phucloi` | Cập nhật item 858, 859 |

---

## PHẦN 6: KIỂM TRA

### Sau khi mua Gói Hào Hoa:
```sql
SELECT * FROM phucloi_info WHERE id = 2;
-- TongDauTu nên tăng lên 1
```

### Sau khi mua Gói Chí Tôn:
```sql
SELECT * FROM phucloi_info WHERE id = 2;
-- TongDauTu nên tăng lên 2
```

### Sau khi mua Thẻ Tháng:
```sql
SELECT * FROM phucloi_info WHERE id = 3;
-- TongSoLanMuaTheThang nên tăng lên 1
```

---

## FILE LIÊN QUAN

- [SQL Đầu Tư](./SQL_DAU_TU_PHUCLOI_INFO.sql)
- [PhucLoiInfo.java](./PhucLoiInfo.java)
