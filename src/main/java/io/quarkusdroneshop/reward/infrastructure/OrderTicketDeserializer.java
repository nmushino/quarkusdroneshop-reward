package io.quarkusdroneshop.qdca10.infrastructure;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;

public class OrderTicketDeserializer extends ObjectMapperDeserializer<OrderIn> {

    public OrderTicketDeserializer() {
        super(OrderIn.class);
    }
}
