package engine.events.domain.ports.payloads;

public final class CollisionPayload implements DomainEventPayload {
    public final boolean haveImmunity;
    public final boolean isAttacking;

    public CollisionPayload(boolean playerHaveImmunity, boolean isAttacking) {
        this.haveImmunity = playerHaveImmunity;
        this.isAttacking = isAttacking;
    }
}
    