/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventClick;

import Service.HanderMessage;
import SqlConnection.CharDB;
import com.sg188.clan.Clan;
import com.sg188.data.DataCenter;
import com.sg188.data.Skill;
import com.sg188.data.SkillClan;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.server.ServerManager;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Writer;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author ADMIN
 */
public class ClickTop {
    
    public static List<InfoTop> cTop;
    public static List<InfoTop> cCuaCai;
    public static List<InfoTop> cTaiPhu;

    public static List<InfoTop> cNhiDong;

    public static List<InfoTop> cChuyenCan;
    public static final Vector[] RANKED= new Vector[14];
    
    public static void ShowTop(Char _myChar, byte typeTop, byte indexClass) {
        switch (typeTop) {
            case 0:
                showTopLevel(_myChar, indexClass);
                break;
            case 1:
                showTopNap(_myChar,indexClass);
                break;
            case 2:
                showTopTaiPhu(_myChar,indexClass);
                break;
            case 3:
                showTopChuyenCan(_myChar,indexClass);
                break;
            case 4:
                showTopGiaToc(_myChar);
                break;
            case 6:
                //showTopNhiDong(_myChar,indexClass);
                break;
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

                writer.writeShort(cS.critical>= 3000?3000:cS.critical);/*
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
                for(Skill sk : cS.Skill.arraySkill)
                {
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
                m.writeByte(cS.Point.expsach);
                _myChar.user.session.sendMessage(m);
            } catch (Exception e) {
                e.printStackTrace();
            }

            cS.user.session.sendMessage(HanderMessage.SendThongBao(_myChar.Info.name + " đang xem thông tin về bạn", HanderMessage.YELLOW_MID));
            
        }else {
            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Đối phương đã offline",HanderMessage.WHITE));
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
    private static void showTopNhiDong(Char _myChar, byte index) {
        if (cNhiDong == null) {
            cNhiDong = CharDB.getTopNhiDong((byte) 0);
        }

        ClickTop.order(cNhiDong);
        Message m = new Message((byte) -22);
        try {
            byte i = 0;
            m.writeBoolean(true); // show top ???
            m.writeByte(cNhiDong.size());
            for (InfoTop c : cNhiDong) {
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
            if(RANKED[2]==null){
                CharDB.initTopGiaToc();
            }
            Message m = new Message((byte) -30);
            m.writeBoolean(true); // show top ???
            m.writeByte(RANKED[2].size());

            for (Object objects:RANKED[2]) {
                Clan c = (Clan)objects;
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
