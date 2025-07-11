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

    // 💡 顧客ごとの購入数トラッキング用
    private final Map<String, Integer> purchaseCount = new ConcurrentHashMap<>();

    @Incoming("orders-in")
    public CompletionStage<Void> onOrderBatch(OrderBatch batch) {
        logger.debug("OrderBatch received: {}", batch.id);

        Stream.concat(
            batch.qdca10LineItems.stream().map(li -> new AbstractMap.SimpleEntry<>(li, false)),
            batch.qdca10proLineItems.stream().map(li -> new AbstractMap.SimpleEntry<>(li, true))
        ).forEach(entry -> {
            OrderBatch.LineItem lineItem = entry.getKey();
            boolean isPro = entry.getValue();

            String customerName = lineItem.name;

            // purchase count update
            int count = purchaseCount.merge(customerName, 1, Integer::sum);

            OrderIn orderIn = new OrderIn(
                batch.id,
                null,
                Item.valueOf(lineItem.item),
                lineItem.name,
                lineItem.price
            );

            OrderProcessingResult result = isPro ? qdca10pro.make(orderIn) : qdca10.make(orderIn);

            if (result.isEightySixed()) {
                eightySixEmitter.send(orderIn.getItem().toString());
            } else {
                orderUpEmitter.send(result.getOrderUp());

                if (count == 5) {
                    BigDecimal reward = lineItem.price.multiply(BigDecimal.valueOf(0.10));
                    rewardEmitter.send(new RewardEvent(customerName, batch.id, reward));
                    // purchaseCount.put(customerName, 0); // 必要なら
                }
            }
        });

        return CompletableFuture.completedFuture(null);
    }
}