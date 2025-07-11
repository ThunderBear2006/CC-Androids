package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.ai.task.Task;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.math.BlockPos;

public abstract class BlockBasedTask extends Task {
    public BlockBasedTask(AndroidEntity android) {
        super(android);
    }

    @Override
    public boolean shouldTick() {
        return this.android.brain.getTargeting().hasBlockTarget();
    }

    protected boolean isInRange(double distance) {
        return this.android.getBlockPos().isWithinDistance(getPos(), distance);
    }

    protected BlockPos getPos() {
        return this.android.brain.getTargeting().getBlockTarget();
    }
}
