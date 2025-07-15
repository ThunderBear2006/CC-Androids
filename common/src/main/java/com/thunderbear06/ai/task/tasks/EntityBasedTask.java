package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.ai.task.Task;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public abstract class EntityBasedTask extends Task {
    private final LivingEntity targetEntity;

    public EntityBasedTask(AndroidEntity android, LivingEntity entity) {
        super(android);

        targetEntity = entity;
    }

    @Override
    public boolean shouldTick() {
        return targetEntity.isAlive();
    }

    @Override
    public void tick() {
        this.android.getLookControl().lookAt(getTarget());
    }

    protected boolean isInRange(double distance) {
        return this.android.getBlockPos().isWithinDistance(getTarget().getBlockPos(), distance);
    }

    protected LivingEntity getTarget() {
        return targetEntity;
    }
}
