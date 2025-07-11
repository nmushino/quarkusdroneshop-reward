package io.quarkusdroneshop.domain.valueobjects;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkusdroneshop.domain.Item;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.StringJoiner;

@RegisterForReflection
public class OrderIn {

    private final String orderId;

    private final String lineItemId;

    private final Item item;

    private final String name;

    private final Instant timestamp;
    
    private final BigDecimal price; 

    // コンストラクタ（すべての値を受け取る）
    public OrderIn(String orderId, String lineItemId, Item item, String name, BigDecimal price) {
        this.orderId = orderId;
        this.lineItemId = lineItemId;
        this.item = item;
        this.name = name;
        this.timestamp = Instant.now();
        this.price = price;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderIn.class.getSimpleName() + "[", "]")
                .add("orderId='" + orderId + "'")
                .add("lineItemId='" + lineItemId + "'")
                .add("item=" + item)
                .add("name='" + name + "'")
                .add("timestamp=" + timestamp)
                .add("price=" + price)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderIn that = (OrderIn) o;

        if (orderId != null ? !orderId.equals(that.orderId) : that.orderId != null) return false;
        if (lineItemId != null ? !lineItemId.equals(that.lineItemId) : that.lineItemId != null) return false;
        if (item != that.item) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (!name.equals(that.name)) return false;
        if (!timestamp.equals(that.timestamp)) return false;
        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = orderId != null ? orderId.hashCode() : 0;
        result = 31 * result + (lineItemId != null ? lineItemId.hashCode() : 0);
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + price.hashCode();
        return result;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getLineItemId() {
        return lineItemId;
    }

    public Item getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

}
