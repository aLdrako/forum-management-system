package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Post;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
@PropertySource("classpath:application.properties")
public class PostRepositorySql implements PostRepository{
    private static final String SQL_JOIN_LIKES_TABLE = """
            left join (select post_id, count(*) as likes 
                            from likes 
                            group by post_id) l on p.id = l.post_id 
            """;
    private static final String SQL_POSTS_LIKES_JOINED = """
            select * 
            from posts p
            left join (select post_id, count(*) as likes 
                         from likes 
                         group by post_id) l on p.id = l.post_id 
            """;
    private final String dbUrl, dbUsername, dbPassword;

    public PostRepositorySql(Environment environment) {
        dbUrl = environment.getProperty("database.url");
        dbUsername = environment.getProperty("database.username");
        dbPassword = environment.getProperty("database.password");
    }

    @Override
    public List<Post> getAll(Optional<Long> userId, Optional<String> title,
                             Optional<String> sortBy, Optional<String> orderBy) {
        String query = SQL_POSTS_LIKES_JOINED;
        query += addToQuery(userId, title, sortBy, orderBy);

        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            int parameterCounter = 1;
            if (title.isPresent()) statement.setString(parameterCounter++, "%" + title.get() + "%");
            if (userId.isPresent()) statement.setLong(parameterCounter++, userId.get());

            try (ResultSet resultSet = statement.executeQuery()){
                return getPosts(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String addToQuery(Optional<Long> userId, Optional<String> title, Optional<String> sortBy,
                              Optional<String> orderBy) {
        String result = "";
        result += userId.isPresent() || title.isPresent() ? "where" : "";
        result += title.isPresent() ? " p.title like ?" : "";
        result += userId.isPresent() && title.isPresent() ? " and" : "";
        result += userId.isPresent() ? " p.user_id = ?" : "";
        result += sortPosts(sortBy);
        result += orderBy.isPresent() &&
                orderBy.get().equalsIgnoreCase("desc") ? " desc" : "";
        return result;
    }

    private String sortPosts(Optional<String> sortBy) {
        if (sortBy.isPresent()) {
            switch (sortBy.get().toLowerCase()) {
                case "title":
                    return " order by title";
                case "userid":
                case "user_id":
                    return " order by user_id";
                case "date":
                case "date_created":
                case "datecreated":
                    return " order by date_created";
            }
        }
        return " order by id";
    }

    @Override
    public Post getById(Long id) {
        String query = SQL_POSTS_LIKES_JOINED;
        query += "where p.id = ?;";
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
    public Post create(Post post) {
        String query = """
                insert into
                posts (title, content, user_id)
                values 
                (?, ?, ?)
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ){
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getContent());
            statement.setLong(3, post.getUserId());
            statement.executeUpdate();

            return getLastestPost();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Post getLastestPost() {
        String query = """
                select max(id) as max
                from posts p
                """;
        query += SQL_JOIN_LIKES_TABLE;
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
            statement.setLong(3, post.getId());

            statement.executeUpdate();
            return getById(post.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void delete(Long id) {
        getById(id);
        deleteCommentsOfPost(id);
        deleteLikesOfPost(id);
        String query = """
                delete from posts
                where id = ?
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
    private void deleteLikesOfPost(Long postId) {
        String query = """
                delete 
                from likes
                where post_id = ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setLong(1, postId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void deleteCommentsOfPost(Long postId) {
        String query = """
                delete 
                from comments
                where post_id = ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ){
            statement.setLong(1, postId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Post> getTopTenMostCommented() {
        String query = SQL_POSTS_LIKES_JOINED;
        query += """
                right join comments c on p.id = c.post_id 
                group by c.post_id 
                order by count(*) desc 
                limit 10;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)
                ){
            List<Post> result = getPosts(resultSet);
            if (result.size() == 10) {
                return result;
            }
            int postsToReturnCount = 10 - result.size();
            result.addAll(returnPostsWithNoComments(postsToReturnCount));
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private List<Post> returnPostsWithNoComments(int postsToReturnCount) {
        String query = SQL_POSTS_LIKES_JOINED;
        query += """
                left join comments c on p.id = c.post_id
                where c.id is null
                limit ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setInt(1, postsToReturnCount);
            try (ResultSet resultSet = statement.executeQuery()){
                return getPosts(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Post> getTopTenMostRecent() {
        String query = SQL_POSTS_LIKES_JOINED;
        query += """
                order by date_created desc 
                limit 10;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(query)
                ){
            return getPosts(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Post getPostByUserId(Long userId, Long postId) {
        getById(postId);
        String query = SQL_POSTS_LIKES_JOINED;
        query += """
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
