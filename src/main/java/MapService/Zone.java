package MapService;

import InfoChar.InfoPoint;
import Manager.Manager;
import MapService.world.Arena;
import MapService.world.DaiHoiVoThuat;
import MapService.world.World;
import Service.HanderCharacter;
import Service.HanderMessage;
import Service.HanderUseSkill;
import com.event.Event;
import com.sg188.data.DataCenter;
import com.sg188.data.LangLa_iw;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.party.Group;
import com.sg188.real.*;
import com.sg188.server.Main;
import com.sg188.server.ServerManager;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Writer;
import com.sg188.data.*;
import com.sg188.task.MobInfo;
import com.sg188.task.TaskFactory;
import com.sg188.task.TaskName;
import com.sg188.task.TaskOrder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Zone {
    public Map map;
    public int zoneID;

    public List<Npc> npcs;
    public List<Mob> monsters;
    public List<Char> players;
    public List<ItemMap> itemMaps;
    public boolean isOpened;
    private List<ItemMap> listRemoveItem;
    private ReadWriteLock lockItem;

    public long lastUpdateEveryHalfSecond;
    public Thread thread;
    public int id_ENTITY_ITEM_MAP;
    private ReadWriteLock lockChar;
    private ReadWriteLock lockRemoveItem;
    public boolean isFinish;
    public boolean isClosed;
    public boolean isLangCo;
    public int timeReviveMob = 2500;

    public Zone(Map map, int zone) {
        this.map = map;
        this.zoneID = zone;
        lockChar = new ReentrantReadWriteLock();
        npcs = new ArrayList<>();
        monsters = new ArrayList<>();
        players = new ArrayList<>();
        itemMaps = new ArrayList<>();
        listRemoveItem = new ArrayList<>();
        lockItem = new ReentrantReadWriteLock();
        lockRemoveItem = new ReentrantReadWriteLock();
    }

    public void SendMessageInZone(Message msg) {
        try {
            for (Char pl : getChars()) {
                if (pl != null && pl.user != null && pl.user.session != null) {
                    pl.user.session.sendMessage(msg);
                }
            }
            msg.close();
        } catch (Exception var3) {
//            var3.printStackTrace();
        } finally {
            if (msg != null) {
                msg.close();
            }
        }
    }

    public void sendTimeMap(int countDown) {
        try {
            Message m = Message.c((byte) -80);
            m.writeLong(System.currentTimeMillis());
            m.writeInt(countDown * 100);
            m.writeBoolean(false);
            SendMessageInZone(m);
        } catch (Exception e) {

        }
    }


    public ItemMap findItemMapById(short id) {
        lockItem.readLock().lock();
        try {
            for (ItemMap item : itemMaps) {
                if (item.getId() == id) {
                    return item;
                }
            }
        } finally {
            lockItem.readLock().unlock();
        }
        return null;
    }

    public void addItemMap(ItemMap item) {
        if (itemMaps != null) {
            lockItem.writeLock().lock();
            try {
                this.itemMaps.add(item);
            } finally {
                lockItem.writeLock().unlock();
            }
        }
    }

    public void removeItem(ItemMap item) {
        if (itemMaps != null) {
            lockItem.writeLock().lock();
            try {
                itemMaps.remove(item);
            } finally {
                lockItem.writeLock().unlock();
            }
        }
    }

    public void removeItem() {
        lockRemoveItem.writeLock().lock();
        try {
            for (ItemMap item : listRemoveItem) {
                try {
                    removeItem(item);
                    Writer writer = new Writer();
                    writer.writeShort(item.getId());
                    for (Char pl : this.getChars()) {
                        if (pl != null && pl.user != null) {
                            pl.service.removeItemMap(writer);
                            pl.service.updateItemMap(itemMaps);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Zone.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            listRemoveItem.clear();
        } finally {
            lockRemoveItem.writeLock().unlock();
        }
    }

    public void addItemRemove(ItemMap itemMap) {
        lockRemoveItem.writeLock().lock();
        try {
            listRemoveItem.add(itemMap);
        } finally {
            lockRemoveItem.writeLock().unlock();
        }
    }

    public int getNumberItem() {
        return itemMaps.size();
    }

    public void updateItemMap() {
        try {
            if (this.itemMaps.size() > 0) {
                lockItem.readLock().lock();
                try {
                    for (ItemMap item : itemMaps) {
                        try {
                            if (item.isExpired()) {
                                addItemRemove(item);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    lockItem.readLock().unlock();
                }
                if (listRemoveItem.size() > 0) {
                    removeItem();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Mob> getLivingMonsters() {
        ArrayList<Mob> mobs = new ArrayList<>();
        for (Mob mob : monsters) {
            if (!mob.isDie) {
                mobs.add(mob);
            }
        }
        return mobs;
    }

    public List<Mob> getLivingMonstersClan() {
        ArrayList<Mob> mobs = new ArrayList<>();
        for (Mob mob : monsters) {
            if (!mob.isDie && mob.id != 130) {
                mobs.add(mob);
            }
        }
        return mobs;
    }

    public void createNpc() {
        npcs.clear();
        for (int i = 0; i < map.getMapTemplate().listNpc.size(); i++) {
            try {
                Npc npc1 = map.getMapTemplate().listNpc.get(i);
                Npc npc2 = npc1.cloneNpc();
                npc2.idEntity = i;
                npcs.add(npc2);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void createMob() {
        monsters.clear();
        int size = map.getMapTemplate().listMob.size();
        for (int i = 0; i < size; i++) {
            try {
                Mob mob1 = map.getMapTemplate().listMob.get(i);
                Mob mob2 = mob1.cloneMob();
                mob2.idEntity = monsters.size();
                mob2.reSpawn(this);
                monsters.add(mob2);
                MobInfo mobInfo = MobInfo.builder().mapID(map.mapID).mobID(mob2.id).level(mob2.level).build();
                if (mob2.id >= 184 && mob2.id <= 198) {
                    TaskFactory.getInstance().addMobInfoTaskBoss(mobInfo);
                } else {
                    if (mob2.getMobTemplate().speedMove != 0)
                        TaskFactory.getInstance().addMobInfoTaskDay(mobInfo);
                }
                //spawn cương thi
                if (i == size - 1 && (map.mapID == 57 || map.mapID == 65 || map.mapID == 87 || map.mapID == 79 || map.mapID == 73) && Event.getEvent() != null && zoneID >= 5 && zoneID <= 10) {
                    Mob mob = new Mob();
                    int id = map.mapID == 57 ? 285 : map.mapID == 65 ? 286 : map.mapID == 87 ? 287 : map.mapID == 79 ? 288 : 289;
                    mob.id = id;
                    mob.level = map.mapID == 57 ? 59 : map.mapID == 65 ? 59 : map.mapID == 87 ? 59 : map.mapID == 79 ? 59 : 60;
                    mob.cx = (short) (map.mapID == 57 ? 632 : map.mapID == 65 ? 920 : map.mapID == 87 ? 776 : map.mapID == 79 ? 500 : 1328);

                    mob.levelBoss = 10;
                    mob.cy = (short) (map.mapID == 57 ? 190 : map.mapID == 65 ? 148 : map.mapID == 87 ? 155 : map.mapID == 79 ? 297 : 306);

                    mob.status = 2;
                    mob.hpGoc = mob.hp = mob.hpFull = 1000000000;
                    mob.expGoc = 5;
                    mob.paintMiniMap = false;
                    mob.idEntity = monsters.size();
                    mob.reSpawn(this);
                    monsters.add(mob);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void write(Char player, Writer writer) throws IOException {
        writer.writeShort(zoneID);
        writer.writeShort(map.mapID);

        player.Info.writeXY(writer);

        writeVecItemMap(writer);
        writeVecChar(player, writer);
        writeVecMob(writer);
        writeVecNpc(writer);

        writer.writeByte(player.InfoGame.TypePk);
        writer.writeLong(player.Info.TimeStartHD);
        writer.writeInt(1);
        writer.writeBoolean(false);
        if (map.mapID == 84) {
            writer.writeBoolean(true);
        } else
            writer.writeBoolean(false);
    }

    public int MAX_CHAR_INZONE = 100;

    public boolean addChar(Char player) {
        if (players != null) {
            try {
                if (players.size() < MAX_CHAR_INZONE) {
                    if (player.taskId == TaskName.NV_NHAN_GIA_HOC_VIEN) {
                        if (map.mapID == 86 && player.taskMain != null && player.taskMain.index == 2) {
                            player.taskNext();
                        }
                    }
                    if (player.trade != null) {
                        player.trade.closeUITrade();
                    }
                    Arena arena = (Arena) player.findWorld(World.ARENA);
                    if (arena != null && !this.isLoiDai()) {
                        arena.out(player);
                    }
                    DaiHoiVoThuat daiHoiVoThuat = (DaiHoiVoThuat) player.findWorld(World.DAI_HOI_VO_THUAT);
                    if (daiHoiVoThuat != null && !this.isDaiHoiVoThuat()) {
                        daiHoiVoThuat.out(player);
                    }
                    addPlayer(player);
                    if (player.zone != null) {
                        if (player.zone.isWorld()) {
                            player.removeMemberFromWorld(player.zone, this);
                        }
                        player.zone.removeChar(player);
                        if (player.zone.map.mapID != this.map.mapID) {
                            WayPoint waypoint = map.getWayPoint_WhenNextMap(player.zone.map.mapID);
                            if (waypoint != null) {
                                XYEntity xy = getXYBlockMapNotCheck(waypoint.cx, waypoint.cy);
                                player.Info.vec.clear();
                                if (xy.cx < 500) {
                                    player.Info.setXY(Utlis.nextInt(20, 50), xy.cy);
                                } else {
                                    player.Info.setXY(this.map.getMapTemplate().maxX - Utlis.nextInt(20, 50), xy.cy);
                                }
                            } else {
                                int cx = 500;
                                int cy = 500;
                                XYEntity xy = getXYBlockMapNotCheck(cx, cy);
                                if (xy != null) {
                                    player.Info.setXY(xy.cx, xy.cy);
                                }
                            }
                        }
                    } else {
                        int cx = 500;
                        int cy = 500;
                        XYEntity xy = getXYBlockMapNotCheck(cx, cy);
                    }
                    player.zone = this;
                    player.zone.addToAllChar(player);
                    player.service.sendArrMap(map.mapID);
                    player.service.sendIntoMap();
                    player.Info._mapID = (short) map.mapID;
                    showClanToPlayer(player);
                    SendMessageInZone(HanderMessage.sendGiaToc(player));
                    player.inLangCo = false;
                    if (player.taskId == TaskName.NV_TU_THIEN_VUONG_LANG_TAKUMI && player.taskMain != null && monsters.size() < 1) {
                        Mob mob = new Mob();
                        mob.idEntity = monsters.size();
                        mob.id = 230 + player.taskMain.index;
                        mob.nameChar = "";
                        mob.hpFull = mob.hp = 100000;
                        mob.exp = 6000;
                        mob.level = 10;
                        mob.cy = (short) (player.taskMain.index == 0 ? 269 : player.taskMain.index == 1 ? 187 : player.taskMain.index == 2 ? 536 : 393);
                        mob.cx = (short) (player.taskMain.index == 0 ? 320 : player.taskMain.index == 1 ? 665 : player.taskMain.index == 2 ? 548 : 760);
                        mob.status = 0;
                        mob.isReSpawn = false;
                        mob.timeRemove = System.currentTimeMillis() + 300000;
                        monsters.add(mob);
                        player.getService().sendMessage(HanderMessage.AddMob(mob));
                    }
                    return true;
                }
            } catch (Exception e) {
                Log.error(" loi add char in map ", e);
            }
        }
        return false;
    }

    private boolean isDaiHoiVoThuat() {
        return map.mapID == 49;
    }

    public void showClanToPlayer(Char pl) {
        try {
            List<Char> list = getChars();
            for (Char player : list) {
                if (player != null && player.user != null && !pl.isClean && player.service != null)
                    pl.getService().sendMessage(HanderMessage.sendGiaToc(player));
            }
        } catch (Exception E) {

        }
    }

    public void addPlayer(Char _char) {
        if (players != null) {
            lockChar.writeLock().lock();
            try {
                this.players.add(_char);
            } finally {
                lockChar.writeLock().unlock();
            }
        }
    }

    public List<Char> getChars() {
        ArrayList<Char> chars = new ArrayList<>();
        lockChar.readLock().lock();
        try {
            if (players != null) {
                for (Char c : players) {
                    if (c == null || c.isClean || c.user == null) {
                        continue;
                    }
                    chars.add(c);
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return chars;
    }

    public void updatePlayer() {
        try {
            long l = System.currentTimeMillis();
            if (players.size() > 0) {
                boolean isUpdateEveryHalfSecond = ((l - this.lastUpdateEveryHalfSecond) >= 500);
                if (isUpdateEveryHalfSecond) {
                    this.lastUpdateEveryHalfSecond = l;
                }
                List<Char> mChars = getChars();
                for (Char _char : mChars) {
                    if (isUpdateEveryHalfSecond) {
                        _char.updateEveryHalfSecond();
                    }
                    _char.update();
                }
            }
        } catch (Exception e) {
            Log.error(" loi update player trong zone", e);
            e.printStackTrace();
        }
    }


    public void sendItemMap(Mob mob, ItemMap item) {
        Writer writer = new Writer();
        try {
            writer.writeShort(mob.idEntity);
            item.write(writer, -1, mob.cy, this);
            List<Char> list = this.getChars();
            for (Char pl : list) {
                if (pl != null && pl.user != null && !pl.isClean)
                    pl.service.sendItemDropFormMob(writer);
            }
        } catch (IOException e) {
        }
    }

    public void removeMobToAllChar(Mob mob) {
        for (Char pl : this.getChars()) {
            if (pl != null && pl.user != null) {
                pl.user.session.sendMessage(HanderMessage.RemoveMob(mob.idEntity));
            }
        }
    }

    public void updateMob() {
        for (int i = monsters.size() - 1; i >= 0; i--) {
            try {
                Mob mob = monsters.get(i);
                if (mob != null) {
                    if (mob.timeRemove > 0 && mob.timeRemove <= System.currentTimeMillis()) {
                        if (!mob.isDie) {
                            removeMobToAllChar(mob);
                        }
                        monsters.remove(i);
                    }
                    if (mob.isReSpawn) {
                        if (canRespawn(mob)) {
                            if (System.currentTimeMillis() - mob.timeDie >= timeReviveMob) {
                                mob.reSpawn(this);
                                reSpawnMobToAllChar(mob);
                            }
                        }
                    } else {
                        if (mob.CanAttack()) {
                            List<Char> charList = getChars();
                            for (int j = charList.size() - 1; j >= 0; j--) {
                                Char mChar = charList.get(j);
                                if (mChar != null && mChar.user != null) {
                                    if (mChar.Point.hp > 0) {
                                        if (mob.getRe(mChar.Info) < 50 + mob.getMobTemplate().speedMove) {
                                            if (System.currentTimeMillis() - mob.delayAttack >= 5000) {
                                                this.mobAttackChar(mob, mChar);
                                                mob.delayAttack = System.currentTimeMillis();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (mob.Effs.size() > 0)
                        mob.UpdateEff(this);
                }
            } catch (Exception e) {
                Log.debug("error update mob in map " + e.getMessage());
            }
        }
    }

    protected boolean canRespawn(Mob mob) {
        return true; // Mặc định cho phép hồi sinh
    }

    private void writeVecItemMap(Writer writer) {
        try {
            writer.writeShort(itemMaps.size());
            for (int i = 0; i < itemMaps.size(); i++) {
                ItemMap item = itemMaps.get(i);
                item.write(writer, -1, -1, this);
            }
        } catch (IOException e) {
            Log.error("loi item map ", e);
        }
    }

    private void writeVecChar(Char player, Writer writer) {
        try {
            ArrayList<Char> vChar = new ArrayList<Char>();
            for (Char c : getChars()) {
                if (c != player && c != null && c.user != null) {
                    vChar.add(c);
                }
            }
            writer.writeByte(vChar.size());
            for (int i = 0; i < vChar.size(); i++) {
                Char c = vChar.get(i);
                if (c != null && c.user != null) {
                    writer.writeInt(c.Info.idEntity);
                    c.write(writer);
                }
            }
        } catch (IOException e) {
            Log.error("loi vec char map ", e);
        }
    }

    private void writeVecMob(Writer writer) {
        try {
            writer.writeShort(monsters.size());
            for (int i = 0; i < monsters.size(); i++) {
                Mob mob = monsters.get(i);
                if (mob != null)
                    monsters.get(i).write(writer);
            }
        } catch (IOException e) {
            Log.error("loi vec mob map ", e);
        }
    }

    private void writeVecNpc(Writer writer) {
        try {
            writer.writeShort(npcs.size());

            for (int i = 0; i < npcs.size(); i++) {
                Npc npc = npcs.get(i);
                if (npc != null) {
                    writer.writeByte(npc.status);
                    writer.writeShort(npc.id);
                    npc.writeXY(writer);
                }
            }
        } catch (IOException e) {
            Log.error("loi vec npc map ", e);
        }
    }

    public void removeChar(Char player) {
        if (players != null) {
            lockChar.writeLock().lock();
            try {
                boolean c = players.remove(player);
                if (c) {
                    removeToAllChar(player);
                }
            } finally {
                lockChar.writeLock().unlock();
            }
        }
    }

    public void removeMob(int id) {
        Mob mob = null;
        for (int i = 0; i < monsters.size(); i++) {
            if (monsters.get(i).idEntity == id) {
                mob = monsters.get(i);
            }
        }
        if (mob != null)
            monsters.remove(mob);
    }

    public void removeToAllChar(Char player) {
        try {
            Writer writer = new Writer();
            writer.writeInt(player.Info.idEntity);
            ArrayList<Char> vChar = new ArrayList<Char>();
            for (Char c : getChars()) {
                if (c != player && c != null && c.user != null) {
                    vChar.add(c);
                }
            }
            for (int i = 0; i < vChar.size(); i++) {
                try {
                    Char pl = vChar.get(i);
                    if (pl != null && pl.user != null)
                        pl.service.removeCharIntoMap(writer);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addToAllChar(Char player) {
        try {
            Writer writer = new Writer();
            writer.writeInt(player.Info.idEntity);
            player.write(writer);
            for (Char pl : getChars()) {
                try {
                    if (pl != null && pl.user != null && pl.service != null && pl.Info.idEntity != player.Info.idEntity) {
                        pl.service.addCharIntoMap(writer);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateXYChar(Char player, boolean when_move) {
        try {
            Writer writer = new Writer();
            writer.writeInt(player.Info.idEntity);
            player.Info.writeXY(writer);
            ArrayList<Char> vChar = new ArrayList<Char>();
            for (Char c : getChars()) {
                if (c != player && c != null && c.user != null) {
                    vChar.add(c);
                }
            }
            for (int i = 0; i < vChar.size(); i++) {
                try {
                    Char pl = vChar.get(i);
                    if (pl != null && pl.user != null && pl.service != null)
                        pl.service.updateXYChar(writer, when_move);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void attackMob(Char pl, int idSkill, int idMob) {

//            if (idSkill != 30) {
//                attackMob(client, 30, idMob);
//            }
        try {
            Skill skill = pl.getSkillWithIdTemplate(idSkill);

            if (skill == null || skill.level == 0 || skill.mpUse > pl.Point.mp || System.currentTimeMillis() - skill.time < skill.coolDown) {
                return;

            }

            skill.time = System.currentTimeMillis();
            pl.Point.mp -= skill.mpUse / 2;
            Mob mob = null;
            mob = findMobInMap(idMob);
            if (mob == null) {
                return;
            }
            if (mob.hp <= 0 || mob.status == 4) {
                Log.debug("123");
                return;
            }
            if (Utlis.getRange(mob.cx, pl.Info.cx) <= skill.rangeNgang + mob.getMobTemplate().speedMove + 5 && Utlis.getRange(mob.cy, pl.Info.cy) <= skill.rangeDoc + mob.getMobTemplate().speedMove + 5) {
               //  LangLa_iw animationSkill = (LangLa_iw) DataCenter.gI().K.get(new Short((short) idSkill));
                LangLa_iw animationSkill = (LangLa_iw) DataCenter.gI().K.get((short) idSkill);
                if (animationSkill == null) {
                    return;
                }
                XYEntity xy = getXYBlockMap(pl.Info.cx, pl.Info.cy);
                int dir;
                if (xy != null) {
                    dir = 1;
                } else {
                    dir = 0;
                }
                int timeDelayDame = animationSkill.d[dir].length * 30;
                if (timeDelayDame >= skill.coolDown) {
                    timeDelayDame = skill.coolDown - 25;
                }
                if (pl.Info.idClass == 0) {
                    timeDelayDame = 50;
                }
                    /*
                     Lôi
                     Thổ
                     Thủy
                     Hỏa
                     Phong
                     */
                int dameCoBan = pl.damage + pl.attackMonsters;
                int dame = (dameCoBan + skill.getDameMob(pl, mob));
                dame = pl.getDameMob(mob, dame);
                dame = Utlis.nextInt(dame * 90 / 100, dame);
                int crit = pl.critical >= 3000 ? 3000 : pl.critical;
                boolean chi_mang = Utlis.isCriticalHit(crit);
                if (chi_mang) {
                    int num = 80;
                    num += pl.criticalAttack;
                    dame = dame + (dame * num / 100);
                }
                int maxTarget = skill.maxTarget;
                if (skill.level > 17 && (skill.idTemplate == 2 || skill.idTemplate == 8 || skill.idTemplate == 14 || skill.idTemplate == 20 || skill.idTemplate == 26)) {
                    maxTarget = 3;
                }
//
//
//                pl.delaySkill = System.currentTimeMillis() + skill.coolDown;
                boolean doc = Utlis.nextInt(0, 100) < pl.poison / 10;
                boolean bong = Utlis.nextInt(0, 100) < pl.burn / 10;
                boolean suyyeu = Utlis.nextInt(0, 100) < pl.weaken / 10;
                boolean choang = Utlis.nextInt(0, 100) < pl.stun / 10;
                boolean lamcham = Utlis.nextInt(0, 100) < pl.slow / 10;
                if (doc) {
                    mob.AddEff(new Effect((short) 9, 0, System.currentTimeMillis(), 2000), this);
                }
                if (bong) {
                    mob.AddEff(new Effect((short) 11, 0, System.currentTimeMillis(), 2000), this);
                }
                if (suyyeu) {
                    mob.AddEff(new Effect((short) 8, 0, System.currentTimeMillis(), 2000), this);
                }
                if (choang) {
                    mob.AddEff(new Effect((short) 12, 0, System.currentTimeMillis(), 2000), this);
                }
                if (lamcham) {
                    mob.AddEff(new Effect((short) 38, 0, System.currentTimeMillis(), 2000), this);
                }
                if (mob.isSuyYeu) {
                    dame += dame * 30 / 100;
                }
                if (mob.isBong) {
                    dame += dame / 2;
                }
                boolean outLevel = Math.abs(pl.level() - mob.level) <= 10;
                if (mob.levelBoss == 10 && !outLevel && mob.id != 293
                        && mob.id != 294 && mob.id != 285 && mob.id != 286
                        && mob.id != 287 && mob.id != 288 && mob.id != 289) {
                    dame = 1;
                }
//                if (mob.id == 293) {
//                    if (pl.Bag.arrItemBody[16] == null || pl.Bag.arrItemBody[16].id != 748) {
//                        dame = 1;
//                    }
//                }
//                if (mob.id == 294) {
//                    if (pl.Bag.arrItemBody[16] == null || pl.Bag.arrItemBody[16].id != 747) {
//                        dame = 1;
//                    }
//                }
                sendAttackMobToAllChar(pl, mob, idSkill);
                if (pl.cloneLive) {
                    pl.user.session.sendMessage(HanderMessage.msgCloneAttack(pl.Info.idEntity, mob.idEntity));
                    setDameMob(pl, mob, dame / 100 * (pl.Point.diempt == 0 ? 2 : 2 * pl.Point.diempt), chi_mang);
                }
                if (pl.mobBird > 0) {
                    setDameMob(pl, mob, pl.mobBird, false);
                    pl.getService().birdAttackMob(mob.id);
                }
                // pl.setAttackMob(mob, dame, timeDelayDame, chi_mang);
                setDameMob(pl, mob, dame, chi_mang);
                HanderUseSkill.SetEffSkillMob(pl, mob, skill);
                ArrayList<Mob> list = new ArrayList<Mob>();
                Set<Integer> selectedIds = new HashSet<Integer>(); // Để lưu trữ các ID của Mob đã được chọn

                // Giả sử `mob` là Mob hiện tại mà bạn đang xử lý
                selectedIds.add(mob.idEntity); // Thêm ID của Mob hiện tại vào danh sách đã chọn để tránh tự chọn nó

                for (int i = 0; i < maxTarget - 1; i++) { // Đảm bảo vòng lặp chạy đủ số lần để có thể chọn tối đa 3 Mob
                    Mob selectedMob = null;
                    for (Mob cmob : monsters) { // Sử dụng vòng lặp for-each cho độ rõ ràng và dễ đọc
                        // Kiểm tra điều kiện: không phải là Mob hiện tại, HP > 0, và chưa được chọn
                        if (cmob.hp > 0 && !selectedIds.contains(cmob.idEntity)) {
                            if (selectedMob == null || cmob.getRe(pl.Info) < selectedMob.getRe(pl.Info) && cmob.getRe(mob) < selectedMob.getRe(mob)) {
                                selectedMob = cmob;
                            }
                        }
                    }

                    if (selectedMob != null) {
                        list.add(selectedMob);
                        selectedIds.add(selectedMob.idEntity); // Cập nhật ID của Mob đã chọn

                        // Thực hiện hành động với Mob được chọn
                        HanderUseSkill.SetEffSkillMob(pl, selectedMob, skill);
                        if (pl.cloneLive) {
                            pl.user.session.sendMessage(HanderMessage.msgCloneAttack(pl.Info.idEntity, selectedMob.idEntity));
                            setDameMob(pl, selectedMob, dame / 100 * (pl.Point.diempt == 0 ? 2 : 2 * pl.Point.diempt), chi_mang);
                        }
                        setDameMob(pl, selectedMob, dame, chi_mang);
                    }
                }
            } else {
                pl.getService().warningMessage("Khoảng cách quá xa không thể tấn công,Hãy thử tắt auto rồi bật lai!");
                Log.debug("mob: " + mob.cx + " mob cy:" + mob.cy + " mobid: " + mob.id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public XYEntity getXYBlockMap(int var0, int var1) {
        try {
            XYEntity var3;
            if ((var3 = map.getMapTemplate().h.c(var0, var1)) != null && Utlis.positive(var1 - var3.cy) > 1) {
                return var3;
            }
        } catch (Exception var2) {
        }

        return null;
    }

    public XYEntity getXYBlockMapNotCheck(int var0, int var1) {
        try {
            XYEntity var3;
            if ((var3 = map.getMapTemplate().h.c(var0, var1)) != null) {
                return var3;
            }
        } catch (Exception var2) {

        }

        return null;
    }

    public Mob findMobInMap(int idMob) {
        for (int i = 0; i < monsters.size(); i++) {
            Mob mob = monsters.get(i);
            if (mob.idEntity == idMob) {
                return mob;
            }
        }
        return null;
    }

    public Char findCharInMap(int idChar) {
        lockChar.writeLock().lock();
        try {
            for (Char _char : players) {
                if (_char != null && _char.user != null && _char.Info.idEntity == idChar) {
                    return _char;
                }
            }
        } finally {
            lockChar.writeLock().unlock();
        }
        return null;
    }

    public Char findCharInMapByName(String name) {
        lockChar.writeLock().lock();
        try {
            for (Char _char : players) {
                if (_char != null && _char.user != null && _char.Info.name.equals(name)) {
                    return _char;
                }
            }
        } finally {
            lockChar.writeLock().unlock();
        }
        return null;
    }

    public int findIdentityNpc(int id) {
        for (Npc npc : npcs) {
            if (npc.id == id) {
                return npc.idEntity;
            }
        }
        return -1;
    }

    public Npc getNpc(int id) {
        for (Npc npc : npcs) {
            if (npc.id == id) {
                return npc;
            }
        }
        return null;
    }

    public void setDameMob(Char player, Mob mob, int dame, boolean chi_mang) {
        try {
            mob.hp -= dame;
            mob.setHp();
            if (mob.hp <= 0) {
                mob.hp = 0;
                if (player.taskOrders != null) {
                    for (TaskOrder task : player.taskOrders) {
                        if (task.isComplete()) {
                            continue;
                        }
                        if (task.killId == mob.id) {
                            if (task.taskId == TaskOrder.TASK_DAY) {
                                task.updateTask(1);
                            }
                            if (task.taskId == TaskOrder.TASK_BOSS) {
                                task.updateTask(1);
                            }
                        }

                    }
                }
                setMobDie(player, mob);
                player.updateTaskKillMonster(mob);
            }
            sendHpMob(mob.idEntity, mob.hp, chi_mang);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendHpMob(int id, int hp, boolean cm) {
        Message m = new Message((byte) 52);
        try {
            m.writeShort(id);
            m.writeInt(hp);
            m.writeBoolean(cm);
            SendMessageInZone(m);
            m.close();
        } catch (IOException e) {
        } finally {
            if (m != null) {
                m.close();
            }
        }
    }


    public void reSpawnMobToAllChar(Mob mob) {
        Message m = new Message((byte) 57);
        try {
            m.writeShort(mob.idEntity);
            m.writeShort(mob.level);
            m.writeByte(mob.he);
            m.writeInt(mob.hp);
            m.writeInt(mob.hpFull);
            m.writeInt(mob.exp);
            m.writeByte(mob.levelBoss);
            m.writeByte(mob.status);
            SendMessageInZone(m);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (m != null) {
                m.close();
            }
        }
    }

    private void sendAttackMobToAllChar(Char player, Mob mob, int idSkill) {
        Message m = new Message((byte) 61);
        try {
            m.writeInt(player.Info.idEntity);
            m.writeInt(player.Point.mp);
            m.writeShort(player.getSkillWithIdTemplate(idSkill).index);
            m.writeShort(mob.idEntity);
            SendMessageInZone(m);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (m != null) {
                m.close();
            }
        }
    }

    public void openTabZone(Char player) {
        try {
            Writer writer = new Writer();
            writer.writeByte(this.map.getZones().size());
            writer.writeByte(this.zoneID);
            writer.writeByte(this.players.size());
            ArrayList<Zone> listZone = new ArrayList<Zone>();
            for (int i = 0; i < map.getZones().size(); i++) {
                Zone z = map.getZones().get(i);
                if (z.players.size() > 0) {
                    listZone.add(z);
                }
            }
            writer.writeByte(listZone.size());
            for (int i = 0; i < listZone.size(); i++) {

                Zone z = listZone.get(i);
                writer.writeByte(map.getZones().indexOf(z));
                writer.writeByte(z.players.size());
            }
            player.service.sendOpenTabZone(writer);
        } catch (Exception ex) {

        }
    }

    public void changeZone(Char player, byte zoneNext) {
        if (zoneNext == this.zoneID) {
            return;
        }
        if (zoneNext >= 0 && zoneNext < map.getZones().size()) {
            Zone z = map.getZones().get(zoneNext);
            if (z.players.size() < z.MAX_CHAR_INZONE) {
                z.addChar(player);
            } else {

                player.service.setXYChar();
            }
        } else {
            player.service.setXYChar();
        }
    }

    public void setMobDie(Char player, Mob mob) {
        try {
            if (Event.getEvent() != null) {
                if (mob.id == 293 || mob.id == 294) {
                    if (mob.itemBoss != null && !mob.itemBoss.isEmpty()) {
                        for (Item item : mob.itemBoss) {
                            Item it = item.cloneItem();
                            if (!it.strOptions.isEmpty()) {
                                it.createItemOptions();
                            }
                            player.addItem(it);
                        }
                    }
                    monsters.remove(mob);
                }
            }
            if (mob.id == 222 || mob.id == 224 || mob.id == 226 || mob.id == 227 || mob.id == 236 || (mob.id >= 230 && mob.id <= 234)) {
                monsters.remove(mob);
            }
            if (player.taskSeal) {
                if (mob.getMobTemplate().name.equals(player.typeSeal)) {
                    player.getService().serverMessage("Đã hoàn thành nhiệm vụ giết cương thi");
                    player.stepSeal = 1;
                }
            }
            if (player.getGroup() != null) {
                List<Char> charList = player.getGroup().getCharsInZone(player.Info._mapID, player.zone.zoneID);
                if (charList != null) {
                    for (Char plToDoi : charList) {
                        if (plToDoi != player) {
                            if (plToDoi.taskSeal) {
                                if (mob.getMobTemplate().name.equals(plToDoi.typeSeal)) {
                                    plToDoi.stepSeal = 1;
                                    plToDoi.getService().serverMessage("Đã hoàn thành nhiệm vụ giết cương thi");
                                }
                            }
                        }
                    }
                }
            }
            //mob.setItemMap();
            boolean isNhanExp = Math.abs(player.level() - mob.level) <= 5;
            if (isNhanExp || isLangCo) {
                if (mob.level >= 44) {
                    if (mob.levelBoss == 1 && Utlis.nextInt(100) < 50) {
                        Item skn = new Item(434);
                        player.addItem(skn);
                    } else if (mob.levelBoss == 2 && Utlis.nextInt(100) < 50) {
                        Item skn = new Item(434);
                        player.addItem(skn);
                    }
                }
                if (player.Bag.itemSach != null && player.Info.sachChienDau >= 18) {
                    if (player.cloneLive) {
                        player.Point.expsach += 32;
                        if (player.tuLuyenChau) {
                            player.Point.expsach += 32;
                        }
                        if (player.Point.expsach >= ((player.Point.diempt + 1) * 5000000)) {
                            player.Point.expsach = 0;
                            player.Point.diempt++;
                            if (player.Point.diempt >= player.Point.maxpt) {
                                player.Point.diempt = player.Point.maxpt;
                                player.user.session.sendMessage(HanderMessage.SendThongBao("Vui lòng mở giới hạn kỹ năng phân thân", HanderMessage.RED_MID));
                            }
                            player.msgDataBag();
                        }
                    }
                }
                if (player.Bag.arrItemBody[11] != null) {
                    int num = 0;
                    if (mob.levelBoss == 1) {
                        num = 1;
                    } else if (mob.levelBoss == 2) {
                        num = 2;
                    } else if (mob.levelBoss == 10) {
                        num = 5;
                    }
                    if (player.buffKLT > 0 && player.Info._mapID == 84) {
                        num += num * player.buffKLT / 100;
                    }
                    if (player.buffRuou > 0) {
                        num += num * player.buffRuou / 100;
                    }
                    if(num > 0) {
                        player.Bag.arrItemBody[11].updateTuLuyen(num);
                    }
                }
                if (player.Bag.arrItemBody[10] != null && player.Bag.arrItemBody[10].isSucManh()) {
                    int num = 0;
                    if (mob.levelBoss == 1) {
                        num = 1;
                    } else if (mob.levelBoss == 2) {
                        num = 2;
                    } else if (mob.levelBoss == 10) {
                        num = 5;
                    }
                    if (player.buffRuou > 0) {
                        num += num * player.buffRuou / 100;
                    }
                    if (player.tuLuyenChau) {
                        num += num;
                    }
                    player.Bag.arrItemBody[10].updateViThu(num);
                }
                long exp = mob.exp;
                if (player.getExpBuff() > 0) {
                    exp += exp * player.getExpBuff() / 100;
                }
                if (player.buffKLT > 0 && player.Info._mapID == 84) {
                    exp += exp * player.buffKLT / 100;
                }
                if (player.buffRuou > 0) {
                    if (mob.levelBoss == 1) {
                        exp += exp * player.buffRuou / 100;
                    } else if (mob.levelBoss == 2) {
                        exp += exp * player.buffRuou / 100;
                    }
                }
                if (player.getEffect(85) != null) {
                    exp += exp;
                }
                player.addExp(exp);
                try {
                    player.findHuPhach();
                    if (player.huphach != null && !player.huphach.isEmpty()) {
                        for (int i = 0; i < player.huphach.size(); i++) {
                            Item hu = player.huphach.get(i);
                            if (hu != null) {
                                if (hu.addExp((int) exp)) {
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {

                }
                /*hết nhi đồng bật lại*/

                if (player.getGroup() != null) {
                    List<Char> charList = player.getGroup().getCharsInZone(player.Info._mapID, player.zone.zoneID);
                    if (charList != null) {
                        exp = exp * 20 / 100;
//                    for (Char plToDoi : charList) {
//                        if(plToDoi.getChiSoFormSkill(104)>0){
//                            exp += exp * plToDoi.getChiSoFormSkill(104) / 100;
//                        }
//                    }
                        for (Char plToDoi : charList) {
                            if (plToDoi != player) {
                                plToDoi.addExp(exp);
                                if (plToDoi.taskSeal) {
                                    if (mob.getMobTemplate().name.equals(plToDoi.typeSeal)) {
                                        plToDoi.stepSeal = 1;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (mob.getMobTemplate().id >= 199 && mob.getMobTemplate().id <= 203) {
                if (mob.itemBoss != null && !mob.itemBoss.isEmpty()) {
                    for (Item item : mob.itemBoss) {
                        Item it = item.cloneItem();
                        if (!it.strOptions.isEmpty()) {
                            if (Utlis.nextInt(0, 1000) < 5) {
                                it.GetOptionHokage(item);
                            }
                            it.createItemOptions();
                        }
                        player.addItem(it);
                    }
                    Main.HeThongCTG("Nhẫn giả "+ player.Info.name +" đã tiêu diệt được cao thủ nhẫn giả và giành được phần thưởng",2);
                    player.Info.chuyenCan += 50;
                    player.Info.chuyenCanTuan += 50;
                    player.addBacKhoa(10000000);
                    Item da = new Item(9);
                    da.isLock = true;
                    da.amount = 1;
                    player.addItem(da);
                    player.msgAddItemBag(da);
                    if (player.clan != null) {
                        player.addClanPoint(50);
                    }
                    player.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 50 điểm chuyên cần, 50 cống hiến gia tộc", HanderMessage.YELLOW_MID));

                }
                monsters.remove(mob);
                MAX_CHAR_INZONE = 100;
            } else if(mob.getMobTemplate().id >= 251 && mob.getMobTemplate().id <= 259) {
                if (mob.itemBoss != null && !mob.itemBoss.isEmpty()) {
                    for (Item item : mob.itemBoss) {
                        Item it = item.cloneItem();
                        if (!it.strOptions.isEmpty()) {
                            if (Utlis.nextInt(0, 100) < 5) {
                                it.GetOptionHokage(item);
                            }
                            it.createItemOptions();
                        }
                        player.addItem(it);
                    }
                    Main.HeThongCTG("Nhẫn giả "+ player.Info.name +" đã tiêu diệt được vĩ thú và giành được 1 lượt ải gia tộc + phần thưởng",2);
                    player.Info.chuyenCan += 50;
                    player.Info.chuyenCanTuan += 50;
                    player.addBacKhoa(10000000);
                    Item veVanMay = new Item(932);
                    veVanMay.isLock = false;
                    veVanMay.amount = 5;
                    player.addItem(veVanMay);
                    player.msgAddItemBag(veVanMay);
                    if (player.clan != null) {
                        player.addClanPoint(50);
                        player.clan.openDun += 1;
                    }
                    player.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 50 điểm chuyên cần, 50 cống hiến gia tộc", HanderMessage.YELLOW_MID));

                }
                monsters.remove(mob);
                MAX_CHAR_INZONE = 100;
            } else if(mob.getMobTemplate().id == 273) {
                if (mob.itemBoss != null && !mob.itemBoss.isEmpty()) {
                    for (Item item : mob.itemBoss) {
                        Item it = item.cloneItem();
                        if (!it.strOptions.isEmpty()) {
                            if (Utlis.nextInt(0, 100) < 5) {
                                it.GetOptionHokage(item);
                            }
                            it.createItemOptions();
                        }
                        player.addItem(it);
                    }
                    Main.HeThongCTG("Nhẫn giả "+ player.Info.name +" đã tiêu diệt được Madara và giành được 1 lượt ải gia tộc + 50 vé quay",2);
                    player.Info.chuyenCan += 50;
                    player.Info.chuyenCanTuan += 50;
                    player.addBacKhoa(10000000);
                    Item da = new Item(9);
                    da.isLock = true;
                    da.amount = 1;
                    player.addItem(da);
                    player.msgAddItemBag(da);
                    if (player.clan != null) {
                        player.addClanPoint(50);
                        player.clan.openDun += 1;
                    }
                    player.user.session.sendMessage(HanderMessage.SendThongBao("Bạn nhận được 50 điểm chuyên cần, 50 cống hiến gia tộc", HanderMessage.YELLOW_MID));

                }
                monsters.remove(mob);
                MAX_CHAR_INZONE = 100;//cũ là 24
            } else
                getRewardMob(player, mob);

        } catch (Exception e) {
            Log.error("Loi mod die: ", e);
        }
    }


    public void getRewardMob(Char player, Mob mob) {
        try {
            int itemFormTask = player.getIdItemTask(mob.id);
            if (itemFormTask != -1) {
                if (player.taskId == TaskName.NV_NHIEM_VU_CAP_DO_B) {
                    if (player.taskMain != null && player.taskMain.index == 0 && mob.levelBoss == 1) {
                        ItemMap itemMap = new ItemMap((short) id_ENTITY_ITEM_MAP++);
                        itemMap.setY((short) mob.cy);
                        itemMap.setX((short) mob.cx);
                        itemMap.setOwnerID(player.id);
                        itemMap.setItem(new Item(itemFormTask));
                        this.addItemMap(itemMap);
                        Writer writer = new Writer();
                        try {
                            writer.writeShort(mob.idEntity);
                            itemMap.write(writer, -1, mob.cy, this);
                            for (Char pl : this.getChars()) {
                                pl.service.sendItemDropFormMob(writer);
                            }
                        } catch (IOException e) {
                        }
                    }
                } else {
                    ItemMap itemMap = new ItemMap((short) id_ENTITY_ITEM_MAP++);
                    itemMap.setY((short) mob.cy);
                    itemMap.setX((short) mob.cx);
                    itemMap.setOwnerID(player.id);
                    itemMap.setItem(new Item(itemFormTask));
                    this.addItemMap(itemMap);
                    Writer writer = new Writer();
                    try {
                        writer.writeShort(mob.idEntity);
                        itemMap.write(writer, -1, mob.cy, this);
                        for (Char pl : this.getChars()) {
                            pl.service.sendItemDropFormMob(writer);
                        }
                    } catch (IOException e) {
                    }
                }
            }
//
            if (player.level() - mob.level <= 5) {
                if (Utlis.nextInt(100) < 2) { // Có 10% cơ hội thực hiện
                    int bac = 0;
                    int lowerBound = 0, upperBound = 0;

                    if (mob.level < 10) {
                        lowerBound = 900;
                        upperBound = 1200;
                    } else if (mob.level < 20) {
                        lowerBound = 1200;
                        upperBound = 1500;
                    } else if (mob.level < 30) {
                        lowerBound = 1500;
                        upperBound = 1800;
                    } else if (mob.level < 40) {
                        lowerBound = 1800;
                        upperBound = 2400;
                    } else if (mob.level < 60) {
                        lowerBound = 3000;
                        upperBound = 4200;
                    } else if (mob.level <= 70) {
                        lowerBound = 6000;
                        upperBound = 6600;
                    }

                    if (upperBound > 0) { // Đảm bảo rằng upperBound đã được thiết lập
                        bac = Utlis.nextInt(lowerBound, upperBound) * 2;
                        if (player.getEffect(86) != null) {
                            bac += bac;
                        }
                        player.addBacKhoa(bac);
                    }
                }
                boolean isReceive = Math.abs(player.level() - mob.level) <= 5;
                if (isReceive) {
                    if (player.Info._mapID != 84 && player.Info._mapID != 89 && Utlis.nextInt(0, 1000) < 2) {
                        Item item = null;
                        if (mob.level < 20 && mob.level > 10) {
                            item = Manager.gI().tb1x.get(Utlis.nextInt(0, Manager.gI().tb1x.size() - 1));
                        } else if (mob.level < 30) {
                            item = Manager.gI().tb2x.get(Utlis.nextInt(0, Manager.gI().tb2x.size() - 1));
                        } else if (mob.level < 40) {
                            item = Manager.gI().tb3x.get(Utlis.nextInt(0, Manager.gI().tb3x.size() - 1));
                        } else if (mob.level < 50) {
                            item = Manager.gI().tb4x.get(Utlis.nextInt(0, Manager.gI().tb4x.size() - 1));
                        } else if (mob.level < 60) {
                            item = Manager.gI().tb5x.get(Utlis.nextInt(0, Manager.gI().tb5x.size() - 1));
                        }
                        if (item != null) {
                            List<String> option = new ArrayList<>(Arrays.asList(item.strOptions.split(";")));
                            int percent = Utlis.nextInt(0, 100);

                            // Xác định số lượng phần tử cần cắt dựa trên tỷ lệ percent
                            int numElementsToRemove = percent / 10; // Ví dụ: <10% -> 1 phần tử, 10-19% -> 2 phần tử, v.v...

                            // Đảm bảo không cắt quá số lượng phần tử hiện có
                            numElementsToRemove = Math.min(numElementsToRemove, option.size());

                            for (int i = 0; i < numElementsToRemove; i++) {
                                if (!option.isEmpty()) { // Kiểm tra lại để tránh lỗi khi danh sách trống
                                    int randomIndex = Utlis.nextInt(option.size());
                                    option.remove(randomIndex); // Cắt phần tử tại vị trí ngẫu nhiên
                                }
                            }
                            item.strOptions = String.join(";", option);
                            item.createItemOptions();
                            ItemMap itemMap = new ItemMap((short) id_ENTITY_ITEM_MAP++);
                            itemMap.setY((short) mob.cy);
                            itemMap.setX((short) mob.cx);
                            itemMap.setOwnerID(player.id);
                            itemMap.setItem(item);
                            this.addItemMap(itemMap);
                            Writer writer = new Writer();
                            try {
                                writer.writeShort(mob.idEntity);
                                itemMap.write(writer, -1, mob.cy, this);
                                for (Char pl : this.getChars()) {
                                    pl.service.sendItemDropFormMob(writer);
                                }
                            } catch (IOException e) {
                            }
                        }

                    } else if (Event.getEvent() != null && mob.level >= 40) {
                        int itemId = Event.getEvent().randomItemID();
                        if (itemId == -1) {
                            return;
                        }
                        Item item = new Item(itemId);
                        ItemMap itemMap = new ItemMap((short) id_ENTITY_ITEM_MAP++);
                        itemMap.setY(mob.cy);
                        itemMap.setX(mob.cx);
                        itemMap.setOwnerID(player.id);
                        itemMap.setItem(item);
                        this.addItemMap(itemMap);
                        Writer writer = new Writer();
                        try {
                            writer.writeShort(mob.idEntity);
                            itemMap.write(writer, -1, mob.cy, this);
                            for (Char pl : this.getChars()) {
                                pl.service.sendItemDropFormMob(writer);
                            }
                        } catch (IOException e) {
                        }
                    }
                 //   int itemDrop = ItemDrop.ITEM_MAP.next();

                    int itemDrop = this.randomItemID(player, mob);

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
                    sendItemMap(mob, itemMap);
                }
            }
        } catch (Exception e) {
            Log.error("Loi reward mob " + e);
        }
    }

    public int randomItemID(Char player, Mob mob) {
        int itemID = ItemDrop.ITEM_MAP.next();
        if (itemID == 0) {
            itemID = mob.level / 10;
            itemID = itemID > 3 ? 3 : itemID;
        } else if (itemID == 12) {
            if (mob.level < 10) {
                itemID = 12;
            } else if (mob.level < 30) {
                itemID = 13;
            } else if (mob.level < 40) {
                itemID = 14;
            } else if (mob.level < 50) {
                itemID = 15;
            } else {
                itemID = 16;
            }
        } else if (itemID == 17) {
            if (mob.level < 10) {
                itemID = 17;
            } else if (mob.level < 30) {
                itemID = 18;
            } else if (mob.level < 40) {
                itemID = 19;
            } else if (mob.level < 50) {
                itemID = 20;
            } else {
                itemID = 21;
            }
        }
        return itemID;
    }

    public void pickUpItem(Char player, short idEntity) {
        try {
            ItemMap itemMap = this.findItemMapById(idEntity);
            if (itemMap != null) {
                itemMap.lock.lock();
                try {
                    if (itemMap.isPickedUp()) {
                        return;
                    }
                    int distance = Utlis.getRange(player.Info.cx, player.Info.cy, itemMap.getX(), itemMap.getY());
                    if (distance < 300) {
                        Item item = itemMap.getItem();
                        if (item == null) {
                            return;
                        }
                        player.updateTaskPickItem(item);
                        int ownerID = itemMap.getOwnerID();
                        if (ownerID == -1 || player.id == ownerID || itemMap.isCanPickup()) {
                            if (ownerID != -1 && player.id != ownerID) {
                                player.user.session.sendMessage(HanderMessage.SendThongBao("Vật phẩm của người khác", HanderMessage.WHITE));
                                return;
                            }
                            int num = player.getCountNullItemBag();
                            if (item.getItemTemplate().isXepChong) {
                                Item itemBag = player.FindItemBag(item.id);
                                if (itemBag != null && itemBag.index == -1 && num == 0) {
                                    player.user.session.sendMessage(HanderMessage.SendThongBao("Full ruong roi ban oi", HanderMessage.WHITE));
                                    return;
                                }
                            } else {
                                if (num == 0) {
                                    player.user.session.sendMessage(HanderMessage.SendThongBao("Full ruong roi ban oi", HanderMessage.WHITE));
                                    return;
                                }
                            }
                            itemMap.setPickedUp(true);
                            Item clone = item.cloneItem();
                            player.addItem(clone);
                            player.msgUpdateItemBag(clone);
                            try {
                                addItemRemove(itemMap);
                                removeItem();
                                Writer writer = new Writer();
                                writer.writeShort(idEntity);
                                writer.writeInt(player.Info.idEntity);
                                itemMap.getItem().write(writer);
                                for (int i = getChars().size() - 1; i >= 0; i--) {
                                    try {
                                        Char pl = getChars().get(i);
                                        if (pl != null && pl.user != null && pl.service != null)
                                            pl.service.pickUpItem(writer);
                                    } catch (Exception ex) {
                                        Log.error("Loi pick char item " + ex);
                                    }
                                }
                                return;
                            } catch (Exception ex) {

                            }

                        } else {
                            player.user.session.sendMessage(HanderMessage.SendThongBao("Vật phẩm của người khác", HanderMessage.WHITE));

                        }
                    }
                } finally {
                    itemMap.lock.unlock();
                }
            } else {
                Writer writer = new Writer();
                writer.writeShort(idEntity);
                player.service.removeItemMap(writer);
            }
        } catch (Exception e) {

        }
    }

    public void addExpToAllChar(Writer writer) {
        for (int i = getChars().size() - 1; i >= 0; i--) {
            try {
                Char pl = getChars().get(i);
                if (pl != null && pl.user != null && pl.service != null)
                    pl.service.addExp(writer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public void mobAttackChar(Mob mob, Char player) {
        try {
            if (mob.getMobTemplate().type == 8 || mob.getMobTemplate().type == 10) {
                return;
            }

            int dameAdjusted = mob.getDame();
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

    public void updateHpMpWhenAttack(Writer writer) {
        for (Char c : getChars()) {
            try {
                c.service.updateHpMpWhenAttack(writer);
            } catch (Exception ex) {
                Log.error("Loi quai hp mp sau khi danh " + ex);
            }
        }
    }

    public void reSpawn(Writer writer) {
        for (Char c : getChars()) {
            try {
                c.service.reSpawn(writer);
            } catch (Exception ex) {
                Log.error("Loi hoi sinh quai " + ex);
            }
        }
    }

    public int getNumberChar() {
        if (!isClosed) {
            return players.size();
        }
        return 0;
    }

    public void nextMap(Char player) {
        boolean b = false;
        XYEntity xy = player.Info;
        WayPoint waypoint_next = map.getWayPoint(xy);
        if (waypoint_next != null) {
            WayPoint waypoint = map.getWayPoint_WhenInMap(waypoint_next.mapNext);
            if (waypoint != null) {
                b = Map.maps[waypoint_next.mapNext].addChar(player);
                if (b) {
                    player.Info._mapID = waypoint_next.mapNext;
                    try {
                        if (player.idCharPk != -1) {
                            player.service.resuiltTyVo(player.id, (byte) 1);
                            Char pl = ServerManager.findCharById(player.idCharPk);
                            player.InfoGame.TypePk = 0;
                            player.idCharPk = -1;
                            if (pl != null && pl.user != null && !pl.isClean) {
                                pl.service.resuiltTyVo(player.id, (byte) 1);
                                pl.InfoGame.TypePk = 0;
                                pl.idCharPk = -1;
                            }
                        }
                    } catch (Exception e) {
                        player.isCuuSat = false;
                    }
                    if (player.InfoGame.TypePk != 0) {
                        HanderCharacter.SendInfoNextMap(player);
                        //  client.session.sendMessage(HanderMessage.SendTypePk(player.Info.idEntity, player.InfoGame.TypePk));
                    }
                }

            }
        }
        if (!b) {
            player.Info.backXY();
            player.service.setXYChar();
            return;
        }
    }

    public void setXYChar(Writer writer) {
        for (Char c : getChars()) {
            try {
                c.service.setXYChar(writer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addEffect(Writer writer) {
        for (Char c : getChars()) {
            try {
                c.service.addEffect(writer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void removeEffect(Writer writer) {
        for (Char c : getChars()) {
            try {
                c.service.removeEffect(writer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void updateHp_Orther(Char player, Writer writer) {
        for (Char c : getChars()) {
            try {
                if (c != player & c != null && c.user != null) {
                    c.service.updateHp_Orther(writer);
                }
            } catch (Exception ex) {
                Log.error("Loi update hp toan bo player in map " + ex);
            }
        }
    }

    public void updateHpFull_Orther(Char player, Writer writer) {
        for (Char c : getChars()) {
            try {
                if (c != player & c != null && c.user != null) {
                    c.service.updateHpFull_Orther(writer);
                }
            } catch (Exception ex) {
                Log.error("Loi update hp full toan bo player in map " + ex);
            }
        }
    }

    public void updateMp_Orther(Char player, Writer writer) {
        for (Char c : getChars()) {
            try {
                if (c != player & c != null && c.user != null) {
                    c.service.updateMp_Orther(writer);
                }
            } catch (Exception ex) {
                Log.error("Loi update mp toan bo player in map " + ex);
            }
        }
    }

    public void updateMpFull_Orther(Char player, Writer writer) {
        for (Char c : getChars()) {
            try {
                if (c != player & c != null && c.user != null) {
                    c.service.updateMpFull_Orther(writer);
                }
            } catch (Exception ex) {
                Log.error("Loi update mp full toan bo player in map " + ex);
            }
        }
    }

    public void updateItemBody_Orther(Char player, Writer writer) {
        for (Char c : getChars()) {
            try {
                if (c != player & c != null && c.user != null) {
                    c.service.updateItemBody_Orther(writer);
                }
            } catch (Exception ex) {
                Log.error("Loi update item body toan bo player in map " + ex);
            }
        }
    }


    public void updateStatusChar(Writer writer) {
        for (Char c : getChars()) {
            try {
                c.service.updateStatusChar(writer);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void update() {
        try {
            if (!isClosed) {
                this.updateMob();
                this.updateItemMap();
            }
        } catch (Exception var14) {
            Log.error("loi update zone", var14);
        }

    }

    public void close() {
        isClosed = true;
    }

    public void sendModToNewChar(Char pl) {
        for (Mob mob : monsters) {
            if (mob != null && !mob.isDie) {
                Message m = new Message((byte) 57);
                try {
                    m.writeShort(mob.idEntity);
                    m.writeShort(mob.level);
                    m.writeByte(mob.he);
                    m.writeInt(mob.hp);
                    m.writeInt(mob.hpFull);
                    m.writeInt(mob.exp);
                    m.writeByte(mob.levelBoss);
                    m.writeByte(mob.status);
                    pl.user.session.sendMessage(m);
                } catch (Exception e) {

                }
            }
        }
    }

    public boolean isWorld() {
        return isCamThuat() || isKLT() || isDiaCung() || isKRC() || isDungeoClan() || isSonCapMyo() || isDungeonSummer();
    }

    public boolean isDungeonSummer() {
        return map.mapID == 67;
    }

    public boolean isCamThuat() {
        return map.mapID == 89;
    }

    public boolean isKLT() {
        return map.mapID == 84;
    }

    public boolean isKRC() {
        return map.mapID == 17 || map.mapID == 31;
    }

    public boolean isDiaCung() {
        return map.getMapTemplate().type == 4;
    }

    public boolean isLoiDai() {
        return map.mapID == 44 || map.mapID == 45;
    }

    public int getNumberGroup() {
        List<Char> chars = getChars();
        java.util.Map<Group, List<Char>> map = Utlis.groupBy(chars, Char::getGroup);
        return map.size();
    }

    public boolean isDungeoClan() {
        return map.mapID == 46 || map.mapID == 47;
    }

    public boolean isSonCapMyo() {
        return map.getMapTemplate().type == 17;
    }

    public boolean isTranhDoatLanhTho() {
        return map.getMapTemplate().type == 16;
    }

    public Char findCharName(String name) {
        lockChar.readLock().lock();
        try {
            for (Char c : players) {
                if (c.user != null && c.user.session != null && c.Info.name.equals(name)) {
                    return c;
                }
            }
        } finally {
            lockChar.readLock().unlock();
        }
        return null;
    }
}


