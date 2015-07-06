package com.subrosagames.subrosa.api.web;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.subrosagames.subrosa.test.matchers.IsPaginatedListWithResultsSize.hasResultsSize;

/**
 * TODO
 */
@DatabaseSetup("/fixtures/games.xml")
public class ApiGameTeamControllerTest extends AbstractApiControllerTest {

    public void listTeams() throws Exception {
        mockMvc.perform(get("/game/{gameUrl}/team"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasResultsSize(5)));
    }

    public void getTeam() throws Exception {

    }

    public void getTeamNotFound() throws Exception {

    }

    public void createTeam() throws Exception {

    }

    public void updateTeam() throws Exception {

    }
}