package engine.model.bodies.impl;

import java.util.List;

import engine.events.domain.ports.BodyToEmitDTO;
import engine.model.bodies.core.AbstractBody;
import engine.model.bodies.ports.BodyEventProcessor;
import engine.model.bodies.ports.BodyState;
import engine.model.bodies.ports.BodyType;
import engine.model.emitter.impl.BasicEmitter;
import engine.model.physics.ports.PhysicsEngine;
import engine.model.physics.ports.PhysicsValuesDTO;
import engine.utils.profiling.impl.BodyProfiler;
import engine.utils.spatial.core.SpatialGrid;

/**
 * Dynamic body with its own physics engine.
 * Processed by MultiBodyRunner instances from the shared thread pool.
 */
public class DynamicBody extends AbstractBody {

    // region Fields
    private double maxThrustForce; //
    private double maxAngularAcc; // degrees*s^-2
    private double angularSpeed; // degrees*s^-1
    private BodyProfiler profiler;
    private String trailId;
    private int spatialCellRadius = -1;
    private double spatialCellSize = -1.0d;

    private final List<String> weaponIds = new java.util.ArrayList<>(4);
    private int currentWeaponIndex = -1; // -1 = sin arma
    // endregion


    // region Constructors
    public DynamicBody(BodyEventProcessor bodyEventProcessor, SpatialGrid spatialGrid,
            PhysicsEngine phyEngine, BodyType bodyType, double maxLifeInSeconds, String emitterId, 
            BodyProfiler profiler) {

        super(bodyEventProcessor, spatialGrid,
                phyEngine,
                bodyType,
                maxLifeInSeconds, 
                emitterId);
        this.profiler = profiler;
    }
    // endregion

    // *** PUBLICS ***

    @Override // AbstractBody
    public synchronized void activate() {
        super.activate();

        this.setState(BodyState.ALIVE);
        // Threading is now handled by Model/BodyBatchManager
    }

    public void addWeapon(String emitterId) {
        this.weaponIds.add(emitterId);

        if (this.currentWeaponIndex < 0) {
            // Signaling existence of weapon in the spaceship
            this.currentWeaponIndex = 0;
        }
    }

    public void registerFireRequest() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weaponIds.size()) {
            System.out.println("> No weapon active or no weapons!");
            return;
        }

        BasicEmitter emitter = this.getEmitter(this.weaponIds.get(this.currentWeaponIndex));
        if (emitter == null) {
            // There is no weapon in this slot
            return;
        }

        emitter.registerRequest();
    }

    public boolean mustFireNow(PhysicsValuesDTO newPhyValues) {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weaponIds.size()) {
            return false;
        }

        BasicEmitter emitter = this.getEmitter(this.weaponIds.get(this.currentWeaponIndex));
        if (emitter == null) {
            return false;
        }

        double dtNanos = newPhyValues.timeStamp - this.getPhysicsValues().timeStamp;
        double dtSeconds = ((double) dtNanos) / 1_000_000_0000.0d;

        return emitter.mustEmitNow(dtSeconds);
    }

    public BodyToEmitDTO getProjectileConfig() {
        if (this.currentWeaponIndex < 0 || this.currentWeaponIndex >= this.weaponIds.size()) {
            return null;
        }

        BasicEmitter emitter = this.getEmitter(this.weaponIds.get(this.currentWeaponIndex));
        if (emitter == null) {
            return null;
        }

        return emitter.getBodyToEmitConfig();
    }

    // region Acceleration control (acceleration***)
    public void accelerationAngularInc(double angularAcceleration) {
        this.getPhysicsEngine().angularAccelerationInc(angularAcceleration);
    }

    public void accelerationReset() {
        this.getPhysicsEngine().resetAcceleration();
    }
    // endregion

    // region Trail management (trail***)
    public String trailEquip(BasicEmitter trailEmitter) {
        this.trailId = this.emitterEquip(trailEmitter);

        return this.trailId;
    }

    public String trailGetId() {
        return this.trailId;
    }

    // endregion

    // region Getters (get***)
    public double getAngularSpeed() {
        return this.angularSpeed;
    }

    public double getMaxThrustForce() {
        return this.maxThrustForce;
    }

    public double getMaxAngularAcceleration() {
        return this.maxAngularAcc;
    }

    public List<String> getWeaponIds() {
        return this.weaponIds;
    }
    // endregion

    // region setters (set***)
    public void setAngularAcceleration(double angularAcc) {
        this.getPhysicsEngine().setAngularAcceleration(angularAcc);
    }

    public void setAngularSpeed(double angularSpeed) {
        this.getPhysicsEngine().setAngularSpeed(angularSpeed);
    }

    public void setMaxThrustForce(double maxThrust) {
        this.maxThrustForce = maxThrust;
    }

    public void setMaxAngularAcceleration(double maxAngularAcc) {
        this.setAngularSpeed(this.angularSpeed);
        this.maxAngularAcc = maxAngularAcc;
    }
    // endregion

    // region Thrust control (thrust***)
    public void thrustMaxOn() {
        this.thurstNow(this.maxThrustForce);
    }

    public void thurstNow(double thrust) {
        this.getPhysicsEngine().setThrust(thrust);
    }

    public void thrustOff() {
        this.getPhysicsEngine().stopPushing();
    }
    // endregion

    // *** INTERFACE IMPLEMENTATIONS ***

    // region AbstractBody
    @Override
    public void onTick() {
        // Physics calculation (already profiled in BasicPhysicsEngine)
        PhysicsValuesDTO newPhyValues = this.getPhysicsEngine().calcNewPhysicsValues();

        // Spatial grid update
        long spatialStart = this.profiler.startInterval();
        double r = newPhyValues.size * 0.5;
        this.getSpatialGrid().upsert(
                this.getBodyId(), 
                newPhyValues.posX - r, newPhyValues.posX + r,
                newPhyValues.posY - r, newPhyValues.posY + r,
                this.getScratchIdxs());
        this.profiler.stopInterval("SPATIAL_GRID", spatialStart);

        // Trail emitter
        if (this.isThrusting() && this.trailId != null) {
            long emitterStart = this.profiler.startInterval();
            this.emitterRequest(this.trailId);
            this.profiler.stopInterval("EMITTERS", emitterStart);
        }

        // Event processing (already profiled in Model.processBodyEvents)
        this.processBodyEvents(this, newPhyValues, this.getPhysicsEngine().getPhysicsValues());
    }
    // endregion

}
