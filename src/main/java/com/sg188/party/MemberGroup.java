package com.sg188.party;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.List;

import MapService.world.World;
import com.sg188.real.Char;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Admin
 */
public class MemberGroup {

    public int charId;
    public String name;
    public int classID;
    private Char p;
    private final List<World> worlds = new ArrayList<>();

    public void add(World world) {
        synchronized (worlds) {
            worlds.add(world);
        }
    }

    public void remove(World world) {
        synchronized (worlds) {
            worlds.remove(world);
        }
    }

    public void setChar(Char p) {
        this.p = p;
    }

    public Char getChar() {
        return this.p;
    }
}
