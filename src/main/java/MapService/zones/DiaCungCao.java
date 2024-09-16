package MapService.zones;

import MapService.Map;
import MapService.world.World;
import Service.HanderMessage;
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

public class DiaCungCao extends ZWorld {
    private int LEVEL_PHUBAN;
    private int level;
    private Mob BigBoss;
    private boolean isInit;
    private int[] listItem = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315}; // ks giết
    private int[] itemdrop = {174, 175, 179, 216, 217, 218, 248, 278, 302, 315}; // cho nhặt tự do
    private boolean isClose;
    private boolean iscreateBoss;
    private World world;

    public DiaCungCao(Map map, int id, int LEVEL_PHUBAN, World world) {
        super(map, id);
        this.LEVEL_PHUBAN = LEVEL_PHUBAN;
        if (this.LEVEL_PHUBAN < 1) {
            this.LEVEL_PHUBAN = 1;
        }
        createMonster();
        isInit = true;
        this.world = world;

    }

    @Override
    public boolean addChar(@NotNull Char p) {
        super.addChar(p);
        if (!isInit)
            super.sendModToNewChar(p);
        return true;
    }

    public void refresh() {
        if (isInit) {
            isInit = false;
            for (Mob mob : monsters) {
                reSpawnMobToAllChar(mob);
            }
        }
        if (this.getLivingMonsters().size() <= 1 && !iscreateBoss) {
            try {
                iscreateBoss = true;
                Mob mob = new Mob();
                mob.id = 77;
                mob.level = LEVEL_PHUBAN;
                mob.cx = 1829;
                mob.levelBoss = 10;
                mob.cy = 849;
                mob.status = 2;
                mob.hpGoc = mob.hp = mob.hpFull = 1000 * LEVEL_PHUBAN * 480;
                mob.expGoc = mob.hpGoc / 8;
                mob.paintMiniMap = false;
                mob.idEntity = monsters.size();
                mob.reSpawn(this);
                monsters.add(mob);
                Message m = new Message((byte) 1);
                mob.write(m.writer);
                for (Char c2 : getChars()) {
                    c2.Info.cx = mob.cx;
                    c2.Info.cy = mob.cy;
                    Message m2 = new Message((byte) 102);
                    m2.writeInt(c2.Info.idEntity);
                    m2.writeShort(c2.Info.cx);
                    m2.writeShort(c2.Info.cy);
                    c2.user.session.sendMessage(m);
                    for (Char c : getChars()) {
                        c.user.session.sendMessage(m2);
                    }
                }
                reSpawnMobToAllChar(mob);
            } catch (Exception e) {

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
        for (int i = 0; i < 80; i++) {
            Mob mob = new Mob();
            if (i < 53)
                mob.id = 66;
            else
                mob.id = 67;
            mob.exp = LEVEL_PHUBAN * 570;
            mob.level = LEVEL_PHUBAN;
            if (i > 29 && i <= 52) {
                mob.cx = (short) (614 + l);
            } else
                mob.cx = (short) (150 + l);
            mob.cy = (short) ((i <= 29) ? 178 : (i <= 52 && i > 29) ? 373 : 654);
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = LEVEL_PHUBAN * 10800;
            mob.expGoc = mob.hpGoc / 8;

            mob.levelBoss = 0;
            mob.paintMiniMap = true;
            mob.idEntity = i;
            monsters.add(mob);
            mob.reSpawn(this);
            if (i == 30 || i == 52) l = 60;
            else l += 60;
        }
        List<XYEntity> entityList = new ArrayList<>();
        int x = 60;
        for (int i = 0; i < 29; i++) {
            entityList.add(new XYEntity((short) (298 + x), (short) 533));
            x += 60;
        }
        x = 60;
        for (int i = 0; i < 15; i++) {
            entityList.add(new XYEntity((short) (16 + x), (short) 849));
            x += 60;
        }
        x = 60;
        for (int i = 0; i < 15; i++) {
            entityList.add(new XYEntity((short) (1330 + x), (short) 849));
            x += 60;
        }
        for (int i = 0; i < entityList.size(); i++) {
            Mob mob = new Mob();
            mob.id = 66;
            mob.exp = LEVEL_PHUBAN * 570;
            mob.level = LEVEL_PHUBAN;
            mob.cx = entityList.get(i).cx;
            mob.cy = entityList.get(i).cy;
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = LEVEL_PHUBAN * 10800;
            mob.expGoc = mob.hpGoc / 8;

            mob.levelBoss = 0;
            mob.paintMiniMap = true;
            mob.idEntity = i + 80;
            monsters.add(mob);
            mob.reSpawn(this);
        }
    }

    @Override
    public void setMobDie(Char player, Mob mob) {
        super.setMobDie(player, mob);
        if (mob.id == 77) {
            for (int i = 0; i < listItem.length; i++) {
                Item it = new Item(listItem[i], false);
                it.amount = 5;
                player.addItem(it);
                player.msgAddItemBag(it);
            }
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
                    itemMap.write(writer, mob.cx + Utlis.nextInt(0, 200), mob.cy, this);
                    for (Char pl : charList) {
                        if (pl != null && pl.user != null && !pl.isClean)
                            pl.service.sendItemDropFormMob(writer);
                    }
                } catch (IOException e) {
                }
            }
            world.setCountdown(15);
            SendMessageInZone(HanderMessage.SendThongBao("Địa cung sẽ đóng sau 15s nữa", HanderMessage.WHITE));
            sendTimeMap(150);
            for (Char pl : charList) {
                if (pl != null && pl.user != null) {
                    Calendar calendar = Calendar.getInstance();
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY) {
                        if (pl.clan != null) {
                            pl.addClanPoint(20);
                        }
                        pl.Info.chuyenCan += 20;
                        pl.Info.chuyenCanTuan += 20;
                    } else {
                        if (pl.clan != null) {
                            pl.addClanPoint(10);
                        }
                        pl.Info.chuyenCan += 10;
                        pl.Info.chuyenCanTuan += 10;
                    }
                    pl.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 10 điểm chuyên cần, 10 điểm cống hiến gia tộc", HanderMessage.YELLOW_MID));
                }


                if (pl != null && pl.user != null && pl.taskId == TaskName.NV_BAT_DAU_THU_THACH) {
                    if (pl.taskMain != null && pl.taskMain.index == 0) {
                        pl.taskNext();
                    }
                }
            }
            if (player.taskId == TaskName.NV_DOI_DAU_VOI_AKATSUKI) {
                if (player.taskMain != null && player.taskMain.index == 0) {
                    player.updateTaskCount(1);
                }
            }

        }



    }

}

