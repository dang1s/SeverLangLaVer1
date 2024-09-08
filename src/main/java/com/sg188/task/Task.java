package com.sg188.task;

import com.sg188.data.DataCenter;
import com.sg188.data.Step;
import com.sg188.data.TaskTemplate;

import java.util.ArrayList;
import java.util.List;

public class Task {

    public static TaskTemplate getTaskTemplate(int id) {
        for (TaskTemplate task : DataCenter.gI().TaskTemplate) {
            if (task.getId() == id) {
                return task;
            }
        }
        return null;
    }

    public int index;
    public short taskId;
    public short count;
    public TaskTemplate template;
    public List<Step> vStep = new ArrayList<>();

    public Task(short taskId, byte index, short count) {
        this.taskId = taskId;
        this.index = index;
        this.count = count;
        this.template = getTaskTemplate(taskId);
        this.vStep = template.getVStep();
    }

    public boolean isComplete() {
        return index >= template.getVStep().size() - 1;
    }
}
