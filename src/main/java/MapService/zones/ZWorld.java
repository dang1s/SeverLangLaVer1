package MapService.zones;

import MapService.Map;
import MapService.Zone;
import MapService.world.World;
import com.sg188.real.Char;
import com.sg188.real.Mob;
import lombok.Getter;
import lombok.Setter;

public class ZWorld extends Zone {

    @Getter
    @Setter
    protected World world;

    public ZWorld(Map map,int id) {
        super(map, id);
    }

    @Override
    public boolean addChar(Char p) {
        Zone preZone = p.zone;
        if (preZone != null) {
            if (!preZone.isWorld()) {
                p.addMemberForWorld(preZone, this);
            }
        }
        super.addChar(p);
        if(world.getType() == World.CamThuat) {
            p.service.sendTimeInMap(world.getCountDown(),false);
        }else {
            p.service.sendTimeInMap(world.getCountDown()*10,false);
        }
        return true;
    }
    @Override
    public void setMobDie(Char player, Mob mob) {
        super.setMobDie(player,mob);
    }

}
