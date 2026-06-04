package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.world.data.dataDaiHoi;
import MapService.zones.ZoneDaihoi;
import Service.HanderMessage;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.Main;
import com.sg188.server.ServerManager;
import com.sg188.server.lib.Message;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DaiHoiNhanGia extends World {
    public static DaiHoiNhanGia DAI_HOI_NHAN_GIA;

    private static final ZoneId ZONE_VN = ZoneId.of("Asia/Ho_Chi_Minh");

    public List<Char> listPlayerInMap;
    public List<Char> listPlayerViewer;

    public long timeStart;
    public boolean isOpened;
    public boolean allCharPlayerAttack;
    public boolean round16;
    public boolean round8;
    public boolean round4;
    public boolean round2;
    public long timeStartRound;
    public List<Char> listGroupDaiHoi;

    public List<dataDaiHoi> listPlayerTarget;
    public List<dataDaiHoi> listPlayerBXH;
    public boolean canRespawnHS;
    public boolean resetListDaiHoi;
    public boolean sendTime;
    public boolean waitChangeRound;
    public boolean accumulationPhase;
    public boolean testMode;
    List<Char> usedChars;

    public int numRound;

    private boolean pendingFightStart;
    private long roundDelayUntilMs;

    private final Set<String> rewardedJoin = new HashSet<>();
    private final Set<String> rewarded16 = new HashSet<>();
    private final Set<String> rewarded8 = new HashSet<>();
    private final Set<String> rewarded4 = new HashSet<>();
    private final Set<String> rewarded2 = new HashSet<>();
    private final Set<String> rewardedWin = new HashSet<>();
    private final java.util.Map<Integer, Long> deathRespawnAt = new java.util.HashMap<>();


    public DaiHoiNhanGia() {
        setType(World.DAI_HOI_VO_THUAT);
        this.name = "DaiHoiNhanGia";
        this.countDown = 2 * 60; // Thời gian CHỜ trước khi MỞ SỰ KIỆN (countdown ban đầu)

        listPlayerInMap = new ArrayList<>();
        listPlayerViewer = new ArrayList<>();
        usedChars = new ArrayList<>();
        allCharPlayerAttack = false;
        isOpened = false;
        waitChangeRound = false;
        accumulationPhase = true;
        round16 = false;
        round8 = false;
        round4 = false;
        round2 = false;
        sendTime = false;
        listGroupDaiHoi = new ArrayList<>();
        listPlayerTarget = new ArrayList<>();
        listPlayerBXH = new ArrayList<>();
        canRespawnHS = true;
        numRound = 0;

        pendingFightStart = false;
        roundDelayUntilMs = 0L;
        testMode = false;

        Map map = new Map(49);
        for (int i = 0; i < 21; i++) {
            ZoneDaihoi z = new ZoneDaihoi(map, i);
            z.setWorld(this);
            map.getZones().add(z);
            zones.add(z);
        }
        initFinished = true;
    }

    private static int todayDow() {
        return ZonedDateTime.now(ZONE_VN).getDayOfWeek().getValue();
    }

    private static boolean isEventOpenToday() {
        return true;
    }

    private static String todayText() {
        return "Hôm nay mở Đại Hội Nhẫn Giả ";
    }

    private static boolean allowLevelToday(int lv) {
        return lv >= 45 && lv <= 59;
    }

    public void playerJoin(boolean viewer, Char player) {
        boolean isAdmin = player != null && player.user != null && player.user.isAdmin;
        if (!isAdmin && !isOpened) {
            player.service.serverMessage("Chưa đến giờ hoạt động Đại Hội Nhẫn Giả.");
            return;
        }

        if (!isAdmin && !testMode && (!isEventOpenToday() || !allowLevelToday(player.level()))) {
            player.service.serverMessage(todayText());
            return;
        }

        if (zones != null) {
            for (Zone zone : zones) {
                if (zone.players.size() < zone.MAX_CHAR_INZONE) {
                    zone.addChar(player);
                    break;
                }
            }
        }

        if (isOpened) {
            player.service.sendTimeInMap(getCountDown(), true, timeStart);
        }

        lock.writeLock().lock();
        try {
            if (!viewer) {
                listPlayerInMap.add(player);
                player.setXY((short) 458, (short) 450);
                player.service.setXYChar();
                rewardParticipation(player);
            } else {
                listPlayerViewer.add(player);
                player.setXY((short) (500 + Utlis.nextInt(-200, 200)), (short) 566);
                player.service.setXYChar();
                setTypePkPlayer(player, false);
            }
        } finally {
            lock.writeLock().unlock();
        }

        if (!isOpened) {
            addMember(player);
        }
    }

    public void playerOut(Char player) {
        try {
            lock.writeLock().lock();
            try {
                listPlayerInMap.remove(player);
                listPlayerViewer.remove(player);
            } finally {
                lock.writeLock().unlock();
            }
            setTypePkPlayer(player, false);
            removeMember(player);
            player.removeWorld(World.DAI_HOI_VO_THUAT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        try {
            for (Zone zone : zones) {
                zone.update();

                long now = System.currentTimeMillis();
                for (Char player : listPlayerInMap) {
                    if (player != null && player.InfoGame != null && player.InfoGame.isDie) {
                        long respawnAt = deathRespawnAt.getOrDefault(player.id, 0L);
                        if (respawnAt == 0L) {
                            deathRespawnAt.put(player.id, now + 5000L); // Thời gian HỒI SINH sau khi chết trong map đấu (người chơi)
                        } else if (now >= respawnAt) {
                            player.reSpawnHS();
                            player.setXY(player.Info.cx, (short) 450);
                            player.service.setXYChar();
                            deathRespawnAt.remove(player.id);
                        }
                    } else if (player != null) {
                        deathRespawnAt.remove(player.id);
                    }
                }

                for (Char viewer : listPlayerViewer) {
                    if (!isFighting(viewer)) {
                        setTypePkPlayer(viewer, false);
                    }
                }

                for (Char viewer : listPlayerViewer) {
                    if (viewer != null && viewer.InfoGame != null && viewer.InfoGame.isDie) {
                        long respawnAt = deathRespawnAt.getOrDefault(viewer.id, 0L);
                        if (respawnAt == 0L) {
                            deathRespawnAt.put(viewer.id, now + 5000L); // Thời gian HỒI SINH sau khi chết trong map đấu (khán giả)
                        } else if (now >= respawnAt) {
                            viewer.reSpawnHS();
                            viewer.setXY(viewer.Info.cx, viewer.Info.cy);
                            viewer.service.setXYChar();
                            deathRespawnAt.remove(viewer.id);
                        }
                    } else if (viewer != null) {
                        deathRespawnAt.remove(viewer.id);
                    }
                }

                if (!isOpened) {
                    for (Char player : listPlayerInMap) {
                        setTypePkPlayer(player, false);
                    }
                }
            }

            if (pendingFightStart && System.currentTimeMillis() >= roundDelayUntilMs) {
                if (listPlayerTarget != null) {
                    for (dataDaiHoi data : listPlayerTarget) {
                        Char p1 = ServerManager.findCharByName(data.namePl1);
                        Char p2 = ServerManager.findCharByName(data.namePl2);
                        setTypePkPlayer(p1, true);
                        setTypePkPlayer(p2, true);
                    }
                }
                timeStartRound = System.currentTimeMillis();
                pendingFightStart = false;
            }

            checkPlayerWin();

            if (countDown > 0) {
                countDown--;
                if (sendTime) {
                    for (Char player : listPlayerInMap) {
                        player.service.sendTimeInMap(getCountDown(), true);
                        sendTime = false;
                    }
                }
                if (countDown == 0) {
                    if (!sendTime) {
                        sendTime = true;
                    }

                    if (!isOpened) {
                        isOpened = true;
                        accumulationPhase = true;
                        timeStart = System.currentTimeMillis();
                        for (Char player : listPlayerInMap) {
                            if (player != null && player.user != null && !player.isClean) {
                                setTypePkPlayer(player, true);
                            }
                        }
                        setCountdown(1 * 60); // [TEST] Thời gian giai đoạn TÍCH LŨY (đấu tự do trước vòng loại) - hiện 1p
                    } else if (accumulationPhase) {
                        accumulationPhase = false;
                        prepareInitialKnockoutRound();
                    } else if (round16) {
                        startNextKnockoutRound(8);
                    } else if (round8) {
                        startNextKnockoutRound(4);
                    } else if (round4) {
                        startNextKnockoutRound(2);
                    } else if (round2) {
                        numRound = 2;
                        resetListDaiHoi = true;
                        round2 = false;
                        timeStart = System.currentTimeMillis();
                        listPlayerBXH.clear();
                        backPlayerZone0();
                        restoreAllBetweenRounds();
                        listPlayerTarget.clear();
                        setUpRoundWithPkOff();
                        pendingFightStart = true;
                        roundDelayUntilMs = System.currentTimeMillis() + 10_000L; // [TEST] Thời gian CHỜ TRƯỚC TRẬN loại 2 (chuẩn bị + hồi HP trước khi bật PK) - hiện 10s
                        setCountdown(1 * 60); // [TEST] Countdown giữa VÒNG LOẠI TRỰC TIẾP 2 và CHUNG KẾT - hiện 1p
                    } else {
                        restoreAllBetweenRounds(); // Hồi full HP/MP toàn bộ người chơi trước khi đóng sự kiện
                        close(); // Đóng sự kiện Đại Hội Nhẫn Giả
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareInitialKnockoutRound() {
        buildBracketFromCurrentPlayers();
        numRound = currentRoundSize();
        resetListDaiHoi = true;
        round16 = numRound == 16;
        round8 = numRound == 8;
        round4 = numRound == 4;
        round2 = numRound == 2;
        if (numRound <= 2) {
            round2 = true;
        }
        canRespawnHS = true;
        timeStart = System.currentTimeMillis();
        backPlayerZone0();
        listPlayerTarget.clear();
        setUpRoundWithPkOff();
        pendingFightStart = true;
        roundDelayUntilMs = System.currentTimeMillis() + 10_000L; // [TEST] Thời gian CHỜ TRƯỚC VÒNG LOẠI ĐẦU TIÊN (16/8/4/2 tuỳ số người) - chuẩn bị + hồi HP trước khi bật PK - hiện 10s
        setCountdown(1 * 60); // [TEST] Countdown trước khi bắt đầu VÒNG LOẠI ĐẦU TIÊN - hiện 1p
        accumulationPhase = false;
    }

    private void startNextKnockoutRound(int nextSize) {
        numRound = nextSize;
        resetListDaiHoi = true;
        round16 = false;
        round8 = false;
        round4 = false;
        round2 = false;
        if (nextSize == 8) round8 = true;
        if (nextSize == 4) round4 = true;
        if (nextSize == 2) round2 = true;
        timeStart = System.currentTimeMillis();
        listPlayerBXH.clear();
        backPlayerZone0();
        restoreAllBetweenRounds();
        listPlayerTarget.clear();
        setUpRoundWithPkOff();
        pendingFightStart = true;
        roundDelayUntilMs = System.currentTimeMillis() + 10_000L; // [TEST] Thời gian CHỜ TRƯỚC MỖI VÒNG LOẠI TIẾP THEO (8→4→2) - chuẩn bị + hồi HP trước khi bật PK - hiện 10s
        setCountdown(1 * 60); // [TEST] Countdown trước khi bắt đầu mỗi VÒNG LOẠI TIẾP THEO (8→4→2) - hiện 1p
    }

    private int currentRoundSize() {
        int count = Math.max(2, listPlayerInMap.size());
        if (count >= 16) return 16;
        if (count >= 8) return 8;
        if (count >= 4) return 4;
        return 2;
    }

    private void buildBracketFromCurrentPlayers() {
        listGroupDaiHoi.clear();
        listGroupDaiHoi.addAll(new ArrayList<>(listPlayerInMap));
        listGroupDaiHoi.sort(Comparator.comparingInt(Char::getPointDaiHoi).reversed());
        listGroupDaiHoi = listGroupDaiHoi.stream().limit(currentRoundSize()).collect(Collectors.toList());
    }

    public void backPlayerZone0() {
        for (Char player : listPlayerInMap) {
            Zone zone = zones.get(0);
            zone.addChar(player);
            player.setXY((short) (500 + Utlis.nextInt(-200, 200)), (short) 566);
            player.service.setXYChar();
            setTypePkPlayer(player, false);
        }
    }

    public void setZoneFight() {
        if (listPlayerTarget == null || listPlayerTarget.isEmpty() || zones == null || zones.isEmpty()) {
            return;
        }
        int pairCount = Math.min(listPlayerTarget.size(), zones.size());
        for (int i = 0; i < pairCount; i++) {
            dataDaiHoi data = listPlayerTarget.get(i);
            if (data == null) {
                continue;
            }
            Char pl = ServerManager.findCharByName(data.namePl1);
            Char plAtt = ServerManager.findCharByName(data.namePl2);
            if (pl == null || plAtt == null) {
                continue;
            }

            Zone zone = zones.get(i);
            if (zone == null) {
                continue;
            }

            listPlayerViewer.remove(pl);
            listPlayerViewer.remove(plAtt);

            if (pl.zone != zone) {
                zone.addChar(pl);
            }
            if (plAtt.zone != zone) {
                zone.addChar(plAtt);
            }

            pl.setXY((short) 359, (short) 245);
            pl.service.setXYChar();
            plAtt.setXY((short) 710, (short) 245);
            plAtt.service.setXYChar();

            pl.buaBaoHo = false;
            plAtt.buaBaoHo = false;

            setTypePkPlayer(pl, false);
            setTypePkPlayer(plAtt, false);
        }
    }

    private void setUpRoundWithPkOff() {
        usedChars.clear();
        int matches = Math.min(numRound / 2, zones.size());
        for (int i = 0; i < matches; i++) {
            if (i > listGroupDaiHoi.size() - 1) {
                return;
            }
            int j = listGroupDaiHoi.size() - 1 - i;
            if (j < 0) {
                return;
            }

            Char pl = listGroupDaiHoi.get(i);
            Char plAtt = listGroupDaiHoi.get(j);

            Zone zone = zones.get(i);
            if (zone == null) {
                continue;
            }

            listPlayerViewer.remove(pl);
            listPlayerViewer.remove(plAtt);

            zone.addChar(pl);
            zone.addChar(plAtt);

            pl.setXY((short) 359, (short) 245);
            pl.service.setXYChar();
            plAtt.setXY((short) 710, (short) 245);
            plAtt.service.setXYChar();

            pl.buaBaoHo = false;
            plAtt.buaBaoHo = false;

            setTypePkPlayer(pl, false);
            setTypePkPlayer(plAtt, false);

            addChartoList(pl, plAtt);
        }
    }

    private boolean isKnockoutRoundForWhiteFlag() {
        return numRound == 16 || numRound == 8 || numRound == 4 || numRound == 2;
    }

    public boolean isFighting(Char player) {
        if (player == null || listPlayerTarget == null) {
            return false;
        }
        for (dataDaiHoi data : listPlayerTarget) {
            if (data.typeWin == 0 && (player.Info.name.equals(data.namePl1) || player.Info.name.equals(data.namePl2))) {
                return true;
            }
        }
        return false;
    }

    public void checkPlayerWin() {
        if (pendingFightStart || listPlayerTarget == null || listPlayerTarget.isEmpty()) {
            return;
        }

        long now = System.currentTimeMillis();
        long matchDurationMs = 1L * 60_000L; // [TEST] Thời gian MỖI TRẬN ĐẤU VÒNG LOẠI - hết giờ thì random thắng thua 50/50 - hiện 1p

        Iterator<dataDaiHoi> it = listPlayerTarget.iterator();
        while (it.hasNext()) {
            dataDaiHoi data = it.next();
            if (data.typeWin != 0) {
                continue;
            }

            Char pl1 = ServerManager.findCharByName(data.namePl1);
            Char pl2 = ServerManager.findCharByName(data.namePl2);

            boolean p1Lose = pl1 == null || pl1.zone == null || !pl1.zone.isDaiHoiVoThuat() || pl1.InfoGame.isDie;
            boolean p2Lose = pl2 == null || pl2.zone == null || !pl2.zone.isDaiHoiVoThuat() || pl2.InfoGame.isDie;

            if (p1Lose ^ p2Lose) {
                if (p1Lose) {
                    handleWin(data, pl2, pl1, it);
                } else {
                    handleWin(data, pl1, pl2, it);
                }
                continue;
            }

            if (now - timeStartRound >= matchDurationMs) {
                if (Utlis.isTrue(50, 100)) {
                    handleWin(data, pl1, pl2, it);
                } else {
                    handleWin(data, pl2, pl1, it);
                }
            }
        }
    }

    private void handleWin(dataDaiHoi data, Char winner, Char loser, Iterator<dataDaiHoi> it) {
        try {
            if (winner != null) {
                listGroupDaiHoi.add(winner);
                setTypePkPlayer(winner, false);
                rewardRoundEntry(data.roundOf, winner);
            }
            if (loser != null && isKnockoutRoundForWhiteFlag()) {
                setTypePkPlayer(loser, false);
                listPlayerViewer.add(loser);
            }

            if (winner != null && data.namePl1.equals(winner.Info.name)) {
                data.typeWin = 1;
            } else {
                data.typeWin = 2;
            }

            it.remove();
            sendNotifyPlayerWin(winner, data.roundOf);

            if (data.roundOf == 2) {
                rewardChampion(winner);
                announceChampionAndClose(winner);
            }
        } catch (Exception ignored) {
        }
    }

    private void announceChampionAndClose(Char champion) {
        try {
            if (champion != null) {
                Main.HeThongCTG(champion.Info.name + " la Nha Vo Dich Dai Hoi!", 2);
            } else {
                Main.HeThongCTG("Dai Hoi da ket thuc.", 2);
            }
        } catch (Exception ignored) {
        }

        HashSet<Char> all = new HashSet<>();
        all.addAll(listPlayerInMap);
        all.addAll(listPlayerViewer);
        for (Char player : all) {
            try {
                setTypePkPlayer(player, false);
            } catch (Exception ignored) {
            }
        }

        List<Char> inMapCopy = new ArrayList<>(listPlayerInMap);
        List<Char> viewerCopy = new ArrayList<>(listPlayerViewer);
        rewardNonChampionParticipants(inMapCopy);
        for (Char player : inMapCopy) {
            try {
                playerOut(player);
            } catch (Exception ignored) {
            }
        }
        for (Char player : viewerCopy) {
            try {
                playerOut(player);
            } catch (Exception ignored) {
            }
        }

        try {
            close();
        } catch (Exception ignored) {
        }
    }

    public void sendNotifyPlayerWin(Char player, int roundOf) {
        if (player == null) {
            return;
        }
        Main.HeThongCTG(
                player.Info.name + " da gianh chien thang o vong " + roundOf + " tai khu vuc "
                        + (player.zone != null ? player.zone.zoneID : -1),
                2
        );
    }

    public void addChartoList(Char player, Char playerTarget) {
        dataDaiHoi data = new dataDaiHoi();
        data.namePl1 = player.Info.name;
        data.namePl2 = player.Info.name.equals(playerTarget.Info.name) ? "???" : playerTarget.Info.name;
        data.typeWin = 0;
        data.roundOf = numRound;
        listPlayerTarget.add(data);
        listPlayerBXH.add(data);
    }

    public void setTypePkPlayer(Char player, boolean isAttack) {
        if (player == null) {
            return;
        }
        byte typePk = (byte) (isAttack ? 2 : 0);
        player.InfoGame.TypePk = typePk;
        if (player.service != null) {
            player.service.sendMessage(HanderMessage.SendTypePk(player.id, typePk));
        }
        if (player.zone != null) {
            player.zone.SendMessageInZone(HanderMessage.SendTypePk(player.id, typePk));
        }
    }

    public void showTopDaiHoi(Char myChar) {
        Message msg = new Message((byte) -32);
        try {
            msg.writeInt(getCountDown());
            msg.writeUTF("Vong " + numRound);
            msg.writeByte(listPlayerBXH.size());
            for (dataDaiHoi player : listPlayerBXH) {
                msg.writeUTF(player.namePl1);
                msg.writeUTF(player.namePl2);
                msg.writeByte(player.typeWin);
            }
            myChar.user.session.sendMessage(msg);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void changeZoneDaiHoi(Char myChar, byte zoneNext) {
        Zone zone = zones.get(zoneNext);
        zone.addChar(myChar);
    }

    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return false;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return false;
    }

    private void restoreFullHpMp(Char player) {
        if (player == null) {
            return;
        }
        try {
            player.Point.hp = player.maxHP;
            player.Point.mp = player.maxMP;
            player.InfoGame.isDie = false;
            player.writeInfo();
        } catch (Exception ignored) {
        }
    }

    public boolean isDeadInsideEvent(Char player) {
        return player != null && player.InfoGame != null && player.InfoGame.isDie;
    }

    private void restoreAllBetweenRounds() {
        HashSet<Char> all = new HashSet<>();
        all.addAll(listPlayerInMap);
        all.addAll(listPlayerViewer);
        for (Char player : all) {
            restoreFullHpMp(player);
        }
    }

    private void rewardParticipation(Char player) {
        if (player == null) {
            return;
        }
        String key = player.Info.name;
        if (!rewardedJoin.add(key)) {
            return;
        }
        player.Info.chuyenCan += 5;
        player.Info.chuyenCanTuan += 5;
        try {
            player.writeInfo();
        } catch (Exception ignored) {
        }
        try {
            player.service.serverMessage("Đã báo danh Đại Hội Nhẫn Giả. +5 chuyên cần");
        } catch (Exception ignored) {
        }
    }

    private void rewardRoundEntry(int roundOf, Char player) {
        if (player == null) {
            return;
        }
        String key = player.Info.name;
        if (roundOf == 16) {
            if (rewarded16.add(key)) {
                giveMyo(player, 3);
                try {
                    player.service.serverMessage("Bạn vào top 16. Nhận 3 item.");
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void rewardChampion(Char player) {
        if (player == null) {
            return;
        }
        String key = player.Info.name;
        if (!rewardedWin.add(key)) {
            return;
        }
        giveBacKhoa(player, 1_000_000_000L);
        giveVang(player, 1_000);
        giveMyo(player, 4);
        try {
            player.service.serverMessage("Thuong vo dich: +1000m bac khoa, +1000 vang, +4 item.");
        } catch (Exception ignored) {
        }
        rewardNonChampionParticipants(new ArrayList<>(listPlayerBXH.stream().map(d -> ServerManager.findCharByName(d.namePl1)).toList()));
    }

    private void rewardNonChampionParticipants(List<Char> participants) {
        for (Char player : participants) {
            if (player == null) continue;
            if (rewardedWin.contains(player.Info.name)) continue;
            if (rewarded16.contains(player.Info.name)) continue;
            giveMyo(player, 2);
            try {
                player.service.serverMessage("Bạn nhận phần thưởng tham gia: 2 item.");
            } catch (Exception ignored) {
            }
        }
    }

    private void giveBacKhoa(Char player, long amount) {
        try {
            player.addBacKhoa(amount);
        } catch (Exception ignored) {
        }
    }

    private void giveVang(Char player, int amount) {
        try {
            player.addVang(amount);
        } catch (Exception ignored) {
        }
    }

    private void giveMyo(Char player, int amount) {
        try {
            Item myo = new Item(353);
            myo.amount = amount;
            player.addItem(myo);
            player.msgAddItemBag(myo);
        } catch (Exception ignored) {
        }
    }
}
