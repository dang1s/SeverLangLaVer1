package MapService;

import com.sg188.data.DataCenter;
import com.sg188.data.MapTemplate;
import com.sg188.lib.Log;
import com.sg188.real.*;
import com.sg188.server.Main;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Map {
    public static Map[] maps;
    public static final int NUM_ZONE = 15;
    @Getter
    private List<Zone> zones = new ArrayList<>();
    public List<WayPoint> listWayPoint = new ArrayList<WayPoint>();

    public int mapID;
    public static boolean running = true;
    public Thread threadUpdateChar, threadUpdateOther;
//    public static final Map<Integer, Integer> solanHSKRC = new HashMap<>();

    public Map(int id) {
        this.mapID = id;
    }

    public MapTemplate getMapTemplate() {
        return DataCenter.gI().MapTemplate[mapID];
    }

    public void createZone() {
        createZone(NUM_ZONE);
    }

    private void createZone(int NUM_ZONE) {
        for (int i = 0; i < NUM_ZONE; i++) {
            Zone zone = new Zone(this, i);
            try {
                zone.createNpc();

                zone.createMob();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            zones.add(zone);
        }
    }

    public void resetMob() {
        for (int i = 0; i < zones.size(); i++) {
            Zone zone = zones.get(i);
            if (zone != null) {
                zone.lockMob.readLock().lock();
                try {
                    for (int j = 0; j < zone.monsters.size(); j++) {
                        Mob mob = zone.monsters.get(j);
                        if (mob != null) {
                            mob.reSpawn(zone);
                            zone.reSpawnMobToAllChar(mob);
                        }
                    }
                } finally {
                    zone.lockMob.readLock().unlock();
                }
            }
        }
    }

    public void addBoss(int zoneid, Mob boss) {
        for (Zone zone : zones) {
            if (zone.zoneID == zoneid) {
                zone.lockMob.writeLock().lock();
                try {
                    boss.idEntity = zone.monsters.size();
                    boss.timeRemove = System.currentTimeMillis() + 60 * 60 * 1000;
                    zone.monsters.add(boss);
                } finally {
                    zone.lockMob.writeLock().unlock();
                }
                zone.reSpawnMobToAllChar(boss);
                zone.MAX_CHAR_INZONE = 100;
                Log.info("khu: " + zoneid + " mod: " + boss.getMobTemplate().name);
            }
        }
    }

    public void createWayPoint() {
        for (int index = 0; index < DataCenter.gI().dataWayPoint.length; ++index) {
            WayPoint waypoint = null;
            if (DataCenter.gI().dataWayPoint[index][0] == this.mapID) {
                (waypoint = new WayPoint(0, 0)).create(DataCenter.gI().dataWayPoint[index][0],
                        DataCenter.gI().dataWayPoint[index][5], DataCenter.gI().dataWayPoint[index][1],
                        DataCenter.gI().dataWayPoint[index][2], DataCenter.gI().dataWayPoint[index][3],
                        DataCenter.gI().dataWayPoint[index][4], DataCenter.gI().dataWayPoint[index][10],
                        DataCenter.gI().dataWayPoint[index][11]);
                waypoint.isNext = true;
                this.listWayPoint.add(waypoint);
            } else if (DataCenter.gI().dataWayPoint[index][5] == this.mapID) {
                (waypoint = new WayPoint(0, 0)).create(DataCenter.gI().dataWayPoint[index][5],
                        DataCenter.gI().dataWayPoint[index][0], DataCenter.gI().dataWayPoint[index][6],
                        DataCenter.gI().dataWayPoint[index][7], DataCenter.gI().dataWayPoint[index][8],
                        DataCenter.gI().dataWayPoint[index][9], DataCenter.gI().dataWayPoint[index][12],
                        DataCenter.gI().dataWayPoint[index][13]);
                waypoint.isNext = false;
                this.listWayPoint.add(waypoint);
            }
        }
    }

    public WayPoint getWayPoint_WhenNextMap(int idMapNext) {
        Map mapNext = this;
        for (int i = 0; i < mapNext.listWayPoint.size(); i++) {
            WayPoint waypoint = mapNext.listWayPoint.get(i);
            if (waypoint.mapNext == idMapNext) {
                return waypoint;
            }
        }
        return null;
    }

    public WayPoint getWayPoint_WhenInMap(int idMapNext) {
        Map mapNext = maps[idMapNext];

        return mapNext.getWayPoint_WhenNextMap(mapID);
    }

    public WayPoint getWayPoint(XYEntity xy) {
        Map mapNext = this;
        WayPoint _waypoint = null;
        for (int i = 0; i < mapNext.listWayPoint.size(); i++) {
            WayPoint waypoint = mapNext.listWayPoint.get(i);
            if (_waypoint == null || waypoint.getRe(xy) < _waypoint.getRe(xy)) {

                _waypoint = waypoint;
            }
        }
        return _waypoint;
    }

    public void AddLoginGame(Char _myChar) {
        boolean isdone = false;
        for (Zone z : this.zones) {
            if (z.players.size() < z.MAX_CHAR_INZONE) {
                z.addPlayer(_myChar);
                _myChar.zone = z;
                _myChar.zone.addToAllChar(_myChar);
                _myChar.service.sendArrMap(this.mapID);
                _myChar.service.sendIntoMap();
//                _myChar.service.alertMessage("Làng Lá Black - Đỉnh Cao Nhẫn Giả - Cày Chay Thả Ga Không Cần Nạp - Truy cập ngay langlablack.com");
//                 _myChar.service.alertMessage("Làng Lá Black - Đỉnh Cao Nhẫn Giả\n" +
//                 "Cày Chay Thả Ga Không Cần Nạp.\n" +
//                 "Truy cập ngay langlablack.com\n" +
//                 "Chúc bạn chơi game vui vẻ.");
                _myChar.service.alertMessage(
                        "Đại Chiến Làng Lá - Dame Gốc\n" +
                                "Gameplay Nguyên Bản - Kỹ Năng Chuẩn\n" +
                                "Cày Chay Công Bằng - PK Cân Não\n" +
                                "Chúc bạn trải nghiệm vui vẻ."
                );
                _myChar.sendMobPet_Other();
//                z.updateMat(_myChar);
//                z.updateWing(_myChar);
                isdone = true;
                break;
            }
        }
        if (!isdone) {
            Map.maps[85].addChar(_myChar);
        }
    }

    public boolean addChar(Char player) {
        for (int i = 0; i < this.zones.size(); i++) {
            Zone z = this.zones.get(i);
            if (z.players.size() < z.MAX_CHAR_INZONE / 2) {
                boolean can = z.addChar(player);
                if (can) {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateChar() {
        String threadKey = "map-" + this.mapID + "-updateChar";
        while (running) {
            try {
                MapThreadWatchdog.getInstance().recordHeartbeat(threadKey);

                long l1 = System.currentTimeMillis();
                List<Zone> list = new ArrayList<>(zones);
                for (Zone zone : list) {
                    if (zone != null && !zone.isClosed)
                        zone.updatePlayer();
                }

                long l2 = System.currentTimeMillis() - l1;
                if (l2 >= 500L) {
                    continue;
                }
                try {
                    Thread.sleep(500L - l2);
                } catch (InterruptedException e) {
                    MapThreadWatchdog.getInstance().recordHeartbeat(threadKey);
                    Log.info("Map " + this.mapID + " - updateChar thread bị interrupt, đang thoát...");
                    Thread.currentThread().interrupt(); // Khôi phục interrupt flag
                    break;
                }
            } catch (Exception e2) {
                Log.error("Map " + this.mapID + " - updateChar gặp lỗi: " + e2.getMessage());
            }
        }

    }

    public void updateOther() {
        String threadKey = "map-" + this.mapID + "-updateOther";
        while (running) {
            try {
                MapThreadWatchdog.getInstance().recordHeartbeat(threadKey);
                long l1 = System.currentTimeMillis();
                List<Zone> list = new ArrayList<>(zones);
                for (Zone zone : list) {
                    if (zone != null && !zone.isClosed)
                        zone.update();
                }
                long l2 = System.currentTimeMillis() - l1;
                if (l2 >= 1000L) {
                    continue;
                }
                try {
                    Thread.sleep(1000L - l2);
                } catch (InterruptedException e) {
                    MapThreadWatchdog.getInstance().recordHeartbeat(threadKey);
                    Log.info("Map " + this.mapID + " - updateOther thread bị interrupt, đang thoát...");
                    Thread.currentThread().interrupt(); // Khôi phục interrupt flag
                    break; // Thoát vòng lặp
                }
            } catch (Exception e2) {
                Log.error("Map " + this.mapID + " - updateOther gặp lỗi: " + e2.getMessage());
            }
        }

    }

    public void update() {
        this.threadUpdateChar = new Thread(new Runnable() {
            @Override
            public void run() {
                updateChar();
            }

        });
        this.threadUpdateChar.start();
        this.threadUpdateOther = new Thread(new Runnable() {
            @Override
            public void run() {
                updateOther();
            }
        });
        this.threadUpdateOther.start();
    }

    public void close() {
        if (this.threadUpdateChar != null && this.threadUpdateChar.isAlive()) {
            this.threadUpdateChar.interrupt();
        }
        this.threadUpdateChar = null;
        if (this.threadUpdateOther != null && this.threadUpdateOther.isAlive()) {
            this.threadUpdateOther.interrupt();
        }
        this.threadUpdateOther = null;
    }

    // public void start() {
    // if (this.runing) {
    // this.close();
    // }
    // this.runing = true;
    // if (this.threadUpdate == null) {
    // this.threadUpdate = new Thread(new RunPlace());
    // }
    // this.threadUpdate.setName("Update Map " + mapID);
    // this.threadUpdate.start();
    // }
    //
    // public void close() {
    // this.runing = false;
    // byte i;
    // for (i = 0; i < this.listZone.size(); ++i) {
    // if (this.listZone.get(i) != null) {
    // this.listZone.get(i).close();
    // }
    // }
    // this.threadUpdate = null;
    // this.LOCK = null;
    // }

    public static void createMap() {
        if (maps == null) {
            maps = new Map[DataCenter.gI().MapTemplate.length];
            for (int i = 0; i < maps.length; i++) {
                Map map = new Map(i);
                if (!DataCenter.gI().MapTemplate[i].notBlock) {
                    map.createZone();
                    map.createWayPoint();
                }

                maps[i] = map;
                maps[i].update();
            }
        }
    }

    public void resetThreadUpdate() {
        this.threadUpdateOther = new Thread(new Runnable() {
            @Override
            public void run() {
                updateOther();
            }
        });
        this.threadUpdateOther.start();
    }

}
