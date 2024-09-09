/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import EventClick.ClickEvent;
import com.sg188.data.DataCenter;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.real.Mob;
import com.sg188.server.lib.Message;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ADMIN
 */
public class HanderMessage {
// -93 => tỉ vỏ lôi đài, 32 => tỷ võ,39 => xin vô nhóm,41 => mời vô nhóm,86 => mời giao dịch    

    public static final byte YELLOW = -108;
    public static final byte WHITE = -107;
    public static final byte YELLOW_MID = -106;
    public static final byte RED_MID = -105;

    public static Message SendTypePk(int id, byte type) {
        Message m = new Message((byte) -15);
        try {
            m.writeInt(id);
            m.writeByte(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }
    public static Message SendCuuSat(int id, boolean isattack) {
        Message m = new Message((byte) 18);
        try {
            m.writeInt(id);
            m.writeBoolean(isattack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }
    public static Message sendGiaToc(Char player) {
        Message m = new Message((byte) -88);
        try {
            if (player.clan != null) {
                m.writeInt(player.Info.idEntity);
                m.writeUTF(player.clan.getName());
                m.writeUTF(player.clan.getMainName());
                m.writeByte(player.clan.getMemberByName(player.Info.name).getType()); // 5 tộc trương // 4 tộc phó // 3 trưởng lão
            } else {
                m.writeInt(player.Info.idEntity);
                m.writeUTF("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }


    public static Message SendAttackChar(int idAttack, int mp, short idSkill, int idAnDame) {
        Message m = new Message((byte) 20);
        try {
            m.writeInt(idAttack);
            m.writeInt(mp);
            m.writeShort(idSkill);
            m.writeInt(idAnDame);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public static Message SendHpAttack(int idC, int hp, int mp, boolean isCrit, short cx, short cy,String name) {
        Message m = new Message((byte) 55);
        try {
            m.writeInt(idC);
            m.writeInt(mp);
            m.writeInt(hp);
            m.writeBoolean(isCrit);
            m.writeShort(cx);
            m.writeShort(cy);
            m.writeUTF(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public static Message UpdateHp(int idChar, int hpDown) {
        Message m = new Message((byte) 66);
        try {
            m.writeInt(idChar);
            m.writeByte(hpDown);
            m.writeShort(0);
            m.writeShort(0);
            m.writeUTF("zz");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }
    public static Message updatePointDungeon(int point) {
        Message m = new Message((byte) 66);
        try {
            m.writeInt(point);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public static Message SendVongQuay(byte b1, byte b2, int i1) {
        Message m = null;
        try {
            m = Message.c((byte) -60);
            m.writeByte(b1);
            m.writeByte(b2);
            m.writeInt(i1);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Message Open_URL(String url) {
        Message m = null;
        try {
            m = Message.c((byte) -121);
            m.writeUTF(url);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Message SendHpAttackChar(int id, int hp, int mp, boolean isCrit) {
        Message m = new Message((byte) 55);
        try {
            m.writeInt(id);
            m.writeInt(hp);
            m.writeInt(mp);
            m.writeBoolean(isCrit);
            m.writeShort(0);
            m.writeShort(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public static Message msgCloneAttack(int id, int idMob) {
        Message m = null;
        try {
            m = new Message((byte) -19);
            m.writeInt(id);
            m.writeShort(idMob);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public static Message SendEff(int idC, short ideff, int value, long timeStart, int seconds) {
        Message m2 = new Message((byte) 50);
        try {
            m2.writeInt(idC);
            m2.writeShort(ideff);
            m2.writeInt(value);
            m2.writeLong(timeStart); // set long show
            m2.writeInt(seconds); // set long down

        } catch (Exception e) {
            e.printStackTrace();
        }
        return m2;
    }

    public static Message RemoveEff(int idC, short idEff) {
        Message m2 = new Message((byte) 51);
        try {
            m2.writeInt(idC);
            m2.writeShort(idEff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m2;
    }

    public static Message BuyShop(Char _myChar, Item it) {
        Message m = new Message((byte) 121);
        try {
            m.writeInt(_myChar.Bag.bac);
            m.writeInt(_myChar.Bag.bacKhoa);
            m.writeInt(_myChar.Bag.vang);
            m.writeInt(_myChar.Bag.vangKhoa);
            m.writeShort(1);
            it.write(m.writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public static Message TestMess7(int id) {
        Message m = new Message((byte) 7);
        try {
            m.writeInt(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

    public static Message SendVQSo(byte sao, int Index, int item) throws IOException {
        Message m = new Message((byte) 73);
        m.writeByte(sao);//So sao
        m.writeByte(Index);// vi tri
        m.writeByte(item); //item
        return m;
    }

    public static Message MsgCheTao(int level, int hoatLuc, short soLuong, Item item) {
        Message m = new Message((byte) -24);
        try {
            m.writeInt(level);
            m.writeInt(hoatLuc);
            m.writeShort(soLuong);
            for (int i = 0; i < soLuong; i++) {
                item.write(m.writer);
            }
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

//    public static  Message closeTab() {
//        Message m = new Message((byte) 123);
//        try {
//            m.writeByte(-43);
//        } catch (IOException e) {
//            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, e);
//        }
//        return m;
//    }
    public static  Message xoaTab(Char _myChar) {
        Message m = new Message((byte) 7);
        try {
            m.writeInt(_myChar.Info.idEntity);
        } catch (IOException e) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, e);
        }
        return m;
    }

    public static Message MsgLoadPhanTram(Char _myChar, int time, String text) {
        Message m = new Message((byte) 4);
        try {
            m.writeInt(time);
            m.writeUTF(text);
            m.writeByte(4);
            m.writeInt(_myChar.Info.idEntity);
            m.writeShort(-1);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;

    }

    public static Message RemoveMob(int id) {
        Message m = new Message((byte) 0);
        try {
            m.writeShort(id);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Message MoveMob(int id, short cx, short cy) {
        Message m = new Message((byte) -1);
        try {
            m.writeShort(id);
            m.writeShort(cx);
            m.writeShort(cy);
        } catch (Exception x) {
            x.printStackTrace();
        }
        return m;
    }

    public static Message AddMob(Mob mob) {
        Message m = new Message((byte) 1);
        try {
            mob.write(m.writer);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Message UpdateHoatLuc(int hoatluc) {
        Message m = new Message((byte) -23);
        try {
            m.writeInt(hoatluc);//
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Message takingItem(int time, int idEntity, String str,int idItem) {
        Message m = new Message((byte) 4);
        try {
            m.writeInt(time);
            m.writeUTF(str);
            m.writeByte(0);
            m.writeInt(idEntity);
            m.writeShort(idItem);
        } catch (IOException e) {
        }
        return m;
    }

    public static Message resetScreen() {
        Message m = new Message((byte) -14);
        return m;
    }

    public static Message SendThongBao(String text, byte color) {

        Message m = new Message((byte) color);
        try {
            m.writeUTF(text);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Message SendCTG(String text, int type, Char _mChar) {
        Message m = new Message((byte) 22);
        try {
            m.writer.writeByte(type);
            m.writer.writeUTF(_mChar.Info.name);
            m.writer.writeUTF(text);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Message SendCTG_HeThong(String text, int type) {
        Message m = new Message((byte) 22);
        try {
            m.writer.writeByte(type);
            m.writer.writeUTF("Hệ thống");
            m.writer.writeUTF(text);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Message ItemBagToBox(int indexBag, int indexBox) {
        Message m = new Message((byte) 115);
        try {
            m.writer.writeShort(indexBag);
            m.writer.writeShort(indexBox);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public static Message ItemBoxToBag(int indexBox, int indexBag) {
        Message m = new Message((byte) 114);
        try {
            m.writer.writeShort(indexBox);
            m.writer.writeShort(indexBag);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }


    public static Message SendTaskStep(int idStep) {
        Message m = new Message((byte) 12);
        try {
            m.writeByte(idStep);
            m.writeUTF("");
            m.writeUTF("");
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }


    public static Message SendCreateGiaToc() {
        Message m = new Message((byte) 122);
        try {
            m.writeByte(53);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;

    }

    public static Message SendMsg122(byte id) {
        Message m = new Message((byte) 122);
        try {
            m.writeByte(id);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;

    }

    public static Message updatePhucLoi(int id) {
        try {
            Message m = Message.c((byte) -70);
            m.writeShort(id);
            return m;
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Message sendPhucLoi(Char _myChar) {
        try {
            Message m = Message.c((byte) -61);
            ClickEvent.WritePhucLoi(_myChar, m);
            return m;
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static Message ThongBao_106(String text) {
        Message m = new Message((byte) -106);
        try {
            m.writeUTF(text);
        } catch (IOException ex) {
            Logger.getLogger(HanderMessage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;

    }

}
