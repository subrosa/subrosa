package com.subrosagames.subrosa.domain.message;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.image.Image;

/**
 * Encapsulates a user post in a game.
 */
public class Post {

    private Integer postId;
    private Integer gameId;
    private Account account;
    private String content;
    private Integer historyId;
    private Integer accoladeId;
    private Image image;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
