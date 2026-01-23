package generators.implementations;

import controller.ports.WorldInitializer;
import generators.implementations.level.SimpleRandomLevelGenerator;
import world.ports.WorldDefinition;

public class DefaultLevelGenerator extends SimpleRandomLevelGenerator {

    // No additional functionality
    // just a default naming wrapper :-)
    // Intended for easier swapping in Main.java

    // *** CONSTRUCTOR *** //

    public DefaultLevelGenerator(WorldInitializer worldInitializer, WorldDefinition worldDef) {
        super(worldInitializer, worldDef);
    }

}
