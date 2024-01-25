package com.service.stock.repository;

import com.service.stock.entity.Item;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRepositoryTest {

    private static final String DATABASE_NAME = "databaseName";
    private static final String DATABASE_USERNAME = "databaseName";
    private static final String DATABASE_USER_PASSWORD = "databaseName";

    public static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:latest").withDatabaseName(DATABASE_NAME).withUsername(DATABASE_USERNAME)
                    .withPassword(DATABASE_USER_PASSWORD).withReuse(true);
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TestEntityManager entityManager;

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // Postgresql
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // Flyway
        registry.add("spring.flyway.cleanDisabled", () -> false);
    }

    @BeforeEach
    void clearDatabase(@Autowired Flyway flyway) {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void save_success() {
        Item item = new Item();
        item.setName("test");
        item.setStockAvailable(1000L);
        item.setStockReserved(0L);

        itemRepository.save(item);

        Item itemFromDb = entityManager.find(Item.class, item.getId());
        assertEquals(item, itemFromDb);
        assertEquals(item.getName(), itemFromDb.getName());
    }

    @Test
    public void findById_success() {
        Item item = new Item();
        item.setName("test");
        item.setStockAvailable(1000L);
        item.setStockReserved(0L);

        entityManager.persist(item);

        Item itemFromDb = itemRepository.findById(item.getId()).orElse(null);

        assertNotNull(itemFromDb);
        assertEquals(item, itemFromDb);
    }

    @Test
    public void deleteById_success() {
        Item item = new Item();
        item.setName("test");
        item.setStockAvailable(1000L);
        item.setStockReserved(0L);

        entityManager.persist(item);

        itemRepository.deleteById(item.getId());

        Item itemFromDb = entityManager.find(Item.class, item.getId());
        assertNull(itemFromDb);
        assertEquals(0, itemRepository.findAll().size());
    }

    @Test
    public void delete_success() {
        Item item = new Item();
        item.setName("test");
        item.setStockAvailable(1000L);
        item.setStockReserved(0L);

        entityManager.persist(item);

        itemRepository.delete(item);

        Item itemFromDb = entityManager.find(Item.class, item.getId());
        assertNull(itemFromDb);
        assertEquals(0, itemRepository.findAll().size());
    }
}
