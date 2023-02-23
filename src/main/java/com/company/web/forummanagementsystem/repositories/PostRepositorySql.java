package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Post;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@PropertySource("classpath:application.properties")
public class PostRepositorySql implements PostRepository{

    private final String dbUrl, dbUsername, dbPassword;


    public PostRepositorySql(Environment environment) {
        dbUrl = environment.getProperty("database.url");
        dbUsername = environment.getProperty("database.username");
        dbPassword = environment.getProperty("database.password");
    }


    @Override
    public List<Post> getAll() {
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM POSTS")
                ) {
            return getPosts(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Post getById(Long id) {
        String query = "SELECT * FROM POSTS WHERE ID = ?";
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()){
                List<Post> result = getPosts(resultSet);
                if (result.size() == 0) {
                    throw new EntityNotFoundException("Post", id);
                }
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> searchByTitle(String title) {
        String query = """
                select *
                from posts
                where title like ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setString(1, '%' + title + '%');
            try (ResultSet resultSet = statement.executeQuery()){
                return getPosts(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Post create(Post post) {
        String query = """
                insert into
                posts (title, content, likes, user_id)
                values 
                (?, ?, ?, ?)
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ){
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setInt(3, post.getLikes());
            statement.setLong(4, post.getUserId());
            statement.executeUpdate();

            return getLastestPost();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Post getLastestPost() {
        String query = """
                select max(id) as max
                from posts;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)
                ){
            Long id = null;
            while (resultSet.next()) {
                id = resultSet.getLong("max");
            }
            return getById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Post update(Post post) {
        getById(post.getId());
        String query = """
                update posts set
                title = ?, content = ?
                where id = ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ){
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setLong(4, post.getId());

            statement.executeUpdate();
            return post;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        getById(id);
        String query = """
                delete from posts
                where id = ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getPostsByUserId(Long userId) {
        String query = """
                select *
                from posts
                where user_id = ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()){
                return getPosts(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        getById(postId);
        String query = """
                select *
                from posts
                where user_id = ? and id = ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setLong(1, userId);
            statement.setLong(2, postId);
            try (ResultSet resultSet = statement.executeQuery()){
                List<Post> result = getPosts(resultSet);
                if (result.size() == 0) {
                    throw new EntityNotFoundException("Post", postId, "user id", userId);
                }
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Post> getPosts(ResultSet postData) throws SQLException {
        List<Post> posts = new ArrayList<>();
        while (postData.next()) {
            Post post = new Post(
                    postData.getLong("id"),
                    postData.getString("title"),
                    postData.getString("content"),
                    postData.getInt("likes"),
                    postData.getLong("user_id"),
                    postData.getTimestamp("date_created").toLocalDateTime()
            );
            posts.add(post);
        }
        return posts;
    }
}
