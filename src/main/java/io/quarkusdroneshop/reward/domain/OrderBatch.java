package io.quarkusdroneshop.reward.domain;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderBatch {

    @JsonProperty("id")
    public String orderId;
    
    public String orderSource;
    public String location;
    public List<LineItem> qdca10LineItems;
    public List<LineItem> qdca10proLineItems;

    public static class LineItem {
        public String item;
        public BigDecimal price;
        public String name;
    }
}