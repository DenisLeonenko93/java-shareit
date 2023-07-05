package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryIT {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    private Long userId;
    private Item item;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
                .name("User")
                .email("user@mail.ru").build();
        userRepository.save(user);
        userId = user.getId();
        item = Item.builder()
                .name("Saw")
                .description("Desc")
                .available(true)
                .owner(user).build();
        item = itemRepository.save(item);
    }

    @Test
    void findAllByOwner_Id() {
        Pageable page = PageRequest.of(0, 1);
        List<Item> actualItems = itemRepository.findAllByOwner_Id(userId, page);

        assertNotNull(actualItems);
        assertFalse(actualItems.isEmpty());
        assertEquals(item, actualItems.get(0));
    }

    @Test
    void search() {
        Pageable page = PageRequest.of(0, 1);
        String text = "esc";

        List<Item> actualItems = itemRepository.search(text, page);

        assertNotNull(actualItems);
        assertFalse(actualItems.isEmpty());
        assertEquals(item, actualItems.get(0));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
    }
}