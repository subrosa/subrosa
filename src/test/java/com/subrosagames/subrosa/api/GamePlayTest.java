package com.subrosagames.subrosa.api;

import com.subrosagames.subrosa.domain.game.GameType;
import org.junit.Test;

import static com.subrosagames.subrosa.test.matchers.IsNotificationList.notificationList;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        mockMvc.perform(
                post("/game/{url}/join", url)
                        .with(user(player)))
                .andExpect(status().isOk());
    }

    // CHECKSTYLE-ON: JavadocMethod
}
