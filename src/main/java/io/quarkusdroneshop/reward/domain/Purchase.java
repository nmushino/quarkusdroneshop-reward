package io.quarkusdroneshop.reward.domain;

import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.*;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class Purchase implements OrderProcessingResult {

    static final Logger logger = LoggerFactory.getLogger(Purchase.class);

    private OrderUp orderUp;
    private boolean isEightySixed;
    private String madeBy;

    @Inject
    @Channel("rewards")
    Emitter<RewardEvent> rewardEmitter;

    @PostConstruct
    void setHostName() {
        try {
            madeBy = InetAddress.getLocalHost().getHostName();
        } catch (IOException e) {
            logger.debug("unable to get hostname");
            madeBy = "unknown";
        }
    }

    public Purchase make(final OrderIn orderIn) {
        this.orderUp = new OrderUp(
            orderIn.getOrderId(),
            orderIn.getLineItemId(),
            orderIn.getItem(),
            orderIn.getName(),
            Instant.now(),
            madeBy
        );

        this.isEightySixed = false;
        return this;
    }

    public void sendRewardIfEligible(String name, String orderId, int totalCount, OrderBatch batch) {
        if (totalCount < 5) return;

        BigDecimal qdca10Total = batch.qdca10LineItems.stream()
            .filter(item -> name.equals(item.name))
            .map(item -> item.price)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal qdca10proTotal = batch.qdca10proLineItems.stream()
            .filter(item -> name.equals(item.name))
            .map(item -> item.price)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal qdca10Points = qdca10Total.multiply(BigDecimal.valueOf(0.10));
        BigDecimal qdca10proPoints = qdca10proTotal.multiply(BigDecimal.valueOf(0.15));
        BigDecimal rewardPoints = qdca10Points.add(qdca10proPoints);

        RewardEvent rewardEvent = new RewardEvent(name, orderId, rewardPoints);
        rewardEmitter.send(rewardEvent);

        logger.info("Reward sent to {}: {} pts (10%: {}, 15%: {})",
            name, rewardPoints, qdca10Points, qdca10proPoints);
    }

    public void processRewards(OrderBatch batch, Map<String, Integer> localCounter, Map<String, Integer> purchaseCount) {
        localCounter.forEach((name, localCount) -> {
            int total = purchaseCount.merge(name, localCount, Integer::sum);
    
            if (total >= 5) {
                BigDecimal qdca10Total = batch.qdca10LineItems.stream()
                    .filter(item -> name.equals(item.name))
                    .map(item -> item.price)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    
                BigDecimal qdca10proTotal = batch.qdca10proLineItems.stream()
                    .filter(item -> name.equals(item.name))
                    .map(item -> item.price)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    
                BigDecimal qdca10Points = qdca10Total.multiply(BigDecimal.valueOf(0.10));
                BigDecimal qdca10proPoints = qdca10proTotal.multiply(BigDecimal.valueOf(0.15));
                BigDecimal rewardPoints = qdca10Points.add(qdca10proPoints);
    
                rewardEmitter.send(new RewardEvent(name, batch.orderId, rewardPoints));
                logger.info("Reward sent to {}: {} pts (10%: {}, 15%: {})",
                    name, rewardPoints, qdca10Points, qdca10proPoints);
            }
        });
    }

    @Override
    public boolean isEightySixed() {
        return isEightySixed;
    }

    @Override
    public OrderUp getOrderUp() {
        return orderUp;
    }

    @Override
    public RewardEvent getRewardEvent() {
        return null;
    }
}