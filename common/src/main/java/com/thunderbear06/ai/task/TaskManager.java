package com.thunderbear06.ai.task;

import com.thunderbear06.CCAndroids;

import java.util.HashMap;

public class TaskManager {
    private Task currentTask = null;

    public void setCurrentTask(Task task) {
        if (this.currentTask != null)
            clearCurrentTask();

        this.currentTask = task;
        this.currentTask.firstTick();
    }

    public String getCurrentTaskName() {
        return this.currentTask == null ? "idle" : this.currentTask.getName();
    }

    public void clearCurrentTask() {
        if (this.currentTask == null)
            return;
        this.currentTask.lastTick();
        this.currentTask = null;
    }

    public void tick() {
        if (this.currentTask == null) {
            return;
        }

        if (this.currentTask.shouldTick()) {
            this.currentTask.tick();
            return;
        }

        clearCurrentTask();
    }

    public boolean hasTask() {
        return this.currentTask != null;
    }
}
