package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.util.math.BlockPos;

public class MoveToEntityTask extends EntityBasedTask{
    private final double moveSpeed;
    private final EntityNavigation nav;

    public MoveToEntityTask(AndroidEntity android, double moveSpeed, LivingEntity entity) {
        super(android, entity);
        this.moveSpeed = moveSpeed;

        nav = android.getNavigation();
    }

    @Override
    public String getName() {
        return "movingToEntity";
    }

    @Override
    public boolean shouldTick() {
        return super.shouldTick() && !isInRange(2);
    }

    @Override
    public void firstTick() {}

    @Override
    public void tick() {
        super.tick();

        if (nav.isIdle()) {
            BlockPos entityPos = getTarget().getBlockPos();
            nav.startMovingTo(entityPos.getX(), entityPos.getY(), entityPos.getZ(), this.moveSpeed);
        }
    }

    @Override
    public void lastTick()
    {
        if (nav.isIdle())
            return;

        nav.stop();
    }
}
