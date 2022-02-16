package uk.co.codelity.inventory.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.co.codelity.event.sourcing.common.annotation.EventScan;
import uk.co.codelity.event.sourcing.common.annotation.EventSourcingEnabled;

@EventSourcingEnabled
@SpringBootApplication
@EventScan(basePackages = "uk.co.codelity.inventory.service.events")
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
