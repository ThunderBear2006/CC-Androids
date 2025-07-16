package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.ai.task.Task;
import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.math.BlockPos;

public abstract class BlockBasedTask extends Task
{
    private final BlockPos target;

    public BlockBasedTask(AndroidEntity android, BlockPos pos)
    {
        super(android);

        target = pos;
    }

    protected boolean isInRange(double distance)
    {
        return this.android.getBlockPos().isWithinDistance(getTarget(), distance);
    }

    protected BlockPos getTarget()
    {
        return target;
    }
}
