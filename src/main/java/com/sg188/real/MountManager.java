package com.sg188.real;

import SqlConnection.DBData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MountManager {
    private static final MountManager instance = new MountManager();
    public static MountManager getInstance() {
        return instance;
    }
    public List<Mount> mounts = new ArrayList<>();
    public void addMount(Mount mount) {
        mounts.add(mount);
    }
    public void removeMount(Mount mount) {
        mounts.remove(mount);
    }
    public Mount getMount(int id) {
        for (Mount mount : mounts) {
            if (mount.id == id) {
                return mount;
            }
        }
        return null;
    }

    public void loadMount(){
        try {
            Connection conn = DBData.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `mount`;");
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int frameStart = resultSet.getInt("frameStart");
                int frameEnd = resultSet.getInt("frameEnd");
                int tick = resultSet.getInt("tick");
                int attackStart = resultSet.getInt("attackStart");
                int attackEnd = resultSet.getInt("attackEnd");
                int tickAttack = resultSet.getInt("tickAttack");
                int dx = resultSet.getInt("dx");
                int dy = resultSet.getInt("dy");
                Mount mount= new Mount(id, frameStart, frameEnd, tick, attackStart, attackEnd, tickAttack, dx, dy);
                MountManager.getInstance().addMount(mount);
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
