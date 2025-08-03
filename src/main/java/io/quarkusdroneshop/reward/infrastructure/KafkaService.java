package io.quarkusdroneshop.reward.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkusdroneshop.domain.Item;
import io.quarkusdroneshop.domain.valueobjects.*;
import io.quarkusdroneshop.reward.domain.OrderBatch;
import io.quarkusdroneshop.reward.domain.Purchase;

import org.eclipse.microprofile.reactive.messaging.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;

@ApplicationScoped
@RegisterForReflection
public class KafkaService {

    Logger logger = LoggerFactory.getLogger(KafkaService.class);

    // 累積購入数
    private final Map<String, Integer> purchaseCount = new ConcurrentHashMap<>();

    @Inject Purchase purchase;

    @Inject @Channel("rewards")
    Emitter<RewardEvent> rewardEmitter;

    @Incoming("orders-in")
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public CompletionStage<Void> onOrderIn(OrderBatch batch) {
        return CompletableFuture.runAsync(() -> {
            logger.info("Received OrderBatch: {}", batch);

            // 今回のバッチでのカウント
            Map<String, Integer> localCounter = new HashMap<>();

            Stream.concat(
                batch.qdca10LineItems.stream().map(item -> Map.entry(item, false)),
                batch.qdca10proLineItems.stream().map(item -> Map.entry(item, true))
            ).forEach(entry -> {
                OrderBatch.LineItem lineItem = entry.getKey();
                boolean isPro = entry.getValue();

                OrderIn reconstructedOrder = new OrderIn(
                    batch.orderId,
                    null,
                    Item.valueOf(lineItem.item),
                    lineItem.name,
                    lineItem.price,
                    batch.orderSource
                );

                OrderProcessingResult result = purchase.make(reconstructedOrder);

                if (!result.isEightySixed()) {
                    localCounter.merge(reconstructedOrder.getName(), 1, Integer::sum);
                }
            });

            // リワード処理（Purchase に委譲）
            purchase.processRewards(batch, localCounter, purchaseCount);
        });
    }
}