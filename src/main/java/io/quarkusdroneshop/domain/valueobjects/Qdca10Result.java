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

public class Qdca10Result {

    private OrderUp orderUp;
    private EightySixEvent eightySixEvent;
    private boolean isEightySixed;

    private static final Logger logger = LoggerFactory.getLogger(Qdca10proResult.class);

    public static Qdca10Result make(OrderIn orderIn, Inventory inventory, String madeBy) {

        logger.debug("making: {}" + orderIn.getItem());

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

            // 🎁 リワード条件に該当する場合にポイント付与
            if (orderIn.getQuantity() >= 5) {
                BigDecimal rewardPoints = orderIn.getPrice()
                                                .multiply(BigDecimal.valueOf(orderIn.getQuantity()))
                                                .multiply(BigDecimal.valueOf(0.10)); // 10%
                RewardEvent rewardEvent = new RewardEvent(
                    orderIn.getName(),
                    orderIn.getItem().toString(),
                    rewardPoints
                );
                orderUp.setRewardEvent(rewardEvent); // OrderUp に追加（次ステップで拡張）
            }

            return new Qdca10Result(orderUp);

        } else {
            return new Qdca10Result(new EightySixEvent(orderIn.getItem()));
        }
    }

    // コンストラクタ（成功時）
    public Qdca10Result(OrderUp orderUp) {
        this.orderUp = orderUp;
        this.eightySixEvent = null;
        this.isEightySixed = false;
    }

    // コンストラクタ（品切れ時）
    public Qdca10Result(EightySixEvent eightySixEvent) {
        this.orderUp = null;
        this.eightySixEvent = eightySixEvent;
        this.isEightySixed = true;
    }

    // 内部スリープ処理（遅延シミュレーション）
    private static void sleepyTimeTime(Item item) {
        int delayMillis;
        switch (item) {
            case QDC_A101:
            case QDC_A102:
                delayMillis = 5000;
                break;
            case QDC_A103:
            case QDC_A104_AC:
                delayMillis = 7000;
                break;
            case QDC_A104_AT:
                delayMillis = 10000;
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

    public EightySixEvent getEightySixEvent() {
        return eightySixEvent;
    }

    public void setEightySixEvent(EightySixEvent eightySixEvent) {
        this.eightySixEvent = eightySixEvent;
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
