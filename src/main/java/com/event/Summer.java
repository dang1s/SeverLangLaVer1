package com.event;

import MapService.world.CamThuat;
import MapService.world.Dungeon;
import MapService.world.SummerEvent;
import Service.HanderMessage;
import com.event.eventpoint.EventPoint;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.real.Npc;

import java.util.List;

public class Summer extends Event {
    public static final String TOP_LAM_KEM = "ice_cream";
    public static final String TOP_FISH = "topfish";

    public Summer() {
        setId(Event.SU_KIEN_HE);
        endTime.set(2024, 30, 5, 23, 59, 59);
        itemsThrownFromMonsters.add(1, 926);
        itemsThrownFromMonsters.add(1, 927);
        itemsThrownFromMonsters.add(1, 928);
        itemsThrownFromMonsters.add(1, 929);
        itemsThrownFromMonsters.add(80, -1);//ko rơi
        keyEventPoint.add(TOP_LAM_KEM);
        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
        keyEventPoint.add(TOP_FISH);
        menuKhaTienNu = "Làm hũ kem,1 cái,10 cái,100 cái,Hướng dẫn;Giải cứu Tiên Nhân,Tham gia(500 vàng),Từ chối;BXH Top Làm Kem;BXH Top Câu Cá;Kiểm tra điểm sự kiện;Đổi điểm,Sách nhẫn thuật đặc biệt, Thẻ đổi tên, Cải trang Madara, Cải trang Madara Lục Đạo, Cải trang Kakashi Lục Đạo";
    }

    private void makeIceCream(Char p, int amount) {
        int[][] itemRequires = new int[][]{{926, 5}, {927, 5}, {928, 5}, {929, 5}};
        int itemIdReceive = 930;
        boolean isDone = makeEventItem(p, amount, itemRequires, 20, 0, 0, itemIdReceive);
        if (isDone) {
            p.getEventPoint().addPoint(TOP_LAM_KEM, amount);
            p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, amount);
        }
    }

    @Override
    public void action(Char p, int type, int amount) {
        if (isEnded()) {
            p.service.serverMessage("Sự kiện đã kết thúc");
            return;
        }
        makeIceCream(p, amount);
    }

    @Override
    public void useItem(Char p, Item item) {
        switch (item.id) {
            case 930:
                if (p.getCountNullItemBag() == 0) {
                    p.warningBagFull();
                    return;
                }
                Npc npc = p.zone.getNpc(31);
                if (npc == null || Utlis.getRange(p.Info.cx, p.Info.cy, npc.cx, npc.cy) > 100) {
                    p.getService().serverMessage("Vui lòng đứng cạnh thầy Jiraiya để sử dụng hũ kem");
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
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
                        p.getService().sendTextNPC("Để làm 1 hũ kem cần: 5 Kem ốc quế + 5 Kem sữa + 5 Kem chocolate + 5 Kem dâu và 20 vàng", "");
                        break;
                }
                break;
            case 1:
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
                                    if (p2.Bag.vang < 500) {
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
                                    if (p.Bag.vang < 500) {
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
                                            p2.addVang(-500);
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
            case 2:
                viewTop(p, TOP_LAM_KEM, "Bảng xếp hạng SK", "%d. %s có %s điểm làm kem");
                break;
            case 3:
                viewTop(p, TOP_FISH, "Bảng xếp hạng Top câu cá", "%d. %s có %s điểm câu cá");
                break;
            case 4:
                p.getService().sendTextNPC("Bảng điểm SK của bạn: ", "Điểm làm kem: " + p.getEventPoint().getPoint(TOP_LAM_KEM) + "; Điểm câu cá: " + p.getEventPoint().getPoint(TOP_FISH)+";Điểm tiêu sài: "+p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI));
                break;
            case 5:
                int point = 0;
                switch (index2){
                    case 0:
                         point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                         if(point < 200){
                             p.service.serverMessage("Bạn không có đủ 200 điểm tiêu sài");
                             return;
                         }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,200);
                        Item tvc4 = new Item(723);
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
                        Item madara = new Item(530);
                        madara.strOptions = "68,100;70,100;0,1000;2,200;4,200;5,200";
                        p.addItem(madara);
                        p.msgAddItemBag(madara);
                        break;
                    case 3:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 4000){
                            p.service.serverMessage("Bạn không có đủ 2000 điểm tiêu sài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,4000);
                        Item madaralucdao = new Item(702);
                        madaralucdao.strOptions = "69,100;71,100;0,1000;2,200;4,200;5,200";
                        p.addItem(madaralucdao);
                        p.msgAddItemBag(madaralucdao);
                        break;
                    case 4:
                        point = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                        if(point < 4000){
                            p.service.serverMessage("Bạn không có đủ 2000 điểm tiêu sài");
                            return;
                        }
                        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI,4000);
                        Item kakashilucdao = new Item(528);
                        kakashilucdao.strOptions = "71,100;72,100;0,1000;2,200;4,200;5,200";
                        p.addItem(kakashilucdao);
                        p.msgAddItemBag(kakashilucdao);
                        break;
                }
                break;
        }
    }
}
