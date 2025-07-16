package com.thunderbear06.ai;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;

public class AndroidLookAtEntityGoal extends LookAtEntityGoal
{
    private final AndroidBrain brain;

    public AndroidLookAtEntityGoal(AndroidEntity android, Class<? extends LivingEntity> targetType, float range)
    {
        super(android, targetType, range);
        brain = android.brain;
    }

    @Override
    public boolean shouldContinue()
    {
        return brain.getTaskManager().isIdle() && super.shouldContinue();
    }

    @Override
    public boolean canStart()
    {
        return brain.getTaskManager().isIdle() && super.canStart();
    }
}
