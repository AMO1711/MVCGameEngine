package generators.implementations.ai;

import java.util.ArrayList;

import _helpers.DoubleVector;
import controller.ports.WorldEvolver;
import generators.core.AbstractIAGenerator;
import generators.ports.AIConfigDTO;
import world.ports.DefItem;
import world.ports.DefItemDTO;
import world.ports.WorldDefinition;

public class AsteroidSpawnFromCenterAIGenerator extends AbstractIAGenerator {

    // region Fields
    private final ArrayList<DefItem> asteroidDefs;
    // endregion

    // *** CONSTRUCTORS ***

    public AsteroidSpawnFromCenterAIGenerator(
            WorldEvolver worldEvolver,
            WorldDefinition worldDefinition,
            AIConfigDTO AIConfig) {

        super(worldEvolver, worldDefinition, AIConfig);

        this.asteroidDefs = this.worldDefinition.asteroids;
    }

    // *** PROTECTED (alphabetical order) ***

    @Override
    protected String getThreadName() {
        return "Asteroid IA generator (from center)";
    }

    @Override
    protected void tickAlive() {
        this.addAsteroidFromCenter();
    }

    // *** PRIVATE (alphabetic order) ***

    private void addAsteroidFromCenter() {

        DefItemDTO asteroid = this.randomAsteroid();

        DoubleVector pos = this.centerPosition();
        DoubleVector speed = this.radialSpeedFromCenter();

        DoubleVector acc = this.AIConfig.fixedAcc
                ? new DoubleVector(this.AIConfig.accX, this.AIConfig.accY)
                : this.randomAcceleration();

        this.worldEvolver.addDynamicBody(
                asteroid.assetId, asteroid.size,
                pos.x, pos.y, speed.x, speed.y,
                acc.x, acc.y,
                asteroid.angle,
                this.randomAngularSpeed(360d),
                0d, 0d);
    }

    private DefItemDTO randomAsteroid() {
        if (this.asteroidDefs == null || this.asteroidDefs.isEmpty()) {
            throw new IllegalStateException(
                    "AsteroidSpawnFromCenterIAGenerator: worldDefinition.asteroids is empty.");
        }

        int index = this.rnd.nextInt(this.asteroidDefs.size());
        return this.toDTO(this.asteroidDefs.get(index));
    }

    private DoubleVector radialSpeedFromCenter() {
        double angle = this.rnd.nextDouble() * Math.PI * 2.0;

        double module = this.AIConfig.fixedSpeed
                ? Math.sqrt(this.AIConfig.speedX * this.AIConfig.speedX
                        + this.AIConfig.speedY * this.AIConfig.speedY)
                : this.AIConfig.maxSpeedModule * this.rnd.nextDouble();

        return new DoubleVector(
                Math.cos(angle),
                Math.sin(angle),
                module);
    }
}
