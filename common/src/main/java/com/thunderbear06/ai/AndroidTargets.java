package com.thunderbear06.ai;

import com.thunderbear06.CCAndroids;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;

public class AndroidTargets {
    private LivingEntity entityTarget = null;
    private BlockPos blockTarget = null;

    public void setEntityTarget(LivingEntity entity) {
        this.entityTarget = entity;
    }

    public void setBlockTarget(BlockPos pos) {
        this.blockTarget = pos;
    }

    public boolean hasEntityTarget() {
        return this.entityTarget != null && !this.entityTarget.isRemoved();
    }

    public boolean hasBlockTarget() {
        return this.blockTarget != null;
    }

    public boolean hasAnyTarget() {
        return this.entityTarget != null || this.blockTarget != null;
    }

    public LivingEntity getEntityTarget() {
        return this.entityTarget;
    }

    public BlockPos getBlockTarget() {
        return this.blockTarget;
    }

    public void clearTargets(){
        if (CCAndroids.CONFIG.DebugLogging)
            CCAndroids.LOGGER.info("Cleared android targets");
        this.entityTarget = null;
        this.blockTarget = null;
    }
}
