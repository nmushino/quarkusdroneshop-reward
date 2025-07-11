package io.quarkusdroneshop.domain.valueobjects;

public interface OrderProcessingResult {
    boolean isEightySixed();
    OrderUp getOrderUp();
    RewardEvent getRewardEvent();
}