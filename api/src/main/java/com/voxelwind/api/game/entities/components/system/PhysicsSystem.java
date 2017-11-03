package com.voxelwind.api.game.entities.components.system;

import com.flowpowered.math.vector.Vector3f;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.entities.components.Physics;

/**
 * Provides a {@link System} for handling a {@link Physics} component.
 */
public class PhysicsSystem implements SystemRunner
{
	public static final System SYSTEM = System.builder ()
			.expectComponent (Physics.class)
			.runner (new PhysicsSystem ())
			.build ();

	private PhysicsSystem ()
	{

	}

	@Override
	public void run (Entity entity)
	{
		Physics physics = entity.ensureAndGet (Physics.class);
		if (entity.getMotion ().lengthSquared () > 0)
		{
			boolean onGroundPreviously = entity.isOnGround ();
			entity.setPositionFromSystem (entity.getPosition ().add (entity.getMotion ()));
			boolean onGroundNow = entity.isOnGround ();

			if (!onGroundPreviously && onGroundNow)
			{
				entity.setPositionFromSystem (new Vector3f (entity.getPosition ().getX (), entity.getPosition ().getFloorY (), entity.getPosition ().getZ ()));
				entity.setMotion (Vector3f.ZERO);
			} else
			{
				entity.setMotion (entity.getMotion ().mul (1f - physics.getDrag ()));
				if (!onGroundNow)
				{
					entity.setMotion (entity.getMotion ().sub (0, physics.getGravity (), 0));
				}
			}
		}
	}
}
