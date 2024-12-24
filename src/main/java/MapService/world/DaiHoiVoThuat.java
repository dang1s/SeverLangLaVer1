package MapService.world;

import EventClick.ClickTop;
import EventClick.InfoTop;
import Manager.Manager;
import MapService.Map;
import MapService.Zone;
import MapService.zones.ZoneDaihoi;
import Service.HanderEff;
import Service.HanderMessage;
import Template.TemplateThu;
import com.sg188.clan.Clan;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Effect;
import com.sg188.real.Item;
import com.sg188.server.Main;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DaiHoiVoThuat extends World{
    public static DaiHoiVoThuat DAIHOI;
    public List<Char>listPlayerInMap;
    public ArrayList<Char> viewers;

    public boolean isOpened;

    public long timeStart;
    public boolean qualifierRound;
    public List<Char>listGroupStage;
    public boolean quarterFinals;
    public List<Char>listQuarterFinals;
    public boolean semiFinals;//bán kết
    public boolean prepareSemiFinals;// đếm ngược chuẩn bị
    public boolean prepareFinals; //đếm ngược chung kết
    public boolean finalRound;
    public List<Char>listFinalRound;
    public boolean isVong18;
    public List<Match> vong1_8 = new ArrayList<>();
    public boolean isVong14;
    public List<Match> vong1_4 = new ArrayList<>();
    public boolean isVongChungKet;
    public List<Match> vongChungKet = new ArrayList<>();
    public boolean checkClose;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    /*public DaiHoiVoThuat() {
        setType(World.DAI_HOI_VO_THUAT);
        this.name = "DaiHoiVoThuat";
        this.countDown = 20;
        listPlayerInMap = new ArrayList<>();
        viewers= new ArrayList<>();
        Map map = new Map(49);
        for (int i = 0; i < 21; i++) {
            ZoneDaihoi z = new ZoneDaihoi(map,i);
            z.setWorld(this);
            map.getZones().add(z);
            zones.add(z);
        }

        initFinished = true;
    }*/
    private static DaiHoiVoThuat instance;

    public static DaiHoiVoThuat gI() {
        if (instance == null) {
            synchronized (DaiHoiVoThuat.class) {
                if (instance == null) {
                    instance = new DaiHoiVoThuat();
                }
            }
        }
        return instance;
    }

    private DaiHoiVoThuat() {
        setType(World.DAI_HOI_VO_THUAT);
        this.name = "DaiHoiVoThuat";
        this.countDown = 300;
        listPlayerInMap = new ArrayList<>();
        viewers = new ArrayList<>();
        listQuarterFinals = new ArrayList<>();
        listFinalRound = new ArrayList<>();
        Map map = new Map(49);
        for (int i = 0; i < 21; i++) {
            ZoneDaihoi z = new ZoneDaihoi(map, i);
            z.setWorld(this);
            map.getZones().add(z);
            zones.add(z);
        }
        initFinished = true;
    }

    public void destroy() {
        close();
        synchronized (DaiHoiVoThuat.class) {
            instance = null; // Xóa tham chiếu tới instance
        }
    }

    public List<InfoTop> listTopDaiHoi() {
        List<InfoTop> list = new ArrayList<>();
        for(Char pl: this.listPlayerInMap) {
            InfoTop top = new InfoTop();
            top.name = pl.getName();
            top.pointDaiHoi = pl.getPointDaiHoi();
            top.idHe = pl.id_he;
            if (pl.clan !=null && pl.clan.id != -1) {
                top.clanName = pl.clan.getName();
            }
            list.add(top);
        }
        return list;
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

    void prepareCharForBattle(Char pl, Zone zone) {
        if (pl.InfoGame.isDie) {
            pl.reSpawn();
        }
        zone.addChar(pl);
        pl.setXY((short) 458, (short) 471);
        pl.service.setXYChar();
    }

    private void setupPlayerInZone(Char player, Zone zone) {
        if (player.InfoGame.isDie) {
            player.reSpawn();
        }
        zone.addChar(player);
        player.setXY((short) 458, (short) 471);
        player.service.setXYChar();
        zone.SendMessageInZone(HanderMessage.SendTypePk(player.id, (byte) 0));
    }

    @Override
    public void update() {
        try {
            for (Zone zone: zones){
                zone.update();
            }
            if (countDown <= 0) {
                if (!isOpened) {
                    qualifierRound=true;
                    isOpened = true;
                    setCountdown(120);
                    Main.HeThongCTG("Vòng loại đại chiến nhẫn giả đã bắt đầu",2);
                    timeStart= System.currentTimeMillis();
                    List<Char> list = getMembers();

                    for(Char pl:list){
                        if(pl!=null&&pl.user!=null&&!pl.isClean){
                            this.zones.get(0).addChar(pl);
                            Effect effect = pl.getEffect(66);
                            if (effect != null) {
                                pl.removeEffect(effect);
                                HanderEff.RemovePointEff(pl, effect);
                                pl.msgRemoveEffect(effect);
                            }
                            pl.InfoGame.TypePk = 3;
                            pl.zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 3));
                            pl.getService().sendTimeInMap(getCountDown()*10,true,timeStart);
                        }
                    }
                } else if(qualifierRound){
                    qualifierRound= false;
                    quarterFinals = true;
                    isVong18 = true;
                    listGroupStage= new ArrayList<>();
                    listGroupStage = listPlayerInMap.stream()
                            .sorted(Comparator.comparingInt(Char::getPointDaiHoi).reversed())
                            .limit(8)
                            .collect(Collectors.toList());
                    setCountdown(120);
                    Main.HeThongCTG("Vòng Knock-out 8 đại chiến nhẫn giả đã bắt đầu",2);
                    timeStart= System.currentTimeMillis();

                    for (int i = 0; i < listGroupStage.size() / 2; i++) {
                        if (listGroupStage.size() % 2 != 0 && i == listGroupStage.size() / 2 - 1) {
                            Char byePlayer = listGroupStage.get(listGroupStage.size() / 2);
                            Zone zone = zones.get(0);
                            prepareCharForBattle(byePlayer, zone);
                            Main.HeThongCTG(byePlayer.getName() + " tự động vào vòng tiếp theo.", 2);
                            vong1_8.add(new Match(byePlayer, byePlayer, 1));
                        } else {
                            Char pl = listGroupStage.get(i);
                            Char plAtt = listGroupStage.get(listGroupStage.size() - 1 - i);
                            Zone zone = zones.get(i + 1);
                            prepareCharForBattle(pl, zone);
                            prepareCharForBattle(plAtt, zone);
                            vong1_8.add(new Match(pl, plAtt));
                            zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 0));
                            zone.SendMessageInZone(HanderMessage.SendTypePk(plAtt.id, (byte) 0));
                        }
                    }

                } else if(quarterFinals) {
                    quarterFinals = false;
                    prepareSemiFinals = true;
                    setCountdown(120);
                    timeStart= System.currentTimeMillis();
                    List<Char> list = listGroupStage;

                    for(Char pl:list){
                        if(pl!=null&&pl.user!=null&&!pl.isClean){
                            pl.InfoGame.TypePk = 3;
                            pl.zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 3));
                            pl.getService().sendTimeInMap(getCountDown()*10,true,timeStart);
                        }
                    }
                } else if(prepareSemiFinals) {
                    isVong18 = false;
                    isVong14 = true;
                    semiFinals = true;
                    prepareSemiFinals = false;
                    setCountdown(120);

                    for (int i = 0; i < listQuarterFinals.size() / 2; i++) {
                        Char pl = listQuarterFinals.get(i);
                        Char plAtt = listQuarterFinals.get(listQuarterFinals.size() - 1 - i);

                        // Kiểm tra nếu số người chơi là lẻ và đây là người chơi cuối cùng chưa được ghép cặp
                        if (listQuarterFinals.size() % 2 != 0 && i == listQuarterFinals.size() / 2 - 1) {
                            Char plBye = listQuarterFinals.get(listQuarterFinals.size() - 1);

                            Zone zone = zones.get(i + 5); // Lấy zone cho trận đấu
                            setupPlayerInZone(plBye, zone); // Đặt người chơi vào zone

                            // Thông báo người chơi tự động đi tiếp mà không phải thi đấu
                            Main.HeThongCTG(plBye.getName() + " tự động vào vòng tiếp theo .", 2);
                            listFinalRound.add(plBye);
                            break;
                        }

                        // Xử lý các cặp đấu
                        Zone zone = zones.get(i + 5);

                        setupPlayerInZone(pl, zone); // Thêm người chơi 1 vào zone
                        setupPlayerInZone(plAtt, zone); // Thêm người chơi 2 vào zone

                        vong1_4.add(new Match(pl, plAtt)); // Tạo cặp đấu
                    }

                } else if (semiFinals) {
                    semiFinals = false;
                    prepareFinals = true;
                    setCountdown(120);
                    timeStart= System.currentTimeMillis();
                    List<Char> list = listQuarterFinals;

                    for(Char pl: list ){
                        if(pl!=null && pl.user!= null && !pl.isClean){
                            pl.InfoGame.TypePk = 3;
                            pl.zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 3));
                            pl.getService().sendTimeInMap(getCountDown()*10,true,timeStart);
                        }
                    }
                } else if(prepareFinals){
                    prepareFinals = false;
                    finalRound = true;
                    isVong14 = false;
                    isVongChungKet = true;
                    setCountdown(120);
                    timeStart= System.currentTimeMillis();
                    List<Char> list = listFinalRound;

                    for (int i = 0; i < list.size() / 2; i++) {
                        Char pl = list.get(i);
                        Char plAtt = list.get(list.size() - 1 - i);

                        // Kiểm tra nếu số người chơi là lẻ
                        if (list.size() % 2 != 0 && i == list.size() / 2 - 1) {
                            // Người chơi cuối cùng sẽ tự động tiến vào vòng tiếp theo
                            Char plBye = list.get(list.size() - 1);
                            Zone zone = zones.get(i + 5);
                            zone.addChar(plBye);
                            plBye.setXY((short) 458, (short) 471);
                            plBye.service.setXYChar();

                            // Thông báo người chơi đi tiếp mà không phải thi đấu
                            Main.HeThongCTG(plBye.getName() + " đã chiến thắng đại hội.", 2);

                            // Thêm trận đấu với một người chơi và người "bye" vào danh sách
                            vongChungKet.add(new Match(plBye, plBye, 1));
                            zone.SendMessageInZone(HanderMessage.SendTypePk(plBye.id, (byte) 0));
                            break;
                        }
                        // Trường hợp số lượng người chơi là chẵn, thực hiện như bình thường
                        Zone zone = zones.get(i + 9);
                        zone.addChar(pl);
                        zone.addChar(plAtt);
                        pl.setXY((short) 458, (short) 471);
                        pl.service.setXYChar();
                        plAtt.setXY((short) 458, (short) 471);
                        plAtt.service.setXYChar();
                        vong1_4.add(new Match(pl, plAtt));
                        zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 0));
                        zone.SendMessageInZone(HanderMessage.SendTypePk(plAtt.id, (byte) 0));
                    }
                } else if(finalRound) {
                    finalRound = false;
                    checkClose = true;
                    setCountdown(120);
                    timeStart= System.currentTimeMillis();
                    if(listFinalRound.size() <= 1) {
                        if (!listFinalRound.isEmpty()) {
                            if (listFinalRound.get(0) != null && listFinalRound.get(0).Info.name != null) {
                                Main.HeThongCTG(listFinalRound.get(0).Info.name + " đã chiến thắng đại hội nhẫn giả! Độc cô cầu bại!!", 2);
                            }
                        }
                        try {
                            Main.HeThongCTG( "Đại hội sẽ đóng sau 15s", 2);
                            close();
                            return;
                        } catch (Exception e) {
                            System.out.println("Error during respawn: "+ e);
                        }
                    }
                    for(Char pl : listFinalRound) {
                        if(pl != null && pl.user != null && !pl.isClean){
                            pl.InfoGame.TypePk = 3;
                            pl.zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 3));
                            pl.getService().sendTimeInMap(getCountDown()*10,true,timeStart);
                        }
                    }
                } else if(checkClose) {
                    close();
                    return;
                }
            }

            if(qualifierRound || !isOpened) {
                for (Char pl : zones.get(0).players) {
                    if (pl != null && pl.user != null && pl.InfoGame.isDie && !pl.InfoGame.isScheduledForRespawn) {
                        pl.InfoGame.isScheduledForRespawn = true;

                        scheduler.schedule(() -> {
                            try {
                                pl.reSpawn();
                            } catch (Exception e) {
                                System.out.println("Error during respawn: "+ e);
                            } finally {
                                pl.InfoGame.isScheduledForRespawn = false;
                            }
                        }, 5, TimeUnit.SECONDS);

                    }
                    if(pl != null) {
                        pl.getService().sendChienTich(pl.getPointDaiHoi());
                    }
                }
            }

            if(qualifierRound && !isVong18 && !isVong14 && !isVongChungKet) {
                scheduler.schedule(() -> {
                    ClickTop.cDaiHoi = this.listTopDaiHoi();
                }, 1, TimeUnit.SECONDS);
            }
            if(isVong18) {
                for (Match match : vong1_8) {
                    if(match.getChar2().InfoGame.isDie || match.getChar2().Info._mapID != 49) {
                        match.setResult(1);
                        if (!listQuarterFinals.contains(match.getChar1())) {
                            listQuarterFinals.add(match.getChar1());
                        }
                    } else if(match.getChar1().InfoGame.isDie || match.getChar1().Info._mapID != 49){
                        match.setResult(2);
                        if (!listQuarterFinals.contains(match.getChar2())) {
                            listQuarterFinals.add(match.getChar2());
                        }
                    } else {
                        if (match.getResult() != 1 && match.getResult() != 2) {
                            match.setResult(3);
                        }
                    }
                }
            } else if(isVong14) {
                for (Match match : vong1_4) {
                    if(match.getChar2().InfoGame.isDie || match.getChar2().Info._mapID != 49) {
                        if(match.getResult() == 3){
                            match.setResult(1);
                        }
                        if (!listFinalRound.contains(match.getChar1())) {
                            listFinalRound.add(match.getChar1());
                        }
                    } else if(match.getChar1().InfoGame.isDie || match.getChar1().Info._mapID != 49){
                        if(match.getResult() == 3){
                            match.setResult(2);
                        }
                        if (!listFinalRound.contains(match.getChar2())) {
                            listFinalRound.add(match.getChar2());
                        }
                    } else {
                        if (match.getResult() != 1 && match.getResult() != 2) {
                            match.setResult(3);
                        }
                    }
                }
            } else if(isVongChungKet) {
                for (Match match : vongChungKet) {
                    if(match.getChar2().InfoGame.isDie || match.getChar2().Info._mapID != 49) {
                        Main.HeThongCTG(match.getChar1().Info.name + " đã chiến thắng đại hội nhẫn giả! Độc cô cầu bại!!", 2);
                        match.setResult(1);
                        scheduler.schedule(() -> {
                            try {
                                Main.HeThongCTG( "Đại hội sẽ đóng sau 15s", 2);
                                close();
                            } catch (Exception e) {
                                System.out.println("Error during respawn: "+ e);
                            } finally {
                                Main.HeThongCTG( "Đại hội đã kết thúc", 2);
                            }
                        }, 15, TimeUnit.SECONDS);
                    } else if(match.getChar1().InfoGame.isDie || match.getChar1().Info._mapID != 49){
                        Main.HeThongCTG(match.getChar2().Info.name + " đã chiến thắng đại hội nhẫn giả! Độc cô cầu bại!!", 2);
                        match.setResult(2);
                        scheduler.schedule(() -> {
                            try {
                                Main.HeThongCTG( "Đại hội sẽ đóng sau 15s", 2);
                                close();
                            } catch (Exception e) {
                                System.out.println("Error during respawn: "+ e);
                            } finally {
                                Main.HeThongCTG( "Đại hội đã kết thúc", 2);
                            }
                        }, 15, TimeUnit.SECONDS);

                    } else {
                        if (match.getResult() != 1 && match.getResult() != 2) {
                            match.setResult(3);
                        }
                    }
                }
            }

            countDown--;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    @Override
    public boolean enterWorld(Zone pre, Zone next) {
        return false;
    }

    @Override
    public boolean leaveWorld(Zone pre, Zone next) {
        return false;
    }

    public void close() {
        if (this.isClosed) {
            return;
        }
        try {
            for (Char _char : listPlayerInMap) {
                try {
                    if (_char.isClean) {
                        continue;
                    }
                    Map.maps[86].addChar(_char);
                    _char.service.serverMessage("Đại hội võ thuật đã kết thúc.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){

        }finally {
            super.close();
            DaiHoiVoThuat.gI().destroy();
        }

    }
}
