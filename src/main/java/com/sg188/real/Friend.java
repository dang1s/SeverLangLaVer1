/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.real;

import com.sg188.lib.ParseData;
import org.json.simple.JSONObject;

/**
 *
 * @author admin
 */
public class Friend {

    public String name;
    public byte type;
    public boolean isFriend;

    public Friend(String name, byte type,boolean isFriend) {
        this.name = name;
        this.type = type;
        this.isFriend=isFriend;
    }

    public Friend(JSONObject obj) {
        load(obj);
    }

    private void load(JSONObject obj) {
        ParseData parse = new ParseData(obj);
        this.type = parse.getByte("type");
        this.name = parse.getString("name");
        this.isFriend = parse.getBoolean("isfriend");
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("name", this.name);
        obj.put("type", this.type);
        obj.put("isfriend", this.isFriend);
        return obj;
    }
}
