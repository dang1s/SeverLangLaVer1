package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.ZoneDaihoi;
import Service.HanderMessage;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;

import java.util.*;
import java.util.stream.Collectors;

import MapService.world.data.dataDaiHoi;
import com.sg188.server.Main;
import com.sg188.server.ServerManager;
import com.sg188.server.lib.Message;

public class DaiHoiNhanGia4x extends World {
    public static DaiHoiNhanGia4x DAI_HOI_NHAN_GIA;
    public List<Char> listPlayerInMap;
    public List<Char> listPlayerViewer;

    public long timeStart;
    public boolean isOpened;
    public boolean allCharPlayerAttack;
    public boolean round16;
    public boolean round8;
    public boolean round4;
    public boolean round2; // CHUNG KẾT
    public long timeStartRound;
    public List<Char> listGroupDaiHoi;

    public List<dataDaiHoi> listPlayerTarget;
    public List<dataDaiHoi> listPlayerBXH;
    public boolean canRespawnHS;
    public boolean resetListDaiHoi;
    public boolean sendTime;
    public boolean waitChangeRound;
    List<Char> usedChars;

    public int numRound;

    // Delay 10s SAU KHI GHÉP CẶP (PK bật sau 10s)
    private boolean pendingFightStart;
    private long roundDelayUntilMs;

    public DaiHoiNhanGia4x() {
        setType(World.DAI_HOI_VO_THUAT);
        this.name = "DaiHoiNhanGia";
        this.countDown = 2 * 60; // default = 10 phút
        listPlayerInMap = new ArrayList<>();
        listPlayerViewer = new ArrayList<>();
        usedChars = new ArrayList<>();
        allCharPlayerAttack = false;
        isOpened = false;
        waitChangeRound = false;
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

        Map map = new Map(49);
        for (int i = 0; i < 21; i++) {
            ZoneDaihoi z = new ZoneDaihoi(map, i);
            z.setWorld(this);
            map.getZones().add(z);
            zones.add(z);
        }
        initFinished = true;
    }

    public void playerJoin(boolean viewer, Char player) {
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
            }
            if (viewer) {
                this.listPlayerViewer.add(player);
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
            if (!isClosed) { }
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
                for (Char player : listPlayerInMap) {
                    if (player.InfoGame.isDie && canRespawnHS) {
                        player.reSpawnHS();
                        player.setXY(player.Info.cx, (short) 450);
                        player.service.setXYChar();
                    }
                }
                // Viewer: chỉ set cờ trắng nếu KHÔNG thuộc cặp đang đấu
                for (Char plChar : listPlayerViewer) {
                    if (!isFighting(plChar)) {
                        setTypePkPlayer(plChar, false);
                    }
                }
                for (Char player : listPlayerViewer) {
                    if (player.InfoGame.isDie && canRespawnHS) {
                        player.reSpawnHS();
                        player.setXY(player.Info.cx, player.Info.cy);
                        player.service.setXYChar();
                    }
                }
                if (!isOpened) {
                    for (Char plChar : listPlayerInMap) {
                        setTypePkPlayer(plChar, false);
                    }
                }
            }

            // ĐỦ 10s SAU GHÉP CẶP -> bật PK cho tất cả cặp + bắt đầu giờ trận
            if (pendingFightStart && System.currentTimeMillis() >= roundDelayUntilMs) {
                if (listPlayerTarget != null) {
                    for (dataDaiHoi d : listPlayerTarget) {
                        Char p1 = ServerManager.findCharByName(d.namePl1);
                        Char p2 = ServerManager.findCharByName(d.namePl2);
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
                    for (Char plChar : listPlayerInMap) {
                        plChar.service.sendTimeInMap(getCountDown(), true);
                        sendTime = false;
                    }
                }
                if (countDown == 0) {
                    if (!sendTime) sendTime = true;

                    if (!isOpened) {
                        isOpened = true;
                        waitChangeRound = true;
                        timeStart = System.currentTimeMillis();
                        for (Char pl : listPlayerInMap) {
                            if (pl != null && pl.user != null && !pl.isClean) {
                                setTypePkPlayer(pl, true);
                            }
                        }
                        setCountdown(1 * 60);

                    } else if (waitChangeRound) {
                        waitChangeRound = false;
                        round16 = true;
                        setCountdown(1 * 60);

                    } else if (round16) {
                        // Bắt đầu vòng 16
                        numRound = 16;
                        resetListDaiHoi = true;
                        canRespawnHS = false;
                        round16 = false;
                        round8 = true;

                        Set<Char> uniqueChars = new HashSet<>(listPlayerInMap);
                        listGroupDaiHoi = uniqueChars.stream()
                                .sorted(Comparator.comparingInt(Char::getPointDaiHoi).reversed())
                                .limit(16).collect(Collectors.toList());

                        timeStart = System.currentTimeMillis();
                        backPlayerZone0();
                        if (listPlayerTarget != null) listPlayerTarget.clear();

                        setUpRoundWithPkOff();
                        pendingFightStart = true;
                        roundDelayUntilMs = System.currentTimeMillis() + 10_000L;

                        setCountdown(1 * 60);

                    } else if (round8) {
                        // Bắt đầu vòng 8
                        numRound = 8;
                        resetListDaiHoi = true;
                        round8 = false;
                        round4 = true;

                        timeStart = System.currentTimeMillis();
                        if (listPlayerBXH != null) listPlayerBXH.clear();
                        backPlayerZone0();

                        // HỒI FULL HP/MP SAU VÒNG 16
                        restoreAllBetweenRounds();

                        if (listPlayerTarget != null) listPlayerTarget.clear();

                        setUpRoundWithPkOff();
                        pendingFightStart = true;
                        roundDelayUntilMs = System.currentTimeMillis() + 10_000L;

                        setCountdown(1 * 60);

                    } else if (round4) {
                        // Bán kết (4)
                        numRound = 4;
                        resetListDaiHoi = true;
                        round4 = false;
                        round2 = true;

                        timeStart = System.currentTimeMillis();
                        if (listPlayerBXH != null) listPlayerBXH.clear();
                        backPlayerZone0();

                        // HỒI FULL HP/MP SAU VÒNG 8
                        restoreAllBetweenRounds();

                        if (listPlayerTarget != null) listPlayerTarget.clear();

                        setUpRoundWithPkOff();
                        pendingFightStart = true;
                        roundDelayUntilMs = System.currentTimeMillis() + 10_000L;

                        setCountdown(1 * 60);

                    } else if (round2) {
                        // CHUNG KẾT = vòng 2
                        numRound = 2;
                        resetListDaiHoi = true;
                        round2 = false;

                        timeStart = System.currentTimeMillis();
                        if (listPlayerBXH != null) listPlayerBXH.clear();
                        backPlayerZone0();

                        // HỒI FULL HP/MP SAU VÒNG 4
                        restoreAllBetweenRounds();

                        if (listPlayerTarget != null) listPlayerTarget.clear();

                        setUpRoundWithPkOff();
                        pendingFightStart = true;
                        roundDelayUntilMs = System.currentTimeMillis() + 10_000L;

                        setCountdown(1 * 60);

                    } else {
                        // Kết thúc giải
                        restoreAllBetweenRounds();
                        close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    // Dự phòng: tổ chức từ listPlayerTarget (PK sẽ bật sau 10s trong update)
    public void setZoneFight() {
        if (listPlayerTarget == null || listPlayerTarget.isEmpty() || zones == null || zones.isEmpty()) return;
        int pairCount = Math.min(listPlayerTarget.size(), zones.size());
        for (int i = 0; i < pairCount; i++) {
            dataDaiHoi d = listPlayerTarget.get(i);
            if (d == null) continue;
            Char pl    = ServerManager.findCharByName(d.namePl1);
            Char plAtt = ServerManager.findCharByName(d.namePl2);
            if (pl == null || plAtt == null) continue;

            Zone zone = zones.get(i);
            if (zone == null) continue;

            // gỡ khỏi viewer để không bị set cờ trắng mỗi tick
            listPlayerViewer.remove(pl);
            listPlayerViewer.remove(plAtt);

            if (pl.zone != zone) zone.addChar(pl);
            if (plAtt.zone != zone) zone.addChar(plAtt);

            // tọa độ
            pl.setXY((short)359, (short)245);    pl.service.setXYChar();
            plAtt.setXY((short)710, (short)245); plAtt.service.setXYChar();

            // TẮT bùa bảo hộ trước khi cho đánh
            pl.buaBaoHo = false;
            plAtt.buaBaoHo = false;

            // PK OFF, sẽ bật sau 10s
            setTypePkPlayer(pl, false);
            setTypePkPlayer(plAtt, false);
        }
    }

    // GHÉP CẶP + đưa vào zone + PK OFF (đợi 10s mới bật PK)
    private void setUpRoundWithPkOff() {
        usedChars.clear();
        int matches = Math.min(numRound / 2, zones.size());
        for (int i = 0; i < matches; i++) {
            if (i > listGroupDaiHoi.size() - 1) return;
            int j = listGroupDaiHoi.size() - 1 - i;
            if (j < 0) return;

            Char pl = listGroupDaiHoi.get(i);
            Char plAtt = listGroupDaiHoi.get(j);

            Zone zone = zones.get(i);
            if (zone == null) continue;

            // gỡ khỏi viewer
            listPlayerViewer.remove(pl);
            listPlayerViewer.remove(plAtt);

            zone.addChar(pl);
            zone.addChar(plAtt);

            // tọa độ
            pl.setXY((short)359, (short)245);
            pl.service.setXYChar();
            plAtt.setXY((short)710, (short)245);
            plAtt.service.setXYChar();

            // TẮT bùa bảo hộ trước khi cho đánh
            pl.buaBaoHo = false;
            plAtt.buaBaoHo = false;

            // PK OFF, bật sau 10s
            setTypePkPlayer(pl, false);
            setTypePkPlayer(plAtt, false);

            addChartoList(pl, plAtt);
        }
    }

    // ép cờ trắng cho kẻ thua ở các vòng 16/8/4/2
    private boolean isKnockoutRoundForWhiteFlag() {
        return numRound == 16 || numRound == 8 || numRound == 4 || numRound == 2;
    }

    // player đang ở trong một cặp đấu chưa kết thúc?
    public boolean isFighting(Char p) {
        if (p == null || listPlayerTarget == null) return false;
        for (dataDaiHoi d : listPlayerTarget) {
            if (d.typeWin == 0 && (p.Info.name.equals(d.namePl1) || p.Info.name.equals(d.namePl2))) {
                return true;
            }
        }
        return false;
    }

    public void checkPlayerWin() {
        // Đang chờ 10s sau GHÉP CẶP -> chưa tính kết quả
        if (pendingFightStart) return;

        if (listPlayerTarget == null || listPlayerTarget.isEmpty()) return;

        long now = System.currentTimeMillis();
        long matchDurationMs = (numRound == 16 ? 3L * 60_000L : 2L * 60_000L);

        // LUÔN kiểm tra thắng thua ngay lập tức nếu có người chết/thoát (không đợi hết giờ)
        Iterator<dataDaiHoi> it = listPlayerTarget.iterator();
        while (it.hasNext()) {
            dataDaiHoi data = it.next();
            if (data.typeWin != 0) continue;

            Char pl1 = ServerManager.findCharByName(data.namePl1);
            Char pl2 = ServerManager.findCharByName(data.namePl2);

            // Ai rớt/thoát/không đúng bản đồ hoặc chết -> thua ngay
            boolean p1Lose = (pl1 == null || pl1.zone == null || !pl1.zone.isDaiHoiVoThuat() || pl1.InfoGame.isDie);
            boolean p2Lose = (pl2 == null || pl2.zone == null || !pl2.zone.isDaiHoiVoThuat() || pl2.InfoGame.isDie);

            if (p1Lose ^ p2Lose) {
                if (p1Lose) handleWin(data, pl2, pl1, it); // pl2 win
                else        handleWin(data, pl1, pl2, it); // pl1 win
                continue;
            }

            // Nếu chưa phân thắng bại và đã quá giờ -> chọn ngẫu nhiên (giữ nguyên luật cũ)
            if (now - timeStartRound >= matchDurationMs) {
                if (Utlis.isTrue(50, 100)) handleWin(data, pl1, pl2, it);
                else                       handleWin(data, pl2, pl1, it);
            }
        }
    }

    // Xử lý kết quả 1 cặp: winner thắng, loser thua; vòng 2 thì kết thúc giải ngay
    private void handleWin(dataDaiHoi data, Char winner, Char loser, Iterator<dataDaiHoi> it) {
        try {
            if (winner != null) {
                listGroupDaiHoi.add(winner);
                setTypePkPlayer(winner, false); // ngừng PK khi đã thắng cặp
            }
            if (loser != null && isKnockoutRoundForWhiteFlag()) {
                setTypePkPlayer(loser, false); // thua -> cờ trắng
                listPlayerViewer.add(loser);
            }

            // typeWin
            if (winner != null && data.namePl1.equals(winner.Info.name)) data.typeWin = 1;
            else data.typeWin = 2;

            // bỏ cặp khỏi danh sách giao tranh
            it.remove();

            // Thông báo thắng cặp
            sendNotifyPlayerWin(winner, data.roundOf);

            // Nếu là CHUNG KẾT (roundOf == 2) -> VÔ ĐỊCH và đóng giải ngay
            if (data.roundOf == 2) {
                announceChampionAndClose(winner);
            }
        } catch (Exception ignored) { }
    }

    // Thông báo vô địch + đá toàn bộ người chơi + close()
    private void announceChampionAndClose(Char champion) {
        try {
            if (champion != null) {
                Main.HeThongCTG(champion.Info.name + " là Nhà Vô Địch Đại Hội!", 2);
            } else {
                Main.HeThongCTG("Đại Hội đã kết thúc.", 2);
            }
        } catch (Exception ignored) { }

        // dọn PK và đá tất cả người chơi khỏi map Đại Hội
        HashSet<Char> all = new HashSet<>();
        all.addAll(listPlayerInMap);
        all.addAll(listPlayerViewer);
        for (Char p : all) {
            try {
                setTypePkPlayer(p, false);
            } catch (Exception ignored) { }
        }
        // copy để tránh ConcurrentModification
        List<Char> copy1 = new ArrayList<>(listPlayerInMap);
        List<Char> copy2 = new ArrayList<>(listPlayerViewer);
        for (Char p : copy1) {
            try { playerOut(p); } catch (Exception ignored) { }
        }
        for (Char p : copy2) {
            try { playerOut(p); } catch (Exception ignored) { }
        }

        // đóng world
        try { close(); } catch (Exception ignored) { }
    }

    // Thông báo theo "vòng của cặp" (roundOf)
    public void sendNotifyPlayerWin(Char player, int roundOf) {
        if (player == null) return;
        Main.HeThongCTG(player.Info.name + " đã giành chiến thắng ở vòng " + roundOf
                + " tại khu vực " + (player.zone != null ? player.zone.zoneID : -1), 2);
    }

    public void addChartoList(Char player, Char playerTarget) {
        dataDaiHoi data = new dataDaiHoi();
        if (player.Info.name.equals(playerTarget.Info.name)) {
            data.namePl1 = player.Info.name;
            data.namePl2 = "???";
        } else {
            data.namePl1 = player.Info.name;
            data.namePl2 = playerTarget.Info.name;
        }
        data.typeWin = 0;
        data.roundOf = numRound; // 16, 8, 4, 2
        listPlayerTarget.add(data);
        listPlayerBXH.add(data);
    }

    public void setTypePkPlayer(Char player, boolean isAttack) {
        if (player == null) return;
        byte typePk = (byte) (isAttack ? 2 : 0);
        player.InfoGame.TypePk = typePk;
        if (player.service != null) {
            player.service.sendMessage(HanderMessage.SendTypePk(player.id, typePk));
        }
        if (player.zone != null) {
            player.zone.SendMessageInZone(HanderMessage.SendTypePk(player.id, typePk));
        }
    }

    public void showTopDaiHoi(Char _myChar) {
        Message msg = new Message((byte) -32);
        try {
            msg.writeInt(getCountDown());
            msg.writeUTF("Vòng " + numRound);
            msg.writeByte(listPlayerBXH.size());
            for (dataDaiHoi player : listPlayerBXH) {
                msg.writeUTF(player.namePl1);
                msg.writeUTF(player.namePl2);
                msg.writeByte(player.typeWin);
            }
            _myChar.user.session.sendMessage(msg);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void changeZoneDaiHoi(Char _myChar, byte zoneNext) {
        Zone zone = zones.get(zoneNext);
        zone.addChar(_myChar);
    }

    @Override public boolean enterWorld(Zone pre, Zone next) { return false; }
    @Override public boolean leaveWorld(Zone pre, Zone next) { return false; }

    // ============ HỒI FULL HP/MP SAU MỖI VÒNG ============
    private void restoreFullHpMp(Char p) {
        if (p == null) return;
        try {
            p.Point.hp = p.maxHP;
            p.Point.mp = p.maxMP;
            p.InfoGame.isDie = false;
            p.writeInfo();
        } catch (Exception ignored) { }
    }

    private void restoreAllBetweenRounds() {
        HashSet<Char> all = new HashSet<>();
        all.addAll(listPlayerInMap);
        all.addAll(listPlayerViewer);
        for (Char c : all) restoreFullHpMp(c);
    }
}
