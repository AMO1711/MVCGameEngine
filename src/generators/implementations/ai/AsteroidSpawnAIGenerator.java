package generators.implementations.ai;

import java.util.ArrayList;

import _helpers.DoubleVector;
import controller.ports.WorldEvolver;
import generators.core.AbstractIAGenerator;
import generators.ports.AIConfigDTO;
import world.ports.DefItem;
import world.ports.DefItemDTO;
import world.ports.WorldDefinition;

public class AsteroidSpawnAIGenerator extends AbstractIAGenerator {

    // region Fields
    private final ArrayList<DefItem> asteroidDefs;
    // endregion

    // *** CONSTRUCTORS ***

    public AsteroidSpawnAIGenerator(
            WorldEvolver worldEvolver,
            WorldDefinition worldDefinition,
            AIConfigDTO lifeConfig) {

        super(worldEvolver, worldDefinition, lifeConfig);

        this.asteroidDefs = this.worldDefinition.asteroids;
    }

    // *** PROTECTED (alphabetical order) ***

    @Override
    protected String getThreadName() {
        return "Asteroid IA generator";
    }

    @Override
    protected void onActivate() {
        this.createPlayers();
    }

    @Override
    protected void tickAlive() {
        this.addRandomDynamicBody();
    }

    // *** PRIVATE (alphabetic order) ***

    private void addRandomDynamicBody() {

        DoubleVector speed = this.AIConfig.fixedSpeed
                ? new DoubleVector(this.AIConfig.speedX, this.AIConfig.speedY)
                : this.randomSpeed();

        DoubleVector acc = this.AIConfig.fixedAcc
                ? new DoubleVector(this.AIConfig.accX, this.AIConfig.accY)
                : this.randomAcceleration();

        DoubleVector pos = this.centerPosition(); // coordinated: center spawn

        this.worldEvolver.addDynamicBody(
                this.randomAssetId(),                this.randomSize(),
                pos.x, pos.y,
                speed.x, speed.y,
                acc.x, acc.y,
                0d,
                this.randomAngularSpeed(460d),
                0d,
                0d);
    }

    private void createPlayers() {
        ArrayList<DefItem> shipDefs = this.worldDefinition.spaceships;
        String playerId = null;

        for (DefItem def : shipDefs) {
            DefItemDTO body = this.toDTO(def);

            playerId = this.worldEvolver.addPlayer(
                    body.assetId,
                    body.size,
                    500, 200,
                    0, 0,
                    0, 0,
                    0,
                    this.randomAngularSpeed(270),
                    0, 0);


                System.out.println("Created player with ID: " + playerId);

            this.worldEvolver.addWeaponToPlayer(
                    playerId, this.worldDefinition.bulletWeapons.get(0), 0);
            this.worldEvolver.addWeaponToPlayer(
                    playerId, this.worldDefinition.burstWeapons.get(0), 0);
            this.worldEvolver.addWeaponToPlayer(
                    playerId, this.worldDefinition.missileLaunchers.get(0), -15);
            this.worldEvolver.addWeaponToPlayer(
                    playerId, this.worldDefinition.mineLaunchers.get(0), 15);

            this.worldEvolver.bodyEquipTrail(
                    playerId, this.worldDefinition.trailEmitters.get(0));
        }

        if (playerId == null) {
            System.out.println("AsteroidSpawnIAGenerator: No player created!");
            return;
        }

        this.worldEvolver.setLocalPlayer(playerId);
    }

    private String randomAssetId() {
        if (this.asteroidDefs == null || this.asteroidDefs.isEmpty()) {
            throw new IllegalStateException(
                    "AsteroidSpawnIAGenerator: worldDefinition.asteroids is empty.");
        }

        int index = this.rnd.nextInt(this.asteroidDefs.size());
        return this.toDTO(this.asteroidDefs.get(index)).assetId;
    }

}
