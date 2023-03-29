package com.oak.domain;

import com.fasterxml.jackson.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    @JsonIgnore
    private Integer id;
    private String userEmail;
    private Integer commodityId;
    private String text;
    private Date date;
    @JsonIgnore
    // recorded votes for the comment
    // -1: dislike, 1: like, 0: neutral
    private final HashMap<String, Integer> userVotes = new HashMap<>();

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
        return date;
    }

    public Integer getVotes(Integer voteValue) {
        int votesCount = 0;
        for (Integer vote : userVotes.values()) {
            if (Objects.equals(vote, voteValue)) {
                votesCount++;
            }
        }
        return votesCount;
    }

    public HashMap<String, Integer> getUserVotes() {
        return userVotes;
    }
}
