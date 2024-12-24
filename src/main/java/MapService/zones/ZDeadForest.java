package MapService.zones;

import MapService.Map;
import MapService.world.World;
import Service.HanderMessage;
import com.sg188.lib.Utlis;
import com.sg188.real.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ZDeadForest extends ZWorld {
    private int level;
    private Mob bigBoss;
    private boolean isCreateMob;
    private boolean isCreateBoss;
    public List<Mob> mobList = new ArrayList<>();
    public long timestart;
    public boolean isNextMap;
    private int[] listitem = { 566, // đá Rinnegan
            174, 175, 179, 216, 217, 218, 248, 278, 302, 315};

    public ZDeadForest(Map map, int id, int level, World world) {
        super(map, id);
        this.level = level;
        setWorld(world);
        createMonster();
    }

    @Override
    public boolean addChar(@NotNull Char p) {
        super.addChar(p);
        if (map.mapID == 31) {
            world.getService().sendTimeInMap(world.getCountDown(), true);
        }
        return true;
    }

    public void createMonster() {
        monsters.clear();
        List<XYEntity> entityList = new ArrayList<>();
        entityList.add(new XYEntity((short) 102, (short) 487));
        entityList.add(new XYEntity((short) 197, (short) 487));
        entityList.add(new XYEntity((short) 297, (short) 487));
        entityList.add(new XYEntity((short) 367, (short) 487));
        entityList.add(new XYEntity((short) 492, (short) 526));
        entityList.add(new XYEntity((short) 567, (short) 577));
        entityList.add(new XYEntity((short) 712, (short) 578));
        entityList.add(new XYEntity((short) 782, (short) 516));
        entityList.add(new XYEntity((short) 922, (short) 489));
        entityList.add(new XYEntity((short) 1192, (short) 322));
        entityList.add(new XYEntity((short) 1117, (short) 322));
        entityList.add(new XYEntity((short) 1010, (short) 323));
        entityList.add(new XYEntity((short) 924, (short) 323));
        entityList.add(new XYEntity((short) 494, (short) 244));
        entityList.add(new XYEntity((short) 553, (short) 244));
        entityList.add(new XYEntity((short) 609, (short) 244));
        entityList.add(new XYEntity((short) 699, (short) 244));
        for (int i = 0; i < entityList.size(); i++) {
            Mob mob = new Mob();
            mob.id = 84;
            mob.exp = level * 237;
            mob.level = level;
            mob.cx = entityList.get(i).cx;
            mob.cy = entityList.get(i).cy;
            mob.status = 4;
            mob.hpGoc = mob.hp = mob.hpFull = level * 2000;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.idEntity = i;
            mobList.add(mob);
            mob.reSpawn(this);
        }
    }

    @Override
    public void update() {
        super.update();
        refesh();
    }
    @Override
    public void createMob() {
        world.setCountdown(3000);
        isCreateMob = true;
        for (Mob mob : mobList) {
            world.getService().sendMessage(HanderMessage.AddMob(mob));
            monsters.add(mob);
        }
        timestart = System.currentTimeMillis();
        world.getService().sendTimeInMap(world.getCountDown(), true);
        world.getService().setTypePKALLMap();
    }

    public void refesh() {
        if (isCreateMob && !isCreateBoss) {
            for (int i = monsters.size() - 1; i >= 0; i--) {
                try {
                    Mob mob = monsters.get(i);
                    if (mob != null) {
                        if (mob.isReSpawn) {
                            if (System.currentTimeMillis() - mob.timeDie >= 10000L) {
                                mob.reSpawn(this);
                                reSpawnMobToAllChar(mob);
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        if (!isCreateBoss && (timestart + 1000 * 10*60) <= System.currentTimeMillis() & isCreateMob) {
            isCreateBoss = true;
            Mob mob = new Mob();
            mob.id = 82;
            mob.level = level;
            mob.cx = 597;
            mob.levelBoss = 10;
            mob.cy = 244;
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = level * 60000*5;
            mob.exp = level * 17841;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.he = Utlis.nextInt(1, 5);
            mob.idEntity = monsters.size();
            monsters.add(mob);
            List<Char> member = getChars();
            for (Char pl : member) {
                pl.Info.cx = mob.cx;
                pl.Info.cy = mob.cy;
                pl.service.setXYChar();
                pl.getService().sendMessage(HanderMessage.AddMob(mob));
            }
        }
        if ((timestart + 1000 * 60*15) <= System.currentTimeMillis() && isNextMap) {
            List<Char> member = getChars();
            for (Char pl : member) {
                world.zones.get(1).addChar(pl);
            }
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
        if (mob.id == 82) {
            for (int i = 0; i < listitem.length; i++) {
                Item it = new Item(listitem[i], false);
                it.amount = 2;
                player.addItem(it);
                player.msgAddItemBag(it);
            }
            List<Char> members = getChars();
            for (Char pl : members) {
                if (pl != null && pl.user != null) {
                    // pl.pointDungeon += 5;
                    // pl.service.updatepointDungeon();
                    Calendar calendar = Calendar.getInstance();
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY) {
                        if (pl.clan != null) {
                            pl.addClanPoint(100*2);
                        }
                        pl.Info.chuyenCan += 100*2;
                        pl.Info.chuyenCanTuan += 100*2;
                    } else {
                        if (pl.clan != null) {
                            pl.addClanPoint(50*2);
                        }
                        pl.Info.chuyenCan += 50*2;
                        pl.Info.chuyenCanTuan += 50*2;
                    }
                    pl.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 100 điểm chuyên cần, 100 điểm cống hiến gia tộc", HanderMessage.YELLOW_MID));
                }
            }
            isNextMap = true;
            world.getService().sendMessage(HanderMessage.SendThongBao("Hoàn thành cổng số 1 , Các nhẫn giả sẽ được dịch chuyển qua cổng số 2 sau ít phút nua",HanderMessage.WHITE));
        }
    }
}
