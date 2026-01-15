package world.ports;

import java.util.ArrayList;

import assets.core.AssetCatalog;

public class WorldDefinition {

    public final int worldWidth;
    public final int worldHeight;

    public final AssetCatalog gameAssets;

    public final WorldDefBackgroundDTO background;
    public final ArrayList<WorldDefPositionItemDTO> spaceDecorators;
    public final ArrayList<WorldDefPositionItemDTO> gravityBodies;
    public final ArrayList<WorldDefItemDTO> asteroids;
    public ArrayList<WorldDefItemDTO> spaceshipsDef;
    public final ArrayList<WorldDefItemDTO> trails;

    public final ArrayList<WorldDefWeaponDTO> primaryWeapon;
    public final ArrayList<WorldDefWeaponDTO> secondaryWeapon;
    public final ArrayList<WorldDefWeaponDTO> mineLaunchers;
    public final ArrayList<WorldDefWeaponDTO> missilLaunchers;


    public WorldDefinition(
            int worldWidth,
            int worldHeight,
            
            AssetCatalog gameAssets,

            WorldDefBackgroundDTO background,
            ArrayList<WorldDefPositionItemDTO> spaceDecorators,
            ArrayList<WorldDefPositionItemDTO> gravityBodies,

            ArrayList<WorldDefItemDTO> asteroids,
            ArrayList<WorldDefItemDTO> spaceships,
            ArrayList<WorldDefItemDTO> trails,

            ArrayList<WorldDefWeaponDTO> primaryWeapon,
            ArrayList<WorldDefWeaponDTO> secondaryWeapon,
            ArrayList<WorldDefWeaponDTO> mineLaunchers,
            ArrayList<WorldDefWeaponDTO> missilLaunchers) {

        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.gameAssets = gameAssets;
        this.background = background;
        this.spaceDecorators = spaceDecorators;
        this.gravityBodies = gravityBodies;
        this.asteroids = asteroids;
        this.primaryWeapon = primaryWeapon;
        this.secondaryWeapon = secondaryWeapon;
        this.mineLaunchers = mineLaunchers;
        this.missilLaunchers = missilLaunchers;
        this.spaceshipsDef = spaceships;
        this.trails = trails;
    }
}
