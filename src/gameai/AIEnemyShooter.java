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
                // Si el jugador no existe, playerPos será null de forma segura
                DoubleVector playerPos = (playerId != null) ? this.worldManager.getBodyPosition(playerId) : null;

                for (String enemyId : this.enemyIds) {
                    DoubleVector enemyPos = this.worldManager.getBodyPosition(enemyId);
                    
                    if (enemyPos == null) {
                        // Limpieza: El enemigo ha muerto, lo quitamos de la lista
                        this.enemyIds.remove(enemyId);
                        continue; // Pasamos al siguiente enemigo
                    }
                    
                    if (playerPos != null) {
                        // El jugador está vivo, apuntamos y disparamos
                        double dx = playerPos.x - enemyPos.x;
                        double dy = playerPos.y - enemyPos.y;
                        double angle = Math.toDegrees(Math.atan2(dy, dx));
                        
                        this.worldManager.playerRotate(enemyId, angle);
                        this.worldManager.playerFire(enemyId);
                    }
                }
            }
            
            try { 
                Thread.sleep(2000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}