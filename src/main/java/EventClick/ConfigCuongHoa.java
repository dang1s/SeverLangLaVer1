package EventClick;

import com.sg188.server.Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConfigCuongHoa {

    public static String START_STR;
    public static String END_STR;

    public static long START;
    public static long END;
    
    // Biến kiểm soát thông báo để không bị lặp lại
    private static boolean isNotified = false;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        LinkedProperties prop = new LinkedProperties();
        File file = new File("event.properties");
        
        if (file.exists()) {
            try (FileInputStream input = new FileInputStream(file)) {
                prop.load(input);
                START_STR = prop.getProperty("cuonghoa.start", "01-02-2025 00:00:00");
                END_STR = prop.getProperty("cuonghoa.end", "01-12-2025 23:59:59");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            START_STR = "01-02-2025 00:00:00";
            END_STR = "01-12-2025 23:59:59";
        }

        START = parseDate(START_STR);
        END = parseDate(END_STR);
        
        System.out.println("Sự kiện Cường Hóa: " + START_STR + " -> " + END_STR);

        // --- Thông báo nổ chữ ngay nếu đang trong thời gian sự kiện ---
        long now = System.currentTimeMillis();
        if (now >= START && now <= END && !isNotified) {
            String thongBao = "Sự kiện Đua Top Cường Hóa đã chính thức bắt đầu, hãy nhanh tay nâng cấp trang bị!";
            Main.HeThongCTG(thongBao, 2); 
            isNotified = true;
        }
    }

    public static void saveConfig(String start, String end) {
        LinkedProperties prop = new LinkedProperties();
        File file = new File("event.properties");

        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                prop.load(in);
            } catch (IOException e) { }
        }

        prop.setProperty("cuonghoa.start", start);
        prop.setProperty("cuonghoa.end", end);

        try (FileOutputStream out = new FileOutputStream(file)) {
            prop.store(out, null);
            isNotified = false; // Reset để có thể nổ thông báo mới khi load lại
            loadConfig(); 
            System.out.println(">>> Đã lưu cấu hình Cường Hóa và cập nhật hệ thống!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long parseDate(String s) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = sdf.parse(s);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    // --- Luồng tự động kiểm tra giờ để nổ thông báo ---
    public static void startCheckTimeThread() {
        new Thread(() -> {
            while (true) {
                try {
                    long now = System.currentTimeMillis();
                    if (now >= START && now <= END) {
                        if (!isNotified) {
                            String msg = "Sự kiện Đua Top Cường Hóa đã chính thức bắt đầu, hãy nhanh tay nâng cấp trang bị!";
                            Main.HeThongCTG(msg, 2); 
                            isNotified = true;
                            System.out.println(">>> [EVENT] Đã nổ thông báo bắt đầu Cường Hóa.");
                        }
                    } else {
                        // Nếu ngoài thời gian sự kiện, cho phép thông báo lại khi sự kiện quay lại
                        isNotified = false;
                    }
                    Thread.sleep(10000); // Kiểm tra mỗi 10 giây
                } catch (Exception e) {
                }
            }
        }).start();
    }
}