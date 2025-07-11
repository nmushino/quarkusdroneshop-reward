package io.quarkusdroneshop.qdca10.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public enum EventType {
    QDCA10_ORDER_IN, QDCA10_ORDER_UP, EIGHTY_SIX, QDCA10Pro_ORDER_IN, QDCA10Pro_ORDER_UP, ORDER_PLACED, RESTOCK, NEW_ORDER
}
