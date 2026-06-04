package MapService.world;

import MapService.Map;
import MapService.zones.HangViThu;

import java.util.ArrayList;
import java.util.List;

public class MapHangViThu {
    private static final MapHangViThu instance = new MapHangViThu();

    public static MapHangViThu gI() {
        return instance;
    }

    public List<Map> maps = new ArrayList<>();

    public MapHangViThu() {
        Map map = new Map(90);
        HangViThu hangViThu = new HangViThu(map, 0);
        hangViThu.createMob();
        hangViThu.createNpc();
        hangViThu.MAX_CHAR_INZONE=999;
        map.getZones().add(hangViThu);
        map.update();
        maps.add(map);
    }

}

