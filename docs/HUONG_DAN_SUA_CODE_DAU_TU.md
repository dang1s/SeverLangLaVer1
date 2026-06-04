# HƯỚNG DẪN SỬA CODE ĐẦU TƯ - SERVER HIỆN TẠI = SERVER MẪU

**Ngày**: 27/05/2026
**Mục tiêu**: Làm đầu tư y hệt server mẫu nhưng logic hợp với server hiện tại

---

## TÓM TẮT THAY ĐỔI

| Gói | Server Mẫu | Server Hiện Tại (Cần sửa) |
|------|-------------|---------------------------|
| **Gói Hào Hoa** | 100 vàng | Giữ nguyên 100 vàng ✅ |
| **Gói Chí Tôn** | 300 vàng | 100 vàng → **Sửa thành 300 vàng** |

---

## PHẦN 1: SỬA CODE TRONG Char.java

### 1.1 Tìm file cần sửa

```
E:\SeverLangLaVer1\SeverLangLaVer1\src\main\java\com\sg188\real\Char.java
```

### 1.2 Tìm method `subscribeBenefitPackage()`

Tìm đến case 13 và case 14 trong method `subscribeBenefitPackage()`.

### 1.3 Sửa Gói Hào Hoa (case 13) - ✅ GIỮ NGUYÊN

Code hiện tại đã đúng, không cần sửa:

```java
case 13:
    if (phucLoi.goiHaoHoa) {
        getService().warningMessage("Bạn đã mua gói hào hoa rồi");
        return;
    }
    if (Bag.vang < 100) {  // ✅ Đúng: 100 vàng
        getService().warningMessage("Bạn không đủ 100 vàng");
        return;
    }
    addVang(-100);          // ✅ Đúng: Trừ 100 vàng
    addVangKhoa(100);       // ✅ Đúng: Nhận 100 vàng khóa
    phucLoi.goiHaoHoa = true;
    getService().serverMessage("Mua thành công gói hào hoa");
    ClickEvent.PhucLoi(this);
    break;
```

### 1.4 Sửa Gói Chí Tôn (case 14) - ❌ CẦN SỬA

**TRƯỚC (sai):**
```java
case 14:
    if (phucLoi.goiChiTon) {
        getService().warningMessage("Bạn đã mua gói chí tôn rồi");
        return;
    }
    if (Bag.vang < 100) {  // ❌ SAI: Phải là 300
        getService().warningMessage("Bạn không đủ 100 vàng");
        return;
    }
    addVang(-100);          // ❌ SAI: Phải là -300
    addVangKhoa(100);      // ❌ SAI: Phải là +300
    phucLoi.goiChiTon = true;
    getService().serverMessage("Mua thành công gói chí tôn");
    ClickEvent.PhucLoi(this);
    break;
```

**SAU (đúng - giống server mẫu):**
```java
case 14:
    if (phucLoi.goiChiTon) {
        getService().warningMessage("Bạn đã mua gói chí tôn rồi");
        return;
    }
    if (Bag.vang < 300) {  // ✅ SỬA: 300 vàng
        getService().warningMessage("Bạn không đủ 300 vàng");
        return;
    }
    addVang(-300);          // ✅ SỬA: Trừ 300 vàng
    addVangKhoa(300);       // ✅ SỬA: Nhận 300 vàng khóa
    phucLoi.goiChiTon = true;
    getService().serverMessage("Mua thành công gói chí tôn");
    ClickEvent.PhucLoi(this);
    break;
```

---

## PHẦN 2: THÊM TRACKING TongDauTu

### 2.1 Tạo class PhucLoiInfo

**Tạo file mới**: `src/main/java/InfoChar/PhucLoiInfo.java`

```java
package InfoChar;

import SqlConnection.MySQL;

public class PhucLoiInfo {

    public static int TongDauTu = 0;
    public static int TongSoLanMuaTheThang = 0;
    public static int TongRank = 0;
    public static int RankCaoNhat = 0;
    public static long ThoiGianX2Online = 0;

    public static void load() {
        try {
            var rs = MySQL.query("SELECT * FROM phucloi_info");
            while (rs.next()) {
                int id = rs.getInt("id");
                long value = rs.getLong("value");
                switch (id) {
                    case 0 -> TongRank = (int) value;
                    case 1 -> RankCaoNhat = (int) value;
                    case 2 -> TongDauTu = (int) value;
                    case 3 -> TongSoLanMuaTheThang = (int) value;
                    case 4 -> ThoiGianX2Online = value;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            for (int i = 0; i <= 4; i++) {
                long value = switch (i) {
                    case 0 -> TongRank;
                    case 1 -> RankCaoNhat;
                    case 2 -> TongDauTu;
                    case 3 -> TongSoLanMuaTheThang;
                    case 4 -> ThoiGianX2Online;
                    default -> 0;
                };
                MySQL.execute("UPDATE phucloi_info SET value = ? WHERE id = ?", value, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void update(int id, int value) {
        try {
            MySQL.execute("UPDATE phucloi_info SET value = ? WHERE id = ?", value, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

### 2.2 Cập nhật Char.java - Thêm tracking khi mua

**Thêm import** (nếu cần):
```java
import InfoChar.PhucLoiInfo;
```

**Sửa case 13 (Gói Hào Hoa)** - Thêm tracking:
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
    // ✅ THÊM: Tracking TongDauTu
    PhucLoiInfo.TongDauTu++;
    PhucLoiInfo.update(2, PhucLoiInfo.TongDauTu);
    getService().serverMessage("Mua thành công gói hào hoa");
    ClickEvent.PhucLoi(this);
    break;
```

**Sửa case 14 (Gói Chí Tôn)** - Thêm tracking:
```java
case 14:
    if (phucLoi.goiChiTon) {
        getService().warningMessage("Bạn đã mua gói chí tôn rồi");
        return;
    }
    if (Bag.vang < 300) {
        getService().warningMessage("Bạn không đủ 300 vàng");
        return;
    }
    addVang(-300);
    addVangKhoa(300);
    phucLoi.goiChiTon = true;
    // ✅ THÊM: Tracking TongDauTu
    PhucLoiInfo.TongDauTu++;
    PhucLoiInfo.update(2, PhucLoiInfo.TongDauTu);
    getService().serverMessage("Mua thành công gói chí tôn");
    ClickEvent.PhucLoi(this);
    break;
```

### 2.3 Cập nhật case Thẻ Tháng (nếu muốn tracking)

```java
case 19:
    // Thẻ tháng
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
    // ✅ THÊM: Tracking TongSoLanMuaTheThang
    PhucLoiInfo.TongSoLanMuaTheThang++;
    PhucLoiInfo.update(3, PhucLoiInfo.TongSoLanMuaTheThang);
    getService().serverMessage("Mua thành công thẻ tháng");
    ClickEvent.PhucLoi(this);
    break;
```

---

## PHẦN 3: KHỞI TẠO KHI SERVER START

### 3.1 Tìm file init/start server

Thêm vào hàm khởi tạo server (thường trong `Server.java` hoặc `Main.java`):

```java
// Load PhucLoiInfo khi server start
PhucLoiInfo.load();
```

---

## PHẦN 4: CHẠY SQL

### 4.1 Chạy file SQL đã tạo

```bash
# Chạy file SQL trong MySQL
mysql -u username -p database_name < SQL_CAP_NHAT_DAU_TU_HIEN_TAI_NHU_MAU.sql
```

Hoặc import file `SQL_CAP_NHAT_DAU_TU_HIEN_TAI_NHU_MAU.sql` qua phpMyAdmin.

---

## TÓM TẮT THAY ĐỔI

### File cần sửa:

| File | Thay đổi |
|------|-----------|
| `Char.java` | Sửa case 14: 100→300 vàng |
| `Char.java` | Thêm tracking TongDauTu vào case 13, 14, 19 |
| `PhucLoiInfo.java` | **Tạo mới** - Class global stats |
| `Server.java` | Thêm `PhucLoiInfo.load()` khi start |

### Database cần cập nhật:

| Bảng | Thay đổi |
|------|-----------|
| `welfare` | Cập nhật mô tả cho welfare_id 13, 14 |
| `phucloi` | Giữ nguyên |
| `phucloi_info` | **Tạo mới** - Bảng global stats |

---

## KIỂM TRA SAU KHI SỬA

### Test case:

1. **Mua Gói Hào Hoa**:
   - User có đủ 100 vàng
   - Trừ 100 vàng
   - Nhận 100 vàng khóa
   - `goiHaoHoa = true`
   - `TongDauTu++`

2. **Mua Gói Chí Tôn**:
   - User có đủ 300 vàng
   - Trừ 300 vàng
   - Nhận 300 vàng khóa
   - `goiChiTon = true`
   - `TongDauTu++`

3. **Kiểm tra Database**:
   ```sql
   SELECT * FROM phucloi_info WHERE id = 2;
   -- TongDauTu nên tăng sau mỗi lần mua
   ```

---

## FILE LIÊN QUAN

- [SQL cập nhật](./SQL_CAP_NHAT_DAU_TU_HIEN_TAI_NHU_MAU.sql)
- [So sánh đầu tư](./SO_SANH_DAU_TU_GOI_HHOA_CHI_TON.md)
- [Tài liệu Server Hiện Tại](./HE_THONG_DAU_TU_PHUC_LOI_SERVER_HIEN_TAI.md)
