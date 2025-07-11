package com.thunderbear06.ai;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.modules.AndroidModules;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

public class AndroidBrain {
    protected final AndroidEntity android;
    protected final AndroidTargets targeting;
    protected final AndroidModules modules;

    @Deprecated
    public AndroidPlayer fakePlayer;
    private GameProfile owningPlayerProfile;

    public AndroidBrain(AndroidEntity android) {
        this.android = android;
        this.targeting = new AndroidTargets();
        this.modules = new AndroidModules(android, this);

        if (android.getWorld() instanceof ServerWorld) {
            this.fakePlayer = AndroidPlayer.get(this);
        } else {
            this.fakePlayer = null;
        }
    }

    public void onShutdown() {
        this.targeting.clearTargets();
        this.android.getTaskManager().clearCurrentTask();
    }

    public void setTask(String taskName) {
        if (CCAndroids.CONFIG.DebugLogging)
            CCAndroids.LOGGER.info("Set current android task to {}", taskName);
        this.android.getTaskManager().setCurrentTask(taskName);
    }

    public AndroidEntity getAndroid() {
        return this.android;
    }

    public AndroidTargets getTargeting() {
        return this.targeting;
    }

    public AndroidModules getModules() {
        return this.modules;
    }

    public boolean isOwningPlayer(PlayerEntity player) {
        return this.owningPlayerProfile == player.getGameProfile();
    }

    public GameProfile getOwningPlayerProfile() {
        return this.owningPlayerProfile;
    }

    public void setOwningPlayer(GameProfile gameProfile) {
        this.owningPlayerProfile = gameProfile;
    }

    public void writeNbt(NbtCompound computerCompound) {
        if (this.owningPlayerProfile == null)
            return;

        computerCompound.putUuid("OwningPlayerUUID", this.owningPlayerProfile.getId());
        computerCompound.putString("OwningPlayerName", this.owningPlayerProfile.getName());
    }

    public void readNbt(NbtCompound computerCompound) {
        if (!computerCompound.contains("OwningPlayerUUID"))
            return;

        this.owningPlayerProfile = new GameProfile(computerCompound.getUuid("OwningPlayerUUID"), computerCompound.getString("OwningPlayerName"));
    }
}
