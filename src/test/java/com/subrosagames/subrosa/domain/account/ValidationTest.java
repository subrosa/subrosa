package com.subrosagames.subrosa.domain.account;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.subrosagames.subrosa.test.matchers.HasConstraintViolation.hasConstraintViolation;


/**
 * Tests validation scenarios for an @{link Account} entity.
 */
@RunWith(JUnit4.class)
public class ValidationTest {

    // CHECKSTYLE-OFF: JavadocMethod
    // CHECKSTYLE-OFF: JavadocVariable

    @Rule
    public ExpectedException expectedException = ExpectedException.none(); // SUPPRESS CHECKSTYLE VisibilityModifier

    @Test
    public void testPrerequisiteAccountValid() throws Exception {
        getValidAccount().assertValid();
    }

    @Test
    public void testEmailIsNull() throws Exception {
        expectedException.expect(AccountValidationException.class);
        expectedException.expect(hasConstraintViolation("email", "required"));
        Account account = getValidAccount();
        account.setEmail(null);
        account.assertValid();
    }

    @Test
    public void testEmailIsEmpty() throws Exception {
        expectedException.expect(AccountValidationException.class);
        expectedException.expect(hasConstraintViolation("email", "required"));
        Account account = getValidAccount();
        account.setEmail(" ");
        account.assertValid();
    }

    @Test
    public void testEmailIsBadlyFormatted() throws Exception {
        expectedException.expect(AccountValidationException.class);
        expectedException.expect(hasConstraintViolation("email", "validEmail"));
        Account account = getValidAccount();
        account.setEmail("notavalidemail:(");
        account.assertValid();
    }

    private Account getValidAccount() {
        Account account = new Account();
        account.setEmail("email@example.org");
        return account;
    }

    // CHECKSTYLE-ON: JavadocMethod
    // CHECKSTYLE-ON: JavadocVariable
}

