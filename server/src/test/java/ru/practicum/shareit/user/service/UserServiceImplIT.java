package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.CreateDuplicateEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIT {
    private final EntityManager em;
    private final UserService service;

    private Long userId;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .name("User")
                .email("user@mail.ru").build();
        userId = service.create(userDto).getId();
    }

    @Test
    void getAll() {
        UserDto userDto1 = UserDto.builder()
                .name("User1")
                .email("user1@mail.ru").build();
        UserDto userDto2 = UserDto.builder()
                .name("User2")
                .email("user2@mail.ru").build();
        UserDto userDto3 = UserDto.builder()
                .name("User3")
                .email("user3@mail.ru").build();

        List<UserDto> sourceUsers = new ArrayList<>(List.of(userDto1, userDto2, userDto3));

        for (UserDto sourceUser : sourceUsers) {
            service.create(sourceUser);
        }
        userDto.setId(userId);
        sourceUsers.add(userDto);

        List<UserDto> targetUsers = service.getAll(0, 10);

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void findById() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User user = query.setParameter("id", userId).getSingleResult();

        assertNotNull(user.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void create() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        assertNotNull(user.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void create_withDuplicateUser() {
        UserDto userDtoDuplicate = UserDto.builder()
                .name("Duplicate")
                .email("user@mail.ru").build();

        assertThrows(CreateDuplicateEntityException.class,
                () -> service.create(userDtoDuplicate));
    }

    @Test
    void delete() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();
        assertNotNull(user.getId());

        service.delete(user.getId());
        TypedQuery<User> queryAfterDelete = em.createQuery(
                "Select u from User u where u.email = :email", User.class);
        List<User> userResult = queryAfterDelete.setParameter("email", userDto.getEmail()).getResultList();

        assertTrue(userResult.isEmpty());
    }

    @Test
    void update() {
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail()).getSingleResult();

        UserDto updateDto = UserDto.builder()
                .name("Update")
                .email("user@mail.ru").build();
        service.update(user.getId(), updateDto);

        query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userUpdated = query.setParameter("email", updateDto.getEmail()).getSingleResult();

        assertNotNull(userUpdated.getId());
        assertEquals(userUpdated.getName(), updateDto.getName());
        assertEquals(userUpdated.getEmail(), updateDto.getEmail());
    }
}