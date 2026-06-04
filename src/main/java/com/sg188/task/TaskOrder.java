package com.sg188.task;

import MapService.Map;
import com.sg188.real.Char;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskOrder {

    public static final byte TASK_DAY = 0;
    public static final byte TASK_BOSS = 1;
    public int taskId;
    public int count;
    public int maxCount;
    public String name;
    public String description;
    public int killId;
    public int mapId;
    public Char p;

    public TaskOrder(Char p, byte type) {
        this.p = p;
        this.taskId = type;
    }
    public TaskOrder(Char p, byte taskId, int count, int maxCount, int killId, int mapId) {
        this.p = p;
        this.count = count;
        this.maxCount = maxCount;
        this.taskId = taskId;
        this.killId = killId;
        this.mapId = mapId;
        switch (taskId) {

            case TASK_DAY:
                this.name = "Nhiệm vụ hàng ngày";
                break;

            case TASK_BOSS:
                this.name = "Nhiệm vụ truy bắt manh thú";
                break;
        }
        this.description = "Ghi chú: đi đến " + Map.maps[mapId].getMapTemplate().name + " để làm nhiệm vụ.";
    }

    public void setTask(int count, int maxCount, String name, String description, int killId, int mapId) {
        this.count = count;
        this.maxCount = maxCount;
        this.name = name;
        this.description = description;
        this.killId = killId;
        this.mapId = mapId;
    }

    public boolean isComplete() {
        return this.count >= this.maxCount;
    }

    public void updateTask(int count) {
        this.count += count;
        if (this.count > this.maxCount) {
            this.count = this.maxCount;
        }
        if(taskId==TASK_DAY)
        p.getService().updateTaskOrder(this);
        else
        p.getService().sendTaskBoss(this);
    }
}