package com.subrosagames.subrosa.service;

import com.subrosagames.subrosa.domain.game.Game;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test {@link GameService}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/test-context.xml")
public class GameServiceTest {

    @Autowired
    GameService gameService;

//    @Autowired
//    JdbcTemplate jdbcTemplate;

    @Before
    public void before() {
    }

    @Test
    public void createNewGameAndTestWorkflow() {
    }
}
