package MapService;

import com.sg188.lib.Log;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapThreadWatchdog {
    private static MapThreadWatchdog instance;
    private Thread watchdogThread;
    private volatile boolean running = true;
    private final long CHECK_INTERVAL = 5000; // Kiểm tra mỗi 5 giây
    private final long HEARTBEAT_TIMEOUT = 10000; // Thread treo nếu không heartbeat trong 10 giây
    
    // Lưu trữ thời gian heartbeat cuối cùng của mỗi thread
    private final Map<String, Long> heartbeatMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> recoveryCountMap = new ConcurrentHashMap<>();
    private final int MAX_RECOVERY_ATTEMPTS = 3; // Tối đa 3 lần khôi phục liên tiếp

    private MapThreadWatchdog() {
    }

    public static MapThreadWatchdog getInstance() {
        if (instance == null) {
            instance = new MapThreadWatchdog();
        }
        return instance;
    }

    /**
     * Được gọi bởi các thread trong Map để báo hiệu thread vẫn hoạt động
     */
    public void recordHeartbeat(String threadKey) {
        heartbeatMap.put(threadKey, System.currentTimeMillis());
    }

    public void start() {
        if (watchdogThread != null && watchdogThread.isAlive()) {
            Log.info("MapThreadWatchdog đã đang chạy");
            return;
        }

        running = true;
        watchdogThread = new Thread(() -> {
            Log.info("MapThreadWatchdog đã khởi động");
            
            while (running) {
                try {
                    checkAndRecoverMapThreads();
                    Thread.sleep(CHECK_INTERVAL);
                } catch (InterruptedException e) {
                    Log.info("MapThreadWatchdog bị gián đoạn");
                    break;
                } catch (Exception e) {
                    Log.error("Lỗi trong MapThreadWatchdog: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            Log.info("MapThreadWatchdog đã dừng");
        }, "MapThreadWatchdog");
        
        watchdogThread.setDaemon(true);
        watchdogThread.start();
    }

    private void checkAndRecoverMapThreads() {
        if (MapService.Map.maps == null) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < MapService.Map.maps.length; i++) {
            MapService.Map map = MapService.Map.maps[i];
            if (map == null) {
                continue;
            }

            String charThreadKey = "map-" + map.mapID + "-updateChar";
            String otherThreadKey = "map-" + map.mapID + "-updateOther";

            // Kiểm tra threadUpdateChar
            if (isThreadDeadOrHanging(map.threadUpdateChar, charThreadKey, currentTime)) {
                Log.warn("Map " + map.mapID + " - threadUpdateChar đã die/treo, đang khôi phục...");
                
                if (canRecover(charThreadKey)) {
                    recoverUpdateCharThread(map, charThreadKey);
                } else {
                    Log.error("Map " + map.mapID + " - threadUpdateChar đã vượt quá số lần khôi phục cho phép!");
                }
            } else {
                // Reset recovery count nếu thread hoạt động bình thường
                recoveryCountMap.put(charThreadKey, 0);
            }

            // Kiểm tra threadUpdateOther
            if (isThreadDeadOrHanging(map.threadUpdateOther, otherThreadKey, currentTime)) {
                Log.warn("Map " + map.mapID + " - threadUpdateOther đã die/treo, đang khôi phục...");
                
                if (canRecover(otherThreadKey)) {
                    recoverUpdateOtherThread(map, otherThreadKey);
                } else {
                    Log.error("Map " + map.mapID + " - threadUpdateOther đã vượt quá số lần khôi phục cho phép!");
                }
            } else {
                // Reset recovery count nếu thread hoạt động bình thường
                recoveryCountMap.put(otherThreadKey, 0);
            }
        }
    }

    private boolean isThreadDeadOrHanging(Thread thread, String threadKey, long currentTime) {
        // Kiểm tra thread die
        if (thread == null || !thread.isAlive()) {
            return true;
        }

        // Kiểm tra thread treo (không có heartbeat)
        Long lastHeartbeat = heartbeatMap.get(threadKey);
        if (lastHeartbeat == null) {
            // Lần đầu tiên kiểm tra, khởi tạo heartbeat
            heartbeatMap.put(threadKey, currentTime);
            return false;
        }

        // Nếu không có heartbeat trong HEARTBEAT_TIMEOUT ms => thread treo
        return (currentTime - lastHeartbeat) > HEARTBEAT_TIMEOUT;
    }

    private boolean canRecover(String threadKey) {
        int count = recoveryCountMap.getOrDefault(threadKey, 0);
        if (count >= MAX_RECOVERY_ATTEMPTS) {
            return false;
        }
        recoveryCountMap.put(threadKey, count + 1);
        return true;
    }

    private void recoverUpdateCharThread(MapService.Map map, String threadKey) {
        try {
            // Đóng thread cũ nếu còn tồn tại
            if (map.threadUpdateChar != null && map.threadUpdateChar.isAlive()) {
                map.threadUpdateChar.interrupt();
                map.threadUpdateChar.join(1000); // Đợi tối đa 1 giây
            }

            // Reset heartbeat
            heartbeatMap.remove(threadKey);

            // Tạo thread mới
            map.threadUpdateChar = new Thread(() -> {
                map.updateChar();
            }, "UpdateChar-Map-" + map.mapID);
            
            map.threadUpdateChar.start();
            Log.info("Map " + map.mapID + " - threadUpdateChar đã được khôi phục");
            
        } catch (Exception e) {
            Log.error("Lỗi khi khôi phục threadUpdateChar cho Map " + map.mapID + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void recoverUpdateOtherThread(MapService.Map map, String threadKey) {
        try {
            // Đóng thread cũ nếu còn tồn tại
            if (map.threadUpdateOther != null && map.threadUpdateOther.isAlive()) {
                map.threadUpdateOther.interrupt();
                map.threadUpdateOther.join(1000); // Đợi tối đa 1 giây
            }

            // Reset heartbeat
            heartbeatMap.remove(threadKey);

            // Tạo thread mới
            map.threadUpdateOther = new Thread(() -> {
                map.updateOther();
            }, "UpdateOther-Map-" + map.mapID);
            
            map.threadUpdateOther.start();
            Log.info("Map " + map.mapID + " - threadUpdateOther đã được khôi phục");
            
        } catch (Exception e) {
            Log.error("Lỗi khi khôi phục threadUpdateOther cho Map " + map.mapID + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        if (watchdogThread != null && watchdogThread.isAlive()) {
            watchdogThread.interrupt();
            try {
                watchdogThread.join(2000);
            } catch (InterruptedException e) {
                Log.error("Lỗi khi dừng MapThreadWatchdog: " + e.getMessage());
            }
        }
        watchdogThread = null;
        heartbeatMap.clear();
        recoveryCountMap.clear();
    }

    public boolean isRunning() {
        return running && watchdogThread != null && watchdogThread.isAlive();
    }

    /**
     * Lấy thông tin trạng thái của các thread (để debug)
     */
    public String getThreadStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Map Thread Status ===\n");
        
        if (MapService.Map.maps == null) {
            sb.append("Maps chưa được khởi tạo\n");
            return sb.toString();
        }

        long currentTime = System.currentTimeMillis();
        for (int i = 0; i < MapService.Map.maps.length; i++) {
            MapService.Map map = MapService.Map.maps[i];
            if (map == null) continue;

            String charThreadKey = "map-" + map.mapID + "-updateChar";
            String otherThreadKey = "map-" + map.mapID + "-updateOther";

            sb.append("Map ").append(map.mapID).append(":\n");
            sb.append("  UpdateChar: ")
              .append(getThreadStatusString(map.threadUpdateChar, charThreadKey, currentTime))
              .append("\n");
            sb.append("  UpdateOther: ")
              .append(getThreadStatusString(map.threadUpdateOther, otherThreadKey, currentTime))
              .append("\n");
        }
        
        return sb.toString();
    }

    private String getThreadStatusString(Thread thread, String threadKey, long currentTime) {
        if (thread == null) {
            return "NULL";
        }
        if (!thread.isAlive()) {
            return "DEAD";
        }
        
        Long lastHeartbeat = heartbeatMap.get(threadKey);
        if (lastHeartbeat == null) {
            return "ALIVE (no heartbeat data)";
        }
        
        long timeSinceHeartbeat = currentTime - lastHeartbeat;
        if (timeSinceHeartbeat > HEARTBEAT_TIMEOUT) {
            return "HANGING (no heartbeat for " + timeSinceHeartbeat + "ms)";
        }
        
        return "OK (heartbeat " + timeSinceHeartbeat + "ms ago)";
    }
}