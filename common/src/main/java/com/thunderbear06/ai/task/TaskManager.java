package com.thunderbear06.ai.task;

public class TaskManager
{
    private Task currentTask = null;

    public void setCurrentTask(Task task)
    {
        if (this.currentTask != null)
            clearCurrentTask();

        this.currentTask = task;
        this.currentTask.firstTick();
    }

    public String getCurrentTaskName()
    {
        return this.currentTask == null ? "idle" : this.currentTask.getName();
    }

    public void clearCurrentTask()
    {
        if (this.currentTask == null)
            return;
        this.currentTask.lastTick();
        this.currentTask = null;
    }

    public void tick()
    {
        if (this.currentTask == null)
        {
            return;
        }

        if (this.currentTask.shouldTick())
        {
            this.currentTask.tick();
            return;
        }

        clearCurrentTask();
    }

    public boolean isIdle()
    {
        return this.currentTask == null;
    }
}
