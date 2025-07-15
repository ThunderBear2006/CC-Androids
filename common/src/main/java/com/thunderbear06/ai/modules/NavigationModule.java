package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.ai.task.tasks.MoveToBlockTask;
import com.thunderbear06.ai.task.tasks.MoveToEntityTask;
import com.thunderbear06.computer.api.AndroidAPI;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.lua.MethodResult;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class NavigationModule extends AbstractAndroidModule {
    public NavigationModule(BaseAndroidEntity owner, AndroidBrain brain) {
        super(owner, brain);
    }

    public MethodResult MoveToBlock(BlockPos pos) {
        if (!this.android.getWorld().isInBuildLimit(pos))
            return MethodResult.of(true, "Block pos must be within build limit");

        this.brain.setTask(new MoveToBlockTask((AndroidEntity) android, 0.5, pos));

        return MethodResult.of();
    }

    public MethodResult MoveToEntity(String entityUUID) {
        LivingEntity target = (LivingEntity) ((ServerWorld)this.brain.getAndroid().getWorld()).getEntity(UUID.fromString(entityUUID));

        if (target == null || target.isRemoved())
            return MethodResult.of(true, "Unknown entity or invalid UUID");

        this.brain.setTask(new MoveToEntityTask((AndroidEntity) android, 0.5, target));

        return MethodResult.of();
    }
}
