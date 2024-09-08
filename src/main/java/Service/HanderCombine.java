/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import com.sg188.data.DataCenter;
import com.sg188.data.ItemOption;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.lib.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ADMIN
 */
public class HanderCombine {






//    public static void KhamNgoc(Char _myChar, Message msg) {
//        try {
//            byte typeTb = msg.readByte();
//            short numTb = msg.readShort();
//            byte sizeNgoc = msg.readByte();
//            Item it = _myChar.checkBag(typeTb)[numTb];
//            if(it== null){
//                return;
//            }
//            Item[] itemNgoc = new Item[sizeNgoc];
//            for (int i = 0; i < sizeNgoc; i++) {
//                Item item = _myChar.Bag.arrItemBag[msg.readShort()];
//                if(item != null){
//                    itemNgoc[i]= item;
//                }
//            }
//            int countKham = it.checkCountKham()+_myChar.Info.countKham;
//            int countDaKham = it.countDaKham();
//            if(countDaKham >= countKham){
//                _myChar.service.alertMessage("Đã tới giới hạn khảm");
//                return;
//            }
//            boolean var1 = true;
//            int var2 = -1;
//            int y =0;
//                if(it!=null) {
//                    for (int var3 = 0; var3 < itemNgoc.length; ++var3) {
//                        if (itemNgoc[var3] != null) {
//                            if (var2 == -1) {
//                                var2 = itemNgoc[var3].id;
//                            } else if (var2 != itemNgoc[var3].id) {
//                                var1 = false;
//                                break;
//                            }
//
//                            y += itemNgoc[var3].getAmount();
//                        }
//                    }
//
//                    Vector var7 = new Vector();
//                    ItemOption var4;
//                    if ((var4 = it.a(var7, var2)) != null) {
//                        if (var4.a(it)) {
//                            var1 = false;
//                        }
//                    } else {
//                        var7 = new Vector();
//                        ItemOption[] var9 = it.L();
//
//                        for (int var5 = 0; var5 < var9.length; ++var5) {
//                            var7.add(var9[var5]);
//                        }
//
//                        if (it.V()) {
//                            var7.insertElementAt(ItemOption.g(var2), var7.size() - 1);
//                        } else {
//                            var7.add(ItemOption.g(var2));
//                        }
//
//                        it.strOptions = Item.a(var7);
//                    }
//                    int[] x;
//                    if (y > 0) {
//                        x = it.ab(y, var2);
//                    } else {
//                        x = null;
//                    }
//                }
//                for(Item item: itemNgoc){
//                    if(item !=null){
//                        _myChar.removeItem(item,true);
//                    }
//                }
//            Message m = new Message((byte) -46);
//            m.writeByte(1);
//            m.writeShort(0);
//            it.write(m.writer);
//            m.writeByte(typeTb);
//            _myChar.user.session.sendMessage(m);
//
////            Log.debug("type " + typeTb + " num " + numTb + " TypeNogc " + sizeNgoc + " numNgoc " + numNgoc + " quanity " + u);
//        } catch (IOException ex) {
//            Logger.getLogger(HanderCombine.class.getName()).log(Level.SEVERE, null, ex);
//            _myChar.service.alertMessage("Co loi say ra vui long bao voi admin");
//            return;
//        }
//
//    }
    public static void DichChuyenTrangBi(Char _myChar, Message msg){
        try {
            byte typeTb = msg.readByte();
            short numTb = msg.readShort();
            byte typeTb_2 = msg.readByte();
            short numTb_2 = msg.readShort();
            short indexBua = msg.readShort();
            Item it = _myChar.checkBag(typeTb)[numTb];
            Item it_2 = _myChar.checkBag(typeTb_2)[numTb_2];
            Item itBua = _myChar.Bag.arrItemBag[indexBua];
            if(it == null|| it_2 == null || itBua == null){
                return;
            }
            byte level = it.level;
            it.a(it_2.level);
            it_2.amount = 1;
            it_2.a(level);
            it.isLock =true;
            it_2.isLock = true;
            _myChar.removeItem(itBua);
            _myChar.msgRemoveItemBag(itBua);
            _myChar.checkBag(typeTb)[numTb] = it;
            _myChar.checkBag(typeTb_2)[numTb_2] = it_2;
            _myChar.msgUpdateItemBody();
            _myChar.msgUpdateItemBody_Orther();
            _myChar.msgSendArrItemBag();
            Message m = new Message((byte) 104);
            m.writeByte(typeTb);
            it.write(m.writer);
            m.writeByte(typeTb_2);
            it_2.write(m.writer);
            m.writeShort(indexBua);
            _myChar.user.session.sendMessage(m);
            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Dịch chuyển trang bị thành công",HanderMessage.WHITE));
            _myChar.user.session.sendMessage(HanderMessage.resetScreen());
        }catch (Exception ex){

        }
    }
}
