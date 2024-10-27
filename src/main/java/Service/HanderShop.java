package Service;

import EventClick.ClickEvent;
import Manager.Manager;
import SqlConnection.DBData;
import com.sg188.Shop.DiscountStore;
import com.sg188.Shop.ItemShop;
import com.sg188.Shop.Store;
import com.sg188.data.DataCenter;
import com.sg188.data.ItemOption;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.lib.Message;
import com.sg188.task.TaskName;

import java.io.IOException;
import java.sql.SQLException;

public class HanderShop {

    public static void BuyShop(Char _myChar, Message msg) {
        try {
            if (_myChar.isSecurity && !_myChar.isUnlockSecurity) {
                _myChar.service.warningMessage("Vui lòng mở khoá bảo mật trước khi sử dụng tính năng này");
                return;
            }

            short idBuy = msg.readShort();
            short quantity = msg.readShort();
            if(quantity <= 0){
                return;
            }
            ItemShop itemShop = Store.getInstance().find(idBuy);
            if(itemShop== null && idBuy < -5000){
                itemShop = DiscountStore.getInstance().find(idBuy);
            }
            else if(itemShop== null && idBuy < 0){
                itemShop = Manager.gI().findShopRank(idBuy);
            }
            if (itemShop == null) {
                _myChar.service.warningMessage("Item không tồn tại trong cửa hàng");
                return;
            }
            if(itemShop.TypeShop == 39){
                if(!_myChar.checkItemShopRank(idBuy)){
                    if(_myChar.Info.rank < itemShop.yeuCau){
                        _myChar.getService().warningMessage("Bạn không đủ mức rank để mua");
                        return;
                    }
                    if(_myChar.Bag.vang < itemShop.Vang){
                        _myChar.getService().warningMessage("Bạn không đủ vàng");
                        return;
                    }
                    if (_myChar.getCountNullItemBag() < 8) {
                        _myChar.warningBagFull();
                        return;
                    }
                    _myChar.addVang(-itemShop.Vang);
                    _myChar.shoprank.add(itemShop.id);
                    for(Item item: itemShop.items){
                        if (item.id == 163) {
                            _myChar.addBacKhoa(item.amount);
                        }
                        else if (item.id == 191) {
                            _myChar.addBac(item.amount);
                        }
                        else if (item.id == 192) {
                            _myChar.addVangKhoa(item.amount);
                        }
                        else if (item.id == 193) {
                            _myChar.addVang(item.amount);
                        }else {
                            if (!item.getItemTemplate().isXepChong) {
                                for (int i = 0; i < item.amount; i++) {
                                    Item clone = item.cloneItem();
                                    clone.setAmount(1);
                                    if(clone.expiry > 0){
                                        clone.expiry+=System.currentTimeMillis();
                                    }
                                    _myChar.addItem(clone);
                                    _myChar.user.session.sendMessage(HanderMessage.BuyShop(_myChar, clone));
                                }
                            } else {
                                Item clone = item.cloneItem();
                                clone.createItemOptions();
                                if (_myChar.addItem(clone)) {
                                    _myChar.user.session.sendMessage(HanderMessage.BuyShop(_myChar, clone));
                                }
                            }
                        }
                    }
                    ClickEvent.shop39(_myChar, (byte) 39);
                }else {
                    _myChar.getService().warningMessage("Bạn đã mua rồi");
                    return;
                }
                return;
            }

            if(itemShop.TypeShop == 40) {
                if(itemShop.conLai <= 0){
                    _myChar.getService().warningMessage("Hết hàng mất rồi");
                    return;
                }
                Item it = createItem(_myChar, itemShop, quantity);
                if (_myChar.getCountNullItemBag() < 1) {
                    _myChar.warningBagFull();
                    return;
                }
                if (!it.getItemTemplate().isXepChong && it.amount > _myChar.getCountNullItemBag()) {
                    _myChar.warningBagFull();
                    return;
                }
                if(it.isTypeTrangBi()){
                    it.he = itemShop.he;
                }

                if (deductCurrency(_myChar, itemShop, it)) return;

                if(DiscountStore.getInstance().purchaseItem(itemShop.id,1)) { //check trừ số lượng trên db
                    Item clone = it.cloneItem();
                    clone.createItemOptions();
                    if (_myChar.addItem(clone)) {
                        _myChar.user.session.sendMessage(HanderMessage.BuyShop(_myChar, clone));
                    }
                    //ClickEvent.shop40(_myChar, (byte) 40);
                } else {
                    _myChar.getService().warningMessage("Hết hàng mất rồi");
                }
                return;
            }
            Item it = createItem(_myChar, itemShop, quantity);

            if (_myChar.getCountNullItemBag() < 1) {
                _myChar.warningBagFull();
                return;
            }

            if (!hasSufficientTaiPhu(_myChar, it)) {
                _myChar.getService().sendMessage(HanderMessage.SendThongBao("Không đủ điểm Tài phú", HanderMessage.RED_MID));
                return;
            }

            if (itemShop.yeuCau > 0 && !CheckDiem(_myChar, itemShop.TypeShop, itemShop.yeuCau)) {
                _myChar.getService().sendMessage(HanderMessage.SendThongBao("Không đủ điểm Hokage " + getNameShop(itemShop.TypeShop), HanderMessage.RED_MID));
                return;
            }

            if (!it.getItemTemplate().isXepChong && it.amount > _myChar.getCountNullItemBag()) {
                _myChar.warningBagFull();
                return;
            }
            if(it.isTypeTrangBi()){
                it.he = itemShop.he;
            }

            if (deductCurrency(_myChar, itemShop, it)) return;

            CheckItem(it, itemShop.TypeShop >= 8 && itemShop.TypeShop <= 17);

            handleItemPurchase(_myChar, it, itemShop);

            if (itemShop.TypeShop == 18) {
                _myChar.msgUpdateStatusChar();
            }

            updateTaskProgress(_myChar, itemShop);

        } catch (IOException ex) {
            Log.error("Loi buy shop " + ex);
        }
    }

    private static Item createItem(Char _myChar, ItemShop itemShop, short quantity) {
        Item it = new Item(itemShop.itemID);
        it.he = determineItemHe(_myChar, itemShop);
        it.amount = quantity;
        it.isLock = itemShop.isLock;
        it.expiry = itemShop.expire > 0 ? itemShop.expire + System.currentTimeMillis() : -1;
        if (itemShop.TypeShop == 30 || itemShop.TypeShop == 39) {
            it.strOptions = itemShop.strOption;
            it.createItemOptions();
        }
        return it;
    }

    private static byte determineItemHe(Char _myChar, ItemShop itemShop) {
        if ((itemShop.TypeShop >= 8 && itemShop.TypeShop <= 17) || (itemShop.TypeShop >= 21 && itemShop.TypeShop <= 29)) {
            return _myChar.id_he;
        } else if (itemShop.TypeShop == 30) {
            return itemShop.he;
        } else {
            return (byte) Utlis.nextInt(1, 5);
        }
    }

    private static boolean hasSufficientTaiPhu(Char _myChar, Item it) {
        return it.getItemTemplate().taiPhuNeed <= _myChar.GetTaiPhu();
    }

    private static boolean deductCurrency(Char _myChar, ItemShop itemShop, Item it) {
        if (itemShop != null) {
            if (itemShop.Bac > 0 && (_myChar.Bag.bac > itemShop.Bac * it.amount)) {
                _myChar.Bag.bac -= itemShop.Bac * it.amount;
            } else if (itemShop.BacKhoa > 0 && (_myChar.Bag.bacKhoa > itemShop.BacKhoa * it.amount)) {
                _myChar.Bag.bacKhoa -= itemShop.BacKhoa * it.amount;
            } else if (itemShop.Vang > 0 && (_myChar.Bag.vang > itemShop.Vang * it.amount)) {
                _myChar.Bag.vang -= itemShop.Vang * it.amount;
                _myChar.phucLoi.tieuNgay += itemShop.Vang * it.amount;
                _myChar.phucLoi.tieuTuan += itemShop.Vang * it.amount;
            } else if (itemShop.VangKhoa > 0 && (_myChar.Bag.vangKhoa > itemShop.VangKhoa * it.amount)) {
                _myChar.Bag.vangKhoa -= itemShop.VangKhoa * it.amount;
            } else if (itemShop.TinhThach > 0 && _myChar.removeItems(160, itemShop.TinhThach)) {
                _myChar.msgSendArrItemBag();
            } else {
                _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không đủ số tiền", HanderMessage.RED_MID));
                return true;
            }
        }
        return false;
    }

    private static void CheckItem(Item it, boolean hoakge) {
        if (it.isVuKhi()) {
            Item.setOptionsVuKhi(it, it.getItemTemplate().levelNeed);
        } else if (it.isPhuKien() || it.isTrangBi()) {
            Item.setOptionsTrangBiPhuKien(it, it.getItemTemplate().levelNeed);
        } else {
            setSpecialItemOptions(it);
        }

        if (hoakge) {
            Item.GetOptionHokage(it);
            it.strOptions += ";148,0";
        }

        it.createItemOptions();
        if (it.id == 619) {
            it.strOptions = "283,0";
        }
    }

    private static void setSpecialItemOptions(Item it) {
        switch (it.getItemTemplate().type) {
            case 12:
                Item.getOptionAoChoang(it);
                break;
            case 14:
                Item.getOptionCaiTrang(it);
                break;
            case 10:
                Item.getOptionThuNuoi(it);
                break;
            case 11:
                Item.getOptionBiKip(it);
                break;
            case 15:
                Item.getOptionTanTo(it);
                break;
        }
    }

    private static void handleItemPurchase(Char _myChar, Item it, ItemShop itemShop) {
        if (itemShop.TypeShop == 39 && !itemShop.strOption.isEmpty()) {
            it.amount = itemShop.amount;
        }
        if (!it.getItemTemplate().isXepChong) {
            for (int i = 0; i < it.amount; i++) {
                Item clone = it.cloneItem();
                clone.setAmount(1);
                _myChar.addItem(clone);
                _myChar.user.session.sendMessage(HanderMessage.BuyShop(_myChar, clone));
            }
        } else {
            if (_myChar.addItem(it)) {
                _myChar.user.session.sendMessage(HanderMessage.BuyShop(_myChar, it));
            }
        }
    }

    private static void updateTaskProgress(Char _myChar, ItemShop itemShop) {
        if (_myChar.taskId == TaskName.NV_LAM_NGUOI_TOT_BUNG && _myChar.taskMain != null && _myChar.taskMain.index == 0) {
            if (itemShop.itemID == _myChar.taskMain.vStep.get(_myChar.taskMain.index).idItem) {
                _myChar.updateTaskCount(1);
            }
        }
    }

    private static String getNameShop(int type) {
        switch (type) {
            case 8: return "Vũ khí";
            case 9: return "Đai trán";
            case 10: return "Áo";
            case 11: return "Bao tay";
            case 12: return "Quần";
            case 13: return "Giày";
            case 14: return "Dây thừng";
            case 15: return "Móc sắt";
            case 16: return "Ống tiêu";
            case 17: return "Túi nhẫn giả";
            default: return "";
        }
    }

    private static boolean CheckDiem(Char myChar, int type, int require) {
        switch (type) {
            case 8: return myChar.Info.pointHokage.Vk >= require;
            case 9: return myChar.Info.pointHokage.Dai >= require;
            case 10: return myChar.Info.pointHokage.Ao >= require;
            case 11: return myChar.Info.pointHokage.BaoTay >= require;
            case 12: return myChar.Info.pointHokage.Quan >= require;
            case 13: return myChar.Info.pointHokage.Giay >= require;
            case 14: return myChar.Info.pointHokage.Day >= require;
            case 15: return myChar.Info.pointHokage.Moc >= require;
            case 16: return myChar.Info.pointHokage.OngTieu >= require;
            case 17: return myChar.Info.pointHokage.Tui >= require;
            default: return false;
        }
    }
}
