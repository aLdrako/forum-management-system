package com.company.web.forummanagementsystem.repositories;

import com.company.web.forummanagementsystem.models.Comment;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@Repository
@PropertySource("classpath:application.properties")
public class CommentRepositorySql implements CommentRepository{

    private final String dbUrl, dbUsername, dbPassword;

    public CommentRepositorySql(Environment environment) {
        dbUrl = environment.getProperty("database.url");
        dbUsername = environment.getProperty("database.username");
        dbPassword = environment.getProperty("database.password");
    }

    @Override
    public List<Comment> getAll() {
        return null;
    }

    @Override
    public Comment getById(Long id) {
        return null;
    }

    @Override
    public void create(Comment comment) {

    }

    @Override
    public void update(Comment comment) {

    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public List<Comment> getCommentsByUserId(Long userId) {
        return null;
    }

    @Override
    public Comment getCommentByUserId(Long userId, Long commentId) {
        return null;
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return null;
    }

    @Override
    public Comment getCommentByPostId(Long postId, Long commentId) {
        return null;
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
