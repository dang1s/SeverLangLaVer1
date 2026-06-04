package EventClick;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ConfigCuongHoaTuan {

    public static String START_STR;
    public static String END_STR; // Thêm biến này để không bị lỗi gạch đỏ ở Main
    public static long START;
    public static long END;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        LinkedProperties prop = new LinkedProperties();
        File file = new File("config.properties");
        
        if (file.exists()) {
            try (FileInputStream input = new FileInputStream(file)) {
                prop.load(input);
                START_STR = prop.getProperty("cuonghoatuan.start", "14-11-2025 00:00:00");
                // Tự động tính END_STR dựa trên START_STR + 7 ngày nếu không có trong file
                START = parse(START_STR);
                END = START + 7L * 24 * 60 * 60 * 1000;
                END_STR = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date(END));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            START_STR = "14-11-2025 00:00:00";
            START = parse(START_STR);
            END = START + 7L * 24 * 60 * 60 * 1000;
            END_STR = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date(END));
        }
    }

    public static void saveConfig(String startStr) {
        LinkedProperties prop = new LinkedProperties();
        File file = new File("config.properties");

        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                prop.load(in);
            } catch (IOException e) { }
        }

        prop.setProperty("cuonghoatuan.start", startStr);

        try (FileOutputStream out = new FileOutputStream(file)) {
            prop.store(out, null);
            loadConfig(); // Cập nhật lại các biến START, END, END_STR
            System.out.println(">>> Đã lưu Cường Hóa Tuần!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long parse(String s) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(s).getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }
}