package com.event;

import com.sg188.real.Char;
import com.sg188.real.Item;

public class SummerEvent2026 extends Event {
    public SummerEvent2026() {
        setId(Event.SU_KIEN_HE);
        endTime.set(2026, 31, 8, 23, 59, 59);
        loadItemsFromMonsters();
        menuKhaTienNu = "Làm Kem ốc qués,1 cái,10 cái,100 cái,Hướng dẫn;Làm Kem sữa,1 cái,10 cái,100 cái,Hướng dẫn;Làm Kem chocolate,1 cái,10 cái,100 cái,Hướng dẫn;Làm Kem dâu,1 cái,10 cái,100 cái,Hướng dẫn;Làm Hũ kem dầm,1 cái,10 cái,100 cái,Hướng dẫn;Thu Thập Biển,Đổi Vật Phẩm,Hướng dẫn";
    }

    private void loadItemsFromMonsters() {
        // Load vật phẩm làm kem - 0.5% mỗi loại
        itemsThrownFromMonsters.add(0.5, 897); // Bột
        itemsThrownFromMonsters.add(0.5, 898); // Đường
        itemsThrownFromMonsters.add(0.5, 899); // Trứng
        itemsThrownFromMonsters.add(0.5, 892); // Bơ
        itemsThrownFromMonsters.add(0.5, 895); // Dâu tây
        itemsThrownFromMonsters.add(0.5, 337); // Hạt ca cao
        
        // Load vật phẩm biển - 0.2% mỗi loại
        itemsThrownFromMonsters.add(0.2, 924); // Sao biển xanh
        itemsThrownFromMonsters.add(0.2, 925); // Cua hoàng đế
        
        // 96.4% không rơi vật phẩm sự kiện
        itemsThrownFromMonsters.add(96.4, -1);
    }

    private void makeIceCreamCone(Char p, int amount) {
        int[][] itemRequires = new int[][]{{897, 5}, {898, 5}, {899, 5}};
        int itemIdReceive = 926;
        makeEventItem(p, amount, itemRequires, 0, 200000, 0, itemIdReceive);
    }

    private void makeIceCreamMilk(Char p, int amount) {
        int[][] itemRequires = new int[][]{{892, 5}, {898, 5}, {899, 5}};
        int itemIdReceive = 927;
        makeEventItem(p, amount, itemRequires, 0, 200000, 0, itemIdReceive);
    }

    private void makeIceCreamChocolate(Char p, int amount) {
        int[][] itemRequires = new int[][]{{337, 5}, {898, 5}, {892, 5}};
        int itemIdReceive = 928;
        makeEventItem(p, amount, itemRequires, 0, 200000, 0, itemIdReceive);
    }

    private void makeIceCreamStrawberry(Char p, int amount) {
        int[][] itemRequires = new int[][]{{895, 5}, {898, 5}, {892, 5}};
        int itemIdReceive = 929;
        makeEventItem(p, amount, itemRequires, 0, 200000, 0, itemIdReceive);
    }

    private void makeIceCreamMash(Char p, int amount) {
        int[][] itemRequires = new int[][]{{895, 10}, {897, 10}, {898, 10}};
        int itemIdReceive = 930;
        makeEventItem(p, amount, itemRequires, 0, 0, 100, itemIdReceive);
    }

    private void exchangeBeachItems(Char p) {
        // Đổi vật phẩm biển lấy Sakura Tiệc Bãi Biển (585)
        int[][] fishRequirements = {{920, 99}, {921, 99}, {922, 99}, {923, 99}, {924, 99}, {925, 99}};
        boolean hasAllItems = true;
        
        // Kiểm tra xem người chơi có đủ vật phẩm không
        for (int[] req : fishRequirements) {
            Item item = p.FindItemBag(req[0]);
            if (item == null || item.amount < req[1]) {
                hasAllItems = false;
                break;
            }
        }
        
        if (hasAllItems) {
            if (p.getCountNullItemBag() == 0) {
                p.warningBagFull();
                return;
            }
            
            // Xóa các vật phẩm
            for (int[] req : fishRequirements) {
                Item item = p.FindItemBag(req[0]);
                if (item != null && item.amount >= req[1]) {
                    item.amount -= req[1];
                    if (item.amount <= 0) {
                        p.removeItem(item);
                    } else {
                        p.msgUseItemBag(item);
                    }
                }
            }
            
            // Thêm Sakura Tiệc Bãi Biển (585)
            Item itemQua = new Item(585);
            itemQua.amount = 1;
            itemQua.strOptions = "0,150;1,150;2,50;209,50;3,100";
            itemQua.isLock = true;
            p.addItem(itemQua);
            p.msgAddItemBag(itemQua);
            p.service.serverMessage("Bạn đã đổi vật phẩm biển thành công nhận được Sakura Tiệc Bãi Biển!");
        } else {
            p.service.serverMessage("Bạn cần 99 Cá giao lục, 99 Cá Koi, 99 Cá đĩa lam, 99 Cá buồm tím, 99 Sao biển xanh, 99 Cua hoàng đế để đổi!");
        }
    }

    @Override
    public void action(Char p, int type, int amount) {
        if (isEnded()) {
            p.service.serverMessage("Sự kiện đã kết thúc");
            return;
        }
        switch (type) {
            case 0:
                makeIceCreamCone(p, amount);
                break;
            case 1:
                makeIceCreamMilk(p, amount);
                break;
            case 2:
                makeIceCreamChocolate(p, amount);
                break;
            case 3:
                makeIceCreamStrawberry(p, amount);
                break;
            case 4:
                makeIceCreamMash(p, amount);
                break;
        }
    }

    @Override
    public void useItem(Char p, Item item) {
        switch (item.id) {
            case 897:
            case 898:
            case 899:
            case 892:
            case 895:
            case 337:
                if (p.getCountNullItemBag() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
                break;
            case 924:
            case 925:
                if (p.getCountNullItemBag() == 0) {
                    p.warningBagFull();
                    return;
                }
                useEventItem(p, item.id, itemsRecFromGoldItem);
                break;
            case 926:
            case 927:
            case 928:
            case 929:
            case 930:
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
            case 0: // Làm Kem ốc qués
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
                        p.getService().sendTextNPC("Để làm 1 Kem ốc qués cần: 5 bột + 5 đường + 5 trứng + 200,000 bạc khóa", "");
                        break;
                }
                break;
            case 1: // Làm Kem sữa
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
                        p.getService().sendTextNPC("Để làm 1 Kem sữa cần: 5 bơ + 5 đường + 5 trứng + 200,000 bạc khóa", "");
                        break;
                }
                break;
            case 2: // Làm Kem chocolate
                switch (index2) {
                    case 0:
                        action(p, 2, 1);
                        break;
                    case 1:
                        action(p, 2, 10);
                        break;
                    case 2:
                        action(p, 2, 100);
                        break;
                    case 3:
                        p.getService().sendTextNPC("Để làm 1 Kem chocolate cần: 5 hạt ca cao + 5 đường + 5 bơ + 200,000 bạc khóa", "");
                        break;
                }
                break;
            case 3: // Làm Kem dâu
                switch (index2) {
                    case 0:
                        action(p, 3, 1);
                        break;
                    case 1:
                        action(p, 3, 10);
                        break;
                    case 2:
                        action(p, 3, 100);
                        break;
                    case 3:
                        p.getService().sendTextNPC("Để làm 1 Kem dâu cần: 5 dâu tây + 5 đường + 5 bơ + 200,000 bạc khóa", "");
                        break;
                }
                break;
            case 4: // Làm Hũ kem dầm
                switch (index2) {
                    case 0:
                        action(p, 4, 1);
                        break;
                    case 1:
                        action(p, 4, 10);
                        break;
                    case 2:
                        action(p, 4, 100);
                        break;
                    case 3:
                        p.getService().sendTextNPC("Để làm 1 Hũ kem dầm cần: 10 dâu tây + 10 bột + 10 đường + 100 vàng", "");
                        break;
                }
                break;
            case 5: // Thu Thập Biển
                switch (index2) {
                    case 0:
                        exchangeBeachItems(p);
                        break;
                    case 1:
                        p.getService().sendTextNPC("Thu thập vật phẩm biển: Cá giao lục (920), Cá Koi (921), Cá đĩa lam (922), Cá buồm tím (923), Sao biển xanh (924), Cua hoàng đế (925)\nCá (920-923) lấy bằng cần câu, Sao biển (924) và Cua (925) rơi từ quái\nĐổi 99 mỗi loại cá để nhận Sakura Tiệc Bãi Biển (585)!", "");
                        break;
                }
                break;
        }
    }
}
