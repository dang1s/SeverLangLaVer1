package EventClick;

import com.sg188.server.Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ConfigLuyenTap {

    public static String START_STR;
    public static String END_STR;

    public static long START;
    public static long END;

    static {
        loadConfig();
    }

    public static void loadConfig() {
    Properties prop = new Properties();
    try (FileInputStream input = new FileInputStream("event.properties")) {
        prop.load(input);
        START_STR = prop.getProperty("luyentap.start", "01-01-2025 00:00:00");
        END_STR = prop.getProperty("luyentap.end", "07-01-2025 00:00:00");

        START = parseDate(START_STR);
        END = parseDate(END_STR);
        
        System.out.println("Sự kiện Luyện Tập " + START_STR + " -> " + END_STR);
    } catch (IOException ex) {
        System.out.println("Không tìm thấy file event.properties, sử dụng mặc định.");
        // ex.printStackTrace(); // Có thể tắt cái này cho đỡ rác log
    }

    // ĐƯA ĐOẠN NÀY RA NGOÀI KHỐI TRY-CATCH ĐỂ NÓ LUÔN CHẠY
    long now = System.currentTimeMillis();
    if (now >= START && now <= END) {
        String thongBao = "Sự kiện Đua Top Luyện Tập đã chính thức bắt đầu, các nhẫn giả hãy nhanh tay tham gia nào!";
        Main.HeThongCTG(thongBao, 2); 
    }
}
    public static void saveConfig(String start, String end) {
    // Sử dụng LinkedProperties để giữ đúng thứ tự dòng
    LinkedProperties prop = new LinkedProperties(); 
    
    // 1. Đọc nội dung cũ để không làm mất dữ liệu các sự kiện khác (Chuyên cần, Luyện tập...)
    File file = new File("event.properties");
    if (file.exists()) {
        try (java.io.FileInputStream in = new java.io.FileInputStream(file)) {
            prop.load(in);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    prop.setProperty("luyentap.start", start);
    prop.setProperty("luyentap.end", end);

    try (java.io.FileOutputStream out = new java.io.FileOutputStream("event.properties")) {
        prop.store(out, null);
        loadConfig(); 
        
        System.out.println(">>> Đã lưu cấu hình Luyện T và cập nhật hệ thống!");
    } catch (java.io.IOException e) {
        System.err.println("Lỗi khi ghi file config!");
        e.printStackTrace();
    }
}
    public static long parseDate(String s) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = sdf.parse(s);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }
    private static boolean isNotified = false;

    public static void startCheckTimeThread() {
        new Thread(() -> {
            while (true) {
                try {
                    long now = System.currentTimeMillis();

                    // Kiểm tra: Nếu hiện tại nằm trong khoảng START và END
                    if (now >= START && now <= END) {
                        // Nếu chưa thông báo thì mới nổ chữ trong game
                        if (!isNotified) {
                            String msg = "Sự kiện Đua Top Luyện Tập đã chính thức bắt đầu, các nhẫn giả hãy tham gia ngay!";
                            
                            // Gọi hàm hệ thống để hiện thông báo nổ giữa màn hình (Type 2)
                            Main.HeThongCTG(msg, 2); 
                            
                            isNotified = true; // Đánh dấu là đã thông báo rồi
                            System.out.println(">>> [EVENT] Đã nổ thông báo bắt đầu Luyện Tập trong game.");
                        }
                    } else {
                        isNotified = false;
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } 
                } catch (Exception e) {
                }
            }
        }).start();
    }
    
}