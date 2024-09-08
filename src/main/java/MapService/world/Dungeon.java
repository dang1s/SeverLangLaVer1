package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.*;
import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.real.Char;

import java.util.ArrayList;
import java.util.List;

public class Dungeon extends World {
    public static final int MAP_DUNGEON_SO = 6;
    public static final int MAP_DUNGEON_TRUNG = 7;
    public static final int MAP_DUNGEON_CAO = 18;
    public static List<Dungeon> dungeons = new ArrayList<>();

    public static void addDungeon(Dungeon dungeon) {
        synchronized (dungeons) {
            dungeons.add(dungeon);
        }
    }
    public static void removeDungeon(Dungeon dungeon) {
        synchronized (dungeons) {
            dungeons.remove(dungeon);
        }
    }

    public static Dungeon findDungeonById(int id) {
        synchronized (dungeons) {
            for (Dungeon dun : dungeons) {
                if (dun.id == id) {
                    return dun;
                }
            }
        }
        return null;
    }

    public ArrayList<Integer> listCharId;
    public int timeFinish = 0;
    public int level;
    public long timeCreate;
    public int index = 0;
    public int levelMonster = 0;
    public int time;
    public boolean bossAppeared = false;
    private boolean finished;

    public Dungeon(int level, int time) {
        setType(World.DUNGEON);
        this.name = "Dungeon";
        generateId();
        this.listCharId = new ArrayList<>();
        this.countDown = time;
        this.time = time;
        this.level = level;
        open();
        this.timeCreate = System.currentTimeMillis();
        initFinished = true;
    }
    public void join(Char p) {
        zones.get(0).addChar(p);
        short cx;
        short cy;
        if(level < 30){
            cx = 64;
            cy = 360;
        } else if (level >= 30 && level < 50) {
            cx = 68;
            cy= 174;
        }else {
            cx = 52;
            cy = 158;
        }
        p.Info.cx= cx;
        p.Info.cy= cy;
        p.service.setXYChar();
    }

    public void open() {
        if(level >= 15&&level <= 29){
            Map map = new Map(MAP_DUNGEON_SO);
            ZWorld daicung = new DiaCungSo(map,0,level,this);
            daicung.setWorld(this);
            addZone(daicung);
        }else if(level >= 30&&level <= 49){
            Map map = new Map(MAP_DUNGEON_TRUNG);
            ZWorld daicung = new DiaCungTrung(map,0,level,this);
            daicung.setWorld(this);
            addZone(daicung);
        }else if (level > 49){
            Map map = new Map(MAP_DUNGEON_CAO);
            ZWorld daicung = new DiaCungCao(map,0,level,this);
            daicung.setWorld(this);
            addZone(daicung);
        }
    }
    public void close() {
        try {
            if (this.isClosed) {
                return;
            }
            List<Char> members = getMembers();
            for (Char _char : members) {
                try {
                    if (_char.isClean) {
                        continue;
                    }
                    Map.maps[_char.Info.mapReSpawm].addChar(_char);
                    _char.idDiaCung = -1;
                    _char.removeWorld(World.DUNGEON);
                } catch (Exception e) {
                    Log.error("player leave map err", e);
                }
            }
        }catch (Exception e){
            Log.error("loi close dungeon roi.......");
        }finally {
            Dungeon.removeDungeon(this);
            super.close();
        }

    }
    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return pre.map.getMapTemplate().type != 4&&next.map.getMapTemplate().type == 4;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return pre.map.getMapTemplate().type == 4&&next.map.getMapTemplate().type != 4;
    }

}
