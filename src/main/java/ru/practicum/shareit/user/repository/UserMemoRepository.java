package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.CreateDuplicateEntityException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserMemoRepository implements UserRepository {

    private Map<Long, User> storage = new HashMap<>();
    private Long lastId = 1L;

    @Override
    public List<User> getAll() {
        return storage.values()
                .stream()
                .sorted((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(Long userId) {
        if (storage.containsKey(userId)) {
            return Optional.of(storage.get(userId));
        }
        return Optional.empty();
    }

    @Override
    public User create(User user) {
        findMatch(user).ifPresent(user1 -> {
            throw new CreateDuplicateEntityException(User.class, user1.getId());
        });
        ;
        user.setId(lastId++);
        storage.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        storage.remove(userId);
    }

    @Override
    public User update(Long userId, User user) {
        findMatch(user).ifPresent(user1 -> {
            if (!userId.equals(user1.getId())) {
                throw new CreateDuplicateEntityException(User.class, user1.getId());
            }
        });

        User oldUser = findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", userId.toString())));
        oldUser.setName(user.getName() != null ? user.getName() : oldUser.getName());
        oldUser.setEmail(user.getEmail() != null ? user.getEmail() : oldUser.getEmail());
        return storage.put(oldUser.getId(), oldUser);
    }

    private Optional<User> findMatch(User user) {
        return storage.values()
                .stream()
                .filter(user::equals)
                .findFirst();
    }
}
