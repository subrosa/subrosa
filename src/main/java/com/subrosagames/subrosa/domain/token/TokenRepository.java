package com.subrosagames.subrosa.domain.token;

import com.subrosagames.subrosa.domain.account.Account;
import com.subrosagames.subrosa.domain.token.persistence.TokenEntity;

/**
 * CRUD operations for tokens.
 */
public interface TokenRepository {

    /**
     * Find specified token.
     *
     * @param token     token string
     * @param tokenType token type
     * @return token entity
     */
    TokenEntity findToken(String token, TokenType tokenType);

    /**
     * Store the given token.
     *
     * @param tokenEntity token entity
     */
    void storeToken(TokenEntity tokenEntity);

    /**
     * Deletes all tokens for the given account.
     *
     * @param account account
     */
    void deleteTokensForAccount(Account account);
}
