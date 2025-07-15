package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class InteractBlockTask extends MoveToBlockTask{
    private boolean complete = false;

    public InteractBlockTask(AndroidEntity android, double moveSpeed, BlockPos pos) {
        super(android, moveSpeed, pos);
    }

    @Override
    public String getName() {
        return "usingBlock";
    }

    @Override
    public void firstTick() {
        this.complete = false;
    }

    @Override
    public boolean shouldTick() {
        return !this.complete;
    }

    @Override
    public void tick() {
        if (isInRange(2)) {
            BlockPos pos = getTarget();
            this.android.getLookControl().lookAt(pos.getX(), pos.getY(), pos.getZ());
            this.android.brain.getModules().interactionModule.interactWithBlock(Hand.MAIN_HAND, pos);
            this.complete = true;
        } else
            super.tick();
    }
}
