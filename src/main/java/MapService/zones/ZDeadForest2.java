package MapService.zones;

import MapService.Map;
import MapService.world.World;
import Service.HanderMessage;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.real.Mob;
import com.sg188.real.XYEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ZDeadForest2 extends ZWorld {
    private int level;
    private boolean isCreateMob;
    private boolean isCreateBoss;
    private int[] listitem = { 566, // đá Rinnegan
            174, 175, 179, 216, 217, 218, 248, 278, 302, 315};

    public ZDeadForest2(Map map, int id, int level, World world) {
        super(map, id);
        this.level = level;
        setWorld(world);
        createMonster();
    }

    @Override
    public boolean addChar(@NotNull Char p) {
        super.addChar(p);
        world.getService().sendTimeInMap(world.getCountDown(), true);
        world.getService().setTypePKALLMap();
        return true;
    }

    public void createMonster() {
        monsters.clear();
        List<XYEntity> entityList = new ArrayList<>();
        int l = 60;
        for (int i = 0; i < 6; i++) {
            entityList.add(new XYEntity((short) ((short) 364 + l), (short) 224));
            l += 60;
        }
        l = 60;
        for (int i = 0; i < 12; i++) {
            entityList.add(new XYEntity((short) ((short) 109 + l), (short) 417));
            l += 60;
        }
        l = 60;
        for (int i = 0; i < 6; i++) {
            entityList.add(new XYEntity((short) ((short) 349 + l), (short) 613));
            l += 60;
        }
        l = 60;
        for (int i = 0; i < 15; i++) {
            entityList.add(new XYEntity((short) ((short) 175 + l), (short) 827));
            l += 60;
        }
        for (int i = 0; i < entityList.size(); i++) {
            Mob mob = new Mob();
            mob.id = 84;
            mob.exp = level * 268;
            mob.level = level;
            mob.cx = entityList.get(i).cx;
            mob.cy = entityList.get(i).cy;
            mob.status = 4;
            mob.hpGoc = mob.hp = mob.hpFull = level * 2300;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.idEntity = i;
            monsters.add(mob);
            mob.reSpawn(this);
            isCreateMob = true;
        }
    }

    @Override
    public void update() {
        super.update();
        refesh();
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
        if (!isCreateBoss && world.getCountDown() <= 1500) {
            isCreateBoss = true;
            Mob mob = new Mob();
            mob.id = 82;
            mob.level = level;
            mob.cx = 609;
            mob.levelBoss = 10;
            mob.cy = 417;
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = level * 69000 * 5;
            mob.exp = level * 20000;
            mob.paintMiniMap = false;
            mob.expGoc = mob.hpGoc / 8;
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
                it.amount = 3;
                player.addItem(it);
                player.msgAddItemBag(it);
            }
            List<Char> members = getChars();
            for (Char pl : members) {
                if (pl != null && pl.user != null) {
                 //   pl.pointDungeon += 5;
                 //   pl.service.updatepointDungeon();
                    Calendar calendar = Calendar.getInstance();
                    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if (dayOfWeek == Calendar.SUNDAY) {
                        if (pl.clan != null) {
                            pl.addClanPoint(200);
                        }
                        pl.Info.chuyenCan += 200;
                        pl.Info.chuyenCanTuan += 200;
                    } else {
                        if (pl.clan != null) {
                            pl.addClanPoint(100);
                        }
                        pl.Info.chuyenCan += 100;
                        pl.Info.chuyenCanTuan += 100;
                    }
                    pl.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 100 điểm chuyên cần, 100 điểm cống hiến gia tộc", HanderMessage.YELLOW_MID));
                }
            }
            world.getService().sendMessage(HanderMessage.SendThongBao("Chúc mừng các nhẫn giả đã hoàn thành khu rừng chết", HanderMessage.WHITE));
        }
    }
}

