/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.real;

import org.json.simple.JSONObject;

/**
 * @author admin
 */
public class DanhHieu {
    public String TextDanhHieu;
    public int b = -1;
    public int c = -16711681;
    public long timeStart;
    public long timeEnd;
    public boolean isNew;
    public int idItem;

    public void GetDanhHieu() {
        String[] var = TextDanhHieu.split("Danh hiệu ");
        TextDanhHieu = var[1];
    }

    public JSONObject toJSONObject() {
        JSONObject obj = new JSONObject();
        obj.put("text", this.TextDanhHieu);
        obj.put("timeStart", this.timeStart);
        obj.put("timeEnd", this.timeEnd);
        if(isNew) {
            obj.put("isNew", this.isNew);
            obj.put("idItem", this.idItem);
        }
        return obj;
    }
}
