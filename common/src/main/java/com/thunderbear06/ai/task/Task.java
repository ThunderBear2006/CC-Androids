package com.thunderbear06.ai.task;

import com.thunderbear06.entity.android.AndroidEntity;

public abstract class Task {
    protected final AndroidEntity android;

    public Task(AndroidEntity android) {
        this.android = android;
    }

    public abstract String getName();

    public boolean shouldTick() {
        return false;
    }

    public abstract void firstTick();
    public abstract void tick();
    public abstract void lastTick();
}
