package MapService.zones;

import MapService.Map;
import MapService.world.World;
import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.real.Mob;
import com.sg188.server.lib.Message;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class ZoneCamThuat extends ZWorld{
    private int LEVEL_PHUBAN;
    private final int HP_FULL = 1850;
    private final int HP_BISSBOSS = 3080000;
    private final int MAX_LOOP = 20;
    private Mob BigBoss;
    private boolean IsRepsawBoss;
    private byte CountVonglap = 1;
    private World camthuat;
    private int[] listItem = {7,7,7,7,7,8,428,428};
    private boolean isClose;
    private boolean IsReSetVongLap;
    private boolean isSpawnBoss;

    public ZoneCamThuat(Map map, int id,int LEVEL_PHUBAN,World camthuat) {
        super(map, id);
        this.LEVEL_PHUBAN = LEVEL_PHUBAN;
        if(this.LEVEL_PHUBAN < 1){
            this.LEVEL_PHUBAN = 1;
        }
        createMonster();
        this.camthuat = camthuat;

    }
    public void refresh() {
        if(camthuat.getCountDown()==1&&!isClose){
            isClose = true;
            camthuat.setCountdown(15);
            for (Char c2 : getChars()) {
                c2.service.sendTimeInMap(150,false);
            }
            CountVonglap = MAX_LOOP;
        }
        if(CountVonglap==MAX_LOOP ) {
            if(!isClose) {
                isClose = true;
                camthuat.setCountdown(15);
                for (Char c2 : getChars()) {
                    c2.service.sendTimeInMap(150,false);
                }

            }
        }
        if(CountVonglap < MAX_LOOP) {
            int countRepsaw = (int) monsters.stream().filter(mob -> mob.id == 237 && mob.isDie).count();
            for (Mob mob : monsters) {
                if (mob.id == 238 && mob.isDie) {
                    mob.isDie = false;
                    IsReSetVongLap = true;
                    isSpawnBoss= false;
                }
            }
            if (countRepsaw == 42&&!IsRepsawBoss) {
                for (Char c : getChars()) {
                    c.user.session.sendMessage(HanderMessage.SendThongBao("Kabuto đã bắt đầu xuất hiện ", HanderMessage.WHITE));
                }
                RespawBoss();
                countRepsaw = 0;
            }
            if (IsReSetVongLap&&!isClose) {
                IsReSetVongLap = false;
                camthuat.setCountdown(360);
                RemoveBigBoss();
                itemMaps.clear();
                int hpGoc = LEVEL_PHUBAN * HP_FULL;
                synchronized (monsters) {
                    for (int i = 0; i < monsters.size(); i++) {
                        Mob m = monsters.get(i);
                        m.hpGoc = m.hp = m.hpFull = (int) (hpGoc + ((hpGoc / 2) * CountVonglap) * 10);
                        if (m.hpGoc < 0) {
                            m.hpGoc = m.hp = m.hpFull = Integer.MAX_VALUE;
                        }
                        m.expGoc = m.hpGoc / 8;
                        m.reSpawn(this);
                        reSpawnMobToAllChar(m);
                    }
                }
                for (Char c2 : getChars()) {
                    c2.Info.cx = 152;
                    c2.Info.cy = 428;
                    Message m2 = new Message((byte) 102);
                    try {
                        m2.writeInt(c2.Info.idEntity);
                        m2.writeShort(c2.Info.cx);
                        m2.writeShort(c2.Info.cy);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    c2.service.sendTimeInMap(camthuat.getCountDown()*10,false);
                    c2.user.session.sendMessage(HanderMessage.SendThongBao("Vòng lặp ảo tưởng lần " + (CountVonglap), HanderMessage.WHITE));
                    for (Char c : getChars()) {
                        c.user.session.sendMessage(m2);
                    }
                }
                CountVonglap++;
            }
        }
    }
    @Override
    public boolean addChar(Char pl){
        super.addChar(pl);
        pl.getService().sendTimeInMap(world.getCountDown()*10,false);
        return true;
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
    private void RemoveBigBoss() {
        try {
            IsRepsawBoss = false;
            monsters.remove(monsters.size() - 1);
            Message m = new Message((byte) 0);
            m.writeShort(BigBoss.idEntity);
            for (Char c : getChars()) {
                c.user.session.sendMessage(m);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    private void RespawBoss() {
        try {
            IsRepsawBoss = true;
            if (this.CountVonglap > 1) {
                int hp = HP_BISSBOSS+(LEVEL_PHUBAN-1)*193000+(193000*CountVonglap);
                if(hp< 0){
                    hp=Integer.MAX_VALUE;
                }
                BigBoss.hpGoc = BigBoss.hp = BigBoss.hpFull = hp;
                BigBoss.expGoc = BigBoss.hpGoc / 8;
                if (BigBoss.expGoc <= 0) {
                    BigBoss.expGoc = 1;
                }
                BigBoss.status = 2;
            }
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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    public void createMonster() {
        monsters.clear();
        int l = 60;
        for (int i = 0; i < 42; i++) {
            Mob mob = new Mob();
            mob.id = 237;
            mob.exp = 9999;
            mob.level = LEVEL_PHUBAN;
            mob.cx = (short) (200 + l);
            mob.cy = (short) ((i > 14 && i <= 28) ? 284 : (i > 28) ? 137 : 428);
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = LEVEL_PHUBAN * HP_FULL;
            mob.expGoc = mob.hpGoc / 8;
            mob.paintMiniMap = false;
            mob.idEntity = i;
            monsters.add(mob);
            mob.reSpawn(this);
            if (i == 14 || i == 28) l = 60;
            else l += 60;
        }
        Mob mob = new Mob();
        mob.id = 238;
        mob.level = LEVEL_PHUBAN;
        mob.cx = 986;
        mob.levelBoss = 10;
        mob.cy = 137;
        mob.status = 2;
        mob.hpGoc = mob.hp = mob.hpFull = HP_BISSBOSS+(LEVEL_PHUBAN-1)*19300;
        mob.expGoc = mob.hpGoc / 8;
        mob.paintMiniMap = false;
        mob.idEntity = monsters.size();
        mob.reSpawn(this);
        BigBoss = mob;
    }
    @Override
    public void setMobDie(Char player, Mob mob) {
        super.setMobDie(player,mob);
        if(mob.id == 238){
            for (int i = 0; i < Utlis.nextInt(4, 10); i++) {
                Item it = new Item(listItem[Utlis.nextInt(0, listItem.length - 1)]);
                player.addItem(it);
            }
            if(CountVonglap == 5){
                List<Char>charList=getChars();
                for (Char pl: charList){
                    if(pl!=null && !pl.isClean&&pl.user!=null){
                        if(pl.clan!=null){
                            pl.addClanPoint(5);
                        }
                    }
                }
            }
        }
    }

}
