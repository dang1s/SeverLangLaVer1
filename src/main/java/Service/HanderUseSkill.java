/*

 * To change this license header, choose License Headers in Project Properties.

 * To change this template file, choose Tools | Templates

 * and open the template in the editor.

 */

package Service;



import InfoChar.InfoPoint;



import MapService.Map;

import com.sg188.data.ItemOption;

import com.sg188.data.Skill;

import com.sg188.data.SkillTemplate;

import com.sg188.lib.Utlis;

import com.sg188.real.Char;

import com.sg188.real.Effect;

import com.sg188.real.Mob;



import java.util.List;



import static Service.HanderCharacter.CanAttackChar;



/**

 *

 * @author ADMIN

 */

public class HanderUseSkill {



    public static void HanderSkillNotFocus(Char _myChar, Skill skill) {

        String[] option = skill.strOptions.split(";");

        long timeUse = skill.time;

        short idEff = -1;

        short idEff2 = -1;

        int seconds = -1;

        int value = -1;

        int value2 = -1;

        if(timeUse > System.currentTimeMillis()){

            return;

        }

        if(_myChar.Point.mp<skill.mpUse){

            _myChar.service.serverMessage("Không đủ mana để sử dụng chiêu");

            return;

        }

        _myChar.Point.mp -= skill.mpUse/2;

        _myChar.msgUpdateMp();

        skill.time = System.currentTimeMillis()+ skill.coolDown;

        switch (skill.idTemplate) {

            /*case 7:

                value = Short.parseShort(option[0].split(",")[1]);

                _myChar.addEffect(new Effect((short) 68, value, System.currentTimeMillis(), 20000));

                break;

            case 10:// 10 triệu hồi chim chi thuật

                int seconds3 = Integer.parseInt(option[0].split(",")[1]);

                value = Short.parseShort(option[1].split(",")[1]);

                if(option.length > 2)

                    value2 = Short.parseShort(option[2].split(",")[1]);

                _myChar.addEffect(new Effect((short) 59, value, System.currentTimeMillis(), seconds3));

                _myChar.addEffect(new Effect((short) 60, value2, System.currentTimeMillis(), seconds3));

                break;

            case 5: // 5 triệu hồi dơi chi thuật

                if (skill.idTemplate == 5) {

                    idEff2 = 62;

                    value = Integer.parseInt(option[0].split(",")[1]);

                    long time = System.currentTimeMillis() + value;

                    _myChar.addEffect(new Effect((short) 62, value, System.currentTimeMillis(), value));

                    _myChar.addEffect(new Effect((short) 63, value, System.currentTimeMillis(), Integer.parseInt(option[2].split(",")[1])));



                }

                break;

            case 16://tăng sinh chi thuật

                value = Short.parseShort(option[0].split(",")[1]);

                _myChar.addHp(value);

                if(_myChar.getGroup()!=null){

                    List<Char>charList = _myChar.getGroup().getCharsInZone(_myChar.Info._mapID,_myChar.zone.zoneID);

                    for (Char pl: charList){

                        if(pl!=null && pl.user != null&&pl!=_myChar){

                            pl.addHp(value);

                        }

                    }

                }

                break;

            case 19:

            case 23: // 19 Byakugan,23 triệu hồi chim yêu chi thuật

                idEff = (short) (skill.idTemplate == 19 ? 31 : 57);

                seconds = (short) (skill.idTemplate == 19 ? 54 : 60);

                if(idEff == 57){

                    value = Short.parseShort(option[1].split(",")[1]);

                }

                else

                    value = 1;

                _myChar.addEffect(new Effect((short) idEff, value, System.currentTimeMillis(), seconds*1000));

                break;

            case 28:

                value = Short.parseShort(option[0].split(",")[1]);

                _myChar.addEffect(new Effect((short) 51, value, System.currentTimeMillis(), 15000));

                _myChar.Info.speedMove += value;

                _myChar.msgUpdateStatusChar();

                break;

            case 29: //

                idEff = 64;

                int sec = Integer.parseInt(option[0].split(",")[1]);

                value = Short.parseShort(option[1].split(",")[1]);

                _myChar.addEffect(new Effect((short) idEff, value, System.currentTimeMillis(), sec));

                break;

            case 35:

                _myChar.addEffect(new Effect((short) 107, 100, System.currentTimeMillis(), 1000*30));

                break;

            case 25:

                value = Integer.parseInt(option[0].split(",")[1]);

                value2 = Integer.parseInt(option[1].split(",")[1]);

                _myChar.addEffect(new Effect((short) 74, value, System.currentTimeMillis(), 1000*10));

                _myChar.addEffect(new Effect((short) 77, value2, System.currentTimeMillis(), 1000*10));

                _myChar.msgUpdateMpFull();

                break;

            case 22:

                for (ItemOption op: skill.getItemOption()){

                    if(op.getId() == 178){

                        value = op.getvalue();

                        _myChar.addEffect(new Effect((short) 53, 0, System.currentTimeMillis(), op.getvalue()));

                    }

                    if(op.getId()==179)

                        _myChar.isFatal=op.getvalue();

                    if(op.getId()==180)

                        _myChar.addEffect(new Effect((short) 54, op.getvalue(), System.currentTimeMillis(), value));

                }

                break;

            case 31://bách hào chi thuật

                value = Integer.parseInt(option[0].split(",")[1]);

                value2 = Integer.parseInt(option[1].split(",")[1]);

                _myChar.addEffect(new Effect((short) 69, value, System.currentTimeMillis(), 1000*10));

                _myChar.addEffect(new Effect((short) 70, value2, System.currentTimeMillis(), 1000*10));

                _myChar.msgUpdateHp();

                break;

            case 13://bya nữ

                value = Integer.parseInt(option[0].split(",")[1]);

                value2 = Integer.parseInt(option[1].split(",")[1]);

                _myChar.addEffect(new Effect((short) 72, value, System.currentTimeMillis(), 1000*10));

                _myChar.addEffect(new Effect((short) 73, value2, System.currentTimeMillis(), 1000*10));

                break;*/

            case 7: // 7 susano

                value = Short.parseShort(option[0].split(",")[1]);

                _myChar.addEffect(new Effect((short) 68, value, System.currentTimeMillis(), 20000));

                break;

            case 10:// 10 triệu hồi chim chi thuật

            {

                int seconds3 = Integer.parseInt(option[0].split(",")[1]);

                _myChar.addEffect(new Effect((short) 59, Short.parseShort(option[1].split(",")[1]), System.currentTimeMillis(), seconds3)); // hồi hp

                _myChar.addEffect(new Effect((short) 60, Short.parseShort(option[2].split(",")[1]), System.currentTimeMillis(), seconds3)); // tăng hp

            }

            break;

            case 5: // 5 triệu hồi dơi chi thuật

                _myChar.addEffect(new Effect((short) 62, Short.parseShort(option[1].split(",")[1]), System.currentTimeMillis(), Integer.parseInt(option[0].split(",")[1])));

                _myChar.addEffect(new Effect((short) 63, Short.parseShort(option[2].split(",")[1]), System.currentTimeMillis(), Integer.parseInt(option[0].split(",")[1])));

                break;

            case 16:

                _myChar.addEffect(new Effect((short) 30, Short.parseShort(option[0].split(",")[1]), System.currentTimeMillis(), 5000));

                break;

            case 19: // 19 Byakugan

                _myChar.removeEffect(9);

                _myChar.removeEffect(11);

                _myChar.removeEffect(8);

                _myChar.removeEffect(12);

                _myChar.removeEffect(38);

                _myChar.addEffect(new Effect((short) 31, 0, System.currentTimeMillis(), Short.parseShort(option[0].split(",")[1])));

                _myChar.addEffect(new Effect((short) 73, Short.parseShort(option[1].split(",")[1]), System.currentTimeMillis(), Integer.parseInt(option[0].split(",")[1])));

                break;

            case 23: //23 triệu hồi chim yêu chi thuật

                _myChar.mobBird = 1;

                _myChar.addEffect(new Effect((short) 57, Short.parseShort(option[1].split(",")[1]), System.currentTimeMillis(), Integer.parseInt(option[0].split(",")[1])));

                _myChar.addEffect(new Effect((short) 58, Short.parseShort(option[2].split(",")[1]), System.currentTimeMillis(), Integer.parseInt(option[0].split(",")[1])));

                break;

            case 28:

                value = Short.parseShort(option[0].split(",")[1]);

                _myChar.addEffect(new Effect((short) 51, value, System.currentTimeMillis(), 15000));

                _myChar.Info.speedMove += value;

                _myChar.msgUpdateStatusChar();

                break;

            case 29: //

                int sec = Integer.parseInt(option[0].split(",")[1]);

                value = Short.parseShort(option[1].split(",")[1]);

                _myChar.addEffect(new Effect((short) 64, value, System.currentTimeMillis(), sec));

                _myChar.addEffect(new Effect((short) 65, Short.parseShort(option[2].split(",")[1]), System.currentTimeMillis(), sec));

                break;

            case 35:

                _myChar.addEffect(new Effect((short) 107, 10, System.currentTimeMillis(), 1000 * 30));

                break;

            case 25:

                value = Integer.parseInt(option[0].split(",")[1]);

                value2 = Integer.parseInt(option[1].split(",")[1]);

                _myChar.addEffect(new Effect((short) 74, value, System.currentTimeMillis(), 1000 * 10));

                _myChar.addEffect(new Effect((short) 77, value2, System.currentTimeMillis(), 1000 * 10));

                _myChar.msgUpdateMpFull();

                break;

            case 22:

                value = Integer.parseInt(option[0].split(",")[1]);

                for (ItemOption op : skill.getItemOption()) {

                    if (op.getId() == 178) {

                        value = op.getvalue();

                        _myChar.addEffect(new Effect((short) 53, 0, System.currentTimeMillis(), op.getvalue()));

                    }

                    if (op.getId() == 179)

                        _myChar.isFatal = op.getvalue();

                    if (op.getId() == 180)

                        _myChar.addEffect(new Effect((short) 54, op.getvalue(), System.currentTimeMillis(), value));

                }

                break;

            case 31:

                _myChar.addEffect(new Effect((short) 69, Integer.parseInt(option[0].split(",")[1]), System.currentTimeMillis(), 10000));

                _myChar.addEffect(new Effect((short) 70, Integer.parseInt(option[1].split(",")[1]), System.currentTimeMillis(), 10000));

                break;

            case 13:

                _myChar.addEffect(new Effect((short) 72, 0, System.currentTimeMillis(), Integer.parseInt(option[0].split(",")[1])));

                _myChar.addEffect(new Effect((short) 73, Integer.parseInt(option[1].split(",")[1]), System.currentTimeMillis(), 10000));

                break;

        }



    }



    public static void SetEffSkillMob(Char _myChar, Mob _mob, Skill skill) {

        // Log.debug("OPTIONS " + skill.strOptions);

        String[] option = skill.strOptions.split(";");

        int ID = skill.idTemplate;

        short IdEff = -1;

        short value = -1;

        short value2 = -1;

        // Log.debug("ID " + ID);

        switch (ID) {

            case SkillTemplate.DICH_CHUYEN_CHI_THUAT:

                int seconds3 = Integer.parseInt(option[2].split(",")[1]);

                value = Short.parseShort(option[1].split(",")[1]);

                value2 = Short.parseShort(option[2].split(",")[1]);

                _mob.IsTeLiet = true;

                _mob.AddEff(new Effect((short) 36, 1, System.currentTimeMillis(), seconds3), _myChar.zone);

                break;

            case SkillTemplate.AN_CHU_CHI_THUAT:

                _mob.AddEff(new Effect((short) 55, 1, System.currentTimeMillis(), 3000), _myChar.zone);

                break;

            case SkillTemplate.ANH_THU_PHUOC_CHI_THUAT:

                _mob.AddEff(new Effect((short) 71, 1, System.currentTimeMillis(), Short.parseShort(option[1].split(",")[1])), _myChar.zone);

                break;

            case SkillTemplate.BIET_THIEN_THAN:

                _mob.IsBietThienThan = true;

                _mob.AddEff(new Effect((short) 94, 1, System.currentTimeMillis(), Short.parseShort(option[1].split(",")[1])), _myChar.zone);

                break;

        }

    }

}

