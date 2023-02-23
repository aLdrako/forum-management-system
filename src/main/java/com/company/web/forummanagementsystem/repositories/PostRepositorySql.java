package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Post;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
                    throw new EntityNotFoundException("Beer", id);
                }
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> searchByTitle(String title) {
        return getAll().stream()
                .filter(post -> post.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
        //String query = """
        //        select *
        //        from posts
        //        where title like '%?%'
        //        """;
        //try (
        //        Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        //        PreparedStatement statement = connection.prepareStatement(query)
        //        ){
        //
        //}

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

            return getPostWithBiggestId();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Post getPostWithBiggestId() {
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
    public void update(Post post) {
        String query = """
                update posts set
                title = ?, content = ?, likes = ?
                where id = ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ){
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setInt(3, post.getLikes());
            statement.setLong(4, post.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
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
                    throw new EntityNotFoundException("Post");
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
