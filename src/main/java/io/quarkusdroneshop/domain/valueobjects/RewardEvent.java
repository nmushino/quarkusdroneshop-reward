package io.quarkusdroneshop.domain.valueobjects;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.math.BigDecimal;

@RegisterForReflection
public class RewardEvent {
    private String customerName;
    private String orderId;
    private BigDecimal rewardAmount;

    public RewardEvent() {}

    // 通常のコンストラクタ
    public RewardEvent(String customerName, String orderId, BigDecimal rewardAmount) {
        this.customerName = customerName;
        this.orderId = orderId;
        this.rewardAmount = rewardAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }

    @Override
    public String toString() {
        return "RewardEvent{" +
                "customerName='" + customerName + '\'' +
                ", orderId='" + orderId + '\'' +
                ", rewardAmount=" + rewardAmount +
                '}';
    }
}