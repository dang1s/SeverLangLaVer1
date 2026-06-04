package EventClick;

import com.sg188.server.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ConfigCuaCaiTuan {
    private static final String PRIMARY_KEY = "cuaCaiTuan.start";
    private static final String LEGACY_KEY = "cuacaituan.start";
    private static final String DEFAULT_START = "14-11-2025 00:00:00";
    private static final long WEEK_MS = 7L * 24 * 60 * 60 * 1000;

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
            START_STR = prop.getProperty(PRIMARY_KEY, prop.getProperty(LEGACY_KEY, DEFAULT_START));
            START = parseDate(START_STR);

            long now = System.currentTimeMillis();
            boolean updated = false;
            while (START + WEEK_MS <= now) {
                START += WEEK_MS;
                updated = true;
            }

            START_STR = formatDate(START);
            END = START + WEEK_MS;

            if (!START_STR.equals(prop.getProperty(PRIMARY_KEY)) || !START_STR.equals(prop.getProperty(LEGACY_KEY))) {
                updated = true;
            }
            if (updated) {
                prop.setProperty(PRIMARY_KEY, START_STR);
                prop.setProperty(LEGACY_KEY, START_STR);
                try (FileOutputStream out = new FileOutputStream("event.properties")) {
                    prop.store(out, "Update Weekly Reset");
                }
            }

            System.out.println("Su kien Cua Cai Tuan: " + START_STR + " -> " + formatDate(END));
        } catch (IOException ex) {
            System.out.println("Khong tim thay file event.properties, su dung mac dinh cho Cua Cai Tuan.");
            START_STR = DEFAULT_START;
            START = parseDate(START_STR);
            END = START + WEEK_MS;
        }

        long now = System.currentTimeMillis();
        if (now >= START && now <= END && !isNotified) {
            Main.HeThongCTG("Su kien Dua Top Cua Cai Tuan da chinh thuc bat dau!", 2);
            isNotified = true;
        }
    }

    public static void saveConfig(String start) {
        Properties prop = new Properties();
        File file = new File("event.properties");

        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                prop.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        prop.setProperty(PRIMARY_KEY, start);
        prop.setProperty(LEGACY_KEY, start);

        try (FileOutputStream out = new FileOutputStream("event.properties")) {
            prop.store(out, "Update Weekly Reset");
            loadConfig();
            isNotified = false;
            System.out.println(">>> Da luu va reset thong bao Cua Cai Tuan!");
        } catch (IOException e) {
            System.err.println("Loi khi ghi file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void nextWeek() {
        long newStart = END;
        saveConfig(formatDate(newStart));
    }

    public static long parseDate(String s) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(s).getTime();
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    public static void startCheckTimeThread() {
        new Thread(() -> {
            while (true) {
                try {
                    long now = System.currentTimeMillis();
                    if (now >= START && now <= END) {
                        if (!isNotified) {
                            Main.HeThongCTG("Su kien Cua Cai Tuan da bat dau, hay tich cuc tham gia de nhan thuong!", 2);
                            isNotified = true;
                        }
                    } else {
                        isNotified = false;
                    }
                    Thread.sleep(60000);
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private static String formatDate(long time) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(time));
    }
}
