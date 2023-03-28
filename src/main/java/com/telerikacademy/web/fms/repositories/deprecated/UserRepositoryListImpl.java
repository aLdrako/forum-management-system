//package com.telerikacademy.web.fms.repositories.deprecated;
//
//import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
//import com.telerikacademy.web.fms.models.Permission;
//import com.telerikacademy.web.fms.models.User;
//import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.stream.Collectors;
//
////@Repository
//public class UserRepositoryListImpl implements UserRepository {
//    private final List<User> users;
//
//    public UserRepositoryListImpl() {
//        users = new ArrayList<>();
//
//        AtomicLong counter = new AtomicLong();
////        User admin = new User(counter.incrementAndGet(), "Alex", "Dune", "alex@gmail.com", "alexgo", "qwerty123");
////        admin.getPermissions().setAdmin(true);
////        users.add(admin);
////        users.add(new User(counter.incrementAndGet(), "John", "Milk", "johnmild@mail.com", "johndo", "abcd111"));
////        users.add(new User(counter.incrementAndGet(), "Adam", "Smith", "adamsmith@gmail.com", "adams", "pass1234"));
////        users.add(new User(counter.incrementAndGet(), "Alan", "Rockstar", "alan@yahoo.com", "rockstar", "rockstar999"));
////        users.add(new User(counter.incrementAndGet(), "Kate", "Alabama", "katyb@yahoo.com", "katy", "secret13"));
//    }
//
//    @Override
//    public List<User> getAll() {
//        return new ArrayList<>(users);
//    }
//
//    @Override
//    public User getById(Long id) {
//        return users.stream()
//                .filter(user -> Objects.equals(user.getId(), id))
//                .findFirst()
//                .orElseThrow(() -> new EntityNotFoundException("User", id));
//    }
//
//    @Override
//    public List<User> search(String parameter) {
//        String[] params = parameter.split("=");
//        List<User> result = switch (params[0]) {
//            case "email" -> users.stream()
//                    .filter(user -> user.getEmail().equals(parameter))
//                    .collect(Collectors.toList());
//            case "username" -> users.stream()
//                    .filter(user -> user.getUsername().equals(parameter))
//                    .collect(Collectors.toList());
//            case "firstName" -> users.stream()
//                    .filter(user -> user.getFirstName().equals(parameter))
//                    .collect(Collectors.toList());
//            default -> users;
//        };
//        if (result.size() == 0) throw new EntityNotFoundException("User", params[0], params[1]);
//        return result;
//    }
//
//    @Override
//    public User create(User user) {
//        long currentId = !users.isEmpty() ? users.get(users.size() - 1).getId() : 1L;
//        user.setId(currentId + 1);
//        users.add(user);
//        return user;
//    }
//
//    @Override
//    public User update(User user) {
//        User userToUpdate = getById(user.getId());
//        userToUpdate.setFirstName(user.getFirstName());
//        userToUpdate.setLastName(user.getLastName());
//        userToUpdate.setEmail(user.getEmail());
//        userToUpdate.setUsername(user.getUsername());
//        userToUpdate.setPassword(user.getPassword());
////        userToUpdate.setAdmin(user.isAdmin());
////        userToUpdate.setBlocked(user.isBlocked());
//        return userToUpdate;
//    }
//
//    @Override
//    public Permission updatePermissions(Permission permission) {
//        return null;
//    }
//
//    @Override
//    public void delete(Long id) {
//        User userToDelete = getById(id);
//        users.remove(userToDelete);
//    }
//}
