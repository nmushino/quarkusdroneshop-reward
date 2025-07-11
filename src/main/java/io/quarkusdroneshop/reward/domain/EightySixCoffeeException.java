package io.quarkusdroneshop.qdca10.domain;

import io.quarkusdroneshop.domain.Item;

import java.util.Arrays;
import java.util.Collection;

public class EightySixCoffeeException extends Exception {

    public Collection<EightySixEvent> getEvents() {
        return Arrays.asList(new EightySixEvent(Item.QDC_A101), new EightySixEvent(Item.QDC_A102));
    }

}
