package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.ZDeadForest;
import MapService.zones.ZDeadForest2;
import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DeadForest extends World{
    @Setter
    @Getter
    private int maxPlayer;
    @Setter
    @Getter
    private boolean opened;
    @Setter
    @Getter
    private int level;
    private ArrayList<Integer> listCharId;
    public static DeadForest DeadForest_2x;
    public static DeadForest DeadForest_3x;
    public static DeadForest DeadForest_4x;
    public static DeadForest DeadForest_5x;
    public static DeadForest DeadForest_6x;
    public boolean createMob;




    public DeadForest(int countDown,int level) {
        setType(World.DeadForest);
        this.name = "DeadForest";
        this.level = level;
        generateId();
        this.listCharId = new ArrayList<>();
        this.countDown = countDown;
        initZone();
        initFinished = true;
        opened = true;
    }

    public int getNumberPlayer() {
        synchronized (members) {
            return members.size();
        }
    }
    public void addCharId(int id) {
        synchronized (listCharId) {
            if (listCharId.indexOf(id) == -1) {
                listCharId.add(id);
            }
        }
    }
    public boolean findCharId(int id) {
        synchronized (listCharId) {
            for(int ID: listCharId){
                if(ID ==id){
                    return true;
                }
            }
        }
        return false;
    }

    public void initZone() {
        Map map = new Map(17);
        addZone(new ZDeadForest(map, 0,level,this));
        zones.get(0).MAX_CHAR_INZONE = 100;
        map = new Map(31);
        addZone(new ZDeadForest2(map, 0,level,this));
        zones.get(1).MAX_CHAR_INZONE = 100;
    }
    @Override
    public void update() {
        zones.forEach(z -> {
            if (z != null&&!isClosed&& !z.isClosed) {
                z.update();
                z.updatePlayer();
            }
        });
        if (countDown > 0) {
            countDown--;
            if (countDown <= 0) {
                if(!createMob){
                    zones.get(0).createMob();
                }else {
                    close();
                    return;
                }
            }
        }
    }


    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return pre.map.getMapTemplate().type != 5&&next.map.getMapTemplate().type == 5;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return pre.map.getMapTemplate().type == 5&&next.map.getMapTemplate().type != 5;
    }
    public void close() {
        try{
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
                    _char.removeWorld(World.DeadForest);
                } catch (Exception e) {
                    Log.error("player leave map err", e);
                }
            }
        }catch (Exception e){
            Log.error("loi close deadforest roi");
        }finally {
            MapService.world.DeadForest.DeadForest_2x = null;
            MapService.world.DeadForest.DeadForest_3x = null;
            MapService.world.DeadForest.DeadForest_4x = null;
            MapService.world.DeadForest.DeadForest_5x = null;
            MapService.world.DeadForest.DeadForest_6x = null;
            super.close();
        }
    }

    public void join(Char p) {
        zones.get(0).addChar(p);
        getService().sendMessage(HanderMessage.SendTypePk(p.id, (byte) 2));
        getService().setTypePKALLMap();
    }

}
