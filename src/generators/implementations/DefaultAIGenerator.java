package generators.implementations;

import controller.ports.WorldEvolver;
import generators.ports.AIConfigDTO;
import world.ports.WorldDefinition;

public class DefaultAIGenerator
        extends generators.implementations.ai.AsteroidSpawnFromCenterAIGenerator {

    // No additional functionality
    //
    // Just a default naming wrapper :-)
    //
    // Intended for easier swapping IA generator implementations.
    //
    // NO MODIFICATIONS REQUIRED IN in Main.java

    // *** CONSTRUCTOR *** //

    public DefaultAIGenerator(WorldEvolver controller,
            WorldDefinition worldDefinition, AIConfigDTO lifeConfig) {
        super(controller, worldDefinition, lifeConfig);
    }
}
