/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.real;

import MapService.Zone;
import Service.HanderEff;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sg188.data.DataCenter;
import com.sg188.data.EffectTemplate;
import com.sg188.lib.Log;
import com.sg188.server.lib.Writer;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Vector;

/**
 *
 * @author admin
 */
public class Effect {

    public short id;
    public int value;
    public long timeStart;
    public int maintain;
    public Vector vEffect = new Vector();
    private long delay;
    @JsonIgnore
    public EffectTemplate effectTemplate;

    public Effect(){

    }
    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("id", this.id);
        obj.put("value", this.value);
        obj.put("timeStart", this.timeStart);
        obj.put("maintain", this.maintain);
        return obj;
    }
    public Effect(short id, int value, long timeStart, int maintain) {
        this.id = id;
        this.value = value;
        this.timeStart = timeStart;
        this.maintain = maintain;
    }


    public void write(Writer writer) throws IOException {
        writer.writeShort(id);
        writer.writeInt(value);
        writer.writeLong(timeStart);
        writer.writeInt(maintain);

    }

    public long getMaintain() {
        return maintain - (System.currentTimeMillis() - timeStart);
    }

    public EffectTemplate getEffectTemplate() {
        return DataCenter.gI().EffectTemplate[id];

    }

    private void UpdatePointChar(Char _myChar) {
        switch (id) {
            case 60:
                _myChar.Point.hpFull -= value;
                break;
        }
    }

    public void updateMob(Mob mob, Zone zone) {
        try {
            long l = System.currentTimeMillis();
            if (l - timeStart >= maintain) {
                mob.RemoveEff(this, zone);
                mob.removeEff(this);
                return;
            }
            EffectTemplate eff = getEffectTemplate();
            switch (eff.id) {
                case 8:
                    mob.isSuyYeu = true;
                    break;
                case 9:
                    if (System.currentTimeMillis() - delay >= 500) {
                        int dame = mob.hp / 100;
                        if (mob.hp - dame > 0) {
                            mob.hp -= dame;
                            zone.sendHpMob(mob.idEntity, mob.hp, false);
                        }
                        delay = System.currentTimeMillis();
                    }
                    break;
                case 11:
                    mob.isBong = true;
                    break;

            }
        }catch (Exception e){
            Log.error("loi update o effect ",e);
        }
    }

    public void update(Char aThis) {
        try {
            long l = System.currentTimeMillis();
            if (l - timeStart >= maintain) {
                aThis.removeEffect(this);
                HanderEff.RemovePointEff(aThis, this);
                aThis.msgRemoveEffect(this);
                return;
            }
            if (aThis.Point.hp <= 0) {
                return;
            }
            if (aThis == null || aThis.user == null) {
                return;
            }
            EffectTemplate eff = getEffectTemplate();
            if (eff.type == 6 || eff.type == 7) {
                if (l - timeStart > 3000 || maintain > 3000) {
                    maintain = 0;
                    aThis.removeEffect(this);
                    HanderEff.RemovePointEff(aThis, this);
                    aThis.msgRemoveEffect(this);
                }
            }
            switch (eff.type) {
                case 0:
                    if (System.currentTimeMillis() - delay >= 500) {
                        aThis.addHp(value);
                        aThis.addMp(value);

                        delay = System.currentTimeMillis();
                    }
                    break;
                case 6:
                    if (System.currentTimeMillis() - delay >= 500) {
                        aThis.addHp(value);

                        delay = System.currentTimeMillis();
                    }
                    break;
                case 7:
                    if (System.currentTimeMillis() - delay >= 500) {
                        aThis.addMp(value);
                        delay = System.currentTimeMillis();
                    }
                    break;
                case 51:
                    if (System.currentTimeMillis() - delay >= 2000) {
                        aThis.addHp(value);
                        delay = System.currentTimeMillis();
                    }
                    break;
                case 59:
                    if (System.currentTimeMillis() - delay >= 2000) {
                        aThis.addHp(value);
                        delay = System.currentTimeMillis();
                    }
                    break;
            }

        }catch (Exception e){
            Log.error("Loi update effect",e);
        }
    }

    public static int getValueEffectFormIdItem(int id) {
        switch (id) {
            /*Thức ăn*/
            case 22:
                return 7;
            case 23:
                return 27;
            case 24:
                return 36;
            case 25:
                return 45;
            case 26:
                return 54;
            case 27:
                return 63;
            case 238:
                return 72;
            case 239:
                return 81;
            case 240:
                return 90;
            case 241:
                return 99;
            case 242:
                return 108;
            case 243:
                return 117;
            /*Hp*/
            case 12:
            case 17:
                return 50;
            case 13:
            case 18:
                return 160;
            case 14:
            case 19:
                return 370;
            case 15:
            case 20:
                return 580;
            case 16:
            case 21:
                return 790;
            case 219:
            case 221:
                return 940;
            case 220:
            case 222:
                return 1100;
        }
        return 0;
    }

}
