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

    // 💡 顧客ごとの購入数トラッキング用
    private final Map<String, Integer> purchaseCount = new ConcurrentHashMap<>();

    @Incoming("orders-in")
    public CompletionStage<Void> onOrderIn(OrderBatch batch) {
        return CompletableFuture.runAsync(() -> {
    
            // 顧客名ごとの件数を処理
            Map<String, Integer> localCounter = new HashMap<>();
    
            // qdca10 と pro を1つのストリームに統合
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
                    lineItem.price
                );
    
                OrderProcessingResult result = isPro ? qdca10pro.make(orderIn) : qdca10.make(orderIn);
    
                if (result.isEightySixed()) {
                    eightySixEmitter.send(orderIn.getItem().toString());
                } else {
                    orderUpEmitter.send(result.getOrderUp());
    
                    // 顧客単位のカウントを更新
                    localCounter.merge(orderIn.getName(), 1, Integer::sum);
                }
            });
    
            // purchaseCount に反映し、5件に達したらリワード発行
            localCounter.forEach((name, localCount) -> {
                int total = purchaseCount.merge(name, localCount, Integer::sum);
                if (total - localCount < 5 && total >= 5) {
                    // 🎁 ちょうど5件に到達した時点でリワード付与（それ以前に付与していないことを前提）
                    BigDecimal rewardPoints = BigDecimal.ZERO;
    
                    // 適当に平均単価×5個で計算（必要に応じて正確な計算ロジックに修正可）
                    BigDecimal averagePrice = Stream.concat(batch.qdca10LineItems.stream(), batch.qdca10proLineItems.stream())
                        .filter(li -> li.name.equals(name))
                        .map(li -> li.price)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(localCount), BigDecimal.ROUND_HALF_UP);
    
                    // 通常は10%、Proなら15%（ここでは混合前提なので仮に10%適用）
                    rewardPoints = averagePrice.multiply(BigDecimal.valueOf(5)).multiply(BigDecimal.valueOf(0.10));
    
                    RewardEvent rewardEvent = new RewardEvent(name, batch.id, rewardPoints);
                    rewardEmitter.send(rewardEvent);
                }
            });
        });
    }
}