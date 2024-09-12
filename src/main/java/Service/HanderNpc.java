/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import Data.DataMenuNpc;
import EventClick.ClickEvent;
import InfoChar.InfoSkill;
import Manager.Manager;
import MapService.Map;
import MapService.world.*;
import SqlConnection.CharDB;
import com.event.Event;
import com.rewards.RewardTop;
import com.sg188.clan.Clan;
import com.sg188.clan.Member;
import com.sg188.data.DataCenter;
import com.sg188.data.ItemOption;
import com.sg188.data.ItemTemplate;
import com.sg188.data.TaskTemplate;
import com.sg188.lib.Log;
import com.sg188.real.*;
import com.sg188.server.LuckyDraw;
import com.sg188.server.LuckyDrawManager;
import com.sg188.server.lib.Writer;
import com.sg188.task.TaskName;


import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ADMIN
 */
public class HanderNpc {

    static final String[] oderPhu = {"Làng lá,75", "Làng sương mù,60", "Làng mây,69", "Làng đá,85", "Làng cát,59", "Làng cỏ,68", "Làng mưa,102"};
//                                        75           60           69         85                 59        68 102

    public static void OpenMenu(Char _myChar, short idNpc) {
        try {
            short idNpcReal = (short) getIdNpcFormMap(idNpc, _myChar);
            String textNpc = DataMenuNpc.textNpc(idNpcReal);
            if (textNpc == null) {
                textNpc = "";
            }
            if(idNpcReal==98&&idNpc==0){
                textNpc = "Đặt cược;Rời khỏi nơi này";
            }
            if(idNpcReal == 73){

                if(Event.getEvent() !=null){
                    textNpc=Event.getEvent().menuKhaTienNu;

                }
                else {
                    textNpc = DataMenuNpc.textNpc(idNpcReal);
                }

            }
            if(idNpcReal == 31&&_myChar.zone.map.mapID==67){
                _myChar.service.sendTextNPC("Ta đã một mình tới đây để triệt phá ổ nhóm của Pain. Không ngờ chúng nó lại đông đến vậy. Các con hãy giúp ta xử lý chúng! ","");
                return;
            }
            int taskNpcId = _myChar.getTaskNpcId();
            if(taskNpcId != -1 &&idNpcReal == taskNpcId){
                if(_myChar.menuTask())
                return;
                if(_myChar.taskId == 20&&_myChar.taskMain!=null&&_myChar.taskMain.index==1){
                    textNpc="Thi lý thuyết";
                }
            }
            Writer wr = new Writer();
            wr.writeShort(idNpc);
            wr.writeUTF(textNpc);
            _myChar.service.openNpc(wr);
        } catch (IOException ex) {
            Logger.getLogger(HanderNpc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void OderMenu(Char _myChar, int indexNpc, int index1, int index2) {
        SelectNpc(_myChar, indexNpc, (byte) index1, (byte) index2);
    }

    public static void OpenNpcItem(Char _myChar, int select, int select2) {
        if (!_myChar.InfoGame.isOderMenu) {
            switch (select) {
                case 0:
                    Map.maps[86].addChar(_myChar);
                    break;
                case 1:
                    Writer writer = new Writer();
                    try {
                        StringBuilder str2 = new StringBuilder();
                        for (int i = 0; i < oderPhu.length; i++) {
                            if (i > 0) {
                                str2.append(";");
                            }
                            str2.append(oderPhu[i].split(",")[0]);
                        }
                        Log.debug(str2.toString());
                        writer.writeUTF("");
                        writer.writeUTF(str2.toString());
                        _myChar.service.openNpcItem(writer);
                        _myChar.InfoGame.isOderMenu = true;
                    } catch (IOException e) {
                    }
                    break;
                case 2:
                    break;
            }
        } else {
            switch (select) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    short mapOder = (short) (select == 0 ? 75 : (select == 1 ? 60 : (select == 2 ? 69 : (select == 3 ? 85 : (select == 4 ? 59 : (select == 5 ? 68 : 102))))));

                    if (Map.maps[mapOder].addChar(_myChar)) {
                        _myChar.InfoGame.clenOder();
                        _myChar.Info._mapID = mapOder;
                    }
                    break;
            }
        }
    }

    private static void SelectNpc(Char _myChar, int indexNpc, byte index1, byte index2) {
        int idNpc = getIdNpcFormMap(indexNpc, _myChar);
        switch (idNpc) {
            case 4:
            case 6:
            case 5:
            case 7:
            case 8:
                    _SelectThayco(_myChar, index1, index2);
                break;
            case 45:
                _SelectPhu(_myChar, index1, index2);
                break;
            case 9:
                _SelectNpc9(_myChar, index1);
                break;
            case 28:
                _SelectNpcThoRen(_myChar, index1);
                break;
            case 93:
                _SelectDuocPham(_myChar, index1, index2);
                break;
            case 99:
                _SelectQuanAn(_myChar, index1, index2);
                break;
            case 47:
                _SelectRuongDo(_myChar, index1);
                break;
            case 96:
                if(_myChar.taskId == 20&&_myChar.taskMain!=null&&_myChar.taskMain.index==1){
                    _myChar.anserChunnin();
                }else
                ClickEvent.ShopTrangBi(_myChar, (byte) 20, _myChar.Info.idhe);
                break;
            case 29:
                _SelectTrangPhuc(_myChar, index1);
                break;
            case 91:
                _SelectPhuKien(_myChar, index1);
                break;
            case 31:
                _SelectHokage(_myChar, index1);
                break;
            case 92:
                _SelectNgoaiTrang(_myChar, index1);
                break;
            case 32:
                _SelectOnoki(_myChar, index1, index2);
                break;
            case 97:
                _SelectKinkaku(_myChar, index1, index2);
                break;
            case 101:
                _SelectNpcEbisu2(_myChar, index1, index2);
                break;
            case 21:
                _SelectGinkaku(_myChar, index1, index2);
                break;
            case 102:
                _SelectTerumi(_myChar, index1, index2);
                break;
            case 76:
                _SelectKhaPhuThuy(_myChar, index1, index2);
                break;
            case 98:
                _SelectRasa(_myChar, index1, index2);
                break;
            case 100:
                _SelectTapHoa(_myChar, index1, index2);
                break;
            case 59:
                _SelectRaiKaGe(_myChar, index1, index2);
                break;
            case 73:
                if(Event.getEvent()!=null){
                    Event.getEvent().menu(_myChar, index1, index2);
                }else {
                    _selectTsunade(_myChar, index1, index2);
                }

                break;
            case 105:
                _SelectFukasaku(_myChar, index1, index2);
                break;
            case 30:
                _selectTsunade(_myChar, index1, index2);
                break;

        }
    }

    private static void _SelectRaiKaGe(Char myChar, byte index1, byte index2) {
        int level = myChar.level();
        int id = 0;

        switch (index1) {
            case 0:
                if (!Manager.gI().reciveKey.containsKey(myChar.Info.name)) {
                    if (level >= 15 && level <= 29) id = 244;
                    else if (level > 29 && level < 50) id = 245;
                    else if (level > 49) id = 246;

                    if (id > 0) {
                        Manager.gI().reciveKey.put(myChar.Info.name, id);
                        Item key = new Item(id);
                        key.isLock = true;
                        myChar.addItem(key);
                        myChar.msgAddItemBag(key);
                    }
                } else {
                    myChar.service.serverMessage("Mỗi ngày chỉ có thể nhận chìa khoá 1 lần");
                }
                break;
            case 1:
            case 2:
            case 3:
                try {
                    if (myChar.idDiaCung != -1) {
                        Dungeon dungeon = Dungeon.findDungeonById(myChar.idDiaCung);
                        if (dungeon != null && !dungeon.isClosed()) {
                            myChar.addWorld(dungeon);
                            dungeon.join(myChar);
                            return;
                        } else {
                            myChar.idDiaCung = -1;
                        }
                    }
                    int requiredLevel = index1 == 1 ? 15 : index1 == 2 ? 30 : 50;
                    int requiredLevelMax = index1 == 1 ? 29 : index1 == 2 ? 49 : 60;
                    int itemrequi = requiredLevel == 15 ? 244 : requiredLevel == 30 ? 245 : 246;
                    if (level >= requiredLevel && level <= requiredLevelMax) {
                        Item key = myChar.FindItemBag(itemrequi);
                        if (key == null) {
                            myChar.service.serverMessage("Không có chìa khoá địa cung trong túi đồ");
                            return;
                        }
                        if (myChar.getGroup() != null) {
                            boolean check = false, checkLuot = true, checkKhu = false;
                            List<Char> chars = myChar.getGroup().getChars();
                            List<Char> party = myChar.getGroup().getCharsInZone(myChar.Info._mapID, myChar.zone.zoneID);
                            if (party.size() < myChar.getGroup().getChars().size()) {
                                myChar.service.alertMessage("Vui lòng tập hợp đủ thành viên lại");
                                return;
                            }
                            for (Char p : chars) {
                                if (p != myChar && p != null && p.user != null) {
                                    if (p.idDiaCung != -1 || p.level() < requiredLevel || p.level() > requiredLevelMax ||
                                            p.zone.zoneID != myChar.zone.zoneID || p.Info._mapID != myChar.Info._mapID || p.FindItemBag(itemrequi) == null) {
                                        check = true;
                                        if (p.idDiaCung != -1)
                                            myChar.service.alertMessage("Có thành viên" + p.Info.name + " trong tổ đội đã tham gia một địa cung khác");
                                        else if (p.level() < requiredLevel || p.level() > requiredLevelMax)
                                            myChar.service.alertMessage("Có thành viên" + p.Info.name + " trong tổ đội không đủ điều kiện tham gia");
                                        else if (p.FindItemBag(itemrequi) == null)
                                            myChar.service.alertMessage("Có thành viên " + p.Info.name + " trong tổ đội không đủ chìa khoá");
                                        return;
                                    }
                                    level += p.level();
                                }
                            }
                            if (!check) {
                                if (myChar.getGroup().memberGroups.get(0).charId == myChar.id) {
                                    level /= chars.size();
                                    Dungeon dungeon = new Dungeon(level, 3600);
                                    myChar.addWorld(dungeon);
                                    Dungeon.addDungeon(dungeon);
                                    dungeon.addMember(myChar);
                                    dungeon.join(myChar);
                                    myChar.removeItem(key);
                                    myChar.idDiaCung = dungeon.getId();
                                    for (Char p : chars) {
                                        if (p != null && p.user != null && p != myChar) {
                                            p.idDiaCung = dungeon.getId();
                                            p.removeItem(p.FindItemBag(itemrequi));
                                            p.service.serverMessage("Đội trưởng đã mở khoá địa cung");
                                        }
                                    }
                                } else {
                                    myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không phải đội trưởng", HanderMessage.WHITE));
                                }
                            }
                        } else {
                            myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không có tổ đội", HanderMessage.WHITE));
                        }
                    } else {
                        myChar.service.serverMessage("Địa cung này không phù hợp với cấp độ của bạn");
                    }
                } catch (Exception e) {
                    Log.error("Loi mo dia cung roi ban oi ", e);
                }
                break;
            case 4:
                break;
        }
    }

    private static void _SelectRasa(Char myChar, byte index1, byte index2) {
        if(myChar.zone.isLoiDai()){
            switch (index1) {
                case 0:
                    myChar.service.openMsg122((byte) 52);
                    break;
                case 1:
                    Arena arena = (Arena) myChar.findWorld(World.ARENA);
                    arena.close();
                    Map.maps[myChar.Info._mapID].addChar(myChar);
                    break;
            }
        }else {
            switch (index1) {
                case 0:
                    switch (index2) {
                        case 0:
                            myChar.orderTaskBoss();
                            break;
                        case 1:
                            myChar.cancelTaskBoss();
                            break;
                        case 2:
                            myChar.finishTaskBoss();
                            break;
                    }
                    break;
                case 1:
                    Item duoivithu = myChar.FindItemBag(687);
                    if (duoivithu == null) {
                        myChar.service.alertMessage("Không tìm thấy lông vĩ thú trong hành trang");
                        return;
                    }
                    if (duoivithu.amount < 800) {
                        myChar.service.alertMessage("Không đủ 800 lông vĩ thú");
                        return;
                    }
                    if (myChar.Bag.bac < 1000000) {
                        myChar.service.alertMessage("Không đủ 1.000.000 Bac");
                        return;
                    }
                    Item nhatvi = new Item(476);
                    nhatvi.addItemOption(new ItemOption(0, 500, 600));
                    nhatvi.addItemOption(new ItemOption(2, 100, 120));
                    nhatvi.addItemOption(new ItemOption(5, 70, 75));
                    nhatvi.addItemOption(new ItemOption(256, 30, 35));
                    nhatvi.addItemOption(new ItemOption(257, 30, 35));
                    nhatvi.addItemOption(new ItemOption(149, 3));
                    nhatvi.addItemOption(new ItemOption(151, 70, 75));
                    nhatvi.addItemOption(new ItemOption(167, 80, 100));
                    nhatvi.addItemOption(new ItemOption(255, 10, 20));
                    nhatvi.createItemOptions();
                    nhatvi.isLock = true;
                    myChar.removeItemByAmount(duoivithu, 800);
                    myChar.msgRemoveItemBag(duoivithu);
                    myChar.addItem(nhatvi);
                    myChar.msgAddItemBag(nhatvi);
                    myChar.addBac(-1000000);
                    break;
                case 2:
                    if (myChar.Bag.arrItemBody[10] != null && myChar.Bag.arrItemBody[10].id == 476) {
                        if (myChar.Bag.arrItemBody[10].isSucManh()) {
                            myChar.user.session.sendMessage(HanderMessage.SendThongBao("Đã mở khoá sức mạnh rồi", HanderMessage.WHITE));
                            return;
                        }
                        myChar.Bag.arrItemBody[10].strOptions = "305,0,180000;" + myChar.Bag.arrItemBody[10].strOptions;
                        myChar.user.session.sendMessage(HanderMessage.SendThongBao("Mở khoá sức mạnh vĩ thú thành công", HanderMessage.WHITE));
                    }
                    break;
                case 3:
                    if (myChar.Bag.arrItemBody[10] != null && myChar.Bag.arrItemBody[10].isSucManh()) {
                        Item vithu = myChar.Bag.arrItemBody[10];
                        if (vithu.id == 484) {
                            myChar.service.alertMessage("Đã đạt tới giới hạn");
                            return;
                        }
                        if (vithu.checkSucManh() == 180000) {
                            duoivithu = myChar.FindItemBag(687);
                            if (duoivithu == null) {
                                myChar.service.alertMessage("Không tìm thấy lông vĩ thú trong hành trang");
                                return;
                            }
                            if (duoivithu.amount < 800) {
                                myChar.service.alertMessage("Không đủ 800 lông vĩ thú");
                                return;
                            }
                            myChar.removeItemByAmount(duoivithu, 800);
                            myChar.msgRemoveItemBag(duoivithu);
                            int idNext = vithu.id += 1;
                            Item bijuuOld = myChar.Bag.arrItemBody[10].cloneItem();
                            myChar.Bag.arrItemBody[10] = null;
                            Item bijuu = new Item(idNext);
                            bijuuOld.a(0);
                            bijuu.strOptions = bijuuOld.strOptions;
                            int endIndex = bijuu.strOptions.indexOf(";") + 1;
                            bijuu.strOptions = bijuu.strOptions.substring(endIndex);
                            Item.getOptionBijuu(bijuu, idNext - 476 + 25);
                            bijuu.strOptions = "305,0,180000;" + bijuu.strOptions;
                            bijuu.isLock = true;
                            myChar.addItem(bijuu);
                            myChar.msgAddItemBag(bijuu);
                            myChar.msgUpdateItemBody();
                            myChar.msgUpdateItemBody_Orther();
                        } else {
                            myChar.service.alertMessage("Vui lòng nâng max sức mạnh để thêm đuôi");
                        }
                    } else {
                        myChar.service.alertMessage("Vui lòng đeo vĩ thú để thêm đuôi");
                    }
                    break;
                case 4:
//                    if (true){
//                        myChar.service.alertMessage("Lôi đài sắp mở");
//                        return;
//                    }
                    switch (index2){
                        case 0:
                            myChar.service.openMsg122((byte) 51);
                            break;
                        case 1:
                            myChar.service.showListLoiDai();
                            break;
                    }
                    break;
            }
        }
    }

    private static void _SelectKhaPhuThuy(Char myChar, byte index1, byte index2) {
        switch (index1) {
            case 0:
                myChar.Info.khoaExp = !myChar.Info.khoaExp;
                String str = myChar.Info.khoaExp ? "Khoá cấp độ thành công" : "Đã mở khoá cấp độ";
                myChar.service.alertMessage(str);
                break;
            case 1:
                if (myChar.user.topSm == 0) {
                    RewardTop rw = Manager.gI().getRewardTopByID(myChar.id);
                    if (rw != null) {
                        if (myChar.getCountNullItemBag() < rw.getItems().size()) {
                            myChar.service.serverMessage("Hành trang không đủ chỗ trống");
                            return;
                        }
                        CharDB.updateTopSm(myChar.id);
                        myChar.user.topSm = 1;
                        myChar.addVang(rw.getGold());
                        myChar.addVangKhoa(rw.getGold_lock());
                        myChar.addBac(rw.getSliver());
                        myChar.addBacKhoa(rw.getSliver_lock());
                        for (Item item : rw.getItems()) {
                            if (item != null) {
                                Item itemadd = item.cloneItem();
                                myChar.addItem(itemadd);
                                myChar.msgAddItemBag(itemadd);
                            }
                        }
                        myChar.service.alertMessage("Quà top đã được gửi vào hành trang của bạn vui lòng kiểm tra hành trang");
                    } else {
                        myChar.service.serverMessage("Bạn không có trong top");
                    }
                } else {
                    myChar.service.serverMessage("Bạn đã nhận quà top rồi");
                }
                break;
        }
    }

    private static void _SelectThayco(Char myChar, byte index1, byte index2) {
        switch (index1) {
            case 0:
                switch (index2) {
                    case 0:
                        myChar.service.openMsg122((byte) 97);
                        break;
                    case 1:
                        myChar.nangcapBuaSieuCap();
                        break;
                    case 2:
                        myChar.nangcapBuaHienNhan();
                        break;

                }
                break;
            case 1:
                myChar.service.openMsg122((byte) 75);
                break;
            case 2:
                myChar.service.openMsg122((byte) 76);
                break;
            case 3:
                myChar.service.openMsg122((byte) 100);
                break;
        }
    }
    private static void _selectTsunade(Char myChar, byte index1, byte index2) {
        switch (index1) {
//            case 0:
//                LuckyDraw lucky = LuckyDrawManager.getInstance().find(0);
//                lucky.show(myChar);
//                break;
            case 0:
                SelectCard.getInstance().open(myChar);
                break;
            case 1:
                myChar.Info.khoaExp = !myChar.Info.khoaExp;
                String str = myChar.Info.khoaExp ? "Khoá cấp thành công" : "Đã mở khoá cấp";
                String str2;
                if (str == "Khoá cấp thành công"){
                    str2 = " (Lúc này bạn không thể nhận Exp)";
                }else {
                    str2= " (Lúc này bạn có thể nhận Exp)";
                }
                myChar.service.alertMessage(str + str2);
                break;
            case 2:
                for (int i = 0; i < myChar.Bag.arrItemBag.length; i++) {
                    myChar.Bag.arrItemBag[i] = null;
                }
                myChar.service.alertMessage("Xóa hành trang thành công!");

                break;

        }
    }

    private static void _SelectGinkaku(Char myChar, byte index1, byte index2) {
        switch (index1) {
            case 0:
                switch (index2) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        myChar.ChangeCoin(index2);
                        break;

                }
                break;
            case 1:
                myChar.user.coin = CharDB.getCoinByName(myChar);
                myChar.service.alertMessage("Số coin hiện có: " + myChar.user.coin);
                break;
//            case 2:
//                myChar.addSlotBag();
//                break;
//            case 2:
////                if (true) {
////                    myChar.service.alertMessage("Thử vận may bạc chưa mở");
////                    return;
////                }
//                myChar.isWheelSilver = true;
//                myChar.isWheelGold = false;
//                HanderClickEvent.thuvanmaySilver(myChar, (byte) 74);
//                break;
//            case 3:
//                if (true) {
//                    myChar.service.alertMessage("Thử vận may VIP chưa mở");
//                    return;
//                }
//                myChar.isWheelSilver = false;
//                myChar.isWheelGold = true;
//                HanderClickEvent.thuvanmay(myChar, (byte) 74);
//                break;
        }
    }

    private static void _SelectKinkaku(Char _myChar, byte select, byte index2) {
        switch (select) {

            case 0:
                switch (index2) {
                    case 0:
                        if (_myChar.Bag.vang < 100) {
                            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không Đủ Vàng ", HanderMessage.WHITE));
                            return;
                        } else {
                            _myChar.addVang(-100);
                            _myChar.addBac(300000);
//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 600.000 bạc ", HanderMessage.WHITE));
                        }
                        break;
                    case 1:
                        if (_myChar.Bag.vang < 1000) {
                            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không Đủ Vàng ", HanderMessage.WHITE));
                            return;
                        } else {
                            _myChar.addVang(-1000);
                            _myChar.addBac(3000000);
//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 45.000.000 bạc khóa ", HanderMessage.WHITE));
                        }
                        break;

                    case 2:
                        if (_myChar.Bag.vang < 100) {
                            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không Đủ Vàng ", HanderMessage.WHITE));
                            return;
                        } else {
                            _myChar.addVang(-100);
                            _myChar.addBacKhoa(5000000);
//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 4.000.000 bạc khóa ", HanderMessage.WHITE));
                        }
                        break;

                    case 3:
                        if (_myChar.Bag.vang < 1000) {
                            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không Đủ Vàng ", HanderMessage.WHITE));
                            return;
                        } else {
                            _myChar.addVang(-1000);
                            _myChar.addBacKhoa(50000000);
//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 45.000.000 bạc khóa ", HanderMessage.WHITE));
                        }
                        break;
                }
                break;

            case 1:
                _myChar.service.openMsg122((byte) 78);
                break;
        }
    }

    private static void _SelectNpcEbisu2(Char myChar, byte index1, byte index2) {
        switch (index1) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                short mapOder = (short) (index1 == 0 ? 75 : (index1 == 1 ? 60 : (index1 == 2 ? 69 : (index1 == 3 ? 85 : (index1 == 4 ? 59 : (index1 == 5 ? 68 : 104))))));
                Map.maps[mapOder].addChar(myChar);
                break;
        }
    }

    private static void _SelectOnoki(Char myChar, byte index1, byte index2) {
        switch (index1) {
            case 0:
                break;
            case 1:
                if(DaiHoiVoThuat.DAIHOI!=null){
                    if(DaiHoiVoThuat.DAIHOI.isOpened){
                        DaiHoiVoThuat.DAIHOI.join(2,myChar);
                    }else {
                        DaiHoiVoThuat.DAIHOI.join(1,myChar);
                    }
                }else
                    myChar.service.serverMessage("Đại hội chưa mở");
                break;
            case 2:
                switch (index2) {
                    case 0:
                        if (!myChar.user.actived){
                            myChar.service.alertMessage("Cần kích hoạt để tạo gia tộc");
                            return;
                        }
                        myChar.service.openMsg122((byte) 53);
                        break;
                    case 1:
                        myChar.service.openMsg122((byte) 55);
                        break;
                    case 2:
//                        if (true){
//                            myChar.service.alertMessage("Ải gia tộc đang bảo trì để sửa lỗi");
//                            return;
//                        }
                        if (myChar.clan != null) {
                            Territory ter = Territory.getTerritory(myChar.clan.id);
                            Member member = myChar.clan.getMemberByName(myChar.Info.name);
                            if (member == null) {
                                return;
                            }
                            int typeClan = member.getType();
                            boolean isClanLeader = typeClan == Clan.TYPE_TOCTRUONG || typeClan == Clan.TYPE_TOCPHO;
                            if (ter == null) {
                                if (myChar.clan.getOpenDun() <= 0) {
                                    myChar.service.serverMessage(
                                            "Gia tộc của ngươi đã hết lượt tham gia ải gia toc.");
                                    return;
                                }
                                if (!isClanLeader) {
                                    myChar.service.serverMessage(
                                            "Để mở lãnh địa gia tộc cần những thành viên cấp cao như Tộc Trưởng và Tộc Phó.");
                                    return;
                                }
                                ter = new Territory(myChar.clan.id);
                                Territory.addTerritory(myChar.clan.id, ter);
                                myChar.clan.openDun -= 1;
                                myChar.clan.getClanService()
                                        .serverMessage(myChar.Info.name + " đã mở ải gia tộc , hãy gặp npc Onoki để điểm danh ngay");
                                myChar.addWorld(ter);
                                ter.joinZone(myChar, 46);
                            } else {
                                myChar.service.alertMessage("Gia tộc đã mở cửa ải");
                            }
                        } else {
                            myChar.service.serverMessage("Bạn chưa có gia tộc");
                        }
                        break;
                    case 3:
                        if (myChar.clan != null) {
                            Territory ter = Territory.getTerritory(myChar.clan.id);
                            Member member = myChar.clan.getMemberByName(myChar.Info.name);
                            if (member == null) {
                                return;
                            }
                            if (ter != null) {
                                if (ter.started && !ter.isInTerritory(myChar.id)) {
                                    myChar.service.serverMessage(
                                            "Ai gia tộc đã bắt đầu, ngươi hãy tham gia vào lần sau.");
                                    return;
                                }
                                myChar.addWorld(ter);
                                ter.joinZone(myChar, 46);
                            } else {

                            }
                        }
                        break;
                }
                break;
            case 3:
                if (myChar.idCamThuat != -1) {
                    CamThuat camThuat = CamThuat.findCamThuatById(myChar.idCamThuat);
                    if (camThuat != null) {
                        if (camThuat.isClosed()) {
                            myChar.idCamThuat = -1;
                            return;
                        }
                        myChar.addWorld(camThuat);
                        camThuat.join(myChar);
                    } else {
                        myChar.idCamThuat = -1;
                    }
                }
                if (myChar.getGroup() != null) {
                    boolean check = false;
                    boolean checkLuot = true;
                    boolean checkLevel = true;
                    int level = 0;
                    List<Char> chars = myChar.getGroup().getCharsInZone(myChar.zone.map.mapID, myChar.zone.zoneID);
                    if (chars.size() < myChar.getGroup().getChars().size()) {
                        myChar.service.alertMessage("Vui lòng tập hợp đủ thành viên lại");
                        return;
                    }
                    if (myChar.level() < 40) {
                        myChar.service.alertMessage("Ban không đủ level gia cấm thuật");
                        return;
                    }
                    for (Char p : chars) {
                        if (p.idCamThuat != -1) {
                            check = true;
                            return;
                        }
                        if (p.Info.countCamThuat < 1) {
                            checkLuot = false;
                            return;
                        }
                        if (p.level() < 40) {
                            checkLevel = false;
                            return;
                        }
                        level += p.level();
                    }
                    if (check) {
                        myChar.service.alertMessage("Có thành viên trong tổ đội đã tham gia một cấm thuật khác");
                        return;
                    }
                    if (!checkLuot) {
                        myChar.service.alertMessage("Có thành viên trong tổ đội không đủ lượt tham gia cấm thuật");
                        return;
                    }
                    if (!checkLevel) {
                        myChar.service.alertMessage("Có thành viên trong tổ đội không đủ level gia cấm thuật");
                        return;
                    }
                    if (myChar.getGroup().memberGroups.get(0).charId == myChar.id) {
                        level = level / chars.size();
                        CamThuat camThuat = new CamThuat(360, level);
                        CamThuat.addCamThuat(camThuat);
                        for (Char p : chars) {
                            p.Info.countCamThuat--;
                            camThuat.addCharId(p.id);
                        }
                        if (camThuat != null) {
                            if (camThuat.isClosed()) {
                                return;
                            }
                            for (Char p : chars) {
                                if (p.idCamThuat == -1) {
                                    p.idCamThuat = camThuat.getId();
                                }
                                p.addWorld(camThuat);
                                camThuat.join(p);
                            }

                        }

                    } else {
                        myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không phải đội trưởng", HanderMessage.WHITE));
                    }
                } else {
                    myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không có tổ đội", HanderMessage.WHITE));
                }
//
                break;
            case 4:
                myChar.service.openMsg122((byte) 65);
                break;
            case 5:
                myChar.service.openMsg122((byte) 66);
                break;
        }
    }

    private static void _SelectTerumi(Char myChar, byte index1, byte index2) {
        switch (index1) {
            case 0:
                switch (index2){
                    case 0:
                        myChar.orderTaskDay();
                        break;
                    case 1:
                        myChar.cancelTaskDay();
                        break;
                    case 2:
                        myChar.finishTaskDay();
                        break;
                }
                break;
        }
    }
    private static void _SelectFukasaku(Char myChar, byte index1, byte index2) {
        switch (index1) {
            case 0:
                myChar.service.openMsg122((byte) 64);
                break;
            case 1:
                myChar.service.openMsg122((byte) 67);
                break;
            case 2:
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                // Calendar.SUNDAY is 1, Calendar.MONDAY is 2, ..., Calendar.SATURDAY is 7
//                if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.WEDNESDAY || dayOfWeek == Calendar.FRIDAY) { tạm thời tắt đang test
//                    if ((hour >= 21 && minute >= 30) && (hour <= 22 && minute <= 40)) {
//                    } else {
//                        myChar.service.alertMessage("Chưa đến thời gian");
//                        return;
//                    }
//                }
                SonCapMyo sonCap = SonCapMyo.findSonCapByCharId(myChar.id);
                if(sonCap!=null){
                    if(sonCap.isClosed()){
                        myChar.service.serverMessage("Phó bản sơn cáp đã kết thúc" +
                                "");
                        return;
                    }
                    myChar.addWorld(sonCap);
                    sonCap.addMember(myChar);
                    sonCap.joinZone(myChar,94);
                }
                if (myChar.getGroup() != null) {
                    boolean check = false;
                    boolean checkLuot = true;
                    boolean checkLevel = true;
                    int level = 0;
                    List<Char> chars = myChar.getGroup().getCharsInZone(myChar.zone.map.mapID, myChar.zone.zoneID);
                    if (chars.size() < myChar.getGroup().getChars().size()) {
                        myChar.service.alertMessage("Vui lòng tập hợp đủ thành viên lại");
                        return;
                    }
                    if (myChar.level() < 40) {
                        myChar.service.alertMessage("Ban không đủ level gia Sơn cáp ");
                        return;
                    }
                    for (Char p : chars) {
                        if (SonCapMyo.isInSonCap(p.id)) {
                            check = true;
                            return;
                        }
                        if (p.level() < 40) {
                            checkLevel = false;
                            return;
                        }
                        level += p.level();
                    }
                    if (check) {
                        myChar.service.alertMessage("Có thành viên trong tổ đội đã tham gia một Sơn cáp khác");
                        return;
                    }
                    if (!checkLevel) {
                        myChar.service.alertMessage("Có thành viên trong tổ đội không đủ level gia Sơn cáp");
                        return;
                    }
                    if (myChar.getGroup().memberGroups.get(0).charId == myChar.id) {
                        level = level / chars.size();
                        SonCapMyo sonCapMyo = new SonCapMyo(level);
                        SonCapMyo.addSonCap(sonCapMyo);
                        if (sonCapMyo != null) {
                            if (sonCapMyo.isClosed()) {
                                return;
                            }
                            for (Char p : chars) {
                                p.addWorld(sonCapMyo);
                                sonCapMyo.addMember(p);
                                sonCapMyo.joinZone(p,94);
                            }
                        }

                    } else {
                        myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không phải đội trưởng", HanderMessage.WHITE));
                    }
                } else {
                    myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không có tổ đội", HanderMessage.WHITE));
                }
                break;
        }
    }

//    private static void NhapHoc(Char _myChar, int idNpc) {
//        if (_myChar.Info.idTask == 8 && _myChar.Info.idStep == 8 && idNpc == _myChar.getThayFormId()) {
//            if (_myChar.Bag.arrItemBody[1] != null) {
//                _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Vui lòng cất vũ khí trước khi nhập học", HanderMessage.WHITE));
//                return;
//            }
//            _myChar.user.session.sendMessage(HanderMessage.SendTaskStep(_myChar.Info.idStep));
//            _myChar.Info.idStep++;
//            _myChar.sendTask();
//            _myChar.Info.idClass = _myChar.getLopFormSelectChar(_myChar.Info.idChar);
//            _myChar.service.sendChar();
//            _myChar.Skill = new InfoSkill(_myChar.Info.idClass);
//            _myChar.msgUpdateDataChar();
//            _myChar.msgUpdateSkill();
//            Item vk = new Item(IdVK1x(_myChar.Info.idClass));
//            vk.he = _myChar.Info.idhe;
//            Item.setOptionsVuKhiToBag(vk, _myChar.Info.idClass);
//            vk.isLock = true;
//            _myChar.addItem(vk);
//            _myChar.msgAddItemBag(vk);
//        }
//    }
//

    private static void OpenMenu(Char _myChar, String text) {
        try {
            Writer wr = new Writer();
            wr.writeShort(0);
            wr.writeUTF(text.toString());
            _myChar.service.openNpc(wr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void _SelectNpc9(Char _myChar, byte select) {
        switch (select) {
            case 0:
                _myChar.user.session.sendMessage(HanderMessage.SendCreateGiaToc());
                break;
        }
    }

    private static void _SelectNgoaiTrang(Char _myChar, byte select) {
        switch (select) {
            case 0:
                ClickEvent.ShopTrangBi(_myChar, (byte) 36, _myChar.Info.idhe);
                break;
            case 1:
                ClickEvent.Shop(_myChar, (byte) 18);
                break;
            case 2:
                ClickEvent.Shop(_myChar, (byte) 30);
                break;
            case 3:
                ClickEvent.Shop(_myChar, (byte) 19);
                break;
            case 4:
                ClickEvent.Shop(_myChar, (byte) 37);
                break;
            case 5:
                ClickEvent.Shop(_myChar, (byte) 38);
                break;
        }
    }

    private static void _SelectTrangPhuc(Char _myChar, byte select) {
        switch (select) {
            case 0:
                ClickEvent.ShopTrangBi(_myChar, (byte) 21, _myChar.Info.idhe);
                break;
            case 1:
                ClickEvent.ShopTrangBi(_myChar, (byte) 22, _myChar.Info.idhe);
                break;
            case 2:
                ClickEvent.ShopTrangBi(_myChar, (byte) 23, _myChar.Info.idhe);
                break;
            case 3:
                ClickEvent.ShopTrangBi(_myChar, (byte) 24, _myChar.Info.idhe);
                break;
            case 4:
                ClickEvent.ShopTrangBi(_myChar, (byte) 25, _myChar.Info.idhe);
                break;
        }
    }

    private static void _SelectHokage(Char _myChar, byte select) {
        switch (select) {
            case 0:
                ClickEvent.ShopTrangBi(_myChar, (byte) 8, _myChar.Info.idhe);
                break;
            case 1:
                ClickEvent.ShopTrangBi(_myChar, (byte) 9, _myChar.Info.idhe);
                break;
            case 2:
                ClickEvent.ShopTrangBi(_myChar, (byte) 10, _myChar.Info.idhe);
                break;
            case 3:
                ClickEvent.ShopTrangBi(_myChar, (byte) 11, _myChar.Info.idhe);
                break;
            case 4:
                ClickEvent.ShopTrangBi(_myChar, (byte) 12, _myChar.Info.idhe);
                break;
            case 5:
                ClickEvent.ShopTrangBi(_myChar, (byte) 13, _myChar.Info.idhe);
                break;
            case 6:
                ClickEvent.ShopTrangBi(_myChar, (byte) 14, _myChar.Info.idhe);
                break;
            case 7:
                ClickEvent.ShopTrangBi(_myChar, (byte) 15, _myChar.Info.idhe);
                break;
            case 8:
                ClickEvent.ShopTrangBi(_myChar, (byte) 16, _myChar.Info.idhe);
                break;
            case 9:
                ClickEvent.ShopTrangBi(_myChar, (byte) 17, _myChar.Info.idhe);
                break;
        }
    }

    private static void _SelectPhuKien(Char _myChar, byte select) {
        switch (select) {
            case 0:
                ClickEvent.ShopTrangBi(_myChar, (byte) 26, _myChar.Info.idhe);
                break;
            case 1:
                ClickEvent.ShopTrangBi(_myChar, (byte) 27, _myChar.Info.idhe);
                break;
            case 2:
                ClickEvent.ShopTrangBi(_myChar, (byte) 28, _myChar.Info.idhe);
                break;
            case 3:
                ClickEvent.ShopTrangBi(_myChar, (byte) 29, _myChar.Info.idhe);
                break;
        }
    }

    private static void _SelectRuongDo(Char _myChar, byte select) {
        switch (select) {
            case 0:
                ClickEvent.OpenBox(_myChar);
                break;
            case 1:
                _myChar.Info.mapReSpawm = _myChar.Info._mapID;
                _myChar.service.alertMessage("Đã lưu vị trí");
                if (_myChar.taskId == TaskName.NV_TRO_GIUP_LANG_SUONG_MU&&_myChar.taskMain != null && _myChar.taskMain.index == 0){
                    _myChar.taskNext();
                }
                break;
        }
    }

    private static void _SelectDuocPham(Char _myChar, byte select, byte index2) {
        switch (select) {
            case 0:
                ClickEvent.ShopDuocPham(_myChar, 0);
                break;
            case 1:
                ClickEvent.ShopDuocPham(_myChar, 1);
                break;
            case 2:
                switch (index2) {
                    case 0:
                    case 1:
                        if (_myChar.idKhuLuyenTap != -1) {
                            Training training = Training.findTrainingById(_myChar.idKhuLuyenTap);
                            if (training != null) {
                                if (training.isClosed()) {
                                    _myChar.idKhuLuyenTap = -1;
                                    return;
                                }
                                _myChar.addWorld(training);
                                training.addMember(_myChar);
                                training.join(_myChar);
                                return;
                            }
                        }
                        if (_myChar.getGroup() != null) {
                            if (index2 == 0 && _myChar.getGroup().getChars().size() > 3) {
                                _myChar.service.alertMessage("Vui lòng chọn lại vì số lượng thành viên không đủ yêu cầu( Trên 3 thành viên không thể chọn tổi đội 3)");
                                return;
                            }
                            List<Char> chars = _myChar.getGroup().getCharsInZone(_myChar.zone.map.mapID, _myChar.zone.zoneID);
                            if (chars.size() < _myChar.getGroup().getChars().size()) {
                                _myChar.service.alertMessage("Vui lòng tập hợp đủ thành viên lại. Nếu đủ người vẫn bị lỗi thì hãy thử chuyển khu vực khác");
                                return;
                            }
                            if (!_myChar.getGroup().checkLevelDifference()) {
                                _myChar.service.alertMessage("Có thành viên trong tổ đội cấp độ chênh lệch quá lớn (Chênh lệch không quá 5 level thì mới có thể vào khu luyện tập)");
                                return;
                            }
                            int level = 0;
                            for (Char p : chars) {
                                if (p.idKhuLuyenTap != -1) {
                                    _myChar.service.alertMessage("Thành viên " + p.Info.name + " trong tổ đội đã tham gia một khu luyện tập khác");
                                    Training klt = Training.findTrainingById(p.idKhuLuyenTap);
                                    if(klt==null||klt.isClosed())
                                        p.idKhuLuyenTap = -1;
                                    return;
                                }
                                if (Manager.gI().checkPlayer(p.id)) {
                                    _myChar.service.alertMessage("Thành viên " + p.Info.name + " đã hết lượt vào khu luyện tập");
                                    return;
                                }
                                level += p.level();
                            }
                            level = level / chars.size();
                            if (_myChar.getGroup().memberGroups.get(0).charId == _myChar.id) {
                                Training training = new Training(level, index2 == 0 ? 100 : 75);
                                if (training != null) {
                                    Training.addTraining(training);
                                    if (training.isClosed()) {
                                        return;
                                    }
                                    for (Char p : chars) {
                                        p.idKhuLuyenTap = training.getId();
                                        p.addWorld(training);
                                        training.addMember(p);
                                        training.join(p);
                                        Manager.gI().addPlayer(p.id);
                                    }
                                }
                            } else {
                                _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không phải đội trưởng", HanderMessage.WHITE));
                            }
                        }else {
                            _myChar.getService().alertMessage("Bạn không có tổ đội");
                        }
                        break;
                }
                break;
        }
    }

    private static void _SelectQuanAn(Char _myChar, byte select, byte index2) {
        switch (select) {
            case 0:
                ClickEvent.ShopQuanAn(_myChar, 4);
                break;
            case 1:
                ClickEvent.ShopQuanAn(_myChar, 5);
                break;
            case 2:
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                DeadForest forest = null;
                int level = _myChar.level();

                if (minute >= 50 && minute <= 59 && (hour == 6 || hour == 9 || hour == 12 || hour == 15 || hour == 18)) {
                    if (level >= 20 && level <= 69) {
                        if (level >= 20 && level <= 29) forest = DeadForest.DeadForest_2x;
                        else if (level >= 30 && level <= 39) forest = DeadForest.DeadForest_3x;
                        else if (level >= 40 && level <= 49) forest = DeadForest.DeadForest_4x;
                        else if (level >= 50 && level <= 59) forest = DeadForest.DeadForest_5x;
                        else if (level >= 60 && level <= 69) forest = DeadForest.DeadForest_6x;

                        if (forest != null) {
                            forest.addCharId(_myChar.id);
                            forest.addMember(_myChar);
                            _myChar.addWorld(forest);
                            forest.join(_myChar);
                        } else {
                            _myChar.service.serverMessage("Chưa tới thời gian báo danh");
                        }
                    }
                } else if ((hour == 7 || hour == 10 || hour == 13 || hour == 16 || hour == 19) && minute < 50) {
                    if (level >= 20 && level <= 69) {
                        if (level >= 20 && level <= 29) forest = DeadForest.DeadForest_2x;
                        else if (level >= 30 && level <= 39) forest = DeadForest.DeadForest_3x;
                        else if (level >= 40 && level <= 49) forest = DeadForest.DeadForest_4x;
                        else if (level >= 50 && level <= 59) forest = DeadForest.DeadForest_5x;
                        else if (level >= 60 && level <= 69) forest = DeadForest.DeadForest_6x;

                        if (forest != null && forest.isOpened() && forest.findCharId(_myChar.id)) {
                            forest.addMember(_myChar);
                            _myChar.addWorld(forest);
                            forest.join(_myChar);
                        } else {
                            _myChar.service.serverMessage("Đã hết thời gian báo danh");
                        }
                    }
                } else {
                    _myChar.service.serverMessage("Chưa tới thời gian báo danh");
                }
                break;
        }
    }

    private static void _SelectTapHoa(Char _myChar, byte select, byte index2) {
        switch (select) {
            case 0:
                ClickEvent.Shop(_myChar, (byte) 2);
                break;
            case 1:
                ClickEvent.Shop(_myChar, (byte) 3);
                break;
        }
    }

    private static void _SelectNpcThoRen(Char _myChar, byte select) {
        switch (select) {
            case 0:
                _myChar.service.openTabGhepDa();
                break;
            case 1:
                _myChar.service.openTabCuongHoa();
                break;
            case 2:
                _myChar.service.openMsg122((byte) 83);
                break;
            case 3:
                _myChar.service.openTabTachCuongHoa();
                break;
            case 4:
                _myChar.service.openTabDichChuyen();
                break;
            case 5:
                _myChar.service.openMsg122((byte) 87);
                break;
            case 6:
                _myChar.service.openTabTachKhamNgoc();
                break;
            case 7:
                _myChar.service.openTabCT();
                break;
            case 8:
                _myChar.service.openTabCT2();
                break;

        }
    }

    private static void _SelectPhu(Char _myChar, byte select, byte select2) {
        switch (select) {
            case 0:
                Map.maps[86].addChar(_myChar);
                break;
            case 1:
                switch (select2) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        short mapOder = (short) (select2 == 0 ? 75 : (select2 == 1 ? 60 : (select2 == 2 ? 69 : (select2 == 3 ? 85 : (select2 == 4 ? 59 : (select2 == 5 ? 68 : 104))))));
                        Map.maps[mapOder].addChar(_myChar);
                        break;
                }
                break;
        }

    }


    public static int getIdNpcFormMap(int idNpc, Char _myChar) {
        if(idNpc == -1)
            return -1;
        Npc npc = _myChar.zone.npcs.get(idNpc);
        if (npc != null) {
            return npc.id;
        }
        return -1;
    }


    public static int IdVK1x(int Idclass) {
        for (ItemTemplate item : DataCenter.gI().ItemTemplate) {
            if (item.idClass == Idclass && item.levelNeed == 10 && item.type == 1) {
                return item.id;
            }
        }
        return 0;
    }
}