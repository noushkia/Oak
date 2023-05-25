package com.oak.domain;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    @JsonProperty("id")
    private Integer id;
    private final String userEmail;
    private final Integer commodityId;
    private final String text;
    private final Date date;
    @JsonIgnore
    // recorded votes for the comment
    // -1: dislike, 1: like, 0: neutral
    private HashMap<String, Integer> userVotes = new HashMap<>();

    @JsonIgnore
    private static Integer newCommentId = 0;

    @JsonCreator
    public Comment(@JsonProperty("userEmail") String userEmail,
                   @JsonProperty("commodityId") Integer commodityId,
                   @JsonProperty("text") String text,
                   @JsonProperty("date") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") Date date) {
        this.userEmail = userEmail;
        this.commodityId = commodityId;
        this.text = text;
        this.date = date;
    }

    public void setId() {
        this.id = newCommentId++;
    }

    public void setId(Integer newId) {
        this.id = newId;
    }

    public void setUserVotes(HashMap<String, Integer> votes) {
        this.userVotes = votes;
    }
    public void addUserVote(String username, Integer vote) {
        userVotes.put(username, vote);
    }

    public Integer getId() {
        return id;
    }

    public Integer getCommodityId() {
        return commodityId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date != null ? date : new Date();
    }

    @JsonProperty("likes")
    public Integer getLikes() {
        return getVotes(1);
    }

    @JsonProperty("dislikes")
    public Integer getDislikes() {
        return getVotes(-1);
    }

    public Integer getVotes(Integer voteValue) {
        return  (int) userVotes.values().stream()
                .filter(vote -> Objects.equals(vote, voteValue))
                .count();
    }

    public HashMap<String, Integer> getUserVotes() {
        return userVotes;
    }
}
