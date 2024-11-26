package com.event;

import Data.DataSkill;
import com.event.eventpoint.EventPoint;
import com.sg188.data.DataCenter;
import com.sg188.data.ItemOption;
import com.sg188.data.Skill;
import com.sg188.data.SkillTemplate;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.Main;

public class Halloween extends Event{
    private static final int DOI_KEO_BI = 0;
    private static final int DOI_KEO_BI_MA = 1;

    public static final String TOP_FISH = "topfish";

    public static final String TOP_KEO = "lamkeo";

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
        keyEventPoint.add(TOP_KEO);
        keyEventPoint.add(TOP_FISH);
        menuKhaTienNu = "Nhận nhiệm vụ,Giết cương thi,Phong ấn;Đổi kẹo bí, 1 cái, 10 cái, 100 cái, Hướng dẫn;Đổi kẹo bí ma, 1 cái, 10 cái, 100 cái, Hướng dẫn;Đổi điểm, Xem điểm, Nhẫn thuật sao chép thượng cấp, Con mắt shisui (SUSANO), Cải trang Bí Ngô, Cải trang Dracula, Bí kíp Bí Ngô;BXH làm kẹo;BXH Top Câu Cá";
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
        if (isDone) {
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
            p.getEventPoint().addPoint(TOP_KEO, amount);
        }
    }

    private void doiKeoBiMa(Char p, int amount) {
        int[][] itemRequires = new int[][]{{712, 10}, {713, 10}, {714, 10},{715, 10},{716, 10},{717, 10},{718, 10}};
        int itemIdReceive = 711;
        boolean isDone = makeEventItem(p, amount, itemRequires, 20, 0, 0, itemIdReceive);
        if (isDone) {
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
            p.getEventPoint().addPoint(TOP_KEO, amount);
        }
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
            case 920:
            case 921:
            case 922:
            case 923:
            case 924:
            case 925:
                if (p.getCountNullItemBag() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
                break;
        }
    }
    @Override
    public void menu(Char p, int index, int index2) {
        switch (index){
            case 0:
                switch (index2){
                    case 0:
                        if(p.Bag.vang < 1000){
                            p.service.alertMessage("Không đủ 1000 vàng");
                            return;
                        }
                        if(p.taskSeal){
                            p.getService().serverMessage("Bạn chưa hoàn thành nhiệm vụ cũ");
                            return;
                        }
                        String[] type = {"Cương thi Sasori","Cương thi Deidara","Cương thi Nagato","Cương thi Kisame","Cương thi Itachi"};
                        p.addVang(-1000);
                        p.taskSeal = true;
                        p.typeSeal = type[Utlis.nextInt(0,type.length-1)];
                        p.getService().alertMessage("Nhiệm vụ của bạn là giết "+p.typeSeal);
                        break;
                    case 1:
                        if(!p.taskSeal){
                            p.getService().serverMessage("Bạn chưa nhận nhiệm vụ phong ấn");
                            return;
                        }
                        if(p.stepSeal == 0){
                            p.getService().alertMessage("Bạn chưa hoàn thành nhiệm vụ giết "+p.typeSeal);
                            return;
                        }
                        p.taskSeal = false;
                        p.stepSeal = 0;
                        p.typeSeal ="";
                        Item thebai = new Item(711);
                        thebai.amount = 100;
                        thebai.isLock=true;
                        p.addItem(thebai);
                        p.msgAddItemBag(thebai);
                        p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, 70);
                        p.getEventPoint().addPoint(TOP_KEO, 100);
                        p.getService().serverMessage("Bạn nhận được 70 điểm tiêu xài và 100 điểm làm kẹo");
                        break;
                    case 2:
                        if(!p.taskSeal){
                            p.getService().serverMessage("Bạn chưa nhận nhiệm vụ phong ấn");
                            return;
                        }
                        p.taskSeal = false;
                        p.stepSeal = 0;
                        p.typeSeal ="";
                        p.getService().serverMessage("Đã huỷ nhiệm vụ phong ấn");
                        break;
                }
                break;
            case 1:
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
            case 2:
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
                        p.service.sendTextNPC("10 chữ H A L O W E N +  20 Vàng","");
                        break;
                }
                break;
            case 3:
                int point = 0;
                switch (index2){
                    case 0:
                        p.getService().sendTextNPC("Bảng điểm SK của bạn: ", "Điểm làm kẹo: " + p.getEventPoint().getPoint(TOP_KEO) + "; Điểm câu cá: " + p.getEventPoint().getPoint(TOP_FISH)+";Điểm tiêu sài: "+p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI));
                        break;
                    case 1:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 5000){
                            p.service.serverMessage("Bạn không có đủ 5000 điểm tiêu sài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,5000);
                        Item tvc4 = new Item(950);
                        p.addItem(tvc4);
                        p.msgAddItemBag(tvc4);
                        break;
                    case 2:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 5000){
                            p.service.serverMessage("Bạn không có đủ 5000 điểm tiêu sài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,5000);
                        Item conMatShisui = new Item(955);
                        p.addItem(conMatShisui);
                        p.msgAddItemBag(conMatShisui);
                        break;
                    case 3:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 5000){
                            p.service.serverMessage("Bạn không có đủ 5000 điểm tiêu sài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,5000);
                        Item caiTrangBiNgo = new Item(556);
                        caiTrangBiNgo.strOptions = "68,100;71,100;0,1000;2,200;4,200;5,200";
                        p.addItem(caiTrangBiNgo);
                        p.msgAddItemBag(caiTrangBiNgo);
                        break;
                    case 4:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 5000){
                            p.service.serverMessage("Bạn không có đủ 5000 điểm tiêu sài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,5000);
                        Item dracula = new Item(653);
                        dracula.strOptions = "70,100;69,100;0,1000;2,200;4,200;5,200";
                        p.addItem(dracula);
                        p.msgAddItemBag(dracula);
                        break;
                    case 5:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 5000){
                            p.service.serverMessage("Bạn không có đủ 5000 điểm tiêu sài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,5000);
                        Item biKipBiNgo = new Item(947);
                        biKipBiNgo.isLock = true;
                        biKipBiNgo.he = p.Info.idhe;
                        biKipBiNgo.addItemOption(new ItemOption(128, 0, 16000));
                        biKipBiNgo.addItemOption(new ItemOption(331, 300, 350));
                        biKipBiNgo.addItemOption(new ItemOption(0, 7000, 7500));
                        biKipBiNgo.addItemOption(new ItemOption(1, 7000, 7500));
                        if (biKipBiNgo.he == 1) {
                            biKipBiNgo.addItemOption(new ItemOption(109, 520, 550));
                            biKipBiNgo.addItemOption(new ItemOption(114, 820, 850));
                        } else if (biKipBiNgo.he == 2) {
                            biKipBiNgo.addItemOption(new ItemOption(110, 520, 550));
                            biKipBiNgo.addItemOption(new ItemOption(115, 820, 850));
                        } else if (biKipBiNgo.he == 3) {
                            biKipBiNgo.addItemOption(new ItemOption(111, 520, 550));
                            biKipBiNgo.addItemOption(new ItemOption(113, 420, 450));
                        } else if (biKipBiNgo.he == 4) {
                            biKipBiNgo.addItemOption(new ItemOption(112, 520, 550));
                            biKipBiNgo.addItemOption(new ItemOption(117, 420, 450));
                        } else if (biKipBiNgo.he == 5) {
                            biKipBiNgo.addItemOption(new ItemOption(108, 520, 550));
                            biKipBiNgo.addItemOption(new ItemOption(113, 420, 450));
                        }
                        biKipBiNgo.createItemOptions();
                        p.addItem(biKipBiNgo);
                        p.msgAddItemBag(biKipBiNgo);
                        break;
                }
                break;
            case 4:
                viewTop(p, TOP_KEO,"Bảng xếp hạng làm kẹo","%d. %s có %s sự kiện");
                break;
            case 5:
                viewTop(p, TOP_FISH, "Bảng xếp hạng Top câu cá", "%d. %s có %s điểm câu cá");
        }
    }
}
