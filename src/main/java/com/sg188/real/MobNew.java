package com.sg188.real;

public class MobNew {
    public int id;
    public int dxMove;
    public int dyMove;
    public int dxStand;
    public int dyStand;
    public int frameStartMove;
    public int frameEndMove;
    public int frameStartStand;
    public int frameEndStand;
    public boolean isBong;

    public MobNew(int id, int dxMove, int dyMove, int dxStand, int dyStand, int frameStartMove, int frameEndMove, int frameStartStand, int frameEndStand, boolean isBong) {
        this.id = id;
        this.dxMove = dxMove;
        this.dyMove = dyMove;
        this.dxStand = dxStand;
        this.dyStand = dyStand;
        this.frameStartMove = frameStartMove;
        this.frameEndMove = frameEndMove;
        this.frameStartStand = frameStartStand;
        this.frameEndStand = frameEndStand;
        this.isBong = isBong;
    }
}
