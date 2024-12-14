package com.event;

import MapService.world.SummerEvent;
import Service.HanderMessage;
import com.event.eventpoint.EventPoint;
import com.sg188.data.ItemOption;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.real.Npc;

import java.util.List;
public class Christmas extends Event{
    public static final String TOP_MAKE_SOCKS = "make_socks";
    private static final int DOI_VO = 0;
    private static final int DOI_BAO_TAY = 1;
    //public static final String TOP_FISH = "topfish";

    public Christmas() {
        setId(Event.CHRISTMAST);
        endTime.set(2024, 30, 5, 23, 59, 59);
        itemsThrownFromMonsters.add(1, 792);
        itemsThrownFromMonsters.add(1, 793);
        itemsThrownFromMonsters.add(1, 794);
        itemsThrownFromMonsters.add(70, -1);//ko rơi
        keyEventPoint.add(TOP_MAKE_SOCKS);
        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
        //keyEventPoint.add(TOP_FISH);
//        menuKhaTienNu = "Làm hũ kem,1 cái,10 cái,100 cái,Hướng dẫn;Giải cứu Tiên Nhân,Tham gia(500 vàng),Từ chối;BXH Top Làm Kem;BXH Top Câu Cá;Kiểm tra điểm sự kiện;Đổi điểm,Sách nhẫn thuật đặc biệt, Thẻ đổi tên, Cải trang Madara, Cải trang Madara Lục Đạo, Cải trang Kakashi Lục Đạo, Bí kíp Bí Ngô";
        menuKhaTienNu = "Làm vớ,1 cái,10 cái,100 cái,Hướng dẫn;Làm găng tay,1 cái,10 cái,100 cái,Hướng dẫn;Giải cứu Tiên Nhân,Tham gia(5000 vàng),Từ chối;BXH Top Làm Vớ;Kiểm tra điểm sự kiện;Đổi điểm, Nhẫn thuật sao chép thượng cấp, Thẻ đổi tên, Cải trang Noel, Đá 12, Vé vận may VIP, Bí kíp Bí Ngô";
    }

    private void makeSocks(Char p, int amount) {
        int[][] itemRequires = new int[][]{{792, 5}, {793, 5}, {794, 5}};
        int itemIdReceive = 795;
        boolean isDone = makeEventItem(p, amount, itemRequires, 100, 0, 0, itemIdReceive);
        if (isDone) {
            p.getEventPoint().addPoint(TOP_MAKE_SOCKS, amount);
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
        }
    }

    private void makeGloves(Char p, int amount) {
        int[][] itemRequires = new int[][]{{792, 5}, {793, 5}, {794, 5}};
        int itemIdReceive = 796;
        boolean isDone = makeEventItem(p, amount, itemRequires, 0, 100000, 0, itemIdReceive);
//        if (isDone) {
//            p.getEventPoint().addPoint(TOP_MAKE_SOCKS, amount);
//            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
//        }
    }

    @Override
    public void action(Char p, int type, int amount) {
        if (isEnded()) {
            p.service.serverMessage("Sự kiện đã kết thúc");
            return;
        }
        switch (type) {
            case DOI_VO:
                makeSocks(p, amount);
                break;
            case DOI_BAO_TAY:
                makeGloves(p,amount);
                break;
        }
    }

    @Override
    public void useItem(Char p, Item item) {
        switch (item.id) {
            case 795:
                if (p.getCountNullItemBag() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
                break;
            case 796:
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
        switch (index) {
            case 0:
                switch (index2) {
                    case 0:
                        action(p, 0, 1);
                        break;
                    case 1:
                        action(p, 0, 10);
                        break;
                    case 2:
                        action(p, 0, 100);
                        break;
                    case 3:
                        p.getService().sendTextNPC("Để làm 1 đôi vớ cần: 5 cuộn len trắng + 5 cuộn len đỏ + 5 chỉ nhung và 200 vàng", "");
                        break;
                }
                break;
            case 1:
                switch (index2) {
                    case 0:
                        action(p, 1, 1);
                        break;
                    case 1:
                        action(p, 1, 10);
                        break;
                    case 2:
                        action(p, 1, 100);
                        break;
                    case 3:
                        p.getService().sendTextNPC("Để làm 1 đôi găng cần: 5 cuộn len trắng + 5 cuộn len đỏ + 5 chỉ nhung và 100k bạc", "");
                        break;
                }
                break;
            case 2:
                switch (index2) {
                    case 0:
                        if (p.idDungeonEvent != -1) {
                            SummerEvent summerEvent = SummerEvent.findSummerEventById(p.idDungeonEvent);
                            if (summerEvent != null && !summerEvent.isClosed()) {
                                p.addWorld(summerEvent);
                                summerEvent.join(p);
                                return;
                            } else {
                                p.idDungeonEvent = -1;
                            }
                        }
                        if (p.level() > 44) {
                            if (p.getGroup() != null) {
                                boolean check = false;
                                boolean checkVang = false;
                                boolean checkLevel = true;
                                int level = 0;
                                List<Char> chars = p.getGroup().getCharsInZone(p.zone.map.mapID, p.zone.zoneID);
                                if (chars.size() < p.getGroup().getChars().size()) {
                                    p.service.alertMessage("Vui lòng tập hợp đủ thành viên lại");
                                    return;
                                }
                                if (p.level() < 45) {
                                    p.service.alertMessage("Ban không đủ level tham gia phó bản");
                                    return;
                                }
                                for (Char p2 : chars) {
                                    if (p2.idDungeonEvent != -1) {
                                        check = true;
                                        return;
                                    }
                                    if (p2.level() < 45) {
                                        checkLevel = false;
                                        return;
                                    }
                                    if (p2.Bag.vang < 5000) {
                                        checkVang = true;
                                        return;
                                    }
                                    level += p2.level();
                                }
                                if (check) {
                                    p.service.alertMessage("Có thành viên trong tổ đội đã tham gia một phó bản giải cứu thầy khác");
                                    return;
                                }
                                if (!checkLevel) {
                                    p.service.alertMessage("Có thành viên trong tổ đội không đủ level gia giải cứu thầy ");
                                    return;
                                }
                                if (checkVang) {
                                    p.service.alertMessage("Có thành viên trong tổ đội không đủ 500 vàng");
                                    return;
                                }
                                if (p.getGroup().memberGroups.get(0).charId == p.id) {
                                    if (p.Bag.vang < 5000) {
                                        p.getService().serverMessage("Bạn không có đủ 500 vàng");
                                        return;
                                    }
                                    level = level / chars.size();
                                    SummerEvent summerEvent = new SummerEvent(level);
                                    SummerEvent.addDungeon(summerEvent);
                                    if (summerEvent != null) {
                                        if (summerEvent.isClosed()) {
                                            return;
                                        }
                                        for (Char p2 : chars) {
                                            if (p2.idDungeonEvent == -1) {
                                                p2.idDungeonEvent = summerEvent.getId();
                                                Log.debug(summerEvent.getId());
                                            }
                                            p2.addVang(-5000);
                                            p2.addWorld(summerEvent);
                                            summerEvent.join(p2);
                                            summerEvent.addMember(p2);
                                        }

                                    }

                                } else {
                                    p.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không phải đội trưởng", HanderMessage.WHITE));
                                }
                            } else {
                                p.user.session.sendMessage(HanderMessage.SendThongBao("Bạn không có tổ đội", HanderMessage.WHITE));
                            }
                        } else {
                            p.service.serverMessage("Phó bản này này không phù hợp với cấp độ của bạn");
                        }
                        break;
                    case 1:
                        break;
                }
                break;
            case 3:
                viewTop(p, TOP_MAKE_SOCKS, "Bảng xếp hạng SK", "%d. %s có %s điểm làm vớ");
                break;
            case 4:
                p.getService().sendTextNPC("Bảng điểm SK của bạn: ", "Điểm làm vớ: " + p.getEventPoint().getPoint(TOP_MAKE_SOCKS) + "; Điểm câu cá: " + p.getEventPoint().getPoint(TOP_MAKE_SOCKS)+";Điểm tiêu xài: "+p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI));
                break;
            case 5:
                int point = 0;
                switch (index2){
                    case 0:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 2000){
                            p.service.serverMessage("Bạn không có đủ 2000 điểm tiêu xài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,2000);
                        Item tvc4 = new Item(940);
                        p.addItem(tvc4);
                        p.msgAddItemBag(tvc4);
                        break;
                    case 1:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 1000){
                            p.service.serverMessage("Bạn không có đủ 1000 điểm tiêu sài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,1000);
                        Item changeName = new Item(437);
                        p.addItem(changeName);
                        p.msgAddItemBag(changeName);
                        break;
                    case 2:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 2000){
                            p.service.serverMessage("Bạn không có đủ 2000 điểm tiêu sài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,2000);
                        Item ctNoel = new Item(662);
                        ctNoel.strOptions = "68,100;70,100;0,1000;2,200;4,200;5,200";
                        p.addItem(ctNoel);
                        p.msgAddItemBag(ctNoel);
                        break;
                    case 3:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 500){
                            p.service.serverMessage("Bạn không có đủ 500 điểm tiêu xài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,500);
//                        Item madaralucdao = new Item(702);
//                        madaralucdao.strOptions = "69,100;71,100;0,1000;2,200;4,200;5,200";
//                        p.addItem(madaralucdao);
//                        p.msgAddItemBag(madaralucdao);
                        Item da12 = new Item(11);
                        da12.amount = 1;
                        da12.isLock = true;
                        p.addItem(da12);
                        p.msgAddItemBag(da12);
                        break;
                    case 4:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 2000){
                            p.service.serverMessage("Bạn không có đủ 2000 điểm tiêu xài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,2000);
//                        Item kakashilucdao = new Item(528);
//                        kakashilucdao.strOptions = "71,100;72,100;0,1000;2,200;4,200;5,200";
//                        p.addItem(kakashilucdao);
//                        p.msgAddItemBag(kakashilucdao);

                        Item veVIP = new Item(966);
                        veVIP.amount = 1;
                        veVIP.isLock = true;
                        p.addItem(veVIP);
                        p.msgAddItemBag(veVIP);
                        break;
                    case 5:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 4000){
                            p.service.serverMessage("Bạn không có đủ 4000 điểm tiêu xài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,4000);
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
        }
    }
}
