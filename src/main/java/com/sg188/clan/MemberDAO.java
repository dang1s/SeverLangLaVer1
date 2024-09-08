package com.sg188.clan;

import SqlConnection.Connect;
import com.sg188.api.Dao;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MemberDAO implements Dao<Member> {
    private Clan clan;
    private List<Member> members = new ArrayList<>();

    public MemberDAO(Clan clan) {
        this.clan = clan;
    }
    public void load() {
        try {
            Connection conn = Connect.getConnection();
            Date now = new Date();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `clan_member` WHERE `clan` = ?");
            try {
                st.setInt(1, clan.id);
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    byte classId = rs.getByte("class_id");
                    int level = rs.getInt("level");
                    String name = rs.getString("name");
                    byte type = rs.getByte("type");
                    int point_clan = rs.getInt("point_clan");
                    int point_clan_week = rs.getInt("point_clan_week");
                    Date updated_at = rs.getDate("updated_at");
                    if (!Utlis.isSameWeek(now, updated_at)) {
                        point_clan_week = 0;
                        PreparedStatement stmt3 = Connect.getConnection().prepareStatement(
                                "UPDATE `clan_member` SET `point_clan_week` = 0, `updated_at` = ? WHERE `id` = ? LIMIT 1;");
                        stmt3.setString(1, Utlis.dateToString(new Date(), "yyyy-MM-dd"));
                        stmt3.setInt(2, id);
                        stmt3.executeUpdate();
                        stmt3.close();
                    }
                    Member member = Member.builder()
                            .id(id)
                            .classId(classId)
                            .level(level)
                            .type(type)
                            .name(name)
                            .pointClan(point_clan)
                            .pointClanWeek(point_clan_week)
                            .build();
                    members.add(member);
                }
                rs.close();
            } finally {
                st.close();
            }
        } catch (SQLException ex) {
            Log.error("load member fail");
        }
    }
    @Override
    public Optional<Member> get(long id) {
        return members.stream().filter(mem -> mem.getId() == id).findFirst();
    }

    @Override
    public List<Member> getAll() {
        return members;
    }

    @Override
    public void save(Member member) {
        try {
            Connection conn = Connect.getConnection();
            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO `clan_member` (`name`, `class_id`, `level`, `clan`, `type`,`idchar`) VALUES (?, ?, ?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = null;
            try {
                st.setString(1, member.getName());
                st.setInt(2, member.getClassId());
                st.setInt(3, member.getLevel());
                st.setInt(4, clan.id);
                st.setInt(5, member.getType());
                st.setInt(6, member.getIdChar());
                st.executeUpdate();
                rs = st.getGeneratedKeys();
                if (rs.next()) {
                    member.setId(rs.getInt(1));
                }
            }catch (Exception e) {
                e.printStackTrace();
            }finally
             {
                st.close();
                if (rs != null) {
                    rs.close();
                }
            }
            PreparedStatement stmt2 = conn.prepareStatement("UPDATE `player` SET `clan` = ? WHERE `IdChar` = ? LIMIT 1;");
            try {
                stmt2.setInt(1, clan.getId());
                stmt2.setInt(2, member.getChar().id);
                stmt2.executeUpdate();
            } finally {
                stmt2.close();
            }
            members.add(member);
        } catch (SQLException ex) {
            Log.error("save err");
        }
    }

    @Override
    public void update(Member member) {
        if (!member.isSaving()) {
            member.setSaving(true);
            try {
                Connection conn = Connect.getConnection();
                PreparedStatement stmt2 = conn.prepareStatement(
                        "UPDATE `clan_member` SET `level` = ?, `point_clan` = ?, `point_clan_week` = ? WHERE `id` = ? LIMIT 1");
                stmt2.setInt(1, member.getLevel());
                stmt2.setInt(2, member.getPointClan());
                stmt2.setInt(3, member.getPointClanWeek());
                stmt2.setInt(4, member.getId());
                stmt2.executeUpdate();
                stmt2.close();
            } catch (SQLException ex) {
                Log.error("update member clan err", ex);
            } finally {
                member.setSaving(false);
            }
        }
    }

    @Override
    public void delete(Member member) {
        try {
            Connection conn = Connect.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM `clan_member` WHERE `id` = ?;");
            try {
                stmt.setInt(1, member.getId());
                stmt.executeUpdate();
            } finally {
                stmt.close();
            }
            PreparedStatement st = conn
                    .prepareStatement("UPDATE `player` SET `clan` = -1 WHERE `name` = ? LIMIT 1;");
            try {
                st.setString(1, member.getName());
                st.executeUpdate();
            } finally {
                st.close();
            }
            get(member.getId()).ifPresent(mem -> members.remove(mem));
        } catch (SQLException ex) {
            ex.printStackTrace();
            Log.error("delete err");
        }
    }
}
