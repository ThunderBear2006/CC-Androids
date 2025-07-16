package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class BreakBlockTask extends MoveToBlockTask
{

    public BreakBlockTask(AndroidEntity android, double moveSpeed, BlockPos pos)
    {
        super(android, moveSpeed, pos);
    }

    @Override
    public String getName()
    {
        return "breakingBlock";
    }

    @Override
    public boolean shouldTick()
    {
        return android.brain.getModules().miningModule.canMineBlock(getTarget());
    }

    @Override
    public void tick()
    {
        Vec3d pos = getTarget().toCenterPos();
        this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());

        if (isInRange(3)) {
            this.android.swingHand(Hand.MAIN_HAND);
            this.android.brain.getModules().miningModule.mine(getTarget());
        }
        else
            super.tick();
    }

    @Override
    public void lastTick()
    {
        this.android.brain.getModules().miningModule.resetBreakProgress(getTarget());
    }
}
