package world.implementations;

import assets.implementations.ProjectAssets;
import assets.ports.AssetType;
import model.bodies.ports.BodyType;
import world.core.AbstractWorldDefinitionProvider;

public final class EarthInCenterWorldDefinitionProvider extends AbstractWorldDefinitionProvider {

    public EarthInCenterWorldDefinitionProvider(double worldWidth, double worldHeight, ProjectAssets assets) {
        super(worldWidth, worldHeight, assets);
    }

    @Override
    protected void define() {

        setBackgroundStatic("back_3");

        addGravityBody("stars_2", 2100, 300, 600);

        addGravityBody("planet_4", worldWidth / 2.0, worldHeight / 2.0, 500);

        addAsteroidPrototypeAnywhereRandomAsset(
                6, AssetType.ASTEROID, 1, 40, 90);

        addSpaceshipPrototypeAnywhereRandomAsset(
                1, AssetType.SPACESHIP, 1, 60, 120);

        addTrailEmitterCosmetic("stars_6", 100.0, BodyType.DECORATOR, 100.0);

        addWeaponPresetBulletRandomAsset(AssetType.BULLET);

        addWeaponPresetBurstRandomAsset(AssetType.BULLET);

        addWeaponPresetMineLauncherRandomAsset(AssetType.MINE);

        addWeaponPresetMissileLauncherRandomAsset(AssetType.MISSILE);
    }
}
