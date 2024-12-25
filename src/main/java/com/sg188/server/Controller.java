package com.sg188.server;

import EventClick.ClickEvent;
import EventClick.ClickTop;
import Manager.Manager;
import MapService.Map;
import MapService.world.CamThuat;
import MapService.world.Dungeon;
import MapService.world.MapLangCo;
import MapService.world.Training;
import Service.*;
import SqlConnection.CharDB;
import com.event.Event;
import com.sg188.clan.Clan;
import com.sg188.clan.Member;
import com.sg188.data.ItemOption;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.*;
import com.sg188.server.handler.IMessageHandler;
import com.sg188.server.lib.Message;
import com.sg188.task.TaskName;
import market.MarketManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller implements IMessageHandler {
    private final Session client;
    private User user;
    private Char _char;
    private Service service;
    private long long_38;
    private long time;

    public Controller(Session client) {
        this.client = client;
    }

    public void setUser(User us) {
        this.user = us;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setChar(Char _char) {
        this._char = _char;
    }

    @Override
    public void readMessage(Message msg) {
        try {
            Log.debug("Đây là MSG " + msg.cmd);
            if (msg.cmd == -127) {
//                String keyApp = msg.readUTF();
//                String path = new String(msg.read(), "UTF-8");
                return;
            } else if (msg.cmd == -122) {
                readMessage122(msg);
                return;
            } else if (msg.cmd == -113) {
                readTypeClient(msg);
                return;
            } else if (msg.cmd == -123 || msg.cmd == -124) {
                readMessage123(msg);
                return;
            }
            if (_char == null || client == null || user == null || _char.isClean || user.isCleaned || client.isClean) {
                return;
            }
//            if (_char.timemsg > System.currentTimeMillis() && msg.cmd != 116) {
////                client.sendMessage(HanderMessage.SendThongBao("Thao tác quá nhanh vui lòng đợi",HanderMessage.WHITE));
//                return;
//            }
//            _char.timemsg = System.currentTimeMillis() + 150;
            switch (msg.cmd) {
                case -22:
                    if (_char != null && _char.user != null)
                        HanderClickEvent.HanderTop(_char, msg);
                    break;
                case -87:
                    break;
                case 28:
                    if (_char != null && _char.user != null) {
                        _char.chatPrivate(msg);
                    }
                    break;
                case 61:
                    if (_char != null && _char.user != null) {
                        int idSkill = msg.readShort();
                        int idMob = -1;
                        try {
                            idMob = msg.readShort();
                        } catch (Exception x) {

                        }
                        if (idMob == -1) {
                            HanderUseSkill.HanderSkillNotFocus(_char, _char.getSkillWithIdTemplate(idSkill));
//                                    Message m = new Message((byte) -85);
//                                    m.writeInt(_char.Point.hp);
//                                    m.writeShort(idSkill);
//                                    m.writeLong(0);
//                                    _char.client.session.sendMessage(m);
                            break;
                        }
                        _char.zone.attackMob(_char, idSkill, idMob);
                    }
                    //attack Mob
                    break;
                case 127:
                    //nextMap
                    if (_char != null && _char.user != null)
                        _char.zone.nextMap(_char);
                    break;
                case -15:
                    if(_char.Info._mapID == 49) {
                        _char.service.serverMessage("Không thể đổi cờ tại đây");
                        return;
                    }
                    if (_char != null) {
                        if (_char.zone.isLoiDai()) {
                            _char.service.serverMessage("Không thể đổi cờ tại đây");
                            return;
                        }
                        HanderCharacter.SetTypePk(_char, msg.readByte());
                    }
                    break;
                case -6:
                    if (_char != null && _char.user != null)
                        _char.zone.openTabZone(_char);
                    break;
                case -7:
                    if (_char != null && _char.user != null)
                        _char.zone.changeZone(_char, msg.readByte());
                    break;
                case 59:
                    if (_char != null && _char.user != null)
                        _char.zone.pickUpItem(_char, msg.readShort());
                    break;
                case -56:
                    int num = msg.readShort();
                    if (num != Utlis.getArrayListNotNull(_char.Bag.arrItemBag, null).size()) {
                        _char.msgSendArrItemBag();
                    }
                    _char.msgUpdateItemBody();

                    break;
                case 117:
                    if (_char != null && _char.user != null)
                        _char.sortItem(msg.readByte());
                    break;
                case 116:
                    if (_char != null && _char.user != null)
                        _char.useItem(msg.readShort());
                    break;
                case 20:
                    if(_char.Info._mapID == 49 && _char.Info.cy >= 500 &&_char.Info.cy <= 566) {
                        break;
                    }
                    if (_char != null) {
                        _char.attackCharacter(msg);
                    }
                    break;
                case 34:
                    if (_char != null) {
                        ClickTop.ShowInfo(_char, msg.readUTF());
                    }
                    break;
                case 36:
                    if (_char != null && _char.user != null)
                        _char.useItemBodyDuPhong(msg.readShort());
                    break;
                case 37:
                    if (_char != null && _char.user != null)
                        _char.itemBodyDuPhongToBag(msg.readByte());
                    break;
                case -38:
                    if (System.currentTimeMillis() - long_38 >= 10000L) {
                        long_38 = System.currentTimeMillis();
                        _char.service.sendArrMap(_char.zone.map.mapID);
                        _char.service.sendIntoMap();
                    }
                    break;
                case 39:
                    if (_char != null && _char.user != null)
                        _char.addPartyAccept(msg);
                    break;
                case 38:
                    // huy vao nhom
                    break;
                case 26:
                    if (_char != null && _char.user != null)
                        if (_char.getGroup() != null) {
                            _char.getGroup().getGroupService().chat(_char.Info.name, msg.readUTF());
                        }
                    break;
                case 41:
                    if (_char != null && _char.user != null)
                        if (msg.Avali() < 1) {
                            _char.createGroup();
                        } else {
                            _char.addParty(msg);
                        }
                    break;
                case 42:
                    if (_char != null && _char.user != null)
                        if (_char.getGroup() != null) {
                            _char.getGroup().isLock = !_char.getGroup().isLock;
                            _char.getGroup().getGroupService().playerInParty();
                        }
                    break;
                case 25:
                    if (_char != null && _char.user != null) {
                        if (_char.clan != null) {
                            String chat = msg.readUTF();
                            _char.clan.getClanService().chat(_char.Info.name, chat);
                        }
                    }
                    break;
                case 43:
                    if (_char != null && _char.user != null)
                        if (_char.getGroup() != null) {
                            _char.getGroup().getGroupService().playerInParty();
                        }
                    break;
                case 44:
                    if (_char != null && _char.user != null)
                        _char.outParty();
                    break;
                case 45:
                    if (_char != null && _char.user != null)
                        _char.openFindParty();
                    break;
                case 46:
                    if (_char != null && _char.user != null)
                        _char.changeTeamLeader(msg);
                    break;
                case 47:
                    if (_char != null && _char.user != null)
                        _char.moveMember(msg);
                    break;
                case 112:
                    if (_char != null && _char.user != null)
                        _char.itemExtendToBag(msg.readByte());
                    break;
                case 113:
                    if (_char != null && _char.user != null)
                        _char.itemBodyToBag(msg.readByte());
                    break;
                case 118:
                    if (_char != null && _char.user != null)
                        _char.tachItem(msg.readShort(), msg.readShort());
                    break;
                case 111:
                    if (_char != null && _char.user != null)
                        _char.vutItem(msg.readShort());
                    break;
                case 21:
                    if (_char != null && _char.user != null)
                        _char.chatPublic(msg.readUTF());
                    break;
                case 22:
//                                Log.debug(msg.readBoolean());
//                                Log.debug(msg.readUTF());
                    if (_char != null && _char.user != null)
                        _char.service.ChatGlobal(msg, _char);
                    break;
                case -95:
                    if (_char != null && _char.user != null)
                        _char.msgDataBag();
                    break;
                case 96:
                    break;
                case 62:
                    if (_char != null && _char.user != null) {
                        if (_char.Info.idClass == 0) {
                            service.alertMessage("Yêu cầu vào lớp mới có thể cộng tiềm năng");
                            _char.msgUpdateDataChar();
                            _char.msgGetInfo();
                            return;
                        }
                        int[] array = new int[4];
                        for (int i = 0; i < array.length; i++) {
                            array[i] = msg.readShort();
                        }
                            array[1] -= (_char.chakra-_char.Point.arrayTiemNang[1]);
                        if (_char.Info.idClass == 1 || _char.Info.idClass == 5) {

                        } else {
                            if (array[0] != 0) {
                                _char.msgUpdateDataChar();
                                _char.msgGetInfo();
                                return;
                            }
                        }
                        int numz = 0;
                        for (int i = 0; i < _char.Point.arrayTiemNang.length; i++) {
                            if (_char.Point.arrayTiemNang[i] > array[i]) {
                                return;
                            }
//                                    if(i == 1 && _char.Point.arrayTiemNang[1] >10)
//                                    {
//                                        numz = numz + (array[0] - _char.Point.arrayTiemNang[0]);
//                                        continue;
//                                    }
                            numz = numz + (array[i] - _char.Point.arrayTiemNang[i]);
                        }
                        if (numz > _char.Point.diemTiemNang) {
                            return;
                        }
                        _char.Point.diemTiemNang -= numz;
                        _char.msgUpdateDataChar();
                        for (int i = 0; i < _char.Point.arrayTiemNang.length; i++) {
                            _char.Point.arrayTiemNang[i] = array[i];
                        }
                        _char.updateTiemNang();
                        _char.msgGetInfo();
                        if (_char.taskId == TaskName.NV_NHAN_GIA_HOC_VIEN && _char.taskMain != null && _char.taskMain.index == 10) {
                            _char.taskNext();
                        }
                    }
                    break;
                case 63:
                    if (_char != null && _char.user != null) {
                        String namepl = msg.readUTF();
                        if (_char.Info.name.equals(namepl)) {
                            _char.msgGetInfo();
                        } else {
                            ClickTop.msgGetInfo(_char, namepl);
                        }
                    }
                    break;
                case 14:
                    if (_char != null && _char.user != null)
                        _char.nangCapSkill(msg.readShort());
                    break;
                case 126:
                    if (_char != null && _char.user != null)
                        _char.focusSkill(msg.readShort());
                    break;
                case 48:
                    if (_char != null && _char.user != null) {
                        if (_char.zone.isLoiDai()) {
                            service.serverMessage("Không thể hồi sinh tại khu vực này");
                            return;
                        }
                        _char.reSpawn();
                        Map.maps[_char.Info.mapReSpawm].addChar(_char);
                        _char.Info._mapID = _char.Info.mapReSpawm;
                    }
                    break;
                case 49:
                    if (_char != null && _char.user != null)
                        _char.reSpawnHS();
                    break;
                case 54:
                    if (_char != null && _char.user != null) {
                        short indexNpc = msg.readShort();
                        if (indexNpc > -1) {
                            HanderNpc.OpenMenu(_char, indexNpc);
                        }
//                                _char.zone.openNpc(client, msg.readShort());
                    }
                    break;
                case 53:
                    if (_char != null && _char.user != null) {
                        short aShort = msg.readShort();
                        byte select = msg.readByte();
                        byte select2 = -1;
                        try {
                            select2 = msg.readByte();
                        } catch (IOException e) {

                        }
                        if (aShort > -1) {
                            HanderNpc.OderMenu(_char, aShort, select, select2);
                        } else {
                            HanderNpc.OpenNpcItem(_char, select, select2);
                        }
                    }
                    //  _char.zone.selectNpc(client, indexNpc, index1, index2);
                    break;
                case 74:
                    if (_char != null && _char.user != null) {
                        byte action = 0;
                        boolean isRuong = false;
                        try {
                            action = msg.readByte();
                        } catch (IOException e) {
                            isRuong = true;
                        }
//                        SpinSystem.getInstance().rewardSpin(_char, action, isRuong);
                        _char.rewardTreasure(action,isRuong);
                    }
                    break;
                case 72:
                    if (_char != null && _char.user != null) {
//                        _char.service.alertMessage(" Do vòng quay sò quá bịp nên Đệ Tứ đã đóng để bảo trì");
                        byte action = msg.readByte();
                        _char.spinTreasure(action);
                    }
                    break;
                case 108:
                    if (_char != null && _char.user != null) {
                        _char.ghepDa(msg);
                    }
                    break;
                case -46:
                    if (_char != null) {
                        _char.khamNgoc(msg);
                    }
                    break;
                case -51:
                    if (_char != null) {
                        _char.tachCt(msg);
//
                    }
                    break;
                case -52:
                    if (_char != null && _char.user != null) {
                        _char.showct(msg);
                    }
                    break;
                case 81:
                    if (_char != null) {
                        _char.tradeAccept();
                    }
                    break;
                case 82:
                    if (_char != null) {
                        _char.tradeItemLock(msg);
                    }
                    break;
                case 83:
                    if (_char != null) {
                        service.updateItemTrade();
                        if (_char.partnerTrade != null) {
                            Char partner = _char.partnerTrade.getChar();
                            if (partner != null) {
                                partner.trade.closeUITrade();
                            }
                        }

                    }
                    break;
                case 84:
                    if (_char != null) {
                        try {
                            if (_char.trade != null) {
                                _char.partnerTrade.getChar().cleanTrade();
                                _char.cleanTrade();
                            }
                        } catch (Exception e) {
                        }
                    }
                    break;
                case 85:
                    if (_char != null) {
                        _char.acceptInviteTrade(msg);
//                        HanderGiaoDich.StartGiaoDich(_char);
                    }
                    break;
                case 86:
                    if (_char != null) {
                        _char.tradeInvite(msg);
//                        HanderGiaoDich.CreateGiaoDich(_char, msg);
                    }
                    break;
                case -50:
                    if (_char != null && _char.user != null) {
                        _char.ghepCt(msg);
                    }
                    break;
                case 105:
                    if (_char != null && _char.user != null) {
                        _char.tachCuongHoa(msg);
                    }
                    break;
                case 88:
                    if (_char != null) {
                        _char.removeLetter(msg);
                    }
                    break;
                case 95:
                    if (_char != null) {
                        int idLetter = msg.readShort();
                        Letter.gI().recive(_char,idLetter);
                    }
                    break;
                case 107:
                    if (_char != null && _char.user != null) {
                        _char.cuongHoa(msg);
                    }
                    break;
                case -25:
                    if (_char != null && _char.user != null) {
                        int indexItem = msg.readShort();
                        byte index1 = msg.readByte();
                        byte index2 = -1;

                        try {
                            index2 = msg.readByte();
                        } catch (Exception xx) {

                        }
                        _char.useItemWithTab(indexItem, index1, index2);
                    }
                    break;
                case 121:
                    if (_char != null) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - _char.lastClickTime < _char.debounceDelay) {
                            user.session.sendMessage(HanderMessage.SendThongBao("Thao tác quá nhanh vui lòng thử lại sau", HanderMessage.WHITE));
                            return;
                        }
                        _char.lastClickTime = currentTime;
                        HanderShop.BuyShop(_char, msg);
                    }
                    break;
                case 122:
                    if (_char != null) {
                        HanderClickEvent.HanderClick(_char, msg);
                    }
                    break;
                case 8:
                    if (_char != null && _char.user != null) {
                        short Id = msg.readShort();
                        int IdItemMap = Item.CheckItemMSG8ByMap(_char.Info._mapID);
                        if (_char.zone.isDungeoClan()) {
                            IdItemMap = 308;
                        }
                        ScheduledExecutorService executor = ScheduledExecutor.getInstance();

                        int finalIdItemMap = IdItemMap;
                        executor.schedule(() -> {
                            client.sendMessage(HanderMessage.TestMess7(_char.Info.idEntity));
                            Item Cay = new Item(finalIdItemMap);
                            Cay.amount = 1;
                            if (Cay.id == 308)
                                Cay.expiry = System.currentTimeMillis() + 43200000;
                            Cay.isLock = true;
                            if (_char.taskId == TaskName.NV_HAI_THUOC_TRI_THUONG) {
                                if (_char.taskMain != null && _char.taskMain.index == 0) {
                                    int idItem = _char.taskMain.vStep.get(_char.taskMain.index).idItem;
                                    if (idItem == Cay.id) {
                                        _char.updateTaskCount(1);
                                    }
                                }
                            }
                            if (_char.taskId == TaskName.NV_CHUA_LANH_VET_THUONG) {
                                if (_char.taskMain != null && _char.taskMain.index == 0) {
                                    int idItem = _char.taskMain.vStep.get(_char.taskMain.index).idItem;
                                    if (idItem == Cay.id) {
                                        _char.updateTaskCount(1);
                                    }
                                }
                            }
                            _char.addItem(Cay);
                            _char.msgAddItemBag(Cay);
                        }, 3, TimeUnit.SECONDS);
                    }
                    break;
                case 7:
                    if (_char != null && _char.isCatchItem) {
                        _char.isCatchItem = false;
                        try {
                            if (_char.future != null && !_char.future.isDone() && !_char.future.isCancelled()) {
                                _char.future.cancel(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        service.serverMessage("Đã huỷ câu cá");
                    }
                    if(_char.isCauCa){
                        _char.isCauCa=false;
                    }
                    Message m1 = new Message((byte) 7);
                    m1.writer.writeInt(_char.Info.idEntity);
                    client.sendMessage(m1);
                    break;
                case 9:
                    if (_char != null && _char.user != null) {
                        if(_char.taskMain != null){
                            _char.taskMain = null;
                            service.sendTaskInfo();
                        }
                    }
                    break;
                case 11:
//                    if (_char.Info.idTask == 20) {
//                        _char.Info.idTask = DataCenter.gI().TaskTemplate.length - 1;
//                        _char.sendTask();
//                        return;
//                    }
//                    HanderNpc.getItemTask(_char);
//                    _char.Info.idStep++;
//                    _char.sendTask();
                    _char.acceptTask();
                    break;
                case 10:

                    if (_char.getCountNullItemBag() < 3) {
                        service.alertMessage("Hành trang không đủ chỗ trống để nhận thưởng");
                        return;
                    }
                    if (msg.Avali() > 0) {
                        if (isSpecialTask(_char.taskId)) {
                            byte size = msg.readByte();
                            for (int i = 0; i < size; i++) {
                                short index = msg.readShort();
                                _char.Bag.arrItemBag[index] = null;
                            }
                            _char.updateTask();
                        }
                        _char.getService().resetScreen();
                    } else {
                        if (shouldOpenMsg122(_char)) {
                            service.openMsg122((byte) 80);
                        } else {
                            if (_char.taskMain != null && _char.taskMain.index >= _char.taskMain.vStep.size())
                                _char.updateTask();
                        }
                    }
                    break;
                case 115:
                    if (_char != null && _char.user != null) {
                        int index = msg.readShort();
                        _char.itemBagToBox(index);
                    }
                    break;
                case 114:
                    if (_char != null && _char.user != null) {
                        int indexbox = msg.readShort();
                        _char.itemBoxToBag((byte) indexbox);
                    }
                    break;
                case 12:
//                                _char.Info.idStep++;
//                                _char.sendTask();
                    break;
                case -3:
                    if (_char != null && _char.user != null) {
                        if (msg.Avali() > 0) {
                            short indexItemNV = msg.readShort();
                            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                            executor = ScheduledExecutor.getInstance();
                            client.sendMessage(HanderMessage.takingItem(4500, _char.Info.idEntity, "Đang giám định", _char.Bag.arrItemBag[indexItemNV].id));
                            executor.schedule(() -> {
                                client.sendMessage(HanderMessage.TestMess7(_char.Info.idEntity));
                                if (_char.taskId == TaskName.NV_BAO_VAT_LANG_LA) {
                                    _char.Bag.arrItemBag[indexItemNV].strOptions += "105,87,3299";
                                } else
                                    _char.Bag.arrItemBag[indexItemNV].strOptions += "105,61,3276";
                            }, 5, TimeUnit.SECONDS);
                        }
                    }
                    break;
                case 119:
                    if (_char != null && _char.user != null) {

                        short IndexSell = msg.readShort();
                        boolean temp = msg.readBoolean();
                        _char.SellItem(IndexSell, temp);
                    }
                    break;
                case -24:
                    if (_char != null && _char.user != null) {

                        int id = msg.readByte();
                        _char.CheTao(id);
                    }
                    break;
                case 87:
                    if (_char != null && _char.user != null) {

                        String name = msg.readUTF();
                        String chuDe = msg.readUTF();
                        String noidung = msg.readUTF();
                        int Bac = msg.readInt();
                        short var = -1;
                        try {
                            var = msg.readShort();
                        } catch (IOException ex) {

                        }
                        _char.guiThu(name, chuDe, noidung, Bac, var);
//                        Log.debug(name + " " + chuDe + " " + noidung + " " + Bac + " " + var);
                    }
                    break;
                case 104:
                    if (_char != null && _char.user != null)
                        HanderCombine.DichChuyenTrangBi(_char, msg);
                    break;
                case -35:
                    if (_char != null && _char.user != null) {
                        _char.updateEqip_1(msg);
                    }
                    break;
                case -39:
                    Log.debug(msg.readByte());
                    break;
                case -47:
                    if (_char != null && _char.user != null) {
                        _char.tachKham(msg);
                    }
                    break;
                case 106:
                    if (_char != null && _char.user != null) {
                        _char.updateBuaNo(msg);
                    }
                    break;
                case -20:
                    if (_char != null && _char.user != null) {
                        _char.doiTinhThach(msg);
                    }
                    break;
                case -10:
                    Message m = new Message((byte) -10);
                    m.writeByte(2);//so lan quay
                    m.writeByte(3);// hệ
                    m.writeByte(10);// cap tich luy
                    m.writeByte(1);
                    client.sendMessage(m);
                    break;
                case -96:
                    if (_char != null && _char.user != null) {
                        _char.nangBiKip(msg);
                    }
                    break;
                case 19:
                    if (_char.buaBaoHo && !_char.inLangCo) {
                        client.sendMessage(HanderMessage.SendThongBao("Dang trong trạng thái bảo hộ", HanderMessage.WHITE));
                        return;
                    }
                    if (_char.Info._mapID == 49) {
                        return;
                    }
                    String name = msg.readUTF();
                    Char pl = ServerManager.findCharByName(name);
                    if (pl != null) {
                        if (pl.buaBaoHo && !pl.inLangCo) {
                            client.sendMessage(HanderMessage.SendThongBao("Đối phương trong trạng thái bảo hộ", HanderMessage.WHITE));
                            return;
                        }
                        _char.isCuuSat = true;
                        _char.idCuuSat = pl.id;
                        pl.idCuuSat = _char.id;
                        m = new Message((byte) 19);
                        m.writeInt(_char.id);
                        m.writeInt(pl.id);
                        client.sendMessage(m);
                        pl.getService().sendMessage(m);
                    }
                    break;
                case 79:
                    if (_char != null && _char.user != null) {
                        _char.addFriend(msg);
                    }
                    break;
                case 76:
                    if (_char != null && _char.user != null) {
                        _char.removeFriend(msg);
                    }
                    break;
                case 99:
                    if (_char != null && _char.user != null) {
                        _char.sellMarket(msg);
                    }
                    break;
                case 98:
                    if (_char != null && _char.user != null) {
                        long idItem = msg.readLong();
                        MarketManager.gI().buy(_char, (int) idItem);
                    }
                    break;
                case 100:

                    break;
                case 101:
                   byte var = msg.readByte();
                   byte var2=msg.readByte();
                   short var3= msg.readShort();
                    MarketManager.gI().show(_char,var,var2,var3);
                    MarketManager.gI().showListSell(_char);
                    break;
                case -33:
                    if (_char != null && _char.user != null) {
                        _char.subMenu(msg);
                    }
                    break;
                case 32:
                    if(_char.Info._mapID == 49) {
                        return;
                    }
                    if (_char != null && _char.user != null) {
                        _char.inviteTyVo(msg);
                    }
                    break;
                case 31:
                    if (_char != null && _char.user != null) {
                        _char.acceptTyVo(msg);
                    }
                    break;
                case 30:
                    if (_char != null && _char.user != null) {
                        _char.cancelTyvo(msg);
                    }
                    break;
                case -93:
                    if (_char != null && _char.user != null) {
                        _char.inviteArena(msg.readUTF());
                    }
                    break;
                case -92:
                    if (_char != null && _char.user != null) {
                        _char.betArena(msg.readInt());
                    }
                    break;
                case -90:
                    if (_char != null && _char.user != null) {
                        _char.acceptInviteLoiDai(msg);
                    }
                    break;
                case -91:
                    if (_char != null && _char.user != null) {
                        name = msg.readUTF();
                        Char attack = ServerManager.findCharByName(name);
                        if (attack != null) {
                            attack.service.serverMessage("Đối phương đã từ chối lời mời thách đấu của bạn");
                        }
                    }
                    break;
                case 5:
                    if (_char != null && _char.user != null) {
                        byte option = msg.readByte();
                        _char.menu_MSG5(option);
                    }
                    break;
                default:
                    Log.debug("recv: " + msg.cmd);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                msg.close();
            } catch (Exception ex) {
            }

        }

    }

    private boolean isSpecialTask(int taskId) {
        return taskId == TaskName.NV_LAM_NGUOI_TOT_BUNG ||
                taskId == TaskName.NV_BAT_CUU_VE ||
                taskId == TaskName.NV_HAI_THUOC_TRI_THUONG ||
                taskId == TaskName.NV_TRO_GIUP_LANG_SUONG_MU ||
                taskId == TaskName.NV_BAT_KE_NGHE_LEN ||
                taskId == TaskName.NV_CAU_CUU_VIEN_BINH ||
                taskId == TaskName.NV_CHUA_LANH_VET_THUONG ||
                taskId == TaskName.NV_TIM_LAI_TAM_BAN_DO ||
                taskId == TaskName.NV_TRUY_TIM_BI_KIP ||
                taskId == TaskName.NV_NHIEM_VU_CAP_DO_B ||
                taskId == TaskName.NV_CAN_THEM_NGUYEN_LIEU ||
                taskId == TaskName.NV_CHUOC_LAI_LOI_LAM ||
                taskId == TaskName.NV_BAO_VAT_LANG_LA||
                taskId == TaskName.NV_KE_HOACH_TAC_CHIEN;
    }

    private boolean shouldOpenMsg122(Char _char) {
        if (_char.taskMain == null) {
            return false;
        }

        switch (_char.taskId) {
            case TaskName.NV_LAM_NGUOI_TOT_BUNG:
                return _char.taskMain.index == 4;
            case TaskName.NV_BAT_CUU_VE:
                return _char.taskMain.index == 1;
            case TaskName.NV_HAI_THUOC_TRI_THUONG:
                return _char.taskMain.index == 1;
            case TaskName.NV_TRO_GIUP_LANG_SUONG_MU:
                return _char.taskMain.index == 3;
            case TaskName.NV_BAT_KE_NGHE_LEN:
                return _char.taskMain.index == 5;
            case TaskName.NV_CAU_CUU_VIEN_BINH:
                return _char.taskMain.index == 7;
            case TaskName.NV_CHUA_LANH_VET_THUONG:
                return _char.taskMain.index == 4;
            case TaskName.NV_TIM_LAI_TAM_BAN_DO:
                return _char.taskMain.index == 1;
            case TaskName.NV_TRUY_TIM_BI_KIP:
                return _char.taskMain.index == 1;
            case TaskName.NV_NHIEM_VU_CAP_DO_B:
                return _char.taskMain.index == 1;
            case TaskName.NV_CAN_THEM_NGUYEN_LIEU:
                return _char.taskMain.index == 1;
            case TaskName.NV_CHUOC_LAI_LOI_LAM:
                return _char.taskMain.index == 1;
            case TaskName.NV_BAO_VAT_LANG_LA:
                return _char.taskMain.index == 1;
            case TaskName.NV_KE_HOACH_TAC_CHIEN:
                return _char.taskMain.index == 2;
            default:
                return false;
        }
    }

    private void readTypeClient(Message msg) {
        try {
            if (!client.isSetClientType) {
                byte byte_1 = msg.readByte();
                byte os = msg.readByte();
                short widthScreen = msg.readShort();
                short heightScreen = msg.readShort();
                byte zoomLevelScreen = msg.readByte();
                byte type_cfg_image = msg.readByte();
                short ver1 = msg.readShort();
                short ver2 = msg.readShort();
                byte typeArr = msg.readByte();
                int int_1 = msg.readInt();
                int int_2 = msg.readInt();
                int int_3 = msg.readInt();
                int int_4 = msg.readInt();
                short short_1 = msg.readShort();
                client.threadSend.start();
                client.isSetClientType = true;
            }
        } catch (Exception ex) {

        }
    }

    private void readMessage122(Message msg) {
        try {
            msg.cmd = msg.readByte();
            Log.debug("MSG 122 " + msg.cmd);
            switch (msg.cmd) {
                case -127:
                    if (user != null && user.numberChar > 0) {
                        selectChar(msg);
                    }
                    break;
                case -125:
                    if (_char != null) {
                        if (!_char.nameInvite.isEmpty()) {
                            _char.acceptInviteClan();
                        } else if (!_char.inviteName.isEmpty()) {
                            _char.acceptClan();
                        }
                    }
                    break;
                case -122:
                    if (_char != null) {
                        if (!_char.nameInvite.isEmpty()) {
                            Char pl = ServerManager.findCharByName(_char.nameInvite);
                            if (pl != null) {
                                pl.user.session.sendMessage(HanderMessage.SendThongBao("Người chơi " + _char.Info.name + " đã từ chối gia nhập gia tộc", HanderMessage.WHITE));
                            }
                            _char.nameInvite = "";
                        }
                    }
                    break;
                case -99:
                    break;
                default:
                    Log.debug("recv(122): " + msg.cmd);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectChar(Message msg) {
        try {
            byte indexClick = msg.readByte();
            if (user.chars == null) {
                service.alertMessage("Co loi say ra vui long thu lai sau?");
                return;
            }
            Char cClick = user.chars.get(indexClick);
            user.chars = null;
//        Char check = ServerManager.findCharByName(cClick.Info.name); // lop bao ve thu 2
//        if(check != null){
//            service.alertMessage("BUG?");
//            return;
//        }
            user.mChar = cClick;
            user.mChar.updateAllChiSo();
            setChar(user.mChar);
            user.mChar.service = this.service;
            user.mChar.service.setChar(user.mChar);
            user.mChar.user = user;
            user.mChar.service.sendChar();
            user.mChar.DoLoginGame();
            checkWorld(cClick);
            Map.maps[user.mChar.Info._mapID].AddLoginGame(user.mChar);
            if (user.mChar.inLangCo) {
                MapLangCo.gI().maps.get(0).addChar(user.mChar);
            }
            user.mChar.msgUpdateDataChar();
            ServerManager.addChar(user.mChar);
            if (user.mChar.id == 14156) //top 1 ct
                Main.HeThongCTG("Chào mừng Đệ Nhất Cao Thủ >>" + user.mChar.Info.name + "<< vừa đăng nhập vào game", 2);
            if (user.mChar.id == 14440) //top 1 nạp
                Main.HeThongCTG("Chào mừng Top 1 Đại Gia >>" + user.mChar.Info.name + "<< vừa đăng nhập vào game", 2);
            if (user.mChar.id == 14096) //top 1 tp
                Main.HeThongCTG("Chào mừng Top 1 Tài Phú >>" + user.mChar.Info.name + "<< vừa đăng nhập vào game", 2);
            if (user.mChar.idDiaCung > -1) {
                Dungeon dungeon = Dungeon.findDungeonById(user.mChar.idDiaCung);
                if (dungeon != null) {
                    user.mChar.addWorld(dungeon);
                } else {
                    user.mChar.idDiaCung = -1;
                }
            }
            if (user.mChar.idCamThuat > -1) {
                CamThuat camThuat = CamThuat.findCamThuatById(user.mChar.idCamThuat);
                if (camThuat != null) {
                    user.mChar.addWorld(camThuat);
                } else {
                    user.mChar.idCamThuat = -1;
                }
            }
            if(user.mChar.idKhuLuyenTap > -1){
                Training klt = Training.findTrainingById(user.mChar.idKhuLuyenTap);
                if(klt==null||klt.isClosed())
                    user.mChar.idKhuLuyenTap = -1;
            }




        } catch (Exception e) {
            e.printStackTrace();
            client.disconnect();
        }
    }

    private void checkWorld(Char cClick) {
        if (user.mChar.Info._mapID == 84 || user.mChar.Info._mapID == 89 || user.mChar.Info._mapID == 6 || user.mChar.Info._mapID == 7 || user.mChar.Info._mapID == 18 || user.mChar.Info._mapID == 17 || user.mChar.Info._mapID == 31 || user.mChar.Info._mapID == 46 || user.mChar.Info._mapID == 47
                || (user.mChar.Info._mapID >= 93 && user.mChar.Info._mapID <= 97)
                || user.mChar.Info._mapID == 67) {
            user.mChar.Info._mapID = cClick.Info.mapReSpawm;
        }
    }

    private void readMessage123(Message msg) {
        try {
            msg.cmd = msg.readByte();
            Log.debug("Msg 123 " + msg.cmd);
//            if (_char != null) {
//                if (_char.timemsg > System.currentTimeMillis() && msg.cmd != 116) {
////                client.sendMessage(HanderMessage.SendThongBao("Thao tác quá nhanh vui lòng đợi",HanderMessage.WHITE));
//                    return;
//                }
//                _char.timemsg = System.currentTimeMillis() + 150;
//            }
            switch (msg.cmd) {
                case -127:
                    client.login(msg);
                    break;
                case -128:
                    int selectChar = msg.readByte();
                    String name = msg.readUTF();
                    if (user.numberChar > 0) {
                        client.sendMessage(HanderMessage.SendThongBao("Chỉ được tạo một nhân vật", HanderMessage.RED_MID));
                        break;
                    }
                    if (name.matches(".*\\s+.*")) {
                        client.sendMessage(HanderMessage.SendThongBao("Tên nhân vật không được chứa ký tự đặc biệt va khoảng trắng", HanderMessage.RED_MID));
                        break;
                    }
                    if (!Utlis.CheckString(name)) {
                        client.sendMessage(HanderMessage.SendThongBao("Tên nhân vật không được chứa ký tự đặc biệt", HanderMessage.RED_MID));
                        break;
                    }
                    if (CharDB.getCharByName(name) != null) {
                        client.sendMessage(HanderMessage.SendThongBao("Đã có tên nhân vật này", HanderMessage.RED_MID));
                        break;
                    }
                    if (name.length() < 5) {
                        client.sendMessage(HanderMessage.SendThongBao("Tên nhân vật phải có tối thiểu 5 ký tự", HanderMessage.RED_MID));
                        break;
                    }
                    if (!client.isCreateChar) {
                        Char c = new Char(user, selectChar, name);
                        user.createCharDB(c, user.numberChar, user.ID_USER);
                        user.numberChar = 1;
                        service.sendTabSelectChar((byte) user.numberChar, user);
                    }
                    break;
                case -126:
                    loadSceen();
                    break;
                case -110:
                    HanderGiftCode.CheckGiftCode(_char, msg);
                    break;
                case -106:
                    String nameClan = msg.readUTF();
                    _char.createClan(nameClan);
                    break;
                case -92:
                    try {
                        nameClan = msg.readUTF();
                        if (!Clan.getClanDAO().checkExist(nameClan)) {
                            client.sendMessage(HanderMessage.ThongBao_106("Gia tộc này không tồn tại"));
                        } else {
                            if (_char.clan != null) {
                                return;
                            }
                            Optional<Clan> g = Clan.getClanDAO().get(nameClan);
                            if (g != null && g.isPresent()) {
                                Clan clan = g.get();
                                Char toctruong = ServerManager.findCharByName(clan.getMainName());
                                if (toctruong == null) {
                                    client.sendMessage(HanderMessage.ThongBao_106("Tộc trưởng đang offline"));
                                } else {
                                    List<Member> members = clan.memberDAO.getAll();
                                    if (members.size() < clan.getMemberMax()) {
                                        toctruong.getService().inviteClan(_char.Info.name);
                                        toctruong.inviteName = _char.Info.name;
                                    } else {
                                        client.sendMessage(HanderMessage.ThongBao_106("Gia tộc đã đủ thành viên."));
                                    }
                                }
                            }


                        }
                    } catch (Exception e) {
                        Log.error(" loi xin vao gia toc ", e);
                    }
                    break;
                case -73:
                    HanderCharacter.ShowThongTin(_char, msg);
                    break;
                case -63:

                    break;
                case -62:

                    break;
                case -52:
                    if (_char != null) {
                        String namepl = msg.readUTF();
                        _char.moveTo(namepl);
                    }
                    break;
                case -57:
                    if (_char != null) {
                        ClickEvent.ThuongBXH(_char);
                    }
                    break;
                case -50:
                    if (_char != null) {
                        Letter.gI().reciveAll(_char);
                    }
                    break;
                case -60:
                    if (_char != null) {
                        HanderClickEvent.VongQuayMayMan(_char);
                    }
                    break;
                case -34:
                    Message m = Message.c((byte) -34);
                    m.writeBoolean(false);
                    m.writeUTF("zzzz");
                    client.sendMessage(m);
                    break;
                case -19:
                    byte type = msg.readByte();
                    if (type == 16)
                        _char.addEffect(new Effect((short) 100, 1000, System.currentTimeMillis(), 50000));
                    if (type == 14) {
                        _char.addEffect(new Effect((short) 101, Utlis.nextInt(100, 500), System.currentTimeMillis(), 120000));
                    }
//                                HanderCharacter.TrieuHoiThuCuoi(_char);
                    break;
                case -123:
//                                Message m1 = new Message((byte)-123);
//                                m1.writer.writeByte(-123);
//                                m1.writer.writeUTF(DataCenter.gI().Task[_char.Info.idTask].STR3);
//                                sendMessage(m1);
                    break;
                case -70:
                    if (_char != null) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - _char.lastClickTime < _char.debounceDelay) {
                            user.session.sendMessage(HanderMessage.SendThongBao("Thao tác quá nhanh vui lòng thử lại sau", HanderMessage.WHITE));
                            return;
                        }
                        _char.lastClickTime = currentTime;
                        int indexPL = msg.readShort();
                        _char.RecivePhucLoi(indexPL);
                        client.sendMessage(HanderMessage.updatePhucLoi(indexPL));
                    }
                    break;
                case -82:
                    _char.MoRongBox();
                    break;
                case -65:
                case -66:
                    int idBenefit = msg.readInt();
                    _char.subscribeBenefitPackage(idBenefit);
//                    ClickEvent.PhucLoi(_char);
                    break;

                case -74:
                    byte index = msg.readByte();
                    _char.Info.selectDanhHieu = index;
                    _char.doiDanhHieu();
                    break;
                case -58:
                    byte select = msg.readByte();
                    if (_char != null && _char.user != null) {
                        Message m2 = Message.c((byte) -58);
                        m2.writeByte(select);
                        client.sendMessage(m2);


                    }
                    break;
                case -48:
                    if (_char != null && _char.user != null) {
                        _char.nangBuaNo(msg);
                    }
                    break;
                case -17:
                    if (_char != null && _char.user != null) {
                        _char.updateLucDao(msg);
                    }
                    break;
                case -117:
//                    if (_char != null && _char.user != null) {
//                        _char.updateThienDao(msg);
//                    }
                    break;
                case -118:
//                    if (_char != null && _char.user != null) {
//                        _char.updateVoCuc(msg);
//                    }
                    break;
                case -85:
                    if (_char != null && _char.user != null) {
                        if (_char.isWheelGold) {
                            if (_char.getCountNullItemBag() < 3) {
                                service.alertMessage("Hành trang không đủ chỗ trống");
                                return;
                            }
                            Item veVanMayVip = _char.FindItemBag(966);
                            if (veVanMayVip == null) {
                                _char.service.alertMessage("Không tìm thấy vé vận may VIP trong hành trang");
                                return;
                            }
                            if (veVanMayVip.amount < 1) {
                                _char.service.alertMessage("Không đủ vé vận may VIP");
                                return;
                            }
                            _char.removeItemByAmount(veVanMayVip, 1);
                            _char.msgRemoveItemBag(veVanMayVip);
//                            if (_char.Bag.vang < 100) {
//                                client.sendMessage(HanderMessage.SendThongBao("Không đủ 100 vàng", HanderMessage.RED_MID));
//                                return;
//                            }
//                            _char.addVang(-100);
                            int indexi = Utlis.nextInt(0, Manager.gI().listTVM[_char.idListTVM].length - 1);
                            Item item = new Item(Manager.gI().listTVM[_char.idListTVM][indexi]);
                            if(item.isItemBody()) {
                                Main.HeThongCTG("Chúc mừng nhẫn giả " + _char.Info.name + "vừa quay trúng " + item.getItemTemplate().name,2);
                            }
                            if (item.isItemBody()) {
                                item.addItemOption(new ItemOption(0, 1000));
                                item.addItemOption(new ItemOption(161, 100));
                                item.addItemOption(new ItemOption(180,100));
                                item.addItemOption(new ItemOption(2, 100));
                                item.addItemOption(new ItemOption(3, 100));
                                item.addItemOption(new ItemOption(209, 100));
                                item.addItemOption(new ItemOption(306, 20));
                            }
                            if (item.id == 163) {
                                _char.addBacKhoa(Manager.gI().amountTVM[_char.idListTVM][indexi]);
                            } else if (item.id == 191) {
                                _char.addBac(Manager.gI().amountTVM[_char.idListTVM][indexi]);
                            } else if (item.id == 192) {
                                _char.addVangKhoa(Manager.gI().amountTVM[_char.idListTVM][indexi]);
                            } else if (item.id == 193) {
                                _char.addVang(Manager.gI().amountTVM[_char.idListTVM][indexi]);
                            } else {
                                item.setAmount(Manager.gI().amountTVM[_char.idListTVM][indexi]);
                                //item.amount = Manager.gI().amountTVM[_char.idListTVM][indexi];
                                if (item.getItemTemplate().type == 15 || item.getItemTemplate().type == 16) {
                                    item.expiry = System.currentTimeMillis() + Event.EXPIRE_7_DAY;
                                }
                                _char.addItem(item);
                                _char.msgAddItemBag(item);
                            }
                            Message message = Message.c((byte) -85);
                            message.writeShort(item.id);
                            message.writeInt(item.amount);
                            client.sendMessage(message);
                        } else if (_char.isWheelSilver) {
                            if (_char.getCountNullItemBag() < 3) {
                                service.alertMessage("Hành trang không đủ chỗ trống");
                                return;
                            }
                            if (_char.Bag.bac < 400000) {
                                client.sendMessage(HanderMessage.SendThongBao("Không đủ 400.000 Bạc ", HanderMessage.RED_MID));
                                return;
                            }
                            _char.addBac(-400000);
                            int indexi = Utlis.nextInt(0, Manager.gI().listTVMSilver[_char.idListTVMSilver].length - 1);
                            Item item = new Item(Manager.gI().listTVMSilver[_char.idListTVMSilver][indexi]);
                            if (item.isItemBody()) {
                                if (item.getItemTemplate().type == 15) {
                                    item.level = 14;
                                    item.addItemOption(new ItemOption(122, Utlis.nextInt(5, 15)));
                                    item.addItemOption(new ItemOption(151, Utlis.nextInt(50, 100)));
                                    item.addItemOption(new ItemOption(152, Utlis.nextInt(50, 100)));
                                    item.addItemOption(new ItemOption(117, Utlis.nextInt(100, 150)));
                                    item.addItemOption(new ItemOption(110, Utlis.nextInt(50, 100)));
                                    item.addItemOption(new ItemOption(158, Utlis.nextInt(1, 3)));
                                    item.addItemOption(new ItemOption(148, 1));

                                } else if (item.getItemTemplate().type == 16) {
                                    item.addItemOption(new ItemOption(0, Utlis.nextInt(200, 500)));
                                    item.addItemOption(new ItemOption(1, Utlis.nextInt(200, 500)));
                                    item.addItemOption(new ItemOption(2, Utlis.nextInt(50, 200)));
                                    item.addItemOption(new ItemOption(3, Utlis.nextInt(70, 250)));
                                    item.addItemOption(new ItemOption(5, Utlis.nextInt(20, 100)));
                                } else if (item.getItemTemplate().type == 14) {
                                    item.addItemOption(new ItemOption(63, Utlis.nextInt(10, 30)));
                                    item.addItemOption(new ItemOption(4, Utlis.nextInt(10, 35)));
                                    item.addItemOption(new ItemOption(66, Utlis.nextInt(1, 3)));
                                    item.addItemOption(new ItemOption(0, Utlis.nextInt(50, 200)));
                                    item.addItemOption(new ItemOption(2, Utlis.nextInt(30, 100)));
                                    item.addItemOption(new ItemOption(3, Utlis.nextInt(40, 60)));
                                    item.addItemOption(new ItemOption(209, Utlis.nextInt(20, 30)));
                                }
                            }
                            if (item.id == 163) {
                                _char.addBacKhoa(Manager.gI().amountTVMSilver[_char.idListTVMSilver][indexi]);
                            } else if (item.id == 191) {
                                _char.addBac(Manager.gI().amountTVMSilver[_char.idListTVMSilver][indexi]);
                            } else if (item.id == 192) {
                                _char.addVangKhoa(Manager.gI().amountTVMSilver[_char.idListTVMSilver][indexi]);
                            } else if (item.id == 193) {
                                _char.addVang(Manager.gI().amountTVMSilver[_char.idListTVMSilver][indexi]);
                            } else {
                                item.setAmount(Manager.gI().amountTVMSilver[_char.idListTVMSilver][indexi]);
                                if (item.getItemTemplate().type == 15 || item.getItemTemplate().type == 16) {
                                    item.expiry = System.currentTimeMillis() + Event.EXPIRE_7_DAY;
                                }
                                _char.addItem(item);
                                _char.msgAddItemBag(item);
                            }
                            //item.amount = 1;
                            Message message = Message.c((byte) -85);
                            message.writeShort(item.id);
                            message.writeInt(item.amount);
                            client.sendMessage(message);
                        }
                    }
                    break;
                case -20:
                    Item BuaPhanThan = _char.FindItemBag(782);
                    if (BuaPhanThan == null) {
                        _char.service.alertMessage("Không tìm thấy bùa phân thân trong hành trang");
                        return;
                    }
                    if (BuaPhanThan.amount < 1) {
                        _char.service.alertMessage("Không đủ bùa phân thân");
                        return;
                    }

                  //  if (!_char.cloneLive){
                        _char.addEffect(new Effect((short) 99,  _char.Point.diempt==0?2:2 * _char.Point.diempt, System.currentTimeMillis(), 60 * 1000 * 60));
                  //  }
                   // if (_char.cloneLive){
                     //   _char.addEffect(new Effect((short) 99,  _char.Point.diempt==0?2:2 * _char.Point.diempt, System.currentTimeMillis(), 60 * 1000 * 60));
                  //  }
                    _char.removeItemByAmount(BuaPhanThan, 1);
                    _char.msgRemoveItemBag(BuaPhanThan);
                    client.sendMessage(Message.c((byte) -43));


                    break;
                case -21:
                    if (_char.Point.maxpt >= 30) {
                        return;
                    }
                    if (_char.Bag.vang > 400) {
                        _char.addVang(-400);
                        _char.Point.maxpt += 5;
                        _char.msgDataBag();
                    }
                    break;
                case -28:
                    if (_char != null && _char.user != null) {
                        _char.nangCapSkillViThu(msg);
                    }
                    break;
                case -26:
                    if (_char != null && _char.user != null) {
                        _char.xoaSkillViThu(msg);
                    }
                    break;
                case -18:
                    if (_char != null && _char.user != null) {
                        if (_char.Info.levelMaxViThu >= 6) {
                            return;
                        }
                        if (_char.Bag.vang < 2000) {
                            return;
                        }
                        _char.addVang(-2000);
                        _char.Info.levelMaxViThu = 6;
                        client.sendMessage(HanderMessage.SendThongBao("Đã mở giới hạn", HanderMessage.WHITE));
                    }
                    break;
                case -44:
                    String input = msg.readUTF();
                    if (_char.captcha.equalsIgnoreCase(input)) {
                        service.resetAllScreen();
                        _char.canRevive = true;
                    }
                    break;
                case -45:
                    if (_char != null && _char.user != null) {
                        _char.moneyToBag(msg);
                    }
                    break;
                case -105:
                    if (_char != null && _char.user != null)
                        _char.clanInvite(msg);
                    break;
                case -93:
                    if (_char != null && _char.user != null) {
                        if (_char.clan != null) {
                            String alert = msg.readUTF();
                            int typeClan = _char.clan.getMemberByName(_char.Info.name).getType();
                            if (typeClan == Clan.TYPE_TOCTRUONG || typeClan == Clan.TYPE_TOCPHO) {
                                _char.clan.setAlert(alert);
                                _char.clan.getClanService().serverMessage("Player " + _char.Info.name + " vừa mới thay đổi thông báo");
                                client.sendMessage(Message.c((byte) -43));
                            }
                        }
                    }
                    break;
                case -95:
                    if (_char != null && _char.user != null) {
                        _char.inputCoinClan(msg);
                    }
                    break;
                case -99:
                    if (_char != null && _char.user != null) {
                        _char.changeClanType(msg);
                    }
                    break;
                case -97:
                    if (_char != null && _char.user != null) {
                        _char.moveOutClan(msg);
                    }
                    break;
                case -96:
                    if (_char != null && _char.user != null) {
                        _char.outClan();
                    }
                    break;
                case -68:
                    if (_char != null && _char.user != null) {
                        _char.moSkill(msg);
                    }
                    break;
                case -41:
                    if (_char != null && _char.user != null) {
                        Map.maps[_char.Info.mapReSpawm].addChar(_char);
                    }
                    break;
                case -47:
                    if (_char != null && _char.user != null) {
                        _char.security(msg);
                    }
                    break;
                case -40:
                    if (_char != null && _char.user != null) {
                        _char.requestMatchInfo(msg);
                    }
                    break;
                case -86:
                    if (_char != null && _char.user != null) {
                        _char.doibikip(msg);
                    }
                    break;

                case -69:
                    if (_char != null && _char.user != null) {
                        if (_char.Bag.vang < 500) {
                            service.warningMessage("Không đủ 500 vàng");
                            return;
                        }
                        if (_char.Info.countKham > 2) {
                            service.warningMessage("Chỉ khảm được thêm tối đa 3 loại");
                            return;
                        }
                        _char.addVang(-500);
                        _char.Info.countKham++;
                        service.openMsg122((byte) 87);
                    }
                    break;
                case -98:
                    if (_char != null && _char.user != null) {
                        _char.phatLuong(msg);
                    }
                    break;
                case -46:
                    if (_char != null && _char.user != null) {
                        _char.moneyToBox(msg);
                    }
                    break;
                default:
                    Log.debug("recv(123): " + msg.cmd);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadSceen() {
        try {

            client.sendMessage(Message.c((byte) -126));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

