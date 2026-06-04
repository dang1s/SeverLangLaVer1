/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Service;

import MapService.Map;
import MapService.world.DaiChienNhanGia3;
import MapService.world.World;
import com.sg188.data.DataCenter;
import com.sg188.data.Skill;
import com.sg188.data.SkillTemplate;
import com.sg188.lib.Log;
import com.sg188.lib.ProfanityFilter;
import com.sg188.lib.Utlis;
import com.sg188.real.*;
import com.sg188.server.ServerManager;
import com.sg188.server.handler.ICharacterHander;
import com.sg188.server.lib.Message;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ADMIN
 */
public class HanderCharacter {
    public static int delayChat = 30000;
    public static ProfanityFilter profanityFilter = new ProfanityFilter();

    public static void SetHpChar(Char _myChar, int hp, int mp) {
        _myChar.addHp(-hp);
        _myChar.addMp(-mp);
        if (_myChar.Point.hp < 0) {
            _myChar.InfoGame.isDie = true;
            _myChar.Point.hp = 0;
        }
    }

    public static void SetTypePk(Char _myChar, byte type) {
        if(_myChar.buaBaoHo&&!_myChar.inLangCo&&!_myChar.inHangViThu){
            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Đang trong trạng thái bảo hộ không thể pk",HanderMessage.WHITE));
            return;
        }
        if(_myChar.zone.isKRC()){
            _myChar.user.session.sendMessage(HanderMessage.SendThongBao("Không thể thay đổi trạng thái tại map này",HanderMessage.WHITE));
            return;
        }
        _myChar.InfoGame.TypePk = type;
        _myChar.zone.SendMessageInZone(HanderMessage.SendTypePk(_myChar.Info.idEntity, type));
    }

    public static boolean CanAttackChar(Char _myChar,Char attacker) {
        try {
            if (_myChar.zone != null && _myChar.zone.isDaiChienNhanGia3()) {
                DaiChienNhanGia3 event = (DaiChienNhanGia3) _myChar.findWorld(World.DAI_CHIEN_NHAN_GIA_3);
                if (event != null) {
                    return event.canAttackPlayer(attacker, _myChar);
                }
            }
        } catch (Exception ignored) {
        }
        if(_myChar.zone.isKRC()||_myChar.inLangCo||_myChar.inHangViThu){
            if(_myChar.InfoGame.isDie){
                return false;
            }
            if(attacker.clan != null&&_myChar.clan!=null){
                if(attacker.clan.id == _myChar.clan.id){
                    return false;
                }
            }
            if(attacker.getGroup() != null&&_myChar.getGroup()!=null){
                if(attacker.getGroup()==_myChar.getGroup()){
                    return false;
                }
            }
            return true;
        }
        if(_myChar.buaBaoHo){
            attacker.getService().serverMessage("Đối phương đang có bùa bảo hộ");
        }
        if(attacker.InfoGame.TypePk == 3&&!_myChar.InfoGame.isDie&&!_myChar.buaBaoHo){
            if(attacker.clan != null&&_myChar.clan!=null){
                if(attacker.clan.id == _myChar.clan.id){
                    return false;
                }
            }
            if(attacker.getGroup() != null&&_myChar.getGroup()!=null){
                if(attacker.getGroup()==_myChar.getGroup()){
                    return false;
                }
            }
            return true;
        }
        return ((_myChar.InfoGame.TypePk != 0||_myChar.isCuuSat) && !_myChar.InfoGame.isDie&&!_myChar.buaBaoHo);
    }
    public static void SendInfoNextMap(Char _myChar) {
        for (Char cSend : _myChar.zone.getChars()) {
            if (cSend != null && cSend.user != null) {
                if (cSend.InfoGame.TypePk != 0) {
                    cSend.user.session.sendMessage(HanderMessage.SendTypePk(cSend.Info.idEntity, cSend.InfoGame.TypePk));
                }
            }
        }
    }

    public static void ShowThongTin(Char _myChar,Message msg)
    {
        try {
            String nameInfo = msg.readUTF();
            if(_myChar.Info.name.equals(nameInfo))
            {
                _myChar.writeInfo();
            }else{
                Char pl = ServerManager.findCharByName(nameInfo);
                if(pl!=null)
                    _myChar.writeInfoOrther(pl);
            }
        } catch (IOException ex) {
            Logger.getLogger(HanderCharacter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
