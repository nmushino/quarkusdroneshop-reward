package io.quarkusdroneshop.reward.infrastructure;

import io.quarkusdroneshop.reward.domain.OrderBatch;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import java.io.IOException;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class OrderTicketDeserializer implements Deserializer<OrderBatch> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public OrderBatch deserialize(String topic, byte[] data) {
        try {
            return mapper.readValue(data, OrderBatch.class);
            //return mapper.readValue(data, OrderIn.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize OrderBatch", e);
        }
    }
}