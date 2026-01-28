package game.worlddef;

import java.awt.Dimension;

import assets.impl.ProjectAssets;
import assets.ports.AssetType;
import model.bodies.ports.BodyType;
import utils.helpers.DoubleVector;
import world.core.AbstractWorldDefinitionProvider;

public final class RandomWorldDefinitionProvider extends AbstractWorldDefinitionProvider {

	// *** CONSTRUCTORS ***

	public RandomWorldDefinitionProvider(DoubleVector worldDimension, ProjectAssets assets) {
		super(worldDimension, assets);
	}

	// *** PROTECTED (alphabetical order) ***

	@Override
	protected void define() {
		double density = 100d;

		// region Background
		this.setBackgroundStatic("back_9");
		// endregion

		// region Decorations
		this.addDecoratorAnywhereRandomAsset(
				20, AssetType.STARS, density, 800, 2000);
		// endregion

		// region Gravity bodies => Static bodies
		this.addGravityBodyAnywhereRandomAsset(
				5, AssetType.PLANET, density, 800, 1600);

		this.addGravityBodyAnywhereRandomAsset(
				2, AssetType.MOON, density, 1000, 2000);

		this.addGravityBodyAnywhereRandomAsset(
				2, AssetType.SUN, density, 100, 600);

		this.addGravityBodyAnywhereRandomAsset(
				2, AssetType.BLACK_HOLE, density, 50, 250);
		// endregion

		// region Dynamic bodies
		this.addAsteroidPrototypeAnywhereRandomAsset(
				6, AssetType.ASTEROID,
				6, 25,
				10, 175,
				0, 150);
		// endregion

		// region Players
		this.addSpaceshipRandomAsset(
			1, AssetType.SPACESHIP, density, 50, 50, 200, 200);

		this.addTrailEmitterCosmetic("stars_6", 100, BodyType.DECORATOR, 20);
		// endregion

		// region Weapons (addWeapon***)
		this.addWeaponPresetBulletRandomAsset(AssetType.BULLET);

		this.addWeaponPresetBurstRandomAsset(AssetType.BULLET);

		this.addWeaponPresetMineLauncherRandomAsset(AssetType.MINE);

		this.addWeaponPresetMissileLauncherRandomAsset(AssetType.MISSILE);
		// endregion
	}
}
