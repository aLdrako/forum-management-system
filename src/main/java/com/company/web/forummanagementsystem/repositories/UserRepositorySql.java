package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.User;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@PropertySource("classpath:application.properties")
public class UserRepositorySql implements UserRepository {

    private static final String SQL_GET_ALL = """
            SELECT
            id, first_name, last_name, email, username, password, join_date, is_admin, is_blocked, phone_number
            FROM users
            JOIN permissions p on users.id = p.user_id
            lEFT JOIN phones p2 on users.id = p2.user_id;
            """;

    private static final String SQL_GET_BY_ID = """
            SELECT
            id, first_name, last_name, email, username, password, join_date, is_admin, is_blocked, phone_number
            FROM users
            JOIN permissions p on users.id = p.user_id
            lEFT JOIN phones p2 on users.id = p2.user_id
            WHERE id = ?;
            """;

    private static final String SQL_GET_BY_USERNAME = """
            SELECT
            id, first_name, last_name, email, username, password, join_date, is_admin, is_blocked, phone_number
            FROM users
            JOIN permissions p on users.id = p.user_id
            lEFT JOIN phones p2 on users.id = p2.user_id
            WHERE username = ?;
            """;

    private static final String SQL_GET_BY_EMAIL = """
            SELECT
            id, email
            FROM users
            WHERE email = ?;
            """;

    private static final String CREATE = """
            INSERT INTO
            users (first_name, last_name, email, username, password, join_date)
            VALUES
            (?, ?, ?, ?, ?, ?)
            """;
    private static final String CREATE_PERMISSIONS = """
            INSERT INTO
            permissions
            (is_blocked, is_admin, user_id)
            VALUES
            (?, ?, ?)
            """;

    private static final String UPDATE = """
            UPDATE users SET
            first_name = ?, last_name = ?, email = ?, username = ?, password = ?
            WHERE id = ?
            """;
    private static final String UPDATE_PERMISSIONS = """
            UPDATE permissions SET
            is_blocked = ?, is_admin = ?
            WHERE user_id = ?
            """;

    private static final String DELETE = """
            DELETE FROM users
            WHERE id = ?
            """;

    private static final String DELETE_PERMISSIONS = """
            DELETE FROM permissions
            WHERE user_id = ?
            """;

    private final String dbUrl, dbUsername, dbPassword;

    public UserRepositorySql(Environment environment) {
        dbUrl = environment.getProperty("database.url");
        dbUsername = environment.getProperty("database.username");
        dbPassword = environment.getProperty("database.password");
    }

    @Override
    public List<User> getAll() {
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL_GET_ALL)
        ) {
            return getUsers(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User getById(Long id) {
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(SQL_GET_BY_ID)
                ) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<User> result = getUsers(resultSet);
                if (result.size() == 0) throw new EntityNotFoundException("User", id);
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User getByUsername(String username) {
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(SQL_GET_BY_USERNAME)
        ) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<User> result = getUsers(resultSet);
                if (result.size() == 0) throw new EntityNotFoundException("User", "username", username);
                return result.get(0);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Helper method to check users by email, maintain uniqueness
     * method is called when new user is created or updated
     * @param email
     * @return User with limited fields
     */
    @Override
    public User getByEmail(String email) {
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(SQL_GET_BY_EMAIL)
        ) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                User user = null;
                if (resultSet.next()) {
                    user = new User();
                    user.setId(resultSet.getLong("id"));
                    user.setEmail(resultSet.getString("email"));
                }
                if (user == null) throw new EntityNotFoundException("User", "email", email);
                return user;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void create(User user) {
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(CREATE);
                PreparedStatement statementPermissions = connection.prepareStatement(CREATE_PERMISSIONS)
        ) {
            prepareUserStatement(user, statement);
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.executeUpdate();

            User newUser = getByEmail(user.getEmail());
            user.setId(newUser.getId());

            preparePermissionStatement(user, statementPermissions, newUser);
            statementPermissions.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {
        getById(user.getId());
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(UPDATE);
                PreparedStatement statementPermissions = connection.prepareStatement(UPDATE_PERMISSIONS)
        ) {
            prepareUserStatement(user, statement);
            statement.setLong(6, user.getId());
            statement.executeUpdate();

            preparePermissionStatement(user, statementPermissions, user);
            statementPermissions.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        getById(id);
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(DELETE);
                PreparedStatement statementPermissions = connection.prepareStatement(DELETE_PERMISSIONS)
        ) {
            statementPermissions.setLong(1, id);
            statementPermissions.executeUpdate();

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllWithParams(Optional<Long> id, Optional<String> username) {
        return null;
    }

    private static void prepareUserStatement(User user, PreparedStatement statement) throws SQLException {
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getLastName());
        statement.setString(3, user.getEmail());
        statement.setString(4, user.getUsername());
        statement.setString(5, user.getPassword());
    }

    private static void preparePermissionStatement(User user, PreparedStatement statementPermissions, User newUser) throws SQLException {
        statementPermissions.setBoolean(1, user.isBlocked());
        statementPermissions.setBoolean(2, user.isAdmin());
        statementPermissions.setLong(3, newUser.getId());
    }

    private List<User> getUsers(ResultSet usersData) throws SQLException {
        List<User> users = new ArrayList<>();
        while (usersData.next()) {
            User user = new User(
                    usersData.getLong("id"),
                    usersData.getString("first_name"),
                    usersData.getString("last_name"),
                    usersData.getString("email"),
                    usersData.getString("username"),
                    usersData.getString("password"),
                    usersData.getTimestamp("join_date").toLocalDateTime(),
                    Optional.ofNullable(usersData.getString("phone_number")),
                    usersData.getBoolean("is_admin"),
                    usersData.getBoolean("is_blocked")
            );
            users.add(user);
        }
        return users;
    }

}
