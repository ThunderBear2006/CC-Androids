package com.thunderbear06.computer;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.server.world.ServerWorld;

public class EntityComputer extends ServerComputer {
    private final BaseAndroidEntity entity;

    public EntityComputer(ServerWorld level, BaseAndroidEntity entity, Properties properties) {
        super(level, entity.getBlockPos(), properties);
        this.entity = entity;
    }

    @Override
    protected void tickServer() {
        super.tickServer();
        setPosition((ServerWorld) entity.getWorld(), entity.getBlockPos());
    }
}
