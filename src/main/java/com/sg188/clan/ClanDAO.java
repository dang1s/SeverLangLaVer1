package com.sg188.clan;

import SqlConnection.Connect;
import com.sg188.api.Dao;
import com.sg188.data.SkillClan;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Item;
import org.apache.commons.lang3.time.DateUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ClanDAO implements Dao<Clan> {
    private List<Clan> clans = new ArrayList<>();

    public boolean checkExist(String name) {
        synchronized (clans) {
            for (Clan clan : clans) {
                if (clan.name.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public Optional<Clan> get(long id) {
        return clans.stream().filter(clan -> clan.id == id).findFirst();
    }
    public Optional<Clan> get(String name) {
        return clans.stream().filter(clan -> clan.name.contains(name)).findFirst();
    }

    @Override
    public List<Clan> getAll() {
        return this.clans;
    }

    @Override
    public void save(Clan clan) {
        try {
            Connection conn = Connect.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO `clan` (`name`, `main_name`, `log`, `box`, `alert`, `skill`) VALUES (?, ?, ?, ?, ?, '[]')", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = null;
            try {
                ps.setString(1, clan.name);
                ps.setString(2, clan.main_name);
                ps.setString(3, clan.getLog());
                ps.setString(4, "[]");
                ps.setString(5, "");
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    clan.id = rs.getInt(1);
                }
            } finally {
                ps.close();
                if (rs != null) {
                    rs.close();
                }
            }
            clans.add(clan);
        } catch (SQLException ex) {
            Log.error("save err: " + ex.getMessage(), ex);
        }
    }

    public void load() {
        try {
            Log.info("Loading clan data");
            Connection conn = Connect.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `clan`");
            java.util.Date now = new java.util.Date();
            try {
                ResultSet res = stmt.executeQuery();
                while (res.next()) {
                    Clan clan = new Clan();
                    clan.id = res.getInt("id");
                    clan.name = res.getString("name");
                    clan.main_name = res.getString("main_name");
                    clan.alert = res.getString("alert");
                    clan.level = res.getByte("level");
                    clan.coin = res.getInt("coin");
                    clan.exp = res.getInt("exp");
                    clan.countInvite=res.getByte("countinvite");
                    clan.countKick=res.getByte("countkick");
                    clan.openDun = res.getByte("open_dun");
                    clan.reg_date = res.getDate("reg_date");
                    clan.log = res.getString("log");
                    Date updated_at = res.getDate("updated_at");
                    clan.loadItem((JSONArray) JSONValue.parse(res.getString("box")));
                    JSONArray jArr = (JSONArray) JSONValue.parse(res.getString("skill"));
                    int len = jArr.size();
                    if(jArr !=null) {
                        for (int i = 0; i < len; i++) {
                            JSONObject obj = (JSONObject) jArr.get(i);
                            SkillClan skillClan = new SkillClan();
                            skillClan.id = Integer.parseInt(obj.get("id").toString());
                            skillClan.name = obj.get("name").toString();
                            skillClan.levelNeed = Integer.parseInt(obj.get("levelNeed").toString());
                            skillClan.strOptions = obj.get("strOptions").toString();
                            clan.skillClans.add(skillClan);
                        }
                    }
                    jArr.clear();

                    if (!DateUtils.isSameDay(now, updated_at)) {
                        clan.openDun = 1;
                        PreparedStatement stmt3 = conn.prepareStatement(
                                "UPDATE `clan` SET `open_dun` = 1,`countinvite` = 20,`countkick` = 5, `updated_at` = ? WHERE `id` = ? LIMIT 1;");
                        stmt3.setString(1, Utlis.dateToString(now, "yyyy-MM-dd"));
                        stmt3.setInt(2, clan.id);
                        stmt3.executeUpdate();
                        stmt3.close();
                    }
                    clan.memberDAO.load();
                    clans.add(clan);
                }
                res.close();
            } finally {
                stmt.close();
            }
            Log.info("Load clan data successfully");
        } catch (SQLException ex) {
            Log.error("load fail", ex);
        }
    }

    @Override
    public void update(Clan clan) {
        if (!clan.isSaving()) {
            clan.setSaving(true);
            try {
                JSONArray skill = new JSONArray();
                for (SkillClan skillClan: clan.skillClans){
                    skill.add(skillClan.toJSONObject());
                }
                Connection conn = Connect.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE `clan` SET `coin` = ?, `level` = ?, `exp` = ?, `open_dun` = ?, `box` = ?, `log` = ?, `skill` = ?,`countinvite` = ?,`countkick` = ? WHERE `id` = ? LIMIT 1;");
                try {
                    stmt.setInt(1, clan.coin);
                    stmt.setInt(2, clan.level);
                    stmt.setInt(3, clan.exp);
                    stmt.setInt(4, clan.openDun);
                    JSONArray boxs = new JSONArray();
                    for (Item itm : clan.items) {
                        if (itm == null) {
                            continue;
                        }
                        boxs.add(itm.toJSONObject());
                    }
                    stmt.setString(5, boxs.toJSONString());
                    stmt.setString(6, clan.log);
                    stmt.setString(7, skill.toJSONString());
                    stmt.setInt(8, clan.countInvite);
                    stmt.setInt(9, clan.countKick);
                    stmt.setInt(10, clan.id);
                    stmt.executeUpdate();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    stmt.close();
                }
                try {
                    List<Member> members = clan.memberDAO.getAll();
                    synchronized (members) {
                        for (Member member : members) {
                            clan.memberDAO.update(member);
                        }
                    }
                }catch (Exception e){

                }
            } catch (SQLException ex) {
                Log.error("update clan fail", ex);
            } finally {
                clan.setSaving(false);
            }
        }
    }

    @Override
    public void delete(Clan clan) {
        try {
            Connection conn = Connect.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM `clan` WHERE `id` = ?;");
            try {
                ps.setInt(1, clan.id);
                ps.executeUpdate();
            } finally {
                ps.close();
            }
            get(clan.id).ifPresent(exist -> clans.remove(exist));
        } catch (SQLException ex) {
            Log.error("delete clan err", ex);
        }
    }
}

