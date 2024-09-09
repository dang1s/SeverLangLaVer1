package MapService.zones;

import MapService.Map;
import MapService.world.World;
import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.*;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Writer;
import com.sg188.task.TaskName;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DiaCungTrung extends ZWorld{
    private int LEVEL_PHUBAN;
    private int level;
    private Mob BigBoss;
    private boolean isInit;
    private int[] listItem = {7,7,7,7,7,8,8,8,932,932,428,428};
    private int[] itemdrop = {7,7,7,7,7,8,8,8,932,932,428,428};
    private boolean isClose;
    private boolean iscreateBoss;
    private World world;

    public DiaCungTrung(Map map, int id, int LEVEL_PHUBAN,World world) {
        super(map, id);
        this.LEVEL_PHUBAN = LEVEL_PHUBAN;
        if(this.LEVEL_PHUBAN < 1){
            this.LEVEL_PHUBAN = 1;
        }
        createMonster();
        isInit = true;
        this.world = world;

    }

    @Override
    public boolean addChar(@NotNull Char p) {
        super.addChar(p);
        if(!isInit)
            super.sendModToNewChar(p);
        return true;
    }
    public void refresh() {
        if(isInit){
            isInit = false;
            for (Mob mob: monsters){
                reSpawnMobToAllChar(mob);
            }
        }
        if(this.getLivingMonsters().size()< 1&&!iscreateBoss){
            try{
                iscreateBoss=true;
                monsters.add(BigBoss);
                Message m = new Message((byte) 1);
                BigBoss.write(m.writer);
                for (Char c2 : getChars()) {
                    c2.Info.cx = BigBoss.cx;
                    c2.Info.cy = BigBoss.cy;
                    Message m2 = new Message((byte) 102);
                    m2.writeInt(c2.Info.idEntity);
                    m2.writeShort(c2.Info.cx);
                    m2.writeShort(c2.Info.cy);
                    c2.user.session.sendMessage(m);
                    for (Char c : getChars()) {
                        c.user.session.sendMessage(m2);
                    }
                }
                reSpawnMobToAllChar(BigBoss);
            }catch (Exception e){

            }
        }
    }
    @Override
    public void update() {
        super.update();
        refresh();
    }
    @Override
    protected boolean canRespawn(Mob mob) {
        // Điều kiện mới cho việc hồi sinh, ví dụ:
        return false; // Luôn chặn việc hồi sinh
    }
    public void createMonster() {
        monsters.clear();
        int l = 60;
        for (int i = 0; i < 70; i++) {
            Mob mob = new Mob();
            mob.id = 120;
            mob.exp = LEVEL_PHUBAN*570;
            mob.level = LEVEL_PHUBAN;
            if(i > 10&&i<= 22){
                mob.cx = (short) (669 + l);
            } else if (i > 22&&i<= 38) {
                mob.cx = (short) (429 + l);
            } else
                mob.cx = (short) (174 + l);
            mob.cy = (short) ((i <= 10) ? 175 : (i <= 22&&i> 10) ? 233: (i <= 38&&i> 22) ? 340: (i <= 43&&i> 38) ? 531: 666);
            if(mob.cy == 542 && mob.cx == 594){
                Log.debug(i);
            }
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = LEVEL_PHUBAN * 10800;
            mob.expGoc = mob.hpGoc / 8;

            mob.levelBoss = 0;
            mob.paintMiniMap = true;
            mob.idEntity = i;
            monsters.add(mob);
            mob.reSpawn(this);
            if (i == 10 || i == 22|| i == 38|| i == 43) l = 60;
            else l += 60;
        }
        List<XYEntity> entityList = new ArrayList<>();
        int x = 60;
        for (int i = 0; i < 4; i++) {
            entityList.add(new XYEntity((short) (1579 + x), (short) 158));
            x += 60;
        }
        x = 60;
        for (int i = 0; i < 10; i++) {
            entityList.add(new XYEntity((short) (1813 + x), (short) 177));
            x += 60;
        }
        x = 60;
        for (int i = 0; i < 12; i++) {
            entityList.add(new XYEntity((short) (2257 + x), (short) 371));
            x += 60;
        }
        x = 60;
        for (int i = 0; i < 5; i++) {
            entityList.add(new XYEntity((short) (1741 + x), (short) 404));
            x += 60;
        }
        x = 60;
        for (int i = 0; i < 15; i++) {
            entityList.add(new XYEntity((short) (1915 + x), (short) 529));
            x += 60;
        }
        x = 60;
        for (int i = 0; i < 35; i++) {
            entityList.add(new XYEntity((short) (751 + x), (short) 666));
            x += 60;
        }
        for (int i = 0; i < entityList.size(); i++) {
            Mob mob = new Mob();
            mob.id = 120;
            mob.exp = LEVEL_PHUBAN*370;
            mob.level = LEVEL_PHUBAN;
            mob.cx = entityList.get(i).cx;
            mob.cy = entityList.get(i).cy;
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = LEVEL_PHUBAN * 6000;
            mob.expGoc = mob.hpGoc / 8;

            mob.levelBoss = 0;
            mob.paintMiniMap = true;
            mob.idEntity = 70+i;
            monsters.add(mob);
            mob.reSpawn(this);
        }
        Mob mob = new Mob();
        mob.id = 76;
        mob.level = LEVEL_PHUBAN;
        mob.cx = 2726;
        mob.levelBoss = 10;
        mob.cy = 371;
        mob.status = 2;
        mob.hpGoc = mob.hp = mob.hpFull = 1000 * LEVEL_PHUBAN * 480;
        mob.expGoc = mob.hpGoc / 8;
        mob.paintMiniMap = false;
        mob.idEntity = monsters.size();
        mob.reSpawn(this);
        BigBoss = mob;
    }
    @Override
    public void setMobDie(Char player, Mob mob) {
        super.setMobDie(player,mob);
        if(mob.id == 76){
            for (int i = 0; i < Utlis.nextInt(4, 10); i++) {
                Item it = new Item(listItem[Utlis.nextInt(0, listItem.length - 1)]);
                player.addItem(it);
            }

            world.setCountdown(15);
            SendMessageInZone(HanderMessage.SendThongBao("Địa cung sẽ đóng sau 15s nữa",HanderMessage.WHITE));
            sendTimeMap(150);
            List<Char> charList = getChars();
            for (int i = 0; i < itemdrop.length; i++) {
                Item item = new Item(itemdrop[i]);
                ItemMap itemMap = new ItemMap((short) id_ENTITY_ITEM_MAP++);
                itemMap.setOwnerID(-1);
                itemMap.setItem(item);
                this.addItemMap(itemMap);
                Writer writer = new Writer();
                try {
                    writer.writeShort(mob.idEntity);
                    itemMap.write(writer, mob.cx+Utlis.nextInt(0,200), mob.cy, this);
                    for (Char pl : charList) {
                        if(pl!=null&&pl.user!=null&&!pl.isClean)
                            pl.service.sendItemDropFormMob(writer);
                    }
                } catch (IOException e) {
                }
            }
            for (Char pl: charList){
                if(pl!=null && pl.user!=null &&pl.clan!=null){
                    Calendar calendar = Calendar.getInstance();
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY) {
                        pl.clan.addExp(4);
                    }else
                        pl.clan.addExp(2);

                }
                if(pl != null && pl.user != null &&pl.taskId== TaskName.NV_BAT_DAU_THU_THACH){
                    if(pl.taskMain!=null&&pl.taskMain.index==0){
                        pl.taskNext();
                    }
                }
            }
        }
    }

}

