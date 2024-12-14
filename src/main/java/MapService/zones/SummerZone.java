package MapService.zones;

import MapService.Map;
import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.*;

import java.util.ArrayList;
import java.util.List;

public class SummerZone extends ZWorld{
    public int level;
    public boolean isCreateBoss;
    private boolean endDungeon;
    private Mob boss;
    private Mob boss2;
    private boolean isPanDie;
    private boolean isKonanDie;

    public SummerZone(Map map, int id) {
        super(map, id);
    }
    @Override
    public void createMob(){
        monsters.clear();
        int[] idmob ={198,192,186,189,190,191};
        List<XYEntity> entityList = new ArrayList<>();
        int x = 60;
            for (int i = 0; i < 10; i++) {
                entityList.add(new XYEntity((short) (241 + x), (short) 192));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 11; i++) {
                entityList.add(new XYEntity((short) (187 + x), (short) 340));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 14; i++) {
                entityList.add(new XYEntity((short) (109 + x), (short) 501));
                x += 60;
            }
            x = 60;
            for (int i = 0; i < 14; i++) {
                entityList.add(new XYEntity((short) (187 + x), (short) 649));
                x += 60;
            }
        for (int i = 0; i < entityList.size(); i++) {
            Mob mob = new Mob();
            mob.id = idmob[Utlis.nextInt(0,idmob.length-1)];
            mob.exp = level * mob.id;
            mob.level = level;
            mob.cx = entityList.get(i).cx;
            mob.cy = entityList.get(i).cy;
            mob.status = 4;
            mob.hpGoc = mob.hp = mob.hpFull = level * mob.id*500;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.idEntity = i;
            monsters.add(mob);
            mob.reSpawn(this);
        }
        Mob mob = new Mob();
        mob.id = 81;
        mob.level = level;
        mob.cx = 819;
        mob.levelBoss = 10;
        mob.cy = 792;
        mob.status = 2;
        mob.hpGoc = mob.hp = mob.hpFull = level * mob.id * 5000;
        mob.expGoc = mob.hpGoc / 8;
        mob.paintMiniMap = false;
        mob.idEntity = monsters.size();
        mob.reSpawn(this);
        boss = mob;
        Mob mob2 = new Mob();
        mob2.id = 203;
        mob2.level = level;
        mob2.cx = 750;
        mob2.levelBoss = 10;
        mob2.cy = 792;
        mob2.status = 2;
        mob2.hpGoc = mob2.hp = mob2.hpFull = level * mob2.id * 5000;
        mob2.expGoc = mob2.hpGoc / 8;
        mob2.paintMiniMap = false;
        mob2.idEntity = monsters.size()+1;
        mob2.reSpawn(this);
        boss2 = mob2;
    }
    @Override
    public void createNpc() {
        npcs.clear();
        Npc npc = new Npc();
        npc.id = 31;
        npc.cx=154;
        npc.cy=192;
        npc.status = 0;
        npcs.add(npc);

    }
    @Override
    protected boolean canRespawn(Mob mob) {
        return false; // Mặc định cho phép hồi sinh
    }
    @Override
    public void update(){
        super.update();
        refresh();
    }
    public void refresh() {
        if (getLivingMonsters().size() == 0) {
            if (!isCreateBoss) {
                isCreateBoss = true;
                monsters.add(boss);
                monsters.add(boss2);
                try {
                    List<Char> charList = getChars();
                    for (Char pl : charList) {
                        if (pl != null && pl.user != null && !pl.isClean) {
                            pl.getService().sendMessage(HanderMessage.AddMob(boss));
                            pl.getService().sendMessage(HanderMessage.AddMob(boss2));
                            pl.Info.cx = boss.cx;
                            pl.Info.cy = boss.cy;
                            pl.getService().setXYChar();
                        }
                    }
                } catch (Exception e) {

                }
                reSpawnMobToAllChar(boss);
                reSpawnMobToAllChar(boss2);
            }
        }
    }
    @Override
    public void setMobDie(Char p,Mob mob){
        super.setMobDie(p,mob);
        if(mob.id== 81){
            isPanDie = true;
        }
        if(mob.id== 203){
            isKonanDie = true;
        }
        if(isPanDie&&isKonanDie){
            isPanDie = false;
            isKonanDie = false;
            world.setCountdown(10);
            try {
                List<Char> charList = getChars();
                for (Char pl:charList){
                    if(pl!=null&&pl.user!=null&&!pl.isClean){
                        pl.Info.cx = 154;
                        pl.Info.cy=192;
                        pl.getService().setXYChar();
                        pl.getService().npcChat(0,"Cảm ơn các con đã cứu ta , phần quà đã được gửi vào túi đồ");
                        //Item item = new Item(930);
                        Item item = new Item(795);
                        item.amount = 50;
                        pl.addItem(item);
                        pl.addClanPoint(50);
                        pl.msgAddItemBag(item);
                        pl.getService().sendTimeInMap(world.getCountDown()*10, false);
                        pl.getService().sendMessage(HanderMessage.RemoveMob(mob.idEntity));
                    }
                }
            } catch (Exception e) {

            }
        }
    }
}
