package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.ClanTerritory;
import MapService.zones.ZoneSonCap;
import com.sg188.real.Char;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SonCapMyo extends World {

    public static List<SonCapMyo> sonCapMyos = new ArrayList<>();

    public static void addSonCap(SonCapMyo sonCapMyo) {
        synchronized (sonCapMyos) {
            sonCapMyos.add(sonCapMyo);
        }
    }
    public static void removeSonCap(SonCapMyo sonCapMyo) {
        synchronized (sonCapMyos) {
            sonCapMyos.remove(sonCapMyo);
        }
    }
    public static final int[] MAPS = {94,95,97,93,96};

    public static ArrayList<Integer> listCharIdInSonCap= new ArrayList<>();
    public ArrayList<Integer> listCharId;

    public boolean started = false;
    public int level;
    public SonCapMyo(int level) {
        setType(World.SONCAP);
        this.name = "SonCapMyo";
        generateId();
        this.listCharId = new ArrayList<>();;
        this.level=level;
        this.countDown = 3600; // 5 minutes
        initZone();
        initFinished = true;
    }
    public void generateId() {
        this.id = number++;
    }


    public void initZone() {
        for (int mapId : MAPS) {
            Map map = new Map(mapId);
            map.createWayPoint();
            ZoneSonCap zoneSonCap = new ZoneSonCap(map,0);
            zoneSonCap.level=level;
            zoneSonCap.setWorld(this);
            zoneSonCap.createMob();
            addZone(zoneSonCap);
        }
    }
    @Override
    public void update() {
        zones.forEach(z -> {
            if (z != null&&!isClosed&& !z.isClosed) {
                z.update();
                z.updatePlayer();
            }
        });
        countDown--;
        if (countDown <= 0) {
            close();
            return;
        }
    }

    public void finish() {
        countDown = 60;
        service.sendTimeInMap(countDown*10,false);
        service.serverMessage("Hành trình thám hiệm Sơn cốc Myonbokyu đã kết thúc.");
        List<Char> members = getMembers();
        for (Char _char : members) {
            try {
                if (_char != null && _char.clan != null) {
                    _char.addClanPoint(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if (this.isClosed) {
            return;
        }
        try {
            List<Char> members = getMembers();
            for (Char _char : members) {
                try {
                    if (_char.isClean) {
                        continue;
                    }
                    _char.pointDungeon=0;
                    Map.maps[85].addChar(_char);
                    _char.service.serverMessage("Hành trình thám hiệm Sơn cốc Myonbokyu đã kết thúc.");
                    _char.removeWorld(World.SONCAP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){

        }finally {
            SonCapMyo.removeSonCap(this);
            super.close();
        }

    }

    public void joinZone(Char _char, int map) {
        for (Zone z : this.zones) {
            if (z.map.mapID == map) {
                try {
                    if (z != null) {
                        z.addChar(_char);
                        _char.getService().sendTimeInMap(countDown*10,true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }


    @Override
    public void addMember(Char _char) {
        super.addMember(_char);
        synchronized (this.listCharId) {
            for (int id : this.listCharId) {
                if (id == _char.id) {
                    return;
                }
            }
            this.listCharId.add(_char.id);
        }
        synchronized (this.listCharIdInSonCap) {
            for (int id : this.listCharIdInSonCap) {
                if (id == _char.id) {
                    return;
                }
            }
            SonCapMyo.listCharIdInSonCap.add(_char.id);
        }
    }
    public static SonCapMyo findSonCapByCharId(int charId){
        for (SonCapMyo sonCapMyo: sonCapMyos){
            for (int id : sonCapMyo.listCharId) {
                if (id == charId) {
                    return sonCapMyo;
                }
            }
        }
        return null;
    }


    public static boolean isInSonCap(int charId) {
        synchronized (listCharIdInSonCap) {
            for (int id : SonCapMyo.listCharIdInSonCap) {
                if (id == charId) {
                    return true;
                }
            }
            return false;
        }
    }


    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return !pre.isSonCapMyo() && next.isSonCapMyo();
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return pre.isSonCapMyo() && !next.isSonCapMyo();
    }
}

