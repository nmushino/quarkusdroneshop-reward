package io.quarkusdroneshop.domain.valueobjects;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkusdroneshop.domain.Item;

import java.time.Instant;
import java.util.StringJoiner;

@RegisterForReflection
public class OrderUp {

    public final String orderId;

    public final String lineItemId;

    public final Item item;

    public final String name;

    public final Instant timestamp;

    public final String madeBy;

    public OrderUp(String orderId, String lineItemId, Item item, String name, Instant timestamp, String madeBy) {
        this.orderId = orderId;
        this.lineItemId = lineItemId;
        this.item = item;
        this.name = name;
        this.timestamp = timestamp;
        this.madeBy = madeBy;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", OrderUp.class.getSimpleName() + "[", "]")
                .add("orderId='" + orderId + "'")
                .add("lineItemId='" + lineItemId + "'")
                .add("item=" + item)
                .add("name='" + name + "'")
                .add("timestamp=" + timestamp)
                .add("madeBy='" + madeBy + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderUp orderUp = (OrderUp) o;

        if (orderId != null ? !orderId.equals(orderUp.orderId) : orderUp.orderId != null) return false;
        if (lineItemId != null ? !lineItemId.equals(orderUp.lineItemId) : orderUp.lineItemId != null) return false;
        if (item != orderUp.item) return false;
        if (name != null ? !name.equals(orderUp.name) : orderUp.name != null) return false;
        if (timestamp != null ? !timestamp.equals(orderUp.timestamp) : orderUp.timestamp != null) return false;
        return madeBy != null ? madeBy.equals(orderUp.madeBy) : orderUp.madeBy == null;
    }

    @Override
    public int hashCode() {
        int result = orderId != null ? orderId.hashCode() : 0;
        result = 31 * result + (lineItemId != null ? lineItemId.hashCode() : 0);
        result = 31 * result + (item != null ? item.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (madeBy != null ? madeBy.hashCode() : 0);
        return result;
    }
}
