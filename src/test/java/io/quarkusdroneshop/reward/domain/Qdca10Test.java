package io.quarkusdroneshop.reward.domain;

import io.quarkusdroneshop.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkusdroneshop.domain.valueobjects.Qdca10Result;
import io.quarkusdroneshop.reward.TestUtil;
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
    Purchase Qdca10;

    private String madeBy = "default";

    Jsonb jsonb = JsonbBuilder.create();

    @Test
    public void testQdca10Order() throws ExecutionException, InterruptedException {
    
        OrderIn orderIn = TestUtil.getOrderTicket();
    
        Qdca10Result qdca10Result = Qdca10Result.make(orderIn, madeBy);
    
        OrderUp orderUp = qdca10Result.getOrderUp();
    
        await().atLeast(Duration.ofSeconds(5));  // 秒数の単位修正（5000ミリ秒 → 5秒）
    
        assertNotNull(orderUp);
        assertEquals(orderUp.orderId, orderIn.getOrderId());
        assertEquals(orderUp.lineItemId, orderIn.getLineItemId());
        assertEquals(orderUp.item, orderIn.getItem());
        assertEquals(orderUp.name, orderIn.getName());
    }


}
