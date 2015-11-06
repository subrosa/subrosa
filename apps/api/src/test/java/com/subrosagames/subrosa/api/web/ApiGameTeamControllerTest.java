package com.subrosagames.subrosa.api.web;

import org.junit.Test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.jayway.jsonpath.JsonPath;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultsSize.hasResultsSize;

/**
 * Test {@link ApiGameTeamController}.
 */
@DatabaseSetup("/fixtures/games.xml")
public class ApiGameTeamControllerTest extends AbstractApiControllerTest {

    static final int TEAM_ID = 100;
    static final String TEAM_NAME = "good team";

    @Test
    public void listTeams() throws Exception {
        mockMvc.perform(get("/game/{gameUrl}/team", "fun_times"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasResultsSize(2)));
    }

    @Test
    public void getTeam() throws Exception {
        mockMvc.perform(get("/game/{gameUrl}/team/{teamId}", "fun_times", TEAM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEAM_ID))
                .andExpect(jsonPath("$.name").value("good team"));
    }

    @Test
    public void getTeamNotFound() throws Exception {
        mockMvc.perform(get("/game/{gameUrl}/team/{teamId}", "fun_times", 666))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createTeam() throws Exception {
        String response = mockMvc.perform(post("/game/{gameUrl}/team", "fun_times")
                .with(user("young@player.com"))
                .content(jsonBuilder().add("name", "team name").build()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("team name"))
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.compile("$.id").read(response);
        mockMvc.perform(get("/game/{gameUrl}/team/{id}", "fun_times", id)
                .with(user("young@player.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("team name"));
    }

    @Test
    public void updateTeam() throws Exception {
        String response = mockMvc.perform(put("/game/{gameUrl}/team/{id}", "fun_times", TEAM_ID)
                .with(user("young@player.com"))
                .content(jsonBuilder().add("name", "new name").build()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new name"))
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.compile("$.id").read(response);
        mockMvc.perform(get("/game/{gameUrl}/team/{id}", "fun_times", id)
                .with(user("young@player.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new name"));
    }

    @Test
    public void joinTeam() throws Exception {
        mockMvc.perform(post("/game/{url}/team/{id}/join", "fun_times", TEAM_ID)
                .with(user("young@player.com"))
                .content(jsonBuilder().build()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/game/{url}/player/{id}", "fun_times", 12)
                .with(user("young@player.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.team.name").value(TEAM_NAME));
    }
}