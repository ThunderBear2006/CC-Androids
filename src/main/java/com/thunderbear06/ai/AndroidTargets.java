package com.thunderbear06.ai;

import com.thunderbear06.CCAndroids;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class AndroidTargets {
    public LivingEntity entityTarget = null;
    public BlockPos blockTarget = null;

    public void setEntityTarget(LivingEntity entity) {
        CCAndroids.LOGGER.info("Set android entity target to {}", entity == null ? "null" : entity.getName());
        this.entityTarget = entity;
    }

    public void setBlockTarget(BlockPos pos) {
        CCAndroids.LOGGER.info("Set android block target to {}", pos == null ? "null" : pos.toString());
        this.blockTarget = pos;
    }

    public boolean hasEntityTarget() {
        return this.entityTarget != null && !this.entityTarget.isRemoved();
    }

    public boolean hasBlockTarget() {
        return this.blockTarget != null;
    }

    public LivingEntity getEntityTarget() {
        return this.entityTarget;
    }

    public BlockPos getBlockTarget() {
        return this.blockTarget;
    }

    public void clearTargets(){
        CCAndroids.LOGGER.info("Cleared android targets");
        this.entityTarget = null;
        this.blockTarget = null;
    }
}
