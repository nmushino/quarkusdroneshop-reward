package io.quarkusdroneshop.domain.valueobjects;

import java.math.BigDecimal;
import java.time.Instant;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import io.quarkusdroneshop.domain.valueobjects.RewardEvent;
import io.quarkusdroneshop.reward.domain.EightySixEvent;
import io.quarkusdroneshop.reward.domain.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Qdca10proResult {

    private static final Logger logger = LoggerFactory.getLogger(Qdca10proResult.class);

    private OrderUp orderUp;
    private EightySixEvent eightySixEvent;
    private boolean isEightySixed;

    public static Qdca10proResult make(OrderIn orderIn, Inventory inventory, String madeBy) {

        logger.debug("making PRO: {}", orderIn.getItem());

        if (inventory.decrementItem(orderIn.getItem())) {

            sleepyTimeTime(orderIn.getItem());

            OrderUp orderUp = new OrderUp(
                    orderIn.getOrderId(),
                    orderIn.getLineItemId(),
                    orderIn.getItem(),
                    orderIn.getName(),
                    Instant.now(),
                    madeBy
            );

            // 🎁 Pro条件：5個以上 → 15%還元
            if (orderIn.getQuantity() >= 5) {
                BigDecimal rewardPoints = orderIn.getPrice()
                        .multiply(BigDecimal.valueOf(orderIn.getQuantity()))
                        .multiply(BigDecimal.valueOf(0.15)); // ← Proでは15%
                RewardEvent rewardEvent = new RewardEvent(
                        orderIn.getName(),
                        orderIn.getItem().toString(),
                        rewardPoints
                );
                orderUp.setRewardEvent(rewardEvent);
            }

            return new Qdca10proResult(orderUp);

        } else {
            return new Qdca10proResult(new EightySixEvent(orderIn.getItem()));
        }
    }

    // コンストラクタ（成功時）
    public Qdca10proResult(OrderUp orderUp) {
        this.orderUp = orderUp;
        this.eightySixEvent = null;
        this.isEightySixed = false;
    }

    // コンストラクタ（品切れ時）
    public Qdca10proResult(EightySixEvent eightySixEvent) {
        this.orderUp = null;
        this.eightySixEvent = eightySixEvent;
        this.isEightySixed = true;
    }

    // 内部スリープ処理（遅延シミュレーション）
    private static void sleepyTimeTime(Item item) {
        int delayMillis;
        switch (item) {
            case QDC_A105_Pro01:
            case QDC_A105_Pro02:
                delayMillis = 5000;
                break;
            case QDC_A105_Pro03:
            case QDC_A105_Pro04:
                delayMillis = 8000;
                break;
            default:
                delayMillis = 10000;
        }

        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isEightySixed() {
        return isEightySixed;
    }

    public OrderUp getOrderUp() {
        return orderUp;
    }

    public EightySixEvent getEightySixEvent() {
        return eightySixEvent;
    }
}