package io.quarkusdroneshop.reward.domain;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkusdroneshop.domain.valueobjects.OrderIn;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBatch {

    Logger LOGGER = LoggerFactory.getLogger(OrderBatch.class.getName());

    @JsonProperty("id")
    public String orderId;

    public String orderSource;
    public String location;
    public String loyaltyMemberId;

    public List<LineItem> qdca10LineItems;
    public List<LineItem> qdca10proLineItems;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LineItem {
        public String item;
        public BigDecimal price;
        public String name;
    }

}
