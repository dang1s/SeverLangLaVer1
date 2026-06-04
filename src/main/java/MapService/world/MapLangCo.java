package MapService.world;

import MapService.Map;
import MapService.Zone;
import MapService.zones.LangCo;
import com.sg188.lib.Log;
import com.sg188.server.Main;

import java.util.ArrayList;
import java.util.List;

public class MapLangCo {
    private static final MapLangCo instance = new MapLangCo();

    public static MapLangCo gI() {
        return instance;
    }

    public List<Map> maps = new ArrayList<>();

    public MapLangCo() {
        Map map = new Map(98);
        LangCo langCo = new LangCo(map, 0);
        langCo.createMob();
        langCo.createNpc();
        langCo.MAX_CHAR_INZONE=999;
        map.getZones().add(langCo);
        map.update();
        maps.add(map);
    }

}
