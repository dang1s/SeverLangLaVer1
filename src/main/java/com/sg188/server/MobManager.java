package com.sg188.server;

import SqlConnection.Connect;
import SqlConnection.DBData;
import com.sg188.lib.Log;
import com.sg188.real.MobNew;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MobManager {
    private static final MobManager instance = new MobManager();
    public static MobManager getInstance() {
        return instance;
    }
    public List<MobNew> mobNews= new ArrayList<>();

    public void addMobNew(MobNew mobNew) {
        mobNews.add(mobNew);
    }
    public void removeMobNew(MobNew mobNew) {
        mobNews.remove(mobNew);
    }
    public MobNew getMobNew(int id) {
        for (MobNew mobNew : mobNews) {
            if (mobNew.id == id) {
                return mobNew;
            }
        }
        return null;
    }
    public void initMobNew() {
            try {
                Connection conn = DBData.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `mobnew`;");
                ResultSet resultSet = stmt.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("pet_id");
                    int dxMove = resultSet.getInt("dxMove");
                    int dyMove = resultSet.getInt("dyMove");
                    int dxStand = resultSet.getInt("dxStand");
                    int dyStand = resultSet.getInt("dyStand");
                    int frameStartMove = resultSet.getInt("frameStartMove");
                    int frameEndMove = resultSet.getInt("frameEndMove");
                    int frameStartStand = resultSet.getInt("frameStartStand");
                    int frameEndStand = resultSet.getInt("frameEndStand");
                    boolean isBong = resultSet.getBoolean("isBong");
                    MobNew mobNew = new MobNew(id, dxMove, dyMove, dxStand, dyStand, frameStartMove, frameEndMove, frameStartStand, frameEndStand, isBong);
                    MobManager.getInstance().addMobNew(mobNew);
                }
                resultSet.close();
                stmt.close();
                Log.info("Init mob new done: " + MobManager.getInstance().mobNews.size());
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

}
