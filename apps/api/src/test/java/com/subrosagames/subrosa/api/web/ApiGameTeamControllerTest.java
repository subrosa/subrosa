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

    @Test
    public void listTeams() throws Exception {
        mockMvc.perform(get("/game/{gameUrl}/team", "fun_times"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasResultsSize(2)));
    }

    @Test
    public void getTeam() throws Exception {
        mockMvc.perform(get("/game/{gameUrl}/team/{teamId}", "fun_times", 100))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
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
        String response = mockMvc.perform(put("/game/{gameUrl}/team/{id}", "fun_times", 100)
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
}