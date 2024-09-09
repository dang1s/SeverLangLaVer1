package MapService.zones;

import MapService.Map;
import Service.HanderMessage;
import com.sg188.lib.Utlis;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.real.Mob;

import java.util.List;

public class TrainingZone extends ZWorld {
    public int level;
    public int buff;

    public TrainingZone(Map map, int id) {
        super(map, id);
        timeReviveMob = 1000;
    }

    @Override
    public void createMob() {
        int[][] coordinates = {
                {219, 212}, {273, 212}, {303, 212}, {357, 212}, {429, 212}, {482, 212}, {587, 276},
                {626, 276}, {706, 276}, {587, 276}, {134, 430}, {231, 430}, {332, 430}, {905, 380},
                {1010, 380}, {1111, 380}, {967, 559}, {1084, 559}, {1204, 559}, {778, 695}, {876, 695},
                {975, 695}, {104, 553}, {189, 553}, {294, 553}, {405, 596}, {1270, 187}, {1192, 188},
                {1035, 151}, {56, 763}, {100, 763}, {170, 763}, {240, 763}, {300, 763}
                , {350, 763}, {400, 763}, {450, 763}, {500, 763}, {550, 763}, {600, 763}
                , {650, 763}, {770, 763}, {800, 763}, {880, 763}, {900, 763}, {970, 763}
        };

        for (int i = 0; i < coordinates.length; i++) {
            Mob mob = new Mob();
            mob.id = 121;
            mob.exp = level * 300;
            mob.level = level;
            mob.cx = (short) coordinates[i][0];
            mob.cy = (short) coordinates[i][1];
            mob.status = 2;
            mob.hpGoc = mob.hp = mob.hpFull = level * 650;
            if (mob.hpGoc <= 0) {
                mob.hpGoc = 1;
            }
            mob.expGoc = level * 107;
            if (mob.expGoc <= 0) {
                mob.expGoc = 1;
            }
            mob.paintMiniMap = false;
            mob.idEntity = i;
            mob.reSpawn(this);
            monsters.add(mob);
        }
    }

    @Override
    protected boolean canRespawn(Mob mob) {
        if (world.isClosed())
            return false;
        return true; // Mặc định cho phép hồi sinh
    }

    @Override
    public void setMobDie(Char player, Mob mob) {
        boolean isNhanExp = Math.abs(player.level() - mob.level) <= 5;
        if (isNhanExp) {
            if (mob.level >= 44) {
                if (mob.levelBoss == 1 && Utlis.nextInt(100) < 25) {
                    Item skn = new Item(434);
                    player.addItem(skn);
                } else if (mob.levelBoss == 2 && Utlis.nextInt(100) < 25) {
                    Item skn = new Item(434);
                    player.addItem(skn);
                }
            }
            if (player.Bag.itemSach != null && player.Info.sachChienDau == 18) {
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
                player.Bag.arrItemBody[11].updateTuLuyen(num);
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
            exp += exp * buff / 100;
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
            if (player.getGroup() != null) {
                List<Char> charList = player.getGroup().getCharsInZone(player.Info._mapID, player.zone.zoneID);
                if (charList != null) {
                    exp = exp * 20 / 100;
                    for (Char plToDoi : charList) {
                        if (plToDoi != player) {
                            plToDoi.addExp(exp);
                        }
                    }
                }
            }
        }
    }
}
