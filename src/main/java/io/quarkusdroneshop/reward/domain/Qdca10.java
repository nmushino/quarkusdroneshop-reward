package io.quarkusdroneshop.reward.domain;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.Instant;

@ApplicationScoped
public class Qdca10 implements OrderProcessingResult {

    static final Logger logger = LoggerFactory.getLogger(Qdca10.class);

    @Inject
    Inventory inventory;

    private OrderUp orderUp;
    private EightySixEvent eightySixEvent;
    private boolean isEightySixed;
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

    public Qdca10 make(final OrderIn orderIn) {
        logger.debug("making: {}", orderIn.getItem());

        if (inventory.decrementItem(orderIn.getItem())) {
            sleepyTimeTime(orderIn.getItem());

            orderUp = new OrderUp(
                orderIn.getOrderId(),
                orderIn.getLineItemId(),
                orderIn.getItem(),
                orderIn.getName(),
                Instant.now(),
                madeBy
            );

            // 🎁 5個以上で10%
            if (orderIn.getQuantity() >= 5) {
                BigDecimal rewardPoints = orderIn.getPrice()
                    .multiply(BigDecimal.valueOf(orderIn.getQuantity()))
                    .multiply(BigDecimal.valueOf(0.10));
                RewardEvent rewardEvent = new RewardEvent(orderIn.getName(), orderIn.getOrderId(), rewardPoints);
                orderUp.setRewardEvent(rewardEvent);
            }

            this.isEightySixed = false;
            return this;
        } else {
            this.orderUp = null;
            this.eightySixEvent = new EightySixEvent(orderIn.getItem());
            this.isEightySixed = true;
            return this;
        }
    }

    private void sleepyTimeTime(final Item item) {
        try {
            Thread.sleep(3000); // 適当な処理時間
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // OrderProcessingResult 実装
    public boolean isEightySixed() {
        return isEightySixed;
    }

    public OrderUp getOrderUp() {
        return orderUp;
    }

    public RewardEvent getRewardEvent() {
        return orderUp != null ? orderUp.getRewardEvent() : null;
    }

    public EightySixEvent getEightySixEvent() {
        return eightySixEvent;
    }

    public void restockItem(Item item) {
        inventory.restock(item);
    }
}