package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.util.Hand;

public class InteractEntityTask extends MoveToEntityTask {
    private boolean complete = false;

    public InteractEntityTask(AndroidEntity android, double moveSpeed) {
        super(android, moveSpeed);
    }

    @Override
    public String getName() {
        return "usingEntity";
    }

    @Override
    public void firstTick() {
        this.complete = false;
    }

    @Override
    public boolean shouldTick() {
        return this.android.brain.getTargeting().hasEntityTarget() && !this.complete;
    }

    @Override
    public void tick() {
        if (isInRange(2)) {
            this.android.getLookControl().lookAt(getTarget());
            this.android.brain.getModules().interactionModule.interactWithEntity(Hand.MAIN_HAND, getTarget());
            this.complete = true;
        }
        else
            super.tick();
    }
}
