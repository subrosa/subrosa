package com.subrosagames.subrosa.domain.game;

import com.subrosagames.subrosa.domain.message.Post;

/**
 * Created with IntelliJ IDEA.
 * User: jgore
 * Date: 7/18/12
 * Time: 11:45 午前
 * To change this template use File | Settings | File Templates.
 */
public interface GameFactory {

    Post getForEntity(Post entity);
}
