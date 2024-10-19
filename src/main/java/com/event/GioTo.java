package com.event;

import com.event.eventpoint.EventPoint;
import com.sg188.data.ItemOption;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;

import java.time.ZonedDateTime;

public class GioTo extends Event {
    private static final int LAM_MAM_BAC = 0;
    private static final int LAM_MAM_VANG = 1;
    private ZonedDateTime start, end;
    public GioTo() {
        setId(Event.GIO_TO_HUNG_VUONG);
        endTime.set(2024, 30, 5, 23, 59, 59);
        itemsThrownFromMonsters.add(2, 815);
        itemsThrownFromMonsters.add(2, 816);
        itemsThrownFromMonsters.add(2, 817);
//        itemsThrownFromMonsters.add(0.2, 4);
//        itemsThrownFromMonsters.add(0.2, 5);
//        itemsThrownFromMonsters.add(0.2, 3);
        itemsThrownFromMonsters.add(60, -1);// ko rơi
        menuKhaTienNu = "Nhận nhiệm vụ,Giết cương thi,Phong ấn;Đổi thẻ,Đinh ba hệ Lôi,Đinh ba hệ Thổ,Đinh ba hệ Thủy,Đinh ba hệ Hỏa,Đinh ba hệ Phong, Đổi cải trang thủy tinh, Đổi cải trang sơn tinh;Làm mâm bạc, 1 cái, 10 cái, 100 cái, Hướng dẫn; Làm mâm vàng, 1 cái, 10 cái, 100 cái, Hướng dẫn;Đổi bánh ít bảo ú bảo, Đổi túi cấp 4, Đổi thẻ đổi tên;BXH";
        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);

    }
    private void makeMamBac(Char p, int amount) {
        int[][] itemRequires = new int[][]{{815, 5}, {816, 5}, {817, 5}};
        int itemIdReceive = 818;
        boolean isDone = makeEventItem(p, amount, itemRequires, 0, 120000, 0, itemIdReceive);
        if (isDone) {
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
        }
    }

    private void makeMamVang(Char p, int amount) {
        int[][] itemRequires = new int[][]{{815, 5}, {816, 5}, {817, 5}};
        int itemIdReceive = 819;
        boolean isDone = makeEventItem(p, amount, itemRequires, 15, 0, 0, itemIdReceive);
        if (isDone) {
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount*2);
        }
    }
    @Override
    public void action(Char p, int type, int amount) {
        if (isEnded()) {
            p.service.serverMessage("Sự kiện đã kết thúc");
            return;
        }
        switch (type) {
            case LAM_MAM_BAC:
                makeMamBac(p, amount);
                break;
            case LAM_MAM_VANG:
                makeMamVang(p, amount);
                break;
        }
    }
    @Override
    public void useItem(Char p, Item item) {
        switch (item.id) {
            case 818:
                if (p.getCountNullItemBag() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromCoinItem);
                break;
            case 819:
                if (p.getCountNullItemBag() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
                break;
        }
    }
    @Override
    public void menu(Char p,int index,int index2) {
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
                        Item thebai = new Item(823);
                        thebai.isLock=true;
                        p.addItem(thebai);
                        p.msgAddItemBag(thebai);
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
                Item thebai = p.FindItemBag(823);
                if(thebai==null){
                    p.service.alertMessage("Không có thẻ liên minh nhẫn giả");
                    return;
                }
                p.removeItem(thebai);
                Item dinhBa = new Item(916);
                switch (index2){
                    case 0:
                        if(p.getCountNullItemBag()<1){
                            p.warningBagFull();
                            return;
                        }
                        dinhBa.isLock = false;
                        dinhBa.he = 1;
                        dinhBa.level = 16;
                        dinhBa.strOptions = "122,10,30;151,50,200;152,50,200;117,100,300;110,50,150;158,1,3";
                        dinhBa.expiry = EXPIRE_3_DAY+System.currentTimeMillis();
                        dinhBa.createItemOptions();
                        p.addItem(dinhBa);
                        p.msgAddItemBag(dinhBa);
                        break;
                    case 1:
                        if(p.getCountNullItemBag()<1){
                            p.warningBagFull();
                            return;
                        }
                        dinhBa.isLock = false;
                        dinhBa.he = 2;
                        dinhBa.level = 16;
                        dinhBa.strOptions = "122,10,30;151,50,200;152,50,200;117,100,300;110,50,150;158,1,3";
                        dinhBa.expiry = EXPIRE_3_DAY+System.currentTimeMillis();
                        dinhBa.createItemOptions();
                        p.addItem(dinhBa);
                        p.msgAddItemBag(dinhBa);
                        break;
                    case 2:
                        if(p.getCountNullItemBag()<1){
                            p.warningBagFull();
                            return;
                        }
                        dinhBa.isLock = false;
                        dinhBa.he = 3;
                        dinhBa.level = 16;
                        dinhBa.strOptions = "122,10,30;151,50,200;152,50,200;117,100,300;110,50,150;158,1,3";
                        dinhBa.expiry = EXPIRE_3_DAY+System.currentTimeMillis();
                        dinhBa.createItemOptions();
                        p.addItem(dinhBa);
                        p.msgAddItemBag(dinhBa);
                        break;
                    case 3:
                        if(p.getCountNullItemBag()<1){
                            p.warningBagFull();
                            return;
                        }
                        dinhBa.isLock = false;
                        dinhBa.he = 4;
                        dinhBa.level = 16;
                        dinhBa.strOptions = "122,10,30;151,50,200;152,50,200;117,100,300;110,50,150;158,1,3";
                        dinhBa.expiry = EXPIRE_3_DAY+System.currentTimeMillis();
                        dinhBa.createItemOptions();
                        p.addItem(dinhBa);
                        p.msgAddItemBag(dinhBa);
                        break;
                    case 4:
                        if(p.getCountNullItemBag()<1){
                            p.warningBagFull();
                            return;
                        }
                        dinhBa.isLock = false;
                        dinhBa.he = 5;
                        dinhBa.level = 16;
                        dinhBa.strOptions = "122,10,30;151,50,200;152,50,200;117,100,300;110,50,150;158,1,3";
                        dinhBa.expiry = EXPIRE_3_DAY+System.currentTimeMillis();
                        dinhBa.createItemOptions();
                        p.addItem(dinhBa);
                        p.msgAddItemBag(dinhBa);
                        break;
                    case 5:
                        if(p.getCountNullItemBag()<1){
                            p.warningBagFull();
                            return;
                        }
                        Item sonTinh = new Item(747);
                        sonTinh.isLock = false;
                        sonTinh.addItemOption(new ItemOption(0,400,500));
                        sonTinh.addItemOption(new ItemOption(2,50,100));
                        sonTinh.addItemOption(new ItemOption(3,100,200));
                        sonTinh.addItemOption(new ItemOption(209,50,100));
                        /*sonTinh.expiry = EXPIRE_7_DAY+System.currentTimeMillis();*/
                        sonTinh.expiry = -1;
                        sonTinh.createItemOptions();
                        p.addItem(sonTinh);
                        p.msgAddItemBag(sonTinh);
                        break;
                    case 6:
                        if(p.getCountNullItemBag()<1){
                            p.warningBagFull();
                        }
                        Item thuyTinh = new Item(748);
                        thuyTinh.isLock = false;
                        thuyTinh.addItemOption(new ItemOption(0,400,500));
                        thuyTinh.addItemOption(new ItemOption(2,50,100));
                        thuyTinh.addItemOption(new ItemOption(3,100,200));
                        thuyTinh.addItemOption(new ItemOption(209,50,100));
                        /*thuyTinh.expiry =  EXPIRE_7_DAY+System.currentTimeMillis();*/
                        thuyTinh.expiry = -1;
                        thuyTinh.createItemOptions();
                        p.addItem(thuyTinh);
                        p.msgAddItemBag(thuyTinh);
                        break;

                }
                break;
            case 2:
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
                        p.service.sendTextNPC("5 Ngà Voi + 5 Cựa Gà + 5 Hồng Mao + 120.000 Bạc","");
                        break;
                }
                break;
            case 3:
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
                        p.service.sendTextNPC("5 Ngà Voi + 5 Cựa Gà + 5 Hồng Mao + 15 Vàng","");
                        break;
                }
                break;
            case 4:
                switch (index2){
                    case 0:
                        if(p.getCountNullItemBag()<1){
                            p.warningBagFull();
                        }
                        Item banhItBao = p.FindItemBag(368);
                        Item banhUBao = p.FindItemBag(369);
                        if(banhItBao == null&&banhUBao==null){
                            p.getService().serverMessage("Không tìm thấy bánh ít bảo hoac bánh ú bảo");
                            return;
                        }
                        if(banhItBao!=null&&banhItBao.getAmount() < 20&&banhUBao==null){
                            p.getService().alertMessage("Cần 20 bánh ít bảo để đổi túi mở rộng cấp 4");
                            return;
                        }
                        if(banhUBao!=null&&banhUBao.getAmount() < 20&&banhItBao==null){
                            p.getService().alertMessage("Cần 20 bánh ú bảo để đổi túi mở rộng cấp 4");
                            return;
                        }
                        if(banhItBao!=null&& banhItBao !=null &&banhItBao.getAmount() <20&&banhUBao.getAmount() < 20){
                            p.getService().alertMessage("Cần 20 bánh ú bảo hoac bánh ít bảo để đổi túi mở rộng cấp 4");
                            return;
                        }
                        if(banhItBao!=null&& banhItBao.getAmount() >= 20){
                            p.removeItemByAmount(banhItBao,20);
                        } else if (banhUBao!=null&& banhUBao.getAmount() >= 20) {
                            p.removeItemByAmount(banhUBao,20);
                        }
                        Item tvc4 = new Item(468);
                        p.addItem(tvc4);
                        p.msgAddItemBag(tvc4);
                        break;
                    case 1:
                        banhItBao = p.FindItemBag(368);
                        banhUBao = p.FindItemBag(369);
                        if(banhItBao == null&&banhUBao==null){
                            p.getService().serverMessage("Không tìm thấy bánh ít bảo hoac bánh ú bảo");
                            return;
                        }
                        if(banhItBao!=null&&banhItBao.getAmount() < 40&&banhUBao==null){
                            p.getService().alertMessage("Cần 40 bánh ít bảo để đổi thẻ đổi tên");
                            return;
                        }
                        if(banhUBao!=null&&banhUBao.getAmount() < 40&&banhItBao==null){
                            p.getService().alertMessage("Cần 40 bánh ú bảo để đổi thẻ đổi tên");
                            return;
                        }
                        if(banhItBao!=null&& banhItBao !=null &&banhItBao.getAmount() <40&&banhUBao.getAmount() < 40){
                            p.getService().alertMessage("Cần 40 bánh ú bảo hoac bánh ít bảo để đổi thẻ đổi tên");
                            return;
                        }
                        if(banhItBao!=null&& banhItBao.getAmount() >= 40){
                            p.removeItemByAmount(banhItBao,40);
                        } else if (banhUBao!=null&& banhUBao.getAmount() >= 40) {
                            p.removeItemByAmount(banhUBao,40);
                        }
                        Item changeName = new Item(437);
                        changeName.isLock=true;
                        p.addItem(changeName);
                        p.msgAddItemBag(changeName);
                        break;
                }
                break;
            case 5:
                viewTop(p,EventPoint.DIEM_TIEU_XAI,"Bảng xếp hạng SK","%d. %s có %s điểm sính lễ");
                break;
        }
    }
}