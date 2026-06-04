package com.sg188.real;

import MapService.Zone;
import com.sg188.data.NpcTemplate;
import com.sg188.lib.Utlis;
import com.sg188.server.handler.IActionNpc;
import java.util.ArrayList;

public class Npc extends Entity implements Cloneable {

    public static String[] getTextNpc(Zone zone, Npc npc) {

        ArrayList<String> list = new ArrayList<String>();
        ActionNpc[] array = getActionNpc(zone, npc);
        for (int i = 0; i < array.length; i++) {
            list.add(array[i].text);
        }
        return list.toArray(new String[list.size()]);
    }

    public static ActionNpc[] getActionNpc(Zone zone, Npc npc) {            
//        if (NpcTemplate.listAction == null) {
//            NpcTemplate.listAction = new ArrayList[DataCenter.gI().NpcTemplate.length];
//        }
        ArrayList<ActionNpc> list = NpcTemplate.listAction[npc.id];
//        Log.debug("openNpc: " + npc.id);
//        if (list == null) {
//            list = new ArrayList<ActionNpc>();
//            switch (npc.id) {
//                case 0:
//
//                    break;
//                case 28:
//                    list.add(new ActionNpc("Ghép đá", new IActionNpc() {
//                        @Override
//                        public void action(Client client) {
//                            client.session.service.openTabGhepDa();
//                        }
//                    }));
//                    list.add(new ActionNpc("Cường hóa", new IActionNpc() {
//                        @Override
//                        public void action(Client client) {
//                            client.session.service.openTabCuongHoa();
//                        }
//                    }));
//                    list.add(new ActionNpc("Tách cường hóa", new IActionNpc() {
//                        @Override
//                        public void action(Client client) {
//                            client.session.service.openTabTachCuongHoa();
//                        }
//                    }));
//                    break;
//            }
//        }
//
//        if (NpcTemplate.listAction[npc.id] == null) {
//            NpcTemplate.listAction[npc.id] = list;
//        }
        return list.toArray(new ActionNpc[list.size()]);
    }

    public int id;

    public Npc cloneNpc() {
        try {
            return (Npc) super.clone();
        } catch (Exception var2) {
            Utlis.println(var2);
            return null;
        }
    }

    public static class ActionNpc {

        public String text;
        public IActionNpc action;

        private ActionNpc(String text, IActionNpc actionNpc) {
            this.text = text;
            this.action = actionNpc;
        }
    }
}
