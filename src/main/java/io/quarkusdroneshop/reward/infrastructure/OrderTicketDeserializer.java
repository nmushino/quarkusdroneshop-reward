package io.quarkusdroneshop.reward.infrastructure;

import io.quarkusdroneshop.reward.domain.OrderBatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import java.io.IOException;

public class OrderTicketDeserializer implements Deserializer<OrderBatch> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public OrderBatch deserialize(String topic, byte[] data) {
        try {
            return mapper.readValue(data, OrderBatch.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize OrderBatch", e);
        }
    }
}