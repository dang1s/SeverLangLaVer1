package com.sg188.server;

import Manager.Manager;
import MapService.world.Arena;
import Service.HanderCharacter;
import Service.HanderMessage;
import Template.TemplateThu;
import com.sg188.clan.Clan;
import com.sg188.clan.Member;
import com.sg188.data.DataCenter;
import com.sg188.data.EffectTemplate;
import com.sg188.data.ItemOptionTemplate;
import com.sg188.data.SkillClan;
import com.sg188.lib.CaptchaUtil;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.party.Group;
import com.sg188.party.MemberGroup;
import com.sg188.real.*;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Writer;
import com.sg188.task.TaskOrder;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Service {
    private Session session;
    private Char player;

    public Service(Session session) {
        this.session = session;
    }

    public void setChar(Char pl) {
        this.player = pl;
    }

    public void alertMessage(String text) {
        try {
            Message msg = new Message((byte) -110);
            msg.writeUTF(text);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }

    }
    public void alertLogin(String text) {
        try {
            Message msg = new Message((byte) -109);
            msg.writeUTF(text);
            msg.writeByte(-99);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }

    }

    public void updateRank() {
        try {
            Message msg = new Message((byte) -5);
            msg.writeInt(player.Info.idEntity);
            msg.writeByte(player.Info.rank);
            session.sendMessage(msg);
        } catch (IOException e) {
        }
    }

    public void ChatGlobal(Message msg, Char _mChar) {
        try {
            if (_mChar.Info.banCTG) {
                _mChar.service.alertMessage("Mày đã bị khóa mõm");
                return;
            }
            long mili = System.currentTimeMillis() - _mChar.timeCTG;
            if (mili < HanderCharacter.delayChat) {
                _mChar.user.session.sendMessage(HanderMessage.SendThongBao(String.format("Chỉ có thể chat sau %s.",
                        Utlis.timeAgo((int) ((HanderCharacter.delayChat - mili) / 1000))), HanderMessage.RED_MID));
                return;
            }
            _mChar.timeCTG = System.currentTimeMillis();
            Boolean isLoa = msg.readBoolean();
            int type = 0;
            if (isLoa) {
                if (_mChar.Bag.vang > 2) {
                    _mChar.addVang(-2);
                    type = 1;
                } else {
                    _mChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không đủ vàng để chat", HanderMessage.RED_MID));
                    return;
                }
            }
            String str = msg.readUTF();
            if (HanderCharacter.profanityFilter.containsProfanity(str)) {
                str = HanderCharacter.profanityFilter.censorProfanity(str);
            }
            SendCTGToAllPlayer(_mChar, str, type);
        } catch (IOException ex) {
            Log.error("Loi ctg " + ex);
        }
    }

    public void SendCTGToAllPlayer(Char _mChar, String str, int type) {
        List<Char> charList = ServerManager.getChars();
        for (Char pl : charList) {
            if (pl != null && pl.user != null && !pl.isClean) {
                pl.user.session.sendMessage(HanderMessage.SendCTG(str, type, _mChar));
            }
        }
    }

    public void serverMessage(String text) {
        try {
            Message ms = new Message((byte) -107);
            ms.writeUTF(text);
            session.sendMessage(ms);
        } catch (Exception ex) {
        }
    }
    public void updatepointDungeon() {
        try {
            Message ms = new Message((byte) -31);
            ms.writeInt(player.pointDungeon);
            session.sendMessage(ms);
        } catch (Exception ex) {
        }
    }
    public void sendPointMap(int point){
        try {
            Message ms = new Message((byte) -31);
            ms.writeInt(point);
            session.sendMessage(ms);
        } catch (Exception ex) {
        }
    }


    public void warningMessage(String text) {
        try {
            Message ms = new Message((byte) -105);
            ms.writeUTF(text);
            session.sendMessage(ms);
        } catch (Exception ex) {
        }
    }

    public void showGiaToc() {
        try {
            Message m = new Message((byte) -88);
            if (player.clan != null) {
                m.writeInt(player.Info.idEntity);
                m.writeUTF(player.clan.getName());
                m.writeUTF(player.clan.getMainName());
                m.writeByte(player.clan.getMemberByName(player.Info.name).getType()); // 5 tộc trương // 4 tộc phó // 3 trưởng lão
            } else {
                m.writeInt(player.Info.idEntity);
                m.writeUTF("");
            }
            session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showGiaTocToChar(Char player) {
        try {
            Message m = new Message((byte) -88);
            if (player.clan != null) {
                m.writeInt(player.Info.idEntity);
                m.writeUTF(player.clan.getName());
                m.writeUTF(player.clan.getMainName());
                m.writeByte(player.clan.getMemberByName(player.Info.name).getType()); // 5 tộc trương // 4 tộc phó // 3 trưởng lão
            } else {
                m.writeInt(player.Info.idEntity);
                m.writeUTF("");
            }
            session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showListGiaToc() {
        try {
            List<Clan> clans = Clan.getClanDAO().getAll();
            Message m = new Message((byte) 122);
            m.writeByte(91);
            m.writeShort(clans.size());
            for (Clan clan : clans) {
                int percent = (int) (clan.getExp() / (float) clan.getExpNext() * 100);
                m.writeUTF(clan.getMainName());//
                m.writeInt(clan.getLevel()); // level
                m.writeInt(percent); // % exp
                m.writeInt(clan.getNumberMember()); // thành viên
                m.writeInt(clan.getMemberMax()); // max thành viên
                m.writeUTF(clan.name); //
            }
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void createGiaToc() {
        try {
            Message m = new Message((byte) -109);
            m.writeUTF("Xin chúc mừng bạn đã chính thức trở thành tộc trưởng của gia tộc " + player.Info.name);
            m.writeByte(-1);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }


    public void sendArrDataGame2() {
        try {
            Message msg = Message.d((byte) -113);
            msg.writer.dos.write(DataCenter.gI().writerArrDataGame2.baos.toByteArray());
            msg.inflate = true;
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendArrMap(int mapID) {
        try {
            Message msg = Message.f((byte) 3);
            msg.writeShort(mapID);
            msg.writer.dos.write(DataCenter.gI().MapTemplate[mapID].arrMap);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendIntoMap() {
        try {
            sendIntoGame();
            Message msg = new Message((byte) -103);
            player.zone.write(player, msg.writer);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    private void sendIntoGame() {
        try {
            session.sendMessage(new Message((byte) -104));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendChar() {
        try {
            Message msg = Message.d((byte) -127);
            player.writeMe(msg.writer);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendTabSelectChar(byte numberChar, User user) {
        try {
            Message msg = Message.d((byte) -128);
            msg.writeByte(numberChar);
            if(numberChar >0)
                user.initCharacterList();
            if (numberChar > 0) {
                for (int i = 0; i < numberChar; i++) {
                    msg.writeInt(i);
                    Char cS = user.chars.get(i);
                    ShowInfoArrC(cS, msg.writer);
                }
            }
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void ShowInfoArrC(Char c, Writer writer) {
        try {
            writer.writeByte(c.Info.status);
            writer.writeUTF(c.Info.name);
            writer.writeByte(c.Info.idChar);
            writer.writeByte(c.Info.gioiTinh);
            writer.writeByte(c.Info.idClass);
            writer.writeByte(0);
            writer.writeByte(0);
            writer.writeShort(0);
            writer.writeInt(1);
            writer.writeInt(1);
            writer.writeInt(1);
            writer.writeInt(1);
            writer.writeLong(c.Point.exp);
            writer.writeShort(c.Info.cx);
            writer.writeShort(c.Info.cy);
            writer.writeByte(0);
            c.writeItemBody(writer, c.Bag.arrItemBody);
            writer.writeByte(0);
            writer.writeByte(0);
            writer.writeByte(0);
            writer.writeByte(c.Info.rank);
            writer.writeByte(c.Info.selectCaiTrang);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCharIntoMap(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -102, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }


    public void removeCharIntoMap(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -101, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateXYChar(Writer writer, boolean when_move) {
        try {
            Message msg = null;
            if (when_move) {
                msg = new Message((byte) 123, writer);
            } else {
                msg = new Message((byte) -84, writer);
            }
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendHpMob(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 52, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendMobReSpawn(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 57, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendAttackMob(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 61, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }
    public void endCuuSat(int id,boolean win) {
        try {
            Message msg = new Message((byte) 18);
            msg.writeInt(id);
            msg.writeBoolean(win);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void setXYChar() {
        try {
            Message msg = new Message((byte) 102);
            msg.writeInt(player.Info.idEntity);
            player.Info.writeXY(msg.writer);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void setXYChar(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 102, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendOpenTabZone(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -6, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void resetScreen() {
        try {
            Message m = new Message((byte) -14);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void openUITrade(String name) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(59);
            m.writeUTF(name);
            session.sendMessage(m);
        } catch (IOException e) {
            Log.error("open ui trade err: " + e.getMessage(), e);
        }
    }

    public void sendItemDropFormMob(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 60, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void removeItemMap(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 58, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateItemMap(List<ItemMap> item) {
        try {
            Message m = Message.c((byte) -119);
            m.writeShort(item.size());
            synchronized (item) {
                for (ItemMap itemMap : item) {
                    if (itemMap != null) {
                        m.writeInt(itemMap.getOwnerID());
                        m.writeShort(itemMap.getId());
                        m.writeShort(itemMap.getX());
                        m.writeShort(itemMap.getY());
                        itemMap.getItem().write(m.writer);
                    }
                }
            }
            session.sendMessage(m);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void pickUpItem(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 59, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void removeItemBag(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 110, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateItemBag(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -4, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void addItemBag(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 109, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void useItemBag(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 116, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void addExp(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 94, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sortItem(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 117, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void itemExtendToBag(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 112, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void tachItem(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 118, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendArrItemBag(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 83, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void vutItem(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 111, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }
    public void birdAttackMob(int id){
        try {
            Message m = new Message((byte) -43);
            m.writeInt(player.id);
            m.writeShort(id);
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }

    public void updateHpMpWhenAttack(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 55, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void mobAttackChar(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 56, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateMp_Me(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 64, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateMpFull_Me(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 65, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateHp_Me(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 66, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateHpFull_Me(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 67, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateMp_Orther(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 68, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateMpFull_Orther(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 69, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateHp_Orther(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 70, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateHpFull_Orther(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 71, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openTabItem(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -25, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateDataChar(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -49, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void dataBag(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -95, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void getInfo(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 63, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateSkill(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 126, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void reSpawn(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 49, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void addEffect(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 50, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void removeEffect(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 51, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void itemBodyToBag_Me(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 113, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateItemBody_Orther(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -99, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void itemBodyDuPhong(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 36, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void itemBodyDuPhongToBag(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 37, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openNpc(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 54, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendTextNPC(String text, String text2) {
        try {
            Message m = new Message((byte) 5);
            m.writeUTF(text);
            m.writeUTF(text2);
            session.sendMessage(m);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }


    public void openNpcItem(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -8, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openTabGhepDa() {
        try {
            Message msg = new Message((byte) 122);
            msg.writeByte(81);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openMsg122(byte id) {
        try {
            Message msg = new Message((byte) 122);
            msg.writeByte(id);
            if (id == 87) {
                msg.writeByte(player.Info.countKham);
            }
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void sendNumCt() {
        try {
            Message msg = Message.c((byte) -25);
            msg.writeByte(player.Info.numct);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openTabCT() {
        try {
            Message msg = new Message((byte) 122);
            msg.writeByte(94);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openTabDichChuyen() {
        try {
            Message msg = new Message((byte) 122);
            msg.writeByte(85);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openTabKhamNgoc() {
        try {
            Message msg = new Message((byte) 122);
            msg.writeByte(87);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openTabTachKhamNgoc() {
        try {
            Message msg = new Message((byte) 122);
            msg.writeByte(90);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openTabCT2() {
        try {
            Message msg = new Message((byte) 122);
            msg.writeByte(95);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openTabCuongHoa() {
        try {
            Message msg = new Message((byte) 122);
            msg.writeByte(82);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void ghepDa(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 108, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void cuongHoa(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 107, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateStatusChar(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 33, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateItemBody_Me(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 35, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateTimeSkill(Writer writer) {
        try {
            session.sendMessage(new Message((byte) -85, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void openTabTachCuongHoa() {
        try {
            Message msg = new Message((byte) 122);
            msg.writeByte(84);
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateSachChienDau(Writer writer) {
        try {
            Message msg = Message.c((byte) -67);
            msg.writer.dos.write(writer.baos.toByteArray());
            session.sendMessage(msg);
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void tachCuongHoa(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 105, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public void updateBac(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 90, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }

    }

    public void updateBacKhoa(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 91, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }

    }

    public void updateVang(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 92, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }

    }

    public void updateVangKhoa(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 93, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }

    }

    public void nextTask(Writer writer) {
        try {
            session.sendMessage(new Message((byte) 103, writer));
        } catch (Exception ex) {
            Log.error("Loi service " + ex);
        }
    }

    public static ByteArrayOutputStream loadFile(String url) {
        try {
            FileInputStream openFileInput = new FileInputStream(url);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[1024];
            while (true) {
                int read = openFileInput.read(bArr);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            openFileInput.close();
            return byteArrayOutputStream;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createData() {
        try {
            Message m = Message.d((byte) -113);
            ItemOptionTemplate[] option = DataCenter.gI().ItemOptionTemplate;
            EffectTemplate[] eff = DataCenter.gI().EffectTemplate;
            m.writeShort(option.length);
            for (int i = 0; i < option.length; i++) {
                m.writeUTF(option[i].name);
                m.writeByte(option[i].type);
                m.writeByte(option[i].level);
                m.writeUTF(option[i].strOption);
            }
            m.writeByte(eff.length);
            for (int i = 0; i < eff.length; i++) {
                m.writeUTF(eff[i].name);
                m.writeUTF(eff[i].detail);
                m.writeByte(eff[i].type);
                m.writeShort(eff[i].idIcon);
                m.writeShort(eff[i].idMob);
            }
            m.writeShort(Manager.gI().itemTemplates.size());
            for (int i = 0; i < Manager.gI().itemTemplates.size(); i++) {
                m.writeUTF(Manager.gI().itemTemplates.get(i).name);
                m.writeUTF(Manager.gI().itemTemplates.get(i).detail);
                m.writeBoolean(Manager.gI().itemTemplates.get(i).isXepChong);
                m.writeByte(Manager.gI().itemTemplates.get(i).gioiTinh);
                m.writeByte(Manager.gI().itemTemplates.get(i).type);
                m.writeByte(Manager.gI().itemTemplates.get(i).idClass);
                m.writeShort(Manager.gI().itemTemplates.get(i).idIcon);
                m.writeByte(Manager.gI().itemTemplates.get(i).levelNeed);
                m.writeShort(Manager.gI().itemTemplates.get(i).taiPhuNeed);
                m.writeShort(Manager.gI().itemTemplates.get(i).idMob);
                m.writeShort(Manager.gI().itemTemplates.get(i).idChar);
            }
            m.writer.dos.write(DataCenter.gI().writerArrDataGame2.baos.toByteArray());
            m.inflate = true;
//            m.writeByte(DataCenter.gI().af.length);
//            for (int i = 0; i < DataCenter.gI().af.length; i++) {
//                m.writeByte(DataCenter.gI().af[i].length);
//                for (int j = 0; j < DataCenter.gI().af[i].length; j++) {
//                    m.writeByte(DataCenter.gI().af[i][j]);
//                }
//            }
//            m.writeShort(DataCenter.gI().I.length);
//            for (int i = 0; i < DataCenter.gI().I.length; i++) {
//                m.writeByte(DataCenter.gI().I[i].a);
//                m.writeByte(DataCenter.gI().I[i].b);
//                m.writeByte(DataCenter.gI().I[i].c);
//                m.writeByte(DataCenter.gI().I[i].d);
//                m.writeBoolean(DataCenter.gI().I[i].e);
//                m.writeBoolean(DataCenter.gI().I[i].f);
//                m.writeByte(DataCenter.gI().I[i].g.length);
//                for (int j = 0; j < DataCenter.gI().I[i].g.length; j++) {
//                    m.writeShort(DataCenter.gI().I[i].g[j]);
//                }
//                m.writeByte(DataCenter.gI().I[i].h.length);
//                for (int j = 0; j < DataCenter.gI().I[i].h.length; j++) {
//                    m.writeByte(DataCenter.gI().I[i].h[j].length);
//                    for (int k = 0; k < DataCenter.gI().I[i].h[j].length; k++) {
//                        m.writeShort(DataCenter.gI().I[i].h[j][k]);
//                    }
//                }
//            }
//            m.writeShort(DataCenter.gI().J.length);
//            for (int i = 0; i < DataCenter.gI().J.length; i++) {
//                m.writeByte(DataCenter.gI().J[i].a);
//                m.writeByte(DataCenter.gI().J[i].b.length);
//                for (int j = 0; j < DataCenter.gI().J[i].b.length; j++) {
//                    m.writeShort(DataCenter.gI().J[i].b[j].a);
//                    m.writeByte(DataCenter.gI().J[i].b[j].b);
//                    if(DataCenter.gI().J[i].b[j].b >= 30){
//                        m.writeShort(DataCenter.gI().J[i].b[j].c);
//                        m.writeShort(DataCenter.gI().J[i].b[j].d);
//                    } else if (DataCenter.gI().J[i].b[j].b >= 20) {
//                        m.writeShort(DataCenter.gI().J[i].b[j].d);
//                    } else if (DataCenter.gI().J[i].b[j].b >= 10) {
//                        m.writeShort(DataCenter.gI().J[i].b[j].c);
//                    }
//                }
//            }
//            m.writeShort(DataCenter.gI().L.length);
//            for (int i = 0; i < DataCenter.gI().L.length; i++) {
//                m.writeByte(DataCenter.gI().L[i].a);
//                m.writeByte(DataCenter.gI().L[i].b.length);
//                for (int j = 0; j < DataCenter.gI().L[i].b.length; j++) {
//                    m.writeShort(DataCenter.gI().L[i].b[j].a);
//                    m.writeByte(DataCenter.gI().L[i].b[j].b);
//                    m.writeByte(DataCenter.gI().L[i].b[j].num);
//                    if (DataCenter.gI().L[i].b[j].num >= 30){
//                        m.writeByte(DataCenter.gI().L[i].b[j].d);
//                        m.writeByte(DataCenter.gI().L[i].b[j].e);
//                    } else if (DataCenter.gI().L[i].b[j].num >= 20) {
//                        m.writeByte(DataCenter.gI().L[i].b[j].e);
//                    } else if (DataCenter.gI().L[i].b[j].num >= 10) {
//                        m.writeByte(DataCenter.gI().L[i].b[j].d);
//                    }
//                }
//            }
//            m.writeShort(DataCenter.gI().K.size());
//            for (Object entryObj : DataCenter.gI().K.entrySet()) {
//                Map.Entry entry = (Map.Entry) entryObj;
//                Object key = entry.getKey();
//                Object value = entry.getValue();
//
//                // Kiểm tra và xử lý kiểu dữ liệu của khóa và giá trị
//                if (key instanceof Short && value instanceof LangLa_iw) {
//                    Short keyTyped = (Short) key;
//                    LangLa_iw valueTyped = (LangLa_iw) value;
//                    m.writeShort(valueTyped.a);
//                    m.writeShort(valueTyped.b);
//                    m.writeByte(valueTyped.c);
//                    m.writeByte(valueTyped.d.length);
//                    for (int i = 0; i < valueTyped.d.length; i++) {
//                        m.writeByte(valueTyped.d[i].length);
//                        for (int j = 0; j < valueTyped.d[i].length; j++) {
//                            m.writeByte(valueTyped.d[i][j].a);
//                            m.writeByte(valueTyped.d[i][j].b);
//                            if(valueTyped.d[i][j].b>=30){
//                                m.writeShort(valueTyped.d[i][j].c);
//                                m.writeByte(valueTyped.d[i][j].d);
//                                m.writeByte(valueTyped.d[i][j].e);
//                                m.writeShort(valueTyped.d[i][j].f);
//                                m.writeByte(valueTyped.d[i][j].g);
//                                m.writeByte(valueTyped.d[i][j].h);
//                            } else if (valueTyped.d[i][j].b>=20) {
//                                m.writeShort(valueTyped.d[i][j].f);
//                                m.writeByte(valueTyped.d[i][j].g);
//                                m.writeByte(valueTyped.d[i][j].h);
//                            } else if (valueTyped.d[i][j].b>= 10) {
//                                m.writeShort(valueTyped.d[i][j].c);
//                                m.writeByte(valueTyped.d[i][j].d);
//                                m.writeByte(valueTyped.d[i][j].e);
//                            }
//                        }
//                    }
//                }
//            }
//            m.writeShort(DataCenter.gI().dataWayPoint.length);
//            for (int index = 0; index < DataCenter.gI().dataWayPoint.length; index++) {
//                m.writeShort(DataCenter.gI().dataWayPoint[index][0]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][1]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][2]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][3]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][4]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][10]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][11]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][5]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][6]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][7]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][8]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][9]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][12]);
//                m.writeShort(DataCenter.gI().dataWayPoint[index][13]);
//            }
            session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void npcChat(int id, String text) {
        try {
            Message m = new Message((byte) -48);
            m.writeShort(id);
            m.writeUTF(text);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void sendTimeInMap(int countDown, boolean start) {
        try {
            Message m = Message.c((byte) -80);
            m.writeLong(System.currentTimeMillis());
            m.writeInt(countDown * 100);
            m.writeBoolean(start);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }
    public void sendTimeInMap(int countDown, boolean start,long timeStart) {
        try {
            Message m = Message.c((byte) -80);
            m.writeLong(timeStart);
            m.writeInt(countDown * 100);
            m.writeBoolean(start);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void tradeItemLock(Trader trader) {
        Message m = new Message((byte) 81);
        try {
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void tradeInvite(String name) {
        try {
            Message ms = new Message((byte) 86);
            ms.writeUTF(name);
            session.sendMessage(ms);
        } catch (IOException e) {

        }
    }

    public void viewItemInfo(Trader trader, Vector<Item> item) {
        try {
            Message m = new Message((byte) 82);
            m.writeInt(trader.coinTradeOrder);
            m.writeByte(item.size());
            for (Item item1 : item) {
                if (item1 != null) {
                    item1.write(m.writer);
                }
            }
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void tradeAccept() {
        Message m = new Message((byte) 81);
        try {
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void resetAllScreen() {
        try {
            Message m = Message.c((byte) -43);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void sendCaptCha(String name) {
        try {
            player.captcha = CaptchaUtil.generateCaptchaText();
            Message m = Message.c((byte) -44);
            m.writeUTF(name);
            byte[] captcha = CaptchaUtil.CapchaToByte(player.captcha);
            if (captcha != null) {
                m.write(captcha);
            }
            session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resendCaptcha() {
        try {
            Message m = Message.c((byte) -44);
            m.writeUTF("");
            byte[] captcha = CaptchaUtil.CapchaToByte(player.captcha);
            if (captcha != null) {
                m.write(captcha);
            }
            session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateItemTrade() {
        try {
            Message m = new Message((byte) 83);
            m.writeInt(player.Bag.bac);
            player.writeItemBag(m.writer, player.Bag.arrItemBag);
            session.sendMessage(m);
        } catch (IOException e) {

        }
    }

    public void bagSort() {
        try {
            Message m = new Message((byte) 117);
            m.writeByte(0);
            session.sendMessage(m);
        } catch (IOException e) {

        }
    }

    public void boxSort() {
        try {
            Message m = new Message((byte) 117);
            m.writeByte(1);
            session.sendMessage(m);
        } catch (IOException e) {

        }
    }

    public void clanInvite(String player, String type, String giatoc) {
        try {
            Message m = new Message((byte) -109);
            m.writeUTF(player + " là " + type + " của gia tộc " + giatoc + " muốn mời bạn vào gia tộc,Bạn có chấp nhận không");
            m.writeByte(-125);
            session.sendMessage(m);
        } catch (IOException e) {

        }
    }

    public void inviteClan(String player) {
        try {
            Message m = new Message((byte) -109);
            m.writeUTF(player + " muốn xin vào gia tộc của bạn,Bạn có chấp nhận không ?");
            m.writeByte(-125);
            session.sendMessage(m);
        } catch (IOException e) {

        }
    }

    public void showInfoGiaToc() {
        try {
            Message mss = new Message((byte) 122);
            mss.writeByte(54);
            mss.writeUTF(player.clan.name); // tên gia tộc
            mss.writeUTF(player.clan.title == null ? " " : player.clan.title); // ngày thành lập
            mss.writeLong(player.clan.getRegDate());    // time ngay thanh lap
            mss.writeShort(player.clan.getLevel()); // cấp
            mss.writeInt(player.clan.getExp()); // exp
            mss.writeInt(player.clan.getExpNext()); // maxExp
            mss.writeInt(player.clan.getCongHien()); // cống hiến tuần
            mss.writeInt(player.clan.getCoin()); // Ngân sách
            mss.writeUTF(player.clan.getAlert()); // ghim text
            mss.writeByte(player.clan.openDun);//so lan mo ai
            List<Member> members = player.clan.memberDAO.getAll();
            synchronized (members) {
                mss.writeShort(members.size());// sl thanh vien
                for (Member mem : members) {
                    mss.writeByte(mem.getType());
                    mss.writeByte(mem.getClassId());
                    mss.writeByte(DataCenter.gI().DataIconChar[mem.getIdChar()].id); // id icon
                    mss.writeShort(mem.getLevel());
                    mss.writeUTF((mem.getName()));
                    mss.writeInt(mem.getPointClan());
                    mss.writeInt(mem.getPointClanWeek());
                    mss.writeBoolean(mem.isOnline());
                    mss.writeBoolean(false);
                }
            }
            String[] array = player.clan.getLog().split("\n");
            mss.writeShort(array.length);
            for (String string : array) {
                mss.writeUTF(string);
            }
            Item[] items = player.clan.getItems();
            mss.writeShort(items.length);
            for (Item item : items) {
                item.write(mss.writer);
            }
            mss.writeByte(player.clan.skillClans.size());
            {
                for (SkillClan skillClan : player.clan.skillClans) {
                    mss.writeByte(skillClan.id);
                }
            }
            mss.writeLong(System.currentTimeMillis() + 100000);
            mss.writeShort(player.clan.countInvite);
            session.sendMessage(mss);
        } catch (Exception e) {

        }
    }

    public void openFindParty(HashMap<String, Group> groups) {
        try {
            Message m = new Message((byte) 45);
            m.writeByte(groups.size());
            for (Group g : groups.values()) {
                MemberGroup party = g.memberGroups.get(0);
                m.writeBoolean(g.isTuVao);
                m.writeByte(g.memberGroups.size());
                m.writeByte(party.classID);
                m.writeByte(party.getChar().Info.idChar);
                m.writeShort(party.getChar().level());
                m.writeUTF(party.name);
            }
            session.sendMessage(m);
        } catch (Exception e) {
        }
    }

    public void partyInvite(String name) {
        try {
            Message m = new Message((byte) 41);
            m.writeUTF(name);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void outParty() {
        try {
            Message m = new Message((byte) 43);
            m.writeBoolean(false);
            m.writeByte(0);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void pleaseInputParty(String name) {
        try {
            Message m = new Message((byte) 39);
            m.writeUTF(name);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void chatPrivate(String to, String name, String text) {
        try {
            Message m = new Message((byte) 28);
            m.writeUTF(to);
            m.writeUTF(name);
            m.writeUTF(text);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void addFriend(String name, int i, boolean isBan) {
        try {
            Message m = new Message((byte) 79);
            m.writeUTF(name);
            m.writeByte(i);
            m.writeBoolean(isBan);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void removeFriend(String name) {
        try {
            Message m = new Message((byte) 76);
            m.writeUTF(name);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void inviteFriend(String name, int i) {
        try {
            Message m = new Message((byte) 77);
            m.writeUTF(name);
            m.writeByte(i);
            session.sendMessage(m);
        } catch (Exception e) {

        }
    }

    public void removeEnemy(String name) {
    }

    public void sendMessage(Message ms) {
        if (session != null) {
            session.sendMessage(ms);
        }
    }

    public void updateTaskOrder(TaskOrder taskOrder) {
        try {
            Message m = new Message((byte) -5);
            m.writeByte(0);
            m.writeByte(1);
            m.writeShort(taskOrder.count);
            m.writeShort(0);
            m.writeShort(0);
            m.writeShort(taskOrder.killId);
            m.writeShort(taskOrder.getMapId());
            m.writeShort(0);
            m.writeShort(0);
            m.writeShort(taskOrder.maxCount);
            m.writeUTF("");
            m.writeUTF("");
            m.writeUTF(taskOrder.getDescription());
            session.sendMessage(m);
            m.close();
        } catch (IOException e) {

        }
    }

    public void resetTaskOrder(int type) {
        try {
            Message m = new Message((byte) -5);
            m.writeByte(type);
            m.writeByte(-1);
            session.sendMessage(m);
            m.close();
        } catch (IOException e) {

        }
    }

public void sendTaskOrder(TaskOrder task) {
    try {
        Message m = new Message((byte) -5);
        m.writeByte(0);
        m.writeByte(1);
        m.writeShort(task.count);
        m.writeShort(0);
        m.writeShort(0);
        m.writeShort(task.killId);
        m.writeShort(task.getMapId());
        m.writeShort(0);
        m.writeShort(0);
        m.writeShort(task.maxCount);
        m.writeUTF("");
        m.writeUTF("");
        m.writeUTF(task.getDescription());
        session.sendMessage(m);
        m.close();
    } catch (IOException e) {

    }
}
    public void sendTaskBoss(TaskOrder task) {
        try {
            Message m = new Message((byte) -5);
            m.writeByte(1);
            m.writeByte(1);
            m.writeByte(task.count);
            m.writeShort(task.killId);
            m.writeShort(task.mapId);
            m.writeShort(6);
            m.writeShort(7);
            m.writeUTF(task.getDescription());
            session.sendMessage(m);
            m.close();
        } catch (IOException e) {

        }
    }
    public void resetSpin(){
        try {
            Message m = new Message((byte) 74);
            m.writeByte(0);
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }
    public void unlockSecurity(){
        try {
            Message m = Message.d((byte) -119);
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }

    public void sendSecurity() {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(98);
            m.writeBoolean(player.isSecurity);
            m.writeBoolean(player.isUnlockSecurity);
            m.writeInt(player.timeRemoveSecurity);
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }

    public void sendTyVo(String name) {
        try {
            Message m = new Message((byte) 32);
            m.writeUTF(name);
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }
    public void startTyVo(int id) {
        try {
            Message m = new Message((byte) 31);
            m.writeInt(player.id);
            m.writeByte(1);
            m.writeInt(id);
            m.writeByte(1);
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }
    public void resuiltTyVo(int id,byte type) {
        try {
            Message m = new Message((byte) 29);
            m.writeByte(type);
            m.writeInt(player.id);
            m.writeInt(id);
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }
    public void submenu(int id,String text) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(73);
            m.writeUTF(text);
            m.writeShort(id);
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }

    public void inviteLoiDai(String name) {
        try {
            Message m = new Message((byte) -93);
            m.writeUTF(name);
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }

    public void showListLoiDai() {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(49);
            m.writeShort(Arena.arenas.size());
            for (Arena arena: Arena.arenas){
                m.writeShort(arena.getId());
                m.writeUTF(arena.leaderTeamOneName);
                m.writeUTF(arena.leaderTeamTwoName);
                m.writeInt(arena.moneyTeamTwo);
            }
            session.sendMessage(m);
        } catch (IOException e) {
        }
    }

    public void updateTaskCount(short count) {
        try {
            Message m = new Message((byte) 103);
            m.writeShort(player.taskId);
            m.writeByte(player.taskMain.index);
            m.writeShort(count);
            sendMessage(m);
        } catch (IOException e) {

        }
    }
    public void sendTaskStep(int idStep) {
        Message m = new Message((byte) 12);
        try {
            m.writeByte(idStep);
            m.writeUTF("");
            m.writeUTF("");
            sendMessage(m);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendSTRtask() {
        Message m = new Message((byte) 12);
        try {
            m.writeByte(-2);
            m.writeUTF(player.taskMain.template.STR2);
            m.writeUTF(player.taskMain.template.STR3);
            sendMessage(m);
        } catch (IOException ex) {
        }
    }

    public void sendTaskInfo() {
        try {
            Message m = new Message((byte) 103);
            m.writeShort(player.taskId);
            if(player.taskMain!=null) {
                m.writeByte(player.taskMain.index);
                m.writeShort(player.taskMain.count);
            }else {
                m.writeByte(-1);
                m.writeShort(0);
            }
            sendMessage(m);
        } catch (IOException e) {

        }
    }
    public void mobChat(int id,String str) {
        try {
            Message m = new Message((byte) -12);
            m.writeShort(id);
            m.writeUTF(str);
            sendMessage(m);
        } catch (IOException e) {

        }
    }

    public void selectCard(Card[] results) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(102);
            for(Card card: results){
                m.writeShort(card.getId());
            }
            sendMessage(m);
        } catch (IOException e) {

        }
    }

    public void spinTreasure() {
        try {
            Message m = new Message((byte) 73);
            m.writeByte(player.treasure.getQuantity());//So sao
            m.writeByte(player.treasure.getIndex());// vi tri
            m.writeByte(player.treasure.getId()); //item
            if(player.treasure.getId()==177){
                m.writeBoolean(true);
            }
            m.writeBoolean(false);
            session.sendMessage(m);
            m.close();
        } catch (IOException e) {
        }
    }
    public void spinLoss(int index,int id){
        try {
            Message m = new Message((byte) 73);
            m.writeByte(-1);//So sao
            m.writeByte(index);// vi tri
            m.writeByte(id); //item
            m.writeBoolean(false);
            session.sendMessage(m);
            m.close();
        } catch (IOException e) {
        }
    }

    public void sellMarket(int fee,short index) {
        try {
            Message m = new Message((byte) 99);
            m.writeInt(fee);
            m.writeShort(index);
            session.sendMessage(m);
            m.close();
        } catch (IOException e) {
        }
    }

    public void buyMarket() {
        try {
            Message m = new Message((byte) 98);
            m.writeInt(player.Bag.bac);
            session.sendMessage(m);
            m.close();
        } catch (IOException e) {
        }
    }

    public void reloadLetter() {
        try {
            Message m = new Message((byte) 97);
            m.writeShort(player.letters.size());
            for (TemplateThu thu : player.letters) {
                m.writeShort(thu.id);
                m.writeBoolean(thu.isSucess);
                m.writeUTF(thu.NameNguoiGui);
                m.writeUTF(thu.Title);
                m.writeUTF(thu.NoiDungThu);
                m.writeInt(thu.Bac);
                m.writeInt(thu.BacKhoa);
                m.writeInt(thu.Vang);
                m.writeInt(thu.VangKhoa);
                m.writeLong(thu.Exp);
                m.writeInt((int) (thu.TimeEnd / 1000 + 2000000));
                if (thu.Item == null) {
                    m.writeShort(-1);
                } else {
                    thu.Item.write(m.writer);
                }
            }
            sendMessage(m);
        } catch (IOException ex) {
            Log.error("Loi reload thu "+ex);
        }
    }
}

