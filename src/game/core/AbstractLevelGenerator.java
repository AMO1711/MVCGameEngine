package game.core;

import java.util.Random;

import controller.ports.WorldInitializer;
import world.ports.DefItem;
import world.ports.DefItemDTO;
import world.ports.DefItemPrototypeDTO;
import world.ports.WorldDefinition;

public abstract class AbstractLevelGenerator {

    // region Fields
    private final Random rnd = new Random();
    private final DefItemMaterializer defItemMaterializer = new DefItemMaterializer();
    private final WorldInitializer worldInitializer;
    private final WorldDefinition worldDefinition;
    // endregion

    // *** CONSTRUCTORS ***

    protected AbstractLevelGenerator(WorldInitializer worldInitializer, WorldDefinition worldDef) {
        if (worldInitializer == null) {
            throw new IllegalArgumentException("WorldInitializer cannot be null.");
        }
        if (worldDef == null) {
            throw new IllegalArgumentException("WorldDefinition cannot be null.");
        }

        this.worldInitializer = worldInitializer;
        this.worldDefinition = worldDef;

        this.createWorld();
    }

    // *** PROTECTED ***
    protected void addDecoratorIntoTheGame(DefItemDTO deco) {
        this.worldInitializer.addDecorator(deco.assetId, deco.size, deco.posX, deco.posY, deco.angle);
    }

    protected void addStaticTheGame(DefItemDTO bodyDef) {
        this.worldInitializer.addStaticBody(
                bodyDef.assetId, bodyDef.size,
                bodyDef.posX, bodyDef.posY,
                bodyDef.angle);
    }

    /**
     * Standard world creation pipeline.
     * Subclasses decide which sections they want to create.
     */
    protected void createWorld() {
        this.worldInitializer.loadAssets(this.worldDefinition.gameAssets);

        this.createSpaceDecorators();
        this.createStaticBodies();
    }

    /**
     * Default: no-op. Override if generator needs it.
     */
    protected void createStaticBodies() {
        // no-op by default
    }

    /**
     * Default: no-op. Override if generator needs it.
     */
    protected void createSpaceDecorators() {
        // no-op by default
    }

    protected final DefItemDTO defItemToDTO(DefItem defitem) {
        return this.defItemMaterializer.defItemToDTO(defitem);
    }

    protected WorldDefinition getWorldDefinition() {
        return this.worldDefinition;
    }

    protected WorldInitializer getWorldInitializer() {
        return this.worldInitializer;
    }

    protected final double randomDoubleBetween(double minInclusive, double maxInclusive) {
        if (maxInclusive < minInclusive) {
            throw new IllegalArgumentException("maxInclusive must be >= minInclusive");
        }
        if (maxInclusive == minInclusive) {
            return minInclusive;
        }
        return minInclusive + (this.rnd.nextDouble() * (maxInclusive - minInclusive));
    }
}
