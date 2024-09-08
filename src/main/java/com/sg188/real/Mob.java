/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.real;

import Service.HanderEff;
import com.sg188.data.DataCenter;
import com.sg188.data.MobTemplate;
import com.sg188.lib.Log;
import com.sg188.lib.Utlis;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Writer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import MapService.*;

import javax.swing.plaf.PanelUI;

/**
 * @author admin
 */
public class Mob extends Entity implements Cloneable {

    public int id;
    public int hp;
    public int hpGoc;
    public int hpFull;

    public int he;
    public int exp;
    public int expGoc;
    public int level;
    public int levelBoss;
    public boolean paintMiniMap;
    public boolean isDie;
    public boolean isReSpawn;
    public long timeDie;
    public long delayAttack;
    public String nameChar = "";
    public long timeRemove = -1;
    public List<Effect> Effs = new ArrayList<>();

    /* Hiệu Ứng khống chế*/
    public boolean IsTeLiet;
    public boolean IsBietThienThan;
    public boolean isSuyYeu;
    public boolean isBong;
    public boolean attacked;
    public String nameCreate;
    public List<Item> itemBoss = new ArrayList<>();
    private int damageOnPlayer;
    private int damageOnPlayer2;

    public boolean CanAttack() {
        return !IsTeLiet && !IsBietThienThan;
    }

    public Lock lockEffect = new ReentrantLock();
    public Zone zone;

    public Mob() {

    }

    public Mob(int id) {
        this.id = id;
    }

    public void AddEff(Effect Eff, Zone zone) {
        lockEffect.lock();
        try {
            Effs.add(Eff);
            Message m = new Message((byte) 15);
            try {
                m.writeShort(idEntity);
                m.writeShort(Eff.id);
                m.writeInt(Eff.value);
                m.writeLong(Eff.timeStart);
                m.writeInt(Eff.maintain);
                zone.SendMessageInZone(m);
                m.close();
            } catch (IOException ex) {
                Logger.getLogger(Mob.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (m != null) {
                    m.close();
                }
            }
        } catch (Exception e) {

        } finally {
            lockEffect.unlock();
        }
    }

    public Mob cloneMob() {
        try {
            Mob cloned = (Mob) super.clone();
            cloned.Effs = new ArrayList<>();
            return cloned;
        } catch (Exception var2) {
            Utlis.println(var2);
            return null;
        }
    }

    public void write(Writer writer) throws IOException {
        writer.writeShort(this.idEntity);
        writer.writeBoolean(paintMiniMap);
        writer.writeUTF(nameChar);
        writer.writeShort(this.id);
        this.writeXY(writer);
        writer.writeShort(this.level);
        writer.writeByte(he);
        writer.writeByte(getStatus());
        writer.writeInt(hp);
        writer.writeInt(hpFull);
        writer.writeInt(exp);
        writer.writeByte(levelBoss);
        writeEffect(writer);
    }

    public static Mob mobTask(short id, String name, short cy, short cx) {
        Mob mob = new Mob();
        mob.idEntity = Utlis.nextInt(999, 5000);
        mob.id = id;
        mob.nameChar = name;
        mob.hpFull = mob.hp = 1000;
        mob.exp = 1000;
        mob.level = 10;
        mob.cy = cy;
        mob.cx = cx;
        mob.status = 0;
        mob.isReSpawn = false;
        mob.timeRemove = System.currentTimeMillis() + 300000;
        return mob;
    }

    public void UpdateEff(Zone zone) {
        try {
            for (int i = 0; i < Effs.size(); i++) {
                Effect effect = Effs.get(i);
                if (effect != null)
                    effect.updateMob(this, zone);
            }
        } catch (Exception e) {
            Log.error("loi update eff mob", e);
        }
    }

    public void removeEff(Effect effect) {
        lockEffect.lock();
        try {
            Effs.remove(effect);
        } catch (Exception e) {

        } finally {
            lockEffect.unlock();
        }
    }

    public void RemoveEff(Effect eff, Zone zone) {
        HanderEff.RemoveEffMob(this, eff);
        try {
            Message m = new Message((byte) 16);
            m.writeShort(idEntity);
            m.writeShort(eff.id);
            zone.SendMessageInZone(m);
            m.close();
        } catch (IOException ex) {
            Logger.getLogger(Mob.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int getStatus() {
        return !paintMiniMap && hp > 0 ? 0 : 4;
    }

    private void writeEffect(Writer writer) throws IOException {
        writer.writeByte(0);
    }

    public void setHp() {
        if (hp <= 0) {
            hp = 0;
            status = 4;
            isDie = true;
            isReSpawn = true;
            isBong = false;
            isSuyYeu = false;
            Effs.clear();
            this.timeDie = System.currentTimeMillis();
        } else {
            status = 0;
            isDie = false;
            isReSpawn = false;
            this.timeDie = 0l;
        }
    }

    public void reSpawn(Zone zone) {
        hp = hpFull = hpGoc;
        he = Utlis.nextInt(1, 5);
        exp = expGoc;
        if(levelBoss < 3) {
            if (zone.isSonCapMyo()) {
                levelBoss = 0;
            } else {
                int num = Utlis.nextInt(0, 20000);
                if (num < 10) {
                    hp = hpFull = hpGoc * 100;
                    exp = expGoc * 100;
                    levelBoss = 2;
                } else if (num < 100) {
                    hp = hpFull = hpGoc * 10;
                    levelBoss = 1;
                    exp = expGoc * 10;
                } else {
                    levelBoss = 0;
                }
            }
        }
        setHp();
    }

    public ArrayList<ItemMap> listItemMap = new ArrayList<ItemMap>();


    public MobTemplate getMobTemplate() {
        return DataCenter.gI().MobTemplate[id];
    }

    public int getDame() {
        this.damageOnPlayer = (int) (this.level + (Math.pow(this.level, 2) / 5));
        if (this.levelBoss==10) {
            this.damageOnPlayer *= 20;
        } else if (this.levelBoss == 1) {
            this.damageOnPlayer *= 2;
        } else if (this.levelBoss == 2) {
            this.damageOnPlayer *= 3;
        }
        return this.damageOnPlayer2 = this.damageOnPlayer - this.damageOnPlayer / 10;
    }

}
