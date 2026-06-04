package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.ZDaiChien3;
import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.server.lib.Writer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DaiChienNhanGia3 extends World {
    public static final int TEAM_LANG_LA = 0;
    public static final int TEAM_LANG_DA = 1;

    public static final short MAP_LANG_DA = 42;
    public static final short MAP_LANG_LA = 43;
    public static final short MAP_CHIEN_TRUONG = 41;

    public static final int MAX_TEAM_SIZE = 126;
    public static final int MAX_TEAM_DIFFERENCE = 5;
    public static final int SIGN_UP_SECONDS = 60;
    public static final int BATTLE_PHASE_SECONDS = 180;
    public static final int TOTAL_DURATION_SECONDS = 240;
    public static final int TRAP_SPAWN_INTERVAL_SECONDS = 60;
    public static final int FINAL_BOSS_PHASE_SECONDS = 180;
    public static final int TRAP_REWARD_POINTS = 300;
    public static final int BOSS_REWARD_POINTS = 500;
    public static final int BOSS_ATTACK_BUFF_VALUE = 500;
    public static final int BOSS_ATTACK_BUFF_SECONDS = 300;
    public static final byte PK_LANG_LA = 2;
    public static final byte PK_LANG_DA = 3;
    public static final short BATTLEFIELD_LANG_DA_X = 70;
    public static final short BATTLEFIELD_LANG_LA_X = 3978;
    public static final short BATTLEFIELD_SPAWN_Y = 582;

    private static final Object GLOBAL_LOCK = new Object();

    public static DaiChienNhanGia3 BanDoanhLangLa;
    public static DaiChienNhanGia3 BanDoanhLangDa;

    private final Object teamLock = new Object();
    private final Set<Integer> registeredCharIds = new HashSet<>();
    private final Set<Integer> blockedReentryIds = new HashSet<>();
    private final Set<Integer> teamLangLaIds = new HashSet<>();
    private final Set<Integer> teamLangDaIds = new HashSet<>();
    private final java.util.Map<Integer, Integer> teamByCharId = new HashMap<>();

    private final long createdAt;
    private final long signUpEndsAt;
    private final long eventEndsAt;
    private volatile long battleStartedAt;
    private final boolean instantStart;

    private int maxPlayer;
    private boolean opened;
    private int level;
    private boolean battleStarted;
    private boolean finalBossSpawned;
    private boolean baseMobsCreated;
    private boolean closingEvent;
    private long nextTrapSpawnAt;

    public DaiChienNhanGia3(int countDown, int level) {
        this(countDown, level, false);
    }

    public DaiChienNhanGia3(int countDown, int level, boolean instantStart) {
        setType(World.DAI_CHIEN_NHAN_GIA_3);
        this.name = "DaiChienNhanGia3";
        this.level = level;
        this.instantStart = instantStart;
        generateId();
        this.countDown = instantStart ? 0 : (countDown > 0 ? countDown : SIGN_UP_SECONDS);
        this.createdAt = System.currentTimeMillis();
        this.signUpEndsAt = this.createdAt + this.countDown * 1000L;
        this.eventEndsAt = this.createdAt + TOTAL_DURATION_SECONDS * 1000L;
        this.maxPlayer = MAX_TEAM_SIZE * 2;
        this.opened = !instantStart;
        this.nextTrapSpawnAt = createdAt + TRAP_SPAWN_INTERVAL_SECONDS * 1000L;
        initZone();
        createBattlefieldMobs();
        if (instantStart) {
            startBattle();
        }
        initFinished = true;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isBattleStarted() {
        return battleStarted;
    }

    public boolean isInstantStart() {
        return instantStart;
    }

    public int getNumberPlayer() {
        synchronized (members) {
            return members == null ? 0 : members.size();
        }
    }

    public void addCharId(int id) {
        synchronized (teamLock) {
            registeredCharIds.add(id);
        }
    }

    public boolean findCharId(int id) {
        synchronized (teamLock) {
            return registeredCharIds.contains(id);
        }
    }

    public int getTeam(Char player) {
        return player == null ? -1 : getTeam(player.id);
    }

    public int getTeam(int charId) {
        synchronized (teamLock) {
            Integer team = teamByCharId.get(charId);
            return team == null ? -1 : team;
        }
    }

    public short getCampMapId(int team) {
        if (team == TEAM_LANG_LA) {
            return MAP_LANG_LA;
        }
        if (team == TEAM_LANG_DA) {
            return MAP_LANG_DA;
        }
        return -1;
    }

    public boolean join(Char player) {
        if (player == null || player.user == null || player.isClean) {
            return false;
        }
        if (isClosed) {
            player.service.serverMessage("Sự kiện đã đóng.");
            return false;
        }

        final int team;
        synchronized (teamLock) {
            if (blockedReentryIds.contains(player.id)) {
                player.service.serverMessage("Bạn đã rời khỏi chiến trường, không thể vào lại.");
                return false;
            }
            Integer previousTeam = teamByCharId.get(player.id);
            boolean registered = registeredCharIds.contains(player.id);

            if (battleStarted && !registered && !instantStart) {
                player.service.serverMessage("Đã hết thời gian báo danh.");
                return false;
            }

            team = resolveTeam(player.id, previousTeam, registered);
            if (team < 0) {
                player.service.serverMessage("Sự kiện đã đủ người tham gia.");
                return false;
            }

            registeredCharIds.add(player.id);
            teamByCharId.put(player.id, team);
            teamLangLaIds.remove(player.id);
            teamLangDaIds.remove(player.id);
            if (team == TEAM_LANG_LA) {
                teamLangLaIds.add(player.id);
            } else {
                teamLangDaIds.add(player.id);
            }
        }

        World currentWorld = player.findWorld(World.DAI_CHIEN_NHAN_GIA_3);
        if (currentWorld != null && currentWorld != this) {
            currentWorld.removeMember(player);
            player.removeWorld(World.DAI_CHIEN_NHAN_GIA_3);
        }
        if (player.findWorld(World.DAI_CHIEN_NHAN_GIA_3) == null) {
            player.addWorld(this);
        }

        Zone camp = getCampZone(team);
        if (camp == null || !camp.addChar(player)) {
            player.service.serverMessage("Không thể vào bản doanh lúc này.");
            return false;
        }

        ensureMember(player);
        applyTeamFlag(player, team);
        player.service.serverMessage(team == TEAM_LANG_LA ? "Bạn được xếp vào phe Làng Lá." : "Bạn được xếp vào phe Làng Đá.");
        if (battleStarted) {
            player.service.serverMessage("Chiến trường đã mở, hãy nhanh chóng xuất chiến.");
            player.service.sendMessage(HanderMessage.SendThongBao("Chiến trường nhẫn giả đã mở!", HanderMessage.WHITE));
        }
        return true;
    }

    private int resolveTeam(int charId, Integer previousTeam, boolean registered) {
        int langLaCount = teamLangLaIds.size();
        int langDaCount = teamLangDaIds.size();

        boolean isReturning = registered && previousTeam != null
                && (previousTeam == TEAM_LANG_LA || previousTeam == TEAM_LANG_DA);

        if (isReturning) {
            return previousTeam;
        }

        boolean langLaFull = langLaCount >= MAX_TEAM_SIZE;
        boolean langDaFull = langDaCount >= MAX_TEAM_SIZE;

        if (langLaFull && langDaFull) {
            return -1;
        }
        if (langLaFull) {
            return TEAM_LANG_DA;
        }
        if (langDaFull) {
            return TEAM_LANG_LA;
        }
        if (langLaCount < langDaCount) {
            return TEAM_LANG_LA;
        }
        if (langDaCount < langLaCount) {
            return TEAM_LANG_DA;
        }

        int randomTeam = Utlis.nextInt(0, 1);
        int projectedLangLa = langLaCount + (randomTeam == TEAM_LANG_LA ? 1 : 0);
        int projectedLangDa = langDaCount + (randomTeam == TEAM_LANG_DA ? 1 : 0);
        if (Math.abs(projectedLangLa - projectedLangDa) > MAX_TEAM_DIFFERENCE) {
            return randomTeam == TEAM_LANG_LA ? TEAM_LANG_DA : TEAM_LANG_LA;
        }
        return randomTeam;
    }

    private void ensureMember(Char player) {
        synchronized (members) {
            if (members != null && !members.contains(player)) {
                members.add(player);
            }
        }
    }

    private Zone getCampZone(int team) {
        if (zones == null || zones.size() < 2) {
            return null;
        }
        return team == TEAM_LANG_LA ? zones.get(1) : zones.get(0);
    }

    private ZDaiChien3 getBattlefieldZone() {
        if (zones == null || zones.size() < 3) {
            return null;
        }
        return (ZDaiChien3) zones.get(2);
    }

    private void createBattlefieldMobs() {
        synchronized (GLOBAL_LOCK) {
            if (baseMobsCreated) {
                return;
            }
            ZDaiChien3 battlefield = getBattlefieldZone();
            if (battlefield != null) {
                battlefield.spawnBaseBattlefieldMobs();
                baseMobsCreated = true;
            }
        }
    }

    private void startBattle() {
        if (battleStarted || isClosed) {
            return;
        }
        createBattlefieldMobs();
        battleStarted = true;
        opened = false;
        battleStartedAt = System.currentTimeMillis();
        countDown = Math.max(0, (int) ((eventEndsAt - battleStartedAt) / 1000L));
        applyFlagsForAllMembers();
        getService().sendMessage(HanderMessage.SendThongBao("Chiến trường nhẫn giả đã mở!", HanderMessage.WHITE));
        getService().sendTimeInMap(countDown, false);
    }

    private void spawnTrapWave() {
        ZDaiChien3 battlefield = getBattlefieldZone();
        if (battlefield == null || isClosed) {
            return;
        }
        battlefield.spawnTrapWave();
    }

    private void spawnFinalBossPhase() {
        if (finalBossSpawned || isClosed) {
            return;
        }
        ZDaiChien3 battlefield = getBattlefieldZone();
        if (battlefield == null) {
            return;
        }
        finalBossSpawned = true;
        battlefield.spawnFinalBossPhase();
    }

    private void applyFlagsForAllMembers() {
        List<Char> memberList = getMembers();
        for (Char player : memberList) {
            applyTeamFlag(player, getTeam(player));
        }
    }

    private void setPkForChar(Char player, byte type) {
        try {
            if (player != null && !player.isClean && player.user != null) {
                player.InfoGame.TypePk = type;
                if (player.zone != null) {
                    player.zone.SendMessageInZone(HanderMessage.SendTypePk(player.Info.idEntity, type));
                }
            }
        } catch (Exception e) {
            Log.error("set pk dai chien nhan gia 3 err", e);
        }
    }

    private void applyTeamFlag(Char player, int team) {
        if (team == TEAM_LANG_LA) {
            setPkForChar(player, PK_LANG_LA);
        } else if (team == TEAM_LANG_DA) {
            setPkForChar(player, PK_LANG_DA);
        }
    }

    public boolean canAttackPlayer(Char attacker, Char target) {
        if (!battleStarted || attacker == null || target == null) {
            return false;
        }
        int attackerTeam = getTeam(attacker);
        int targetTeam = getTeam(target);
        return attackerTeam != -1 && targetTeam != -1 && attackerTeam != targetTeam;
    }

    public boolean canAttackMob(Char player, int mobId) {
        return true;
    }

    public boolean returnToCamp(Char player) {
        return moveToCamp(player, false);
    }

    public boolean reviveToCamp(Char player) {
        return moveToCamp(player, true);
    }

    private boolean moveToCamp(Char player, boolean revive) {
        if (player == null || player.isClean || player.user == null) {
            return false;
        }
        int team = getTeam(player);
        Zone camp = getCampZone(team);
        if (camp == null) {
            return false;
        }
        if (!camp.addChar(player)) {
            return false;
        }
        if (revive) {
            player.InfoGame.isDie = false;
            player.Point.hp = player.maxHP;
            player.Point.mp = player.maxMP;
            player.writeInfo();
            try {
                Writer writer = new Writer();
                writer.writeInt(player.Info.idEntity);
                writer.writeInt(player.Point.hp);
                writer.writeInt(player.Point.mp);
                camp.reSpawn(writer);
            } catch (Exception e) {
                Log.error("dai chien nhan gia 3 revive camp err", e);
            }
        }
        ensureMember(player);
        applyTeamFlag(player, team);
        return true;
    }

    public boolean enterBattlefield(Char player) {
        if (player == null || player.isClean || player.user == null) {
            return false;
        }
        ZDaiChien3 battlefield = getBattlefieldZone();
        if (battlefield == null) {
            return false;
        }
        if (!battlefield.addChar(player)) {
            return false;
        }
        int team = getTeam(player);
        if (team == TEAM_LANG_LA) {
            player.setXY(BATTLEFIELD_LANG_LA_X, BATTLEFIELD_SPAWN_Y);
        } else if (team == TEAM_LANG_DA) {
            player.setXY(BATTLEFIELD_LANG_DA_X, BATTLEFIELD_SPAWN_Y);
        }
        player.service.setXYChar();
        ensureMember(player);
        applyTeamFlag(player, team);
        return true;
    }

    @Override
    public void update() {
        zones.forEach(z -> {
            if (z != null && !isClosed && !z.isClosed) {
                z.update();
                z.updatePlayer();
            }
        });

        long now = System.currentTimeMillis();
        if (!battleStarted) {
            countDown = Math.max(0, (int) ((signUpEndsAt - now + 999L) / 1000L));
            if (now >= signUpEndsAt) {
                startBattle();
            }
            return;
        }

        countDown = Math.max(0, (int) ((eventEndsAt - now + 999L) / 1000L));
        if (!instantStart && now >= nextTrapSpawnAt && now < battleStartedAt + BATTLE_PHASE_SECONDS * 1000L) {
            spawnTrapWave();
            nextTrapSpawnAt = now + TRAP_SPAWN_INTERVAL_SECONDS * 1000L;
        }
        if (!instantStart && !finalBossSpawned && now - battleStartedAt >= FINAL_BOSS_PHASE_SECONDS * 1000L) {
            spawnFinalBossPhase();
        }
        if (now >= eventEndsAt) {
            close();
        }
    }

    public void initZone() {
        Map map = new Map(MAP_LANG_DA);
        map.createWayPoint();
        addCampWayPoints(map, false);
        addZone(new ZDaiChien3(map, 0, level, this));
        zones.get(0).MAX_CHAR_INZONE = MAX_TEAM_SIZE;

        map = new Map(MAP_LANG_LA);
        map.createWayPoint();
        addCampWayPoints(map, true);
        addZone(new ZDaiChien3(map, 0, level, this));
        zones.get(1).MAX_CHAR_INZONE = MAX_TEAM_SIZE;

        map = new Map(MAP_CHIEN_TRUONG);
        map.createWayPoint();
        addBattlefieldWayPoints(map);
        addZone(new ZDaiChien3(map, 0, level, this));
        zones.get(2).MAX_CHAR_INZONE = maxPlayer;
    }

    private void addCampWayPoints(Map map, boolean isLangLa) {
        short maxX = map.getMapTemplate().maxX;
        short x1 = isLangLa ? (short) 10 : (short) Math.max(10, maxX - 120);
        short x2 = isLangLa ? (short) 120 : (short) Math.max(20, maxX - 10);
        addCustomWayPoint(map, MAP_CHIEN_TRUONG, x1, (short) 500, x2, (short) 660);
    }

    private void addBattlefieldWayPoints(Map map) {
        removeWayPointToMap(map, MAP_LANG_DA);
        removeWayPointToMap(map, MAP_LANG_LA);
        addCustomWayPoint(map, MAP_LANG_DA, (short) 10, (short) 520, (short) 130, BATTLEFIELD_SPAWN_Y);
        addCustomWayPoint(map, MAP_LANG_LA, (short) 3918, (short) 520, (short) 4038, BATTLEFIELD_SPAWN_Y);
    }

    private void addCustomWayPoint(Map map, short mapNext, short x1, short y1, short x2, short y2) {
        for (var waypoint : map.listWayPoint) {
            if (waypoint.mapNext == mapNext && Math.abs(waypoint.cx - ((x1 + x2) / 2)) < 120) {
                return;
            }
        }
        com.sg188.real.WayPoint waypoint = new com.sg188.real.WayPoint(0, 0);
        waypoint.create((short) map.mapID, mapNext, x1, y1, x2, y2, (short) 0, (short) 0);
        waypoint.isNext = true;
        map.listWayPoint.add(waypoint);
    }

    private void removeWayPointToMap(Map map, short mapNext) {
        map.listWayPoint.removeIf(waypoint -> waypoint != null && waypoint.mapNext == mapNext);
    }

    @Override
    public void removeMember(Char player) {
        super.removeMember(player);
        if (player == null) {
            return;
        }
        if (!isClosed && !closingEvent) {
            synchronized (teamLock) {
                if (registeredCharIds.contains(player.id)) {
                    blockedReentryIds.add(player.id);
                }
            }
        }
        player.InfoGame.TypePk = 0;
    }

    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return !isEventMap(pre) && isEventMap(next);
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return isEventMap(pre) && !isEventMap(next);
    }

    private boolean isEventMap(Zone zone) {
        if (zone == null || zone.map == null) {
            return false;
        }
        int mapId = zone.map.mapID;
        return mapId == MAP_LANG_DA || mapId == MAP_LANG_LA || mapId == MAP_CHIEN_TRUONG;
    }

    @Override
    public void close() {
        try {
            if (this.isClosed) {
                return;
            }
            closingEvent = true;
            opened = false;
            List<Char> memberList = getMembers();
            for (Char player : memberList) {
                try {
                    if (player == null || player.isClean) {
                        continue;
                    }
                    player.InfoGame.TypePk = 0;
                    Map.maps[player.Info.mapReSpawm].addChar(player);
                    player.removeWorld(World.DAI_CHIEN_NHAN_GIA_3);
                } catch (Exception e) {
                    Log.error("player leave DaiChienNhanGia3 err", e);
                }
            }
        } catch (Exception e) {
            Log.error("close DaiChienNhanGia3 err", e);
        } finally {
            synchronized (teamLock) {
                registeredCharIds.clear();
                blockedReentryIds.clear();
                teamLangLaIds.clear();
                teamLangDaIds.clear();
                teamByCharId.clear();
            }
            if (DaiChienNhanGia3.BanDoanhLangLa == this) {
                DaiChienNhanGia3.BanDoanhLangLa = null;
            }
            if (DaiChienNhanGia3.BanDoanhLangDa == this) {
                DaiChienNhanGia3.BanDoanhLangDa = null;
            }
            super.close();
        }
    }
}
