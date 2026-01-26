package world.implementations;

import assets.implementations.ProjectAssets;
import assets.ports.AssetType;
import world.core.AbstractWorldDefinitionProvider;

public final class RandomWorldDefinitionProvider extends AbstractWorldDefinitionProvider {

	public RandomWorldDefinitionProvider(double worldWidth, double worldHeight, ProjectAssets assets) {
		super(worldWidth, worldHeight, assets);
	}

	@Override
	protected void define() {
		double density = 100d;

		this.setBackgroundStatic("back_9");

		this.addDecoratorAnywhereRandomAsset(
				2, AssetType.STARS, density, 200, 600);

		this.addGravityBodyAnywhereRandomAsset(
				1, AssetType.PLANET, density, 100, 200);

		this.addGravityBodyAnywhereRandomAsset(
				1, AssetType.MOON, density, 40, 80);

		this.addGravityBodyAnywhereRandomAsset(
				1, AssetType.SUN, density, 20, 40);

		this.addGravityBodyAnywhereRandomAsset(
				1, AssetType.BLACK_HOLE, density, 45, 55);

		this.addAsteroidPrototypeAnywhereRandomAsset(
				6, AssetType.ASTEROID, density, 6, 25);

		this.addSpaceshipAnywhereRandomAsset(
				1, AssetType.SPACESHIP, density, 40, 40);

		this.addTrailEmitterCosmetic("stars_6", 100, 20);

		// region Weapons (addWeapon***)
		this.addWeaponPresetBulletRandomAsset(AssetType.BULLET);

		this.addWeaponPresetBurstRandomAsset(AssetType.BULLET, 0.0);

		this.addWeaponPresetMineLauncherRandomAsset(AssetType.MINE);

		this.addWeaponPresetMissileLauncherRandomAsset(AssetType.MISSILE);
		// endregion
	}
}
