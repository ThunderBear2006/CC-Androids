package com.thunderbear06.ai.task.tasks;

import com.thunderbear06.entity.android.AndroidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;

public class AttackEntityTask extends MoveToEntityTask
{
    private int attackCooldown;

    public AttackEntityTask(AndroidEntity android, double moveSpeed, LivingEntity entity)
    {
        super(android, moveSpeed, entity);
    }

    @Override
    public String getName()
    {
        return "attacking";
    }

    @Override
    public boolean shouldTick()
    {
        return getTarget().isAlive();
    }

    @Override
    public void tick()
    {
        if (this.attackCooldown-- > 0)
            return;

        if (isInRange(2) && this.attackCooldown <= 0)
            attack();
        else
            super.tick();
    }

    private void attack()
    {
        this.attackCooldown = 10;

        LivingEntity target = getTarget();

        this.android.getLookControl().lookAt(target);
        this.android.swingHand(Hand.MAIN_HAND);
        this.android.tryAttack(target);
    }
}
