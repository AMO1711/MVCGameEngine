package events.domain.implementations;

import events.domain.core.AbstractDomainEventDTO;
import events.domain.ports.BodyRefDTO;
import events.domain.ports.DomainEventPayload;
import events.domain.ports.DomainEventType;

public class EmitEventDTO extends AbstractDomainEventDTO {

    public EmitEventDTO(
            BodyRefDTO primaryBody,
            DomainEventPayload payload) {

        super(DomainEventType.EMIT_REQUESTED, primaryBody, null,  payload);
    }
}
