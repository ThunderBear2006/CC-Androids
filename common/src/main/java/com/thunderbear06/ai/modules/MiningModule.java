package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class MiningModule extends AbstractAndroidModule {
    private float breakProgress = 0.0f;

    public MiningModule(BaseAndroidEntity owner, AndroidBrain brain) {
        super(owner, brain);
    }

    public void mine(BlockPos pos) {
        this.breakProgress = tickBreakProgress(pos, this.breakProgress);

        if (this.breakProgress >= 10.0f) {
            breakBlock(pos);
        }
    }

    public void breakBlock(BlockPos pos) {
        AndroidPlayer.get(this.brain).player().interactionManager.tryBreakBlock(pos);
    }

    public void resetBreakProgress(BlockPos pos) {
        this.android.getWorld().setBlockBreakingInfo(this.android.getId(), pos, 0);
        this.breakProgress = 0.0f;
    }

    public boolean canMineBlock(BlockPos pos) {
        BlockState state = this.android.getWorld().getBlockState(pos);

        return !state.isAir() && state.getHardness(this.android.getWorld(), pos) > -1;
    }

    private float tickBreakProgress(BlockPos pos, float progress) {
        this.android.getWorld().setBlockBreakingInfo(this.android.getId(), pos, (int) progress);
        progress += getBreakSpeed(pos);
        return progress;
    }

    private float getBreakSpeed(BlockPos pos) {
        BlockState state = android.getWorld().getBlockState(pos);

        ServerPlayerEntity player = AndroidPlayer.get(brain).player();

        float hardnessMod = state.getHardness(android.getWorld(), pos);
        int canHarvestMod = player.canHarvest(state) ? 30 : 100;

        return (player.getBlockBreakingSpeed(state) / hardnessMod / canHarvestMod) * 100.0f;
    }
}
