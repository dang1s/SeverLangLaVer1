package MapService.zones;

import MapService.Map;
import MapService.Zone;
import MapService.world.DaiHoiVoThuat;
import Service.HanderMessage;
import com.sg188.real.Char;

public class ZoneDaihoi extends ZWorld{
    public ZoneDaihoi(Map map, int id) {
        super(map, id);
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
        p.service.sendMessage(HanderMessage.SendTypePk(p.id, (byte) 0));
    }
    @Override
    public void changeZone(Char player, byte zoneNext) {
        if (zoneNext == this.zoneID) {
            return;
        }
        DaiHoiVoThuat daiHoiVoThuat = (DaiHoiVoThuat) world;
//        if(daiHoiVoThuat.groupStage||daiHoiVoThuat.qualifierRound||daiHoiVoThuat.semiFinals||daiHoiVoThuat.finalRound)
//            return;
        if(DaiHoiVoThuat.gI().qualifierRound||DaiHoiVoThuat.gI().semiFinals||DaiHoiVoThuat.gI().finalRound){
            player.setXY((short) 410, (short) 566);
            player.InfoGame.TypePk = 1;
            //player.service.setXYChar();
            //return;
        }
        if (zoneNext >= 0 && zoneNext < map.getZones().size()) {
            Zone z = map.getZones().get(zoneNext);
            if (z.players.size() < z.MAX_CHAR_INZONE) {
                z.addChar(player);
            } else {

                player.service.setXYChar();
            }
        } else {
            player.service.setXYChar();
        }
    }
    @Override
    public void update(){
        updateMob();
        updatePlayer();
        updateItemMap();
    }
}
