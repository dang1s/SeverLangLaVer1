package com.sg188.server;

import com.sg188.lib.Log;
import com.sg188.lib.Utlis;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AutoMaintenance {
    public static void maintenance(int hours, int minutes, int seconds) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext5;
        zonedNext5 = zonedNow.withHour(hours).withMinute(minutes).withSecond(seconds);
        if (zonedNow.compareTo(zonedNext5) > 0) {
            zonedNext5 = zonedNext5.plusDays(1);
        }

        Duration duration = Duration.between(zonedNow, zonedNext5);
        long initalDelay = duration.getSeconds();
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    Main.maintance();
                } finally {
                    openCmd(new String(Utlis.getFile("run.bat")));
                    System.exit(1);
                }
            }
        };
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
        Log.info("Tự động bảo trì " + hours + "h" + minutes);

    }

    public static void openCmd(String cmd) {
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec("cmd /c start cmd.exe /K \"dir && " + cmd);
        } catch (IOException ex) {
            Logger.getLogger(AutoMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
