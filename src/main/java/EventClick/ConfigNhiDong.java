package EventClick;

import com.sg188.server.Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ConfigNhiDong {

    public static String START_STR;
    public static String END_STR;

    public static long START;
    public static long END;
    
    private static boolean isNotified = false;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("event.properties")) {
            prop.load(input);
            // Thay đổi key cho Nhi Đồng
            START_STR = prop.getProperty("nhidong.start", "01-01-2025 00:00:00");
            END_STR = prop.getProperty("nhidong.end", "07-01-2025 00:00:00");

            START = parseDate(START_STR);
            END = parseDate(END_STR);
            
            System.out.println("Sự kiện Nhi Đồng: " + START_STR + " -> " + END_STR);
        } catch (IOException ex) {
            System.out.println("Không tìm thấy file event.properties, sử dụng mặc định cho Nhi Đồng.");
        }

        // Thông báo ngay khi nạp config nếu đang trong giờ
        long now = System.currentTimeMillis();
        if (now >= START && now <= END && !isNotified) {
            String thongBao = "Sự kiện Đua Top Nhi Đồng đã chính thức bắt đầu!";
            Main.HeThongCTG(thongBao, 2); 
            isNotified = true;
        }
    }

    public static void saveConfig(String start, String end) {
        LinkedProperties prop = new LinkedProperties(); 
        File file = new File("event.properties");
        if (file.exists()) {
            try (java.io.FileInputStream in = new java.io.FileInputStream(file)) {
                prop.load(in);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        prop.setProperty("nhidong.start", start);
        prop.setProperty("nhidong.end", end);

        try (java.io.FileOutputStream out = new java.io.FileOutputStream("event.properties")) {
            prop.store(out, null);
            isNotified = false; // Reset để nổ lại thông báo mới
            loadConfig(); 
            System.out.println(">>> Đã lưu cấu hình Nhi Đồng và cập nhật hệ thống!");
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

    public static void startCheckTimeThread() {
        new Thread(() -> {
            while (true) {
                try {
                    long now = System.currentTimeMillis();
                    if (now >= START && now <= END) {
                        if (!isNotified) {
                            String msg = "Sự kiện Đua Top Nhi Đồng đã chính thức bắt đầu, các nhẫn giả hãy tham gia ngay!";
                            Main.HeThongCTG(msg, 2); 
                            isNotified = true;
                            System.out.println(">>> [EVENT] Đã nổ thông báo bắt đầu Nhi Đồng.");
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