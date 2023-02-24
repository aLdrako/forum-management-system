package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.User;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@PropertySource("classpath:application.properties")
public class UserRepositorySql implements UserRepository {

    private static final String SQL_GET = """
            SELECT id, first_name, last_name, email, username, password, join_date, is_admin, is_blocked, is_deleted, phone_number
            FROM users
            lEFT JOIN permissions p on users.id = p.user_id
            lEFT JOIN phones p2 on users.id = p2.user_id
            WHERE is_deleted IS NOT true
            """;
    private static final String CREATE = """
            INSERT INTO users (first_name, last_name, email, username, password)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String CREATE_PERMISSIONS = """
            INSERT INTO permissions (is_deleted, is_blocked, is_admin, user_id)
            VALUES (?, ?, ?, ?)
            """;
    private static final String UPDATE = """
            UPDATE users SET first_name = ?, last_name = ?, email = ?, username = ?, password = ?
            WHERE id = ?
            """;
    private static final String UPDATE_PERMISSIONS = """
            UPDATE permissions SET is_deleted = ?, is_blocked = ?, is_admin = ?
            WHERE user_id = ?
            """;
    private static final String DELETE = """
            UPDATE permissions SET is_deleted = true
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
                ResultSet resultSet = statement.executeQuery(SQL_GET + ";")
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
                PreparedStatement statement = connection.prepareStatement(SQL_GET + " AND id = ?;")
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
    public List<User> search(String parameter) {
        String[] params = parameter.split("=");
        boolean hasParams = true;
        String searchParam = switch (params[0]) {
            case "email" -> " AND email = ?;";
            case "username" -> " AND username = ?;";
            case "firstName" -> " AND first_name = ?;";
            default -> {
                hasParams = false;
                yield ";";
            }
        };

        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(SQL_GET + searchParam)
        ) {
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
                PreparedStatement statement = connection.prepareStatement(CREATE);
                PreparedStatement statementPermissions = connection.prepareStatement(CREATE_PERMISSIONS)
        ) {
            userStatement(user, statement);
            statement.executeUpdate();

            User newUser = search("email=" + user.getEmail()).get(0);
            user.setId(newUser.getId());

            permissionStatement(user, statementPermissions, "create");
            statementPermissions.executeUpdate();
            return newUser;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User update(User user) {
        getById(user.getId());
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(UPDATE);
                PreparedStatement statementPermissions = connection.prepareStatement(UPDATE_PERMISSIONS)
        ) {
            userStatement(user, statement);
            statement.setLong(6, user.getId());
            statement.executeUpdate();

            permissionStatement(user, statementPermissions, "update");
            statementPermissions.executeUpdate();

            return search("email=" + user.getEmail()).get(0);
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
        ) {

            statement.setLong(1, id);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
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
            statement.setBoolean(1, user.isDeleted());
            statement.setBoolean(2, user.isBlocked());
            statement.setBoolean(3, user.isAdmin());
        } else if (operation.equals("create")) {
            statement.setBoolean(1, false);
            statement.setBoolean(2, false);
            statement.setBoolean(3, false);
        }
        statement.setLong(4, user.getId());
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
                    usersData.getBoolean("is_blocked"),
                    usersData.getBoolean("is_deleted")
            );
            users.add(user);
        }
        return users;
    }

}
