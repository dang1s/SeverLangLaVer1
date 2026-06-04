package EventClick;

import com.sg188.server.Main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ConfigChuyenCanTuan {

    public static String START_STR;
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
            // Lấy ngày bắt đầu từ file, nếu không có mặc định là 14-11-2025
            START_STR = prop.getProperty("chuyencantuan.start", "19-12-2025 00:00:00");

            START = parseDate(START_STR);
            // Tự động tính END = START + 7 ngày
            END = START + 7L * 24 * 60 * 60 * 1000;

            System.out.println("Sự kiện Chuyên Cần Tuần: " + START_STR + " -> " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(END)));
        } catch (IOException ex) {
            System.out.println("Không tìm thấy file event.properties, sử dụng mặc định cho Chuyên Cần.");
            START_STR = "14-11-2025 00:00:00";
            START = parseDate(START_STR);
            END = START + 7L * 24 * 60 * 60 * 1000;
        }
        long now = System.currentTimeMillis();
        if (now >= START && now <= END && !isNotified) {
            String thongBao = "Sự kiện Đua Top Chuyên Cần Tuần đã chính thức bắt đầu!";
            Main.HeThongCTG(thongBao, 2); 
            isNotified = true;
        }
    }

    // Hàm lưu cấu hình (Chỉ cần truyền start vì end tự tính)
    public static void saveConfig(String start) {
        Properties prop = new Properties();
        File file = new File("event.properties");

        // 1. Đọc dữ liệu cũ lên để giữ lại các config của sự kiện khác
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                prop.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        prop.setProperty("chuyencantuan.start", start);

        try (FileOutputStream out = new FileOutputStream("event.properties")) {
            prop.store(out, "Update Weekly Reset");
            loadConfig();
            isNotified = false;

            System.out.println(">>> Đã lưu và reset thông báo Chuyên Cần Tuần!");
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void nextWeek() {
        long newStart = END; // Tuần mới bắt đầu từ lúc tuần cũ kết thúc
        String newStartStr = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(newStart));
        saveConfig(newStartStr); // Lưu thẳng vào file để reset bền vững
    }

    public static long parseDate(String s) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(s).getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    // Luồng thông báo hệ thống
    public static void startCheckTimeThread() {
        new Thread(() -> {
            while (true) {
                try {
                    long now = System.currentTimeMillis();
                    // Thông báo nếu đang trong tuần sự kiện
                    if (now >= START && now <= END) {
                        if (!isNotified) {
                            String msg = "Sự kiện Chuyên Cần Tuần đã bắt đầu, hãy tích cực tham gia để nhận thưởng!";
                            Main.HeThongCTG(msg, 2);
                            isNotified = true;
                        }
                    } else {
                        isNotified = false;
                    }
                    Thread.sleep(60000); // Check mỗi phút 1 lần cho nhẹ
                } catch (Exception e) {
                }
            }
        }).start();
    }
}