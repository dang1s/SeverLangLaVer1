package Manager;

import EventClick.ConfigCuongHoa;
import EventClick.ConfigLuyenTap;
import EventClick.ConfigNhiDong;
import EventClick.ClickTop;
import EventClick.InfoTop;
import SqlConnection.DBData;
import SqlConnection.CharDB;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.ServerManager;
import Template.TemplateThu;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.ToIntFunction;

/**
 * Quản lý tự động trao thưởng cho bảng xếp hạng
 */
public class RankingRewardManager {
    
    // Trạng thái đã trao thưởng (tránh trao lại)
    private static Map<Integer, Set<String>> rewardedPlayers = new HashMap<>(); // topIndex -> player names
    private static final File MANAGED_EVENT_BASELINE_FILE = new File("event_top_baselines.json");
    private static final Map<String, Map<String, Integer>> managedEventBaselines = new HashMap<>();

    public enum ManagedEventTop {
        NHI_DONG("nhidong", "Top Nhi Đồng", 6),
        LUYEN_TAP("luyentap", "Top Luyện Tập", 12),
        CUONG_HOA("cuonghoa", "Top Cường Hóa", 8),
        NAP_NHIEU("napnhieu", "Top Nạp Nhiều", 2);

        private final String key;
        private final String displayName;
        private final int rewardType;

        ManagedEventTop(String key, String displayName, int rewardType) {
            this.key = key;
            this.displayName = displayName;
            this.rewardType = rewardType;
        }
    }

    static {
        loadManagedEventBaselines();
    }
    
    /**
     * Khởi tạo hệ thống ranking
     */
    public static void initialize() {
        // Khởi tạo set rewarded
        for (int i = 0; i < 12; i++) {
            if (!rewardedPlayers.containsKey(i)) {
                rewardedPlayers.put(i, new HashSet<>());
            }
        }
        Log.info("✅ [RankingRewardManager] Đã khởi tạo hệ thống ranking");
    }

    public static String[] getManagedEventTopNames() {
        ManagedEventTop[] values = ManagedEventTop.values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].displayName;
        }
        return names;
    }

    public static synchronized void captureManagedEventBaselines() {
        managedEventBaselines.clear();
        managedEventBaselines.put(ManagedEventTop.LUYEN_TAP.key,
                toScoreMap(CharDB.getTopLuyenTap(ConfigLuyenTap.START, ConfigLuyenTap.END), info -> info.luyenTap));
        managedEventBaselines.put(ManagedEventTop.CUONG_HOA.key,
                toScoreMap(CharDB.getTopCuongHoaCurrent(), info -> info.cuongHoa));
        managedEventBaselines.put(ManagedEventTop.NAP_NHIEU.key,
                toScoreMap(CharDB.getTopNapTuanCurrentAll(), info -> info.pointNapTuan));
        saveManagedEventBaselines();
        Log.info("✅ [RankingRewardManager] Đã chụp mốc điểm bắt đầu cho top sự kiện");
    }

    public static void applyManagedEventBaseline(ManagedEventTop eventTop, List<InfoTop> list) {
        if (eventTop == null || list == null || list.isEmpty()) {
            return;
        }
        Map<String, Integer> baselineMap = managedEventBaselines.getOrDefault(eventTop.key, Collections.emptyMap());
        Iterator<InfoTop> iterator = list.iterator();
        while (iterator.hasNext()) {
            InfoTop info = iterator.next();
            int baseline = baselineMap.getOrDefault(info.name, 0);
            switch (eventTop) {
                case LUYEN_TAP:
                    info.luyenTap = Math.max(0, info.luyenTap - baseline);
                    if (info.luyenTap <= 0) {
                        iterator.remove();
                    }
                    break;
                case CUONG_HOA:
                    info.cuongHoa = Math.max(0, info.cuongHoa - baseline);
                    if (info.cuongHoa <= 0) {
                        iterator.remove();
                    }
                    break;
                case NAP_NHIEU:
                    info.pointNapTuan = Math.max(0, info.pointNapTuan - baseline);
                    if (info.pointNapTuan <= 0) {
                        iterator.remove();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static boolean rewardManagedEventTops() {
        boolean hasSuccess = false;
        for (ManagedEventTop eventTop : ManagedEventTop.values()) {
            hasSuccess |= rewardManagedEventTop(eventTop);
        }
        return hasSuccess;
    }
    
    /**
     * Phát quà thủ công cho top players (gọi từ GUI)
     */
    public static boolean manualRewardTopPlayers(int topIndex, boolean resetRewardedList) {
        try {
            if (topIndex < 0 || topIndex >= 12) {
                Log.error("❌ [RankingRewardManager] Top index không hợp lệ: " + topIndex);
                return false;
            }
            
            // Reset danh sách đã trao thưởng nếu cần
            if (resetRewardedList || !rewardedPlayers.containsKey(topIndex)) {
                rewardedPlayers.put(topIndex, new HashSet<>());
            }
            
            List<InfoTop> topList = getTopList(topIndex);
            if (topList == null || topList.isEmpty()) {
                Log.warn("⚠️ [RankingRewardManager] Không có dữ liệu cho Top " + (topIndex + 1));
                return false;
            }
            
            String topName = getTopName(topIndex);
            int maxRank = (topIndex < 6) ? 5 : 4; // Top 1-6: Top 1-5, Top 7-12: Top 1-4
            
            Log.info("🎁 [RankingRewardManager] Bắt đầu phát quà thủ công cho " + topName + " (Top " + (topIndex + 1) + ")");
            
            int rewardedCount = 0;
            
            for (int rank = 0; rank < Math.min(maxRank, topList.size()); rank++) {
                InfoTop playerInfo = topList.get(rank);
                if (playerInfo == null || playerInfo.name == null) continue;
                
                String playerName = playerInfo.name;
                
                // Lấy phần thưởng theo rank
                List<RewardItem> rewards = getRewardForTopAndRank(topIndex, rank);
                if (rewards == null || rewards.isEmpty()) continue;
                
                // Gửi thư với phần thưởng
                sendRewardLetter(playerName, topName, rank + 1, rewards);
                
                // Đánh dấu đã trao thưởng
                rewardedPlayers.get(topIndex).add(playerName);
                
                rewardedCount++;
                Log.info("✅ [RankingRewardManager] Đã phát quà Top " + (rank + 1) + " cho " + playerName + " (" + topName + ")");
            }
            
            Log.info("🎉 [RankingRewardManager] Hoàn tất phát quà cho " + topName + " - Tổng: " + rewardedCount + " người chơi");
            return true;
        } catch (Exception ex) {
            Log.error("❌ [RankingRewardManager] Lỗi khi phát quà cho Top " + (topIndex + 1) + ": " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private static boolean rewardManagedEventTop(ManagedEventTop eventTop) {
        try {
            List<InfoTop> topList = getManagedEventTopList(eventTop);
            if (topList.isEmpty()) {
                Log.warn("⚠️ [RankingRewardManager] Không có dữ liệu cho " + eventTop.displayName);
                return false;
            }

            int rewardedCount = 0;
            for (int rank = 1; rank <= Math.min(10, topList.size()); rank++) {
                InfoTop playerInfo = topList.get(rank - 1);
                if (playerInfo == null || playerInfo.name == null || playerInfo.name.isEmpty()) {
                    continue;
                }

                RewardPackage rewardPackage = loadRewardPackage(eventTop.rewardType, rank);
                if (rewardPackage == null || rewardPackage.isEmpty()) {
                    continue;
                }

                sendRewardPackageLetter(playerInfo.name, eventTop.displayName, rank, rewardPackage);
                rewardedCount++;
                Log.info("✅ [RankingRewardManager] Đã auto trao quà " + eventTop.displayName + " Top " + rank + " cho " + playerInfo.name);
            }

            Log.info("🎉 [RankingRewardManager] Hoàn tất auto trao quà cho " + eventTop.displayName + " - Tổng: " + rewardedCount);
            return rewardedCount > 0;
        } catch (Exception ex) {
            Log.error("❌ [RankingRewardManager] Lỗi auto trao quà cho " + eventTop.displayName + ": " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    private static List<InfoTop> getManagedEventTopList(ManagedEventTop eventTop) {
        List<InfoTop> list = switch (eventTop) {
            case NHI_DONG -> CharDB.getTopNhiDong(ConfigNhiDong.START, ConfigNhiDong.END);
            case LUYEN_TAP -> CharDB.getTopLuyenTap(ConfigLuyenTap.START, ConfigLuyenTap.END);
            case CUONG_HOA -> CharDB.getTopCuongHoaCurrent();
            case NAP_NHIEU -> CharDB.getTopNapTuanCurrentAll();
        };

        if (eventTop != ManagedEventTop.NHI_DONG) {
            applyManagedEventBaseline(eventTop, list);
        }

        Comparator<InfoTop> comparator = switch (eventTop) {
            case NHI_DONG -> Comparator
                    .comparingInt((InfoTop t) -> t.level).reversed()
                    .thenComparing(Comparator.comparingLong((InfoTop t) -> t.exp).reversed())
                    .thenComparing(t -> t.name);
            case LUYEN_TAP -> Comparator
                    .comparingInt((InfoTop t) -> t.luyenTap).reversed()
                    .thenComparing(t -> t.name);
            case CUONG_HOA -> Comparator
                    .comparingInt((InfoTop t) -> t.cuongHoa).reversed()
                    .thenComparing(t -> t.name);
            case NAP_NHIEU -> Comparator
                    .comparingInt((InfoTop t) -> t.pointNapTuan).reversed()
                    .thenComparing(t -> t.name);
        };

        list = new ArrayList<>(list);
        list.sort(comparator);
        return list;
    }
    
    /**
     * Lấy danh sách top theo index
     */
    private static List<InfoTop> getTopList(int topIndex) {
        switch (topIndex) {
            case 0: // Top Cao Thủ
                ClickTop.cTop = null; // Reset để load lại
                return CharDB.getTop((byte) 0);
            case 1: // Top Nạp Nhiều
                ClickTop.cCuaCai = null; // Reset để load lại
                return CharDB.getTopCuaCai();
            case 2: // Top Tài Phú
                ClickTop.cTaiPhu = null; // Reset để load lại
                return CharDB.getTopTaiPhu();
            case 3: // Top Chuyên Cần
                ClickTop.cTop = null;
                return CharDB.getTop((byte) 0); // TODO: Cần thêm method getTopChuyenCan
            case 4: // Top Gia Tộc
                if (ClickTop.RANKED[2] == null) {
                    CharDB.initTopGiaToc();
                }
                // TODO: Convert Vector<Clan> to List<InfoTop> nếu cần
                return new ArrayList<>();
            case 5: // Top Nhi Đồng
                ClickTop.cTop = null;
                return CharDB.getTop((byte) 0); // TODO: Cần thêm method getTopNhiDong
            default:
                // Top 7-12: Cần thêm các method tương ứng
                return new ArrayList<>();
        }
    }
    
    /**
     * Lấy tên top theo index
     */
    private static String getTopName(int topIndex) {
        String[] names = new String[]{
            "Top Cao Thủ",
            "Top Nạp Nhiều",
            "Top Tài Phú",
            "Top Chuyên Cần",
            "Top Gia Tộc",
            "Top Nhi Đồng",
            "Top Của Cải Tuần",
            "Top Cường Hóa Tuần",
            "Top Chuyên Cần Tuần",
            "Top Cống Hiến Tuần",
            "Top Lôi Đài Tháng",
            "Top Luyện Tập Tuần"
        };
        return topIndex >= 0 && topIndex < names.length ? names[topIndex] : "Top " + (topIndex + 1);
    }
    
    /**
     * Lấy phần thưởng cho top và rank cụ thể
     */
    private static List<RewardItem> getRewardForTopAndRank(int topIndex, int rank) {
        // Mỗi TopIndex (0-11) có bộ quà RIÊNG
        return getRewardForAdminTopIndex(topIndex, rank);
    }
    
    /**
     * Lấy phần thưởng cho hạng cụ thể theo TopIndex (0-11) dành cho admin phát quà BXH
     */
    private static List<RewardItem> getRewardForAdminTopIndex(int topIndex, int rank) {
        List<RewardItem> rewards = new ArrayList<>();
        
        // rank: 0 = Top 1, 1 = Top 2, 2 = Top 3, 3 = Top 4, 4 = Top 5
        switch (topIndex) {
            case 0: // Top Cao Thủ (Top 1)
                switch (rank) {
                    case 0: // Top 1
                        rewards.add(new RewardItem(174, 2, "", 0));
                        rewards.add(new RewardItem(428, 2, "", 0));
                        rewards.add(new RewardItem(163, 300000, "", 0));
                        rewards.add(new RewardItem(880, 1, "", 0));
                        rewards.add(new RewardItem(724, 1, "", 0));
                        break;
                    case 1: // Top 2
                        rewards.add(new RewardItem(174, 2, "", 0));
                        rewards.add(new RewardItem(428, 2, "", 0));
                        rewards.add(new RewardItem(163, 250000, "", 0));
                        rewards.add(new RewardItem(880, 1, "", 0));
                        rewards.add(new RewardItem(724, 1, "", 0));
                        break;
                    case 2: // Top 3
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(428, 1, "", 0));
                        rewards.add(new RewardItem(163, 200000, "", 0));
                        rewards.add(new RewardItem(880, 1, "", 0));
                        break;
                    case 3: // Top 4
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(428, 1, "", 0));
                        rewards.add(new RewardItem(163, 150000, "", 0));
                        break;
                    case 4: // Top 5
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(163, 100000, "", 0));
                        break;
                }
                break;
                
            case 1: // Top Nạp Nhiều (Top 2)
                switch (rank) {
                    case 0:
                        rewards.add(new RewardItem(174, 2, "", 0));
                        rewards.add(new RewardItem(428, 2, "", 0));
                        rewards.add(new RewardItem(163, 300000, "", 0));
                        rewards.add(new RewardItem(880, 1, "", 0));
                        rewards.add(new RewardItem(724, 1, "", 0));
                        break;
                    case 1:
                        rewards.add(new RewardItem(174, 2, "", 0));
                        rewards.add(new RewardItem(428, 2, "", 0));
                        rewards.add(new RewardItem(163, 250000, "", 0));
                        rewards.add(new RewardItem(880, 1, "", 0));
                        rewards.add(new RewardItem(724, 1, "", 0));
                        break;
                    case 2:
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(428, 1, "", 0));
                        rewards.add(new RewardItem(163, 200000, "", 0));
                        rewards.add(new RewardItem(880, 1, "", 0));
                        break;
                    case 3:
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(428, 1, "", 0));
                        rewards.add(new RewardItem(163, 150000, "", 0));
                        break;
                    case 4:
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(163, 100000, "", 0));
                        break;
                }
                break;
                
            case 2: // Top Tài Phú (Top 3)
                switch (rank) {
                    case 0:
                        rewards.add(new RewardItem(174, 2, "", 0));
                        rewards.add(new RewardItem(428, 2, "", 0));
                        rewards.add(new RewardItem(163, 300000, "", 0));
                        rewards.add(new RewardItem(880, 1, "", 0));
                        rewards.add(new RewardItem(724, 1, "", 0));
                        break;
                    case 1:
                        rewards.add(new RewardItem(174, 2, "", 0));
                        rewards.add(new RewardItem(428, 2, "", 0));
                        rewards.add(new RewardItem(163, 250000, "", 0));
                        rewards.add(new RewardItem(880, 1, "", 0));
                        rewards.add(new RewardItem(724, 1, "", 0));
                        break;
                    case 2:
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(428, 1, "", 0));
                        rewards.add(new RewardItem(163, 200000, "", 0));
                        rewards.add(new RewardItem(880, 1, "", 0));
                        break;
                    case 3:
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(428, 1, "", 0));
                        rewards.add(new RewardItem(163, 150000, "", 0));
                        break;
                    case 4:
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(163, 100000, "", 0));
                        break;
                }
                break;
                
            // Các top khác có thể thêm tương tự
            default:
                // Mặc định: giống Top Cao Thủ
                switch (rank) {
                    case 0:
                        rewards.add(new RewardItem(174, 2, "", 0));
                        rewards.add(new RewardItem(428, 2, "", 0));
                        rewards.add(new RewardItem(163, 300000, "", 0));
                        break;
                    case 1:
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(428, 1, "", 0));
                        rewards.add(new RewardItem(163, 250000, "", 0));
                        break;
                    case 2:
                        rewards.add(new RewardItem(174, 1, "", 0));
                        rewards.add(new RewardItem(163, 200000, "", 0));
                        break;
                    case 3:
                        rewards.add(new RewardItem(163, 150000, "", 0));
                        break;
                }
                break;
        }
        
        return rewards;
    }
    
    /**
     * Gửi thư phần thưởng cho player
     */
    private static void sendRewardLetter(String playerName, String topName, int rank, List<RewardItem> rewards) {
        try {
            String title = "Phần thưởng " + topName + " - Top " + rank;
            
            // Tính tổng bạc khóa, vàng khóa từ rewards
            int totalBacKhoa = 0;
            int totalVangKhoa = 0;
            List<Item> items = new ArrayList<>();
            
            for (RewardItem reward : rewards) {
                if (reward.itemId == 163) { // Bạc khóa
                    totalBacKhoa += reward.quantity;
                } else if (reward.itemId == 192) { // Vàng khóa
                    totalVangKhoa += reward.quantity;
                } else {
                    // Tạo item
                    Item item = new Item((short) reward.itemId);
                    item.amount = reward.quantity;
                    if (reward.options != null && !reward.options.isEmpty()) {
                        item.strOptions = reward.options;
                    }
                    // Set hạn sử dụng (HSD)
                    if (reward.hsd > 0) {
                        item.expiry = System.currentTimeMillis() + (reward.hsd * 86400000L); // Convert days to ms
                    }
                    items.add(item);
                }
            }
            
            // Tìm player online hoặc offline
            Char player = ServerManager.findCharByName(playerName);
            if (player != null) {
                // Player online
                sendLetterToPlayer(player, title, topName, rank, totalBacKhoa, totalVangKhoa, items);
            } else {
                // Player offline - lấy từ DB
                com.sg188.real.Char offlinePlayer = SqlConnection.CharDB.getCharByName(playerName);
                if (offlinePlayer != null) {
                    sendLetterToOfflinePlayer(offlinePlayer, playerName, title, topName, rank, totalBacKhoa, totalVangKhoa, items);
                } else {
                    Log.warn("⚠️ [RankingRewardManager] Không tìm thấy player: " + playerName);
                }
            }
        } catch (Exception ex) {
            Log.error("❌ [RankingRewardManager] Lỗi khi gửi thư cho " + playerName + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Gửi thư cho player online
     */
    private static void sendLetterToPlayer(Char player, String title, String topName, int rank, 
                                          int totalBacKhoa, int totalVangKhoa, List<Item> items) {
        // Gửi thư với tiền (nếu có)
        if (totalBacKhoa > 0 || totalVangKhoa > 0) {
            String content = "Chúc mừng bạn đã đạt Top " + rank + " trong " + topName + "!\n\nPhần thưởng tiền đã được gửi kèm trong thư này.";
            TemplateThu thu = new TemplateThu();
            int id = player.letters.size() + 1;
            if (player.letters.size() > 0) {
                id = player.letters.get(player.letters.size() - 1).id + 1;
            }
            thu.id = (short) id;
            thu.NameNguoiGui = "Hệ thống";
            thu.Title = title;
            thu.NoiDungThu = content;
            thu.BacKhoa = totalBacKhoa;
            thu.VangKhoa = totalVangKhoa;
            thu.TimeEnd = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000); // 30 ngày
            player.letters.add(thu);
            player.getService().reloadLetter();
        }
        
        // Gửi từng item trong từng thư riêng
        for (Item item : items) {
            String itemContent = "Chúc mừng bạn đã đạt Top " + rank + " trong " + topName + "!\n\nPhần thưởng vật phẩm đã được gửi kèm trong thư này.";
            TemplateThu thu = new TemplateThu();
            int id = player.letters.size() + 1;
            if (player.letters.size() > 0) {
                id = player.letters.get(player.letters.size() - 1).id + 1;
            }
            thu.id = (short) id;
            thu.NameNguoiGui = "Hệ thống";
            thu.Title = title;
            thu.NoiDungThu = itemContent;
            thu.Item = item;
            thu.TimeEnd = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000); // 30 ngày
            player.letters.add(thu);
            player.getService().reloadLetter();
        }
    }
    
    /**
     * Gửi thư cho player offline
     */
    private static void sendLetterToOfflinePlayer(com.sg188.real.Char offlinePlayer, String playerName, String title, String topName, int rank,
                                                  int totalBacKhoa, int totalVangKhoa, List<Item> items) {
        // Gửi thư với tiền (nếu có)
        if (totalBacKhoa > 0 || totalVangKhoa > 0) {
            String content = "Chúc mừng bạn đã đạt Top " + rank + " trong " + topName + "!\n\nPhần thưởng tiền đã được gửi kèm trong thư này.";
            TemplateThu thu = new TemplateThu();
            int id = offlinePlayer.letters.size() + 1;
            if (offlinePlayer.letters.size() > 0) {
                id = offlinePlayer.letters.get(offlinePlayer.letters.size() - 1).id + 1;
            }
            thu.id = (short) id;
            thu.NameNguoiGui = "Hệ thống";
            thu.Title = title;
            thu.NoiDungThu = content;
            thu.BacKhoa = totalBacKhoa;
            thu.VangKhoa = totalVangKhoa;
            thu.TimeEnd = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000); // 30 ngày
            offlinePlayer.letters.add(thu);
        }
        
        // Gửi từng item trong từng thư riêng
        for (Item item : items) {
            String itemContent = "Chúc mừng bạn đã đạt Top " + rank + " trong " + topName + "!\n\nPhần thưởng vật phẩm đã được gửi kèm trong thư này.";
            TemplateThu thu = new TemplateThu();
            int id = offlinePlayer.letters.size() + 1;
            if (offlinePlayer.letters.size() > 0) {
                id = offlinePlayer.letters.get(offlinePlayer.letters.size() - 1).id + 1;
            }
            thu.id = (short) id;
            thu.NameNguoiGui = "Hệ thống";
            thu.Title = title;
            thu.NoiDungThu = itemContent;
            thu.Item = item;
            thu.TimeEnd = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000); // 30 ngày
            offlinePlayer.letters.add(thu);
        }
        
        // Lưu vào DB
        SqlConnection.CharDB.updateDBThu(offlinePlayer, playerName);
    }

    private static RewardPackage loadRewardPackage(int rewardType, int rank) {
        try {
            Connection conn = DBData.getConnection();
            if (conn == null || conn.isClosed()) {
                DBData.openConnection();
                conn = DBData.getConnection();
            }
            if (conn == null) {
                return null;
            }

            RewardPackage rewardPackage = queryRewardPackage(conn, rewardType, rank);
            if (rewardPackage != null) {
                return rewardPackage;
            }
            return queryRewardPackage(conn, 0, rank);
        } catch (Exception ex) {
            Log.error("❌ [RankingRewardManager] Lỗi load quà từ DB type=" + rewardType + ", rank=" + rank, ex);
            return null;
        }
    }

    private static RewardPackage queryRewardPackage(Connection conn, int rewardType, int rank) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT silver, gold, silver_locked, gold_locked, items_list FROM rewards WHERE type = ? AND player_id = ? ORDER BY id ASC LIMIT 1")) {
            ps.setInt(1, rewardType);
            ps.setInt(2, rank);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                RewardPackage rewardPackage = new RewardPackage();
                rewardPackage.bac = rs.getInt("silver");
                rewardPackage.vang = rs.getInt("gold");
                rewardPackage.bacKhoa = rs.getInt("silver_locked");
                rewardPackage.vangKhoa = rs.getInt("gold_locked");

                Object parsed = JSONValue.parse(rs.getString("items_list"));
                if (parsed instanceof JSONArray array) {
                    for (Object obj : array) {
                        if (obj instanceof JSONObject) {
                            Item item = new Item((JSONObject) obj);
                            rewardPackage.items.add(item);
                        }
                    }
                }
                return rewardPackage;
            }
        }
    }

    private static void sendRewardPackageLetter(String playerName, String topName, int rank, RewardPackage rewardPackage) {
        try {
            String title = "Phần thưởng " + topName + " - Top " + rank;
            Char player = ServerManager.findCharByName(playerName);
            if (player != null) {
                sendRewardPackageToOnlinePlayer(player, title, topName, rank, rewardPackage);
            } else {
                Char offlinePlayer = CharDB.getCharByName(playerName);
                if (offlinePlayer != null) {
                    sendRewardPackageToOfflinePlayer(offlinePlayer, playerName, title, topName, rank, rewardPackage);
                } else {
                    Log.warn("⚠️ [RankingRewardManager] Không tìm thấy người chơi để gửi quà: " + playerName);
                }
            }
        } catch (Exception ex) {
            Log.error("❌ [RankingRewardManager] Lỗi gửi quà DB cho " + playerName + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void sendRewardPackageToOnlinePlayer(Char player, String title, String topName, int rank, RewardPackage rewardPackage) {
        if (rewardPackage.hasCurrency()) {
            TemplateThu thu = new TemplateThu();
            int id = player.letters.size() + 1;
            if (player.letters.size() > 0) {
                id = player.letters.get(player.letters.size() - 1).id + 1;
            }
            thu.id = (short) id;
            thu.NameNguoiGui = "Hệ thống";
            thu.Title = title;
            thu.NoiDungThu = "Chúc mừng bạn đã đạt Top " + rank + " trong " + topName + "!";
            thu.Bac = rewardPackage.bac;
            thu.BacKhoa = rewardPackage.bacKhoa;
            thu.Vang = rewardPackage.vang;
            thu.VangKhoa = rewardPackage.vangKhoa;
            thu.TimeEnd = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
            player.letters.add(thu);
        }

        for (Item item : rewardPackage.items) {
            TemplateThu thu = new TemplateThu();
            int id = player.letters.size() + 1;
            if (player.letters.size() > 0) {
                id = player.letters.get(player.letters.size() - 1).id + 1;
            }
            thu.id = (short) id;
            thu.NameNguoiGui = "Hệ thống";
            thu.Title = title;
            thu.NoiDungThu = "Chúc mừng bạn đã đạt Top " + rank + " trong " + topName + "!";
            thu.Item = item.cloneItem();
            thu.TimeEnd = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
            player.letters.add(thu);
        }

        if (rewardPackage.hasCurrency() || !rewardPackage.items.isEmpty()) {
            player.getService().reloadLetter();
        }
    }

    private static void sendRewardPackageToOfflinePlayer(Char offlinePlayer, String playerName, String title, String topName, int rank, RewardPackage rewardPackage) {
        if (rewardPackage.hasCurrency()) {
            TemplateThu thu = new TemplateThu();
            int id = offlinePlayer.letters.size() + 1;
            if (offlinePlayer.letters.size() > 0) {
                id = offlinePlayer.letters.get(offlinePlayer.letters.size() - 1).id + 1;
            }
            thu.id = (short) id;
            thu.NameNguoiGui = "Hệ thống";
            thu.Title = title;
            thu.NoiDungThu = "Chúc mừng bạn đã đạt Top " + rank + " trong " + topName + "!";
            thu.Bac = rewardPackage.bac;
            thu.BacKhoa = rewardPackage.bacKhoa;
            thu.Vang = rewardPackage.vang;
            thu.VangKhoa = rewardPackage.vangKhoa;
            thu.TimeEnd = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
            offlinePlayer.letters.add(thu);
        }

        for (Item item : rewardPackage.items) {
            TemplateThu thu = new TemplateThu();
            int id = offlinePlayer.letters.size() + 1;
            if (offlinePlayer.letters.size() > 0) {
                id = offlinePlayer.letters.get(offlinePlayer.letters.size() - 1).id + 1;
            }
            thu.id = (short) id;
            thu.NameNguoiGui = "Hệ thống";
            thu.Title = title;
            thu.NoiDungThu = "Chúc mừng bạn đã đạt Top " + rank + " trong " + topName + "!";
            thu.Item = item.cloneItem();
            thu.TimeEnd = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
            offlinePlayer.letters.add(thu);
        }

        CharDB.updateDBThu(offlinePlayer, playerName);
    }

    private static Map<String, Integer> toScoreMap(List<InfoTop> list, ToIntFunction<InfoTop> extractor) {
        Map<String, Integer> map = new HashMap<>();
        for (InfoTop info : list) {
            if (info != null && info.name != null && !info.name.isEmpty()) {
                map.put(info.name, extractor.applyAsInt(info));
            }
        }
        return map;
    }

    private static synchronized void loadManagedEventBaselines() {
        managedEventBaselines.clear();
        if (!MANAGED_EVENT_BASELINE_FILE.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(MANAGED_EVENT_BASELINE_FILE)) {
            Object parsed = JSONValue.parse(reader);
            if (!(parsed instanceof JSONObject root)) {
                return;
            }
            for (ManagedEventTop eventTop : ManagedEventTop.values()) {
                Object eventObj = root.get(eventTop.key);
                if (!(eventObj instanceof JSONObject scores)) {
                    continue;
                }
                Map<String, Integer> baselineMap = new HashMap<>();
                for (Object key : scores.keySet()) {
                    Object value = scores.get(key);
                    if (key != null && value != null) {
                        baselineMap.put(String.valueOf(key), Integer.parseInt(String.valueOf(value)));
                    }
                }
                managedEventBaselines.put(eventTop.key, baselineMap);
            }
        } catch (Exception ex) {
            Log.error("❌ [RankingRewardManager] Lỗi đọc file mốc top sự kiện", ex);
        }
    }

    private static synchronized void saveManagedEventBaselines() {
        JSONObject root = new JSONObject();
        for (Map.Entry<String, Map<String, Integer>> entry : managedEventBaselines.entrySet()) {
            JSONObject scores = new JSONObject();
            for (Map.Entry<String, Integer> scoreEntry : entry.getValue().entrySet()) {
                scores.put(scoreEntry.getKey(), scoreEntry.getValue());
            }
            root.put(entry.getKey(), scores);
        }
        try (FileWriter writer = new FileWriter(MANAGED_EVENT_BASELINE_FILE, false)) {
            writer.write(root.toJSONString());
        } catch (Exception ex) {
            Log.error("❌ [RankingRewardManager] Lỗi lưu file mốc top sự kiện", ex);
        }
    }

    private static class RewardPackage {
        private int bac;
        private int bacKhoa;
        private int vang;
        private int vangKhoa;
        private final List<Item> items = new ArrayList<>();

        private boolean hasCurrency() {
            return bac > 0 || bacKhoa > 0 || vang > 0 || vangKhoa > 0;
        }

        private boolean isEmpty() {
            return !hasCurrency() && items.isEmpty();
        }
    }
    
    /**
     * Class để lưu thông tin reward item
     */
    public static class RewardItem {
        public int itemId;
        public int quantity;
        public String options;
        public int hsd; // Hạn sử dụng (số ngày)
        
        public RewardItem(int itemId, int quantity, String options, int hsd) {
            this.itemId = itemId;
            this.quantity = quantity;
            this.options = options;
            this.hsd = hsd;
        }
    }
}

