package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.AttendanceArea;
import MapService.zones.BattleZone;
import Service.HanderMessage;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.server.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Arena extends World {

    public static ReadWriteLock lockArena = new ReentrantReadWriteLock();
    public static ArrayList<Arena> arenas = new ArrayList<>();
    public static ArrayList<String> results = new ArrayList<>();
    public static byte number = 0;

    public static Arena getArenaByID(int id) {
        lockArena.readLock().lock();
        try {
            for (Arena arena : arenas) {
                if (arena.id == id) {
                    return arena;
                }
            }
        } finally {
            lockArena.readLock().unlock();
        }
        return null;
    }

    public static void addArena(Arena arena) {
        arenas.add(arena);
    }

    public static void removeArena(Arena arena) {
        arenas.remove(arena);
    }

    public ArrayList<Char> teamOne;
    public ArrayList<Char> teamTwo;
    public ArrayList<Char> viewers;
    private int moneyTeamOne;
    public int moneyTeamTwo;
    private Zone zone;
    public boolean isOpened;
    private boolean isLeaderTeamOneOut;
    private boolean isLeaderTeamTwoOut;
    public String leaderTeamOneName, leaderTeamTwoName;
    public boolean isTwoTeamsEtered;
    private boolean finished;

    public Arena() {
        setType(World.ARENA);
        this.name = "Arena";
        this.id = number++;
        this.teamOne = new ArrayList<>();
        this.teamTwo = new ArrayList<>();
        this.viewers = new ArrayList<>();
        this.countDown = 300;
        Map map = new Map(44);
        AttendanceArea z = new AttendanceArea(0, map);
        z.setWorld(this);
        this.zone = z;
        initFinished = true;
    }

    public void join(int team, Char _char) {
        zone.addChar(_char);
        lock.writeLock().lock();
        try {
            if (team == 1) {
                if (teamOne.isEmpty()) {
                    leaderTeamOneName = _char.Info.name;
                }
                this.teamOne.add(_char);
                _char.setXY((short) 458, (short) 450);
                _char.service.setXYChar();
            }
            if (team == 2) {
                if (teamTwo.isEmpty()) {
                    leaderTeamTwoName = _char.Info.name;
                }
                this.teamTwo.add(_char);
                _char.setXY((short) 458, (short) 450);
                _char.service.setXYChar();
            }
            if (team == 3) {
                this.viewers.add(_char);
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
                if (teamOne.size() > 0) {
                    if (teamOne.get(0) == _char) {
                        this.isLeaderTeamOneOut = true;
                    }
                }
                if (teamTwo.size() > 0) {
                    if (teamTwo.get(0) == _char) {
                        this.isLeaderTeamTwoOut = true;
                    }
                }
            }
            lock.writeLock().lock();
            try {
                teamOne.remove(_char);
                teamTwo.remove(_char);
                viewers.remove(_char);
            } finally {
                lock.writeLock().unlock();
            }
            _char.InfoGame.TypePk = 0;
            _char.service.sendMessage(HanderMessage.SendTypePk(_char.id, (byte)0));
            removeMember(_char);
            Zone z = zone;
            z.removeChar(_char);
            _char.removeWorld(World.ARENA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getTeam(Char _char) {
        int team = -1;
        if (teamOne.get(0) == _char) {
            team = 1;
        }
        if (teamTwo.get(0) == _char) {
            team = 2;
        }
        return team;
    }

    public void setMoney(int team, int money) {
        String name = "";
        if (team == 1) {
            this.moneyTeamOne = money;
            name = leaderTeamOneName;
        }
        if (team == 2) {
            this.moneyTeamTwo = money;
            name = leaderTeamTwoName;
        }
        String text = name + " thay đổi tiền dặt cược là: " + Utlis.getCurrency(money) + " Bạc.";
        service.serverMessage(text);
        if (moneyTeamOne == moneyTeamTwo) {
            Char leaderTeamOne = teamOne.get(0);
            Char leaderTeamTwo = teamTwo.get(0);
            if(leaderTeamTwo.Bag.bac < moneyTeamTwo||leaderTeamOne.Bag.bac <moneyTeamOne){
                close();
                return;
            }
            service.serverMessage("Trận đấu bắt đầu.");
            open();
        }
    }

    public boolean isTeamOneAllDead() {
        if (isLeaderTeamOneOut) {
            return true;
        }
        lock.readLock().lock();
        try {
            for (Char _char : teamOne) {
                if (_char!=null&&!_char.isClean&&!_char.InfoGame.isDie) {
                    return false;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }

    public boolean isTeamTwoAllDead() {
        if (isLeaderTeamTwoOut) {
            return true;
        }
        lock.readLock().lock();
        try {
            for (Char _char : teamTwo) {
                if (_char!=null&&!_char.isClean&&!_char.InfoGame.isDie) {
                    return false;
                }
            }
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }

    public boolean isViewer(Char p) {
        return this.viewers.stream().filter(pp -> pp.id == p.id).count() > 0;
    }

    public void open() {
        this.isOpened = true;
        zone.close();
        Map map = new Map(45);
        BattleZone z = new BattleZone(0, map);
        z.setWorld(this);
        this.zone = z;
        Char leaderTeamOne = teamOne.get(0);
        Char leaderTeamTwo = teamTwo.get(0);
        leaderTeamOne.addBac(-moneyTeamOne);
        leaderTeamTwo.addBac(-moneyTeamTwo);
        if (moneyTeamOne >= 1000000) {
            Main.HeThongCTG(
                    String.format("%s (%d) đang thách đấu với %s (%d) %s Bac ở lôi đài.", leaderTeamOneName,
                            leaderTeamOne.level(), leaderTeamTwoName, leaderTeamTwo.level(),
                            Utlis.getCurrency(moneyTeamOne)),2);
        }
        for (Char _char : teamOne) {
            _char.InfoGame.TypePk = 2;
            this.zone.addChar(_char);
            _char.setXY((short) 344, (short) 450);
            _char.service.setXYChar();
            _char.service.sendMessage(HanderMessage.SendTypePk(_char.id, (byte) 2));
        }
        for (Char _char : teamTwo) {
            _char.InfoGame.TypePk = 2;
            this.zone.addChar(_char);
            _char.setXY((short) 626, (short) 450);
            _char.service.setXYChar();
            _char.service.sendMessage(HanderMessage.SendTypePk(_char.id, (byte) 2));
        }
        addArena(this);
        this.countDown = 600;
        service.sendTimeInMap(countDown*10,false);

    }

    public void close() {
        try {
            if (isOpened) {
                removeArena(this);
            }
            try {
                List<Char> chars = zone.getChars();
                for (Char _char : chars) {
                    if (_char.isClean) {
                        continue;
                    }
                    _char.InfoGame.TypePk = 0;
                    _char.service.sendMessage(HanderMessage.SendTypePk(_char.id, (byte)0));
                    Map.maps[_char.Info.mapReSpawm].addChar(_char);
                }
            } catch (Exception e) {

            }
            if (zone != null) {
                zone.close();
            }
            this.zone = null;
            this.teamOne = null;
            this.teamTwo = null;
            this.viewers = null;
            this.leaderTeamOneName = null;
            this.leaderTeamTwoName = null;
            super.close();
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void update() {
        try {
            zone.update();
            if (countDown <= 0) {
                if (!finished) {
                    int coin = moneyTeamOne + moneyTeamTwo;
                    coin -= coin * 10 / 100;
                    Char leaderTeamTwo = teamTwo.get(0);
                    leaderTeamTwo.addBac(coin / 2);
                    Char leaderTeamOne = teamOne.get(0);
                    leaderTeamOne.addBac(coin / 2);
                    String result = "Phe " + leaderTeamOneName + " với phe " + leaderTeamTwoName + " hòa";
                    Arena.results.add(0, result);
                }
                close();
                return;
            }
            if (!finished) {
                boolean isTeamOneAllDead = isTeamOneAllDead();
                boolean isTeamTwoAllDead = isTeamTwoAllDead();
                if ((isTeamOneAllDead || isTeamTwoAllDead) && isTwoTeamsEtered) {
                    finished = true;
                    if (isOpened) {
                        String nameTeamWin = "";
                        int coin = moneyTeamOne + moneyTeamTwo;
                        coin -= coin / 100;
                        if (isTeamOneAllDead) {
                            Char leaderTeamTwo = teamTwo.get(0);
                            nameTeamWin = leaderTeamTwo.Info.name;
                            leaderTeamTwo.addBac(coin);
                            String result = "Phe " +nameTeamWin + " thắng phe " + leaderTeamOneName;

                            Arena.results.add(0, result);
                        } else if (isTeamTwoAllDead) {
                            Char leaderTeamOne = teamOne.get(0);
                            leaderTeamOne.addBac(coin);
                            String result = "Phe " + leaderTeamOneName + " thắng phe " +leaderTeamTwoName;
                            Arena.results.add(0, result);
                        }
                        if (Arena.results.size() >= 21) {
                            Arena.results.remove(20);
                        }
                        countDown = 10;
                        service.sendTimeInMap(countDown,false);
                        String text = "Phe " + nameTeamWin + " đã dành chiến thắng nhận được "
                                + Utlis.getCurrency(coin) + " xu.";
                        getService().serverMessage(text);
                    } else {
                        if (isTeamOneAllDead || isTeamTwoAllDead) {
                            getService().serverMessage(
                                    "Trận đấu đã bị hủy vì phe đối phương đã khiếp sợ và bỏ chạy.");
                        }
                        close();
                        return;
                    }
                }
            }
            countDown--;
        } catch (Exception e) {
            close();
        }
    }
}

