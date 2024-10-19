package com.event;

import com.event.eventpoint.EventPoint;
import com.sg188.data.ItemOption;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.Main;

public class Halloween extends Event{
    private static final int DOI_KEO_BI = 0;
    private static final int DOI_KEO_BI_MA = 1;
    public Halloween() {
        setId(Event.HALLOWEEN);
        endTime.set(2024, 30, 5, 23, 59, 59);
        itemsThrownFromMonsters.add(1, 721);
        itemsThrownFromMonsters.add(1, 721);
        itemsThrownFromMonsters.add(1, 721);
        itemsThrownFromMonsters.add(2, 2);
        itemsThrownFromMonsters.add(2, 1);
        itemsThrownFromMonsters.add(2, 3);
        itemsThrownFromMonsters.add(94, -1);// ko rơi
        menuKhaTienNu = "Đổi kẹo bí, 1 cái, 10 cái, 100 cái, Hướng dẫn;Đổi kẹo bí ma, 1 cái, 10 cái, 100 cái, Hướng dẫn;Đổi bí kíp Huyền Vũ";
        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
    }

    @Override
    public void action(Char p, int type, int amount) {
        if (isEnded()) {
            p.service.serverMessage("Sự kiện đã kết thúc");
            return;
        }
        switch (type) {
            case DOI_KEO_BI:
                doiKeoBi(p, amount);
                break;
            case DOI_KEO_BI_MA:
                doiKeoBiMa(p, amount);
                break;
        }
    }

    private void doiKeoBi(Char p, int amount) {
        int[][] itemRequires = new int[][]{{712, 10}, {713, 10}, {714, 10},{715, 10},{716, 10},{717, 10},{718, 10}};
        int itemIdReceive = 851;
        boolean isDone = makeEventItem(p, amount, itemRequires, 0, 100000, 0, itemIdReceive);
//        if (isDone) {
//            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
//        }
    }

    private void doiKeoBiMa(Char p, int amount) {
        int[][] itemRequires = new int[][]{{712, 10}, {713, 10}, {714, 10},{715, 10},{716, 10},{717, 10},{718, 10}};
        int itemIdReceive = 711;
        boolean isDone = makeEventItem(p, amount, itemRequires, 10, 0, 0, itemIdReceive);
//        if (isDone) {
//            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount*2);
//        }
    }
    @Override
    public void useItem(Char p, Item item) {
        switch (item.id) {
            case 721:
                p.removeItem(item);
                p.msgUseItemBag(item);
                Item chu = new Item(Utlis.nextInt(712, 718));
                chu.isLock = true;
                p.addItem(chu);
                p.msgAddItemBag(chu);
                break;
            case 711:// vàng
                if (p.getCountNullItemBag() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
                break;
            case 851:// bạc
                if (p.getCountNullItemBag() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromCoinItem);
                break;
        }
    }
    @Override
    public void menu(Char p, int index, int index2) {
        switch (index){
            case 0:
                switch (index2){
                    case 0:
                        action(p,0,1);
                        break;
                    case 1:
                        action(p,0,10);
                        break;
                    case 2:
                        action(p,0,100);
                        break;
                    case 3:
                        p.service.sendTextNPC("10 chữ H A L O W E N +  100.000 Bạc","");
                        break;
                }
                break;
            case 1:
                switch (index2){
                    case 0:
                        action(p,1,1);
                        break;
                    case 1:
                        action(p,1,10);
                        break;
                    case 2:
                        action(p,1,100);
                        break;
                    case 3:
                        p.service.sendTextNPC("10 chữ H A L O W E N +  10 Vàng","");
                        break;
                }
                break;
            case 2:
                Item bkHuyenVu = new Item(947);
                bkHuyenVu.isLock = true;
                bkHuyenVu.he = p.Info.idhe;
                bkHuyenVu.addItemOption(new ItemOption(128, 0, 16000));
                bkHuyenVu.addItemOption(new ItemOption(331, 300, 350));
                bkHuyenVu.addItemOption(new ItemOption(0, 15300, 15500));
                bkHuyenVu.addItemOption(new ItemOption(1, 15300, 15500));
                if (bkHuyenVu.he == 1) {
                    bkHuyenVu.addItemOption(new ItemOption(109, 520, 550));
                    bkHuyenVu.addItemOption(new ItemOption(114, 820, 850));
                } else if (bkHuyenVu.he == 2) {
                    bkHuyenVu.addItemOption(new ItemOption(110, 520, 550));
                    bkHuyenVu.addItemOption(new ItemOption(115, 820, 850));
                } else if (bkHuyenVu.he == 3) {
                    bkHuyenVu.addItemOption(new ItemOption(111, 520, 550));
                    bkHuyenVu.addItemOption(new ItemOption(113, 420, 450));
                } else if (bkHuyenVu.he == 4) {
                    bkHuyenVu.addItemOption(new ItemOption(112, 520, 550));
                    bkHuyenVu.addItemOption(new ItemOption(117, 420, 450));
                } else if (bkHuyenVu.he == 5) {
                    bkHuyenVu.addItemOption(new ItemOption(108, 520, 550));
                    bkHuyenVu.addItemOption(new ItemOption(113, 420, 450));
                }
                bkHuyenVu.createItemOptions();
                p.addItem(bkHuyenVu);
                p.service.serverMessage("Đổi thành công Bí kíp Huyền Vũ");
                p.service.resetScreen();
                break;
            case 3:
                viewTop(p,EventPoint.DIEM_TIEU_XAI,"Bảng xếp hạng SK","%d. %s có %s sự kiện");
                break;
        }
    }
}
