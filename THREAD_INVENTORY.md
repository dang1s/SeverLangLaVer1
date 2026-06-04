# Thread Inventory - Server Game LangLa

## Tổng quan

Server hiện tại tạo **quá nhiều thread** cho workload thực tế. Vấn đề chính:
- **268 threads cho 12 người chơi** = bất thường
- **ScheduledExecutor 50 threads** là cấu hình quá cao
- **Mỗi Map 2 threads** chạy vĩnh viễn dù map có người hay không
- **Thread leak tiềm ẩn** trong các cleanup chưa hoàn hảo

---

## DANH SÁCH ĐẦY ĐỦ TẤT CẢ THREAD

### NHÓM A: SINGLETON - CHẠY VĨNH VIỄN (giữ permanent)

| # | Tên | File | Dòng | Mục đích | Pool | Có stop? |
|---|------|------|-------|----------|------|-----------|
| 1 | Server Socket | MyServerSocket.java | 27 | Lắng nghe kết nối client | 1 | Có close() |
| 2 | Console-Input-Handler | Main.java | 691 | Đọc lệnh console | 1 | Vĩnh viễn |
| 3 | AutoTopReward-Worker-N | Main.java | 128 | Auto phát thưởng top (4 threads) | 4 | Có shutdown |
| 4 | AdminApiServer-Thread | AdminApiServer.java | 54 | HTTP API server cho admin web | 1 | Có stop() |
| 5 | Thread-SaveData | Main.java | 756 | Auto lưu data mỗi 1 phút | 1 | Chạy đến BaoTri |
| 6 | Market-Manager | MarketManager.java | 759 | Quản lý chợ | 1 | Vĩnh viễn |
| 7 | Vòng-Xoay-LuckyDraw | Main.java | 770 | Vòng quay may mắn | 1 | Vĩnh viễn |
| 8 | Clan | Clan.java | 41 | Update clan mỗi 1s | 1 | Có stop() |
| 9 | MapThreadWatchdog | MapThreadWatchdog.java | 43 | Giám sát + khôi phục map thread | 1 | Có stop() |
| 10 | ConnectionRateLimiter-Cleanup | ConnectionRateLimiter.java | 178 | Cleanup blacklist IP | 1 | Vĩnh viễn |
| 11 | dbExecutor pool | MarketManager.java | 57 | Xử lý DB async (5 threads) | 5 | Không shutdown |

**Subtotal NHÓM A: ~17 threads cố định**

---

### NHÓM B: PER-EVENT CONFIG - CHẠY VĨNH VIỄN, CÓ THỂ GỘP

| # | Tên | File | Dòng | Mục đích | Interval | Vấn đề |
|---|------|------|-------|----------|----------|---------|
| 12 | ConfigLuyenTap-check | ConfigLuyenTap.java | 85 | Check sự kiện luyện tập | 10s | **THREAD RIÊNG** |
| 13 | ConfigNhiDong-check | ConfigNhiDong.java | 85 | Check sự kiện nhi đồng | 10s | **THREAD RIÊNG** |
| 14 | ConfigCuongHoa-check | ConfigCuongHoa.java | 92 | Check sự kiện cường hóa | 10s | **THREAD RIÊNG** |
| 15 | ConfigChuyenCanTuan-check | ConfigChuyenCanTuan.java | 94 | Check sự kiện chuyên cần | 60s | **THREAD RIÊNG** |

**Vấn đề:** 4 threads riêng chạy `while(true) { Thread.sleep(10s) }` hoàn toàn có thể gộp vào 1 thread duy nhất hoặc dùng ScheduledExecutor.

**Subtotal NHÓM B: 4 threads**

---

### NHÓM C: PER-SCHEDULER POOL - LƯỜI BIẾNG

| # | Pool | File | Dòng | Kích thước | Nhiệm vụ |
|---|------|------|-------|-----------|----------|
| 16-65 | ScheduledExecutorService | Service/ScheduledExecutor.java | 13 | **50 threads** | Tất cả scheduled tasks (câu cá, giám định, đánh giá...) |

**Vấn đề NGHIÊM TRỌNG:** 50 threads nhưng thực tế dùng rất ít. Hầu hết task là one-shot `schedule(() -> {...}, X, TimeUnit.SECONDS)` cho:
- Câu cá (3s delay)
- Giám định (5s delay)
- Các tác vụ one-shot khác

**Thực tế:** Thường chỉ cần **2-4 threads** là đủ cho mọi scheduled task.

**Subtotal NHÓM C: 50 threads (CẦN GIẢM)**

---

### NHÓM D: PER-MAP - CẦN TỐI ƯU

| # | Tên | File | Dòng | Mục đích |
|---|------|------|-------|----------|
| D1-Dn | UpdateChar-Map-N | Map.java | 253 | Update nhân vật trong map |
| Dn+1-D2n | UpdateOther-Map-N | Map.java | 261 | Update NPC/mob/other trong map |

**Vấn đề NGHIÊM TRỌNG:** Mỗi map tạo **2 threads chạy vĩnh viễn** (`while(running)`). Với ~50 maps = **100 threads**.

**Thực tế:**
- Không phải map nào cũng có người chơi
- Map không có người vẫn chạy thread liên tục
- Nên: **Dừng threads của map khi không có người, khởi động lại khi có người vào**

**Subtotal NHÓM D: 2 × Số maps (thường ~100 threads)**

---

### NHÓM E: PER-SESSION (CLIENT) - CẦN FIX LEAK

| # | Tên | File | Dòng | Mục đích | Cleanup |
|---|------|------|-------|----------|---------|
| E1-En | Sender: IP | Session.java | 62 | Gửi data đến client | Có interrupt |
| En+1-E2n | reader: IP | Session.java | 125 | Nhận data từ client | Có interrupt |
| E2n+1-E3n | SessionTimeout-N | Session.java | 149 | Timeout check | Có interrupt |

**Vấn đề:** Với 12 người = 36 threads. Cleanup code có vẻ đúng nhưng threads có thể **không exit ngay** vì:
1. `threadSend` chạy `while(isConnected())` + `Thread.sleep(10)` — nếu `isConnected()` bị stuck ở `socket.isConnected()` thì sẽ không exit
2. `threadRecv` chạy `while(isConnected())` — `reader.readByte()` có thể block vĩnh viễn nếu socket đã close nhưng chưa được detect

**NÊN:** Thêm `socket.setSoTimeout()` hoặc đảm bảo interrupt được propagate đúng.

**Subtotal NHÓM E: 3 × Số client (thường ~36 threads với 12 online)**

---

### NHÓM F: FIRE-AND-FORGET - CẦN CHUYỂN SANG SHARED EXECUTOR

| # | Gọi từ | Dòng | Trigger | Vấn đề |
|---|---------|-------|---------|---------|
| F1 | Controller.java | 696 | Giám định item | Tạo `newScheduledThreadPool(1)` rồi bỏ lìn (leak!) |
| F2 | AdminApiServer.java | 106 | Notify online players | Tạo thread mới mỗi lần gọi |
| F3 | Main.java | 1009, 2117, 2160 | Bảo trì, đóng server | Tạo thread mỗi action |
| F4 | Utlis.java | 427 | setTimeout() utility | Tạo thread mới mỗi lần gọi |
| F5 | Char.java | 8584 | Console command | Tạo thread cho baotri |
| F6 | AutoMaintenance.java | 53 | Tự động bảo trì | Tạo `newScheduledThreadPool(1)` |

**Vấn đề:** `F1` (Controller.java:696) tạo `Executors.newScheduledThreadPool(1)` rồi immediately gán biến local cho singleton - thread pool bị tạo nhưng không ai giữ reference, có thể leak.

**Subtotal NHÓM F: 0 threads idle, nhưng tạo thread mới mỗi khi trigger**

---

### NHÓM G: COMMENTED OUT - KHÔNG CHẠY

| # | Code | File | Dòng | Ghi chú |
|---|------|------|-------|---------|
| G1 | MonthlyReset-Worker-N (commented) | Main.java | 2087 | Vòng quay nạp - disabled |

---

## BẢNG PHÂN LOẠI & ĐỀ XUẤT

### ✅ GIỮ NGUYÊN (Singleton vĩnh viễn - cần thiết)

| Thread | Số lượng | Lý do |
|--------|----------|-------|
| Server Socket | 1 | Chấp nhận kết nối |
| Console-Input-Handler | 1 | Admin console |
| AutoTopReward (4 threads) | 4 | Cần chạy task định kỳ |
| AdminApiServer | 1 | HTTP API |
| Thread-SaveData | 1 | Auto save |
| Market-Manager | 1 | Quản lý chợ |
| Vòng-Xoay-LuckyDraw | 1 | Vòng quay |
| Clan | 1 | Update clan |
| MapThreadWatchdog | 1 | Giám sát map |
| ConnectionRateLimiter-Cleanup | 1 | Cleanup IP |
| dbExecutor (5 threads) | 5 | DB async |

**Subtotal: 18 threads (không thay đổi)**

---

### ⚠️ TỐI ƯU (Cần giảm kích thước pool)

| Thread/Pool | Hiện tại | Đề xuất | Lý do |
|-------------|----------|---------|-------|
| ScheduledExecutor | **50 threads** | **4-8 threads** | Thực tế dùng rất ít, one-shot task |
| NHÓM B (4 event check) | **4 threads** | **1 thread** | Gộp 4 event check vào 1 vòng lặp |

**Tiết kiệm: 42-46 threads**

---

### 🔧 SỬA LEAK (Thread không exit đúng)

| Vấn đề | File | Dòng | Fix |
|---------|------|-------|-----|
| threadSend có thể block | Session.java | 62 | Đảm bảo `interrupt()` break `sleep(10)` |
| threadRecv block trên readByte | Session.java | 125 | Thêm `socket.setSoTimeout()` hoặc catch IOException |
| Controller.java tạo pool rồi discard | Controller.java | 696 | Xóa `Executors.newScheduledThreadPool(1)` vì dòng sau gán singleton |

**Subtotal: 0 thread mới, nhưng fix leak giúp thread count ổn định**

---

### 💡 CẢI TIẾN (Per-map threads)

| Hiện tại | Đề xuất |
|----------|---------|
| Mỗi map 2 threads chạy vĩnh viễn | Chỉ chạy khi map có người |

**Tiết kiệm tiềm năng: 50-100 threads (tùy số maps trống)**

---

## TÍNH TOÁN SAU TỐI ƯU

| Nhóm | Hiện tại (ước tính) | Sau tối ưu |
|------|---------------------|------------|
| NHÓM A (singleton) | ~18 | **18** (giữ nguyên) |
| NHÓM B (4 event) | 4 | **1** |
| NHÓM C (Scheduled) | **50** | **8** |
| NHÓM D (per-map) | ~100 | ~30-60 (chỉ maps có người) |
| NHÓM E (per-session) | ~36 | ~36 (cần thêm leak fix) |
| NHÓM F (fire-forget) | 0 idle | 0 idle |
| **TỔNG** | **~268** | **~93-123** |

---

## HÀNH ĐỘNG CẦN THỰC HIỆN

### Ưu tiên CAO (dễ làm, hiệu quả lớn)

1. **Giảm ScheduledExecutor từ 50 → 8** (`Service/ScheduledExecutor.java:13`)
2. **Gộp 4 event check threads → 1 thread** dùng ScheduledExecutor
3. **Fix Controller.java:696** — xóa dòng `Executors.newScheduledThreadPool(1)` vì immediately overwritten
4. **Fix Session thread leak** — đảm bảo interrupt break sleep loops

### Ưu tiên TRUNG BÌNH (cần refactor nhiều hơn)

5. **Per-map threads lazy start/stop** — chỉ chạy khi map có char, stop khi empty

### Ưu tiên THẤP (cần thiết kế lại)

6. **Chuyển Utlis.setTimeout()** sang dùng ScheduledExecutor thay vì `new Thread()`
7. **AdminApiServer notify** — dùng thread pool có sẵn thay vì tạo thread mới
