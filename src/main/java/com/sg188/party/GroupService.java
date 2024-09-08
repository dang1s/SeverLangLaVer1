/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.party;

import com.sg188.lib.Log;
import com.sg188.party.Group;
import com.sg188.party.MemberGroup;
import com.sg188.real.Char;
import com.sg188.server.lib.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class GroupService {

    private Group group;

    public GroupService(Group group) {
        this.group = group;
    }

    public void playerInParty() {
        try {
            Message ms = new Message((byte) 43);
            ms.writeBoolean(group.isLock);
            List<MemberGroup> partys = group.getMemberGroup();
            ms.writeByte(partys.size());
            for (MemberGroup p : partys) {
                ms.writeByte(p.classID);
                ms.writeByte(p.getChar().Info.idChar);
                ms.writeShort(p.getChar().level()); // ID ICON CHAR
                ms.writeUTF(p.name);
            }
            sendMessage(ms);
        } catch (IOException ex) {
            Log.error("playerInParty err:" + ex.getMessage(), ex);
        }
    }


    public void chat(String name, String text) {
        try {
            Message mss = new Message((byte) 26);
            mss.writeUTF(name);
            mss.writeUTF(text);
            sendMessage(mss);
            mss.close();
        } catch (IOException ex) {
            Log.error("chat err: " + ex.getMessage(), ex);
        }
    }


    public void sendMessage(Message ms) {
        List<Char> chars = group.getChars();
        for (Char _char : chars) {
            _char.user.session.sendMessage(ms);
        }
    }
}
