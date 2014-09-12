package com.subrosagames.subrosa.domain.account;

import org.junit.Test;

public class AccountTest {

    @Test
    public void testPrerequisiteAccountValid() throws Exception {
        getValidAccount().assertValid();
    }

    @Test(expected = AccountValidationException.class)
    public void testMissingEmail() throws Exception {
        Account account = getValidAccount();
        account.setEmail(null);
        account.assertValid();
    }

    @Test(expected = AccountValidationException.class)
    public void testEmptyEmail() throws Exception {
        Account account = getValidAccount();
        account.setEmail("");
        account.assertValid();
    }

    @Test(expected = AccountValidationException.class)
    public void testBadEmail() throws Exception {
        Account account = getValidAccount();
        account.setEmail("notavalidemail:(");
        account.assertValid();
    }

    private Account getValidAccount() {
        Account account = new Account();
        account.setEmail("email@example.org");
        return account;
    }

}
