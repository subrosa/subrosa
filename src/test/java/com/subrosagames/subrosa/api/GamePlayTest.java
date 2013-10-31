package com.subrosagames.subrosa.api;

import org.junit.Test;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.player.GameRole;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.NotificationDetailKey.withDetailKey;
import static com.subrosagames.subrosa.test.matchers.NotificationListHas.hasNotification;

/**
 * Test game play workflows.
 */
public class GamePlayTest extends AbstractApiControllerTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testJoinGameRequiresAuth() throws Exception {
        mockMvc.perform(
                post("/game/{url}/join", "fun_times"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value(is(notificationList())));
    }

    @Test
    public void testJoinAssassinGame() throws Exception {
        String url = newRandomGame(GameType.ASSASSIN);
        String player = newRandomUser();

        perform(post("/game/{url}/join", url)
                .with(user(player)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("name"))));

        perform(post("/game/{url}/join", url)
                .with(user(player))
                .content(jsonBuilder().add("name", "  ").build()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value(is(notificationList())))
                .andExpect(jsonPath("$.notifications").value(hasNotification(withDetailKey("name"))));

        perform(post("/game/{url}/join", url)
                .with(user(player))
                .content(jsonBuilder().add("name", "Thrilla in Manila").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Thrilla in Manila"))
                .andExpect(jsonPath("$.gameRole").value(GameRole.PLAYER.name()));
    }

    // CHECKSTYLE-ON: JavadocMethod
}
