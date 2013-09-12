package com.subrosagames.subrosa.api;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.jsonpath.JsonPath;
import com.subrosagames.subrosa.domain.game.GameRepository;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.Lifecycle;
import com.subrosagames.subrosa.domain.game.assassins.AssassinGame;
import com.subrosagames.subrosa.domain.game.persistence.GameEntity;
import com.subrosagames.subrosa.domain.game.persistence.LifecycleEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MvcResult;

import static com.subrosagames.subrosa.test.matchers.IsPaginatedList.paginatedList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test {@link com.subrosagames.subrosa.api.ApiGameController}.
 */
@TestExecutionListeners({
        DbUnitTestExecutionListener.class
})
@DatabaseSetup("/fixtures/games.xml")
public class ApiGameControllerTest extends AbstractApiControllerTest {

    @Autowired
    private GameRepository gameRepository;

    @Test
    public void testGameRetrieval() throws Exception {
        GameEntity game = new AssassinGame();
        game.setName("Test game");
        game.setUrl("test_game_url");
        game.setGameType(GameType.ASSASSIN);
        Lifecycle lifecycle = new LifecycleEntity();
        gameRepository.save(lifecycle);
        game.setLifecycle(lifecycle);
        gameRepository.create(game);

        mockMvc.perform(
                get("/game/test_game_url")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value("Test game"));

        mockMvc.perform(
                get("/game/fun_times")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.name").value("Fun Times!"));

    }

    @Test
    public void testGameNotFound() throws Exception {
        mockMvc.perform(get("/game/does_not_exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testListGamesNoArgumentsDefaultsTo10() throws Exception {
        mockMvc.perform(get("/game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$.results").value(hasSize(10)));
    }

    @Test
    public void testListGamesWithOffsetAndLimit() throws Exception {
        MvcResult result = mockMvc.perform(
                get("/game")
                        .param("limit", "5")
                        .param("offset", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$.results").value(hasSize(5)))
                .andReturn();
        String response = result.getResponse().getContentAsString();
        Integer count = JsonPath.compile("$.resultCount").read(response);

        mockMvc.perform(
                get("/game")
                        .param("limit", "5")
                        .param("offset", String.valueOf(count - 2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(is(paginatedList())))
                .andExpect(jsonPath("$.results").value(hasSize(2)));
    }

}
