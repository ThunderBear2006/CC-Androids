package com.thunderbear06.computer.peripherals;

import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketAccess;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.impl.PocketUpgrades;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DummyPocket implements IPocketAccess {
    private final BaseAndroidEntity owner;

    public DummyPocket(BaseAndroidEntity container) {
        this.owner = container;
    }

    public UpgradeData<IPocketUpgrade> createUpgrade(ItemStack stack) {
        return PocketUpgrades.instance().get(stack);
    }

    @Override
    public ServerWorld getLevel() {
        return (ServerWorld) this.owner.getWorld();
    }

    @Override
    public Vec3d getPosition() {
        return this.owner.getPos();
    }

    @Override
    public @Nullable Entity getEntity() {
        return this.owner;
    }

    @Override
    public int getColour() {
        return 0;
    }

    @Override
    public void setColour(int colour) {}

    @Override
    public int getLight() {
        return 0;
    }

    @Override
    public void setLight(int colour) {}

    @Override
    public @Nullable UpgradeData<IPocketUpgrade> getUpgrade() {
        return null;
    }

    @Override
    public void setUpgrade(@Nullable UpgradeData<IPocketUpgrade> upgrade) {}

    @Override
    public NbtCompound getUpgradeNBTData() {
        return null;
    }

    @Override
    public void updateUpgradeNBTData() {}

    @Override
    public void invalidatePeripheral() {}

    @Override
    public Map<Identifier, IPeripheral> getUpgrades() {
        return Map.of();
    }
}
