package gameai;

import java.util.ArrayList;
import java.util.Random;

import engine.controller.ports.WorldManager;
import engine.generators.AbstractIAGenerator;
import engine.world.ports.DefItem;
import engine.world.ports.DefItemDTO;
import engine.world.ports.WorldDefinition;

public class AIBasicSpawner extends AbstractIAGenerator {

    // region Fields
    private final ArrayList<DefItem> enemieDefs;
    private final Random rnd = new Random();
    private final AIEnemyShooter enemyShooter;
    // endregion

    // *** CONSTRUCTORS ***

    public AIBasicSpawner(
            WorldManager worldEvolver, WorldDefinition worldDefinition,
            int maxCreationDelay) {

        super(worldEvolver, worldDefinition, maxCreationDelay);

        this.enemieDefs = this.worldDefinition.enemies;
        this.enemyShooter = new AIEnemyShooter(worldEvolver);
        this.enemyShooter.activate();
    }

    // *** PROTECTED (alphabetical order) ***

    @Override
    protected String getThreadName() {
        return "AIBasicSpawner";
    }

    @Override
    protected void onActivate() {
        // At this place you can initialize any resource
        // needed for your AI spawner
        // ... or do nothing.
    }

    @Override
    protected void onTick() {
        if (!this.enemieDefs.isEmpty()) {
            DefItem defItem1 = this.enemieDefs.get(this.rnd.nextInt(this.enemieDefs.size()));
            String newEnemyId = this.addEnemy(defItem1);
            
            // Registramos el enemigo en el hilo disparador
            this.enemyShooter.registerEnemy(newEnemyId); 
        }
    }

    // *** PRIVATE (alphabetic order) ***

    private void addDynamic(DefItem defItem) {
        // If defItem is a prototype, we need to convert it to a DTO
        // to resolve range-based properties ...
        DefItemDTO bodyDef = this.defItemToDTO(defItem);

        // At this place you can modify position,
        // speed, thrust, etc. as needed
        // or you can accept definition values
        // as they came form world definition.
        // ... or do nothing.

        // Injecting dynamic body into the game
        this.addDynamicIntoTheGame(bodyDef);
    }

    private String addEnemy(DefItem defItem) {
        double posX = this.rnd.nextDouble(0.0, this.worldDefinition.worldWidth);
                double posY = this.rnd.nextDouble(0.0, this.worldDefinition.worldHeight);

        // If defItem is a prototype, we need to convert it to a DTO
        // to resolve range-based properties ...
        DefItemDTO bodyDef = this.defItemToDTO(defItem);

        // At this place you can modify position,
        // speed, thrust, etc. as needed
        // or you can accept definition values
        // as they came form world definition.
        // ... or do nothing.
        bodyDef = new DefItemDTO(bodyDef.assetId, bodyDef.size, 0.0, posX, posY, bodyDef.density);

        // Injecting enemy into the game
        return this.addEnemyIntoTheGame(bodyDef, worldDefinition.weapons);
    }
}
