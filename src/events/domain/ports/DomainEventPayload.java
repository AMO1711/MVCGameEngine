package events.domain.ports;

/**
 * Optional extra data attached to a DomainEvent.
 * Add new permitted payloads as you grow.
 */
public sealed interface DomainEventPayload permits
        EmitPayloadDTO {
}