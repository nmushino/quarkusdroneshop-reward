package io.quarkusdroneshop.qdca10.domain;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.Qdca10Result;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Instant;

@ApplicationScoped
public class Qdca10 {

    static final Logger logger = LoggerFactory.getLogger(Qdca10.class);
    @Inject
    Inventory inventory;
    private String madeBy;

    @PostConstruct
    void setHostName() {
        try {
            madeBy = InetAddress.getLocalHost().getHostName();
        } catch (IOException e) {
            logger.debug("unable to get hostname");
            madeBy = "unknown";
        }
    }

    public Qdca10Result make(final OrderIn orderIn) {

        logger.debug("making: {}" + orderIn.getItem());

        if (inventory.decrementItem(orderIn.getItem())) {

            sleepyTimeTime(orderIn.getItem());

            return new Qdca10Result(new OrderUp(
                    orderIn.getOrderId(),
                    orderIn.getLineItemId(),
                    orderIn.getItem(),
                    orderIn.getName(),
                    Instant.now(),
                    madeBy));
        } else {

            return new Qdca10Result(new EightySixEvent(orderIn.getItem()));
        }

    }

    private void sleepyTimeTime(final Item item) {
        int i = calculateDelay(item);
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int calculateDelay(final Item item) {
        int delay;
        switch (item) {
            case QDC_A101:
                delay = 5000;
                break;
            case QDC_A102:
                delay = 5000;
                break;
            case QDC_A103:
                delay = 7000;
                break;
            case QDC_A104_AC:
                delay = 7000;
                break;
            case QDC_A104_AT:
                delay = 10000;
                break;
            default:
                delay = 10000;
                break;
        }
        ;
        return delay;
    }


    public void restockItem(Item item) {
        inventory.restock(item);
    }
}
