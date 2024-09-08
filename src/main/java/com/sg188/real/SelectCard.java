package com.sg188.real;

import Manager.Manager;
import com.event.Event;
import com.sg188.data.ItemOption;
import com.sg188.lib.Utlis;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SelectCard extends AbsSelectCard {
    private static final SelectCard instance = new SelectCard();

    public static SelectCard getInstance() {
        return instance;
    }

    public static final long EXPIRE_3_DAY = 3 * 24 * 60 * 60 * 1000;
    public static final long EXPIRE_7_DAY = 7 * 24 * 60 * 60 * 1000;
    public static final long EXPIRE_20_DAY = 20 * 24 * 60 * 60 * 1000;

    @Override
    protected void init() {
//        add(Card.builder().id(6).rate(15).build());
//        add(Card.builder().id(7).rate(12).build());
        add(Card.builder().id(8).rate(10).build());
        add(Card.builder().id(9).rate(2).build());
        add(Card.builder().id(10).rate(0.5).build());

        add(Card.builder().id(551).rate(0.5).build());//phucloi hien nhan
        add(Card.builder().id(644).rate(10).quantity(10).build());
        add(Card.builder().id(644).rate(10).quantity(20).build());
        add(Card.builder().id(277).rate(10).quantity(20).build());
        add(Card.builder().id(161).rate(10).quantity(10).build());
        add(Card.builder().id(160).rate(5).quantity(1000).build());
        add(Card.builder().id(176).rate(5).quantity(500).build());
        add(Card.builder().id(176).rate(2).quantity(1000).build());

        add(Card.builder().id(187).rate(2).build());
        add(Card.builder().id(134).rate(3).build());
        add(Card.builder().id(468).rate(0.3).build());

        add(Card.builder().id(404).rate(3).quantity(100).build());

        add(Card.builder().id(163).rate(10).quantity(200000).build());//vang khoa bac khoa
        add(Card.builder().id(163).rate(5).quantity(500000).build());
        add(Card.builder().id(163).rate(3).quantity(1000000).build());
        add(Card.builder().id(163).rate(1).quantity(5000000).build());
        add(Card.builder().id(163).rate(0.5).quantity(15000000).build());
        add(Card.builder().id(192).rate(10).quantity(500).build());
        add(Card.builder().id(192).rate(5).quantity(1000).build());
        add(Card.builder().id(192).rate(3).quantity(2000).build());
        add(Card.builder().id(192).rate(1).quantity(3000).build());

        add(Card.builder().id(464).rate(0.5).build());// cai trang
        add(Card.builder().id(465).rate(0.5).build());
        add(Card.builder().id(516).rate(0.5).build());
        add(Card.builder().id(520).rate(0.5).build());
        add(Card.builder().id(521).rate(0.5).build());
        add(Card.builder().id(522).rate(0.5).build());
        add(Card.builder().id(672).rate(0.5).build());
        add(Card.builder().id(726).rate(0.5).build());
        add(Card.builder().id(784).rate(0.5).build());
        add(Card.builder().id(886).rate(0.5).build());
        add(Card.builder().id(887).rate(0.5).build());
        add(Card.builder().id(514).rate(0.5).build());

        add(Card.builder().id(524).rate(0.5).build());
        add(Card.builder().id(525).rate(0.5).build());
        add(Card.builder().id(526).rate(0.5).build());

        add(Card.builder().id(647).rate(0.5).build());
        add(Card.builder().id(648).rate(0.5).build());
        add(Card.builder().id(649).rate(0.5).build());
        add(Card.builder().id(650).rate(0.5).build());
        add(Card.builder().id(651).rate(0.5).build());
        add(Card.builder().id(653).rate(0.5).build());
        add(Card.builder().id(856).rate(0.5).build());

        add(Card.builder().id(812).rate(0.5).expire(EXPIRE_7_DAY).build());// thoi trang
//        add(Card.builder().id(814).rate(0.3).expire(EXPIRE_7_DAY).build());
//        add(Card.builder().id(820).rate(0.3).expire(EXPIRE_7_DAY).build());
//        add(Card.builder().id(724).rate(0.3).expire(EXPIRE_7_DAY).build());
        add(Card.builder().id(812).rate(1.5).expire(EXPIRE_3_DAY).build());
//        add(Card.builder().id(814).rate(1).expire(EXPIRE_3_DAY).build());
//        add(Card.builder().id(820).rate(1).expire(EXPIRE_3_DAY).build());
//        add(Card.builder().id(724).rate(1).expire(EXPIRE_3_DAY).build());

//        add(Card.builder().id(871).rate(0.1).expire(EXPIRE_7_DAY).build());// tanto
        add(Card.builder().id(881).rate(0.5).expire(EXPIRE_7_DAY).build());
//        add(Card.builder().id(882).rate(0.3).expire(EXPIRE_7_DAY).build());
//        add(Card.builder().id(284).rate(0.3).expire(EXPIRE_7_DAY).build());
//        add(Card.builder().id(871).rate(0.6).expire(EXPIRE_3_DAY).build());
        add(Card.builder().id(881).rate(1.5).expire(EXPIRE_3_DAY).build());
//        add(Card.builder().id(882).rate(1).expire(EXPIRE_3_DAY).build());
//        add(Card.builder().id(284).rate(1).expire(EXPIRE_3_DAY).build());

        add(Card.builder().id(150).rate(0.1).build());//sach tiem nang , ky nang
        add(Card.builder().id(151).rate(0.1).build());
        add(Card.builder().id(152).rate(0.1).build());
        add(Card.builder().id(153).rate(0.1).build());
        add(Card.builder().id(154).rate(0.1).build());
        add(Card.builder().id(155).rate(0.1).build());

        add(Card.builder().id(704).rate(0.8).build());
        add(Card.builder().id(790).rate(0.5).build());

        add(Card.builder().id(434).rate(1).quantity(100).build());
        add(Card.builder().id(687).rate(1).quantity(50).build());
        add(Card.builder().id(687).rate(2).quantity(20).build());
        add(Card.builder().id(688).rate(1.5).build());
        add(Card.builder().id(860).rate(0.2).build());
        add(Card.builder().id(281).rate(2).quantity(20).build());
        add(Card.builder().id(347).rate(1).quantity(20).build());
        add(Card.builder().id(599).rate(5).quantity(20).build());
        add(Card.builder().id(428).rate(1).quantity(20).build());

    }

    @Override
    protected boolean isCanSelect(@NotNull Char p) {
        Item pmm = p.FindItemBag(932);
        if (pmm == null || !pmm.has()) {
            p.getService().serverMessage("Bạn không có Vé vận may");
            return false;
        }
        if (p.getCountNullItemBag() == 0) {
            p.warningBagFull();
            return false;
        }
        return true;
    }

    @Override
    protected void selecctCardSuccessful(@NotNull Char p) {
        Item pmm = p.FindItemBag(932);
        if (pmm != null) {
            p.removeItem(pmm);
            p.msgUseItemBag(pmm);
        }
    }

    @Override
    protected Card reward(@NotNull Char p, Card card) {
        int itemID = card.getId();
        int quantity = card.getQuantity();
        Item item = new Item(itemID);
        item.amount = quantity;
        if (card.getExpire() > 0) {
            item.expiry = System.currentTimeMillis() + card.getExpire();
        }
        if (item.isItemBody()) {
            if (item.getItemTemplate().type == 14) {
                item.addItemOption(new ItemOption(0, Utlis.nextInt(100, 200)));
                item.addItemOption(new ItemOption(1, Utlis.nextInt(100, 200)));
                item.addItemOption(new ItemOption(3, Utlis.nextInt(50, 100)));
                item.addItemOption(new ItemOption(209, Utlis.nextInt(20, 50)));
            }

            if (item.getItemTemplate().type == 15) {
                item.level = 16;
                item.addItemOption(new ItemOption(122, Utlis.nextInt(5, 15)));
                item.addItemOption(new ItemOption(151, Utlis.nextInt(50, 100)));
                item.addItemOption(new ItemOption(152, Utlis.nextInt(50, 100)));
                item.addItemOption(new ItemOption(117, Utlis.nextInt(100, 150)));
                item.addItemOption(new ItemOption(110, Utlis.nextInt(50, 100)));
                item.addItemOption(new ItemOption(158, Utlis.nextInt(1, 3)));
            }

            if (item.getItemTemplate().type == 16) {
                item.addItemOption(new ItemOption(2, Utlis.nextInt(50, 100)));
                item.addItemOption(new ItemOption(5, Utlis.nextInt(50, 100)));
                item.addItemOption(new ItemOption(306, Utlis.nextInt(30, 60)));
                item.addItemOption(new ItemOption(0, 200));
            }

            if (item.getItemTemplate().id == 134) {
                switch (p.Info.idhe) {
                    case 1:
                        item.addItemOption(new ItemOption(54, 0, 500));
                        item.addItemOption(new ItemOption(62, 0, 500));
                        break;
                    case 2:
                        item.addItemOption(new ItemOption(55, 0, 500));
                        item.addItemOption(new ItemOption(58, 0, 500));
                        break;
                    case 3:
                        item.addItemOption(new ItemOption(56, 0, 500));
                        item.addItemOption(new ItemOption(59, 0, 500));
                        break;
                    case 4:
                        item.addItemOption(new ItemOption(57, 0, 500));
                        item.addItemOption(new ItemOption(60, 0, 500));
                        break;
                    case 5:
                        item.addItemOption(new ItemOption(53, 0, 500));
                        item.addItemOption(new ItemOption(61, 0, 500));
                        break;
                }

            }
        }
        if (item.id == 163) {
            p.addBacKhoa(quantity);
        } else if (item.id == 191) {
            p.addBac(quantity);
        } else if (item.id == 192) {
            p.addVangKhoa(quantity);
        } else if (item.id == 193) {
            p.addVang(quantity);
        } else {
            p.addItem(item);
            p.msgAddItemBag(item);
        }
        return card;
    }
}
