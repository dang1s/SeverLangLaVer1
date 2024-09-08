package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.ZoneTranhDoatLanhDia;
import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.real.Char;

import java.util.ArrayList;
import java.util.List;

public class TranhDoatLanhTho extends World{
   public static TranhDoatLanhTho tranhDoatLanhTho3x;
   public static TranhDoatLanhTho tranhDoatLanhTho4x;
   public static TranhDoatLanhTho tranhDoatLanhTho5x;
   public static TranhDoatLanhTho tranhDoatLanhTho6x;
   public Zone zone;
   public long timestart;

    public TranhDoatLanhTho(int countDown,int level) {
        setType(World.TRANH_DOAT_LANH_THO);
        this.name = "TranhDoatLanhTho";
        generateId();
        this.countDown = countDown;
        ZoneTranhDoatLanhDia z = new ZoneTranhDoatLanhDia(new Map(58),0);
        z.level = level;
        z.createMob();
        z.setWorld(this);
        zone= z;
        zones.add(z);
        timestart=System.currentTimeMillis();
        initFinished = true;
    }
    public void join(Char p) {
        zone.addChar(p);
        p.getService().sendTimeInMap(getCountDown(),true,timestart);
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
                    _char.removeWorld(World.DAI_HOI_VO_THUAT);
                } catch (Exception e) {
                    Log.error("player leave map err", e);
                }
            }
        }catch (Exception e){
            Log.error("loi close tranh doat lanh tho roi");
        }finally {
            TranhDoatLanhTho.tranhDoatLanhTho3x = null;
            TranhDoatLanhTho.tranhDoatLanhTho4x = null;
            TranhDoatLanhTho.tranhDoatLanhTho5x = null;
            TranhDoatLanhTho.tranhDoatLanhTho6x = null;
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
