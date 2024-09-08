package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.ZWorld;
import MapService.zones.ZoneCamThuat;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class CamThuat extends World {

    private ArrayList<Integer> listCharId;
    private int level;


    public static List<CamThuat> camThuats = new ArrayList<>();

    public static void addCamThuat(CamThuat dungeon) {
        synchronized (camThuats) {
            camThuats.add(dungeon);
        }
    }
    public static void removeCamThuat(CamThuat dungeon) {
        synchronized (camThuats) {
            camThuats.remove(dungeon);
        }
    }

    public static CamThuat findCamThuatById(int id) {
        synchronized (camThuats) {
            for (CamThuat dun : camThuats) {
                if (dun.id == id) {
                    return dun;
                }
            }
        }
        return null;
    }
    public CamThuat(int countDown,int level) {
        setType(World.CamThuat);
        this.name = "CamThuat";
        generateId();
        this.listCharId = new ArrayList<>();
        this.countDown = countDown;
        this.level= level;
        initZone();
        initFinished = true;
    }
    public void addCharId(int id) {
        synchronized (listCharId) {
            if (listCharId.indexOf(id) == -1) {
                listCharId.add(id);
            }
        }
    }

    public boolean isInSevenBeasts(int id) {
        synchronized (listCharId) {
            return listCharId.indexOf(id) != -1;
        }
    }

    public void initZone() {
        Map map = Map.maps[89];
        ZWorld waitingArea = new ZoneCamThuat(map,0,level,this);
        waitingArea.setWorld(this);
        addZone(waitingArea);
    }

    public void join(Char p) {
        zones.get(0).addChar(p);
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
                    _char.idCamThuat = -1;
                    _char.removeWorld(World.CamThuat);
                } catch (Exception e) {
                    Log.error("player leave map err", e);
                }
            }
        }catch (Exception e){
            Log.error("loi close cam thuat roi");
        }finally {
            MapService.world.CamThuat.removeCamThuat(this);
            super.close();
        }

    }
    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return pre.map.mapID!= 89&&next.map.mapID == 89;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return pre.map.mapID == 89&&next.map.mapID != 89;
    }
}
