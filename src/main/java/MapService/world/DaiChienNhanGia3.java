package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.ZDaiChien3;
import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
public class DaiChienNhanGia3 extends World{
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
    public static DaiChienNhanGia3 BanDoanhLangLa;
    public static DaiChienNhanGia3 BanDoanhLangDa;

    public boolean createMob;
    public DaiChienNhanGia3(int countDown,int level) {
        setType(World.DAI_CHIEN_NHAN_GIA_3);
        this.name = "DaiChienNhanGia3";
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
                zones.get(2).createMob();
            }
        }
    }
    public void initZone() {
        Map map = new Map(42);
        addZone(new ZDaiChien3(map, 0,level,this));
        zones.get(0).MAX_CHAR_INZONE = 100;
        map = new Map(43);
        addZone(new ZDaiChien3(map, 0,level,this));
        zones.get(1).MAX_CHAR_INZONE = 100;
        map = new Map(41);
        addZone(new ZDaiChien3(map, 0,level,this));
        zones.get(2).MAX_CHAR_INZONE = 100;
    }
    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return false;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return false;
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
                    _char.removeWorld(World.DAI_CHIEN_NHAN_GIA_3);
                } catch (Exception e) {
                    Log.error("player leave map err", e);
                }
            }
        }catch (Exception e){
            Log.error("loi close deadforest roi");
        }finally {
            DaiChienNhanGia3.BanDoanhLangLa = null;
            DaiChienNhanGia3.BanDoanhLangDa = null;
            super.close();
        }
    }

    public void join(Char p) {
        zones.get(0).addChar(p);
        getService().sendMessage(HanderMessage.SendTypePk(p.id, (byte) 2));
        getService().setTypePKALLMap();
    }
}
