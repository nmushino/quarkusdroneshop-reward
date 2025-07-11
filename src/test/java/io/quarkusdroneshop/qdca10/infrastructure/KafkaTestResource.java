package io.quarkusdroneshop.qdca10.infrastructure;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import org.testcontainers.containers.KafkaContainer;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

public class KafkaTestResource implements QuarkusTestResourceLifecycleManager {

    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        Map<String, String> props1 = InMemoryConnector.switchIncomingChannelsToInMemory("orders-in");
        Map<String, String> props2 = InMemoryConnector.switchOutgoingChannelsToInMemory("orders-up");
        env.putAll(props1);
        env.putAll(props2);
        return env;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }

}