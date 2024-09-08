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
            if (props.containsKey("game.event")) {
                event = props.getProperty("game.event");
            }
        } catch (IOException | NumberFormatException ex) {
            Log.error("load config err: " + ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public String getJdbcUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
    }
    public String getMongodbUrl() {
        if (!StringUtils.isNullOrEmpty(mongodbUser) && !StringUtils.isNullOrEmpty(mongodbPassword)) {
            return String.format("mongodb://%s:%s@%s:%d/%s", mongodbUser, mongodbPassword, mongodbHost, mongodbPort, mongodbName);
        }
        return String.format("mongodb://%s:%d", mongodbHost, mongodbPort);
    }
}