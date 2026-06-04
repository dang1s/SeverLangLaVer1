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
        try (Connection conn = Connect.getConnection()) {
            conn.setAutoCommit(false); // Bắt đầu transaction
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO `clan` (`name`, `main_name`, `log`, `box`, `alert`, `skill`) VALUES (?, ?, ?, ?, ?, '[]')", 
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, clan.name);
                ps.setString(2, clan.main_name);
                ps.setString(3, clan.getLog());
                ps.setString(4, "[]");
                ps.setString(5, "");
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        clan.id = rs.getInt(1);
                    }
                }
                clans.add(clan);
                conn.commit(); // Commit transaction
            } catch (SQLException e) {
                Log.error("save clan err: " + e.getMessage(), e);
                // Không rollback
                throw e;
            }
        } catch (SQLException ex) {
            Log.error("save err: " + ex.getMessage(), ex);
        }
    }

    public void load() {
        try (Connection conn = Connect.getConnection()) {
            Log.info("Loading clan data");
            conn.setAutoCommit(false); // Transaction cho load
            try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM `clan`")) {
                Date now = new Date();
                try (ResultSet res = stmt.executeQuery()) {
                    while (res.next()) {
                        Clan clan = new Clan();
                        clan.id = res.getInt("id");
                        clan.name = res.getString("name");
                        clan.main_name = res.getString("main_name");
                        clan.alert = res.getString("alert");
                        clan.level = res.getByte("level");
                        clan.coin = res.getInt("coin");
                        clan.exp = res.getInt("exp");
                        clan.countInvite = res.getByte("countinvite");
                        clan.countKick = res.getByte("countkick");
                        clan.openDun = res.getByte("open_dun");
                        clan.reg_date = res.getDate("reg_date");
                        clan.log = res.getString("log");
                        Date updated_at = res.getDate("updated_at");
                        clan.loadItem((JSONArray) JSONValue.parse(res.getString("box")));
                        JSONArray jArr = (JSONArray) JSONValue.parse(res.getString("skill"));
                        if (jArr != null) {
                            for (int i = 0; i < jArr.size(); i++) {
                                JSONObject obj = (JSONObject) jArr.get(i);
                                SkillClan skillClan = new SkillClan();
                                if (obj.containsKey("id")) {
                                    skillClan.id = Integer.parseInt(obj.get("id").toString());
                                }
                                if (obj.containsKey("name")) {
                                    skillClan.name = obj.get("name").toString();
                                }
                                if (obj.containsKey("levelNeed")) {
                                    skillClan.levelNeed = Integer.parseInt(obj.get("levelNeed").toString());
                                }
                                if (obj.containsKey("strOptions")) {
                                    skillClan.strOptions = obj.get("strOptions").toString();
                                }
                                clan.skillClans.add(skillClan);
                            }
                        }
                        if (!DateUtils.isSameDay(now, updated_at)) {
                            clan.openDun = 1;
                            try (PreparedStatement stmt3 = conn.prepareStatement(
                                    "UPDATE `clan` SET `open_dun` = 1, `countinvite` = 20, `countkick` = 5, `updated_at` = ? WHERE `id` = ? LIMIT 1")) {
                                stmt3.setString(1, Utlis.dateToString(now, "yyyy-MM-dd"));
                                stmt3.setInt(2, clan.id);
                                stmt3.executeUpdate();
                            }
                        }
                        clan.memberDAO.load();
                        clans.add(clan);
                    }
                    conn.commit();
                }
            } catch (SQLException e) {
                Log.error("load clan fail", e);
                // Không rollback
                throw e;
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
            try (Connection conn = Connect.getConnection()) {
                conn.setAutoCommit(false); // Transaction cho update
                try (PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE `clan` SET `coin` = ?, `level` = ?, `exp` = ?, `open_dun` = ?, `box` = ?, `log` = ?, `skill` = ?, `countinvite` = ?, `countkick` = ?, `main_name` = ? WHERE `id` = ? LIMIT 1")) {
                    JSONArray skill = new JSONArray();
                    for (SkillClan skillClan : clan.skillClans) {
                        skill.add(skillClan.toJSONObject());
                    }
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
                    stmt.setString(10, clan.main_name);
                    stmt.setInt(11, clan.id);
                    stmt.executeUpdate();

                    // Batch update members
                    List<Member> members = clan.memberDAO.getAll();
                    synchronized (members) {
                        if (!members.isEmpty()) {
                            try (PreparedStatement batchStmt = conn.prepareStatement(
                                    "UPDATE `clan_member` SET `level` = ?, `point_clan` = ?, `point_clan_week` = ? WHERE `id` = ? LIMIT 1")) {
                                for (Member member : members) {
                                    if (!member.isSaving()) {
                                        member.setSaving(true);
                                        batchStmt.setInt(1, member.getLevel());
                                        batchStmt.setInt(2, member.getPointClan());
                                        batchStmt.setInt(3, member.getPointClanWeek());
                                        batchStmt.setInt(4, member.getId());
                                        batchStmt.addBatch();
                                        member.setSaving(false);
                                    }
                                }
                                batchStmt.executeBatch();
                            }
                        }
                    }
                    conn.commit();
                } catch (SQLException e) {
                    Log.error("update clan fail", e);
                    // Không rollback
                    throw e;
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
        try (Connection conn = Connect.getConnection()) {
            conn.setAutoCommit(false); // Transaction cho delete
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM `clan` WHERE `id` = ?")) {
                ps.setInt(1, clan.id);
                ps.executeUpdate();
                get(clan.id).ifPresent(exist -> clans.remove(exist));
                conn.commit();
            } catch (SQLException e) {
                Log.error("delete clan err", e);
                // Không rollback
                throw e;
            }
        } catch (SQLException ex) {
            Log.error("delete clan err", ex);
        }
    }
}