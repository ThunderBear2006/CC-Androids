package com.thunderbear06.ai;

import com.mojang.authlib.GameProfile;
import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.modules.AndroidModules;
import com.thunderbear06.ai.task.Task;
import com.thunderbear06.ai.task.TaskManager;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.player.AndroidPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

public class AndroidBrain
{
    protected final AndroidEntity android;
    protected final TaskManager taskManager;
    protected final AndroidModules modules;

    @Deprecated
    public AndroidPlayer fakePlayer;
    private GameProfile owningPlayerProfile;

    public AndroidBrain(AndroidEntity entity)
    {
        android = entity;
        taskManager = new TaskManager();
        modules = new AndroidModules(entity, this);

        if (android.getWorld() instanceof ServerWorld)
        {
            this.fakePlayer = AndroidPlayer.get(this);
        }
        else
        {
            this.fakePlayer = null;
        }
    }

    public void onShutdown()
    {
        taskManager.clearCurrentTask();
    }

    public void setTask(Task task)
    {
        if (CCAndroids.CONFIG.DebugLogging)
            CCAndroids.LOGGER.info("Set current android task to {}", task.getName());

        taskManager.setCurrentTask(task);
    }

    public AndroidEntity getAndroid()
    {
        return this.android;
    }

    public AndroidModules getModules()
    {
        return this.modules;
    }

    public TaskManager getTaskManager()
    {
        return taskManager;
    }

    public boolean isOwningPlayer(PlayerEntity player)
    {
        return this.owningPlayerProfile == player.getGameProfile();
    }

    public GameProfile getOwningPlayerProfile()
    {
        return this.owningPlayerProfile;
    }

    public void setOwningPlayer(GameProfile gameProfile)
    {
        this.owningPlayerProfile = gameProfile;
    }

    public void writeNbt(NbtCompound computerCompound)
    {
        if (this.owningPlayerProfile == null)
            return;

        computerCompound.putUuid("OwningPlayerUUID", this.owningPlayerProfile.getId());
        computerCompound.putString("OwningPlayerName", this.owningPlayerProfile.getName());
    }

    public void readNbt(NbtCompound computerCompound)
    {
        if (!computerCompound.contains("OwningPlayerUUID"))
            return;

        this.owningPlayerProfile = new GameProfile(computerCompound.getUuid("OwningPlayerUUID"), computerCompound.getString("OwningPlayerName"));
    }
}
