package com.event;

import com.event.eventpoint.EventPoint;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.Main;

import java.util.Calendar;

/**
 * Sự kiện câu cá cuối tuần
 * Diễn ra vào 11h sáng Chủ Nhật hàng tuần
 * ID 499: Cần câu bạc (ra cá 501-505 khóa: cá tre, lóc, mè, ngát, koi vàng)
 * ID 500: Cần câu vàng (ra cá 506-509 không khóa: cá điêu hồng, rô phi, tai tượng, trảm cỏ)
 */
public class WeekendFishing extends Event {
    public static final String TOP_FISH_WEEKEND = "topfish_weekend";
    private static final int FISHING_MAP_ID = 85; // Làng Đá
    private static final int FISHING_CY = 692;
    private static final int FISHING_ROD_SILVER = 499; // Cần câu bạc
    private static final int FISHING_ROD_GOLD = 500; // Cần câu vàng
    private static final int GIAY_KHEN_CAN_THU = 615; // Giấy khen cần thủ
    
    // Phần thưởng từ cần câu bạc (501-505: cá tre, lóc, mè, ngát, koi vàng - khóa)
    private static final int[] SILVER_ROD_REWARDS = {501, 502, 503, 504, 505};
    private static final int[] SILVER_ROD_PERCENTS = {40, 30, 20, 8, 2}; // Tổng 100%
    
    // Phần thưởng từ cần câu vàng (506-509: cá điêu hồng, rô phi, tai tượng, trảm cỏ - không khóa)
    private static final int[] GOLD_ROD_REWARDS = {506, 507, 508, 509};
    private static final int[] GOLD_ROD_PERCENTS = {40, 30, 20, 10}; // Tổng 100%
    
    private static boolean isEventActive = false;
    private static long eventEndTime = 0; // Thời gian kết thúc sự kiện (1 tiếng)

    public WeekendFishing() {
        setId(5); // ID mới cho sự kiện câu cá cuối tuần
        endTime.set(2026, Calendar.DECEMBER, 31, 23, 59, 59);
        keyEventPoint.add(TOP_FISH_WEEKEND);
        keyEventPoint.add(EventPoint.DIEM_TIEU_XAI);
        menuKhaTienNu = "Nhận cần câu,Cần câu bạc (10k vàng),Cần câu vàng (50k vàng);Đổi cá,Cá tre (2k bạc khóa),Cá lóc (4k bạc khóa),Cá mè (6k bạc khóa + 1 giấy khen),Cá ngát (20k bạc khóa + 3 giấy khen),Cá koi vàng (200k bạc khóa + 30 giấy khen),Cá điêu hồng (2 vàng khóa + 2 giấy khen),Cá rô phi (5 vàng khóa + 4 giấy khen),Cá tai tượng (7 vàng khóa + 6 giấy khen),Cá trảm cỏ (15 vàng khóa + 10 giấy khen);Xem điểm;Bảng xếp hạng;Đổi cải trang AnBu;Đổi cải trang Văn Lang;Cải trang Yamato;Đổi sách tiềm năng cao;Đổi sách kỹ năng cao";
    }

    /**
     * Kích hoạt sự kiện (dùng cho lệnh admin)
     */
    public static void activateEvent() {
        isEventActive = true;
        eventEndTime = System.currentTimeMillis() + (60 * 60 * 1000); // 1 tiếng
        Log.info("🎣 Sự kiện Câu cá cuối tuần đã được kích hoạt bởi admin!");
    }

    /**
     * Tắt sự kiện
     */
    public static void deactivateEvent() {
        isEventActive = false;
        eventEndTime = 0;
        Log.info("🎣 Sự kiện Câu cá cuối tuần đã được tắt!");
    }

    /**
     * Kiểm tra xem hiện tại có phải thời gian sự kiện không
     * (11h sáng Chủ Nhật hoặc được admin kích hoạt)
     */
    public static boolean isEventTime() {
        // Kiểm tra nếu đã hết thời gian sự kiện
        if (isEventActive && System.currentTimeMillis() >= eventEndTime) {
            isEventActive = false;
            eventEndTime = 0;
            Main.HeThongCTG("🎣 Hoạt động câu cá cuối tuần đã kết thúc! Hẹn gặp lại vào Chủ Nhật tuần sau!", 2);
        }
        
        if (isEventActive) {
            return true;
        }
        
        Calendar now = Calendar.getInstance();
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        
        // Chỉ diễn ra vào Chủ Nhật (Calendar.SUNDAY = 1) lúc 11h sáng
        return dayOfWeek == Calendar.SUNDAY && hour == 11;
    }

    /**
     * Xử lý sử dụng cần câu
     */
    @Override
    public void useItem(Char p, Item item) {
        if (item.id != FISHING_ROD_SILVER && item.id != FISHING_ROD_GOLD) {
            return;
        }

        // Kiểm tra thời gian sự kiện
        if (!isEventTime()) {
            p.getService().serverMessage("Sự kiện câu cá cuối tuần chỉ diễn ra vào 11h sáng Chủ Nhật");
            return;
        }

        // Kiểm tra vị trí
        if (p.Info._mapID != FISHING_MAP_ID || p.Info.cy != FISHING_CY) {
            p.getService().serverMessage("Ở đây làm gì có cá, muốn câu cá hãy đến Làng Đá (tọa độ y=692)");
            return;
        }

        // Kiểm tra túi đồ
        if (p.getCountNullItemBag() == 0) {
            p.warningBagFull();
            return;
        }

        // Xử lý câu cá
        handleFishing(p, item);
    }

    /**
     * Xử lý logic câu cá
     */
    private void handleFishing(Char p, Item fishingRod) {
        // Xóa cần câu
        p.removeItem(fishingRod, true);
        p.msgRemoveItemBag(fishingRod);

        int fishId;
        boolean isLocked;
        
        if (fishingRod.id == FISHING_ROD_GOLD) {
            // Cần câu vàng → câu ra 506-509 (không khóa)
            fishId = randomReward(GOLD_ROD_REWARDS, GOLD_ROD_PERCENTS);
            isLocked = false;
        } else {
            // Cần câu bạc → câu ra 501-505 (khóa)
            fishId = randomReward(SILVER_ROD_REWARDS, SILVER_ROD_PERCENTS);
            isLocked = true;
        }

        // Tạo item cá
        Item fish = new Item(fishId, isLocked);
        p.addItem(fish);
        p.msgAddItemBag(fish);

        // Thêm kinh nghiệm (cuối tuần x2: 100000)
        if (!p.Info.khoaExp) {
            p.addExp(100000);
        }

        // Thông báo đặc biệt cho cá koi vàng (ID 505)
        if (fishId == 505) {
            String fishName = fish.getItemTemplate().name;
            Main.HeThongCTG("🎣 Nhẫn giả " + p.Info.name + " đã câu được " + fishName + ", thật là may mắn! Hoạt động câu cá cuối tuần đang diễn ra sôi nổi tại Làng Đá!", 2);
        } else {
            // Thông báo cho cá hiếm khác (tỷ lệ <= 10%)
            int rewardIndex = -1;
            int[] rewards = fishingRod.id == FISHING_ROD_GOLD ? GOLD_ROD_REWARDS : SILVER_ROD_REWARDS;
            int[] percents = fishingRod.id == FISHING_ROD_GOLD ? GOLD_ROD_PERCENTS : SILVER_ROD_PERCENTS;
            
            for (int i = 0; i < rewards.length; i++) {
                if (rewards[i] == fishId) {
                    rewardIndex = i;
                    break;
                }
            }
            
            if (rewardIndex >= 0 && percents[rewardIndex] <= 10) {
                String fishName = fish.getItemTemplate().name;
                Main.HeThongCTG("🎣 " + p.Info.name + " câu được " + fishName + " - Cuối tuần may mắn!", 2);
            }
        }

        // Cập nhật điểm sự kiện
        if (p.getEventPoint() == null) {
            p.setEventPoint(createEventPoint());
        }

        // Thêm điểm câu cá cuối tuần
        p.getEventPoint().addPoint(TOP_FISH_WEEKEND, 1);
        
        // Thêm điểm tiêu sài (cuối tuần x2)
        p.getEventPoint().addPoint(EventPoint.DIEM_TIEU_XAI, 2);
    }

    /**
     * Random phần thưởng dựa trên tỷ lệ
     */
    private int randomReward(int[] rewards, int[] percents) {
        int totalPercent = 0;
        for (int percent : percents) {
            totalPercent += percent;
        }

        int random = Utlis.nextInt(0, totalPercent);
        int currentPercent = 0;

        for (int i = 0; i < rewards.length; i++) {
            currentPercent += percents[i];
            if (random < currentPercent) {
                return rewards[i];
            }
        }

        return rewards[rewards.length - 1];
    }

    @Override
    public void menu(Char p, int index, int index2) {
        switch (index) {
            case 0:
                // Nhận cần câu
                switch (index2) {
                    case 0:
                        // Cần câu bạc (10k vàng)
                        buyCane(p, FISHING_ROD_SILVER, 10000, "Cần câu bạc");
                        break;
                    case 1:
                        // Cần câu vàng (50k vàng)
                        buyCane(p, FISHING_ROD_GOLD, 50000, "Cần câu vàng");
                        break;
                }
                break;
                
            case 1:
                // Đổi cá
                switch (index2) {
                    case 0:
                        exchangeFish(p, 501, 2000, 0, 0, "Cá tre");
                        break;
                    case 1:
                        exchangeFish(p, 502, 4000, 0, 0, "Cá lóc");
                        break;
                    case 2:
                        exchangeFish(p, 503, 6000, 0, 1, "Cá mè");
                        break;
                    case 3:
                        exchangeFish(p, 504, 20000, 0, 3, "Cá ngát");
                        break;
                    case 4:
                        exchangeFish(p, 505, 200000, 0, 30, "Cá koi vàng");
                        break;
                    case 5:
                        exchangeFish(p, 506, 0, 2, 2, "Cá điêu hồng");
                        break;
                    case 6:
                        exchangeFish(p, 507, 0, 5, 4, "Cá rô phi");
                        break;
                    case 7:
                        exchangeFish(p, 508, 0, 7, 6, "Cá tai tượng");
                        break;
                    case 8:
                        exchangeFish(p, 509, 0, 15, 10, "Cá trảm cỏ");
                        break;
                }
                break;
                
            case 2:
                // Xem điểm
                if (p.getEventPoint() == null) {
                    p.setEventPoint(createEventPoint());
                }
                int fishPoint = p.getEventPoint().getPoint(TOP_FISH_WEEKEND);
                int spendingPoint = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
                p.getService().sendTextNPC(
                    "Bảng điểm SK của bạn",
                    "Điểm câu cá cuối tuần: " + fishPoint + "\n" +
                    "Điểm tiêu sài: " + spendingPoint
                );
                break;
                
            case 3:
                // BXH Top Câu Cá
                viewTop(p, TOP_FISH_WEEKEND, "Bảng xếp hạng Câu Cá Cuối Tuần", "%d. %s có %s điểm câu cá");
                break;
                
            case 4:
                // Đổi cải trang AnBu
                exchangeReward(p, 3000, 514, "Cải trang AnBu");
                break;
                
            case 5:
                // Đổi cải trang Văn Lang
                exchangeReward(p, 3000, 515, "Cải trang Văn Lang");
                break;
                
            case 6:
                // Cải trang Yamato
                exchangeReward(p, 3000, 516, "Cải trang Yamato");
                break;
                
            case 7:
                // Đổi sách tiềm năng cao
                exchangeReward(p, 2000, 368, "Sách tiềm năng cao");
                break;
                
            case 8:
                // Đổi sách kỹ năng cao
                exchangeReward(p, 2000, 369, "Sách kỹ năng cao");
                break;
        }
    }
    
    /**
     * Mua cần câu
     */
    private void buyCane(Char p, int caneId, int price, String caneName) {
        if (!isEventTime()) {
            p.getService().serverMessage("Sự kiện câu cá cuối tuần chỉ diễn ra vào 11h sáng Chủ Nhật");
            return;
        }
        
        if (p.getCountNullItemBag() < 1) {
            p.warningBagFull();
            return;
        }
        
        if (p.Bag.vang < price) {
            p.getService().serverMessage("Bạn không có đủ " + price + " vàng");
            return;
        }
        
        // Trừ vàng
        p.addVang(-price);
        
        // Tạo cần câu
        Item cane = new Item(caneId);
        p.addItem(cane);
        p.msgAddItemBag(cane);
        
        p.getService().serverMessage("Mua " + caneName + " thành công!");
    }
    
    /**
     * Đổi cá lấy phần thưởng
     */
    private void exchangeFish(Char p, int fishId, int bacKhoa, int vangKhoa, int giayKhen, String fishName) {
        // Kiểm tra có cá không
        Item fish = p.FindItemBag(fishId);
        if (fish == null) {
            p.getService().serverMessage("Bạn không có " + fishName);
            return;
        }
        
        // Kiểm tra giấy khen cần thủ
        if (giayKhen > 0) {
            Item giayKhenItem = p.FindItemBag(GIAY_KHEN_CAN_THU);
            if (giayKhenItem == null || giayKhenItem.getAmount() < giayKhen) {
                p.getService().serverMessage("Bạn cần " + giayKhen + " giấy khen cần thủ");
                return;
            }
        }
        
        // Kiểm tra túi đồ
        int slotsNeeded = 0;
        if (bacKhoa > 0) slotsNeeded++;
        if (vangKhoa > 0) slotsNeeded++;
        if (giayKhen > 0) slotsNeeded++;
        
        if (p.getCountNullItemBag() < slotsNeeded) {
            p.warningBagFull();
            return;
        }
        
        // Xóa cá
        if (fish.getAmount() > 1) {
            p.removeItemByAmount(fish, 1);
            p.msgUseItemBag(fish);
        } else {
            p.removeItem(fish, true);
            p.msgRemoveItemBag(fish);
        }
        
        // Xóa giấy khen nếu cần
        if (giayKhen > 0) {
            Item giayKhenItem = p.FindItemBag(GIAY_KHEN_CAN_THU);
            if (giayKhenItem.getAmount() > giayKhen) {
                p.removeItemByAmount(giayKhenItem, giayKhen);
                p.msgUseItemBag(giayKhenItem);
            } else {
                p.removeItem(giayKhenItem, true);
                p.msgRemoveItemBag(giayKhenItem);
            }
        }
        
        // Thêm phần thưởng
        if (bacKhoa > 0) {
            p.addBacKhoa(bacKhoa);
        }
        if (vangKhoa > 0) {
            p.addVangKhoa(vangKhoa);
        }
        if (giayKhen > 0) {
            Item reward = new Item(GIAY_KHEN_CAN_THU);
            reward.isLock = true;
            reward.setAmount(giayKhen);
            p.addItem(reward);
            p.msgAddItemBag(reward);
        }
        
        p.getService().serverMessage("Đổi " + fishName + " thành công!");
    }
    
    /**
     * Đổi phần thưởng bằng điểm tiêu sài
     */
    private void exchangeReward(Char p, int pointRequired, int itemId, String itemName) {
        if (p.getEventPoint() == null) {
            p.setEventPoint(createEventPoint());
        }
        
        int currentPoint = p.getEventPoint().getPoint(EventPoint.DIEM_TIEU_XAI);
        if (currentPoint < pointRequired) {
            p.getService().serverMessage("Bạn không có đủ " + pointRequired + " điểm tiêu sài");
            return;
        }
        
        if (p.getCountNullItemBag() < 1) {
            p.warningBagFull();
            return;
        }
        
        // Trừ điểm
        p.getEventPoint().subPoint(EventPoint.DIEM_TIEU_XAI, pointRequired);
        
        // Tạo item
        Item reward = new Item(itemId);
        reward.isLock = true;
        p.addItem(reward);
        p.msgAddItemBag(reward);
        
        p.getService().serverMessage("Đổi " + itemName + " thành công!");
    }

    @Override
    public void action(Char p, int type, int amount) {
        // Không sử dụng cho sự kiện này
    }
}
