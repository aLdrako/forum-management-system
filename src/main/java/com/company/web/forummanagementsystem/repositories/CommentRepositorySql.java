package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.exceptions.EntityNotFoundException;
import com.company.web.forummanagementsystem.models.Comment;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Repository
@PropertySource("classpath:application.properties")
public class CommentRepositorySql implements CommentRepository{
    private static final String SQL_COMMENTS_TABLE = """
            select * 
            from comments
            """;
    private final String dbUrl, dbUsername, dbPassword;

    public CommentRepositorySql(Environment environment) {
        dbUrl = environment.getProperty("database.url");
        dbUsername = environment.getProperty("database.username");
        dbPassword = environment.getProperty("database.password");
    }

    @Override
    public List<Comment> getAll() {
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(SQL_COMMENTS_TABLE);
                ){
            return getComments(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Comment getById(Long id) {
        String query = SQL_COMMENTS_TABLE;
        query += """
                where id = ? 
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()){
                List<Comment> result = getComments(resultSet);
                if (result.size() == 0) {
                    throw new EntityNotFoundException("Comment", id);
                }
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Comment create(Comment comment) {
        String query = """
                insert into 
                comments (post_id, user_id, content)
                values 
                (?, ?, ?);
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setLong(1, comment.getPostId());
            statement.setLong(2, comment.getUserId());
            statement.setString(3, comment.getContent());
            statement.executeUpdate();
            return getLatestComment();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Comment getLatestComment() {
        String query = """
                select max(id) as max
                from comments;
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
    public Comment update(Comment comment) {
        String query = """
                update comments set 
                content = ?
                where id = ?;
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setString(1, comment.getContent());
            statement.setLong(2, comment.getId());
            statement.executeUpdate();
            return getById(comment.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Long id) {
        getById(id);
        String query = """
                delete
                from comments
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
    public List<Comment> getCommentsByUserId(Long userId) {
        String query = SQL_COMMENTS_TABLE;
        query += """
                where user_id = ? 
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ){
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()){
                return getComments(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Comment getCommentByUserId(Long userId, Long commentId) {
        String query = SQL_COMMENTS_TABLE;
        query += """
                where user_id = ? and id = ? 
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query)
                ){
            statement.setLong(1, userId);
            statement.setLong(2, commentId);
            try (ResultSet resultSet = statement.executeQuery()){
                List<Comment> result = getComments(resultSet);
                if (result.size() == 0) {
                    throw new EntityNotFoundException("Comment", commentId, "user id", userId);
                }
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        getById(postId);
        String query = SQL_COMMENTS_TABLE;
        query += """
                where post_id = ?
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ){
            statement.setLong(1, postId);
            try(ResultSet resultSet = statement.executeQuery()) {
                return getComments(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
                }

    }

    @Override
    public Comment getCommentByPostId(Long postId, Long commentId) {
        getById(commentId);
        String query = SQL_COMMENTS_TABLE;
        query += """
                where id = ? and post_id = ?
                """;
        try (
                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
                PreparedStatement statement = connection.prepareStatement(query);
                ){
            statement.setLong(1, commentId);
            statement.setLong(2, postId);
            try (ResultSet resultSet = statement.executeQuery()){
                List<Comment> result = getComments(resultSet);
                if (result.size() == 0) {
                    throw new EntityNotFoundException("Comment", commentId, "post id", postId);
                }
                return result.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Comment> getComments(ResultSet resultSet) throws SQLException {
        List<Comment> comments = new ArrayList<>();
        while(resultSet.next()) {
            Comment comment = new Comment(
                    resultSet.getLong("id"),
                    resultSet.getString("content"),
                    resultSet.getLong("post_id"),
                    resultSet.getLong("user_id"),
                    resultSet.getTimestamp("date_created").toLocalDateTime()
            );
            comments.add(comment);
        }
        return comments;
    }
}
