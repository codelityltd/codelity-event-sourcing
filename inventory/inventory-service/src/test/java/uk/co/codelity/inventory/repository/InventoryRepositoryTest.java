package uk.co.codelity.inventory.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import uk.co.codelity.inventory.entity.Inventory;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
class InventoryRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    void shouldSaveInventory(){
        UUID productId = UUID.randomUUID();

        inventoryRepository.save(new Inventory(productId, 2));
        Optional<Inventory> inventory = inventoryRepository.findById(productId);
        assertThat(inventory.isPresent(), is(true));
        assertThat(inventory.get().getProductId(), is(productId));
        assertThat(inventory.get().getStock(), is(2));
    }
}