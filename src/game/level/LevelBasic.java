package game.level;

import java.util.ArrayList;

import controller.ports.WorldInitializer;
import game.core.AbstractLevelGenerator;
import world.ports.DefItem;
import world.ports.DefItemDTO;
import world.ports.WorldDefinition;

public class LevelBasic extends AbstractLevelGenerator {

    // *** CONSTRUCTORS ***

    public LevelBasic(WorldInitializer worldInitializer, WorldDefinition worldDef) {
        super(worldInitializer, worldDef);
    }

    // *** PROTECTED (alphabetic order) ***

    @Override
    protected void createSpaceDecorators() {
        ArrayList<DefItem> decorators = this.getWorldDefinition().spaceDecorators;

        for (DefItem def : decorators) {
            DefItemDTO deco = this.defItemToDTO(def);
            this.addDecoratorIntoTheGame(deco);
        }
    }

    @Override
    protected void createStaticBodies() {
        ArrayList<DefItem> bodyDefs = this.getWorldDefinition().gravityBodies;

        for (DefItem def : bodyDefs) {
            DefItemDTO body = this.defItemToDTO(def);
            this.addStaticTheGame(body);
        }
    }
}
