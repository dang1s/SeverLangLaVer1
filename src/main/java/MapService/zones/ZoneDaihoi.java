package MapService.zones;

import MapService.Map;
import MapService.Zone;
import MapService.world.DaiHoiNhanGia;
import Service.HanderMessage;
import com.sg188.real.Char;


public class ZoneDaihoi extends ZWorld {
    public ZoneDaihoi(Map map, int id) {
        super(map, id);
    }

    @Override
    public boolean addChar(Char p) {
        super.addChar(p);
        p.Point.hp = p.maxHP;
        p.InfoGame.isDie = false;
        p.msgGetInfo();
        return true;
    }

    @Override
    public void removeChar(Char p) {
        super.removeChar(p);
        p.Point.hp = p.maxHP;
        p.InfoGame.isDie = false;
        p.msgGetInfo();
        p.service.sendMessage(HanderMessage.SendTypePk(p.id, (byte) 0));
    }

    @Override
    public void changeZone(Char player, byte zoneNext) {
        if (zoneNext == this.zoneID) {
            return;
        }

        DaiHoiNhanGia daiHoiNhanGia = DaiHoiNhanGia.DAI_HOI_NHAN_GIA;

        if (daiHoiNhanGia != null && daiHoiNhanGia == this.world && daiHoiNhanGia.isOpened && daiHoiNhanGia.isFighting(player)) {
            if (player != null && player.service != null) {
                player.service.warningMessage("Ban dang thi dau, khong the doi khu!");
            }
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

    @Override
    public void update() {
        updateMob();
        updatePlayer();
        updateItemMap();
    }
}
