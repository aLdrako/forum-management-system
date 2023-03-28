//package com.telerikacademy.web.fms.repositories.deprecated;
//
//import com.telerikacademy.web.fms.exceptions.EntityNotFoundException;
//import com.telerikacademy.web.fms.models.Comment;
//import com.telerikacademy.web.fms.repositories.contracts.CommentRepository;
//import com.telerikacademy.web.fms.repositories.contracts.PostRepository;
//import com.telerikacademy.web.fms.repositories.contracts.UserRepository;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
////@Repository
//@PropertySource("classpath:application.properties")
//public class CommentRepositoryJDBCImpl implements CommentRepository {
//    private static final String SQL_GET = """
//            SELECT * FROM comments
//            """;
//    private static final String SQL_CREATE = """
//            INSERT INTO comments (post_id, user_id, content)
//            VALUES (?, ?, ?);
//            """;
//    private static final String SQL_UPDATE = """
//            UPDATE comments SET content = ?
//            WHERE id = ?;
//            """;
//    private static final String SQL_DELETE = """
//            DELETE FROM comments
//            WHERE id = ?;
//            """;
//
//    private final UserRepository userRepository;
//    private final PostRepository postRepository;
//    private final String dbUrl, dbUsername, dbPassword;
//
//    public CommentRepositoryJDBCImpl(UserRepository userRepository, PostRepository postRepository, Environment environment) {
//        this.userRepository = userRepository;
//        this.postRepository = postRepository;
//        dbUrl = environment.getProperty("database.url");
//        dbUsername = environment.getProperty("database.username");
//        dbPassword = environment.getProperty("database.password");
//    }
//
//    @Override
//    public List<Comment> getAll(Map<String, String> parameters) {
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                Statement statement = connection.createStatement();
//                ResultSet resultSet = statement.executeQuery(SQL_GET);
//        ){
//            return getComments(resultSet);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Comment getById(Long id) {
//        String query = " WHERE id = ?;";
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                PreparedStatement statement = connection.prepareStatement(SQL_GET + query)
//        ){
//            statement.setLong(1, id);
//            try (ResultSet resultSet = statement.executeQuery()){
//                List<Comment> result = getComments(resultSet);
//                if (result.size() == 0) {
//                    throw new EntityNotFoundException("Comment", id);
//                }
//                return result.get(0);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Comment create(Comment comment) {
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                PreparedStatement statement = connection.prepareStatement(SQL_CREATE)
//        ){
//            statement.setLong(1, comment.getPostedOn().getId());
//            statement.setLong(2, comment.getCreatedBy().getId());
//            statement.setString(3, comment.getContent());
//            statement.executeUpdate();
//            return getLatestComment();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private Comment getLatestComment() {
//        String query = "SELECT max(id) AS max FROM comments;";
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                Statement statement = connection.createStatement();
//                ResultSet resultSet = statement.executeQuery(query)
//        ){
//            Long id = null;
//            while (resultSet.next()) {
//                id = resultSet.getLong("max");
//            }
//            return getById(id);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Comment update(Comment comment) {
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)
//        ){
//            statement.setString(1, comment.getContent());
//            statement.setLong(2, comment.getId());
//            statement.executeUpdate();
//            return getById(comment.getId());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public void delete(Long id) {
//        getById(id);
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                PreparedStatement statement = connection.prepareStatement(SQL_DELETE)
//        ){
//            statement.setLong(1, id);
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public List<Comment> getCommentsByUserId(Long userId, Map<String, String> parameters) {
//        String query = " WHERE user_id = ?;";
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                PreparedStatement statement = connection.prepareStatement(SQL_GET + query);
//        ){
//            statement.setLong(1, userId);
//            try (ResultSet resultSet = statement.executeQuery()){
//                return getComments(resultSet);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Comment getCommentByUserId(Long userId, Long commentId) {
//        String query = " WHERE user_id = ? AND id = ?;";
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                PreparedStatement statement = connection.prepareStatement(SQL_GET + query)
//        ){
//            statement.setLong(1, userId);
//            statement.setLong(2, commentId);
//            try (ResultSet resultSet = statement.executeQuery()){
//                List<Comment> result = getComments(resultSet);
//                if (result.size() == 0) {
//                    throw new EntityNotFoundException("Comment", commentId, "user id", userId);
//                }
//                return result.get(0);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public List<Comment> getCommentsByPostId(Long postId, Map<String, String> parameters) {
//        getById(postId);
//        String query = " WHERE post_id = ?;";
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                PreparedStatement statement = connection.prepareStatement(SQL_GET + query);
//        ){
//            statement.setLong(1, postId);
//            try(ResultSet resultSet = statement.executeQuery()) {
//                return getComments(resultSet);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    @Override
//    public Comment getCommentByPostId(Long postId, Long commentId) {
//        getById(commentId);
//        String query = " WHERE id = ? AND post_id = ?;";
//        try (
//                Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
//                PreparedStatement statement = connection.prepareStatement(SQL_GET + query);
//        ){
//            statement.setLong(1, commentId);
//            statement.setLong(2, postId);
//            try (ResultSet resultSet = statement.executeQuery()){
//                List<Comment> result = getComments(resultSet);
//                if (result.size() == 0) {
//                    throw new EntityNotFoundException("Comment", commentId, "post id", postId);
//                }
//                return result.get(0);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private List<Comment> getComments(ResultSet resultSet) throws SQLException {
//        List<Comment> comments = new ArrayList<>();
//        while(resultSet.next()) {
//            Comment comment = new Comment();
//            comment.setId(resultSet.getLong("id"));
//            comment.setContent(resultSet.getString("content"));
//            comment.setPostedOn(postRepository.getById(resultSet.getLong("post_id")));
//            comment.setCreatedBy(userRepository.getById(resultSet.getLong("user_id")));
//            comment.setDateCreated(resultSet.getTimestamp("date_created").toLocalDateTime());
//            comments.add(comment);
//        }
//        return comments;
//    }
//}
