package com.sg188.clan;

import com.sg188.data.DataCenter;
import com.sg188.data.SkillClan;
import com.sg188.lib.Log;
import com.sg188.real.Char;
import com.sg188.real.Item;
import com.sg188.server.lib.Message;

import java.io.DataOutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClanService {
    private Clan clan;

    public ClanService(Clan clan) {
        this.clan = clan;
    }
    public void chat(String name, String text) {
        try {
            Message mss = new Message((byte) 25);
            mss.writeUTF(name);
            mss.writeUTF(text);
            sendMessage(mss);
            mss.close();
        } catch (Exception ex) {
            Log.error("chat err: " + ex.getMessage(), ex);
        }
    }
    public void serverMessage(String text) {
        try {
            Message ms = new Message((byte) -107);
            ms.writeUTF(text);
            sendMessage(ms);
        } catch (Exception ex) {
        }
    }
    public void sendMessage(Message ms) {
        List<Member> members = clan.memberDAO.getAll();
        synchronized (members) {
            for (Member mem : members) {
                if (mem != null) {
                    Char _char = mem.getChar();
                    if (_char != null&&_char.user!=null) {
                        _char.user.session.sendMessage(ms);
                    }
                }
            }
        }
    }
}
