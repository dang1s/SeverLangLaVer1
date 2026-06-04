package SqlConnection;

import java.sql.Connection;
import java.sql.SQLException;

import com.sg188.lib.Log;
import com.sg188.server.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

/**
 * Connection Pool Manager sử dụng HikariCP
 * Tối ưu hiệu suất và quản lý kết nối database hiệu quả
 */
public class ConnectionPool {
    
    private static HikariDataSource dataSource;
    private static HikariDataSource dataSourceDBData;
    
    private static final int MAX_POOL_SIZE = 50; // Tối đa 50 connections trong pool
    private static final int MIN_IDLE = 10; // Giữ tối thiểu 10 connections
    private static final long CONNECTION_TIMEOUT = 30000; // 30 giây timeout
    private static final long IDLE_TIMEOUT = 600000; // 10 phút idle timeout
    private static final long MAX_LIFETIME = 1800000; // 30 phút max lifetime
    
    static {
        initializePools();
    }
    
    private static void initializePools() {
        try {
            // Pool cho database chính (Connect)
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + Config.getInstance().dbHost + ":" + 
                             Config.getInstance().dbPort + "/" + Config.getInstance().dbName);
            config.setUsername(Config.getInstance().dbUser);
            config.setPassword(Config.getInstance().dbPassword);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            // Tối ưu pool settings
            config.setMaximumPoolSize(MAX_POOL_SIZE);
            config.setMinimumIdle(MIN_IDLE);
            config.setConnectionTimeout(CONNECTION_TIMEOUT);
            config.setIdleTimeout(IDLE_TIMEOUT);
            config.setMaxLifetime(MAX_LIFETIME);
            config.setLeakDetectionThreshold(60000); // Phát hiện leak connection sau 60s
            config.setPoolName("MainPool");
            
            // Tối ưu MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
            
            dataSource = new HikariDataSource(config);
            
            // Pool cho DBData
            HikariConfig configDBData = new HikariConfig();
            configDBData.setJdbcUrl("jdbc:mysql://" + Config.getInstance().dbHost + ":" + 
                                   Config.getInstance().dbPort + "/" + Config.getInstance().dbData);
            configDBData.setUsername(Config.getInstance().dbUser);
            configDBData.setPassword(Config.getInstance().dbPassword);
            configDBData.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            configDBData.setMaximumPoolSize(MAX_POOL_SIZE);
            configDBData.setMinimumIdle(MIN_IDLE);
            configDBData.setConnectionTimeout(CONNECTION_TIMEOUT);
            configDBData.setIdleTimeout(IDLE_TIMEOUT);
            configDBData.setMaxLifetime(MAX_LIFETIME);
            configDBData.setLeakDetectionThreshold(60000);
            configDBData.setPoolName("DBDataPool");
            
            // Tối ưu MySQL
            configDBData.addDataSourceProperty("cachePrepStmts", "true");
            configDBData.addDataSourceProperty("prepStmtCacheSize", "250");
            configDBData.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            configDBData.addDataSourceProperty("useServerPrepStmts", "true");
            configDBData.addDataSourceProperty("useLocalSessionState", "true");
            configDBData.addDataSourceProperty("rewriteBatchedStatements", "true");
            configDBData.addDataSourceProperty("cacheResultSetMetadata", "true");
            configDBData.addDataSourceProperty("cacheServerConfiguration", "true");
            configDBData.addDataSourceProperty("elideSetAutoCommits", "true");
            configDBData.addDataSourceProperty("maintainTimeStats", "false");
            
            dataSourceDBData = new HikariDataSource(configDBData);
            
        //    Log.info("Connection pools initialized successfully");
        } catch (Exception e) {
            Log.error("Failed to initialize connection pools", e);
            throw new RuntimeException("Connection pool initialization failed", e);
        }
    }
    
    public static boolean isPoolOpen() {
        return dataSource != null && !dataSource.isClosed();
    }
    public static boolean isDBDataPoolOpen() {
        return dataSourceDBData != null && !dataSourceDBData.isClosed();
    }
    
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Connection pool not initialized or has been closed");
        }
        return dataSource.getConnection();
    }
    
    public static Connection getDBDataConnection() throws SQLException {
        if (dataSourceDBData == null || dataSourceDBData.isClosed()) {
            throw new SQLException("DBData connection pool not initialized or has been closed");
        }
        return dataSourceDBData.getConnection();
    }
    
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
            Log.info("Main connection pool closed");
        }
        if (dataSourceDBData != null) {
            dataSourceDBData.close();
            Log.info("DBData connection pool closed");
        }
    }
    
    public static String getPoolStats() {
        StringBuilder stats = new StringBuilder();
        if (dataSource != null) {
            HikariPoolMXBean poolBean = dataSource.getHikariPoolMXBean();
            stats.append("Main Pool - Active: ").append(poolBean.getActiveConnections())
                 .append(", Idle: ").append(poolBean.getIdleConnections())
                 .append(", Total: ").append(poolBean.getTotalConnections())
                 .append(", Threads Awaiting: ").append(poolBean.getThreadsAwaitingConnection())
                 .append("\n");
        }
        if (dataSourceDBData != null) {
            HikariPoolMXBean poolBean = dataSourceDBData.getHikariPoolMXBean();
            stats.append("DBData Pool - Active: ").append(poolBean.getActiveConnections())
                 .append(", Idle: ").append(poolBean.getIdleConnections())
                 .append(", Total: ").append(poolBean.getTotalConnections())
                 .append(", Threads Awaiting: ").append(poolBean.getThreadsAwaitingConnection());
        }
        return stats.toString();
    }
}
