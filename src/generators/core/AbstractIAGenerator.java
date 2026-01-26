package generators.core;

import java.util.Random;

import _helpers.DoubleVector;
import controller.ports.EngineState;
import controller.ports.WorldEvolver;
import generators.ports.AIConfigDTO;
import world.ports.DefItem;
import world.ports.DefItemDTO;
import world.ports.DefItemPrototypeDTO;
import world.ports.WorldDefinition;

public abstract class AbstractIAGenerator implements Runnable {

    // region Fields
    protected final Random rnd = new Random();

    protected final WorldEvolver worldEvolver;
    protected final WorldDefinition worldDefinition;
    protected final AIConfigDTO AIConfig;

    private Thread thread;
    // endregion

    // *** CONSTRUCTORS ***

    protected AbstractIAGenerator(
            WorldEvolver worldEvolver,
            WorldDefinition worldDefinition,
            AIConfigDTO AIConfig) {

        if (worldEvolver == null) {
            throw new IllegalArgumentException("WorldEvolver cannot be null.");
        }
        if (worldDefinition == null) {
            throw new IllegalArgumentException("WorldDefinition cannot be null.");
        }
        if (AIConfig == null) {
            throw new IllegalArgumentException("IAConfigDTO cannot be null.");
        }

        this.worldEvolver = worldEvolver;
        this.worldDefinition = worldDefinition;
        this.AIConfig = AIConfig;
    }

    // *** PUBLIC ***

    public void activate() {
        this.thread = new Thread(this);
        this.thread.setName(this.getThreadName());
        this.thread.setPriority(Thread.MIN_PRIORITY);
        this.thread.start();

        this.onActivate();

        System.out.println(this.getThreadName() + " activated!");
    }

    // *** PROTECTED (alphabetical order) ***

    protected String getThreadName() {
        return this.getClass().getSimpleName();
    }

    // Optional hook for subclasses (e.g., create players).
    protected void onActivate() {
        // no-op by default
    }

    // Override to implement logic for ALIVE tick
    protected abstract void tickAlive();

    protected final DoubleVector centerPosition() {
        double x = this.worldEvolver.getWorldDimension().width / 2.0;
        double y = this.worldEvolver.getWorldDimension().height / 2.0;
        return new DoubleVector(x, y);
    }

    // Converts a DefItem (prototype or DTO) into a concrete DefItemDTO
    protected final DefItemDTO toDTO(DefItem defItem) {
        if (defItem == null) {
            throw new IllegalArgumentException("DefItem cannot be null.");
        }

        if (defItem instanceof DefItemDTO defItemDTO) {
            return defItemDTO;
        }

        if (defItem instanceof DefItemPrototypeDTO proto) {
            double size = this.randomDoubleBetween(proto.minSize, proto.maxSize);
            double angle = this.randomDoubleBetween(proto.minAngle, proto.maxAngle);

            // Position may be ignored by caller (some IA spawn fixes position explicitly),
            // but we still return a valid DTO.
            double x = this.randomDoubleBetween(proto.posMinX, proto.posMaxX);
            double y = this.randomDoubleBetween(proto.posMinY, proto.posMaxY);

            return new DefItemDTO(
                    proto.assetId, size, angle, x, y, proto.density);
        }

        throw new IllegalStateException(
                "Unsupported DefItem implementation: " + defItem.getClass().getName());
    }

    // region Random helpers (random***)
    protected DoubleVector randomAcceleration() {
        return new DoubleVector(
                this.rnd.nextGaussian(),
                this.rnd.nextGaussian(),
                this.rnd.nextFloat() * this.AIConfig.maxAccModule);
    }

    protected double randomAngularSpeed(double maxAngularSpeed) {
        return this.rnd.nextFloat() * maxAngularSpeed - maxAngularSpeed / 2;
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

    protected int randomSize() {
        return (int) (this.AIConfig.minSize
                + (this.rnd.nextFloat()
                        * (this.AIConfig.maxSize - this.AIConfig.minSize)));
    }

    protected DoubleVector randomSpeed() {
        return new DoubleVector(
                this.rnd.nextGaussian(),
                this.rnd.nextGaussian(),
                this.rnd.nextFloat() * this.AIConfig.maxSpeedModule);
    }

    // endregion

    // *** INTERFACE IMPLEMENTATION ***

    // region Runnable
    @Override
    public final void run() {
        while (this.worldEvolver.getEngineState() != EngineState.STOPPED) {

            if (this.worldEvolver.getEngineState() == EngineState.ALIVE) {
                this.tickAlive();
            }

            try {
                Thread.sleep(this.rnd.nextInt(this.AIConfig.maxCreationDelay));
            } catch (InterruptedException ex) {
                // ignore
            }
        }
    }
    // endregion

}
