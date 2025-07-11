package io.quarkusdroneshop.qdca10.infrastructure;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkusdroneshop.qdca10.domain.Qdca10;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;
import io.quarkusdroneshop.domain.valueobjects.OrderUp;
import io.quarkusdroneshop.domain.valueobjects.Qdca10Result;

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
    @Channel("orders-up")
    Emitter<OrderUp> orderUpEmitter;

    @Inject
    @Channel("eighty-six")
    Emitter<String> eightySixEmitter;

    @Incoming("orders-in")
    public CompletableFuture<Void> onOrderIn(final OrderIn orderIn) {

        logger.debug("OrderTicket received: {}", orderIn);
    
        return CompletableFuture
            .supplyAsync(() -> qdca10.make(orderIn))
            .thenAccept(result -> {
    
                if (result.isEightySixed()) {
                    eightySixEmitter.send(orderIn.getItem().toString());
                } else {
                    logger.debug("OrderUp: {}", result.getOrderUp());
                    orderUpEmitter.send(result.getOrderUp());
                }
            });
    }
}
