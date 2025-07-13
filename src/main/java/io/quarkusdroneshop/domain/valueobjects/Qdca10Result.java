package io.quarkusdroneshop.domain.valueobjects;

import java.math.BigDecimal;
import java.time.Instant;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import io.quarkusdroneshop.domain.valueobjects.RewardEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Qdca10Result {

    private OrderUp orderUp;
    private boolean isEightySixed;

    private static final Logger logger = LoggerFactory.getLogger(Qdca10proResult.class);

    public static Qdca10Result make(OrderIn orderIn, String madeBy) {

        logger.debug("making: {}" + orderIn.getItem());

            OrderUp orderUp = new OrderUp(
                    orderIn.getOrderId(),
                    orderIn.getLineItemId(),
                    orderIn.getItem(),
                    orderIn.getName(),
                    Instant.now(),
                    madeBy
            );

            // // 🎁 リワード条件に該当する場合にポイント付与
            // if (orderIn.getQuantity() >= 5) {
            //     BigDecimal rewardPoints = orderIn.getPrice()
            //                                     .multiply(BigDecimal.valueOf(orderIn.getQuantity()))
            //                                     .multiply(BigDecimal.valueOf(0.10)); // 10%
            //     RewardEvent rewardEvent = new RewardEvent(
            //         orderIn.getName(),
            //         orderIn.getItem().toString(),
            //         rewardPoints
            //     );
            //     orderUp.setRewardEvent(rewardEvent); // OrderUp に追加（次ステップで拡張）
            // }

            return new Qdca10Result(orderUp);
    }

    // コンストラクタ（成功時）
    public Qdca10Result(OrderUp orderUp) {
        this.orderUp = orderUp;
        this.isEightySixed = false;
    }

    public OrderUp getOrderUp() {
        return orderUp;
    }

    public void setOrderUp(OrderUp orderUp) {
        this.orderUp = orderUp;
    }

    public boolean isEightySixed() {
        return isEightySixed;
    }

    public void setEightySixed(boolean eightySixed) {
        isEightySixed = eightySixed;
    }
}
