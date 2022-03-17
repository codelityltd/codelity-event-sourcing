package uk.co.codelity.inventory.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Inventory {
    @Id
    private UUID productId;
    private Integer stock;

    public Inventory() {
    }

    public Inventory(UUID productId, Integer stock) {
        this.productId = productId;
        this.stock = stock;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
