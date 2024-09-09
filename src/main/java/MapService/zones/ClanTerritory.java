package MapService.zones;

import MapService.Map;
import MapService.Zone;
import MapService.world.Territory;
import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.*;
import com.sg188.server.lib.Writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClanTerritory extends ZWorld {
    private int level;
    private int[] listitem = {7, 7, 7, 8, 8, 8, 8,8,8,8,8,8,8,8,8,8,8,8,932,932,932};
    private boolean createBoss;

    public ClanTerritory(Map map, int id) {
        super(map, id);
    }
    @Override
    public void createMob(){
        monsters.clear();
        Territory territory = (Territory) world;
        level = territory.level;
        List<XYEntity> entityList = new ArrayList<>();
        int x = 60;
        if(map.mapID == 46) {
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (163 + x), (short) 734));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 34; i++) {
                entityList.add(new XYEntity((short) (600 + x), (short) 800));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 36; i++) {
                entityList.add(new XYEntity((short) (463 + x), (short) 363));
                x += 60;
            }
            entityList.add(new XYEntity((short) 2755, (short) 628));
            entityList.add(new XYEntity((short) 2809, (short) 628));
            entityList.add(new XYEntity((short) 2857, (short) 628));
            entityList.add(new XYEntity((short) 2911, (short) 648));
            entityList.add(new XYEntity((short) 2959, (short) 648));
            entityList.add(new XYEntity((short) 2995, (short) 648));
            x = 60;
            for (int i = 0; i < 5; i++) {
                entityList.add(new XYEntity((short) (3079 + x), (short) 721));
                x += 60;
            }
            for (int i = 0; i < entityList.size(); i++) {
                Mob mob = new Mob();
                mob.id = 126;
                mob.exp = level * 22470;
                mob.level = level;
                mob.cx = entityList.get(i).cx;
                mob.cy = entityList.get(i).cy;
                mob.status = 4;
                mob.hpGoc = mob.hp = mob.hpFull = level * 174800;
                mob.expGoc = 100000;
                mob.paintMiniMap = false;

                mob.levelBoss = 0;
                mob.idEntity = i;
                monsters.add(mob);
                mob.reSpawn(this);
                world.getService().sendMessage(HanderMessage.AddMob(mob));
            }
        }else {
            for (int i = 0; i < 20; i++) {
                entityList.add(new XYEntity((short) (163 + x), (short) 600));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 20; i++) {
                entityList.add(new XYEntity((short) (163 + x), (short) 873));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 10; i++) {
                entityList.add(new XYEntity((short) (2154 + x), (short) 771));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 20; i++) {
                entityList.add(new XYEntity((short) (96 + x), (short) 302));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 10; i++) {
                entityList.add(new XYEntity((short) (2154 + x), (short) 372));
                x += 60;
            }
            for (int i = 0; i < entityList.size(); i++) {
                Mob mob = new Mob();
                mob.id = 127;
                mob.exp = level * 23540;
                mob.level = level;
                mob.cx = entityList.get(i).cx;
                mob.cy = entityList.get(i).cy;
                mob.status = 4;
                mob.hpGoc = mob.hp = mob.hpFull = level * 184000;
                mob.expGoc = 100000;

                mob.levelBoss = 0;
                mob.paintMiniMap = false;
                mob.idEntity = i;
                monsters.add(mob);
                mob.reSpawn(this);
            }
        }
    }

    @Override
    public void nextMap(Char player) {
        boolean b = false;
        XYEntity xy = player.Info;
        WayPoint waypoint_next = map.getWayPoint(xy);
        if (waypoint_next != null) {
           int nextID = waypoint_next.mapNext;
            Zone z = world.find(nextID);
            if (z != null) {
                if (z.isOpened) {
                    Territory territory = (Territory) world;
                    territory.joinZone(player,47);
                    return;
                }
            }
        }
        if (!b) {
            player.service.serverMessage("Cửa này chưa mở bạn ơi");
            player.Info.backXY();
            player.service.setXYChar();
            return;
        }
    }
    @Override
    protected boolean canRespawn(Mob mob) {
        // Điều kiện mới cho việc hồi sinh, ví dụ:
        return false; // Luôn chặn việc hồi sinh
    }
    @Override
    public void setMobDie(Char player, Mob mob) {
        super.setMobDie(player, mob);
            if(getLivingMonstersClan().size() == 0){
                Territory territory = (Territory) world;
                if(map.mapID==46) {
                    territory.waitForNextTurn();
                }else {
                    createBoss();
                }
            }
            if(mob.id == 112){
                Territory territory = (Territory) world;
                territory.finish();
                List<Char> member = getChars();
                for (int i = 0; i < listitem.length; i++) {
                    Item item = new Item(listitem[i]);
                    ItemMap itemMap = new ItemMap((short) id_ENTITY_ITEM_MAP++);
                    itemMap.setOwnerID(-1);
                    itemMap.setItem(item);
                    this.addItemMap(itemMap);
                    Writer writer = new Writer();
                    try {
                        writer.writeShort(mob.idEntity);
                        itemMap.write(writer, mob.cx+Utlis.nextInt(0,200), mob.cy, this);
                        for (Char pl : member) {
                            if(pl!=null&&pl.user!=null&&!pl.isClean)
                            pl.service.sendItemDropFormMob(writer);
                        }
                    } catch (IOException e) {
                    }
                }
            }
    }
    @Override
    public void mobAttackChar(Mob mob, Char player) {
        if(mob.id==112){
            if(!player.isBiDuoc){
                player.addEffect(new Effect((short) 12, 0, System.currentTimeMillis(), 2000));
            }
        }
        super.mobAttackChar(mob,player);
    }

    private void createBoss() {
        if (!createBoss) {
            createBoss=true;
            List<XYEntity> entityList = new ArrayList<>();
            int x = 60;
            for (int i = 0; i < 10; i++) {
                entityList.add(new XYEntity((short) (2154 + x), (short) 372));
                x += 60;
            }
            for (int i = 0; i < entityList.size(); i++) {
                Mob mob = new Mob();
                mob.id = 130;
                mob.exp = 1;
                mob.level = level;
                mob.cx = entityList.get(i).cx;
                mob.cy = entityList.get(i).cy;
                mob.status = 4;
                mob.hpGoc = mob.hp = mob.hpFull = 1;
                mob.expGoc = 0;
                mob.paintMiniMap = false;
                mob.idEntity = monsters.size();
                monsters.add(mob);
                mob.reSpawn(this);
                world.getService().sendMessage(HanderMessage.AddMob(mob));
            }
            Mob boss = new Mob();
            boss.id = 112;
            boss.level = level;
            boss.cx = 2570;
            boss.levelBoss = 10;
            boss.cy = 724;
            boss.status = 2;
            boss.hpGoc = boss.hp = boss.hpFull = 1999999999;
            boss.exp = 10000000;
            boss.expGoc = 0;
            boss.paintMiniMap = false;
            boss.he = Utlis.nextInt(1, 5);
            boss.idEntity = monsters.size();
            monsters.add(boss);
            List<Char> member = getChars();
            for (Char pl : member) {
                pl.Info.cx = boss.cx;
                pl.Info.cy = boss.cy;
                pl.service.setXYChar();
                pl.getService().sendMessage(HanderMessage.AddMob(boss));
            }
        }
    }
}
