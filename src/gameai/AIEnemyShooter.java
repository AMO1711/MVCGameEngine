package gameai;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import engine.controller.ports.EngineState;
import engine.controller.ports.WorldManager;
import engine.utils.helpers.DoubleVector;

public class AIEnemyShooter implements Runnable {

    private final WorldManager worldManager;
    // CopyOnWriteArrayList evita errores si añadimos enemigos mientras el bucle los lee
    private final List<String> enemyIds = new CopyOnWriteArrayList<>();

    public AIEnemyShooter(WorldManager worldManager) {
        this.worldManager = worldManager;
    }

    public void registerEnemy(String enemyId) {
        if (enemyId != null) {
            this.enemyIds.add(enemyId);
        }
    }

    public void activate() {
        Thread thread = new Thread(this, "AIEnemyShooter");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    @Override
    public void run() {
        while (this.worldManager.getEngineState() != EngineState.STOPPED) {
            if (this.worldManager.getEngineState() == EngineState.ALIVE) {
                String playerId = this.worldManager.getLocalPlayerId();
                DoubleVector playerPos = this.worldManager.getBodyPosition(playerId);

                for (String enemyId : this.enemyIds) {
                    DoubleVector enemyPos = this.worldManager.getBodyPosition(enemyId);

                    // Si getBodyPosition nos da null, el enemigo ha muerto o no es válido
                    if (playerPos != null && enemyPos != null) {
                        double dx = playerPos.x - enemyPos.x;
                        double dy = playerPos.y - enemyPos.y;
                        double angle = Math.toDegrees(Math.atan2(dy, dx));
                        
                        // this.worldManager.playerRotate(enemyId, angle);
                        this.worldManager.playerFire(enemyId);
                    } else {
                        // Limpieza: si getBodyPosition es null, el ID ya no sirve
                        this.enemyIds.remove(enemyId);
                    }
                }
            }
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
        }
    }
}