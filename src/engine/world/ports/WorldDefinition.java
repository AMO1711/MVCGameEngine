package engine.world.ports;

import java.util.ArrayList;

import engine.assets.core.AssetCatalog;

public class WorldDefinition {

	// region Fields
	public final double worldWidth;
	public final double worldHeight;
	public final AssetCatalog gameAssets;

	public final DefBackgroundDTO background;

	// Entity polymorphic lists are grouped by type to simplify core consumption
	public final ArrayList<DefItem> spaceDecorators;
	public final ArrayList<DefItem> gravityBodies;
	public final ArrayList<DefItem> asteroids;
	public final ArrayList<DefItem> spaceships;

	public final ArrayList<DefEmitterDTO> trailEmitters;
	public final ArrayList<DefEmitterDTO> weapons;
	// endregion
	public String musicAssetId;
	// *** CONSTRUCTOR ***

	public WorldDefinition(
			double worldWidth,
			double worldHeight,
			AssetCatalog gameAssets,
			DefBackgroundDTO background,
			ArrayList<DefItem> spaceDecorators,
			ArrayList<DefItem> gravityBodies,
			ArrayList<DefItem> asteroids,
			ArrayList<DefItem> spaceships,
			ArrayList<DefEmitterDTO> trailEmitters,
			ArrayList<DefEmitterDTO> weapons) {

		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.gameAssets = gameAssets;
		this.background = background;
		this.spaceDecorators = spaceDecorators;
		this.gravityBodies = gravityBodies;
		this.asteroids = asteroids;
		this.weapons = weapons;
		this.spaceships = spaceships;
		this.trailEmitters = trailEmitters;
	}

	public WorldDefinition(
			double worldWidth,
			double worldHeight,
			AssetCatalog gameAssets,
			DefBackgroundDTO background,
			ArrayList<DefItem> spaceDecorators,
			ArrayList<DefItem> gravityBodies,
			ArrayList<DefItem> asteroids,
			ArrayList<DefItem> spaceships,
			ArrayList<DefEmitterDTO> trailEmitters,
			ArrayList<DefEmitterDTO> weapons,
			String musicAssetId) {

		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;
		this.gameAssets = gameAssets;
		this.background = background;
		this.spaceDecorators = spaceDecorators;
		this.gravityBodies = gravityBodies;
		this.asteroids = asteroids;
		this.weapons = weapons;
		this.spaceships = spaceships;
		this.trailEmitters = trailEmitters;
		this.musicAssetId = musicAssetId;
	}
}
