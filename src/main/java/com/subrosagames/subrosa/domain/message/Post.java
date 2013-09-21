package com.subrosagames.subrosa.domain.message;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.game.PostType;
import com.subrosagames.subrosa.domain.image.Image;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Encapsulates a user post in a game.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public interface Post {

    /**
     * Get post id.
     * @return post id
     */
    Integer getPostId();

    /**
     * Get game id.
     * @return game id
     */
    Integer getGameId();

    /**
     * Get account that created the post.
     * @return owning account
     */
    Account getAccount();

    /**
     * Get the type of post.
     * @return post type
     */
    PostType getPostType();

    /**
     * Get post content.
     * @return post content
     */
    String getContent();

    /**
     * Get id to historical event.
     * @return history id
     */
    Integer getHistoryId();

    /**
     * Get id of associated accolade.
     * @return accolade id
     */
    Integer getAccoladeId();

    /**
     * Get associated image.
     * @return image
     */
    Image getImage();

}
