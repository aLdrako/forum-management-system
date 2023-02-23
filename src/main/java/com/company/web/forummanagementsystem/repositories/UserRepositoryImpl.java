package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

//@Repository
public class UserRepositoryImpl implements UserRepository {
    private final List<User> users;

    public UserRepositoryImpl() {
        users = new ArrayList<>();

        AtomicLong counter = new AtomicLong();
        User admin = new User(counter.incrementAndGet(), "Alex", "Dune", "alex@gmail.com", "alexgo", "qwerty123");
        admin.setAdmin(true);
        users.add(admin);
        users.add(new User(counter.incrementAndGet(), "John", "Milk", "johnmild@mail.com", "johndo", "abcd111"));
        users.add(new User(counter.incrementAndGet(), "Adam", "Smith", "adamsmith@gmail.com", "adams", "pass1234"));
        users.add(new User(counter.incrementAndGet(), "Alan", "Rockstar", "alan@yahoo.com", "rockstar", "rockstar999"));
        users.add(new User(counter.incrementAndGet(), "Kate", "Alabama", "katyb@yahoo.com", "katy", "secret13"));
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users);
    }

    @Override
    public User getById(Long id) {
        return users.stream()
                .filter(user -> Objects.equals(user.getId(), id))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    @Override
    public List<User> search(String parameter) {
        List<User> result = users.stream()
                .filter(user -> user.getUsername().equals(parameter))
                .collect(Collectors.toList());
        if (result.size() == 0) throw new EntityNotFoundException("User", String.valueOf(parameter), parameter);
        return result;
    }

    /**
     * Helper method to check users by email, maintain uniqueness
     * method is called when new user is created or updated
     * @param email
     * @return
     */
    @Override
    public User getByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User", "email", email));
    }

    @Override
    public User create(User user) {
        long currentId = !users.isEmpty() ? users.get(users.size() - 1).getId() : 1L;
        user.setId(currentId + 1);
        users.add(user);
        return user;
    }

    @Override
    public User update(User user) {
        User userToUpdate = getById(user.getId());
        userToUpdate.setFirstName(user.getFirstName());
        userToUpdate.setLastName(user.getLastName());
        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setPassword(user.getPassword());
        userToUpdate.setAdmin(user.isAdmin());
        userToUpdate.setBlocked(user.isBlocked());
        return userToUpdate;
    }

    @Override
    public void delete(Long id) {
        User userToDelete = getById(id);
        users.remove(userToDelete);
    }
}
