package com.subrosagames.subrosa.domain.message;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.image.Image;

/**
 * Encapsulates a user post in a game.
 */
public interface Post {

    public Integer getPostId();

    public Integer getGameId();

    public Account getAccount();

    public String getContent();

    public Integer getHistoryId();

    public Integer getAccoladeId();

    public Image getImage();

}
