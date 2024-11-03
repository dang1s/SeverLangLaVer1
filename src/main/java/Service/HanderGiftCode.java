/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import Data.DataCode;
import Template.TemplateThu;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.CreateGiftCode.Code;
import com.sg188.server.lib.Message;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author ADMIN
 */
public class HanderGiftCode {

    //  => show thong báo text -108(vàng trên đầu),-107(trắng mid),-106(vàng mid),-105(red mid)
    // => nhắc nhở , -123
    public static synchronized void CheckGiftCode(Char _myChar, Message msg) {
        try {
            String code = msg.readUTF();
            int index = -1;
            boolean isOk = DataCode.Codes.stream().anyMatch(s -> s.Code.equals(code));
//            if(){
//                _myChar.service.alertMessage("Vui lòng kích hoạt tài khoản");
//                return;
//            }
            if (isOk) {
                boolean DaNhan = _myChar.GiftCode.GiftDaNhan.stream().anyMatch(s -> s.equals(code));
                if (DaNhan) {
                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao( "Bạn đã nhận mã quà tặng này rồi", HanderMessage.WHITE));

                    return;
                }
//                if(code.equals("rank10")){
//                    if(_myChar.Info.rank != 10){
//                        _myChar.service.alertMessage("Mã quà tặng này cần rank10 để sử dụng");
//                        return;
//                    }
//                }
//
                if(code.equals("kichhoat") || code.equals("vequay")){
                    if(!_myChar.user.actived){
                        _myChar.service.alertMessage("Sau khi kích hoạt bạn sẽ sử dụng được Giftcode này!");
                        return;
                    }
                }
                
                index = IntStream.range(0, DataCode.Codes.size())
                        .filter(userInd -> DataCode.Codes.get(userInd).Code.equals(code))
                        .findFirst()
                        .getAsInt();
                Code checkcode = DataCode.Codes.get(index);
                if(checkcode.count < 1){
                    _myChar.service.alertMessage("Mã quà tặng đã hết lượt sử dụng");
                    return;
                }
                for (int i = 0; i < 1; i++) {
                    Code code2 = DataCode.Codes.get(index);
                    code2.count--;
                    TemplateThu thu = new TemplateThu();
                    int idThu = _myChar.letters.size()+1;
                    if (_myChar.letters.size() > 0) {
                        idThu = _myChar.letters.get(_myChar.letters.size() - 1).id + 1;
                    }
                    thu.id = (short) idThu;
                    thu.Bac = code2.Bac;
                    thu.BacKhoa = code2.BacKhoa;
                    thu.Vang = code2.Vang;
                    thu.VangKhoa = code2.VangKhoa;
                    thu.Exp = code2.Exp;
                    thu.Title = "Thư của hệ thống";
                    thu.NameNguoiGui = "Hệ thống";
                    thu.NoiDungThu = "Phần thưởng mã quà tặng";
                    thu.TimeEnd = System.currentTimeMillis() + (code2.Day * 86400000);
                    if (code2.infoItem != null) {
                        thu.Item = code2.infoItem.cloneItem();
                        if(code2.infoItem.expiry != -1){
                            thu.Item.expiry = System.currentTimeMillis()+code2.infoItem.expiry;

                        }

                    }

                    _myChar.letters.add(thu);
                    Log.debug("id thu add "+thu.id);
                }
                _myChar.getService().reloadLetter();
                Message m = Message.c((byte) -43);
                _myChar.user.session.sendMessage(m);
                _myChar.user.session.sendMessage(HanderMessage.SendThongBao( "Mã quà tặng hợp lệ,vui lòng mở hộp thư để nhận thưởng", HanderMessage.YELLOW_MID));

                _myChar.GiftCode.GiftDaNhan.add(code);
            } else {
                _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Mã quà tặng không chính xác", HanderMessage.RED_MID));
            }
        } catch (IOException ex) {
            Logger.getLogger(HanderGiftCode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
