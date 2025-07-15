package com.thunderbear06;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class AndroidPlatformHelper {
	protected static AndroidPlatformHelper INSTANCE;

	public abstract IPeripheral getPeripheral(ServerWorld world, BlockPos pos, Direction side, Runnable invalidate);

	public static AndroidPlatformHelper get() {
		return INSTANCE;
	}
}
