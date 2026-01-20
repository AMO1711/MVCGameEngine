package events.domain.implementations;

import events.domain.core.AbstractDomainEventDTO;
import events.domain.ports.BodyRefDTO;
import events.domain.ports.DomainEventType;

public final class CollisionEventDTO extends AbstractDomainEventDTO {

    public CollisionEventDTO(
            BodyRefDTO primaryBody,
            BodyRefDTO secondaryBody) {

        super(DomainEventType.COLLISION, primaryBody, secondaryBody, null);

        if (secondaryBody == null)
            throw new IllegalArgumentException("CollisionEvent requires secondaryBody");
    }
}