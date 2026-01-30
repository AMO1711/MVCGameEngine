package game.worlddef;

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
		this.setBackgroundStatic("back_10");
		// endregion

		// region Decorations
		this.addDecoratorAnywhereRandomAsset(5, AssetType.STARS, density, 200, 600);
		this.addDecorator("rainbow_1", 7000, 5000, 3500);
		this.addDecoratorAnywhereRandomAsset(5, AssetType.GALAXY, density, 200, 600);
		this.addDecoratorAnywhereRandomAsset(5, AssetType.GALAXY, density, 50, 300);
		this.addDecoratorAnywhereRandomAsset(5, AssetType.HALO, density, 50, 200);
		// endregion

		// region Gravity bodies => Static bodies
		this.addGravityBody("planet_4", 4500, 4500, 1000);
		this.addGravityBody("cosmic_portal_1", 300, 1100, 400);
		this.addGravityBody("sun_2", 24000, 2000, 2000);
		this.addGravityBody("lab_1", 3000, 14000, 700);
		this.addGravityBody("black_hole_1", 26000, 26000, 800);
		this.addGravityBody("black_hole_2", 22000, 9000, 400);

		this.addGravityBodyAnywhereRandomAsset(5, AssetType.PLANET, density, 100, 300);
		this.addGravityBodyAnywhereRandomAsset(5, AssetType.MOON, density, 100, 600);
		this.addGravityBodyAnywhereRandomAsset(20, AssetType.MINE, density, 50, 100);
		// endregion

		// region Dynamic bodies
		this.addAsteroidPrototypeAnywhereRandomAsset(
				6, AssetType.ASTEROID,
				6, 25,
				10, 175,
				0, 150);
		// endregion

		// region Players
		this.addSpaceshipRandomAsset(1, AssetType.SPACESHIP, density, 50, 60, 200, 200);
		this.addTrailEmitterCosmetic("stars_6", 200, BodyType.DECORATOR, 20);
		// endregion

		// region Weapons (addWeapon***)
		this.addWeaponPresetBulletRandomAsset(AssetType.BULLET);

		this.addWeaponPresetBurstRandomAsset(AssetType.BULLET);

		this.addWeaponPresetMineLauncherRandomAsset(AssetType.MINE);

		this.addWeaponPresetMissileLauncherRandomAsset(AssetType.MISSILE);
		// endregion
	}
}
