package org.ie.tk.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    @JsonProperty("id")
    @JsonIgnore
    private Integer id;
    @JsonProperty("userEmail")
    private String userEmail;
    @JsonProperty("commodityId")
    private Integer commodityId;
    @JsonProperty("text")
    private String text;
    @JsonProperty("date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    @JsonIgnore
    // recorded votes for the comment
    // -1: dislike, 1: like, 0: neutral
    private final HashMap<String, Integer> votes = new HashMap<>();

    @JsonIgnore
    private static Integer newCommentId = 0;

    @JsonProperty("id")
    public void setId() {
        id = newCommentId++;
    }

    public void addVote(String username, Integer newVote) {
        votes.put(username, newVote);
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
        for (Integer vote : votes.values()) {
            if (Objects.equals(vote, voteValue)) {
                votesCount++;
            }
        }
        return votesCount;
    }

}
