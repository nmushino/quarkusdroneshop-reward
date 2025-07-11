package io.quarkusdroneshop.domain.valueobjects;

import java.math.BigDecimal;

public class RewardEvent {
    private final String customerName;
    private final String orderId;
    private final BigDecimal rewardAmount;

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