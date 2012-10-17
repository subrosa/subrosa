package com.subrosagames.subrosa.domain.message;

import java.util.Date;
import java.util.List;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.AssassinsGame;
import com.subrosagames.subrosa.domain.game.Game;
import com.subrosagames.subrosa.domain.game.event.GameEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;

/**
 * Encapsulates a user post in a game.
 */
public class Post {

    private Integer postId;
    private Integer gameId;
    private Integer accountId;
    private String content;
    private Integer historyId;
    private Integer accoladeId;
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
