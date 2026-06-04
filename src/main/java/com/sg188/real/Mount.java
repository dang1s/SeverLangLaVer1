package com.sg188.real;

public class Mount {
    public int id;
    public int frameStart;
    public int frameEnd;
    public int attackStart;
    public int attackEnd;
    public int tick;
    public int tickAttack;
    public int dx;
    public int dy;

    public Mount(int id, int frameStart, int frameEnd, int tick, int attackStart, int attackEnd, int tickAttack, int dx, int dy) {
        this.id = id;
        this.frameStart = frameStart;
        this.frameEnd = frameEnd;
        this.tick = tick;
        this.attackStart = attackStart;
        this.attackEnd = attackEnd;
        this.tickAttack = tickAttack;
        this.dx = dx;
        this.dy = dy;
    }
}