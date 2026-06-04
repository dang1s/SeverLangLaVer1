package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.SummerZone;
import com.sg188.lib.Log;
import com.sg188.real.Char;

import java.util.ArrayList;
import java.util.List;

public class SummerEvent extends World{
    public static List<SummerEvent> summerEventList = new ArrayList<>();
    public static void addDungeon(SummerEvent dungeon) {
        synchronized (summerEventList) {
            summerEventList.add(dungeon);
        }
    }
    public static void removeDungeon(SummerEvent dungeon) {
        synchronized (summerEventList) {
            summerEventList.remove(dungeon);
        }
    }

    public static SummerEvent findSummerEventById(int id) {
        synchronized (summerEventList) {
            for (SummerEvent dun : summerEventList) {
                if (dun.id == id) {
                    return dun;
                }
            }
        }
        return null;
    }


    public SummerEvent(int level) {
        setType(World.EVENT);
        this.name = "event";
        generateId();
        SummerZone zone = new SummerZone(new Map(67),0);
        zone.setWorld(this);
        zone.level=level;
        zone.createNpc();
        zone.createMob();
        zones.add(zone);
        this.countDown = 3600;
        initFinished = true;
    }
    public void generateId() {
        this.id = number++;
    }
    public void join(Char p) {
        zones.get(0).addChar(p);
        p.Info.cx= 197;
        p.Info.cy= 192;
        p.service.setXYChar();
        p.getService().npcChat(0,"ĐM bọn nó đông quá. Các con đấm nó giúp thầy!");
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
                    _char.idDungeonEvent = -1;
                    _char.removeWorld(World.EVENT);
                } catch (Exception e) {
                    Log.error("player leave map err", e);
                }
            }
        }catch (Exception e){
            Log.error("loi close dungeon roi.......");
        }finally {
            SummerEvent.removeDungeon(this);
            super.close();
        }

    }


    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return false;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return false;
    }
}
