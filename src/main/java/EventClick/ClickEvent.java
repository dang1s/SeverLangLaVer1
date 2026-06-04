/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EventClick;

import Manager.Manager;
import SqlConnection.DBData;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author ADMIN
 */
public class ClickEvent {

    private static final int VONG_QUAY_NAP_WELFARE_ID = 90;
    private static final String VONG_QUAY_NAP_WELFARE_NAME = "Vòng quay nạp";

    public static void HoatDong(Char _myChar) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(56);

            m.writeUTF("c#cyanLƯU Ý TÂN THỦ\n"
                    + "c#white- CODE GAME: xem tại website hoặc kênh thông báo chính thức\n"
                    + "- Chỉ nạp qua đúng kênh Admin công bố\n"
                    + "- Không chia sẻ tài khoản, OTP hoặc key nạp cho người lạ");

            String[][] guides = new String[][]{
                {"1. Cần biết", "c#white- Cấp PK: mỗi ngày giảm 1 cấp nếu > 0\n"
                    + "- Điểm chuyên cần: mỗi ngày trừ 1 điểm, đạt mốc cao sẽ bị trừ nhiều hơn\n"
                    + "- Chat thế giới miễn phí 20 lần/ngày"},
                {"2. Trạng thái chiến đấu", "c#white- Cờ trắng: hòa bình\n"
                    + "- Cờ xanh: chiến đấu không tăng PK\n"
                    + "- Cờ đỏ và Truy Sát sẽ tăng PK"},
                {"3. Gia tộc", "c#white- Cần Gia Tộc Lệnh, gặp NPC Onoki và có bạc ngân sách\n"
                    + "- Mỗi cấp gia tộc thêm thành viên\n"
                    + "- Vượt ải, phân quyền và công hiến đều tính vào gia tộc"},
                {"4. Nhiệm vụ tuần hoàn", "c#white- Yêu cầu cấp 20+\n"
                    + "- NPC: Mei Terumi\n"
                    + "- Hoàn thành 10 nhiệm vụ để nhận thưởng"},
                {"5. Thu phục Linh Thú", "c#white- Yêu cầu cấp 15+\n"
                    + "- NPC: Rasa\n"
                    + "- Nhận nhiệm vụ mỗi ngày để săn và thu phục"},
                {"6. Địa Cung", "c#white- Yêu cầu cấp 15+\n"
                    + "- NPC: Raikage\n"
                    + "- Có thể tổ đội và nhận chìa khóa mỗi ngày"},
                {"7. Cấm thuật Izanami", "c#white- Yêu cầu cấp 40+\n"
                    + "- NPC: Onoki\n"
                    + "- Có thể tổ đội\n"
                    + "- Vòng lặp ảo tưởng có giới hạn hồi sinh"},
                {"8. Vượt Ải Gia Tộc", "c#white- Yêu cầu có gia tộc\n"
                    + "- NPC: Onoki\n"
                    + "- Chỉ Tộc trưởng hoặc Tộc phó mới mở cửa"},
                {"9. Đại Chiến Nhẫn Giả Lần III", "c#white- Yêu cầu cấp 15+\n"
                    + "- Thời gian: 20h00 - 20h10 thứ 3, 5, 7\n"
                    + "- Phá Long Trụ để gây sát thương Boss"},
                {"10. Đại Hội Nhẫn Giả", "c#white- Yêu cầu cấp 15+\n"
                    + "- Thời gian: 20h20 - 20h30 thứ 2, 4, 6\n"
                    + "- NPC: Onoki"},
                {"11. Khu Rừng Chết", "c#white- Yêu cầu cấp 15+\n"
                    + "- NPC: Anko\n"
                    + "- Mỗi ngày tối đa 2 lần"},
                {"12. Kiến Thức Nhẫn Giả", "c#white- Thời gian: 18h00 - 18h45\n"
                    + "- NPC: Senju Tsunade\n"
                    + "- Trả lời câu hỏi để nhận thưởng"},
                {"13. Cao Thủ Nhẫn Giả", "c#white- Xuất hiện theo khung giờ trong ngày\n"
                    + "- Có mặt tại nhiều map ngẫu nhiên\n"
                    + "- Đánh bại để nhận thưởng"},
                {"14. Câu Cá Cuối Tuần", "c#white- Thời gian: 11h00 - 11h30 Chủ Nhật\n"
                    + "- Địa điểm: Làng Đá\n"
                    + "- Top cao có phần thưởng giá trị"},
                {"15. Gia Tộc Tranh Đoạt Lãnh Thổ Vĩ Thú", "c#white- Vĩ thú xuất hiện tại bãi quái\n"
                    + "- Gia tộc chiếm được sẽ được cộng thêm lợi ích"},
                {"16. Đại Hội Nhẫn Giả Liên Server", "c#white- Thời gian: 20h20 - 20h30 Chủ Nhật\n"
                    + "- Đăng ký tại NPC Onoki\n"
                    + "- Dữ liệu reset vào đầu tuần"},
                {"17. Sơn Cấp Myoboku", "c#white- Thời gian: 21h30 - 21h40 thứ 2, 4, 6\n"
                    + "- NPC: Nhị Đại Hiền Nhân"},
                {"18. Tranh Giành Vĩ Thú", "c#white- Thời gian: 17h00 - 17h30 Chủ Nhật\n"
                    + "- NPC: Rasa\n"
                    + "- Top đầu có thưởng bạc và đá cường hóa"},
                {"19. Khu luyện tập", "c#white- Cần mở giới hạn kỹ năng phân thân để luyện tốt hơn\n"
                    + "- Đánh quái sẽ tích điểm luyện tập\n"
                    + "- Tu Luyện Châu sẽ tăng điểm nhận được"},
                {"20. Chuyên cần & Cống hiến", "c#white- Địa Cung, Cấm Thuật, Khu Rừng Chết và Đại Chiến cho điểm chuyên cần và công hiến\n"
                    + "- Đạt mốc sẽ nhận thưởng"},
                {"21. Đua top sự kiện", "c#white- Top Nhi Đồng, Luyện Tập, Cường Hóa, Nạp Nhiều chỉ tính khi sự kiện bật\n"
                    + "- Hết giờ sẽ tự chốt top và gửi quà qua thư"}
            };

            m.writeByte(guides.length);
            for (String[] guide : guides) {
                m.writeUTF(guide[0]);
                m.writeUTF(guide[1]);
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

    public static void HoatDongUtf8(Char _myChar) {
        HoatDong(_myChar);
    }

    public static void KhoBau(Char _myChar) {
        try {
            Message m = new Message((byte) 122);
            m.writeByte(86);

            if (_myChar.treasure != null) {
                m.writeBoolean(true);
                m.writeByte(_myChar.Info.typeVQMM);
                m.writeByte(_myChar.treasure.getQuantity());
                m.writeByte(_myChar.treasure.getIndex());
                m.writeByte(_myChar.treasure.getId());

                // Item đặc biệt ID 177 (thường là kho báu cao cấp)
                if (_myChar.treasure.getId() == 177) {
                    m.writeBoolean(true);
                } else {
                    m.writeBoolean(false);
                }
            } else {
                m.writeBoolean(false);
            }

            // Phần comment này đang bị vô hiệu hóa
            // m.writeByte(1);   // max 2
            // m.writeByte(22);  // level tạo
            // m.writeByte(30);
            // m.writeByte(40);
            // m.writeBoolean(false);
            m.writeInt(_myChar.Info.expCheTao); // EXP chế tạo
            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void NapDau(Char _myChar) { // Nạp Đầu (Quà nạp lần đầu)
        try {
            Message m = new Message((byte) 122);
            m.writeByte(89);

            // Item 1
            Item item = new Item(558, true, 2);
            item.write(m.writer);

            // Item 2 - Trang bị có option
            item = new Item(376, true);
            item.addItemOption(new ItemOption(0, 150));
            item.addItemOption(new ItemOption(1, 150));
            item.addItemOption(new ItemOption(3, 150));
            item.addItemOption(new ItemOption(209, 60));
            item.write(m.writer);

            // Item 3
            item = new Item(1004, true);
            item.amount = 1;
            item.write(m.writer);

            // Item 4
            item = new Item(687, true, 500);
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

            // Lọc ra các item không null trong rương
            List<Item> itemList = Arrays.stream(_myChar.Bag.arrItemBox)
                    .filter(item -> item != null)
                    .collect(Collectors.toList());

            m.writeShort(itemList.size());

            for (Item item : itemList) {
                if (item != null) {
                    item.write(m.writer);
                }
            }

            // Kiểm tra xem có đang giữ tiền không (theGiuTien)
            m.writeBoolean(_myChar.theGiuTien > System.currentTimeMillis());

            // Số tiền trong rương
            m.writeLong(_myChar.Bag.bacBox);
            m.writeLong(_myChar.Bag.bacKhoaBox);
            m.writeLong(_myChar.Bag.vangBox);
            m.writeLong(_myChar.Bag.vangKhoaBox);

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
            if (typeShop == 41) {
                int count41 = 0;
                for (ItemShop it : Store.getInstance().items) {
                    if (it.TypeShop == 41) {
                        count41++;
                        //  System.out.println("Item ID: " + it.id + ", ItemID: " + it.itemID + ", TypeShop: " + it.TypeShop + ", TinhThach: " + it.TinhThach);
                    }
                }
                //   System.out.println("Số item với TypeShop = 41: " + count41);
            }
            for (ItemShop it : Store.getInstance().items) {
                try {
                    if (it.TypeShop == typeShop) {
                        if (typeShop == 19) {
                            Item itemCheck = new Item(it.itemID);
                            if (itemCheck.getItemTemplate().idClass == _myChar.Info.idClass) {
                                ic.add(it);
                            }
                        } else {
                            // Shop Lục Đạo (41) và các shop khác (6, 7, 18, 30, etc.)
                            ic.add(it);
                        }
                    }
                } catch (Exception e) {
                    if (typeShop == 41) {
                        //       System.out.println("Lỗi khi xử lý item shop 41 - ID: " + it.id + ", ItemID: " + it.itemID + ", Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            boolean[] shop39;
            if (typeShop == 39) {
                shop39 = new boolean[ic.size()];
            }
            if (typeShop == 41) {
            }
            m.writeShort(ic.size());
            for (int i = 0; i < ic.size(); i++) {
                ItemShop it = ic.get(i);
                Item item = new Item(it.itemID);
                // Shop type 41 (Lục Đạo) sử dụng strOption từ database, không gọi các hàm getOption
                if (typeShop != 41) {
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

            // Lấy danh sách vật phẩm trong Shop Hạng
            int len = Manager.gI().shopRank.size();
            m.writeShort(len);

            for (int i = 0; i < len; i++) {
                ItemShop itemShop = Manager.gI().shopRank.get(i);

                m.writeShort(itemShop.id);      // ID của vật phẩm trong shop
                m.writeShort(0);                // ID template của item (mặc định 0 nếu dùng danh sách con)
                m.writeBoolean(true);           // Trạng thái khóa (mặc định khóa)
                m.writeLong(-1);                // Hạn sử dụng (-1 là vĩnh viễn)
                m.writeUTF("");                 // Các tùy chọn (options) kèm theo
                m.writeInt(0);                  // Giá bằng Tinh Thạch
                m.writeInt(itemShop.Vang);      // Giá bằng Vàng
                m.writeInt(0);                  // Giá bằng Vàng khóa
                m.writeInt(0);                  // Giá bằng Bạc
                m.writeInt(0);                  // Giá bằng Bạc khóa

                // Kiểm tra xem nhân vật đã mua vật phẩm này chưa
                m.writeBoolean(_myChar.checkItemShopRank(itemShop.id));

                // Gửi thông tin 3 vật phẩm con nằm trong gói/hạng này
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

            // Lọc danh sách vật phẩm giảm giá (Discount Store)
            List<ItemShop> ic = new ArrayList<>();
            for (ItemShop it : DiscountStore.getInstance().items) {
                if (it.TypeShop == typeShop) {
                    ic.add(it);
                }
            }

            m.writeShort(ic.size());
            for (int i = 0; i < ic.size(); i++) {
                ItemShop it = ic.get(i);

                m.writeShort(it.id);            // ID shop
                m.writeShort(it.itemID);        // ID vật phẩm
                m.writeBoolean(false);          // Không khóa
                m.writeLong(it.expire);         // Hạn dùng
                m.writeUTF(it.strOption);       // Chỉ số vật phẩm
                m.writeInt(it.TinhThach);       // Giá Tinh Thạch
                m.writeInt(it.Vang);            // Giá Vàng
                m.writeInt(it.VangKhoa);        // Giá Vàng khóa
                m.writeInt(it.BacKhoa);         // Giá Bạc khóa
                m.writeInt(it.Bac);             // Giá Bạc
                m.writeInt(it.giaCu);           // Giá cũ (để hiển thị gạch ngang)
                m.writeInt(it.conLai);          // Số lượng còn lại trong kho
            }

            // Thời gian bắt đầu và kết thúc sự kiện giảm giá (dạng Miliseconds)
            // 1730160000000L tương ứng với một mốc thời gian trong năm 2024
            m.writeLong(1730160000000L);
            m.writeLong(1730160000000L + 604800000); // Kết thúc sau 7 ngày (604.800.000 ms)

            _myChar.user.session.sendMessage(m);
        } catch (Exception e) {
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
                if (it.TypeShop == typeShop && it.he == he
                        && (item.getItemTemplate().gioiTinh == 2 || item.getItemTemplate().gioiTinh == _myChar.Info.gioiTinh)
                        && (item.getItemTemplate().idClass == 0 || item.getItemTemplate().idClass == _myChar.Info.idClass)) {
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
        m.writeByte(5);
        String[] allTypes = {"Phúc lợi", "Quà nạp", "Quà Rank", "Đầu tư", "Thẻ tháng"};
        for (String type : allTypes) {
            m.writeUTF(type);
            List<Welfare> welfares = new ArrayList<>(PhucLoi.getInstance().getWelfaresByType(type));
            if ("Đầu tư".equals(type)) {
                boolean has13 = false;
                boolean has14 = false;
                for (Welfare w : welfares) {
                    if (w.getWelfareId() == 13) has13 = true;
                    if (w.getWelfareId() == 14) has14 = true;
                    if (w.getWelfareId() == 13 || w.getWelfareId() == 14) {
                        w.setPackage(true);
                    }
                }
                if (!has13) {
                    welfares.add(new Welfare(13, "Đầu tư", 13, "Gói hào hoa", true, "Giá : 200 vàng @ Phần thưởng sau khi mua: @ +"));
                }
                if (!has14) {
                    welfares.add(new Welfare(14, "Đầu tư", 14, "Gói chí tôn", true, "Giá : 300 vàng @ Phần thưởng sau khi mua: @ +"));
                }
            }
            if ("Thẻ tháng".equals(type)) {
                boolean has15 = false;
                boolean has16 = false;
                for (Welfare w : welfares) {
                    if (w.getWelfareId() == 15) has15 = true;
                    if (w.getWelfareId() == 16) has16 = true;
                    if (w.getWelfareId() == 15 || w.getWelfareId() == 16) {
                        w.setPackage(true);
                    }
                }
                if (!has15) {
                    welfares.add(new Welfare(15, "Thẻ tháng", 15, "Thẻ tháng", true, "Giá : 100 vàng @ Phần thưởng sau khi mua: @ + Nhận ngay 100 vàng khóa@ + Mỗi ngày nhận thêm 30 vàng khóa @ (nhận liên tục trong 30 ngày)"));
                }
                if (!has16) {
                    welfares.add(new Welfare(16, "Thẻ tháng", 16, "Thẻ vĩnh viễn", true, "Giá : 300 vàng @ Phần thưởng sau khi mua: @ + Nhận ngay 300 vàng khóa@ + Mỗi ngày nhận thêm 20 vàng khóa @ (nhận vĩnh viễn)"));
                }
            }
            m.writeByte(welfares.size());

            for (Welfare welfare : welfares) {
                m.writeUTF(welfare.getWelfareName());
                m.writeInt(welfare.getId());
                String description = _myChar.getTextWelfare(welfare.getId(), welfare.getDescription());
                m.writeUTF(description);
                m.writeBoolean(welfare.isPackage());
                m.writeByte(welfare.item.size());
                for (TemplatePL itemPl : welfare.item) {
                    m.writeShort(itemPl.Id);
                    m.writeUTF(itemPl.name);
                    m.writeShort(itemPl.IDPhucLoi);
                    Item it = new Item(itemPl.IdItem);
                    it.strOptions = itemPl.strOption;
                    it.amount = itemPl.Amount;
                    it.write(m.writer);
                    m.writeBoolean(_myChar.checkItemPl(itemPl));
                }
                m.writeBoolean(_myChar.checkBuyWekfare(welfare.getId()));
            }
        }
    }

    public static void ThuongBXH(Char _myChar) {
        try {
            List<RewardDisplayData> rewards = loadRewardBXHFromDb();
            if (rewards.isEmpty()) {
                rewards = createFallbackRewardBXH();
            }

            Message m = Message.c((byte) -57);
            m.writeByte(rewards.size());

            for (RewardDisplayData reward : rewards) {
                m.writeUTF(reward.topName);
                m.writeUTF(reward.description);
                m.writeLong(reward.startTime);
                m.writeLong(reward.endTime);
                m.writeByte(reward.items.size());

                for (DisplayRewardItem rewardItem : reward.items) {
                    m.writeUTF(rewardItem.label);
                    rewardItem.item.write(m.writer);
                }
            }

            _myChar.user.session.sendMessage(m);
        } catch (IOException ex) {
            Logger.getLogger(ClickEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static List<RewardDisplayData> loadRewardBXHFromDb() {
        List<RewardDisplayData> rewards = new ArrayList<>();
        try {
            Connection conn = DBData.getConnection();
            if (conn == null || conn.isClosed()) {
                DBData.openConnection();
                conn = DBData.getConnection();
            }
            if (conn == null) {
                return rewards;
            }

            Map<Integer, RewardDisplayData> rewardMap = new HashMap<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM rewards ORDER BY type ASC, COALESCE(id, player_id) ASC, player_id ASC")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int rewardType = rs.getInt("type");
                        int rank = rs.getInt("player_id");
                        if (rank < 1 || rank > 10) {
                            continue;
                        }

                        List<DisplayRewardItem> rankItems = new ArrayList<>();
                        rankItems.addAll(buildCurrencyDisplayItems(
                                rs.getInt("silver"),
                                rs.getInt("gold"),
                                rs.getInt("silver_locked"),
                                rs.getInt("gold_locked")
                        ));
                        rankItems.addAll(buildItemDisplayItems(rs.getString("items_list")));
                        normalizeRewardItems(rank, rankItems);

                        if (rewardType == 0) {
                            for (int boardIndex = 1; boardIndex <= 12; boardIndex++) {
                                RewardDisplayData reward = rewardMap.computeIfAbsent(boardIndex, ClickEvent::createSourceRewardData);
                                setTopRewardItems(reward, rank, rankItems);
                            }
                        } else if (rewardType >= 1 && rewardType <= 12) {
                            RewardDisplayData reward = rewardMap.computeIfAbsent(rewardType, ClickEvent::createSourceRewardData);
                            setTopRewardItems(reward, rank, rankItems);
                        }
                    }
                }
            }
            for (int rank = 1; rank <= getRewardBoardCount(); rank++) {
                RewardDisplayData reward = createEmptyRewardBXH(rank);
                RewardDisplayData sourceReward = rewardMap.get(getRewardBoardSourceType(rank));
                if (sourceReward != null) {
                    applySourceRewardItems(reward, sourceReward);
                }
                rewards.add(reward);
            }
        } catch (Exception ex) {
            Log.error("Loi load thuong BXH tu db.dbData", ex);
        }
        return rewards;
    }

    private static List<DisplayRewardItem> buildCurrencyDisplayItems(int silver, int gold, int silverLocked, int goldLocked) {
        List<DisplayRewardItem> items = new ArrayList<>();

        // 1. Xử lý Bạc (Thường) - Item ID: 163
        if (silver > 0) {
            Item item = new Item(163);
            item.amount = silver;
            item.isLock = false;
            items.add(new DisplayRewardItem("Bạc x" + silver, item));
        }

        // 2. Xử lý Vàng (Thường) - Item ID: 192
        if (gold > 0) {
            Item item = new Item(192);
            item.amount = gold;
            item.isLock = false;
            items.add(new DisplayRewardItem("Vàng x" + gold, item));
        }

        // 3. Xử lý Bạc khóa
        if (silverLocked > 0) {
            Item item = new Item(163);
            item.amount = silverLocked;
            item.isLock = true;
            items.add(new DisplayRewardItem("Bạc khóa x" + silverLocked, item));
        }

        // 4. Xử lý Vàng khóa
        if (goldLocked > 0) {
            Item item = new Item(192);
            item.amount = goldLocked;
            item.isLock = true;
            items.add(new DisplayRewardItem("Vàng khóa x" + goldLocked, item));
        }

        return items;
    }

    private static List<DisplayRewardItem> buildItemDisplayItems(String itemsJson) {
        List<DisplayRewardItem> items = new ArrayList<>();
        if (itemsJson == null || itemsJson.trim().isEmpty()) {
            return items;
        }
        Object parsed = JSONValue.parse(itemsJson);
        if (!(parsed instanceof JSONArray)) {
            return items;
        }
        JSONArray array = (JSONArray) parsed;
        for (Object obj : array) {
            if (!(obj instanceof JSONObject)) {
                continue;
            }
            Item item = new Item((JSONObject) obj);
            String label = item.getItemTemplate() != null ? item.getItemTemplate().name : "V\u1eadt ph\u1ea9m";
            if (item.amount > 1) {
                label += " x" + item.amount;
            }
            items.add(new DisplayRewardItem(label, item));
        }
        return items;
    }

    private static void setTopRewardItems(RewardDisplayData reward, int topRank, List<DisplayRewardItem> rankItems) {
        if (topRank < 1 || topRank > 10) {
            return;
        }

        normalizeRewardItems(topRank, rankItems);
        int startIndex = (topRank - 1) * 5;
        String topLabel = "Top " + topRank + " " + getRewardBoardShortName(reward.topName);
        for (int i = 0; i < 5; i++) {
            DisplayRewardItem rewardItem = rankItems.get(i);
            Item itemCopy = rewardItem.item != null ? rewardItem.item.cloneItem() : null;
            reward.items.set(startIndex + i, new DisplayRewardItem(topLabel, itemCopy));
        }
    }

    private static List<RewardDisplayData> createFallbackRewardBXH() {
        List<RewardDisplayData> rewards = new ArrayList<>();
        for (int i = 1; i <= getRewardBoardCount(); i++) {
            rewards.add(createEmptyRewardBXH(i));
        }
        return rewards;
    }

    private static RewardDisplayData createEmptyRewardBXH(int rank) {
        RewardDisplayData reward = new RewardDisplayData();
        reward.topName = getRewardBoardName(rank);
        reward.description = getRewardBoardDescription(rank);
        RewardPeriod period = resolveRewardBoardPeriod(rank);
        reward.startTime = period.startTime;
        reward.endTime = period.endTime;
        String topSuffix = getRewardBoardShortName(reward.topName);
        for (int topRank = 1; topRank <= 10; topRank++) {
            List<DisplayRewardItem> rankItems = new ArrayList<>();
            Item sampleItem = new Item(11);
            rankItems.add(new DisplayRewardItem("Top " + topRank + " " + topSuffix, sampleItem));
            normalizeRewardItems(topRank, rankItems);
            for (DisplayRewardItem rewardItem : rankItems) {
                reward.items.add(new DisplayRewardItem("Top " + topRank + " " + topSuffix, rewardItem.item));
            }
        }
        return reward;
    }

    private static void normalizeRewardItems(int rank, List<DisplayRewardItem> items) {
        if (rank < 1 || rank > 10) {
            return;
        }

        while (items.size() > 5) {
            items.remove(items.size() - 1);
        }

        while (items.size() < 5) {
            Item placeholder = new Item(11);
            items.add(new DisplayRewardItem("Top " + rank, placeholder));
        }
    }

    private static RewardDisplayData createSourceRewardData(int rewardType) {
        RewardDisplayData reward = new RewardDisplayData();
        reward.topName = "Source " + rewardType;
        reward.description = "";
        for (int topRank = 1; topRank <= 10; topRank++) {
            List<DisplayRewardItem> rankItems = new ArrayList<>();
            Item sampleItem = new Item(11);
            rankItems.add(new DisplayRewardItem("Top " + topRank, sampleItem));
            normalizeRewardItems(topRank, rankItems);
            for (DisplayRewardItem rewardItem : rankItems) {
                reward.items.add(new DisplayRewardItem("Top " + topRank, rewardItem.item));
            }
        }
        return reward;
    }

    private static void applySourceRewardItems(RewardDisplayData displayReward, RewardDisplayData sourceReward) {
        for (int topRank = 1; topRank <= 10; topRank++) {
            List<DisplayRewardItem> rankItems = new ArrayList<>();
            int startIndex = (topRank - 1) * 5;
            for (int i = 0; i < 5 && startIndex + i < sourceReward.items.size(); i++) {
                DisplayRewardItem sourceItem = sourceReward.items.get(startIndex + i);
                Item itemCopy = sourceItem.item != null ? sourceItem.item.cloneItem() : null;
                rankItems.add(new DisplayRewardItem(sourceItem.label, itemCopy));
            }
            normalizeRewardItems(topRank, rankItems);
            setTopRewardItems(displayReward, topRank, rankItems);
        }
    }

    private static class RewardDisplayData {

        private String topName;
        private String description;
        private long startTime;
        private long endTime;
        private final List<DisplayRewardItem> items = new ArrayList<>();
    }

    private static class RewardPeriod {

        private final long startTime;
        private final long endTime;
        private final boolean configured;

        private RewardPeriod(long startTime, long endTime, boolean configured) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.configured = configured;
        }
    }

    private static class DisplayRewardItem {

        private final String label;
        private final Item item;

        private DisplayRewardItem(String label, Item item) {
            this.label = label;
            this.item = item;
        }
    }

    private static String getRewardBoardName(int rank) {
        return switch (rank) {
            case 1 -> "1. Phần Thưởng TOP Cao Thủ";
            case 2 -> "2. Phần Thưởng TOP Của Cải";
            case 3 -> "3. Phần Thưởng TOP Tài Phú";
            case 4 -> "4. Phần Thưởng TOP Chuyên Cần";
            case 5 -> "5. Phần Thưởng TOP Gia Tộc";
            case 6 -> "6. Phần Thưởng TOP Nhi Đồng";
            case 7 -> "7. Phần Thưởng TOP Luyện Tập";
            case 8 -> "8. Phần Thưởng TOP Cường Hóa";
            case 9 -> "9. Phần Thưởng TOP Nạp Nhiều";
            case 10 -> "10. Phần Thưởng TOP Của Cải Tuần";
            case 11 -> "11. Phần Thưởng TOP Chuyên Cần Tuần";
            case 12 -> "12. Phần Thưởng TOP Cống Hiến Tuần";
            case 13 -> "13. Phần Thưởng TOP Lôi Đài Tháng";
            default -> "Top " + rank;
        };
    }

    private static String getRewardBoardDescription(int rank) {
        return "Danh sách quà cho " + getRewardBoardName(rank);
    }

    private static String getRewardBoardShortName(String boardName) {
        int topIndex = boardName.indexOf("TOP ");
        if (topIndex >= 0) {
            return boardName.substring(topIndex + 4).trim();
        }
        return boardName;
    }

    private static RewardPeriod resolveRewardBoardPeriod(int rank) {
        return switch (rank) {
            case 1, 2, 3, 4, 5 -> loadFixedPeriod("event.properties", "duatop.start", "duatop.end");
            case 6 -> loadFixedPeriod("event.properties", "nhidong.start", "nhidong.end");
            case 7 -> loadFixedPeriod("event.properties", "luyentap.start", "luyentap.end");
            case 8 -> loadFixedPeriod("event.properties", "cuonghoa.start", "cuonghoa.end");
            case 9 -> loadFixedPeriod("event.properties", "napnhieu.start", "napnhieu.end");
            case 10 -> firstConfiguredPeriod(
                    loadWeeklyPeriod("event.properties", "cuacaituan.start"),
                    loadWeeklyPeriod("event.properties", "cuaCaiTuan.start")
            );
            case 11 -> loadWeeklyPeriod("event.properties", "chuyencantuan.start");
            case 12 -> firstConfiguredPeriod(
                    loadWeeklyPeriod("event.properties", "conghientuan.start"),
                    loadWeeklyPeriod("event.properties", "congHienTuan.start")
            );
            case 13 -> loadFixedPeriod("event.properties", "loidai.start", "loidai.end");
            default -> inactiveRewardPeriod();
        };
    }

    private static int getRewardBoardSourceType(int displayIndex) {
        return switch (displayIndex) {
            case 1, 2, 3, 4, 5, 6 -> displayIndex;
            case 7 -> 12;
            case 8 -> 8;
            case 9 -> 2;
            case 10 -> 7;
            case 11 -> 9;
            case 12 -> 10;
            case 13 -> 11;
            default -> 0;
        };
    }

    private static int getRewardBoardCount() {
        return 13;
    }

    private static RewardPeriod firstConfiguredPeriod(RewardPeriod... periods) {
        for (RewardPeriod period : periods) {
            if (period != null && period.configured) {
                return period;
            }
        }
        return inactiveRewardPeriod();
    }

    private static RewardPeriod loadFixedPeriod(String fileName, String startKey, String endKey) {
        Properties properties = loadRewardProperties(fileName);
        String startValue = properties.getProperty(startKey);
        String endValue = properties.getProperty(endKey);
        if (startValue == null || endValue == null) {
            return inactiveRewardPeriod();
        }

        long startTime = parseRewardTime(startValue);
        long endTime = parseRewardTime(endValue);
        if (startTime <= 0 || endTime <= 0 || endTime <= startTime) {
            return inactiveRewardPeriod();
        }
        return new RewardPeriod(startTime, endTime, true);
    }

    private static RewardPeriod loadWeeklyPeriod(String fileName, String startKey) {
        Properties properties = loadRewardProperties(fileName);
        String startValue = properties.getProperty(startKey);
        if (startValue == null) {
            return inactiveRewardPeriod();
        }

        long startTime = parseRewardTime(startValue);
        if (startTime <= 0) {
            return inactiveRewardPeriod();
        }

        long endTime = startTime + (7L * 24 * 60 * 60 * 1000);
        return new RewardPeriod(startTime, endTime, true);
    }

    private static Properties loadRewardProperties(String fileName) {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(fileName)) {
            properties.load(input);
        } catch (IOException ignored) {
        }
        return properties;
    }

    private static long parseRewardTime(String value) {
        try {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(value).getTime();
        } catch (Exception ex) {
            return 0L;
        }
    }

    private static RewardPeriod inactiveRewardPeriod() {
        return new RewardPeriod(0L, 0L, false);
    }

    public static void ThuongBXHUtf8(Char _myChar) {
        ThuongBXH(_myChar);
    }
}
