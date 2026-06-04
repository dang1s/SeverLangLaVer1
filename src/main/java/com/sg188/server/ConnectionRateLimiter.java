package com.sg188.server;

import com.sg188.lib.Log;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Quản lý rate limiting và chống spam kết nối
 */
public class ConnectionRateLimiter {
    
    // Cấu hình rate limiting
    private static final int MAX_CONNECTIONS_PER_IP_PER_MINUTE = 500; // Tối đa 50 kết nối/phút từ 1 IP
    private static final int MAX_CONNECTIONS_PER_IP_PER_HOUR = 50000; // Tối đa 50 kết nối/giờ từ 1 IP
    private static final int MAX_FAILED_ATTEMPTS = 500; // Sau 5 lần thất bại sẽ bị blacklist
    private static final long BLACKLIST_DURATION = 1 * 60 * 1000; // Blacklist 1 phút (tự động clean)
    private static final long CLEANUP_INTERVAL = 1 * 60 * 1000; // Dọn dẹp mỗi 1 phút để clean blacklist
    
    // Giới hạn tổng số session đồng thời
    private static final int MAX_TOTAL_SESSIONS = 1000;
    
    // Tracking kết nối theo IP
    private static final ConcurrentHashMap<String, IPConnectionInfo> ipConnectionMap = new ConcurrentHashMap<>();
    
    // Tracking số session hiện tại
    private static final AtomicInteger currentSessionCount = new AtomicInteger(0);
    
    // Thread cleanup
    private static Thread cleanupThread;
    
    static {
        startCleanupThread();
    }
    
    /**
     * Kiểm tra xem IP có được phép kết nối không (chỉ kiểm tra, không tăng counter)
     */
    public static boolean allowConnection(String ip) {
        // Kiểm tra blacklist
        IPConnectionInfo info = ipConnectionMap.get(ip);
        if (info != null && info.isBlacklisted()) {
            Log.debug("Connection blocked: IP " + ip + " is blacklisted");
            return false;
        }
        
        // Kiểm tra giới hạn tổng số session
        if (currentSessionCount.get() >= MAX_TOTAL_SESSIONS) {
            Log.debug("Connection blocked: Maximum total sessions reached (" + MAX_TOTAL_SESSIONS + ")");
            return false;
        }
        
        // Kiểm tra rate limit
        long currentTime = System.currentTimeMillis();
        if (info == null) {
            info = new IPConnectionInfo();
            ipConnectionMap.put(ip, info);
        }
        
        // Kiểm tra số kết nối trong 1 phút
        info.cleanupOldConnections(currentTime);
        if (info.getConnectionsInLastMinute() >= MAX_CONNECTIONS_PER_IP_PER_MINUTE) {
            Log.debug("Connection blocked: IP " + ip + " exceeded rate limit per minute");
            return false;
        }
        
        // Kiểm tra số kết nối trong 1 giờ
        if (info.getConnectionsInLastHour() >= MAX_CONNECTIONS_PER_IP_PER_HOUR) {
            Log.debug("Connection blocked: IP " + ip + " exceeded rate limit per hour");
            return false;
        }
        
        // Cho phép kết nối - ghi nhận kết nối attempt
        info.addConnection(currentTime);
        return true;
    }
    
    /**
     * Ghi nhận session đã được tạo thành công (tăng counter)
     */
    public static void onSessionCreated() {
        currentSessionCount.incrementAndGet();
    }
    
    /**
     * Ghi nhận kết nối thất bại
     */
    public static void recordFailedConnection(String ip) {
        IPConnectionInfo info = ipConnectionMap.get(ip);
        if (info == null) {
            info = new IPConnectionInfo();
            ipConnectionMap.put(ip, info);
        }
        
        int failedCount = info.incrementFailedAttempts();
        if (failedCount >= MAX_FAILED_ATTEMPTS) {
            info.setBlacklisted(true);
            Log.debug("IP " + ip + " has been blacklisted due to " + failedCount + " failed connection attempts");
        }
    }
    
    /**
     * Ghi nhận kết nối thành công (reset failed attempts)
     */
    public static void recordSuccessfulConnection(String ip) {
        IPConnectionInfo info = ipConnectionMap.get(ip);
        if (info != null) {
            info.resetFailedAttempts();
        }
    }
    
    /**
     * Giảm số session khi một session đóng
     */
    public static void onSessionClosed() {
        currentSessionCount.decrementAndGet();
    }
    
    /**
     * Lấy số session hiện tại
     */
    public static int getCurrentSessionCount() {
        return currentSessionCount.get();
    }
    
    /**
     * Lấy số IP đang bị blacklist
     */
    public static int getBlacklistedIPCount() {
        int count = 0;
        for (IPConnectionInfo info : ipConnectionMap.values()) {
            if (info.isBlacklisted()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Xóa blacklist cho một IP (dùng cho admin)
     */
    public static void unblacklistIP(String ip) {
        IPConnectionInfo info = ipConnectionMap.get(ip);
        if (info != null) {
            info.setBlacklisted(false);
            info.resetFailedAttempts();
            Log.info("IP " + ip + " has been unblacklisted");
        }
    }
    
    /**
     * Xóa tất cả blacklist (dùng cho admin)
     */
    public static int clearAllBlacklist() {
        int count = 0;
        for (IPConnectionInfo info : ipConnectionMap.values()) {
            if (info.isBlacklisted()) {
                info.setBlacklisted(false);
                info.resetFailedAttempts();
                count++;
            }
        }
        // Nếu không có IP nào đang blacklist, vẫn reset tất cả failed attempts để đảm bảo clean hoàn toàn
        if (count == 0) {
            for (IPConnectionInfo info : ipConnectionMap.values()) {
                info.resetFailedAttempts();
                info.setBlacklisted(false); // Đảm bảo reset cả blacklistTime
            }
        }
        Log.info("Cleared " + count + " blacklisted IPs and reset all failed attempts");
        return count;
    }
    
    /**
     * Thread dọn dẹp các IP cũ và hết hạn blacklist
     */
    private static void startCleanupThread() {
        cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(CLEANUP_INTERVAL);
                    cleanup();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        cleanupThread.setName("ConnectionRateLimiter-Cleanup");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }
    
    /**
     * Dọn dẹp các IP cũ và kiểm tra hết hạn blacklist
     */
    private static void cleanup() {
        long currentTime = System.currentTimeMillis();
        int removed = 0;
        
        for (java.util.Map.Entry<String, IPConnectionInfo> entry : ipConnectionMap.entrySet()) {
            IPConnectionInfo info = entry.getValue();
            
            // Kiểm tra hết hạn blacklist
            if (info.isBlacklisted() && (currentTime - info.getBlacklistTime()) > BLACKLIST_DURATION) {
                info.setBlacklisted(false);
                info.resetFailedAttempts();
                Log.debug("IP " + entry.getKey() + " blacklist expired");
            }
            
            // Xóa các IP không có hoạt động trong 2 giờ
            info.cleanupOldConnections(currentTime);
            if (info.getConnectionsInLastHour() == 0 && 
                (currentTime - info.getLastConnectionTime()) > 2 * 60 * 60 * 1000) {
                ipConnectionMap.remove(entry.getKey());
                removed++;
            }
        }
        
        if (removed > 0) {
            Log.debug("Cleaned up " + removed + " inactive IP entries");
        }
    }
    
    /**
     * Thông tin kết nối của một IP
     */
    private static class IPConnectionInfo {
        private final java.util.List<Long> connectionTimes = new java.util.ArrayList<>();
        private final AtomicInteger failedAttempts = new AtomicInteger(0);
        private final AtomicLong blacklistTime = new AtomicLong(0);
        private volatile boolean blacklisted = false;
        private volatile long lastConnectionTime = 0;
        
        public synchronized void addConnection(long time) {
            connectionTimes.add(time);
            lastConnectionTime = time;
        }
        
        public synchronized int getConnectionsInLastMinute() {
            long oneMinuteAgo = System.currentTimeMillis() - 60 * 1000;
            return (int) connectionTimes.stream()
                    .filter(t -> t > oneMinuteAgo)
                    .count();
        }
        
        public synchronized int getConnectionsInLastHour() {
            long oneHourAgo = System.currentTimeMillis() - 60 * 60 * 1000;
            return (int) connectionTimes.stream()
                    .filter(t -> t > oneHourAgo)
                    .count();
        }
        
        public synchronized void cleanupOldConnections(long currentTime) {
            long oneHourAgo = currentTime - 60 * 60 * 1000;
            connectionTimes.removeIf(t -> t < oneHourAgo);
        }
        
        public int incrementFailedAttempts() {
            return failedAttempts.incrementAndGet();
        }
        
        public void resetFailedAttempts() {
            failedAttempts.set(0);
        }
        
        public boolean isBlacklisted() {
            return blacklisted;
        }
        
        public void setBlacklisted(boolean blacklisted) {
            this.blacklisted = blacklisted;
            if (blacklisted) {
                blacklistTime.set(System.currentTimeMillis());
            } else {
                // Reset blacklistTime về 0 khi clear blacklist để tránh bị block lại
                blacklistTime.set(0);
            }
        }
        
        public long getBlacklistTime() {
            return blacklistTime.get();
        }
        
        public long getLastConnectionTime() {
            return lastConnectionTime;
        }
    }
}
