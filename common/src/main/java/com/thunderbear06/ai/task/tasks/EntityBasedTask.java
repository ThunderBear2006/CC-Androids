package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.ai.task.Task;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.LivingEntity;

public abstract class EntityBasedTask extends Task {

    public EntityBasedTask(AndroidEntity android) {
        super(android);
    }

    @Override
    public boolean shouldTick() {
        return this.android.brain.getTargeting().hasEntityTarget();
    }

    @Override
    public void tick() {
        this.android.getLookControl().lookAt(getTarget());
    }

    protected boolean isInRange(double distance) {
        return this.android.getBlockPos().isWithinDistance(getTarget().getBlockPos(), distance);
    }

    protected LivingEntity getTarget() {
        return this.android.brain.getTargeting().getEntityTarget();
    }
}
