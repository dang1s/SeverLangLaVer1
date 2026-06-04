# SO SÁNH ĐẦU TƯ: GÓI HÀO HOA & GÓI CHÍ TÔN

**Ngày cập nhật**: 27/05/2026
**Server Mẫu**: D:\langlasever1
**Server Hiện Tại**: E:\SeverLangLaVer1

---

## 1. GÓI HÀO HOA

### Thông số so sánh

| Thuộc tính | Server Mẫu | Server Hiện Tại |
|------------|------------|-----------------|
| **ID** | 13 | 13 |
| **Message ID** | -63 | 13 |
| **Giá** | 100 vàng | 100 vàng |
| **Phần thưởng** | 100 vàng khóa | 100 vàng khóa |
| **Kiểm tra đã mua** | `isGoiHaoHoa` | `goiHaoHoa` |
| **Tracking TongDauTu** | ✅ Có | ❌ Không |
| **MKMBV Check** | ✅ Có | ❌ Không |
| **ClickEvent** | ❌ Không | ✅ Có |

### Code Server Mẫu

**File**: `Session.java` (case -63)

```java
case -63:
    if(client.mChar == null) break;
    if(!client.mChar.infoChar.MKMBV) {  // ◄── Kiểm tra MKMBV
        client.session.serivce.ShowMessRed("Không thể thực hiện");
        break;
    }
    if(client.mChar.phucLoi.isGoiHaoHoa) break;
    if(client.mChar.infoChar.vang > 200){  // ◄── Lưu ý: 200 vàng
        client.mChar.mineVang(200, true, true, "Mua gói hào hoa");
        client.mChar.phucLoi.isGoiHaoHoa = true;
        DataCenter.gI().phucLoiInfo.TongDauTu++;  // ◄── Tracking
        DataCenter.gI().updatePhucLoi(2, DataCenter.gI().phucLoiInfo.TongDauTu);
        Session.this.serivce.sendPhucLoi(client.mChar);
    } else {
        Session.this.serivce.ShowMessRed("Không đủ vàng");
    }
    break;
```

### Code Server Hiện Tại

**File**: `Char.java` (case 13)

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
    ClickEvent.PhucLoi(this);  // ◄── Event trigger
    break;
```

### Kết luận Gói Hào Hoa

| Tiêu chí | Đánh giá |
|-----------|----------|
| Giá/Phần thưởng | ✅ Giống nhau |
| Tracking | ❌ Server mới thiếu |
| MKMBV | ❌ Server mới thiếu |
| Event | ⚠️ Server mới có thêm |

---

## 2. GÓI CHÍ TÔN

### Thông số so sánh

| Thuộc tính | Server Mẫu | Server Hiện Tại | Khác biệt |
|------------|------------|-----------------|-----------|
| **ID** | 14 | 14 | ✅ |
| **Message ID** | -62 | 14 | ✅ |
| **Giá** | 300 vàng | 100 vàng | ❌ **300%** |
| **Phần thưởng** | 300 vàng khóa | 100 vàng khóa | ❌ **300%** |
| **Kiểm tra đã mua** | `isGoiChiTon` | `goiChiTon` | ⚠️ Đặt tên |
| **Tracking TongDauTu** | ✅ Có | ❌ Không | ❌ |
| **MKMBV Check** | ✅ Có | ❌ Không | ❌ |
| **ClickEvent** | ❌ Không | ✅ Có | ⚠️ |

### Code Server Mẫu

**File**: `Session.java` (case -62)

```java
case -62:
    if(client.mChar == null) break;
    if(!client.mChar.infoChar.MKMBV) {
        client.session.serivce.ShowMessRed("Không thể thực hiện");
        break;
    }
    if(client.mChar.phucLoi.isGoiChiTon) break;
    if(client.mChar.infoChar.vang > 300){  // ◄── 300 vàng
        client.mChar.mineVang(300, true, true, "Mua gói chí tôn");
        client.mChar.phucLoi.isGoiChiTon = true;
        DataCenter.gI().phucLoiInfo.TongDauTu++;  // ◄── Tracking
        DataCenter.gI().updatePhucLoi(2, DataCenter.gI().phucLoiInfo.TongDauTu);
        Session.this.serivce.sendPhucLoi(client.mChar);
    } else {
        Session.this.serivce.ShowMessRed("Không đủ vàng");
    }
    break;
```

### Code Server Hiện Tại

**File**: `Char.java` (case 14)

```java
case 14:
    if (phucLoi.goiChiTon) {
        getService().warningMessage("Bạn đã mua gói chí tôn rồi");
        return;
    }
    if (Bag.vang < 100) {  // ◄── SAI! Phải là 300
        getService().warningMessage("Bạn không đủ 100 vàng");
        return;
    }
    addVang(-100);  // ◄── SAI! Phải là -300
    addVangKhoa(100);  // ◄── SAI! Phải là +300
    phucLoi.goiChiTon = true;
    getService().serverMessage("Mua thành công gói chí tôn");
    ClickEvent.PhucLoi(this);
    break;
```

### Kết luận Gói Chí Tôn

| Tiêu chí | Đánh giá |
|-----------|----------|
| Giá/Phần thưởng | ❌ **KHÁC NHAU (3x)** |
| Tracking | ❌ Server mới thiếu |
| MKMBV | ❌ Server mới thiếu |
| Event | ⚠️ Server mới có thêm |

---

## 3. SỰ KHÁC BIỆT QUAN TRỌNG

### ⚠️ LỖI CẦN SỬA TRONG SERVER HIỆN TẠI

**Gói Chí Tôn bị giảm giá từ 300 vàng xuống 100 vàng** (3 lần)

| Gói | Server Mẫu | Server Hiện Tại |
|------|-------------|-----------------|
| Hào Hoa | 100 vàng | 100 vàng |
| Chí Tôn | 300 vàng | 100 vàng |

→ User mua Gói Chí Tôn ở server mới chỉ tốn **1/3 giá** so với server mẫu!

---

## 4. BẢNG SO SÁNH TỔNG HỢP

| Tiêu chí | Server Mẫu | Server Hiện Tại | Cần sửa? |
|-----------|------------|-----------------|-----------|
| **Giá Hào Hoa** | 100 | 100 | ❌ |
| **Giá Chí Tôn** | 300 | 100 | ✅ **CÓ** |
| **Thưởng Hào Hoa** | 100 VK | 100 VK | ❌ |
| **Thưởng Chí Tôn** | 300 VK | 100 VK | ✅ **CÓ** |
| **TongDauTu++** | ✅ | ❌ | ⚠️ Nên thêm |
| **MKMBV Check** | ✅ | ❌ | ⚠️ Tùy chọn |
| **ClickEvent** | ❌ | ✅ | ⚠️ Giữ lại |

---

## 5. CODE CẦN SỬA

### Sửa Gói Chí Tôn

**File**: `Char.java` (case 14)

```java
// TRƯỚC (sai):
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

// SAU (đúng):
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
    getService().serverMessage("Mua thành công gói chí tôn");
    ClickEvent.PhucLoi(this);
    break;
```

---

## 6. FILE LIÊN QUAN

| File | Server | Nội dung |
|------|--------|----------|
| `Session.java` | Mẫu | Logic mua Gói Hào Hoa & Chí Tôn |
| `Char.java` | Hiện tại | Logic mua Gói Hào Hoa & Chí Tôn |
| `PhucLoiInfo.java` | Mẫu | Global tracking TongDauTu |
| `InfoPhucLoi.java` | Hiện tại | Per-player data (thiếu tracking) |
