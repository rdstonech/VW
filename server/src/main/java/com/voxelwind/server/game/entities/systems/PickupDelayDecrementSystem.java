package com.voxelwind.server.game.entities.systems;

import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.entities.components.PickupDelay;
import com.voxelwind.api.game.entities.components.system.System;
import com.voxelwind.api.game.entities.components.system.SystemRunner;

public class PickupDelayDecrementSystem implements SystemRunner
{
	public static final System SYSTEM = System.builder ()
			.expectComponent (PickupDelay.class)
			.runner (new PickupDelayDecrementSystem ())
			.build ();

	private PickupDelayDecrementSystem ()
	{

	}

	@Override
	public void run (Entity entity)
	{
		PickupDelay delay = entity.ensureAndGet (PickupDelay.class);
		if (delay.getDelayPickupTicks () > 0)
		{
			delay.setDelayPickupTicks (delay.getDelayPickupTicks () - 1);
		}
	}
}
