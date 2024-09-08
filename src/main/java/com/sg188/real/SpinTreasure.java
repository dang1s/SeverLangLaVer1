package com.sg188.real;

import Service.HanderMessage;
import com.sg188.data.DataCenter;
import com.sg188.lib.Log;
import com.sg188.lib.RandomCollection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SpinTreasure {
    private static final SpinTreasure instance = new SpinTreasure();

    public static SpinTreasure getInstance() {
        return instance;
    }

    private RandomCollection<Treasure> treasures = new RandomCollection<>();

    public SpinTreasure() {
        add(Treasure.builder().id(0).index(0).quantity(1).rate(25).build());
        add(Treasure.builder().id(1).index(1).quantity(3).rate(4).build());
        add(Treasure.builder().id(1).index(2).quantity(1).rate(25).build());
        add(Treasure.builder().id(177).index(3).quantity(1).rate(1).build());
        add(Treasure.builder().id(0).index(4).quantity(2).rate(8).build());
        add(Treasure.builder().id(1).index(5).quantity(1).rate(25).build());
        add(Treasure.builder().id(3).index(6).quantity(3).rate(4).build());
        add(Treasure.builder().id(3).index(7).quantity(1).rate(25).build());
        add(Treasure.builder().id(2).index(8).quantity(1).rate(25).build());
        add(Treasure.builder().id(0).index(9).quantity(1).rate(25).build());
        add(Treasure.builder().id(1).index(10).quantity(2).rate(8).build());
        add(Treasure.builder().id(3).index(11).quantity(1).rate(25).build());
        add(Treasure.builder().id(2).index(12).quantity(3).rate(4).build());
        add(Treasure.builder().id(2).index(13).quantity(1).rate(25).build());
        add(Treasure.builder().id(177).index(14).quantity(1).rate(1).build());
        add(Treasure.builder().id(3).index(15).quantity(2).rate(8).build());
        add(Treasure.builder().id(2).index(16).quantity(1).rate(25).build());
        add(Treasure.builder().id(0).index(17).quantity(3).rate(4).build());
        add(Treasure.builder().id(0).index(18).quantity(1).rate(25).build());
        add(Treasure.builder().id(1).index(19).quantity(1).rate(25).build());
        add(Treasure.builder().id(3).index(20).quantity(1).rate(25).build());
        add(Treasure.builder().id(2).index(21).quantity(1).rate(25).build());
    }

    protected void add(Treasure card) {
        treasures.add(card.getRate(), card);
    }

    public void spinning(@NotNull Char p, int type) {
        int quantity = type == 0 ? 2 : type == 1 ? 10 : 50;
        if (isCanSpin(p, quantity)) {
            Treasure spin = treasures.next();
            if (p.treasure == null) {
                p.Info.typeVQMM = (byte) type;
                spinSuccessful(p, quantity);
                Treasure treasure = Treasure.builder().id(spin.getId()).index(spin.getIndex()).quantity(spin.getQuantity()-1).build();
                p.treasure = treasure;
            } else {
                if (p.treasure.getId() == spin.getId()) {
                    p.treasure.setQuantity(spin.getQuantity() + p.treasure.getQuantity());
                    if(p.treasure.getQuantity() >= 6){
                        p.treasure.setQuantity(6);
                    }
                    p.treasure.setIndex(spin.getIndex());
                } else {
                    p.treasure = null;
                    p.getService().spinLoss(spin.getIndex(), spin.getId());
                    return;
                }
            }
            p.getService().spinTreasure();

        }
    }

    public void spinReward(@NotNull Char p, int type, boolean isRuong) {
        if (p.treasure == null) {
            return;
        }
        try {
            int quantity = p.Info.typeVQMM == 0 ? 1 : p.Info.typeVQMM == 1 ? 5 : 25;
            int treasureId = p.treasure.getId();
            int treasureQuantity = p.treasure.getQuantity();
            if(treasureId < 0||treasureQuantity < 0){
                p.treasure = null;
                p.getService().resetSpin();
                return;
            }
            DataCenter dataCenter = DataCenter.gI();
            p.treasure = null;
            p.getService().resetSpin();
            if (isRuong) {
                Item ruong = new Item(177);
                ruong.amount = quantity;
                ruong.isLock = true;
                p.addItem(ruong);
                p.msgAddItemBag(ruong);
                p.getService().resetSpin();
            }
            if (type == 0) {
                switch (treasureId) {
                    case 0:
                        int soLuong = dataCenter.dataGiftQuaySo[treasureId][treasureQuantity];
                        p.Point.hoatLuc += soLuong * quantity;
                        p.user.session.sendMessage(HanderMessage.UpdateHoatLuc(p.Point.hoatLuc));
                        break;
                    case 1:
                        p.addBacKhoa(dataCenter.dataGiftQuaySo[treasureId][treasureQuantity] * quantity);
                        break;
                    case 2:
                        p.addVangKhoa(dataCenter.dataGiftQuaySo[treasureId][treasureQuantity] * quantity);
                        break;
                    case 3:
                        if (p.getCountNullItemBag() < quantity) {
                            p.warningBagFull();
                            return;
                        }
                        for (int i = 0; i < quantity; i++) {
                            Item da = new Item(dataCenter.dataGiftQuaySo[treasureId][treasureQuantity]);
                            da.amount = 1;
                            da.isLock = true;
                            p.addItem(da);
                            p.msgAddItemBag(da);
                        }
                        break;
                }
            }
        }catch (Exception e){
            Log.error("Loi tra thuong kho bau: "+e);
            p.getService().alertMessage("Lỗi trả thưởng vui lòng inbox admin");
        }
    }

    protected boolean isCanSpin(@NotNull Char p, int quantity) {
        Item pmm = p.FindItemBag(176);
        if (pmm == null || !pmm.has()) {
            p.getService().serverMessage("Bạn không có Sò");
            return false;
        } else if (pmm.amount < quantity) {
            p.getService().serverMessage("Bạn không đủ Sò");
            return false;
        }

        if (p.getCountNullItemBag() == 0) {
            p.warningBagFull();
            return false;
        }
        return true;
    }

    protected void spinSuccessful(@NotNull Char p, int quantity) {
        Item pmm = p.FindItemBag(176);
        if (pmm != null) {
            p.removeItemByAmount(pmm, quantity);
            p.msgUseItemBag(pmm);
        }
    }

}
