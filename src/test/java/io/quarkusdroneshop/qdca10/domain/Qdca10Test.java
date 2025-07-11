package io.quarkusdroneshop.qdca10.domain;

import io.quarkusdroneshop.qdca10.TestUtil;
import io.quarkusdroneshop.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkusdroneshop.domain.valueobjects.Qdca10Result;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class Qdca10Test {

    @Inject
    Qdca10 QDCA10;

    Jsonb jsonb = JsonbBuilder.create();

    @BeforeEach
    public void restock() {
        QDCA10.restockItem(Item.QDC_A101);
    }

    @Test
    public void testBlackCoffeeOrder() throws ExecutionException, InterruptedException {

        OrderIn orderIn = TestUtil.getOrderTicket();

        Qdca10Result QDCA10Result = QDCA10.make(orderIn);

        OrderUp orderUp = QDCA10Result.getOrderUp();

        await().atLeast(Duration.ofSeconds(5000));

        assertNotNull(orderUp);
        assertEquals(orderUp.orderId, orderIn.getOrderId());
        assertEquals(orderUp.lineItemId, orderIn.getLineItemId());
        assertEquals(orderUp.item, orderIn.getItem());
        assertEquals(orderUp.name, orderIn.getName());

    }


}
