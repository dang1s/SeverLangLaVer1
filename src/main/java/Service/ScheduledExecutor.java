package Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScheduledExecutor {
    private static ScheduledExecutorService executor;

    private ScheduledExecutor() {} // Private constructor to prevent instantiation

    public static ScheduledExecutorService getInstance() {
        if (executor == null) {
            executor = Executors.newScheduledThreadPool(50);
        }
        return executor;
    }

    // Optional: method to shut down the executor service when it's no longer needed
    public static void shutdown() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}

