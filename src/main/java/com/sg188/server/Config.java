package com.sg188.server;

import com.mysql.cj.util.StringUtils;
import com.sg188.lib.Log;
import lombok.Getter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.concurrent.Semaphore;

@Getter
public class Config {

    private static final Config instance = new Config();

    public static Config getInstance() {
        return instance;
    }
    public String dbHost;
    public int dbPort;
    public String dbUser;
    public String dbPassword;
    public String dbName;
    public String dbData;
    public boolean isDebug;
    // MongoDB
    private String mongodbHost;
    private int mongodbPort;
    public String mongodbName;
    private String mongodbUser;
    private String mongodbPassword;
    private String event;
    private int gameExpRate = 4;
    private int gameServerPort = 2907;
    private int healthCheckPort = 2908;
    private String clientKey = "default_key_12345";
    // Admin Web API
    private int adminWebPort = 8081;
    private String adminWebToken = "langla_local_admin_2026";
    public final Semaphore loginSemaphore = new Semaphore(300);
    public boolean load() {
        try {
            FileInputStream input = new FileInputStream(new File("config.properties"));
            Properties props = new Properties();
            props.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            props.forEach((t, u) -> {
                Log.info(String.format("Config - %s: %s", t, u));
            });
            dbHost = props.getProperty("db.host");
            dbPort = Integer.parseInt(props.getProperty("db.port"));
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");
            dbName = props.getProperty("db.dbname");
            dbData = props.getProperty("db.dbData");
            isDebug = Boolean.parseBoolean(props.getProperty("server.debug"));
            mongodbHost = props.getProperty("mongodb.host");
            mongodbPort = Integer.parseInt(props.getProperty("mongodb.port"));
            mongodbUser = props.getProperty("mongodb.user");
            mongodbPassword = props.getProperty("mongodb.password");
            mongodbName = props.getProperty("mongodb.dbname");
            gameServerPort = Integer.parseInt(props.getProperty("server.port", String.valueOf(gameServerPort)));
            healthCheckPort = Integer.parseInt(props.getProperty("server.check.port", String.valueOf(healthCheckPort)));
            if (props.containsKey("game.exp")) {
                gameExpRate = Integer.parseInt(props.getProperty("game.exp"));
            }
            if (props.containsKey("game.event")) {
                event = props.getProperty("game.event");
            }
            if (props.containsKey("server.clientKey")) {
                clientKey = props.getProperty("server.clientKey");
            }
            adminWebPort = Integer.parseInt(props.getProperty("admin.web.port", String.valueOf(adminWebPort)));
            adminWebToken = props.getProperty("admin.web.token", adminWebToken);
        } catch (IOException | NumberFormatException ex) {
            Log.error("load config err: " + ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public String getJdbcUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
    }

    public String getEvent() {
        return event;
    }

    public int getGameExpRate() {
        return gameExpRate;
    }

    public String getMongodbUrl() {
        if (!StringUtils.isNullOrEmpty(mongodbUser) && !StringUtils.isNullOrEmpty(mongodbPassword)) {
            return String.format("mongodb://%s:%s@%s:%d/%s", mongodbUser, mongodbPassword, mongodbHost, mongodbPort, mongodbName);
        }
        return String.format("mongodb://%s:%d", mongodbHost, mongodbPort);
    }

    public synchronized void setRuntimeEvent(String event) {
        this.event = event;
    }

    public synchronized void setRuntimeGameExpRate(int gameExpRate) {
        this.gameExpRate = Math.max(1, gameExpRate);
    }

    public String getClientKey() {
        return clientKey;
    }

    public int getAdminWebPort() {
        return adminWebPort;
    }

    public String getAdminWebToken() {
        return adminWebToken;
    }
}
