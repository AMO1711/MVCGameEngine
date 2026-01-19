package model.bodies.ports;

import model.bodies.implementations.DynamicBody;
import model.bodies.implementations.PlayerBody;
import model.bodies.implementations.ProjectileBody;
import model.bodies.implementations.StaticBody;
import model.physics.implementations.BasicPhysicsEngine;
import model.physics.ports.PhysicsEngine;
import model.physics.ports.PhysicsValuesDTO;
import model.spatial.core.SpatialGrid;

public class BodyFactory {

    public static Body create(
            BodyEventProcessor bodyEventProcessor,
            SpatialGrid spatialGrid,
            PhysicsValuesDTO phyVals,
            BodyType bodyType,
            double maxLifeInSeconds,
            String shooterId) {

        Body body = null;
        PhysicsEngine phyEngine = null;

        switch (bodyType) {
            case DYNAMIC:
                phyEngine = new BasicPhysicsEngine(phyVals);
                body = new DynamicBody(
                        bodyEventProcessor, spatialGrid, phyEngine,
                        BodyType.DYNAMIC,
                        maxLifeInSeconds);
                break;

            case PLAYER:
                phyEngine = new BasicPhysicsEngine(phyVals);
                body = new PlayerBody(
                        bodyEventProcessor, spatialGrid, phyEngine,
                        maxLifeInSeconds);
                break;

            case PROJECTILE:
                phyEngine = new BasicPhysicsEngine(phyVals);
                body = new ProjectileBody(
                        bodyEventProcessor, spatialGrid, phyEngine,
                        maxLifeInSeconds,
                        shooterId);
                break;

            case DECORATOR:
            case GRAVITY:
                body = new StaticBody(
                        bodyEventProcessor, spatialGrid, bodyType,
                        phyVals.size, phyVals.posX, phyVals.posY, phyVals.angle,
                        maxLifeInSeconds);

                break;

            default:
                break;
        }

        return body;
    }

}
