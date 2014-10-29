package com.subrosagames.subrosa.domain.game;

import java.util.Calendar;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.Lists;
import com.subrosagames.subrosa.domain.game.persistence.ScheduledEventEntity;
import com.subrosagames.subrosa.domain.game.validation.GameValidationException;
import com.subrosagames.subrosa.domain.game.validation.PublishAction;
import com.subrosagames.subrosa.event.ScheduledEvent;

import static org.hamcrest.Matchers.not;

import static com.subrosagames.subrosa.test.matchers.HasConstraintViolation.hasConstraintViolation;


/**
 * Tests validation scenarios for a @{link BaseGame} entity.
 */
@RunWith(JUnit4.class)
public class ValidationTest {

    // CHECKSTYLE-OFF: JavadocMethod
    // CHECKSTYLE-OFF: JavadocVariable

    @Rule
    public ExpectedException expectedException = ExpectedException.none(); // SUPPRESS CHECKSTYLE VisibilityModifier

    @Test
    public void testPrerequisiteBaseGameValid() throws Exception {
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
        BaseGame game = new BaseGame();
        game.assertValid();
    }

    @Test
    public void testPublishViolations() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("registrationStart", "required"));
        expectedException.expect(hasConstraintViolation("registrationEnd", "required"));
        expectedException.expect(hasConstraintViolation("gameStart", "required"));
        expectedException.expect(hasConstraintViolation("gameEnd", "required"));
        BaseGame game = new BaseGame();
        game.assertValid(PublishAction.class);
    }

    @Test
    public void testRegistrationEndBeforeStart() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("registrationStart", "startBeforeEnd"));
        expectedException.expect(hasConstraintViolation("registrationEnd", "startBeforeEnd"));
        BaseGame game = getPublishableGame();
        game.setRegistrationEnd(Lists.newArrayList(eventXDaysFromNow(1)));
        game.assertValid();
    }

    @Test
    public void testGameEndBeforeStart() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("gameStart", "startBeforeEnd"));
        expectedException.expect(hasConstraintViolation("gameEnd", "startBeforeEnd"));
        BaseGame game = getPublishableGame();
        game.setGameEnd(Lists.newArrayList(eventXDaysFromNow(25)));
        game.assertValid();
    }

    @Test
    public void testNameIsEmpty() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("name", "required"));
        expectedException.expect(not(hasConstraintViolation("url", "required")));
        BaseGame game = getValidGame();
        game.setName("");
        game.assertValid();
    }

    @Test
    public void testUrlIsEmpty() throws Exception {
        expectedException.expect(GameValidationException.class);
        expectedException.expect(hasConstraintViolation("url", "required"));
        expectedException.expect(not(hasConstraintViolation("name", "required")));
        BaseGame game = getValidGame();
        game.setUrl("");
        game.assertValid();
    }

    private BaseGame getValidGame() {
        BaseGame game = new BaseGame();
        game.setName("name of the game");
        game.setUrl("abc123");
        game.setGameType(GameType.PAPARAZZI);
        return game;
    }

    private BaseGame getPublishableGame() {
        BaseGame game = getValidGame();
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

    // CHECKSTYLE-ON: JavadocMethod
    // CHECKSTYLE-ON: JavadocVariable

}

