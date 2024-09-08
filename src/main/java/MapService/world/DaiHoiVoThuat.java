package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.AttendanceArea;
import MapService.zones.ZoneDaihoi;
import Service.HanderMessage;
import Template.TemplateThu;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DaiHoiVoThuat extends World{
    public static DaiHoiVoThuat DAIHOI;
    public List<Char>listPlayerInMap;
    public ArrayList<Char> viewers;

    public boolean isOpened;
    private boolean finished;
    public long timeStart;
    public boolean qualifierRound;
    public boolean groupStage;
    public List<Char>listGroupStage;
    public boolean quarterFinals;
    public List<Char>listQuarterFinals;

    public boolean semiFinals;
    public List<Char>listSemiFinals;
    public boolean finalRound;
    public List<Char>listFinalRound;
    public DaiHoiVoThuat() {
        setType(World.DAI_HOI_VO_THUAT);
        this.name = "DaiHoiVoThuat";
        this.countDown = 600;
        listPlayerInMap=new ArrayList<>();
        viewers= new ArrayList<>();
        Map map = new Map(49);
        for (int i = 0; i < 21; i++) {
            ZoneDaihoi z = new ZoneDaihoi(map,i);
            z.setWorld(this);
            map.getZones().add(z);
            zones.add(z);
        }

        initFinished = true;
    }
    public void join(int team, Char _char) {
        if(zones!=null) {
            for (Zone zone : zones) {
                if (zone.players.size() < zone.MAX_CHAR_INZONE) {
                    zone.addChar(_char);
                    break;
                }
            }
        }
        if(isOpened){
            _char.service.sendTimeInMap(getCountDown(),true,timeStart);
        }
        lock.writeLock().lock();
        try {
            if (team == 1) {
                listPlayerInMap.add(_char);
                _char.setXY((short) 458, (short) 450);
                _char.service.setXYChar();
            }
            if (team == 2) {
                this.viewers.add(_char);
                _char.setXY((short) (500+ Utlis.nextInt(-200,200)), (short) 566);
                _char.service.setXYChar();
//                _char.setTypePk(Char.PK_NORMAL);
//                _char.zone.getService().changePk(_char);
            }
        } finally {
            lock.writeLock().unlock();
        }
        if (!isOpened) {
            addMember(_char);
        }
    }
    public void out(Char _char) {
        try {
            if (!isClosed) {

            }
            lock.writeLock().lock();
            try {
                listPlayerInMap.remove(_char);
                viewers.remove(_char);
            } finally {
                lock.writeLock().unlock();
            }
            _char.InfoGame.TypePk = 0;
            _char.service.sendMessage(HanderMessage.SendTypePk(_char.id, (byte)0));
            removeMember(_char);
//            Zone z = zone;
//            z.removeChar(_char);
            _char.removeWorld(World.DAI_HOI_VO_THUAT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void update() {
//        try {
//           for (Zone zone: zones){
//                zone.update();
//           }
//            if (countDown <= 0) {
//                if (!isOpened) {
//                    qualifierRound=true;
//                    isOpened = true;
//                    setCountdown(600);
//                    timeStart= System.currentTimeMillis();
//                    List<Char>list = getMembers();
//                    for(Char pl:list){
//                        if(pl!=null&&pl.user!=null&&!pl.isClean){
//                            pl.zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 2));
//                            pl.getService().sendTimeInMap(getCountDown()*10,true,timeStart);
//                        }
//                    }
//                }else if(qualifierRound){
//                    groupStage = true;
//                    qualifierRound= false;
//                    listGroupStage= new ArrayList<>();
//                    listGroupStage = listPlayerInMap.stream()
//                            .sorted(Comparator.comparingInt(Char::getPointDaiHoi).reversed())
//                            .limit(16)
//                            .collect(Collectors.toList());
//                    setCountdown(600);
//                    timeStart= System.currentTimeMillis();
//                    for (Char pl:listPlayerInMap){
//                        if(pl!=null&&pl.user!=null&&!pl.isClean){
//                            for (int i = 0; i < 3; i++) {
//                                TemplateThu thu = new TemplateThu();
//                                thu.id = (short) (pl.letters.size() + 1);
//                                thu.Bac = 0;
//                                thu.BacKhoa = 0;
//                                thu.Vang = 0;
//                                thu.VangKhoa = 0;
//                                thu.Exp = 0;
//                                thu.Title = "Quà tham gia đại hội võ thuật ";
//                                thu.NameNguoiGui = "Hệ thống";
//                                thu.NoiDungThu = "";
//                                thu.TimeEnd = System.currentTimeMillis() + 864000000;
//                                thu.Item = new Item(14);
//                                pl.letters.add(thu);
//                                pl.getService().reloadLetter();
//                            }
//                            pl.setXY((short) (500+ Utlis.nextInt(-200,200)), (short) 566);
//                            pl.service.setXYChar();
//                            pl.getService().sendTimeInMap(getCountDown()*10,true,timeStart);
//                        }
//                    }
//                    for (int i = 0; i < listGroupStage.size()/2; i++) {
//                        Char pl = listGroupStage.get(i);
//                        Char plAtt = listGroupStage.get(listGroupStage.size()-1-i);
//                        Zone zone = zones.get(i);
//                        zone.addChar(pl);
//                        zone.addChar(plAtt);
//                        pl.setXY((short) 458, (short) 450);
//                        pl.service.setXYChar();
//                        plAtt.setXY((short) 458, (short) 450);
//                        plAtt.service.setXYChar();
//                        zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 2));
//                        zone.SendMessageInZone(HanderMessage.SendTypePk(plAtt.id, (byte) 2));
//                    }
//                }else {
//                    close();
//                    return;
//                }
//            }
//            countDown--;
//        }catch (Exception e){
//
//        }
    }
    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return false;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return false;
    }
}
