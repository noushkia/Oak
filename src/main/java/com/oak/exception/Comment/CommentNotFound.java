package com.oak.exception.Comment;

public class CommentNotFound extends Exception{

    public CommentNotFound(Integer commentId) {
        super("Comment with id " + commentId + " not found");
    }

}
