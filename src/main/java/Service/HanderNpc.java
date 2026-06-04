/*



 * To change this license header, choose License Headers in Project Properties.



 * To change this template file, choose Tools | Templates



 * and open the template in the editor.



 */
package Service;

import Data.DataMenuNpc;

import EventClick.ClickEvent;

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

import com.sg188.lib.Log;

import com.sg188.real.*;

import com.sg188.server.lib.Writer;

import com.sg188.task.TaskName;

import java.io.IOException;

import java.util.Calendar;

import java.util.List;

import java.util.logging.Level;

import java.util.logging.Logger;

/**
 *
 *
 *
 * @author ADMIN
 *
 *
 *
 */
public class HanderNpc {

    private static final java.util.Map<Integer, Short> npcMenuIndexByChar = new java.util.HashMap<>();

    private static final java.util.Map<Integer, Byte> onokiSubMenuByChar = new java.util.HashMap<>();

    static final String[] oderPhu = {"Làng lá,75", "Làng sương mù,60", "Làng mây,69", "Làng đá,85", "Làng cát,59", "Làng cỏ,68", "Làng mưa,102"};

//                                        75           60           69         85                 59        68 102
    public static void OpenMenu(Char _myChar, short idNpc) {

        try {

            short idNpcReal = (short) getIdNpcFormMap(idNpc, _myChar);

            if (idNpcReal == 32) {

                npcMenuIndexByChar.put(_myChar.id, idNpc);

                onokiSubMenuByChar.remove(_myChar.id);

            }

            String textNpc = DataMenuNpc.textNpc(idNpcReal);

            if (textNpc == null) {

                textNpc = "";

            }

            if (idNpcReal == 98 && idNpc == 0) {

                textNpc = "Đặt cược;Rời khỏi nơi này";

            }

            if (idNpcReal == 73) {

                if (Event.getEvent() != null) {

                    textNpc = Event.getEvent().menuKhaTienNu;

                } else {

                    textNpc = DataMenuNpc.textNpc(idNpcReal);

                }

            }

            if (idNpcReal == 97) {

                textNpc = "Thu vàng lấy bạc;Thu vàng lấy bạc Vip;Vĩ Thuật;Khóa/mở khóa cấp";

            }

            if (idNpcReal == 57) {

                textNpc = HanderNpc.getMenuNpc57();

            }

            if (idNpcReal == 31 && _myChar.zone.map.mapID == 67) {

                _myChar.service.sendTextNPC("Ta đã một mình tới đây để triệt phá ổ nhóm của Pain. Không ngờ chúng nó lại đông đến vậy. Các con hãy giúp ta xử lý chúng! ", "");

                return;

            }

            int taskNpcId = _myChar.getTaskNpcId();

            if (taskNpcId != -1 && idNpcReal == taskNpcId) {

                if (_myChar.menuTask()) {
                    return;
                }

                if (_myChar.taskId == 20 && _myChar.taskMain != null && _myChar.taskMain.index == 1) {

                    textNpc = "Thi lý thuyết";

                }

            }

            if (idNpcReal == 32) {

                textNpc = "Đại hội nhẫn giả;Đại hội nhẫn giả III;Gia tộc;Cấm thuật Izanami;Trang bị Sharingan;Trang bị Byakugan;Liên Server";

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

                    joinDeadForestQuick(_myChar);

                    break;

                case 3:

                    joinDaiHoiNhanGiaTest(_myChar);

                    break;

                case 4:

                    joinDaiChienNhanGiaTest(_myChar, true);

                    break;

                case 5:

                    joinDaiChienNhanGiaTest(_myChar, false);

                    break;

                case 99:

                    if (DaiHoiNhanGia.DAI_HOI_NHAN_GIA != null) {

                        if (DaiHoiNhanGia.DAI_HOI_NHAN_GIA.isOpened) {

                            DaiHoiNhanGia.DAI_HOI_NHAN_GIA.playerJoin(true, _myChar);

                        } else {

                            DaiHoiNhanGia.DAI_HOI_NHAN_GIA.playerJoin(false, _myChar);

                        }

                    } else {
                        _myChar.service.serverMessage("Đại hội chưa mở");
                    }

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

                if (_myChar.taskId == 20 && _myChar.taskMain != null && _myChar.taskMain.index == 1) {

                    _myChar.anserChunnin();

                } else {
                    ClickEvent.ShopTrangBi(_myChar, (byte) 20, _myChar.Info.idhe);
                }

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

                _SelectOnoki(_myChar, indexNpc, index1, index2);

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

            case 57:

                HanderNpc.handleMenuNpc57(_myChar, index1, index2);

                break;

            case 73:

                if (Event.getEvent() != null) {

                    Event.getEvent().menu(_myChar, index1, index2);

                } else {

                    _selectTsunade(_myChar, index1, index2);

                }

                break;

            case 105:

                _SelectFukasaku(_myChar, index1, index2);

                break;

            case 30:

                _selectTsunade(_myChar, index1, index2);

                break;

            case 74:

                _SelectThanThu(_myChar, index1, index2);

                break;

            case 78:

                _SelectKyLan(_myChar, index1, index2);

                break;

        }

    }

    private static void _SelectRaiKaGe(Char myChar, byte index1, byte index2) {

        int level = myChar.level();

        int id = 0;

        switch (index1) {

            case 0:

                if (!Manager.gI().reciveKey.containsKey(myChar.Info.name)) {

                    if (level >= 15 && level <= 29) {
                        id = 244;
                    } else if (level > 29 && level < 50) {
                        id = 245;
                    } else if (level > 49) {
                        id = 246;
                    }

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

                int requiredLevelMax = index1 == 1 ? 29 : index1 == 2 ? 49 : 70;

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

                                if (p.idDiaCung != -1 || p.level() < requiredLevel || p.level() > requiredLevelMax
                                        || p.zone.zoneID != myChar.zone.zoneID || p.Info._mapID != myChar.Info._mapID || p.FindItemBag(itemrequi) == null) {

                                    check = true;

                                    if (p.idDiaCung != -1) {
                                        myChar.service.alertMessage("Có thành viên" + p.Info.name + " trong tổ đội đã tham gia một địa cung khác");
                                    } else if (p.level() < requiredLevel || p.level() > requiredLevelMax) {
                                        myChar.service.alertMessage("Có thành viên" + p.Info.name + " trong tổ đội không đủ điều kiện tham gia");
                                    } else if (p.FindItemBag(itemrequi) == null) {
                                        myChar.service.alertMessage("Có thành viên " + p.Info.name + " trong tổ đội không đủ chìa khoá");
                                    }

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

                                    if (p != null && p.user != null && p.taskId == TaskName.NV_BAT_DAU_THU_THACH) {

                                        if (p.taskMain != null && p.taskMain.index == 0) {

                                            p.taskNext();

                                        }

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

        if (myChar.zone.isLoiDai()) {

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

        } else {

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

                        myChar.Bag.arrItemBody[10].strOptions = "305,0,190000;" + myChar.Bag.arrItemBody[10].strOptions;

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

                        if (vithu.checkSucManh() == 190000) {

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

                            bijuu.strOptions = "305,0,190000;" + bijuu.strOptions;

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
                    switch (index2) {

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

                SelectCard.getInstance().open(myChar);

                break;

            case 1:

                myChar.isWheelSilver = false; // thử vận may 200 vàng

                myChar.isWheelGold = true;

                HanderClickEvent.thuvanmay(myChar, (byte) 74);

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

            case 4:

                myChar.BiKipHienNhan();

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

                myChar.isWheelSilver = false; // thử vận may 200 vàng

                myChar.isWheelGold = true;

                HanderClickEvent.thuvanmay(myChar, (byte) 74);

                break;

            case 1:

                switch (index2) {

                    case 0:

                        SelectCard.getInstance().open(myChar);

                        //myChar.service.alertMessage("Xóa hành trang thành công!");
                        break;

                    case 1:

                        String[] cauNoi = {
                            "Con me may muon choi tao a (negav)",
                            "Cho bố mày cái địa chỉ (đầu cắt moi) ",
                            "Trên đời này có làm thì mới có ăn (huấn rose)",
                            "Không cho kẹo cút đi đánh boss (admin)",
                            "Cần Cù Thì Bù Siêng Năng ( Huấn Rose )",
                            " Thầy Nhớ Diễm My Quá Đi À ( thầy ông nội ) "

                        };

                        int randomIndex = (int) (Math.random() * cauNoi.length);

                        myChar.service.alertMessage(cauNoi[randomIndex]);

                        break;

                }

                break;

//                SelectCard.getInstance().open(myChar);
//                break;
            case 2: // làm vé vô cực

                switch (index2) {

                    case 0:

                        for (int i = 0; i < myChar.Bag.arrItemBag.length; i++) {

                            myChar.Bag.arrItemBag[i] = null;

                        }

                        myChar.service.alertMessage("Xóa hành trang thành công!");

                        break;

                    case 1:

                        break;

                }

                break;

//            case 3:
//                switch (index2) {
//                    case 0:
//                        try {
//                            if(myChar.Bag.bacKhoa >= 2000000000) {
//                                myChar.service.alertMessage("Full bạc khóa!");
//                                return;
//                            }
//                            if(myChar.Info.chuyenCan < 3000) {
//                                myChar.service.alertMessage("Không đủ 3000 chuyên cần!");
//                                return;
//                            }
//                            myChar.Info.chuyenCan -= 3000;
//                            myChar.addBacKhoa(200000000);
//
//                        } catch (Exception ex) {
//
//                        }
//                        break;
//                    case 1:
//                        try {
//                            if(myChar.getCountNullItemBag() < 0) {
//                                myChar.service.alertMessage("Túi đầy!");
//                                return;
//                            }
//                            if(myChar.Info.chuyenCan < 2000) {
//                                myChar.service.alertMessage("Không đủ 2000 chuyên cần!");
//                                return;
//                            }
//                            myChar.Info.chuyenCan -= 2000;
//                            Item danhHieuCH = new Item(11);
//                            danhHieuCH.isLock = true;
//                            myChar.addItem(danhHieuCH);
//                            myChar.msgAddItemBag(danhHieuCH);
//                        } catch (Exception ex) {
//
//                        }
//                        break;
//                    case 2:
//                        try {
//                            if(myChar.getCountNullItemBag() < 0) {
//                                myChar.service.alertMessage("Túi đầy!");
//                                return;
//                            }
//                            if(myChar.Info.chuyenCan < 2100) {
//                                myChar.service.alertMessage("Không đủ 2100 chuyên cần!");
//                                return;
//                            }
//                            myChar.Info.chuyenCan -= 2100;
//                            Item danhHieuTK = new Item(11);
//                            danhHieuTK.isLock = true;
//                            myChar.addItem(danhHieuTK);
//                            myChar.msgAddItemBag(danhHieuTK);
//                        } catch (Exception ex) {
//
//                        }
//                        break;
//                    case 3:
//                        try {
//                            if(myChar.getCountNullItemBag() < 0) {
//                                myChar.service.alertMessage("Túi đầy!");
//                                return;
//                            }
//                            if(myChar.Info.chuyenCan < 2200) {
//                                myChar.service.alertMessage("Không đủ 2200 chuyên cần!");
//                                return;
//                            }
//                            myChar.Info.chuyenCan -= 2200;
//                            Item danhHieuUV = new Item(11);
//                            danhHieuUV.isLock = true;
//                            myChar.addItem(danhHieuUV);
//                            myChar.msgAddItemBag(danhHieuUV);
//                        } catch (Exception ex) {
//
//                        }
//                        break;
//                }
            //myChar.service.alertMessage("Xóa hành trang thành công!");
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
                myChar.service.alertMessage("Số tiền hiện có: " + myChar.user.coin);
                break;

            case 2: // MỞ KHÓA: Thử vận may Bạc (Thường) - Giá 1 Tr Bạc
                myChar.isWheelSilver = true;
                myChar.isWheelGold = false;
                HanderClickEvent.thuvanmaySilver(myChar, (byte) 74);
                break;

            case 3: // MỞ KHÓA: Thử vận may Vàng (VIP) - Giá 500 Vàng
                myChar.isWheelSilver = false;
                myChar.isWheelGold = true;
                HanderClickEvent.thuvanmay(myChar, (byte) 74);
                break;
            case 4:  // Đổi đủ 5 Cải trang Anbu (647, 648, 649, 650, 651) thành Cải trang Thủ lĩnh Anbu (775)
                Item anbu647 = myChar.FindItemBag(647);
                Item anbu648 = myChar.FindItemBag(648);
                Item anbu649 = myChar.FindItemBag(649);
                Item anbu650 = myChar.FindItemBag(650);
                Item anbu651 = myChar.FindItemBag(651);

                // Kiểm tra đủ 5 loại cải trang Anbu
                StringBuilder thieu = new StringBuilder();
                if (anbu647 == null) {
                    thieu.append("Cải trang Anbu Gấu, ");
                }
                if (anbu648 == null) {
                    thieu.append("Cải trang Anbu Cọp, ");
                }
                if (anbu649 == null) {
                    thieu.append("Cải trang Anbu Chim, ");
                }
                if (anbu650 == null) {
                    thieu.append("Cải trang Anbu Gà, ");
                }
                if (anbu651 == null) {
                    thieu.append("Cải trang Anbu Dê, ");
                }

                if (thieu.length() > 0) {
                    // Xóa dấu ", " cuối cùng
                    String thieuStr = thieu.substring(0, thieu.length() - 2);
                    myChar.service.alertMessage("Bạn thiếu: " + thieuStr);
                    myChar.service.serverMessage(myChar.Info.name + " thiếu: " + thieuStr);
                    return;
                }

                // Kiểm tra vàng
                if (myChar.Bag.vang < 10000) {
                    myChar.service.alertMessage("Cần 10.000 vàng để đổi!");
                    return;
                }

                // Kiểm tra hành trang có trống không
                if (myChar.getCountNullItemBag() < 1) {
                    myChar.service.alertMessage("Hành trang không đủ chỗ trống!");
                    return;
                }

                // Trừ vàng
                myChar.addVang(-10000);

                // Xóa 5 item cũ
                myChar.removeItem(anbu647);
                myChar.msgUseItemBag(anbu647);
                myChar.removeItem(anbu648);
                myChar.msgUseItemBag(anbu648);
                myChar.removeItem(anbu649);
                myChar.msgUseItemBag(anbu649);
                myChar.removeItem(anbu650);
                myChar.msgUseItemBag(anbu650);
                myChar.removeItem(anbu651);
                myChar.msgUseItemBag(anbu651);

                // Tạo item mới với option
                Item newItem_775 = new Item(775);
                newItem_775.addItemOption(new ItemOption(209, 100)); // Tăng Chakra: +100
                newItem_775.addItemOption(new ItemOption(332, 8));   // Giảm 5 loại hiệu ứng: -8
                newItem_775.addItemOption(new ItemOption(167, 50));   // Tăng Chính Xác: +50
                newItem_775.isLock = true;
                newItem_775.createItemOptions();

                // Thêm item mới
                myChar.addItem(newItem_775);
                myChar.msgAddItemBag(newItem_775);
                myChar.service.alertMessage("Đổi thành công Cải trang Thủ lĩnh Anbu!");
                myChar.service.serverMessage(myChar.Info.name + " đổi 5 Cải trang Anbu Thành Cải trang Thủ lĩnh Anbu");
                break;

            case 5: // Chuyển tính năng Đổi điểm chuyên cần sang case 4 để không trùng menu
                switch (index2) {
                    case 0:
                    try {
                        if (myChar.Info.chuyenCan < 4000) {
                            myChar.service.alertMessage("Không đủ 4000 chuyên cần!");
                            return;
                        }
                        myChar.Info.chuyenCan -= 4000;
                        myChar.addBacKhoa(1000000000); // 1 Tỷ bạc khóa
                    } catch (Exception ex) {
                        // Log hoặc xử lý lỗi nếu cần
                    }
                    break;

                    case 1:
                    try {
                        if (myChar.Info.chuyenCan < 1000) {
                            myChar.service.alertMessage("Không đủ 1000 chuyên cần!");
                            return;
                        }
                        myChar.Info.chuyenCan -= 1000;
                        myChar.addBac(100000); // 100k bạc thường
                    } catch (Exception ex) {
                        // Log hoặc xử lý lỗi nếu cần
                    }
                    break;
                }
                break;
        }
    }

    public static void _SelectKyLan(Char myChar, byte index1, byte index2) {

        switch (index1) {

            case 0:

                myChar.Info.khoaExp = !myChar.Info.khoaExp;

                String str = myChar.Info.khoaExp ? "Khoá cấp thành công" : "Đã mở khoá cấp";

                String str2;

                if (str == "Khoá cấp thành công") {

                    str2 = " (Lúc này bạn không thể nhận Exp)";

                } else {

                    str2 = " (Lúc này bạn có thể nhận Exp)";

                }

                myChar.service.alertMessage(str + str2);

                break;

            case 1:

//                Item veVip = myChar.FindItemBag(741);
//                if (veVip == null) {
//                    myChar.service.alertMessage(" Vui Lòng Nhận Vé Thiên Đạo Sơ ");
//                    return;
//                }
//                if (veVip.amount < 1) {
//                    myChar.service.alertMessage("Bạn Không có Vé Vào Làng Cổ bạn có thể nhận quà hàng ngày hoặc săn boss");
//                    return;
//                }
                myChar.service.openMsg122((byte) 103);

                break;

            case 2:

//                myChar.service.alertMessage(" Chưa Tày Đâu");
                myChar.service.openMsg122((byte) 104);

                break;

            case 3:

                switch (index2) {

                    case 0:

                        for (int i = 0; i < myChar.Bag.arrItemBag.length; i++) {

                            myChar.Bag.arrItemBag[i] = null;

                        }

                        myChar.service.alertMessage("Xóa hành trang thành công!");

                        break;

                    case 1:

                        break;

                }

                break;

            case 4:

                // myChar.service.alertMessage("Tắt Làng Cổ");
                Item veVanMayVip = myChar.FindItemBag(919);

                if (veVanMayVip == null) {

                    myChar.service.alertMessage(" Bạn Không có Vé Vào Làng Cổ bạn có thể nhận quà hàng ngày hoặc săn boss ");

                    return;

                }

                if (veVanMayVip.amount < 1) {

                    myChar.service.alertMessage("Bạn Không có Vé Vào Làng Cổ bạn có thể nhận quà hàng ngày hoặc săn boss");

                    return;

                }

                myChar.removeItemByAmount(veVanMayVip, 1);

                myChar.msgRemoveItemBag(veVanMayVip);

                MapHangViThu.gI().maps.get(0).addChar(myChar);

                break;

//                switch (index3) {
//                    case 0:
//                        try {
////                            if(myChar.Bag.bacKhoa >= 2000000000) {
////                                myChar.service.alertMessage("Full bạc khóa!");
////                                return;
////                            }
//                            if(myChar.Info.chuyenCan < 3000) {
//                                myChar.service.alertMessage("Không đủ 3000 chuyên cần!");
//                                return;
//                            }
//                            myChar.Info.chuyenCan -= 3000;
//                            myChar.addBacKhoa(2000000000);
//
//                        } catch (Exception ex) {
//
//                        }
//                        break;
//                    case 1:
//                        try {
////                            if(myChar.getCountNullItemBag() < 0) {
////                                myChar.service.alertMessage("Túi đầy!");
////                                return;
////                            }
//                            if(myChar.Info.chuyenCan < 1000) {
//                                myChar.service.alertMessage("Không đủ 1000 chuyên cần!");
//                                return;
//                            }
//                            myChar.Info.chuyenCan -= 1000;
//                            myChar.addBac(100000);
//                        } catch (Exception ex) {
//
//                        }
//                        break;
        }

    }

    public static void _SelectThanThu(Char myChar, byte index1, byte index2) {

        switch (index1) {

            case 0:

//                if (true) {
//                    myChar.service.alertMessage("Thử vận may Vàng");
//                    return;
//                }
                myChar.isWheelSilver = true;

                myChar.isWheelGold = false;

                HanderClickEvent.thuvanmaySilver(myChar, (byte) 74);

                break;

            case 1:

//                if (true) {
//                    myChar.service.alertMessage("Thử vận may VIP chưa mở");
//                    return;
//                }
                myChar.isWheelSilver = false;

                myChar.isWheelGold = true;

                HanderClickEvent.thuvanmay(myChar, (byte) 74);

                break;

            case 2:

                SelectCard.getInstance().open(myChar);

                break;

        }

    }

    // th van may ; thu van may ve vip ; chon card
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

                            _myChar.addBac(500000);

//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 600.000 bạc ", HanderMessage.WHITE));
                        }

                        break;

                    case 1:

                        if (_myChar.Bag.vang < 1000) {

                            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không Đủ Vàng ", HanderMessage.WHITE));

                            return;

                        } else {

                            _myChar.addVang(-1000);

                            _myChar.addBac(5000000);

//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 45.000.000 bạc khóa ", HanderMessage.WHITE));
                        }

                        break;
                    case 2:

                        if (_myChar.Bag.vang < 10000) {

                            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không Đủ Vàng ", HanderMessage.WHITE));

                            return;

                        } else {

                            _myChar.addVang(-1000);

                            _myChar.addBac(50000000);

//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 45.000.000 bạc khóa ", HanderMessage.WHITE));
                        }

                        break;

                    case 3:

                        if (_myChar.Bag.vang < 100) {

                            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không Đủ Vàng ", HanderMessage.WHITE));

                            return;

                        } else {

                            _myChar.addVang(-100);

                            _myChar.addBacKhoa(5000000);

//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 4.000.000 bạc khóa ", HanderMessage.WHITE));
                        }

                        break;

                    case 4:

                        if (_myChar.Bag.vang < 1000) {

                            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không Đủ Vàng ", HanderMessage.WHITE));

                            return;

                        } else {

                            _myChar.addVang(-1000);

                            _myChar.addBacKhoa(50000000);

//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 45.000.000 bạc khóa ", HanderMessage.WHITE));
                        }

                        break;

                    case 5:

                        if (_myChar.Bag.vang < 10000) { // open sửa

                            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không Đủ Vàng ", HanderMessage.WHITE));

                            return;

                        } else {

                            _myChar.addVang(-10000);

                            _myChar.addBacKhoa(600000000);

//                    _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 45.000.000 bạc khóa ", HanderMessage.WHITE));
                        }

                        break;

                }

                break;

            case 1:

                _myChar.service.openMsg122((byte) 78);

                break;

            case 2:

                _myChar.Info.khoaExp = !_myChar.Info.khoaExp;

                String strKhoa = _myChar.Info.khoaExp ? "Khoá cấp thành công" : "Đã mở khoá cấp";

                String strKhoa2 = _myChar.Info.khoaExp ? " (Lúc này bạn không thể nhận Exp)" : " (Lúc này bạn có thể nhận Exp)";

                _myChar.service.alertMessage(strKhoa + strKhoa2);

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

    private static void joinDaiChienNhanGiaTest(Char myChar, boolean is4x) {

        joinDaiHoiNhanGiaTest(myChar);

    }

    private static void joinDaiHoiNhanGiaTest(Char myChar) {

        if (DaiHoiNhanGia.DAI_HOI_NHAN_GIA == null || DaiHoiNhanGia.DAI_HOI_NHAN_GIA.isClosed()) {

            DaiHoiNhanGia.DAI_HOI_NHAN_GIA = new DaiHoiNhanGia();

            myChar.service.serverMessage("Đã mở Đại hội nhẫn giả để test.");

        }

        if (DaiHoiNhanGia.DAI_HOI_NHAN_GIA.isOpened) {

            DaiHoiNhanGia.DAI_HOI_NHAN_GIA.playerJoin(true, myChar);

        } else {

            DaiHoiNhanGia.DAI_HOI_NHAN_GIA.playerJoin(false, myChar);

        }

    }

    private static void joinDeadForestQuick(Char myChar) {

        if (myChar.countDeadForest > 30) {

            myChar.service.serverMessage("Đã hết lượt đi khu rừng chết");

            return;

        } else {

            myChar.countDeadForest++;

        }

        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        int minute = calendar.get(Calendar.MINUTE);

        DeadForest forest = null;

        int level = myChar.level();

        if (minute >= 50 && minute <= 59 && (hour == 6 || hour == 9 || hour == 12 || hour == 15 || hour == 18)) {

            if (level >= 20 && level <= 69) {

                if (level >= 20 && level <= 29) {
                    forest = DeadForest.DeadForest_2x;
                } else if (level >= 30 && level <= 39) {
                    forest = DeadForest.DeadForest_3x;
                } else if (level >= 40 && level <= 49) {
                    forest = DeadForest.DeadForest_4x;
                } else if (level >= 50 && level <= 59) {
                    forest = DeadForest.DeadForest_5x;
                } else if (level >= 60 && level <= 69) {
                    forest = DeadForest.DeadForest_6x;
                }

                if (forest != null) {

                    forest.addCharId(myChar.id);

                    forest.addMember(myChar);

                    myChar.addWorld(forest);

                    forest.join(myChar);

                } else {

                    myChar.service.serverMessage("Chưa tới thời gian báo danh");

                }

            } else {

                myChar.service.serverMessage("Level không phù hợp để vào Khu Rừng Chết");

            }

        } else if ((hour == 7 || hour == 10 || hour == 13 || hour == 16 || hour == 19) && minute < 50) {

            if (level >= 20 && level <= 69) {

                if (level >= 20 && level <= 29) {
                    forest = DeadForest.DeadForest_2x;
                } else if (level >= 30 && level <= 39) {
                    forest = DeadForest.DeadForest_3x;
                } else if (level >= 40 && level <= 49) {
                    forest = DeadForest.DeadForest_4x;
                } else if (level >= 50 && level <= 59) {
                    forest = DeadForest.DeadForest_5x;
                } else if (level >= 60 && level <= 69) {
                    forest = DeadForest.DeadForest_6x;
                }

                if (forest != null) {

                    if (forest.isOpened() && forest.findCharId(myChar.id)) {

                        forest.addMember(myChar);

                        myChar.addWorld(forest);

                        forest.join(myChar);

                    } else {

                        myChar.service.serverMessage("Đã hết thời gian báo danh");

                    }

                }

            } else {

                myChar.service.serverMessage("Level không phù hợp để vào Khu Rừng Chết");

            }

        } else {

            myChar.service.serverMessage("Chưa tới thời gian báo danh");

        }

    }

    private static void _SelectOnoki(Char myChar, int indexNpc, byte index1, byte index2) {

        Byte activeSubMenu = onokiSubMenuByChar.get(myChar.id);

        if (activeSubMenu != null && index2 < 0) {

            index2 = index1;

            index1 = activeSubMenu;

            onokiSubMenuByChar.remove(myChar.id);

        }

        switch (index1) {

            case 0:

//                Calendar calendar = Calendar.getInstance();
//                int hour = calendar.get(Calendar.HOUR_OF_DAY);
//                int minute = calendar.get(Calendar.MINUTE);
//                DaiChienNhanGia3 daiChienNhanGia3 = null;
//                switch (index2) {
//                    case 0:
//                        if (minute >= 30 && (hour == 23)) {
//                            daiChienNhanGia3 = DaiChienNhanGia3.BanDoanhLangLa;
//                            if (daiChienNhanGia3 != null) {
//                                daiChienNhanGia3.addCharId(myChar.id);
//                                daiChienNhanGia3.addMember(myChar);
//                                myChar.addWorld(daiChienNhanGia3);
//                                daiChienNhanGia3.join(myChar);
//                            } else {
//                                myChar.service.serverMessage("Chưa tới thời gian báo danh");
//                            }
//                        }
//                        else if ((hour == 7 || hour == 10 || hour == 13 || hour == 16 || hour == 19) && minute < 50) {
//                            if (daiChienNhanGia3 != null && daiChienNhanGia3.isOpened() && daiChienNhanGia3.findCharId(myChar.id)) {
//                                daiChienNhanGia3.addMember(myChar);
//                                myChar.addWorld(daiChienNhanGia3);
//                                daiChienNhanGia3.join(myChar);
//                            } else {
//                                myChar.service.serverMessage("Đã hết thời gian báo danh");
//                            }
//
//                        } else {
//                            myChar.service.serverMessage("Chưa tới thời gian báo danh");
//                        }
//                    case 1:
//                        if (minute >= 50 && (hour == 6 || hour == 9 || hour == 12 || hour == 15 || hour == 18)) {
//                            daiChienNhanGia3 = DaiChienNhanGia3.BanDoanhLangLa;
//                            if (daiChienNhanGia3 != null) {
//                                daiChienNhanGia3.addCharId(myChar.id);
//                                daiChienNhanGia3.addMember(myChar);
//                                myChar.addWorld(daiChienNhanGia3);
//                                daiChienNhanGia3.join(myChar);
//                            } else {
//                                myChar.service.serverMessage("Chưa tới thời gian báo danh");
//                            }
//                        }
//                        else if ((hour == 7 || hour == 10 || hour == 13 || hour == 16 || hour == 19) && minute < 50) {
//                            if (daiChienNhanGia3 != null && daiChienNhanGia3.isOpened() && daiChienNhanGia3.findCharId(myChar.id)) {
//                                daiChienNhanGia3.addMember(myChar);
//                                myChar.addWorld(daiChienNhanGia3);
//                                daiChienNhanGia3.join(myChar);
//                            } else {
//                                myChar.service.serverMessage("Đã hết thời gian báo danh");
//                            }
//
//                        } else {
//                            myChar.service.serverMessage("Chưa tới thời gian báo danh");
//                        }
//                }
                joinDaiHoiNhanGiaTest(myChar);

                break;

            case 1:

                joinDaiHoiNhanGiaTest(myChar);

                break;

            // case 2:

            //     joinDaiHoiNhanGiaTest(myChar);

            //     if (false && DaiHoiNhanGia.DAI_HOI_NHAN_GIA != null) {

            //         if (DaiHoiNhanGia.DAI_HOI_NHAN_GIA.isOpened) {

            //             DaiHoiNhanGia.DAI_HOI_NHAN_GIA.playerJoin(true, myChar);

            //         } else {

            //             DaiHoiNhanGia.DAI_HOI_NHAN_GIA.playerJoin(false, myChar);

            //         }

            //     } else if (false) {
            //         myChar.service.serverMessage("Đại hội chưa mở");
            //     }

            //     break;

            case 2:

                if (index2 < 0) {

                    npcMenuIndexByChar.put(myChar.id, (short) indexNpc);

                    onokiSubMenuByChar.put(myChar.id, (byte) 3);

                    OpenMenu(myChar, "Thành lập;Xin vào gia tộc;Mở cửa ải gia tộc;Vào ải gia tộc");

                    break;

                }

                switch (index2) {

                    case 0:

                        if (!myChar.user.actived) {

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

                            myChar.service.alertMessage("Người chơi " + p.Info.name + " đang ở trong cấm thuật");

                            return;

                        }

                        if (p.Info.countCamThuat < 1) {

                            myChar.service.alertMessage("Người chơi " + p.Info.name + " không đủ lượt tham gia cấm thuật");

                            checkLuot = false;

                            return;

                        }

                        if (p.level() < 40) {

                            myChar.service.alertMessage("Người chơi " + p.Info.name + " không đủ level tham gia cấm thuật");

                            checkLevel = false;

                            return;

                        }

                        level += p.level();

                    }

                    if (check) {

                        //   myChar.service.alertMessage("Có thành viên trong tổ đội đã tham gia một cấm thuật khác");
                        return;

                    }

                    if (!checkLuot) {

                        //  myChar.service.alertMessage("Có thành viên trong tổ đội không đủ lượt tham gia cấm thuật");
                        return;

                    }

                    if (!checkLevel) {

                        //     myChar.service.alertMessage("Có thành viên trong tổ đội không đủ level gia cấm thuật");
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

            case 6:

                myChar.service.serverMessage("Tính Năng đang được cập nhật");

                break;

        }

    }

    private static void _SelectTerumi(Char myChar, byte index1, byte index2) {

        switch (index1) {

            case 0:

                switch (index2) {

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

                if (sonCap != null) {

                    if (sonCap.isClosed()) {

                        myChar.service.serverMessage("Phó bản sơn cáp đã kết thúc"
                                + "");

                        return;

                    }

                    myChar.addWorld(sonCap);

                    sonCap.addMember(myChar);

                    sonCap.joinZone(myChar, 94);

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

                        myChar.service.alertMessage("Bạn không đủ cấp độ, yêu cầu level 40 trở lên");

                        return;

                    }

                    for (Char p : chars) {

                        if (SonCapMyo.isInSonCap(p.id)) {

                            myChar.service.alertMessage("Người chơi " + p.Info.name + " đang ở trong Sơn cáp");

                            check = true;

                            return;

                        }

                        if (p.level() < 40) {

                            myChar.service.alertMessage("Người chơi " + p.Info.name + " không đủ cấp độ, yêu cầu level 40 trở lên");

                            checkLevel = false;

                            return;

                        }

                        level += p.level();

                    }

                    if (check) {

                        //    myChar.service.alertMessage("Có thành viên trong tổ đội đã tham gia một Sơn cáp khác");
                        return;

                    }

                    if (!checkLevel) {

                        // myChar.service.alertMessage("Có thành viên trong tổ đội không đủ level gia Sơn cáp");
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

                                sonCapMyo.joinZone(p, 94);

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

            short npcIndex = npcMenuIndexByChar.getOrDefault(_myChar.id, (short) 0);

            wr.writeShort(npcIndex);

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

                if (_myChar.taskId == TaskName.NV_TRO_GIUP_LANG_SUONG_MU && _myChar.taskMain != null && _myChar.taskMain.index == 0) {

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

                                    if (klt == null || klt.isClosed()) {
                                        p.idKhuLuyenTap = -1;
                                    }

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

                        } else {

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

                if (_myChar.countDeadForest > 30) {//check lượt khu rừng chết

                    _myChar.service.serverMessage("Đã hết lượt đi khu rừng chết");

                    return;

                } else {

                    _myChar.countDeadForest++;

                }

                Calendar calendar = Calendar.getInstance();

                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                int minute = calendar.get(Calendar.MINUTE);

                DeadForest forest = null;

                int level = _myChar.level();

                if (minute >= 50 && minute <= 59 && (hour == 6 || hour == 9 || hour == 12 || hour == 15 || hour == 18)) {

                    if (level >= 20 && level <= 69) {

                        if (level >= 20 && level <= 29) {
                            forest = DeadForest.DeadForest_2x;
                        } else if (level >= 30 && level <= 39) {
                            forest = DeadForest.DeadForest_3x;
                        } else if (level >= 40 && level <= 49) {
                            forest = DeadForest.DeadForest_4x;
                        } else if (level >= 50 && level <= 59) {
                            forest = DeadForest.DeadForest_5x;
                        } else if (level >= 60 && level <= 69) {
                            forest = DeadForest.DeadForest_6x;
                        }

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

                        if (level >= 20 && level <= 29) {
                            forest = DeadForest.DeadForest_2x;
                        } else if (level >= 30 && level <= 39) {
                            forest = DeadForest.DeadForest_3x;
                        } else if (level >= 40 && level <= 49) {
                            forest = DeadForest.DeadForest_4x;
                        } else if (level >= 50 && level <= 59) {
                            forest = DeadForest.DeadForest_5x;
                        } else if (level >= 60 && level <= 69) {
                            forest = DeadForest.DeadForest_6x;
                        }

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

//            case 2:
//                // Giới hạn lượt
//                if(_myChar.countDeadForest > 30) {
//                    _myChar.service.serverMessage("Đã hết lượt đi khu rừng chết");
//                    return;
//                } else {
//                    _myChar.countDeadForest++;
//                }
//
//                // Lấy level
//                int level = _myChar.level();
//                DeadForest forest = null;
//
//                // Chọn DeadForest theo level
//                if (level >= 20 && level <= 29) forest = DeadForest.DeadForest_2x;
//                else if (level >= 30 && level <= 39) forest = DeadForest.DeadForest_3x;
//                else if (level >= 40 && level <= 49) forest = DeadForest.DeadForest_4x;
//                else if (level >= 50 && level <= 59) forest = DeadForest.DeadForest_5x;
//                else if (level >= 60 && level <= 69) forest = DeadForest.DeadForest_6x;
//
//                if (forest == null) {
//                    _myChar.service.serverMessage("Level không phù hợp để vào Khu Rừng Chết");
//                    return;
//                }
//
//                // Cho vào luôn, không cần báo danh - không cần giờ
//                forest.addCharId(_myChar.id);
//                forest.addMember(_myChar);
//                _myChar.addWorld(forest);
//                forest.join(_myChar);
//                break;
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

                if (_myChar.user != null && _myChar.user.isAdmin) {

                    _myChar.service.openTabDichChuyen();

                } else {

                    _myChar.service.serverMessage("Chi admin moi co the su dung dich chuyen nhanh.");

                }

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

        if (_myChar.user == null || !_myChar.user.isAdmin) {

            _myChar.service.serverMessage("Chi admin moi co the su dung dich chuyen nhanh.");

            return;

        }

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

        if (idNpc == -1) {
            return -1;
        }

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

    // ==================== NPC 57 - Câu cá cuối tuần ====================
    private static final String TOP_FISH_WEEKEND = "topfish_weekend";

    private static final int FISHING_MAP_ID = 85;

    private static final int FISHING_CY = 692;

    private static final int FISHING_ROD_SILVER = 499; // Cần câu bạc

    private static final int FISHING_ROD_GOLD = 500; // Cần câu vàng

    private static final int GIAY_KHEN_CAN_THU = 615; // Giấy khen cần thủ

    // Phần thưởng từ cần câu bạc (501-505: cá tre, lóc, mè, ngát, koi vàng - khóa)
    private static final int[] SILVER_ROD_REWARDS = {501, 502, 503, 504, 505};

    private static final int[] SILVER_ROD_PERCENTS = {40, 30, 20, 8, 2}; // Tổng 100%

    // Phần thưởng từ cần câu vàng (506-510: cá điêu hồng, rô phi, tai tượng, trảm cỏ, koi đỏ - không khóa)
    private static final int[] GOLD_ROD_REWARDS = {506, 507, 508, 509, 510};

    private static final int[] GOLD_ROD_PERCENTS = {40, 30, 20, 9, 1}; // Tổng 100%, koi đỏ 1%

    private static boolean isWeekendFishingActive = false;

    private static long weekendFishingEndTime = 0; // Thời gian kết thúc sự kiện (1 tiếng)

    public static String getMenuNpc57() {

        return "Nhận cần câu,Cần câu bạc (10k vàng),Cần câu vàng (50k vàng);Đổi cá,Cá tre (2k bạc khóa),Cá lóc (4k bạc khóa),Cá mè (6k bạc khóa + 1 giấy khen),Cá ngát (20k bạc khóa + 3 giấy khen),Cá koi vàng (200k bạc khóa + 30 giấy khen),Cá điêu hồng (2 vàng khóa + 2 giấy khen),Cá rô phi (5 vàng khóa + 4 giấy khen),Cá tai tượng (7 vàng khóa + 6 giấy khen),Cá trảm cỏ (15 vàng khóa + 10 giấy khen),Cá koi đỏ (100 vàng khóa + 100 giấy khen);Xem điểm;Bảng xếp hạng;Đổi cải trang AnBu (1tr bạc khóa + 1000 giấy khen);Đổi cải trang Văn Lang (5tr bạc khóa + 5000 giấy khen);Cải trang Yamato x10 (10tr bạc khóa + 10000 giấy khen);Sách tiềm năng cao (10tr bạc khóa + 10000 giấy khen);Sách kỹ năng cao (10tr bạc khóa + 10000 giấy khen)";

    }

    public static void activateWeekendFishing() {

        isWeekendFishingActive = true;

        weekendFishingEndTime = System.currentTimeMillis() + (60 * 60 * 1000); // 1 tiếng

    }

    public static void deactivateWeekendFishing() {

        isWeekendFishingActive = false;

        weekendFishingEndTime = 0;

    }

    private static boolean isWeekendFishingTime() {

        // Kiểm tra nếu đã hết thời gian sự kiện
        if (isWeekendFishingActive && System.currentTimeMillis() >= weekendFishingEndTime) {

            isWeekendFishingActive = false;

            weekendFishingEndTime = 0;

            com.sg188.server.Main.HeThongCTG("🎣 Hoạt động câu cá cuối tuần đã kết thúc! Hẹn gặp lại vào Chủ Nhật tuần sau!", 2);

        }

        if (isWeekendFishingActive) {

            return true;

        }

        Calendar now = Calendar.getInstance();

        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);

        int hour = now.get(Calendar.HOUR_OF_DAY);

        return dayOfWeek == Calendar.SUNDAY && hour == 11;

    }

    public static void handleMenuNpc57(Char p, int index, int index2) {

        switch (index) {

            case 0:

                // Nhận cần câu
                switch (index2) {

                    case 0:

                        buyFishingCane(p, FISHING_ROD_SILVER, 10000, "Cần câu bạc");

                        break;

                    case 1:

                        buyFishingCane(p, FISHING_ROD_GOLD, 50000, "Cần câu vàng");

                        break;

                }

                break;

            case 1:

                // Đổi cá
                switch (index2) {

                    case 0:

                        exchangeFish(p, 501, 2000, 0, 0, "Cá tre"); // 2k bạc khóa

                        break;

                    case 1:

                        exchangeFish(p, 502, 4000, 0, 0, "Cá lóc"); // 4k bạc khóa

                        break;

                    case 2:

                        exchangeFish(p, 503, 6000, 0, 1, "Cá mè"); // 6k bạc khóa + 1 giấy khen

                        break;

                    case 3:

                        exchangeFish(p, 504, 20000, 0, 3, "Cá ngát"); // 20k bạc khóa + 3 giấy khen

                        break;

                    case 4:

                        exchangeFish(p, 505, 200000, 0, 30, "Cá koi vàng"); // 200k bạc khóa + 30 giấy khen

                        break;

                    case 5:

                        exchangeFish(p, 506, 0, 2, 2, "Cá điêu hồng"); // 2 vàng khóa + 2 giấy khen

                        break;

                    case 6:

                        exchangeFish(p, 507, 0, 5, 4, "Cá rô phi"); // 5 vàng khóa + 4 giấy khen

                        break;

                    case 7:

                        exchangeFish(p, 508, 0, 7, 6, "Cá tai tượng"); // 7 vàng khóa + 6 giấy khen

                        break;

                    case 8:

                        exchangeFish(p, 509, 0, 15, 10, "Cá trảm cỏ"); // 15 vàng khóa + 10 giấy khen

                        break;

                    case 9:

                        exchangeFish(p, 510, 0, 100, 100, "Cá koi đỏ"); // 100 vàng khóa + 100 giấy khen

                        break;

                }

                break;

            case 2:

                // Xem điểm
                if (p.getEventPoint() == null) {

                    p.setEventPoint(new com.event.eventpoint.EventPoint());

                }

                int fishPoint = p.getEventPoint().getPoint(TOP_FISH_WEEKEND);

                int spendingPoint = p.getEventPoint().getPoint(com.event.eventpoint.EventPoint.DIEM_TIEU_XAI);

                p.getService().sendTextNPC("Bảng điểm SK của bạn",
                        "Điểm câu cá cuối tuần: " + fishPoint + "\nĐiểm tiêu sài: " + spendingPoint);

                break;

            case 3:

                // Bảng xếp hạng
                p.getService().serverMessage("Bảng xếp hạng đang được cập nhật");

            case 4:

                // Đổi cải trang AnBu - 1,000,000 bạc khóa + 1000 giấy khen
                exchangeCostume(p, 1000000, 1000, 514, "Cải trang AnBu");

                break;

            case 5:

                // Đổi cải trang Văn Lang x5 - 5,000,000 bạc khóa + 5000 giấy khen
                exchangeCostume(p, 5000000, 5000, 515, "Cải trang Văn Lang");

                break;

            case 6:

                // Cải trang Yamato x10 - 10,000,000 bạc khóa + 10000 giấy khen
                exchangeCostume(p, 10000000, 10000, 516, "Cải trang Yamato");

                break;

            case 7:

                // Sách tiềm năng cao x10 - 10,000,000 bạc khóa + 10000 giấy khen
                exchangeCostume(p, 10000000, 10000, 368, "Sách tiềm năng cao");

                break;

            case 8:

                // Sách kỹ năng cao x10 - 10,000,000 bạc khóa + 10000 giấy khen
                exchangeCostume(p, 10000000, 10000, 369, "Sách kỹ năng cao");

                break;

        }

    }

    private static void buyFishingCane(Char p, int caneId, int price, String caneName) {

        if (!isWeekendFishingTime()) {

            p.getService().serverMessage("Sự kiện câu cá cuối tuần chỉ diễn ra vào 11h sáng Chủ Nhật");

            return;

        }

        if (p.getCountNullItemBag() < 1) {

            p.warningBagFull();

            return;

        }

        if (p.Bag.vang < price) {

            p.getService().serverMessage("Bạn không có đủ " + price + " vàng");

            return;

        }

        p.addVang(-price);

        Item cane = new Item(caneId);

        p.addItem(cane);

        p.msgAddItemBag(cane);

        p.getService().serverMessage("Mua " + caneName + " thành công!");

    }

    private static void exchangeFish(Char p, int fishId, int bacKhoa, int vangKhoa, int giayKhen, String fishName) {

        // Kiểm tra có cá không
        Item fish = p.FindItemBag(fishId);

        if (fish == null) {

            p.getService().serverMessage("Bạn không có " + fishName);

            return;

        }

        // Kiểm tra giấy khen cần thủ
        if (giayKhen > 0) {

            Item giayKhenItem = p.FindItemBag(GIAY_KHEN_CAN_THU);

            if (giayKhenItem == null || giayKhenItem.getAmount() < giayKhen) {

                p.getService().serverMessage("Bạn cần " + giayKhen + " giấy khen cần thủ");

                return;

            }

        }

        // Kiểm tra túi đồ
        int slotsNeeded = 0;

        if (bacKhoa > 0) {
            slotsNeeded++;
        }

        if (vangKhoa > 0) {
            slotsNeeded++;
        }

        if (giayKhen > 0) {
            slotsNeeded++;
        }

        if (p.getCountNullItemBag() < slotsNeeded) {

            p.warningBagFull();

            return;

        }

        // Xóa cá
        if (fish.getAmount() > 1) {

            p.removeItemByAmount(fish, 1);

            p.msgUseItemBag(fish);

        } else {

            p.removeItem(fish, true);

            p.msgRemoveItemBag(fish);

        }

        // Xóa giấy khen nếu cần
        if (giayKhen > 0) {

            Item giayKhenItem = p.FindItemBag(GIAY_KHEN_CAN_THU);

            if (giayKhenItem.getAmount() > giayKhen) {

                p.removeItemByAmount(giayKhenItem, giayKhen);

                p.msgUseItemBag(giayKhenItem);

            } else {

                p.removeItem(giayKhenItem, true);

                p.msgRemoveItemBag(giayKhenItem);

            }

        }

        // Thêm phần thưởng
        if (bacKhoa > 0) {

            p.addBacKhoa(bacKhoa);

        }

        if (vangKhoa > 0) {

            p.addVangKhoa(vangKhoa);

        }

        if (giayKhen > 0) {

            Item reward = new Item(GIAY_KHEN_CAN_THU);

            reward.isLock = true;

            reward.setAmount(giayKhen);

            p.addItem(reward);

            p.msgAddItemBag(reward);

        }

        p.getService().serverMessage("Đổi " + fishName + " thành công!");

    }

    private static void exchangeFishingReward(Char p, int pointRequired, int itemId, String itemName) {

        if (p.getEventPoint() == null) {

            p.setEventPoint(new com.event.eventpoint.EventPoint());

        }

        int currentPoint = p.getEventPoint().getPoint(com.event.eventpoint.EventPoint.DIEM_TIEU_XAI);

        if (currentPoint < pointRequired) {

            p.getService().serverMessage("Bạn không có đủ " + pointRequired + " điểm tiêu sài");

            return;

        }

        if (p.getCountNullItemBag() < 1) {

            p.warningBagFull();

            return;

        }

        p.getEventPoint().subPoint(com.event.eventpoint.EventPoint.DIEM_TIEU_XAI, pointRequired);

        Item reward = new Item(itemId);

        reward.isLock = true;

        p.addItem(reward);

        p.msgAddItemBag(reward);

        p.getService().serverMessage("Đổi " + itemName + " thành công!");

    }

    /**
     *
     *
     *
     * Đổi cải trang bằng bạc khóa + giấy khen cần thủ
     *
     *
     *
     */
    private static void exchangeCostume(Char p, int bacKhoaRequired, int giayKhenRequired, int itemId, String itemName) {

        exchangeCostume(p, bacKhoaRequired, giayKhenRequired, itemId, itemName, 1);

    }

    private static void exchangeCostume(Char p, int bacKhoaRequired, int giayKhenRequired, int itemId, String itemName, int quantity) {

        // Kiểm tra bạc khóa
        if (p.Bag.bacKhoa < bacKhoaRequired) {

            long thieuBacKhoa = bacKhoaRequired - p.Bag.bacKhoa;

            p.getService().serverMessage("Bạn thiếu " + thieuBacKhoa + " bạc khóa");

            return;

        }

        // Kiểm tra giấy khen cần thủ
        Item giayKhenItem = p.FindItemBag(GIAY_KHEN_CAN_THU);

        int currentGiayKhen = (giayKhenItem != null) ? giayKhenItem.getAmount() : 0;

        if (currentGiayKhen < giayKhenRequired) {

            int thieuGiayKhen = giayKhenRequired - currentGiayKhen;

            p.getService().serverMessage("Bạn thiếu " + thieuGiayKhen + " giấy khen cần thủ");

            return;

        }

        // Kiểm tra túi đồ
        if (p.getCountNullItemBag() < quantity) {

            p.warningBagFull();

            return;

        }

        // Trừ bạc khóa
        p.addBacKhoa(-bacKhoaRequired);

        // Xóa giấy khen
        if (giayKhenItem.getAmount() > giayKhenRequired) {

            p.removeItemByAmount(giayKhenItem, giayKhenRequired);

            p.msgUseItemBag(giayKhenItem);

        } else {

            p.removeItem(giayKhenItem, true);

            p.msgRemoveItemBag(giayKhenItem);

        }

        // Thêm cải trang
        for (int i = 0; i < quantity; i++) {

            Item costume = new Item(itemId);

            costume.isLock = true;

            p.addItem(costume);

            p.msgAddItemBag(costume);

        }

        if (quantity > 1) {

            p.getService().serverMessage("Đổi " + quantity + " " + itemName + " thành công!");

        } else {

            p.getService().serverMessage("Đổi " + itemName + " thành công!");

        }

    }

    public static void handleFishingRod(Char p, Item fishingRod) {

        if (!isWeekendFishingTime()) {

            p.getService().serverMessage("Sự kiện câu cá cuối tuần chỉ diễn ra vào 11h sáng Chủ Nhật");

            return;

        }

        if (p.Info._mapID != FISHING_MAP_ID || p.Info.cy != FISHING_CY) {

            p.getService().serverMessage("Ở đây làm gì có cá, muốn câu cá hãy đến Làng Đá (tọa độ y=692)");

            return;

        }

        if (p.getCountNullItemBag() == 0) {

            p.warningBagFull();

            return;

        }

        // Chặn spam
        if (p.isCatchItem) {

            return;

        }

        p.isCatchItem = true;

        java.util.concurrent.ScheduledExecutorService executor = Service.ScheduledExecutor.getInstance();

        int time = com.sg188.lib.Utlis.nextInt(3500, 7000);

        try {

            p.user.session.sendMessage(Service.HanderMessage.takingItem(time, p.Info.idEntity, "Đang thả câu", fishingRod.id));

        } catch (Exception e) {

            Log.error("Error sending fishing animation", e);

        }

        p.future = executor.schedule(() -> {

            try {

                if (!p.isClean && p.isCatchItem) {

                    // Xóa cần câu
                    if (fishingRod.getAmount() > 1) {

                        p.removeItemByAmount(fishingRod, 1);

                        p.msgUseItemBag(fishingRod);

                    } else {

                        p.removeItem(fishingRod, true);

                        p.msgRemoveItemBag(fishingRod);

                    }

                    // Random cá
                    int fishId;

                    boolean isLocked;

                    if (fishingRod.id == FISHING_ROD_GOLD) {

                        fishId = randomFishingReward(GOLD_ROD_REWARDS, GOLD_ROD_PERCENTS);

                        isLocked = false;

                    } else {

                        fishId = randomFishingReward(SILVER_ROD_REWARDS, SILVER_ROD_PERCENTS);

                        isLocked = true;

                    }

                    // Tạo item cá
                    Item fish = new Item(fishId, isLocked);

                    p.addItem(fish);

                    p.msgAddItemBag(fish);

                    // Thêm exp
                    if (!p.Info.khoaExp) {

                        p.addExp(100000);

                    }

                    // Thông báo đặc biệt cho cá koi vàng (ID 505)
                    if (fishId == 505) {

                        String fishName = fish.getItemTemplate().name;

                        com.sg188.server.Main.HeThongCTG("🎣 Nhẫn giả " + p.Info.name + " đã câu được " + fishName + ", thật là may mắn! Hoạt động câu cá cuối tuần đang diễn ra sôi nổi tại Làng Đá!", 2);

                    } else {

                        // Thông báo cho cá hiếm khác (tỷ lệ <= 10%)
                        int rewardIndex = -1;

                        int[] rewards = fishingRod.id == FISHING_ROD_GOLD ? GOLD_ROD_REWARDS : SILVER_ROD_REWARDS;

                        int[] percents = fishingRod.id == FISHING_ROD_GOLD ? GOLD_ROD_PERCENTS : SILVER_ROD_PERCENTS;

                        for (int i = 0; i < rewards.length; i++) {

                            if (rewards[i] == fishId) {

                                rewardIndex = i;

                                break;

                            }

                        }

                        if (rewardIndex >= 0 && percents[rewardIndex] <= 10) {

                            p.chatPublic("Haha! Được một " + fish.getItemTemplate().name + " rồi nè!");

                        }

                    }

                    // Gửi animation
                    try {

                        p.service.sendMessage(Service.HanderMessage.TestMess7(p.Info.idEntity));

                    } catch (Exception e) {

                        Log.error("Error sending fishing complete animation", e);

                    }

                    // Cập nhật điểm
                    if (p.getEventPoint() == null) {

                        p.setEventPoint(new com.event.eventpoint.EventPoint());

                    }

                    p.getEventPoint().addPoint(TOP_FISH_WEEKEND, 1);

                    p.getEventPoint().addPoint(com.event.eventpoint.EventPoint.DIEM_TIEU_XAI, 2);

                    p.isCatchItem = false;

                }

            } catch (Exception e) {

                Log.error("Error in fishing task", e);

                p.isCatchItem = false;

            }

        }, time, java.util.concurrent.TimeUnit.MILLISECONDS);

    }

    private static int randomFishingReward(int[] rewards, int[] percents) {

        int totalPercent = 0;

        for (int percent : percents) {

            totalPercent += percent;

        }

        int random = com.sg188.lib.Utlis.nextInt(0, totalPercent);

        int currentPercent = 0;

        for (int i = 0; i < rewards.length; i++) {

            currentPercent += percents[i];

            if (random < currentPercent) {

                return rewards[i];

            }

        }

        return rewards[rewards.length - 1];

    }

    /**
     *
     *
     *
     * Xử lý sử dụng cá để đổi phần thưởng
     *
     *
     *
     */
    public static void handleUseFish(Char p, Item fish) {

        int bacKhoa = 0;

        int vangKhoa = 0;

        int giayKhen = 0;

        String fishName = fish.getItemTemplate().name;

        // Xác định phần thưởng dựa vào ID cá
        switch (fish.id) {

            case 501: // Cá tre

                bacKhoa = 2000;

                break;

            case 502: // Cá lóc

                bacKhoa = 4000;

                break;

            case 503: // Cá mè

                bacKhoa = 6000;

                giayKhen = 1;

                break;

            case 504: // Cá ngát

                bacKhoa = 20000;

                giayKhen = 3;

                break;

            case 505: // Cá koi vàng

                bacKhoa = 200000;

                giayKhen = 30;

                break;

            case 506: // Cá điêu hồng

                vangKhoa = 2;

                giayKhen = 2;

                break;

            case 507: // Cá rô phi

                vangKhoa = 5;

                giayKhen = 4;

                break;

            case 508: // Cá tai tượng

                vangKhoa = 7;

                giayKhen = 6;

                break;

            case 509: // Cá trảm cỏ

                vangKhoa = 15;

                giayKhen = 10;

                break;

            case 510: // Cá koi đỏ

                vangKhoa = 100;

                giayKhen = 100;

                break;

            default:

                p.getService().serverMessage("Không thể sử dụng loại cá này");

                return;

        }

        // Kiểm tra túi đồ
        int slotsNeeded = 0;

        if (giayKhen > 0) {
            slotsNeeded++;
        }

        if (p.getCountNullItemBag() < slotsNeeded) {

            p.warningBagFull();

            return;

        }

        // Xóa cá
        if (fish.getAmount() > 1) {

            p.removeItemByAmount(fish, 1);

            p.msgUseItemBag(fish);

        } else {

            p.removeItem(fish, true);

            p.msgRemoveItemBag(fish);

        }

        // Thêm phần thưởng
        if (bacKhoa > 0) {

            p.addBacKhoa(bacKhoa);

        }

        if (vangKhoa > 0) {

            p.addVangKhoa(vangKhoa);

        }

        if (giayKhen > 0) {

            Item reward = new Item(GIAY_KHEN_CAN_THU);

            reward.isLock = true;

            reward.setAmount(giayKhen);

            p.addItem(reward);

            p.msgAddItemBag(reward);

        }

        // Thông báo
        StringBuilder msg = new StringBuilder("Sử dụng " + fishName + " nhận được: ");

        if (bacKhoa > 0) {
            msg.append(bacKhoa).append(" bạc khóa");
        }

        if (vangKhoa > 0) {

            if (bacKhoa > 0) {
                msg.append(", ");
            }

            msg.append(vangKhoa).append(" vàng khóa");

        }

        if (giayKhen > 0) {

            if (bacKhoa > 0 || vangKhoa > 0) {
                msg.append(", ");
            }

            msg.append(giayKhen).append(" giấy khen cần thủ");

        }

        p.getService().serverMessage(msg.toString());

    }

}
