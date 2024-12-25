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

public class ZDaiChien3 extends ZWorld{
    private int level;
    private Mob bigBoss;
    private boolean isCreateMob;
    private boolean isCreateBoss;
    public List<Mob> mobList = new ArrayList<>();
    public long timestart;
    public boolean isNextMap;

    boolean hasRun = false;
    private int[] listitem = { 566, // đá Rinnegan
            174, 175, 179, 216, 217, 218, 248, 278, 302, 315};

    public ZDaiChien3(Map map, int id, int level, World world) {
        super(map, id);
        this.level = level;
        setWorld(world);
        createMonster();
    }
    @Override
    public boolean addChar(@NotNull Char p) {
        super.addChar(p);
        if (map.mapID == 42 || map.mapID == 43) {
            world.getService().sendTimeInMap(world.getCountDown(), true);
        }
        return true;
    }
    @Override
    public void update() {
        super.update();
        refesh();
    }
    public void createMonster() {
        monsters.clear();
        List<XYEntity> entityList = new ArrayList<>();
        entityList.add(new XYEntity((short) 300, (short) 742));
        entityList.add(new XYEntity((short) 600, (short) 742));
        entityList.add(new XYEntity((short) 900, (short) 742));
        entityList.add(new XYEntity((short) 1200, (short) 742));
        entityList.add(new XYEntity((short) 1500, (short) 742));
        entityList.add(new XYEntity((short) 1800, (short) 742));
        entityList.add(new XYEntity((short) 2100, (short) 742));
        entityList.add(new XYEntity((short) 2400, (short) 742));
        entityList.add(new XYEntity((short) 2700, (short) 742));
        entityList.add(new XYEntity((short) 3000, (short) 742));
        entityList.add(new XYEntity((short) 3300, (short) 742));
        entityList.add(new XYEntity((short) 3600, (short) 742));
        entityList.add(new XYEntity((short) 3600, (short) 297));
        entityList.add(new XYEntity((short) 3300, (short) 297));
        entityList.add(new XYEntity((short) 3000, (short) 297));
        entityList.add(new XYEntity((short) 2700, (short) 297));
        entityList.add(new XYEntity((short) 2400, (short) 297));
        entityList.add(new XYEntity((short) 272, (short) 292));
        entityList.add(new XYEntity((short) 472, (short) 292));
        entityList.add(new XYEntity((short) 672, (short) 292));
        entityList.add(new XYEntity((short) 872, (short) 292));
        entityList.add(new XYEntity((short) 972, (short) 292));
        entityList.add(new XYEntity((short) 1072, (short) 292));
        entityList.add(new XYEntity((short) 1272, (short) 292));
        entityList.add(new XYEntity((short) 1472, (short) 292));
        entityList.add(new XYEntity((short) 1672, (short) 292));
        entityList.add(new XYEntity((short) 1804, (short) 577));
        entityList.add(new XYEntity((short) 1904, (short) 577));
        entityList.add(new XYEntity((short) 2104, (short) 577));
        entityList.add(new XYEntity((short) 2204, (short) 577));

        for (int i = 0; i < entityList.size(); i++) {
            Mob mob = new Mob();
            mob.id = Utlis.nextInt(94,103);
            mob.exp = level * 237;
            mob.level = level;
            mob.cx = entityList.get(i).cx;
            mob.cy = entityList.get(i).cy;
            mob.status = 4;
            mob.hpGoc = mob.hp = mob.hpFull = level * 9000;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.idEntity = i;
            mobList.add(mob);
            mob.reSpawn(this);
        }
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
            //ngân long trụ
            Mob mob = new Mob();
            mob.id = 240;
            mob.level = level;
            mob.cx = 1259;
            mob.levelBoss = 10;
            mob.cy = 292;
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = level * 60000*5;
            mob.exp = level * 17841;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.he = Utlis.nextInt(1, 5);
            mob.idEntity = monsters.size();
            monsters.add(mob);
            //hoàng kim trụ
            mob = new Mob();
            mob.id = 241;
            mob.level = level;
            mob.cx = 2830;
            mob.levelBoss = 10;
            mob.cy = 298;
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = level * 60000*50;
            mob.exp = level * 17841;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.he = Utlis.nextInt(1, 5);
            mob.idEntity = monsters.size();
            monsters.add(mob);
            //
            List<Char> member = getChars();
            for (Char pl : member) {
                pl.Info.cx = mob.cx;
                pl.Info.cy = mob.cy;
                pl.service.setXYChar();
                pl.getService().sendMessage(HanderMessage.AddMob(mob));
            }
        }
        if (!hasRun && (timestart + 1000 * 60*0.5) <= System.currentTimeMillis()) {
            List<Char> member = getChars();
            for (Char pl : member) {
                world.zones.get(2).addChar(pl);
            }
            hasRun = true;
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
