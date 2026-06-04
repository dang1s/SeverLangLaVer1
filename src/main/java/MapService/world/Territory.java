package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.ClanTerritory;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.real.Mob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Territory extends World {
    public static HashMap<Integer, Territory> territories = new HashMap<Integer, Territory>();
    public static final int[] MAPS = {46,47};

    public ArrayList<Integer> listCharId;
    public ArrayList<Integer> listGuestId;
    public boolean started = false;
    public int level;
    private int countOpen = 0;
    private int nextMapId = -1;
    public Territory(int clanId) {
        setType(World.TERRITORY);
        this.name = "Territory";
        this.id = clanId;
        this.listCharId = new ArrayList<>();;
        this.listGuestId = new ArrayList<>();;
        this.countDown = 120; // 2 minutes
        initZone();
        initFinished = true;
        nextMapId = 46;
    }

    public void initZone() {
        for (int mapId : MAPS) {
            Map map = new Map(mapId);
            map.createWayPoint();
            ClanTerritory clanTerritory = new ClanTerritory(map,0);
            clanTerritory.MAX_CHAR_INZONE=100;
            clanTerritory.setWorld(this);
            addZone(clanTerritory);
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
            if (nextMapId != -1) {
                if(nextMapId== 46){
                    createMonterMap46();
                }else
                    openMap();
                nextTurn();
            } else {
                close();
                return;
            }
        }
    }

    private void createMonterMap46() {
        try {
            started = true;
//            List<Char>charList = getMembers();
//            int levelSum = charList.stream()
//                    .filter(c -> c!=null&&!c.isClean)
//                    .mapToInt(Char::level)
//                    .sum();
//            level = levelSum / charList.size();
            level = 56;
            zones.get(0).createMob();
            zones.get(0).isOpened=true;
        } catch (Exception e) {
            close();
        }
    }

    public void waitForNextTurn() {
        List<Char> members = getMembers();
        try {
            for (Char _char : members) {
                try {
                    if (_char != null && _char.clan != null) {
                        _char.addClanPoint(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.nextMapId = 47;
            this.countDown = 60;
            service.sendTimeInMap(countDown*10,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void nextTurn() {
        try {
            this.nextMapId = -1;
            this.countDown = 1800;
            service.sendTimeInMap(countDown,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finish() {
        countDown = 60;
        service.sendTimeInMap(countDown*10,false);
        service.serverMessage("Hành trình ai gia tộc đã kết thúc.");
        List<Char> members = getMembers();
        for (Char _char : members) {
            try {
                if (_char != null && _char.clan != null) {
                    _char.addClanPoint(20);
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
                    Map.maps[85].addChar(_char);
                    _char.service.serverMessage("Ai gia tộc đã được khép lại.");
                    _char.removeWorld(World.TERRITORY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){

        }finally {
            removeTerritory(this.id);
            super.close();
        }

    }

    public void joinZone(Char _char, int map) {
        for (Zone z : this.zones) {
            if (z.map.mapID == map) {
                try {
                    if (z != null) {
                        z.addChar(_char);
                        if(z.isOpened){
                            _char.getService().sendTimeInMap(countDown*10,true);
                        }else
                        _char.getService().sendTimeInMap(countDown*10,false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }

    public void openMap() {
        try {
            for (Zone zone : this.zones) {
                if (zone.map.mapID == 47) {
                    if (!zone.isOpened) {
                        zone.isOpened = true;
                        zone.createMob();
                        this.countDown = 3600;
                        this.started = true;
                        getService().sendTimeInMap(countDown,true);
                        getService().serverMessage("Ải 2 gia tộc đã mở.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();        }
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
    }

    public void addGuest(int charId) {
        synchronized (listGuestId) {
            this.listGuestId.add(charId);
        }
    }

    public boolean isInGuestList(int charId) {
        synchronized (listGuestId) {
            for (int id : this.listGuestId) {
                if (id == charId) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean isInTerritory(int charId) {
        synchronized (listCharId) {
            for (int id : this.listCharId) {
                if (id == charId) {
                    return true;
                }
            }
            return false;
        }
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static void addTerritory(int clanId, Territory territory) {
        territories.put(clanId, territory);
    }

    public static Territory getTerritory(int clanId) {
        return territories.get(clanId);
    }

    public static void removeTerritory(int clanId) {
        territories.remove(clanId);
    }

    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return !pre.isDungeoClan() && next.isDungeoClan();
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return pre.isDungeoClan() && !next.isDungeoClan();
    }
}
