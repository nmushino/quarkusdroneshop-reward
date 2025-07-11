package io.quarkusdroneshop.reward.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkusdroneshop.reward.domain.Qdca10;
import io.quarkusdroneshop.reward.domain.Qdca10pro;
import io.quarkusdroneshop.domain.valueobjects.*;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

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

    @Incoming("orders-in")
    public CompletableFuture<Void> onOrderIn(final OrderIn orderIn) {

        logger.debug("OrderTicket received: {}", orderIn);

        return CompletableFuture
            .supplyAsync(() -> {
                if (orderIn.getName() != null && orderIn.getName().startsWith("pro")) {
                    return qdca10pro.make(orderIn);
                } else {
                    return qdca10.make(orderIn);
                }
            })
            .thenAccept(result -> {
                if (result.isEightySixed()) {
                    eightySixEmitter.send(orderIn.getItem().toString());
                } else {
                    OrderUp orderUp = result.getOrderUp();
                    orderUpEmitter.send(orderUp);

                    RewardEvent rewardEvent = result.getRewardEvent();
                    if (rewardEvent != null) {
                        rewardEmitter.send(rewardEvent);
                    }
                }
            });
    }
}