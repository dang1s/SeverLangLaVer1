/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sg188.real;

import com.sg188.lib.Log;
import com.sg188.lib.TimeUtils;
import com.sg188.server.lib.Writer;
import lombok.Getter;
import lombok.Setter;
import MapService.*;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author admin
 */
@Getter
@Setter
public class ItemMap {

    protected short id;
    protected Item item;
    protected int ownerID;
    protected short x;
    protected short y;
    protected boolean pickedUp;
    protected int requireItemID = -1;
    protected long createdAt;
    protected long expired = 30000;

    public Lock lock = new ReentrantLock();

    public ItemMap(short id) {
        this.id = id;
        this.createdAt = System.currentTimeMillis();
    }
    public void write(Writer writer, int cx, int cy,Zone zone) throws IOException {
        if (cx == -1) {
            cx = this.x;
        }
        if (cy == -1) {
            cy = this.y;
        }
        this.x = (short) cx;
        this.y = (short) cy;
        writer.writeInt(ownerID);
        writer.writeShort(id);
        try {
            this.y = zone.getXYBlockMap(cx, cy).cy;
        } catch (Exception e) {

        }
        writeXY(writer);
        item.write(writer);
    }
    public void writeXY(Writer writer) throws IOException {
        writer.writeShort(x);
        writer.writeShort(y);
    }
    public boolean isExpired() {
        return TimeUtils.canDoWithTime(createdAt, expired);
    }

    public boolean isCanPickup() {
        return TimeUtils.canDoWithTime(createdAt, 20000);
    }

    public int getItemID() {
        return item.id;
    }

}