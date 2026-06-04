package EventClick;

import SqlConnection.DBData;
import com.sg188.lib.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

// VongQuayNap disabled - not used
/*
public final class VongQuayNapConfig {

    public static int SeasonId = 0;
    public static long StartTimeMillis = 0L;
    public static long EndTimeMillis = 0L;

    private static final String LOAD_QUERY =
            "SELECT Id, start_time_ms, end_time_ms FROM event_vongquay_nap WHERE IsActive = 1 ORDER BY Id DESC LIMIT 1";
    private static final String DEACTIVATE_QUERY =
            "UPDATE event_vongquay_nap SET IsActive = 0 WHERE IsActive = 1";
    private static final String INSERT_QUERY =
            "INSERT INTO event_vongquay_nap (start_time_ms, end_time_ms, IsActive) VALUES (?, ?, 1)";

    private VongQuayNapConfig() {
    }

    public static synchronized boolean isValid() {
        return SeasonId > 0
                && StartTimeMillis > 0
                && EndTimeMillis > 0
                && EndTimeMillis > StartTimeMillis;
    }

    public static synchronized void loadConfig() {
        try {
            Connection connection = getDataConnection();
            if (connection == null) {
                resetConfig();
                Log.warn("[VONG QUAY NAP] Khong mo duoc ket noi DBData de load config.");
                return;
            }

            try (PreparedStatement ps = connection.prepareStatement(LOAD_QUERY);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SeasonId = rs.getInt("Id");
                    StartTimeMillis = rs.getLong("start_time_ms");
                    EndTimeMillis = rs.getLong("end_time_ms");
                    logCurrentConfig();
                    return;
                }
            }

            resetConfig();
            Log.warn("[VONG QUAY NAP] Khong tim thay ban ghi event_vongquay_nap IsActive=1 trong langladata.");
        } catch (Exception ex) {
            resetConfig();
            Log.error("[VONG QUAY NAP] Loi load config", ex);
        }
    }

    public static synchronized boolean saveConfig(long startTimeMillis, long endTimeMillis) {
        if (startTimeMillis <= 0 || endTimeMillis <= startTimeMillis) {
            Log.warn("[VONG QUAY NAP] Tu choi luu config khong hop le. Start=" + startTimeMillis + ", End=" + endTimeMillis);
            return false;
        }

        try {
            Connection connection = getDataConnection();
            if (connection == null) {
                Log.warn("[VONG QUAY NAP] Khong mo duoc ket noi DBData de luu config.");
                return false;
            }

            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try (PreparedStatement deactivate = connection.prepareStatement(DEACTIVATE_QUERY);
                 PreparedStatement insert = connection.prepareStatement(INSERT_QUERY)) {
                deactivate.executeUpdate();

                insert.setLong(1, startTimeMillis);
                insert.setLong(2, endTimeMillis);
                insert.executeUpdate();

                connection.commit();
                restoreAutoCommit(connection, oldAutoCommit);
                Log.info("[VONG QUAY NAP] Da luu config moi vao langladata. Start=" + startTimeMillis + ", End=" + endTimeMillis);
                return true;
            } catch (Exception ex) {
                rollbackQuietly(connection);
                restoreAutoCommit(connection, oldAutoCommit);
                Log.error("[VONG QUAY NAP] Loi luu config", ex);
                return false;
            }
        } catch (Exception ex) {
            Log.error("[VONG QUAY NAP] Loi ket noi khi luu config", ex);
            return false;
        }
    }

    public static synchronized void logCurrentConfig() {
        Log.info("[VONG QUAY NAP] Config: SeasonId=" + SeasonId
                + ", Start=" + StartTimeMillis
                + " (" + formatMillis(StartTimeMillis) + ")"
                + ", End=" + EndTimeMillis
                + " (" + formatMillis(EndTimeMillis) + ")"
                + ", IsValid=" + isValid());
    }

    public static synchronized void resetMonthlySeason() {
        resetConfig();
        logCurrentConfig();
    }

    public static synchronized String getFormattedStart() {
        return formatMillis(StartTimeMillis);
    }

    public static synchronized String getFormattedEnd() {
        return formatMillis(EndTimeMillis);
    }

    public static String formatMillis(long millis) {
        if (millis <= 0) {
            return "";
        }
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(millis));
    }

    private static synchronized void resetConfig() {
        SeasonId = 0;
        StartTimeMillis = 0L;
        EndTimeMillis = 0L;
    }

    private static Connection getDataConnection() throws SQLException {
        Connection connection = DBData.getConnection();
        if (connection == null || connection.isClosed()) {
            DBData.openConnection();
            connection = DBData.getConnection();
        }
        return connection;
    }

    private static void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
        }
    }

    private static void restoreAutoCommit(Connection connection, boolean autoCommit) {
        try {
            connection.setAutoCommit(autoCommit);
        } catch (SQLException ignored) {
        }
    }
}
*/
