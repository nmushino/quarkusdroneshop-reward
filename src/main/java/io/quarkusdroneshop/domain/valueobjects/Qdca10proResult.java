package io.quarkusdroneshop.domain.valueobjects;

import java.math.BigDecimal;
import java.time.Instant;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import io.quarkusdroneshop.domain.valueobjects.RewardEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Qdca10proResult {

    private static final Logger logger = LoggerFactory.getLogger(Qdca10proResult.class);

    private OrderUp orderUp;
    private boolean isEightySixed;

    public static Qdca10proResult make(OrderIn orderIn, String madeBy) {

        logger.debug("making PRO: {}", orderIn.getItem());

            OrderUp orderUp = new OrderUp(
                    orderIn.getOrderId(),
                    orderIn.getLineItemId(),
                    orderIn.getItem(),
                    orderIn.getName(),
                    Instant.now(),
                    madeBy
            );

            // // 🎁 Pro条件：5個以上 → 15%還元
            // if (orderIn.getQuantity() >= 5) {
            //     BigDecimal rewardPoints = orderIn.getPrice()
            //             .multiply(BigDecimal.valueOf(orderIn.getQuantity()))
            //             .multiply(BigDecimal.valueOf(0.15)); // ← Proでは15%
            //     RewardEvent rewardEvent = new RewardEvent(
            //             orderIn.getName(),
            //             orderIn.getItem().toString(),
            //             rewardPoints
            //     );
            //     orderUp.setRewardEvent(rewardEvent);
            // }

            return new Qdca10proResult(orderUp);
    }

    // コンストラクタ（成功時）
    public Qdca10proResult(OrderUp orderUp) {
        this.orderUp = orderUp;
        this.isEightySixed = false;
    }

    public boolean isEightySixed() {
        return isEightySixed;
    }

    public OrderUp getOrderUp() {
        return orderUp;
    }
}