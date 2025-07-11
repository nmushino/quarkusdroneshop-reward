package io.quarkusdroneshop.qdca10.infrastructure;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.quarkusdroneshop.qdca10.TestUtil;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySink;
import io.smallrye.reactive.messaging.connectors.InMemorySource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.inject.Any;
import javax.inject.Inject;

import java.time.Duration;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
@QuarkusTestResource(KafkaTestResource.class)
public class KafkaServiceTest {

    @InjectSpy
    KafkaService kafkaService;

    @Inject
    @Any
    InMemoryConnector ordersInConnector;

    InMemorySource<OrderIn> ordersIn;

    @BeforeEach
    public void setUp(){

        ordersIn = ordersInConnector.source("orders-in");
    }

    @Test
    public void testSingleBlackCoffee() {

        OrderIn orderIn = TestUtil.getOrderTicket();
        ordersIn.send(orderIn);
        verify(kafkaService, times(1)).onOrderIn(any(OrderIn.class));
    }
}
