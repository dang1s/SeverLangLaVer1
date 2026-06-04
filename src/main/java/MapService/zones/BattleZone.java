package MapService.zones;

import MapService.Map;
import MapService.world.Arena;
import Service.HanderMessage;
import com.mongodb.lang.Nullable;
import com.sg188.real.Char;

public class BattleZone extends ZWorld {

    public BattleZone(int id, Map map) {
        super(map,id);
    }

    @Override
    public boolean addChar(Char p) {
        super.addChar(p);
        p.Point.hp = p.maxHP;
        p.InfoGame.isDie = false;
        p.msgGetInfo();
        return true;
    }

    @Override
    public void removeChar(Char p) {
        super.removeChar(p);
        p.Point.hp = p.maxHP;
        p.InfoGame.isDie = false;
        p.msgGetInfo();
    }
    @Override
    public void update(){
        super.update();
        super.updatePlayer();
    }


}
