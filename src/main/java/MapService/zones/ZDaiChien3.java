package MapService.zones;

import MapService.Map;
import MapService.world.DaiChienNhanGia3;
import MapService.world.World;
import Service.HanderMessage;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Mob;
import com.sg188.real.WayPoint;
import org.jetbrains.annotations.NotNull;

public class ZDaiChien3 extends ZWorld {
    private static final int[][] LONG_TRU_GUARD_GROUPS = {
            {0, 2, 3, 4},
            {1, 6, 7, 8, 9},
            {10, 13, 14, 15},
            {11, 16, 17, 18, 19}
    };

    private final int level;

    public ZDaiChien3(Map map, int id, int level, World world) {
        super(map, id);
        this.level = level;
        this.timeReviveMob = 15000;
        setWorld(world);
    }

    @Override
    public boolean addChar(@NotNull Char player) {
        super.addChar(player);
        DaiChienNhanGia3 event = (DaiChienNhanGia3) world;
        player.service.sendTimeInMap(world.getCountDown(), !event.isBattleStarted());
        if (event.isBattleStarted()) {
            player.service.sendMessage(HanderMessage.SendThongBao("Chiến trường nhẫn giả đã mở!", HanderMessage.WHITE));
        }
        return true;
    }

    @Override
    public void attackMob(Char player, int idSkill, int idMob) {
        Mob mob = findMobInMap(idMob);
        if (mob != null) {
            DaiChienNhanGia3 event = (DaiChienNhanGia3) world;
            if (!event.canAttackMob(player, mob.id)) {
                player.service.serverMessage(mob.id == 98
                        ? "Boss này chỉ phe Làng Lá mới tấn công được."
                        : "Boss này chỉ phe Làng Đá mới tấn công được.");
                return;
            }
            if (isProtectedLongTru(mob)) {
                player.service.serverMessage("Phá hết quái xung quanh Ngân Long Trụ trước rồi mới đánh được.");
                return;
            }
        }
        super.attackMob(player, idSkill, idMob);
    }

    @Override
    public void setDameMob(Char player, Mob mob, int dame, boolean chi_mang) {
        if (isProtectedLongTru(mob)) {
            return;
        }
        super.setDameMob(player, mob, dame, chi_mang);
    }

    @Override
    public void nextMap(Char player) {
        if (player == null) {
            return;
        }
        DaiChienNhanGia3 event = (DaiChienNhanGia3) world;
        WayPoint waypoint = map.getWayPoint(player.Info);
        if (waypoint == null) {
            player.Info.backXY();
            player.service.setXYChar();
            return;
        }
        if (map.mapID == DaiChienNhanGia3.MAP_CHIEN_TRUONG && !isInsideWayPoint(player, waypoint)) {
            player.Info.backXY();
            player.service.setXYChar();
            player.service.serverMessage("Hãy vào cổng của phe mình để quay về bản doanh.");
            return;
        }

        if (map.mapID == DaiChienNhanGia3.MAP_CHIEN_TRUONG) {
            short ownCampMap = event.getCampMapId(event.getTeam(player));
            if (ownCampMap < 0) {
                player.Info.backXY();
                player.service.setXYChar();
                return;
            }
            if (waypoint.mapNext != ownCampMap) {
                player.Info.backXY();
                player.service.setXYChar();
                player.service.serverMessage(ownCampMap == DaiChienNhanGia3.MAP_LANG_LA
                        ? "Phe Làng Lá chỉ được quay về Bản Doanh Làng Lá."
                        : "Phe Làng Đá chỉ được quay về Bản Doanh Làng Đá.");
                return;
            }
            if (!event.returnToCamp(player)) {
                player.Info.backXY();
                player.service.setXYChar();
            }
            return;
        }

        if ((map.mapID == DaiChienNhanGia3.MAP_LANG_DA || map.mapID == DaiChienNhanGia3.MAP_LANG_LA)
                && waypoint.mapNext == DaiChienNhanGia3.MAP_CHIEN_TRUONG) {
            if (!event.enterBattlefield(player)) {
                player.Info.backXY();
                player.service.setXYChar();
            }
            return;
        }
        player.Info.backXY();
        player.service.setXYChar();
    }

    @Override
    public void createMob() {
        if (map.mapID == DaiChienNhanGia3.MAP_CHIEN_TRUONG) {
            spawnBaseBattlefieldMobs();
        }
    }

    public void spawnBaseBattlefieldMobs() {
        if (map.mapID != DaiChienNhanGia3.MAP_CHIEN_TRUONG) {
            return;
        }
        monsters.clear();
        for (int entityId = 0; entityId < 20; entityId++) {
            Mob mob = createBaseMobFromCSharp(entityId);
            if (mob != null) {
                monsters.add(mob);
            }
        }
        broadcastMobsToPlayers(monsters);
    }

    public void spawnTrapWave() {
        if (map.mapID != DaiChienNhanGia3.MAP_CHIEN_TRUONG) {
            return;
        }
        spawnOrRefreshMob(302, 97, 60, 500000, 0, 1900, 582);
        SendMessageInZone(HanderMessage.SendThongBao(
                "Trâu vàng đã xuất hiện tại chiến trường! Hạ gục để nhận tăng lực.",
                HanderMessage.WHITE));
    }

    public void spawnFinalBossPhase() {
        if (map.mapID != DaiChienNhanGia3.MAP_CHIEN_TRUONG) {
            return;
        }
        clearFinalBossMobs();

        addFinalBossMob(300, 98, 70, 90000000, 10, 1200, 582);
        addFinalBossMob(301, 99, 70, 90000000, 10, 2600, 582);
        SendMessageInZone(HanderMessage.SendThongBao(
                "Hai boss cuối đã xuất hiện! Phe nào hạ boss đối thủ trước sẽ thắng.",
                HanderMessage.WHITE));
    }

    @Override
    public void setMobDie(Char player, Mob mob) {
        super.setMobDie(player, mob);
        if (map.mapID != DaiChienNhanGia3.MAP_CHIEN_TRUONG || mob == null) {
            return;
        }
        DaiChienNhanGia3 event = (DaiChienNhanGia3) world;
        if (mob.id == 97) {
            rewardPlayer(player, DaiChienNhanGia3.BOSS_ATTACK_BUFF_VALUE, DaiChienNhanGia3.BOSS_ATTACK_BUFF_SECONDS,
                    "Bạn đã nhận được tăng lực trong 5 phút!");
            return;
        }
        if (mob.id == 98 || mob.id == 99) {
            rewardPlayer(player, DaiChienNhanGia3.BOSS_REWARD_POINTS, 0,
                    "Bạn nhận được 500 điểm từ việc hạ boss!");
            if (mob.id == 98 || mob.id == 99) {
                finishEventIfBossKilled(mob);
            }
            return;
        }
        if (mob.idEntity >= 300) {
            return;
        }
        if (!isLongTru(mob)) {
            return;
        }
        rewardPlayer(player, DaiChienNhanGia3.TRAP_REWARD_POINTS, 0,
                "Bạn nhận được 300 điểm khi phá trụ!");
    }

    private Mob createBaseMobFromCSharp(int entityId) {
        switch (entityId) {
            case 0:
                return createBaseMob(entityId, 240, 3097, 298, 10);
            case 1:
                return createBaseMob(entityId, 240, 3097, 742, 10);
            case 2:
                return createBaseMob(entityId, 103, 3313, 298, 0);
            case 3:
                return createBaseMob(entityId, 102, 2918, 198, 0);
            case 4:
                return createBaseMob(entityId, 103, 2513, 297, 0);
            case 6:
                return createBaseMob(entityId, 103, 3528, 742, 0);
            case 7:
                return createBaseMob(entityId, 102, 3285, 636, 0);
            case 8:
                return createBaseMob(entityId, 103, 2835, 742, 0);
            case 9:
                return createBaseMob(entityId, 102, 2504, 627, 0);
            case 10:
                return createBaseMob(entityId, 241, 667, 292, 10);
            case 11:
                return createBaseMob(entityId, 241, 667, 742, 10);
            case 13:
                return createBaseMob(entityId, 100, 422, 292, 0);
            case 14:
                return createBaseMob(entityId, 102, 876, 216, 0);
            case 15:
                return createBaseMob(entityId, 100, 1234, 292, 0);
            case 16:
                return createBaseMob(entityId, 100, 505, 742, 0);
            case 17:
                return createBaseMob(entityId, 100, 896, 742, 0);
            case 18:
                return createBaseMob(entityId, 102, 1028, 642, 0);
            case 19:
                return createBaseMob(entityId, 100, 1343, 742, 0);
            default:
                return null;
        }
    }

    private Mob createBaseMob(int entityId, int mobId, int cx, int cy, int bossLevel) {
        Mob mob = new Mob();
        mob.idEntity = entityId;
        mob.id = mobId;
        mob.level = 40;
        mob.levelBoss = bossLevel;
        mob.status = 0;
        mob.cx = (short) cx;
        mob.cy = (short) cy;
        mob.hpGoc = mob.hpFull = mob.hp = 999999;
        mob.expGoc = mob.exp = mob.hp / 7;
        mob.paintMiniMap = false;
        mob.he = Utlis.nextInt(1, 5);
        mob.zone = this;
        mob.setHp();
        return mob;
    }

    private void spawnOrRefreshMob(int entityId, int mobId, int mobLevel, int hp, int bossLevel, int cx, int cy) {
        Mob existing = findMobInMap(entityId);
        if (existing != null) {
            existing.isDie = false;
            existing.hpGoc = existing.hpFull = existing.hp = hp;
            existing.cx = (short) cx;
            existing.cy = (short) cy;
            existing.setHp();
            SendMessageInZone(HanderMessage.AddMob(existing));
            return;
        }
        Mob mob = new Mob();
        mob.idEntity = entityId;
        mob.id = mobId;
        mob.level = mobLevel;
        mob.levelBoss = bossLevel;
        mob.status = 0;
        mob.cx = (short) cx;
        mob.cy = (short) cy;
        mob.hpGoc = mob.hpFull = mob.hp = hp;
        mob.expGoc = mob.exp = Math.max(1, hp / 7);
        mob.paintMiniMap = false;
        mob.he = Utlis.nextInt(1, 5);
        mob.zone = this;
        mob.isReSpawn = false;
        mob.setHp();
        monsters.add(mob);
        SendMessageInZone(HanderMessage.AddMob(mob));
    }

    private void addFinalBossMob(int entityId, int mobId, int mobLevel, int hp, int bossLevel, int cx, int cy) {
        if (findMobInMap(entityId) != null) {
            return;
        }
        spawnOrRefreshMob(entityId, mobId, mobLevel, hp, bossLevel, cx, cy);
    }

    private void clearFinalBossMobs() {
        for (int i = monsters.size() - 1; i >= 0; i--) {
            Mob mob = monsters.get(i);
            if (mob != null && mob.idEntity >= 300 && mob.idEntity <= 301) {
                monsters.remove(i);
            }
        }
    }

    private void rewardPlayer(Char player, int points, int buffSeconds, String message) {
        if (player == null || player.isClean || player.user == null) {
            return;
        }
        try {
            if (points > 0 && player.getEventPoint() != null) {
                player.getEventPoint().addPoint(com.event.eventpoint.EventPoint.DIEM_TIEU_XAI, points);
                player.updateEventPoint();
            }
        } catch (Exception ignored) {
        }
        if (buffSeconds > 0) {
            player.Point.hoatLuc += points;
            try {
                player.service.sendPointMap(player.Point.hoatLuc);
            } catch (Exception ignored) {
            }
            player.writeInfo();
        }
        if (player.service != null) {
            player.service.serverMessage(message);
        }
    }

    private void finishEventIfBossKilled(Mob mob) {
        // Giữ sẵn điểm kết thúc để phía world có thể xử lý thắng thua khi cần mở rộng tiếp.
    }

    private boolean hasAliveBaseMobs() {
        for (Mob mob : monsters) {
            if (mob != null && !mob.isDie && mob.idEntity >= 0 && mob.idEntity < 300) {
                return true;
            }
        }
        return false;
    }

    private void broadcastMobsToPlayers(Iterable<Mob> mobList) {
        if (getChars().isEmpty()) {
            return;
        }
        for (Mob mob : mobList) {
            if (mob != null) {
                SendMessageInZone(HanderMessage.AddMob(mob));
            }
        }
    }

    @Override
    protected boolean canRespawn(Mob mob) {
        return mob != null;
    }

    private boolean isProtectedLongTru(Mob mob) {
        if (mob == null || mob.isDie) {
            return false;
        }
        for (int[] group : LONG_TRU_GUARD_GROUPS) {
            if (group.length == 0 || mob.idEntity != group[0]) {
                continue;
            }
            for (int i = 1; i < group.length; i++) {
                Mob guard = findMobInMap(group[i]);
                if (guard != null && !guard.isDie) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    private boolean isLongTru(Mob mob) {
        return mob != null && (mob.id == 240 || mob.id == 241);
    }

    private boolean isInsideWayPoint(Char player, WayPoint waypoint) {
        if (player == null || waypoint == null) {
            return false;
        }
        return player.Info.cx >= waypoint.l && player.Info.cx <= waypoint.n
                && player.Info.cy >= waypoint.m && player.Info.cy <= waypoint.o;
    }
}

