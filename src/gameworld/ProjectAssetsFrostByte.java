package gameworld;

import engine.assets.core.AssetCatalog;
import engine.assets.ports.AssetIntensity;
import engine.assets.ports.AssetType;

public class ProjectAssetsFrostByte {

    public final AssetCatalog catalog;

    public ProjectAssetsFrostByte() {
        this.catalog = new AssetCatalog("src/resources/images2/");

        this.catalog.register("playerRight", "playerRight.png", AssetType.SPACESHIP, AssetIntensity.HIGH);
        this.catalog.register("playerLeft", "playerLeft.png", AssetType.SPACESHIP, AssetIntensity.HIGH);

        this.catalog.register("projectile", "projectile.png", AssetType.ASTEROID, AssetIntensity.MEDIUM);

        this.catalog.register("enemy", "enemy.png", AssetType.ENEMY, AssetIntensity.HIGH);
        this.catalog.register("enemyDead", "enemyDead.png", AssetType.ENEMY, AssetIntensity.MEDIUM);

        this.catalog.register("playerAttackingRight", "playerAttackingRight.png", AssetType.SPACESHIP, AssetIntensity.HIGH);
        this.catalog.register("playerAttackingLeft", "playerAttackingLeft.png", AssetType.SPACESHIP, AssetIntensity.HIGH);

        this.catalog.register("playerDodgingRight", "playerDodgingRight.png", AssetType.SPACESHIP, AssetIntensity.HIGH);
        this.catalog.register("playerDodgingLeft", "playerDodgingLeft.png", AssetType.SPACESHIP, AssetIntensity.HIGH);

        this.catalog.register("backHielo", "pistaHielo.png", AssetType.BACKGROUND, AssetIntensity.HIGH);
    }
}
