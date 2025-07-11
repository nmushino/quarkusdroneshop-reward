package io.quarkusdroneshop.qdca10;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;

import java.util.UUID;

public class TestUtil {

    public static OrderIn getOrderTicket() {

        return new OrderIn(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                Item.QDC_A101,
                "Lemmy"
        );
    }

}
