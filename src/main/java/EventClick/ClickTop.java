/*

 * To change this license header, choose License Headers in Project Properties.

 * To change this template file, choose Tools | Templates

 * and open the template in the editor.

 */

package EventClick;

import Manager.RankingRewardManager;
import MapService.world.DaiHoiNhanGia;
import Service.HanderMessage;
import SqlConnection.CharDB;
import com.sg188.clan.Clan;
import com.sg188.clan.Member;
import com.sg188.data.*;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.server.ServerManager;

import com.sg188.server.lib.Message;

import com.sg188.server.lib.Writer;



import java.util.*;

import java.util.stream.Collectors;



/**

 * @author ADMIN

 */

public class ClickTop {

    public static List<InfoTop> cLuyenTap;

    public static List<InfoTop> cCuongHoa;

    public static List<InfoTop> cTop;

    public static List<InfoTop> cCuaCai;

    public static List<InfoTop> cTaiPhu;

    public static List<InfoTop> cNhiDong;

    public static List<InfoTop> cNhiDongTaiPhu;

    public static List<InfoTop> cCuaCaiTuan;

    public static List<InfoTop> cChuyenCan;

    public static List<InfoTop> cNapTuan;

    public static List<InfoTop> cDaiHoi;

    public static List<InfoTop> cDaiHoiVoThuat;

    public static final Vector[] RANKED = new Vector[14];



    public static void ShowTop(Char _myChar, byte typeTop, byte indexClass) {
        if (!_myChar.zone.isDaiHoiVoThuat()) {
            switch (typeTop) {
                case 20:
                    showTopCuaCaiTuan(_myChar, indexClass);
                    return;
                case 21:
                    showTopChuyenCanTuan(_myChar, indexClass);
                    return;
                case 22:
                    showTopCuongHoaTuan(_myChar, indexClass);
                    return;
                case 23:
                    showTopCongHienTuan(_myChar, indexClass);
                    return;
                case 24:
                    showTopLoiDaiThang(_myChar, indexClass);
                    return;
                default:
                    break;
            }
        }
        if (_myChar.zone.isDaiHoiVoThuat()) {
            switch (typeTop) {

                case 0: // top đại hội nhẫn giả

                    if (DaiHoiNhanGia.DAI_HOI_NHAN_GIA.numRound != 0) {

                        DaiHoiNhanGia.DAI_HOI_NHAN_GIA.showTopDaiHoi(_myChar);

                    } else {

                        showDaiHoiVoThuat(_myChar, indexClass);

                    }

                    break;

            }

        } else {

            switch (typeTop) {

                case 0:

                    showTopLevel(_myChar, indexClass);

                    break;

                case 1:

                    showTopCuaCai(_myChar, indexClass);

                    break;

                case 2:

                    showTopTaiPhu(_myChar, indexClass);

                    break;

                case 3:

                    showTopChuyenCan(_myChar, indexClass);

                    break;

                case 4:

                    showTopGiaToc(_myChar);

                    break;

                case 6:
                    showTopNhiDong(_myChar, indexClass);
                    break;
                case 7:
                    showTopLuyenTap(_myChar, indexClass);
                    break;
                case 8:
                    showTopCuongHoa(_myChar, indexClass);
                    break;
                case 9:
                case 20: // giữ luôn case cũ để không lệch client custom
                    showTopNapTuan(_myChar, indexClass);
                    break;
                case 21: //cc tuần
                    break;
            }
        }

    }



    private static void showTopCuaCai(Char me, byte indexClass) {



        // Lấy danh sách từ DB trực tiếp mỗi lần mở

        List<InfoTop> list = CharDB.getTopCuaCai();



        // Sort theo điểm của cải → tên

        Comparator<InfoTop> cmp = Comparator

                .comparingLong((InfoTop t) -> t.cuaCai).reversed()

                .thenComparing(t -> t.name);



        list = list.stream().sorted(cmp).collect(Collectors.toList());



        try {

            Message m = new Message((byte) -22);

            m.writeBoolean(true);

            m.writeByte(list.size());



            byte rank = 1;



            for (InfoTop t : list) {

                m.writeByte(rank++);      // Rank

                m.writeUTF(t.name);       // Tên

                m.writeShort(t.cuaCai);   // hiển thị kiểu level

                m.writeLong(t.cuaCai);    // điểm của cải

                m.writeByte(t.idHe);      // hệ

                m.writeUTF(t.clanName);   // gia tộc

            }



            me.user.session.sendMessage(m);



        } catch (Exception e) {

            e.printStackTrace();

        }

    } 

    private static void showDaiHoiVoThuat(Char _myChar, byte indexClass) {

        cDaiHoiVoThuat = CharDB.getTopDaiHoiVoThuat(_myChar);

        Collections.sort(cDaiHoiVoThuat, Comparator.comparing(InfoTop::getChienTich).reversed());

        try {

            int i = 0;

            Message m = new Message((byte) -22);

            m.writeBoolean(true);

            m.writeByte(cDaiHoiVoThuat.size());



            for (InfoTop c : cDaiHoiVoThuat) {

                m.writeByte(i);

                m.writeUTF(c.name);

                m.writeShort((int) c.getChienTich());

                m.writeLong(c.getChienTich());

                m.writeByte(c.idHe);

                m.writeUTF(c.clanName);

                i++;

            }



            _myChar.user.session.sendMessage(m);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }



    private static void showTopNap(Char _myChar, byte indexClass) {

        if (cCuaCai == null) {

            cCuaCai = CharDB.getTopCuaCai();

        }

        Collections.sort(cCuaCai, Comparator.comparing(InfoTop::getPointNap).reversed());

        try {

            byte i = 0;

            Message m = new Message((byte) -22);

            m.writeBoolean(true); // show top ???

            m.writeByte(cCuaCai.size());



            for (InfoTop c : cCuaCai) {

                m.writeByte(i);

                m.writeUTF(c.name);

                m.writeShort((int) c.getPointNap());

                m.writeLong(c.getPointNap());

                m.writeByte(c.idHe);

                m.writeUTF(c.clanName);

                i++;

            }

            _myChar.user.session.sendMessage(m);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }



    private static void showTopNapTuan(Char _myChar, byte indexClass) {
        List<InfoTop> list = CharDB.getTopNapTuanCurrentAll();
        RankingRewardManager.applyManagedEventBaseline(RankingRewardManager.ManagedEventTop.NAP_NHIEU, list);
        list = list.stream()
                .sorted(Comparator.comparing(InfoTop::getPointNapTuan).reversed().thenComparing(t -> t.name))
                .collect(Collectors.toList());
        try {
            byte i = 0;
            Message m = new Message((byte) -22);
            m.writeBoolean(true); // show top ???
            m.writeByte(list.size());

            for (InfoTop c : list) {
                m.writeByte(i);
                m.writeUTF(c.name);
                m.writeShort((int) c.getPointNapTuan());
                m.writeLong(c.getPointNapTuan());

                m.writeByte(c.idHe);

                m.writeUTF(c.clanName);

                i++;

            }

            _myChar.user.session.sendMessage(m);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }



    public static void msgGetInfo(Char _myChar, String name) {

        Char cS = ServerManager.findCharByName(name);

        if (cS != null) {

            try {

                Writer writer = new Writer();

                writer.writeInt(cS.Info.levelCheTao);

                writer.writeInt(cS.Point.hoatLuc);

                writer.writeByte(cS.Info.sachChienDau);

                writer.writeShort(cS.Point.diemKyNang);

                writer.writeShort(cS.Point.diemTiemNang);

                writer.writeBoolean(false);

                writer.writeByte(0);

                writer.writeShort(cS.Point.arrayTiemNang[0]);

                writer.writeShort(cS.chakra);

                writer.writeShort(cS.Point.arrayTiemNang[2]);

                writer.writeShort(cS.Point.arrayTiemNang[3]);

                writer.writeInt(cS.damage);

                writer.writeInt(cS.attackMonsters);



            /*

             0. Chính xác*/

                writer.writeShort(cS.exactly);/*

             1. Bỏ qua né tránh*/



                writer.writeShort(cS.ignoreMiss);/*

             2. Chí mạng*/



                writer.writeShort(cS.critical >= 3000 ? 3000 : cS.critical);/*

             3. Tấn công khi đánh chí mạng*/



                writer.writeShort(cS.criticalAttack);/*

             4. Tăng tấn công lên hệ Lôi*/



                writer.writeShort(cS.lightningAttackBoost);/*

             5. Tăng tấn công lên hệ Thổ*/



                writer.writeShort(cS.earthAttackBoost);/*

             6. Tăng tấn công lên hệ Thủy*/



                writer.writeShort(cS.waterAttackBoost);/*



             7. Tăng tấn công lên hệ Hỏa*/



                writer.writeShort(cS.fireAttackBoost);/*

             8. Tăng tấn công lên hệ Phong*/



                writer.writeShort(cS.windAttackBoost);/*

             9. Gây suy yếu*/



                writer.writeShort(cS.weaken);/*

             10. Gây trúng độc*/



                writer.writeShort(cS.poison);/*

             11. Gây làm chậm*/



                writer.writeShort(cS.slow);/*

             12. Gây bỏng*/



                writer.writeShort(cS.burn);/*

             13. Gây choáng*/



                writer.writeShort(cS.stun);/*

             14. Bỏ qua kháng tính*/



                writer.writeShort(cS.ignoreResistance);/*

             15. Kháng Lôi*/



                writer.writeShort(cS.lightningResistance);/*

             16. Kháng Thổ*/



                writer.writeShort(cS.earthResistance);/*

             17. Kháng Thủy*/



                writer.writeShort(cS.waterResistance);/*

             18. Kháng Hỏa*/



                writer.writeShort(cS.fireResistance);/*

             19. Kháng Phong*/



                writer.writeShort(cS.windResistance);/*

             20. Giảm sát thương*/



                writer.writeShort(cS.damageReduction);/*

             21. Tốc độ di chuyển*/



                writer.writeShort(cS.movementSpeed);/*

             22. Né tránh*/



                writer.writeShort(cS.miss);/*

             23. Phản đòn*/



                writer.writeShort(cS.counterAttack);/*

             24. Phòng chí mạng*/



                writer.writeShort(cS.criticalDefense);/*

             25. Tương khắc lên hệ */



                writer.writeShort(cS.elementalCounter);/*

             26. Giảm tương khắc của hệ */



                writer.writeShort(cS.elementalCounterReduce);/*

             27. Giảm gây suy yếu*/



                writer.writeShort(cS.reduceWeaken);/*

             28. Giảm gây trúng độc*/



                writer.writeShort(cS.reducePoison);/*

             29. Giảm gây làm chậm*/



                writer.writeShort(cS.reduceSlow);/*

             30. Giảm gây bỏng*/



                writer.writeShort(cS.reduceBurn);/*

             31. Giảm gây choáng*/



                writer.writeShort(cS.reduceStun);/*

             32. Giảm trừ chí mạng*/



                writer.writeShort(cS.criticalDefense);/*

                 */



                _myChar.service.getInfo(writer);

            } catch (Exception ex) {

                ex.printStackTrace();



            }

        }

    }



    public static void ShowInfo(Char _myChar, String name) {

        Char cS = ServerManager.findCharByName(name);

        if (cS != null) {

            Message m = new Message((byte) 34);

            try {

                m.writeUTF(cS.Info.name);

                m.writeLong(cS.Point.exp);

                m.writeByte(cS.Info.idChar);

                m.writeByte(cS.Info.idhe);

                m.writeByte(cS.Info.idClass);

                m.writeByte(cS.Info.gioiTinh);

                m.writeByte(cS.Info.sachChienDau);

                cS.writeItemBody(m.writer, cS.Bag.arrItemBody);

                cS.writeItemBody(m.writer, cS.Bag.arrItemBody2);



                // cS.writeSkill(m.writer);

                m.writeShort(cS.Skill.arraySkill.length);

                for (Skill sk : cS.Skill.arraySkill) {

                    m.writeShort(sk.index);

                }





                m.writeUTF("");



                cS.writeDanhHieu(m.writer);

                m.writeByte(cS.Info.selectCaiTrang);



                m.writeByte(cS.listSkill.size());

                for (int i = 0; i < cS.listSkill.size(); i++) {

                    SkillClan skill = cS.listSkill.get(i);

                    m.writeByte(skill.id);

                    m.writeByte(skill.levelNeed);

                }



                m.writeByte(cS.Point.diempt);

                m.writeByte(cS.Point.maxpt);

                m.writeInt(cS.Point.expsach);

                m.writeByte(0);

                m.writeByte(0);

                cS.writeItemPet(m.writer, cS.Bag.arrItemPet);

//                WingTemplate wing = null;

//                if (cS.Bag.arrItemBody[18] != null) {

//                    wing = WingTemplate.wins.stream().filter(w -> w.itemId == cS.Bag.arrItemBody[18].id).findFirst().orElse(null);

//                }

//                if (wing == null) {

//                    m.writeInt(-1);

//                    m.writeInt(-1);

//                    m.writeInt(1);

//                    m.writeInt(0);

//                    m.writeInt(0);

//                } else {

//                    m.writeInt(wing.frameStart);

//                    m.writeInt(wing.frameEnd);

//                    m.writeInt(wing.tick);

//                    m.writeInt(wing.dx);

//                    m.writeInt(wing.dy);

//                }

//                MatTemplate mat = null;

//                if (cS.Bag.arrItemBody[17] != null) {

//                    mat = MatTemplate.mats.stream().filter(w -> w.itemId == cS.Bag.arrItemBody[17].id).findFirst().orElse(null);

//                }

//                if (mat == null) {

//                    m.writeInt(-1);

//                    m.writeInt(-1);

//                    m.writeInt(1);

//                    m.writeInt(0);

//                    m.writeInt(0);

//                } else {

//                    m.writeInt(mat.frameStart);

//                    m.writeInt(mat.frameEnd);

//                    m.writeInt(mat.tick);

//                    m.writeInt(mat.dx);

//                    m.writeInt(mat.dy);

//                }

                _myChar.user.session.sendMessage(m);

            } catch (Exception e) {

                e.printStackTrace();

            }



            cS.user.session.sendMessage(HanderMessage.SendThongBao(_myChar.Info.name + " đang xem thông tin về bạn", HanderMessage.YELLOW_MID));



        } else {

            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Đối phương đã offline", HanderMessage.WHITE));

        }





    }



    private static void showTopLevel(Char _myChar, byte index) {

        if (cTop == null) {

            cTop = CharDB.getTop((byte) 0);

        }



        ClickTop.order(cTop);

        Message m = new Message((byte) -22);

        try {

            byte i = 0;

            m.writeBoolean(true); // show top ???

            m.writeByte(cTop.size());

            for (InfoTop c : cTop) {

                m.writeByte(i);

                m.writeUTF(c.name);

                m.writeShort(c.level);

                m.writeLong(0);

                m.writeByte(c.idHe);

                m.writeUTF(c.clanName);

                i++;

            }

            _myChar.user.session.sendMessage(m);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }



    private static void showTopNhiDongTP(Char _myChar, byte index) {

        if (cNhiDongTaiPhu == null) {

            cNhiDongTaiPhu = CharDB.getTopNhiDongTaiPhu();

        }

        Collections.sort(cNhiDongTaiPhu, Comparator.comparing(InfoTop::getTaiPhu).reversed());

        try {

            byte i = 0;

            Message m = new Message((byte) -22);

            m.writeBoolean(true); // show top ???

            m.writeByte(cNhiDongTaiPhu.size());



            for (InfoTop c : cNhiDongTaiPhu) {

                m.writeByte(i);

                m.writeUTF(c.name);

                m.writeShort((int) c.getTaiPhu());

                m.writeLong(c.getTaiPhu());

                m.writeByte(c.idHe);

                m.writeUTF(c.clanName);

                i++;

            }

            _myChar.user.session.sendMessage(m);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }



    private static void showTopNhiDong(Char me, byte indexClass) {

        long now = System.currentTimeMillis();



        if (now < ConfigNhiDong.START) {

            me.user.session.sendMessage(

                    HanderMessage.SendThongBao("Sự kiện Nhi Đồng chưa bắt đầu!", HanderMessage.RED_MID));

            return;

        }



        if (now > ConfigNhiDong.END) {

            me.user.session.sendMessage(

                    HanderMessage.SendThongBao("Sự kiện Nhi Đồng đã kết thúc!", HanderMessage.RED_MID));

            return;

        }



        // Dùng cache thay vì query liên tục

        if (cNhiDong == null) {

            cNhiDong = CharDB.getTopNhiDong(ConfigNhiDong.START, ConfigNhiDong.END);

        }



        List<InfoTop> list = cNhiDong;



        // Sắp xếp theo kiểu TopLevel

        Comparator<InfoTop> cmp = Comparator

                .comparingInt((InfoTop t) -> t.level).reversed()

                .thenComparing(Comparator.comparingLong((InfoTop t) -> t.exp).reversed())

                .thenComparing(t -> t.name);



        list = list.stream().sorted(cmp).collect(Collectors.toList());



        try {

            Message m = new Message((byte) -22);

            m.writeBoolean(true);

            m.writeByte(list.size());



            byte rank = 1;

            for (InfoTop t : list) {

                m.writeByte(rank++);

                m.writeUTF(t.name);

                m.writeShort(t.level);

                m.writeLong(t.exp);

                m.writeByte(t.idHe);

                m.writeUTF(t.clanName);

            }



            me.user.session.sendMessage(m);



        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    // nhảy điểm liên tục



    private static void showTopLuyenTap(Char me, byte indexClass) {
        long now = System.currentTimeMillis();


        if (now < ConfigLuyenTap.START) {

            me.user.session.sendMessage(

                    HanderMessage.SendThongBao("Sự kiện Luyện Tập chưa bắt đầu!", HanderMessage.RED_MID));

            return;

        }



        if (now > ConfigLuyenTap.END) {

            me.user.session.sendMessage(

                    HanderMessage.SendThongBao("Sự kiện Luyện Tập đã kết thúc!", HanderMessage.RED_MID));

            return;

        }



        // Lấy danh sách từ DB
        List<InfoTop> list = CharDB.getTopLuyenTap(ConfigLuyenTap.START, ConfigLuyenTap.END);
        RankingRewardManager.applyManagedEventBaseline(RankingRewardManager.ManagedEventTop.LUYEN_TAP, list);

        // -- Sort theo điểm luyện tập → tên
        Comparator<InfoTop> cmp = Comparator
                .comparingInt((InfoTop t) -> t.luyenTap).reversed()

                .thenComparing(t -> t.name);



        list = list.stream().sorted(cmp).collect(Collectors.toList());



        try {

            Message m = new Message((byte) -22);

            m.writeBoolean(true);

            m.writeByte(list.size());



            byte rank = 1;



            for (InfoTop t : list) {

                m.writeByte(rank++);          // 1. Gửi Hạng

                m.writeUTF(t.name);           // 2. Gửi Tên



                // XÓA dòng writeShort này đi vì nó làm lệch dữ liệu

                m.writeShort(t.luyenTap);

                m.writeLong(t.luyenTap);      // 3. Gửi Điểm (Dạng Long để khớp với Client)



                m.writeByte(t.idHe);          // 4. Gửi Hệ

                m.writeUTF(t.clanName);       // 5. Gửi Gia tộc

            }



            me.user.session.sendMessage(m);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showTopCuongHoa(Char me, byte indexClass) {
        long now = System.currentTimeMillis();

        if (now < ConfigCuongHoa.START) {
            me.user.session.sendMessage(
                    HanderMessage.SendThongBao("Sự kiện Cường Hóa chưa bắt đầu!", HanderMessage.RED_MID));
            return;
        }

        if (now > ConfigCuongHoa.END) {
            me.user.session.sendMessage(
                    HanderMessage.SendThongBao("Sự kiện Cường Hóa đã kết thúc!", HanderMessage.RED_MID));
            return;
        }

        List<InfoTop> list = CharDB.getTopCuongHoaCurrent();
        RankingRewardManager.applyManagedEventBaseline(RankingRewardManager.ManagedEventTop.CUONG_HOA, list);

        Comparator<InfoTop> cmp = Comparator
                .comparingInt((InfoTop t) -> t.cuongHoa).reversed()
                .thenComparing(t -> t.name);

        list = list.stream()
                .filter(t -> t.cuongHoa > 0)
                .sorted(cmp)
                .collect(Collectors.toList());

        try {
            Message m = new Message((byte) -22);
            m.writeBoolean(true);
            m.writeByte(list.size());

            byte rank = 1;

            for (InfoTop t : list) {
                m.writeByte(rank++);
                m.writeUTF(t.name);
                m.writeShort(t.cuongHoa);
                m.writeLong(t.cuongHoa);
                m.writeByte(t.idHe);
                m.writeUTF(t.clanName);
            }

            me.user.session.sendMessage(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showTopCongHienTuan(Char me, byte indexClass) {

        List<InfoTop> list = Clan.getClanDAO().getAll().stream()
                .map(clan -> {
                    if (clan == null || clan.getName() == null || clan.getName().isEmpty()) {
                        return null;
                    }

                    List<Member> members = clan.memberDAO.getAll();
                    int totalCongHienTuan = 0;
                    if (members != null) {
                        for (Member member : members) {
                            if (member != null) {
                                totalCongHienTuan += member.getPointClanWeek();
                            }
                        }
                    }

                    InfoTop top = new InfoTop();
                    top.name = clan.getName();
                    top.clanName = clan.getMainName() == null ? "" : clan.getMainName();
                    top.congHienTuan = totalCongHienTuan;
                    top.level = clan.getLevel();
                    int percent = clan.getExpNext() <= 0 ? 0 : (int) (clan.getExp() / (float) clan.getExpNext() * 100);
                    top.idHe = (byte) Math.max(0, Math.min(100, percent));
                    return top;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Comparator<InfoTop> cmp = Comparator
                .comparingInt((InfoTop t) -> t.congHienTuan).reversed()
                .thenComparing(t -> t.name);



        list = list.stream().sorted(cmp).collect(Collectors.toList());



        try {

            Message m = new Message((byte) -22);

            m.writeBoolean(true);

            m.writeByte(list.size());



            byte rank = 1;



            for (InfoTop t : list) {
                m.writeByte(rank++);
                m.writeUTF(t.name);
                m.writeShort(t.level);
                m.writeLong(t.congHienTuan);
                m.writeByte(t.idHe);
                m.writeUTF(t.clanName);
            }


            me.user.session.sendMessage(m);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showTopCuaCaiTuan(Char me, byte indexClass) {

        List<InfoTop> list = CharDB.getTopCuaCaiTuan().stream()
                .filter(t -> t.cuaCaiTuan > 0)
                .sorted(Comparator.comparingInt((InfoTop t) -> t.cuaCaiTuan).reversed().thenComparing(t -> t.name))
                .collect(Collectors.toList());

        try {
            Message m = new Message((byte) -22);
            m.writeBoolean(true);
            m.writeByte(list.size());

            byte rank = 1;

            for (InfoTop t : list) {
                m.writeByte(rank++);
                m.writeUTF(t.name);
                m.writeShort(t.cuaCaiTuan);
                m.writeLong(t.cuaCaiTuan);
                m.writeByte(t.idHe);
                m.writeUTF(t.clanName);
            }

            me.user.session.sendMessage(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showTopChuyenCanTuan(Char me, byte indexClass) {

        List<InfoTop> list = CharDB.getTopChuyenCanTuan().stream()
                .filter(t -> t.chuyenCanTuan > 0)
                .sorted(Comparator.comparingInt((InfoTop t) -> t.chuyenCanTuan).reversed().thenComparing(t -> t.name))
                .collect(Collectors.toList());

        try {
            Message m = new Message((byte) -22);
            m.writeBoolean(true);
            m.writeByte(list.size());

            byte rank = 1;

            for (InfoTop t : list) {
                m.writeByte(rank++);
                m.writeUTF(t.name);
                m.writeShort(t.chuyenCanTuan);
                m.writeLong(t.chuyenCanTuan);
                m.writeByte(t.idHe);
                m.writeUTF(t.clanName);
            }

            me.user.session.sendMessage(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showTopCuongHoaTuan(Char me, byte indexClass) {

        List<InfoTop> list = CharDB.getTopCuongHoaTuan().stream()
                .filter(t -> t.cuongHoaTuan > 0)
                .sorted(Comparator.comparingInt((InfoTop t) -> t.cuongHoaTuan).reversed().thenComparing(t -> t.name))
                .collect(Collectors.toList());

        try {
            Message m = new Message((byte) -22);
            m.writeBoolean(true);
            m.writeByte(list.size());

            byte rank = 1;

            for (InfoTop t : list) {
                m.writeByte(rank++);
                m.writeUTF(t.name);
                m.writeShort(t.cuongHoaTuan);
                m.writeLong(t.cuongHoaTuan);
                m.writeByte(t.idHe);
                m.writeUTF(t.clanName);
            }

            me.user.session.sendMessage(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showTopLoiDaiThang(Char me, byte indexClass) {

        List<InfoTop> list = CharDB.getTopLoiDaiThang().stream()
                .filter(t -> t.loiDai > 0)
                .sorted(Comparator.comparingInt(InfoTop::getLoiDai).reversed().thenComparing(t -> t.name))
                .collect(Collectors.toList());

        try {
            Message m = new Message((byte) -22);
            m.writeBoolean(true);
            m.writeByte(list.size());

            byte rank = 1;

            for (InfoTop t : list) {
                m.writeByte(rank++);
                m.writeUTF(t.name);
                m.writeShort(t.loiDai);
                m.writeLong(t.loiDai);
                m.writeByte(t.idHe);
                m.writeUTF(t.clanName);
            }

            me.user.session.sendMessage(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void order(List<InfoTop> ranks) {


        Collections.sort(ranks, new Comparator() {



            public int compare(Object o1, Object o2) {



                Integer level1 = (int) ((InfoTop) o1).level;

                Integer level2 = (int) ((InfoTop) o2).level;

                int sComp = level2.compareTo(level1);

                if (sComp != 0) {

                    return sComp;

                }

                Long x1 = ((InfoTop) o1).exp;

                Long x2 = ((InfoTop) o2).exp;

                return x2.compareTo(x1);

            }

        });

    }



    private static void showTopTaiPhu(Char _myChar, byte index) {

        if (cTaiPhu == null) {

            cTaiPhu = CharDB.getTopTaiPhu();

        }

        Collections.sort(cTaiPhu, Comparator.comparing(InfoTop::getTaiPhu).reversed());

        try {

            byte i = 0;

            Message m = new Message((byte) -22);

            m.writeBoolean(true); // show top ???

            m.writeByte(cTaiPhu.size());



            for (InfoTop c : cTaiPhu) {

                m.writeByte(i);

                m.writeUTF(c.name);

                m.writeShort((int) c.getTaiPhu());

                m.writeLong(c.getTaiPhu());

                m.writeByte(c.idHe);

                m.writeUTF(c.clanName);

                i++;

            }

            _myChar.user.session.sendMessage(m);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }



    private static void showTopChuyenCan(Char _myChar, byte index) {

        if (cChuyenCan == null) {

            cChuyenCan = CharDB.getTopChuyenCan();

        }

        Collections.sort(cChuyenCan, Comparator.comparing(InfoTop::getChuyenCan).reversed());

        try {

            byte i = 0;

            Message m = new Message((byte) -22);

            m.writeBoolean(true); // show top ???

            m.writeByte(cChuyenCan.size());



            for (InfoTop c : cChuyenCan) {

                m.writeByte(i);

                m.writeUTF(c.name);

                m.writeShort((int) c.getChuyenCan());

                m.writeLong(c.getChuyenCan());

                m.writeByte(c.idHe);

                m.writeUTF(c.clanName);

                i++;

            }

            _myChar.user.session.sendMessage(m);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }





    private static void showTopGiaToc(Char _myChar) {

        try {

            if (RANKED[2] == null) {

                CharDB.initTopGiaToc();

            }

            Message m = new Message((byte) -30);

            m.writeBoolean(true); // show top ???

            m.writeByte(RANKED[2].size());



            for (Object objects : RANKED[2]) {

                Clan c = (Clan) objects;

                int percent = (int) (c.getExp() / (float) c.getExpNext() * 100);

                m.writeUTF(c.getMainName());

                m.writeInt(c.getLevel());

                m.writeInt(percent);

                m.writeInt(c.getNumberMember());

                m.writeInt(c.getMemberMax());

                m.writeUTF(c.getName());

            }

            _myChar.user.session.sendMessage(m);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

}

