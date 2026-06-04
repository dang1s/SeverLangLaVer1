# TÀI LIỆU SO SÁNH HỆ THỐNG ĐẦU TƯ / PHÚC LỢI

**Ngày cập nhật**: 27/05/2026
**Server Mẫu**: D:\langlasever1
**Server Hiện Tại**: E:\SeverLangLaVer1\SeverLangLaVer1

---

## MỤC LỤC

1. [Tổng quan so sánh](#1-tổng-quan-so-sánh)
2. [So sánh kiến trúc](#2-so-sánh-kiến-trúc)
3. [So sánh Gói Hào Hoa](#3-so-sánh-gói-hào-hoa)
4. [So sánh Gói Chí Tôn](#4-so-sánh-gói-chí-tôn)
5. [So sánh Thẻ Tháng](#5-so-sánh-thẻ-tháng)
6. [So sánh Thẻ Vĩnh Viễn](#6-so-sánh-thẻ-vĩnh-viễn)
7. [So sánh Vòng Quay Nạp](#7-so-sánh-vòng-quay-nạp)
8. [So sánh Data Model](#8-so-sánh-data-model)
9. [Danh sách thay đổi cần thực hiện](#9-danh-sách-thay-đổi-cần-thực-hiện)

---

## 1. TỔNG QUAN SO SÁNH

| Tiêu chí | Server Mẫu | Server Hiện Tại | Khác biệt |
|-----------|------------|-----------------|-----------|
| **Kiến trúc** | Object-oriented thuần | Service-oriented | Khác |
| **Package** | `com.langla.real.phucloi` | `com.sg188.PhucLoi` | Khác |
| **Welfare System** | Không có | Có | Mới |
| **ClickEvent** | Không có | Có | Mới |
| **Vòng Quay Nạp** | Hoạt động | Disabled | Thiếu |
| **MKMBV Check** | Có | Không | Thiếu |
| **Global Tracking** | Có | Không | Thiếu |
| **Giá Gói Chí Tôn** | 300 vàng | 100 vàng | **Khác lớn** |

---

## 2. SO SÁNH KIẾN TRÚC

### 2.1 Server Mẫu

```
┌────────────────────────────────────────────────────────────┐
│ Session.java                                               │
│ ├─ case -63: Mua Gói Hào Hoa                            │
│ ├─ case -62: Mua Gói Chí Tôn                            │
│ ├─ case -65: Mua Thẻ Vĩnh Viễn                         │
│ ├─ case -66: Mua Thẻ Tháng                              │
│ ├─ case -60: Vòng Quay Nạp                              │
│ └─ case -70: Nhận Phúc Lợi                             │
└────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────────┐
│ Char.java                                                 │
│ ├─ phucLoi: PhucLoi (per player)                       │
│ ├─ isCheckPhucLoi()                                     │
│ ├─ nhanPhucLoi()                                        │
│ └─ writePhucLoi()                                       │
└────────────────────────────────────────────────────────────┘
```

### 2.2 Server Hiện Tại

```
┌────────────────────────────────────────────────────────────┐
│ Char.java                                                 │
│ ├─ subscribeBenefitPackage()                             │
│ ├─ isBenefitPackageActive()                              │
│ ├─ getTextWelfare()                                      │
│ └─ ClickEvent.PhucLoi() ◄─── MỚI                        │
└────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌────────────────────────────────────────────────────────────┐
│ com.sg188.PhucLoi ◄─── MỚI                              │
│ ├─ PhucLoi (Service)                                    │
│ ├─ Welfare (Model) ◄─── MỚI                              │
│ └─ TemplatePL (Model)                                    │
└────────────────────────────────────────────────────────────┘
```

---

## 3. SO SÁNH GÓI HÀO HOA

### 3.1 Thông số so sánh

| Thuộc tính | Server Mẫu | Server Hiện Tại | Status |
|------------|------------|-----------------|--------|
| ID | 13 | 13 | ✅ Giống |
| Message ID | -63 | 13 | ⚠️ Khác |
| Giá | 100 vàng | 100 vàng | ✅ Giống |
| Phần thưởng | 100 vàng khóa | 100 vàng khóa | ✅ Giống |
| Kiểm tra đã mua | `isGoiHaoHoa` | `goiHaoHoa` | ⚠️ Đặt tên |
| Tracking | `TongDauTu++` | Không | ❌ Thiếu |
| MKMBV Check | Có | Không | ❌ Thiếu |
| ClickEvent | Không | `ClickEvent.PhucLoi()` | ⚠️ Thêm mới |

### 3.2 Code Server Mẫu

```java
// Session.java - case -63
case -63:
    if(client.mChar == null) break;
    if(!client.mChar.infoChar.MKMBV) {
        client.session.serivce.ShowMessRed("Không thể thực hiện");
        break;
    }
    if(client.mChar.phucLoi.isGoiHaoHoa) break;
    if(client.mChar.infoChar.vang > 200){
        client.mChar.mineVang(200, true, true, "Mua gói hào hoa");
        client.mChar.phucLoi.isGoiHaoHoa = true;
        DataCenter.gI().phucLoiInfo.TongDauTu++;  // ◄── Tracking
        DataCenter.gI().updatePhucLoi(2, DataCenter.gI().phucLoiInfo.TongDauTu);
        Session.this.serivce.sendPhucLoi(client.mChar);
    }
    break;
```

### 3.3 Code Server Hiện Tại

```java
// Char.java - case 13
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
    ClickEvent.PhucLoi(this);  // ◄── Mới
    break;
```

---

## 4. SO SÁNH GÓI CHÍ TÔN

### 4.1 Thông số so sánh

| Thuộc tính | Server Mẫu | Server Hiện Tại | Status |
|------------|------------|-----------------|--------|
| ID | 14 | 14 | ✅ Giống |
| Message ID | -62 | 14 | ⚠️ Khác |
| **Giá** | **300 vàng** | **100 vàng** | ❌ **KHÁC LỚN** |
| Phần thưởng | 300 vàng khóa | 100 vàng khóa | ❌ Khác |
| Kiểm tra đã mua | `isGoiChiTon` | `goiChiTon` | ⚠️ Đặt tên |
| Tracking | `TongDauTu++` | Không | ❌ Thiếu |
| MKMBV Check | Có | Không | ❌ Thiếu |
| ClickEvent | Không | `ClickEvent.PhucLoi()` | ⚠️ Thêm mới |

### 4.2 ⚠️ SỰ KHÁC BIỆT QUAN TRỌNG

**Server Mẫu:**
- Giá: **300 vàng**
- Phần thưởng: **300 vàng khóa**
- Logic: Đắt hơn nhưng nhận nhiều hơn

**Server Hiện Tại:**
- Giá: **100 vàng**
- Phần thưởng: **100 vàng khóa**
- Logic: Rẻ hơn nhưng nhận ít hơn

### 4.3 Code Server Mẫu

```java
// Session.java - case -62
case -62:
    if(client.mChar.phucLoi.isGoiChiTon) break;
    if(client.mChar.infoChar.vang > 300){  // ◄── 300 vàng
        client.mChar.mineVang(300, true, true, "Mua gói chí tôn");
        client.mChar.phucLoi.isGoiChiTon = true;
        DataCenter.gI().phucLoiInfo.TongDauTu++;
        DataCenter.gI().updatePhucLoi(2, DataCenter.gI().phucLoiInfo.TongDauTu);
        Session.this.serivce.sendPhucLoi(client.mChar);
    }
    break;
```

### 4.4 Code Server Hiện Tại

```java
// Char.java - case 14
case 14:
    if (phucLoi.goiChiTon) {
        getService().warningMessage("Bạn đã mua gói chí tôn rồi");
        return;
    }
    if (Bag.vang < 100) {  // ◄── 100 vàng (SAI!)
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

---

## 5. SO SÁNH THẺ THÁNG

### 5.1 Thông số so sánh

| Thuộc tính | Server Mẫu | Server Hiện Tại | Status |
|------------|------------|-----------------|--------|
| ID | 15 | 15, 19 | ⚠️ Khác |
| Message ID | -66 | 15, 19 | ⚠️ Khác |
| Giá | 100 vàng | 100 vàng | ✅ Giống |
| Thời hạn | 30 ngày | 30 ngày | ✅ Giống |
| Nhận ngay | 100 vàng khóa | 100 vàng khóa | ✅ Giống |
| **Nhận ngày** | **30 vàng khóa/ngày** | **Không cài** | ❌ Thiếu |
| Tracking | `TongSoLanMuaTheThang++` | Không | ❌ Thiếu |
| MKMBV Check | Có | Không | ❌ Thiếu |

### 5.2 Phần thưởng hàng ngày

**Server Mẫu** - Có cài đặt trong `updatePhucLoiHangNgay()`:

```java
if (phucLoi.timeTheThang > System.currentTimeMillis()) {
    // Cộng 30 vàng khóa mỗi ngày
    addVangKhoa(30, true, true, "Thẻ tháng ngày");
}
```

**Server Hiện Tại** - Không có logic này.

---

## 6. SO SÁNH THẺ VĨNH VIỄN

### 6.1 Thông số so sánh

| Thuộc tính | Server Mẫu | Server Hiện Tại | Status |
|------------|------------|-----------------|--------|
| ID | 16 | 16, 20 | ⚠️ Khác |
| Message ID | -65 | 16, 20 | ⚠️ Khác |
| Giá | 300 vàng | 300 vàng | ✅ Giống |
| Thời hạn | 365 ngày | 365 ngày | ✅ Giống |
| Nhận ngay | 300 vàng khóa | 300 vàng khóa | ✅ Giống |
| **Nhận ngày** | **50 vàng + 550 vàng/ngày** | **Không cài** | ❌ Thiếu |
| Tracking | `TongSoLanMuaTheThang++` | Không | ❌ Thiếu |
| MKMBV Check | Có | Không | ❌ Thiếu |

### 6.2 Phần thưởng hàng ngày

**Server Mẫu** - Có cài đặt trong `updatePhucLoiHangNgay()`:

```java
if (phucLoi.timeTheVinhVinhVien >= 0L) {
    // Cộng 50 vàng khóa + 550 vàng mỗi ngày
    addVangKhoa(50, true, true, "Thẻ vĩnh viễn ngày");
    addVang(550, true, true, "Thẻ vĩnh viễn ngày");
}
```

**Server Hiện Tại** - Không có logic này.

---

## 7. SO SÁNH VÒNG QUAY NẠP

### 7.1 Trạng thái

| Thuộc tính | Server Mẫu | Server Hiện Tại |
|------------|------------|-----------------|
| Trạng thái | ✅ Hoạt động | ❌ Disabled |
| Message ID | -60 | - |
| Tỷ lệ quay | 6 cấp độ | - |
| Phần thưởng | 1.1x - 1.8x | - |

### 7.2 Server Mẫu - Logic đầy đủ

```java
// Session.java - case -60
int slquay = DataCenter.gI().getVongQuayNap(
    client.mChar.phucLoi.diemTichLuyVongQuay,
    client.mChar.phucLoi.solanQuay
);

if(slquay > 0){
    int vang = DataCenter.gI().getDiem(
        client.mChar.phucLoi.diemTichLuyVongQuay
    );

    int tilequay;
    int rand = Utlis.nextInt(100);

    if(rand < 3) tilequay = 5;      // 1.8x
    else if(rand < 8) tilequay = 4; // 1.5x
    else if(rand < 15) tilequay = 3;// 1.4x
    else if(rand < 25) tilequay = 2;// 1.3x
    else if(rand < 40) tilequay = 1;// 1.2x
    else tilequay = 0;              // 1.1x

    if(tilequay == 1) vang *= 1.2;
    else if(tilequay == 2) vang *= 1.3;
    else if(tilequay == 3) vang *= 1.4;
    else if(tilequay == 4) vang *= 1.5;
    else if(tilequay == 5) vang *= 1.8;
    else vang *= 1.1;

    Session.this.serivce.sendVongQuayNap(
        (byte) client.mChar.phucLoi.solanQuay,
        (byte) tilequay,
        (int) vang
    );
    client.mChar.addVangKhoa(vang, false, false, "Vòng quay nạp");
}
```

### 7.3 Server Hiện Tại - Commented Out

```java
// InfoPhucLoi.java
// VongQuayNap disabled - not used
// public long vongQuayNapTichLuy;
// public long luotQuayVongXoay;
// public int daQuayVongXoay;
// public int vongQuayNapSeasonId;
```

---

## 8. SO SÁNH DATA MODEL

### 8.1 Server Mẫu - PhucLoi (Per Player)

```java
public class PhucLoi {
    // Thời gian
    public int thoigianOnlineHomNay = 0;
    public Long lastRequestTime = null;

    // Đếm ngày
    public byte soNgayOnlineLienTuc = 1;
    public byte soNgayNapLienTuc = 0;
    public LocalDate lastLoginDate;
    public LocalDate lastDailyUpdate;
    public LocalDate lastWeeklyUpdate;
    public LocalDate lastNapLienTucUpdate;

    // Tiền nạp
    public int vangNapTichLuy = 0;
    public int vangNapHomNay = 0;
    public int vangNapTuan = 0;
    public int vangNapMoc = 0;
    public int vangNapDon = 0;

    // Tiền tiêu
    public int vangTieuHomNay = 0;
    public int vangTieuTuan = 0;

    // Gói đặc biệt
    public boolean isGoiHaoHoa = false;
    public boolean isGoiChiTon = false;
    public long timeTheThang = -1;
    public long timeTheVinhVinhVien = -1;

    // Vòng quay
    public int diemTichLuyVongQuay = 0;
    public int solanQuay = 0;

    // Log
    public final List<LogPhucLoi> logData = new ArrayList<>();
}
```

### 8.2 Server Hiện Tại - InfoPhucLoi (Per Player)

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

    // ⚠️ Vòng quay DISABLED
}
```

### 8.3 Server Mẫu - PhucLoiInfo (Global) - ⚠️ THIẾU TRONG SERVER HIỆN TẠI

```java
public class PhucLoiInfo {
    public int TongRank;              // ◄── Thiếu trong server mới
    public int RankCaoNhat;           // ◄── Thiếu trong server mới
    public int TongDauTu;              // ◄── Thiếu trong server mới
    public int TongSoLanMuaTheThang;   // ◄── Thiếu trong server mới
    public long ThoiGianX2Online;     // ◄── Thiếu trong server mới
}
```

---

## 9. DANH SÁCH THAY ĐỔI CẦN THỰC HIỆN

### 9.1 Ưu tiên CAO - Cần sửa ngay

#### 1. Sửa giá Gói Chí Tôn

**File**: `Char.java` (line ~14222)

```java
// Trước:
if (Bag.vang < 100) {
    getService().warningMessage("Bạn không đủ 100 vàng");
    return;
}

// Sau:
if (Bag.vang < 300) {
    getService().warningMessage("Bạn không đủ 300 vàng");
    return;
}
```

#### 2. Sửa phần thưởng Gói Chí Tôn

**File**: `Char.java` (line ~14226)

```java
// Trước:
addVang(-100);
addVangKhoa(100);

// Sau:
addVang(-300);
addVangKhoa(300);
```

### 9.2 Ưu tiên TRUNG BÌNH - Nên thêm

#### 3. Thêm Global Tracking

**File**: Tạo mới `InfoChar/PhucLoiInfo.java`

```java
package InfoChar;

public class PhucLoiInfo {
    public static int tongDauTu = 0;
    public static int tongSoLanMuaTheThang = 0;
    public static int tongRank = 0;
    public static int rankCaoNhat = 0;
    public static long thoiGianX2Online = 0;

    public static void save() {
        // Lưu vào database
    }

    public static void load() {
        // Load từ database
    }
}
```

#### 4. Thêm Tracking khi mua

**File**: `Char.java` - Thêm vào `subscribeBenefitPackage()`

```java
case 13: // Gói Hào Hoa
    // ... existing code ...
    PhucLoiInfo.tongDauTu++;
    PhucLoiInfo.save();
    break;

case 14: // Gói Chí Tôn
    // ... existing code ...
    PhucLoiInfo.tongDauTu++;
    PhucLoiInfo.save();
    break;

case 15: // Thẻ Tháng
case 19:
    // ... existing code ...
    PhucLoiInfo.tongSoLanMuaTheThang++;
    PhucLoiInfo.save();
    break;
```

### 9.3 Ưu tiên THẤP - Tùy chọn

#### 5. Thêm phần thưởng hàng ngày cho Thẻ

**File**: `Char.java` - Thêm method mới

```java
public void checkDailyCardRewards() {
    long now = System.currentTimeMillis();

    // Thẻ tháng - 30 vàng khóa/ngày
    if (phucLoi.theThang > now) {
        addVangKhoa(30, true, true, "Thẻ tháng ngày");
    }

    // Thẻ vĩnh viễn - 50 vàng khóa + 550 vàng/ngày
    if (phucLoi.theVinhVien > now) {
        addVangKhoa(50, true, true, "Thẻ vĩnh viễn ngày");
        addVang(550, true, true, "Thẻ vĩnh viễn ngày");
    }
}
```

Gọi trong `updateGameLoop()` hoặc khi login.

#### 6. Bật lại Vòng Quay Nạp

Uncomment trong `InfoPhucLoi.java`:
```java
public long vongQuayNapTichLuy;
public long luotQuayVongXoay;
public int daQuayVongXoay;
public int vongQuayNapSeasonId;
```

Thêm message handler `-60` trong `Session.java`.

---

## 10. BẢNG TÓM TẮT

| STT | Thay đổi | Ưu tiên | File |
|-----|-----------|---------|------|
| 1 | Sửa giá Gói Chí Tôn (100→300) | CAO | Char.java |
| 2 | Sửa phần thưởng Gói Chí Tôn (100→300) | CAO | Char.java |
| 3 | Thêm PhucLoiInfo global tracking | TRUNG BÌNH | InfoChar/PhucLoiInfo.java |
| 4 | Thêm tracking khi mua gói | TRUNG BÌNH | Char.java |
| 5 | Thêm phần thưởng ngày cho Thẻ | THẤP | Char.java |
| 6 | Bật lại Vòng Quay Nạp | THẤP | Nhiều file |

---

## 11. FILE TÀI LIỆU CHI TIẾT

- [Server Mẫu](./HE_THONG_DAU_TU_PHUC_LOI_SERVER_MAU.md)
- [Server Hiện Tại](./HE_THONG_DAU_TU_PHUC_LOI_SERVER_HIEN_TAI.md)
