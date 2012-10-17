package com.subrosagames.subrosa.domain.game.post;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 */
@Entity
@Table(name = "post")
public class PostEntity {

    @Id
    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "game_id")
    private Integer gameId;

    @Column(name = "account_id")
    private Integer accountId;

    @Column
    private String content;

    @Column(name = "history_id")
    private Integer historyId;

    @Column(name = "accolade_id")
    private Integer accoladeId;

    @Column(name = "image_id")
    private Integer imageId;

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Integer historyId) {
        this.historyId = historyId;
    }

    public Integer getAccoladeId() {
        return accoladeId;
    }

    public void setAccoladeId(Integer accoladeId) {
        this.accoladeId = accoladeId;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}
