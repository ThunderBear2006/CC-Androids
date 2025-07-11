package com.thunderbear06.forge;

import com.thunderbear06.AndroidPlatformHelper;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.impl.Peripherals;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AndroidPlatformHelperForge extends AndroidPlatformHelper {
	@Override
	public IPeripheral getPeripheral(ServerWorld world, BlockPos pos, Direction side, Runnable invalidate) {
		return Peripherals.getPeripheral(world, pos, side, invalidate::run);
	}

	public static void init() {
		if (INSTANCE == null) {
			INSTANCE = new AndroidPlatformHelperForge();
		}
	}
}
