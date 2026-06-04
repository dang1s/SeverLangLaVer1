# TÀI LIỆU HỆ THỐNG ĐẦU TƯ / PHÚC LỢI - SERVER HIỆN TẠI

**Server**: E:\SeverLangLaVer1\SeverLangLaVer1
**Ngày cập nhật**: 27/05/2026
**Phiên bản**: SG188 LangLa

---

## MỤC LỤC

1. [Tổng quan hệ thống](#1-tổng-quan-hệ-thống)
2. [Cấu trúc kiến trúc](#2-cấu-trúc-kiến-trúc)
3. [Gói Hào Hoa](#3-gói-hào-hoa)
4. [Gói Chí Tôn](#4-gói-chí-tôn)
5. [Thẻ Tháng](#5-thẻ-tháng)
6. [Thẻ Vĩnh Viễn](#6-thẻ-vĩnh-viễn)
7. [Welfare System](#7-welfare-system)
8. [Cấu trúc dữ liệu](#8-cấu-trúc-dữ-liệu)
9. [Database](#9-database)
10. [So sánh với Server Mẫu](#10-so-sánh-với-server-mẫu)
11. [Khuyến nghị](#11-khuyến-nghị)

---

## 1. TỔNG QUAN HỆ THỐNG

### 1.1 Mô tả
Hệ thống Phúc Lợi trong server hiện tại là phiên bản cải tiến với:
- **Service-oriented architecture** với package riêng `com.sg188.PhucLoi`
- **Database-driven configuration** cho phúc lợi và welfare
- **Welfare System** để phân loại phúc lợi theo danh mục
- **ClickEvent integration** để trigger events sau khi mua

### 1.2 Đặc điểm nổi bật

| Tính năng | Mô tả |
|-----------|--------|
| Welfare System | Hệ thống phân loại phúc lợi theo danh mục |
| Database-driven | Cấu hình lưu trong database (phucloi, welfare) |
| ClickEvent | Tích hợp event sau khi mua package |
| JSON Serialization | Dữ liệu player được serialize thành JSON |

---

## 2. CẤU TRÚC KIẾN TRÚC

```
┌─────────────────────────────────────────────────────────────┐
│                     CLIENT                                   │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────────┐      │
│  │ Welfare UI  │  │ Buy Package  │  │ Claim Rewards   │      │
│  └──────┬──────┘  └──────┬───────┘  └────────┬────────┘      │
└─────────┼────────────────┼───────────────────┼───────────────┘
          │                 │                   │
          │ MSG (13,14,15)  │ MSG (15,16,19,20) │ MSG (request)
          ▼                 ▼                   ▼
┌─────────────────────────────────────────────────────────────┐
│                     SERVER                                   │
│  ┌─────────────────────────────────────────────────────┐     │
│  │              Char.java                              │     │
│  │  ├─ subscribeBenefitPackage() - Mua gói           │     │
│  │  ├─ isBenefitPackageActive() - Kiểm tra điều kiện│     │
│  │  ├─ getTextWelfare() - Lấy text mô tả            │     │
│  │  └─ ClickEvent.PhucLoi() - Trigger event          │     │
│  └─────────────────────────────────────────────────────┘     │
│  ┌─────────────────────────────────────────────────────┐     │
│  │           com.sg188.PhucLoi                        │     │
│  │  ┌─────────────┐  ┌──────────────┐  ┌───────────┐ │     │
│  │  │  PhucLoi    │  │  Welfare     │  │TemplatePL │ │     │
│  │  │  (Service)  │  │  (Model)     │  │ (Model)   │ │     │
│  │  └─────────────┘  └──────────────┘  └───────────┘ │     │
│  └─────────────────────────────────────────────────────┘     │
│  ┌─────────────────────────────────────────────────────┐     │
│  │           InfoChar.InfoPhucLoi                      │     │
│  │  (Per-player data model)                           │     │
│  └─────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. GÓI HÀO HOA

### 3.1 Thông tin cơ bản

| Thuộc tính | Giá trị |
|------------|---------|
| **ID** | 13 |
| **Giá** | 100 vàng |
| **Phần thưởng** | 100 vàng khóa |
| **Kiểm tra đã mua** | `phucLoi.goiHaoHoa` |
| **Event** | `ClickEvent.PhucLoi(this)` |
| **Welfare Type** | "Đầu tư" |

### 3.2 Logic mua hàng

**File**: `Char.java` (case 13 trong `subscribeBenefitPackage`)

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
    getService().serverMessage("Mua thành công gói hào hoa");
    ClickEvent.PhucLoi(this);
    break;
```

### 3.3 Kiểm tra điều kiện

**File**: `Char.java` (hàm `isBenefitPackageActive`)

```java
case 13:
    return phucLoi.goiHaoHoa;
```

---

## 4. GÓI CHÍ TÔN

### 4.1 Thông tin cơ bản

| Thuộc tính | Giá trị |
|------------|---------|
| **ID** | 14 |
| **Giá** | 100 vàng |
| **Phần thưởng** | 100 vàng khóa |
| **Kiểm tra đã mua** | `phucLoi.goiChiTon` |
| **Event** | `ClickEvent.PhucLoi(this)` |
| **Welfare Type** | "Đầu tư" |

### 4.2 Logic mua hàng

**File**: `Char.java` (case 14 trong `subscribeBenefitPackage`)

```java
case 14:
    if (phucLoi.goiChiTon) {
        getService().warningMessage("Bạn đã mua gói chí tôn rồi");
        return;
    }
    if (Bag.vang < 100) {
        getService().warningMessage("Bạn không đủ 100 vàng");
        return;
    }
    addVang(-100);
    addVangKhoa(100);
    phucLoi.goiChiTon = true;
    getService().serverMessage("Mua thành công gói chí tôn");
    ClickEvent.PhucLoi(this);
    break;
```

### 4.3 Kiểm tra điều kiện

**File**: `Char.java` (hàm `isBenefitPackageActive`)

```java
case 14:
    return phucLoi.goiChiTon;
```

---

## 5. THẺ THÁNG

### 5.1 Thông tin cơ bản

| Thuộc tính | Giá trị |
|------------|---------|
| **ID** | 15, 19 |
| **Giá** | 100 vàng |
| **Phần thưởng ngay** | 100 vàng khóa |
| **Thời hạn** | 30 ngày |
| **Kiểm tra đã mua** | `phucLoi.theThang > System.currentTimeMillis()` |
| **Welfare Type** | "Thẻ tháng" |

### 5.2 Logic mua hàng

**File**: `Char.java` (case 15, 19 trong `subscribeBenefitPackage`)

```java
case 15:
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
    getService().serverMessage("Mua thành công thẻ tháng");
    ClickEvent.PhucLoi(this);
    break;
```

### 5.3 Kiểm tra điều kiện

```java
case 15:
    return phucLoi.theThang > System.currentTimeMillis();
```

---

## 6. THẺ VĨNH VIỄN

### 6.1 Thông tin cơ bản

| Thuộc tính | Giá trị |
|------------|---------|
| **ID** | 16, 20 |
| **Giá** | 300 vàng |
| **Phần thưởng ngay** | 300 vàng khóa |
| **Thời hạn** | 365 ngày |
| **Kiểm tra đã mua** | `phucLoi.theVinhVien > System.currentTimeMillis()` |
| **Welfare Type** | "Thẻ tháng" |

### 6.2 Logic mua hàng

**File**: `Char.java` (case 16, 20 trong `subscribeBenefitPackage`)

```java
case 16:
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
    getService().serverMessage("Mua thành công thẻ vĩnh viễn");
    ClickEvent.PhucLoi(this);
    break;
```

---

## 7. WELFARE SYSTEM

### 7.1 Giới thiệu

Welfare System là hệ thống phân loại phúc lợi theo danh mục, được quản lý bởi class `Welfare` và `PhucLoi`.

### 7.2 Welfare Model

**File**: `Welfare.java`

```java
public class Welfare {
    private int id;
    private String welfareType;       // Loại: "Đầu tư", "Thẻ tháng", ...
    private int welfareId;            // ID welfare
    private String welfareName;        // Tên welfare
    private boolean isPackage;         // Là gói đặc biệt
    private String description;        // Mô tả
    public List<TemplatePL> item;      // Danh sách items
}
```

### 7.3 PhucLoi Service

**File**: `PhucLoi.java`

```java
public class PhucLoi {
    private static final PhucLoi instance = new PhucLoi();

    public List<TemplatePL> itemPL = new ArrayList<>();
    public Map<String, List<Welfare>> welfareMap = new HashMap<>();

    // Các welfare types được normalize
    // - "Đầu tư" (welfareId: 13, 14)
    // - "Thẻ tháng" (welfareId: 15, 16)
    // - "Quà Rank"
    // - "Quà nạp"
    // - "Phúc lợi"

    public boolean load() {
        // Load từ bảng phucloi
    }

    public void loadWelfare() {
        // Load từ bảng welfare
        // Tự động gán welfare_type cho 13, 14 = "Đầu tư"
        // Tự động gán welfare_type cho 15, 16 = "Thẻ tháng"
    }

    public List<Welfare> getWelfaresByType(String welfareType) { }

    public Set<String> getAllTypes() { }
}
```

### 7.4 TemplatePL Model

**File**: `TemplatePL.java`

```java
public class TemplatePL {
    public int Id;              // ID template
    public int IdItem;          // ID item
    public int IDPhucLoi;       // ID welfare cha
    public boolean isLock;      // Khóa hay không
    public String name;         // Tên
    public String strOption;    // Options string
    public int Amount;          // Số lượng
    public int yeucau;         // Yêu cầu
}
```

### 7.5 Welfare Types

| Welfare ID | Type | Tên |
|------------|------|-----|
| 13 | Đầu tư | Gói hào hoa |
| 14 | Đầu tư | Gói chí tôn |
| 15 | Thẻ tháng | Thẻ tháng |
| 16 | Thẻ tháng | Thẻ vĩnh viễn |

---

## 8. CẤU TRÚC DỮ LIỆU

### 8.1 InfoPhucLoi (Per Player)

**File**: `InfoChar/InfoPhucLoi.java`

```java
public class InfoPhucLoi {
    // Thời gian
    public int timeOnline;
    public int soNgayOnline;

    // Tiền tiêu
    public int tieuNgay;
    public int tieuTuan;

    // Gói đặc biệt
    public boolean goiChiTon;
    public boolean goiHaoHoa;

    // Tiền nạp
    public int napNgay;
    public int napDon;
    public int nap3moc;
    public int napTuan;
    public int napLienTuc;

    // Thẻ
    public long theThang = -1;
    public long theVinhVien = -1;

    // Danh sách
    public List<TemplatePL> listPl = new ArrayList<>();
    public List<Integer> listnap = new ArrayList<>();

    // NOTE: Vòng quay nạp DISABLED
}
```

### 8.2 JSON Serialization

```java
public JSONObject toJSONObject() {
    JSONObject obj = new JSONObject();
    obj.put("timeOnline", this.timeOnline);
    obj.put("songayonline", this.soNgayOnline);
    obj.put("tieungay", this.tieuNgay);
    obj.put("tieutuan", this.tieuTuan);
    obj.put("goichiton", this.goiChiTon);
    obj.put("goihaohoa", this.goiHaoHoa);
    obj.put("napngay", this.napNgay);
    obj.put("naptuan", this.napTuan);
    obj.put("naplientuc", this.napLienTuc);
    obj.put("thethang", this.theThang);
    obj.put("thevinhvien", this.theVinhVien);
    obj.put("napdon", this.napDon);
    obj.put("nap3moc", this.nap3moc);
    JSONArray jsonArray = new JSONArray();
    jsonArray.addAll(listnap);
    obj.put("listnap", jsonArray);
    return obj;
}
```

---

## 9. DATABASE

### 9.1 Bảng phucloi

Lưu trữ template phúc lợi.

| Column | Type | Mô tả |
|--------|------|--------|
| id | INT | ID template |
| ID_Item | INT | ID item thưởng |
| ID_PhucLoi | INT | ID welfare cha |
| soluong | INT | Số lượng |
| isLock | BOOLEAN | Khóa |
| Name | VARCHAR | Tên |
| strOption | TEXT | Options |
| yeucau | INT | Yêu cầu |

### 9.2 Bảng welfare

Lưu trữ danh sách welfare.

| Column | Type | Mô tả |
|--------|------|--------|
| id | INT | ID |
| welfare_id | INT | ID welfare |
| welfare_type | VARCHAR | Loại welfare |
| welfare_name | VARCHAR | Tên welfare |
| is_package | BOOLEAN | Là gói đặc biệt |
| description | TEXT | Mô tả |

### 9.3 Mẫu dữ liệu welfare

```sql
-- Gói Hào Hoa
INSERT INTO welfare (welfare_id, welfare_type, welfare_name, is_package)
VALUES (13, 'Đầu tư', 'Gói hào hoa', true);

-- Gói Chí Tôn
INSERT INTO welfare (welfare_id, welfare_type, welfare_name, is_package)
VALUES (14, 'Đầu tư', 'Gói chí tôn', true);

-- Thẻ Tháng
INSERT INTO welfare (welfare_id, welfare_type, welfare_name, is_package)
VALUES (15, 'Thẻ tháng', 'Thẻ tháng', true);

-- Thẻ Vĩnh Viễn
INSERT INTO welfare (welfare_id, welfare_type, welfare_name, is_package)
VALUES (16, 'Thẻ tháng', 'Thẻ vĩnh viễn', true);
```

---

## 10. SO SÁNH VỚI SERVER MẪU

### 10.1 Bảng so sánh tổng quan

| Tính năng | Server Mẫu | Server Hiện Tại |
|-----------|------------|-----------------|
| Kiến trúc | Object-oriented | Service-oriented |
| Package | `com.langla.real.phucloi` | `com.sg188.PhucLoi` |
| Welfare System | Không có | Có |
| ClickEvent | Không có | Có |
| Vòng Quay Nạp | Hoạt động | Disabled |
| MKMBV Check | Có | Không |

### 10.2 So sánh Gói Hào Hoa

| Thuộc tính | Server Mẫu | Server Hiện Tại |
|------------|------------|-----------------|
| ID | 13 | 13 |
| Message ID | -63 | 13 |
| Giá | 100 vàng | 100 vàng |
| Phần thưởng | 100 vàng khóa | 100 vàng khóa |
| Tracking | TongDauTu++ | Không |
| MKMBV | Có | Không |
| ClickEvent | Không | Có |

### 10.3 So sánh Gói Chí Tôn

| Thuộc tính | Server Mẫu | Server Hiện Tại |
|------------|------------|-----------------|
| ID | 14 | 14 |
| Message ID | -62 | 14 |
| **Giá** | **300 vàng** | **100 vàng** |
| Phần thưởng | 300 vàng khóa | 100 vàng khóa |
| Tracking | TongDauTu++ | Không |
| MKMBV | Có | Không |
| ClickEvent | Không | Có |

### 10.4 So sánh Thẻ Tháng

| Thuộc tính | Server Mẫu | Server Hiện Tại |
|------------|------------|-----------------|
| ID | 15 | 15, 19 |
| Message ID | -66 | 15, 19 |
| Giá | 100 vàng | 100 vàng |
| Thời hạn | 30 ngày | 30 ngày |
| Tracking | TongSoLanMuaTheThang++ | Không |
| Nhận ngày | 30 vàng khóa/ngày | Không cài |

### 10.5 So sánh Thẻ Vĩnh Viễn

| Thuộc tính | Server Mẫu | Server Hiện Tại |
|------------|------------|-----------------|
| ID | 16 | 16, 20 |
| Message ID | -65 | 16, 20 |
| Giá | 300 vàng | 300 vàng |
| Thời hạn | 365 ngày | 365 ngày |
| Phần thưởng ngày | 50 vàng + 550 vàng | Không cài |

---

## 11. KHUYẾN NGHỊ

### 11.1 Sửa lỗi hiện tại

#### Sửa giá Gói Chí Tôn
**File**: `Char.java` (line 14222)

```java
// Hiện tại:
if (Bag.vang < 100) {
    getService().warningMessage("Bạn không đủ 100 vàng");
    return;
}

// Sửa thành:
if (Bag.vang < 300) {
    getService().warningMessage("Bạn không đủ 300 vàng");
    return;
}
```

### 11.2 Thêm tính năng còn thiếu

#### Thêm Global Tracking
**File**: Tạo class mới `PhucLoiInfo.java` trong `InfoChar`

```java
package InfoChar;

public class PhucLoiInfo {
    public static int tongDauTu = 0;
    public static int tongSoLanMuaTheThang = 0;
    public static int tongRank = 0;
    public static int rankCaoNhat = 0;
}
```

#### Thêm Tracking khi mua
**File**: `Char.java`

```java
case 13: // Gói Hào Hoa
    // ... existing code ...
    PhucLoiInfo.tongDauTu++;
    break;

case 14: // Gói Chí Tôn
    // ... existing code ...
    PhucLoiInfo.tongDauTu++;
    break;

case 15: // Thẻ Tháng
case 19:
    // ... existing code ...
    PhucLoiInfo.tongSoLanMuaTheThang++;
    break;
```

### 11.3 Cài đặt phần thưởng hàng ngày

#### Thẻ Tháng - Nhận 30 vàng/ngày
Thêm vào hàm update hàng ngày:

```java
public void updateDailyRewards() {
    // Thẻ tháng
    if (phucLoi.theThang > System.currentTimeMillis()) {
        addVangKhoa(30, true, true, "Thẻ tháng ngày");
    }

    // Thẻ vĩnh viễn
    if (phucLoi.theVinhVien > System.currentTimeMillis()) {
        addVangKhoa(50, true, true, "Thẻ vĩnh viễn ngày");
        addVang(550, true, true, "Thẻ vĩnh viễn ngày");
    }
}
```

### 11.4 Bật lại Vòng Quay Nạp

Uncomment các trường trong `InfoPhucLoi.java`:

```java
// public long vongQuayNapTichLuy;
// public long luotQuayVongXoay;
// public int daQuayVongXoay;
// public int vongQuayNapSeasonId;
```

Thêm message handler trong `Session.java`:

```java
case -60:
    // Vòng quay nạp logic
    break;
```

---

## 12. FILE LIÊN QUAN

| File | Đường dẫn | Mô tả |
|------|-----------|-------|
| Char.java | `src/main/java/com/sg188/real/Char.java` | Logic mua/kiểm tra |
| PhucLoi.java | `src/main/java/com/sg188/PhucLoi/PhucLoi.java` | Service quản lý |
| Welfare.java | `src/main/java/com/sg188/PhucLoi/Welfare.java` | Model welfare |
| TemplatePL.java | `src/main/java/com/sg188/PhucLoi/TemplatePL.java` | Template phúc lợi |
| InfoPhucLoi.java | `src/main/java/InfoChar/InfoPhucLoi.java` | Per-player data |

---

## 13. MESSAGE PROTOCOL

| Message ID | Hành động | Mô tả |
|------------|-----------|--------|
| 13 | Mua Gói Hào Hoa | Subscribe benefit package |
| 14 | Mua Gói Chí Tôn | Subscribe benefit package |
| 15 | Mua Thẻ Tháng | Subscribe benefit package |
| 16 | Mua Thẻ Vĩnh Viễn | Subscribe benefit package |
| 19 | Mua Thẻ Tháng (alt) | Subscribe benefit package |
| 20 | Mua Thẻ Vĩnh Viễn (alt) | Subscribe benefit package |

---

## 14. GETTEXTWELFARE

Hàm `getTextWelfare` trong `Char.java` dùng để lấy text mô tả với tham số động:

```java
public String getTextWelfare(int id, String description) {
    switch (id) {
        case 0: return String.format(description, phucLoi.timeOnline / 60000);
        case 1: return String.format(description, phucLoi.soNgayOnline);
        case 2: return String.format(description, level());
        case 3: return String.format(description, phucLoi.tieuNgay);
        case 4: return String.format(description, phucLoi.tieuTuan);
        case 5: return String.format(description, phucLoi.napNgay);
        case 6: return String.format(description, phucLoi.napTuan);
        case 7: return String.format(description, phucLoi.napLienTuc);
        case 8: return String.format(description, phucLoi.nap3moc);
        case 9: return String.format(description, phucLoi.napDon);
        case 10: return String.format(description, Bag.pointNAP);
        case 11: return String.format(description, Manager.gI().rankCaoNhat);
        case 12: return String.format(description, Manager.gI().countRank);
    }
    return description;
}
```

---

## 15. CHÚ Ý QUAN TRỌNG

1. **Vòng Quay Nạp Disabled**: Code đã được comment out, cần bật lại nếu cần

2. **Không có Global Tracking**: Server hiện tại không có `PhucLoiInfo` như server mẫu

3. **Giá Gói Chí Tôn**: Khác với server mẫu (100 vs 300)

4. **ClickEvent Integration**: Server hiện tại gọi `ClickEvent.PhucLoi()` sau khi mua thành công

5. **Welfare System**: Hệ thống phân loại mới, cần cập nhật database để sử dụng đầy đủ
