package io.quarkusdroneshop.reward.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkusdroneshop.reward.domain.OrderBatch;
import io.quarkusdroneshop.reward.domain.Qdca10;
import io.quarkusdroneshop.reward.domain.Qdca10pro;
import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.*;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
@RegisterForReflection
public class KafkaService {

    Logger logger = LoggerFactory.getLogger(KafkaService.class);

    @Inject
    Qdca10 qdca10;

    @Inject
    Qdca10pro qdca10pro;

    @Inject
    @Channel("orders-up")
    Emitter<OrderUp> orderUpEmitter;

    @Inject
    @Channel("eighty-six")
    Emitter<String> eightySixEmitter;

    @Inject
    @Channel("rewards")
    Emitter<RewardEvent> rewardEmitter;

    private final Map<String, Integer> purchaseCount = new ConcurrentHashMap<>();

    @Incoming("orders-in")
    public CompletionStage<Void> onOrderIn(OrderBatch batch) {
        return CompletableFuture.runAsync(() -> {

            // 🔍 qdca10LineItems の中身を出力（デバッグ用）
            for (OrderBatch.LineItem lineItem : batch.qdca10LineItems) {
                System.out.println("Item: " + lineItem.item + ", name: " + lineItem.name + ", price: " + lineItem.price);
            }
            for (OrderBatch.LineItem lineItem : batch.qdca10proLineItems) {
                System.out.println("Item: " + lineItem.item + ", name: " + lineItem.name + ", price: " + lineItem.price);
            }

            Map<String, Integer> localCounter = new HashMap<>();

            Stream.concat(
                batch.qdca10LineItems.stream().map(item -> Map.entry(item, false)),
                batch.qdca10proLineItems.stream().map(item -> Map.entry(item, true))
            ).forEach(entry -> {
                OrderBatch.LineItem lineItem = entry.getKey();
                boolean isPro = entry.getValue();

                OrderIn orderIn = new OrderIn(
                    batch.orderId,
                    null,
                    Item.valueOf(lineItem.item),
                    lineItem.name,
                    lineItem.price,
                    batch.orderSource
                );

                OrderProcessingResult result = isPro ? qdca10pro.make(orderIn) : qdca10.make(orderIn);

                if (result.isEightySixed()) {
                    eightySixEmitter.send(orderIn.getItem().toString());
                } else {
                    orderUpEmitter.send(result.getOrderUp());
                    localCounter.merge(orderIn.getName(), 1, Integer::sum);
                }
            });

            localCounter.forEach((name, localCount) -> {
                int total = purchaseCount.merge(name, localCount, Integer::sum);
                if (total - localCount < 5 && total >= 5) {
                    BigDecimal averagePrice = Stream.concat(batch.qdca10LineItems.stream(), batch.qdca10proLineItems.stream())
                        .filter(li -> li.name.equals(name))
                        .map(li -> li.price)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(localCount), BigDecimal.ROUND_HALF_UP);

                    BigDecimal rewardPoints = averagePrice.multiply(BigDecimal.valueOf(5)).multiply(BigDecimal.valueOf(0.10));
                    RewardEvent rewardEvent = new RewardEvent(name, batch.orderId, rewardPoints);
                    rewardEmitter.send(rewardEvent);
                }
            });
        });
    }
}