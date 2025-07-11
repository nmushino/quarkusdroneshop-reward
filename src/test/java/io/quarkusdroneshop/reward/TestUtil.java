package io.quarkusdroneshop.reward;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;

import java.math.BigDecimal;
import java.util.UUID;

public class TestUtil {

    public static OrderIn getOrderTicket() {

        return new OrderIn(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Item.QDC_A101,
                "Lemmy",
                new BigDecimal("135.50"),
                "WEB"
        );
    }

}
