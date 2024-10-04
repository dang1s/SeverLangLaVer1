package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.ZoneDaihoi;
import Service.HanderMessage;
import Template.TemplateThu;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DaiHoiVoThuat extends World {
    public static DaiHoiVoThuat DAIHOI;
    public List<Char> listPlayerInMap;
    public ArrayList<Char> viewers;

    public boolean isOpened;
    private boolean finished;
    public long timeStart;
    public boolean qualifierRound;
    public boolean groupStage;
    public List<Char> listGroupStage;
    public boolean quarterFinals;
    public List<Char> listQuarterFinals;

    public boolean semiFinals;
    public List<Char> listSemiFinals;
    public boolean finalRound;
    public List<Char> listFinalRound;

    public DaiHoiVoThuat() {
        setType(World.DAI_HOI_VO_THUAT);
        this.name = "DaiHoiVoThuat";
        this.countDown = 600;
        listPlayerInMap = new ArrayList<>();
        viewers = new ArrayList<>();
        Map map = new Map(49);
        for (int i = 0; i < 21; i++) {
            ZoneDaihoi z = new ZoneDaihoi(map, i);
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
        try {
            for (Zone zone : zones) {
                zone.update();
            }

            if (countDown <= 0) {
                if (!isOpened) {
                    // Start of Qualifier Round
                    qualifierRound = true;
                    isOpened = true;
                    setCountdown(600);
                    timeStart = System.currentTimeMillis();
                    List<Char> list = getMembers();
                    for (Char pl : list) {
                        if (pl != null && pl.user != null && !pl.isClean) {
                            pl.zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 2));
                            pl.getService().sendTimeInMap(getCountDown() * 10, true, timeStart);
                        }
                    }
                } else if (qualifierRound) {
                    // End of Qualifier Round, Start of Group Stage
                    groupStage = true;
                    qualifierRound = false;

                    listGroupStage = listPlayerInMap.stream()
                            .sorted(Comparator.comparingInt(Char::getPointDaiHoi).reversed())
                            .limit(16)
                            .collect(Collectors.toList());

                    setCountdown(600);
                    timeStart = System.currentTimeMillis();

                    for (Char pl : listPlayerInMap) {
                        if (pl != null && pl.user != null && !pl.isClean) {
                            giveParticipationRewards(pl);
                            moveToSpectatorArea(pl);
                            pl.getService().sendTimeInMap(getCountDown() * 10, true, timeStart);
                        }
                    }

                    assignPlayersToZones(listGroupStage, 0);
                } else if (groupStage) {
                    // End of Group Stage, Start of Quarter-Finals
                    quarterFinals = true;
                    groupStage = false;
                    listQuarterFinals = determineWinners(listGroupStage);
                    setCountdown(600);
                    timeStart = System.currentTimeMillis();
                    assignPlayersToZones(listQuarterFinals, 0);
                } else if (quarterFinals) {
                    // End of Quarter-Finals, Start of Semi-Finals
                    semiFinals = true;
                    quarterFinals = false;
                    listSemiFinals = determineWinners(listQuarterFinals);
                    setCountdown(600);
                    timeStart = System.currentTimeMillis();
                    assignPlayersToZones(listSemiFinals, 0);
                } else if (semiFinals) {
                    // End of Semi-Finals, Start of Final Round
                    finalRound = true;
                    semiFinals = false;
                    listFinalRound = determineWinners(listSemiFinals);
                    setCountdown(600);
                    timeStart = System.currentTimeMillis();
                    assignPlayersToZones(listFinalRound, 0);
                } else if (finalRound) {
                    // Determine the ultimate winner and close the tournament
                    Char champion = determineWinners(listFinalRound).get(0);
                    // Announce the champion (example)
                    System.out.println("The champion is: " + 111);
                    // Distribute rewards, etc. (implement your logic here)
                    close();
                    return;
                }
            }
            countDown--;
        } catch (Exception e) {
            // Handle exceptions appropriately (logging, etc.)
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

    // Helper method to determine winners from a list of players
    private List<Char> determineWinners(List<Char> players) {
        List<Char> sortedPlayers = players.stream()
                .sorted(Comparator.comparingInt(Char::getPointDaiHoi).reversed())
                .collect(Collectors.toList());

        int numWinners = (players.size() == 2) ? 1 : players.size() / 2;

        return sortedPlayers.subList(0, numWinners);
    }

    // Helper method to assign players to zones for battles
    private void assignPlayersToZones(List<Char> players, int startingZoneIndex) {
        for (int i = 0; i < players.size() / 2; i++) {
            Char pl = players.get(i);
            Char plAtt = players.get(players.size() - 1 - i);
            Zone zone = zones.get(startingZoneIndex + i);
            zone.addChar(pl);
            zone.addChar(plAtt);
            pl.setXY((short) 458, (short) 450);
            pl.service.setXYChar();
            plAtt.setXY((short) 458, (short) 450);
            plAtt.service.setXYChar();
            zone.SendMessageInZone(HanderMessage.SendTypePk(pl.id, (byte) 2));
            zone.SendMessageInZone(HanderMessage.SendTypePk(plAtt.id, (byte) 2));
        }
    }

    // Helper method to give participation rewards
    private void giveParticipationRewards(Char player) {
        for (int i = 0; i < 3; i++) {
            TemplateThu thu = new TemplateThu();
            thu.id = (short) (player.letters.size() + 1);
            // ... (Set other attributes of the reward letter)
            thu.Item = new Item(14);
            player.letters.add(thu);
            player.getService().reloadLetter();
        }
    }

    // Helper method to move a player to the spectator area
    private void moveToSpectatorArea(Char player) {
        player.setXY((short) (500 + Utlis.nextInt(-200, 200)), (short) 566);
        player.service.setXYChar();
    }
}