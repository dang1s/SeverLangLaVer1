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
//test cho thêm đá
        add(Card.builder().id(354).rate(10).quantity(6).build()); // đá myo
        add(Card.builder().id(354).rate(10).quantity(6).build()); // đá myo
        add(Card.builder().id(354).rate(10).quantity(6).build()); // đá myo

        add(Card.builder().id(562).rate(10).quantity(10).build()); // đá baku
        add(Card.builder().id(562).rate(10).quantity(10).build()); // đá baku
        add(Card.builder().id(562).rate(10).quantity(10).build()); // đá baku

        add(Card.builder().id(564).rate(10).quantity(10).build()); // đá sharin
        add(Card.builder().id(564).rate(10).quantity(10).build()); // đá sharin
        add(Card.builder().id(564).rate(10).quantity(10).build()); // đá sharin

        add(Card.builder().id(566).rate(10).quantity(10).build()); // đá rine
        add(Card.builder().id(566).rate(10).quantity(10).build()); // đá rine
        add(Card.builder().id(566).rate(10).quantity(10).build()); // đá rine

        add(Card.builder().id(645).rate(15).quantity(2).build()); // than thu
        add(Card.builder().id(645).rate(15).quantity(2).build()); // than thu
        add(Card.builder().id(645).rate(15).quantity(2).build()); // than thu

        //add(Card.builder().id(6).rate(15).build()); // đá 7
        add(Card.builder().id(8).rate(15).build()); // đá 8
        add(Card.builder().id(8).rate(10).build()); // đá 8
        add(Card.builder().id(8).rate(5).build()); // đá 9
        add(Card.builder().id(9).rate(3).build()); // đá 10
        add(Card.builder().id(644).rate(10).quantity(10).build()); // bùa bạc khóa
        //add(Card.builder().id(643).rate(10).quantity(5).build()); // bùa exp
        add(Card.builder().id(643).rate(10).quantity(5).build()); // bùa exp
        add(Card.builder().id(277).rate(10).quantity(10).build()); // rương đá
        add(Card.builder().id(161).rate(10).quantity(1).build()); // rương tinh thạch
        add(Card.builder().id(161).rate(10).quantity(2).build()); // rương tinh thạch
        add(Card.builder().id(160).rate(5).quantity(100).build()); // tinh thạch
        add(Card.builder().id(160).rate(5).quantity(120).build()); // tinh thạch
        add(Card.builder().id(176).rate(5).quantity(50).build()); //vỏ sò
        add(Card.builder().id(176).rate(5).quantity(50).build()); //vỏ sò
        //add(Card.builder().id(187).rate(2).build()); // túi mở rộng cấp 3
        //add(Card.builder().id(187).rate(2).build()); // túi mở rộng cấp 3
        add(Card.builder().id(134).rate(3).build()); //bùa nổ
        add(Card.builder().id(468).rate(2).build()); //túi mở rộng cấp 4
        add(Card.builder().id(468).rate(2).build()); //túi mở rộng cấp 4
        //add(Card.builder().id(163).rate(10).quantity(500000).build());// bạc khóa
        //add(Card.builder().id(163).rate(10).quantity(700000).build());// bạc khóa
        //add(Card.builder().id(163).rate(10).quantity(900000).build());// bạc khóa
        add(Card.builder().id(163).rate(8).quantity(3000000).build());// bạc khóa
        add(Card.builder().id(163).rate(8).quantity(5000000).build());// bạc khóa
        add(Card.builder().id(163).rate(5).quantity(7000000).build());// bạc khóa
        add(Card.builder().id(163).rate(5).quantity(9000000).build());// bạc khóa
        add(Card.builder().id(163).rate(3).quantity(10000000).build());// bạc khóa
        add(Card.builder().id(163).rate(1).quantity(15000000).build());// bạc khóa

        //add(Card.builder().id(192).rate(20).quantity(150).build()); // vàng khóa
        add(Card.builder().id(192).rate(10).quantity(200).build()); // vàng khóa

        //add(Card.builder().id(192).rate(5).quantity(100).build());// vàng khóa
        add(Card.builder().id(192).rate(5).quantity(150).build());// vàng khóa
        add(Card.builder().id(192).rate(5).quantity(200).build());// vàng khóa
        add(Card.builder().id(192).rate(3).quantity(300).build());// vàng khóa
        add(Card.builder().id(192).rate(1).quantity(500).build());// vàng khóa


        //add(Card.builder().id(463).rate(0.5).build());// Cải trang Lục Đạo

        add(Card.builder().id(464).rate(0.5).build());// cai trang kakashi
        add(Card.builder().id(465).rate(0.5).build()); //minato
        add(Card.builder().id(516).rate(0.5).build()); //hashirama

        add(Card.builder().id(495).rate(0.5).build()); //anbu
        add(Card.builder().id(496).rate(0.5).build()); //anbu cội rễ
        add(Card.builder().id(513).rate(0.5).build()); //haku
        add(Card.builder().id(514).rate(0.5).build()); //zabuza
        add(Card.builder().id(515).rate(0.5).build()); //sai
        add(Card.builder().id(516).rate(0.5).build()); //iruka

        add(Card.builder().id(517).rate(0.5).build()); //kurenai
        add(Card.builder().id(518).rate(0.5).build()); //asuma
        add(Card.builder().id(519).rate(0.5).build()); //gai
        add(Card.builder().id(520).rate(0.5).build()); //karin
        add(Card.builder().id(521).rate(0.5).build()); //suigetsu
        add(Card.builder().id(522).rate(0.5).build()); //juugo
        add(Card.builder().id(523).rate(0.5).build()); //yamato
        add(Card.builder().id(524).rate(0.5).build()); //kabuto
        add(Card.builder().id(525).rate(0.5).build()); //tsunade
        add(Card.builder().id(526).rate(0.5).build()); //orochimaru
        add(Card.builder().id(527).rate(0.5).build()); //jiraiya
        //add(Card.builder().id(528).rate(0.5).build()); //kakashi lục đạo
        add(Card.builder().id(529).rate(0.5).build()); //tobirama
        add(Card.builder().id(530).rate(0.5).build()); //madara
        add(Card.builder().id(555).rate(0.5).build()); //phù thủy
        add(Card.builder().id(556).rate(0.5).build()); //bí ngô

//        add(Card.builder().id(592).rate(0.5).build()); //rương ct giáo viên
//        add(Card.builder().id(329).rate(0.5).build()); //rương ct

        add(Card.builder().id(611).rate(0.5).build()); //văn lang
        add(Card.builder().id(618).rate(0.5).build()); //seimei

        add(Card.builder().id(623).rate(0.5).build()); //obito lục đạo
        add(Card.builder().id(642).rate(0.5).build()); //bạch zetsu
        add(Card.builder().id(646).rate(0.5).build()); //đạo ngọc cầu
        add(Card.builder().id(647).rate(0.5).build()); //anbu gấu

        add(Card.builder().id(648).rate(0.5).build()); //anbu cọp
        add(Card.builder().id(649).rate(0.5).build()); //anbu chim
        add(Card.builder().id(650).rate(0.5).build()); //anbu gà
        add(Card.builder().id(651).rate(0.5).build()); //anbu dê
        add(Card.builder().id(653).rate(0.5).build()); //dracula

        add(Card.builder().id(662).rate(0.5).build()); //noel
        add(Card.builder().id(672).rate(0.5).build()); //killbe
        add(Card.builder().id(677).rate(0.5).build()); //cương thi itachi

        add(Card.builder().id(682).rate(0.5).build()); //thu linh văn lang

        //add(Card.builder().id(702).rate(0.5).build()); //madara lục đạo

        add(Card.builder().id(722).rate(0.5).build()); //franken

        add(Card.builder().id(725).rate(0.5).build()); //ct tết

        add(Card.builder().id(726).rate(0.5).build()); //tử môn gai
        add(Card.builder().id(762).rate(0.5).build()); //mị nương
        add(Card.builder().id(775).rate(0.5).build()); //thũ lĩnh anbu
        add(Card.builder().id(783).rate(0.5).build()); //người sói
        add(Card.builder().id(784).rate(0.5).build()); //nagato
        //add(Card.builder().id(788).rate(0.5).build()); //shisui

        add(Card.builder().id(813).rate(0.5).build()); //thần cọp
        add(Card.builder().id(821).rate(0.5).build()); //sơn tinh
        add(Card.builder().id(822).rate(0.5).build()); //thủy tinh

        add(Card.builder().id(847).rate(0.5).build()); //thỏ ngọc
        add(Card.builder().id(856).rate(0.5).build()); //ma tốc độ

        add(Card.builder().id(863).rate(0.5).build()); //mèo hiệp sĩ
        add(Card.builder().id(883).rate(0.5).build()); //thánh gióng

        add(Card.builder().id(886).rate(0.5).build()); //hanzo
        add(Card.builder().id(887).rate(0.5).build()); //danzo



        add(Card.builder().id(724).rate(0.5).expire(EXPIRE_7_DAY).build()); // mũ thần tài
        add(Card.builder().id(727).rate(0.5).expire(EXPIRE_3_DAY).build());//mn thổ địa

        add(Card.builder().id(747).rate(0.5).expire(EXPIRE_3_DAY).build());//bộ giáp sơn tinh
        add(Card.builder().id(748).rate(0.5).expire(EXPIRE_3_DAY).build());//bộ giáp thủy tinh

        add(Card.builder().id(776).rate(0.5).expire(EXPIRE_3_DAY).build());//mũ sinh nhật

        add(Card.builder().id(785).rate(0.5).expire(EXPIRE_3_DAY).build());//mũ akatsuki
        add(Card.builder().id(787).rate(0.5).expire(EXPIRE_3_DAY).build());//nón lá orochimaru


        add(Card.builder().id(789).rate(0.5).expire(EXPIRE_3_DAY).build());//thú cưỡi tuần lộc
        add(Card.builder().id(812).rate(0.5).expire(EXPIRE_3_DAY).build());//thú cưỡi ph lửa
        add(Card.builder().id(814).rate(0.5).expire(EXPIRE_3_DAY).build());//thời trang mèo hiệp sĩ
        add(Card.builder().id(820).rate(0.5).expire(EXPIRE_3_DAY).build());//mũ tai thỏ
        add(Card.builder().id(844).rate(0.5).expire(EXPIRE_3_DAY).build());//đèn ông sao
        add(Card.builder().id(854).rate(0.5).expire(EXPIRE_3_DAY).build());//mũ halloween
        add(Card.builder().id(855).rate(0.5).expire(EXPIRE_3_DAY).build());//chổi bay
        add(Card.builder().id(861).rate(0.5).expire(EXPIRE_3_DAY).build());//áo choàng tết
        add(Card.builder().id(862).rate(0.5).expire(EXPIRE_3_DAY).build());//thời trang kha
        add(Card.builder().id(879).rate(0.5).expire(EXPIRE_3_DAY).build());//thời trang thánh gióng
        add(Card.builder().id(880).rate(0.5).expire(EXPIRE_3_DAY).build());// ngựa sắt
        add(Card.builder().id(906).rate(0.5).expire(EXPIRE_3_DAY).build());//mn thỏ ngọc
        add(Card.builder().id(907).rate(0.5).expire(EXPIRE_3_DAY).build());//mn thỏ tinh

        //tanto type 15
        add(Card.builder().id(284).rate(0.2).expire(EXPIRE_7_DAY).build());//gậy kẹo
        add(Card.builder().id(309).rate(0.2).expire(EXPIRE_7_DAY).build());//gậy phép thuật
        add(Card.builder().id(871).rate(0.2).expire(EXPIRE_7_DAY).build());//gậy trường thọ
        add(Card.builder().id(881).rate(0.2).expire(EXPIRE_3_DAY).build());//gậy sắt
        add(Card.builder().id(882).rate(0.2).expire(EXPIRE_7_DAY).build());//bó tre
        add(Card.builder().id(908).rate(0.2).expire(EXPIRE_3_DAY).build()); //lồng đèn trái bí
        add(Card.builder().id(909).rate(0.2).expire(EXPIRE_3_DAY).build()); //đèn lồng đỏ
        add(Card.builder().id(910).rate(0.2).expire(EXPIRE_3_DAY).build()); //lồng đèn hoa sen
        add(Card.builder().id(911).rate(0.2).expire(EXPIRE_3_DAY).build()); //lồng đèn quả trám

        add(Card.builder().id(150).rate(1).build());//sach tiem nang , ky nang
        add(Card.builder().id(151).rate(1).build());
        add(Card.builder().id(152).rate(1).build());
        add(Card.builder().id(153).rate(1).build());
        add(Card.builder().id(154).rate(1).build());
        add(Card.builder().id(155).rate(1).build());

        add(Card.builder().id(704).rate(5).build());//ct +17
        add(Card.builder().id(790).rate(3).build());//ct +18

        add(Card.builder().id(294).rate(5).quantity(2000).build());// tu luyện đan 20
        add(Card.builder().id(434).rate(5).quantity(200).build());// mảnh sách
        add(Card.builder().id(434).rate(5).quantity(200).build());// mảnh sách
        add(Card.builder().id(434).rate(5).quantity(200).build());// mảnh sách
        //add(Card.builder().id(763).rate(5).quantity(100).build());// charka vĩ thú
        add(Card.builder().id(763).rate(5).quantity(50000).build());// charka vĩ thú 500
        //add(Card.builder().id(763).rate(5).quantity(200).build());// charka vĩ thú

        add(Card.builder().id(687).rate(10).quantity(1000).build());//lông vĩ thú 10
        add(Card.builder().id(687).rate(10).quantity(1000).build());//lông vĩ thú 10
        add(Card.builder().id(688).rate(1.5).build());//knvt
        add(Card.builder().id(860).rate(0.1).build());//knvt db
        add(Card.builder().id(281).rate(2).quantity(10).build());// sâm 75
        add(Card.builder().id(347).rate(1).quantity(5).build());// sâm 100
        add(Card.builder().id(599).rate(5).quantity(200).build());// mảnh huyết kế
        add(Card.builder().id(428).rate(5).quantity(10).build());// rương khảm
        add(Card.builder().id(428).rate(5).quantity(10).build());// rương khảm
        add(Card.builder().id(310).rate(5).quantity(400).build());// mảnh bí kíp
        //add(Card.builder().id(310).rate(5).quantity(100).build());// mảnh bí kíp
        //add(Card.builder().id(310).rate(5).quantity(100).build());// mảnh bí kíp
        //add(Card.builder().id(754).rate(20).quantity(500).build());// mảnh bí kíp siêu cấp
        //add(Card.builder().id(754).rate(15).quantity(700).build());// mảnh bí kíp siêu cấp
        add(Card.builder().id(754).rate(10).quantity(10000).build());// mảnh bí kíp siêu cấp

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
                item.level = (byte)Utlis.nextInt(19, 29);
                item.addItemOption(new ItemOption(122, Utlis.nextInt(5, 15)));
                item.addItemOption(new ItemOption(151, Utlis.nextInt(50, 100)));
                item.addItemOption(new ItemOption(152, Utlis.nextInt(50, 100)));
                item.addItemOption(new ItemOption(117, Utlis.nextInt(100, 150)));
                item.addItemOption(new ItemOption(110, Utlis.nextInt(50, 100)));
                item.addItemOption(new ItemOption(158, Utlis.nextInt(1, 3)));
            }

            if (item.getItemTemplate().type == 16) {
                item.level = (byte)Utlis.nextInt(19, 29);
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
