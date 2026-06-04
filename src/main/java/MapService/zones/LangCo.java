package MapService.zones;

import InfoChar.InfoPoint;
import MapService.Map;
import MapService.Zone;
import MapService.world.MapLangCo;
import MapService.world.SonCapMyo;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.*;
import com.sg188.server.lib.Writer;

import java.util.ArrayList;
import java.util.List;

public class LangCo extends Zone {
    private int mobid;
    private byte level = 70;

    public LangCo(Map map, int zone) {
        super(map, zone);
        Log.info("=== LangCo khoi tao | mapID=" + map.mapID + " | zone=" + zone + " ===");
        isLangCo = true;
        timeReviveMob=5000;
    }
    @Override
    public boolean addChar(Char player) {
        super.addChar(player);
        player.Info.cx= 2318;
        player.Info.cy= 421;
        player.service.setXYChar();
        player.inLangCo = true;
//        if(player.Info.lvPk < 0){
//            Map.maps[player.Info.mapReSpawm].addChar(player);
//            player.getService().serverMessage("Bạn đang có điểm PK không thể vào khu vực này");
//        }
        return true;
    }
    @Override
    public void createMob(){
        monsters.clear();
        int[] idmob ={291,291,291};
        List<XYEntity> entityList = new ArrayList<>();
        int x = 60;
        if (map.mapID==98) {
            mobid = 98;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (0 + x), (short) 364));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 13; i++) {
                entityList.add(new XYEntity((short) (200 + x), (short) 364));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 22; i++) {
                entityList.add(new XYEntity((short) (960 + x), (short) 364));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 6; i++) {
                entityList.add(new XYEntity((short) (1800 + x), (short) 364));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (2000 + x), (short) 364));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (2500 + x), (short) 364));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 7; i++) {
                entityList.add(new XYEntity((short) (3000 + x), (short) 364));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 35; i++) {
                entityList.add(new XYEntity((short) (0 + x), (short) 364));
                x += 60;
            }
        }
        for (int i = 0; i < entityList.size(); i++) {
            Mob mob = new Mob();
            mob.id = idmob[Utlis.nextInt(0,idmob.length-1)];
            mob.exp = level * mobid*10;
            mob.level = level;
            mob.cx = entityList.get(i).cx;
            mob.cy = entityList.get(i).cy;
            mob.status = 4;
            mob.hpGoc = mob.hp = mob.hpFull = level * mobid*850/8;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.idEntity = i;
            monsters.add(mob);
            mob.reSpawn(this);
        }
    }
    //    @Override
//    public void nextMap(Char player) {
//        MapLangCo.gI().maps.get(0).addChar(player);
//    }
    @Override
    public void createNpc() {
        npcs.clear();
        for (int i = 0; i < map.getMapTemplate().listNpc.size(); i++) {
            try {
                Npc npc1 = map.getMapTemplate().listNpc.get(i);
                if(npc1.id!=93)
                    continue;
                Npc npc2 = npc1.cloneNpc();
                npc2.idEntity = i;
                npcs.add(npc2);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    @Override
    public void getRewardMob(Char player, Mob mob) {
        if(Utlis.nextInt(1,100)< 20) {
            int bac = Utlis.nextInt(5000, 7000) * 5;
            if (player.getEffect(86) != null) {
                bac += bac;
            }
            player.addBacKhoa(bac);
        }
        int itemDrop = ItemDrop.ITEM_LANG_CO.next();
        if (itemDrop == -1) {
            return;
        }
        Item item = new Item(itemDrop);
        item.amount = 1;
//                    item.isLock = true;
        ItemMap itemMap = new ItemMap((short) id_ENTITY_ITEM_MAP++);
        itemMap.setY((short) mob.cy);
        itemMap.setX((short) mob.cx);
        itemMap.setOwnerID(player.id);
        itemMap.setItem(item);
        this.addItemMap(itemMap);
        sendItemMap(mob,itemMap);
    }
    @Override
    public void mobAttackChar(Mob mob, Char player) {
        try {
            int dameAdjusted = mob.getDame()*5;// sua dame o day
            int khang = InfoPoint.getKhangByClass(player, mob.he);
            dameAdjusted -= dameAdjusted * InfoPoint.calculateKhang(khang) / 100;
            dameAdjusted -= player.damageReduction;
            dameAdjusted = Math.max(dameAdjusted, 1);
            if (player.mpHutDame > 0) {
                int damehut = dameAdjusted * player.mpHutDame / 100;
                player.addMp(-damehut);
                player.addHp(-(dameAdjusted - damehut));
                player.msgUpdateMp();
            } else
                player.addHp(-dameAdjusted);//mob.getDame();
            Writer writer = new Writer();
            writer.writeShort(mob.idEntity);
            writer.writeInt(player.Info.idEntity);
            for (int i = getChars().size() - 1; i >= 0; i--) {
                try {
                    Char pl = getChars().get(i);
                    if (pl != null && pl.user != null && pl.service != null)
                        pl.service.mobAttackChar(writer);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            player.msgUpdateHpMpWhenAttack(false, mob.getMobTemplate().name);
        } catch (Exception ex) {
            Log.error("Loi quai danh nguoi " + ex);
        }
    }
}
