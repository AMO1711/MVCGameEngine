package model.ports;

import model.bodies.ports.BodyType;

public class ActionDTO {
    final public String entityId;
    final public BodyType bodyType;
    final public ActionType type;
    final public ActionExecutor executor;
    final public ActionPriority priority;

    public ActionDTO(String entityId, BodyType bodyType, ActionType type, ActionExecutor executor, ActionPriority priority) {
        this.entityId = entityId;
        this.bodyType = bodyType;
        this.type = type;
        this.executor = executor;
        this.priority = priority;
    }
}