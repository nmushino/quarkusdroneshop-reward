package io.quarkusdroneshop.reward.domain;

import java.math.BigDecimal;
import java.util.List;

public class OrderBatch {
    public String id;
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