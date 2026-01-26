package generators.implementations.level;

import java.util.ArrayList;
import java.util.Random;

import controller.ports.WorldInitializer;
import world.ports.DefItemDTO;
import world.ports.WorldDefinition;

public class SimpleRandomLevelGenerator {

    private final Random rnd = new Random();
    private final WorldInitializer worldInitializer;
    WorldDefinition worldDefinition;

    public SimpleRandomLevelGenerator(WorldInitializer worldInitializer, WorldDefinition worldDef) {
        this.worldInitializer = worldInitializer;
        this.worldDefinition = worldDef;

        this.createWorld();
    }

    private void createWorld() {
        this.worldInitializer.loadAssets(this.worldDefinition.gameAssets);

        this.createSpaceDecorators();
        this.createSBodies();
    }

    private void createSBodies() {
        ArrayList<DefItemDTO> sBodies = this.worldDefinition.gravityBodies;

        for (DefItemDTO body : sBodies) {
            this.worldInitializer.addStaticBody(body.assetId, body.size, body.posX, body.posY, body.angle);
        }
    }

    private void createSpaceDecorators() {
        ArrayList<DefItemDTO> decorators = this.worldDefinition.spaceDecorators;

        for (DefItemDTO deco : decorators) {
            this.worldInitializer.addDecorator(deco.assetId, deco.size, deco.posX, deco.posY, deco.angle);
        }
    }

    private double randomAngularSpeed(double maxAngularSpeed) {
        return this.rnd.nextFloat() * maxAngularSpeed;
    }
}
