/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import EventClick.ClickEvent;
import EventClick.ClickTop;
import Manager.Manager;
import com.sg188.data.DataCenter;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.LuckyDraw;
import com.sg188.server.LuckyDrawManager;
import com.sg188.server.lib.Message;
import com.sg188.task.TaskName;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ADMIN
 */
public class HanderClickEvent {

    public static void HanderClick(Char _myChar, Message msg) {
        // kho báu 86
        // phúc lợi 88
        // hoạt động 56
        // Shop 6
        // Rương đồ 50
        // Ghép đá 81
        // Cường hóa 82
        // Nâng cấp bùa nổ 83
        // Tách cường hóa 84
        // Dịch chuyển trang bị 85
        // Khảm ngọc 87
        // Tách khảm ngọc 90
        // Ghép cải trang 94
        // Tách cải trang 95
        // Gia tộc 54
        // Thưởng BXH -57
        try {
            byte idEvent = msg.readByte();
            switch (idEvent) {
                case 0:
                case 1:
                    ClickEvent.ShopDuocPham(_myChar, idEvent);
                    break;
                case 2:
                case 3:
                    ClickEvent.Shop(_myChar, idEvent);
                    break;
                case 4:
                case 5:
                    ClickEvent.ShopQuanAn(_myChar, idEvent);
                    break;
                case 6:
                case 7:
                case 18:
//                case 40:
                case 38:
                case 30:
                case 37:
                case 19:
                    ClickEvent.Shop(_myChar, idEvent);
                    break;
                case 39:
                    ClickEvent.shop39(_myChar, idEvent);
                    break;
                case 40:
                    ClickEvent.shop40(_myChar, idEvent);
                    break;
                case 56:
                    ClickEvent.HoatDong(_myChar);
                    break;
                case 81:
                case 82:
                case 83:                // nang cap bua no
                case 84:
                case 85:
                case 87:
                case 90:
                case 94:
                case 95:
                    Message m = new Message((byte) 122);
                    m.writeByte(idEvent);
                    if (idEvent == 87) {
                        m.writeByte(_myChar.Info.countKham);
                    }
                    _myChar.user.session.sendMessage(m);
                    break;
                case 86:
                    ClickEvent.KhoBau(_myChar);
                    break;
                case 88:
                    ClickEvent.PhucLoi(_myChar);
                    break;
                case 89:
                    ClickEvent.NapDau(_myChar);
                    break;
                case 50:
                    ClickEvent.OpenBox(_myChar);
                    break;
                case 54:
                    if (_myChar.clan != null) {
                        _myChar.service.showInfoGiaToc();
                    } else {
                        _myChar.service.showListGiaToc();
                    }
                    if(_myChar.taskId== TaskName.NV_GIAI_CUU_INARI &&_myChar.taskMain!=null&&_myChar.taskMain.index==0)
                        _myChar.taskNext();
                    break;
                case 92:
                    if(_myChar.taskId==0&&_myChar.taskMain!=null&&_myChar.taskMain.index==1)
                    _myChar.taskNext();
                    break;
                case 93:
                    break;
                case 72:
                    ClickEvent.ShowTask(_myChar);
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 17:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                    if(_myChar.Info.idClass == 0){
                        _myChar.service.alertMessage("Vui lòng nhập học để mua đồ");
                        return;
                    }
                    byte he = msg.readByte();
                    ClickEvent.ShopTrangBi(_myChar, idEvent, he);
                    break;
                case 64:// trang bi hien nhan
                case 65: //trang bi sharigan
                case 66:// trang bi bayakugan
                case 67: //trang bi rinegan
                case 68://doi he
                case 69://doi he
                case 70: //doi he
                case 71://doi he
                case 75: //doi bi kip
                case 76:// luyen bi kip
                case 78:// doi tb thanh tinh thach
                case 79://chuc phuc
                case 96://dan duoc
                case 97:// bua no
                    break;
                case 100: //trang bi luc dao
                    break;
                case 74:
                    thuvanmay(_myChar, idEvent);
                    break;
                case 98:
                    _myChar.service.sendSecurity();
                    break;
                case 49:
                    _myChar.service.showListLoiDai();
                    break;
                case 101:
                    byte type = msg.readByte();
                    if(type == 0){
                        int money = msg.readInt();
                        LuckyDraw lucky = LuckyDrawManager.getInstance().find(type);
                        if(money > 0)
                        lucky.join(_myChar, money);
                        lucky.show(_myChar);
                    }
                    break;
                case 102:
                    _myChar.selectCard(msg);
                    break;
            }

            Log.debug("id event " + idEvent);
        } catch (IOException ex) {
            Logger.getLogger(HanderClickEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void thuvanmay(Char _myChar, byte idEvent) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(idEvent);
            _myChar.idListTVM = (byte) Utlis.nextInt(0, 2);
            m.writeShort(Manager.gI().listTVM[_myChar.idListTVM].length);
            for (int i = 0; i < Manager.gI().listTVM[_myChar.idListTVM].length; i++) {
                Item item = new Item(Manager.gI().listTVM[_myChar.idListTVM][i]);
                item.amount = Manager.gI().amountTVM[_myChar.idListTVM][i];
                item.write(m.writer);
            }
            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            Log.error("loi thu van may");
        }
    }

    public static void LuyenBiKip(Char _myChar) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(76);
            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void thuvanmaySilver(Char _myChar, byte idEvent) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(idEvent);
            _myChar.idListTVMSilver = (byte) Utlis.nextInt(0, 2);
            m.writeShort(Manager.gI().listTVMSilver[_myChar.idListTVMSilver].length);
            for (int i = 0; i < Manager.gI().listTVMSilver[_myChar.idListTVMSilver].length; i++) {
                Item item = new Item(Manager.gI().listTVMSilver[_myChar.idListTVMSilver][i]);
                item.amount = Manager.gI().amountTVMSilver[_myChar.idListTVMSilver][i];
                item.write(m.writer);
            }
            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            Log.error("loi thu van may");
        }
    }

    public static void HanderTop(Char _myChar, Message m) {
        try {
            byte indexTop = m.readByte();
            byte mSceenIndex = -1;
            byte mSceenIndex2 = -1;
            while (m.reader.dis.available() > 0) {
                mSceenIndex = m.readByte();
                if (m.reader.dis.available() > 0) {
                    mSceenIndex2 = m.readByte();
                }
            }

            Log.debug("indexTop " + indexTop + " mSceenIndex " + mSceenIndex + " mSceenIndex2 " + mSceenIndex2);
            ClickTop.ShowTop(_myChar, indexTop, mSceenIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void VongQuayMayMan(Char _myChar) {

        _myChar.user.session.sendMessage(HanderMessage.SendVongQuay((byte) 1, (byte) new Random().nextInt(1), 33));
    }

    public static void BuyEvent(Char _myChar, byte idCmd) {
        Message m = new Message((byte) -105);
        try {
            String text = "Bạn không đủ vàng";
            m.writeUTF(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        _myChar.user.session.sendMessage(m);
    }
}
