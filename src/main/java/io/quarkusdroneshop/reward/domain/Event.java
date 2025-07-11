package io.quarkusdroneshop.qdca10.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Marker interface for events
 */
@RegisterForReflection
public interface Event {

    EventType getEventType();
}
