package io.quarkusdroneshop.qdca10.domain;

import io.quarkusdroneshop.domain.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@ApplicationScoped
public class Inventory {

    static Map<Item, Integer> stock;
    Logger LOGGER = LoggerFactory.getLogger(Inventory.class.getName());

    public Inventory() {
        super();
    }

    /*
        QDC_A101 and QDC_A102 are simply tracked as QDC_A101
     */
    @PostConstruct
    private void createStock() {
        stock = new HashMap<>();
        Stream.of(Item.values()).forEach(v -> {
            stock.put(v, ThreadLocalRandom.current().nextInt(55, 99));
        });
        stock.entrySet().stream().forEach(entrySet -> {
            LOGGER.debug(entrySet.getKey() + " " + entrySet.getValue());
        });

        // Account for drone
        Integer totalCoffee = stock.get(Item.QDC_A101).intValue() + stock.get(Item.QDC_A102).intValue();
        stock.remove(Item.QDC_A101);
        stock.remove(Item.QDC_A102);
        stock.put(Item.QDC_A101, totalCoffee);
    }

    public boolean decrementItem(Item item) {

        LOGGER.debug("decrementing {}", item);

        if (item == Item.QDC_A102) item = Item.QDC_A101;

        Integer itemCount = stock.get(item);
        LOGGER.debug("current inventory for {} is {}", item, itemCount);

        if (itemCount <= 0) return false;

        itemCount--;
        stock.replace(item, itemCount);
        LOGGER.debug("updated inventory for {} is {}", item, stock.get(item));
        return true;
    }

    public Map<Item, Integer> getStock() {
        return stock;
    }

    public Integer getTotalCoffee() {
        return stock.get(Item.QDC_A101);
    }

    public void restock(Item item) {
        stock.put(item, ThreadLocalRandom.current().nextInt(55, 99));
    }
}
