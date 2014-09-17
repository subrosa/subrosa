package com.subrosagames.subrosa.domain.game.persistence;

import java.util.Calendar;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.game.GameType;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.game.validation.PublishAction;
import com.subrosagames.subrosa.event.ScheduledEvent;

import static org.hamcrest.Matchers.not;

import static com.subrosagames.subrosa.test.matchers.HasConstraintViolation.hasConstraintViolation;


@RunWith(JUnit4.class)
public class ValidationTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testPrerequisiteGameEntityValid() throws Exception {
        getValidGame().assertValid();
    }

    @Test
    public void testPrerequisitePublisableGameIsValid() throws Exception {
        getPublishableGame().assertValid(PublishAction.class);
    }

    @Test
    public void testDefaultValidations() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("name", "required"));
        expectedException.expect(hasConstraintViolation("url", "required"));
        expectedException.expect(hasConstraintViolation("gameType", "required"));
        GameEntity game = new GameEntity();
        game.assertValid();
    }

    @Test
    public void testPublishViolations() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("registrationStart", "required"));
        expectedException.expect(hasConstraintViolation("registrationEnd", "required"));
        expectedException.expect(hasConstraintViolation("gameStart", "required"));
        expectedException.expect(hasConstraintViolation("gameEnd", "required"));
        GameEntity game = new GameEntity();
        game.assertValid(PublishAction.class);
    }

    @Test
    public void testRegistrationEndBeforeStart() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("registrationStart", "startBeforeEnd"));
        expectedException.expect(hasConstraintViolation("registrationEnd", "startBeforeEnd"));
        GameEntity game = getPublishableGame();
        game.setRegistrationEnd(Lists.newArrayList(eventXDaysFromNow(1)));
        game.assertValid();
    }

    @Test
    public void testGameEndBeforeStart() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("gameStart", "startBeforeEnd"));
        expectedException.expect(hasConstraintViolation("gameEnd", "startBeforeEnd"));
        GameEntity game = getPublishableGame();
        game.setGameEnd(Lists.newArrayList(eventXDaysFromNow(25)));
        game.assertValid();
    }

    @Test
    public void testNameIsEmpty() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("name", "required"));
        expectedException.expect(not(hasConstraintViolation("url", "required")));
        GameEntity game = getValidGame();
        game.setName("");
        game.assertValid();
    }

    @Test
    public void testUrlIsEmpty() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("url", "required"));
        expectedException.expect(not(hasConstraintViolation("name", "required")));
        GameEntity game = getValidGame();
        game.setUrl("");
        game.assertValid();
    }

    private GameEntity getValidGame() {
        GameEntity game = new GameEntity();
        game.setName("name of the game");
        game.setUrl("abc123");
        game.setGameType(GameType.PAPARAZZI);
        return game;
    }

    private GameEntity getPublishableGame() {
        GameEntity game = getValidGame();
        game.setRegistrationStart(Lists.newArrayList(eventXDaysFromNow(10)));
        game.setRegistrationEnd(Lists.newArrayList(eventXDaysFromNow(20)));
        game.setGameStart(Lists.newArrayList(eventXDaysFromNow(30)));
        game.setGameEnd(Lists.newArrayList(eventXDaysFromNow(40)));
        return game;
    }

    private ScheduledEvent eventXDaysFromNow(int days) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        ScheduledEventEntity event = new ScheduledEventEntity();
        event.setDate(calendar.getTime());
        return event;
    }


}

