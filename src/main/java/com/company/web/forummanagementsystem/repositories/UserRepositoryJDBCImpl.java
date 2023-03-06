package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Permission;
import com.company.web.forummanagementsystem.models.User;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//@Repository
@PropertySource("classpath:application.properties")
public class UserRepositoryJDBCImpl implements UserRepository {

    private static final String SQL_GET = """
            SELECT id, first_name, last_name, email, username, password, join_date, is_admin, is_blocked, is_deleted, phone_number, photo
            FROM users
            lEFT JOIN permission p on users.id = p.user_id
            lEFT JOIN phones p2 on users.id = p2.user_id
            lEFT JOIN photos p3 on users.id = p3.user_id
            WHERE is_deleted <> 1
            """;
    private static final String SQL_CREATE = """
            INSERT INTO users (first_name, last_name, email, username, password)
            VALUES (?, ?, ?, ?, ?);
            """;
    private static final String SQL_CREATE_PERMISSIONS = """
            INSERT INTO permission (is_deleted, is_blocked, is_admin, user_id)
            VALUES (?, ?, ?, ?);
            """;
    private static final String SQL_CREATE_PHONE = """
            INSERT INTO phones (phone_number, user_id)
            VALUES (?, ?);
            """;
    private static final String SQL_CREATE_PHOTO = """
            INSERT INTO photos (photo, user_id)
            VALUES (?, ?);
            """;
    private static final String SQL_UPDATE = """
            UPDATE users SET first_name = ?, last_name = ?, email = ?, username = ?, password = ?
            WHERE id = ?;
            """;
    private static final String SQL_UPDATE_PERMISSIONS = """
            UPDATE permission SET is_deleted = ?, is_blocked = ?, is_admin = ?
            WHERE user_id = ?;
            """;
    private static final String SQL_UPDATE_PHONE = """
            UPDATE permission SET phone_number = ?
            WHERE user_id = ?;
            """;
    private static final String SQL_UPDATE_PHOTO = """
            UPDATE permission SET photo = ?
            WHERE user_id = ?;
            """;
    private static final String SQL_DELETE = """
            UPDATE permission SET is_deleted = true
            WHERE user_id = ?;
            """;
    private static final String SQL_GET_ID = """
            SELECT id FROM users
            WHERE email = ?;
            """;

    private final String dbUrl, dbUsername, dbPassword;
    private final PermissionRepository permissionRepository;

    public UserRepositoryJDBCImpl(PermissionRepository permissionRepository, Environment environment) {
        this.permissionRepository = permissionRepository;
        dbUrl = environment.getProperty("database.url");
        dbUsername = environment.getProperty("database.username");
        dbPassword = environment.getProperty("database.password");
    }

    @Override
    public List<User> getAll() {
        return search("*=*");
    }

    @Override
    public User getById(Long id) {
        return search("id=" + id).get(0);
    }

    @Override
    public List<User> search(String parameter) {
        String[] params = parameter.split("=");
        boolean hasParams = true;
        String searchParam = switch (params[0]) {
            case "id" -> " AND id = ?;";
            case "email" -> " AND email = ?;";
            case "username" -> " AND username = ?;";
            case "firstName" -> " AND first_name = ?;";
            default -> { hasParams = false; yield ";"; }
        };
        try (PreparedStatement statement = DriverManager.getConnection(dbUrl, dbUsername, dbPassword).prepareStatement(SQL_GET + searchParam)) {
            if (hasParams) statement.setString(1, params[1]);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<User> result = getUsers(resultSet);
                if (result.size() == 0) throw new EntityNotFoundException("User", params[0], params[1]);
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User create(User user) {
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(SQL_CREATE);
                PreparedStatement statementPermissions = connection.prepareStatement(SQL_CREATE_PERMISSIONS);
                PreparedStatement statementPhone = connection.prepareStatement(SQL_CREATE_PHONE);
                PreparedStatement statementPhoto = connection.prepareStatement(SQL_CREATE_PHOTO)
        ) {
            userStatement(user, statement);
            statement.executeUpdate();

            User newUser = getIdByEmail(user.getEmail());
            permissionStatement(newUser, statementPermissions, "create");
            statementPermissions.executeUpdate();

            phoneStatement(statementPhone, newUser);
            statement.executeUpdate();

            photoStatement(statementPhoto, newUser);
            statementPhoto.executeUpdate();

            return getById(newUser.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User update(User user) {
        getById(user.getId());
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(SQL_UPDATE);
                PreparedStatement statementPermissions = connection.prepareStatement(SQL_UPDATE_PERMISSIONS);
                PreparedStatement statementPhone = connection.prepareStatement(SQL_UPDATE_PHONE);
                PreparedStatement statementPhoto = connection.prepareStatement(SQL_UPDATE_PHOTO)
        ) {
            userStatement(user, statement);
            statement.setLong(6, user.getId());
            statement.executeUpdate();

            permissionStatement(user, statementPermissions, "update");
            statementPermissions.executeUpdate();

            phoneStatement(statementPhone, user);
            statementPhone.executeUpdate();

            photoStatement(statementPhoto, user);
            statementPhoto.executeUpdate();

            return search("email=" + user.getEmail()).get(0);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(Long id) {
        getById(id);
        try (PreparedStatement statement = DriverManager.getConnection(dbUrl, dbUsername, dbPassword).prepareStatement(SQL_DELETE)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public User getIdByEmail(String email) {
        try (PreparedStatement statement = DriverManager.getConnection(dbUrl, dbUsername, dbPassword).prepareStatement(SQL_GET_ID)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? new User(resultSet.getLong("id")) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void userStatement(User user, PreparedStatement statement) throws SQLException {
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getEmail());
        statement.setString(4, user.getUsername());
        statement.setString(5, user.getPassword());
    }

    private static void permissionStatement(User user, PreparedStatement statement, String operation) throws SQLException {
        if (operation.equals("update")) {
            statement.setBoolean(1, user.getPermission().isDeleted());
            statement.setBoolean(2, user.getPermission().isBlocked());
            statement.setBoolean(3, user.getPermission().isAdmin());
        } else for (int i = 1; i <= 3; i++) statement.setBoolean(i, false);
        statement.setLong(4, user.getId());
    }

    private static void photoStatement(PreparedStatement statementPhoto, User user) throws SQLException {
        statementPhoto.setBytes(1, user.getPhoto().orElse(null));
        statementPhoto.setLong(2, user.getId());
    }

    private static void phoneStatement(PreparedStatement statementPhone, User user) throws SQLException {
        statementPhone.setString(1, user.getPhoneNumber().orElse(null));
        statementPhone.setLong(2, user.getId());
    }

    private List<User> getUsers(ResultSet usersData) throws SQLException {
        List<User> users = new ArrayList<>();
        while (usersData.next()) {
            User user = new User();
            user.setId(usersData.getLong("id"));
            user.setLastName(usersData.getString("first_name"));
            user.setFirstName(usersData.getString("last_name"));
            user.setEmail(usersData.getString("email"));
            user.setUsername(usersData.getString("username"));
            user.setPassword(usersData.getString("password"));
            user.setJoiningDate(usersData.getTimestamp("join_date").toLocalDateTime());
            user.setPhoneNumber(usersData.getString("phone_number"));
            user.setPhoto(usersData.getBytes("photo"));
            user.setPermission(permissionRepository.getById(usersData.getLong("id")));
            users.add(user);
        }
        return users;
    }
}