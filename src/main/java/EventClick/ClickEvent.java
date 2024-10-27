/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventClick;

import Manager.Manager;
import com.sg188.PhucLoi.PhucLoi;
import com.sg188.PhucLoi.TemplatePL;
import com.sg188.PhucLoi.Welfare;
import com.sg188.Shop.DiscountStore;
import com.sg188.Shop.ItemShop;
import com.sg188.Shop.Store;
import com.sg188.data.ItemOption;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.lib.Message;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author ADMIN
 */
public class ClickEvent {
    public static void HoatDong(Char _myChar) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(56);
            m.writeUTF("---------------------------------THÔNG BÁO---------------------------------\n" +
                    "Link tải game: langlatoithuong.com\n" +
                    "Gia nhập box zalo để chơi game tốt hơn.\n" +
                    "Hãy tham gia like share để nhận code mới nhất.\n" +
                    "Chúc bạn chơi game vui vẻ.");
            m.writeByte(18);
            for (int i = 0; i < 18; i++) {
                m.writeUTF("");
                m.writeUTF("");
            }
            _myChar.user.session.sendMessage(m);
        } catch (IOException ex) {
            Logger.getLogger(ClickEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void ShowTask(Char _myChar) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(72);
            m.writer.writeShort(1);

            _myChar.user.session.sendMessage(m);
        } catch (IOException ex) {
            Logger.getLogger(ClickEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void KhoBau(Char _myChar) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(86);
            if (_myChar.treasure != null) {
                m.writeBoolean(true);
                m.writeByte(_myChar.Info.typeVQMM);
                m.writeByte(_myChar.treasure.getQuantity()); //
                m.writeByte(_myChar.treasure.getIndex());
                m.writeByte(_myChar.treasure.getId());
                if (_myChar.treasure.getId() == 177) {
                    m.writeBoolean(true);
                } else
                    m.writeBoolean(false);
            } else
                m.writeBoolean(false);
//            m.writeByte(1); // max 2
//            m.writeByte(22); // level chế tạo
//            m.writeByte(30);
//            m.writeByte(40);
//            m.writeBoolean(false);
            m.writeInt(_myChar.Info.expCheTao);//exp
            _myChar.user.session.sendMessage(m);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void NapDau(Char _myChar) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(89);

            Item item = new Item(558, true, 1);
            item.write(m.writer);

            item = new Item(521, true);
            item.addItemOption(new ItemOption(0, 150));
            item.addItemOption(new ItemOption(1, 150));
            item.addItemOption(new ItemOption(3, 150));
            item.addItemOption(new ItemOption(209, 60));
            item.write(m.writer);

            item = new Item(277, true);
            item.amount = 10;
            item.write(m.writer);

            item = new Item(443, true);
            item.write(m.writer);
            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void OpenBox(Char _myChar) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(50);
            if (_myChar.Bag.arrItemBox == null) {
                _myChar.Bag.arrItemBox = new Item[27];
            }
            m.writeShort(_myChar.Bag.arrItemBox.length);
            List<Item> itemList = Arrays.stream(_myChar.Bag.arrItemBox)
                    .filter(item -> item != null) // Lọc, chỉ giữ các phần tử không null
                    .collect(Collectors.toList());
            m.writeShort(itemList.size());
            for (Item item : itemList) {
                if (item != null) {
                    item.write(m.writer);
                }
            }
            m.writeBoolean(_myChar.theGiuTien > System.currentTimeMillis());
            m.writeInt(_myChar.Bag.bacBox);
            m.writeInt(_myChar.Bag.bacKhoaBox);
            m.writeInt(_myChar.Bag.vangBox);
            m.writeInt(_myChar.Bag.vangKhoaBox);
            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Shop(Char _myChar, byte typeShop) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(typeShop);

            List<ItemShop> ic = new ArrayList<>();
            for (ItemShop it : Store.getInstance().items) {
                Item itemCheck = new Item(it.itemID);
                if (typeShop == 19) {
                    if (itemCheck.getItemTemplate().idClass == _myChar.Info.idClass && it.TypeShop == typeShop) {
                        ic.add(it);
                    }
                } else if (it.TypeShop == typeShop) {
                    ic.add(it);
                }
            }
            boolean[] shop39;
            if (typeShop == 39) {
                shop39 = new boolean[ic.size()];
            }
            m.writeShort(ic.size());
            for (int i = 0; i < ic.size(); i++) {
                ItemShop it = ic.get(i);
                Item item = new Item(it.itemID);
                if (item.getItemTemplate().type == 12) {
                    Item.getOptionAoChoang(item);
                    it.strOption = item.strOptions;
                }
                if (item.getItemTemplate().type == 14) {
                    Item.getOptionCaiTrang(item);
                    it.strOption = item.strOptions;
                }
                if (item.getItemTemplate().type == 10) {
                    Item.getOptionThuNuoi(item);
                    it.strOption = item.strOptions;
                }
                if (item.getItemTemplate().type == 11) {
                    Item.getOptionBiKip(item);
                    it.strOption = item.strOptions;
                }
//                if(item.getItemTemplate().type == 15){
//                    Item.getOptionTanTo(item);
//                    it.strOption = item.strOptions;
//                }

                m.writeShort(it.id);
                m.writeShort(it.itemID);
                m.writeBoolean(it.isLock);
                m.writeLong(it.expire);
                m.writeUTF(it.strOption);
                m.writeInt(it.TinhThach);
                m.writeInt(it.Vang);
                m.writeInt(it.VangKhoa);
                m.writeInt(it.BacKhoa);
                m.writeInt(it.Bac);
                if (typeShop == 39) {
                    m.writeBoolean(false);
                    for (int j = 0; j < 3; j++) {
//                         item.write(m.writer);
                    }
                } else if (typeShop == 40) {
                    m.writeInt(20);
                    m.writeInt(21);
                }
            }
            if (typeShop == 40) {
                m.writeLong(System.currentTimeMillis() + 5000);
                m.writeLong(System.currentTimeMillis() + 10000);
            }

            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void shop39(Char _myChar, byte typeShop) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(typeShop);
            int len = Manager.gI().shopRank.size();
            m.writeShort(len);
            for (int i = 0; i < len; i++) {
                ItemShop itemShop = Manager.gI().shopRank.get(i);
                m.writeShort(itemShop.id);// id của item shop
                m.writeShort(0);// id cua item
                m.writeBoolean(true);// khoa hay khong
                m.writeLong(-1);//hạn
                m.writeUTF("");//options
                m.writeInt(0);// tinh thach
                m.writeInt(itemShop.Vang);// vang
                m.writeInt(0);//vang khoa
                m.writeInt(0);//bac
                m.writeInt(0);// bac khoa
                m.writeBoolean(_myChar.checkItemShopRank(itemShop.id));
                    for (int j = 0; j < 3; j++) {
                        Item item = itemShop.items.get(j);
                         item.write(m.writer);
                    }
            }
            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shop40(Char _myChar, byte typeShop) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(typeShop);
            List<ItemShop> ic = new ArrayList<>();
            for (ItemShop it : DiscountStore.getInstance().items) {
                if (it.TypeShop == typeShop) {
                    ic.add(it);
                }
            }
            m.writeShort(ic.size());
            for (int i = 0; i < ic.size(); i++) {
                ItemShop it = ic.get(i);
                m.writeShort(it.id);
                m.writeShort(it.itemID);
                m.writeBoolean(true);
                m.writeLong(it.expire);
                m.writeUTF(it.strOption);
                m.writeInt(it.TinhThach);
                m.writeInt(it.Vang);
                m.writeInt(it.VangKhoa);
                m.writeInt(it.BacKhoa);
                m.writeInt(it.Bac);
                m.writeInt(it.giaCu);// giá cũ
                m.writeInt(it.conLai);// số lượng còn lại
            }

            m.writeLong(1730160000000L);
            m.writeLong(1730160000000L+604800000);
            _myChar.user.session.sendMessage(m);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void ShopRank(Char _myChar, byte typeShop) {
//        try {
//            Message m = new Message((byte) 122);
//            m.writeByte(typeShop);
//            List<ItemShop> ic = new ArrayList<>();
//            for(ItemShop item:Manager.gI().shopRank.keySet()){
//                ic.add(item);
//            }
//            Collections.sort(ic, new Comparator<ItemShop>() {
//                @Override
//                public int compare(ItemShop o1, ItemShop o2) {
//                    return o1.id - o2.id;
//                }
//            });
//            m.writeShort(ic.size()-(10-_myChar.Info.rank));
//            for (int i = 0; i < _myChar.Info.rank; i++) {
//                ItemShop it = ic.get(i);
//                m.writeShort(it.id);
//                m.writeShort(it.itemID);
//                m.writeBoolean(it.isLock);
//                m.writeLong(it.expire);
//                m.writeUTF(it.strOption);
//                m.writeInt(it.TinhThach);
//                m.writeInt(it.Vang);
//                m.writeInt(it.VangKhoa);
//                m.writeInt(it.BacKhoa);
//                m.writeInt(it.Bac);
//                if (typeShop == 39) {
//                    m.writeBoolean(Manager.gI().hasPurchasedItem(_myChar.Info.name,it.id));
//                    ItemShop[] itemShops = Manager.gI().shopRank.get(it);
//                    for (int j = 0; j < itemShops.length; j++) {
//                        Item item = new Item(itemShops[j].itemID);
//                        item.amount = itemShops[j].amount;
//                        item.strOptions=itemShops[j].strOption;
//                        item.write(m.writer);
//                    }
//                }
//            }
//
//            _myChar.user.session.sendMessage(m);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void ShopTrangBi(Char _myChar, byte typeShop, byte he) {
        try {
            boolean isHoakge = false;
            Message m = new Message((byte) 122);
            m.writeByte(typeShop);
//
//                        List<ItemTemplate> ic = Arrays.asList(DataCenter.gI().ItemTemplate).stream().filter(s -> s.type == 1).collect(Collectors.toList());;
//            List<ItemTemplate> icmp = Arrays.asList(DataCenter.gI().ItemTemplate).stream().filter(s -> s.type == 2).collect(Collectors.toList());;
//            ic.addAll(icmp);
            List<ItemShop> ic = new ArrayList<>();
            for (ItemShop it : Store.getInstance().items) {
                Item item = new Item(it.itemID);
                if (it.TypeShop == typeShop && it.he == he &&
                        (item.getItemTemplate().gioiTinh == 2 || item.getItemTemplate().gioiTinh == _myChar.Info.gioiTinh) &&
                        (item.getItemTemplate().idClass == 0 || item.getItemTemplate().idClass == _myChar.Info.idClass)) {
                    ic.add(it);
                }
            }

            if (typeShop >= 8 && typeShop <= 17) {
                isHoakge = true;
            }
            m.writeByte(he);
            m.writeShort(ic.size());
//            Log.debug(ic.size()+"SIZE SHOP");
            for (int i = 0; i < ic.size(); i++) {
                ItemShop it = ic.get(i);
                Item item = new Item(it.itemID);
                item.he = it.he;
                if (item.isVuKhi()) {
                    Item.setOptionsVuKhi(item, item.getItemTemplate().levelNeed);
                } else if (item.isTrangBi() || item.isPhuKien()) {
                    Item.setOptionsTrangBiPhuKien(item, item.getItemTemplate().levelNeed);
                }
                it.strOption = item.strOptions;
                if (isHoakge) {
                    Item.GetOptionHokage(item);
                    it.strOption = item.strOptions + ";148,0";
                }
                m.writeShort(it.id);
                m.writeShort(it.itemID);
                m.writeLong(it.expire);
                m.writeUTF(it.strOption);
                m.writeInt(it.TinhThach);
                m.writeInt(it.Vang);
                m.writeInt(it.VangKhoa);
                m.writeInt(it.BacKhoa);
                m.writeInt(it.Bac);
                m.writeInt(it.yeuCau);
            }

            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ShopDuocPham(Char _myChar, int TypeShop) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(TypeShop);

//            List<ItemTemplate> ic = Arrays.asList(DataCenter.gI().ItemTemplate).stream().filter(s -> s.type == 22).collect(Collectors.toList());;
//            List<ItemTemplate> icmp = Arrays.asList(DataCenter.gI().ItemTemplate).stream().filter(s -> s.type == 23).collect(Collectors.toList());;
//            ic.addAll(icmp);
            List<ItemShop> ic = new ArrayList<>();
            for (ItemShop it : Store.getInstance().items) {
                if (it.TypeShop == TypeShop) {
                    ic.add(it);
                }
            }
            m.writeShort(ic.size());
            for (int i = 0; i < ic.size(); i++) {
                ItemShop it = ic.get(i);
                m.writeShort(it.id);
                m.writeShort(it.itemID);
                m.writeBoolean(true);
                m.writeLong(it.expire);
                m.writeUTF(it.strOption);
                m.writeInt(it.TinhThach);
                m.writeInt(it.Vang);
                m.writeInt(it.VangKhoa);
                m.writeInt(it.BacKhoa);
                m.writeInt(it.Bac);
            }

            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ShopQuanAn(Char _myChar, int TypeShop) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(TypeShop);

//            List<ItemTemplate> ic = Arrays.asList(DataCenter.gI().ItemTemplate).stream().filter(s -> s.type == 22).collect(Collectors.toList());;
//            List<ItemTemplate> icmp = Arrays.asList(DataCenter.gI().ItemTemplate).stream().filter(s -> s.type == 23).collect(Collectors.toList());;
//            ic.addAll(icmp);
            List<ItemShop> ic = new ArrayList<>();
            for (ItemShop it : Store.getInstance().items) {
                if (it.TypeShop == TypeShop) {
                    ic.add(it);
                }
            }
            m.writeShort(ic.size());
            for (int i = 0; i < ic.size(); i++) {
                ItemShop it = ic.get(i);
                m.writeShort(it.id);
                m.writeShort(it.itemID);
                m.writeBoolean(true);
                m.writeLong(it.expire);
                m.writeUTF(it.strOption);
                m.writeInt(it.TinhThach);
                m.writeInt(it.Vang);
                m.writeInt(it.VangKhoa);
                m.writeInt(it.BacKhoa);
                m.writeInt(it.Bac);
            }

            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void PhucLoi(Char _myChar) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(88);
            WritePhucLoi(_myChar, m);
            _myChar.user.session.sendMessage(m);
        } catch (IOException ex) {
            Logger.getLogger(ClickEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void WritePhucLoi(Char _myChar, Message m) throws IOException {
        m.writeByte(PhucLoi.getInstance().welfareMap.size());
        String[] allTypes = {"Phúc lợi", "Quà nạp", "Quà Rank","Thẻ tháng"};
        for (String type : allTypes) {
            m.writeUTF(type);
            List<Welfare> welfares = PhucLoi.getInstance().getWelfaresByType(type);
            m.writeByte(welfares.size());
            for (int j = 0; j < welfares.size(); j++) {
                Welfare welfare = welfares.get(j);
                m.writeUTF(welfare.getWelfareName());
                m.writeInt(welfare.getId());
                String str = _myChar.getTextWelfare(welfare.getId(), welfare.getDescription());
                m.writeUTF(str);
                m.writeBoolean(welfare.isPackage());
                m.writeByte(welfare.item.size());
                for (int k = 0; k < welfare.item.size(); k++) {
                    TemplatePL item = welfare.item.get(k);
                    m.writeShort(item.Id);
                    m.writeUTF(item.name);
                    m.writeShort(item.IDPhucLoi);
                    Item it = new Item(item.IdItem);
                    it.strOptions = item.strOption;
                    it.amount = item.Amount;
                    it.write(m.writer);
                    m.writeBoolean(_myChar.checkItemPl(item));
                }
                m.writeBoolean(_myChar.checkBuyWekfare(welfare.getId()));
            }
        }
    }

    public static void ThuongBXH(Char _myChar) {
        try {
            Message m = Message.c((byte) -57);
            m.writeByte(10); // size Event
            Item it = new Item(756);
            for (int i = 0; i < 10; i++) {
                m.writeUTF("Làng lá " + i);
                m.writeUTF("" + i);
                m.writeLong(System.currentTimeMillis() - (86400000 * 10));
                m.writeLong(System.currentTimeMillis() + (86400000 * 10));
                m.writeByte(5);//size item
                for (int j = 0; j < 5; j++) {
                    m.writeUTF("" + i);
                    it.write(m.writer);
                }
            }
            _myChar.user.session.sendMessage(m);
        } catch (IOException ex) {
            Logger.getLogger(ClickEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
