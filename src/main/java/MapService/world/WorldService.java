package MapService.world;

import Service.HanderMessage;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.server.lib.Message;
import com.sg188.server.lib.Writer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class WorldService {

    private World world;

    public WorldService(World world) {
        this.world = world;
    }

    public void sendTimeInMap(Char player, Writer writer) throws IOException {
//        writer.writeShort(zoneID);
//        writer.writeShort(map.mapID);
//
//        player.Info.writeXY(writer);
//
//        writeVecItemMap(writer);
//        writeVecChar(player, writer);
//        writeVecMob(writer);
//        writeVecNpc(writer);
//
//        writer.writeByte(player.InfoGame.TypePk);
//        writer.writeLong(player.Info.TimeStartHD);
//        writer.writeInt(1);
//        writer.writeBoolean(false);
//        if (map.mapID == 84) {
//            writer.writeBoolean(true);
//        } else
//            writer.writeBoolean(false);
    }
    public void sendTimeInMap(int countDown,boolean start) {
        try {
            Message m = Message.c((byte) -80);
            m.writeLong(System.currentTimeMillis());
            m.writeInt(countDown * 100);
            m.writeBoolean(start);
            sendMessage(m);
        } catch (Exception e) {

        }
    }
    public void chat(String name, String text) {

    }
    public void setTypePKALLMap(){
        List<Char> members = world.getMembers();
        for (Char _char : members) {
            try {
                if(_char!=null&&!_char.isClean&&_char.zone!=null)
                _char.zone.SendMessageInZone(HanderMessage.SendTypePk(_char.id, (byte) 2));
            } catch (Exception e) {
                Log.error("worldService sendMessage ex: " + e.getMessage(), e);
            }
        }
    }

    public void sendMessage(Message ms) {
        List<Char> members = world.getMembers();
        for (Char _char : members) {
            try {
                if(!_char.isClean&&_char!=null&&_char.user!=null)
                _char.getService().sendMessage(ms);
            } catch (Exception e) {
                Log.error("worldService sendMessage ex: " + e.getMessage(), e);
            }
        }
    }

    public void serverMessage(String s) {
        try {
            Message ms = new Message((byte) -107);
            ms.writeUTF(s);
            sendMessage(ms);
        } catch (IOException e) {
        }
    }
}
