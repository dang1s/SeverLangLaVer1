package MapService.zones;

import MapService.Map;
import Service.HanderCharacter;
import Service.HanderMessage;
import com.sg188.clan.Clan;
import com.sg188.real.Char;
import com.sg188.real.Mob;
import com.sg188.real.XYEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ZoneTranhDoatLanhDia extends ZWorld{
    public long timeChangePK;
    public int level;

    public ZoneTranhDoatLanhDia(Map map, int id) {
        super(map, id);
        timeChangePK=System.currentTimeMillis()+ TimeUnit.MINUTES.toMillis(3);
        timeReviveMob=10000;
    }
    @Override
    public void createMob(){
        monsters.clear();
        List<XYEntity> entityList = new ArrayList<>();
        int l = 60;
        for (int i = 0; i < 7; i++) {
            entityList.add(new XYEntity((short) (19+l), (short) 677));
            l+=60;
        }
        l = 60;
        for (int i = 0; i < 5; i++) {
            entityList.add(new XYEntity((short) (55+l), (short) 378));
            l+=60;
        }
        l = 60;
        for (int i = 0; i < 10; i++) {
            entityList.add(new XYEntity((short) (289+l), (short) 210));
            l+=60;
        }
        l = 60;
        for (int i = 0; i < 7; i++) {
            entityList.add(new XYEntity((short) (895+l), (short) 379));
            l+=60;
        }
        l = 60;
        for (int i = 0; i < 4; i++) {
            entityList.add(new XYEntity((short) (583+l), (short) 542));
            l+=60;
        }
        for (int i = 0; i < entityList.size(); i++) {
            Mob mob = new Mob();
            mob.id = 97;
            mob.exp = level * 100;
            mob.level = level;
            mob.cx = entityList.get(i).cx;
            mob.cy = entityList.get(i).cy;
            mob.status = 4;
            mob.hpGoc = mob.hp = mob.hpFull = level * 400;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.idEntity = i;
            monsters.add(mob);
            mob.reSpawn(this);
        }
        Mob mob = new Mob();
        mob.id = level==35?251:level==45?252:level==55?253:254;
        mob.exp = level * 100000;
        mob.level = level;
        mob.cx = 703;
        mob.cy = 210;
        mob.status = 10;
        mob.hpGoc = mob.hp = mob.hpFull = level * 250000;
        mob.expGoc = mob.hpGoc / 8;
        mob.paintMiniMap = false;
        mob.idEntity = monsters.size();
        monsters.add(mob);
        mob.reSpawn(this);
    }
    @Override
    public void update(){
        updateMob();
        updateItemMap();
        updatePlayer();
        refresh();
    }
    @Override
    public void setMobDie(Char player, Mob mob) {
        super.setMobDie(player,mob);
        player.pointTranhDoat+=2;
        player.getService().sendPointMap(player.pointTranhDoat);
    }
    public void refresh(){
        if(timeChangePK < System.currentTimeMillis()){
            timeChangePK= System.currentTimeMillis()+ TimeUnit.MINUTES.toMillis(3);
            List<Char>charList = getChars();
            for (Char pl : charList){
                if(pl!=null&&pl.user!=null&&!pl.isClean){
                    HanderCharacter.SetTypePk(pl, (byte) 2);
                }
            }
        }
    }
}
