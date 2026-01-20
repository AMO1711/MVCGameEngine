package events.domain.core;

import events.domain.ports.BodyRefDTO;
import events.domain.ports.DomainEventType;
import events.domain.ports.DomainEventPayload;

public abstract class AbstractDomainEventDTO {

    public final DomainEventType type;
    public final BodyRefDTO primaryBody;
    public final BodyRefDTO secondaryBody; // nullable
    public final DomainEventPayload payload; // nullable

    public AbstractDomainEventDTO(
            DomainEventType type,
            BodyRefDTO primaryBody,
            BodyRefDTO secondaryBody,
            DomainEventPayload payload) {

        if (type == null)
            throw new IllegalArgumentException("Event: type is required!");
        if (primaryBody == null)
            throw new IllegalArgumentException("Event: primaryBody is required!");

        this.type = type;
        this.primaryBody = primaryBody;
        this.secondaryBody = secondaryBody;
        this.payload = payload;
    }

    protected AbstractDomainEventDTO(
            DomainEventType type,
            BodyRefDTO primaryBody,
            DomainEventPayload payload) {

        this(type, primaryBody, null, payload);
    }

}
