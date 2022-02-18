package uk.co.codelity.inventory.api.contracts;

import javax.validation.constraints.NotNull;

public class SupplyRequest {
    @NotNull
    private Integer quantity;

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
