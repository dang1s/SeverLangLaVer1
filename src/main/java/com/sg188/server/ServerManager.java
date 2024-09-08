package com.sg188.server;

import com.sg188.real.Char;
import com.sg188.real.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerManager {

    public static ConcurrentHashMap<String,User> users = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer,Char> chars = new ConcurrentHashMap<>();
    public static final HashMap<Integer, Long> HASH_MAP = new HashMap<>();

    public static List<Char> getChars() {
        return new ArrayList<>(chars.values());
    }

    public static List<User> getUsers() {
        // Chuyển đổi Collection của các giá trị sang ArrayList mới
        return new ArrayList<>(users.values());
    }

    public static int getNumberOnline() {
        return chars.size();
    }
    public static  HashMap<String, Long> timeWaitLogin = new HashMap();


    public static User findUserByUsername(String username) {
        return users.get(username);

    }

    public static Char findCharById(int id) {
            return chars.get(id);

    }

    public static Char findCharByName(String name) {
        for (Char _char : chars.values()) {
            if (_char != null && _char.Info.name.equalsIgnoreCase(name)) {
                return _char;
            }
        }
        return null;
    }

    public static void addUser(User user) {
        users.put(user.username, user);
    }

    public static void addChar(Char _char) {
        chars.put(_char.id,_char);
    }

    public static void removeUser(User user) {
        users.remove(user.username);
        if(!timeWaitLogin.containsKey(user.username)) {
            timeWaitLogin.put(user.username, System.currentTimeMillis() + 1000L);
        }
    }

    public static void removeChar(Char _char) {
        chars.remove(_char.id);
    }
}
