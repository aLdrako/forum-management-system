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

//@Repository
@PropertySource("classpath:application.properties")
public class UserRepositorySql implements UserRepository {

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
                ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS")
                ) {
            return getUsers(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public User getById(Long id) {
        String query = "SELECT * FROM USERS WHERE ID = ?";
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<User> result = getUsers(resultSet);
                if (result.size() == 0) {
                    throw new EntityNotFoundException("User", id);
                }
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public User getByUsername(String username) {
        return null;
    }

    @Override
    public User getByEmail(String email) {
        return null;
    }

    @Override
    public void create(User user) {

    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<User> getAllWithParams(Optional<Long> id, Optional<String> username) {
        return null;
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
                    usersData.getString("password")
            );
            users.add(user);
        }
        return users;
    }
}
