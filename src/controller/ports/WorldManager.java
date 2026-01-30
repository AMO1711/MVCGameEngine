package controller.ports;

import assets.core.AssetCatalog;
import utils.helpers.DoubleVector;
import world.ports.DefEmitterDTO;
import world.ports.DefWeaponDTO;

public interface WorldManager {

        public void addDecorator(String assetId, double size, double posX, double posY, double angle);

        public void addDynamicBody(String assetId, double size, double posX, double posY,
                        double speedX, double speedY, double accX, double accY,
                        double angle, double angularSpeed, double angularAcc, double thrust);

        public String addPlayer(String assetId, double size, double posX, double posY,
                        double speedX, double speedY, double accX, double accY,
                        double angle, double angularSpeed, double angularAcc, double thrust);

        public void addStaticBody(String assetId, double size, double posX, double posY, double angle);

        public void equipWeapon(String playerId, DefWeaponDTO weaponDef, int shootingOffset);

        public void equipTrail(
                        String playerId, DefEmitterDTO bodyEmitterDef);

        public DoubleVector getWorldDimension();

        public EngineState getEngineState();

        public void setLocalPlayer(String playerId);

        public void loadAssets(AssetCatalog assets);

}
