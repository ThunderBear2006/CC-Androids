package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.BlockPos;

public class MoveToBlockTask extends BlockBasedTask
{
    private final double moveSpeed;
    private final EntityNavigation nav;

    public MoveToBlockTask(AndroidEntity android, double moveSpeed, BlockPos pos)
    {
        super(android, pos);
        this.moveSpeed = moveSpeed;
        nav = android.getNavigation();
    }

    @Override
    public String getName()
    {
        return "movingToBlock";
    }

    @Override
    public boolean shouldTick()
    {
        return !isInRange(1);
    }

    @Override
    public void firstTick() {}

    @Override
    public void tick()
    {
        if (!nav.isIdle())
            return;

        nav.startMovingAlong(nav.findPathTo(getTarget(), 0), moveSpeed);
    }

    @Override
    public void lastTick()
    {
        if (nav.isIdle())
            return;

        nav.stop();
    }
}
