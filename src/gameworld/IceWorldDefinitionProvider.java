package gameworld;

import engine.assets.ports.AssetType;
import engine.model.bodies.ports.BodyType;
import engine.utils.helpers.DoubleVector;
import engine.world.core.AbstractWorldDefinitionProvider;

public class IceWorldDefinitionProvider extends AbstractWorldDefinitionProvider{

    public IceWorldDefinitionProvider(DoubleVector worldDimension, ProjectAssets assets) {
		super(worldDimension, assets);
	}

    @Override
    protected void define() {
        double density = 100d;

		// region Background
		this.setBackgroundStatic("backHielo");
		// endregion

		this.setMusic("musicaHielo");

		// region Gravity bodies => Static bodies
		this.addGravityBody("lab_01", 12000, 24000, 400);

		// endregion

		// region Dynamic bodies
		this.addAsteroidPrototypeAnywhereRandomAsset(
				6, AssetType.ASTEROID,
				10, 25,
				10, 750,
				0, 150);

		this.addEnemyPrototypeAnywhereRandomAsset(1, AssetType.ENEMY, 120, 180, 0, 0, 0, 0);
		// endregion

		// region Players
		this.addSpaceship("playerRight", 2000, 2000, 100);
		this.addTrailEmitterCosmetic("stars_05", 150, BodyType.DECORATOR, 25);
		// endregion

		// region Weapons (addWeapon***)
		this.addWeaponPresetBulletRandomAsset(AssetType.BULLET);

		this.addWeaponPresetBurstRandomAsset(AssetType.BULLET);

		this.addWeaponPresetMineLauncherRandomAsset(AssetType.MINE);

		this.addWeaponPresetMissileLauncherRandomAsset(AssetType.MISSILE);
		// endregion
    }


}
