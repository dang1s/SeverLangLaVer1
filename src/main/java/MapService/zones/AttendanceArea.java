package MapService.zones;

import MapService.Map;
import com.sg188.real.Char;
import com.sg188.real.Npc;

public class AttendanceArea extends ZWorld {

    public AttendanceArea(int id,Map map) {
        super(map,id);
        createNpc();
    }
    @Override
    public void createNpc() {
        npcs.clear();
        Npc npc = new Npc();
        npc.id = 98;
        npc.cy = 450;
        npc.cx = 494;
        npc.status =0;
        npc.idEntity =0;
        npcs.add(npc);
    }

    @Override
    public boolean addChar(Char p) {
        super.addChar(p);
        p.Point.hp = p.maxHP;
        p.InfoGame.isDie = false;
        p.msgGetInfo();
        return true;
//        p.setTypePk(Char.PK_NORMAL);
    }

    @Override
    public void removeChar(Char p) {
        super.removeChar(p);
        p.Point.hp = p.maxHP;
        p.InfoGame.isDie = false;
        p.msgGetInfo();
//        p.setTypePk(Char.PK_NORMAL);
    }

}
