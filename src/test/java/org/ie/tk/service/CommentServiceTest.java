package org.ie.tk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ie.tk.application.service.CommentService;
import org.ie.tk.data.Database;
import org.ie.tk.domain.Comment;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.User;
import org.ie.tk.exception.Comment.CommentNotFound;
import org.ie.tk.exception.User.UserNotFound;
import org.junit.*;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class CommentServiceTest {
    private static Database database;
    private static CommentService commentService;
    static User[] users;
    static Commodity[] commodities;
    static Comment[] comments;

    @BeforeClass
    public static void initialSetup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        users = mapper.readValue(new File("src/test/resources/users.json"), User[].class);
        commodities = mapper.readValue(new File("src/test/resources/commodities.json"), Commodity[].class);
        comments = mapper.readValue(new File("src/test/resources/comments.json"), Comment[].class);
    }

    @Before
    public void setup() {
        database = new Database();
        for (User user : users) {
            database.addUser(user);
        }
        for (Commodity commodity : commodities) {
            database.addCommodity(commodity);
        }
        for (Comment comment : comments) {
            database.addComment(comment);
        }

        commentService = new CommentService(database);
    }

    // vote comment tests
    @Test
    public void voteComment_withValidInputs_shouldAddVote() throws Exception {
        // Arrange
        String username = users[0].getUsername();
        Integer commentId = comments[0].getId();
        Integer vote = 1;

        // Act
        commentService.voteComment(username, commentId, vote);

        // Verify: Get the comment and verify the vote was added
        Comment comment = database.fetchComment(commentId);
        assertTrue(comment.getUserVotes().containsKey(username));
        assertEquals(1, comment.getUserVotes().get(username).intValue());
    }

    @Test
    public void voteComment_withNonExistingUser_shouldGetUserNotFound() {
        // Arrange
        String username = "amirfery";
        Integer commentId = comments[0].getId();
        Integer vote = 1;

        // Act & Assert
        assertThrows(UserNotFound.class, () -> commentService.voteComment(username, commentId, vote));
    }

    @Test
    public void voteComment_withNonExistingComment_shouldGetCommentNotFound() {
        // Arrange
        String username = users[0].getUsername();
        Integer commentId = 100;
        Integer vote = 1;

        // Act & Assert
        assertThrows(CommentNotFound.class, () -> commentService.voteComment(username, commentId, vote));
    }

    @AfterClass
    public static void tearDown() {
        commentService = null;
        database = null;
        users = null;
        commodities = null;
        comments = null;
    }
}

